package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 收支汇总对象
 * 
 * @author canglian
 */
public class FinRevenueExpenseReport
{
    /** 收入金额 */
    private BigDecimal incomeAmount;

    /** 支出金额 */
    private BigDecimal expenseAmount;

    /** 净额 */
    private BigDecimal netAmount;

    /**
     * 获取收入金额
     * 
     * @return 收入金额
     */
    public BigDecimal getIncomeAmount()
    {
        return incomeAmount;
    }

    /**
     * 设置收入金额
     * 
     * @param incomeAmount 收入金额
     */
    public void setIncomeAmount(BigDecimal incomeAmount)
    {
        this.incomeAmount = incomeAmount;
    }

    /**
     * 获取支出金额
     * 
     * @return 支出金额
     */
    public BigDecimal getExpenseAmount()
    {
        return expenseAmount;
    }

    /**
     * 设置支出金额
     * 
     * @param expenseAmount 支出金额
     */
    public void setExpenseAmount(BigDecimal expenseAmount)
    {
        this.expenseAmount = expenseAmount;
    }

    /**
     * 获取净额
     * 
     * @return 净额
     */
    public BigDecimal getNetAmount()
    {
        return netAmount;
    }

    /**
     * 设置净额
     * 
     * @param netAmount 净额
     */
    public void setNetAmount(BigDecimal netAmount)
    {
        this.netAmount = netAmount;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("incomeAmount", getIncomeAmount())
            .append("expenseAmount", getExpenseAmount())
            .append("netAmount", getNetAmount())
            .toString();
    }
}

