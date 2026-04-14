package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

public class WmsStock extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long stockId;

    private Long warehouseId;

    private Long productId;

    private Long locationId;

    private String batchNo;

    private BigDecimal quantity;

    private BigDecimal lockedQuantity;

    private BigDecimal frozenQuantity;

    private BigDecimal availableQuantity;

    private BigDecimal warningMinQty;

    private BigDecimal warningMaxQty;

    private Long version;

    public Long getStockId()
    {
        return stockId;
    }

    public void setStockId(Long stockId)
    {
        this.stockId = stockId;
    }

    public Long getWarehouseId()
    {
        return warehouseId;
    }

    public void setWarehouseId(Long warehouseId)
    {
        this.warehouseId = warehouseId;
    }

    public Long getProductId()
    {
        return productId;
    }

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

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
    }

    public BigDecimal getLockedQuantity()
    {
        return lockedQuantity;
    }

    public void setLockedQuantity(BigDecimal lockedQuantity)
    {
        this.lockedQuantity = lockedQuantity;
    }

    public BigDecimal getFrozenQuantity()
    {
        return frozenQuantity;
    }

    public void setFrozenQuantity(BigDecimal frozenQuantity)
    {
        this.frozenQuantity = frozenQuantity;
    }

    /**
     * 获取可用库存数量
     * 
     * @return 可用库存数量
     */
    public BigDecimal getAvailableQuantity()
    {
        return availableQuantity;
    }

    /**
     * 设置可用库存数量
     * 
     * @param availableQuantity 可用库存数量
     */
    public void setAvailableQuantity(BigDecimal availableQuantity)
    {
        this.availableQuantity = availableQuantity;
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

    public Long getVersion()
    {
        return version;
    }

    public void setVersion(Long version)
    {
        this.version = version;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("stockId", getStockId())
            .append("warehouseId", getWarehouseId())
            .append("productId", getProductId())
            .append("locationId", getLocationId())
            .append("batchNo", getBatchNo())
            .append("quantity", getQuantity())
            .append("lockedQuantity", getLockedQuantity())
            .append("frozenQuantity", getFrozenQuantity())
            .append("availableQuantity", getAvailableQuantity())
            .append("warningMinQty", getWarningMinQty())
            .append("warningMaxQty", getWarningMaxQty())
            .append("version", getVersion())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

