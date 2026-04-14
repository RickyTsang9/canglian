package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 账龄报表对象
 * 
 * @author canglian
 */
public class FinAgingReport
{
    /** 维度id */
    private Long dimensionId;

    /** 未逾期金额 */
    private BigDecimal notDueAmount;

    /** 0-30天金额 */
    private BigDecimal days0To30Amount;

    /** 31-60天金额 */
    private BigDecimal days31To60Amount;

    /** 61-90天金额 */
    private BigDecimal days61To90Amount;

    /** 90天以上金额 */
    private BigDecimal days90AboveAmount;

    /**
     * 获取维度id
     * 
     * @return 维度id
     */
    public Long getDimensionId()
    {
        return dimensionId;
    }

    /**
     * 设置维度id
     * 
     * @param dimensionId 维度id
     */
    public void setDimensionId(Long dimensionId)
    {
        this.dimensionId = dimensionId;
    }

    /**
     * 获取未逾期金额
     * 
     * @return 未逾期金额
     */
    public BigDecimal getNotDueAmount()
    {
        return notDueAmount;
    }

    /**
     * 设置未逾期金额
     * 
     * @param notDueAmount 未逾期金额
     */
    public void setNotDueAmount(BigDecimal notDueAmount)
    {
        this.notDueAmount = notDueAmount;
    }

    /**
     * 获取0-30天金额
     * 
     * @return 0-30天金额
     */
    public BigDecimal getDays0To30Amount()
    {
        return days0To30Amount;
    }

    /**
     * 设置0-30天金额
     * 
     * @param days0To30Amount 0-30天金额
     */
    public void setDays0To30Amount(BigDecimal days0To30Amount)
    {
        this.days0To30Amount = days0To30Amount;
    }

    /**
     * 获取31-60天金额
     * 
     * @return 31-60天金额
     */
    public BigDecimal getDays31To60Amount()
    {
        return days31To60Amount;
    }

    /**
     * 设置31-60天金额
     * 
     * @param days31To60Amount 31-60天金额
     */
    public void setDays31To60Amount(BigDecimal days31To60Amount)
    {
        this.days31To60Amount = days31To60Amount;
    }

    /**
     * 获取61-90天金额
     * 
     * @return 61-90天金额
     */
    public BigDecimal getDays61To90Amount()
    {
        return days61To90Amount;
    }

    /**
     * 设置61-90天金额
     * 
     * @param days61To90Amount 61-90天金额
     */
    public void setDays61To90Amount(BigDecimal days61To90Amount)
    {
        this.days61To90Amount = days61To90Amount;
    }

    /**
     * 获取90天以上金额
     * 
     * @return 90天以上金额
     */
    public BigDecimal getDays90AboveAmount()
    {
        return days90AboveAmount;
    }

    /**
     * 设置90天以上金额
     * 
     * @param days90AboveAmount 90天以上金额
     */
    public void setDays90AboveAmount(BigDecimal days90AboveAmount)
    {
        this.days90AboveAmount = days90AboveAmount;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("dimensionId", getDimensionId())
            .append("notDueAmount", getNotDueAmount())
            .append("days0To30Amount", getDays0To30Amount())
            .append("days31To60Amount", getDays31To60Amount())
            .append("days61To90Amount", getDays61To90Amount())
            .append("days90AboveAmount", getDays90AboveAmount())
            .toString();
    }
}

