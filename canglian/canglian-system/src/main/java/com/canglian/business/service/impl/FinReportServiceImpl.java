package com.canglian.business.service.impl;

import java.util.List;
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
}

