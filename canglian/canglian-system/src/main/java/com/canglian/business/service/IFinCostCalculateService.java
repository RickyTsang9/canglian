package com.canglian.business.service;

import java.math.BigDecimal;
import java.util.Map;
import com.canglian.business.domain.FinCostCalculate;
import com.canglian.business.domain.FinCostLog;

/**
 * 成本核算 服务层
 *
 * @author canglian
 */
public interface IFinCostCalculateService
{
    /**
     * 计算单笔出入库成本
     */
    public FinCostLog calculateCost(FinCostCalculate finCostCalculate);

    /**
     * 纯内存沙箱模拟测算单笔出入库成本（只读，不修改成本台账、成本层或审计流水）
     */
    public Map<String, Object> simulateCost(FinCostCalculate finCostCalculate);

    /**
     * 查询当前全局成本核算算法配置与历史流水状态
     */
    public Map<String, Object> getGlobalCostConfig();

    /**
     * 更新全局成本核算算法（存在历史成本流水时禁止跨法切换）
     */
    public Map<String, Object> updateGlobalCostMethod(String costMethod, String operator);

    /**
     * 获取全局默认成本算法（moving / fifo）
     */
    public String getGlobalCostMethod();

    /**
     * 解析销售退货/出库退货应当回补的真实单位成本（避免误将售价计入库存成本）
     */
    public BigDecimal resolveReturnUnitCost(Long warehouseId, Long productId, String sourceBillType, Long sourceBillId, BigDecimal fallbackPrice);

    /**
     * 按库存流水时间序重算未结账期库存成本台账、成本层与成本流水
     */
    public Map<String, Object> recalculateCost(Long warehouseId, Long productId, String operator);

    /**
     * 受控成本调价（记录调整流水与凭证事件，需会计期间未结账且写明调整原因）
     */
    public FinCostLog adjustCost(Long warehouseId, Long productId, BigDecimal newAvgCost, String reason, String operator);

    /**
     * 按原入库精确单位成本扣回成本台账/成本层（用于入库反审核/红冲，避免稀释加权均价或错扣旧FIFO层）
     */
    public FinCostLog calculateExactInboundReversal(Long warehouseId, Long productId, Long billId, String billType, BigDecimal quantity, BigDecimal exactUnitCost);

    /**
     * 跨仓调拨成本结转：从调出仓扣减成本并按同单位成本结转至调入仓，返回结转单位成本
     */
    public BigDecimal calculateTransferCost(Long outWarehouseId, Long inWarehouseId, Long productId, Long transferId, String outBillType, String inBillType, BigDecimal quantity);

    /**
     * 盘盈/盘亏成本核算（盘盈按均价/参考成本入账，盘亏按当前库存成本出账）
     */
    public FinCostLog calculateInventoryCheckCost(Long warehouseId, Long productId, Long checkId, String billType, String inOut, BigDecimal quantity, BigDecimal fallbackUnitCost);

    /**
     * 汇总指定出库单的实际结转销售成本(COGS)总额
     */
    public BigDecimal resolveOutboundCogsAmount(Long outboundId);
}
