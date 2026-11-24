package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.annotation.Excel;
import com.canglian.common.core.domain.BaseEntity;

public class FinReceipt extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long receiptId;

    @Excel(name = "收款单号")
    private String receiptNo;

    @Excel(name = "客户ID")
    private Long customerId;

    @Excel(name = "应收单ID")
    private Long receivableId;

    @Excel(name = "资金账户ID")
    private Long fundAccountId;

    private Long deptId;

    private Long ownerUserId;

    @Excel(name = "收款金额")
    private BigDecimal amount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "收款日期", dateFormat = "yyyy-MM-dd")
    private Date receiptDate;

    @Excel(name = "结算方式")
    private String payChannel;

    /** 0草稿 1已确认 */
    @Excel(name = "状态", readConverterExp = "0=草稿,1=已确认")
    private String status;

    /** draft/confirmed */
    private String bizStatus;

    @Excel(name = "确认人")
    private String auditBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;

    public Long getReceiptId() { return receiptId; }
    public void setReceiptId(Long receiptId) { this.receiptId = receiptId; }
    public String getReceiptNo() { return receiptNo; }
    public void setReceiptNo(String receiptNo) { this.receiptNo = receiptNo; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getReceivableId() { return receivableId; }
    public void setReceivableId(Long receivableId) { this.receivableId = receivableId; }
    public Long getFundAccountId() { return fundAccountId; }
    public void setFundAccountId(Long fundAccountId) { this.fundAccountId = fundAccountId; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(Long ownerUserId) { this.ownerUserId = ownerUserId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Date getReceiptDate() { return receiptDate; }
    public void setReceiptDate(Date receiptDate) { this.receiptDate = receiptDate; }
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
            .append("receiptId", getReceiptId())
            .append("receiptNo", getReceiptNo())
            .append("customerId", getCustomerId())
            .append("receivableId", getReceivableId())
            .append("fundAccountId", getFundAccountId())
            .append("amount", getAmount())
            .append("status", getStatus())
            .append("bizStatus", getBizStatus())
            .toString();
    }
}
