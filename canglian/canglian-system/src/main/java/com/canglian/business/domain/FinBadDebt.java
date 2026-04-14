package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 坏账对象 fin_bad_debt
 * 
 * @author canglian
 */
public class FinBadDebt extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 坏账id */
    private Long badDebtId;

    /** 坏账单号 */
    private String badDebtNo;

    /** 应收id */
    private Long receivableId;

    /** 客户id */
    private Long customerId;

    /** 坏账金额 */
    private BigDecimal amount;

    /** 坏账日期 */
    private Date badDebtDate;

    /** 原因 */
    private String reason;

    /** 状态 */
    private String status;

    /**
     * 获取坏账id
     * 
     * @return 坏账id
     */
    public Long getBadDebtId()
    {
        return badDebtId;
    }

    /**
     * 设置坏账id
     * 
     * @param badDebtId 坏账id
     */
    public void setBadDebtId(Long badDebtId)
    {
        this.badDebtId = badDebtId;
    }

    /**
     * 获取坏账单号
     * 
     * @return 坏账单号
     */
    public String getBadDebtNo()
    {
        return badDebtNo;
    }

    /**
     * 设置坏账单号
     * 
     * @param badDebtNo 坏账单号
     */
    public void setBadDebtNo(String badDebtNo)
    {
        this.badDebtNo = badDebtNo;
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
     * 获取客户id
     * 
     * @return 客户id
     */
    public Long getCustomerId()
    {
        return customerId;
    }

    /**
     * 设置客户id
     * 
     * @param customerId 客户id
     */
    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    /**
     * 获取坏账金额
     * 
     * @return 坏账金额
     */
    public BigDecimal getAmount()
    {
        return amount;
    }

    /**
     * 设置坏账金额
     * 
     * @param amount 坏账金额
     */
    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }

    /**
     * 获取坏账日期
     * 
     * @return 坏账日期
     */
    public Date getBadDebtDate()
    {
        return badDebtDate;
    }

    /**
     * 设置坏账日期
     * 
     * @param badDebtDate 坏账日期
     */
    public void setBadDebtDate(Date badDebtDate)
    {
        this.badDebtDate = badDebtDate;
    }

    /**
     * 获取原因
     * 
     * @return 原因
     */
    public String getReason()
    {
        return reason;
    }

    /**
     * 设置原因
     * 
     * @param reason 原因
     */
    public void setReason(String reason)
    {
        this.reason = reason;
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
            .append("badDebtId", getBadDebtId())
            .append("badDebtNo", getBadDebtNo())
            .append("receivableId", getReceivableId())
            .append("customerId", getCustomerId())
            .append("amount", getAmount())
            .append("badDebtDate", getBadDebtDate())
            .append("reason", getReason())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

