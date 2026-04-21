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

    private String voucherNo;

    private String voucherStatus;

    private Date voucherDate;

    private String generatedBy;

    private Date generatedTime;

    private String writebackStatus;

    private String writebackMessage;

    private String reverseVoucherNo;

    private String reversedBy;

    private Date reversedTime;

    /**
     * 获取凭证事件id
     *
     * @return 凭证事件id
     */
    public Long getVoucherEventId()
    {
        return voucherEventId;
    }

    /**
     * 设置凭证事件id
     *
     * @param voucherEventId 凭证事件id
     */
    public void setVoucherEventId(Long voucherEventId)
    {
        this.voucherEventId = voucherEventId;
    }

    /**
     * 获取单据类型
     *
     * @return 单据类型
     */
    public String getBillType()
    {
        return billType;
    }

    /**
     * 设置单据类型
     *
     * @param billType 单据类型
     */
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

    /**
     * 获取凭证编号
     *
     * @return 凭证编号
     */
    public String getVoucherNo()
    {
        return voucherNo;
    }

    /**
     * 设置凭证编号
     *
     * @param voucherNo 凭证编号
     */
    public void setVoucherNo(String voucherNo)
    {
        this.voucherNo = voucherNo;
    }

    /**
     * 获取凭证状态
     *
     * @return 凭证状态
     */
    public String getVoucherStatus()
    {
        return voucherStatus;
    }

    /**
     * 设置凭证状态
     *
     * @param voucherStatus 凭证状态
     */
    public void setVoucherStatus(String voucherStatus)
    {
        this.voucherStatus = voucherStatus;
    }

    /**
     * 获取凭证日期
     *
     * @return 凭证日期
     */
    public Date getVoucherDate()
    {
        return voucherDate;
    }

    /**
     * 设置凭证日期
     *
     * @param voucherDate 凭证日期
     */
    public void setVoucherDate(Date voucherDate)
    {
        this.voucherDate = voucherDate;
    }

    /**
     * 获取生成凭证人
     *
     * @return 生成凭证人
     */
    public String getGeneratedBy()
    {
        return generatedBy;
    }

    /**
     * 设置生成凭证人
     *
     * @param generatedBy 生成凭证人
     */
    public void setGeneratedBy(String generatedBy)
    {
        this.generatedBy = generatedBy;
    }

    /**
     * 获取生成凭证时间
     *
     * @return 生成凭证时间
     */
    public Date getGeneratedTime()
    {
        return generatedTime;
    }

    /**
     * 设置生成凭证时间
     *
     * @param generatedTime 生成凭证时间
     */
    public void setGeneratedTime(Date generatedTime)
    {
        this.generatedTime = generatedTime;
    }

    /**
     * 获取回写状态
     *
     * @return 回写状态
     */
    public String getWritebackStatus()
    {
        return writebackStatus;
    }

    /**
     * 设置回写状态
     *
     * @param writebackStatus 回写状态
     */
    public void setWritebackStatus(String writebackStatus)
    {
        this.writebackStatus = writebackStatus;
    }

    /**
     * 获取回写消息
     *
     * @return 回写消息
     */
    public String getWritebackMessage()
    {
        return writebackMessage;
    }

    /**
     * 设置回写消息
     *
     * @param writebackMessage 回写消息
     */
    public void setWritebackMessage(String writebackMessage)
    {
        this.writebackMessage = writebackMessage;
    }

    /**
     * 获取冲销凭证编号
     *
     * @return 冲销凭证编号
     */
    public String getReverseVoucherNo()
    {
        return reverseVoucherNo;
    }

    /**
     * 设置冲销凭证编号
     *
     * @param reverseVoucherNo 冲销凭证编号
     */
    public void setReverseVoucherNo(String reverseVoucherNo)
    {
        this.reverseVoucherNo = reverseVoucherNo;
    }

    /**
     * 获取冲销人
     *
     * @return 冲销人
     */
    public String getReversedBy()
    {
        return reversedBy;
    }

    /**
     * 设置冲销人
     *
     * @param reversedBy 冲销人
     */
    public void setReversedBy(String reversedBy)
    {
        this.reversedBy = reversedBy;
    }

    /**
     * 获取冲销时间
     *
     * @return 冲销时间
     */
    public Date getReversedTime()
    {
        return reversedTime;
    }

    /**
     * 设置冲销时间
     *
     * @param reversedTime 冲销时间
     */
    public void setReversedTime(Date reversedTime)
    {
        this.reversedTime = reversedTime;
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
            .append("voucherNo", getVoucherNo())
            .append("voucherStatus", getVoucherStatus())
            .append("voucherDate", getVoucherDate())
            .append("generatedBy", getGeneratedBy())
            .append("generatedTime", getGeneratedTime())
            .append("writebackStatus", getWritebackStatus())
            .append("writebackMessage", getWritebackMessage())
            .append("reverseVoucherNo", getReverseVoucherNo())
            .append("reversedBy", getReversedBy())
            .append("reversedTime", getReversedTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
