package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.FinBadDebt;
import com.canglian.business.domain.FinCostCalculate;
import com.canglian.business.domain.FinInvoice;
import com.canglian.business.domain.FinReceivable;
import com.canglian.business.domain.FinReceipt;
import com.canglian.business.domain.FinWriteOff;
import com.canglian.business.domain.MdCustomer;
import com.canglian.business.domain.MdLocation;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.MdWarehouse;
import com.canglian.business.domain.PriceRecentTransaction;
import com.canglian.business.domain.SalOrder;
import com.canglian.business.domain.SalOrderItem;
import com.canglian.business.domain.WmsOutbound;
import com.canglian.business.domain.WmsOutboundItem;
import com.canglian.business.domain.WmsSaleReturn;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsStockLog;
import com.canglian.business.mapper.FinBadDebtMapper;
import com.canglian.business.mapper.FinInvoiceMapper;
import com.canglian.business.mapper.FinReceivableMapper;
import com.canglian.business.mapper.FinReceiptMapper;
import com.canglian.business.mapper.FinWriteOffMapper;
import com.canglian.business.mapper.MdCustomerMapper;
import com.canglian.business.mapper.MdLocationMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.MdWarehouseMapper;
import com.canglian.business.mapper.PriceRecentTransactionMapper;
import com.canglian.business.mapper.SalOrderItemMapper;
import com.canglian.business.mapper.SalOrderMapper;
import com.canglian.business.mapper.WmsOutboundItemMapper;
import com.canglian.business.mapper.WmsOutboundMapper;
import com.canglian.business.mapper.WmsSaleReturnMapper;
import com.canglian.business.mapper.WmsStockLogMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IFinCostCalculateService;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IFinReceivableService;
import com.canglian.business.service.IFinReceiptService;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.business.service.IMdProductUnitService;
import com.canglian.business.service.IPriceResolverService;
import com.canglian.business.service.ISalOrderService;
import com.canglian.business.service.IWmsOutboundService;
import com.canglian.business.service.IWmsSerialNoService;
import com.canglian.business.service.IWmsStockService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;
import com.canglian.system.service.ISysConfigService;

/**
 * 出库单 服务层实现
 *
 * @author canglian
 */
@Service
public class WmsOutboundServiceImpl implements IWmsOutboundService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final String LOCK_ON_APPROVE_KEY = "canglian.stock.lockOnSaleApprove";
    private static final String ALLOW_NEGATIVE_STOCK_KEY = "canglian.stock.allowNegative";

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

    @Autowired
    private IFinGeneralLedgerService finGeneralLedgerService;

    @Autowired
    private IFinCostCalculateService finCostCalculateService;

    @Autowired
    private WmsSaleReturnMapper wmsSaleReturnMapper;

    @Autowired(required = false)
    private IMdProductUnitService mdProductUnitService;

    @Autowired(required = false)
    private IWmsStockService wmsStockService;

    @Autowired(required = false)
    private ISysConfigService sysConfigService;

    @Lazy
    @Autowired(required = false)
    private ISalOrderService salOrderService;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Autowired(required = false)
    private MdWarehouseMapper mdWarehouseMapper;

    @Autowired(required = false)
    private MdLocationMapper mdLocationMapper;

    @Autowired(required = false)
    private IWmsSerialNoService wmsSerialNoService;

    @Autowired(required = false)
    private IPriceResolverService priceResolverService;

    @Autowired(required = false)
    private FinReceiptMapper finReceiptMapper;

    @Autowired(required = false)
    private FinWriteOffMapper finWriteOffMapper;

    @Autowired(required = false)
    private FinBadDebtMapper finBadDebtMapper;

    @Autowired(required = false)
    private FinInvoiceMapper finInvoiceMapper;

    @Autowired(required = false)
    private SalOrderItemMapper salOrderItemMapper;

    @Override
    public WmsOutbound selectWmsOutboundById(Long outboundId)
    {
        WmsOutbound outbound = wmsOutboundMapper.selectWmsOutboundById(outboundId);
        if (outbound != null)
        {
            if (bizScopeHelper != null)
            {
                bizScopeHelper.assertRecordAccess(outbound.getDeptId(), outbound.getOwnerUserId(), outbound.getWarehouseId());
            }
            WmsOutboundItem itemQuery = new WmsOutboundItem();
            itemQuery.setOutboundId(outboundId);
            outbound.setItemList(wmsOutboundItemMapper.selectWmsOutboundItemList(itemQuery));
        }
        return outbound;
    }

    @Override
    public List<WmsOutbound> selectWmsOutboundList(WmsOutbound wmsOutbound)
    {
        if (bizScopeHelper != null)
        {
            bizScopeHelper.applyScopeFilter(wmsOutbound, "", true, true);
        }
        return wmsOutboundMapper.selectWmsOutboundList(wmsOutbound);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWmsOutbound(WmsOutbound wmsOutbound)
    {
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(wmsOutbound.getWarehouseId());
            if (wmsOutbound.getDeptId() == null)
            {
                wmsOutbound.setDeptId(bizScopeHelper.currentDeptId());
            }
            if (wmsOutbound.getOwnerUserId() == null)
            {
                wmsOutbound.setOwnerUserId(bizScopeHelper.currentUserId());
            }
        }
        if (StringUtils.isEmpty(wmsOutbound.getOutboundNo()))
        {
            wmsOutbound.setOutboundNo(generateOutboundNo());
        }
        if (wmsOutbound.getBusinessDate() == null)
        {
            wmsOutbound.setBusinessDate(new Date());
        }
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(wmsOutbound.getBusinessDate());
        }
        validateActiveMasterRefs(wmsOutbound);
        wmsOutbound.setTotalQty(BigDecimal.ZERO);
        wmsOutbound.setTotalAmount(BigDecimal.ZERO);
        wmsOutbound.setStatus("0");
        wmsOutbound.setBizStatus("draft");
        wmsOutbound.setDeliveryStatus("0");
        wmsOutbound.setIsReversal("0");
        wmsOutbound.setReversedFlag("0");
        wmsOutbound.setVerifiedFlag("0");
        wmsOutbound.setAuditBy(null);
        wmsOutbound.setAuditTime(null);
        int rows = wmsOutboundMapper.insertWmsOutbound(wmsOutbound);
        if (rows > 0 && wmsOutbound.getItemList() != null && !wmsOutbound.getItemList().isEmpty())
        {
            saveOutboundItemsAndRecalculate(wmsOutbound);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWmsOutbound(WmsOutbound wmsOutbound)
    {
        WmsOutbound outbound = getExistingOutbound(wmsOutbound.getOutboundId());
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(outbound.getDeptId(), outbound.getOwnerUserId(), outbound.getWarehouseId());
            bizScopeHelper.assertWarehouseAccess(wmsOutbound.getWarehouseId());
        }
        validateOutboundEditable(outbound);
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(outbound.getBusinessDate());
            if (wmsOutbound.getBusinessDate() != null)
            {
                finGeneralLedgerService.assertPeriodOpen(wmsOutbound.getBusinessDate());
            }
        }
        if (wmsOutbound.getWarehouseId() == null)
        {
            wmsOutbound.setWarehouseId(outbound.getWarehouseId());
        }
        validateActiveMasterRefs(wmsOutbound);
        wmsOutbound.setSourceBillType(outbound.getSourceBillType());
        wmsOutbound.setSourceBillId(outbound.getSourceBillId());
        wmsOutbound.setSourceBillNo(outbound.getSourceBillNo());
        wmsOutbound.setTotalQty(outbound.getTotalQty());
        wmsOutbound.setTotalAmount(outbound.getTotalAmount());
        wmsOutbound.setStatus(outbound.getStatus());
        wmsOutbound.setBizStatus(outbound.getBizStatus());
        wmsOutbound.setDeliveryStatus(outbound.getDeliveryStatus());
        wmsOutbound.setAuditBy(outbound.getAuditBy());
        wmsOutbound.setAuditTime(outbound.getAuditTime());
        int rows = wmsOutboundMapper.updateWmsOutbound(wmsOutbound);
        if (rows > 0 && wmsOutbound.getItemList() != null)
        {
            wmsOutboundItemMapper.deleteWmsOutboundItemByOutboundId(wmsOutbound.getOutboundId());
            saveOutboundItemsAndRecalculate(wmsOutbound);
            validateOutboundItemsWithinSaleOrder(wmsOutbound);
            syncSourceSaleOrderProgress(wmsOutbound, wmsOutbound.getUpdateBy());
        }
        return rows;
    }

    private void saveOutboundItemsAndRecalculate(WmsOutbound wmsOutbound)
    {
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal taxTotalAmount = BigDecimal.ZERO;
        if (wmsOutbound.getItemList() != null)
        {
            for (WmsOutboundItem item : wmsOutbound.getItemList())
            {
                if (item.getProductId() == null)
                {
                    throw new ServiceException("出库明细商品不能为空");
                }
                BigDecimal qty = defaultBigDecimal(item.getQuantity());
                if (qty.compareTo(BigDecimal.ZERO) <= 0)
                {
                    throw new ServiceException("出库明细数量必须大于0");
                }
                BigDecimal price = defaultBigDecimal(item.getPrice());
                if (price.compareTo(BigDecimal.ZERO) < 0)
                {
                    throw new ServiceException("出库明细单价不能为负数");
                }
                BigDecimal amount = item.getAmount();
                if (amount == null || (amount.compareTo(BigDecimal.ZERO) <= 0 && price.compareTo(BigDecimal.ZERO) > 0))
                {
                    amount = qty.multiply(price).setScale(2, RoundingMode.HALF_UP);
                }
                MdProduct product = mdProductMapper != null ? mdProductMapper.selectMdProductById(item.getProductId()) : null;
                if (product != null && "1".equals(product.getStatus()))
                {
                    throw new ServiceException("商品[" + product.getProductName() + "]已停用，禁止出库");
                }
                validateLocationBelongsToWarehouse(item.getLocationId(), wmsOutbound.getWarehouseId());
                if (StringUtils.isEmpty(item.getUnitName()) && product != null)
                {
                    item.setUnitName(StringUtils.isNotEmpty(product.getSaleUnit()) ? product.getSaleUnit() : product.getUnitName());
                }
                BigDecimal ratio = mdProductUnitService != null
                    ? mdProductUnitService.resolveConvertRatio(item.getProductId(), item.getUnitName())
                    : (item.getConvertRatio() != null && item.getConvertRatio().compareTo(BigDecimal.ZERO) > 0 ? item.getConvertRatio() : BigDecimal.ONE);
                item.setConvertRatio(ratio);
                item.setBaseQuantity(qty.multiply(ratio).setScale(4, RoundingMode.HALF_UP));

                item.setOutboundId(wmsOutbound.getOutboundId());
                item.setLocationId(item.getLocationId() == null ? 0L : item.getLocationId());
                item.setBatchNo(defaultBatchNo(item.getBatchNo()));
                item.setQuantity(qty);
                item.setPrice(price);
                item.setAmount(amount);
                if (item.getTaxAmount() != null)
                {
                    taxTotalAmount = taxTotalAmount.add(item.getTaxAmount());
                }
                wmsOutboundItemMapper.insertWmsOutboundItem(item);
                totalQty = totalQty.add(qty);
                totalAmount = totalAmount.add(amount);
            }
        }
        wmsOutbound.setTotalQty(totalQty);
        wmsOutbound.setTotalAmount(totalAmount);
        wmsOutbound.setTaxTotalAmount(taxTotalAmount);
        wmsOutboundMapper.updateWmsOutbound(wmsOutbound);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsOutboundById(Long outboundId)
    {
        WmsOutbound outbound = checkOutboundDeletable(outboundId);
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(outbound.getDeptId(), outbound.getOwnerUserId(), outbound.getWarehouseId());
        }
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(outbound.getBusinessDate());
        }
        releaseLockIfClosedParentOrder(outbound, outbound.getUpdateBy());
        wmsOutboundItemMapper.deleteWmsOutboundItemByOutboundId(outboundId);
        int rows = wmsOutboundMapper.deleteWmsOutboundById(outboundId);
        syncSourceSaleOrderProgress(outbound, outbound.getUpdateBy());
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsOutboundByIds(Long[] outboundIds)
    {
        List<WmsOutbound> toDelete = new ArrayList<WmsOutbound>();
        for (Long outboundId : outboundIds)
        {
            WmsOutbound outbound = checkOutboundDeletable(outboundId);
            if (bizScopeHelper != null)
            {
                bizScopeHelper.assertRecordAccess(outbound.getDeptId(), outbound.getOwnerUserId(), outbound.getWarehouseId());
            }
            if (finGeneralLedgerService != null)
            {
                finGeneralLedgerService.assertPeriodOpen(outbound.getBusinessDate());
            }
            toDelete.add(outbound);
        }
        for (WmsOutbound outbound : toDelete)
        {
            releaseLockIfClosedParentOrder(outbound, outbound.getUpdateBy());
            wmsOutboundItemMapper.deleteWmsOutboundItemByOutboundId(outbound.getOutboundId());
        }
        int rows = wmsOutboundMapper.deleteWmsOutboundByIds(outboundIds);
        for (WmsOutbound outbound : toDelete)
        {
            syncSourceSaleOrderProgress(outbound, outbound.getUpdateBy());
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int auditWmsOutbound(Long outboundId, String operator)
    {
        WmsOutbound outbound = wmsOutboundMapper.selectWmsOutboundByIdForUpdate(outboundId);
        if (outbound == null)
        {
            throw new ServiceException("出库单不存在");
        }
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(outbound.getDeptId(), outbound.getOwnerUserId(), outbound.getWarehouseId());
        }
        if (!"0".equals(outbound.getStatus()))
        {
            throw new ServiceException("出库单状态已变更，无法审核");
        }
        finGeneralLedgerService.assertPeriodOpen(outbound.getBusinessDate());
        validateActiveMasterRefs(outbound);
        WmsOutboundItem outboundItemQuery = new WmsOutboundItem();
        outboundItemQuery.setOutboundId(outboundId);
        List<WmsOutboundItem> outboundItemList = wmsOutboundItemMapper.selectWmsOutboundItemList(outboundItemQuery);
        if (outboundItemList == null || outboundItemList.isEmpty())
        {
            throw new ServiceException("出库单明细不能为空");
        }
        // 直开销售出库单（无上游销售订单）也必须校验客户信用额度
        if (outbound.getSourceBillId() == null && outbound.getCustomerId() != null)
        {
            BigDecimal directTotal = BigDecimal.ZERO;
            for (WmsOutboundItem item : outboundItemList)
            {
                BigDecimal amt = item.getAmount();
                if (amt == null || amt.compareTo(BigDecimal.ZERO) <= 0)
                {
                    amt = defaultBigDecimal(item.getPrice()).multiply(defaultBigDecimal(item.getQuantity())).setScale(2, RoundingMode.HALF_UP);
                }
                directTotal = directTotal.add(amt);
            }
            validateDirectOutboundCreditLimit(outbound.getCustomerId(), directTotal);
        }
        boolean releaseOrderLock = isParentOrderStockLocked(outbound) && wmsStockService != null;
        boolean allowNegative = shouldAllowNegativeStock();
        Date todayStart = startOfToday();

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
            validateLocationBelongsToWarehouse(outboundItem.getLocationId(), outbound.getWarehouseId());
            MdProduct product = mdProductMapper != null ? mdProductMapper.selectMdProductById(outboundItem.getProductId()) : null;
            if (product != null && "1".equals(product.getStatus()))
            {
                throw new ServiceException("商品[" + product.getProductName() + "]已停用，禁止出库审核");
            }
            BigDecimal baseQuantity = resolveBaseQuantity(outboundItem);
            Long locationId = defaultLocationId(outboundItem.getLocationId());
            String batchNo = defaultBatchNo(outboundItem.getBatchNo());
            // 按单+库位+批次精准释放该订单自有锁库，杜绝跨单窃锁与跨批次死锁
            if (releaseOrderLock)
            {
                wmsStockService.releaseStockLockForSaleOrder(outbound.getWarehouseId(), outboundItem.getProductId(),
                    locationId, batchNo, baseQuantity, outbound.getSourceBillId(), operator);
            }
            WmsStock stockQuery = new WmsStock();
            stockQuery.setWarehouseId(outbound.getWarehouseId());
            stockQuery.setProductId(outboundItem.getProductId());
            stockQuery.setLocationId(locationId);
            stockQuery.setBatchNo(batchNo);
            WmsStock stock = wmsStockMapper.selectWmsStockByKeyForUpdate(stockQuery);
            BigDecimal beforeQuantity;
            BigDecimal afterQuantity;
            if (stock == null)
            {
                if (!allowNegative)
                {
                    throw new ServiceException("库存不足，无法出库");
                }
                beforeQuantity = BigDecimal.ZERO;
                afterQuantity = baseQuantity.negate();
                stock = new WmsStock();
                stock.setWarehouseId(outbound.getWarehouseId());
                stock.setProductId(outboundItem.getProductId());
                stock.setLocationId(locationId);
                stock.setBatchNo(batchNo);
                stock.setQuantity(afterQuantity);
                stock.setLockedQuantity(BigDecimal.ZERO);
                stock.setFrozenQuantity(BigDecimal.ZERO);
                fillStockWarningQty(stock, outboundItem.getProductId());
                stock.setVersion(0L);
                stock.setCreateBy(operator);
                wmsStockMapper.insertWmsStock(stock);
            }
            else
            {
                // P6-2：过期批次严禁出库拦截（即使允许负库存也严禁出过期商品）
                if (stock.getExpiryDate() != null && stock.getExpiryDate().before(todayStart))
                {
                    String expStr = new SimpleDateFormat("yyyy-MM-dd").format(stock.getExpiryDate());
                    throw new ServiceException("商品 #" + outboundItem.getProductId() + " 批次[" + batchNo + "] 已过有效期(" + expStr + ")，禁止出库");
                }
                beforeQuantity = defaultBigDecimal(stock.getQuantity());
                BigDecimal availableQuantity = calculateAvailableQuantity(stock);
                if (!allowNegative && availableQuantity.compareTo(baseQuantity) < 0)
                {
                    throw new ServiceException("可用库存不足，无法出库");
                }
                afterQuantity = beforeQuantity.subtract(baseQuantity);
                if (!allowNegative && afterQuantity.compareTo(BigDecimal.ZERO) < 0)
                {
                    throw new ServiceException("库存不足，无法出库");
                }
                stock.setQuantity(afterQuantity);
                stock.setUpdateBy(operator);
                wmsStockMapper.updateWmsStock(stock);
            }
            // 序列号强管控：启用序列号的商品必须录入足额在库 SN 并核销出库
            if (wmsSerialNoService != null)
            {
                wmsSerialNoService.handleOutboundSerials(product, outbound.getWarehouseId(), outboundId,
                    outbound.getOutboundNo(), outbound.getCustomerId(), outboundItem.getSnCodes(), baseQuantity, operator);
            }

            BigDecimal itemPrice = defaultBigDecimal(outboundItem.getPrice());
            BigDecimal itemAmount = outboundItem.getAmount();
            if (itemAmount == null || itemAmount.compareTo(BigDecimal.ZERO) <= 0)
            {
                itemAmount = itemPrice.multiply(itemQuantity).setScale(2, RoundingMode.HALF_UP);
            }
            BigDecimal baseUnitPrice = baseQuantity.compareTo(BigDecimal.ZERO) > 0
                ? itemAmount.divide(baseQuantity, 4, RoundingMode.HALF_UP) : itemPrice;

            WmsStockLog stockLog = new WmsStockLog();
            stockLog.setWarehouseId(outbound.getWarehouseId());
            stockLog.setProductId(outboundItem.getProductId());
            stockLog.setLocationId(locationId);
            stockLog.setBatchNo(batchNo);
            stockLog.setBillType("outbound");
            stockLog.setBillId(outboundId);
            stockLog.setInOut("2");
            stockLog.setQuantity(baseQuantity);
            stockLog.setPrice(baseUnitPrice);
            stockLog.setAmount(itemAmount);
            stockLog.setBeforeQty(beforeQuantity);
            stockLog.setAfterQty(afterQuantity);
            stockLog.setCreateBy(operator);
            wmsStockLogMapper.insertWmsStockLog(stockLog);
            recordOutboundCost(outbound.getWarehouseId(), outboundItem.getProductId(), outboundId, "outbound", "2", baseQuantity, baseUnitPrice);
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unauditWmsOutbound(Long outboundId, String operator)
    {
        WmsOutbound outbound = wmsOutboundMapper.selectWmsOutboundByIdForUpdate(outboundId);
        if (outbound == null)
        {
            throw new ServiceException("出库单不存在");
        }
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(outbound.getDeptId(), outbound.getOwnerUserId(), outbound.getWarehouseId());
        }
        if (!"1".equals(outbound.getStatus()))
        {
            throw new ServiceException("仅已审核出库单允许反审核");
        }
        if ("1".equals(outbound.getIsReversal()))
        {
            throw new ServiceException("红冲单据不允许反审核");
        }
        if ("1".equals(outbound.getReversedFlag()))
        {
            throw new ServiceException("该出库单已被红冲，禁止反审核");
        }
        if ("3".equals(outbound.getDeliveryStatus()))
        {
            throw new ServiceException("该出库单已办理退货，禁止反审核");
        }
        finGeneralLedgerService.assertPeriodOpen(outbound.getBusinessDate());
        assertNoAuditedSaleReturn(outbound);
        finVoucherEventService.cancelPendingVoucherEvents("outbound", outboundId);
        finVoucherEventService.cancelPendingVoucherEvents("outbound_cogs", outboundId);

        FinReceivable linkedReceivable = selectLinkedReceivable(outbound);
        if (linkedReceivable != null)
        {
            if (defaultBigDecimal(linkedReceivable.getReceivedAmount()).compareTo(BigDecimal.ZERO) > 0
                || defaultBigDecimal(linkedReceivable.getBadDebtAmount()).compareTo(BigDecimal.ZERO) > 0)
            {
                throw new ServiceException("关联应收单已发生收款、核销或坏账确认，禁止反审核，请使用红冲功能");
            }
            assertNoDownstreamFinanceOnOutbound(outbound, linkedReceivable);
            finReceivableMapper.deleteFinReceivableById(linkedReceivable.getReceivableId());
        }
        else
        {
            assertNoDownstreamFinanceOnOutbound(outbound, null);
        }

        List<WmsOutboundItem> items = getOutboundItemList(outboundId);
        boolean relockOrder = shouldRelockParentSaleOrder(outbound) && wmsStockService != null;
        for (WmsOutboundItem item : items)
        {
            MdProduct product = mdProductMapper != null ? mdProductMapper.selectMdProductById(item.getProductId()) : null;
            if (wmsSerialNoService != null)
            {
                wmsSerialNoService.rollbackOutboundSerials(product, outbound.getWarehouseId(),
                    defaultLocationId(item.getLocationId()), outboundId, outbound.getOutboundNo(),
                    item.getSnCodes(), "出库单反审核回退至在库", operator);
            }
            BigDecimal baseQty = resolveBaseQuantity(item);
            restoreStockForReverse(outbound, item, baseQty, "outbound_unaudit", outboundId, operator);
            if (relockOrder)
            {
                wmsStockService.lockStockForSaleOrder(outbound.getWarehouseId(), item.getProductId(),
                    baseQty, outbound.getSourceBillId(), operator);
            }
        }
        if (priceResolverService != null)
        {
            priceResolverService.rollbackRecentSalePrice("outbound", outboundId);
        }

        outbound.setStatus("0");
        outbound.setBizStatus("draft");
        outbound.setDeliveryStatus("0");
        outbound.setAuditBy("");
        outbound.setAuditTime(null);
        outbound.setUnauditBy(operator);
        outbound.setUnauditTime(new Date());
        outbound.setUpdateBy(operator);
        int rows = wmsOutboundMapper.updateWmsOutbound(outbound);
        if (rows > 0)
        {
            syncSourceSaleOrderProgress(outbound, operator);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsOutbound reverseWmsOutbound(Long outboundId, String operator)
    {
        WmsOutbound origin = wmsOutboundMapper.selectWmsOutboundByIdForUpdate(outboundId);
        if (origin == null)
        {
            throw new ServiceException("出库单不存在");
        }
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(origin.getDeptId(), origin.getOwnerUserId(), origin.getWarehouseId());
        }
        if (!"1".equals(origin.getStatus()))
        {
            throw new ServiceException("仅已审核出库单允许红冲");
        }
        if ("1".equals(origin.getIsReversal()))
        {
            throw new ServiceException("红冲单据不允许再次红冲");
        }
        if ("1".equals(origin.getReversedFlag()))
        {
            throw new ServiceException("该出库单已被红冲，请勿重复红冲");
        }
        if ("3".equals(origin.getDeliveryStatus()))
        {
            throw new ServiceException("该出库单已办理退货，请勿重复红冲");
        }
        finGeneralLedgerService.assertPeriodOpen(new Date());
        assertNoAuditedSaleReturn(origin);
        List<WmsOutboundItem> originItems = getOutboundItemList(outboundId);

        WmsOutbound reversal = new WmsOutbound();
        reversal.setOutboundNo(generateOutboundNo());
        reversal.setOutboundType(origin.getOutboundType());
        reversal.setCustomerId(origin.getCustomerId());
        reversal.setWarehouseId(origin.getWarehouseId());
        reversal.setDeptId(origin.getDeptId());
        reversal.setOwnerUserId(origin.getOwnerUserId());
        reversal.setSourceBillType("outbound_reverse");
        reversal.setSourceBillId(origin.getOutboundId());
        reversal.setSourceBillNo(origin.getOutboundNo());
        reversal.setBusinessDate(new Date());
        reversal.setTotalQty(defaultBigDecimal(origin.getTotalQty()).negate());
        reversal.setTotalAmount(defaultBigDecimal(origin.getTotalAmount()).negate());
        reversal.setStatus("1");
        reversal.setBizStatus("completed");
        reversal.setDeliveryStatus("3");
        reversal.setAuditBy(operator);
        reversal.setAuditTime(new Date());
        reversal.setIsReversal("1");
        reversal.setOriginBillId(origin.getOutboundId());
        reversal.setReversedFlag("0");
        reversal.setCreateBy(operator);
        reversal.setRemark("红冲原出库单：" + origin.getOutboundNo());
        wmsOutboundMapper.insertWmsOutbound(reversal);

        boolean relockOrder = shouldRelockParentSaleOrder(origin) && wmsStockService != null;
        for (WmsOutboundItem originItem : originItems)
        {
            MdProduct product = mdProductMapper != null ? mdProductMapper.selectMdProductById(originItem.getProductId()) : null;
            if (wmsSerialNoService != null)
            {
                wmsSerialNoService.rollbackOutboundSerials(product, origin.getWarehouseId(),
                    defaultLocationId(originItem.getLocationId()), origin.getOutboundId(), reversal.getOutboundNo(),
                    originItem.getSnCodes(), "出库单红冲回退至在库", operator);
            }
            BigDecimal baseQty = resolveBaseQuantity(originItem);
            restoreStockForReverse(origin, originItem, baseQty, "outbound_reverse", reversal.getOutboundId(), operator);
            if (relockOrder)
            {
                wmsStockService.lockStockForSaleOrder(origin.getWarehouseId(), originItem.getProductId(),
                    baseQty, origin.getSourceBillId(), operator);
            }
            WmsOutboundItem revItem = new WmsOutboundItem();
            revItem.setOutboundId(reversal.getOutboundId());
            revItem.setSourceItemId(originItem.getSourceItemId());
            revItem.setProductId(originItem.getProductId());
            revItem.setLocationId(defaultLocationId(originItem.getLocationId()));
            revItem.setBatchNo(defaultBatchNo(originItem.getBatchNo()));
            revItem.setUnitName(originItem.getUnitName());
            revItem.setConvertRatio(originItem.getConvertRatio());
            revItem.setBaseQuantity(baseQty.negate());
            revItem.setQuantity(defaultBigDecimal(originItem.getQuantity()).negate());
            revItem.setPrice(originItem.getPrice());
            revItem.setAmount(defaultBigDecimal(originItem.getAmount()).negate());
            revItem.setDiscountRate(originItem.getDiscountRate());
            revItem.setTaxRate(originItem.getTaxRate());
            revItem.setTaxAmount(originItem.getTaxAmount() == null ? null : originItem.getTaxAmount().negate());
            revItem.setSnCodes(originItem.getSnCodes());
            wmsOutboundItemMapper.insertWmsOutboundItem(revItem);
        }

        origin.setReversedFlag("1");
        origin.setUpdateBy(operator);
        wmsOutboundMapper.updateWmsOutbound(origin);

        // 统一以签收确认为收入/成本确认口径：仅已签收的出库单红冲时才冲减应收、冲销收入与销售成本凭证
        if ("2".equals(origin.getDeliveryStatus()))
        {
            if (origin.getCustomerId() != null && defaultBigDecimal(origin.getTotalAmount()).compareTo(BigDecimal.ZERO) > 0)
            {
                finReceivableService.createRedReceivableForReturn(
                    origin.getCustomerId(), "outbound_reverse", reversal.getOutboundId(), reversal.getOutboundNo(),
                    new Date(), origin.getTotalAmount(), operator, "红冲出库单 " + origin.getOutboundNo() + " 自动生成红字应收");
            }
            finVoucherEventService.recordVoucherEvent("outbound", reversal.getOutboundId(), reversal.getOutboundNo(), "outbound_return",
                new Date(), origin.getTotalAmount(), operator, "已签收出库单红冲冲减收入凭证");
            BigDecimal cogsAmt = finCostCalculateService.resolveOutboundCogsAmount(origin.getOutboundId());
            if (cogsAmt.compareTo(BigDecimal.ZERO) > 0)
            {
                finVoucherEventService.recordVoucherEvent("outbound_cogs", reversal.getOutboundId(), reversal.getOutboundNo(), "outbound_cogs_return",
                    new Date(), cogsAmt, operator, "已签收出库单红冲冲回销售成本凭证");
            }
        }
        syncSourceSaleOrderProgress(origin, operator);
        return selectWmsOutboundById(reversal.getOutboundId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int shipWmsOutbound(Long outboundId, WmsOutbound wmsOutbound, String operator)
    {
        WmsOutbound outbound = wmsOutboundMapper.selectWmsOutboundByIdForUpdate(outboundId);
        if (outbound == null)
        {
            throw new ServiceException("出库单不存在");
        }
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(outbound.getDeptId(), outbound.getOwnerUserId(), outbound.getWarehouseId());
        }
        validateOutboundShippable(outbound);
        finGeneralLedgerService.assertPeriodOpen(outbound.getBusinessDate());
        WmsOutbound outboundPayload = wmsOutbound == null ? new WmsOutbound() : wmsOutbound;
        outbound.setCarrier(outboundPayload.getCarrier());
        outbound.setWaybillNo(outboundPayload.getWaybillNo());
        outbound.setFreightCost(outboundPayload.getFreightCost());
        outbound.setDeliveryStatus("1");
        outbound.setBizStatus("executing");
        outbound.setUpdateBy(operator);
        return wmsOutboundMapper.updateWmsOutbound(outbound);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int signWmsOutbound(Long outboundId, String operator)
    {
        WmsOutbound outbound = wmsOutboundMapper.selectWmsOutboundByIdForUpdate(outboundId);
        if (outbound == null)
        {
            throw new ServiceException("出库单不存在");
        }
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(outbound.getDeptId(), outbound.getOwnerUserId(), outbound.getWarehouseId());
        }
        validateOutboundSignable(outbound);
        finGeneralLedgerService.assertPeriodOpen(outbound.getBusinessDate());
        outbound.setDeliveryStatus("2");
        outbound.setBizStatus("completed");
        outbound.setUpdateBy(operator);
        int updateRows = wmsOutboundMapper.updateWmsOutbound(outbound);
        if (updateRows > 0)
        {
            syncReceivableCandidate(outbound, operator);
            syncRecentTransactionPrice(outbound, operator);
            syncSourceSaleOrderProgress(outbound, operator);
            // 统一签收确认节点同步确认收入凭证与结转主营业务成本(COGS)凭证
            finVoucherEventService.recordVoucherEvent("outbound", outbound.getOutboundId(), outbound.getOutboundNo(), "outbound_complete",
                outbound.getBusinessDate(), outbound.getTotalAmount(), operator, "销货出库签收确认收入待生成凭证");
            BigDecimal cogsAmt = finCostCalculateService.resolveOutboundCogsAmount(outbound.getOutboundId());
            if (cogsAmt.compareTo(BigDecimal.ZERO) > 0)
            {
                finVoucherEventService.recordVoucherEvent("outbound_cogs", outbound.getOutboundId(), outbound.getOutboundNo(), "outbound_cogs",
                    outbound.getBusinessDate(), cogsAmt, operator, "销货出库签收结转主营业务成本凭证");
            }
        }
        return updateRows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int returnWmsOutbound(Long outboundId, BigDecimal refundAmount, Long receivableId, String operator)
    {
        return returnWmsOutbound(outboundId, refundAmount, receivableId, null, operator);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int returnWmsOutbound(Long outboundId, BigDecimal refundAmount, Long receivableId, Long fundAccountId, String operator)
    {
        WmsOutbound outbound = wmsOutboundMapper.selectWmsOutboundByIdForUpdate(outboundId);
        if (outbound == null)
        {
            throw new ServiceException("出库单不存在");
        }
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(outbound.getDeptId(), outbound.getOwnerUserId(), outbound.getWarehouseId());
        }
        validateOutboundReturnable(outbound);
        finGeneralLedgerService.assertPeriodOpen(new Date());
        assertNoAuditedSaleReturn(outbound);
        validateRefundRequest(outbound, refundAmount);
        boolean wasSigned = "2".equals(outbound.getDeliveryStatus());
        restoreReturnedStock(outbound, operator);
        outbound.setDeliveryStatus("3");
        outbound.setBizStatus("closed");
        outbound.setUpdateBy(operator);
        int rows = wmsOutboundMapper.updateWmsOutbound(outbound);
        if (rows > 0)
        {
            if (wasSigned)
            {
                if (outbound.getCustomerId() != null && defaultBigDecimal(outbound.getTotalAmount()).compareTo(BigDecimal.ZERO) > 0)
                {
                    finReceivableService.createRedReceivableForReturn(
                        outbound.getCustomerId(), "outbound_return", outbound.getOutboundId(), outbound.getOutboundNo(),
                        new Date(), outbound.getTotalAmount(), operator, "已签收出库整单退货自动生成红字应收冲减单");
                }
                finVoucherEventService.recordVoucherEvent("outbound", outbound.getOutboundId(), outbound.getOutboundNo(), "outbound_return",
                    new Date(), outbound.getTotalAmount(), operator, "已签收出库整单退货冲减收入凭证");
                BigDecimal cogsAmt = finCostCalculateService.resolveOutboundCogsAmount(outbound.getOutboundId());
                if (cogsAmt.compareTo(BigDecimal.ZERO) > 0)
                {
                    finVoucherEventService.recordVoucherEvent("outbound_cogs", outbound.getOutboundId(), outbound.getOutboundNo(), "outbound_cogs_return",
                        new Date(), cogsAmt, operator, "已签收出库整单退货冲回销售成本凭证");
                }
            }
            if (refundAmount != null && refundAmount.compareTo(BigDecimal.ZERO) > 0 && outbound.getCustomerId() != null)
            {
                FinReceipt refundReceipt = buildRefundReceipt(outbound, refundAmount, receivableId, fundAccountId, operator);
                int receiptRows = finReceiptService.insertRefundReceipt(refundReceipt);
                if (receiptRows <= 0)
                {
                    throw new ServiceException("退货退款单生成失败");
                }
            }
            syncSourceSaleOrderProgress(outbound, operator);
        }
        return rows;
    }

    private void restoreStockForReverse(WmsOutbound origin, WmsOutboundItem item, BigDecimal baseQty, String billType, Long billId, String operator)
    {
        Long locationId = defaultLocationId(item.getLocationId());
        String batchNo = defaultBatchNo(item.getBatchNo());
        WmsStock stockQuery = new WmsStock();
        stockQuery.setWarehouseId(origin.getWarehouseId());
        stockQuery.setProductId(item.getProductId());
        stockQuery.setLocationId(locationId);
        stockQuery.setBatchNo(batchNo);
        WmsStock stock = wmsStockMapper.selectWmsStockByKeyForUpdate(stockQuery);
        BigDecimal beforeQty;
        if (stock == null)
        {
            stock = new WmsStock();
            stock.setWarehouseId(origin.getWarehouseId());
            stock.setProductId(item.getProductId());
            stock.setLocationId(locationId);
            stock.setBatchNo(batchNo);
            stock.setQuantity(baseQty);
            stock.setLockedQuantity(BigDecimal.ZERO);
            stock.setFrozenQuantity(BigDecimal.ZERO);
            fillStockWarningQty(stock, item.getProductId());
            stock.setVersion(0L);
            stock.setCreateBy(operator);
            wmsStockMapper.insertWmsStock(stock);
            beforeQty = BigDecimal.ZERO;
        }
        else
        {
            beforeQty = defaultBigDecimal(stock.getQuantity());
            stock.setQuantity(beforeQty.add(baseQty));
            stock.setUpdateBy(operator);
            wmsStockMapper.updateWmsStock(stock);
        }
        BigDecimal afterQty = beforeQty.add(baseQty);
        BigDecimal itemPrice = defaultBigDecimal(item.getPrice());
        BigDecimal baseFallbackCost = baseQty.compareTo(BigDecimal.ZERO) > 0
            ? defaultBigDecimal(item.getAmount()).divide(baseQty, 4, RoundingMode.HALF_UP) : itemPrice;
        // 严禁把销售单价写入回退库存流水，先解析原出库历史基准单位成本
        BigDecimal returnUnitCost = finCostCalculateService.resolveReturnUnitCost(
            origin.getWarehouseId(), item.getProductId(), "outbound", origin.getOutboundId(), baseFallbackCost);
        BigDecimal returnCostAmount = returnUnitCost.multiply(baseQty).setScale(2, RoundingMode.HALF_UP);
        WmsStockLog stockLog = new WmsStockLog();
        stockLog.setWarehouseId(origin.getWarehouseId());
        stockLog.setProductId(item.getProductId());
        stockLog.setLocationId(locationId);
        stockLog.setBatchNo(batchNo);
        stockLog.setBillType(billType);
        stockLog.setBillId(billId);
        stockLog.setInOut("1");
        stockLog.setQuantity(baseQty);
        stockLog.setPrice(returnUnitCost);
        stockLog.setAmount(returnCostAmount);
        stockLog.setBeforeQty(beforeQty);
        stockLog.setAfterQty(afterQty);
        stockLog.setCreateBy(operator);
        wmsStockLogMapper.insertWmsStockLog(stockLog);
        recordOutboundCost(origin.getWarehouseId(), item.getProductId(), billId, billType, "1", baseQty, returnUnitCost);
    }

    private void restoreReturnedStock(WmsOutbound outbound, String operator)
    {
        if (outbound.getWarehouseId() == null)
        {
            throw new ServiceException("退货仓库不能为空");
        }
        List<WmsOutboundItem> outboundItemList = getOutboundItemList(outbound.getOutboundId());
        for (WmsOutboundItem outboundItem : outboundItemList)
        {
            BigDecimal baseQty = resolveBaseQuantity(outboundItem);
            if (baseQty.compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("退货数量必须大于0");
            }
            if (outboundItem.getProductId() == null)
            {
                throw new ServiceException("退货明细商品不能为空");
            }
            MdProduct product = mdProductMapper != null ? mdProductMapper.selectMdProductById(outboundItem.getProductId()) : null;
            if (wmsSerialNoService != null)
            {
                wmsSerialNoService.rollbackOutboundSerials(product, outbound.getWarehouseId(),
                    defaultLocationId(outboundItem.getLocationId()), outbound.getOutboundId(), outbound.getOutboundNo(),
                    outboundItem.getSnCodes(), "出库整单退货回库至在库", operator);
            }
            restoreStockForReverse(outbound, outboundItem, baseQty, "outbound_return", outbound.getOutboundId(), operator);
        }
    }

    private BigDecimal resolveBaseQuantity(WmsOutboundItem item)
    {
        if (item.getBaseQuantity() != null && item.getBaseQuantity().compareTo(BigDecimal.ZERO) > 0)
        {
            return item.getBaseQuantity();
        }
        BigDecimal qty = defaultBigDecimal(item.getQuantity());
        BigDecimal ratio = item.getConvertRatio();
        if (ratio == null || ratio.compareTo(BigDecimal.ZERO) <= 0)
        {
            ratio = mdProductUnitService != null
                ? mdProductUnitService.resolveConvertRatio(item.getProductId(), item.getUnitName())
                : BigDecimal.ONE;
        }
        return qty.multiply(ratio).setScale(4, RoundingMode.HALF_UP);
    }

    private boolean shouldLockStockOnApprove()
    {
        if (sysConfigService == null)
        {
            return false;
        }
        String cfg = sysConfigService.selectConfigByKey(LOCK_ON_APPROVE_KEY);
        return "true".equalsIgnoreCase(cfg) || "1".equals(cfg);
    }

    private Date startOfToday()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void syncSourceSaleOrderProgress(WmsOutbound outbound, String operator)
    {
        if (outbound == null || !"sale_order".equals(outbound.getSourceBillType()) || outbound.getSourceBillId() == null)
        {
            return;
        }
        if (salOrderService != null)
        {
            salOrderService.syncOrderProgressFromWms(outbound.getSourceBillId(), operator);
            return;
        }
        SalOrder salOrder = salOrderMapper.selectSalOrderById(outbound.getSourceBillId());
        if (salOrder != null && "2".equals(outbound.getDeliveryStatus()))
        {
            salOrder.setStatus("2");
            salOrder.setBizStatus("completed");
            salOrder.setUpdateBy(operator);
            salOrderMapper.updateSalOrder(salOrder);
        }
    }

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
        if (refundAmount.compareTo(defaultBigDecimal(outbound.getTotalAmount())) > 0)
        {
            throw new ServiceException("退款金额不能超过出库单总金额");
        }
        if (refundAmount.compareTo(BigDecimal.ZERO) > 0 && outbound.getCustomerId() == null)
        {
            throw new ServiceException("存在退款金额时客户编号不能为空");
        }
    }

    private BigDecimal defaultBigDecimal(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO : value;
    }

    private BigDecimal calculateAvailableQuantity(WmsStock stock)
    {
        BigDecimal quantity = defaultBigDecimal(stock.getQuantity());
        BigDecimal lockedQuantity = defaultBigDecimal(stock.getLockedQuantity());
        BigDecimal frozenQuantity = defaultBigDecimal(stock.getFrozenQuantity());
        return quantity.subtract(lockedQuantity).subtract(frozenQuantity);
    }

    private Long defaultLocationId(Long locationId)
    {
        return locationId == null ? 0L : locationId;
    }

    private String defaultBatchNo(String batchNo)
    {
        return batchNo == null ? "" : batchNo.trim();
    }

    private boolean shouldAllowNegativeStock()
    {
        if (sysConfigService == null)
        {
            return false;
        }
        String cfg = sysConfigService.selectConfigByKey(ALLOW_NEGATIVE_STOCK_KEY);
        return "true".equalsIgnoreCase(cfg) || "1".equals(cfg);
    }

    private boolean isParentOrderStockLocked(WmsOutbound outbound)
    {
        if (!"sale_order".equals(outbound.getSourceBillType()) || outbound.getSourceBillId() == null)
        {
            return false;
        }
        if (salOrderMapper != null)
        {
            SalOrder parent = salOrderMapper.selectSalOrderById(outbound.getSourceBillId());
            if (parent != null && parent.getStockLockedFlag() != null)
            {
                return "1".equals(parent.getStockLockedFlag());
            }
        }
        return shouldLockStockOnApprove();
    }

    private boolean shouldRelockParentSaleOrder(WmsOutbound outbound)
    {
        if (!"sale_order".equals(outbound.getSourceBillType()) || outbound.getSourceBillId() == null)
        {
            return false;
        }
        if (salOrderMapper != null)
        {
            SalOrder parent = salOrderMapper.selectSalOrderById(outbound.getSourceBillId());
            if (parent != null)
            {
                if ("closed".equals(parent.getBizStatus()) || "3".equals(parent.getStatus()))
                {
                    return false;
                }
                if (parent.getStockLockedFlag() != null)
                {
                    return "1".equals(parent.getStockLockedFlag());
                }
            }
        }
        return shouldLockStockOnApprove();
    }

    private void releaseLockIfClosedParentOrder(WmsOutbound outbound, String operator)
    {
        if (!"sale_order".equals(outbound.getSourceBillType()) || outbound.getSourceBillId() == null
            || wmsStockService == null || salOrderMapper == null)
        {
            return;
        }
        SalOrder parent = salOrderMapper.selectSalOrderById(outbound.getSourceBillId());
        if (parent != null && ("closed".equals(parent.getBizStatus()) || "3".equals(parent.getStatus()))
            && "1".equals(parent.getStockLockedFlag()))
        {
            WmsOutboundItem q = new WmsOutboundItem();
            q.setOutboundId(outbound.getOutboundId());
            List<WmsOutboundItem> items = wmsOutboundItemMapper.selectWmsOutboundItemList(q);
            if (items != null)
            {
                for (WmsOutboundItem item : items)
                {
                    BigDecimal bq = resolveBaseQuantity(item);
                    if (bq.compareTo(BigDecimal.ZERO) > 0)
                    {
                        wmsStockService.releaseStockLockForSaleOrder(outbound.getWarehouseId(), item.getProductId(),
                            defaultLocationId(item.getLocationId()), defaultBatchNo(item.getBatchNo()),
                            bq, outbound.getSourceBillId(), operator);
                    }
                }
            }
        }
    }

    private void validateActiveMasterRefs(WmsOutbound wmsOutbound)
    {
        if (wmsOutbound.getCustomerId() != null && mdCustomerMapper != null)
        {
            MdCustomer customer = mdCustomerMapper.selectMdCustomerById(wmsOutbound.getCustomerId());
            if (customer != null && "1".equals(customer.getStatus()))
            {
                throw new ServiceException("客户[" + customer.getCustomerName() + "]已停用，禁止出库");
            }
        }
        if (wmsOutbound.getWarehouseId() != null && mdWarehouseMapper != null)
        {
            MdWarehouse warehouse = mdWarehouseMapper.selectMdWarehouseById(wmsOutbound.getWarehouseId());
            if (warehouse != null && "1".equals(warehouse.getStatus()))
            {
                throw new ServiceException("仓库[" + warehouse.getWarehouseName() + "]已停用，禁止出库");
            }
        }
    }

    private void validateLocationBelongsToWarehouse(Long locationId, Long warehouseId)
    {
        if (locationId == null || locationId <= 0L || warehouseId == null || mdLocationMapper == null)
        {
            return;
        }
        MdLocation location = mdLocationMapper.selectMdLocationByLocationId(locationId);
        if (location == null)
        {
            throw new ServiceException("指定库位(#" + locationId + ")不存在");
        }
        if (!warehouseId.equals(location.getWarehouseId()))
        {
            throw new ServiceException("库位[" + location.getLocationName() + "]不属于当前出库仓库，禁止跨仓混用库位");
        }
    }

    private void validateDirectOutboundCreditLimit(Long customerId, BigDecimal currentAmount)
    {
        if (customerId == null || currentAmount == null || currentAmount.compareTo(BigDecimal.ZERO) <= 0
            || mdCustomerMapper == null || finReceivableMapper == null)
        {
            return;
        }
        MdCustomer customer = mdCustomerMapper.selectMdCustomerById(customerId);
        if (customer == null || customer.getCreditLimit() == null || customer.getCreditLimit().compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }
        FinReceivable q = new FinReceivable();
        q.setCustomerId(customerId);
        List<FinReceivable> list = finReceivableMapper.selectFinReceivableList(q);
        BigDecimal unpaid = BigDecimal.ZERO;
        if (list != null)
        {
            for (FinReceivable r : list)
            {
                if ("draft".equals(r.getBizStatus()))
                {
                    continue;
                }
                BigDecimal rem = defaultBigDecimal(r.getAmount())
                    .subtract(defaultBigDecimal(r.getReceivedAmount()))
                    .subtract(defaultBigDecimal(r.getBadDebtAmount()));
                if ("2".equals(r.getBillDirection()))
                {
                    unpaid = unpaid.subtract(rem.max(BigDecimal.ZERO));
                }
                else
                {
                    unpaid = unpaid.add(rem.max(BigDecimal.ZERO));
                }
            }
        }
        if (unpaid.compareTo(BigDecimal.ZERO) < 0)
        {
            unpaid = BigDecimal.ZERO;
        }
        BigDecimal projected = unpaid.add(currentAmount);
        if (projected.compareTo(customer.getCreditLimit()) > 0)
        {
            throw new ServiceException("客户[" + customer.getCustomerName() + "]当前应收敞口(" + unpaid
                + ")加本次直开出库金额(" + currentAmount + ")合计 " + projected + " 超过信用额度 " + customer.getCreditLimit());
        }
    }

    private void validateOutboundItemsWithinSaleOrder(WmsOutbound wmsOutbound)
    {
        if (!"sale_order".equals(wmsOutbound.getSourceBillType()) || wmsOutbound.getSourceBillId() == null || salOrderItemMapper == null)
        {
            return;
        }
        List<SalOrderItem> orderItems = salOrderItemMapper.selectSalOrderItemListByOrderId(wmsOutbound.getSourceBillId());
        if (orderItems == null || orderItems.isEmpty())
        {
            return;
        }
        Map<Long, BigDecimal> orderLimitByItem = new HashMap<Long, BigDecimal>();
        Map<Long, BigDecimal> orderLimitByProduct = new HashMap<Long, BigDecimal>();
        for (SalOrderItem oi : orderItems)
        {
            BigDecimal base = oi.getBaseQuantity() != null && oi.getBaseQuantity().compareTo(BigDecimal.ZERO) > 0
                ? oi.getBaseQuantity() : defaultBigDecimal(oi.getQuantity());
            orderLimitByItem.put(oi.getOrderItemId(), base);
            orderLimitByProduct.merge(oi.getProductId(), base, BigDecimal::add);
        }
        WmsOutbound sibQ = new WmsOutbound();
        sibQ.setSourceBillType("sale_order");
        sibQ.setSourceBillId(wmsOutbound.getSourceBillId());
        List<WmsOutbound> siblings = wmsOutboundMapper.selectWmsOutboundList(sibQ);
        Map<Long, BigDecimal> usedByItem = new HashMap<Long, BigDecimal>();
        Map<Long, BigDecimal> usedByProduct = new HashMap<Long, BigDecimal>();
        if (siblings != null)
        {
            for (WmsOutbound sib : siblings)
            {
                if ("1".equals(sib.getIsReversal()) || "1".equals(sib.getReversedFlag()) || "3".equals(sib.getDeliveryStatus()))
                {
                    continue;
                }
                WmsOutboundItem iq = new WmsOutboundItem();
                iq.setOutboundId(sib.getOutboundId());
                List<WmsOutboundItem> sibItems = wmsOutboundItemMapper.selectWmsOutboundItemList(iq);
                if (sibItems == null)
                {
                    continue;
                }
                for (WmsOutboundItem si : sibItems)
                {
                    BigDecimal bq = resolveBaseQuantity(si);
                    if (si.getSourceItemId() != null)
                    {
                        usedByItem.merge(si.getSourceItemId(), bq, BigDecimal::add);
                    }
                    if (si.getProductId() != null)
                    {
                        usedByProduct.merge(si.getProductId(), bq, BigDecimal::add);
                    }
                }
            }
        }
        for (Map.Entry<Long, BigDecimal> e : usedByItem.entrySet())
        {
            BigDecimal limit = orderLimitByItem.get(e.getKey());
            if (limit != null && e.getValue().compareTo(limit) > 0)
            {
                throw new ServiceException("关联销售订单明细累计出库量(" + e.getValue() + ")超出订单数量(" + limit + ")，禁止超量出库");
            }
        }
        for (Map.Entry<Long, BigDecimal> e : usedByProduct.entrySet())
        {
            BigDecimal limit = orderLimitByProduct.get(e.getKey());
            if (limit != null && e.getValue().compareTo(limit) > 0)
            {
                throw new ServiceException("商品 #" + e.getKey() + " 累计出库量(" + e.getValue() + ")超出销售订单数量(" + limit + ")");
            }
        }
    }

    private void assertNoDownstreamFinanceOnOutbound(WmsOutbound outbound, FinReceivable linkedReceivable)
    {
        if (linkedReceivable != null)
        {
            if (finReceiptMapper != null)
            {
                FinReceipt recQ = new FinReceipt();
                recQ.setReceivableId(linkedReceivable.getReceivableId());
                List<FinReceipt> recs = finReceiptMapper.selectFinReceiptList(recQ);
                if (recs != null && !recs.isEmpty())
                {
                    throw new ServiceException("关联应收单存在收款单(" + recs.get(0).getReceiptNo() + ")，请先删除草稿或通过红冲处理");
                }
            }
            if (finWriteOffMapper != null)
            {
                FinWriteOff woQ = new FinWriteOff();
                woQ.setReceivableId(linkedReceivable.getReceivableId());
                List<FinWriteOff> wos = finWriteOffMapper.selectFinWriteOffList(woQ);
                if (wos != null && !wos.isEmpty())
                {
                    throw new ServiceException("关联应收单存在核销单(" + wos.get(0).getWriteOffNo() + ")，禁止反审核");
                }
            }
            if (finBadDebtMapper != null)
            {
                FinBadDebt bdQ = new FinBadDebt();
                bdQ.setReceivableId(linkedReceivable.getReceivableId());
                List<FinBadDebt> bds = finBadDebtMapper.selectFinBadDebtList(bdQ);
                if (bds != null && !bds.isEmpty())
                {
                    throw new ServiceException("关联应收单存在坏账记录(" + bds.get(0).getBadDebtNo() + ")，禁止反审核");
                }
            }
            if (finInvoiceMapper != null)
            {
                FinInvoice invQ = new FinInvoice();
                invQ.setSourceBillType("fin_receivable");
                invQ.setSourceBillId(linkedReceivable.getReceivableId());
                List<FinInvoice> invs = finInvoiceMapper.selectFinInvoiceList(invQ);
                if (invs != null && !invs.isEmpty())
                {
                    throw new ServiceException("关联应收单已开具销项发票(" + invs.get(0).getInvoiceNo() + ")，禁止反审核");
                }
            }
        }
        if (finInvoiceMapper != null)
        {
            FinInvoice invQ = new FinInvoice();
            invQ.setSourceBillType("wms_outbound");
            invQ.setSourceBillId(outbound.getOutboundId());
            List<FinInvoice> invs = finInvoiceMapper.selectFinInvoiceList(invQ);
            if (invs != null && !invs.isEmpty())
            {
                throw new ServiceException("该出库单已开具销项发票(" + invs.get(0).getInvoiceNo() + ")，禁止反审核");
            }
        }
    }

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
        existingFinReceivable.setSourceBillType("outbound");
        existingFinReceivable.setSourceBillId(outbound.getOutboundId());
        existingFinReceivable.setSourceBillNo(StringUtils.isNotEmpty(outbound.getOutboundNo()) ? outbound.getOutboundNo() : outbound.getSourceBillNo());
        existingFinReceivable.setBusinessDate(outbound.getBusinessDate());
        existingFinReceivable.setAmount(outbound.getTotalAmount());
        existingFinReceivable.setDueDate(resolveReceivableDueDate(outbound.getCustomerId(), outbound.getBusinessDate()));
        existingFinReceivable.setStatus(calculateReceivableStatus(existingFinReceivable.getReceivedAmount(), outbound.getTotalAmount()));
        existingFinReceivable.setBizStatus("confirmed");
        existingFinReceivable.setUpdateBy(operator);
        existingFinReceivable.setRemark(buildOutboundReceivableRemark(outbound));
        finReceivableMapper.updateFinReceivable(existingFinReceivable);
    }

    private FinReceivable selectLinkedReceivable(WmsOutbound outbound)
    {
        FinReceivable finReceivableQuery = new FinReceivable();
        finReceivableQuery.setBillType("outbound");
        List<FinReceivable> finReceivableList = finReceivableMapper.selectFinReceivableList(finReceivableQuery);
        if (finReceivableList != null)
        {
            for (FinReceivable finReceivable : finReceivableList)
            {
                if ("outbound".equals(finReceivable.getBillType()) && outbound.getOutboundId().equals(finReceivable.getBillId()))
                {
                    return finReceivable;
                }
            }
        }
        return null;
    }

    private FinReceivable buildReceivableCandidate(WmsOutbound outbound, String operator)
    {
        FinReceivable finReceivable = new FinReceivable();
        finReceivable.setCustomerId(outbound.getCustomerId());
        finReceivable.setBillType("outbound");
        finReceivable.setBillId(outbound.getOutboundId());
        // 以出库单自身作为唯一来源键，杜绝同销售订单分批出库或红冲重推时触发 uk_fin_receivable_source 冲突
        finReceivable.setSourceBillType("outbound");
        finReceivable.setSourceBillId(outbound.getOutboundId());
        finReceivable.setSourceBillNo(StringUtils.isNotEmpty(outbound.getOutboundNo()) ? outbound.getOutboundNo() : outbound.getSourceBillNo());
        finReceivable.setBusinessDate(outbound.getBusinessDate());
        finReceivable.setAmount(outbound.getTotalAmount());
        finReceivable.setDueDate(resolveReceivableDueDate(outbound.getCustomerId(), outbound.getBusinessDate()));
        finReceivable.setCreateBy(operator);
        finReceivable.setRemark(buildOutboundReceivableRemark(outbound));
        return finReceivable;
    }

    private String buildOutboundReceivableRemark(WmsOutbound outbound)
    {
        return "销货出库完成自动生成，关联出库单号：" + outbound.getOutboundNo();
    }

    private Date resolveReceivableDueDate(Long customerId, Date businessDate)
    {
        MdCustomer mdCustomer = mdCustomerMapper.selectMdCustomerById(customerId);
        int receivableDays = mdCustomer == null || mdCustomer.getReceivableDays() == null ? 30 : mdCustomer.getReceivableDays();
        Calendar dueDateCalendar = Calendar.getInstance();
        dueDateCalendar.setTime(businessDate == null ? new Date() : businessDate);
        dueDateCalendar.add(Calendar.DAY_OF_MONTH, receivableDays);
        return dueDateCalendar.getTime();
    }

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
            BigDecimal ratio = outboundItem.getConvertRatio();
            if (ratio == null || ratio.compareTo(BigDecimal.ZERO) <= 0)
            {
                ratio = mdProductUnitService != null
                    ? mdProductUnitService.resolveConvertRatio(outboundItem.getProductId(), outboundItem.getUnitName())
                    : BigDecimal.ONE;
            }
            if (priceResolverService != null)
            {
                priceResolverService.saveRecentSalePrice(outbound.getCustomerId(), outboundItem.getProductId(),
                    outboundItem.getPrice(), ratio, "outbound", outbound.getOutboundId(), outbound.getBusinessDate(), operator);
                continue;
            }
            BigDecimal baseUnitPrice = ratio.compareTo(BigDecimal.ONE) > 0
                ? outboundItem.getPrice().divide(ratio, 4, RoundingMode.HALF_UP) : outboundItem.getPrice();
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
                fillRecentTransaction(priceRecentTransaction, outbound, baseUnitPrice, operator);
                priceRecentTransactionMapper.insertPriceRecentTransaction(priceRecentTransaction);
            }
            else
            {
                fillRecentTransaction(priceRecentTransaction, outbound, baseUnitPrice, operator);
                priceRecentTransaction.setUpdateBy(operator);
                priceRecentTransaction.setRemark("销货出库完成自动更新");
                priceRecentTransactionMapper.updatePriceRecentTransaction(priceRecentTransaction);
            }
        }
    }

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

    private FinReceipt buildRefundReceipt(WmsOutbound outbound, BigDecimal refundAmount, Long receivableId, Long fundAccountId, String operator)
    {
        FinReceipt finReceipt = new FinReceipt();
        finReceipt.setCustomerId(outbound.getCustomerId());
        finReceipt.setReceivableId(receivableId);
        finReceipt.setFundAccountId(fundAccountId);
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

    private WmsOutbound getExistingOutbound(Long outboundId)
    {
        WmsOutbound outbound = wmsOutboundMapper.selectWmsOutboundById(outboundId);
        if (outbound == null)
        {
            throw new ServiceException("出库单不存在");
        }
        return outbound;
    }

    private void validateOutboundEditable(WmsOutbound outbound)
    {
        if (!"0".equals(outbound.getStatus()))
        {
            throw new ServiceException("出库单已审核，无法修改");
        }
    }

    private void validateOutboundShippable(WmsOutbound outbound)
    {
        if ("1".equals(outbound.getIsReversal()) || "1".equals(outbound.getReversedFlag()))
        {
            throw new ServiceException("红冲单据或已红冲出库单禁止发货");
        }
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

    private void validateOutboundSignable(WmsOutbound outbound)
    {
        if ("1".equals(outbound.getIsReversal()) || "1".equals(outbound.getReversedFlag()))
        {
            throw new ServiceException("红冲单据或已红冲出库单禁止签收");
        }
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

    private void validateOutboundReturnable(WmsOutbound outbound)
    {
        if ("1".equals(outbound.getIsReversal()) || "1".equals(outbound.getReversedFlag()))
        {
            throw new ServiceException("红冲单据或已红冲出库单禁止办理退货");
        }
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

    private WmsOutbound checkOutboundDeletable(Long outboundId)
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
        return outbound;
    }

    private void assertNoAuditedSaleReturn(WmsOutbound outbound)
    {
        WmsSaleReturn query = new WmsSaleReturn();
        query.setSourceBillId(outbound.getOutboundId());
        query.setStatus("1");
        List<WmsSaleReturn> byId = wmsSaleReturnMapper.selectWmsSaleReturnList(query);
        if (byId != null)
        {
            for (WmsSaleReturn sr : byId)
            {
                if (!"1".equals(sr.getIsReversal()) && !"1".equals(sr.getReversedFlag()))
                {
                    throw new ServiceException("该出库单已通过销售退货单办理退货，请勿重复操作");
                }
            }
        }
        if (StringUtils.isNotEmpty(outbound.getOutboundNo()))
        {
            WmsSaleReturn noQuery = new WmsSaleReturn();
            noQuery.setSourceBillNo(outbound.getOutboundNo());
            noQuery.setStatus("1");
            List<WmsSaleReturn> byNo = wmsSaleReturnMapper.selectWmsSaleReturnList(noQuery);
            if (byNo != null)
            {
                for (WmsSaleReturn sr : byNo)
                {
                    if (!"1".equals(sr.getIsReversal()) && !"1".equals(sr.getReversedFlag())
                        && outbound.getOutboundNo().equals(sr.getSourceBillNo()))
                    {
                        throw new ServiceException("该出库单已通过销售退货单办理退货，请勿重复操作");
                    }
                }
            }
        }
    }

    private void recordOutboundCost(Long warehouseId, Long productId, Long outboundId, String billType, String inOut, BigDecimal quantity, BigDecimal price)
    {
        FinCostCalculate req = new FinCostCalculate();
        req.setWarehouseId(warehouseId);
        req.setProductId(productId);
        req.setBillType(billType);
        req.setBillId(outboundId);
        req.setInOut(inOut);
        req.setQuantity(quantity);
        req.setPrice(price.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : price);
        finCostCalculateService.calculateCost(req);
    }

    private void fillStockWarningQty(WmsStock stock, Long productId)
    {
        MdProduct mdProduct = mdProductMapper.selectMdProductById(productId);
        stock.setWarningMinQty(mdProduct == null ? BigDecimal.ZERO : defaultBigDecimal(mdProduct.getWarningMinQty()));
        stock.setWarningMaxQty(mdProduct == null ? BigDecimal.ZERO : defaultBigDecimal(mdProduct.getWarningMaxQty()));
    }
}
