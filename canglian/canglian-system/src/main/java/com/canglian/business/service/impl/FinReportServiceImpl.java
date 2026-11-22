package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private static final BigDecimal HUNDRED = new BigDecimal("100");

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
        FinProfitLossReport report = finReportMapper.selectProfitLossReport(startDate, endDate);
        if (report == null)
        {
            report = new FinProfitLossReport();
        }
        BigDecimal salesIncome = safe(report.getSalesIncomeAmount());
        BigDecimal saleReturn = safe(report.getSaleReturnAmount());
        BigDecimal netIncome = salesIncome.subtract(saleReturn).setScale(2, RoundingMode.HALF_UP);
        BigDecimal salesCost = safe(report.getSalesCostAmount()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal grossProfit = netIncome.subtract(salesCost).setScale(2, RoundingMode.HALF_UP);
        BigDecimal expense = safe(report.getExpenseAmount()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal badDebt = safe(report.getBadDebtAmount()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal netProfit = grossProfit.subtract(expense).subtract(badDebt).setScale(2, RoundingMode.HALF_UP);

        BigDecimal grossMarginRate = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal netMarginRate = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        if (netIncome.compareTo(BigDecimal.ZERO) != 0)
        {
            grossMarginRate = grossProfit.multiply(HUNDRED).divide(netIncome, 2, RoundingMode.HALF_UP);
            netMarginRate = netProfit.multiply(HUNDRED).divide(netIncome, 2, RoundingMode.HALF_UP);
        }

        report.setSalesIncomeAmount(salesIncome.setScale(2, RoundingMode.HALF_UP));
        report.setSaleReturnAmount(saleReturn.setScale(2, RoundingMode.HALF_UP));
        report.setNetIncomeAmount(netIncome);
        report.setSalesCostAmount(salesCost);
        report.setGrossProfitAmount(grossProfit);
        report.setGrossMarginRate(grossMarginRate);
        report.setExpenseAmount(expense);
        report.setBadDebtAmount(badDebt);
        report.setNetProfitAmount(netProfit);
        report.setNetMarginRate(netMarginRate);

        // 兼容旧字段
        report.setIncomeAmount(netIncome);
        report.setCostAmount(salesCost.add(expense).add(badDebt).setScale(2, RoundingMode.HALF_UP));
        report.setProfitAmount(netProfit);
        return report;
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

    @Override
    public List<Map<String, Object>> selectSalesGrossProfitReport(String startDate, String endDate)
    {
        return finReportMapper.selectSalesGrossProfitReport(startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> selectCustomerContributionReport(String startDate, String endDate)
    {
        return finReportMapper.selectCustomerContributionReport(startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> selectProductTurnoverReport(String startDate, String endDate)
    {
        return finReportMapper.selectProductTurnoverReport(startDate, endDate);
    }

    @Override
    public List<Map<String, Object>> selectStockBalanceReport()
    {
        return finReportMapper.selectStockBalanceReport();
    }

    @Override
    public List<Map<String, Object>> selectFundAnalysisReport(String startDate, String endDate)
    {
        return finReportMapper.selectFundAnalysisReport(startDate, endDate);
    }

    private BigDecimal safe(BigDecimal value)
    {
        return value == null ? BigDecimal.ZERO : value;
    }
}
