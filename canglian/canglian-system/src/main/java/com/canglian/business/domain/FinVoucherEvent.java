package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 凭证事件实体
 * 
 * @author canglian
 */
public class FinVoucherEvent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long voucherEventId;

    private String billType;

    private Long billId;

    private String billNo;

    private String eventType;

    private Date eventDate;

    private BigDecimal eventAmount;

    private String status;

    public Long getVoucherEventId()
    {
        return voucherEventId;
    }

    public void setVoucherEventId(Long voucherEventId)
    {
        this.voucherEventId = voucherEventId;
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

    public String getBillNo()
    {
        return billNo;
    }

    public void setBillNo(String billNo)
    {
        this.billNo = billNo;
    }

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    public Date getEventDate()
    {
        return eventDate;
    }

    public void setEventDate(Date eventDate)
    {
        this.eventDate = eventDate;
    }

    public BigDecimal getEventAmount()
    {
        return eventAmount;
    }

    public void setEventAmount(BigDecimal eventAmount)
    {
        this.eventAmount = eventAmount;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("voucherEventId", getVoucherEventId())
            .append("billType", getBillType())
            .append("billId", getBillId())
            .append("billNo", getBillNo())
            .append("eventType", getEventType())
            .append("eventDate", getEventDate())
            .append("eventAmount", getEventAmount())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
