package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.FinBadDebt;
import com.canglian.business.domain.FinOffsetLog;
import com.canglian.business.domain.FinReceipt;
import com.canglian.business.domain.FinReceivable;
import com.canglian.business.domain.FinWriteOff;
import com.canglian.business.mapper.FinBadDebtMapper;
import com.canglian.business.mapper.FinOffsetLogMapper;
import com.canglian.business.mapper.FinReceiptMapper;
import com.canglian.business.mapper.FinReceivableMapper;
import com.canglian.business.mapper.FinWriteOffMapper;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IFinReceivableService;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 应收单 服务层实现
 * 
 * @author canglian
 */
@Service
public class FinReceivableServiceImpl implements IFinReceivableService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private FinReceivableMapper finReceivableMapper;

    @Autowired
    private IFinGeneralLedgerService finGeneralLedgerService;

    @Autowired(required = false)
    private FinOffsetLogMapper finOffsetLogMapper;

    @Autowired(required = false)
    private IFinVoucherEventService finVoucherEventService;

    @Autowired(required = false)
    private FinReceiptMapper finReceiptMapper;

    @Autowired(required = false)
    private FinWriteOffMapper finWriteOffMapper;

    @Autowired(required = false)
    private FinBadDebtMapper finBadDebtMapper;

    @Override
    public FinReceivable selectFinReceivableById(Long receivableId)
    {
        return finReceivableMapper.selectFinReceivableById(receivableId);
    }

    @Override
    public List<FinReceivable> selectFinReceivableList(FinReceivable finReceivable)
    {
        return finReceivableMapper.selectFinReceivableList(finReceivable);
    }

    @Override
    public int insertFinReceivable(FinReceivable finReceivable)
    {
        if (finReceivable.getBusinessDate() == null)
        {
            finReceivable.setBusinessDate(new Date());
        }
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(finReceivable.getBusinessDate());
        }
        if (StringUtils.isEmpty(finReceivable.getReceivableNo()))
        {
            finReceivable.setReceivableNo(generateReceivableNo());
        }
        if (StringUtils.isEmpty(finReceivable.getSourceBillType()))
        {
            finReceivable.setSourceBillType(null);
        }
        finReceivable.setBillDirection("1");
        finReceivable.setReceivedAmount(BigDecimal.ZERO);
        finReceivable.setBadDebtAmount(BigDecimal.ZERO);
        finReceivable.setStatus(calculateCollectionStatus(finReceivable.getReceivedAmount(), finReceivable.getBadDebtAmount(), finReceivable.getAmount()));
        finReceivable.setBizStatus("draft");
        validateReceivableAmount(finReceivable);
        return finReceivableMapper.insertFinReceivable(finReceivable);
    }

    @Override
    public int updateFinReceivable(FinReceivable finReceivable)
    {
        FinReceivable existingFinReceivable = checkReceivableEditable(finReceivable.getReceivableId());
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(existingFinReceivable.getBusinessDate() == null ? new Date() : existingFinReceivable.getBusinessDate());
            if (finReceivable.getBusinessDate() != null)
            {
                finGeneralLedgerService.assertPeriodOpen(finReceivable.getBusinessDate());
            }
        }
        if (StringUtils.isEmpty(finReceivable.getSourceBillType()))
        {
            finReceivable.setSourceBillType(null);
        }
        finReceivable.setReceivableNo(existingFinReceivable.getReceivableNo());
        finReceivable.setBillDirection(StringUtils.isEmpty(existingFinReceivable.getBillDirection()) ? "1" : existingFinReceivable.getBillDirection());
        finReceivable.setStatus(existingFinReceivable.getStatus());
        finReceivable.setBizStatus(existingFinReceivable.getBizStatus());
        finReceivable.setReceivedAmount(existingFinReceivable.getReceivedAmount());
        finReceivable.setBadDebtAmount(existingFinReceivable.getBadDebtAmount());
        validateReceivableAmount(finReceivable);
        return finReceivableMapper.updateFinReceivable(finReceivable);
    }

    @Override
    public int deleteFinReceivableById(Long receivableId)
    {
        FinReceivable ar = checkReceivableDeletable(receivableId);
        if (finGeneralLedgerService != null) finGeneralLedgerService.assertPeriodOpen(ar.getBusinessDate());
        return finReceivableMapper.deleteFinReceivableById(receivableId);
    }

    @Override
    public int deleteFinReceivableByIds(Long[] receivableIds)
    {
        for (Long receivableId : receivableIds)
        {
            FinReceivable ar = checkReceivableDeletable(receivableId);
            if (finGeneralLedgerService != null) finGeneralLedgerService.assertPeriodOpen(ar.getBusinessDate());
        }
        return finReceivableMapper.deleteFinReceivableByIds(receivableIds);
    }

    @Override
    public int auditFinReceivable(Long receivableId, String operator)
    {
        FinReceivable finReceivable = getExistingFinReceivable(receivableId);
        if ("confirmed".equals(finReceivable.getBizStatus()))
        {
            throw new ServiceException("应收单已审核，请勿重复操作");
        }
        finGeneralLedgerService.assertPeriodOpen(finReceivable.getBusinessDate());
        validateReceivableAmount(finReceivable);
        finReceivable.setStatus(calculateCollectionStatus(finReceivable.getReceivedAmount(), finReceivable.getBadDebtAmount(), finReceivable.getAmount()));
        finReceivable.setBizStatus("confirmed");
        finReceivable.setUpdateBy(operator);
        int rows = finReceivableMapper.updateFinReceivable(finReceivable);
        if (rows > 0 && finVoucherEventService != null
            && (StringUtils.isEmpty(finReceivable.getBillType()) || "manual".equals(finReceivable.getBillType())))
        {
            finVoucherEventService.recordVoucherEvent("receivable", finReceivable.getReceivableId(), finReceivable.getReceivableNo(),
                "manual_receivable", finReceivable.getBusinessDate(), finReceivable.getAmount(), operator, "手工应收单审核待生成凭证");
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unconfirmFinReceivable(Long receivableId, String operator)
    {
        FinReceivable finReceivable = getExistingFinReceivable(receivableId);
        if (!"confirmed".equals(finReceivable.getBizStatus()))
        {
            throw new ServiceException("仅已审核确认的应收单允许反确认");
        }
        if ("2".equals(finReceivable.getBillDirection()))
        {
            throw new ServiceException("退货自动生成的红字应收冲减单不允许直接反确认，请操作原退货单");
        }
        if ("outbound".equals(finReceivable.getBillType()) || "invoice".equals(finReceivable.getBillType()))
        {
            throw new ServiceException("由业务单据自动生成的应收单不允许直接反确认，请先反审核来源单据");
        }
        finGeneralLedgerService.assertPeriodOpen(finReceivable.getBusinessDate());
        if (defaultAmount(finReceivable.getReceivedAmount()).compareTo(BigDecimal.ZERO) != 0
            || defaultAmount(finReceivable.getBadDebtAmount()).compareTo(BigDecimal.ZERO) != 0)
        {
            throw new ServiceException("应收单已存在收款、坏账或对冲记录，禁止反确认");
        }
        assertNoDraftDependentsOnReceivable(receivableId);
        if (finVoucherEventService != null)
        {
            finVoucherEventService.cancelPendingVoucherEvents("receivable", receivableId);
        }
        finReceivable.setBizStatus("draft");
        finReceivable.setStatus("0");
        finReceivable.setUpdateBy(operator);
        return finReceivableMapper.updateFinReceivable(finReceivable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinReceivable createRedReceivableForReturn(Long customerId, String sourceBillType, Long sourceBillId,
        String sourceBillNo, Date businessDate, BigDecimal returnAmount, String operator, String remark)
    {
        if (customerId == null || sourceBillId == null || StringUtils.isEmpty(sourceBillType))
        {
            return null;
        }
        if (returnAmount == null || returnAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            return null;
        }
        FinReceivable existing = finReceivableMapper.selectFinReceivableBySource(sourceBillType, sourceBillId);
        if (existing != null)
        {
            return existing;
        }
        Date bizDate = businessDate == null ? new Date() : businessDate;
        FinReceivable redAr = new FinReceivable();
        redAr.setReceivableNo(generateReceivableNo());
        redAr.setCustomerId(customerId);
        redAr.setBillType(sourceBillType);
        redAr.setBillId(sourceBillId);
        redAr.setSourceBillType(sourceBillType);
        redAr.setSourceBillId(sourceBillId);
        redAr.setSourceBillNo(sourceBillNo);
        redAr.setBusinessDate(bizDate);
        redAr.setDueDate(bizDate);
        redAr.setAmount(returnAmount.abs().negate());
        redAr.setBillDirection("2");
        redAr.setReceivedAmount(BigDecimal.ZERO);
        redAr.setStatus("0");
        redAr.setBizStatus("confirmed");
        redAr.setCreateBy(operator);
        redAr.setRemark(StringUtils.isEmpty(remark) ? "退货自动生成红字应收冲减单" : remark);
        finReceivableMapper.insertFinReceivable(redAr);
        return redAr;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int offsetRedReceivable(Long redReceivableId, Long targetReceivableId, BigDecimal offsetAmount, String operator)
    {
        if (redReceivableId == null || targetReceivableId == null)
        {
            throw new ServiceException("红字应收单与目标正向应收单均不能为空");
        }
        if (Objects.equals(redReceivableId, targetReceivableId))
        {
            throw new ServiceException("红字应收单与目标正向应收单不能为同一单据");
        }
        if (offsetAmount == null || offsetAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("红蓝对冲金额必须大于0");
        }
        finGeneralLedgerService.assertPeriodOpen(new Date());

        FinReceivable redAr = getExistingFinReceivable(redReceivableId);
        FinReceivable blueAr = getExistingFinReceivable(targetReceivableId);
        if (!"2".equals(redAr.getBillDirection()) || defaultAmount(redAr.getAmount()).compareTo(BigDecimal.ZERO) >= 0)
        {
            throw new ServiceException("来源单据不是有效的红字应收冲减单");
        }
        if ("2".equals(blueAr.getBillDirection()) || defaultAmount(blueAr.getAmount()).compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("目标单据必须是正向应收单");
        }
        if (!"confirmed".equals(redAr.getBizStatus()) || !"confirmed".equals(blueAr.getBizStatus()))
        {
            throw new ServiceException("仅已审核确认的红蓝应收单允许执行对冲");
        }
        if (!Objects.equals(redAr.getCustomerId(), blueAr.getCustomerId()))
        {
            throw new ServiceException("红字应收单与目标正向应收单客户不一致，无法对冲");
        }

        // 红字单剩余可抵扣额 = |amount| - |receivedAmount|
        BigDecimal redRemainingCredit = defaultAmount(redAr.getAmount()).abs().subtract(defaultAmount(redAr.getReceivedAmount()).abs());
        if (offsetAmount.compareTo(redRemainingCredit) > 0)
        {
            throw new ServiceException("对冲金额超过红字应收单剩余可抵扣余额(" + redRemainingCredit + ")");
        }
        BigDecimal blueUnpaid = defaultAmount(blueAr.getAmount())
            .subtract(defaultAmount(blueAr.getReceivedAmount()))
            .subtract(defaultAmount(blueAr.getBadDebtAmount()));
        if (offsetAmount.compareTo(blueUnpaid) > 0)
        {
            throw new ServiceException("对冲金额超过目标正向应收单未收余额(" + blueUnpaid + ")");
        }

        BigDecimal newBlueReceived = defaultAmount(blueAr.getReceivedAmount()).add(offsetAmount);
        blueAr.setReceivedAmount(newBlueReceived);
        blueAr.setStatus(calculateCollectionStatus(newBlueReceived, blueAr.getBadDebtAmount(), blueAr.getAmount()));
        blueAr.setUpdateBy(operator);
        finReceivableMapper.updateFinReceivable(blueAr);

        BigDecimal newRedReceived = defaultAmount(redAr.getReceivedAmount()).subtract(offsetAmount);
        redAr.setReceivedAmount(newRedReceived);
        redAr.setStatus(calculateRedOffsetStatus(newRedReceived, redAr.getAmount()));
        redAr.setUpdateBy(operator);
        int rows = finReceivableMapper.updateFinReceivable(redAr);
        if (rows > 0 && finOffsetLogMapper != null)
        {
            FinOffsetLog log = new FinOffsetLog();
            log.setOffsetType("receivable");
            log.setPositiveBillId(targetReceivableId);
            log.setRedBillId(redReceivableId);
            log.setOffsetAmount(offsetAmount);
            log.setStatus("0");
            log.setCreateBy(operator);
            log.setRemark("应收红蓝对冲：" + redAr.getReceivableNo() + " -> " + blueAr.getReceivableNo());
            finOffsetLogMapper.insertFinOffsetLog(log);
        }
        return rows;
    }

    @Override
    public List<FinOffsetLog> selectOffsetLogs(Long receivableId)
    {
        if (finOffsetLogMapper == null) return new ArrayList<FinOffsetLog>();
        FinOffsetLog q = new FinOffsetLog();
        q.setOffsetType("receivable");
        List<FinOffsetLog> all = finOffsetLogMapper.selectFinOffsetLogList(q);
        if (receivableId == null || all == null) return all == null ? new ArrayList<FinOffsetLog>() : all;
        List<FinOffsetLog> filtered = new ArrayList<FinOffsetLog>();
        for (FinOffsetLog row : all)
        {
            if (receivableId.equals(row.getPositiveBillId()) || receivableId.equals(row.getRedBillId()))
            {
                filtered.add(row);
            }
        }
        return filtered;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unoffsetRedReceivable(Long offsetId, String operator)
    {
        if (finOffsetLogMapper == null || offsetId == null) throw new ServiceException("对冲流水不存在");
        FinOffsetLog log = finOffsetLogMapper.selectFinOffsetLogById(offsetId);
        if (log == null || !"receivable".equals(log.getOffsetType())) throw new ServiceException("应收对冲记录不存在");
        if (!"0".equals(log.getStatus())) throw new ServiceException("该对冲记录已撤销，请勿重复操作");
        finGeneralLedgerService.assertPeriodOpen(new Date());

        FinReceivable blueAr = getExistingFinReceivable(log.getPositiveBillId());
        FinReceivable redAr = getExistingFinReceivable(log.getRedBillId());
        BigDecimal amt = defaultAmount(log.getOffsetAmount());

        BigDecimal nextBlueReceived = defaultAmount(blueAr.getReceivedAmount()).subtract(amt);
        if (nextBlueReceived.compareTo(BigDecimal.ZERO) < 0) nextBlueReceived = BigDecimal.ZERO;
        blueAr.setReceivedAmount(nextBlueReceived);
        blueAr.setStatus(calculateCollectionStatus(nextBlueReceived, blueAr.getBadDebtAmount(), blueAr.getAmount()));
        blueAr.setUpdateBy(operator);
        finReceivableMapper.updateFinReceivable(blueAr);

        BigDecimal nextRedReceived = defaultAmount(redAr.getReceivedAmount()).add(amt);
        if (nextRedReceived.compareTo(BigDecimal.ZERO) > 0) nextRedReceived = BigDecimal.ZERO;
        redAr.setReceivedAmount(nextRedReceived);
        redAr.setStatus(calculateRedOffsetStatus(nextRedReceived, redAr.getAmount()));
        redAr.setUpdateBy(operator);
        finReceivableMapper.updateFinReceivable(redAr);

        log.setStatus("1");
        log.setReverseBy(operator);
        log.setReverseTime(new Date());
        return finOffsetLogMapper.updateFinOffsetLog(log);
    }

    private void assertNoDraftDependentsOnReceivable(Long receivableId)
    {
        if (finReceiptMapper != null)
        {
            FinReceipt rq = new FinReceipt();
            rq.setReceivableId(receivableId);
            List<FinReceipt> list = finReceiptMapper.selectFinReceiptList(rq);
            if (list != null && !list.isEmpty()) throw new ServiceException("该应收单存在关联收款单(" + list.get(0).getReceiptNo() + ")，禁止反确认");
        }
        if (finWriteOffMapper != null)
        {
            FinWriteOff wq = new FinWriteOff();
            wq.setReceivableId(receivableId);
            List<FinWriteOff> list = finWriteOffMapper.selectFinWriteOffList(wq);
            if (list != null && !list.isEmpty()) throw new ServiceException("该应收单存在关联核销单(" + list.get(0).getWriteOffNo() + ")，禁止反确认");
        }
        if (finBadDebtMapper != null)
        {
            FinBadDebt bq = new FinBadDebt();
            bq.setReceivableId(receivableId);
            List<FinBadDebt> list = finBadDebtMapper.selectFinBadDebtList(bq);
            if (list != null && !list.isEmpty()) throw new ServiceException("该应收单存在关联坏账单(" + list.get(0).getBadDebtNo() + ")，禁止反确认");
        }
    }

    private void validateReceivableAmount(FinReceivable finReceivable)
    {
        if ("2".equals(finReceivable.getBillDirection()))
        {
            if (finReceivable.getAmount() == null || finReceivable.getAmount().compareTo(BigDecimal.ZERO) >= 0)
            {
                throw new ServiceException("红字应收冲减单金额必须小于0");
            }
            return;
        }
        if (finReceivable.getAmount() == null || finReceivable.getAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("应收金额必须大于0");
        }
        if (defaultAmount(finReceivable.getReceivedAmount()).compareTo(finReceivable.getAmount()) > 0)
        {
            throw new ServiceException("应收单已收金额不能大于应收金额");
        }
    }

    private FinReceivable getExistingFinReceivable(Long receivableId)
    {
        FinReceivable finReceivable = finReceivableMapper.selectFinReceivableById(receivableId);
        if (finReceivable == null)
        {
            throw new ServiceException("应收单不存在");
        }
        return finReceivable;
    }

    private FinReceivable checkReceivableEditable(Long receivableId)
    {
        FinReceivable finReceivable = getExistingFinReceivable(receivableId);
        if ("confirmed".equals(finReceivable.getBizStatus()))
        {
            throw new ServiceException("应收单已审核，无法修改");
        }
        return finReceivable;
    }

    private FinReceivable checkReceivableDeletable(Long receivableId)
    {
        FinReceivable finReceivable = getExistingFinReceivable(receivableId);
        if ("confirmed".equals(finReceivable.getBizStatus()))
        {
            throw new ServiceException("应收单已审核，无法删除");
        }
        if (defaultAmount(finReceivable.getReceivedAmount()).compareTo(BigDecimal.ZERO) != 0
            || defaultAmount(finReceivable.getBadDebtAmount()).compareTo(BigDecimal.ZERO) != 0)
        {
            throw new ServiceException("应收单已存在收款、坏账或对冲记录，无法删除");
        }
        return finReceivable;
    }

    private BigDecimal defaultAmount(BigDecimal amount)
    {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private String generateReceivableNo()
    {
        String receivableNoPrefix = "ar" + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String currentMaxReceivableNo = finReceivableMapper.selectMaxReceivableNoByPrefix(receivableNoPrefix);
        int nextSequence = 1;
        if (StringUtils.isNotEmpty(currentMaxReceivableNo) && currentMaxReceivableNo.length() > receivableNoPrefix.length())
        {
            String sequenceText = currentMaxReceivableNo.substring(receivableNoPrefix.length());
            if (StringUtils.isNumeric(sequenceText))
            {
                nextSequence = Integer.parseInt(sequenceText) + 1;
            }
        }
        return receivableNoPrefix + String.format("%03d", nextSequence);
    }

    private String calculateCollectionStatus(BigDecimal receivedAmount, BigDecimal badDebtAmount, BigDecimal receivableAmount)
    {
        BigDecimal settled = defaultAmount(receivedAmount).add(defaultAmount(badDebtAmount));
        BigDecimal receivableAmountValue = defaultAmount(receivableAmount);
        if (settled.compareTo(BigDecimal.ZERO) <= 0)
        {
            return "0";
        }
        if (settled.compareTo(receivableAmountValue) >= 0)
        {
            return "2";
        }
        return "1";
    }

    private String calculateRedOffsetStatus(BigDecimal redOffsetAmount, BigDecimal redTotalAmount)
    {
        BigDecimal offsetAbs = defaultAmount(redOffsetAmount).abs();
        BigDecimal totalAbs = defaultAmount(redTotalAmount).abs();
        if (offsetAbs.compareTo(BigDecimal.ZERO) <= 0)
        {
            return "0";
        }
        if (offsetAbs.compareTo(totalAbs) >= 0)
        {
            return "2";
        }
        return "1";
    }
}
