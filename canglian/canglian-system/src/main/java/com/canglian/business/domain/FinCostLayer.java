package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 成本分层对象 fin_cost_layer
 * 
 * @author canglian
 */
public class FinCostLayer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 成本分层id */
    private Long costLayerId;

    /** 成本账户id */
    private Long costAccountId;

    /** 商品id */
    private Long productId;

    /** 仓库id */
    private Long warehouseId;

    /** 入库数量 */
    private BigDecimal quantity;

    /** 剩余数量 */
    private BigDecimal remainingQty;

    /** 单价 */
    private BigDecimal price;

    /** 金额 */
    private BigDecimal amount;

    /**
     * 获取成本分层id
     * 
     * @return 成本分层id
     */
    public Long getCostLayerId()
    {
        return costLayerId;
    }

    /**
     * 设置成本分层id
     * 
     * @param costLayerId 成本分层id
     */
    public void setCostLayerId(Long costLayerId)
    {
        this.costLayerId = costLayerId;
    }

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
     * 获取入库数量
     * 
     * @return 入库数量
     */
    public BigDecimal getQuantity()
    {
        return quantity;
    }

    /**
     * 设置入库数量
     * 
     * @param quantity 入库数量
     */
    public void setQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
    }

    /**
     * 获取剩余数量
     * 
     * @return 剩余数量
     */
    public BigDecimal getRemainingQty()
    {
        return remainingQty;
    }

    /**
     * 设置剩余数量
     * 
     * @param remainingQty 剩余数量
     */
    public void setRemainingQty(BigDecimal remainingQty)
    {
        this.remainingQty = remainingQty;
    }

    /**
     * 获取单价
     * 
     * @return 单价
     */
    public BigDecimal getPrice()
    {
        return price;
    }

    /**
     * 设置单价
     * 
     * @param price 单价
     */
    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    /**
     * 获取金额
     * 
     * @return 金额
     */
    public BigDecimal getAmount()
    {
        return amount;
    }

    /**
     * 设置金额
     * 
     * @param amount 金额
     */
    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("costLayerId", getCostLayerId())
            .append("costAccountId", getCostAccountId())
            .append("productId", getProductId())
            .append("warehouseId", getWarehouseId())
            .append("quantity", getQuantity())
            .append("remainingQty", getRemainingQty())
            .append("price", getPrice())
            .append("amount", getAmount())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .toString();
    }
}

