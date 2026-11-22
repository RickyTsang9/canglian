package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 销售退货 实体
 *
 * @author canglian
 */
public class WmsSaleReturn extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long saleReturnId;

    private String returnNo;

    private String returnType;

    private Long customerId;

    private Long warehouseId;

    private Long deptId;

    private Long ownerUserId;

    private String sourceBillType;

    private Long sourceBillId;

    private String sourceBillNo;

    private Date businessDate;

    private BigDecimal totalQty;

    private BigDecimal totalAmount;

    private Long receivableId;

    private String status;

    private String bizStatus;

    private String auditBy;

    private Date auditTime;

    private String isReversal;

    private Long originBillId;

    private String reversedFlag;

    private String unauditBy;

    private Date unauditTime;

    private List<WmsSaleReturnItem> itemList;

    public List<WmsSaleReturnItem> getItemList() { return itemList; }
    public void setItemList(List<WmsSaleReturnItem> itemList) { this.itemList = itemList; }
    public Long getReceivableId() { return receivableId; }
    public void setReceivableId(Long receivableId) { this.receivableId = receivableId; }
    public Long getSaleReturnId() { return saleReturnId; }
    public void setSaleReturnId(Long saleReturnId) { this.saleReturnId = saleReturnId; }
    public String getReturnNo() { return returnNo; }
    public void setReturnNo(String returnNo) { this.returnNo = returnNo; }
    public String getReturnType() { return returnType; }
    public void setReturnType(String returnType) { this.returnType = returnType; }
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

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("saleReturnId", getSaleReturnId())
            .append("returnNo", getReturnNo())
            .append("customerId", getCustomerId())
            .append("warehouseId", getWarehouseId())
            .append("totalQty", getTotalQty())
            .append("totalAmount", getTotalAmount())
            .append("status", getStatus())
            .append("bizStatus", getBizStatus())
            .append("isReversal", getIsReversal())
            .append("reversedFlag", getReversedFlag())
            .toString();
    }
}
