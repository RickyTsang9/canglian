package com.canglian.web.controller.business;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.business.domain.FinAgingReport;
import com.canglian.business.domain.FinCostStructureReport;
import com.canglian.business.domain.FinProfitLossReport;
import com.canglian.business.domain.FinReconciliationReport;
import com.canglian.business.domain.FinRevenueExpenseReport;
import com.canglian.business.service.IFinReportService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.enums.BusinessType;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.poi.ExcelUtil;

/**
 * 财务与经营报表 信息操作处理
 *
 * @author canglian
 */
@RestController
@RequestMapping("/business/report")
public class FinReportController extends BaseController
{
    @Autowired
    private IFinReportService finReportService;

    @PreAuthorize("@ss.hasPermi('business:report:query')")
    @GetMapping("/receivable/aging")
    public AjaxResult receivableAging(@RequestParam(required = false) Long customerId)
    {
        return success(finReportService.selectReceivableAgingReport(customerId));
    }

    @PreAuthorize("@ss.hasPermi('business:report:query')")
    @GetMapping("/payable/aging")
    public AjaxResult payableAging(@RequestParam(required = false) Long supplierId)
    {
        return success(finReportService.selectPayableAgingReport(supplierId));
    }

    @PreAuthorize("@ss.hasPermi('business:report:query')")
    @GetMapping("/receivable/reconciliation")
    public AjaxResult receivableReconciliation(@RequestParam(required = false) Long customerId)
    {
        return success(finReportService.selectReceivableReconciliationReport(customerId));
    }

    @PreAuthorize("@ss.hasPermi('business:report:query')")
    @GetMapping("/payable/reconciliation")
    public AjaxResult payableReconciliation(@RequestParam(required = false) Long supplierId)
    {
        return success(finReportService.selectPayableReconciliationReport(supplierId));
    }

    @PreAuthorize("@ss.hasPermi('business:report:query')")
    @GetMapping("/profitLoss")
    public AjaxResult profitLoss(@RequestParam String startDate, @RequestParam String endDate)
    {
        return success(finReportService.selectProfitLossReport(startDate, endDate));
    }

    @PreAuthorize("@ss.hasPermi('business:report:query')")
    @GetMapping("/revenueExpense")
    public AjaxResult revenueExpense(@RequestParam String startDate, @RequestParam String endDate)
    {
        return success(finReportService.selectRevenueExpenseReport(startDate, endDate));
    }

    @PreAuthorize("@ss.hasPermi('business:report:query')")
    @GetMapping("/costStructure")
    public AjaxResult costStructure(@RequestParam String startDate, @RequestParam String endDate)
    {
        return success(finReportService.selectCostStructureReport(startDate, endDate));
    }

    @PreAuthorize("@ss.hasPermi('business:report:query')")
    @GetMapping("/operation/salesGrossProfit")
    public AjaxResult salesGrossProfit(@RequestParam String startDate, @RequestParam String endDate)
    {
        return success(finReportService.selectSalesGrossProfitReport(startDate, endDate));
    }

    @PreAuthorize("@ss.hasPermi('business:report:query')")
    @GetMapping("/operation/customerContribution")
    public AjaxResult customerContribution(@RequestParam String startDate, @RequestParam String endDate)
    {
        return success(finReportService.selectCustomerContributionReport(startDate, endDate));
    }

    @PreAuthorize("@ss.hasPermi('business:report:query')")
    @GetMapping("/operation/productTurnover")
    public AjaxResult productTurnover(@RequestParam String startDate, @RequestParam String endDate)
    {
        return success(finReportService.selectProductTurnoverReport(startDate, endDate));
    }

    @PreAuthorize("@ss.hasPermi('business:report:query')")
    @GetMapping("/operation/stockBalance")
    public AjaxResult stockBalance()
    {
        return success(finReportService.selectStockBalanceReport());
    }

    @PreAuthorize("@ss.hasPermi('business:report:query')")
    @GetMapping("/operation/fundAnalysis")
    public AjaxResult fundAnalysis(@RequestParam String startDate, @RequestParam String endDate)
    {
        return success(finReportService.selectFundAnalysisReport(startDate, endDate));
    }

    /**
     * 统一财务与经营报表 Excel 导出
     */
    @PreAuthorize("@ss.hasPermi('business:report:export')")
    @Log(title = "财务经营报表导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void exportReport(
        HttpServletResponse response,
        @RequestParam String reportType,
        @RequestParam(required = false) Long customerId,
        @RequestParam(required = false) Long supplierId,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate) throws IOException
    {
        switch (reportType)
        {
            case "receivableAging":
                new ExcelUtil<FinAgingReport>(FinAgingReport.class)
                    .exportExcel(response, finReportService.selectReceivableAgingReport(customerId), "应收账龄报表");
                break;
            case "payableAging":
                new ExcelUtil<FinAgingReport>(FinAgingReport.class)
                    .exportExcel(response, finReportService.selectPayableAgingReport(supplierId), "应付账龄报表");
                break;
            case "receivableReconciliation":
                new ExcelUtil<FinReconciliationReport>(FinReconciliationReport.class)
                    .exportExcel(response, finReportService.selectReceivableReconciliationReport(customerId), "应收对账报表");
                break;
            case "payableReconciliation":
                new ExcelUtil<FinReconciliationReport>(FinReconciliationReport.class)
                    .exportExcel(response, finReportService.selectPayableReconciliationReport(supplierId), "应付对账报表");
                break;
            case "profitLoss":
                FinProfitLossReport pl = finReportService.selectProfitLossReport(startDate, endDate);
                new ExcelUtil<FinProfitLossReport>(FinProfitLossReport.class)
                    .exportExcel(response, Collections.singletonList(pl), "经营利润表");
                break;
            case "revenueExpense":
                FinRevenueExpenseReport re = finReportService.selectRevenueExpenseReport(startDate, endDate);
                new ExcelUtil<FinRevenueExpenseReport>(FinRevenueExpenseReport.class)
                    .exportExcel(response, Collections.singletonList(re), "现金收支汇总表");
                break;
            case "costStructure":
                new ExcelUtil<FinCostStructureReport>(FinCostStructureReport.class)
                    .exportExcel(response, finReportService.selectCostStructureReport(startDate, endDate), "成本结构分析");
                break;
            case "salesGrossProfit":
                exportMapList(response, "销售毛利报表",
                    new String[] { "商品编码", "商品名称", "销售数量", "退货数量", "净销量", "销售金额", "退货金额", "净销售额", "真实成本", "毛利", "毛利率(%)" },
                    new String[] { "productCode", "productName", "saleQty", "returnQty", "netSaleQty", "saleAmount", "returnAmount", "netSaleAmount", "costAmount", "grossProfit", "grossMarginRate" },
                    finReportService.selectSalesGrossProfitReport(startDate, endDate));
                break;
            case "customerContribution":
                exportMapList(response, "客户贡献报表",
                    new String[] { "客户编码", "客户名称", "订单数", "销售金额", "已收贡献", "未收金额" },
                    new String[] { "customerCode", "customerName", "orderCount", "saleAmount", "receivedContribution", "unpaidAmount" },
                    finReportService.selectCustomerContributionReport(startDate, endDate));
                break;
            case "productTurnover":
                exportMapList(response, "商品周转报表",
                    new String[] { "商品编码", "商品名称", "入库数量", "出库数量", "当前库存", "周转率" },
                    new String[] { "productCode", "productName", "inboundQty", "outboundQty", "stockQty", "turnoverRate" },
                    finReportService.selectProductTurnoverReport(startDate, endDate));
                break;
            case "stockBalance":
                exportMapList(response, "库存余额报表",
                    new String[] { "仓库名称", "库位名称", "商品编码", "商品名称", "批次号", "库存数量", "锁定数量", "可用数量", "库存金额" },
                    new String[] { "warehouseName", "locationName", "productCode", "productName", "batchNo", "stockQty", "lockedQty", "availableQty", "stockAmount" },
                    finReportService.selectStockBalanceReport());
                break;
            case "fundAnalysis":
                exportMapList(response, "资金分析报表",
                    new String[] { "资金类型", "类型编码", "金额" },
                    new String[] { "fundTypeName", "fundType", "amount" },
                    finReportService.selectFundAnalysisReport(startDate, endDate));
                break;
            default:
                throw new ServiceException("不支持导出的报表类型：" + reportType);
        }
    }

    private void exportMapList(HttpServletResponse response, String sheetName, String[] headers, String[] keys, List<Map<String, Object>> rows) throws IOException
    {
        try (XSSFWorkbook workbook = new XSSFWorkbook())
        {
            Sheet sheet = workbook.createSheet(sheetName);
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++)
            {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, Math.max(headers[i].length() * 600, 4000));
            }
            if (rows != null)
            {
                for (int r = 0; r < rows.size(); r++)
                {
                    Map<String, Object> rowMap = rows.get(r);
                    Row row = sheet.createRow(r + 1);
                    for (int c = 0; c < keys.length; c++)
                    {
                        Cell cell = row.createCell(c);
                        Object val = rowMap == null ? null : rowMap.get(keys[c]);
                        if (val == null)
                        {
                            cell.setCellValue("");
                        }
                        else if (val instanceof BigDecimal)
                        {
                            cell.setCellValue(((BigDecimal) val).doubleValue());
                        }
                        else if (val instanceof Number)
                        {
                            cell.setCellValue(((Number) val).doubleValue());
                        }
                        else
                        {
                            cell.setCellValue(String.valueOf(val));
                        }
                    }
                }
            }
            String encoded = URLEncoder.encode(sheetName + ".xlsx", StandardCharsets.UTF_8.name()).replace("+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encoded + "\"");
            workbook.write(response.getOutputStream());
            response.getOutputStream().flush();
        }
    }
}
