package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.annotation.Excel;
import com.canglian.common.core.domain.BaseEntity;

public class WmsOutbound extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long outboundId;

    @Excel(name = "出库单号")
    private String outboundNo;

    @Excel(name = "出库类型", readConverterExp = "sale=销售出库,return=退货出库,other=其他出库")
    private String outboundType;

    @Excel(name = "客户ID")
    private Long customerId;

    @Excel(name = "仓库ID")
    private Long warehouseId;

    private Long deptId;

    private Long ownerUserId;

    private String sourceBillType;

    private Long sourceBillId;

    @Excel(name = "来源单号")
    private String sourceBillNo;

    @Excel(name = "业务日期", dateFormat = "yyyy-MM-dd")
    private Date businessDate;

    @Excel(name = "出库总数")
    private BigDecimal totalQty;

    @Excel(name = "出库总额")
    private BigDecimal totalAmount;

    private BigDecimal discountAmount;

    @Excel(name = "税额")
    private BigDecimal taxTotalAmount;

    private String verifiedFlag;

    private String verifiedBy;

    private Date verifiedTime;

    @Excel(name = "审核状态", readConverterExp = "0=草稿,1=已审核")
    private String status;

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public BigDecimal getTaxTotalAmount() { return taxTotalAmount; }
    public void setTaxTotalAmount(BigDecimal taxTotalAmount) { this.taxTotalAmount = taxTotalAmount; }
    public String getVerifiedFlag() { return verifiedFlag; }
    public void setVerifiedFlag(String verifiedFlag) { this.verifiedFlag = verifiedFlag; }
    public String getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(String verifiedBy) { this.verifiedBy = verifiedBy; }
    public Date getVerifiedTime() { return verifiedTime; }
    public void setVerifiedTime(Date verifiedTime) { this.verifiedTime = verifiedTime; }

    private String bizStatus;

    @Excel(name = "审核人")
    private String auditBy;

    private Date auditTime;

    private String isReversal;

    private Long originBillId;

    private String reversedFlag;

    private String unauditBy;

    private Date unauditTime;

    private String platformType;

    private Long storeId;

    private String sourceOrderNo;

    @Excel(name = "物流承运商")
    private String carrier;

    @Excel(name = "运单号")
    private String waybillNo;

    private BigDecimal freightCost;

    @Excel(name = "物流状态", readConverterExp = "0=待发货,1=已发货,2=已签收,3=已整单退货")
    private String deliveryStatus;

    private List<WmsOutboundItem> itemList;

    public List<WmsOutboundItem> getItemList() { return itemList; }
    public void setItemList(List<WmsOutboundItem> itemList) { this.itemList = itemList; }
    public Long getOutboundId() { return outboundId; }
    public void setOutboundId(Long outboundId) { this.outboundId = outboundId; }
    public String getOutboundNo() { return outboundNo; }
    public void setOutboundNo(String outboundNo) { this.outboundNo = outboundNo; }
    public String getOutboundType() { return outboundType; }
    public void setOutboundType(String outboundType) { this.outboundType = outboundType; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getWarehouseId() { return warehouseId; }
    public void setWarehouseId(Long warehouseId) { this.warehouseId = warehouseId; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(Long ownerUserId) { this.ownerUserId = ownerUserId; }
    public String getSourceBillType() { return sourceBillType; }
    public void setSourceBillType(String sourceBillType) { this.sourceBillType = sourceBillType; }
    public Long getSourceBillId() { return sourceBillId; }
    public void setSourceBillId(Long sourceBillId) { this.sourceBillId = sourceBillId; }
    public String getSourceBillNo() { return sourceBillNo; }
    public void setSourceBillNo(String sourceBillNo) { this.sourceBillNo = sourceBillNo; }
    public Date getBusinessDate() { return businessDate; }
    public void setBusinessDate(Date businessDate) { this.businessDate = businessDate; }
    public BigDecimal getTotalQty() { return totalQty; }
    public void setTotalQty(BigDecimal totalQty) { this.totalQty = totalQty; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBizStatus() { return bizStatus; }
    public void setBizStatus(String bizStatus) { this.bizStatus = bizStatus; }
    public String getAuditBy() { return auditBy; }
    public void setAuditBy(String auditBy) { this.auditBy = auditBy; }
    public Date getAuditTime() { return auditTime; }
    public void setAuditTime(Date auditTime) { this.auditTime = auditTime; }
    public String getIsReversal() { return isReversal; }
    public void setIsReversal(String isReversal) { this.isReversal = isReversal; }
    public Long getOriginBillId() { return originBillId; }
    public void setOriginBillId(Long originBillId) { this.originBillId = originBillId; }
    public String getReversedFlag() { return reversedFlag; }
    public void setReversedFlag(String reversedFlag) { this.reversedFlag = reversedFlag; }
    public String getUnauditBy() { return unauditBy; }
    public void setUnauditBy(String unauditBy) { this.unauditBy = unauditBy; }
    public Date getUnauditTime() { return unauditTime; }
    public void setUnauditTime(Date unauditTime) { this.unauditTime = unauditTime; }
    public String getPlatformType() { return platformType; }
    public void setPlatformType(String platformType) { this.platformType = platformType; }
    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }
    public String getSourceOrderNo() { return sourceOrderNo; }
    public void setSourceOrderNo(String sourceOrderNo) { this.sourceOrderNo = sourceOrderNo; }
    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
    public String getWaybillNo() { return waybillNo; }
    public void setWaybillNo(String waybillNo) { this.waybillNo = waybillNo; }
    public BigDecimal getFreightCost() { return freightCost; }
    public void setFreightCost(BigDecimal freightCost) { this.freightCost = freightCost; }
    public String getDeliveryStatus() { return deliveryStatus; }
    public void setDeliveryStatus(String deliveryStatus) { this.deliveryStatus = deliveryStatus; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("outboundId", getOutboundId())
            .append("outboundNo", getOutboundNo())
            .append("outboundType", getOutboundType())
            .append("customerId", getCustomerId())
            .append("warehouseId", getWarehouseId())
            .append("deptId", getDeptId())
            .append("ownerUserId", getOwnerUserId())
            .append("sourceBillType", getSourceBillType())
            .append("sourceBillId", getSourceBillId())
            .append("sourceBillNo", getSourceBillNo())
            .append("businessDate", getBusinessDate())
            .append("totalQty", getTotalQty())
            .append("totalAmount", getTotalAmount())
            .append("status", getStatus())
            .append("bizStatus", getBizStatus())
            .append("auditBy", getAuditBy())
            .append("auditTime", getAuditTime())
            .append("isReversal", getIsReversal())
            .append("originBillId", getOriginBillId())
            .append("reversedFlag", getReversedFlag())
            .append("unauditBy", getUnauditBy())
            .append("unauditTime", getUnauditTime())
            .toString();
    }
}
