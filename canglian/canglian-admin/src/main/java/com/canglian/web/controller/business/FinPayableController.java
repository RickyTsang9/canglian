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
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;
import com.canglian.business.domain.FinPayable;
import com.canglian.business.service.IFinPayableService;

/**
 * 应付单 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/payable")
public class FinPayableController extends BaseController
{
    @Autowired
    private IFinPayableService finPayableService;

    /**
     * 获取应付单列表
     */
    @PreAuthorize("@ss.hasPermi('business:payable:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinPayable finPayable)
    {
        startPage();
        List<FinPayable> list = finPayableService.selectFinPayableList(finPayable);
        return getDataTable(list);
    }

    /**
     * 根据应付单编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:payable:query')")
    @GetMapping(value = "/{payableId}")
    public AjaxResult getInfo(@PathVariable Long payableId)
    {
        return success(finPayableService.selectFinPayableById(payableId));
    }

    /**
     * 新增应付单
     */
    @PreAuthorize("@ss.hasPermi('business:payable:add')")
    @Log(title = "应付单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinPayable finPayable)
    {
        finPayable.setCreateBy(getUsername());
        return toAjax(finPayableService.insertFinPayable(finPayable));
    }

    /**
     * 修改应付单
     */
    @PreAuthorize("@ss.hasPermi('business:payable:edit')")
    @Log(title = "应付单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinPayable finPayable)
    {
        finPayable.setUpdateBy(getUsername());
        return toAjax(finPayableService.updateFinPayable(finPayable));
    }

    /**
     * 应付单审核
     */
    @PreAuthorize("@ss.hasPermi('business:payable:audit')")
    @Log(title = "应付审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{payableId}")
    public AjaxResult audit(@PathVariable Long payableId)
    {
        return toAjax(finPayableService.auditFinPayable(payableId, getUsername()));
    }

    /**
     * 删除应付单
     */
    @PreAuthorize("@ss.hasPermi('business:payable:remove')")
    @Log(title = "应付单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{payableIds}")
    public AjaxResult remove(@PathVariable Long[] payableIds)
    {
        return toAjax(finPayableService.deleteFinPayableByIds(payableIds));
    }
}

