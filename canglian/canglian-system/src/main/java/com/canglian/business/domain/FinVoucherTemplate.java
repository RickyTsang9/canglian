package com.canglian.business.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 业财凭证借贷科目映射模板实体
 *
 * @author canglian
 */
public class FinVoucherTemplate extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long templateId;

    private String eventType;

    private Integer entrySeq;

    /** 借贷方向(debit/credit) */
    private String direction;

    private String subjectCode;

    private String subjectName;

    /** 辅助核算类型(customer/supplier/warehouse/none) */
    private String auxiliaryType;

    private String summaryTemplate;

    /** 状态(0启用 1停用) */
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getTemplateId()
    {
        return templateId;
    }

    public void setTemplateId(Long templateId)
    {
        this.templateId = templateId;
    }

    public String getEventType()
    {
        return eventType;
    }

    public void setEventType(String eventType)
    {
        this.eventType = eventType;
    }

    public Integer getEntrySeq()
    {
        return entrySeq;
    }

    public void setEntrySeq(Integer entrySeq)
    {
        this.entrySeq = entrySeq;
    }

    public String getDirection()
    {
        return direction;
    }

    public void setDirection(String direction)
    {
        this.direction = direction;
    }

    public String getSubjectCode()
    {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode)
    {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName()
    {
        return subjectName;
    }

    public void setSubjectName(String subjectName)
    {
        this.subjectName = subjectName;
    }

    public String getAuxiliaryType()
    {
        return auxiliaryType;
    }

    public void setAuxiliaryType(String auxiliaryType)
    {
        this.auxiliaryType = auxiliaryType;
    }

    public String getSummaryTemplate()
    {
        return summaryTemplate;
    }

    public void setSummaryTemplate(String summaryTemplate)
    {
        this.summaryTemplate = summaryTemplate;
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
    public Date getCreateTime()
    {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime)
    {
        this.createTime = createTime;
    }
}
