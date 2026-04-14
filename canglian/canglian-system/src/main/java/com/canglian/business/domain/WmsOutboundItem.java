package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 出库单明细
 * 
 * @author canglian
 */
public class WmsOutboundItem
{
    private Long outboundItemId;

    private Long outboundId;

    private Long productId;

    private Long locationId;

    private String batchNo;

    private BigDecimal quantity;

    private BigDecimal price;

    private BigDecimal amount;

    /**
     * 获取出库明细id
     * 
     * @return 出库明细id
     */
    public Long getOutboundItemId()
    {
        return outboundItemId;
    }

    /**
     * 设置出库明细id
     * 
     * @param outboundItemId 出库明细id
     */
    public void setOutboundItemId(Long outboundItemId)
    {
        this.outboundItemId = outboundItemId;
    }

    /**
     * 获取出库单id
     * 
     * @return 出库单id
     */
    public Long getOutboundId()
    {
        return outboundId;
    }

    /**
     * 设置出库单id
     * 
     * @param outboundId 出库单id
     */
    public void setOutboundId(Long outboundId)
    {
        this.outboundId = outboundId;
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

    public Long getLocationId()
    {
        return locationId;
    }

    public void setLocationId(Long locationId)
    {
        this.locationId = locationId;
    }

    public String getBatchNo()
    {
        return batchNo;
    }

    public void setBatchNo(String batchNo)
    {
        this.batchNo = batchNo;
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
     * 获取出库单明细字符串
     * 
     * @return 出库单明细字符串
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("outboundItemId", getOutboundItemId())
            .append("outboundId", getOutboundId())
            .append("productId", getProductId())
            .append("locationId", getLocationId())
            .append("batchNo", getBatchNo())
            .append("quantity", getQuantity())
            .append("price", getPrice())
            .append("amount", getAmount())
            .toString();
    }
}

