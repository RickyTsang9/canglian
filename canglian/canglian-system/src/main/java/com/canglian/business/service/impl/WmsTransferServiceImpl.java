package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.MdLocation;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.MdWarehouse;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsStockLog;
import com.canglian.business.domain.WmsTransfer;
import com.canglian.business.domain.WmsTransferItem;
import com.canglian.business.mapper.MdLocationMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.MdWarehouseMapper;
import com.canglian.business.mapper.WmsStockLogMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.mapper.WmsTransferItemMapper;
import com.canglian.business.mapper.WmsTransferMapper;
import com.canglian.business.service.IFinCostCalculateService;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IMdProductUnitService;
import com.canglian.business.service.IWmsSerialNoService;
import com.canglian.business.service.IWmsTransferService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 调拨单 服务层实现
 *
 * @author canglian
 */
@Service
public class WmsTransferServiceImpl implements IWmsTransferService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private WmsTransferMapper wmsTransferMapper;

    @Autowired
    private WmsTransferItemMapper wmsTransferItemMapper;

    @Autowired
    private WmsStockMapper wmsStockMapper;

    @Autowired
    private WmsStockLogMapper wmsStockLogMapper;

    @Autowired
    private MdProductMapper mdProductMapper;

    @Autowired
    private IFinGeneralLedgerService finGeneralLedgerService;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Autowired(required = false)
    private MdWarehouseMapper mdWarehouseMapper;

    @Autowired(required = false)
    private MdLocationMapper mdLocationMapper;

    @Autowired(required = false)
    private IMdProductUnitService mdProductUnitService;

    @Autowired(required = false)
    private IFinCostCalculateService finCostCalculateService;

    @Autowired(required = false)
    private IWmsSerialNoService wmsSerialNoService;

    @Override
    public WmsTransfer selectWmsTransferById(Long transferId)
    {
        WmsTransfer transfer = wmsTransferMapper.selectWmsTransferById(transferId);
        if (transfer != null)
        {
            assertTransferReadAccess(transfer);
            WmsTransferItem query = new WmsTransferItem();
            query.setTransferId(transferId);
            transfer.setItemList(wmsTransferItemMapper.selectWmsTransferItemList(query));
        }
        return transfer;
    }

    @Override
    public List<WmsTransfer> selectWmsTransferList(WmsTransfer wmsTransfer)
    {
        if (bizScopeHelper != null)
        {
            bizScopeHelper.applyScopeFilter(wmsTransfer, "", true, false);
            Set<Long> allowed = bizScopeHelper.getRestrictedWarehouseIds();
            if (allowed != null)
            {
                if (allowed.isEmpty())
                {
                    wmsTransfer.getParams().put("warehouseScope", " AND 1 = 0 ");
                }
                else
                {
                    StringBuilder ids = new StringBuilder();
                    for (Long id : allowed)
                    {
                        if (ids.length() > 0) ids.append(",");
                        ids.append(id);
                    }
                    wmsTransfer.getParams().put("warehouseScope",
                        " AND (out_warehouse_id IN (" + ids + ") OR in_warehouse_id IN (" + ids + ")) ");
                }
            }
        }
        return wmsTransferMapper.selectWmsTransferList(wmsTransfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWmsTransfer(WmsTransfer wmsTransfer)
    {
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(wmsTransfer.getOutWarehouseId());
            bizScopeHelper.assertWarehouseAccess(wmsTransfer.getInWarehouseId());
            if (wmsTransfer.getDeptId() == null) wmsTransfer.setDeptId(bizScopeHelper.currentDeptId());
            if (wmsTransfer.getOwnerUserId() == null) wmsTransfer.setOwnerUserId(bizScopeHelper.currentUserId());
        }
        if (wmsTransfer.getBusinessDate() == null)
        {
            wmsTransfer.setBusinessDate(new Date());
        }
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(wmsTransfer.getBusinessDate());
        }
        validateActiveWarehouses(wmsTransfer.getOutWarehouseId(), wmsTransfer.getInWarehouseId());
        if (StringUtils.isEmpty(wmsTransfer.getTransferNo()))
        {
            wmsTransfer.setTransferNo(generateTransferNo());
        }
        wmsTransfer.setTotalQty(BigDecimal.ZERO);
        wmsTransfer.setTotalAmount(BigDecimal.ZERO);
        wmsTransfer.setStatus("0");
        wmsTransfer.setIsReversal("0");
        wmsTransfer.setReversedFlag("0");
        int rows = wmsTransferMapper.insertWmsTransfer(wmsTransfer);
        if (rows > 0 && wmsTransfer.getItemList() != null && !wmsTransfer.getItemList().isEmpty())
        {
            saveTransferItemsAndRecalculate(wmsTransfer);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWmsTransfer(WmsTransfer wmsTransfer)
    {
        WmsTransfer transfer = getExistingTransfer(wmsTransfer.getTransferId());
        assertTransferWriteAccess(transfer);
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(wmsTransfer.getOutWarehouseId());
            bizScopeHelper.assertWarehouseAccess(wmsTransfer.getInWarehouseId());
        }
        validateTransferEditable(transfer);
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(resolveBusinessDate(transfer));
            if (wmsTransfer.getBusinessDate() != null)
            {
                finGeneralLedgerService.assertPeriodOpen(wmsTransfer.getBusinessDate());
            }
        }
        if (wmsTransfer.getOutWarehouseId() == null) wmsTransfer.setOutWarehouseId(transfer.getOutWarehouseId());
        if (wmsTransfer.getInWarehouseId() == null) wmsTransfer.setInWarehouseId(transfer.getInWarehouseId());
        if (wmsTransfer.getBusinessDate() == null) wmsTransfer.setBusinessDate(resolveBusinessDate(transfer));
        validateActiveWarehouses(wmsTransfer.getOutWarehouseId(), wmsTransfer.getInWarehouseId());
        wmsTransfer.setTotalQty(transfer.getTotalQty());
        wmsTransfer.setTotalAmount(transfer.getTotalAmount());
        wmsTransfer.setStatus(transfer.getStatus());
        int rows = wmsTransferMapper.updateWmsTransfer(wmsTransfer);
        if (rows > 0 && wmsTransfer.getItemList() != null)
        {
            wmsTransferItemMapper.deleteWmsTransferItemByTransferId(wmsTransfer.getTransferId());
            saveTransferItemsAndRecalculate(wmsTransfer);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsTransferById(Long transferId)
    {
        WmsTransfer transfer = checkTransferDeletable(transferId);
        assertTransferWriteAccess(transfer);
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(resolveBusinessDate(transfer));
        }
        wmsTransferItemMapper.deleteWmsTransferItemByTransferId(transferId);
        return wmsTransferMapper.deleteWmsTransferById(transferId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsTransferByIds(Long[] transferIds)
    {
        for (Long transferId : transferIds)
        {
            WmsTransfer transfer = checkTransferDeletable(transferId);
            assertTransferWriteAccess(transfer);
            if (finGeneralLedgerService != null)
            {
                finGeneralLedgerService.assertPeriodOpen(resolveBusinessDate(transfer));
            }
        }
        for (Long transferId : transferIds)
        {
            wmsTransferItemMapper.deleteWmsTransferItemByTransferId(transferId);
        }
        return wmsTransferMapper.deleteWmsTransferByIds(transferIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int auditWmsTransfer(Long transferId, String operator)
    {
        WmsTransfer transfer = wmsTransferMapper.selectWmsTransferByIdForUpdate(transferId);
        if (transfer == null) throw new ServiceException("调拨单不存在");
        assertTransferWriteAccess(transfer);
        if (!"0".equals(transfer.getStatus())) throw new ServiceException("调拨单状态已变更，无法审核");
        finGeneralLedgerService.assertPeriodOpen(resolveBusinessDate(transfer));
        if (transfer.getOutWarehouseId() == null) throw new ServiceException("调出仓库不能为空");
        if (transfer.getInWarehouseId() == null) throw new ServiceException("调入仓库不能为空");
        validateActiveWarehouses(transfer.getOutWarehouseId(), transfer.getInWarehouseId());
        WmsTransferItem itemQuery = new WmsTransferItem();
        itemQuery.setTransferId(transferId);
        List<WmsTransferItem> itemList = wmsTransferItemMapper.selectWmsTransferItemList(itemQuery);
        if (itemList == null || itemList.isEmpty()) throw new ServiceException("调拨单明细不能为空");

        BigDecimal[] totals = moveStockItems(transfer.getOutWarehouseId(), transfer.getInWarehouseId(), itemList,
            "transfer", transferId, transfer.getTransferNo(), operator, false);
        transfer.setStatus("1");
        transfer.setUpdateBy(operator);
        transfer.setUpdateTime(new Date());
        transfer.setTotalQty(totals[0]);
        transfer.setTotalAmount(totals[1]);
        return wmsTransferMapper.updateWmsTransfer(transfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unauditWmsTransfer(Long transferId, String operator)
    {
        WmsTransfer transfer = wmsTransferMapper.selectWmsTransferByIdForUpdate(transferId);
        if (transfer == null) throw new ServiceException("调拨单不存在");
        assertTransferWriteAccess(transfer);
        if (!"1".equals(transfer.getStatus())) throw new ServiceException("仅已审核调拨单允许反审核");
        if ("1".equals(transfer.getIsReversal())) throw new ServiceException("红冲单据不允许反审核");
        if ("1".equals(transfer.getReversedFlag())) throw new ServiceException("该调拨单已被红冲，禁止反审核");
        finGeneralLedgerService.assertPeriodOpen(resolveBusinessDate(transfer));

        WmsTransferItem itemQuery = new WmsTransferItem();
        itemQuery.setTransferId(transferId);
        List<WmsTransferItem> itemList = wmsTransferItemMapper.selectWmsTransferItemList(itemQuery);
        if (itemList != null && !itemList.isEmpty())
        {
            moveStockItems(transfer.getInWarehouseId(), transfer.getOutWarehouseId(), itemList,
                "transfer_unaudit", transferId, transfer.getTransferNo(), operator, true);
        }
        transfer.setStatus("0");
        transfer.setUnauditBy(operator);
        transfer.setUnauditTime(new Date());
        transfer.setUpdateBy(operator);
        return wmsTransferMapper.updateWmsTransfer(transfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsTransfer reverseWmsTransfer(Long transferId, String operator)
    {
        WmsTransfer origin = wmsTransferMapper.selectWmsTransferByIdForUpdate(transferId);
        if (origin == null) throw new ServiceException("调拨单不存在");
        assertTransferWriteAccess(origin);
        if (!"1".equals(origin.getStatus())) throw new ServiceException("仅已审核调拨单允许红冲");
        if ("1".equals(origin.getIsReversal())) throw new ServiceException("红冲单据不允许再次红冲");
        if ("1".equals(origin.getReversedFlag())) throw new ServiceException("该调拨单已被红冲，请勿重复红冲");
        finGeneralLedgerService.assertPeriodOpen(new Date());

        WmsTransferItem q = new WmsTransferItem();
        q.setTransferId(transferId);
        List<WmsTransferItem> originItems = wmsTransferItemMapper.selectWmsTransferItemList(q);
        if (originItems == null || originItems.isEmpty()) throw new ServiceException("原调拨单无明细，无法红冲");

        WmsTransfer reversal = new WmsTransfer();
        reversal.setTransferNo(generateTransferNo());
        reversal.setOutWarehouseId(origin.getInWarehouseId());
        reversal.setInWarehouseId(origin.getOutWarehouseId());
        reversal.setBusinessDate(new Date());
        reversal.setDeptId(origin.getDeptId());
        reversal.setOwnerUserId(origin.getOwnerUserId());
        reversal.setTotalQty(defaultBigDecimal(origin.getTotalQty()).negate());
        reversal.setTotalAmount(defaultBigDecimal(origin.getTotalAmount()).negate());
        reversal.setStatus("1");
        reversal.setIsReversal("1");
        reversal.setOriginBillId(origin.getTransferId());
        reversal.setReversedFlag("0");
        reversal.setCreateBy(operator);
        reversal.setRemark("红冲原调拨单：" + origin.getTransferNo());
        wmsTransferMapper.insertWmsTransfer(reversal);

        moveStockItems(origin.getInWarehouseId(), origin.getOutWarehouseId(), originItems,
            "transfer_reverse", reversal.getTransferId(), reversal.getTransferNo(), operator, true);
        for (WmsTransferItem oi : originItems)
        {
            WmsTransferItem ri = new WmsTransferItem();
            ri.setTransferId(reversal.getTransferId());
            ri.setProductId(oi.getProductId());
            ri.setOutLocationId(oi.getInLocationId());
            ri.setInLocationId(oi.getOutLocationId());
            ri.setBatchNo(defaultBatchNo(oi.getBatchNo()));
            ri.setUnitName(oi.getUnitName());
            ri.setConvertRatio(oi.getConvertRatio());
            ri.setBaseQuantity(resolveBaseQuantity(oi).negate());
            ri.setQuantity(defaultBigDecimal(oi.getQuantity()).negate());
            ri.setPrice(oi.getPrice());
            ri.setAmount(defaultBigDecimal(oi.getAmount()).negate());
            ri.setSnCodes(oi.getSnCodes());
            wmsTransferItemMapper.insertWmsTransferItem(ri);
        }

        origin.setReversedFlag("1");
        origin.setUpdateBy(operator);
        wmsTransferMapper.updateWmsTransfer(origin);
        return selectWmsTransferById(reversal.getTransferId());
    }

    private BigDecimal[] moveStockItems(Long fromWhId, Long toWhId, List<WmsTransferItem> items,
                                        String billType, Long billId, String billNo, String operator, boolean swapLocations)
    {
        boolean sameWarehouse = fromWhId.equals(toWhId);
        Date todayStart = startOfToday();
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmt = BigDecimal.ZERO;
        for (WmsTransferItem item : items)
        {
            BigDecimal rawQty = defaultBigDecimal(item.getQuantity());
            BigDecimal qty = swapLocations ? rawQty.abs() : rawQty;
            if (qty.compareTo(BigDecimal.ZERO) <= 0) throw new ServiceException("调拨数量必须大于0");
            if (item.getProductId() == null) throw new ServiceException("调拨明细商品不能为空");
            MdProduct product = mdProductMapper != null ? mdProductMapper.selectMdProductById(item.getProductId()) : null;
            if (!swapLocations && product != null && "1".equals(product.getStatus()))
            {
                throw new ServiceException("商品[" + product.getProductName() + "]已停用，禁止调拨");
            }
            Long outLocId = defaultLocationId(swapLocations ? item.getInLocationId() : item.getOutLocationId());
            Long inLocId = defaultLocationId(swapLocations ? item.getOutLocationId() : item.getInLocationId());
            validateLocationBelongsToWarehouse(outLocId, fromWhId, "调出");
            validateLocationBelongsToWarehouse(inLocId, toWhId, "调入");
            if (sameWarehouse && outLocId.equals(inLocId)) throw new ServiceException("同仓调拨时调出库位与调入库位不能相同");
            String batchNo = defaultBatchNo(item.getBatchNo());
            BigDecimal baseQty = resolveBaseQuantity(item).abs();
            if (baseQty.compareTo(BigDecimal.ZERO) <= 0) baseQty = qty;

            WmsStock outQ = new WmsStock();
            outQ.setWarehouseId(fromWhId);
            outQ.setProductId(item.getProductId());
            outQ.setLocationId(outLocId);
            outQ.setBatchNo(batchNo);
            WmsStock inQ = new WmsStock();
            inQ.setWarehouseId(toWhId);
            inQ.setProductId(item.getProductId());
            inQ.setLocationId(inLocId);
            inQ.setBatchNo(batchNo);

            boolean lockOutFirst = fromWhId < toWhId || (sameWarehouse && outLocId < inLocId);
            WmsStock outStock = lockOutFirst ? wmsStockMapper.selectWmsStockByKeyForUpdate(outQ) : null;
            WmsStock inStock = wmsStockMapper.selectWmsStockByKeyForUpdate(inQ);
            if (!lockOutFirst) outStock = wmsStockMapper.selectWmsStockByKeyForUpdate(outQ);

            if (outStock == null) throw new ServiceException("调出仓可用库存不足，无法执行调拨/回退");
            if (!swapLocations && outStock.getExpiryDate() != null && outStock.getExpiryDate().before(todayStart))
            {
                String expStr = new SimpleDateFormat("yyyy-MM-dd").format(outStock.getExpiryDate());
                throw new ServiceException("商品 #" + item.getProductId() + " 批次[" + batchNo + "] 已过有效期(" + expStr + ")，禁止调拨");
            }
            BigDecimal outBefore = defaultBigDecimal(outStock.getQuantity());
            if (calculateAvailableQuantity(outStock).compareTo(baseQty) < 0)
            {
                throw new ServiceException("调出仓可用库存不足，无法执行调拨/回退");
            }
            BigDecimal outAfter = outBefore.subtract(baseQty);
            outStock.setQuantity(outAfter);
            outStock.setUpdateBy(operator);
            wmsStockMapper.updateWmsStock(outStock);

            // 序列号强管控：启用序列号的商品必须核验足额在库 SN 并变更所在仓库/库位
            if (wmsSerialNoService != null)
            {
                wmsSerialNoService.handleTransferSerials(product, fromWhId, toWhId, inLocId, billId, billNo,
                    item.getSnCodes(), baseQty, operator);
            }

            // 跨仓调拨同步结转真实库存成本（调出仓出库成本 -> 调入仓入库成本）
            BigDecimal unitCost;
            if (!sameWarehouse && finCostCalculateService != null)
            {
                unitCost = finCostCalculateService.calculateTransferCost(fromWhId, toWhId, item.getProductId(),
                    billId, billType + "_out", billType + "_in", baseQty);
            }
            else if (finCostCalculateService != null)
            {
                unitCost = finCostCalculateService.resolveReturnUnitCost(fromWhId, item.getProductId(), null, null, defaultBigDecimal(item.getPrice()));
            }
            else
            {
                unitCost = defaultBigDecimal(item.getPrice());
            }
            BigDecimal costAmount = unitCost.multiply(baseQty).setScale(2, RoundingMode.HALF_UP);

            WmsStockLog outLog = new WmsStockLog();
            outLog.setWarehouseId(fromWhId);
            outLog.setProductId(item.getProductId());
            outLog.setLocationId(outLocId);
            outLog.setBatchNo(batchNo);
            outLog.setBillType(billType + "_out");
            outLog.setBillId(billId);
            outLog.setInOut("2");
            outLog.setQuantity(baseQty);
            outLog.setPrice(unitCost);
            outLog.setAmount(costAmount);
            outLog.setBeforeQty(outBefore);
            outLog.setAfterQty(outAfter);
            outLog.setCreateBy(operator);
            wmsStockLogMapper.insertWmsStockLog(outLog);

            BigDecimal inBefore;
            if (inStock == null)
            {
                inStock = new WmsStock();
                inStock.setWarehouseId(toWhId);
                inStock.setProductId(item.getProductId());
                inStock.setLocationId(inLocId);
                inStock.setBatchNo(batchNo);
                inStock.setProductionDate(outStock.getProductionDate());
                inStock.setExpiryDate(outStock.getExpiryDate());
                inStock.setQuantity(baseQty);
                inStock.setLockedQuantity(BigDecimal.ZERO);
                inStock.setFrozenQuantity(BigDecimal.ZERO);
                fillStockWarningQty(inStock, item.getProductId());
                inStock.setVersion(0L);
                inStock.setCreateBy(operator);
                wmsStockMapper.insertWmsStock(inStock);
                inBefore = BigDecimal.ZERO;
            }
            else
            {
                inBefore = defaultBigDecimal(inStock.getQuantity());
                inStock.setQuantity(inBefore.add(baseQty));
                if (inStock.getProductionDate() == null && outStock.getProductionDate() != null)
                {
                    inStock.setProductionDate(outStock.getProductionDate());
                }
                if (inStock.getExpiryDate() == null && outStock.getExpiryDate() != null)
                {
                    inStock.setExpiryDate(outStock.getExpiryDate());
                }
                inStock.setUpdateBy(operator);
                wmsStockMapper.updateWmsStock(inStock);
            }
            BigDecimal inAfter = inBefore.add(baseQty);
            WmsStockLog inLog = new WmsStockLog();
            inLog.setWarehouseId(toWhId);
            inLog.setProductId(item.getProductId());
            inLog.setLocationId(inLocId);
            inLog.setBatchNo(batchNo);
            inLog.setBillType(billType + "_in");
            inLog.setBillId(billId);
            inLog.setInOut("1");
            inLog.setQuantity(baseQty);
            inLog.setPrice(unitCost);
            inLog.setAmount(costAmount);
            inLog.setBeforeQty(inBefore);
            inLog.setAfterQty(inAfter);
            inLog.setCreateBy(operator);
            wmsStockLogMapper.insertWmsStockLog(inLog);

            BigDecimal lineAmt = item.getAmount() != null && item.getAmount().abs().compareTo(BigDecimal.ZERO) > 0
                ? item.getAmount().abs() : costAmount;
            totalQty = totalQty.add(qty);
            totalAmt = totalAmt.add(lineAmt);
        }
        return new BigDecimal[] { totalQty, totalAmt };
    }

    private void assertTransferReadAccess(WmsTransfer transfer)
    {
        if (bizScopeHelper == null) return;
        bizScopeHelper.assertRecordAccess(transfer.getDeptId(), transfer.getOwnerUserId(), null);
        Set<Long> allowed = bizScopeHelper.getRestrictedWarehouseIds();
        if (allowed != null)
        {
            boolean canOut = transfer.getOutWarehouseId() != null && allowed.contains(transfer.getOutWarehouseId());
            boolean canIn = transfer.getInWarehouseId() != null && allowed.contains(transfer.getInWarehouseId());
            if (!canOut && !canIn)
            {
                throw new ServiceException("当前角色无权查看该调拨单涉及的仓库");
            }
        }
    }

    private void assertTransferWriteAccess(WmsTransfer transfer)
    {
        if (bizScopeHelper == null) return;
        bizScopeHelper.assertRecordAccess(transfer.getDeptId(), transfer.getOwnerUserId(), transfer.getOutWarehouseId());
        bizScopeHelper.assertWarehouseAccess(transfer.getInWarehouseId());
    }

    private void validateActiveWarehouses(Long outWarehouseId, Long inWarehouseId)
    {
        if (mdWarehouseMapper == null) return;
        if (outWarehouseId != null)
        {
            MdWarehouse outWh = mdWarehouseMapper.selectMdWarehouseById(outWarehouseId);
            if (outWh != null && "1".equals(outWh.getStatus()))
            {
                throw new ServiceException("调出仓库[" + outWh.getWarehouseName() + "]已停用，禁止调拨");
            }
        }
        if (inWarehouseId != null)
        {
            MdWarehouse inWh = mdWarehouseMapper.selectMdWarehouseById(inWarehouseId);
            if (inWh != null && "1".equals(inWh.getStatus()))
            {
                throw new ServiceException("调入仓库[" + inWh.getWarehouseName() + "]已停用，禁止调拨");
            }
        }
    }

    private void validateLocationBelongsToWarehouse(Long locationId, Long warehouseId, String side)
    {
        if (locationId == null || locationId <= 0L || warehouseId == null || mdLocationMapper == null) return;
        MdLocation loc = mdLocationMapper.selectMdLocationByLocationId(locationId);
        if (loc == null) throw new ServiceException(side + "库位(#" + locationId + ")不存在");
        if (!warehouseId.equals(loc.getWarehouseId()))
        {
            throw new ServiceException(side + "库位[" + loc.getLocationName() + "]不属于对应仓库，禁止跨仓混用");
        }
    }

    private Date resolveBusinessDate(WmsTransfer transfer)
    {
        return transfer.getBusinessDate() != null ? transfer.getBusinessDate()
            : (transfer.getCreateTime() != null ? transfer.getCreateTime() : new Date());
    }

    private BigDecimal resolveBaseQuantity(WmsTransferItem item)
    {
        if (item.getBaseQuantity() != null && item.getBaseQuantity().compareTo(BigDecimal.ZERO) != 0)
        {
            return item.getBaseQuantity().abs();
        }
        BigDecimal qty = defaultBigDecimal(item.getQuantity()).abs();
        BigDecimal ratio = item.getConvertRatio();
        if (ratio == null || ratio.compareTo(BigDecimal.ZERO) <= 0)
        {
            ratio = mdProductUnitService != null
                ? mdProductUnitService.resolveConvertRatio(item.getProductId(), item.getUnitName())
                : BigDecimal.ONE;
        }
        return qty.multiply(ratio).setScale(4, RoundingMode.HALF_UP);
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

    private BigDecimal defaultBigDecimal(BigDecimal value) { return value == null ? BigDecimal.ZERO : value; }

    private BigDecimal calculateAvailableQuantity(WmsStock stock)
    {
        return defaultBigDecimal(stock.getQuantity())
            .subtract(defaultBigDecimal(stock.getLockedQuantity()))
            .subtract(defaultBigDecimal(stock.getFrozenQuantity()));
    }

    private Long defaultLocationId(Long locationId) { return locationId == null ? 0L : locationId; }
    private String defaultBatchNo(String batchNo) { return batchNo == null ? "" : batchNo.trim(); }

    private void fillStockWarningQty(WmsStock stock, Long productId)
    {
        MdProduct mdProduct = mdProductMapper.selectMdProductById(productId);
        stock.setWarningMinQty(mdProduct == null ? BigDecimal.ZERO : defaultBigDecimal(mdProduct.getWarningMinQty()));
        stock.setWarningMaxQty(mdProduct == null ? BigDecimal.ZERO : defaultBigDecimal(mdProduct.getWarningMaxQty()));
    }

    private WmsTransfer getExistingTransfer(Long transferId)
    {
        WmsTransfer transfer = wmsTransferMapper.selectWmsTransferById(transferId);
        if (transfer == null) throw new ServiceException("调拨单不存在");
        return transfer;
    }

    private void validateTransferEditable(WmsTransfer transfer)
    {
        if (!"0".equals(transfer.getStatus())) throw new ServiceException("调拨单已审核，无法修改");
    }

    private String generateTransferNo()
    {
        String noPrefix = "trf" + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String currentMaxNo = wmsTransferMapper.selectMaxTransferNoByPrefix(noPrefix);
        int nextSequence = 1;
        if (StringUtils.isNotEmpty(currentMaxNo) && currentMaxNo.length() > noPrefix.length())
        {
            String sequenceText = currentMaxNo.substring(noPrefix.length());
            if (StringUtils.isNumeric(sequenceText)) nextSequence = Integer.parseInt(sequenceText) + 1;
        }
        return noPrefix + String.format("%03d", nextSequence);
    }

    private void saveTransferItemsAndRecalculate(WmsTransfer wmsTransfer)
    {
        BigDecimal totalQty = BigDecimal.ZERO;
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (WmsTransferItem item : wmsTransfer.getItemList())
        {
            if (item.getProductId() == null) continue;
            BigDecimal qty = defaultBigDecimal(item.getQuantity());
            if (qty.compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("调拨明细数量必须大于0");
            }
            MdProduct product = mdProductMapper != null ? mdProductMapper.selectMdProductById(item.getProductId()) : null;
            if (product != null && "1".equals(product.getStatus()))
            {
                throw new ServiceException("商品[" + product.getProductName() + "]已停用，禁止调拨");
            }
            validateLocationBelongsToWarehouse(item.getOutLocationId(), wmsTransfer.getOutWarehouseId(), "调出");
            validateLocationBelongsToWarehouse(item.getInLocationId(), wmsTransfer.getInWarehouseId(), "调入");
            if (StringUtils.isEmpty(item.getUnitName()) && product != null)
            {
                item.setUnitName(product.getUnitName());
            }
            BigDecimal ratio = mdProductUnitService != null
                ? mdProductUnitService.resolveConvertRatio(item.getProductId(), item.getUnitName())
                : (item.getConvertRatio() != null && item.getConvertRatio().compareTo(BigDecimal.ZERO) > 0 ? item.getConvertRatio() : BigDecimal.ONE);
            item.setConvertRatio(ratio);
            item.setBaseQuantity(qty.multiply(ratio).setScale(4, RoundingMode.HALF_UP));
            item.setBatchNo(defaultBatchNo(item.getBatchNo()));
            item.setOutLocationId(defaultLocationId(item.getOutLocationId()));
            item.setInLocationId(defaultLocationId(item.getInLocationId()));
            item.setTransferId(wmsTransfer.getTransferId());
            BigDecimal price = defaultBigDecimal(item.getPrice());
            if (price.compareTo(BigDecimal.ZERO) <= 0 && finCostCalculateService != null)
            {
                price = finCostCalculateService.resolveReturnUnitCost(wmsTransfer.getOutWarehouseId(), item.getProductId(), null, null, BigDecimal.ZERO)
                    .multiply(ratio).setScale(2, RoundingMode.HALF_UP);
                item.setPrice(price);
            }
            BigDecimal amount = item.getAmount();
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0)
            {
                amount = qty.multiply(price).setScale(2, RoundingMode.HALF_UP);
                item.setAmount(amount);
            }
            wmsTransferItemMapper.insertWmsTransferItem(item);
            totalQty = totalQty.add(qty);
            totalAmount = totalAmount.add(amount);
        }
        wmsTransfer.setTotalQty(totalQty);
        wmsTransfer.setTotalAmount(totalAmount);
        wmsTransferMapper.updateWmsTransfer(wmsTransfer);
    }

    private WmsTransfer checkTransferDeletable(Long transferId)
    {
        WmsTransfer transfer = wmsTransferMapper.selectWmsTransferById(transferId);
        if (transfer == null) throw new ServiceException("调拨单不存在");
        if (!"0".equals(transfer.getStatus())) throw new ServiceException("调拨单状态已变更，无法删除");
        return transfer;
    }
}
