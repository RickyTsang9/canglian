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

    /**
     * 查询成本账户信息
     * 
     * @param costAccountId 成本账户id
     * @return 成本账户信息
     */
    @Override
    public FinCostAccount selectFinCostAccountById(Long costAccountId)
    {
        return finCostAccountMapper.selectFinCostAccountById(costAccountId);
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
        return finCostAccountMapper.selectFinCostAccountList(finCostAccount);
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
        return finCostAccountMapper.deleteFinCostAccountByIds(costAccountIds);
    }
}

