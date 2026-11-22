package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 资金账户内部互转单实体 fin_fund_transfer
 *
 * @author canglian
 */
public class FinFundTransfer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long transferId;

    private String transferNo;

    private Long outAccountId;

    private Long inAccountId;

    private BigDecimal amount;

    private BigDecimal feeAmount;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date transferDate;

    /** 0草稿 1已确认 */
    private String status;

    private String auditBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date auditTime;

    public Long getTransferId() { return transferId; }
    public void setTransferId(Long transferId) { this.transferId = transferId; }
    public String getTransferNo() { return transferNo; }
    public void setTransferNo(String transferNo) { this.transferNo = transferNo; }
    public Long getOutAccountId() { return outAccountId; }
    public void setOutAccountId(Long outAccountId) { this.outAccountId = outAccountId; }
    public Long getInAccountId() { return inAccountId; }
    public void setInAccountId(Long inAccountId) { this.inAccountId = inAccountId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getFeeAmount() { return feeAmount; }
    public void setFeeAmount(BigDecimal feeAmount) { this.feeAmount = feeAmount; }
    public Date getTransferDate() { return transferDate; }
    public void setTransferDate(Date transferDate) { this.transferDate = transferDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAuditBy() { return auditBy; }
    public void setAuditBy(String auditBy) { this.auditBy = auditBy; }
    public Date getAuditTime() { return auditTime; }
    public void setAuditTime(Date auditTime) { this.auditTime = auditTime; }
}
