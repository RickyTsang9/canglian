package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.FinCostLog;
import com.canglian.business.domain.MdLocation;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.MdWarehouse;
import com.canglian.business.domain.WmsInventoryCheck;
import com.canglian.business.domain.WmsInventoryCheckItem;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsStockLog;
import com.canglian.business.mapper.MdLocationMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.MdWarehouseMapper;
import com.canglian.business.mapper.WmsInventoryCheckItemMapper;
import com.canglian.business.mapper.WmsInventoryCheckMapper;
import com.canglian.business.mapper.WmsStockLogMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IFinCostCalculateService;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.business.service.IMdProductUnitService;
import com.canglian.business.service.IWmsInventoryCheckService;
import com.canglian.business.service.IWmsSerialNoService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 盘点单 服务层实现
 *
 * @author canglian
 */
@Service
public class WmsInventoryCheckServiceImpl implements IWmsInventoryCheckService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private WmsInventoryCheckMapper wmsInventoryCheckMapper;

    @Autowired
    private WmsInventoryCheckItemMapper wmsInventoryCheckItemMapper;

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
    private IFinVoucherEventService finVoucherEventService;

    @Autowired(required = false)
    private IWmsSerialNoService wmsSerialNoService;

    @Override
    public WmsInventoryCheck selectWmsInventoryCheckById(Long checkId)
    {
        WmsInventoryCheck check = wmsInventoryCheckMapper.selectWmsInventoryCheckById(checkId);
        if (check != null)
        {
            if (bizScopeHelper != null)
            {
                bizScopeHelper.assertRecordAccess(check.getDeptId(), check.getOwnerUserId(), check.getWarehouseId());
            }
            WmsInventoryCheckItem query = new WmsInventoryCheckItem();
            query.setCheckId(checkId);
            check.setItemList(wmsInventoryCheckItemMapper.selectWmsInventoryCheckItemList(query));
        }
        return check;
    }

    @Override
    public List<WmsInventoryCheck> selectWmsInventoryCheckList(WmsInventoryCheck wmsInventoryCheck)
    {
        if (bizScopeHelper != null)
        {
            bizScopeHelper.applyScopeFilter(wmsInventoryCheck, "", true, true);
        }
        return wmsInventoryCheckMapper.selectWmsInventoryCheckList(wmsInventoryCheck);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertWmsInventoryCheck(WmsInventoryCheck wmsInventoryCheck)
    {
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(wmsInventoryCheck.getWarehouseId());
            if (wmsInventoryCheck.getDeptId() == null) wmsInventoryCheck.setDeptId(bizScopeHelper.currentDeptId());
            if (wmsInventoryCheck.getOwnerUserId() == null) wmsInventoryCheck.setOwnerUserId(bizScopeHelper.currentUserId());
        }
        if (wmsInventoryCheck.getBusinessDate() == null)
        {
            wmsInventoryCheck.setBusinessDate(new Date());
        }
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(wmsInventoryCheck.getBusinessDate());
        }
        validateActiveWarehouse(wmsInventoryCheck.getWarehouseId());
        if (StringUtils.isEmpty(wmsInventoryCheck.getCheckNo()))
        {
            wmsInventoryCheck.setCheckNo(generateCheckNo());
        }
        wmsInventoryCheck.setTotalDiffQty(BigDecimal.ZERO);
        wmsInventoryCheck.setTotalDiffAmount(BigDecimal.ZERO);
        wmsInventoryCheck.setStatus("0");
        wmsInventoryCheck.setIsReversal("0");
        wmsInventoryCheck.setReversedFlag("0");
        int rows = wmsInventoryCheckMapper.insertWmsInventoryCheck(wmsInventoryCheck);
        if (rows > 0 && wmsInventoryCheck.getItemList() != null && !wmsInventoryCheck.getItemList().isEmpty())
        {
            saveCheckItemsAndRecalculate(wmsInventoryCheck);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateWmsInventoryCheck(WmsInventoryCheck wmsInventoryCheck)
    {
        WmsInventoryCheck inventoryCheck = getExistingInventoryCheck(wmsInventoryCheck.getCheckId());
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(inventoryCheck.getDeptId(), inventoryCheck.getOwnerUserId(), inventoryCheck.getWarehouseId());
            bizScopeHelper.assertWarehouseAccess(wmsInventoryCheck.getWarehouseId());
        }
        validateInventoryCheckEditable(inventoryCheck);
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(resolveBusinessDate(inventoryCheck));
            if (wmsInventoryCheck.getBusinessDate() != null)
            {
                finGeneralLedgerService.assertPeriodOpen(wmsInventoryCheck.getBusinessDate());
            }
        }
        if (wmsInventoryCheck.getWarehouseId() == null) wmsInventoryCheck.setWarehouseId(inventoryCheck.getWarehouseId());
        if (wmsInventoryCheck.getBusinessDate() == null) wmsInventoryCheck.setBusinessDate(resolveBusinessDate(inventoryCheck));
        validateActiveWarehouse(wmsInventoryCheck.getWarehouseId());
        wmsInventoryCheck.setTotalDiffQty(inventoryCheck.getTotalDiffQty());
        wmsInventoryCheck.setTotalDiffAmount(inventoryCheck.getTotalDiffAmount());
        wmsInventoryCheck.setStatus(inventoryCheck.getStatus());
        int rows = wmsInventoryCheckMapper.updateWmsInventoryCheck(wmsInventoryCheck);
        if (rows > 0 && wmsInventoryCheck.getItemList() != null)
        {
            wmsInventoryCheckItemMapper.deleteWmsInventoryCheckItemByCheckId(wmsInventoryCheck.getCheckId());
            saveCheckItemsAndRecalculate(wmsInventoryCheck);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsInventoryCheckById(Long checkId)
    {
        WmsInventoryCheck check = checkInventoryCheckDeletable(checkId);
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(check.getDeptId(), check.getOwnerUserId(), check.getWarehouseId());
        }
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(resolveBusinessDate(check));
        }
        wmsInventoryCheckItemMapper.deleteWmsInventoryCheckItemByCheckId(checkId);
        return wmsInventoryCheckMapper.deleteWmsInventoryCheckById(checkId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteWmsInventoryCheckByIds(Long[] checkIds)
    {
        for (Long checkId : checkIds)
        {
            WmsInventoryCheck check = checkInventoryCheckDeletable(checkId);
            if (bizScopeHelper != null)
            {
                bizScopeHelper.assertRecordAccess(check.getDeptId(), check.getOwnerUserId(), check.getWarehouseId());
            }
            if (finGeneralLedgerService != null)
            {
                finGeneralLedgerService.assertPeriodOpen(resolveBusinessDate(check));
            }
        }
        for (Long checkId : checkIds) wmsInventoryCheckItemMapper.deleteWmsInventoryCheckItemByCheckId(checkId);
        return wmsInventoryCheckMapper.deleteWmsInventoryCheckByIds(checkIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int auditWmsInventoryCheck(Long checkId, String operator)
    {
        WmsInventoryCheck inventoryCheck = wmsInventoryCheckMapper.selectWmsInventoryCheckByIdForUpdate(checkId);
        if (inventoryCheck == null) throw new ServiceException("盘点单不存在");
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(inventoryCheck.getDeptId(), inventoryCheck.getOwnerUserId(), inventoryCheck.getWarehouseId());
        }
        if (!"0".equals(inventoryCheck.getStatus())) throw new ServiceException("盘点单状态已变更，无法审核");
        Date bizDate = resolveBusinessDate(inventoryCheck);
        finGeneralLedgerService.assertPeriodOpen(bizDate);
        if (inventoryCheck.getWarehouseId() == null) throw new ServiceException("盘点仓库不能为空");
        validateActiveWarehouse(inventoryCheck.getWarehouseId());

        WmsInventoryCheckItem inventoryCheckItemQuery = new WmsInventoryCheckItem();
        inventoryCheckItemQuery.setCheckId(checkId);
        List<WmsInventoryCheckItem> inventoryCheckItemList = wmsInventoryCheckItemMapper.selectWmsInventoryCheckItemList(inventoryCheckItemQuery);
        if (inventoryCheckItemList == null || inventoryCheckItemList.isEmpty()) throw new ServiceException("盘点单明细不能为空");

        Set<String> seenKeys = new HashSet<String>();
        BigDecimal totalDifferenceQuantity = BigDecimal.ZERO;
        BigDecimal totalDifferenceAmount = BigDecimal.ZERO;
        BigDecimal totalGainAmount = BigDecimal.ZERO;
        BigDecimal totalLossAmount = BigDecimal.ZERO;
        for (WmsInventoryCheckItem inventoryCheckItem : inventoryCheckItemList)
        {
            if (inventoryCheckItem.getProductId() == null) throw new ServiceException("盘点明细商品不能为空");
            if (inventoryCheckItem.getActualQty() == null) throw new ServiceException("实盘数量不能为空");
            BigDecimal actualQuantity = defaultBigDecimal(inventoryCheckItem.getActualQty());
            if (actualQuantity.compareTo(BigDecimal.ZERO) < 0) throw new ServiceException("实盘数量不能小于0");
            validateLocationBelongsToWarehouse(inventoryCheckItem.getLocationId(), inventoryCheck.getWarehouseId());

            Long locationId = defaultLocationId(inventoryCheckItem.getLocationId());
            String batchNo = defaultBatchNo(inventoryCheckItem.getBatchNo());
            MdProduct product = mdProductMapper != null ? mdProductMapper.selectMdProductById(inventoryCheckItem.getProductId()) : null;
            if (product != null && "1".equals(product.getEnableBatch()) && StringUtils.isEmpty(batchNo))
            {
                throw new ServiceException("商品[" + product.getProductName() + "]已启用批次管理，盘点明细必须填写批次号");
            }
            String itemKey = inventoryCheckItem.getProductId() + "#" + locationId + "#" + batchNo;
            if (!seenKeys.add(itemKey)) throw new ServiceException("同一盘点单中存在重复的商品/库位/批次明细，请合并后再审核");

            BigDecimal ratio = resolveConvertRatio(inventoryCheckItem);
            BigDecimal actualBaseQty = actualQuantity.multiply(ratio).setScale(4, RoundingMode.HALF_UP);
            if (product != null && "1".equals(product.getEnableSerial()) && actualBaseQty.stripTrailingZeros().scale() > 0)
            {
                throw new ServiceException("序列号管控商品[" + product.getProductName() + "]的实盘基准数量必须为整数");
            }

            WmsStock stockQuery = new WmsStock();
            stockQuery.setWarehouseId(inventoryCheck.getWarehouseId());
            stockQuery.setProductId(inventoryCheckItem.getProductId());
            stockQuery.setLocationId(locationId);
            stockQuery.setBatchNo(batchNo);
            WmsStock stock = wmsStockMapper.selectWmsStockByKeyForUpdate(stockQuery);
            BigDecimal beforeQuantity;
            if (stock == null)
            {
                beforeQuantity = BigDecimal.ZERO;
                if (actualBaseQty.compareTo(BigDecimal.ZERO) > 0)
                {
                    stock = new WmsStock();
                    stock.setWarehouseId(inventoryCheck.getWarehouseId());
                    stock.setProductId(inventoryCheckItem.getProductId());
                    stock.setLocationId(locationId);
                    stock.setBatchNo(batchNo);
                    stock.setProductionDate(inventoryCheckItem.getProductionDate());
                    stock.setExpiryDate(inventoryCheckItem.getExpiryDate());
                    stock.setQuantity(actualBaseQty);
                    stock.setLockedQuantity(BigDecimal.ZERO);
                    stock.setFrozenQuantity(BigDecimal.ZERO);
                    fillStockWarningQty(stock, inventoryCheckItem.getProductId());
                    stock.setVersion(0L);
                    stock.setCreateBy(operator);
                    wmsStockMapper.insertWmsStock(stock);
                }
            }
            else
            {
                if (defaultBigDecimal(stock.getLockedQuantity()).compareTo(BigDecimal.ZERO) > 0
                    || defaultBigDecimal(stock.getFrozenQuantity()).compareTo(BigDecimal.ZERO) > 0)
                {
                    throw new ServiceException("盘点范围存在占用或冻结库存，无法审核");
                }
                beforeQuantity = defaultBigDecimal(stock.getQuantity());
                stock.setQuantity(actualBaseQty);
                if (stock.getProductionDate() == null && inventoryCheckItem.getProductionDate() != null)
                {
                    stock.setProductionDate(inventoryCheckItem.getProductionDate());
                }
                if (stock.getExpiryDate() == null && inventoryCheckItem.getExpiryDate() != null)
                {
                    stock.setExpiryDate(inventoryCheckItem.getExpiryDate());
                }
                stock.setUpdateBy(operator);
                wmsStockMapper.updateWmsStock(stock);
            }
            BigDecimal baseDiffQty = actualBaseQty.subtract(beforeQuantity);
            if (wmsSerialNoService != null && baseDiffQty.compareTo(BigDecimal.ZERO) != 0)
            {
                wmsSerialNoService.handleInventoryCheckSerials(product, inventoryCheck.getWarehouseId(), locationId, batchNo,
                    checkId, inventoryCheck.getCheckNo(), inventoryCheckItem.getExpiryDate(), inventoryCheckItem.getSnCodes(), baseDiffQty, operator);
            }
            BigDecimal displayStockQty = ratio.compareTo(BigDecimal.ONE) > 0
                ? beforeQuantity.divide(ratio, 4, RoundingMode.HALF_UP) : beforeQuantity;
            BigDecimal displayDiffQty = actualQuantity.subtract(displayStockQty);
            BigDecimal unitCostFallback = defaultBigDecimal(inventoryCheckItem.getPrice());
            if (unitCostFallback.compareTo(BigDecimal.ZERO) <= 0 && finCostCalculateService != null)
            {
                unitCostFallback = finCostCalculateService.resolveReturnUnitCost(
                    inventoryCheck.getWarehouseId(), inventoryCheckItem.getProductId(), null, null, BigDecimal.ZERO);
            }
            BigDecimal baseUnitCost = unitCostFallback;
            BigDecimal signedCostDiff = baseDiffQty.multiply(baseUnitCost).setScale(2, RoundingMode.HALF_UP);

            if (baseDiffQty.compareTo(BigDecimal.ZERO) != 0)
            {
                String inOut = baseDiffQty.compareTo(BigDecimal.ZERO) > 0 ? "1" : "2";
                FinCostLog costLog = finCostCalculateService != null
                    ? finCostCalculateService.calculateInventoryCheckCost(
                        inventoryCheck.getWarehouseId(), inventoryCheckItem.getProductId(),
                        checkId, "inventory_check", inOut, baseDiffQty.abs(), baseUnitCost)
                    : null;
                if (costLog != null && defaultBigDecimal(costLog.getPrice()).compareTo(BigDecimal.ZERO) > 0)
                {
                    baseUnitCost = costLog.getPrice();
                    BigDecimal absCost = defaultBigDecimal(costLog.getAmount()).setScale(2, RoundingMode.HALF_UP);
                    signedCostDiff = baseDiffQty.compareTo(BigDecimal.ZERO) > 0 ? absCost : absCost.negate();
                }
                WmsStockLog stockLog = new WmsStockLog();
                stockLog.setWarehouseId(inventoryCheck.getWarehouseId());
                stockLog.setProductId(inventoryCheckItem.getProductId());
                stockLog.setLocationId(locationId);
                stockLog.setBatchNo(batchNo);
                stockLog.setBillType("inventory_check");
                stockLog.setBillId(checkId);
                stockLog.setInOut(inOut);
                stockLog.setQuantity(baseDiffQty.abs());
                stockLog.setPrice(baseUnitCost);
                stockLog.setAmount(signedCostDiff.abs());
                stockLog.setBeforeQty(beforeQuantity);
                stockLog.setAfterQty(actualBaseQty);
                stockLog.setCreateBy(operator);
                wmsStockLogMapper.insertWmsStockLog(stockLog);

                if (signedCostDiff.compareTo(BigDecimal.ZERO) > 0)
                {
                    totalGainAmount = totalGainAmount.add(signedCostDiff);
                }
                else if (signedCostDiff.compareTo(BigDecimal.ZERO) < 0)
                {
                    totalLossAmount = totalLossAmount.add(signedCostDiff.abs());
                }
            }
            inventoryCheckItem.setConvertRatio(ratio);
            inventoryCheckItem.setStockQty(displayStockQty);
            inventoryCheckItem.setDiffQty(displayDiffQty);
            inventoryCheckItem.setBaseDiffQty(baseDiffQty);
            inventoryCheckItem.setPrice(baseUnitCost);
            inventoryCheckItem.setDiffAmount(signedCostDiff);
            wmsInventoryCheckItemMapper.updateWmsInventoryCheckItem(inventoryCheckItem);

            totalDifferenceQuantity = totalDifferenceQuantity.add(baseDiffQty);
            totalDifferenceAmount = totalDifferenceAmount.add(signedCostDiff);
        }
        inventoryCheck.setStatus("1");
        inventoryCheck.setTotalDiffQty(totalDifferenceQuantity);
        inventoryCheck.setTotalDiffAmount(totalDifferenceAmount);
        inventoryCheck.setUpdateBy(operator);
        inventoryCheck.setUpdateTime(new Date());
        int rows = wmsInventoryCheckMapper.updateWmsInventoryCheck(inventoryCheck);
        if (rows > 0 && finVoucherEventService != null)
        {
            if (totalGainAmount.compareTo(BigDecimal.ZERO) > 0)
            {
                finVoucherEventService.recordVoucherEvent("inventory_check", checkId, inventoryCheck.getCheckNo(),
                    "inventory_check_gain", bizDate, totalGainAmount, operator, "盘点盘盈待生成入账凭证");
            }
            if (totalLossAmount.compareTo(BigDecimal.ZERO) > 0)
            {
                finVoucherEventService.recordVoucherEvent("inventory_check", checkId, inventoryCheck.getCheckNo(),
                    "inventory_check_loss", bizDate, totalLossAmount, operator, "盘点盘亏待生成损失凭证");
            }
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unauditWmsInventoryCheck(Long checkId, String operator)
    {
        WmsInventoryCheck check = wmsInventoryCheckMapper.selectWmsInventoryCheckByIdForUpdate(checkId);
        if (check == null) throw new ServiceException("盘点单不存在");
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(check.getDeptId(), check.getOwnerUserId(), check.getWarehouseId());
        }
        if (!"1".equals(check.getStatus())) throw new ServiceException("仅已审核盘点单允许反审核");
        if ("1".equals(check.getIsReversal())) throw new ServiceException("红冲单据不允许反审核");
        if ("1".equals(check.getReversedFlag())) throw new ServiceException("该盘点单已被红冲，禁止反审核");
        finGeneralLedgerService.assertPeriodOpen(resolveBusinessDate(check));
        if (finVoucherEventService != null)
        {
            finVoucherEventService.cancelPendingVoucherEvents("inventory_check", checkId);
        }

        WmsInventoryCheckItem q = new WmsInventoryCheckItem();
        q.setCheckId(checkId);
        List<WmsInventoryCheckItem> items = wmsInventoryCheckItemMapper.selectWmsInventoryCheckItemList(q);
        if (items != null)
        {
            revertCheckDiffs(check.getWarehouseId(), items, "inventory_check_unaudit", checkId, check.getCheckNo(), true, operator);
        }
        check.setStatus("0");
        check.setUnauditBy(operator);
        check.setUnauditTime(new Date());
        check.setUpdateBy(operator);
        return wmsInventoryCheckMapper.updateWmsInventoryCheck(check);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsInventoryCheck reverseWmsInventoryCheck(Long checkId, String operator)
    {
        WmsInventoryCheck origin = wmsInventoryCheckMapper.selectWmsInventoryCheckByIdForUpdate(checkId);
        if (origin == null) throw new ServiceException("盘点单不存在");
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(origin.getDeptId(), origin.getOwnerUserId(), origin.getWarehouseId());
        }
        if (!"1".equals(origin.getStatus())) throw new ServiceException("仅已审核盘点单允许红冲");
        if ("1".equals(origin.getIsReversal())) throw new ServiceException("红冲单据不允许再次红冲");
        if ("1".equals(origin.getReversedFlag())) throw new ServiceException("该盘点单已被红冲，请勿重复红冲");
        finGeneralLedgerService.assertPeriodOpen(resolveBusinessDate(origin));
        finGeneralLedgerService.assertPeriodOpen(new Date());

        WmsInventoryCheckItem q = new WmsInventoryCheckItem();
        q.setCheckId(checkId);
        List<WmsInventoryCheckItem> originItems = wmsInventoryCheckItemMapper.selectWmsInventoryCheckItemList(q);
        if (originItems == null || originItems.isEmpty()) throw new ServiceException("原盘点单无明细，无法红冲");

        WmsInventoryCheck reversal = new WmsInventoryCheck();
        reversal.setCheckNo(generateCheckNo());
        reversal.setWarehouseId(origin.getWarehouseId());
        reversal.setBusinessDate(new Date());
        reversal.setDeptId(origin.getDeptId());
        reversal.setOwnerUserId(origin.getOwnerUserId());
        reversal.setStatus("1");
        reversal.setTotalDiffQty(defaultBigDecimal(origin.getTotalDiffQty()).negate());
        reversal.setTotalDiffAmount(defaultBigDecimal(origin.getTotalDiffAmount()).negate());
        reversal.setIsReversal("1");
        reversal.setOriginBillId(origin.getCheckId());
        reversal.setReversedFlag("0");
        reversal.setCreateBy(operator);
        reversal.setRemark("红冲原盘点单：" + origin.getCheckNo());
        wmsInventoryCheckMapper.insertWmsInventoryCheck(reversal);

        BigDecimal[] revTotals = revertCheckDiffs(origin.getWarehouseId(), originItems, "inventory_check_reverse", reversal.getCheckId(), reversal.getCheckNo(), false, operator);
        for (WmsInventoryCheckItem oi : originItems)
        {
            WmsInventoryCheckItem ri = new WmsInventoryCheckItem();
            ri.setCheckId(reversal.getCheckId());
            ri.setProductId(oi.getProductId());
            ri.setLocationId(defaultLocationId(oi.getLocationId()));
            ri.setBatchNo(defaultBatchNo(oi.getBatchNo()));
            ri.setUnitName(oi.getUnitName());
            ri.setConvertRatio(oi.getConvertRatio());
            ri.setProductionDate(oi.getProductionDate());
            ri.setExpiryDate(oi.getExpiryDate());
            ri.setSnCodes(oi.getSnCodes());
            ri.setStockQty(oi.getActualQty());
            ri.setActualQty(oi.getStockQty());
            ri.setDiffQty(defaultBigDecimal(oi.getDiffQty()).negate());
            ri.setBaseDiffQty(resolveBaseDiffQty(oi).negate());
            ri.setPrice(oi.getPrice());
            ri.setDiffAmount(defaultBigDecimal(oi.getDiffAmount()).negate());
            wmsInventoryCheckItemMapper.insertWmsInventoryCheckItem(ri);
        }

        origin.setReversedFlag("1");
        origin.setUpdateBy(operator);
        wmsInventoryCheckMapper.updateWmsInventoryCheck(origin);
        if (finVoucherEventService != null)
        {
            if (revTotals[0].compareTo(BigDecimal.ZERO) > 0)
            {
                finVoucherEventService.recordVoucherEvent("inventory_check", reversal.getCheckId(), reversal.getCheckNo(),
                    "inventory_check_gain", new Date(), revTotals[0], operator, "盘点单红冲冲回盘亏凭证");
            }
            if (revTotals[1].compareTo(BigDecimal.ZERO) > 0)
            {
                finVoucherEventService.recordVoucherEvent("inventory_check", reversal.getCheckId(), reversal.getCheckNo(),
                    "inventory_check_loss", new Date(), revTotals[1], operator, "盘点单红冲冲回盘盈凭证");
            }
        }
        return selectWmsInventoryCheckById(reversal.getCheckId());
    }

    private BigDecimal[] revertCheckDiffs(Long warehouseId, List<WmsInventoryCheckItem> items, String billType, Long billId,
                                          String billNo, boolean deleteSerials, String operator)
    {
        BigDecimal gainAmt = BigDecimal.ZERO;
        BigDecimal lossAmt = BigDecimal.ZERO;
        for (WmsInventoryCheckItem item : items)
        {
            BigDecimal diffQty = resolveBaseDiffQty(item);
            if (diffQty.compareTo(BigDecimal.ZERO) == 0) continue;
            Long locationId = defaultLocationId(item.getLocationId());
            String batchNo = defaultBatchNo(item.getBatchNo());
            MdProduct product = mdProductMapper != null ? mdProductMapper.selectMdProductById(item.getProductId()) : null;
            if (wmsSerialNoService != null)
            {
                wmsSerialNoService.rollbackInventoryCheckSerials(product, warehouseId, locationId, billId, billNo,
                    item.getSnCodes(), diffQty, deleteSerials, operator);
            }
            WmsStock q = new WmsStock();
            q.setWarehouseId(warehouseId);
            q.setProductId(item.getProductId());
            q.setLocationId(locationId);
            q.setBatchNo(batchNo);
            WmsStock stock = wmsStockMapper.selectWmsStockByKeyForUpdate(q);
            BigDecimal revertDelta = diffQty.negate();
            BigDecimal beforeQty;
            BigDecimal afterQty;
            if (stock == null)
            {
                if (revertDelta.compareTo(BigDecimal.ZERO) < 0)
                {
                    throw new ServiceException("盘盈库存已消耗，无法反审核/红冲盘点单");
                }
                stock = new WmsStock();
                stock.setWarehouseId(warehouseId);
                stock.setProductId(item.getProductId());
                stock.setLocationId(locationId);
                stock.setBatchNo(batchNo);
                stock.setQuantity(revertDelta);
                stock.setLockedQuantity(BigDecimal.ZERO);
                stock.setFrozenQuantity(BigDecimal.ZERO);
                fillStockWarningQty(stock, item.getProductId());
                stock.setVersion(0L);
                stock.setCreateBy(operator);
                wmsStockMapper.insertWmsStock(stock);
                beforeQty = BigDecimal.ZERO;
                afterQty = revertDelta;
            }
            else
            {
                beforeQty = defaultBigDecimal(stock.getQuantity());
                if (revertDelta.compareTo(BigDecimal.ZERO) < 0)
                {
                    BigDecimal avail = beforeQty.subtract(defaultBigDecimal(stock.getLockedQuantity())).subtract(defaultBigDecimal(stock.getFrozenQuantity()));
                    if (avail.compareTo(revertDelta.abs()) < 0)
                    {
                        throw new ServiceException("盘盈库存可用数量不足，无法回退盘点单");
                    }
                }
                afterQty = beforeQty.add(revertDelta);
                stock.setQuantity(afterQty);
                stock.setUpdateBy(operator);
                wmsStockMapper.updateWmsStock(stock);
            }
            String inOut = revertDelta.compareTo(BigDecimal.ZERO) > 0 ? "1" : "2";
            BigDecimal unitCost = defaultBigDecimal(item.getPrice());
            FinCostLog costLog = finCostCalculateService != null
                ? finCostCalculateService.calculateInventoryCheckCost(warehouseId, item.getProductId(), billId, billType, inOut, revertDelta.abs(), unitCost)
                : null;
            BigDecimal costAmt;
            if (costLog != null && defaultBigDecimal(costLog.getAmount()).compareTo(BigDecimal.ZERO) > 0)
            {
                unitCost = defaultBigDecimal(costLog.getPrice());
                costAmt = defaultBigDecimal(costLog.getAmount()).setScale(2, RoundingMode.HALF_UP);
            }
            else
            {
                costAmt = unitCost.multiply(revertDelta.abs()).setScale(2, RoundingMode.HALF_UP);
            }
            if ("1".equals(inOut))
            {
                gainAmt = gainAmt.add(costAmt);
            }
            else
            {
                lossAmt = lossAmt.add(costAmt);
            }
            WmsStockLog log = new WmsStockLog();
            log.setWarehouseId(warehouseId);
            log.setProductId(item.getProductId());
            log.setLocationId(locationId);
            log.setBatchNo(batchNo);
            log.setBillType(billType);
            log.setBillId(billId);
            log.setInOut(inOut);
            log.setQuantity(revertDelta.abs());
            log.setPrice(unitCost);
            log.setAmount(costAmt);
            log.setBeforeQty(beforeQty);
            log.setAfterQty(afterQty);
            log.setCreateBy(operator);
            wmsStockLogMapper.insertWmsStockLog(log);
        }
        return new BigDecimal[] { gainAmt, lossAmt };
    }

    private void validateActiveWarehouse(Long warehouseId)
    {
        if (warehouseId == null || mdWarehouseMapper == null) return;
        MdWarehouse wh = mdWarehouseMapper.selectMdWarehouseById(warehouseId);
        if (wh != null && "1".equals(wh.getStatus()))
        {
            throw new ServiceException("仓库[" + wh.getWarehouseName() + "]已停用，禁止盘点");
        }
    }

    private void validateLocationBelongsToWarehouse(Long locationId, Long warehouseId)
    {
        if (locationId == null || locationId <= 0L || warehouseId == null || mdLocationMapper == null) return;
        MdLocation loc = mdLocationMapper.selectMdLocationByLocationId(locationId);
        if (loc == null) throw new ServiceException("指定库位(#" + locationId + ")不存在");
        if ("1".equals(loc.getStatus())) throw new ServiceException("库位[" + loc.getLocationName() + "]已停用，禁止盘点");
        if (!warehouseId.equals(loc.getWarehouseId()))
        {
            throw new ServiceException("库位[" + loc.getLocationName() + "]不属于当前盘点仓库");
        }
    }

    private Date resolveBusinessDate(WmsInventoryCheck check)
    {
        return check.getBusinessDate() != null ? check.getBusinessDate()
            : (check.getCreateTime() != null ? check.getCreateTime() : new Date());
    }

    private BigDecimal resolveConvertRatio(WmsInventoryCheckItem item)
    {
        if (mdProductUnitService != null && StringUtils.isNotEmpty(item.getUnitName()))
        {
            return mdProductUnitService.resolveConvertRatio(item.getProductId(), item.getUnitName());
        }
        return item.getConvertRatio() != null && item.getConvertRatio().compareTo(BigDecimal.ZERO) > 0
            ? item.getConvertRatio() : BigDecimal.ONE;
    }

    private BigDecimal resolveBaseDiffQty(WmsInventoryCheckItem item)
    {
        if (item.getBaseDiffQty() != null && item.getBaseDiffQty().compareTo(BigDecimal.ZERO) != 0)
        {
            return item.getBaseDiffQty();
        }
        return defaultBigDecimal(item.getDiffQty()).multiply(resolveConvertRatio(item)).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal defaultBigDecimal(BigDecimal value) { return value == null ? BigDecimal.ZERO : value; }
    private Long defaultLocationId(Long locationId) { return locationId == null ? 0L : locationId; }
    private String defaultBatchNo(String batchNo) { return batchNo == null ? "" : batchNo.trim(); }

    private void fillStockWarningQty(WmsStock stock, Long productId)
    {
        MdProduct mdProduct = mdProductMapper.selectMdProductById(productId);
        stock.setWarningMinQty(mdProduct == null ? BigDecimal.ZERO : defaultBigDecimal(mdProduct.getWarningMinQty()));
        stock.setWarningMaxQty(mdProduct == null ? BigDecimal.ZERO : defaultBigDecimal(mdProduct.getWarningMaxQty()));
    }

    private WmsInventoryCheck getExistingInventoryCheck(Long checkId)
    {
        WmsInventoryCheck inventoryCheck = wmsInventoryCheckMapper.selectWmsInventoryCheckById(checkId);
        if (inventoryCheck == null) throw new ServiceException("盘点单不存在");
        return inventoryCheck;
    }

    private String generateCheckNo()
    {
        String checkNoPrefix = "chk" + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String currentMaxCheckNo = wmsInventoryCheckMapper.selectMaxCheckNoByPrefix(checkNoPrefix);
        int nextSequence = 1;
        if (StringUtils.isNotEmpty(currentMaxCheckNo) && currentMaxCheckNo.length() > checkNoPrefix.length())
        {
            String sequenceText = currentMaxCheckNo.substring(checkNoPrefix.length());
            if (StringUtils.isNumeric(sequenceText)) nextSequence = Integer.parseInt(sequenceText) + 1;
        }
        return checkNoPrefix + String.format("%03d", nextSequence);
    }

    private void validateInventoryCheckEditable(WmsInventoryCheck inventoryCheck)
    {
        if (!"0".equals(inventoryCheck.getStatus())) throw new ServiceException("盘点单已审核，无法修改");
    }

    private void saveCheckItemsAndRecalculate(WmsInventoryCheck check)
    {
        BigDecimal totalDiffQty = BigDecimal.ZERO;
        BigDecimal totalDiffAmount = BigDecimal.ZERO;
        for (WmsInventoryCheckItem item : check.getItemList())
        {
            if (item.getProductId() == null) continue;
            validateLocationBelongsToWarehouse(item.getLocationId(), check.getWarehouseId());
            item.setCheckId(check.getCheckId());
            Long locationId = defaultLocationId(item.getLocationId());
            String batchNo = defaultBatchNo(item.getBatchNo());
            item.setLocationId(locationId);
            item.setBatchNo(batchNo);
            BigDecimal ratio = resolveConvertRatio(item);
            item.setConvertRatio(ratio);
            BigDecimal stockQty = item.getStockQty();
            if (stockQty == null && check.getWarehouseId() != null)
            {
                WmsStock q = new WmsStock();
                q.setWarehouseId(check.getWarehouseId());
                q.setProductId(item.getProductId());
                q.setLocationId(locationId);
                q.setBatchNo(batchNo);
                WmsStock current = wmsStockMapper.selectWmsStockByKey(q);
                BigDecimal baseStock = current == null ? BigDecimal.ZERO : defaultBigDecimal(current.getQuantity());
                stockQty = ratio.compareTo(BigDecimal.ONE) > 0 ? baseStock.divide(ratio, 4, RoundingMode.HALF_UP) : baseStock;
                item.setStockQty(stockQty);
            }
            stockQty = defaultBigDecimal(stockQty);
            BigDecimal actualQty = defaultBigDecimal(item.getActualQty());
            BigDecimal diffQty = actualQty.subtract(stockQty);
            BigDecimal baseDiffQty = diffQty.multiply(ratio).setScale(4, RoundingMode.HALF_UP);
            BigDecimal price = defaultBigDecimal(item.getPrice());
            if (price.compareTo(BigDecimal.ZERO) <= 0 && finCostCalculateService != null)
            {
                price = finCostCalculateService.resolveReturnUnitCost(check.getWarehouseId(), item.getProductId(), null, null, BigDecimal.ZERO);
                item.setPrice(price);
            }
            BigDecimal diffAmount = baseDiffQty.multiply(price).setScale(2, RoundingMode.HALF_UP);
            item.setDiffQty(diffQty);
            item.setBaseDiffQty(baseDiffQty);
            item.setDiffAmount(diffAmount);
            wmsInventoryCheckItemMapper.insertWmsInventoryCheckItem(item);
            totalDiffQty = totalDiffQty.add(baseDiffQty);
            totalDiffAmount = totalDiffAmount.add(diffAmount);
        }
        check.setTotalDiffQty(totalDiffQty);
        check.setTotalDiffAmount(totalDiffAmount);
        wmsInventoryCheckMapper.updateWmsInventoryCheck(check);
    }

    private WmsInventoryCheck checkInventoryCheckDeletable(Long checkId)
    {
        WmsInventoryCheck inventoryCheck = wmsInventoryCheckMapper.selectWmsInventoryCheckById(checkId);
        if (inventoryCheck == null) throw new ServiceException("盘点单不存在");
        if (!"0".equals(inventoryCheck.getStatus())) throw new ServiceException("盘点单状态已变更，无法删除");
        return inventoryCheck;
    }
}
