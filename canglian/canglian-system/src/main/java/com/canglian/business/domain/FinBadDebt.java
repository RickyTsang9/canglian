package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.annotation.Excel;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 坏账对象 fin_bad_debt
 *
 * @author canglian
 */
public class FinBadDebt extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long badDebtId;

    @Excel(name = "坏账单号")
    private String badDebtNo;

    @Excel(name = "应收单ID")
    private Long receivableId;

    @Excel(name = "客户ID")
    private Long customerId;

    private Long deptId;

    private Long ownerUserId;

    @Excel(name = "坏账金额")
    private BigDecimal amount;

    @Excel(name = "已收回金额")
    private BigDecimal recoveredAmount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date recoverTime;

    private Long recoverAccountId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "坏账日期", dateFormat = "yyyy-MM-dd")
    private Date badDebtDate;

    @Excel(name = "坏账原因")
    private String reason;

    /** 状态：0草稿 1已确认 2已收回 */
    @Excel(name = "状态", readConverterExp = "0=草稿,1=已确认,2=已收回")
    private String status;

    public Long getBadDebtId() { return badDebtId; }
    public void setBadDebtId(Long badDebtId) { this.badDebtId = badDebtId; }
    public String getBadDebtNo() { return badDebtNo; }
    public void setBadDebtNo(String badDebtNo) { this.badDebtNo = badDebtNo; }
    public Long getReceivableId() { return receivableId; }
    public void setReceivableId(Long receivableId) { this.receivableId = receivableId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(Long ownerUserId) { this.ownerUserId = ownerUserId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public BigDecimal getRecoveredAmount() { return recoveredAmount; }
    public void setRecoveredAmount(BigDecimal recoveredAmount) { this.recoveredAmount = recoveredAmount; }
    public Date getRecoverTime() { return recoverTime; }
    public void setRecoverTime(Date recoverTime) { this.recoverTime = recoverTime; }
    public Long getRecoverAccountId() { return recoverAccountId; }
    public void setRecoverAccountId(Long recoverAccountId) { this.recoverAccountId = recoverAccountId; }
    public Date getBadDebtDate() { return badDebtDate; }
    public void setBadDebtDate(Date badDebtDate) { this.badDebtDate = badDebtDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("badDebtId", getBadDebtId())
            .append("badDebtNo", getBadDebtNo())
            .append("receivableId", getReceivableId())
            .append("customerId", getCustomerId())
            .append("amount", getAmount())
            .append("recoveredAmount", getRecoveredAmount())
            .append("status", getStatus())
            .toString();
    }
}
