package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 成本结构对象
 * 
 * @author canglian
 */
public class FinCostStructureReport
{
    /** 成本类型 */
    private String costType;

    /** 成本金额 */
    private BigDecimal costAmount;

    /**
     * 获取成本类型
     * 
     * @return 成本类型
     */
    public String getCostType()
    {
        return costType;
    }

    /**
     * 设置成本类型
     * 
     * @param costType 成本类型
     */
    public void setCostType(String costType)
    {
        this.costType = costType;
    }

    /**
     * 获取成本金额
     * 
     * @return 成本金额
     */
    public BigDecimal getCostAmount()
    {
        return costAmount;
    }

    /**
     * 设置成本金额
     * 
     * @param costAmount 成本金额
     */
    public void setCostAmount(BigDecimal costAmount)
    {
        this.costAmount = costAmount;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("costType", getCostType())
            .append("costAmount", getCostAmount())
            .toString();
    }
}

