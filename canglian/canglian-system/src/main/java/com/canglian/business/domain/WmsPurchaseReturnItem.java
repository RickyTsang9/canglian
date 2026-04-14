package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 采购退货明细 实体
 * 
 * @author canglian
 */
public class WmsPurchaseReturnItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long purchaseReturnItemId;

    private Long purchaseReturnId;

    private Long productId;

    private BigDecimal quantity;

    private BigDecimal price;

    private BigDecimal amount;

    /**
     * 获取采购退货明细id
     * 
     * @return 采购退货明细id
     */
    public Long getPurchaseReturnItemId()
    {
        return purchaseReturnItemId;
    }

    /**
     * 设置采购退货明细id
     * 
     * @param purchaseReturnItemId 采购退货明细id
     */
    public void setPurchaseReturnItemId(Long purchaseReturnItemId)
    {
        this.purchaseReturnItemId = purchaseReturnItemId;
    }

    /**
     * 获取采购退货id
     * 
     * @return 采购退货id
     */
    public Long getPurchaseReturnId()
    {
        return purchaseReturnId;
    }

    /**
     * 设置采购退货id
     * 
     * @param purchaseReturnId 采购退货id
     */
    public void setPurchaseReturnId(Long purchaseReturnId)
    {
        this.purchaseReturnId = purchaseReturnId;
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
     * 获取数量
     * 
     * @return 数量
     */
    public BigDecimal getQuantity()
    {
        return quantity;
    }

    /**
     * 设置数量
     * 
     * @param quantity 数量
     */
    public void setQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
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

    /**
     * 获取对象字符串
     * 
     * @return 对象字符串
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("purchaseReturnItemId", getPurchaseReturnItemId())
            .append("purchaseReturnId", getPurchaseReturnId())
            .append("productId", getProductId())
            .append("quantity", getQuantity())
            .append("price", getPrice())
            .append("amount", getAmount())
            .toString();
    }
}

