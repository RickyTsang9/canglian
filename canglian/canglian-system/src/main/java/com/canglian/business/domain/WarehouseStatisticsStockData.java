package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 仓库库存统计对象
 * 
 * @author canglian
 */
public class WarehouseStatisticsStockData
{
    /** 当前库存总量 */
    private BigDecimal totalQuantity;

    /** 当前库存总额 */
    private BigDecimal totalAmount;

    /**
     * 获取当前库存总量
     * 
     * @return 当前库存总量
     */
    public BigDecimal getTotalQuantity()
    {
        return totalQuantity;
    }

    /**
     * 设置当前库存总量
     * 
     * @param totalQuantity 当前库存总量
     */
    public void setTotalQuantity(BigDecimal totalQuantity)
    {
        this.totalQuantity = totalQuantity;
    }

    /**
     * 获取当前库存总额
     * 
     * @return 当前库存总额
     */
    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    /**
     * 设置当前库存总额
     * 
     * @param totalAmount 当前库存总额
     */
    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("totalQuantity", getTotalQuantity())
            .append("totalAmount", getTotalAmount())
            .toString();
    }
}

