package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.FinExpense;

/**
 * 费用 服务层
 * 
 * @author canglian
 */
public interface IFinExpenseService
{
    /**
     * 查询费用信息
     * 
     * @param expenseId 费用id
     * @return 费用信息
     */
    public FinExpense selectFinExpenseById(Long expenseId);

    /**
     * 查询费用列表
     * 
     * @param finExpense 费用信息
     * @return 费用集合
     */
    public List<FinExpense> selectFinExpenseList(FinExpense finExpense);

    /**
     * 新增费用
     * 
     * @param finExpense 费用信息
     * @return 结果
     */
    public int insertFinExpense(FinExpense finExpense);

    /**
     * 修改费用
     * 
     * @param finExpense 费用信息
     * @return 结果
     */
    public int updateFinExpense(FinExpense finExpense);

    /**
     * 删除费用
     * 
     * @param expenseId 费用id
     * @return 结果
     */
    public int deleteFinExpenseById(Long expenseId);

    /**
     * 批量删除费用
     * 
     * @param expenseIds 需要删除的费用id
     * @return 结果
     */
    public int deleteFinExpenseByIds(Long[] expenseIds);
}

