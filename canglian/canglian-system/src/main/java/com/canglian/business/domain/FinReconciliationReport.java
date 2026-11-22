package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.annotation.Excel;

/**
 * 对账报表对象
 *
 * @author canglian
 */
public class FinReconciliationReport
{
    /** 维度id */
    private Long dimensionId;

    /** 往来单位名称 */
    @Excel(name = "往来单位")
    private String dimensionName;

    /** 应收应付净额 */
    @Excel(name = "应收/应付净额")
    private BigDecimal billAmount;

    /** 已收已付金额 */
    @Excel(name = "已收/已付金额")
    private BigDecimal paidAmount;

    /** 未结差额 */
    @Excel(name = "未结差额")
    private BigDecimal diffAmount;

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

    public BigDecimal getBillAmount()
    {
        return billAmount;
    }

    public void setBillAmount(BigDecimal billAmount)
    {
        this.billAmount = billAmount;
    }

    public BigDecimal getPaidAmount()
    {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount)
    {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getDiffAmount()
    {
        return diffAmount;
    }

    public void setDiffAmount(BigDecimal diffAmount)
    {
        this.diffAmount = diffAmount;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("dimensionId", getDimensionId())
            .append("dimensionName", getDimensionName())
            .append("billAmount", getBillAmount())
            .append("paidAmount", getPaidAmount())
            .append("diffAmount", getDiffAmount())
            .toString();
    }
}
