package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.canglian.common.annotation.Excel;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 复式记账凭证分录子表实体
 *
 * @author canglian
 */
public class FinVoucherEntry extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long entryId;

    private Long voucherEventId;

    @Excel(name = "凭证号")
    private String voucherNo;

    @Excel(name = "分录行号")
    private Integer entrySeq;

    @Excel(name = "借贷方向", readConverterExp = "debit=借方,credit=贷方")
    private String direction;

    @Excel(name = "科目编码")
    private String subjectCode;

    @Excel(name = "科目名称")
    private String subjectName;

    @Excel(name = "辅助核算类型", readConverterExp = "customer=客户,supplier=供应商,warehouse=仓库,none=无")
    private String auxiliaryType;

    @Excel(name = "辅助核算项目")
    private String auxiliaryName;

    @Excel(name = "借方金额")
    private BigDecimal debitAmount;

    @Excel(name = "贷方金额")
    private BigDecimal creditAmount;

    @Excel(name = "摘要")
    private String summary;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "生成时间", width = 22, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 导出辅助字段：凭证日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "业务日期", width = 16, dateFormat = "yyyy-MM-dd")
    private Date voucherDate;

    /** 导出辅助字段：来源单据号 */
    @Excel(name = "来源单据号")
    private String billNo;

    /** 导出辅助字段：导出标准(standard/kingdee/yonyou) */
    @Excel(name = "凭证字/账套格式")
    private String voucherWord;

    public Long getEntryId()
    {
        return entryId;
    }

    public void setEntryId(Long entryId)
    {
        this.entryId = entryId;
    }

    public Long getVoucherEventId()
    {
        return voucherEventId;
    }

    public void setVoucherEventId(Long voucherEventId)
    {
        this.voucherEventId = voucherEventId;
    }

    public String getVoucherNo()
    {
        return voucherNo;
    }

    public void setVoucherNo(String voucherNo)
    {
        this.voucherNo = voucherNo;
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

    public String getAuxiliaryName()
    {
        return auxiliaryName;
    }

    public void setAuxiliaryName(String auxiliaryName)
    {
        this.auxiliaryName = auxiliaryName;
    }

    public BigDecimal getDebitAmount()
    {
        return debitAmount;
    }

    public void setDebitAmount(BigDecimal debitAmount)
    {
        this.debitAmount = debitAmount;
    }

    public BigDecimal getCreditAmount()
    {
        return creditAmount;
    }

    public void setCreditAmount(BigDecimal creditAmount)
    {
        this.creditAmount = creditAmount;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
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

    public Date getVoucherDate()
    {
        return voucherDate;
    }

    public void setVoucherDate(Date voucherDate)
    {
        this.voucherDate = voucherDate;
    }

    public String getBillNo()
    {
        return billNo;
    }

    public void setBillNo(String billNo)
    {
        this.billNo = billNo;
    }

    public String getVoucherWord()
    {
        return voucherWord;
    }

    public void setVoucherWord(String voucherWord)
    {
        this.voucherWord = voucherWord;
    }
}
