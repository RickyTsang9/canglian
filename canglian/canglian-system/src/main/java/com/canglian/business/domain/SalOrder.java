package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 销售单据实体
 *
 * @author canglian
 */
public class SalOrder extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long orderId;

    private String orderNo;

    private String orderType;

    private Long customerId;

    private Long warehouseId;

    private Long deptId;

    private Long ownerUserId;

    private String sourceBillType;

    private Long sourceBillId;

    private String sourceBillNo;

    private Date businessDate;

    private Date expectedDate;

    private BigDecimal totalQty;

    private BigDecimal totalAmount;

    private BigDecimal discountAmount;

    private BigDecimal taxTotalAmount;

    private String closeReason;

    private String stockLockedFlag;

    private String status;

    public String getStockLockedFlag() { return stockLockedFlag; }
    public void setStockLockedFlag(String stockLockedFlag) { this.stockLockedFlag = stockLockedFlag; }

    private String bizStatus;

    private String approveBy;

    private Date approveTime;

    /** 汇总已下推/已出库数量（非持久化计算字段） */
    private BigDecimal shippedTotalQty;

    private List<SalOrderItem> orderItemList;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getOrderType() { return orderType; }
    public void setOrderType(String orderType) { this.orderType = orderType; }
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
    public BigDecimal getShippedTotalQty() { return shippedTotalQty; }
    public void setShippedTotalQty(BigDecimal shippedTotalQty) { this.shippedTotalQty = shippedTotalQty; }
    public List<SalOrderItem> getOrderItemList() { return orderItemList; }
    public void setOrderItemList(List<SalOrderItem> orderItemList) { this.orderItemList = orderItemList; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("orderId", getOrderId())
            .append("orderNo", getOrderNo())
            .append("orderType", getOrderType())
            .append("customerId", getCustomerId())
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
