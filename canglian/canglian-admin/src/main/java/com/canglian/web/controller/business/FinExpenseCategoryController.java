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
import com.canglian.business.domain.FinExpenseCategory;
import com.canglian.business.service.IFinExpenseCategoryService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.enums.BusinessType;

@RestController
@RequestMapping("/business/expenseCategory")
public class FinExpenseCategoryController extends BaseController
{
    @Autowired
    private IFinExpenseCategoryService finExpenseCategoryService;

    @PreAuthorize("@ss.hasPermi('business:expenseCategory:list')")
    @GetMapping("/list")
    public AjaxResult list(FinExpenseCategory finExpenseCategory)
    {
        List<FinExpenseCategory> list = finExpenseCategoryService.selectFinExpenseCategoryList(finExpenseCategory);
        return success(list);
    }

    @GetMapping("/treeselect")
    public AjaxResult treeselect(FinExpenseCategory finExpenseCategory)
    {
        List<FinExpenseCategory> list = finExpenseCategoryService.selectFinExpenseCategoryList(finExpenseCategory);
        return success(finExpenseCategoryService.buildExpenseCategoryTreeSelect(list));
    }

    @PreAuthorize("@ss.hasPermi('business:expenseCategory:query')")
    @GetMapping(value = "/{categoryId}")
    public AjaxResult getInfo(@PathVariable Long categoryId)
    {
        return success(finExpenseCategoryService.selectFinExpenseCategoryById(categoryId));
    }

    @PreAuthorize("@ss.hasPermi('business:expenseCategory:add')")
    @Log(title = "费用类别", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinExpenseCategory finExpenseCategory)
    {
        if (!finExpenseCategoryService.checkCategoryNameUnique(finExpenseCategory))
        {
            return error("新增费用类别'" + finExpenseCategory.getCategoryName() + "'失败，费用类别名称已存在");
        }
        finExpenseCategory.setCreateBy(getUsername());
        return toAjax(finExpenseCategoryService.insertFinExpenseCategory(finExpenseCategory));
    }

    @PreAuthorize("@ss.hasPermi('business:expenseCategory:edit')")
    @Log(title = "费用类别", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinExpenseCategory finExpenseCategory)
    {
        if (!finExpenseCategoryService.checkCategoryNameUnique(finExpenseCategory))
        {
            return error("修改费用类别'" + finExpenseCategory.getCategoryName() + "'失败，费用类别名称已存在");
        }
        else if (finExpenseCategory.getCategoryId().equals(finExpenseCategory.getParentId()))
        {
            return error("修改费用类别'" + finExpenseCategory.getCategoryName() + "'失败，上级费用类别不能选择自己");
        }
        finExpenseCategory.setUpdateBy(getUsername());
        return toAjax(finExpenseCategoryService.updateFinExpenseCategory(finExpenseCategory));
    }

    @PreAuthorize("@ss.hasPermi('business:expenseCategory:remove')")
    @Log(title = "费用类别", businessType = BusinessType.DELETE)
    @DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@PathVariable Long[] categoryIds)
    {
        if (categoryIds.length == 1 && finExpenseCategoryService.hasChildByCategoryId(categoryIds[0]))
        {
            return warn("存在下级费用类别,不允许删除");
        }
        return toAjax(finExpenseCategoryService.deleteFinExpenseCategoryByIds(categoryIds));
    }
}

