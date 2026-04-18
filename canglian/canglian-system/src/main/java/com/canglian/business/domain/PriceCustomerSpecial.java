package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 客户专属价格实体
 * 
 * @author canglian
 */
public class PriceCustomerSpecial extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long specialPriceId;

    private Long customerId;

    private Long productId;

    private BigDecimal salePrice;

    private String status;

    public Long getSpecialPriceId()
    {
        return specialPriceId;
    }

    public void setSpecialPriceId(Long specialPriceId)
    {
        this.specialPriceId = specialPriceId;
    }

    public Long getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    public Long getProductId()
    {
        return productId;
    }

    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public BigDecimal getSalePrice()
    {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice)
    {
        this.salePrice = salePrice;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("specialPriceId", getSpecialPriceId())
            .append("customerId", getCustomerId())
            .append("productId", getProductId())
            .append("salePrice", getSalePrice())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
