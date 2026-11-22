package com.canglian.web.controller.business;

import java.util.List;
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
import com.canglian.business.domain.FinPayment;
import com.canglian.business.service.IFinPaymentService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;

/**
 * 付款单 信息操作处理
 *
 * @author canglian
 */
@RestController
@RequestMapping("/business/payment")
public class FinPaymentController extends BaseController
{
    @Autowired
    private IFinPaymentService finPaymentService;

    @PreAuthorize("@ss.hasPermi('business:payment:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinPayment finPayment)
    {
        startPage();
        List<FinPayment> list = finPaymentService.selectFinPaymentList(finPayment);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('business:payment:query')")
    @GetMapping(value = "/{paymentId}")
    public AjaxResult getInfo(@PathVariable Long paymentId)
    {
        return success(finPaymentService.selectFinPaymentById(paymentId));
    }

    @PreAuthorize("@ss.hasPermi('business:payment:add')")
    @Log(title = "付款单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinPayment finPayment)
    {
        finPayment.setCreateBy(getUsername());
        return toAjax(finPaymentService.insertFinPayment(finPayment));
    }

    @PreAuthorize("@ss.hasPermi('business:payment:edit')")
    @Log(title = "付款单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinPayment finPayment)
    {
        finPayment.setUpdateBy(getUsername());
        return toAjax(finPaymentService.updateFinPayment(finPayment));
    }

    @PreAuthorize("@ss.hasPermi('business:payment:confirm') or @ss.hasPermi('business:payment:edit')")
    @Log(title = "付款单确认", businessType = BusinessType.UPDATE)
    @PostMapping("/confirm/{paymentId}")
    public AjaxResult confirm(@PathVariable Long paymentId)
    {
        return toAjax(finPaymentService.confirmFinPayment(paymentId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:payment:confirm') or @ss.hasPermi('business:payment:edit')")
    @Log(title = "付款单反确认", businessType = BusinessType.UPDATE)
    @PostMapping("/unconfirm/{paymentId}")
    public AjaxResult unconfirm(@PathVariable Long paymentId)
    {
        return toAjax(finPaymentService.unconfirmFinPayment(paymentId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:payment:remove')")
    @Log(title = "付款单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{paymentIds}")
    public AjaxResult remove(@PathVariable Long[] paymentIds)
    {
        return toAjax(finPaymentService.deleteFinPaymentByIds(paymentIds));
    }
}
