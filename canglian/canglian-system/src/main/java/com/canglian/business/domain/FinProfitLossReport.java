package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 利润表对象
 * 
 * @author canglian
 */
public class FinProfitLossReport
{
    /** 收入金额 */
    private BigDecimal incomeAmount;

    /** 成本费用金额 */
    private BigDecimal costAmount;

    /** 利润金额 */
    private BigDecimal profitAmount;

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
     * 获取成本费用金额
     * 
     * @return 成本费用金额
     */
    public BigDecimal getCostAmount()
    {
        return costAmount;
    }

    /**
     * 设置成本费用金额
     * 
     * @param costAmount 成本费用金额
     */
    public void setCostAmount(BigDecimal costAmount)
    {
        this.costAmount = costAmount;
    }

    /**
     * 获取利润金额
     * 
     * @return 利润金额
     */
    public BigDecimal getProfitAmount()
    {
        return profitAmount;
    }

    /**
     * 设置利润金额
     * 
     * @param profitAmount 利润金额
     */
    public void setProfitAmount(BigDecimal profitAmount)
    {
        this.profitAmount = profitAmount;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("incomeAmount", getIncomeAmount())
            .append("costAmount", getCostAmount())
            .append("profitAmount", getProfitAmount())
            .toString();
    }
}

