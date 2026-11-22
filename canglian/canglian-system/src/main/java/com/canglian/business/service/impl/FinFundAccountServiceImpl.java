package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinBadDebt;
import com.canglian.business.domain.FinExpense;
import com.canglian.business.domain.FinFundAccount;
import com.canglian.business.domain.FinFundTransfer;
import com.canglian.business.domain.FinPayment;
import com.canglian.business.domain.FinReceipt;
import com.canglian.business.mapper.FinBadDebtMapper;
import com.canglian.business.mapper.FinExpenseMapper;
import com.canglian.business.mapper.FinFundAccountMapper;
import com.canglian.business.mapper.FinPaymentMapper;
import com.canglian.business.mapper.FinReceiptMapper;
import com.canglian.business.service.IFinFundAccountService;
import com.canglian.business.service.IFinFundLogService;
import com.canglian.common.exception.ServiceException;

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

    @Autowired
    private FinExpenseMapper finExpenseMapper;

    @Autowired
    private FinReceiptMapper finReceiptMapper;

    @Autowired
    private FinPaymentMapper finPaymentMapper;

    @Autowired(required = false)
    private IFinFundLogService finFundLogService;

    @Autowired(required = false)
    private FinBadDebtMapper finBadDebtMapper;

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
        if (finFundAccount != null && finFundAccount.getAccountId() != null && finFundLogService != null
            && finFundLogService.hasLogs(finFundAccount.getAccountId()))
        {
            FinFundAccount existing = finFundAccountMapper.selectFinFundAccountById(finFundAccount.getAccountId());
            if (existing != null && finFundAccount.getBalance() != null)
            {
                BigDecimal oldBal = existing.getBalance() == null ? BigDecimal.ZERO : existing.getBalance();
                if (finFundAccount.getBalance().compareTo(oldBal) != 0)
                {
                    throw new ServiceException("资金账户[" + existing.getAccountName() + "]已产生资金流水，禁止手工直接修改余额，请通过收付款或内部互转入账");
                }
            }
        }
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
        checkAccountDeletable(accountId);
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
        for (Long accountId : accountIds)
        {
            checkAccountDeletable(accountId);
        }
        return finFundAccountMapper.deleteFinFundAccountByIds(accountIds);
    }

    private void checkAccountDeletable(Long accountId)
    {
        FinFundAccount account = finFundAccountMapper.selectFinFundAccountById(accountId);
        if (account == null)
        {
            throw new ServiceException("资金账户不存在");
        }
        if (account.getBalance() != null && account.getBalance().compareTo(BigDecimal.ZERO) != 0)
        {
            throw new ServiceException("资金账户[" + account.getAccountName() + "]余额不为0，不允许删除");
        }
        FinExpense eq = new FinExpense();
        eq.setFundAccountId(accountId);
        List<FinExpense> expenses = finExpenseMapper.selectFinExpenseList(eq);
        if (expenses != null && !expenses.isEmpty())
        {
            throw new ServiceException("资金账户[" + account.getAccountName() + "]已被费用单引用，不允许删除");
        }
        FinReceipt rq = new FinReceipt();
        rq.setFundAccountId(accountId);
        List<FinReceipt> receipts = finReceiptMapper.selectFinReceiptList(rq);
        if (receipts != null && !receipts.isEmpty())
        {
            throw new ServiceException("资金账户[" + account.getAccountName() + "]已被收款单引用，不允许删除");
        }
        FinPayment pq = new FinPayment();
        pq.setFundAccountId(accountId);
        List<FinPayment> payments = finPaymentMapper.selectFinPaymentList(pq);
        if (payments != null && !payments.isEmpty())
        {
            throw new ServiceException("资金账户[" + account.getAccountName() + "]已被付款单引用，不允许删除");
        }
        if (finFundLogService != null)
        {
            if (finFundLogService.hasLogs(accountId))
            {
                throw new ServiceException("资金账户[" + account.getAccountName() + "]已产生历史资金流水，不允许删除（可停用）");
            }
            List<FinFundTransfer> transfers = finFundLogService.selectFinFundTransferList(new FinFundTransfer());
            if (transfers != null)
            {
                for (FinFundTransfer t : transfers)
                {
                    if (accountId.equals(t.getOutAccountId()) || accountId.equals(t.getInAccountId()))
                    {
                        throw new ServiceException("资金账户[" + account.getAccountName() + "]已被资金互转单引用，不允许删除");
                    }
                }
            }
        }
        if (finBadDebtMapper != null)
        {
            FinBadDebt bq = new FinBadDebt();
            bq.setRecoverAccountId(accountId);
            List<FinBadDebt> badDebts = finBadDebtMapper.selectFinBadDebtList(bq);
            if (badDebts != null && !badDebts.isEmpty())
            {
                throw new ServiceException("资金账户[" + account.getAccountName() + "]已被坏账收回记录引用，不允许删除");
            }
        }
    }
}

