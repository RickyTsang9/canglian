package com.canglian.web.controller.business;

import java.util.List;
import com.canglian.common.utils.poi.ExcelUtil;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.business.domain.FinReceipt;
import com.canglian.business.service.IFinReceiptService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;

/**
 * 收款单 信息操作处理
 *
 * @author canglian
 */
@RestController
@RequestMapping("/business/receipt")
public class FinReceiptController extends BaseController
{
    @Autowired
    private IFinReceiptService finReceiptService;

    @PreAuthorize("@ss.hasPermi('business:receipt:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinReceipt finReceipt)
    {
        startPage();
        List<FinReceipt> list = finReceiptService.selectFinReceiptList(finReceipt);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('business:receipt:query')")
    @GetMapping(value = "/{receiptId}")
    public AjaxResult getInfo(@PathVariable Long receiptId)
    {
        return success(finReceiptService.selectFinReceiptById(receiptId));
    }

    @PreAuthorize("@ss.hasPermi('business:receipt:add')")
    @Log(title = "收款单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinReceipt finReceipt)
    {
        finReceipt.setCreateBy(getUsername());
        return toAjax(finReceiptService.insertFinReceipt(finReceipt));
    }

    @PreAuthorize("@ss.hasPermi('business:receipt:edit')")
    @Log(title = "收款单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinReceipt finReceipt)
    {
        finReceipt.setUpdateBy(getUsername());
        return toAjax(finReceiptService.updateFinReceipt(finReceipt));
    }

    @PreAuthorize("@ss.hasPermi('business:receipt:confirm') or @ss.hasPermi('business:receipt:edit')")
    @Log(title = "收款单确认", businessType = BusinessType.UPDATE)
    @PostMapping("/confirm/{receiptId}")
    public AjaxResult confirm(@PathVariable Long receiptId)
    {
        return toAjax(finReceiptService.confirmFinReceipt(receiptId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:receipt:confirm') or @ss.hasPermi('business:receipt:edit')")
    @Log(title = "收款单反确认", businessType = BusinessType.UPDATE)
    @PostMapping("/unconfirm/{receiptId}")
    public AjaxResult unconfirm(@PathVariable Long receiptId)
    {
        return toAjax(finReceiptService.unconfirmFinReceipt(receiptId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:receipt:remove')")
    @Log(title = "收款单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{receiptIds}")
    public AjaxResult remove(@PathVariable Long[] receiptIds)
    {
        return toAjax(finReceiptService.deleteFinReceiptByIds(receiptIds));
    }

    /**
     * 导出收款单
     */
    @Log(title = "收款单", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('business:receipt:export') or @ss.hasPermi('business:receipt:list')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinReceipt finReceipt)
    {
        List<FinReceipt> list = finReceiptService.selectFinReceiptList(finReceipt);
        ExcelUtil<FinReceipt> util = new ExcelUtil<FinReceipt>(FinReceipt.class);
        util.exportExcel(response, list, "收款单");
    }
}
