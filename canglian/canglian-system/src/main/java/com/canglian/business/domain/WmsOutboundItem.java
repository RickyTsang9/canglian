package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 出库单明细
 *
 * @author canglian
 */
public class WmsOutboundItem
{
    private Long outboundItemId;

    private Long outboundId;

    private Long sourceItemId;

    private Long productId;

    private Long locationId;

    private String batchNo;

    private String unitName;

    private BigDecimal convertRatio;

    private BigDecimal baseQuantity;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date productionDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date expiryDate;

    private BigDecimal quantity;

    private BigDecimal price;

    private BigDecimal discountRate;

    private BigDecimal taxRate;

    private BigDecimal taxAmount;

    private BigDecimal amount;

    private String snCodes;

    public BigDecimal getDiscountRate() { return discountRate; }
    public void setDiscountRate(BigDecimal discountRate) { this.discountRate = discountRate; }
    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public String getSnCodes() { return snCodes; }
    public void setSnCodes(String snCodes) { this.snCodes = snCodes; }

    public Long getOutboundItemId() { return outboundItemId; }
    public void setOutboundItemId(Long outboundItemId) { this.outboundItemId = outboundItemId; }
    public Long getOutboundId() { return outboundId; }
    public void setOutboundId(Long outboundId) { this.outboundId = outboundId; }
    public Long getSourceItemId() { return sourceItemId; }
    public void setSourceItemId(Long sourceItemId) { this.sourceItemId = sourceItemId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public String getUnitName() { return unitName; }
    public void setUnitName(String unitName) { this.unitName = unitName; }
    public BigDecimal getConvertRatio() { return convertRatio; }
    public void setConvertRatio(BigDecimal convertRatio) { this.convertRatio = convertRatio; }
    public BigDecimal getBaseQuantity() { return baseQuantity; }
    public void setBaseQuantity(BigDecimal baseQuantity) { this.baseQuantity = baseQuantity; }
    public Date getProductionDate() { return productionDate; }
    public void setProductionDate(Date productionDate) { this.productionDate = productionDate; }
    public Date getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Date expiryDate) { this.expiryDate = expiryDate; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("outboundItemId", getOutboundItemId())
            .append("outboundId", getOutboundId())
            .append("sourceItemId", getSourceItemId())
            .append("productId", getProductId())
            .append("locationId", getLocationId())
            .append("batchNo", getBatchNo())
            .append("unitName", getUnitName())
            .append("convertRatio", getConvertRatio())
            .append("baseQuantity", getBaseQuantity())
            .append("productionDate", getProductionDate())
            .append("expiryDate", getExpiryDate())
            .append("quantity", getQuantity())
            .append("price", getPrice())
            .append("amount", getAmount())
            .toString();
    }
}
