package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.annotation.Excel;

/**
 * 成本结构对象
 *
 * @author canglian
 */
public class FinCostStructureReport
{
    /** 成本类型 */
    @Excel(name = "成本/费用类型")
    private String costType;

    /** 成本金额 */
    @Excel(name = "金额")
    private BigDecimal costAmount;

    public String getCostType()
    {
        return costType;
    }

    public void setCostType(String costType)
    {
        this.costType = costType;
    }

    public BigDecimal getCostAmount()
    {
        return costAmount;
    }

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
