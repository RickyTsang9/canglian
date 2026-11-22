package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.FinCostAccount;
import com.canglian.business.domain.FinCostCalculate;
import com.canglian.business.domain.FinCostLayer;
import com.canglian.business.domain.FinCostLog;
import com.canglian.business.domain.MdProduct;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.domain.WmsStockLog;
import com.canglian.business.mapper.FinCostAccountMapper;
import com.canglian.business.mapper.FinCostLayerMapper;
import com.canglian.business.mapper.FinCostLogMapper;
import com.canglian.business.mapper.MdProductMapper;
import com.canglian.business.mapper.WmsStockLogMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IFinCostCalculateService;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.SecurityUtils;
import com.canglian.common.utils.StringUtils;
import com.canglian.system.domain.SysConfig;
import com.canglian.system.service.ISysConfigService;

@Service
public class FinCostCalculateServiceImpl implements IFinCostCalculateService
{
    public static final String COST_METHOD_CONFIG_KEY = "canglian.cost.defaultMethod";

    @Autowired
    private FinCostAccountMapper finCostAccountMapper;

    @Autowired
    private FinCostLayerMapper finCostLayerMapper;

    @Autowired
    private FinCostLogMapper finCostLogMapper;

    @Autowired
    private WmsStockLogMapper wmsStockLogMapper;

    @Autowired
    private WmsStockMapper wmsStockMapper;

    @Autowired
    private MdProductMapper mdProductMapper;

    @Autowired(required = false)
    private ISysConfigService sysConfigService;

    @Autowired(required = false)
    private IFinGeneralLedgerService finGeneralLedgerService;

    @Autowired(required = false)
    private IFinVoucherEventService finVoucherEventService;

    @Override
    public String getGlobalCostMethod()
    {
        if (sysConfigService != null)
        {
            String configured = sysConfigService.selectConfigByKey(COST_METHOD_CONFIG_KEY);
            if (StringUtils.isNotEmpty(configured))
            {
                return normalizeCostMethod(configured);
            }
        }
        return "moving";
    }

    @Override
    public Map<String, Object> getGlobalCostConfig()
    {
        String currentMethod = getGlobalCostMethod();
        long historyLogCount = finCostLogMapper.countAllCostLogs();
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("costMethod", currentMethod);
        result.put("costMethodLabel", "fifo".equals(currentMethod) ? "先进先出 (FIFO)" : "移动加权平均");
        result.put("historyLogCount", historyLogCount);
        result.put("canSwitch", historyLogCount == 0L);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> updateGlobalCostMethod(String costMethod, String operator)
    {
        if (StringUtils.isEmpty(costMethod))
        {
            throw new ServiceException("成本核算算法不能为空");
        }
        String normalized = normalizeCostMethod(costMethod);
        if (!"moving".equals(normalized) && !"fifo".equals(normalized))
        {
            throw new ServiceException("成本核算算法仅支持 moving(移动加权) 或 fifo(先进先出)");
        }
        String currentMethod = getGlobalCostMethod();
        long historyLogCount = finCostLogMapper.countAllCostLogs();
        if (!normalized.equals(currentMethod) && historyLogCount > 0L)
        {
            throw new ServiceException("系统已存在 " + historyLogCount + " 条历史成本流水，禁止跨算法切换；请在新账套启用前配置核算算法");
        }
        if (sysConfigService != null)
        {
            SysConfig query = new SysConfig();
            query.setConfigKey(COST_METHOD_CONFIG_KEY);
            List<SysConfig> list = sysConfigService.selectConfigList(query);
            if (list != null && !list.isEmpty())
            {
                SysConfig existing = list.get(0);
                existing.setConfigValue(normalized);
                existing.setUpdateBy(operator);
                sysConfigService.updateConfig(existing);
            }
            else
            {
                SysConfig cfg = new SysConfig();
                cfg.setConfigName("成本核算-全局默认算法");
                cfg.setConfigKey(COST_METHOD_CONFIG_KEY);
                cfg.setConfigValue(normalized);
                cfg.setConfigType("Y");
                cfg.setCreateBy(operator);
                sysConfigService.insertConfig(cfg);
            }
        }
        return getGlobalCostConfig();
    }

    @Override
    public BigDecimal resolveReturnUnitCost(Long warehouseId, Long productId, String sourceBillType, Long sourceBillId, BigDecimal fallbackPrice)
    {
        // 1. 若关联了来源出库单，优先取原出库时的历史结转成本单价
        if ("outbound".equals(sourceBillType) && sourceBillId != null && productId != null)
        {
            FinCostLog q = new FinCostLog();
            q.setBillType("outbound");
            q.setBillId(sourceBillId);
            q.setProductId(productId);
            List<FinCostLog> logs = finCostLogMapper.selectFinCostLogList(q);
            if (logs != null && !logs.isEmpty())
            {
                BigDecimal totalQty = BigDecimal.ZERO;
                BigDecimal totalAmt = BigDecimal.ZERO;
                for (FinCostLog logRow : logs)
                {
                    totalQty = totalQty.add(getSafeDecimal(logRow.getQuantity()));
                    totalAmt = totalAmt.add(getSafeDecimal(logRow.getAmount()));
                }
                if (totalQty.compareTo(BigDecimal.ZERO) > 0 && totalAmt.compareTo(BigDecimal.ZERO) > 0)
                {
                    return calculateAverageCost(totalQty, totalAmt);
                }
            }
        }
        // 2. 否则取当前仓库该商品成本账户均价
        if (warehouseId != null && productId != null)
        {
            FinCostAccount account = finCostAccountMapper.selectFinCostAccountByUnique(productId, warehouseId, getGlobalCostMethod());
            if (account != null && getSafeDecimal(account.getAvgCost()).compareTo(BigDecimal.ZERO) > 0)
            {
                return account.getAvgCost();
            }
        }
        // 3. 其次取商品档案参考成本价
        if (productId != null && mdProductMapper != null)
        {
            MdProduct product = mdProductMapper.selectMdProductById(productId);
            if (product != null && getSafeDecimal(product.getCostPrice()).compareTo(BigDecimal.ZERO) > 0)
            {
                return product.getCostPrice();
            }
        }
        // 4. 兜底使用传入单价
        BigDecimal safeFallback = getSafeDecimal(fallbackPrice);
        return safeFallback.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : safeFallback;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinCostLog calculateCost(FinCostCalculate finCostCalculate)
    {
        if (finCostCalculate == null)
        {
            throw new ServiceException("成本核算参数不能为空");
        }
        if (finCostCalculate.getProductId() == null || finCostCalculate.getWarehouseId() == null)
        {
            throw new ServiceException("商品编号和仓库编号不能为空");
        }
        if (finCostCalculate.getQuantity() == null || finCostCalculate.getQuantity().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("核算数量必须大于0");
        }
        if (finCostCalculate.getPrice() != null && finCostCalculate.getPrice().compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("核算单价不能小于0");
        }
        if (!"1".equals(finCostCalculate.getInOut()) && !"2".equals(finCostCalculate.getInOut()))
        {
            throw new ServiceException("出入库标识仅支持1(入库)或2(出库)");
        }

        String configuredMethod = sysConfigService != null ? sysConfigService.selectConfigByKey(COST_METHOD_CONFIG_KEY) : null;
        String costMethod;
        if (StringUtils.isNotEmpty(configuredMethod))
        {
            costMethod = normalizeCostMethod(configuredMethod);
        }
        else
        {
            costMethod = StringUtils.isEmpty(finCostCalculate.getCostMethod())
                ? getGlobalCostMethod()
                : normalizeCostMethod(finCostCalculate.getCostMethod());
        }

        String operatorName = resolveOperator();
        FinCostAccount finCostAccount = finCostAccountMapper.selectFinCostAccountByUnique(finCostCalculate.getProductId(), finCostCalculate.getWarehouseId(), costMethod);
        if (finCostAccount == null)
        {
            finCostAccount = buildDefaultCostAccount(finCostCalculate, costMethod, operatorName);
            finCostAccountMapper.insertFinCostAccount(finCostAccount);
        }

        if ("1".equals(finCostCalculate.getInOut()))
        {
            return handleInbound(finCostCalculate, finCostAccount, costMethod, operatorName);
        }
        return handleOutbound(finCostCalculate, finCostAccount, costMethod, operatorName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> recalculateCost(Long warehouseId, Long productId, String operator)
    {
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(new Date());
            List<Map<String, Object>> closedPeriods = finGeneralLedgerService.selectFiscalPeriodList("closed");
            if (closedPeriods != null && !closedPeriods.isEmpty())
            {
                throw new ServiceException("系统存在已结账会计期间(" + closedPeriods.size() + "期)，全量重算将影响已结账历史成本；请先反结账后再执行全量成本重算");
            }
        }
        String costMethod = getGlobalCostMethod();
        List<WmsStockLog> allLogs = wmsStockLogMapper.selectWmsStockLogChronological(warehouseId, productId);

        Map<String, List<WmsStockLog>> grouped = new LinkedHashMap<String, List<WmsStockLog>>();
        if (allLogs != null)
        {
            for (WmsStockLog stockLog : allLogs)
            {
                if (stockLog.getWarehouseId() == null || stockLog.getProductId() == null)
                {
                    continue;
                }
                if (isNonCostStockLog(stockLog.getBillType()))
                {
                    continue;
                }
                String key = stockLog.getWarehouseId() + "_" + stockLog.getProductId();
                grouped.computeIfAbsent(key, k -> new ArrayList<WmsStockLog>()).add(stockLog);
            }
        }

        int recalculatedAccounts = 0;
        int replayedLogs = 0;
        List<Map<String, Object>> details = new ArrayList<Map<String, Object>>();

        for (List<WmsStockLog> logs : grouped.values())
        {
            Long whId = logs.get(0).getWarehouseId();
            Long prodId = logs.get(0).getProductId();
            FinCostAccount account = finCostAccountMapper.selectFinCostAccountByUnique(prodId, whId, costMethod);
            BigDecimal beforeAvgCost = account == null ? BigDecimal.ZERO : getSafeDecimal(account.getAvgCost());
            BigDecimal beforeTotalQty = account == null ? BigDecimal.ZERO : getSafeDecimal(account.getTotalQty());

            // 保留历史手工调价流水，重放完成后按时间顺序补回
            List<FinCostLog> preservedAdjustLogs = new ArrayList<FinCostLog>();
            if (account != null)
            {
                FinCostLog adjQ = new FinCostLog();
                adjQ.setCostAccountId(account.getCostAccountId());
                adjQ.setBillType("cost_adjust");
                List<FinCostLog> existingAdj = finCostLogMapper.selectFinCostLogList(adjQ);
                if (existingAdj != null)
                {
                    preservedAdjustLogs.addAll(existingAdj);
                }
                finCostLayerMapper.deleteFinCostLayerByAccountId(account.getCostAccountId());
                finCostLogMapper.deleteFinCostLogByAccountId(account.getCostAccountId());
                account.setTotalQty(BigDecimal.ZERO);
                account.setTotalAmount(BigDecimal.ZERO);
                account.setAvgCost(BigDecimal.ZERO);
                account.setLastCostPrice(BigDecimal.ZERO);
                account.setLastCostQty(BigDecimal.ZERO);
                account.setUpdateBy(operator);
                finCostAccountMapper.updateFinCostAccount(account);
            }

            for (WmsStockLog stockLog : logs)
            {
                BigDecimal qty = getSafeDecimal(stockLog.getQuantity());
                if (qty.compareTo(BigDecimal.ZERO) <= 0)
                {
                    continue;
                }
                String bt = stockLog.getBillType();
                if ("inbound_unaudit".equals(bt) || "inbound_reverse".equals(bt))
                {
                    calculateExactInboundReversal(whId, prodId, stockLog.getBillId(), bt, qty, getSafeDecimal(stockLog.getPrice()));
                    replayedLogs++;
                    continue;
                }
                FinCostCalculate req = new FinCostCalculate();
                req.setCostMethod(costMethod);
                req.setWarehouseId(whId);
                req.setProductId(prodId);
                req.setBillType(bt);
                req.setBillId(stockLog.getBillId());
                req.setInOut(stockLog.getInOut());
                req.setQuantity(qty);
                if ("sale_return".equals(bt) || "outbound_return".equals(bt)
                    || "outbound_unaudit".equals(bt) || "outbound_reverse".equals(bt))
                {
                    BigDecimal retUnitCost = resolveReturnUnitCost(whId, prodId,
                        "outbound_unaudit".equals(bt) ? "outbound" : null,
                        "outbound_unaudit".equals(bt) ? stockLog.getBillId() : null,
                        stockLog.getPrice());
                    req.setPrice(retUnitCost);
                }
                else
                {
                    BigDecimal logPrice = getSafeDecimal(stockLog.getPrice());
                    if ("1".equals(stockLog.getInOut()) && logPrice.compareTo(BigDecimal.ZERO) <= 0)
                    {
                        logPrice = resolveReturnUnitCost(whId, prodId, null, null, BigDecimal.ZERO);
                    }
                    req.setPrice(logPrice);
                }
                calculateCost(req);
                replayedLogs++;
            }

            // 按历史调价记录重演受控成本调价（不重复发出凭证事件）
            if (!preservedAdjustLogs.isEmpty())
            {
                FinCostAccount curAcc = finCostAccountMapper.selectFinCostAccountByUnique(prodId, whId, costMethod);
                if (curAcc != null && getSafeDecimal(curAcc.getTotalQty()).compareTo(BigDecimal.ZERO) > 0)
                {
                    FinCostLog lastAdj = preservedAdjustLogs.get(preservedAdjustLogs.size() - 1);
                    BigDecimal targetAvg = getSafeDecimal(lastAdj.getPrice()).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal curQty = getSafeDecimal(curAcc.getTotalQty());
                    BigDecimal beforeAmt = getSafeDecimal(curAcc.getTotalAmount());
                    BigDecimal afterAmt = curQty.multiply(targetAvg).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal delta = afterAmt.subtract(beforeAmt);
                    curAcc.setTotalAmount(afterAmt);
                    curAcc.setAvgCost(targetAvg);
                    curAcc.setLastCostPrice(targetAvg);
                    curAcc.setUpdateBy(operator);
                    finCostAccountMapper.updateFinCostAccount(curAcc);
                    if ("fifo".equalsIgnoreCase(costMethod))
                    {
                        adjustFifoLayersToTargetAvg(curAcc.getCostAccountId(), targetAvg);
                    }
                    FinCostLog replayLog = new FinCostLog();
                    replayLog.setCostAccountId(curAcc.getCostAccountId());
                    replayLog.setProductId(prodId);
                    replayLog.setWarehouseId(whId);
                    replayLog.setBillType("cost_adjust");
                    replayLog.setBillId(curAcc.getCostAccountId());
                    replayLog.setInOut(delta.compareTo(BigDecimal.ZERO) >= 0 ? "1" : "2");
                    replayLog.setQuantity(curQty);
                    replayLog.setPrice(targetAvg);
                    replayLog.setAmount(delta.abs());
                    replayLog.setCostMethod(costMethod);
                    replayLog.setCreateBy(operator);
                    replayLog.setRemark("重算保留历史调价：" + (lastAdj.getRemark() == null ? "" : lastAdj.getRemark()));
                    finCostLogMapper.insertFinCostLog(replayLog);
                }
            }

            FinCostAccount afterAccount = finCostAccountMapper.selectFinCostAccountByUnique(prodId, whId, costMethod);
            recalculatedAccounts++;
            Map<String, Object> item = new LinkedHashMap<String, Object>();
            item.put("warehouseId", whId);
            item.put("productId", prodId);
            item.put("costMethod", costMethod);
            item.put("beforeTotalQty", beforeTotalQty);
            item.put("beforeAvgCost", beforeAvgCost);
            item.put("afterTotalQty", afterAccount == null ? BigDecimal.ZERO : getSafeDecimal(afterAccount.getTotalQty()));
            item.put("afterTotalAmount", afterAccount == null ? BigDecimal.ZERO : getSafeDecimal(afterAccount.getTotalAmount()));
            item.put("afterAvgCost", afterAccount == null ? BigDecimal.ZERO : getSafeDecimal(afterAccount.getAvgCost()));
            details.add(item);
        }

        Map<String, Object> summary = new LinkedHashMap<String, Object>();
        summary.put("costMethod", costMethod);
        summary.put("recalculatedAccounts", recalculatedAccounts);
        summary.put("replayedLogs", replayedLogs);
        summary.put("details", details);
        return summary;
    }

    private boolean isNonCostStockLog(String billType)
    {
        return "stock_freeze".equals(billType) || "stock_unfreeze".equals(billType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinCostLog adjustCost(Long warehouseId, Long productId, BigDecimal newAvgCost, String reason, String operator)
    {
        if (warehouseId == null || productId == null)
        {
            throw new ServiceException("仓库和商品不能为空");
        }
        if (newAvgCost == null || newAvgCost.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("调整后单位成本不能小于0");
        }
        if (StringUtils.isEmpty(reason))
        {
            throw new ServiceException("受控成本调价必须填写调整原因");
        }
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(new Date());
        }
        String costMethod = getGlobalCostMethod();
        FinCostAccount account = finCostAccountMapper.selectFinCostAccountByUnique(productId, warehouseId, costMethod);
        if (account == null)
        {
            // 对齐当前库存数量建账
            BigDecimal currentStockQty = sumCurrentStockQty(warehouseId, productId);
            FinCostCalculate seed = new FinCostCalculate();
            seed.setWarehouseId(warehouseId);
            seed.setProductId(productId);
            account = buildDefaultCostAccount(seed, costMethod, operator);
            account.setTotalQty(currentStockQty);
            finCostAccountMapper.insertFinCostAccount(account);
        }
        BigDecimal qty = getSafeDecimal(account.getTotalQty());
        if (qty.compareTo(BigDecimal.ZERO) <= 0)
        {
            BigDecimal currentStockQty = sumCurrentStockQty(warehouseId, productId);
            if (currentStockQty.compareTo(BigDecimal.ZERO) <= 0)
            {
                throw new ServiceException("当前商品在该仓库库存为0，无需执行成本调价");
            }
            qty = currentStockQty;
            account.setTotalQty(qty);
        }
        BigDecimal beforeTotalAmount = getSafeDecimal(account.getTotalAmount());
        BigDecimal targetAvg = newAvgCost.setScale(2, RoundingMode.HALF_UP);
        BigDecimal afterTotalAmount = qty.multiply(targetAvg).setScale(2, RoundingMode.HALF_UP);
        BigDecimal deltaAmount = afterTotalAmount.subtract(beforeTotalAmount);

        account.setTotalAmount(afterTotalAmount);
        account.setAvgCost(targetAvg);
        account.setLastCostPrice(targetAvg);
        account.setUpdateBy(operator);
        account.setRemark("成本调价：" + reason);
        finCostAccountMapper.updateFinCostAccount(account);
        if ("fifo".equalsIgnoreCase(costMethod))
        {
            adjustFifoLayersToTargetAvg(account.getCostAccountId(), targetAvg);
        }

        FinCostLog logRow = new FinCostLog();
        logRow.setCostAccountId(account.getCostAccountId());
        logRow.setProductId(productId);
        logRow.setWarehouseId(warehouseId);
        logRow.setBillType("cost_adjust");
        logRow.setBillId(account.getCostAccountId());
        logRow.setInOut(deltaAmount.compareTo(BigDecimal.ZERO) >= 0 ? "1" : "2");
        logRow.setQuantity(qty);
        logRow.setPrice(targetAvg);
        logRow.setAmount(deltaAmount.abs());
        logRow.setCostMethod(costMethod);
        logRow.setCreateBy(operator);
        logRow.setRemark("成本调价(" + beforeTotalAmount + "->" + afterTotalAmount + ")：" + reason);
        finCostLogMapper.insertFinCostLog(logRow);

        if (finVoucherEventService != null && deltaAmount.compareTo(BigDecimal.ZERO) != 0)
        {
            // 传入带正负号的差额，便于凭证分录准确区分调增与调减方向
            finVoucherEventService.recordVoucherEvent("cost_adjust", logRow.getCostLogId(),
                "CA" + logRow.getCostLogId(), "cost_adjust", new Date(), deltaAmount,
                operator, "受控成本调价：" + reason);
        }
        return logRow;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinCostLog calculateExactInboundReversal(Long warehouseId, Long productId, Long billId, String billType, BigDecimal quantity, BigDecimal exactUnitCost)
    {
        if (warehouseId == null || productId == null || quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0)
        {
            return null;
        }
        String costMethod = getGlobalCostMethod();
        String operatorName = resolveOperator();
        FinCostAccount account = finCostAccountMapper.selectFinCostAccountByUnique(productId, warehouseId, costMethod);
        if (account == null)
        {
            FinCostCalculate seed = new FinCostCalculate();
            seed.setWarehouseId(warehouseId);
            seed.setProductId(productId);
            account = buildDefaultCostAccount(seed, costMethod, operatorName);
            finCostAccountMapper.insertFinCostAccount(account);
        }
        BigDecimal unitCost = getSafeDecimal(exactUnitCost);
        if (unitCost.compareTo(BigDecimal.ZERO) <= 0)
        {
            unitCost = getSafeDecimal(account.getAvgCost());
        }
        BigDecimal reverseAmount = quantity.multiply(unitCost).setScale(2, RoundingMode.HALF_UP);
        if ("fifo".equalsIgnoreCase(costMethod))
        {
            consumeMatchingOrLatestFifoLayers(quantity, unitCost, account.getCostAccountId());
        }
        BigDecimal beforeQty = getSafeDecimal(account.getTotalQty());
        BigDecimal beforeAmount = getSafeDecimal(account.getTotalAmount());
        BigDecimal afterQty = beforeQty.subtract(quantity);
        BigDecimal afterAmount;
        if (afterQty.compareTo(BigDecimal.ZERO) <= 0)
        {
            afterQty = BigDecimal.ZERO;
            afterAmount = BigDecimal.ZERO;
        }
        else
        {
            afterAmount = beforeAmount.subtract(reverseAmount);
            if (afterAmount.compareTo(BigDecimal.ZERO) < 0)
            {
                afterAmount = BigDecimal.ZERO;
            }
        }
        account.setTotalQty(afterQty);
        account.setTotalAmount(afterAmount);
        account.setAvgCost(calculateAverageCost(afterQty, afterAmount));
        account.setUpdateBy(operatorName);
        finCostAccountMapper.updateFinCostAccount(account);

        FinCostCalculate req = new FinCostCalculate();
        req.setWarehouseId(warehouseId);
        req.setProductId(productId);
        req.setBillType(billType);
        req.setBillId(billId);
        req.setInOut("2");
        req.setQuantity(quantity);
        req.setPrice(unitCost);
        FinCostLog logRow = buildCostLog(req, account.getCostAccountId(), unitCost, reverseAmount, costMethod, operatorName);
        finCostLogMapper.insertFinCostLog(logRow);
        return logRow;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal calculateTransferCost(Long outWarehouseId, Long inWarehouseId, Long productId, Long transferId, String outBillType, String inBillType, BigDecimal quantity)
    {
        if (outWarehouseId == null || inWarehouseId == null || productId == null
            || quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0)
        {
            return BigDecimal.ZERO;
        }
        BigDecimal fallbackCost = resolveReturnUnitCost(outWarehouseId, productId, null, null, BigDecimal.ZERO);
        FinCostCalculate outReq = new FinCostCalculate();
        outReq.setWarehouseId(outWarehouseId);
        outReq.setProductId(productId);
        outReq.setBillType(outBillType);
        outReq.setBillId(transferId);
        outReq.setInOut("2");
        outReq.setQuantity(quantity);
        outReq.setPrice(fallbackCost);
        FinCostLog outLog = calculateCost(outReq);

        BigDecimal transferUnitCost = outLog != null && getSafeDecimal(outLog.getPrice()).compareTo(BigDecimal.ZERO) > 0
            ? outLog.getPrice() : fallbackCost;

        FinCostCalculate inReq = new FinCostCalculate();
        inReq.setWarehouseId(inWarehouseId);
        inReq.setProductId(productId);
        inReq.setBillType(inBillType);
        inReq.setBillId(transferId);
        inReq.setInOut("1");
        inReq.setQuantity(quantity);
        inReq.setPrice(transferUnitCost);
        calculateCost(inReq);
        return transferUnitCost;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinCostLog calculateInventoryCheckCost(Long warehouseId, Long productId, Long checkId, String billType, String inOut, BigDecimal quantity, BigDecimal fallbackUnitCost)
    {
        if (warehouseId == null || productId == null || quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0)
        {
            return null;
        }
        BigDecimal unitCost = resolveReturnUnitCost(warehouseId, productId, null, null, fallbackUnitCost);
        FinCostCalculate req = new FinCostCalculate();
        req.setWarehouseId(warehouseId);
        req.setProductId(productId);
        req.setBillType(billType);
        req.setBillId(checkId);
        req.setInOut(inOut);
        req.setQuantity(quantity);
        req.setPrice(unitCost);
        return calculateCost(req);
    }

    @Override
    public BigDecimal resolveOutboundCogsAmount(Long outboundId)
    {
        if (outboundId == null)
        {
            return BigDecimal.ZERO;
        }
        FinCostLog q = new FinCostLog();
        q.setBillType("outbound");
        q.setBillId(outboundId);
        List<FinCostLog> logs = finCostLogMapper.selectFinCostLogList(q);
        BigDecimal total = BigDecimal.ZERO;
        if (logs != null)
        {
            for (FinCostLog row : logs)
            {
                total = total.add(getSafeDecimal(row.getAmount()));
            }
        }
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private void adjustFifoLayersToTargetAvg(Long costAccountId, BigDecimal targetAvg)
    {
        List<FinCostLayer> layers = finCostLayerMapper.selectAvailableCostLayers(costAccountId);
        if (layers == null || layers.isEmpty())
        {
            return;
        }
        for (FinCostLayer layer : layers)
        {
            BigDecimal remQty = getSafeDecimal(layer.getRemainingQty());
            if (remQty.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            layer.setPrice(targetAvg);
            layer.setAmount(getSafeDecimal(layer.getQuantity()).multiply(targetAvg).setScale(2, RoundingMode.HALF_UP));
            finCostLayerMapper.updateFinCostLayer(layer);
        }
    }

    private void consumeMatchingOrLatestFifoLayers(BigDecimal requiredQty, BigDecimal exactPrice, Long costAccountId)
    {
        List<FinCostLayer> layers = finCostLayerMapper.selectAvailableCostLayers(costAccountId);
        if (layers == null || layers.isEmpty())
        {
            return;
        }
        BigDecimal remaining = requiredQty;
        // 优先从最新入库且单价匹配的层扣回
        for (int i = layers.size() - 1; i >= 0 && remaining.compareTo(BigDecimal.ZERO) > 0; i--)
        {
            FinCostLayer layer = layers.get(i);
            BigDecimal layerRem = getSafeDecimal(layer.getRemainingQty());
            if (layerRem.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            if (getSafeDecimal(layer.getPrice()).compareTo(exactPrice) == 0)
            {
                BigDecimal take = layerRem.min(remaining);
                layer.setRemainingQty(layerRem.subtract(take));
                finCostLayerMapper.updateFinCostLayer(layer);
                remaining = remaining.subtract(take);
            }
        }
        // 仍有剩余时按倒序扣回最新层
        for (int i = layers.size() - 1; i >= 0 && remaining.compareTo(BigDecimal.ZERO) > 0; i--)
        {
            FinCostLayer layer = layers.get(i);
            BigDecimal layerRem = getSafeDecimal(layer.getRemainingQty());
            if (layerRem.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            BigDecimal take = layerRem.min(remaining);
            layer.setRemainingQty(layerRem.subtract(take));
            finCostLayerMapper.updateFinCostLayer(layer);
            remaining = remaining.subtract(take);
        }
    }

    private BigDecimal sumCurrentStockQty(Long warehouseId, Long productId)
    {
        if (wmsStockMapper == null)
        {
            return BigDecimal.ZERO;
        }
        WmsStock q = new WmsStock();
        q.setWarehouseId(warehouseId);
        q.setProductId(productId);
        List<WmsStock> list = wmsStockMapper.selectWmsStockList(q);
        BigDecimal sum = BigDecimal.ZERO;
        if (list != null)
        {
            for (WmsStock row : list)
            {
                sum = sum.add(getSafeDecimal(row.getQuantity()));
            }
        }
        return sum;
    }

    private String normalizeCostMethod(String rawMethod)
    {
        if (StringUtils.isEmpty(rawMethod) || "moving_average".equalsIgnoreCase(rawMethod) || "moving".equalsIgnoreCase(rawMethod))
        {
            return "moving";
        }
        if ("fifo".equalsIgnoreCase(rawMethod))
        {
            return "fifo";
        }
        return rawMethod.trim().toLowerCase();
    }

    private FinCostLog handleInbound(FinCostCalculate finCostCalculate, FinCostAccount finCostAccount, String costMethod, String operatorName)
    {
        BigDecimal inboundPrice = getSafeDecimal(finCostCalculate.getPrice());
        BigDecimal inboundAmount = finCostCalculate.getQuantity().multiply(inboundPrice);

        BigDecimal totalQuantity = getSafeDecimal(finCostAccount.getTotalQty()).add(finCostCalculate.getQuantity());
        BigDecimal totalAmount = getSafeDecimal(finCostAccount.getTotalAmount()).add(inboundAmount);
        finCostAccount.setTotalQty(totalQuantity);
        finCostAccount.setTotalAmount(totalAmount);
        finCostAccount.setAvgCost(calculateAverageCost(totalQuantity, totalAmount));
        finCostAccount.setLastCostPrice(inboundPrice);
        finCostAccount.setLastCostQty(finCostCalculate.getQuantity());
        finCostAccount.setUpdateBy(operatorName);
        finCostAccountMapper.updateFinCostAccount(finCostAccount);

        if ("fifo".equalsIgnoreCase(costMethod))
        {
            FinCostLayer finCostLayer = new FinCostLayer();
            finCostLayer.setCostAccountId(finCostAccount.getCostAccountId());
            finCostLayer.setProductId(finCostAccount.getProductId());
            finCostLayer.setWarehouseId(finCostAccount.getWarehouseId());
            finCostLayer.setQuantity(finCostCalculate.getQuantity());
            finCostLayer.setRemainingQty(finCostCalculate.getQuantity());
            finCostLayer.setPrice(inboundPrice);
            finCostLayer.setAmount(inboundAmount);
            finCostLayer.setCreateBy(operatorName);
            finCostLayerMapper.insertFinCostLayer(finCostLayer);
        }

        FinCostLog finCostLog = buildCostLog(finCostCalculate, finCostAccount.getCostAccountId(), inboundPrice, inboundAmount, costMethod, operatorName);
        finCostLogMapper.insertFinCostLog(finCostLog);
        return finCostLog;
    }

    private FinCostLog handleOutbound(FinCostCalculate finCostCalculate, FinCostAccount finCostAccount, String costMethod, String operatorName)
    {
        BigDecimal beforeQty = getSafeDecimal(finCostAccount.getTotalQty());
        BigDecimal beforeAmount = getSafeDecimal(finCostAccount.getTotalAmount());
        BigDecimal outQty = finCostCalculate.getQuantity();
        BigDecimal outboundPrice;
        BigDecimal outboundAmount;
        if ("fifo".equalsIgnoreCase(costMethod))
        {
            CostConsumeResult costConsumeResult = consumeFifoLayers(outQty, finCostAccount.getCostAccountId());
            BigDecimal remainingQuantity = costConsumeResult.getRemainingQuantity();
            BigDecimal costAmountFromLayers = costConsumeResult.getConsumedAmount();
            BigDecimal fallbackPrice = getSafeDecimal(finCostAccount.getAvgCost());
            if (fallbackPrice.compareTo(BigDecimal.ZERO) == 0)
            {
                fallbackPrice = getSafeDecimal(finCostCalculate.getPrice());
            }
            BigDecimal remainingAmount = remainingQuantity.multiply(fallbackPrice);
            outboundAmount = costAmountFromLayers.add(remainingAmount);
            outboundPrice = calculateAverageCost(outQty, outboundAmount);
        }
        else
        {
            if (beforeQty.compareTo(BigDecimal.ZERO) > 0 && outQty.compareTo(beforeQty) == 0)
            {
                outboundAmount = beforeAmount;
                outboundPrice = calculateAverageCost(outQty, outboundAmount);
            }
            else if (beforeQty.compareTo(BigDecimal.ZERO) > 0 && outQty.compareTo(beforeQty) > 0)
            {
                // 超卖/负库存场景按当前移动均价或兜底单价核算，禁止用 beforeAmount / outQty 稀释成本单价
                outboundPrice = getSafeDecimal(finCostAccount.getAvgCost());
                if (outboundPrice.compareTo(BigDecimal.ZERO) <= 0)
                {
                    outboundPrice = getSafeDecimal(finCostCalculate.getPrice());
                }
                BigDecimal excessQty = outQty.subtract(beforeQty);
                outboundAmount = beforeAmount.add(excessQty.multiply(outboundPrice)).setScale(2, RoundingMode.HALF_UP);
            }
            else
            {
                outboundPrice = getSafeDecimal(finCostAccount.getAvgCost());
                if (outboundPrice.compareTo(BigDecimal.ZERO) == 0)
                {
                    outboundPrice = getSafeDecimal(finCostCalculate.getPrice());
                }
                outboundAmount = outQty.multiply(outboundPrice);
            }
        }

        BigDecimal totalQuantity = beforeQty.subtract(outQty);
        BigDecimal totalAmount;
        if (totalQuantity.compareTo(BigDecimal.ZERO) <= 0)
        {
            totalQuantity = BigDecimal.ZERO;
            totalAmount = BigDecimal.ZERO;
        }
        else
        {
            totalAmount = beforeAmount.subtract(outboundAmount);
            if (totalAmount.compareTo(BigDecimal.ZERO) < 0)
            {
                totalAmount = BigDecimal.ZERO;
            }
        }
        finCostAccount.setTotalQty(totalQuantity);
        finCostAccount.setTotalAmount(totalAmount);
        finCostAccount.setAvgCost(calculateAverageCost(totalQuantity, totalAmount));
        finCostAccount.setUpdateBy(operatorName);
        finCostAccountMapper.updateFinCostAccount(finCostAccount);

        FinCostLog finCostLog = buildCostLog(finCostCalculate, finCostAccount.getCostAccountId(), outboundPrice, outboundAmount, costMethod, operatorName);
        finCostLogMapper.insertFinCostLog(finCostLog);
        return finCostLog;
    }

    private CostConsumeResult consumeFifoLayers(BigDecimal requiredQuantity, Long costAccountId)
    {
        BigDecimal remainingQuantity = requiredQuantity;
        BigDecimal consumedAmount = BigDecimal.ZERO;
        List<FinCostLayer> costLayerList = finCostLayerMapper.selectAvailableCostLayers(costAccountId);
        for (int layerIndex = 0; layerIndex < costLayerList.size(); layerIndex++)
        {
            if (remainingQuantity.compareTo(BigDecimal.ZERO) <= 0)
            {
                break;
            }
            FinCostLayer finCostLayer = costLayerList.get(layerIndex);
            BigDecimal layerRemainingQuantity = getSafeDecimal(finCostLayer.getRemainingQty());
            if (layerRemainingQuantity.compareTo(BigDecimal.ZERO) <= 0)
            {
                continue;
            }
            BigDecimal consumeQuantity = layerRemainingQuantity.min(remainingQuantity);
            BigDecimal layerAmount = consumeQuantity.multiply(getSafeDecimal(finCostLayer.getPrice()));
            consumedAmount = consumedAmount.add(layerAmount);
            BigDecimal newRemainingQuantity = layerRemainingQuantity.subtract(consumeQuantity);
            finCostLayer.setRemainingQty(newRemainingQuantity);
            finCostLayerMapper.updateFinCostLayer(finCostLayer);
            remainingQuantity = remainingQuantity.subtract(consumeQuantity);
        }
        return new CostConsumeResult(remainingQuantity, consumedAmount);
    }

    private String resolveOperator()
    {
        try
        {
            String username = SecurityUtils.getUsername();
            return StringUtils.isEmpty(username) ? "system" : username;
        }
        catch (Exception ex)
        {
            return "system";
        }
    }

    private FinCostAccount buildDefaultCostAccount(FinCostCalculate finCostCalculate, String costMethod, String operatorName)
    {
        FinCostAccount finCostAccount = new FinCostAccount();
        finCostAccount.setProductId(finCostCalculate.getProductId());
        finCostAccount.setWarehouseId(finCostCalculate.getWarehouseId());
        finCostAccount.setCostMethod(costMethod);
        finCostAccount.setTotalQty(BigDecimal.ZERO);
        finCostAccount.setTotalAmount(BigDecimal.ZERO);
        finCostAccount.setAvgCost(BigDecimal.ZERO);
        finCostAccount.setLastCostPrice(BigDecimal.ZERO);
        finCostAccount.setLastCostQty(BigDecimal.ZERO);
        finCostAccount.setCreateBy(operatorName);
        return finCostAccount;
    }

    private FinCostLog buildCostLog(FinCostCalculate finCostCalculate, Long costAccountId, BigDecimal price, BigDecimal amount, String costMethod, String operatorName)
    {
        FinCostLog finCostLog = new FinCostLog();
        finCostLog.setCostAccountId(costAccountId);
        finCostLog.setProductId(finCostCalculate.getProductId());
        finCostLog.setWarehouseId(finCostCalculate.getWarehouseId());
        finCostLog.setBillType(finCostCalculate.getBillType());
        finCostLog.setBillId(finCostCalculate.getBillId());
        finCostLog.setInOut(finCostCalculate.getInOut());
        finCostLog.setQuantity(finCostCalculate.getQuantity());
        finCostLog.setPrice(price);
        finCostLog.setAmount(amount);
        finCostLog.setCostMethod(costMethod);
        finCostLog.setCreateBy(operatorName);
        return finCostLog;
    }

    private BigDecimal getSafeDecimal(BigDecimal sourceDecimal)
    {
        return sourceDecimal == null ? BigDecimal.ZERO : sourceDecimal;
    }

    private BigDecimal calculateAverageCost(BigDecimal quantity, BigDecimal amount)
    {
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0)
        {
            return BigDecimal.ZERO;
        }
        if (amount == null)
        {
            return BigDecimal.ZERO;
        }
        return amount.divide(quantity, 2, RoundingMode.HALF_UP);
    }

    private static class CostConsumeResult
    {
        private final BigDecimal remainingQuantity;
        private final BigDecimal consumedAmount;

        private CostConsumeResult(BigDecimal remainingQuantity, BigDecimal consumedAmount)
        {
            this.remainingQuantity = remainingQuantity;
            this.consumedAmount = consumedAmount;
        }

        public BigDecimal getRemainingQuantity()
        {
            return remainingQuantity;
        }

        public BigDecimal getConsumedAmount()
        {
            return consumedAmount;
        }
    }
}
