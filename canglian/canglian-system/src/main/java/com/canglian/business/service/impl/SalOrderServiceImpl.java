package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.FinReceivable;
import com.canglian.business.domain.MdCustomer;
import com.canglian.business.domain.SalOrder;
import com.canglian.business.domain.SalOrderItem;
import com.canglian.business.domain.WmsOutbound;
import com.canglian.business.domain.WmsOutboundItem;
import com.canglian.business.mapper.FinReceivableMapper;
import com.canglian.business.mapper.MdCustomerMapper;
import com.canglian.business.mapper.SalOrderItemMapper;
import com.canglian.business.mapper.SalOrderMapper;
import com.canglian.business.mapper.WmsOutboundItemMapper;
import com.canglian.business.mapper.WmsOutboundMapper;
import com.canglian.business.service.IPriceResolverService;
import com.canglian.business.service.ISalOrderService;
import com.canglian.business.service.IWmsOutboundService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 销售单据服务实现
 * 
 * @author canglian
 */
@Service
public class SalOrderServiceImpl implements ISalOrderService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private SalOrderMapper salOrderMapper;

    @Autowired
    private SalOrderItemMapper salOrderItemMapper;

    @Autowired
    private MdCustomerMapper mdCustomerMapper;

    @Autowired
    private FinReceivableMapper finReceivableMapper;

    @Autowired
    private WmsOutboundMapper wmsOutboundMapper;

    @Autowired
    private WmsOutboundItemMapper wmsOutboundItemMapper;

    @Autowired
    private IPriceResolverService priceResolverService;

    @Autowired
    private IWmsOutboundService wmsOutboundService;

    /**
     * 查询销售单据详情
     * 
     * @param orderId 销售单据id
     * @return 销售单据详情
     */
    @Override
    public SalOrder selectSalOrderById(Long orderId)
    {
        SalOrder salOrder = salOrderMapper.selectSalOrderById(orderId);
        if (salOrder != null)
        {
            salOrder.setOrderItemList(salOrderItemMapper.selectSalOrderItemListByOrderId(orderId));
        }
        return salOrder;
    }

    /**
     * 查询销售单据列表
     * 
     * @param salOrder 销售单据
     * @return 销售单据集合
     */
    @Override
    public List<SalOrder> selectSalOrderList(SalOrder salOrder)
    {
        return salOrderMapper.selectSalOrderList(salOrder);
    }

    /**
     * 新增销售单据
     * 
     * @param salOrder 销售单据
     * @return 结果
     */
    @Override
    @Transactional
    public int insertSalOrder(SalOrder salOrder)
    {
        prepareOrderForSave(salOrder, true);
        int insertRows = salOrderMapper.insertSalOrder(salOrder);
        saveOrderItemList(salOrder);
        return insertRows;
    }

    /**
     * 修改销售单据
     * 
     * @param salOrder 销售单据
     * @return 结果
     */
    @Override
    @Transactional
    public int updateSalOrder(SalOrder salOrder)
    {
        SalOrder existingSalOrder = getExistingSalOrder(salOrder.getOrderId());
        validateEditable(existingSalOrder);
        preserveReadonlyFields(existingSalOrder, salOrder);
        prepareOrderForSave(salOrder, false);
        int updateRows = salOrderMapper.updateSalOrder(salOrder);
        salOrderItemMapper.deleteSalOrderItemByOrderId(salOrder.getOrderId());
        saveOrderItemList(salOrder);
        return updateRows;
    }

    /**
     * 审批销售单据
     * 
     * @param orderId 销售单据id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    @Transactional
    public int approveSalOrder(Long orderId, String operator)
    {
        SalOrder salOrder = getExistingSalOrder(orderId);
        validateEditable(salOrder);
        List<SalOrderItem> salOrderItemList = salOrderItemMapper.selectSalOrderItemListByOrderId(orderId);
        if (salOrderItemList == null || salOrderItemList.isEmpty())
        {
            throw new ServiceException("销售单据明细不能为空");
        }
        rebuildItemAmount(salOrder, salOrderItemList);
        fillOrderTotal(salOrder, salOrderItemList);
        validateCustomerCreditLimit(salOrder);
        salOrder.setStatus("1");
        salOrder.setBizStatus("approved");
        salOrder.setApproveBy(operator);
        salOrder.setApproveTime(new Date());
        salOrder.setUpdateBy(operator);
        return salOrderMapper.updateSalOrder(salOrder);
    }

    /**
     * 报价单下推销货订单
     * 
     * @param orderId 报价单id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    @Transactional
    public int pushQuoteToSale(Long orderId, String operator)
    {
        SalOrder sourceSalOrder = getExistingSalOrder(orderId);
        if (!"quote".equals(sourceSalOrder.getOrderType()))
        {
            throw new ServiceException("仅报价单允许下推销货订单");
        }
        if (!"approved".equals(sourceSalOrder.getBizStatus()))
        {
            throw new ServiceException("报价单审批通过后才允许下推");
        }
        SalOrder querySalOrder = new SalOrder();
        querySalOrder.setSourceBillNo(sourceSalOrder.getOrderNo());
        List<SalOrder> pushedSalOrderList = salOrderMapper.selectSalOrderList(querySalOrder);
        for (SalOrder pushedSalOrder : pushedSalOrderList)
        {
            if ("sale".equals(pushedSalOrder.getOrderType()))
            {
                throw new ServiceException("该报价单已下推销货订单");
            }
        }
        SalOrder targetSalOrder = buildDerivedSaleOrder(sourceSalOrder, operator);
        salOrderMapper.insertSalOrder(targetSalOrder);
        saveOrderItemList(targetSalOrder);
        sourceSalOrder.setBizStatus("closed");
        sourceSalOrder.setStatus("2");
        sourceSalOrder.setUpdateBy(operator);
        salOrderMapper.updateSalOrder(sourceSalOrder);
        return 1;
    }

    /**
     * 销货订单下推出库单
     * 
     * @param orderId 销货订单id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    @Transactional
    public int pushSaleOrderToOutbound(Long orderId, String operator)
    {
        SalOrder salOrder = getExistingSalOrder(orderId);
        if (!"sale".equals(salOrder.getOrderType()))
        {
            throw new ServiceException("仅销货订单允许下推出库单");
        }
        if (!"approved".equals(salOrder.getBizStatus()))
        {
            throw new ServiceException("销货订单审批通过后才允许下推");
        }
        WmsOutbound outboundQuery = new WmsOutbound();
        outboundQuery.setSourceBillNo(salOrder.getOrderNo());
        List<WmsOutbound> wmsOutboundList = wmsOutboundMapper.selectWmsOutboundList(outboundQuery);
        if (wmsOutboundList != null && !wmsOutboundList.isEmpty())
        {
            throw new ServiceException("该销货订单已下推出库单");
        }
        List<SalOrderItem> salOrderItemList = salOrderItemMapper.selectSalOrderItemListByOrderId(orderId);
        if (salOrderItemList == null || salOrderItemList.isEmpty())
        {
            throw new ServiceException("销货订单明细不能为空");
        }
        WmsOutbound wmsOutbound = new WmsOutbound();
        wmsOutbound.setOutboundType("sale_order");
        wmsOutbound.setCustomerId(salOrder.getCustomerId());
        wmsOutbound.setWarehouseId(salOrder.getWarehouseId());
        wmsOutbound.setSourceBillType("sale_order");
        wmsOutbound.setSourceBillId(salOrder.getOrderId());
        wmsOutbound.setSourceBillNo(salOrder.getOrderNo());
        wmsOutbound.setBusinessDate(salOrder.getBusinessDate());
        wmsOutbound.setStatus("0");
        wmsOutbound.setBizStatus("draft");
        wmsOutbound.setDeliveryStatus("0");
        wmsOutbound.setCreateBy(operator);
        wmsOutbound.setRemark("由销货订单下推生成");
        wmsOutboundService.insertWmsOutbound(wmsOutbound);
        for (SalOrderItem salOrderItem : salOrderItemList)
        {
            WmsOutboundItem wmsOutboundItem = new WmsOutboundItem();
            wmsOutboundItem.setOutboundId(wmsOutbound.getOutboundId());
            wmsOutboundItem.setProductId(salOrderItem.getProductId());
            wmsOutboundItem.setLocationId(0L);
            wmsOutboundItem.setBatchNo("");
            wmsOutboundItem.setQuantity(defaultAmount(salOrderItem.getQuantity()));
            wmsOutboundItem.setPrice(defaultAmount(salOrderItem.getPrice()));
            wmsOutboundItem.setAmount(defaultAmount(salOrderItem.getAmount()));
            wmsOutboundItemMapper.insertWmsOutboundItem(wmsOutboundItem);
        }
        salOrder.setBizStatus("executing");
        salOrder.setUpdateBy(operator);
        salOrderMapper.updateSalOrder(salOrder);
        return 1;
    }

    /**
     * 删除销售单据
     * 
     * @param orderId 销售单据id
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteSalOrderById(Long orderId)
    {
        SalOrder salOrder = getExistingSalOrder(orderId);
        validateEditable(salOrder);
        salOrderItemMapper.deleteSalOrderItemByOrderId(orderId);
        return salOrderMapper.deleteSalOrderById(orderId);
    }

    /**
     * 批量删除销售单据
     * 
     * @param orderIds 销售单据id集合
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteSalOrderByIds(Long[] orderIds)
    {
        for (Long orderId : orderIds)
        {
            SalOrder salOrder = getExistingSalOrder(orderId);
            validateEditable(salOrder);
            salOrderItemMapper.deleteSalOrderItemByOrderId(orderId);
        }
        return salOrderMapper.deleteSalOrderByIds(orderIds);
    }

    /**
     * 预处理销售单据
     * 
     * @param salOrder 销售单据
     * @param isNew 是否新增
     */
    private void prepareOrderForSave(SalOrder salOrder, boolean isNew)
    {
        if (StringUtils.isEmpty(salOrder.getOrderType()))
        {
            salOrder.setOrderType("sale");
        }
        if (StringUtils.isEmpty(salOrder.getOrderNo()))
        {
            salOrder.setOrderNo(generateOrderNo(salOrder.getOrderType()));
        }
        if (salOrder.getBusinessDate() == null)
        {
            salOrder.setBusinessDate(new Date());
        }
        if (isNew)
        {
            salOrder.setStatus("0");
            salOrder.setBizStatus("draft");
            salOrder.setApproveBy("");
            salOrder.setApproveTime(null);
        }
        List<SalOrderItem> salOrderItemList = normalizeOrderItemList(salOrder.getOrderItemList());
        rebuildItemAmount(salOrder, salOrderItemList);
        fillOrderTotal(salOrder, salOrderItemList);
        salOrder.setOrderItemList(salOrderItemList);
    }

    /**
     * 保存销售单据明细
     * 
     * @param salOrder 销售单据
     */
    private void saveOrderItemList(SalOrder salOrder)
    {
        for (SalOrderItem salOrderItem : salOrder.getOrderItemList())
        {
            salOrderItem.setOrderId(salOrder.getOrderId());
            salOrderItemMapper.insertSalOrderItem(salOrderItem);
        }
    }

    /**
     * 标准化销售单据明细
     * 
     * @param orderItemList 销售单据明细集合
     * @return 标准化后的明细集合
     */
    private List<SalOrderItem> normalizeOrderItemList(List<SalOrderItem> orderItemList)
    {
        List<SalOrderItem> normalizedOrderItemList = orderItemList == null ? new ArrayList<SalOrderItem>() : orderItemList;
        if (normalizedOrderItemList.isEmpty())
        {
            throw new ServiceException("销售单据明细不能为空");
        }
        return normalizedOrderItemList;
    }

    /**
     * 重建明细金额
     * 
     * @param salOrder 销售单据
     * @param salOrderItemList 销售单据明细集合
     */
    private void rebuildItemAmount(SalOrder salOrder, List<SalOrderItem> salOrderItemList)
    {
        for (SalOrderItem salOrderItem : salOrderItemList)
        {
            if (salOrderItem.getProductId() == null)
            {
                throw new ServiceException("销售单据明细商品不能为空");
            }
            BigDecimal itemQuantity = defaultAmount(salOrderItem.getQuantity());
            if (itemQuantity.compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("销售单据明细数量必须大于0");
            }
            if (salOrderItem.getPrice() == null || salOrderItem.getPrice().compareTo(BigDecimal.ZERO) <= 0)
            {
                salOrderItem.setPrice(priceResolverService.resolveSalePrice(salOrder.getCustomerId(), salOrderItem.getProductId()));
            }
            salOrderItem.setQuantity(itemQuantity);
            salOrderItem.setAmount(defaultAmount(salOrderItem.getPrice()).multiply(itemQuantity));
        }
    }

    /**
     * 汇总单据总数
     * 
     * @param salOrder 销售单据
     * @param salOrderItemList 销售单据明细集合
     */
    private void fillOrderTotal(SalOrder salOrder, List<SalOrderItem> salOrderItemList)
    {
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (SalOrderItem salOrderItem : salOrderItemList)
        {
            totalQuantity = totalQuantity.add(defaultAmount(salOrderItem.getQuantity()));
            totalAmount = totalAmount.add(defaultAmount(salOrderItem.getAmount()));
        }
        salOrder.setTotalQty(totalQuantity);
        salOrder.setTotalAmount(totalAmount);
    }

    /**
     * 校验客户信用额度
     * 
     * @param salOrder 销售单据
     */
    private void validateCustomerCreditLimit(SalOrder salOrder)
    {
        if (!"sale".equals(salOrder.getOrderType()) || salOrder.getCustomerId() == null)
        {
            return;
        }
        MdCustomer mdCustomer = mdCustomerMapper.selectMdCustomerById(salOrder.getCustomerId());
        if (mdCustomer == null || defaultAmount(mdCustomer.getCreditLimit()).compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }
        FinReceivable finReceivableQuery = new FinReceivable();
        finReceivableQuery.setCustomerId(salOrder.getCustomerId());
        List<FinReceivable> finReceivableList = finReceivableMapper.selectFinReceivableList(finReceivableQuery);
        BigDecimal outstandingReceivableAmount = BigDecimal.ZERO;
        for (FinReceivable finReceivable : finReceivableList)
        {
            if (!"confirmed".equals(finReceivable.getBizStatus()) || "2".equals(finReceivable.getStatus()))
            {
                continue;
            }
            BigDecimal unsettledAmount = defaultAmount(finReceivable.getAmount()).subtract(defaultAmount(finReceivable.getReceivedAmount()));
            if (unsettledAmount.compareTo(BigDecimal.ZERO) > 0)
            {
                outstandingReceivableAmount = outstandingReceivableAmount.add(unsettledAmount);
            }
        }
        BigDecimal projectedReceivableAmount = outstandingReceivableAmount.add(defaultAmount(salOrder.getTotalAmount()));
        if (projectedReceivableAmount.compareTo(defaultAmount(mdCustomer.getCreditLimit())) > 0)
        {
            throw new ServiceException("客户信用额度不足，当前未收金额与本单金额合计超出信用额度");
        }
    }

    /**
     * 获取已存在销售单据
     * 
     * @param orderId 销售单据id
     * @return 销售单据
     */
    private SalOrder getExistingSalOrder(Long orderId)
    {
        SalOrder salOrder = salOrderMapper.selectSalOrderById(orderId);
        if (salOrder == null)
        {
            throw new ServiceException("销售单据不存在");
        }
        return salOrder;
    }

    /**
     * 校验是否允许编辑
     * 
     * @param salOrder 销售单据
     */
    private void validateEditable(SalOrder salOrder)
    {
        if (!"draft".equals(salOrder.getBizStatus()))
        {
            throw new ServiceException("当前销售单据状态不允许编辑");
        }
    }

    /**
     * 保留只读字段
     * 
     * @param existingSalOrder 原销售单据
     * @param salOrder 新销售单据
     */
    private void preserveReadonlyFields(SalOrder existingSalOrder, SalOrder salOrder)
    {
        salOrder.setOrderNo(existingSalOrder.getOrderNo());
        salOrder.setOrderType(existingSalOrder.getOrderType());
        salOrder.setStatus(existingSalOrder.getStatus());
        salOrder.setBizStatus(existingSalOrder.getBizStatus());
        salOrder.setApproveBy(existingSalOrder.getApproveBy());
        salOrder.setApproveTime(existingSalOrder.getApproveTime());
    }

    /**
     * 构建下推销货订单
     * 
     * @param sourceSalOrder 原报价单
     * @param operator 操作人
     * @return 销货订单
     */
    private SalOrder buildDerivedSaleOrder(SalOrder sourceSalOrder, String operator)
    {
        SalOrder targetSalOrder = new SalOrder();
        targetSalOrder.setOrderNo(generateOrderNo("sale"));
        targetSalOrder.setOrderType("sale");
        targetSalOrder.setCustomerId(sourceSalOrder.getCustomerId());
        targetSalOrder.setWarehouseId(sourceSalOrder.getWarehouseId());
        targetSalOrder.setSourceBillType("quote");
        targetSalOrder.setSourceBillId(sourceSalOrder.getOrderId());
        targetSalOrder.setSourceBillNo(sourceSalOrder.getOrderNo());
        targetSalOrder.setBusinessDate(sourceSalOrder.getBusinessDate());
        targetSalOrder.setExpectedDate(sourceSalOrder.getExpectedDate());
        targetSalOrder.setStatus("0");
        targetSalOrder.setBizStatus("draft");
        targetSalOrder.setCreateBy(operator);
        targetSalOrder.setRemark("由报价单下推生成");
        List<SalOrderItem> sourceOrderItemList = salOrderItemMapper.selectSalOrderItemListByOrderId(sourceSalOrder.getOrderId());
        List<SalOrderItem> targetOrderItemList = new ArrayList<SalOrderItem>();
        for (SalOrderItem sourceOrderItem : sourceOrderItemList)
        {
            SalOrderItem targetOrderItem = new SalOrderItem();
            targetOrderItem.setProductId(sourceOrderItem.getProductId());
            targetOrderItem.setQuantity(sourceOrderItem.getQuantity());
            targetOrderItem.setPrice(sourceOrderItem.getPrice());
            targetOrderItem.setAmount(sourceOrderItem.getAmount());
            targetOrderItem.setRemark(sourceOrderItem.getRemark());
            targetOrderItemList.add(targetOrderItem);
        }
        targetSalOrder.setOrderItemList(targetOrderItemList);
        fillOrderTotal(targetSalOrder, targetOrderItemList);
        return targetSalOrder;
    }

    /**
     * 生成销售单据号
     * 
     * @param orderType 单据类型
     * @return 销售单据号
     */
    private String generateOrderNo(String orderType)
    {
        String orderPrefix = ("quote".equals(orderType) ? "quo" : "sal") + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String currentMaxOrderNo = salOrderMapper.selectMaxOrderNoByPrefix(orderPrefix);
        int nextSequence = 1;
        if (StringUtils.isNotEmpty(currentMaxOrderNo) && currentMaxOrderNo.length() > orderPrefix.length())
        {
            String sequenceText = currentMaxOrderNo.substring(orderPrefix.length());
            if (StringUtils.isNumeric(sequenceText))
            {
                nextSequence = Integer.parseInt(sequenceText) + 1;
            }
        }
        return orderPrefix + String.format("%03d", nextSequence);
    }

    /**
     * 默认金额处理
     * 
     * @param amount 金额
     * @return 默认金额
     */
    private BigDecimal defaultAmount(BigDecimal amount)
    {
        return amount == null ? BigDecimal.ZERO : amount;
    }
}
