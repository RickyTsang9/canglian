package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.annotation.Excel;

/**
 * 账龄报表对象
 *
 * @author canglian
 */
public class FinAgingReport
{
    /** 维度id */
    private Long dimensionId;

    /** 往来单位名称 */
    @Excel(name = "往来单位")
    private String dimensionName;

    /** 未逾期金额 */
    @Excel(name = "未逾期金额")
    private BigDecimal notDueAmount;

    /** 0-30天金额 */
    @Excel(name = "0-30天逾期")
    private BigDecimal days0To30Amount;

    /** 31-60天金额 */
    @Excel(name = "31-60天逾期")
    private BigDecimal days31To60Amount;

    /** 61-90天金额 */
    @Excel(name = "61-90天逾期")
    private BigDecimal days61To90Amount;

    /** 90天以上金额 */
    @Excel(name = "90天以上逾期")
    private BigDecimal days90AboveAmount;

    /** 红字退货待抵扣余额（负数或0） */
    @Excel(name = "红字待抵扣余额")
    private BigDecimal creditBalance;

    /** 净欠款余额 */
    @Excel(name = "净欠款余额")
    private BigDecimal netBalance;

    public Long getDimensionId()
    {
        return dimensionId;
    }

    public void setDimensionId(Long dimensionId)
    {
        this.dimensionId = dimensionId;
    }

    public String getDimensionName()
    {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName)
    {
        this.dimensionName = dimensionName;
    }

    public BigDecimal getNotDueAmount()
    {
        return notDueAmount;
    }

    public void setNotDueAmount(BigDecimal notDueAmount)
    {
        this.notDueAmount = notDueAmount;
    }

    public BigDecimal getDays0To30Amount()
    {
        return days0To30Amount;
    }

    public void setDays0To30Amount(BigDecimal days0To30Amount)
    {
        this.days0To30Amount = days0To30Amount;
    }

    public BigDecimal getDays31To60Amount()
    {
        return days31To60Amount;
    }

    public void setDays31To60Amount(BigDecimal days31To60Amount)
    {
        this.days31To60Amount = days31To60Amount;
    }

    public BigDecimal getDays61To90Amount()
    {
        return days61To90Amount;
    }

    public void setDays61To90Amount(BigDecimal days61To90Amount)
    {
        this.days61To90Amount = days61To90Amount;
    }

    public BigDecimal getDays90AboveAmount()
    {
        return days90AboveAmount;
    }

    public void setDays90AboveAmount(BigDecimal days90AboveAmount)
    {
        this.days90AboveAmount = days90AboveAmount;
    }

    public BigDecimal getCreditBalance()
    {
        return creditBalance;
    }

    public void setCreditBalance(BigDecimal creditBalance)
    {
        this.creditBalance = creditBalance;
    }

    public BigDecimal getNetBalance()
    {
        return netBalance;
    }

    public void setNetBalance(BigDecimal netBalance)
    {
        this.netBalance = netBalance;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("dimensionId", getDimensionId())
            .append("dimensionName", getDimensionName())
            .append("notDueAmount", getNotDueAmount())
            .append("days0To30Amount", getDays0To30Amount())
            .append("days31To60Amount", getDays31To60Amount())
            .append("days61To90Amount", getDays61To90Amount())
            .append("days90AboveAmount", getDays90AboveAmount())
            .append("creditBalance", getCreditBalance())
            .append("netBalance", getNetBalance())
            .toString();
    }
}
