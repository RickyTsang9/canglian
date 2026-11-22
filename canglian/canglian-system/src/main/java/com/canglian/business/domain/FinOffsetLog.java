package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 往来红蓝对冲台账 fin_offset_log
 *
 * @author canglian
 */
public class FinOffsetLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long offsetId;

    /** 类型(receivable应收/payable应付) */
    private String offsetType;

    private Long positiveBillId;

    private Long redBillId;

    private BigDecimal offsetAmount;

    /** 状态(0生效 1已撤销) */
    private String status;

    private String reverseBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reverseTime;

    public Long getOffsetId() { return offsetId; }
    public void setOffsetId(Long offsetId) { this.offsetId = offsetId; }
    public String getOffsetType() { return offsetType; }
    public void setOffsetType(String offsetType) { this.offsetType = offsetType; }
    public Long getPositiveBillId() { return positiveBillId; }
    public void setPositiveBillId(Long positiveBillId) { this.positiveBillId = positiveBillId; }
    public Long getRedBillId() { return redBillId; }
    public void setRedBillId(Long redBillId) { this.redBillId = redBillId; }
    public BigDecimal getOffsetAmount() { return offsetAmount; }
    public void setOffsetAmount(BigDecimal offsetAmount) { this.offsetAmount = offsetAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReverseBy() { return reverseBy; }
    public void setReverseBy(String reverseBy) { this.reverseBy = reverseBy; }
    public Date getReverseTime() { return reverseTime; }
    public void setReverseTime(Date reverseTime) { this.reverseTime = reverseTime; }
}
