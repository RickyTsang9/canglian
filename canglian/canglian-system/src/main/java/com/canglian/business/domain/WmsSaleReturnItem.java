package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 销售退货明细 实体
 * 
 * @author canglian
 */
public class WmsSaleReturnItem extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long saleReturnItemId;

    private Long saleReturnId;

    private Long productId;

    private BigDecimal quantity;

    private BigDecimal price;

    private BigDecimal amount;

    /**
     * 获取销售退货明细id
     * 
     * @return 销售退货明细id
     */
    public Long getSaleReturnItemId()
    {
        return saleReturnItemId;
    }

    /**
     * 设置销售退货明细id
     * 
     * @param saleReturnItemId 销售退货明细id
     */
    public void setSaleReturnItemId(Long saleReturnItemId)
    {
        this.saleReturnItemId = saleReturnItemId;
    }

    /**
     * 获取销售退货id
     * 
     * @return 销售退货id
     */
    public Long getSaleReturnId()
    {
        return saleReturnId;
    }

    /**
     * 设置销售退货id
     * 
     * @param saleReturnId 销售退货id
     */
    public void setSaleReturnId(Long saleReturnId)
    {
        this.saleReturnId = saleReturnId;
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
            .append("saleReturnItemId", getSaleReturnItemId())
            .append("saleReturnId", getSaleReturnId())
            .append("productId", getProductId())
            .append("quantity", getQuantity())
            .append("price", getPrice())
            .append("amount", getAmount())
            .toString();
    }
}

