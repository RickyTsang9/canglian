package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.annotation.Excel;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 发票登记实体
 *
 * @author canglian
 */
public class FinInvoice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long invoiceId;

    @Excel(name = "发票号码")
    private String invoiceNo;

    /** sale/purchase */
    @Excel(name = "类型", readConverterExp = "sale=销项发票,purchase=进项发票")
    private String invoiceType;

    /** blue蓝票 / red红票 */
    @Excel(name = "票种", readConverterExp = "normal=蓝字正常票,red=红字冲红票")
    private String invoiceKind;

    private Long originInvoiceId;

    private Long customerId;

    private Long supplierId;

    private Long deptId;

    private Long ownerUserId;

    private String sourceBillType;

    private Long sourceBillId;

    @Excel(name = "关联单号")
    private String sourceBillNo;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date businessDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开票日期", dateFormat = "yyyy-MM-dd")
    private Date invoiceDate;

    @Excel(name = "价税合计")
    private BigDecimal invoiceAmount;

    @Excel(name = "税率(%)")
    private BigDecimal taxRate;

    @Excel(name = "税额")
    private BigDecimal taxAmount;

    @Excel(name = "不含税金额")
    private BigDecimal untaxedAmount;

    @Excel(name = "状态", readConverterExp = "0=草稿,1=已确认")
    private String status;

    private String bizStatus;

    @Excel(name = "确认人")
    private String confirmBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date confirmTime;

    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }
    public String getInvoiceNo() { return invoiceNo; }
    public void setInvoiceNo(String invoiceNo) { this.invoiceNo = invoiceNo; }
    public String getInvoiceType() { return invoiceType; }
    public void setInvoiceType(String invoiceType) { this.invoiceType = invoiceType; }
    public String getInvoiceKind() { return invoiceKind; }
    public void setInvoiceKind(String invoiceKind) { this.invoiceKind = invoiceKind; }
    public Long getOriginInvoiceId() { return originInvoiceId; }
    public void setOriginInvoiceId(Long originInvoiceId) { this.originInvoiceId = originInvoiceId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public Long getOwnerUserId() { return ownerUserId; }
    public void setOwnerUserId(Long ownerUserId) { this.ownerUserId = ownerUserId; }
    public String getSourceBillType() { return sourceBillType; }
    public void setSourceBillType(String sourceBillType) { this.sourceBillType = sourceBillType; }
    public Long getSourceBillId() { return sourceBillId; }
    public void setSourceBillId(Long sourceBillId) { this.sourceBillId = sourceBillId; }
    public String getSourceBillNo() { return sourceBillNo; }
    public void setSourceBillNo(String sourceBillNo) { this.sourceBillNo = sourceBillNo; }
    public Date getBusinessDate() { return businessDate; }
    public void setBusinessDate(Date businessDate) { this.businessDate = businessDate; }
    public Date getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(Date invoiceDate) { this.invoiceDate = invoiceDate; }
    public BigDecimal getInvoiceAmount() { return invoiceAmount; }
    public void setInvoiceAmount(BigDecimal invoiceAmount) { this.invoiceAmount = invoiceAmount; }
    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public BigDecimal getUntaxedAmount() { return untaxedAmount; }
    public void setUntaxedAmount(BigDecimal untaxedAmount) { this.untaxedAmount = untaxedAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getBizStatus() { return bizStatus; }
    public void setBizStatus(String bizStatus) { this.bizStatus = bizStatus; }
    public String getConfirmBy() { return confirmBy; }
    public void setConfirmBy(String confirmBy) { this.confirmBy = confirmBy; }
    public Date getConfirmTime() { return confirmTime; }
    public void setConfirmTime(Date confirmTime) { this.confirmTime = confirmTime; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("invoiceId", getInvoiceId())
            .append("invoiceNo", getInvoiceNo())
            .append("invoiceType", getInvoiceType())
            .append("invoiceKind", getInvoiceKind())
            .append("originInvoiceId", getOriginInvoiceId())
            .append("invoiceAmount", getInvoiceAmount())
            .append("bizStatus", getBizStatus())
            .toString();
    }
}
