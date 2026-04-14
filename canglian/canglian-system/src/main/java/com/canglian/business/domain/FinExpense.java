package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 费用对象 fin_expense
 * 
 * @author canglian
 */
public class FinExpense extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 费用id */
    private Long expenseId;

    /** 费用单号 */
    private String expenseNo;

    /** 费用类型 */
    private String expenseType;

    /** 费用金额 */
    private BigDecimal amount;

    /** 费用日期 */
    private Date expenseDate;

    /** 支付方式 */
    private String payChannel;

    /** 资金账户id */
    private Long fundAccountId;

    /** 状态 */
    private String status;

    /**
     * 获取费用id
     * 
     * @return 费用id
     */
    public Long getExpenseId()
    {
        return expenseId;
    }

    /**
     * 设置费用id
     * 
     * @param expenseId 费用id
     */
    public void setExpenseId(Long expenseId)
    {
        this.expenseId = expenseId;
    }

    /**
     * 获取费用单号
     * 
     * @return 费用单号
     */
    public String getExpenseNo()
    {
        return expenseNo;
    }

    /**
     * 设置费用单号
     * 
     * @param expenseNo 费用单号
     */
    public void setExpenseNo(String expenseNo)
    {
        this.expenseNo = expenseNo;
    }

    /**
     * 获取费用类型
     * 
     * @return 费用类型
     */
    public String getExpenseType()
    {
        return expenseType;
    }

    /**
     * 设置费用类型
     * 
     * @param expenseType 费用类型
     */
    public void setExpenseType(String expenseType)
    {
        this.expenseType = expenseType;
    }

    /**
     * 获取费用金额
     * 
     * @return 费用金额
     */
    public BigDecimal getAmount()
    {
        return amount;
    }

    /**
     * 设置费用金额
     * 
     * @param amount 费用金额
     */
    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    /**
     * 获取费用日期
     * 
     * @return 费用日期
     */
    public Date getExpenseDate()
    {
        return expenseDate;
    }

    /**
     * 设置费用日期
     * 
     * @param expenseDate 费用日期
     */
    public void setExpenseDate(Date expenseDate)
    {
        this.expenseDate = expenseDate;
    }

    /**
     * 获取支付方式
     * 
     * @return 支付方式
     */
    public String getPayChannel()
    {
        return payChannel;
    }

    /**
     * 设置支付方式
     * 
     * @param payChannel 支付方式
     */
    public void setPayChannel(String payChannel)
    {
        this.payChannel = payChannel;
    }

    /**
     * 获取资金账户id
     * 
     * @return 资金账户id
     */
    public Long getFundAccountId()
    {
        return fundAccountId;
    }

    /**
     * 设置资金账户id
     * 
     * @param fundAccountId 资金账户id
     */
    public void setFundAccountId(Long fundAccountId)
    {
        this.fundAccountId = fundAccountId;
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
            .append("expenseId", getExpenseId())
            .append("expenseNo", getExpenseNo())
            .append("expenseType", getExpenseType())
            .append("amount", getAmount())
            .append("expenseDate", getExpenseDate())
            .append("payChannel", getPayChannel())
            .append("fundAccountId", getFundAccountId())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

