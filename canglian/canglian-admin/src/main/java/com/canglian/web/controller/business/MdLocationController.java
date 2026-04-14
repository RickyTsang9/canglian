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
import com.canglian.business.domain.MdLocation;
import com.canglian.business.service.IMdLocationService;

/**
 * 库位档案 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/location")
public class MdLocationController extends BaseController
{
    @Autowired
    private IMdLocationService mdLocationService;

    /**
     * 获取库位档案列表
     */
    @PreAuthorize("@ss.hasPermi('business:location:list')")
    @GetMapping("/list")
    public TableDataInfo list(MdLocation mdLocation)
    {
        startPage();
        List<MdLocation> list = mdLocationService.selectMdLocationList(mdLocation);
        return getDataTable(list);
    }

    /**
     * 根据库位档案编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:location:query')")
    @GetMapping(value = "/{locationId}")
    public AjaxResult getInfo(@PathVariable Long locationId)
    {
        return success(mdLocationService.selectMdLocationByLocationId(locationId));
    }

    /**
     * 新增库位档案
     */
    @PreAuthorize("@ss.hasPermi('business:location:add')")
    @Log(title = "库位档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody MdLocation mdLocation)
    {
        mdLocation.setCreateBy(getUsername());
        return toAjax(mdLocationService.insertMdLocation(mdLocation));
    }

    /**
     * 修改库位档案
     */
    @PreAuthorize("@ss.hasPermi('business:location:edit')")
    @Log(title = "库位档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody MdLocation mdLocation)
    {
        mdLocation.setUpdateBy(getUsername());
        return toAjax(mdLocationService.updateMdLocation(mdLocation));
    }

    /**
     * 删除库位档案
     */
    @PreAuthorize("@ss.hasPermi('business:location:remove')")
    @Log(title = "库位档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{locationIds}")
    public AjaxResult remove(@PathVariable Long[] locationIds)
    {
        return toAjax(mdLocationService.deleteMdLocationByLocationIds(locationIds));
    }
}

