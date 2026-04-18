package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 销售退货 实体
 * 
 * @author canglian
 */
public class WmsSaleReturn extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long saleReturnId;

    private String returnNo;

    private String returnType;

    private Long customerId;

    private Long warehouseId;

    private String sourceBillType;

    private Long sourceBillId;

    private String sourceBillNo;

    private Date businessDate;

    private BigDecimal totalQty;

    private BigDecimal totalAmount;

    private String status;

    private String bizStatus;

    private String auditBy;

    private Date auditTime;

    /**
     * 获取销售退货id
     * 
     * @return 销售退货id
     */
    public Long getSaleReturnId()
    {
        return saleReturnId;
    }

    /**
     * 设置销售退货id
     * 
     * @param saleReturnId 销售退货id
     */
    public void setSaleReturnId(Long saleReturnId)
    {
        this.saleReturnId = saleReturnId;
    }

    /**
     * 获取退货单号
     * 
     * @return 退货单号
     */
    public String getReturnNo()
    {
        return returnNo;
    }

    /**
     * 设置退货单号
     * 
     * @param returnNo 退货单号
     */
    public void setReturnNo(String returnNo)
    {
        this.returnNo = returnNo;
    }

    /**
     * 获取退货类型
     * 
     * @return 退货类型
     */
    public String getReturnType()
    {
        return returnType;
    }

    /**
     * 设置退货类型
     * 
     * @param returnType 退货类型
     */
    public void setReturnType(String returnType)
    {
        this.returnType = returnType;
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
     * 获取仓库id
     * 
     * @return 仓库id
     */
    public Long getWarehouseId()
    {
        return warehouseId;
    }

    /**
     * 设置仓库id
     * 
     * @param warehouseId 仓库id
     */
    public void setWarehouseId(Long warehouseId)
    {
        this.warehouseId = warehouseId;
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

    /**
     * 获取总数量
     * 
     * @return 总数量
     */
    public BigDecimal getTotalQty()
    {
        return totalQty;
    }

    /**
     * 设置总数量
     * 
     * @param totalQty 总数量
     */
    public void setTotalQty(BigDecimal totalQty)
    {
        this.totalQty = totalQty;
    }

    /**
     * 获取总金额
     * 
     * @return 总金额
     */
    public BigDecimal getTotalAmount()
    {
        return totalAmount;
    }

    /**
     * 设置总金额
     * 
     * @param totalAmount 总金额
     */
    public void setTotalAmount(BigDecimal totalAmount)
    {
        this.totalAmount = totalAmount;
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

    public String getBizStatus()
    {
        return bizStatus;
    }

    public void setBizStatus(String bizStatus)
    {
        this.bizStatus = bizStatus;
    }

    /**
     * 获取审核人
     * 
     * @return 审核人
     */
    public String getAuditBy()
    {
        return auditBy;
    }

    /**
     * 设置审核人
     * 
     * @param auditBy 审核人
     */
    public void setAuditBy(String auditBy)
    {
        this.auditBy = auditBy;
    }

    /**
     * 获取审核时间
     * 
     * @return 审核时间
     */
    public Date getAuditTime()
    {
        return auditTime;
    }

    /**
     * 设置审核时间
     * 
     * @param auditTime 审核时间
     */
    public void setAuditTime(Date auditTime)
    {
        this.auditTime = auditTime;
    }

    /**
     * 获取对象字符串
     * 
     * @return 对象字符串
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("saleReturnId", getSaleReturnId())
            .append("returnNo", getReturnNo())
            .append("returnType", getReturnType())
            .append("customerId", getCustomerId())
            .append("warehouseId", getWarehouseId())
            .append("sourceBillType", getSourceBillType())
            .append("sourceBillId", getSourceBillId())
            .append("sourceBillNo", getSourceBillNo())
            .append("businessDate", getBusinessDate())
            .append("totalQty", getTotalQty())
            .append("totalAmount", getTotalAmount())
            .append("status", getStatus())
            .append("bizStatus", getBizStatus())
            .append("auditBy", getAuditBy())
            .append("auditTime", getAuditTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

