package com.canglian.business.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinAgingReport;
import com.canglian.business.domain.FinCostStructureReport;
import com.canglian.business.domain.FinProfitLossReport;
import com.canglian.business.domain.FinReconciliationReport;
import com.canglian.business.domain.FinRevenueExpenseReport;
import com.canglian.business.mapper.FinReportMapper;
import com.canglian.business.service.IFinReportService;

@Service
public class FinReportServiceImpl implements IFinReportService
{
    @Autowired
    private FinReportMapper finReportMapper;

    @Override
    public List<FinAgingReport> selectReceivableAgingReport(Long customerId)
    {
        return finReportMapper.selectReceivableAgingReport(customerId);
    }

    @Override
    public List<FinAgingReport> selectPayableAgingReport(Long supplierId)
    {
        return finReportMapper.selectPayableAgingReport(supplierId);
    }

    @Override
    public List<FinReconciliationReport> selectReceivableReconciliationReport(Long customerId)
    {
        return finReportMapper.selectReceivableReconciliationReport(customerId);
    }

    @Override
    public List<FinReconciliationReport> selectPayableReconciliationReport(Long supplierId)
    {
        return finReportMapper.selectPayableReconciliationReport(supplierId);
    }

    @Override
    public FinProfitLossReport selectProfitLossReport(String startDate, String endDate)
    {
        return finReportMapper.selectProfitLossReport(startDate, endDate);
    }

    @Override
    public FinRevenueExpenseReport selectRevenueExpenseReport(String startDate, String endDate)
    {
        return finReportMapper.selectRevenueExpenseReport(startDate, endDate);
    }

    @Override
    public List<FinCostStructureReport> selectCostStructureReport(String startDate, String endDate)
    {
        return finReportMapper.selectCostStructureReport(startDate, endDate);
    }

    /**
     * 查询销售毛利报表
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 销售毛利集合
     */
    @Override
    public List<Map<String, Object>> selectSalesGrossProfitReport(String startDate, String endDate)
    {
        return finReportMapper.selectSalesGrossProfitReport(startDate, endDate);
    }

    /**
     * 查询客户贡献报表
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 客户贡献集合
     */
    @Override
    public List<Map<String, Object>> selectCustomerContributionReport(String startDate, String endDate)
    {
        return finReportMapper.selectCustomerContributionReport(startDate, endDate);
    }

    /**
     * 查询商品周转报表
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 商品周转集合
     */
    @Override
    public List<Map<String, Object>> selectProductTurnoverReport(String startDate, String endDate)
    {
        return finReportMapper.selectProductTurnoverReport(startDate, endDate);
    }

    /**
     * 查询库存余额报表
     *
     * @return 库存余额集合
     */
    @Override
    public List<Map<String, Object>> selectStockBalanceReport()
    {
        return finReportMapper.selectStockBalanceReport();
    }

    /**
     * 查询资金分析报表
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 资金分析集合
     */
    @Override
    public List<Map<String, Object>> selectFundAnalysisReport(String startDate, String endDate)
    {
        return finReportMapper.selectFundAnalysisReport(startDate, endDate);
    }
}

