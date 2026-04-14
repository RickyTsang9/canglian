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
import com.canglian.business.domain.MdSupplier;
import com.canglian.business.service.IMdSupplierService;

/**
 * 供应商档案 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/supplier")
public class MdSupplierController extends BaseController
{
    @Autowired
    private IMdSupplierService mdSupplierService;

    /**
     * 获取供应商档案列表
     */
    @PreAuthorize("@ss.hasPermi('business:supplier:list')")
    @GetMapping("/list")
    public TableDataInfo list(MdSupplier mdSupplier)
    {
        startPage();
        List<MdSupplier> list = mdSupplierService.selectMdSupplierList(mdSupplier);
        return getDataTable(list);
    }

    /**
     * 根据供应商档案编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:supplier:query')")
    @GetMapping(value = "/{supplierId}")
    public AjaxResult getInfo(@PathVariable Long supplierId)
    {
        return success(mdSupplierService.selectMdSupplierById(supplierId));
    }

    /**
     * 新增供应商档案
     */
    @PreAuthorize("@ss.hasPermi('business:supplier:add')")
    @Log(title = "供应商档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody MdSupplier mdSupplier)
    {
        mdSupplier.setCreateBy(getUsername());
        return toAjax(mdSupplierService.insertMdSupplier(mdSupplier));
    }

    /**
     * 修改供应商档案
     */
    @PreAuthorize("@ss.hasPermi('business:supplier:edit')")
    @Log(title = "供应商档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody MdSupplier mdSupplier)
    {
        mdSupplier.setUpdateBy(getUsername());
        return toAjax(mdSupplierService.updateMdSupplier(mdSupplier));
    }

    /**
     * 删除供应商档案
     */
    @PreAuthorize("@ss.hasPermi('business:supplier:remove')")
    @Log(title = "供应商档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{supplierIds}")
    public AjaxResult remove(@PathVariable Long[] supplierIds)
    {
        return toAjax(mdSupplierService.deleteMdSupplierByIds(supplierIds));
    }
}

