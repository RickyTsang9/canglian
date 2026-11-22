package com.canglian.web.controller.business;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
import com.canglian.business.domain.FinInvoice;
import com.canglian.business.service.IFinInvoiceService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;

/**
 * 发票登记控制器
 *
 * @author canglian
 */
@RestController
@RequestMapping("/business/invoice")
public class FinInvoiceController extends BaseController
{
    @Autowired
    private IFinInvoiceService finInvoiceService;

    @PreAuthorize("@ss.hasPermi('business:invoice:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinInvoice finInvoice)
    {
        startPage();
        List<FinInvoice> finInvoiceList = finInvoiceService.selectFinInvoiceList(finInvoice);
        return getDataTable(finInvoiceList);
    }

    @PreAuthorize("@ss.hasPermi('business:invoice:query')")
    @GetMapping("/{invoiceId}")
    public AjaxResult getInfo(@PathVariable Long invoiceId)
    {
        return success(finInvoiceService.selectFinInvoiceById(invoiceId));
    }

    @PreAuthorize("@ss.hasPermi('business:invoice:add')")
    @Log(title = "发票登记", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinInvoice finInvoice)
    {
        finInvoice.setCreateBy(getUsername());
        return toAjax(finInvoiceService.insertFinInvoice(finInvoice));
    }

    @PreAuthorize("@ss.hasPermi('business:invoice:edit')")
    @Log(title = "发票登记", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinInvoice finInvoice)
    {
        finInvoice.setUpdateBy(getUsername());
        return toAjax(finInvoiceService.updateFinInvoice(finInvoice));
    }

    @PreAuthorize("@ss.hasPermi('business:invoice:confirm')")
    @Log(title = "发票登记确认", businessType = BusinessType.UPDATE)
    @PutMapping("/confirm/{invoiceId}")
    public AjaxResult confirm(@PathVariable Long invoiceId)
    {
        return toAjax(finInvoiceService.confirmFinInvoice(invoiceId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:invoice:red') or @ss.hasPermi('business:invoice:confirm')")
    @Log(title = "红字发票冲销", businessType = BusinessType.INSERT)
    @PostMapping("/red/{originInvoiceId}")
    public AjaxResult issueRed(@PathVariable Long originInvoiceId, @RequestBody(required = false) Map<String, Object> body)
    {
        Object redObj = body == null ? null : (body.get("redAmount") != null ? body.get("redAmount") : body.get("amount"));
        BigDecimal redAmount = (redObj != null && !"".equals(String.valueOf(redObj)))
            ? new BigDecimal(String.valueOf(redObj)) : null;
        String remark = body != null && body.get("remark") != null ? String.valueOf(body.get("remark")) : null;
        return success(finInvoiceService.issueRedInvoice(originInvoiceId, redAmount, remark, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:invoice:remove')")
    @Log(title = "发票登记", businessType = BusinessType.DELETE)
    @DeleteMapping("/{invoiceIds}")
    public AjaxResult remove(@PathVariable Long[] invoiceIds)
    {
        return toAjax(finInvoiceService.deleteFinInvoiceByIds(invoiceIds));
    }
}
