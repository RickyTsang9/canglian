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
import com.canglian.business.domain.MdWarehouse;
import com.canglian.business.service.IMdWarehouseService;

/**
 * 仓库档案 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/warehouse")
public class MdWarehouseController extends BaseController
{
    @Autowired
    private IMdWarehouseService mdWarehouseService;

    /**
     * 获取仓库档案列表
     */
    @PreAuthorize("@ss.hasPermi('business:warehouse:list')")
    @GetMapping("/list")
    public TableDataInfo list(MdWarehouse mdWarehouse)
    {
        startPage();
        List<MdWarehouse> list = mdWarehouseService.selectMdWarehouseList(mdWarehouse);
        return getDataTable(list);
    }

    /**
     * 根据仓库档案编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:warehouse:query')")
    @GetMapping(value = "/{warehouseId}")
    public AjaxResult getInfo(@PathVariable Long warehouseId)
    {
        return success(mdWarehouseService.selectMdWarehouseById(warehouseId));
    }

    /**
     * 新增仓库档案
     */
    @PreAuthorize("@ss.hasPermi('business:warehouse:add')")
    @Log(title = "仓库档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody MdWarehouse mdWarehouse)
    {
        mdWarehouse.setCreateBy(getUsername());
        return toAjax(mdWarehouseService.insertMdWarehouse(mdWarehouse));
    }

    /**
     * 修改仓库档案
     */
    @PreAuthorize("@ss.hasPermi('business:warehouse:edit')")
    @Log(title = "仓库档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody MdWarehouse mdWarehouse)
    {
        mdWarehouse.setUpdateBy(getUsername());
        return toAjax(mdWarehouseService.updateMdWarehouse(mdWarehouse));
    }

    /**
     * 删除仓库档案
     */
    @PreAuthorize("@ss.hasPermi('business:warehouse:remove')")
    @Log(title = "仓库档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{warehouseIds}")
    public AjaxResult remove(@PathVariable Long[] warehouseIds)
    {
        return toAjax(mdWarehouseService.deleteMdWarehouseByIds(warehouseIds));
    }
}

