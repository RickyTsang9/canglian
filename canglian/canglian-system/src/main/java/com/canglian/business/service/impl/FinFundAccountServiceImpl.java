package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinFundAccount;
import com.canglian.business.mapper.FinFundAccountMapper;
import com.canglian.business.service.IFinFundAccountService;

/**
 * 资金账户 服务层实现
 * 
 * @author canglian
 */
@Service
public class FinFundAccountServiceImpl implements IFinFundAccountService
{
    @Autowired
    private FinFundAccountMapper finFundAccountMapper;

    /**
     * 查询资金账户信息
     * 
     * @param accountId 资金账户id
     * @return 资金账户信息
     */
    @Override
    public FinFundAccount selectFinFundAccountById(Long accountId)
    {
        return finFundAccountMapper.selectFinFundAccountById(accountId);
    }

    /**
     * 查询资金账户列表
     * 
     * @param finFundAccount 资金账户信息
     * @return 资金账户集合
     */
    @Override
    public List<FinFundAccount> selectFinFundAccountList(FinFundAccount finFundAccount)
    {
        return finFundAccountMapper.selectFinFundAccountList(finFundAccount);
    }

    /**
     * 新增资金账户
     * 
     * @param finFundAccount 资金账户信息
     * @return 结果
     */
    @Override
    public int insertFinFundAccount(FinFundAccount finFundAccount)
    {
        return finFundAccountMapper.insertFinFundAccount(finFundAccount);
    }

    /**
     * 修改资金账户
     * 
     * @param finFundAccount 资金账户信息
     * @return 结果
     */
    @Override
    public int updateFinFundAccount(FinFundAccount finFundAccount)
    {
        return finFundAccountMapper.updateFinFundAccount(finFundAccount);
    }

    /**
     * 删除资金账户
     * 
     * @param accountId 资金账户id
     * @return 结果
     */
    @Override
    public int deleteFinFundAccountById(Long accountId)
    {
        return finFundAccountMapper.deleteFinFundAccountById(accountId);
    }

    /**
     * 批量删除资金账户
     * 
     * @param accountIds 需要删除的资金账户id
     * @return 结果
     */
    @Override
    public int deleteFinFundAccountByIds(Long[] accountIds)
    {
        return finFundAccountMapper.deleteFinFundAccountByIds(accountIds);
    }
}

