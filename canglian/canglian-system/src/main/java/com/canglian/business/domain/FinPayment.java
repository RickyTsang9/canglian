package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

public class FinPayment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long paymentId;

    private String paymentNo;

    private Long supplierId;

    private Long payableId;

    private Long fundAccountId;

    private Long deptId;

    private Long ownerUserId;

    private BigDecimal amount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date paymentDate;

    private String payChannel;

    /** 0草稿 1已确认 */
    private String status;

    /** draft/confirmed */
    private String bizStatus;

    private String auditBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    public String getPaymentNo() { return paymentNo; }
    public void setPaymentNo(String paymentNo) { this.paymentNo = paymentNo; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public Long getPayableId() { return payableId; }
    public void setPayableId(Long payableId) { this.payableId = payableId; }
    public Long getFundAccountId() { return fundAccountId; }
    public void setFundAccountId(Long fundAccountId) { this.fundAccountId = fundAccountId; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(Long ownerUserId) { this.ownerUserId = ownerUserId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Date getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Date paymentDate) { this.paymentDate = paymentDate; }
    public String getPayChannel() { return payChannel; }
    public void setPayChannel(String payChannel) { this.payChannel = payChannel; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBizStatus() { return bizStatus; }
    public void setBizStatus(String bizStatus) { this.bizStatus = bizStatus; }
    public String getAuditBy() { return auditBy; }
    public void setAuditBy(String auditBy) { this.auditBy = auditBy; }
    public Date getAuditTime() { return auditTime; }
    public void setAuditTime(Date auditTime) { this.auditTime = auditTime; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("paymentId", getPaymentId())
            .append("paymentNo", getPaymentNo())
            .append("supplierId", getSupplierId())
            .append("payableId", getPayableId())
            .append("fundAccountId", getFundAccountId())
            .append("amount", getAmount())
            .append("status", getStatus())
            .append("bizStatus", getBizStatus())
            .toString();
    }
}
