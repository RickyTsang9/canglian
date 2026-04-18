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
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.PurOrder;
import com.canglian.business.domain.PurOrderItem;
import com.canglian.business.domain.WmsInbound;
import com.canglian.business.domain.WmsInboundItem;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.PurOrderItemMapper;
import com.canglian.business.mapper.PurOrderMapper;
import com.canglian.business.mapper.WmsInboundItemMapper;
import com.canglian.business.mapper.WmsInboundMapper;
import com.canglian.business.service.IWmsInboundService;
import com.canglian.business.service.IPurOrderService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 购货订单服务实现
 * 
 * @author canglian
 */
@Service
public class PurOrderServiceImpl implements IPurOrderService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private PurOrderMapper purOrderMapper;

    @Autowired
    private PurOrderItemMapper purOrderItemMapper;

    @Autowired
    private WmsInboundMapper wmsInboundMapper;

    @Autowired
    private WmsInboundItemMapper wmsInboundItemMapper;

    @Autowired
    private MdProductMapper mdProductMapper;

    @Autowired
    private IWmsInboundService wmsInboundService;

    /**
     * 查询购货订单详情
     * 
     * @param purchaseOrderId 购货订单id
     * @return 购货订单详情
     */
    @Override
    public PurOrder selectPurOrderById(Long purchaseOrderId)
    {
        PurOrder purOrder = purOrderMapper.selectPurOrderById(purchaseOrderId);
        if (purOrder != null)
        {
            purOrder.setOrderItemList(purOrderItemMapper.selectPurOrderItemListByOrderId(purchaseOrderId));
        }
        return purOrder;
    }

    /**
     * 查询购货订单列表
     * 
     * @param purOrder 购货订单
     * @return 购货订单集合
     */
    @Override
    public List<PurOrder> selectPurOrderList(PurOrder purOrder)
    {
        return purOrderMapper.selectPurOrderList(purOrder);
    }

    /**
     * 新增购货订单
     * 
     * @param purOrder 购货订单
     * @return 结果
     */
    @Override
    @Transactional
    public int insertPurOrder(PurOrder purOrder)
    {
        prepareOrderForSave(purOrder, true);
        int insertRows = purOrderMapper.insertPurOrder(purOrder);
        saveOrderItemList(purOrder);
        return insertRows;
    }

    /**
     * 修改购货订单
     * 
     * @param purOrder 购货订单
     * @return 结果
     */
    @Override
    @Transactional
    public int updatePurOrder(PurOrder purOrder)
    {
        PurOrder existingPurOrder = getExistingPurOrder(purOrder.getPurchaseOrderId());
        validateEditable(existingPurOrder);
        preserveReadonlyFields(existingPurOrder, purOrder);
        prepareOrderForSave(purOrder, false);
        int updateRows = purOrderMapper.updatePurOrder(purOrder);
        purOrderItemMapper.deletePurOrderItemByOrderId(purOrder.getPurchaseOrderId());
        saveOrderItemList(purOrder);
        return updateRows;
    }

    /**
     * 审批购货订单
     * 
     * @param purchaseOrderId 购货订单id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    @Transactional
    public int approvePurOrder(Long purchaseOrderId, String operator)
    {
        PurOrder purOrder = getExistingPurOrder(purchaseOrderId);
        validateEditable(purOrder);
        List<PurOrderItem> purOrderItemList = purOrderItemMapper.selectPurOrderItemListByOrderId(purchaseOrderId);
        if (purOrderItemList == null || purOrderItemList.isEmpty())
        {
            throw new ServiceException("购货订单明细不能为空");
        }
        rebuildItemAmount(purOrderItemList);
        fillOrderTotal(purOrder, purOrderItemList);
        purOrder.setStatus("1");
        purOrder.setBizStatus("approved");
        purOrder.setApproveBy(operator);
        purOrder.setApproveTime(new Date());
        purOrder.setUpdateBy(operator);
        return purOrderMapper.updatePurOrder(purOrder);
    }

    /**
     * 下推入库单
     * 
     * @param purchaseOrderId 购货订单id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    @Transactional
    public int pushPurOrderToInbound(Long purchaseOrderId, String operator)
    {
        PurOrder purOrder = getExistingPurOrder(purchaseOrderId);
        if (!"approved".equals(purOrder.getBizStatus()))
        {
            throw new ServiceException("购货订单审批通过后才允许下推");
        }
        WmsInbound wmsInboundQuery = new WmsInbound();
        wmsInboundQuery.setSourceBillNo(purOrder.getPurchaseOrderNo());
        List<WmsInbound> wmsInboundList = wmsInboundMapper.selectWmsInboundList(wmsInboundQuery);
        if (wmsInboundList != null && !wmsInboundList.isEmpty())
        {
            throw new ServiceException("该购货订单已下推入库单");
        }
        List<PurOrderItem> purOrderItemList = purOrderItemMapper.selectPurOrderItemListByOrderId(purchaseOrderId);
        if (purOrderItemList == null || purOrderItemList.isEmpty())
        {
            throw new ServiceException("购货订单明细不能为空");
        }
        WmsInbound wmsInbound = new WmsInbound();
        wmsInbound.setInboundType("purchase_order");
        wmsInbound.setSupplierId(purOrder.getSupplierId());
        wmsInbound.setWarehouseId(purOrder.getWarehouseId());
        wmsInbound.setSourceBillType("pur_order");
        wmsInbound.setSourceBillId(purOrder.getPurchaseOrderId());
        wmsInbound.setSourceBillNo(purOrder.getPurchaseOrderNo());
        wmsInbound.setBusinessDate(purOrder.getBusinessDate());
        wmsInbound.setStatus("0");
        wmsInbound.setBizStatus("draft");
        wmsInbound.setCreateBy(operator);
        wmsInbound.setRemark("由购货订单下推生成");
        wmsInboundService.insertWmsInbound(wmsInbound);
        for (PurOrderItem purOrderItem : purOrderItemList)
        {
            WmsInboundItem wmsInboundItem = new WmsInboundItem();
            wmsInboundItem.setInboundId(wmsInbound.getInboundId());
            wmsInboundItem.setProductId(purOrderItem.getProductId());
            wmsInboundItem.setLocationId(0L);
            wmsInboundItem.setBatchNo("");
            wmsInboundItem.setQuantity(defaultAmount(purOrderItem.getQuantity()));
            wmsInboundItem.setPrice(defaultAmount(purOrderItem.getPrice()));
            wmsInboundItem.setAmount(defaultAmount(purOrderItem.getAmount()));
            wmsInboundItemMapper.insertWmsInboundItem(wmsInboundItem);
        }
        purOrder.setBizStatus("executing");
        purOrder.setUpdateBy(operator);
        purOrderMapper.updatePurOrder(purOrder);
        return 1;
    }

    /**
     * 删除购货订单
     * 
     * @param purchaseOrderId 购货订单id
     * @return 结果
     */
    @Override
    @Transactional
    public int deletePurOrderById(Long purchaseOrderId)
    {
        PurOrder purOrder = getExistingPurOrder(purchaseOrderId);
        validateEditable(purOrder);
        purOrderItemMapper.deletePurOrderItemByOrderId(purchaseOrderId);
        return purOrderMapper.deletePurOrderById(purchaseOrderId);
    }

    /**
     * 批量删除购货订单
     * 
     * @param purchaseOrderIds 购货订单id集合
     * @return 结果
     */
    @Override
    @Transactional
    public int deletePurOrderByIds(Long[] purchaseOrderIds)
    {
        for (Long purchaseOrderId : purchaseOrderIds)
        {
            PurOrder purOrder = getExistingPurOrder(purchaseOrderId);
            validateEditable(purOrder);
            purOrderItemMapper.deletePurOrderItemByOrderId(purchaseOrderId);
        }
        return purOrderMapper.deletePurOrderByIds(purchaseOrderIds);
    }

    /**
     * 预处理购货订单
     * 
     * @param purOrder 购货订单
     * @param isNew 是否新增
     */
    private void prepareOrderForSave(PurOrder purOrder, boolean isNew)
    {
        if (StringUtils.isEmpty(purOrder.getPurchaseOrderNo()))
        {
            purOrder.setPurchaseOrderNo(generateOrderNo());
        }
        if (purOrder.getBusinessDate() == null)
        {
            purOrder.setBusinessDate(new Date());
        }
        if (isNew)
        {
            purOrder.setStatus("0");
            purOrder.setBizStatus("draft");
            purOrder.setApproveBy("");
            purOrder.setApproveTime(null);
        }
        List<PurOrderItem> purOrderItemList = normalizeOrderItemList(purOrder.getOrderItemList());
        rebuildItemAmount(purOrderItemList);
        fillOrderTotal(purOrder, purOrderItemList);
        purOrder.setOrderItemList(purOrderItemList);
    }

    /**
     * 保存购货订单明细
     * 
     * @param purOrder 购货订单
     */
    private void saveOrderItemList(PurOrder purOrder)
    {
        for (PurOrderItem purOrderItem : purOrder.getOrderItemList())
        {
            purOrderItem.setPurchaseOrderId(purOrder.getPurchaseOrderId());
            purOrderItemMapper.insertPurOrderItem(purOrderItem);
        }
    }

    /**
     * 标准化购货订单明细
     * 
     * @param orderItemList 购货订单明细集合
     * @return 购货订单明细集合
     */
    private List<PurOrderItem> normalizeOrderItemList(List<PurOrderItem> orderItemList)
    {
        List<PurOrderItem> normalizedOrderItemList = orderItemList == null ? new ArrayList<PurOrderItem>() : orderItemList;
        if (normalizedOrderItemList.isEmpty())
        {
            throw new ServiceException("购货订单明细不能为空");
        }
        return normalizedOrderItemList;
    }

    /**
     * 重建明细金额
     * 
     * @param purOrderItemList 购货订单明细集合
     */
    private void rebuildItemAmount(List<PurOrderItem> purOrderItemList)
    {
        for (PurOrderItem purOrderItem : purOrderItemList)
        {
            if (purOrderItem.getProductId() == null)
            {
                throw new ServiceException("购货订单明细商品不能为空");
            }
            BigDecimal itemQuantity = defaultAmount(purOrderItem.getQuantity());
            if (itemQuantity.compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("购货订单明细数量必须大于0");
            }
            if (purOrderItem.getPrice() == null || purOrderItem.getPrice().compareTo(BigDecimal.ZERO) <= 0)
            {
                MdProduct mdProduct = mdProductMapper.selectMdProductById(purOrderItem.getProductId());
                purOrderItem.setPrice(mdProduct == null ? BigDecimal.ZERO : defaultAmount(mdProduct.getCostPrice()));
            }
            purOrderItem.setQuantity(itemQuantity);
            purOrderItem.setAmount(defaultAmount(purOrderItem.getPrice()).multiply(itemQuantity));
        }
    }

    /**
     * 汇总订单金额
     * 
     * @param purOrder 购货订单
     * @param purOrderItemList 购货订单明细集合
     */
    private void fillOrderTotal(PurOrder purOrder, List<PurOrderItem> purOrderItemList)
    {
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurOrderItem purOrderItem : purOrderItemList)
        {
            totalQuantity = totalQuantity.add(defaultAmount(purOrderItem.getQuantity()));
            totalAmount = totalAmount.add(defaultAmount(purOrderItem.getAmount()));
        }
        purOrder.setTotalQty(totalQuantity);
        purOrder.setTotalAmount(totalAmount);
    }

    /**
     * 获取已存在购货订单
     * 
     * @param purchaseOrderId 购货订单id
     * @return 购货订单
     */
    private PurOrder getExistingPurOrder(Long purchaseOrderId)
    {
        PurOrder purOrder = purOrderMapper.selectPurOrderById(purchaseOrderId);
        if (purOrder == null)
        {
            throw new ServiceException("购货订单不存在");
        }
        return purOrder;
    }

    /**
     * 校验是否允许编辑
     * 
     * @param purOrder 购货订单
     */
    private void validateEditable(PurOrder purOrder)
    {
        if (!"draft".equals(purOrder.getBizStatus()))
        {
            throw new ServiceException("当前购货订单状态不允许编辑");
        }
    }

    /**
     * 保留只读字段
     * 
     * @param existingPurOrder 原购货订单
     * @param purOrder 新购货订单
     */
    private void preserveReadonlyFields(PurOrder existingPurOrder, PurOrder purOrder)
    {
        purOrder.setPurchaseOrderNo(existingPurOrder.getPurchaseOrderNo());
        purOrder.setStatus(existingPurOrder.getStatus());
        purOrder.setBizStatus(existingPurOrder.getBizStatus());
        purOrder.setApproveBy(existingPurOrder.getApproveBy());
        purOrder.setApproveTime(existingPurOrder.getApproveTime());
    }

    /**
     * 生成购货订单号
     * 
     * @return 购货订单号
     */
    private String generateOrderNo()
    {
        String orderPrefix = "pur" + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String currentMaxOrderNo = purOrderMapper.selectMaxPurchaseOrderNoByPrefix(orderPrefix);
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
