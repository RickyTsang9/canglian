package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 入库单明细
 * 
 * @author canglian
 */
public class WmsInboundItem
{
    private Long inboundItemId;

    private Long inboundId;

    private Long productId;

    private Long locationId;

    private String batchNo;

    private BigDecimal quantity;

    private BigDecimal price;

    private BigDecimal amount;

    /**
     * 获取入库明细id
     * 
     * @return 入库明细id
     */
    public Long getInboundItemId()
    {
        return inboundItemId;
    }

    /**
     * 设置入库明细id
     * 
     * @param inboundItemId 入库明细id
     */
    public void setInboundItemId(Long inboundItemId)
    {
        this.inboundItemId = inboundItemId;
    }

    /**
     * 获取入库单id
     * 
     * @return 入库单id
     */
    public Long getInboundId()
    {
        return inboundId;
    }

    /**
     * 设置入库单id
     * 
     * @param inboundId 入库单id
     */
    public void setInboundId(Long inboundId)
    {
        this.inboundId = inboundId;
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
     * 获取入库单明细字符串
     * 
     * @return 入库单明细字符串
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("inboundItemId", getInboundItemId())
            .append("inboundId", getInboundId())
            .append("productId", getProductId())
            .append("locationId", getLocationId())
            .append("batchNo", getBatchNo())
            .append("quantity", getQuantity())
            .append("price", getPrice())
            .append("amount", getAmount())
            .toString();
    }
}

