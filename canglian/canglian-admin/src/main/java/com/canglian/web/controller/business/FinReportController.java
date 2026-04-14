package com.canglian.web.controller.business;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.business.domain.FinAgingReport;
import com.canglian.business.domain.FinReconciliationReport;
import com.canglian.business.domain.FinProfitLossReport;
import com.canglian.business.domain.FinRevenueExpenseReport;
import com.canglian.business.domain.FinCostStructureReport;
import com.canglian.business.service.IFinReportService;

/**
 * 财务报表 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/report")
public class FinReportController extends BaseController
{
    @Autowired
    private IFinReportService finReportService;

    /**
     * 查询应收账龄报表
     */
    @PreAuthorize("@ss.hasPermi('business:report:query')")
    @GetMapping("/receivable/aging")
    public AjaxResult receivableAging(@RequestParam(required = false) Long customerId)
    {
        List<FinAgingReport> list = finReportService.selectReceivableAgingReport(customerId);
        return success(list);
    }

    /**
     * 查询应付账龄报表
     */
    @PreAuthorize("@ss.hasPermi('business:report:query')")
    @GetMapping("/payable/aging")
    public AjaxResult payableAging(@RequestParam(required = false) Long supplierId)
    {
        List<FinAgingReport> list = finReportService.selectPayableAgingReport(supplierId);
        return success(list);
    }

    /**
     * 查询应收对账报表
     */
    @PreAuthorize("@ss.hasPermi('business:report:query')")
    @GetMapping("/receivable/reconciliation")
    public AjaxResult receivableReconciliation(@RequestParam(required = false) Long customerId)
    {
        List<FinReconciliationReport> list = finReportService.selectReceivableReconciliationReport(customerId);
        return success(list);
    }

    /**
     * 查询应付对账报表
     */
    @PreAuthorize("@ss.hasPermi('business:report:query')")
    @GetMapping("/payable/reconciliation")
    public AjaxResult payableReconciliation(@RequestParam(required = false) Long supplierId)
    {
        List<FinReconciliationReport> list = finReportService.selectPayableReconciliationReport(supplierId);
        return success(list);
    }

    /**
     * 查询利润表
     */
    @PreAuthorize("@ss.hasPermi('business:report:query')")
    @GetMapping("/profitLoss")
    public AjaxResult profitLoss(@RequestParam String startDate, @RequestParam String endDate)
    {
        FinProfitLossReport data = finReportService.selectProfitLossReport(startDate, endDate);
        return success(data);
    }

    /**
     * 查询收支汇总
     */
    @PreAuthorize("@ss.hasPermi('business:report:query')")
    @GetMapping("/revenueExpense")
    public AjaxResult revenueExpense(@RequestParam String startDate, @RequestParam String endDate)
    {
        FinRevenueExpenseReport data = finReportService.selectRevenueExpenseReport(startDate, endDate);
        return success(data);
    }

    /**
     * 查询成本结构分析
     */
    @PreAuthorize("@ss.hasPermi('business:report:query')")
    @GetMapping("/costStructure")
    public AjaxResult costStructure(@RequestParam String startDate, @RequestParam String endDate)
    {
        List<FinCostStructureReport> list = finReportService.selectCostStructureReport(startDate, endDate);
        return success(list);
    }
}

