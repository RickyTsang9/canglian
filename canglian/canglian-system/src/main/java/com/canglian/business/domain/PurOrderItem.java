package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 购货订单明细实体
 * 
 * @author canglian
 */
public class PurOrderItem
{
    private Long purchaseOrderItemId;

    private Long purchaseOrderId;

    private Long productId;

    private BigDecimal quantity;

    private BigDecimal price;

    private BigDecimal amount;

    private String remark;

    public Long getPurchaseOrderItemId()
    {
        return purchaseOrderItemId;
    }

    public void setPurchaseOrderItemId(Long purchaseOrderItemId)
    {
        this.purchaseOrderItemId = purchaseOrderItemId;
    }

    public Long getPurchaseOrderId()
    {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId)
    {
        this.purchaseOrderId = purchaseOrderId;
    }

    public Long getProductId()
    {
        return productId;
    }

    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("purchaseOrderItemId", getPurchaseOrderItemId())
            .append("purchaseOrderId", getPurchaseOrderId())
            .append("productId", getProductId())
            .append("quantity", getQuantity())
            .append("price", getPrice())
            .append("amount", getAmount())
            .append("remark", getRemark())
            .toString();
    }
}
