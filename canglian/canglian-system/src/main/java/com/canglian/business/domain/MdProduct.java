package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

public class MdProduct extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long productId;

    private String productCode;

    private String productName;

    private String productSpec;

    private String unitName;

    private String purchaseUnit;

    private String saleUnit;

    private String baseUnit;

    private BigDecimal unitConvertRatio;

    private String enableBatch;

    private Integer shelfLifeDays;

    private String enableSerial;

    private BigDecimal warningMinQty;

    private BigDecimal warningMaxQty;

    private String barCode;

    private String categoryName;

    private String brandName;

    private String productImage;

    private BigDecimal costPrice;

    private BigDecimal salePrice;

    private String status;

    private String delFlag;

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

    public String getProductSpec()
    {
        return productSpec;
    }

    public void setProductSpec(String productSpec)
    {
        this.productSpec = productSpec;
    }

    public String getUnitName()
    {
        return unitName;
    }

    public void setUnitName(String unitName)
    {
        this.unitName = unitName;
    }

    public String getPurchaseUnit()
    {
        return purchaseUnit;
    }

    public void setPurchaseUnit(String purchaseUnit)
    {
        this.purchaseUnit = purchaseUnit;
    }

    public String getSaleUnit()
    {
        return saleUnit;
    }

    public void setSaleUnit(String saleUnit)
    {
        this.saleUnit = saleUnit;
    }

    public String getBaseUnit()
    {
        return baseUnit;
    }

    public void setBaseUnit(String baseUnit)
    {
        this.baseUnit = baseUnit;
    }

    public BigDecimal getUnitConvertRatio()
    {
        return unitConvertRatio;
    }

    public void setUnitConvertRatio(BigDecimal unitConvertRatio)
    {
        this.unitConvertRatio = unitConvertRatio;
    }

    public String getEnableBatch()
    {
        return enableBatch;
    }

    public void setEnableBatch(String enableBatch)
    {
        this.enableBatch = enableBatch;
    }

    public Integer getShelfLifeDays()
    {
        return shelfLifeDays;
    }

    public void setShelfLifeDays(Integer shelfLifeDays)
    {
        this.shelfLifeDays = shelfLifeDays;
    }

    public String getEnableSerial()
    {
        return enableSerial;
    }

    public void setEnableSerial(String enableSerial)
    {
        this.enableSerial = enableSerial;
    }

    public BigDecimal getWarningMinQty()
    {
        return warningMinQty;
    }

    public void setWarningMinQty(BigDecimal warningMinQty)
    {
        this.warningMinQty = warningMinQty;
    }

    public BigDecimal getWarningMaxQty()
    {
        return warningMaxQty;
    }

    public void setWarningMaxQty(BigDecimal warningMaxQty)
    {
        this.warningMaxQty = warningMaxQty;
    }

    public String getBarCode()
    {
        return barCode;
    }

    public void setBarCode(String barCode)
    {
        this.barCode = barCode;
    }

    public String getCategoryName()
    {
        return categoryName;
    }

    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    public String getBrandName()
    {
        return brandName;
    }

    public void setBrandName(String brandName)
    {
        this.brandName = brandName;
    }

    public String getProductImage()
    {
        return productImage;
    }

    public void setProductImage(String productImage)
    {
        this.productImage = productImage;
    }

    public BigDecimal getCostPrice()
    {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice)
    {
        this.costPrice = costPrice;
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

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("productId", getProductId())
            .append("productCode", getProductCode())
            .append("productName", getProductName())
            .append("productSpec", getProductSpec())
            .append("unitName", getUnitName())
            .append("purchaseUnit", getPurchaseUnit())
            .append("saleUnit", getSaleUnit())
            .append("baseUnit", getBaseUnit())
            .append("unitConvertRatio", getUnitConvertRatio())
            .append("enableBatch", getEnableBatch())
            .append("shelfLifeDays", getShelfLifeDays())
            .append("enableSerial", getEnableSerial())
            .append("warningMinQty", getWarningMinQty())
            .append("warningMaxQty", getWarningMaxQty())
            .append("barCode", getBarCode())
            .append("categoryName", getCategoryName())
            .append("brandName", getBrandName())
            .append("productImage", getProductImage())
            .append("costPrice", getCostPrice())
            .append("salePrice", getSalePrice())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

