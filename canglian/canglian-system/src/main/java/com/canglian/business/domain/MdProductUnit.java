package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 商品多单位与换算率实体 md_product_unit
 *
 * @author canglian
 */
public class MdProductUnit extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long unitId;

    private Long productId;

    private String unitName;

    private String isBaseUnit;

    private BigDecimal convertRatio;

    private String barCode;

    private BigDecimal purchasePrice;

    private BigDecimal salePrice;

    private String status;

    public Long getUnitId()
    {
        return unitId;
    }

    public void setUnitId(Long unitId)
    {
        this.unitId = unitId;
    }

    public Long getProductId()
    {
        return productId;
    }

    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public String getUnitName()
    {
        return unitName;
    }

    public void setUnitName(String unitName)
    {
        this.unitName = unitName;
    }

    public String getIsBaseUnit()
    {
        return isBaseUnit;
    }

    public void setIsBaseUnit(String isBaseUnit)
    {
        this.isBaseUnit = isBaseUnit;
    }

    public BigDecimal getConvertRatio()
    {
        return convertRatio;
    }

    public void setConvertRatio(BigDecimal convertRatio)
    {
        this.convertRatio = convertRatio;
    }

    public String getBarCode()
    {
        return barCode;
    }

    public void setBarCode(String barCode)
    {
        this.barCode = barCode;
    }

    public BigDecimal getPurchasePrice()
    {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice)
    {
        this.purchasePrice = purchasePrice;
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
            .append("unitId", getUnitId())
            .append("productId", getProductId())
            .append("unitName", getUnitName())
            .append("isBaseUnit", getIsBaseUnit())
            .append("convertRatio", getConvertRatio())
            .append("barCode", getBarCode())
            .append("purchasePrice", getPurchasePrice())
            .append("salePrice", getSalePrice())
            .append("status", getStatus())
            .toString();
    }
}
