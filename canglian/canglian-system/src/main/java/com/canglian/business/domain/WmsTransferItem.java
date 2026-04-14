package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 调拨单明细
 * 
 * @author canglian
 */
public class WmsTransferItem
{
    private Long transferItemId;

    private Long transferId;

    private Long productId;

    private Long outLocationId;

    private Long inLocationId;

    private String batchNo;

    private BigDecimal quantity;

    private BigDecimal price;

    private BigDecimal amount;

    /**
     * 获取调拨明细id
     * 
     * @return 调拨明细id
     */
    public Long getTransferItemId()
    {
        return transferItemId;
    }

    /**
     * 设置调拨明细id
     * 
     * @param transferItemId 调拨明细id
     */
    public void setTransferItemId(Long transferItemId)
    {
        this.transferItemId = transferItemId;
    }

    /**
     * 获取调拨单id
     * 
     * @return 调拨单id
     */
    public Long getTransferId()
    {
        return transferId;
    }

    /**
     * 设置调拨单id
     * 
     * @param transferId 调拨单id
     */
    public void setTransferId(Long transferId)
    {
        this.transferId = transferId;
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

    public Long getOutLocationId()
    {
        return outLocationId;
    }

    public void setOutLocationId(Long outLocationId)
    {
        this.outLocationId = outLocationId;
    }

    public Long getInLocationId()
    {
        return inLocationId;
    }

    public void setInLocationId(Long inLocationId)
    {
        this.inLocationId = inLocationId;
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
     * 获取调拨单明细字符串
     * 
     * @return 调拨单明细字符串
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("transferItemId", getTransferItemId())
            .append("transferId", getTransferId())
            .append("productId", getProductId())
            .append("outLocationId", getOutLocationId())
            .append("inLocationId", getInLocationId())
            .append("batchNo", getBatchNo())
            .append("quantity", getQuantity())
            .append("price", getPrice())
            .append("amount", getAmount())
            .toString();
    }
}

