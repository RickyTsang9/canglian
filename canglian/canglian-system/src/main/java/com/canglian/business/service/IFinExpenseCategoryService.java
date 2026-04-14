package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.FinExpenseCategory;
import com.canglian.common.core.domain.TreeSelect;

public interface IFinExpenseCategoryService
{
    public FinExpenseCategory selectFinExpenseCategoryById(Long categoryId);

    public List<FinExpenseCategory> selectFinExpenseCategoryList(FinExpenseCategory finExpenseCategory);

    public List<FinExpenseCategory> buildExpenseCategoryTree(List<FinExpenseCategory> expenseCategories);

    public List<TreeSelect> buildExpenseCategoryTreeSelect(List<FinExpenseCategory> expenseCategories);

    public boolean hasChildByCategoryId(Long categoryId);

    public boolean checkCategoryNameUnique(FinExpenseCategory finExpenseCategory);

    public int insertFinExpenseCategory(FinExpenseCategory finExpenseCategory);

    public int updateFinExpenseCategory(FinExpenseCategory finExpenseCategory);

    public int deleteFinExpenseCategoryById(Long categoryId);

    public int deleteFinExpenseCategoryByIds(Long[] categoryIds);
}

