package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

public class WmsInbound extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long inboundId;

    private String inboundNo;

    private String inboundType;

    private Long supplierId;

    private Long warehouseId;

    private Long deptId;

    private Long ownerUserId;

    private String sourceBillType;

    private Long sourceBillId;

    private String sourceBillNo;

    private Date businessDate;

    private BigDecimal totalQty;

    private BigDecimal totalAmount;

    private BigDecimal discountAmount;

    private BigDecimal taxTotalAmount;

    private String status;

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public BigDecimal getTaxTotalAmount() { return taxTotalAmount; }
    public void setTaxTotalAmount(BigDecimal taxTotalAmount) { this.taxTotalAmount = taxTotalAmount; }

    private String bizStatus;

    private String auditBy;

    private Date auditTime;

    private String isReversal;

    private Long originBillId;

    private String reversedFlag;

    private String unauditBy;

    private Date unauditTime;

    private List<WmsInboundItem> itemList;

    public List<WmsInboundItem> getItemList() { return itemList; }
    public void setItemList(List<WmsInboundItem> itemList) { this.itemList = itemList; }
    public Long getInboundId() { return inboundId; }
    public void setInboundId(Long inboundId) { this.inboundId = inboundId; }
    public String getInboundNo() { return inboundNo; }
    public void setInboundNo(String inboundNo) { this.inboundNo = inboundNo; }
    public String getInboundType() { return inboundType; }
    public void setInboundType(String inboundType) { this.inboundType = inboundType; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("inboundId", getInboundId())
            .append("inboundNo", getInboundNo())
            .append("inboundType", getInboundType())
            .append("supplierId", getSupplierId())
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
