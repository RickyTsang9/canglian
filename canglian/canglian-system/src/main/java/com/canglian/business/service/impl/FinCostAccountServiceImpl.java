package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinCostAccount;
import com.canglian.business.mapper.FinCostAccountMapper;
import com.canglian.business.service.IFinCostAccountService;

/**
 * 成本账户 服务层实现
 * 
 * @author canglian
 */
@Service
public class FinCostAccountServiceImpl implements IFinCostAccountService
{
    @Autowired
    private FinCostAccountMapper finCostAccountMapper;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    /**
     * 查询成本账户信息
     * 
     * @param costAccountId 成本账户id
     * @return 成本账户信息
     */
    @Override
    public FinCostAccount selectFinCostAccountById(Long costAccountId)
    {
        FinCostAccount acc = finCostAccountMapper.selectFinCostAccountById(costAccountId);
        if (acc != null && bizScopeHelper != null) bizScopeHelper.assertWarehouseAccess(acc.getWarehouseId());
        return acc;
    }

    /**
     * 查询成本账户列表
     * 
     * @param finCostAccount 成本账户信息
     * @return 成本账户集合
     */
    @Override
    public List<FinCostAccount> selectFinCostAccountList(FinCostAccount finCostAccount)
    {
        List<FinCostAccount> list = finCostAccountMapper.selectFinCostAccountList(finCostAccount);
        java.util.Set<Long> allowed = bizScopeHelper == null ? null : bizScopeHelper.getRestrictedWarehouseIds();
        if (allowed == null || list == null) return list;
        java.util.List<FinCostAccount> filtered = new java.util.ArrayList<FinCostAccount>();
        for (FinCostAccount a : list)
        {
            if (allowed.contains(a.getWarehouseId())) filtered.add(a);
        }
        return filtered;
    }

    /**
     * 新增成本账户
     * 
     * @param finCostAccount 成本账户信息
     * @return 结果
     */
    @Override
    public int insertFinCostAccount(FinCostAccount finCostAccount)
    {
        if (bizScopeHelper != null && finCostAccount != null) bizScopeHelper.assertWarehouseAccess(finCostAccount.getWarehouseId());
        return finCostAccountMapper.insertFinCostAccount(finCostAccount);
    }

    /**
     * 修改成本账户
     * 
     * @param finCostAccount 成本账户信息
     * @return 结果
     */
    @Override
    public int updateFinCostAccount(FinCostAccount finCostAccount)
    {
        if (bizScopeHelper != null && finCostAccount != null) bizScopeHelper.assertWarehouseAccess(finCostAccount.getWarehouseId());
        return finCostAccountMapper.updateFinCostAccount(finCostAccount);
    }

    /**
     * 删除成本账户
     * 
     * @param costAccountId 成本账户id
     * @return 结果
     */
    @Override
    public int deleteFinCostAccountById(Long costAccountId)
    {
        if (bizScopeHelper != null && costAccountId != null)
        {
            FinCostAccount acc = finCostAccountMapper.selectFinCostAccountById(costAccountId);
            if (acc != null) bizScopeHelper.assertWarehouseAccess(acc.getWarehouseId());
        }
        return finCostAccountMapper.deleteFinCostAccountById(costAccountId);
    }

    /**
     * 批量删除成本账户
     * 
     * @param costAccountIds 需要删除的成本账户id
     * @return 结果
     */
    @Override
    public int deleteFinCostAccountByIds(Long[] costAccountIds)
    {
        if (bizScopeHelper != null && costAccountIds != null)
        {
            for (Long id : costAccountIds)
            {
                FinCostAccount acc = finCostAccountMapper.selectFinCostAccountById(id);
                if (acc != null) bizScopeHelper.assertWarehouseAccess(acc.getWarehouseId());
            }
        }
        return finCostAccountMapper.deleteFinCostAccountByIds(costAccountIds);
    }
}

