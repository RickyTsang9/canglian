package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 客户等级价格实体
 * 
 * @author canglian
 */
public class PriceCustomerLevel extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long levelPriceId;

    private String customerLevel;

    private Long productId;

    private BigDecimal salePrice;

    private String status;

    public Long getLevelPriceId()
    {
        return levelPriceId;
    }

    public void setLevelPriceId(Long levelPriceId)
    {
        this.levelPriceId = levelPriceId;
    }

    public String getCustomerLevel()
    {
        return customerLevel;
    }

    public void setCustomerLevel(String customerLevel)
    {
        this.customerLevel = customerLevel;
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
            .append("levelPriceId", getLevelPriceId())
            .append("customerLevel", getCustomerLevel())
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
