package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 仓库作业清单对象
 * 
 * @author canglian
 */
public class WarehouseStatisticsOperationItem
{
    /** 单据id */
    private Long billId;

    /** 单据编号 */
    private String billNo;

    /** 业务类型 */
    private String businessType;

    /** 数量 */
    private BigDecimal totalQuantity;

    /** 金额 */
    private BigDecimal totalAmount;

    /** 业务时间 */
    private Date businessTime;

    /** 摘要信息 */
    private String summary;

    /**
     * 获取单据id
     * 
     * @return 单据id
     */
    public Long getBillId()
    {
        return billId;
    }

    /**
     * 设置单据id
     * 
     * @param billId 单据id
     */
    public void setBillId(Long billId)
    {
        this.billId = billId;
    }

    /**
     * 获取单据编号
     * 
     * @return 单据编号
     */
    public String getBillNo()
    {
        return billNo;
    }

    /**
     * 设置单据编号
     * 
     * @param billNo 单据编号
     */
    public void setBillNo(String billNo)
    {
        this.billNo = billNo;
    }

    /**
     * 获取业务类型
     * 
     * @return 业务类型
     */
    public String getBusinessType()
    {
        return businessType;
    }

    /**
     * 设置业务类型
     * 
     * @param businessType 业务类型
     */
    public void setBusinessType(String businessType)
    {
        this.businessType = businessType;
    }

    /**
     * 获取数量
     * 
     * @return 数量
     */
    public BigDecimal getTotalQuantity()
    {
        return totalQuantity;
    }

    /**
     * 设置数量
     * 
     * @param totalQuantity 数量
     */
    public void setTotalQuantity(BigDecimal totalQuantity)
    {
        this.totalQuantity = totalQuantity;
    }

    /**
     * 获取金额
     * 
     * @return 金额
     */
    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    /**
     * 设置金额
     * 
     * @param totalAmount 金额
     */
    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
    }

    /**
     * 获取业务时间
     * 
     * @return 业务时间
     */
    public Date getBusinessTime()
    {
        return businessTime;
    }

    /**
     * 设置业务时间
     * 
     * @param businessTime 业务时间
     */
    public void setBusinessTime(Date businessTime)
    {
        this.businessTime = businessTime;
    }

    /**
     * 获取摘要信息
     * 
     * @return 摘要信息
     */
    public String getSummary()
    {
        return summary;
    }

    /**
     * 设置摘要信息
     * 
     * @param summary 摘要信息
     */
    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("billId", getBillId())
            .append("billNo", getBillNo())
            .append("businessType", getBusinessType())
            .append("totalQuantity", getTotalQuantity())
            .append("totalAmount", getTotalAmount())
            .append("businessTime", getBusinessTime())
            .append("summary", getSummary())
            .toString();
    }
}

