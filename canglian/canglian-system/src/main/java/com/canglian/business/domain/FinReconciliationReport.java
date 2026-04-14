package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 对账报表对象
 * 
 * @author canglian
 */
public class FinReconciliationReport
{
    /** 维度id */
    private Long dimensionId;

    /** 应收应付金额 */
    private BigDecimal billAmount;

    /** 已收已付金额 */
    private BigDecimal paidAmount;

    /** 差额 */
    private BigDecimal diffAmount;

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
     * 获取应收应付金额
     * 
     * @return 应收应付金额
     */
    public BigDecimal getBillAmount()
    {
        return billAmount;
    }

    /**
     * 设置应收应付金额
     * 
     * @param billAmount 应收应付金额
     */
    public void setBillAmount(BigDecimal billAmount)
    {
        this.billAmount = billAmount;
    }

    /**
     * 获取已收已付金额
     * 
     * @return 已收已付金额
     */
    public BigDecimal getPaidAmount()
    {
        return paidAmount;
    }

    /**
     * 设置已收已付金额
     * 
     * @param paidAmount 已收已付金额
     */
    public void setPaidAmount(BigDecimal paidAmount)
    {
        this.paidAmount = paidAmount;
    }

    /**
     * 获取差额
     * 
     * @return 差额
     */
    public BigDecimal getDiffAmount()
    {
        return diffAmount;
    }

    /**
     * 设置差额
     * 
     * @param diffAmount 差额
     */
    public void setDiffAmount(BigDecimal diffAmount)
    {
        this.diffAmount = diffAmount;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("dimensionId", getDimensionId())
            .append("billAmount", getBillAmount())
            .append("paidAmount", getPaidAmount())
            .append("diffAmount", getDiffAmount())
            .toString();
    }
}

