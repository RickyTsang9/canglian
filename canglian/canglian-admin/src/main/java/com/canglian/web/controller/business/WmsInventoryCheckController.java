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
import com.canglian.business.domain.WmsInventoryCheck;
import com.canglian.business.service.IWmsInventoryCheckService;

@RestController
@RequestMapping("/business/inventoryCheck")
public class WmsInventoryCheckController extends BaseController
{
    @Autowired
    private IWmsInventoryCheckService wmsInventoryCheckService;

    @PreAuthorize("@ss.hasPermi('business:inventoryCheck:list')")
    @GetMapping("/list")
    public TableDataInfo list(WmsInventoryCheck wmsInventoryCheck)
    {
        startPage();
        List<WmsInventoryCheck> wmsInventoryCheckList = wmsInventoryCheckService.selectWmsInventoryCheckList(wmsInventoryCheck);
        return getDataTable(wmsInventoryCheckList);
    }

    @PreAuthorize("@ss.hasPermi('business:inventoryCheck:query')")
    @GetMapping(value = "/{checkId}")
    public AjaxResult getInfo(@PathVariable Long checkId)
    {
        return success(wmsInventoryCheckService.selectWmsInventoryCheckById(checkId));
    }

    @PreAuthorize("@ss.hasPermi('business:inventoryCheck:add')")
    @Log(title = "盘点单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody WmsInventoryCheck wmsInventoryCheck)
    {
        wmsInventoryCheck.setCreateBy(getUsername());
        return toAjax(wmsInventoryCheckService.insertWmsInventoryCheck(wmsInventoryCheck));
    }

    @PreAuthorize("@ss.hasPermi('business:inventoryCheck:edit')")
    @Log(title = "盘点单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WmsInventoryCheck wmsInventoryCheck)
    {
        wmsInventoryCheck.setUpdateBy(getUsername());
        return toAjax(wmsInventoryCheckService.updateWmsInventoryCheck(wmsInventoryCheck));
    }

    /**
     * 盘点单审核
     */
    @PreAuthorize("@ss.hasPermi('business:inventoryCheck:audit')")
    @Log(title = "盘点审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{checkId}")
    public AjaxResult audit(@PathVariable Long checkId)
    {
        return toAjax(wmsInventoryCheckService.auditWmsInventoryCheck(checkId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:inventoryCheck:remove')")
    @Log(title = "盘点单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{checkIds}")
    public AjaxResult remove(@PathVariable Long[] checkIds)
    {
        return toAjax(wmsInventoryCheckService.deleteWmsInventoryCheckByIds(checkIds));
    }
}

