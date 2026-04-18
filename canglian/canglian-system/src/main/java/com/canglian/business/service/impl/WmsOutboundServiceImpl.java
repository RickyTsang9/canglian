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
import com.canglian.business.domain.FinReceivable;
import com.canglian.business.domain.FinReceipt;
import com.canglian.business.domain.MdCustomer;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.PriceRecentTransaction;
import com.canglian.business.domain.SalOrder;
import com.canglian.common.exception.ServiceException;
import com.canglian.business.domain.WmsOutbound;
import com.canglian.business.domain.WmsOutboundItem;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsStockLog;
import com.canglian.business.mapper.FinReceivableMapper;
import com.canglian.business.mapper.MdCustomerMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.PriceRecentTransactionMapper;
import com.canglian.business.mapper.SalOrderMapper;
import com.canglian.business.mapper.WmsOutboundMapper;
import com.canglian.business.mapper.WmsOutboundItemMapper;
import com.canglian.business.mapper.WmsStockLogMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IFinReceivableService;
import com.canglian.business.service.IFinReceiptService;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.business.service.IWmsOutboundService;
import com.canglian.common.utils.StringUtils;

/**
 * 出库单 服务层实现
 * 
 * @author canglian
 */
@Service
public class WmsOutboundServiceImpl implements IWmsOutboundService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private WmsOutboundMapper wmsOutboundMapper;

    @Autowired
    private WmsOutboundItemMapper wmsOutboundItemMapper;

    @Autowired
    private WmsStockMapper wmsStockMapper;

    @Autowired
    private WmsStockLogMapper wmsStockLogMapper;

    @Autowired
    private IFinReceiptService finReceiptService;

    @Autowired
    private FinReceivableMapper finReceivableMapper;

    @Autowired
    private MdCustomerMapper mdCustomerMapper;

    @Autowired
    private MdProductMapper mdProductMapper;

    @Autowired
    private PriceRecentTransactionMapper priceRecentTransactionMapper;

    @Autowired
    private SalOrderMapper salOrderMapper;

    @Autowired
    private IFinReceivableService finReceivableService;

    @Autowired
    private IFinVoucherEventService finVoucherEventService;

    /**
     * 查询出库单信息
     * 
     * @param outboundId 出库单id
     * @return 出库单信息
     */
    @Override
    public WmsOutbound selectWmsOutboundById(Long outboundId)
    {
        return wmsOutboundMapper.selectWmsOutboundById(outboundId);
    }

    /**
     * 查询出库单列表
     * 
     * @param wmsOutbound 出库单信息
     * @return 出库单集合
     */
    @Override
    public List<WmsOutbound> selectWmsOutboundList(WmsOutbound wmsOutbound)
    {
        return wmsOutboundMapper.selectWmsOutboundList(wmsOutbound);
    }

    /**
     * 新增出库单
     * 
     * @param wmsOutbound 出库单信息
     * @return 结果
     */
    @Override
    public int insertWmsOutbound(WmsOutbound wmsOutbound)
    {
        if (StringUtils.isEmpty(wmsOutbound.getOutboundNo()))
        {
            wmsOutbound.setOutboundNo(generateOutboundNo());
        }
        if (wmsOutbound.getBusinessDate() == null)
        {
            wmsOutbound.setBusinessDate(new Date());
        }
        wmsOutbound.setTotalQty(null);
        wmsOutbound.setTotalAmount(null);
        wmsOutbound.setStatus("0");
        wmsOutbound.setBizStatus("draft");
        wmsOutbound.setDeliveryStatus("0");
        wmsOutbound.setAuditBy(null);
        wmsOutbound.setAuditTime(null);
        return wmsOutboundMapper.insertWmsOutbound(wmsOutbound);
    }

    /**
     * 修改出库单
     * 
     * @param wmsOutbound 出库单信息
     * @return 结果
     */
    @Override
    public int updateWmsOutbound(WmsOutbound wmsOutbound)
    {
        WmsOutbound outbound = getExistingOutbound(wmsOutbound.getOutboundId());
        validateOutboundEditable(outbound);
        wmsOutbound.setTotalQty(outbound.getTotalQty());
        wmsOutbound.setTotalAmount(outbound.getTotalAmount());
        wmsOutbound.setStatus(outbound.getStatus());
        wmsOutbound.setBizStatus(outbound.getBizStatus());
        wmsOutbound.setDeliveryStatus(outbound.getDeliveryStatus());
        wmsOutbound.setAuditBy(outbound.getAuditBy());
        wmsOutbound.setAuditTime(outbound.getAuditTime());
        return wmsOutboundMapper.updateWmsOutbound(wmsOutbound);
    }

    /**
     * 删除出库单
     * 
     * @param outboundId 出库单id
     * @return 结果
     */
    @Override
    public int deleteWmsOutboundById(Long outboundId)
    {
        checkOutboundDeletable(outboundId);
        return wmsOutboundMapper.deleteWmsOutboundById(outboundId);
    }

    /**
     * 批量删除出库单
     * 
     * @param outboundIds 需要删除的出库单id
     * @return 结果
     */
    @Override
    public int deleteWmsOutboundByIds(Long[] outboundIds)
    {
        for (Long outboundId : outboundIds)
        {
            checkOutboundDeletable(outboundId);
        }
        return wmsOutboundMapper.deleteWmsOutboundByIds(outboundIds);
    }

    /**
     * 出库单审核
     * 
     * @param outboundId 出库单id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    @Transactional
    public int auditWmsOutbound(Long outboundId, String operator)
    {
        WmsOutbound outbound = getExistingOutbound(outboundId);
        if (!"0".equals(outbound.getStatus()))
        {
            throw new ServiceException("出库单状态已变更，无法审核");
        }
        WmsOutboundItem outboundItemQuery = new WmsOutboundItem();
        outboundItemQuery.setOutboundId(outboundId);
        List<WmsOutboundItem> outboundItemList = wmsOutboundItemMapper.selectWmsOutboundItemList(outboundItemQuery);
        if (outboundItemList == null || outboundItemList.isEmpty())
        {
            throw new ServiceException("出库单明细不能为空");
        }
        BigDecimal totalQuantity = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (WmsOutboundItem outboundItem : outboundItemList)
        {
            BigDecimal itemQuantity = defaultBigDecimal(outboundItem.getQuantity());
            if (itemQuantity.compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("出库数量必须大于0");
            }
            if (outboundItem.getProductId() == null)
            {
                throw new ServiceException("出库明细商品不能为空");
            }
            if (outbound.getWarehouseId() == null)
            {
                throw new ServiceException("出库仓库不能为空");
            }
            Long locationId = defaultLocationId(outboundItem.getLocationId());
            String batchNo = defaultBatchNo(outboundItem.getBatchNo());
            WmsStock stockQuery = new WmsStock();
            stockQuery.setWarehouseId(outbound.getWarehouseId());
            stockQuery.setProductId(outboundItem.getProductId());
            stockQuery.setLocationId(locationId);
            stockQuery.setBatchNo(batchNo);
            WmsStock stock = wmsStockMapper.selectWmsStockByKey(stockQuery);
            if (stock == null)
            {
                throw new ServiceException("库存不足，无法出库");
            }
            BigDecimal beforeQuantity = defaultBigDecimal(stock.getQuantity());
            BigDecimal availableQuantity = calculateAvailableQuantity(stock);
            if (availableQuantity.compareTo(itemQuantity) < 0)
            {
                throw new ServiceException("可用库存不足，无法出库");
            }
            BigDecimal afterQuantity = beforeQuantity.subtract(itemQuantity);
            if (afterQuantity.compareTo(BigDecimal.ZERO) < 0)
            {
                throw new ServiceException("库存不足，无法出库");
            }
            stock.setQuantity(afterQuantity);
            stock.setUpdateBy(operator);
            wmsStockMapper.updateWmsStock(stock);
            WmsStockLog stockLog = new WmsStockLog();
            stockLog.setWarehouseId(outbound.getWarehouseId());
            stockLog.setProductId(outboundItem.getProductId());
            stockLog.setLocationId(locationId);
            stockLog.setBatchNo(batchNo);
            stockLog.setBillType("outbound");
            stockLog.setBillId(outboundId);
            stockLog.setInOut("2");
            stockLog.setQuantity(itemQuantity);
            BigDecimal itemPrice = defaultBigDecimal(outboundItem.getPrice());
            BigDecimal itemAmount = outboundItem.getAmount();
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
        outbound.setStatus("1");
        outbound.setAuditBy(operator);
        outbound.setAuditTime(new Date());
        outbound.setUpdateBy(operator);
        outbound.setTotalQty(totalQuantity);
        outbound.setTotalAmount(totalAmount);
        outbound.setBizStatus("approved");
        return wmsOutboundMapper.updateWmsOutbound(outbound);
    }

    /**
     * 出库单发货
     * 
     * @param outboundId 出库单id
     * @param wmsOutbound 出库单信息
     * @param operator 操作人
     * @return 结果
     */
    @Override
    public int shipWmsOutbound(Long outboundId, WmsOutbound wmsOutbound, String operator)
    {
        WmsOutbound outbound = getExistingOutbound(outboundId);
        validateOutboundShippable(outbound);
        WmsOutbound outboundPayload = wmsOutbound == null ? new WmsOutbound() : wmsOutbound;
        outbound.setCarrier(outboundPayload.getCarrier());
        outbound.setWaybillNo(outboundPayload.getWaybillNo());
        outbound.setFreightCost(outboundPayload.getFreightCost());
        outbound.setDeliveryStatus("1");
        outbound.setBizStatus("executing");
        outbound.setUpdateBy(operator);
        return wmsOutboundMapper.updateWmsOutbound(outbound);
    }

    /**
     * 出库单签收
     * 
     * @param outboundId 出库单id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    public int signWmsOutbound(Long outboundId, String operator)
    {
        WmsOutbound outbound = getExistingOutbound(outboundId);
        validateOutboundSignable(outbound);
        outbound.setDeliveryStatus("2");
        outbound.setBizStatus("completed");
        outbound.setUpdateBy(operator);
        int updateRows = wmsOutboundMapper.updateWmsOutbound(outbound);
        if (updateRows > 0)
        {
            syncReceivableCandidate(outbound, operator);
            syncRecentTransactionPrice(outbound, operator);
            syncSourceSaleOrder(outbound, operator);
            finVoucherEventService.recordVoucherEvent("outbound", outbound.getOutboundId(), outbound.getOutboundNo(), "outbound_complete",
                outbound.getBusinessDate(), outbound.getTotalAmount(), operator, "销货出库完成待生成凭证");
        }
        return updateRows;
    }

    /**
     * 出库单退货
     * 
     * @param outboundId 出库单id
     * @param refundAmount 退款金额
     * @param receivableId 关联应收单id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    @Transactional
    public int returnWmsOutbound(Long outboundId, BigDecimal refundAmount, Long receivableId, String operator)
    {
        WmsOutbound outbound = getExistingOutbound(outboundId);
        validateOutboundReturnable(outbound);
        validateRefundRequest(outbound, refundAmount);
        restoreReturnedStock(outbound, operator);
        outbound.setDeliveryStatus("3");
        outbound.setBizStatus("closed");
        outbound.setUpdateBy(operator);
        int rows = wmsOutboundMapper.updateWmsOutbound(outbound);
        if (rows > 0 && refundAmount != null && refundAmount.compareTo(BigDecimal.ZERO) > 0 && outbound.getCustomerId() != null)
        {
            FinReceipt refundReceipt = buildRefundReceipt(outbound, refundAmount, receivableId, operator);
            int receiptRows = finReceiptService.insertFinReceipt(refundReceipt);
            if (receiptRows <= 0)
            {
                throw new ServiceException("退货退款单生成失败");
            }
        }
        return rows;
    }

    /**
     * 回补出库退货库存
     * 
     * @param outbound 出库单信息
     * @param operator 操作人
     */
    private void restoreReturnedStock(WmsOutbound outbound, String operator)
    {
        if (outbound.getWarehouseId() == null)
        {
            throw new ServiceException("退货仓库不能为空");
        }
        List<WmsOutboundItem> outboundItemList = getOutboundItemList(outbound.getOutboundId());
        for (WmsOutboundItem outboundItem : outboundItemList)
        {
            BigDecimal itemQuantity = defaultBigDecimal(outboundItem.getQuantity());
            if (itemQuantity.compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("退货数量必须大于0");
            }
            if (outboundItem.getProductId() == null)
            {
                throw new ServiceException("退货明细商品不能为空");
            }
            Long locationId = defaultLocationId(outboundItem.getLocationId());
            String batchNo = defaultBatchNo(outboundItem.getBatchNo());
            WmsStock stockQuery = new WmsStock();
            stockQuery.setWarehouseId(outbound.getWarehouseId());
            stockQuery.setProductId(outboundItem.getProductId());
            stockQuery.setLocationId(locationId);
            stockQuery.setBatchNo(batchNo);
            WmsStock stock = wmsStockMapper.selectWmsStockByKey(stockQuery);
            BigDecimal beforeQuantity;
            if (stock == null)
            {
                stock = new WmsStock();
                stock.setWarehouseId(outbound.getWarehouseId());
                stock.setProductId(outboundItem.getProductId());
                stock.setLocationId(locationId);
                stock.setBatchNo(batchNo);
                stock.setQuantity(itemQuantity);
                stock.setLockedQuantity(BigDecimal.ZERO);
                stock.setFrozenQuantity(BigDecimal.ZERO);
                fillStockWarningQty(stock, outboundItem.getProductId());
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
            stockLog.setWarehouseId(outbound.getWarehouseId());
            stockLog.setProductId(outboundItem.getProductId());
            stockLog.setLocationId(locationId);
            stockLog.setBatchNo(batchNo);
            stockLog.setBillType("outbound_return");
            stockLog.setBillId(outbound.getOutboundId());
            stockLog.setInOut("1");
            stockLog.setQuantity(itemQuantity);
            BigDecimal itemPrice = defaultBigDecimal(outboundItem.getPrice());
            BigDecimal itemAmount = outboundItem.getAmount();
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
        }
    }

    /**
     * 校验退货退款请求
     * 
     * @param outbound 出库单信息
     * @param refundAmount 退款金额
     */
    private void validateRefundRequest(WmsOutbound outbound, BigDecimal refundAmount)
    {
        if (refundAmount == null)
        {
            return;
        }
        if (refundAmount.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("退款金额不能小于0");
        }
        if (refundAmount.compareTo(BigDecimal.ZERO) > 0 && outbound.getCustomerId() == null)
        {
            throw new ServiceException("存在退款金额时客户编号不能为空");
        }
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
     * 计算可用库存
     * 
     * @param stock 库存
     * @return 可用库存
     */
    private BigDecimal calculateAvailableQuantity(WmsStock stock)
    {
        BigDecimal quantity = defaultBigDecimal(stock.getQuantity());
        BigDecimal lockedQuantity = defaultBigDecimal(stock.getLockedQuantity());
        BigDecimal frozenQuantity = defaultBigDecimal(stock.getFrozenQuantity());
        return quantity.subtract(lockedQuantity).subtract(frozenQuantity);
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
     * 同步应收候选单
     * 
     * @param outbound 出库单信息
     * @param operator 操作人
     */
    private void syncReceivableCandidate(WmsOutbound outbound, String operator)
    {
        if (outbound.getCustomerId() == null || defaultBigDecimal(outbound.getTotalAmount()).compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }
        FinReceivable existingFinReceivable = selectLinkedReceivable(outbound);
        if (existingFinReceivable == null)
        {
            FinReceivable finReceivable = buildReceivableCandidate(outbound, operator);
            finReceivableService.insertFinReceivable(finReceivable);
            finReceivableService.auditFinReceivable(finReceivable.getReceivableId(), operator);
            return;
        }
        if (defaultBigDecimal(existingFinReceivable.getReceivedAmount()).compareTo(defaultBigDecimal(outbound.getTotalAmount())) > 0)
        {
            throw new ServiceException("应收候选单已收金额大于当前出库金额，无法自动回写");
        }
        existingFinReceivable.setCustomerId(outbound.getCustomerId());
        existingFinReceivable.setBillType("outbound");
        existingFinReceivable.setBillId(outbound.getOutboundId());
        existingFinReceivable.setSourceBillType(outbound.getSourceBillType());
        existingFinReceivable.setSourceBillId(outbound.getSourceBillId());
        existingFinReceivable.setSourceBillNo(outbound.getSourceBillNo());
        existingFinReceivable.setBusinessDate(outbound.getBusinessDate());
        existingFinReceivable.setAmount(outbound.getTotalAmount());
        existingFinReceivable.setDueDate(resolveReceivableDueDate(outbound.getCustomerId(), outbound.getBusinessDate()));
        existingFinReceivable.setStatus(calculateReceivableStatus(existingFinReceivable.getReceivedAmount(), outbound.getTotalAmount()));
        existingFinReceivable.setBizStatus("confirmed");
        existingFinReceivable.setUpdateBy(operator);
        existingFinReceivable.setRemark(buildOutboundReceivableRemark(outbound));
        finReceivableMapper.updateFinReceivable(existingFinReceivable);
    }

    /**
     * 查询已关联的应收候选单
     * 
     * @param outbound 出库单信息
     * @return 应收候选单
     */
    private FinReceivable selectLinkedReceivable(WmsOutbound outbound)
    {
        FinReceivable finReceivableQuery = new FinReceivable();
        finReceivableQuery.setBillType("outbound");
        if (StringUtils.isNotEmpty(outbound.getSourceBillNo()))
        {
            finReceivableQuery.setSourceBillNo(outbound.getSourceBillNo());
        }
        List<FinReceivable> finReceivableList = finReceivableMapper.selectFinReceivableList(finReceivableQuery);
        for (FinReceivable finReceivable : finReceivableList)
        {
            if ("outbound".equals(finReceivable.getBillType()) && outbound.getOutboundId().equals(finReceivable.getBillId()))
            {
                return finReceivable;
            }
        }
        return null;
    }

    /**
     * 构建应收候选单
     * 
     * @param outbound 出库单信息
     * @param operator 操作人
     * @return 应收候选单
     */
    private FinReceivable buildReceivableCandidate(WmsOutbound outbound, String operator)
    {
        FinReceivable finReceivable = new FinReceivable();
        finReceivable.setCustomerId(outbound.getCustomerId());
        finReceivable.setBillType("outbound");
        finReceivable.setBillId(outbound.getOutboundId());
        finReceivable.setSourceBillType(outbound.getSourceBillType());
        finReceivable.setSourceBillId(outbound.getSourceBillId());
        finReceivable.setSourceBillNo(outbound.getSourceBillNo());
        finReceivable.setBusinessDate(outbound.getBusinessDate());
        finReceivable.setAmount(outbound.getTotalAmount());
        finReceivable.setDueDate(resolveReceivableDueDate(outbound.getCustomerId(), outbound.getBusinessDate()));
        finReceivable.setCreateBy(operator);
        finReceivable.setRemark(buildOutboundReceivableRemark(outbound));
        return finReceivable;
    }

    /**
     * 构建出库应收备注
     * 
     * @param outbound 出库单信息
     * @return 备注
     */
    private String buildOutboundReceivableRemark(WmsOutbound outbound)
    {
        return "销货出库完成自动生成，关联出库单号：" + outbound.getOutboundNo();
    }

    /**
     * 计算应收到期日
     * 
     * @param customerId 客户编号
     * @param businessDate 业务日期
     * @return 到期日
     */
    private Date resolveReceivableDueDate(Long customerId, Date businessDate)
    {
        MdCustomer mdCustomer = mdCustomerMapper.selectMdCustomerById(customerId);
        int receivableDays = mdCustomer == null || mdCustomer.getReceivableDays() == null ? 30 : mdCustomer.getReceivableDays();
        Calendar dueDateCalendar = Calendar.getInstance();
        dueDateCalendar.setTime(businessDate == null ? new Date() : businessDate);
        dueDateCalendar.add(Calendar.DAY_OF_MONTH, receivableDays);
        return dueDateCalendar.getTime();
    }

    /**
     * 计算应收状态
     * 
     * @param receivedAmount 已收金额
     * @param receivableAmount 应收金额
     * @return 应收状态
     */
    private String calculateReceivableStatus(BigDecimal receivedAmount, BigDecimal receivableAmount)
    {
        BigDecimal receivedAmountValue = defaultBigDecimal(receivedAmount);
        BigDecimal receivableAmountValue = defaultBigDecimal(receivableAmount);
        if (receivedAmountValue.compareTo(BigDecimal.ZERO) <= 0)
        {
            return "0";
        }
        if (receivedAmountValue.compareTo(receivableAmountValue) >= 0)
        {
            return "2";
        }
        return "1";
    }

    /**
     * 同步最近成交价
     * 
     * @param outbound 出库单信息
     * @param operator 操作人
     */
    private void syncRecentTransactionPrice(WmsOutbound outbound, String operator)
    {
        if (outbound.getCustomerId() == null)
        {
            return;
        }
        List<WmsOutboundItem> outboundItemList = getOutboundItemList(outbound.getOutboundId());
        for (WmsOutboundItem outboundItem : outboundItemList)
        {
            if (outboundItem.getProductId() == null || defaultBigDecimal(outboundItem.getPrice()).compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            PriceRecentTransaction priceRecentTransactionQuery = new PriceRecentTransaction();
            priceRecentTransactionQuery.setCustomerId(outbound.getCustomerId());
            priceRecentTransactionQuery.setProductId(outboundItem.getProductId());
            List<PriceRecentTransaction> priceRecentTransactionList = priceRecentTransactionMapper.selectPriceRecentTransactionList(priceRecentTransactionQuery);
            PriceRecentTransaction priceRecentTransaction = priceRecentTransactionList == null || priceRecentTransactionList.isEmpty() ? null : priceRecentTransactionList.get(0);
            if (priceRecentTransaction == null)
            {
                priceRecentTransaction = new PriceRecentTransaction();
                priceRecentTransaction.setCustomerId(outbound.getCustomerId());
                priceRecentTransaction.setProductId(outboundItem.getProductId());
                priceRecentTransaction.setCreateBy(operator);
                priceRecentTransaction.setRemark("销货出库完成自动更新");
                fillRecentTransaction(priceRecentTransaction, outbound, outboundItem.getPrice(), operator);
                priceRecentTransactionMapper.insertPriceRecentTransaction(priceRecentTransaction);
            }
            else
            {
                fillRecentTransaction(priceRecentTransaction, outbound, outboundItem.getPrice(), operator);
                priceRecentTransaction.setUpdateBy(operator);
                priceRecentTransaction.setRemark("销货出库完成自动更新");
                priceRecentTransactionMapper.updatePriceRecentTransaction(priceRecentTransaction);
            }
        }
    }

    /**
     * 回填最近成交价信息
     * 
     * @param priceRecentTransaction 最近成交价
     * @param outbound 出库单信息
     * @param salePrice 成交单价
     * @param operator 操作人
     */
    private void fillRecentTransaction(PriceRecentTransaction priceRecentTransaction, WmsOutbound outbound, BigDecimal salePrice, String operator)
    {
        priceRecentTransaction.setSalePrice(salePrice);
        priceRecentTransaction.setSourceBillType("outbound");
        priceRecentTransaction.setSourceBillId(outbound.getOutboundId());
        priceRecentTransaction.setBusinessDate(outbound.getBusinessDate());
        if (StringUtils.isEmpty(priceRecentTransaction.getCreateBy()))
        {
            priceRecentTransaction.setCreateBy(operator);
        }
    }

    /**
     * 同步来源销货订单状态
     * 
     * @param outbound 出库单信息
     * @param operator 操作人
     */
    private void syncSourceSaleOrder(WmsOutbound outbound, String operator)
    {
        if (!"sale_order".equals(outbound.getSourceBillType()) || outbound.getSourceBillId() == null)
        {
            return;
        }
        SalOrder salOrder = salOrderMapper.selectSalOrderById(outbound.getSourceBillId());
        if (salOrder == null)
        {
            return;
        }
        salOrder.setStatus("2");
        salOrder.setBizStatus("completed");
        salOrder.setUpdateBy(operator);
        salOrderMapper.updateSalOrder(salOrder);
    }

    /**
     * 构建退货退款收款单
     * 
     * @param outbound 出库单信息
     * @param refundAmount 退款金额
     * @param receivableId 关联应收单id
     * @param operator 操作人
     * @return 收款单信息
     */
    private FinReceipt buildRefundReceipt(WmsOutbound outbound, BigDecimal refundAmount, Long receivableId, String operator)
    {
        FinReceipt finReceipt = new FinReceipt();
        finReceipt.setCustomerId(outbound.getCustomerId());
        finReceipt.setReceivableId(receivableId);
        finReceipt.setAmount(refundAmount.negate());
        finReceipt.setReceiptDate(new Date());
        finReceipt.setPayChannel("refund");
        finReceipt.setCreateBy(operator);
        if (StringUtils.isNotEmpty(outbound.getOutboundNo()))
        {
            finReceipt.setRemark("出库退货自动生成退款收款单，关联出库单号：" + outbound.getOutboundNo());
        }
        else
        {
            finReceipt.setRemark("出库退货自动生成退款收款单");
        }
        return finReceipt;
    }

    /**
     * 获取出库单明细集合
     * 
     * @param outboundId 出库单id
     * @return 出库单明细集合
     */
    private List<WmsOutboundItem> getOutboundItemList(Long outboundId)
    {
        WmsOutboundItem outboundItemQuery = new WmsOutboundItem();
        outboundItemQuery.setOutboundId(outboundId);
        List<WmsOutboundItem> outboundItemList = wmsOutboundItemMapper.selectWmsOutboundItemList(outboundItemQuery);
        if (outboundItemList == null || outboundItemList.isEmpty())
        {
            throw new ServiceException("出库单明细不能为空");
        }
        return outboundItemList;
    }

    /**
     * 获取存在的出库单
     * 
     * @param outboundId 出库单id
     * @return 出库单信息
     */
    private WmsOutbound getExistingOutbound(Long outboundId)
    {
        WmsOutbound outbound = wmsOutboundMapper.selectWmsOutboundById(outboundId);
        if (outbound == null)
        {
            throw new ServiceException("出库单不存在");
        }
        return outbound;
    }

    /**
     * 校验出库单是否允许修改
     * 
     * @param outbound 出库单信息
     */
    private void validateOutboundEditable(WmsOutbound outbound)
    {
        if (!"0".equals(outbound.getStatus()))
        {
            throw new ServiceException("出库单已审核，无法修改");
        }
    }

    /**
     * 校验出库单是否允许发货
     * 
     * @param outbound 出库单信息
     */
    private void validateOutboundShippable(WmsOutbound outbound)
    {
        if (!"1".equals(outbound.getStatus()))
        {
            throw new ServiceException("出库单未审核，无法发货");
        }
        if ("1".equals(outbound.getDeliveryStatus()))
        {
            throw new ServiceException("出库单已发货，请勿重复操作");
        }
        if ("2".equals(outbound.getDeliveryStatus()))
        {
            throw new ServiceException("出库单已签收，无法重复发货");
        }
        if ("3".equals(outbound.getDeliveryStatus()))
        {
            throw new ServiceException("出库单已退货，无法发货");
        }
    }

    /**
     * 校验出库单是否允许签收
     * 
     * @param outbound 出库单信息
     */
    private void validateOutboundSignable(WmsOutbound outbound)
    {
        if (!"1".equals(outbound.getStatus()))
        {
            throw new ServiceException("出库单未审核，无法签收");
        }
        if ("0".equals(outbound.getDeliveryStatus()) || StringUtils.isEmpty(outbound.getDeliveryStatus()))
        {
            throw new ServiceException("出库单未发货，无法签收");
        }
        if ("2".equals(outbound.getDeliveryStatus()))
        {
            throw new ServiceException("出库单已签收，请勿重复操作");
        }
        if ("3".equals(outbound.getDeliveryStatus()))
        {
            throw new ServiceException("出库单已退货，无法签收");
        }
    }

    /**
     * 校验出库单是否允许退货
     * 
     * @param outbound 出库单信息
     */
    private void validateOutboundReturnable(WmsOutbound outbound)
    {
        if (!"1".equals(outbound.getStatus()))
        {
            throw new ServiceException("出库单未审核，无法退货");
        }
        if ("0".equals(outbound.getDeliveryStatus()) || StringUtils.isEmpty(outbound.getDeliveryStatus()))
        {
            throw new ServiceException("出库单未发货，无法退货");
        }
        if ("3".equals(outbound.getDeliveryStatus()))
        {
            throw new ServiceException("出库单已退货，请勿重复操作");
        }
    }

    /**
     * 生成出库单号
     * 
     * @return 出库单号
     */
    private String generateOutboundNo()
    {
        String noPrefix = "out" + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String currentMaxNo = wmsOutboundMapper.selectMaxOutboundNoByPrefix(noPrefix);
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
     * 校验出库单是否可删除
     * 
     * @param outboundId 出库单id
     */
    private void checkOutboundDeletable(Long outboundId)
    {
        WmsOutbound outbound = wmsOutboundMapper.selectWmsOutboundById(outboundId);
        if (outbound == null)
        {
            throw new ServiceException("出库单不存在");
        }
        if (!"0".equals(outbound.getStatus()))
        {
            throw new ServiceException("出库单状态已变更，无法删除");
        }
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
}

