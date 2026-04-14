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
import com.canglian.business.domain.FinWriteOff;
import com.canglian.business.service.IFinWriteOffService;

/**
 * 核销 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/writeOff")
public class FinWriteOffController extends BaseController
{
    @Autowired
    private IFinWriteOffService finWriteOffService;

    /**
     * 获取核销列表
     */
    @PreAuthorize("@ss.hasPermi('business:writeOff:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinWriteOff finWriteOff)
    {
        startPage();
        List<FinWriteOff> list = finWriteOffService.selectFinWriteOffList(finWriteOff);
        return getDataTable(list);
    }

    /**
     * 根据核销编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:writeOff:query')")
    @GetMapping(value = "/{writeOffId}")
    public AjaxResult getInfo(@PathVariable Long writeOffId)
    {
        return success(finWriteOffService.selectFinWriteOffById(writeOffId));
    }

    /**
     * 新增核销
     */
    @PreAuthorize("@ss.hasPermi('business:writeOff:add')")
    @Log(title = "核销", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinWriteOff finWriteOff)
    {
        finWriteOff.setCreateBy(getUsername());
        return toAjax(finWriteOffService.insertFinWriteOff(finWriteOff));
    }

    /**
     * 修改核销
     */
    @PreAuthorize("@ss.hasPermi('business:writeOff:edit')")
    @Log(title = "核销", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinWriteOff finWriteOff)
    {
        finWriteOff.setUpdateBy(getUsername());
        return toAjax(finWriteOffService.updateFinWriteOff(finWriteOff));
    }

    /**
     * 删除核销
     */
    @PreAuthorize("@ss.hasPermi('business:writeOff:remove')")
    @Log(title = "核销", businessType = BusinessType.DELETE)
    @DeleteMapping("/{writeOffIds}")
    public AjaxResult remove(@PathVariable Long[] writeOffIds)
    {
        return toAjax(finWriteOffService.deleteFinWriteOffByIds(writeOffIds));
    }
}

