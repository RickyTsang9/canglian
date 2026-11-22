package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.annotation.Excel;

/**
 * 现金收支汇总对象（收付实现制）
 *
 * @author canglian
 */
public class FinRevenueExpenseReport
{
    /** 现金收款收入 */
    @Excel(name = "现金收款金额")
    private BigDecimal incomeAmount;

    /** 现金支出金额（采购付款+已确认费用） */
    @Excel(name = "现金支出金额")
    private BigDecimal expenseAmount;

    /** 现金净流量 */
    @Excel(name = "现金净额")
    private BigDecimal netAmount;

    public BigDecimal getIncomeAmount()
    {
        return incomeAmount;
    }

    public void setIncomeAmount(BigDecimal incomeAmount)
    {
        this.incomeAmount = incomeAmount;
    }

    public BigDecimal getExpenseAmount()
    {
        return expenseAmount;
    }

    public void setExpenseAmount(BigDecimal expenseAmount)
    {
        this.expenseAmount = expenseAmount;
    }

    public BigDecimal getNetAmount()
    {
        return netAmount;
    }

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
