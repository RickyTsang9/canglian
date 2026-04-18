package com.canglian.business.domain;

import java.math.BigDecimal;

/**
 * 扫码调拨明细实体
 * 
 * @author canglian
 */
public class ScanTransferItem
{
    private String barCode;

    private Long productId;

    private String productCode;

    private String productName;

    private BigDecimal quantity;

    private BigDecimal availableQty;

    private BigDecimal costPrice;

    public String getBarCode()
    {
        return barCode;
    }

    public void setBarCode(String barCode)
    {
        this.barCode = barCode;
    }

    public Long getProductId()
    {
        return productId;
    }

    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public String getProductCode()
    {
        return productCode;
    }

    public void setProductCode(String productCode)
    {
        this.productCode = productCode;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
    }

    public BigDecimal getAvailableQty()
    {
        return availableQty;
    }

    public void setAvailableQty(BigDecimal availableQty)
    {
        this.availableQty = availableQty;
    }

    public BigDecimal getCostPrice()
    {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice)
    {
        this.costPrice = costPrice;
    }
}
