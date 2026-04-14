package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 资金账户对象 fin_fund_account
 * 
 * @author canglian
 */
public class FinFundAccount extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 账户id */
    private Long accountId;

    /** 账户编号 */
    private String accountNo;

    /** 账户名称 */
    private String accountName;

    /** 账户类型 */
    private String accountType;

    /** 开户行 */
    private String bankName;

    /** 银行账号 */
    private String bankAccount;

    /** 币种 */
    private String currency;

    /** 余额 */
    private BigDecimal balance;

    /** 状态 */
    private String status;

    /**
     * 获取账户id
     * 
     * @return 账户id
     */
    public Long getAccountId()
    {
        return accountId;
    }

    /**
     * 设置账户id
     * 
     * @param accountId 账户id
     */
    public void setAccountId(Long accountId)
    {
        this.accountId = accountId;
    }

    /**
     * 获取账户编号
     * 
     * @return 账户编号
     */
    public String getAccountNo()
    {
        return accountNo;
    }

    /**
     * 设置账户编号
     * 
     * @param accountNo 账户编号
     */
    public void setAccountNo(String accountNo)
    {
        this.accountNo = accountNo;
    }

    /**
     * 获取账户名称
     * 
     * @return 账户名称
     */
    public String getAccountName()
    {
        return accountName;
    }

    /**
     * 设置账户名称
     * 
     * @param accountName 账户名称
     */
    public void setAccountName(String accountName)
    {
        this.accountName = accountName;
    }

    /**
     * 获取账户类型
     * 
     * @return 账户类型
     */
    public String getAccountType()
    {
        return accountType;
    }

    /**
     * 设置账户类型
     * 
     * @param accountType 账户类型
     */
    public void setAccountType(String accountType)
    {
        this.accountType = accountType;
    }

    /**
     * 获取开户行
     * 
     * @return 开户行
     */
    public String getBankName()
    {
        return bankName;
    }

    /**
     * 设置开户行
     * 
     * @param bankName 开户行
     */
    public void setBankName(String bankName)
    {
        this.bankName = bankName;
    }

    /**
     * 获取银行账号
     * 
     * @return 银行账号
     */
    public String getBankAccount()
    {
        return bankAccount;
    }

    /**
     * 设置银行账号
     * 
     * @param bankAccount 银行账号
     */
    public void setBankAccount(String bankAccount)
    {
        this.bankAccount = bankAccount;
    }

    /**
     * 获取币种
     * 
     * @return 币种
     */
    public String getCurrency()
    {
        return currency;
    }

    /**
     * 设置币种
     * 
     * @param currency 币种
     */
    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    /**
     * 获取余额
     * 
     * @return 余额
     */
    public BigDecimal getBalance()
    {
        return balance;
    }

    /**
     * 设置余额
     * 
     * @param balance 余额
     */
    public void setBalance(BigDecimal balance)
    {
        this.balance = balance;
    }

    /**
     * 获取状态
     * 
     * @return 状态
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * 设置状态
     * 
     * @param status 状态
     */
    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("accountId", getAccountId())
            .append("accountNo", getAccountNo())
            .append("accountName", getAccountName())
            .append("accountType", getAccountType())
            .append("bankName", getBankName())
            .append("bankAccount", getBankAccount())
            .append("currency", getCurrency())
            .append("balance", getBalance())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

