package com.canglian.business.mapper;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.canglian.business.domain.FinAgingReport;
import com.canglian.business.domain.FinReconciliationReport;
import com.canglian.business.domain.FinProfitLossReport;
import com.canglian.business.domain.FinRevenueExpenseReport;
import com.canglian.business.domain.FinCostStructureReport;

/**
 * 财务报表 数据层
 *
 * @author canglian
 */
public interface FinReportMapper
{
    /**
     * 查询应收账龄报表
     *
     * @param customerId 客户id
     * @return 账龄报表集合
     */
    public List<FinAgingReport> selectReceivableAgingReport(@Param("customerId") Long customerId);

    /**
     * 查询应付账龄报表
     *
     * @param supplierId 供应商id
     * @return 账龄报表集合
     */
    public List<FinAgingReport> selectPayableAgingReport(@Param("supplierId") Long supplierId);

    /**
     * 查询应收对账报表
     *
     * @param customerId 客户id
     * @return 对账报表集合
     */
    public List<FinReconciliationReport> selectReceivableReconciliationReport(@Param("customerId") Long customerId);

    /**
     * 查询应付对账报表
     *
     * @param supplierId 供应商id
     * @return 对账报表集合
     */
    public List<FinReconciliationReport> selectPayableReconciliationReport(@Param("supplierId") Long supplierId);

    /**
     * 查询利润表
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 利润表
     */
    public FinProfitLossReport selectProfitLossReport(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 查询收支汇总
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 收支汇总
     */
    public FinRevenueExpenseReport selectRevenueExpenseReport(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 查询成本结构分析
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 成本结构集合
     */
    public List<FinCostStructureReport> selectCostStructureReport(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 查询销售毛利报表
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 销售毛利集合
     */
    public List<Map<String, Object>> selectSalesGrossProfitReport(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 查询客户贡献报表
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 客户贡献集合
     */
    public List<Map<String, Object>> selectCustomerContributionReport(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 查询商品周转报表
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 商品周转集合
     */
    public List<Map<String, Object>> selectProductTurnoverReport(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 查询库存余额报表
     *
     * @return 库存余额集合
     */
    public List<Map<String, Object>> selectStockBalanceReport();

    /**
     * 查询资金分析报表
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 资金分析集合
     */
    public List<Map<String, Object>> selectFundAnalysisReport(@Param("startDate") String startDate, @Param("endDate") String endDate);
}

