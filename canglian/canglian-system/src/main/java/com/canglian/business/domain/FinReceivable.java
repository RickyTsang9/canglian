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

    private BigDecimal amount;

    private BigDecimal receivedAmount;

    private String status;

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

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public BigDecimal getReceivedAmount()
    {
        return receivedAmount;
    }

    public void setReceivedAmount(BigDecimal receivedAmount)
    {
        this.receivedAmount = receivedAmount;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
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
            .append("amount", getAmount())
            .append("receivedAmount", getReceivedAmount())
            .append("status", getStatus())
            .append("dueDate", getDueDate())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

