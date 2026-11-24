package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.FinFundAccount;
import com.canglian.business.domain.FinFundLog;
import com.canglian.business.domain.FinFundTransfer;
import com.canglian.business.mapper.FinFundAccountMapper;
import com.canglian.business.mapper.FinFundLogMapper;
import com.canglian.business.service.IFinFundLogService;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 资金流水与内部互转服务实现
 *
 * @author canglian
 */
@Service
public class FinFundLogServiceImpl implements IFinFundLogService
{
    private static final DateTimeFormatter NO_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private FinFundLogMapper finFundLogMapper;

    @Autowired
    private FinFundAccountMapper finFundAccountMapper;

    @Autowired
    private IFinGeneralLedgerService finGeneralLedgerService;

    @Autowired
    private IFinVoucherEventService finVoucherEventService;

    @Override
    public List<FinFundLog> selectFinFundLogList(FinFundLog query)
    {
        return finFundLogMapper.selectFinFundLogList(query);
    }

    @Override
    public boolean hasLogs(Long accountId)
    {
        return accountId != null && finFundLogMapper.countLogsByAccountId(accountId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinFundLog recordBalanceChange(Long accountId, String bizType, Long billId, String billNo,
                                          BigDecimal delta, Date bizDate, String counterpartyName,
                                          String remark, String operator)
    {
        if (accountId == null || delta == null || delta.compareTo(BigDecimal.ZERO) == 0)
        {
            return null;
        }
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(bizDate == null ? new Date() : bizDate);
        }
        FinFundAccount account = finFundAccountMapper.selectFinFundAccountByIdForUpdate(accountId);
        if (account == null)
        {
            throw new ServiceException("关联资金账户不存在");
        }
        if ("1".equals(account.getStatus()))
        {
            throw new ServiceException("关联资金账户[" + account.getAccountName() + "]已停用，无法记账");
        }
        BigDecimal before = account.getBalance() == null ? BigDecimal.ZERO : account.getBalance();
        BigDecimal after = before.add(delta);
        if (after.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("资金账户[" + account.getAccountName() + "]余额不足（当前余额：" + before + "），无法扣减");
        }
        account.setBalance(after);
        account.setUpdateBy(operator);
        finFundAccountMapper.updateFinFundAccount(account);

        FinFundLog log = new FinFundLog();
        log.setAccountId(accountId);
        log.setAccountName(account.getAccountName());
        log.setBizType(StringUtils.isEmpty(bizType) ? "other" : bizType);
        log.setBillId(billId);
        log.setBillNo(billNo);
        log.setDirection(delta.compareTo(BigDecimal.ZERO) > 0 ? "1" : "2");
        log.setChangeAmount(delta.abs());
        log.setBeforeBalance(before);
        log.setAfterBalance(after);
        log.setBizDate(bizDate == null ? new Date() : bizDate);
        log.setCounterpartyName(counterpartyName);
        log.setRemark(remark);
        log.setCreateBy(operator);
        finFundLogMapper.insertFinFundLog(log);
        return log;
    }

    @Override
    public List<FinFundTransfer> selectFinFundTransferList(FinFundTransfer query)
    {
        return finFundLogMapper.selectFinFundTransferList(query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinFundTransfer(FinFundTransfer transfer, String operator)
    {
        validateTransfer(transfer);
        if (StringUtils.isEmpty(transfer.getTransferNo()))
        {
            transfer.setTransferNo(generateTransferNo());
        }
        if (transfer.getTransferDate() == null)
        {
            transfer.setTransferDate(new Date());
        }
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(transfer.getTransferDate());
        }
        if (transfer.getFeeAmount() == null)
        {
            transfer.setFeeAmount(BigDecimal.ZERO);
        }
        transfer.setStatus("0");
        transfer.setCreateBy(operator);
        return finFundLogMapper.insertFinFundTransfer(transfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirmFinFundTransfer(Long transferId, String operator)
    {
        FinFundTransfer transfer = finFundLogMapper.selectFinFundTransferById(transferId);
        if (transfer == null) throw new ServiceException("资金互转单不存在");
        if ("1".equals(transfer.getStatus())) throw new ServiceException("该资金互转单已确认，请勿重复操作");
        validateTransfer(transfer);
        Date bizDate = transfer.getTransferDate() == null ? new Date() : transfer.getTransferDate();
        finGeneralLedgerService.assertPeriodOpen(bizDate);

        BigDecimal fee = transfer.getFeeAmount() == null ? BigDecimal.ZERO : transfer.getFeeAmount();
        BigDecimal totalOut = transfer.getAmount().add(fee);
        // 统一按 accountId 升序上锁避免并发互转死锁
        Long firstId = Math.min(transfer.getOutAccountId(), transfer.getInAccountId());
        Long secondId = Math.max(transfer.getOutAccountId(), transfer.getInAccountId());
        finFundAccountMapper.selectFinFundAccountByIdForUpdate(firstId);
        finFundAccountMapper.selectFinFundAccountByIdForUpdate(secondId);

        recordBalanceChange(transfer.getOutAccountId(), "transfer_out", transfer.getTransferId(), transfer.getTransferNo(),
            totalOut.negate(), bizDate, "内部互转转出至账户#" + transfer.getInAccountId(),
            fee.compareTo(BigDecimal.ZERO) > 0 ? ("含手续费 " + fee) : transfer.getRemark(), operator);
        recordBalanceChange(transfer.getInAccountId(), "transfer_in", transfer.getTransferId(), transfer.getTransferNo(),
            transfer.getAmount(), bizDate, "内部互转自账户#" + transfer.getOutAccountId() + "转入",
            transfer.getRemark(), operator);

        transfer.setStatus("1");
        transfer.setAuditBy(operator);
        transfer.setAuditTime(new Date());
        transfer.setUpdateBy(operator);
        int rows = finFundLogMapper.updateFinFundTransfer(transfer);
        if (rows > 0)
        {
            finVoucherEventService.recordVoucherEvent("fund_transfer", transfer.getTransferId(), transfer.getTransferNo(),
                "fund_transfer", bizDate, transfer.getAmount(), operator, "资金内部互转完成待生成凭证");
            if (fee.compareTo(BigDecimal.ZERO) > 0)
            {
                finVoucherEventService.recordVoucherEvent("fund_transfer_fee", transfer.getTransferId(), transfer.getTransferNo(),
                    "fund_transfer_fee", bizDate, fee, operator, "资金内部互转银行手续费待生成凭证");
            }
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unconfirmFinFundTransfer(Long transferId, String operator)
    {
        FinFundTransfer transfer = finFundLogMapper.selectFinFundTransferById(transferId);
        if (transfer == null) throw new ServiceException("资金互转单不存在");
        if (!"1".equals(transfer.getStatus())) throw new ServiceException("仅已确认状态的资金互转单允许反确认");
        Date bizDate = transfer.getTransferDate() == null ? new Date() : transfer.getTransferDate();
        finGeneralLedgerService.assertPeriodOpen(bizDate);
        finVoucherEventService.cancelPendingVoucherEvents("fund_transfer", transferId);
        finVoucherEventService.cancelPendingVoucherEvents("fund_transfer_fee", transferId);

        BigDecimal fee = transfer.getFeeAmount() == null ? BigDecimal.ZERO : transfer.getFeeAmount();
        BigDecimal totalOut = transfer.getAmount().add(fee);
        Long firstId = Math.min(transfer.getOutAccountId(), transfer.getInAccountId());
        Long secondId = Math.max(transfer.getOutAccountId(), transfer.getInAccountId());
        finFundAccountMapper.selectFinFundAccountByIdForUpdate(firstId);
        finFundAccountMapper.selectFinFundAccountByIdForUpdate(secondId);

        recordBalanceChange(transfer.getInAccountId(), "unconfirm", transfer.getTransferId(), transfer.getTransferNo(),
            transfer.getAmount().negate(), bizDate, "互转反确认扣回", "反确认资金互转单：" + transfer.getTransferNo(), operator);
        recordBalanceChange(transfer.getOutAccountId(), "unconfirm", transfer.getTransferId(), transfer.getTransferNo(),
            totalOut, bizDate, "互转反确认退回", "反确认资金互转单：" + transfer.getTransferNo(), operator);

        transfer.setStatus("0");
        transfer.setAuditBy("");
        transfer.setAuditTime(null);
        transfer.setUpdateBy(operator);
        return finFundLogMapper.updateFinFundTransfer(transfer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinFundTransfer reverseFinFundTransfer(Long transferId, String operator)
    {
        FinFundTransfer origin = finFundLogMapper.selectFinFundTransferById(transferId);
        if (origin == null) throw new ServiceException("资金互转单不存在");
        if (!"1".equals(origin.getStatus())) throw new ServiceException("仅已确认状态的资金互转单允许红冲");
        if (origin.getRemark() != null && origin.getRemark().contains("[已红冲]"))
        {
            throw new ServiceException("该资金互转单已红冲，请勿重复操作");
        }
        Date now = new Date();
        finGeneralLedgerService.assertPeriodOpen(origin.getTransferDate() == null ? now : origin.getTransferDate());
        finGeneralLedgerService.assertPeriodOpen(now);

        BigDecimal fee = origin.getFeeAmount() == null ? BigDecimal.ZERO : origin.getFeeAmount();
        BigDecimal totalOut = origin.getAmount().add(fee);
        Long firstId = Math.min(origin.getOutAccountId(), origin.getInAccountId());
        Long secondId = Math.max(origin.getOutAccountId(), origin.getInAccountId());
        finFundAccountMapper.selectFinFundAccountByIdForUpdate(firstId);
        finFundAccountMapper.selectFinFundAccountByIdForUpdate(secondId);

        FinFundTransfer rev = new FinFundTransfer();
        rev.setTransferNo(generateTransferNo());
        rev.setOutAccountId(origin.getInAccountId());
        rev.setInAccountId(origin.getOutAccountId());
        rev.setAmount(origin.getAmount().negate());
        rev.setFeeAmount(fee.negate());
        rev.setTransferDate(now);
        rev.setStatus("1");
        rev.setAuditBy(operator);
        rev.setAuditTime(now);
        rev.setCreateBy(operator);
        rev.setRemark("红冲原资金互转单：" + origin.getTransferNo());
        finFundLogMapper.insertFinFundTransfer(rev);

        recordBalanceChange(origin.getInAccountId(), "transfer_reverse", rev.getTransferId(), rev.getTransferNo(),
            origin.getAmount().negate(), now, "互转红冲转出", "红冲原互转单：" + origin.getTransferNo(), operator);
        recordBalanceChange(origin.getOutAccountId(), "transfer_reverse", rev.getTransferId(), rev.getTransferNo(),
            totalOut, now, "互转红冲退回", "红冲原互转单：" + origin.getTransferNo(), operator);

        origin.setRemark((StringUtils.isEmpty(origin.getRemark()) ? "" : origin.getRemark() + " ") + "[已红冲:" + rev.getTransferNo() + "]");
        origin.setUpdateBy(operator);
        finFundLogMapper.updateFinFundTransfer(origin);

        finVoucherEventService.recordVoucherEvent("fund_transfer", rev.getTransferId(), rev.getTransferNo(),
            "fund_transfer", now, origin.getAmount().negate(), operator, "资金内部互转红冲待生成凭证");
        if (fee.compareTo(BigDecimal.ZERO) > 0)
        {
            finVoucherEventService.recordVoucherEvent("fund_transfer_fee", rev.getTransferId(), rev.getTransferNo(),
                "fund_transfer_fee", now, fee.negate(), operator, "资金互转手续费红冲待生成凭证");
        }
        return rev;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinFundTransferById(Long transferId)
    {
        FinFundTransfer transfer = finFundLogMapper.selectFinFundTransferById(transferId);
        if (transfer == null) throw new ServiceException("资金互转单不存在");
        if ("1".equals(transfer.getStatus())) throw new ServiceException("已确认的资金互转单不允许删除");
        finGeneralLedgerService.assertPeriodOpen(transfer.getTransferDate() == null ? new Date() : transfer.getTransferDate());
        return finFundLogMapper.deleteFinFundTransferById(transferId);
    }

    private void validateTransfer(FinFundTransfer t)
    {
        if (t == null || t.getOutAccountId() == null || t.getInAccountId() == null)
        {
            throw new ServiceException("转出账户与转入账户均不能为空");
        }
        if (t.getOutAccountId().equals(t.getInAccountId()))
        {
            throw new ServiceException("转出账户与转入账户不能为同一账户");
        }
        if (t.getAmount() == null || t.getAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("转账金额必须大于0");
        }
        if (t.getFeeAmount() != null && t.getFeeAmount().compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("手续费不能小于0");
        }
    }

    private String generateTransferNo()
    {
        String prefix = "ftr" + LocalDate.now().format(NO_FMT);
        String maxNo = finFundLogMapper.selectMaxTransferNoByPrefix(prefix);
        int nextSeq = 1;
        if (StringUtils.isNotEmpty(maxNo) && maxNo.length() > prefix.length())
        {
            String tail = maxNo.substring(prefix.length());
            if (StringUtils.isNumeric(tail)) nextSeq = Integer.parseInt(tail) + 1;
        }
        return prefix + String.format("%03d", nextSeq);
    }
}
