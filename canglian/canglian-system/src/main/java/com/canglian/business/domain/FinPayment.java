package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
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

    private BigDecimal amount;

    private Date paymentDate;

    private String payChannel;

    public Long getPaymentId()
    {
        return paymentId;
    }

    public void setPaymentId(Long paymentId)
    {
        this.paymentId = paymentId;
    }

    public String getPaymentNo()
    {
        return paymentNo;
    }

    public void setPaymentNo(String paymentNo)
    {
        this.paymentNo = paymentNo;
    }

    public Long getSupplierId()
    {
        return supplierId;
    }

    public void setSupplierId(Long supplierId)
    {
        this.supplierId = supplierId;
    }

    public Long getPayableId()
    {
        return payableId;
    }

    public void setPayableId(Long payableId)
    {
        this.payableId = payableId;
    }

    public BigDecimal getAmount()
    {
        return amount;
    }

    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    public Date getPaymentDate()
    {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate)
    {
        this.paymentDate = paymentDate;
    }

    public String getPayChannel()
    {
        return payChannel;
    }

    public void setPayChannel(String payChannel)
    {
        this.payChannel = payChannel;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("paymentId", getPaymentId())
            .append("paymentNo", getPaymentNo())
            .append("supplierId", getSupplierId())
            .append("payableId", getPayableId())
            .append("amount", getAmount())
            .append("paymentDate", getPaymentDate())
            .append("payChannel", getPayChannel())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

