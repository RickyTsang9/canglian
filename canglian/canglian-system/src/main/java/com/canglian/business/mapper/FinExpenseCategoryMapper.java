package com.canglian.business.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.canglian.business.domain.FinExpenseCategory;

public interface FinExpenseCategoryMapper
{
    public FinExpenseCategory selectFinExpenseCategoryById(Long categoryId);

    public List<FinExpenseCategory> selectFinExpenseCategoryList(FinExpenseCategory finExpenseCategory);

    public List<FinExpenseCategory> selectChildrenExpenseCategoryById(Long categoryId);

    public int hasChildByCategoryId(Long categoryId);

    public FinExpenseCategory checkCategoryNameUnique(@Param("categoryName") String categoryName, @Param("parentId") Long parentId);

    public int insertFinExpenseCategory(FinExpenseCategory finExpenseCategory);

    public int updateFinExpenseCategory(FinExpenseCategory finExpenseCategory);

    public int updateExpenseCategoryChildren(@Param("expenseCategories") List<FinExpenseCategory> expenseCategories);

    public int deleteFinExpenseCategoryById(Long categoryId);

    public int deleteFinExpenseCategoryByIds(Long[] categoryIds);
}

