package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.FinPayable;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.MdSupplier;
import com.canglian.business.domain.PurOrder;
import com.canglian.common.exception.ServiceException;
import com.canglian.business.domain.WmsInbound;
import com.canglian.business.domain.WmsInboundItem;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsStockLog;
import com.canglian.business.mapper.FinPayableMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.MdSupplierMapper;
import com.canglian.business.mapper.PurOrderMapper;
import com.canglian.business.mapper.WmsInboundMapper;
import com.canglian.business.mapper.WmsInboundItemMapper;
import com.canglian.business.mapper.WmsStockLogMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IFinPayableService;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.business.service.IWmsInboundService;
import com.canglian.common.utils.StringUtils;

/**
 * 入库单 服务层实现
 * 
 * @author canglian
 */
@Service
public class WmsInboundServiceImpl implements IWmsInboundService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private WmsInboundMapper wmsInboundMapper;

    @Autowired
    private WmsInboundItemMapper wmsInboundItemMapper;

    @Autowired
    private WmsStockMapper wmsStockMapper;

    @Autowired
    private WmsStockLogMapper wmsStockLogMapper;

    @Autowired
    private FinPayableMapper finPayableMapper;

    @Autowired
    private MdSupplierMapper mdSupplierMapper;

    @Autowired
    private MdProductMapper mdProductMapper;

    @Autowired
    private PurOrderMapper purOrderMapper;

    @Autowired
    private IFinPayableService finPayableService;

    @Autowired
    private IFinVoucherEventService finVoucherEventService;

    /**
     * 查询入库单信息
     * 
     * @param inboundId 入库单id
     * @return 入库单信息
     */
    @Override
    public WmsInbound selectWmsInboundById(Long inboundId)
    {
        return wmsInboundMapper.selectWmsInboundById(inboundId);
    }

    /**
     * 查询入库单列表
     * 
     * @param wmsInbound 入库单信息
     * @return 入库单集合
     */
    @Override
    public List<WmsInbound> selectWmsInboundList(WmsInbound wmsInbound)
    {
        return wmsInboundMapper.selectWmsInboundList(wmsInbound);
    }

    /**
     * 新增入库单
     * 
     * @param wmsInbound 入库单信息
     * @return 结果
     */
    @Override
    public int insertWmsInbound(WmsInbound wmsInbound)
    {
        if (StringUtils.isEmpty(wmsInbound.getInboundNo()))
        {
            wmsInbound.setInboundNo(generateInboundNo());
        }
        if (wmsInbound.getBusinessDate() == null)
        {
            wmsInbound.setBusinessDate(new Date());
        }
        wmsInbound.setTotalQty(null);
        wmsInbound.setTotalAmount(null);
        wmsInbound.setStatus("0");
        wmsInbound.setBizStatus("draft");
        wmsInbound.setAuditBy(null);
        wmsInbound.setAuditTime(null);
        return wmsInboundMapper.insertWmsInbound(wmsInbound);
    }

    /**
     * 修改入库单
     * 
     * @param wmsInbound 入库单信息
     * @return 结果
     */
    @Override
    public int updateWmsInbound(WmsInbound wmsInbound)
    {
        WmsInbound inbound = getExistingInbound(wmsInbound.getInboundId());
        validateInboundEditable(inbound);
        wmsInbound.setTotalQty(inbound.getTotalQty());
        wmsInbound.setTotalAmount(inbound.getTotalAmount());
        wmsInbound.setStatus(inbound.getStatus());
        wmsInbound.setBizStatus(inbound.getBizStatus());
        wmsInbound.setAuditBy(inbound.getAuditBy());
        wmsInbound.setAuditTime(inbound.getAuditTime());
        return wmsInboundMapper.updateWmsInbound(wmsInbound);
    }

    /**
     * 删除入库单
     * 
     * @param inboundId 入库单id
     * @return 结果
     */
    @Override
    public int deleteWmsInboundById(Long inboundId)
    {
        checkInboundDeletable(inboundId);
        return wmsInboundMapper.deleteWmsInboundById(inboundId);
    }

    /**
     * 批量删除入库单
     * 
     * @param inboundIds 需要删除的入库单id
     * @return 结果
     */
    @Override
    public int deleteWmsInboundByIds(Long[] inboundIds)
    {
        for (Long inboundId : inboundIds)
        {
            checkInboundDeletable(inboundId);
        }
        return wmsInboundMapper.deleteWmsInboundByIds(inboundIds);
    }

    /**
     * 入库单审核
     * 
     * @param inboundId 入库单id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    @Transactional
    public int auditWmsInbound(Long inboundId, String operator)
    {
        WmsInbound inbound = getExistingInbound(inboundId);
        if (!"0".equals(inbound.getStatus()))
        {
            throw new ServiceException("入库单状态已变更，无法审核");
        }
        WmsInboundItem inboundItemQuery = new WmsInboundItem();
        inboundItemQuery.setInboundId(inboundId);
        List<WmsInboundItem> inboundItemList = wmsInboundItemMapper.selectWmsInboundItemList(inboundItemQuery);
        if (inboundItemList == null || inboundItemList.isEmpty())
        {
            throw new ServiceException("入库单明细不能为空");
        }
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (WmsInboundItem inboundItem : inboundItemList)
        {
            BigDecimal itemQuantity = defaultBigDecimal(inboundItem.getQuantity());
            if (itemQuantity.compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("入库数量必须大于0");
            }
            if (inboundItem.getProductId() == null)
            {
                throw new ServiceException("入库明细商品不能为空");
            }
            if (inbound.getWarehouseId() == null)
            {
                throw new ServiceException("入库仓库不能为空");
            }
            Long locationId = defaultLocationId(inboundItem.getLocationId());
            String batchNo = defaultBatchNo(inboundItem.getBatchNo());
            WmsStock stockQuery = new WmsStock();
            stockQuery.setWarehouseId(inbound.getWarehouseId());
            stockQuery.setProductId(inboundItem.getProductId());
            stockQuery.setLocationId(locationId);
            stockQuery.setBatchNo(batchNo);
            WmsStock stock = wmsStockMapper.selectWmsStockByKey(stockQuery);
            BigDecimal beforeQuantity;
            if (stock == null)
            {
                stock = new WmsStock();
                stock.setWarehouseId(inbound.getWarehouseId());
                stock.setProductId(inboundItem.getProductId());
                stock.setLocationId(locationId);
                stock.setBatchNo(batchNo);
                stock.setQuantity(itemQuantity);
                stock.setLockedQuantity(BigDecimal.ZERO);
                stock.setFrozenQuantity(BigDecimal.ZERO);
                fillStockWarningQty(stock, inboundItem.getProductId());
                stock.setVersion(0L);
                stock.setCreateBy(operator);
                wmsStockMapper.insertWmsStock(stock);
                beforeQuantity = BigDecimal.ZERO;
            }
            else
            {
                beforeQuantity = defaultBigDecimal(stock.getQuantity());
                stock.setQuantity(beforeQuantity.add(itemQuantity));
                stock.setUpdateBy(operator);
                wmsStockMapper.updateWmsStock(stock);
            }
            BigDecimal afterQuantity = beforeQuantity.add(itemQuantity);
            WmsStockLog stockLog = new WmsStockLog();
            stockLog.setWarehouseId(inbound.getWarehouseId());
            stockLog.setProductId(inboundItem.getProductId());
            stockLog.setLocationId(locationId);
            stockLog.setBatchNo(batchNo);
            stockLog.setBillType("inbound");
            stockLog.setBillId(inboundId);
            stockLog.setInOut("1");
            stockLog.setQuantity(itemQuantity);
            BigDecimal itemPrice = defaultBigDecimal(inboundItem.getPrice());
            BigDecimal itemAmount = inboundItem.getAmount();
            if (itemAmount == null)
            {
                itemAmount = itemPrice.multiply(itemQuantity);
            }
            stockLog.setPrice(itemPrice);
            stockLog.setAmount(itemAmount);
            stockLog.setBeforeQty(beforeQuantity);
            stockLog.setAfterQty(afterQuantity);
            stockLog.setCreateBy(operator);
            wmsStockLogMapper.insertWmsStockLog(stockLog);
            totalQuantity = totalQuantity.add(itemQuantity);
            totalAmount = totalAmount.add(itemAmount);
        }
        inbound.setStatus("1");
        inbound.setAuditBy(operator);
        inbound.setAuditTime(new Date());
        inbound.setUpdateBy(operator);
        inbound.setTotalQty(totalQuantity);
        inbound.setTotalAmount(totalAmount);
        inbound.setBizStatus("completed");
        int updateRows = wmsInboundMapper.updateWmsInbound(inbound);
        if (updateRows > 0)
        {
            syncPayableCandidate(inbound, operator);
            syncSourcePurchaseOrder(inbound, operator);
            finVoucherEventService.recordVoucherEvent("inbound", inbound.getInboundId(), inbound.getInboundNo(), "inbound_complete",
                inbound.getBusinessDate(), inbound.getTotalAmount(), operator, "采购入库完成待生成凭证");
        }
        return updateRows;
    }

    /**
     * 空值转默认值
     * 
     * @param value 数值
     * @return 默认数值
     */
    private BigDecimal defaultBigDecimal(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO : value;
    }

    /**
     * 空库位转默认值
     * 
     * @param locationId 库位id
     * @return 默认库位id
     */
    private Long defaultLocationId(Long locationId)
    {
        return locationId == null ? 0L : locationId;
    }

    /**
     * 空批次转默认值
     * 
     * @param batchNo 批次号
     * @return 默认批次号
     */
    private String defaultBatchNo(String batchNo)
    {
        return batchNo == null ? "" : batchNo;
    }

    /**
     * 同步应付候选单
     * 
     * @param inbound 入库单信息
     * @param operator 操作人
     */
    private void syncPayableCandidate(WmsInbound inbound, String operator)
    {
        if (inbound.getSupplierId() == null || defaultBigDecimal(inbound.getTotalAmount()).compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }
        FinPayable existingFinPayable = selectLinkedPayable(inbound);
        if (existingFinPayable == null)
        {
            FinPayable finPayable = buildPayableCandidate(inbound, operator);
            finPayableService.insertFinPayable(finPayable);
            finPayableService.auditFinPayable(finPayable.getPayableId(), operator);
            return;
        }
        if (defaultBigDecimal(existingFinPayable.getPaidAmount()).compareTo(defaultBigDecimal(inbound.getTotalAmount())) > 0)
        {
            throw new ServiceException("应付候选单已付款金额大于当前入库金额，无法自动回写");
        }
        existingFinPayable.setSupplierId(inbound.getSupplierId());
        existingFinPayable.setBillType("inbound");
        existingFinPayable.setBillId(inbound.getInboundId());
        existingFinPayable.setSourceBillType(inbound.getSourceBillType());
        existingFinPayable.setSourceBillId(inbound.getSourceBillId());
        existingFinPayable.setSourceBillNo(inbound.getSourceBillNo());
        existingFinPayable.setBusinessDate(inbound.getBusinessDate());
        existingFinPayable.setAmount(inbound.getTotalAmount());
        existingFinPayable.setDueDate(resolvePayableDueDate(inbound.getSupplierId(), inbound.getBusinessDate()));
        existingFinPayable.setStatus(calculatePayableStatus(existingFinPayable.getPaidAmount(), inbound.getTotalAmount()));
        existingFinPayable.setBizStatus("confirmed");
        existingFinPayable.setUpdateBy(operator);
        existingFinPayable.setRemark(buildInboundPayableRemark(inbound));
        finPayableMapper.updateFinPayable(existingFinPayable);
    }

    /**
     * 查询已关联的应付候选单
     * 
     * @param inbound 入库单信息
     * @return 应付候选单
     */
    private FinPayable selectLinkedPayable(WmsInbound inbound)
    {
        FinPayable finPayableQuery = new FinPayable();
        finPayableQuery.setBillType("inbound");
        if (StringUtils.isNotEmpty(inbound.getSourceBillNo()))
        {
            finPayableQuery.setSourceBillNo(inbound.getSourceBillNo());
        }
        List<FinPayable> finPayableList = finPayableMapper.selectFinPayableList(finPayableQuery);
        for (FinPayable finPayable : finPayableList)
        {
            if ("inbound".equals(finPayable.getBillType()) && inbound.getInboundId().equals(finPayable.getBillId()))
            {
                return finPayable;
            }
        }
        return null;
    }

    /**
     * 构建应付候选单
     * 
     * @param inbound 入库单信息
     * @param operator 操作人
     * @return 应付候选单
     */
    private FinPayable buildPayableCandidate(WmsInbound inbound, String operator)
    {
        FinPayable finPayable = new FinPayable();
        finPayable.setSupplierId(inbound.getSupplierId());
        finPayable.setBillType("inbound");
        finPayable.setBillId(inbound.getInboundId());
        finPayable.setSourceBillType(inbound.getSourceBillType());
        finPayable.setSourceBillId(inbound.getSourceBillId());
        finPayable.setSourceBillNo(inbound.getSourceBillNo());
        finPayable.setBusinessDate(inbound.getBusinessDate());
        finPayable.setAmount(inbound.getTotalAmount());
        finPayable.setDueDate(resolvePayableDueDate(inbound.getSupplierId(), inbound.getBusinessDate()));
        finPayable.setCreateBy(operator);
        finPayable.setRemark(buildInboundPayableRemark(inbound));
        return finPayable;
    }

    /**
     * 构建入库应付备注
     * 
     * @param inbound 入库单信息
     * @return 备注
     */
    private String buildInboundPayableRemark(WmsInbound inbound)
    {
        return "采购入库完成自动生成，关联入库单号：" + inbound.getInboundNo();
    }

    /**
     * 计算应付状态
     * 
     * @param paidAmount 已付金额
     * @param payableAmount 应付金额
     * @return 应付状态
     */
    private String calculatePayableStatus(BigDecimal paidAmount, BigDecimal payableAmount)
    {
        BigDecimal paidAmountValue = defaultBigDecimal(paidAmount);
        BigDecimal payableAmountValue = defaultBigDecimal(payableAmount);
        if (paidAmountValue.compareTo(BigDecimal.ZERO) <= 0)
        {
            return "0";
        }
        if (paidAmountValue.compareTo(payableAmountValue) >= 0)
        {
            return "2";
        }
        return "1";
    }

    /**
     * 计算应付到期日
     * 
     * @param supplierId 供应商编号
     * @param businessDate 业务日期
     * @return 到期日
     */
    private Date resolvePayableDueDate(Long supplierId, Date businessDate)
    {
        MdSupplier mdSupplier = mdSupplierMapper.selectMdSupplierById(supplierId);
        int payableDays = mdSupplier == null || mdSupplier.getPayableDays() == null ? 30 : mdSupplier.getPayableDays();
        Calendar dueDateCalendar = Calendar.getInstance();
        dueDateCalendar.setTime(businessDate == null ? new Date() : businessDate);
        dueDateCalendar.add(Calendar.DAY_OF_MONTH, payableDays);
        return dueDateCalendar.getTime();
    }

    /**
     * 同步来源购货订单状态
     * 
     * @param inbound 入库单信息
     * @param operator 操作人
     */
    private void syncSourcePurchaseOrder(WmsInbound inbound, String operator)
    {
        if (!"pur_order".equals(inbound.getSourceBillType()) || inbound.getSourceBillId() == null)
        {
            return;
        }
        PurOrder purOrder = purOrderMapper.selectPurOrderById(inbound.getSourceBillId());
        if (purOrder == null)
        {
            return;
        }
        purOrder.setStatus("2");
        purOrder.setBizStatus("completed");
        purOrder.setUpdateBy(operator);
        purOrderMapper.updatePurOrder(purOrder);
    }

    /**
     * 按商品档案填充库存预警阈值
     * 
     * @param stock 库存信息
     * @param productId 商品id
     */
    private void fillStockWarningQty(WmsStock stock, Long productId)
    {
        MdProduct mdProduct = mdProductMapper.selectMdProductById(productId);
        stock.setWarningMinQty(mdProduct == null ? BigDecimal.ZERO : defaultBigDecimal(mdProduct.getWarningMinQty()));
        stock.setWarningMaxQty(mdProduct == null ? BigDecimal.ZERO : defaultBigDecimal(mdProduct.getWarningMaxQty()));
    }

    /**
     * 获取存在的入库单
     * 
     * @param inboundId 入库单id
     * @return 入库单信息
     */
    private WmsInbound getExistingInbound(Long inboundId)
    {
        WmsInbound inbound = wmsInboundMapper.selectWmsInboundById(inboundId);
        if (inbound == null)
        {
            throw new ServiceException("入库单不存在");
        }
        return inbound;
    }

    /**
     * 校验入库单是否允许修改
     * 
     * @param inbound 入库单信息
     */
    private void validateInboundEditable(WmsInbound inbound)
    {
        if (!"0".equals(inbound.getStatus()))
        {
            throw new ServiceException("入库单已审核，无法修改");
        }
    }

    /**
     * 生成入库单号
     * 
     * @return 入库单号
     */
    private String generateInboundNo()
    {
        String noPrefix = "inb" + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String currentMaxNo = wmsInboundMapper.selectMaxInboundNoByPrefix(noPrefix);
        int nextSequence = 1;
        if (StringUtils.isNotEmpty(currentMaxNo) && currentMaxNo.length() > noPrefix.length())
        {
            String sequenceText = currentMaxNo.substring(noPrefix.length());
            if (StringUtils.isNumeric(sequenceText))
            {
                nextSequence = Integer.parseInt(sequenceText) + 1;
            }
        }
        return noPrefix + String.format("%03d", nextSequence);
    }

    /**
     * 校验入库单是否可删除
     * 
     * @param inboundId 入库单id
     */
    private void checkInboundDeletable(Long inboundId)
    {
        WmsInbound inbound = wmsInboundMapper.selectWmsInboundById(inboundId);
        if (inbound == null)
        {
            throw new ServiceException("入库单不存在");
        }
        if (!"0".equals(inbound.getStatus()))
        {
            throw new ServiceException("入库单状态已变更，无法删除");
        }
    }
}

