package com.canglian.business.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.canglian.business.domain.FinFundLog;
import com.canglian.business.domain.FinFundTransfer;

/**
 * 资金流水与内部互转服务接口
 *
 * @author canglian
 */
public interface IFinFundLogService
{
    public List<FinFundLog> selectFinFundLogList(FinFundLog query);

    public boolean hasLogs(Long accountId);

    /**
     * 统一资金账户余额变更并自动登记逐笔不可篡改流水（行锁 FOR UPDATE 防并发覆盖）
     */
    public FinFundLog recordBalanceChange(Long accountId, String bizType, Long billId, String billNo,
                                          BigDecimal delta, Date bizDate, String counterpartyName,
                                          String remark, String operator);

    public List<FinFundTransfer> selectFinFundTransferList(FinFundTransfer query);

    public int insertFinFundTransfer(FinFundTransfer transfer, String operator);

    public int confirmFinFundTransfer(Long transferId, String operator);

    public int unconfirmFinFundTransfer(Long transferId, String operator);

    public int deleteFinFundTransferById(Long transferId);
}
