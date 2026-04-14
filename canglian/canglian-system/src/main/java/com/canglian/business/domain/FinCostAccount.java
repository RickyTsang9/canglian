package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 成本账户对象 fin_cost_account
 * 
 * @author canglian
 */
public class FinCostAccount extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 成本账户id */
    private Long costAccountId;

    /** 商品id */
    private Long productId;

    /** 仓库id */
    private Long warehouseId;

    /** 成本算法 */
    private String costMethod;

    /** 总数量 */
    private BigDecimal totalQty;

    /** 总金额 */
    private BigDecimal totalAmount;

    /** 平均成本 */
    private BigDecimal avgCost;

    /** 最近入库成本单价 */
    private BigDecimal lastCostPrice;

    /** 最近入库数量 */
    private BigDecimal lastCostQty;

    /**
     * 获取成本账户id
     * 
     * @return 成本账户id
     */
    public Long getCostAccountId()
    {
        return costAccountId;
    }

    /**
     * 设置成本账户id
     * 
     * @param costAccountId 成本账户id
     */
    public void setCostAccountId(Long costAccountId)
    {
        this.costAccountId = costAccountId;
    }

    /**
     * 获取商品id
     * 
     * @return 商品id
     */
    public Long getProductId()
    {
        return productId;
    }

    /**
     * 设置商品id
     * 
     * @param productId 商品id
     */
    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    /**
     * 获取仓库id
     * 
     * @return 仓库id
     */
    public Long getWarehouseId()
    {
        return warehouseId;
    }

    /**
     * 设置仓库id
     * 
     * @param warehouseId 仓库id
     */
    public void setWarehouseId(Long warehouseId)
    {
        this.warehouseId = warehouseId;
    }

    /**
     * 获取成本算法
     * 
     * @return 成本算法
     */
    public String getCostMethod()
    {
        return costMethod;
    }

    /**
     * 设置成本算法
     * 
     * @param costMethod 成本算法
     */
    public void setCostMethod(String costMethod)
    {
        this.costMethod = costMethod;
    }

    /**
     * 获取总数量
     * 
     * @return 总数量
     */
    public BigDecimal getTotalQty()
    {
        return totalQty;
    }

    /**
     * 设置总数量
     * 
     * @param totalQty 总数量
     */
    public void setTotalQty(BigDecimal totalQty)
    {
        this.totalQty = totalQty;
    }

    /**
     * 获取总金额
     * 
     * @return 总金额
     */
    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    /**
     * 设置总金额
     * 
     * @param totalAmount 总金额
     */
    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    /**
     * 获取平均成本
     * 
     * @return 平均成本
     */
    public BigDecimal getAvgCost()
    {
        return avgCost;
    }

    /**
     * 设置平均成本
     * 
     * @param avgCost 平均成本
     */
    public void setAvgCost(BigDecimal avgCost)
    {
        this.avgCost = avgCost;
    }

    /**
     * 获取最近入库成本单价
     * 
     * @return 最近入库成本单价
     */
    public BigDecimal getLastCostPrice()
    {
        return lastCostPrice;
    }

    /**
     * 设置最近入库成本单价
     * 
     * @param lastCostPrice 最近入库成本单价
     */
    public void setLastCostPrice(BigDecimal lastCostPrice)
    {
        this.lastCostPrice = lastCostPrice;
    }

    /**
     * 获取最近入库数量
     * 
     * @return 最近入库数量
     */
    public BigDecimal getLastCostQty()
    {
        return lastCostQty;
    }

    /**
     * 设置最近入库数量
     * 
     * @param lastCostQty 最近入库数量
     */
    public void setLastCostQty(BigDecimal lastCostQty)
    {
        this.lastCostQty = lastCostQty;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("costAccountId", getCostAccountId())
            .append("productId", getProductId())
            .append("warehouseId", getWarehouseId())
            .append("costMethod", getCostMethod())
            .append("totalQty", getTotalQty())
            .append("totalAmount", getTotalAmount())
            .append("avgCost", getAvgCost())
            .append("lastCostPrice", getLastCostPrice())
            .append("lastCostQty", getLastCostQty())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

