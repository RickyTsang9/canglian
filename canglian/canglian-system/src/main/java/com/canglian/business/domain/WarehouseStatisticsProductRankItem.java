package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 仓库产品库存排行对象
 * 
 * @author canglian
 */
public class WarehouseStatisticsProductRankItem
{
    /** 产品id */
    private Long productId;

    /** 产品编码 */
    private String productCode;

    /** 产品名称 */
    private String productName;

    /** 计量单位 */
    private String unitName;

    /** 可用库存 */
    private BigDecimal availableQuantity;

    /**
     * 获取产品id
     * 
     * @return 产品id
     */
    public Long getProductId()
    {
        return productId;
    }

    /**
     * 设置产品id
     * 
     * @param productId 产品id
     */
    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    /**
     * 获取产品编码
     * 
     * @return 产品编码
     */
    public String getProductCode()
    {
        return productCode;
    }

    /**
     * 设置产品编码
     * 
     * @param productCode 产品编码
     */
    public void setProductCode(String productCode)
    {
        this.productCode = productCode;
    }

    /**
     * 获取产品名称
     * 
     * @return 产品名称
     */
    public String getProductName()
    {
        return productName;
    }

    /**
     * 设置产品名称
     * 
     * @param productName 产品名称
     */
    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    /**
     * 获取计量单位
     * 
     * @return 计量单位
     */
    public String getUnitName()
    {
        return unitName;
    }

    /**
     * 设置计量单位
     * 
     * @param unitName 计量单位
     */
    public void setUnitName(String unitName)
    {
        this.unitName = unitName;
    }

    /**
     * 获取可用库存
     * 
     * @return 可用库存
     */
    public BigDecimal getAvailableQuantity()
    {
        return availableQuantity;
    }

    /**
     * 设置可用库存
     * 
     * @param availableQuantity 可用库存
     */
    public void setAvailableQuantity(BigDecimal availableQuantity)
    {
        this.availableQuantity = availableQuantity;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("productId", getProductId())
            .append("productCode", getProductCode())
            .append("productName", getProductName())
            .append("unitName", getUnitName())
            .append("availableQuantity", getAvailableQuantity())
            .toString();
    }
}

