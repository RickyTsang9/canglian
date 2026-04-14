package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 盘点单明细
 * 
 * @author canglian
 */
public class WmsInventoryCheckItem
{
    private Long checkItemId;

    private Long checkId;

    private Long productId;

    private Long locationId;

    private String batchNo;

    private BigDecimal stockQty;

    private BigDecimal actualQty;

    private BigDecimal diffQty;

    private BigDecimal price;

    private BigDecimal diffAmount;

    /**
     * 获取盘点明细id
     * 
     * @return 盘点明细id
     */
    public Long getCheckItemId()
    {
        return checkItemId;
    }

    /**
     * 设置盘点明细id
     * 
     * @param checkItemId 盘点明细id
     */
    public void setCheckItemId(Long checkItemId)
    {
        this.checkItemId = checkItemId;
    }

    /**
     * 获取盘点单id
     * 
     * @return 盘点单id
     */
    public Long getCheckId()
    {
        return checkId;
    }

    /**
     * 设置盘点单id
     * 
     * @param checkId 盘点单id
     */
    public void setCheckId(Long checkId)
    {
        this.checkId = checkId;
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
     * 获取账面数量
     * 
     * @return 账面数量
     */
    public BigDecimal getStockQty()
    {
        return stockQty;
    }

    /**
     * 设置账面数量
     * 
     * @param stockQty 账面数量
     */
    public void setStockQty(BigDecimal stockQty)
    {
        this.stockQty = stockQty;
    }

    /**
     * 获取实盘数量
     * 
     * @return 实盘数量
     */
    public BigDecimal getActualQty()
    {
        return actualQty;
    }

    /**
     * 设置实盘数量
     * 
     * @param actualQty 实盘数量
     */
    public void setActualQty(BigDecimal actualQty)
    {
        this.actualQty = actualQty;
    }

    /**
     * 获取差异数量
     * 
     * @return 差异数量
     */
    public BigDecimal getDiffQty()
    {
        return diffQty;
    }

    /**
     * 设置差异数量
     * 
     * @param diffQty 差异数量
     */
    public void setDiffQty(BigDecimal diffQty)
    {
        this.diffQty = diffQty;
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
     * 获取差异金额
     * 
     * @return 差异金额
     */
    public BigDecimal getDiffAmount()
    {
        return diffAmount;
    }

    /**
     * 设置差异金额
     * 
     * @param diffAmount 差异金额
     */
    public void setDiffAmount(BigDecimal diffAmount)
    {
        this.diffAmount = diffAmount;
    }

    /**
     * 获取盘点单明细字符串
     * 
     * @return 盘点单明细字符串
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("checkItemId", getCheckItemId())
            .append("checkId", getCheckId())
            .append("productId", getProductId())
            .append("locationId", getLocationId())
            .append("batchNo", getBatchNo())
            .append("stockQty", getStockQty())
            .append("actualQty", getActualQty())
            .append("diffQty", getDiffQty())
            .append("price", getPrice())
            .append("diffAmount", getDiffAmount())
            .toString();
    }
}

