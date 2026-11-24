package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.FinCostCalculate;
import com.canglian.business.domain.MdLocation;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.MdWarehouse;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsStockLock;
import com.canglian.business.domain.WmsStockLog;
import com.canglian.business.mapper.MdLocationMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.MdWarehouseMapper;
import com.canglian.business.mapper.WmsStockLockMapper;
import com.canglian.business.mapper.WmsStockLogMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IFinCostCalculateService;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IWmsStockService;
import com.canglian.common.annotation.WarehouseScope;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;
import com.canglian.system.service.ISysConfigService;

/**
 * 库存 服务层实现（含效期预警、FEFO 推荐、手工冻结/解冻、订单级锁库台账与内控防篡改）
 *
 * @author canglian
 */
@Service
public class WmsStockServiceImpl implements IWmsStockService
{
    @Autowired
    private WmsStockMapper wmsStockMapper;

    @Autowired(required = false)
    private WmsStockLogMapper wmsStockLogMapper;

    @Autowired(required = false)
    private WmsStockLockMapper wmsStockLockMapper;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Autowired(required = false)
    private IFinGeneralLedgerService finGeneralLedgerService;

    @Autowired(required = false)
    private ISysConfigService sysConfigService;

    @Autowired(required = false)
    private MdWarehouseMapper mdWarehouseMapper;

    @Autowired(required = false)
    private MdProductMapper mdProductMapper;

    @Autowired(required = false)
    private MdLocationMapper mdLocationMapper;

    @Autowired(required = false)
    private IFinCostCalculateService finCostCalculateService;

    @Override
    public WmsStock selectWmsStockById(Long stockId)
    {
        WmsStock stock = wmsStockMapper.selectWmsStockById(stockId);
        if (stock != null && bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(stock.getWarehouseId());
        }
        fillAvailableAndExpiry(stock);
        return stock;
    }

    @Override
    @WarehouseScope(column = "warehouse_id")
    public List<WmsStock> selectWmsStockList(WmsStock wmsStock)
    {
        List<WmsStock> stockList = wmsStockMapper.selectWmsStockList(wmsStock);
        enrichMasterNames(stockList);
        for (WmsStock stock : stockList)
        {
            fillAvailableAndExpiry(stock);
        }
        return stockList;
    }

    private void enrichMasterNames(List<WmsStock> stockList)
    {
        if (stockList == null || stockList.isEmpty()) return;
        java.util.Map<Long, MdWarehouse> whMap = new java.util.HashMap<Long, MdWarehouse>();
        if (mdWarehouseMapper != null)
        {
            List<MdWarehouse> whs = mdWarehouseMapper.selectMdWarehouseList(new MdWarehouse());
            if (whs != null) for (MdWarehouse w : whs) whMap.put(w.getWarehouseId(), w);
        }
        java.util.Map<Long, MdProduct> prodMap = new java.util.HashMap<Long, MdProduct>();
        if (mdProductMapper != null)
        {
            List<MdProduct> prods = mdProductMapper.selectMdProductList(new MdProduct());
            if (prods != null) for (MdProduct p : prods) prodMap.put(p.getProductId(), p);
        }
        java.util.Map<Long, MdLocation> locMap = new java.util.HashMap<Long, MdLocation>();
        if (mdLocationMapper != null)
        {
            List<MdLocation> locs = mdLocationMapper.selectMdLocationList(new MdLocation());
            if (locs != null) for (MdLocation l : locs) locMap.put(l.getLocationId(), l);
        }
        for (WmsStock s : stockList)
        {
            MdWarehouse w = whMap.get(s.getWarehouseId());
            if (w != null) { s.setWarehouseCode(w.getWarehouseCode()); s.setWarehouseName(w.getWarehouseName()); }
            MdProduct p = prodMap.get(s.getProductId());
            if (p != null) { s.setProductCode(p.getProductCode()); s.setProductName(p.getProductName()); }
            if (s.getLocationId() == null || s.getLocationId() == 0L)
            {
                s.setLocationCode("");
                s.setLocationName("默认库位");
            }
            else
            {
                MdLocation l = locMap.get(s.getLocationId());
                if (l != null) { s.setLocationCode(l.getLocationCode()); s.setLocationName(l.getLocationName()); }
            }
        }
    }

    @Override
    public String importWmsStock(List<WmsStock> stockList, Boolean updateSupport, String operator)
    {
        if (StringUtils.isNull(stockList) || stockList.isEmpty())
        {
            throw new ServiceException("导入库存数据不能为空");
        }
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(new Date());
        }
        int successNumber = 0;
        StringBuilder failureMessage = new StringBuilder();
        for (int stockIndex = 0; stockIndex < stockList.size(); stockIndex++)
        {
            WmsStock stock = stockList.get(stockIndex);
            try
            {
                MdProduct matchedProduct = resolveImportStockRefs(stock);
                validateImportStock(stock, matchedProduct);
                if (bizScopeHelper != null)
                {
                    bizScopeHelper.assertWarehouseAccess(stock.getWarehouseId());
                }
                if (stock.getLocationId() == null)
                {
                    stock.setLocationId(0L);
                }
                stock.setBatchNo(StringUtils.isEmpty(stock.getBatchNo()) ? "" : stock.getBatchNo().trim());
                WmsStock existingStock = wmsStockMapper.selectWmsStockByKey(stock);
                BigDecimal oldQty = existingStock == null ? BigDecimal.ZERO : defaultBigDecimal(existingStock.getQuantity());
                BigDecimal newQty = defaultBigDecimal(stock.getQuantity());
                BigDecimal delta = newQty.subtract(oldQty);
                if (existingStock == null)
                {
                    fillDefaultStockValue(stock);
                    stock.setCreateBy(operator);
                    wmsStockMapper.insertWmsStock(stock);
                    syncOpeningStockLogAndCost(stock, matchedProduct, oldQty, newQty, delta, operator);
                    successNumber++;
                }
                else if (Boolean.TRUE.equals(updateSupport))
                {
                    if (defaultBigDecimal(existingStock.getLockedQuantity()).compareTo(BigDecimal.ZERO) > 0
                        || defaultBigDecimal(existingStock.getFrozenQuantity()).compareTo(BigDecimal.ZERO) > 0)
                    {
                        throw new ServiceException("该库存维度存在锁定或冻结数量，禁止期初导入覆盖");
                    }
                    stock.setStockId(existingStock.getStockId());
                    stock.setLockedQuantity(defaultBigDecimal(existingStock.getLockedQuantity()));
                    stock.setFrozenQuantity(defaultBigDecimal(existingStock.getFrozenQuantity()));
                    if (stock.getWarningMinQty() == null)
                    {
                        stock.setWarningMinQty(defaultBigDecimal(existingStock.getWarningMinQty()));
                    }
                    if (stock.getWarningMaxQty() == null)
                    {
                        stock.setWarningMaxQty(defaultBigDecimal(existingStock.getWarningMaxQty()));
                    }
                    if (stock.getProductionDate() == null) stock.setProductionDate(existingStock.getProductionDate());
                    if (stock.getExpiryDate() == null) stock.setExpiryDate(existingStock.getExpiryDate());
                    stock.setVersion(existingStock.getVersion() == null ? 0L : existingStock.getVersion());
                    stock.setUpdateBy(operator);
                    wmsStockMapper.updateWmsStock(stock);
                    syncOpeningStockLogAndCost(stock, matchedProduct, oldQty, newQty, delta, operator);
                    successNumber++;
                }
                else
                {
                    failureMessage.append("\n第").append(stockIndex + 1).append("行库存维度已存在");
                }
            }
            catch (Exception exception)
            {
                failureMessage.append("\n第").append(stockIndex + 1).append("行导入失败：").append(exception.getMessage());
            }
        }
        if (failureMessage.length() > 0)
        {
            return "库存导入完成，成功 " + successNumber + " 条，失败信息：" + failureMessage;
        }
        return "库存导入成功，共 " + successNumber + " 条";
    }

    @Override
    @WarehouseScope(column = "warehouse_id")
    public List<WmsStock> selectWmsStockWarningList(WmsStock wmsStock)
    {
        List<WmsStock> stockList = wmsStockMapper.selectWmsStockWarningList(wmsStock);
        for (WmsStock stock : stockList)
        {
            fillAvailableAndExpiry(stock);
        }
        return stockList;
    }

    @Override
    @WarehouseScope(warehouseAlias = "stock", column = "warehouse_id")
    public List<WmsStock> selectWmsStockRiskList(WmsStock wmsStock)
    {
        if (wmsStock.getWarningDays() == null)
        {
            wmsStock.setWarningDays(90);
        }
        List<WmsStock> stockList = wmsStockMapper.selectWmsStockRiskList(wmsStock);
        for (WmsStock stock : stockList)
        {
            fillAvailableAndExpiry(stock);
        }
        return stockList;
    }

    @Override
    @WarehouseScope(warehouseAlias = "s", column = "warehouse_id")
    public List<WmsStock> selectExpiryWarningList(WmsStock wmsStock)
    {
        if (wmsStock.getWarningDays() == null)
        {
            wmsStock.setWarningDays(getDefaultExpiryWarningDays());
        }
        List<WmsStock> stockList = wmsStockMapper.selectExpiryWarningList(wmsStock);
        for (WmsStock stock : stockList)
        {
            fillAvailableAndExpiry(stock);
        }
        return stockList;
    }

    @Override
    public List<WmsStock> selectFefoBatchList(Long warehouseId, Long productId)
    {
        if (warehouseId == null || productId == null)
        {
            throw new ServiceException("查询 FEFO 批次时仓库和商品不能为空");
        }
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(warehouseId);
        }
        List<WmsStock> list = wmsStockMapper.selectFefoStockList(warehouseId, productId);
        for (WmsStock stock : list)
        {
            fillAvailableAndExpiry(stock);
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsStock freezeStock(Long stockId, BigDecimal freezeQty, String reason, String operator)
    {
        if (stockId == null || freezeQty == null || freezeQty.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("冻结数量必须大于0");
        }
        if (StringUtils.isEmpty(reason))
        {
            throw new ServiceException("冻结库存必须填写冻结原因");
        }
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(new Date());
        }
        WmsStock stock = wmsStockMapper.selectWmsStockByIdForUpdate(stockId);
        if (stock == null)
        {
            throw new ServiceException("库存记录不存在");
        }
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(stock.getWarehouseId());
        }
        BigDecimal available = calculateAvailableQuantity(stock);
        if (freezeQty.compareTo(available) > 0)
        {
            throw new ServiceException("冻结数量(" + freezeQty + ")不能超过当前可用库存(" + available + ")");
        }
        BigDecimal newFrozen = defaultBigDecimal(stock.getFrozenQuantity()).add(freezeQty);
        stock.setFrozenQuantity(newFrozen);
        stock.setUpdateBy(operator);
        wmsStockMapper.updateWmsStock(stock);
        recordStockActionLog(stock, "stock_freeze", stockId, "2", freezeQty, operator);
        fillAvailableAndExpiry(stock);
        return stock;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsStock unfreezeStock(Long stockId, BigDecimal unfreezeQty, String reason, String operator)
    {
        if (stockId == null || unfreezeQty == null || unfreezeQty.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("解冻数量必须大于0");
        }
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(new Date());
        }
        WmsStock stock = wmsStockMapper.selectWmsStockByIdForUpdate(stockId);
        if (stock == null)
        {
            throw new ServiceException("库存记录不存在");
        }
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(stock.getWarehouseId());
        }
        BigDecimal currentFrozen = defaultBigDecimal(stock.getFrozenQuantity());
        if (unfreezeQty.compareTo(currentFrozen) > 0)
        {
            throw new ServiceException("解冻数量(" + unfreezeQty + ")不能超过当前冻结数量(" + currentFrozen + ")");
        }
        stock.setFrozenQuantity(currentFrozen.subtract(unfreezeQty));
        stock.setUpdateBy(operator);
        wmsStockMapper.updateWmsStock(stock);
        recordStockActionLog(stock, "stock_unfreeze", stockId, "1", unfreezeQty, operator);
        fillAvailableAndExpiry(stock);
        return stock;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void lockStockForSaleOrder(Long warehouseId, Long productId, BigDecimal lockQty, Long orderId, String operator)
    {
        if (warehouseId == null || productId == null || lockQty == null || lockQty.compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }
        List<WmsStock> rows = wmsStockMapper.selectWmsStockByWarehouseAndProductForUpdate(warehouseId, productId);
        Date todayStart = startOfToday();
        List<WmsStock> eligibleRows = new ArrayList<WmsStock>();
        BigDecimal totalAvail = BigDecimal.ZERO;
        if (rows != null)
        {
            for (WmsStock row : rows)
            {
                // 跳过已过期批次，防止锁定无法出库的过期商品
                if (row.getExpiryDate() != null && row.getExpiryDate().before(todayStart))
                {
                    continue;
                }
                BigDecimal avail = calculateAvailableQuantity(row);
                if (avail.compareTo(BigDecimal.ZERO) > 0)
                {
                    totalAvail = totalAvail.add(avail);
                    eligibleRows.add(row);
                }
            }
        }
        if (totalAvail.compareTo(lockQty) < 0)
        {
            throw new ServiceException("仓库 #" + warehouseId + " 商品 #" + productId + " 未过期可用库存不足(" + totalAvail + " < " + lockQty + ")，无法锁库审批");
        }
        BigDecimal remaining = lockQty;
        for (WmsStock row : eligibleRows)
        {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0)
            {
                break;
            }
            BigDecimal avail = calculateAvailableQuantity(row);
            if (avail.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            BigDecimal take = avail.min(remaining);
            row.setLockedQuantity(defaultBigDecimal(row.getLockedQuantity()).add(take));
            row.setUpdateBy(operator);
            wmsStockMapper.updateWmsStock(row);

            if (wmsStockLockMapper != null && orderId != null)
            {
                WmsStockLock lock = new WmsStockLock();
                lock.setOrderId(orderId);
                lock.setWarehouseId(warehouseId);
                lock.setProductId(productId);
                lock.setLocationId(row.getLocationId() == null ? 0L : row.getLocationId());
                lock.setBatchNo(row.getBatchNo() == null ? "" : row.getBatchNo());
                lock.setStockId(row.getStockId());
                lock.setLockedQty(take);
                lock.setReleasedQty(BigDecimal.ZERO);
                lock.setStatus("0");
                lock.setCreateBy(operator);
                wmsStockLockMapper.insertWmsStockLock(lock);
            }
            remaining = remaining.subtract(take);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseStockLockForSaleOrder(Long warehouseId, Long productId, BigDecimal releaseQty, Long orderId, String operator)
    {
        releaseStockLockForSaleOrder(warehouseId, productId, null, null, releaseQty, orderId, operator);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseStockLockForSaleOrder(Long warehouseId, Long productId, Long locationId, String batchNo,
                                             BigDecimal releaseQty, Long orderId, String operator)
    {
        if (warehouseId == null || productId == null || releaseQty == null || releaseQty.compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }
        // 优先严格按订单锁库明细台账释放，绝不跨单窃解其他订单的锁定库存
        if (wmsStockLockMapper != null && orderId != null)
        {
            List<WmsStockLock> orderLocks = wmsStockLockMapper.selectActiveLocksByOrderAndProductForUpdate(orderId, productId);
            if (orderLocks != null && !orderLocks.isEmpty())
            {
                BigDecimal remaining = releaseQty;
                String normBatch = batchNo == null ? "" : batchNo.trim();
                // 两轮：第1轮优先释放匹配当前拣货库位+批次的锁行，第2轮释放同订单下其余锁行
                for (int pass = 0; pass < 2 && remaining.compareTo(BigDecimal.ZERO) > 0; pass++)
                {
                    for (WmsStockLock lk : orderLocks)
                    {
                        if (remaining.compareTo(BigDecimal.ZERO) <= 0)
                        {
                            break;
                        }
                        BigDecimal lockRem = lk.getRemainingLockedQty();
                        if (lockRem.compareTo(BigDecimal.ZERO) <= 0)
                        {
                            continue;
                        }
                        boolean exactMatch = locationId != null
                            && locationId.equals(lk.getLocationId() == null ? 0L : lk.getLocationId())
                            && normBatch.equals(lk.getBatchNo() == null ? "" : lk.getBatchNo());
                        if (pass == 0 && locationId != null && !exactMatch)
                        {
                            continue;
                        }
                        if (pass == 1 && exactMatch)
                        {
                            continue;
                        }
                        BigDecimal rel = lockRem.min(remaining);
                        WmsStock stockRow = wmsStockMapper.selectWmsStockByIdForUpdate(lk.getStockId());
                        if (stockRow != null)
                        {
                            BigDecimal curLocked = defaultBigDecimal(stockRow.getLockedQuantity());
                            BigDecimal actualRel = curLocked.min(rel);
                            stockRow.setLockedQuantity(curLocked.subtract(actualRel));
                            stockRow.setUpdateBy(operator);
                            wmsStockMapper.updateWmsStock(stockRow);
                        }
                        BigDecimal newReleased = defaultBigDecimal(lk.getReleasedQty()).add(rel);
                        lk.setReleasedQty(newReleased);
                        lk.setStatus(newReleased.compareTo(defaultBigDecimal(lk.getLockedQty())) >= 0 ? "1" : "0");
                        lk.setUpdateBy(operator);
                        wmsStockLockMapper.updateWmsStockLock(lk);
                        remaining = remaining.subtract(rel);
                    }
                }
                return;
            }
        }

        // 兜底兼容历史未建台账的锁库（仅当 orderId 为空或该订单无台账记录时）
        List<WmsStock> rows = wmsStockMapper.selectWmsStockByWarehouseAndProductForUpdate(warehouseId, productId);
        if (rows == null || rows.isEmpty())
        {
            return;
        }
        BigDecimal remaining = releaseQty;
        String normBatch = batchNo == null ? "" : batchNo.trim();
        for (int pass = 0; pass < 2 && remaining.compareTo(BigDecimal.ZERO) > 0; pass++)
        {
            for (WmsStock row : rows)
            {
                if (remaining.compareTo(BigDecimal.ZERO) <= 0)
                {
                    break;
                }
                BigDecimal locked = defaultBigDecimal(row.getLockedQuantity());
                if (locked.compareTo(BigDecimal.ZERO) <= 0)
                {
                    continue;
                }
                boolean exactMatch = locationId != null
                    && locationId.equals(row.getLocationId() == null ? 0L : row.getLocationId())
                    && normBatch.equals(row.getBatchNo() == null ? "" : row.getBatchNo());
                if (pass == 0 && locationId != null && !exactMatch)
                {
                    continue;
                }
                if (pass == 1 && exactMatch)
                {
                    continue;
                }
                BigDecimal rel = locked.min(remaining);
                row.setLockedQuantity(locked.subtract(rel));
                row.setUpdateBy(operator);
                wmsStockMapper.updateWmsStock(row);
                remaining = remaining.subtract(rel);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseAllRemainingLocksForOrder(Long orderId, String operator)
    {
        if (orderId == null || wmsStockLockMapper == null)
        {
            return;
        }
        List<WmsStockLock> orderLocks = wmsStockLockMapper.selectActiveLocksByOrderForUpdate(orderId);
        if (orderLocks == null || orderLocks.isEmpty())
        {
            return;
        }
        for (WmsStockLock lk : orderLocks)
        {
            BigDecimal rem = lk.getRemainingLockedQty();
            if (rem.compareTo(BigDecimal.ZERO) <= 0)
            {
                lk.setStatus("1");
                lk.setUpdateBy(operator);
                wmsStockLockMapper.updateWmsStockLock(lk);
                continue;
            }
            WmsStock stockRow = wmsStockMapper.selectWmsStockByIdForUpdate(lk.getStockId());
            if (stockRow != null)
            {
                BigDecimal curLocked = defaultBigDecimal(stockRow.getLockedQuantity());
                BigDecimal actualRel = curLocked.min(rem);
                stockRow.setLockedQuantity(curLocked.subtract(actualRel));
                stockRow.setUpdateBy(operator);
                wmsStockMapper.updateWmsStock(stockRow);
            }
            lk.setReleasedQty(defaultBigDecimal(lk.getLockedQty()));
            lk.setStatus("1");
            lk.setUpdateBy(operator);
            wmsStockLockMapper.updateWmsStockLock(lk);
        }
    }

    @Override
    public int insertWmsStock(WmsStock wmsStock)
    {
        throw new ServiceException("禁止直接手工新增库存台账，请通过入库单、盘点单或期初库存导入维护库存");
    }

    @Override
    public int updateWmsStock(WmsStock wmsStock)
    {
        if (wmsStock == null || wmsStock.getStockId() == null)
        {
            throw new ServiceException("库存记录ID不能为空");
        }
        WmsStock existing = wmsStockMapper.selectWmsStockById(wmsStock.getStockId());
        if (existing == null)
        {
            throw new ServiceException("库存记录不存在");
        }
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(existing.getWarehouseId());
        }
        // 仅允许修改上下限预警阈值及批次效期信息，严禁直接手工篡改账面/锁定/冻结数量
        WmsStock safePatch = new WmsStock();
        safePatch.setStockId(existing.getStockId());
        safePatch.setWarningMinQty(wmsStock.getWarningMinQty() != null ? wmsStock.getWarningMinQty() : existing.getWarningMinQty());
        safePatch.setWarningMaxQty(wmsStock.getWarningMaxQty() != null ? wmsStock.getWarningMaxQty() : existing.getWarningMaxQty());
        if (wmsStock.getProductionDate() != null) safePatch.setProductionDate(wmsStock.getProductionDate());
        if (wmsStock.getExpiryDate() != null) safePatch.setExpiryDate(wmsStock.getExpiryDate());
        safePatch.setUpdateBy(wmsStock.getUpdateBy());
        return wmsStockMapper.updateWmsStock(safePatch);
    }

    @Override
    public int deleteWmsStockById(Long stockId)
    {
        throw new ServiceException("禁止直接删除库存台账记录，请通过出库单、盘点单或红冲单据归零库存");
    }

    @Override
    public int deleteWmsStockByIds(Long[] stockIds)
    {
        throw new ServiceException("禁止直接删除库存台账记录，请通过出库单、盘点单或红冲单据归零库存");
    }

    private void recordStockActionLog(WmsStock stock, String billType, Long billId, String inOut, BigDecimal qty, String operator)
    {
        if (wmsStockLogMapper == null)
        {
            return;
        }
        WmsStockLog log = new WmsStockLog();
        log.setWarehouseId(stock.getWarehouseId());
        log.setProductId(stock.getProductId());
        log.setLocationId(stock.getLocationId() == null ? 0L : stock.getLocationId());
        log.setBatchNo(stock.getBatchNo() == null ? "" : stock.getBatchNo());
        log.setBillType(billType);
        log.setBillId(billId);
        log.setInOut(inOut);
        log.setQuantity(qty);
        log.setPrice(BigDecimal.ZERO);
        log.setAmount(BigDecimal.ZERO);
        log.setBeforeQty(defaultBigDecimal(stock.getQuantity()));
        log.setAfterQty(defaultBigDecimal(stock.getQuantity()));
        log.setCreateBy(operator);
        wmsStockLogMapper.insertWmsStockLog(log);
    }

    private void fillAvailableAndExpiry(WmsStock stock)
    {
        if (stock == null)
        {
            return;
        }
        stock.setAvailableQuantity(calculateAvailableQuantity(stock));
        int defaultDays = getDefaultExpiryWarningDays();
        if (stock.getExpiryDate() != null)
        {
            long diffDays = TimeUnit.MILLISECONDS.toDays(truncateDate(stock.getExpiryDate()).getTime() - startOfToday().getTime());
            stock.setRemainDays((int) diffDays);
            if (diffDays < 0)
            {
                stock.setExpiryStatus("expired");
            }
            else if (diffDays <= defaultDays)
            {
                stock.setExpiryStatus("near_expiry");
            }
            else
            {
                stock.setExpiryStatus("normal");
            }
        }
        else if (StringUtils.isEmpty(stock.getExpiryStatus()))
        {
            stock.setExpiryStatus("normal");
        }
    }

    private int getDefaultExpiryWarningDays()
    {
        if (sysConfigService != null)
        {
            String val = sysConfigService.selectConfigByKey("canglian.stock.expiryWarningDays");
            if (StringUtils.isNotEmpty(val))
            {
                try
                {
                    int parsed = Integer.parseInt(val.trim());
                    if (parsed > 0)
                    {
                        return parsed;
                    }
                }
                catch (Exception ignored)
                {
                }
            }
        }
        return 30;
    }

    private Date startOfToday()
    {
        return truncateDate(new Date());
    }

    private Date truncateDate(Date d)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private BigDecimal calculateAvailableQuantity(WmsStock stock)
    {
        BigDecimal quantity = defaultBigDecimal(stock.getQuantity());
        BigDecimal lockedQuantity = defaultBigDecimal(stock.getLockedQuantity());
        BigDecimal frozenQuantity = defaultBigDecimal(stock.getFrozenQuantity());
        return quantity.subtract(lockedQuantity).subtract(frozenQuantity);
    }

    private BigDecimal defaultBigDecimal(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO : value;
    }

    private MdProduct resolveImportStockRefs(WmsStock stock)
    {
        if (stock.getWarehouseId() == null && StringUtils.isNotEmpty(stock.getWarehouseCode()) && mdWarehouseMapper != null)
        {
            String code = stock.getWarehouseCode().trim();
            MdWarehouse q = new MdWarehouse();
            q.setWarehouseCode(code);
            List<MdWarehouse> list = mdWarehouseMapper.selectMdWarehouseList(q);
            MdWarehouse matched = null;
            if (list != null)
            {
                for (MdWarehouse w : list)
                {
                    if (code.equalsIgnoreCase(w.getWarehouseCode()))
                    {
                        matched = w;
                        break;
                    }
                }
            }
            if (matched == null)
            {
                throw new ServiceException("仓库编码[" + code + "]不存在");
            }
            if ("1".equals(matched.getStatus()))
            {
                throw new ServiceException("仓库[" + matched.getWarehouseName() + "]已停用，禁止期初导入");
            }
            stock.setWarehouseId(matched.getWarehouseId());
        }

        MdProduct matchedProduct = null;
        if (stock.getProductId() == null && StringUtils.isNotEmpty(stock.getProductCode()) && mdProductMapper != null)
        {
            String code = stock.getProductCode().trim();
            MdProduct q = new MdProduct();
            q.setProductCode(code);
            List<MdProduct> list = mdProductMapper.selectMdProductList(q);
            if (list != null)
            {
                for (MdProduct p : list)
                {
                    if (code.equalsIgnoreCase(p.getProductCode()))
                    {
                        matchedProduct = p;
                        break;
                    }
                }
            }
            if (matchedProduct == null)
            {
                throw new ServiceException("商品编码[" + code + "]不存在");
            }
            if ("1".equals(matchedProduct.getStatus()))
            {
                throw new ServiceException("商品[" + matchedProduct.getProductName() + "]已停用，禁止期初导入");
            }
            stock.setProductId(matchedProduct.getProductId());
        }
        else if (stock.getProductId() != null && mdProductMapper != null)
        {
            matchedProduct = mdProductMapper.selectMdProductById(stock.getProductId());
        }

        if (stock.getLocationId() == null && StringUtils.isNotEmpty(stock.getLocationCode()) && mdLocationMapper != null)
        {
            String code = stock.getLocationCode().trim();
            MdLocation q = new MdLocation();
            q.setWarehouseId(stock.getWarehouseId());
            q.setLocationCode(code);
            List<MdLocation> list = mdLocationMapper.selectMdLocationList(q);
            MdLocation matched = null;
            if (list != null)
            {
                for (MdLocation l : list)
                {
                    if (code.equalsIgnoreCase(l.getLocationCode()))
                    {
                        matched = l;
                        break;
                    }
                }
            }
            if (matched == null)
            {
                throw new ServiceException("当前仓库下不存在库位编码[" + code + "]");
            }
            if ("1".equals(matched.getStatus()))
            {
                throw new ServiceException("库位[" + matched.getLocationName() + "]已停用，禁止期初导入");
            }
            stock.setLocationId(matched.getLocationId());
        }
        return matchedProduct;
    }

    private void syncOpeningStockLogAndCost(WmsStock stock, MdProduct product, BigDecimal oldQty, BigDecimal newQty,
        BigDecimal delta, String operator)
    {
        if (delta == null || delta.compareTo(BigDecimal.ZERO) == 0)
        {
            return;
        }
        BigDecimal unitCost = (stock.getCostPrice() != null && stock.getCostPrice().compareTo(BigDecimal.ZERO) > 0)
            ? stock.getCostPrice()
            : (product != null && product.getCostPrice() != null && product.getCostPrice().compareTo(BigDecimal.ZERO) > 0
                ? product.getCostPrice() : BigDecimal.ZERO);
        BigDecimal absQty = delta.abs();
        String inOut = delta.compareTo(BigDecimal.ZERO) > 0 ? "1" : "2";
        if (wmsStockLogMapper != null)
        {
            WmsStockLog log = new WmsStockLog();
            log.setWarehouseId(stock.getWarehouseId());
            log.setProductId(stock.getProductId());
            log.setLocationId(stock.getLocationId() == null ? 0L : stock.getLocationId());
            log.setBatchNo(stock.getBatchNo() == null ? "" : stock.getBatchNo());
            log.setBillType("stock_opening");
            log.setBillId(stock.getStockId() == null ? 0L : stock.getStockId());
            log.setInOut(inOut);
            log.setQuantity(absQty);
            log.setPrice(unitCost);
            log.setAmount(absQty.multiply(unitCost).setScale(2, java.math.RoundingMode.HALF_UP));
            log.setBeforeQty(oldQty);
            log.setAfterQty(newQty);
            log.setCreateBy(operator);
            wmsStockLogMapper.insertWmsStockLog(log);
        }
        if (finCostCalculateService != null)
        {
            FinCostCalculate calc = new FinCostCalculate();
            calc.setProductId(stock.getProductId());
            calc.setWarehouseId(stock.getWarehouseId());
            calc.setBillId(stock.getStockId() == null ? 0L : stock.getStockId());
            calc.setBillType("stock_opening");
            calc.setInOut(inOut);
            calc.setQuantity(absQty);
            calc.setPrice(unitCost);
            finCostCalculateService.calculateCost(calc);
        }
    }

    private void validateImportStock(WmsStock stock, MdProduct product)
    {
        if (stock.getWarehouseId() == null)
        {
            throw new ServiceException("仓库编码/仓库编号不能为空");
        }
        if (stock.getProductId() == null)
        {
            throw new ServiceException("商品编码/商品编号不能为空");
        }
        if (stock.getQuantity() == null || stock.getQuantity().compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("库存数量不能为空且不能为负数");
        }
        if (product != null && "1".equals(product.getEnableBatch()) && StringUtils.isEmpty(stock.getBatchNo()))
        {
            throw new ServiceException("商品[" + product.getProductName() + "]启用了批次管理，期初导入必须填写批次号");
        }
    }

    private void fillDefaultStockValue(WmsStock stock)
    {
        if (stock.getLocationId() == null)
        {
            stock.setLocationId(0L);
        }
        stock.setBatchNo(StringUtils.isEmpty(stock.getBatchNo()) ? "" : stock.getBatchNo().trim());
        if (stock.getLockedQuantity() == null)
        {
            stock.setLockedQuantity(BigDecimal.ZERO);
        }
        if (stock.getFrozenQuantity() == null)
        {
            stock.setFrozenQuantity(BigDecimal.ZERO);
        }
        if (stock.getWarningMinQty() == null)
        {
            stock.setWarningMinQty(BigDecimal.ZERO);
        }
        if (stock.getWarningMaxQty() == null)
        {
            stock.setWarningMaxQty(BigDecimal.ZERO);
        }
        if (stock.getVersion() == null)
        {
            stock.setVersion(0L);
        }
    }
}
