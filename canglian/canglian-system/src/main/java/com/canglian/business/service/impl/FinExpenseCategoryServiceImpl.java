package com.canglian.business.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinExpenseCategory;
import com.canglian.business.mapper.FinExpenseCategoryMapper;
import com.canglian.business.service.IFinExpenseCategoryService;
import com.canglian.common.constant.UserConstants;
import com.canglian.common.core.domain.TreeSelect;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

@Service
public class FinExpenseCategoryServiceImpl implements IFinExpenseCategoryService
{
    @Autowired
    private FinExpenseCategoryMapper finExpenseCategoryMapper;

    @Override
    public FinExpenseCategory selectFinExpenseCategoryById(Long categoryId)
    {
        return finExpenseCategoryMapper.selectFinExpenseCategoryById(categoryId);
    }

    @Override
    public List<FinExpenseCategory> selectFinExpenseCategoryList(FinExpenseCategory finExpenseCategory)
    {
        return finExpenseCategoryMapper.selectFinExpenseCategoryList(finExpenseCategory);
    }

    @Override
    public List<FinExpenseCategory> buildExpenseCategoryTree(List<FinExpenseCategory> expenseCategories)
    {
        List<FinExpenseCategory> returnList = new ArrayList<FinExpenseCategory>();
        List<Long> tempList = expenseCategories.stream().map(FinExpenseCategory::getCategoryId).collect(Collectors.toList());
        for (FinExpenseCategory expenseCategory : expenseCategories)
        {
            if (!tempList.contains(expenseCategory.getParentId()))
            {
                recursionFn(expenseCategories, expenseCategory);
                returnList.add(expenseCategory);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = expenseCategories;
        }
        return returnList;
    }

    @Override
    public List<TreeSelect> buildExpenseCategoryTreeSelect(List<FinExpenseCategory> expenseCategories)
    {
        List<FinExpenseCategory> expenseCategoryTrees = buildExpenseCategoryTree(expenseCategories);
        return expenseCategoryTrees.stream().map(this::buildExpenseCategoryTreeSelectNode).collect(Collectors.toList());
    }

    private TreeSelect buildExpenseCategoryTreeSelectNode(FinExpenseCategory expenseCategory)
    {
        TreeSelect treeSelect = new TreeSelect();
        treeSelect.setId(expenseCategory.getCategoryId());
        treeSelect.setLabel(expenseCategory.getCategoryName());
        treeSelect.setDisabled(StringUtils.equals(UserConstants.NORMAL, expenseCategory.getStatus()) ? false : true);
        List<?> children = expenseCategory.getChildren();
        if (StringUtils.isNotEmpty(children))
        {
            List<TreeSelect> childrenTreeSelects = new ArrayList<TreeSelect>();
            for (Object child : children)
            {
                if (child instanceof FinExpenseCategory)
                {
                    childrenTreeSelects.add(buildExpenseCategoryTreeSelectNode((FinExpenseCategory) child));
                }
            }
            treeSelect.setChildren(childrenTreeSelects);
        }
        return treeSelect;
    }

    @Override
    public boolean hasChildByCategoryId(Long categoryId)
    {
        int result = finExpenseCategoryMapper.hasChildByCategoryId(categoryId);
        return result > 0;
    }

    @Override
    public boolean checkCategoryNameUnique(FinExpenseCategory finExpenseCategory)
    {
        Long categoryId = StringUtils.isNull(finExpenseCategory.getCategoryId()) ? -1L : finExpenseCategory.getCategoryId();
        FinExpenseCategory info = finExpenseCategoryMapper.checkCategoryNameUnique(finExpenseCategory.getCategoryName(), finExpenseCategory.getParentId());
        if (StringUtils.isNotNull(info) && info.getCategoryId().longValue() != categoryId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public int insertFinExpenseCategory(FinExpenseCategory finExpenseCategory)
    {
        Long parentId = finExpenseCategory.getParentId();
        if (StringUtils.isNull(parentId))
        {
            finExpenseCategory.setParentId(0L);
            parentId = 0L;
        }
        if (parentId == 0L)
        {
            finExpenseCategory.setAncestors("0");
        }
        else
        {
            FinExpenseCategory parentExpenseCategory = finExpenseCategoryMapper.selectFinExpenseCategoryById(parentId);
            if (StringUtils.isNull(parentExpenseCategory))
            {
                throw new ServiceException("上级费用类别不存在");
            }
            finExpenseCategory.setAncestors(parentExpenseCategory.getAncestors() + "," + parentId);
        }
        return finExpenseCategoryMapper.insertFinExpenseCategory(finExpenseCategory);
    }

    @Override
    public int updateFinExpenseCategory(FinExpenseCategory finExpenseCategory)
    {
        FinExpenseCategory newParentExpenseCategory = finExpenseCategoryMapper.selectFinExpenseCategoryById(finExpenseCategory.getParentId());
        FinExpenseCategory oldExpenseCategory = finExpenseCategoryMapper.selectFinExpenseCategoryById(finExpenseCategory.getCategoryId());
        if (StringUtils.isNotNull(newParentExpenseCategory) && StringUtils.isNotNull(oldExpenseCategory))
        {
            String newAncestors = newParentExpenseCategory.getAncestors() + "," + newParentExpenseCategory.getCategoryId();
            String oldAncestors = oldExpenseCategory.getAncestors();
            finExpenseCategory.setAncestors(newAncestors);
            updateExpenseCategoryChildren(finExpenseCategory.getCategoryId(), newAncestors, oldAncestors);
        }
        return finExpenseCategoryMapper.updateFinExpenseCategory(finExpenseCategory);
    }

    public void updateExpenseCategoryChildren(Long categoryId, String newAncestors, String oldAncestors)
    {
        List<FinExpenseCategory> children = finExpenseCategoryMapper.selectChildrenExpenseCategoryById(categoryId);
        for (FinExpenseCategory child : children)
        {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        if (children.size() > 0)
        {
            finExpenseCategoryMapper.updateExpenseCategoryChildren(children);
        }
    }

    @Override
    public int deleteFinExpenseCategoryById(Long categoryId)
    {
        return finExpenseCategoryMapper.deleteFinExpenseCategoryById(categoryId);
    }

    @Override
    public int deleteFinExpenseCategoryByIds(Long[] categoryIds)
    {
        return finExpenseCategoryMapper.deleteFinExpenseCategoryByIds(categoryIds);
    }

    private void recursionFn(List<FinExpenseCategory> list, FinExpenseCategory expenseCategory)
    {
        List<FinExpenseCategory> childList = getChildList(list, expenseCategory);
        expenseCategory.setChildren(childList);
        for (FinExpenseCategory child : childList)
        {
            if (hasChild(list, child))
            {
                recursionFn(list, child);
            }
        }
    }

    private List<FinExpenseCategory> getChildList(List<FinExpenseCategory> list, FinExpenseCategory expenseCategory)
    {
        List<FinExpenseCategory> childList = new ArrayList<FinExpenseCategory>();
        Iterator<FinExpenseCategory> iterator = list.iterator();
        while (iterator.hasNext())
        {
            FinExpenseCategory currentItem = iterator.next();
            if (StringUtils.isNotNull(currentItem.getParentId()) && currentItem.getParentId().longValue() == expenseCategory.getCategoryId().longValue())
            {
                childList.add(currentItem);
            }
        }
        return childList;
    }

    private boolean hasChild(List<FinExpenseCategory> list, FinExpenseCategory expenseCategory)
    {
        return getChildList(list, expenseCategory).size() > 0;
    }
}

