package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.annotation.Excel;

/**
 * 权责发生制经营利润表对象
 *
 * @author canglian
 */
public class FinProfitLossReport
{
    /** 销售出库收入 */
    @Excel(name = "销售出库收入")
    private BigDecimal salesIncomeAmount;

    /** 销售退货冲减 */
    @Excel(name = "销售退货冲减")
    private BigDecimal saleReturnAmount;

    /** 营业净收入（= 销售收入 - 销售退货） */
    @Excel(name = "营业净收入")
    private BigDecimal netIncomeAmount;

    /** 销售成本（取 fin_cost_log 历史真实出库成本减退货成本冲回） */
    @Excel(name = "销售成本")
    private BigDecimal salesCostAmount;

    /** 销售毛利 */
    @Excel(name = "销售毛利")
    private BigDecimal grossProfitAmount;

    /** 毛利率(%) */
    @Excel(name = "毛利率(%)")
    private BigDecimal grossMarginRate;

    /** 期间费用（已确认费用） */
    @Excel(name = "期间费用")
    private BigDecimal expenseAmount;

    /** 坏账损失（已确认坏账） */
    @Excel(name = "坏账损失")
    private BigDecimal badDebtAmount;

    /** 营业净利润 */
    @Excel(name = "营业净利润")
    private BigDecimal netProfitAmount;

    /** 净利率(%) */
    @Excel(name = "净利率(%)")
    private BigDecimal netMarginRate;

    /** 兼容旧字段：收入金额（等于营业净收入） */
    private BigDecimal incomeAmount;

    /** 兼容旧字段：成本费用合计（等于销售成本+期间费用+坏账损失） */
    private BigDecimal costAmount;

    /** 兼容旧字段：利润金额（等于营业净利润） */
    private BigDecimal profitAmount;

    public BigDecimal getSalesIncomeAmount()
    {
        return salesIncomeAmount;
    }

    public void setSalesIncomeAmount(BigDecimal salesIncomeAmount)
    {
        this.salesIncomeAmount = salesIncomeAmount;
    }

    public BigDecimal getSaleReturnAmount()
    {
        return saleReturnAmount;
    }

    public void setSaleReturnAmount(BigDecimal saleReturnAmount)
    {
        this.saleReturnAmount = saleReturnAmount;
    }

    public BigDecimal getNetIncomeAmount()
    {
        return netIncomeAmount;
    }

    public void setNetIncomeAmount(BigDecimal netIncomeAmount)
    {
        this.netIncomeAmount = netIncomeAmount;
    }

    public BigDecimal getSalesCostAmount()
    {
        return salesCostAmount;
    }

    public void setSalesCostAmount(BigDecimal salesCostAmount)
    {
        this.salesCostAmount = salesCostAmount;
    }

    public BigDecimal getGrossProfitAmount()
    {
        return grossProfitAmount;
    }

    public void setGrossProfitAmount(BigDecimal grossProfitAmount)
    {
        this.grossProfitAmount = grossProfitAmount;
    }

    public BigDecimal getGrossMarginRate()
    {
        return grossMarginRate;
    }

    public void setGrossMarginRate(BigDecimal grossMarginRate)
    {
        this.grossMarginRate = grossMarginRate;
    }

    public BigDecimal getExpenseAmount()
    {
        return expenseAmount;
    }

    public void setExpenseAmount(BigDecimal expenseAmount)
    {
        this.expenseAmount = expenseAmount;
    }

    public BigDecimal getBadDebtAmount()
    {
        return badDebtAmount;
    }

    public void setBadDebtAmount(BigDecimal badDebtAmount)
    {
        this.badDebtAmount = badDebtAmount;
    }

    public BigDecimal getNetProfitAmount()
    {
        return netProfitAmount;
    }

    public void setNetProfitAmount(BigDecimal netProfitAmount)
    {
        this.netProfitAmount = netProfitAmount;
    }

    public BigDecimal getNetMarginRate()
    {
        return netMarginRate;
    }

    public void setNetMarginRate(BigDecimal netMarginRate)
    {
        this.netMarginRate = netMarginRate;
    }

    public BigDecimal getIncomeAmount()
    {
        return incomeAmount;
    }

    public void setIncomeAmount(BigDecimal incomeAmount)
    {
        this.incomeAmount = incomeAmount;
    }

    public BigDecimal getCostAmount()
    {
        return costAmount;
    }

    public void setCostAmount(BigDecimal costAmount)
    {
        this.costAmount = costAmount;
    }

    public BigDecimal getProfitAmount()
    {
        return profitAmount;
    }

    public void setProfitAmount(BigDecimal profitAmount)
    {
        this.profitAmount = profitAmount;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("salesIncomeAmount", getSalesIncomeAmount())
            .append("saleReturnAmount", getSaleReturnAmount())
            .append("netIncomeAmount", getNetIncomeAmount())
            .append("salesCostAmount", getSalesCostAmount())
            .append("grossProfitAmount", getGrossProfitAmount())
            .append("grossMarginRate", getGrossMarginRate())
            .append("expenseAmount", getExpenseAmount())
            .append("badDebtAmount", getBadDebtAmount())
            .append("netProfitAmount", getNetProfitAmount())
            .append("netMarginRate", getNetMarginRate())
            .toString();
    }
}
