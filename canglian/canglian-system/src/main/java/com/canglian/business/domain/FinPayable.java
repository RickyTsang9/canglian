package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

public class FinPayable extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long payableId;

    private String payableNo;

    private Long supplierId;

    private String billType;

    private Long billId;

    private BigDecimal amount;

    private BigDecimal paidAmount;

    private String status;

    private Date dueDate;

    public Long getPayableId()
    {
        return payableId;
    }

    public void setPayableId(Long payableId)
    {
        this.payableId = payableId;
    }

    public String getPayableNo()
    {
        return payableNo;
    }

    public void setPayableNo(String payableNo)
    {
        this.payableNo = payableNo;
    }

    public Long getSupplierId()
    {
        return supplierId;
    }

    public void setSupplierId(Long supplierId)
    {
        this.supplierId = supplierId;
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

    public BigDecimal getPaidAmount()
    {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount)
    {
        this.paidAmount = paidAmount;
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
            .append("payableId", getPayableId())
            .append("payableNo", getPayableNo())
            .append("supplierId", getSupplierId())
            .append("billType", getBillType())
            .append("billId", getBillId())
            .append("amount", getAmount())
            .append("paidAmount", getPaidAmount())
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

