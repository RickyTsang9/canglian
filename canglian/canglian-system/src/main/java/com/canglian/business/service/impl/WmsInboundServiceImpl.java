package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.canglian.business.domain.FinCostCalculate;
import com.canglian.business.domain.FinInvoice;
import com.canglian.business.domain.FinPayable;
import com.canglian.business.domain.FinPayment;
import com.canglian.business.domain.FinWriteOff;
import com.canglian.business.domain.MdLocation;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.MdSupplier;
import com.canglian.business.domain.MdWarehouse;
import com.canglian.business.domain.PurOrder;
import com.canglian.business.domain.PurOrderItem;
import com.canglian.business.domain.WmsInbound;
import com.canglian.business.domain.WmsInboundItem;
import com.canglian.business.domain.WmsPurchaseReturn;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsStockLog;
import com.canglian.business.mapper.FinInvoiceMapper;
import com.canglian.business.mapper.FinPayableMapper;
import com.canglian.business.mapper.FinPaymentMapper;
import com.canglian.business.mapper.FinWriteOffMapper;
import com.canglian.business.mapper.MdLocationMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.MdSupplierMapper;
import com.canglian.business.mapper.MdWarehouseMapper;
import com.canglian.business.mapper.PurOrderItemMapper;
import com.canglian.business.mapper.PurOrderMapper;
import com.canglian.business.mapper.WmsInboundItemMapper;
import com.canglian.business.mapper.WmsInboundMapper;
import com.canglian.business.mapper.WmsPurchaseReturnMapper;
import com.canglian.business.mapper.WmsStockLogMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IFinCostCalculateService;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IFinPayableService;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.business.service.IMdProductUnitService;
import com.canglian.business.service.IPurOrderService;
import com.canglian.business.service.IWmsInboundService;
import com.canglian.business.service.IWmsSerialNoService;
import com.canglian.common.exception.ServiceException;
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

    @Autowired(required = false)
    private WmsPurchaseReturnMapper wmsPurchaseReturnMapper;

    @Autowired
    private IFinPayableService finPayableService;

    @Autowired
    private IFinVoucherEventService finVoucherEventService;

    @Autowired
    private IFinGeneralLedgerService finGeneralLedgerService;

    @Autowired
    private IFinCostCalculateService finCostCalculateService;

    @Autowired(required = false)
    private IMdProductUnitService mdProductUnitService;

    @Lazy
    @Autowired(required = false)
    private IPurOrderService purOrderService;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Autowired(required = false)
    private MdWarehouseMapper mdWarehouseMapper;

    @Autowired(required = false)
    private MdLocationMapper mdLocationMapper;

    @Autowired(required = false)
    private IWmsSerialNoService wmsSerialNoService;

    @Autowired(required = false)
    private FinPaymentMapper finPaymentMapper;

    @Autowired(required = false)
    private FinWriteOffMapper finWriteOffMapper;

    @Autowired(required = false)
    private FinInvoiceMapper finInvoiceMapper;

    @Autowired(required = false)
    private PurOrderItemMapper purOrderItemMapper;

    @Override
    public WmsInbound selectWmsInboundById(Long inboundId)
    {
        WmsInbound inbound = wmsInboundMapper.selectWmsInboundById(inboundId);
        if (inbound != null)
        {
            if (bizScopeHelper != null)
            {
                bizScopeHelper.assertRecordAccess(inbound.getDeptId(), inbound.getOwnerUserId(), inbound.getWarehouseId());
            }
            WmsInboundItem itemQuery = new WmsInboundItem();
            itemQuery.setInboundId(inboundId);
            inbound.setItemList(wmsInboundItemMapper.selectWmsInboundItemList(itemQuery));
        }
        return inbound;
    }

    @Override
    public List<WmsInbound> selectWmsInboundList(WmsInbound wmsInbound)
    {
        if (bizScopeHelper != null)
        {
            bizScopeHelper.applyScopeFilter(wmsInbound, "", true, true);
        }
        return wmsInboundMapper.selectWmsInboundList(wmsInbound);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWmsInbound(WmsInbound wmsInbound)
    {
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(wmsInbound.getWarehouseId());
            if (wmsInbound.getDeptId() == null)
            {
                wmsInbound.setDeptId(bizScopeHelper.currentDeptId());
            }
            if (wmsInbound.getOwnerUserId() == null)
            {
                wmsInbound.setOwnerUserId(bizScopeHelper.currentUserId());
            }
        }
        if (StringUtils.isEmpty(wmsInbound.getInboundNo()))
        {
            wmsInbound.setInboundNo(generateInboundNo());
        }
        if (wmsInbound.getBusinessDate() == null)
        {
            wmsInbound.setBusinessDate(new Date());
        }
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(wmsInbound.getBusinessDate());
        }
        validateActiveMasterRefs(wmsInbound);
        wmsInbound.setTotalQty(BigDecimal.ZERO);
        wmsInbound.setTotalAmount(BigDecimal.ZERO);
        wmsInbound.setStatus("0");
        wmsInbound.setBizStatus("draft");
        wmsInbound.setIsReversal("0");
        wmsInbound.setReversedFlag("0");
        wmsInbound.setAuditBy(null);
        wmsInbound.setAuditTime(null);
        int rows = wmsInboundMapper.insertWmsInbound(wmsInbound);
        if (rows > 0 && wmsInbound.getItemList() != null && !wmsInbound.getItemList().isEmpty())
        {
            saveInboundItemsAndRecalculate(wmsInbound);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWmsInbound(WmsInbound wmsInbound)
    {
        WmsInbound inbound = getExistingInbound(wmsInbound.getInboundId());
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(inbound.getDeptId(), inbound.getOwnerUserId(), inbound.getWarehouseId());
            bizScopeHelper.assertWarehouseAccess(wmsInbound.getWarehouseId());
        }
        validateInboundEditable(inbound);
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(inbound.getBusinessDate());
            if (wmsInbound.getBusinessDate() != null)
            {
                finGeneralLedgerService.assertPeriodOpen(wmsInbound.getBusinessDate());
            }
        }
        if (wmsInbound.getWarehouseId() == null)
        {
            wmsInbound.setWarehouseId(inbound.getWarehouseId());
        }
        validateActiveMasterRefs(wmsInbound);
        wmsInbound.setSourceBillType(inbound.getSourceBillType());
        wmsInbound.setSourceBillId(inbound.getSourceBillId());
        wmsInbound.setSourceBillNo(inbound.getSourceBillNo());
        wmsInbound.setTotalQty(inbound.getTotalQty());
        wmsInbound.setTotalAmount(inbound.getTotalAmount());
        wmsInbound.setStatus(inbound.getStatus());
        wmsInbound.setBizStatus(inbound.getBizStatus());
        wmsInbound.setAuditBy(inbound.getAuditBy());
        wmsInbound.setAuditTime(inbound.getAuditTime());
        int rows = wmsInboundMapper.updateWmsInbound(wmsInbound);
        if (rows > 0 && wmsInbound.getItemList() != null)
        {
            wmsInboundItemMapper.deleteWmsInboundItemByInboundId(wmsInbound.getInboundId());
            saveInboundItemsAndRecalculate(wmsInbound);
            validateInboundItemsWithinPurchaseOrder(wmsInbound);
            syncSourcePurchaseOrderProgress(wmsInbound, wmsInbound.getUpdateBy());
        }
        return rows;
    }

    private void saveInboundItemsAndRecalculate(WmsInbound wmsInbound)
    {
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal taxTotalAmount = BigDecimal.ZERO;
        if (wmsInbound.getItemList() != null)
        {
            for (WmsInboundItem item : wmsInbound.getItemList())
            {
                if (item.getProductId() == null)
                {
                    throw new ServiceException("入库明细商品不能为空");
                }
                BigDecimal qty = defaultBigDecimal(item.getQuantity());
                if (qty.compareTo(BigDecimal.ZERO) <= 0)
                {
                    throw new ServiceException("入库明细数量必须大于0");
                }
                BigDecimal price = defaultBigDecimal(item.getPrice());
                if (price.compareTo(BigDecimal.ZERO) < 0)
                {
                    throw new ServiceException("入库明细单价不能为负数");
                }
                BigDecimal amount = item.getAmount();
                if (amount == null || (amount.compareTo(BigDecimal.ZERO) <= 0 && price.compareTo(BigDecimal.ZERO) > 0))
                {
                    amount = qty.multiply(price).setScale(2, RoundingMode.HALF_UP);
                }
                MdProduct product = mdProductMapper != null ? mdProductMapper.selectMdProductById(item.getProductId()) : null;
                if (product != null && "1".equals(product.getStatus()))
                {
                    throw new ServiceException("商品[" + product.getProductName() + "]已停用，禁止入库");
                }
                validateLocationBelongsToWarehouse(item.getLocationId(), wmsInbound.getWarehouseId());
                if (StringUtils.isEmpty(item.getUnitName()) && product != null)
                {
                    item.setUnitName(StringUtils.isNotEmpty(product.getPurchaseUnit()) ? product.getPurchaseUnit() : product.getUnitName());
                }
                BigDecimal ratio = mdProductUnitService != null
                    ? mdProductUnitService.resolveConvertRatio(item.getProductId(), item.getUnitName())
                    : (item.getConvertRatio() != null && item.getConvertRatio().compareTo(BigDecimal.ZERO) > 0 ? item.getConvertRatio() : BigDecimal.ONE);
                item.setConvertRatio(ratio);
                item.setBaseQuantity(qty.multiply(ratio).setScale(4, RoundingMode.HALF_UP));
                autoFillExpiryDate(item, product);

                item.setInboundId(wmsInbound.getInboundId());
                item.setLocationId(item.getLocationId() == null ? 0L : item.getLocationId());
                item.setBatchNo(defaultBatchNo(item.getBatchNo()));
                item.setQuantity(qty);
                item.setPrice(price);
                item.setAmount(amount);
                if (item.getTaxAmount() != null)
                {
                    taxTotalAmount = taxTotalAmount.add(item.getTaxAmount());
                }
                wmsInboundItemMapper.insertWmsInboundItem(item);
                totalQty = totalQty.add(qty);
                totalAmount = totalAmount.add(amount);
            }
        }
        wmsInbound.setTotalQty(totalQty);
        wmsInbound.setTotalAmount(totalAmount);
        wmsInbound.setTaxTotalAmount(taxTotalAmount);
        wmsInboundMapper.updateWmsInbound(wmsInbound);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsInboundById(Long inboundId)
    {
        WmsInbound inbound = checkInboundDeletable(inboundId);
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(inbound.getDeptId(), inbound.getOwnerUserId(), inbound.getWarehouseId());
        }
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(inbound.getBusinessDate());
        }
        wmsInboundItemMapper.deleteWmsInboundItemByInboundId(inboundId);
        int rows = wmsInboundMapper.deleteWmsInboundById(inboundId);
        syncSourcePurchaseOrderProgress(inbound, inbound.getUpdateBy());
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsInboundByIds(Long[] inboundIds)
    {
        List<WmsInbound> toDelete = new ArrayList<WmsInbound>();
        for (Long inboundId : inboundIds)
        {
            WmsInbound inbound = checkInboundDeletable(inboundId);
            if (bizScopeHelper != null)
            {
                bizScopeHelper.assertRecordAccess(inbound.getDeptId(), inbound.getOwnerUserId(), inbound.getWarehouseId());
            }
            if (finGeneralLedgerService != null)
            {
                finGeneralLedgerService.assertPeriodOpen(inbound.getBusinessDate());
            }
            toDelete.add(inbound);
        }
        for (Long inboundId : inboundIds)
        {
            wmsInboundItemMapper.deleteWmsInboundItemByInboundId(inboundId);
        }
        int rows = wmsInboundMapper.deleteWmsInboundByIds(inboundIds);
        for (WmsInbound inbound : toDelete)
        {
            syncSourcePurchaseOrderProgress(inbound, inbound.getUpdateBy());
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int auditWmsInbound(Long inboundId, String operator)
    {
        WmsInbound inbound = wmsInboundMapper.selectWmsInboundByIdForUpdate(inboundId);
        if (inbound == null)
        {
            throw new ServiceException("入库单不存在");
        }
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(inbound.getDeptId(), inbound.getOwnerUserId(), inbound.getWarehouseId());
        }
        if (!"0".equals(inbound.getStatus()))
        {
            throw new ServiceException("入库单状态已变更，无法审核");
        }
        finGeneralLedgerService.assertPeriodOpen(inbound.getBusinessDate());
        validateActiveMasterRefs(inbound);
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
            validateLocationBelongsToWarehouse(inboundItem.getLocationId(), inbound.getWarehouseId());
            MdProduct product = mdProductMapper != null ? mdProductMapper.selectMdProductById(inboundItem.getProductId()) : null;
            if (product != null && "1".equals(product.getStatus()))
            {
                throw new ServiceException("商品[" + product.getProductName() + "]已停用，禁止入库审核");
            }
            BigDecimal baseQuantity = resolveBaseQuantity(inboundItem);
            Date beforeExpiry = inboundItem.getExpiryDate();
            autoFillExpiryDate(inboundItem, product);
            if (beforeExpiry == null && inboundItem.getExpiryDate() != null && inboundItem.getInboundItemId() != null)
            {
                wmsInboundItemMapper.updateWmsInboundItem(inboundItem);
            }

            Long locationId = defaultLocationId(inboundItem.getLocationId());
            String batchNo = defaultBatchNo(inboundItem.getBatchNo());
            // 序列号强管控：启用序列号的商品必须录入足额唯一 SN 并自动建档入库
            if (wmsSerialNoService != null)
            {
                wmsSerialNoService.handleInboundSerials(product, inbound.getWarehouseId(), locationId, batchNo,
                    inboundId, inbound.getInboundNo(), inboundItem.getExpiryDate(), inboundItem.getSnCodes(), baseQuantity, operator);
            }
            WmsStock stockQuery = new WmsStock();
            stockQuery.setWarehouseId(inbound.getWarehouseId());
            stockQuery.setProductId(inboundItem.getProductId());
            stockQuery.setLocationId(locationId);
            stockQuery.setBatchNo(batchNo);
            WmsStock stock = wmsStockMapper.selectWmsStockByKeyForUpdate(stockQuery);
            BigDecimal beforeQuantity;
            if (stock == null)
            {
                stock = new WmsStock();
                stock.setWarehouseId(inbound.getWarehouseId());
                stock.setProductId(inboundItem.getProductId());
                stock.setLocationId(locationId);
                stock.setBatchNo(batchNo);
                stock.setProductionDate(inboundItem.getProductionDate());
                stock.setExpiryDate(inboundItem.getExpiryDate());
                stock.setQuantity(baseQuantity);
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
                stock.setQuantity(beforeQuantity.add(baseQuantity));
                if (inboundItem.getProductionDate() != null)
                {
                    stock.setProductionDate(inboundItem.getProductionDate());
                }
                if (inboundItem.getExpiryDate() != null)
                {
                    stock.setExpiryDate(inboundItem.getExpiryDate());
                }
                stock.setUpdateBy(operator);
                wmsStockMapper.updateWmsStock(stock);
            }
            BigDecimal afterQuantity = beforeQuantity.add(baseQuantity);
            BigDecimal itemPrice = defaultBigDecimal(inboundItem.getPrice());
            BigDecimal itemAmount = inboundItem.getAmount();
            if (itemAmount == null || itemAmount.compareTo(BigDecimal.ZERO) <= 0)
            {
                itemAmount = itemPrice.multiply(itemQuantity).setScale(2, RoundingMode.HALF_UP);
            }
            BigDecimal baseUnitCost = baseQuantity.compareTo(BigDecimal.ZERO) > 0
                ? itemAmount.divide(baseQuantity, 4, RoundingMode.HALF_UP) : itemPrice;

            WmsStockLog stockLog = new WmsStockLog();
            stockLog.setWarehouseId(inbound.getWarehouseId());
            stockLog.setProductId(inboundItem.getProductId());
            stockLog.setLocationId(locationId);
            stockLog.setBatchNo(batchNo);
            stockLog.setBillType("inbound");
            stockLog.setBillId(inboundId);
            stockLog.setInOut("1");
            stockLog.setQuantity(baseQuantity);
            stockLog.setPrice(baseUnitCost);
            stockLog.setAmount(itemAmount);
            stockLog.setBeforeQty(beforeQuantity);
            stockLog.setAfterQty(afterQuantity);
            stockLog.setCreateBy(operator);
            wmsStockLogMapper.insertWmsStockLog(stockLog);
            recordInboundCost(inbound.getWarehouseId(), inboundItem.getProductId(), inboundId, "inbound", "1", baseQuantity, baseUnitCost);
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
            syncSourcePurchaseOrderProgress(inbound, operator);
            finVoucherEventService.recordVoucherEvent("inbound", inbound.getInboundId(), inbound.getInboundNo(), "inbound_complete",
                inbound.getBusinessDate(), inbound.getTotalAmount(), operator, "采购入库完成待生成凭证");
        }
        return updateRows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unauditWmsInbound(Long inboundId, String operator)
    {
        WmsInbound inbound = wmsInboundMapper.selectWmsInboundByIdForUpdate(inboundId);
        if (inbound == null)
        {
            throw new ServiceException("入库单不存在");
        }
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(inbound.getDeptId(), inbound.getOwnerUserId(), inbound.getWarehouseId());
        }
        if (!"1".equals(inbound.getStatus()))
        {
            throw new ServiceException("仅已审核入库单允许反审核");
        }
        if ("1".equals(inbound.getIsReversal()))
        {
            throw new ServiceException("红冲单据不允许反审核");
        }
        if ("1".equals(inbound.getReversedFlag()))
        {
            throw new ServiceException("该入库单已被红冲，禁止反审核");
        }
        finGeneralLedgerService.assertPeriodOpen(inbound.getBusinessDate());
        assertNoAuditedPurchaseReturn(inbound);
        finVoucherEventService.cancelPendingVoucherEvents("inbound", inboundId);

        FinPayable linkedPayable = selectLinkedPayable(inbound);
        if (linkedPayable != null)
        {
            if (defaultBigDecimal(linkedPayable.getPaidAmount()).compareTo(BigDecimal.ZERO) > 0)
            {
                throw new ServiceException("关联应付单已发生付款或核销，禁止反审核，请使用红冲功能");
            }
            assertNoDownstreamFinanceOnInbound(inbound, linkedPayable);
            finPayableMapper.deleteFinPayableById(linkedPayable.getPayableId());
        }
        else
        {
            assertNoDownstreamFinanceOnInbound(inbound, null);
        }

        WmsInboundItem q = new WmsInboundItem();
        q.setInboundId(inboundId);
        List<WmsInboundItem> items = wmsInboundItemMapper.selectWmsInboundItemList(q);
        if (items != null)
        {
            for (WmsInboundItem item : items)
            {
                MdProduct product = mdProductMapper != null ? mdProductMapper.selectMdProductById(item.getProductId()) : null;
                if (wmsSerialNoService != null)
                {
                    wmsSerialNoService.rollbackInboundSerials(product, inboundId, inbound.getInboundNo(), item.getSnCodes(), true, operator);
                }
                BigDecimal baseQuantity = resolveBaseQuantity(item);
                deductStockForReverse(inbound.getWarehouseId(), item, baseQuantity, "inbound_unaudit", inboundId, operator);
            }
        }

        inbound.setStatus("0");
        inbound.setBizStatus("draft");
        inbound.setAuditBy("");
        inbound.setAuditTime(null);
        inbound.setUnauditBy(operator);
        inbound.setUnauditTime(new Date());
        inbound.setUpdateBy(operator);
        int rows = wmsInboundMapper.updateWmsInbound(inbound);
        if (rows > 0)
        {
            syncSourcePurchaseOrderProgress(inbound, operator);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsInbound reverseWmsInbound(Long inboundId, String operator)
    {
        WmsInbound origin = wmsInboundMapper.selectWmsInboundByIdForUpdate(inboundId);
        if (origin == null)
        {
            throw new ServiceException("入库单不存在");
        }
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(origin.getDeptId(), origin.getOwnerUserId(), origin.getWarehouseId());
        }
        if (!"1".equals(origin.getStatus()))
        {
            throw new ServiceException("仅已审核入库单允许红冲");
        }
        if ("1".equals(origin.getIsReversal()))
        {
            throw new ServiceException("红冲单据不允许再次红冲");
        }
        if ("1".equals(origin.getReversedFlag()))
        {
            throw new ServiceException("该入库单已被红冲，请勿重复红冲");
        }
        finGeneralLedgerService.assertPeriodOpen(new Date());
        assertNoAuditedPurchaseReturn(origin);

        WmsInboundItem q = new WmsInboundItem();
        q.setInboundId(inboundId);
        List<WmsInboundItem> originItems = wmsInboundItemMapper.selectWmsInboundItemList(q);
        if (originItems == null || originItems.isEmpty())
        {
            throw new ServiceException("原入库单无明细，无法红冲");
        }

        WmsInbound reversal = new WmsInbound();
        reversal.setInboundNo(generateInboundNo());
        reversal.setInboundType(origin.getInboundType());
        reversal.setSupplierId(origin.getSupplierId());
        reversal.setWarehouseId(origin.getWarehouseId());
        reversal.setDeptId(origin.getDeptId());
        reversal.setOwnerUserId(origin.getOwnerUserId());
        reversal.setSourceBillType("inbound_reverse");
        reversal.setSourceBillId(origin.getInboundId());
        reversal.setSourceBillNo(origin.getInboundNo());
        reversal.setBusinessDate(new Date());
        reversal.setTotalQty(defaultBigDecimal(origin.getTotalQty()).negate());
        reversal.setTotalAmount(defaultBigDecimal(origin.getTotalAmount()).negate());
        reversal.setStatus("1");
        reversal.setBizStatus("completed");
        reversal.setAuditBy(operator);
        reversal.setAuditTime(new Date());
        reversal.setIsReversal("1");
        reversal.setOriginBillId(origin.getInboundId());
        reversal.setReversedFlag("0");
        reversal.setCreateBy(operator);
        reversal.setRemark("红冲原入库单：" + origin.getInboundNo());
        wmsInboundMapper.insertWmsInbound(reversal);

        for (WmsInboundItem originItem : originItems)
        {
            MdProduct product = mdProductMapper != null ? mdProductMapper.selectMdProductById(originItem.getProductId()) : null;
            if (wmsSerialNoService != null)
            {
                wmsSerialNoService.rollbackInboundSerials(product, origin.getInboundId(), reversal.getInboundNo(), originItem.getSnCodes(), false, operator);
            }
            BigDecimal baseQuantity = resolveBaseQuantity(originItem);
            deductStockForReverse(origin.getWarehouseId(), originItem, baseQuantity, "inbound_reverse", reversal.getInboundId(), operator);
            WmsInboundItem revItem = new WmsInboundItem();
            revItem.setInboundId(reversal.getInboundId());
            revItem.setSourceItemId(originItem.getSourceItemId());
            revItem.setProductId(originItem.getProductId());
            revItem.setLocationId(defaultLocationId(originItem.getLocationId()));
            revItem.setBatchNo(defaultBatchNo(originItem.getBatchNo()));
            revItem.setUnitName(originItem.getUnitName());
            revItem.setConvertRatio(originItem.getConvertRatio());
            revItem.setBaseQuantity(baseQuantity.negate());
            revItem.setProductionDate(originItem.getProductionDate());
            revItem.setExpiryDate(originItem.getExpiryDate());
            revItem.setQuantity(defaultBigDecimal(originItem.getQuantity()).negate());
            revItem.setPrice(originItem.getPrice());
            revItem.setAmount(defaultBigDecimal(originItem.getAmount()).negate());
            revItem.setDiscountRate(originItem.getDiscountRate());
            revItem.setTaxRate(originItem.getTaxRate());
            revItem.setTaxAmount(originItem.getTaxAmount() == null ? null : originItem.getTaxAmount().negate());
            revItem.setSnCodes(originItem.getSnCodes());
            wmsInboundItemMapper.insertWmsInboundItem(revItem);
        }

        origin.setReversedFlag("1");
        origin.setUpdateBy(operator);
        wmsInboundMapper.updateWmsInbound(origin);

        if (origin.getSupplierId() != null && defaultBigDecimal(origin.getTotalAmount()).compareTo(BigDecimal.ZERO) > 0)
        {
            finPayableService.createRedPayableForReturn(
                origin.getSupplierId(), "inbound_reverse", reversal.getInboundId(), reversal.getInboundNo(),
                new Date(), origin.getTotalAmount(), operator, "红冲入库单 " + origin.getInboundNo() + " 自动生成红字应付");
        }
        finVoucherEventService.recordVoucherEvent("inbound", reversal.getInboundId(), reversal.getInboundNo(), "purchase_return_complete",
            new Date(), origin.getTotalAmount(), operator, "入库单红冲待生成冲销凭证");
        syncSourcePurchaseOrderProgress(origin, operator);
        return selectWmsInboundById(reversal.getInboundId());
    }

    private void deductStockForReverse(Long warehouseId, WmsInboundItem item, BigDecimal baseQty, String billType, Long billId, String operator)
    {
        Long locationId = defaultLocationId(item.getLocationId());
        String batchNo = defaultBatchNo(item.getBatchNo());
        WmsStock stockQuery = new WmsStock();
        stockQuery.setWarehouseId(warehouseId);
        stockQuery.setProductId(item.getProductId());
        stockQuery.setLocationId(locationId);
        stockQuery.setBatchNo(batchNo);
        WmsStock stock = wmsStockMapper.selectWmsStockByKeyForUpdate(stockQuery);
        if (stock == null)
        {
            throw new ServiceException("当前库存记录不存在或已消耗，无法回退入库单");
        }
        BigDecimal beforeQty = defaultBigDecimal(stock.getQuantity());
        BigDecimal availableQty = beforeQty.subtract(defaultBigDecimal(stock.getLockedQuantity())).subtract(defaultBigDecimal(stock.getFrozenQuantity()));
        if (availableQty.compareTo(baseQty) < 0)
        {
            throw new ServiceException("商品 #" + item.getProductId() + " 可用库存(" + availableQty + ")不足以扣回数量(" + baseQty + ")，请先处理后续出库/锁定");
        }
        BigDecimal afterQty = beforeQty.subtract(baseQty);
        stock.setQuantity(afterQty);
        stock.setUpdateBy(operator);
        wmsStockMapper.updateWmsStock(stock);

        BigDecimal itemAmount = defaultBigDecimal(item.getAmount());
        BigDecimal baseUnitCost = baseQty.compareTo(BigDecimal.ZERO) > 0
            ? itemAmount.divide(baseQty, 4, RoundingMode.HALF_UP) : defaultBigDecimal(item.getPrice());
        WmsStockLog log = new WmsStockLog();
        log.setWarehouseId(warehouseId);
        log.setProductId(item.getProductId());
        log.setLocationId(locationId);
        log.setBatchNo(batchNo);
        log.setBillType(billType);
        log.setBillId(billId);
        log.setInOut("2");
        log.setQuantity(baseQty);
        log.setPrice(baseUnitCost);
        log.setAmount(itemAmount);
        log.setBeforeQty(beforeQty);
        log.setAfterQty(afterQty);
        log.setCreateBy(operator);
        wmsStockLogMapper.insertWmsStockLog(log);
        finCostCalculateService.calculateExactInboundReversal(warehouseId, item.getProductId(), billId, billType, baseQty, baseUnitCost);
    }

    private void assertNoAuditedPurchaseReturn(WmsInbound inbound)
    {
        if (wmsPurchaseReturnMapper == null)
        {
            return;
        }
        WmsPurchaseReturn q = new WmsPurchaseReturn();
        q.setSourceBillType("inbound");
        q.setSourceBillId(inbound.getInboundId());
        q.setStatus("1");
        List<WmsPurchaseReturn> list = wmsPurchaseReturnMapper.selectWmsPurchaseReturnList(q);
        if (list != null)
        {
            for (WmsPurchaseReturn pr : list)
            {
                if (!"1".equals(pr.getIsReversal()) && !"1".equals(pr.getReversedFlag()))
                {
                    throw new ServiceException("该入库单已存在已审核的采购退货单(" + pr.getReturnNo() + ")，禁止操作");
                }
            }
        }
    }

    private void autoFillExpiryDate(WmsInboundItem item, MdProduct product)
    {
        if (item.getProductionDate() != null && item.getExpiryDate() == null
            && product != null && product.getShelfLifeDays() != null && product.getShelfLifeDays() > 0)
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(item.getProductionDate());
            cal.add(Calendar.DAY_OF_MONTH, product.getShelfLifeDays());
            item.setExpiryDate(cal.getTime());
        }
    }

    private BigDecimal resolveBaseQuantity(WmsInboundItem item)
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

    private void syncSourcePurchaseOrderProgress(WmsInbound inbound, String operator)
    {
        if (inbound == null || !"pur_order".equals(inbound.getSourceBillType()) || inbound.getSourceBillId() == null)
        {
            return;
        }
        if (purOrderService != null)
        {
            purOrderService.syncOrderProgressFromWms(inbound.getSourceBillId(), operator);
            return;
        }
        PurOrder purOrder = purOrderMapper.selectPurOrderById(inbound.getSourceBillId());
        if (purOrder != null && "1".equals(inbound.getStatus()))
        {
            purOrder.setStatus("2");
            purOrder.setBizStatus("completed");
            purOrder.setUpdateBy(operator);
            purOrderMapper.updatePurOrder(purOrder);
        }
    }

    private BigDecimal defaultBigDecimal(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO : value;
    }

    private Long defaultLocationId(Long locationId)
    {
        return locationId == null ? 0L : locationId;
    }

    private String defaultBatchNo(String batchNo)
    {
        return batchNo == null ? "" : batchNo.trim();
    }

    private void validateActiveMasterRefs(WmsInbound wmsInbound)
    {
        if (wmsInbound.getSupplierId() != null && mdSupplierMapper != null)
        {
            MdSupplier supplier = mdSupplierMapper.selectMdSupplierById(wmsInbound.getSupplierId());
            if (supplier != null && "1".equals(supplier.getStatus()))
            {
                throw new ServiceException("供应商[" + supplier.getSupplierName() + "]已停用，禁止入库");
            }
        }
        if (wmsInbound.getWarehouseId() != null && mdWarehouseMapper != null)
        {
            MdWarehouse warehouse = mdWarehouseMapper.selectMdWarehouseById(wmsInbound.getWarehouseId());
            if (warehouse != null && "1".equals(warehouse.getStatus()))
            {
                throw new ServiceException("仓库[" + warehouse.getWarehouseName() + "]已停用，禁止入库");
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
            throw new ServiceException("库位[" + location.getLocationName() + "]不属于当前入库仓库，禁止跨仓混用库位");
        }
    }

    private void validateInboundItemsWithinPurchaseOrder(WmsInbound wmsInbound)
    {
        if (!"pur_order".equals(wmsInbound.getSourceBillType()) || wmsInbound.getSourceBillId() == null || purOrderItemMapper == null)
        {
            return;
        }
        List<PurOrderItem> orderItems = purOrderItemMapper.selectPurOrderItemListByOrderId(wmsInbound.getSourceBillId());
        if (orderItems == null || orderItems.isEmpty())
        {
            return;
        }
        Map<Long, BigDecimal> orderLimitByItem = new HashMap<Long, BigDecimal>();
        Map<Long, BigDecimal> orderLimitByProduct = new HashMap<Long, BigDecimal>();
        for (PurOrderItem oi : orderItems)
        {
            BigDecimal base = oi.getBaseQuantity() != null && oi.getBaseQuantity().compareTo(BigDecimal.ZERO) > 0
                ? oi.getBaseQuantity() : defaultBigDecimal(oi.getQuantity());
            orderLimitByItem.put(oi.getPurchaseOrderItemId(), base);
            orderLimitByProduct.merge(oi.getProductId(), base, BigDecimal::add);
        }
        WmsInbound sibQ = new WmsInbound();
        sibQ.setSourceBillType("pur_order");
        sibQ.setSourceBillId(wmsInbound.getSourceBillId());
        List<WmsInbound> siblings = wmsInboundMapper.selectWmsInboundList(sibQ);
        Map<Long, BigDecimal> usedByItem = new HashMap<Long, BigDecimal>();
        Map<Long, BigDecimal> usedByProduct = new HashMap<Long, BigDecimal>();
        if (siblings != null)
        {
            for (WmsInbound sib : siblings)
            {
                if ("1".equals(sib.getIsReversal()) || "1".equals(sib.getReversedFlag()))
                {
                    continue;
                }
                WmsInboundItem iq = new WmsInboundItem();
                iq.setInboundId(sib.getInboundId());
                List<WmsInboundItem> sibItems = wmsInboundItemMapper.selectWmsInboundItemList(iq);
                if (sibItems == null)
                {
                    continue;
                }
                for (WmsInboundItem si : sibItems)
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
                throw new ServiceException("关联采购订单明细累计入库量(" + e.getValue() + ")超出订单数量(" + limit + ")，禁止超量入库");
            }
        }
        for (Map.Entry<Long, BigDecimal> e : usedByProduct.entrySet())
        {
            BigDecimal limit = orderLimitByProduct.get(e.getKey());
            if (limit != null && e.getValue().compareTo(limit) > 0)
            {
                throw new ServiceException("商品 #" + e.getKey() + " 累计入库量(" + e.getValue() + ")超出采购订单数量(" + limit + ")");
            }
        }
    }

    private void assertNoDownstreamFinanceOnInbound(WmsInbound inbound, FinPayable linkedPayable)
    {
        if (linkedPayable != null)
        {
            if (finPaymentMapper != null)
            {
                FinPayment payQ = new FinPayment();
                payQ.setPayableId(linkedPayable.getPayableId());
                List<FinPayment> pays = finPaymentMapper.selectFinPaymentList(payQ);
                if (pays != null && !pays.isEmpty())
                {
                    throw new ServiceException("关联应付单存在付款单(" + pays.get(0).getPaymentNo() + ")，请先删除草稿或通过红冲处理");
                }
            }
            if (finWriteOffMapper != null)
            {
                FinWriteOff woQ = new FinWriteOff();
                woQ.setPayableId(linkedPayable.getPayableId());
                List<FinWriteOff> wos = finWriteOffMapper.selectFinWriteOffList(woQ);
                if (wos != null && !wos.isEmpty())
                {
                    throw new ServiceException("关联应付单存在核销单(" + wos.get(0).getWriteOffNo() + ")，禁止反审核");
                }
            }
            if (finInvoiceMapper != null)
            {
                FinInvoice invQ = new FinInvoice();
                invQ.setSourceBillType("fin_payable");
                invQ.setSourceBillId(linkedPayable.getPayableId());
                List<FinInvoice> invs = finInvoiceMapper.selectFinInvoiceList(invQ);
                if (invs != null && !invs.isEmpty())
                {
                    throw new ServiceException("关联应付单已登记进项发票(" + invs.get(0).getInvoiceNo() + ")，禁止反审核");
                }
            }
        }
        if (finInvoiceMapper != null)
        {
            FinInvoice invQ = new FinInvoice();
            invQ.setSourceBillType("wms_inbound");
            invQ.setSourceBillId(inbound.getInboundId());
            List<FinInvoice> invs = finInvoiceMapper.selectFinInvoiceList(invQ);
            if (invs != null && !invs.isEmpty())
            {
                throw new ServiceException("该入库单已登记进项发票(" + invs.get(0).getInvoiceNo() + ")，禁止反审核");
            }
        }
    }

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
        existingFinPayable.setSourceBillType("inbound");
        existingFinPayable.setSourceBillId(inbound.getInboundId());
        existingFinPayable.setSourceBillNo(StringUtils.isNotEmpty(inbound.getInboundNo()) ? inbound.getInboundNo() : inbound.getSourceBillNo());
        existingFinPayable.setBusinessDate(inbound.getBusinessDate());
        existingFinPayable.setAmount(inbound.getTotalAmount());
        existingFinPayable.setDueDate(resolvePayableDueDate(inbound.getSupplierId(), inbound.getBusinessDate()));
        existingFinPayable.setStatus(calculatePayableStatus(existingFinPayable.getPaidAmount(), inbound.getTotalAmount()));
        existingFinPayable.setBizStatus("confirmed");
        existingFinPayable.setUpdateBy(operator);
        existingFinPayable.setRemark(buildInboundPayableRemark(inbound));
        finPayableMapper.updateFinPayable(existingFinPayable);
    }

    private FinPayable selectLinkedPayable(WmsInbound inbound)
    {
        FinPayable finPayableQuery = new FinPayable();
        finPayableQuery.setBillType("inbound");
        List<FinPayable> finPayableList = finPayableMapper.selectFinPayableList(finPayableQuery);
        if (finPayableList != null)
        {
            for (FinPayable finPayable : finPayableList)
            {
                if ("inbound".equals(finPayable.getBillType()) && inbound.getInboundId().equals(finPayable.getBillId()))
                {
                    return finPayable;
                }
            }
        }
        return null;
    }

    private FinPayable buildPayableCandidate(WmsInbound inbound, String operator)
    {
        FinPayable finPayable = new FinPayable();
        finPayable.setSupplierId(inbound.getSupplierId());
        finPayable.setBillType("inbound");
        finPayable.setBillId(inbound.getInboundId());
        // 以入库单自身作为唯一来源键，避免同一采购订单分批入库或红冲重推触发 uk_fin_payable_source 冲突
        finPayable.setSourceBillType("inbound");
        finPayable.setSourceBillId(inbound.getInboundId());
        finPayable.setSourceBillNo(StringUtils.isNotEmpty(inbound.getInboundNo()) ? inbound.getInboundNo() : inbound.getSourceBillNo());
        finPayable.setBusinessDate(inbound.getBusinessDate());
        finPayable.setAmount(inbound.getTotalAmount());
        finPayable.setDueDate(resolvePayableDueDate(inbound.getSupplierId(), inbound.getBusinessDate()));
        finPayable.setCreateBy(operator);
        finPayable.setRemark(buildInboundPayableRemark(inbound));
        return finPayable;
    }

    private String buildInboundPayableRemark(WmsInbound inbound)
    {
        return "采购入库完成自动生成，关联入库单号：" + inbound.getInboundNo();
    }

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

    private Date resolvePayableDueDate(Long supplierId, Date businessDate)
    {
        MdSupplier mdSupplier = mdSupplierMapper.selectMdSupplierById(supplierId);
        int payableDays = mdSupplier == null || mdSupplier.getPayableDays() == null ? 30 : mdSupplier.getPayableDays();
        Calendar dueDateCalendar = Calendar.getInstance();
        dueDateCalendar.setTime(businessDate == null ? new Date() : businessDate);
        dueDateCalendar.add(Calendar.DAY_OF_MONTH, payableDays);
        return dueDateCalendar.getTime();
    }

    private void fillStockWarningQty(WmsStock stock, Long productId)
    {
        MdProduct mdProduct = mdProductMapper.selectMdProductById(productId);
        stock.setWarningMinQty(mdProduct == null ? BigDecimal.ZERO : defaultBigDecimal(mdProduct.getWarningMinQty()));
        stock.setWarningMaxQty(mdProduct == null ? BigDecimal.ZERO : defaultBigDecimal(mdProduct.getWarningMaxQty()));
    }

    private WmsInbound getExistingInbound(Long inboundId)
    {
        WmsInbound inbound = wmsInboundMapper.selectWmsInboundById(inboundId);
        if (inbound == null)
        {
            throw new ServiceException("入库单不存在");
        }
        return inbound;
    }

    private void validateInboundEditable(WmsInbound inbound)
    {
        if (!"0".equals(inbound.getStatus()))
        {
            throw new ServiceException("入库单已审核，无法修改");
        }
    }

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

    private WmsInbound checkInboundDeletable(Long inboundId)
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
        return inbound;
    }

    private void recordInboundCost(Long warehouseId, Long productId, Long inboundId, String billType, String inOut, BigDecimal quantity, BigDecimal price)
    {
        FinCostCalculate req = new FinCostCalculate();
        req.setWarehouseId(warehouseId);
        req.setProductId(productId);
        req.setBillType(billType);
        req.setBillId(inboundId);
        req.setInOut(inOut);
        req.setQuantity(quantity);
        req.setPrice(price.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : price);
        finCostCalculateService.calculateCost(req);
    }
}
