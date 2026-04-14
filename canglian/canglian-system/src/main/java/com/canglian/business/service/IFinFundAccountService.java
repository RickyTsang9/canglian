package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.FinFundAccount;

public interface IFinFundAccountService
{
    /**
     * 查询资金账户信息
     * 
     * @param accountId 资金账户id
     * @return 资金账户信息
     */
    public FinFundAccount selectFinFundAccountById(Long accountId);

    /**
     * 查询资金账户列表
     * 
     * @param finFundAccount 资金账户信息
     * @return 资金账户集合
     */
    public List<FinFundAccount> selectFinFundAccountList(FinFundAccount finFundAccount);

    /**
     * 新增资金账户
     * 
     * @param finFundAccount 资金账户信息
     * @return 结果
     */
    public int insertFinFundAccount(FinFundAccount finFundAccount);

    /**
     * 修改资金账户
     * 
     * @param finFundAccount 资金账户信息
     * @return 结果
     */
    public int updateFinFundAccount(FinFundAccount finFundAccount);

    /**
     * 删除资金账户
     * 
     * @param accountId 资金账户id
     * @return 结果
     */
    public int deleteFinFundAccountById(Long accountId);

    /**
     * 批量删除资金账户
     * 
     * @param accountIds 需要删除的资金账户id
     * @return 结果
     */
    public int deleteFinFundAccountByIds(Long[] accountIds);
}

