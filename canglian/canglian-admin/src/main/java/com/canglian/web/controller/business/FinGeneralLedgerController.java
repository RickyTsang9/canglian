package com.canglian.web.controller.business;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.business.domain.FinVoucherTemplate;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.enums.BusinessType;

/**
 * 基础总账接口控制器
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/generalLedger")
public class FinGeneralLedgerController extends BaseController
{
    @Autowired
    private IFinGeneralLedgerService finGeneralLedgerService;

    @PreAuthorize("@ss.hasPermi('business:generalLedger:query')")
    @GetMapping("/subject/list")
    public AjaxResult subjectList(@RequestParam(required = false) String status)
    {
        return success(finGeneralLedgerService.selectAccountSubjectList(status));
    }

    @PreAuthorize("@ss.hasPermi('business:generalLedger:edit')")
    @Log(title = "会计科目", businessType = BusinessType.INSERT)
    @PostMapping("/subject")
    public AjaxResult addSubject(@RequestBody Map<String, Object> subject)
    {
        return toAjax(finGeneralLedgerService.insertAccountSubject(subject, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:generalLedger:edit')")
    @Log(title = "会计科目", businessType = BusinessType.UPDATE)
    @PutMapping("/subject")
    public AjaxResult editSubject(@RequestBody Map<String, Object> subject)
    {
        return toAjax(finGeneralLedgerService.updateAccountSubject(subject, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:generalLedger:edit')")
    @Log(title = "会计科目", businessType = BusinessType.DELETE)
    @DeleteMapping("/subject/{subjectId}")
    public AjaxResult removeSubject(@PathVariable Long subjectId)
    {
        return toAjax(finGeneralLedgerService.deleteAccountSubjectById(subjectId));
    }

    @PreAuthorize("@ss.hasPermi('business:generalLedger:query')")
    @GetMapping("/auxiliary/list")
    public AjaxResult auxiliaryList(@RequestParam(required = false) String auxiliaryType)
    {
        return success(finGeneralLedgerService.selectAuxiliaryItemList(auxiliaryType));
    }

    @PreAuthorize("@ss.hasPermi('business:generalLedger:edit')")
    @Log(title = "辅助核算同步", businessType = BusinessType.UPDATE)
    @PostMapping("/auxiliary/sync")
    public AjaxResult syncAuxiliary()
    {
        return success(finGeneralLedgerService.syncAuxiliaryItems(getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:generalLedger:query')")
    @GetMapping("/period/list")
    public AjaxResult periodList(@RequestParam(required = false) String closeStatus)
    {
        return success(finGeneralLedgerService.selectFiscalPeriodList(closeStatus));
    }

    @PreAuthorize("@ss.hasPermi('business:generalLedger:edit')")
    @Log(title = "初始化年度会计期间", businessType = BusinessType.INSERT)
    @PostMapping("/period/initYear/{year}")
    public AjaxResult initPeriodYear(@PathVariable Integer year)
    {
        return success(finGeneralLedgerService.initFiscalPeriodsByYear(year, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:generalLedger:query')")
    @GetMapping("/period/checkReadiness/{periodId}")
    public AjaxResult checkReadiness(@PathVariable Long periodId)
    {
        return success(finGeneralLedgerService.checkPeriodCloseReadiness(periodId));
    }

    @PreAuthorize("@ss.hasPermi('business:generalLedger:close')")
    @Log(title = "会计期间结账", businessType = BusinessType.UPDATE)
    @PutMapping("/period/close/{periodId}")
    public AjaxResult closePeriod(@PathVariable Long periodId)
    {
        return toAjax(finGeneralLedgerService.closeFiscalPeriod(periodId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:generalLedger:reopen')")
    @Log(title = "会计期间反结账", businessType = BusinessType.UPDATE)
    @PutMapping("/period/reopen/{periodId}")
    public AjaxResult reopenPeriod(@PathVariable Long periodId)
    {
        return toAjax(finGeneralLedgerService.reopenFiscalPeriod(periodId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:generalLedger:query')")
    @GetMapping("/voucherTemplate/list")
    public AjaxResult voucherTemplateList(FinVoucherTemplate template)
    {
        return success(finGeneralLedgerService.selectVoucherTemplateList(template));
    }

    @PreAuthorize("@ss.hasPermi('business:generalLedger:edit')")
    @Log(title = "凭证科目模板", businessType = BusinessType.INSERT)
    @PostMapping("/voucherTemplate")
    public AjaxResult addVoucherTemplate(@RequestBody FinVoucherTemplate template)
    {
        return toAjax(finGeneralLedgerService.insertVoucherTemplate(template, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:generalLedger:edit')")
    @Log(title = "凭证科目模板", businessType = BusinessType.UPDATE)
    @PutMapping("/voucherTemplate")
    public AjaxResult editVoucherTemplate(@RequestBody FinVoucherTemplate template)
    {
        return toAjax(finGeneralLedgerService.updateVoucherTemplate(template));
    }

    @PreAuthorize("@ss.hasPermi('business:generalLedger:edit')")
    @Log(title = "凭证科目模板", businessType = BusinessType.DELETE)
    @DeleteMapping("/voucherTemplate/{templateId}")
    public AjaxResult removeVoucherTemplate(@PathVariable Long templateId)
    {
        return toAjax(finGeneralLedgerService.deleteVoucherTemplateById(templateId));
    }
}
