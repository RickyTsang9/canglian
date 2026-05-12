package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 业务链路节点
 *
 * @author canglian
 */
public class BusinessTraceNode extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String nodeType;

    private String nodeName;

    private String billType;

    private Long billId;

    private String billNo;

    private Date businessDate;

    private BigDecimal amount;

    private String status;

    private String bizStatus;

    private String sourceBillType;

    private Long sourceBillId;

    private String sourceBillNo;

    private String relationLabel;

    public String getNodeType()
    {
        return nodeType;
    }

    public void setNodeType(String nodeType)
    {
        this.nodeType = nodeType;
    }

    public String getNodeName()
    {
        return nodeName;
    }

    public void setNodeName(String nodeName)
    {
        this.nodeName = nodeName;
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

    public String getRelationLabel()
    {
        return relationLabel;
    }

    public void setRelationLabel(String relationLabel)
    {
        this.relationLabel = relationLabel;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("nodeType", getNodeType())
            .append("nodeName", getNodeName())
            .append("billType", getBillType())
            .append("billId", getBillId())
            .append("billNo", getBillNo())
            .append("businessDate", getBusinessDate())
            .append("amount", getAmount())
            .append("status", getStatus())
            .append("bizStatus", getBizStatus())
            .append("sourceBillType", getSourceBillType())
            .append("sourceBillId", getSourceBillId())
            .append("sourceBillNo", getSourceBillNo())
            .append("relationLabel", getRelationLabel())
            .toString();
    }
}
