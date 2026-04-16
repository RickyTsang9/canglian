package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.common.exception.ServiceException;
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
        finExpense.setStatus("0");
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
        FinExpense existingExpense = checkExpenseEditable(finExpense.getExpenseId());
        finExpense.setStatus(existingExpense.getStatus());
        return finExpenseMapper.updateFinExpense(finExpense);
    }

    /**
     * 确认费用单
     * 
     * @param expenseId 费用id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    public int confirmFinExpense(Long expenseId, String operator)
    {
        FinExpense finExpense = getExistingExpense(expenseId);
        if ("1".equals(finExpense.getStatus()))
        {
            throw new ServiceException("费用单已确认，请勿重复操作");
        }
        finExpense.setStatus("1");
        finExpense.setUpdateBy(operator);
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
        checkExpenseEditable(expenseId);
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
        for (Long expenseId : expenseIds)
        {
            checkExpenseEditable(expenseId);
        }
        return finExpenseMapper.deleteFinExpenseByIds(expenseIds);
    }

    /**
     * 获取费用单信息
     * 
     * @param expenseId 费用id
     * @return 费用单信息
     */
    private FinExpense getExistingExpense(Long expenseId)
    {
        FinExpense finExpense = finExpenseMapper.selectFinExpenseById(expenseId);
        if (finExpense == null)
        {
            throw new ServiceException("费用单不存在");
        }
        return finExpense;
    }

    /**
     * 校验费用单是否允许编辑
     * 
     * @param expenseId 费用id
     * @return 当前费用单信息
     */
    private FinExpense checkExpenseEditable(Long expenseId)
    {
        FinExpense finExpense = getExistingExpense(expenseId);
        if ("1".equals(finExpense.getStatus()))
        {
            throw new ServiceException("费用单已确认，不允许修改或删除");
        }
        return finExpense;
    }
}

