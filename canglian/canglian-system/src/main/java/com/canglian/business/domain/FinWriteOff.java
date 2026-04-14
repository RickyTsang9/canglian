package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 核销对象 fin_write_off
 * 
 * @author canglian
 */
public class FinWriteOff extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 核销id */
    private Long writeOffId;

    /** 核销单号 */
    private String writeOffNo;

    /** 核销类型 */
    private String writeOffType;

    /** 应收id */
    private Long receivableId;

    /** 应付id */
    private Long payableId;

    /** 收款单id */
    private Long receiptId;

    /** 付款单id */
    private Long paymentId;

    /** 核销金额 */
    private BigDecimal amount;

    /** 核销日期 */
    private Date writeOffDate;

    /** 状态 */
    private String status;

    /**
     * 获取核销id
     * 
     * @return 核销id
     */
    public Long getWriteOffId()
    {
        return writeOffId;
    }

    /**
     * 设置核销id
     * 
     * @param writeOffId 核销id
     */
    public void setWriteOffId(Long writeOffId)
    {
        this.writeOffId = writeOffId;
    }

    /**
     * 获取核销单号
     * 
     * @return 核销单号
     */
    public String getWriteOffNo()
    {
        return writeOffNo;
    }

    /**
     * 设置核销单号
     * 
     * @param writeOffNo 核销单号
     */
    public void setWriteOffNo(String writeOffNo)
    {
        this.writeOffNo = writeOffNo;
    }

    /**
     * 获取核销类型
     * 
     * @return 核销类型
     */
    public String getWriteOffType()
    {
        return writeOffType;
    }

    /**
     * 设置核销类型
     * 
     * @param writeOffType 核销类型
     */
    public void setWriteOffType(String writeOffType)
    {
        this.writeOffType = writeOffType;
    }

    /**
     * 获取应收id
     * 
     * @return 应收id
     */
    public Long getReceivableId()
    {
        return receivableId;
    }

    /**
     * 设置应收id
     * 
     * @param receivableId 应收id
     */
    public void setReceivableId(Long receivableId)
    {
        this.receivableId = receivableId;
    }

    /**
     * 获取应付id
     * 
     * @return 应付id
     */
    public Long getPayableId()
    {
        return payableId;
    }

    /**
     * 设置应付id
     * 
     * @param payableId 应付id
     */
    public void setPayableId(Long payableId)
    {
        this.payableId = payableId;
    }

    /**
     * 获取收款单id
     * 
     * @return 收款单id
     */
    public Long getReceiptId()
    {
        return receiptId;
    }

    /**
     * 设置收款单id
     * 
     * @param receiptId 收款单id
     */
    public void setReceiptId(Long receiptId)
    {
        this.receiptId = receiptId;
    }

    /**
     * 获取付款单id
     * 
     * @return 付款单id
     */
    public Long getPaymentId()
    {
        return paymentId;
    }

    /**
     * 设置付款单id
     * 
     * @param paymentId 付款单id
     */
    public void setPaymentId(Long paymentId)
    {
        this.paymentId = paymentId;
    }

    /**
     * 获取核销金额
     * 
     * @return 核销金额
     */
    public BigDecimal getAmount()
    {
        return amount;
    }

    /**
     * 设置核销金额
     * 
     * @param amount 核销金额
     */
    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    /**
     * 获取核销日期
     * 
     * @return 核销日期
     */
    public Date getWriteOffDate()
    {
        return writeOffDate;
    }

    /**
     * 设置核销日期
     * 
     * @param writeOffDate 核销日期
     */
    public void setWriteOffDate(Date writeOffDate)
    {
        this.writeOffDate = writeOffDate;
    }

    /**
     * 获取状态
     * 
     * @return 状态
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * 设置状态
     * 
     * @param status 状态
     */
    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("writeOffId", getWriteOffId())
            .append("writeOffNo", getWriteOffNo())
            .append("writeOffType", getWriteOffType())
            .append("receivableId", getReceivableId())
            .append("payableId", getPayableId())
            .append("receiptId", getReceiptId())
            .append("paymentId", getPaymentId())
            .append("amount", getAmount())
            .append("writeOffDate", getWriteOffDate())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

