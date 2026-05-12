package com.canglian.business.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;
import com.canglian.common.annotation.Excel;

public class MdSupplier extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long supplierId;

    @Excel(name = "供应商编码")
    private String supplierCode;

    @Excel(name = "供应商名称")
    private String supplierName;

    @Excel(name = "供应商等级")
    private String supplierLevel;

    @Excel(name = "付款账期")
    private Integer payableDays;

    @Excel(name = "联系人")
    private String contactPerson;

    @Excel(name = "联系电话")
    private String contactPhone;

    @Excel(name = "联系邮箱")
    private String contactEmail;

    @Excel(name = "地址")
    private String address;

    @Excel(name = "状态")
    private String status;

    private String delFlag;

    public Long getSupplierId()
    {
        return supplierId;
    }

    public void setSupplierId(Long supplierId)
    {
        this.supplierId = supplierId;
    }

    public String getSupplierCode()
    {
        return supplierCode;
    }

    public void setSupplierCode(String supplierCode)
    {
        this.supplierCode = supplierCode;
    }

    public String getSupplierName()
    {
        return supplierName;
    }

    public void setSupplierName(String supplierName)
    {
        this.supplierName = supplierName;
    }

    public String getSupplierLevel()
    {
        return supplierLevel;
    }

    public void setSupplierLevel(String supplierLevel)
    {
        this.supplierLevel = supplierLevel;
    }

    public Integer getPayableDays()
    {
        return payableDays;
    }

    public void setPayableDays(Integer payableDays)
    {
        this.payableDays = payableDays;
    }

    public String getContactPerson()
    {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson)
    {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone()
    {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    public String getContactEmail()
    {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail)
    {
        this.contactEmail = contactEmail;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("supplierId", getSupplierId())
            .append("supplierCode", getSupplierCode())
            .append("supplierName", getSupplierName())
            .append("supplierLevel", getSupplierLevel())
            .append("payableDays", getPayableDays())
            .append("contactPerson", getContactPerson())
            .append("contactPhone", getContactPhone())
            .append("contactEmail", getContactEmail())
            .append("address", getAddress())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}

