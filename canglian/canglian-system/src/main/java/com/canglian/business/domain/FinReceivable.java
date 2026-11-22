package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

public class FinReceivable extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long receivableId;

    private String receivableNo;

    private Long customerId;

    private String billType;

    private Long billId;

    private String sourceBillType;

    private Long sourceBillId;

    private String sourceBillNo;

    private Date businessDate;

    private BigDecimal amount;

    /** 单据方向(1正向应收 2红字退货冲减) */
    private String billDirection;

    private BigDecimal receivedAmount;

    /** 已核销坏账金额（独立核算，避免与现金收款混淆） */
    private BigDecimal badDebtAmount;

    private String status;

    private String bizStatus;

    private Date dueDate;

    public Long getReceivableId()
    {
        return receivableId;
    }

    public void setReceivableId(Long receivableId)
    {
        this.receivableId = receivableId;
    }

    public String getReceivableNo()
    {
        return receivableNo;
    }

    public void setReceivableNo(String receivableNo)
    {
        this.receivableNo = receivableNo;
    }

    public Long getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    public String getBillType()
    {
        return billType;
    }

    public void setBillType(String billType)
    {
        this.billType = billType;
    }

    public Long getBillId()
    {
        return billId;
    }

    public void setBillId(Long billId)
    {
        this.billId = billId;
    }

    public String getSourceBillType()
    {
        return sourceBillType;
    }

    public void setSourceBillType(String sourceBillType)
    {
        this.sourceBillType = sourceBillType;
    }

    public Long getSourceBillId()
    {
        return sourceBillId;
    }

    public void setSourceBillId(Long sourceBillId)
    {
        this.sourceBillId = sourceBillId;
    }

    public String getSourceBillNo()
    {
        return sourceBillNo;
    }

    public void setSourceBillNo(String sourceBillNo)
    {
        this.sourceBillNo = sourceBillNo;
    }

    public Date getBusinessDate()
    {
        return businessDate;
    }

    public void setBusinessDate(Date businessDate)
    {
        this.businessDate = businessDate;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public String getBillDirection()
    {
        return billDirection;
    }

    public void setBillDirection(String billDirection)
    {
        this.billDirection = billDirection;
    }

    public BigDecimal getReceivedAmount()
    {
        return receivedAmount;
    }

    public void setReceivedAmount(BigDecimal receivedAmount)
    {
        this.receivedAmount = receivedAmount;
    }

    public BigDecimal getBadDebtAmount()
    {
        return badDebtAmount;
    }

    public void setBadDebtAmount(BigDecimal badDebtAmount)
    {
        this.badDebtAmount = badDebtAmount;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getBizStatus()
    {
        return bizStatus;
    }

    public void setBizStatus(String bizStatus)
    {
        this.bizStatus = bizStatus;
    }

    public Date getDueDate()
    {
        return dueDate;
    }

    public void setDueDate(Date dueDate)
    {
        this.dueDate = dueDate;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("receivableId", getReceivableId())
            .append("receivableNo", getReceivableNo())
            .append("customerId", getCustomerId())
            .append("billType", getBillType())
            .append("billId", getBillId())
            .append("sourceBillType", getSourceBillType())
            .append("sourceBillId", getSourceBillId())
            .append("sourceBillNo", getSourceBillNo())
            .append("businessDate", getBusinessDate())
            .append("amount", getAmount())
            .append("receivedAmount", getReceivedAmount())
            .append("status", getStatus())
            .append("bizStatus", getBizStatus())
            .append("dueDate", getDueDate())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

