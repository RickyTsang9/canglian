package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.annotation.Excel;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 购货订单实体
 *
 * @author canglian
 */
public class PurOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long purchaseOrderId;

    @Excel(name = "采购订单号")
    private String purchaseOrderNo;

    @Excel(name = "供应商ID")
    private Long supplierId;

    @Excel(name = "仓库ID")
    private Long warehouseId;

    private Long deptId;

    private Long ownerUserId;

    private String sourceBillType;

    private Long sourceBillId;

    private String sourceBillNo;

    @Excel(name = "业务日期", dateFormat = "yyyy-MM-dd")
    private Date businessDate;

    @Excel(name = "预计到货日", dateFormat = "yyyy-MM-dd")
    private Date expectedDate;

    @Excel(name = "订单数量")
    private BigDecimal totalQty;

    @Excel(name = "含税总额")
    private BigDecimal totalAmount;

    @Excel(name = "折扣金额")
    private BigDecimal discountAmount;

    @Excel(name = "税额")
    private BigDecimal taxTotalAmount;

    private String closeReason;

    private String status;

    @Excel(name = "业务状态", readConverterExp = "draft=草稿,approved=已审核,partial_received=部分入库,received=已入库,closed=已关闭")
    private String bizStatus;

    @Excel(name = "审核人")
    private String approveBy;

    private Date approveTime;

    /** 汇总已下推/已入库数量（列表进度展示，非持久化计算字段） */
    @Excel(name = "已入库数")
    private BigDecimal receivedTotalQty;

    private List<PurOrderItem> orderItemList;

    public Long getPurchaseOrderId() { return purchaseOrderId; }
    public void setPurchaseOrderId(Long purchaseOrderId) { this.purchaseOrderId = purchaseOrderId; }
    public String getPurchaseOrderNo() { return purchaseOrderNo; }
    public void setPurchaseOrderNo(String purchaseOrderNo) { this.purchaseOrderNo = purchaseOrderNo; }
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
    public Date getExpectedDate() { return expectedDate; }
    public void setExpectedDate(Date expectedDate) { this.expectedDate = expectedDate; }
    public BigDecimal getTotalQty() { return totalQty; }
    public void setTotalQty(BigDecimal totalQty) { this.totalQty = totalQty; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
    public BigDecimal getTaxTotalAmount() { return taxTotalAmount; }
    public void setTaxTotalAmount(BigDecimal taxTotalAmount) { this.taxTotalAmount = taxTotalAmount; }
    public String getCloseReason() { return closeReason; }
    public void setCloseReason(String closeReason) { this.closeReason = closeReason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBizStatus() { return bizStatus; }
    public void setBizStatus(String bizStatus) { this.bizStatus = bizStatus; }
    public String getApproveBy() { return approveBy; }
    public void setApproveBy(String approveBy) { this.approveBy = approveBy; }
    public Date getApproveTime() { return approveTime; }
    public void setApproveTime(Date approveTime) { this.approveTime = approveTime; }
    public BigDecimal getReceivedTotalQty() { return receivedTotalQty; }
    public void setReceivedTotalQty(BigDecimal receivedTotalQty) { this.receivedTotalQty = receivedTotalQty; }
    public List<PurOrderItem> getOrderItemList() { return orderItemList; }
    public void setOrderItemList(List<PurOrderItem> orderItemList) { this.orderItemList = orderItemList; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("purchaseOrderId", getPurchaseOrderId())
            .append("purchaseOrderNo", getPurchaseOrderNo())
            .append("supplierId", getSupplierId())
            .append("warehouseId", getWarehouseId())
            .append("deptId", getDeptId())
            .append("ownerUserId", getOwnerUserId())
            .append("sourceBillType", getSourceBillType())
            .append("sourceBillId", getSourceBillId())
            .append("sourceBillNo", getSourceBillNo())
            .append("businessDate", getBusinessDate())
            .append("expectedDate", getExpectedDate())
            .append("totalQty", getTotalQty())
            .append("totalAmount", getTotalAmount())
            .append("discountAmount", getDiscountAmount())
            .append("taxTotalAmount", getTaxTotalAmount())
            .append("closeReason", getCloseReason())
            .append("status", getStatus())
            .append("bizStatus", getBizStatus())
            .append("approveBy", getApproveBy())
            .append("approveTime", getApproveTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
