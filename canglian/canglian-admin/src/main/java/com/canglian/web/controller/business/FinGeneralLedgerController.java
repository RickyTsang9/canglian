package com.canglian.web.controller.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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

    /**
     * 查询会计科目列表
     * 
     * @param status 状态
     * @return 会计科目列表
     */
    @PreAuthorize("@ss.hasPermi('business:generalLedger:query')")
    @GetMapping("/subject/list")
    public AjaxResult subjectList(@RequestParam(required = false) String status)
    {
        return success(finGeneralLedgerService.selectAccountSubjectList(status));
    }

    /**
     * 查询辅助核算列表
     * 
     * @param auxiliaryType 辅助核算类型
     * @return 辅助核算列表
     */
    @PreAuthorize("@ss.hasPermi('business:generalLedger:query')")
    @GetMapping("/auxiliary/list")
    public AjaxResult auxiliaryList(@RequestParam(required = false) String auxiliaryType)
    {
        return success(finGeneralLedgerService.selectAuxiliaryItemList(auxiliaryType));
    }

    /**
     * 查询会计期间列表
     * 
     * @param closeStatus 结账状态
     * @return 会计期间列表
     */
    @PreAuthorize("@ss.hasPermi('business:generalLedger:query')")
    @GetMapping("/period/list")
    public AjaxResult periodList(@RequestParam(required = false) String closeStatus)
    {
        return success(finGeneralLedgerService.selectFiscalPeriodList(closeStatus));
    }

    /**
     * 结账会计期间
     * 
     * @param periodId 会计期间id
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:generalLedger:close')")
    @Log(title = "会计期间结账", businessType = BusinessType.UPDATE)
    @PutMapping("/period/close/{periodId}")
    public AjaxResult closePeriod(@PathVariable Long periodId)
    {
        return toAjax(finGeneralLedgerService.closeFiscalPeriod(periodId, getUsername()));
    }

    /**
     * 反结账会计期间
     * 
     * @param periodId 会计期间id
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:generalLedger:reopen')")
    @Log(title = "会计期间反结账", businessType = BusinessType.UPDATE)
    @PutMapping("/period/reopen/{periodId}")
    public AjaxResult reopenPeriod(@PathVariable Long periodId)
    {
        return toAjax(finGeneralLedgerService.reopenFiscalPeriod(periodId, getUsername()));
    }
}
