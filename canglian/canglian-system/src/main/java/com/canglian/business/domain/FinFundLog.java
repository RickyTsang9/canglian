package com.canglian.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.canglian.common.annotation.Excel;
import com.canglian.common.core.domain.BaseEntity;

/**
 * 资金账户逐笔流水台账实体 fin_fund_log
 *
 * @author canglian
 */
public class FinFundLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long fundLogId;

    @Excel(name = "资金账户ID")
    private Long accountId;

    @Excel(name = "账户名称")
    private String accountName;

    @Excel(name = "业务类型")
    private String bizType;

    private Long billId;

    @Excel(name = "来源单据号")
    private String billNo;

    @Excel(name = "收支方向", readConverterExp = "1=收入,2=支出")
    private String direction;

    @Excel(name = "变动金额")
    private BigDecimal changeAmount;

    @Excel(name = "变动前余额")
    private BigDecimal beforeBalance;

    @Excel(name = "变动后余额")
    private BigDecimal afterBalance;

    @Excel(name = "业务日期", dateFormat = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date bizDate;

    @Excel(name = "往来对象/摘要")
    private String counterpartyName;

    public Long getFundLogId() { return fundLogId; }
    public void setFundLogId(Long fundLogId) { this.fundLogId = fundLogId; }
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getBizType() { return bizType; }
    public void setBizType(String bizType) { this.bizType = bizType; }
    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }
    public String getBillNo() { return billNo; }
    public void setBillNo(String billNo) { this.billNo = billNo; }
    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }
    public BigDecimal getChangeAmount() { return changeAmount; }
    public void setChangeAmount(BigDecimal changeAmount) { this.changeAmount = changeAmount; }
    public BigDecimal getBeforeBalance() { return beforeBalance; }
    public void setBeforeBalance(BigDecimal beforeBalance) { this.beforeBalance = beforeBalance; }
    public BigDecimal getAfterBalance() { return afterBalance; }
    public void setAfterBalance(BigDecimal afterBalance) { this.afterBalance = afterBalance; }
    public Date getBizDate() { return bizDate; }
    public void setBizDate(Date bizDate) { this.bizDate = bizDate; }
    public String getCounterpartyName() { return counterpartyName; }
    public void setCounterpartyName(String counterpartyName) { this.counterpartyName = counterpartyName; }
}
