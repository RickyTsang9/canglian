package com.canglian.business.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.canglian.common.core.domain.BaseEntity;

public class MdCustomer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long customerId;

    private String customerCode;

    private String customerName;

    private String customerLevel;

    private java.math.BigDecimal creditLimit;

    private Integer receivableDays;

    private String pricePolicyType;

    private String contactPerson;

    private String contactPhone;

    private String contactEmail;

    private String address;

    private String status;

    private String delFlag;

    public Long getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    public String getCustomerCode()
    {
        return customerCode;
    }

    public void setCustomerCode(String customerCode)
    {
        this.customerCode = customerCode;
    }

    public String getCustomerName()
    {
        return customerName;
    }

    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    public String getCustomerLevel()
    {
        return customerLevel;
    }

    public void setCustomerLevel(String customerLevel)
    {
        this.customerLevel = customerLevel;
    }

    public java.math.BigDecimal getCreditLimit()
    {
        return creditLimit;
    }

    public void setCreditLimit(java.math.BigDecimal creditLimit)
    {
        this.creditLimit = creditLimit;
    }

    public Integer getReceivableDays()
    {
        return receivableDays;
    }

    public void setReceivableDays(Integer receivableDays)
    {
        this.receivableDays = receivableDays;
    }

    public String getPricePolicyType()
    {
        return pricePolicyType;
    }

    public void setPricePolicyType(String pricePolicyType)
    {
        this.pricePolicyType = pricePolicyType;
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
            .append("customerId", getCustomerId())
            .append("customerCode", getCustomerCode())
            .append("customerName", getCustomerName())
            .append("customerLevel", getCustomerLevel())
            .append("creditLimit", getCreditLimit())
            .append("receivableDays", getReceivableDays())
            .append("pricePolicyType", getPricePolicyType())
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

