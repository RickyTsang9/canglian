package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinExpense;
import com.canglian.business.mapper.FinExpenseMapper;
import com.canglian.business.service.IFinExpenseService;

/**
 * 费用 服务层实现
 * 
 * @author canglian
 */
@Service
public class FinExpenseServiceImpl implements IFinExpenseService
{
    @Autowired
    private FinExpenseMapper finExpenseMapper;

    /**
     * 查询费用信息
     * 
     * @param expenseId 费用id
     * @return 费用信息
     */
    @Override
    public FinExpense selectFinExpenseById(Long expenseId)
    {
        return finExpenseMapper.selectFinExpenseById(expenseId);
    }

    /**
     * 查询费用列表
     * 
     * @param finExpense 费用信息
     * @return 费用集合
     */
    @Override
    public List<FinExpense> selectFinExpenseList(FinExpense finExpense)
    {
        return finExpenseMapper.selectFinExpenseList(finExpense);
    }

    /**
     * 新增费用
     * 
     * @param finExpense 费用信息
     * @return 结果
     */
    @Override
    public int insertFinExpense(FinExpense finExpense)
    {
        return finExpenseMapper.insertFinExpense(finExpense);
    }

    /**
     * 修改费用
     * 
     * @param finExpense 费用信息
     * @return 结果
     */
    @Override
    public int updateFinExpense(FinExpense finExpense)
    {
        return finExpenseMapper.updateFinExpense(finExpense);
    }

    /**
     * 删除费用
     * 
     * @param expenseId 费用id
     * @return 结果
     */
    @Override
    public int deleteFinExpenseById(Long expenseId)
    {
        return finExpenseMapper.deleteFinExpenseById(expenseId);
    }

    /**
     * 批量删除费用
     * 
     * @param expenseIds 需要删除的费用id
     * @return 结果
     */
    @Override
    public int deleteFinExpenseByIds(Long[] expenseIds)
    {
        return finExpenseMapper.deleteFinExpenseByIds(expenseIds);
    }
}

