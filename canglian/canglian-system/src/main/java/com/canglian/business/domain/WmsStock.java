package com.canglian.business.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;
import com.canglian.common.annotation.Excel;

public class WmsStock extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long stockId;

    private Long warehouseId;

    @Excel(name = "仓库编码", prompt = "必填，填写仓库档案中的仓库编码")
    private String warehouseCode;

    @Excel(name = "仓库名称", type = Excel.Type.EXPORT)
    private String warehouseName;

    private Long productId;

    @Excel(name = "商品编码", prompt = "必填，填写商品档案中的商品编码")
    private String productCode;

    @Excel(name = "商品名称", type = Excel.Type.EXPORT)
    private String productName;

    private Long locationId;

    @Excel(name = "库位编码", prompt = "选填，留空为默认库位")
    private String locationCode;

    @Excel(name = "库位名称", type = Excel.Type.EXPORT)
    private String locationName;

    @Excel(name = "批次号", prompt = "启用批次管理的商品必填")
    private String batchNo;

    @Excel(name = "库存数量")
    private BigDecimal quantity;

    @Excel(name = "期初单位成本", prompt = "选填，留空则取商品档案参考进价")
    private BigDecimal costPrice;

    @Excel(name = "锁定数量", type = Excel.Type.EXPORT)
    private BigDecimal lockedQuantity;

    @Excel(name = "冻结数量", type = Excel.Type.EXPORT)
    private BigDecimal frozenQuantity;

    @Excel(name = "可用数量", type = Excel.Type.EXPORT)
    private BigDecimal availableQuantity;

    @Excel(name = "最小预警")
    private BigDecimal warningMinQty;

    @Excel(name = "最大预警")
    private BigDecimal warningMaxQty;

    private String warningType;

    private String warningMessage;

    private Integer unsoldDays;

    private Integer warningDays;

    private java.util.Date lastOutboundTime;

    /** 生产日期 */
    @Excel(name = "生产日期", dateFormat = "yyyy-MM-dd", prompt = "选填，格式 yyyy-MM-dd")
    private java.util.Date productionDate;

    /** 有效期至 */
    @Excel(name = "有效期至", dateFormat = "yyyy-MM-dd", prompt = "选填，格式 yyyy-MM-dd")
    private java.util.Date expiryDate;

    /** 效期状态(expired已过期 / expiring临期 / normal正常) */
    private String expiryStatus;

    /** 剩余有效天数 */
    private Integer remainDays;

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

    /**
     * 获取预警类型
     *
     * @return 预警类型
     */
    public String getWarningType()
    {
        return warningType;
    }

    /**
     * 设置预警类型
     *
     * @param warningType 预警类型
     */
    public void setWarningType(String warningType)
    {
        this.warningType = warningType;
    }

    /**
     * 获取预警说明
     *
     * @return 预警说明
     */
    public String getWarningMessage()
    {
        return warningMessage;
    }

    /**
     * 设置预警说明
     *
     * @param warningMessage 预警说明
     */
    public void setWarningMessage(String warningMessage)
    {
        this.warningMessage = warningMessage;
    }

    /**
     * 获取滞销天数
     *
     * @return 滞销天数
     */
    public Integer getUnsoldDays()
    {
        return unsoldDays;
    }

    /**
     * 设置滞销天数
     *
     * @param unsoldDays 滞销天数
     */
    public void setUnsoldDays(Integer unsoldDays)
    {
        this.unsoldDays = unsoldDays;
    }

    /**
     * 获取预警天数
     *
     * @return 预警天数
     */
    public Integer getWarningDays()
    {
        return warningDays;
    }

    /**
     * 设置预警天数
     *
     * @param warningDays 预警天数
     */
    public void setWarningDays(Integer warningDays)
    {
        this.warningDays = warningDays;
    }

    /**
     * 获取最近出库时间
     *
     * @return 最近出库时间
     */
    public java.util.Date getLastOutboundTime()
    {
        return lastOutboundTime;
    }

    /**
     * 设置最近出库时间
     *
     * @param lastOutboundTime 最近出库时间
     */
    public void setLastOutboundTime(java.util.Date lastOutboundTime)
    {
        this.lastOutboundTime = lastOutboundTime;
    }

    public Long getVersion()
    {
        return version;
    }

    public java.util.Date getProductionDate()
    {
        return productionDate;
    }

    public void setProductionDate(java.util.Date productionDate)
    {
        this.productionDate = productionDate;
    }

    public java.util.Date getExpiryDate()
    {
        return expiryDate;
    }

    public void setExpiryDate(java.util.Date expiryDate)
    {
        this.expiryDate = expiryDate;
    }

    public String getExpiryStatus()
    {
        return expiryStatus;
    }

    public void setExpiryStatus(String expiryStatus)
    {
        this.expiryStatus = expiryStatus;
    }

    public Integer getRemainDays()
    {
        return remainDays;
    }

    public void setRemainDays(Integer remainDays)
    {
        this.remainDays = remainDays;
    }

    public void setVersion(Long version)
    {
        this.version = version;
    }

    public String getWarehouseCode() { return warehouseCode; }
    public void setWarehouseCode(String warehouseCode) { this.warehouseCode = warehouseCode; }
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String warehouseName) { this.warehouseName = warehouseName; }
    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getLocationCode() { return locationCode; }
    public void setLocationCode(String locationCode) { this.locationCode = locationCode; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

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
            .append("warningType", getWarningType())
            .append("warningMessage", getWarningMessage())
            .append("unsoldDays", getUnsoldDays())
            .append("warningDays", getWarningDays())
            .append("lastOutboundTime", getLastOutboundTime())
            .append("version", getVersion())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}

