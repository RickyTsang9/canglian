package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 销售单据明细实体
 *
 * @author canglian
 */
public class SalOrderItem
{
    private Long orderItemId;

    private Long orderId;

    private Long productId;

    private String unitName;

    private BigDecimal convertRatio;

    private BigDecimal baseQuantity;

    private BigDecimal quantity;

    private BigDecimal shippedQty;

    private BigDecimal price;

    private BigDecimal discountRate;

    private BigDecimal taxRate;

    private BigDecimal taxAmount;

    private BigDecimal amount;

    private String remark;

    public Long getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Long orderItemId) { this.orderItemId = orderItemId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }
    public BigDecimal getConvertRatio() { return convertRatio; }
    public void setConvertRatio(BigDecimal convertRatio) { this.convertRatio = convertRatio; }
    public BigDecimal getBaseQuantity() { return baseQuantity; }
    public void setBaseQuantity(BigDecimal baseQuantity) { this.baseQuantity = baseQuantity; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getShippedQty() { return shippedQty; }
    public void setShippedQty(BigDecimal shippedQty) { this.shippedQty = shippedQty; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getDiscountRate() { return discountRate; }
    public void setDiscountRate(BigDecimal discountRate) { this.discountRate = discountRate; }
    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("orderItemId", getOrderItemId())
            .append("orderId", getOrderId())
            .append("productId", getProductId())
            .append("unitName", getUnitName())
            .append("convertRatio", getConvertRatio())
            .append("baseQuantity", getBaseQuantity())
            .append("quantity", getQuantity())
            .append("shippedQty", getShippedQty())
            .append("price", getPrice())
            .append("discountRate", getDiscountRate())
            .append("taxRate", getTaxRate())
            .append("taxAmount", getTaxAmount())
            .append("amount", getAmount())
            .append("remark", getRemark())
            .toString();
    }
}
