package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 仓库统计卡片对象
 * 
 * @author canglian
 */
public class WarehouseStatisticsCardData
{
    /** 单据数量 */
    private Long billCount;

    /** 产品总数量 */
    private BigDecimal totalQuantity;

    /** 产品总金额 */
    private BigDecimal totalAmount;

    /**
     * 获取单据数量
     * 
     * @return 单据数量
     */
    public Long getBillCount()
    {
        return billCount;
    }

    /**
     * 设置单据数量
     * 
     * @param billCount 单据数量
     */
    public void setBillCount(Long billCount)
    {
        this.billCount = billCount;
    }

    /**
     * 获取产品总数量
     * 
     * @return 产品总数量
     */
    public BigDecimal getTotalQuantity()
    {
        return totalQuantity;
    }

    /**
     * 设置产品总数量
     * 
     * @param totalQuantity 产品总数量
     */
    public void setTotalQuantity(BigDecimal totalQuantity)
    {
        this.totalQuantity = totalQuantity;
    }

    /**
     * 获取产品总金额
     * 
     * @return 产品总金额
     */
    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    /**
     * 设置产品总金额
     * 
     * @param totalAmount 产品总金额
     */
    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("billCount", getBillCount())
            .append("totalQuantity", getTotalQuantity())
            .append("totalAmount", getTotalAmount())
            .toString();
    }
}

