package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.canglian.business.domain.MdCustomer;
import com.canglian.business.mapper.FinBadDebtMapper;
import com.canglian.business.mapper.FinOffsetLogMapper;
import com.canglian.business.mapper.FinReceiptMapper;
import com.canglian.business.mapper.FinReceivableMapper;
import com.canglian.business.mapper.FinWriteOffMapper;
import com.canglian.business.mapper.MdCustomerMapper;
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

    @Autowired(required = false)
    private MdCustomerMapper mdCustomerMapper;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Override
    public FinReceivable selectFinReceivableById(Long receivableId)
    {
        FinReceivable ar = finReceivableMapper.selectFinReceivableById(receivableId);
        if (ar != null && bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(ar.getDeptId(), ar.getOwnerUserId());
        }
        return ar;
    }

    @Override
    public List<FinReceivable> selectFinReceivableList(FinReceivable finReceivable)
    {
        if (bizScopeHelper != null)
        {
            bizScopeHelper.applyScopeFilter(finReceivable, "", true, false);
        }
        return finReceivableMapper.selectFinReceivableList(finReceivable);
    }

    @Override
    public int insertFinReceivable(FinReceivable finReceivable)
    {
        if (bizScopeHelper != null)
        {
            if (finReceivable.getDeptId() == null) finReceivable.setDeptId(bizScopeHelper.getCurrentDeptId());
            if (finReceivable.getOwnerUserId() == null) finReceivable.setOwnerUserId(bizScopeHelper.getCurrentUserId());
        }
        if (finReceivable.getBusinessDate() == null)
        {
            finReceivable.setBusinessDate(new Date());
        }
        if (finReceivable.getDueDate() == null)
        {
            finReceivable.setDueDate(resolveCustomerDueDate(finReceivable.getCustomerId(), finReceivable.getBusinessDate(), null));
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
            && (StringUtils.isEmpty(finReceivable.getBillType()) || "manual".equals(finReceivable.getBillType()) || "invoice".equals(finReceivable.getBillType())))
        {
            finVoucherEventService.recordVoucherEvent("receivable", finReceivable.getReceivableId(), finReceivable.getReceivableNo(),
                "manual_receivable", finReceivable.getBusinessDate(), finReceivable.getAmount(), operator,
                "invoice".equals(finReceivable.getBillType()) ? "独立销售发票自动应收待生成凭证" : "手工应收单审核待生成凭证");
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
        if ("outbound".equals(finReceivable.getBillType()) || "invoice".equals(finReceivable.getBillType()) || "opening".equals(finReceivable.getBillType()))
        {
            throw new ServiceException("由业务单据或客户期初自动生成的应收单不允许直接反确认，请操作来源单据或客户档案");
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
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(bizDate);
        }
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
        if (bizScopeHelper != null)
        {
            redAr.setDeptId(bizScopeHelper.getCurrentDeptId());
            redAr.setOwnerUserId(bizScopeHelper.getCurrentUserId());
        }
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
        finGeneralLedgerService.assertPeriodOpen(redAr.getBusinessDate());
        finGeneralLedgerService.assertPeriodOpen(blueAr.getBusinessDate());
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
        finGeneralLedgerService.assertPeriodOpen(redAr.getBusinessDate());
        finGeneralLedgerService.assertPeriodOpen(blueAr.getBusinessDate());
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinReceivable syncOpeningReceivable(Long customerId, String customerCode, BigDecimal openingAmount, Integer receivableDays, String operator)
    {
        if (customerId == null)
        {
            return null;
        }
        BigDecimal amt = defaultAmount(openingAmount);
        if (amt.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("期初应收余额不能小于0");
        }
        FinReceivable existing = finReceivableMapper.selectFinReceivableBySource("customer_opening", customerId);
        if (existing == null)
        {
            if (amt.compareTo(BigDecimal.ZERO) == 0)
            {
                return null;
            }
            Date now = new Date();
            if (finGeneralLedgerService != null)
            {
                finGeneralLedgerService.assertPeriodOpen(now);
            }
            FinReceivable openAr = new FinReceivable();
            openAr.setReceivableNo(generateReceivableNo());
            openAr.setCustomerId(customerId);
            openAr.setBillType("opening");
            openAr.setBillId(customerId);
            openAr.setSourceBillType("customer_opening");
            openAr.setSourceBillId(customerId);
            openAr.setSourceBillNo(StringUtils.isNotEmpty(customerCode) ? customerCode : ("CUST-" + customerId));
            openAr.setBusinessDate(now);
            openAr.setDueDate(resolveCustomerDueDate(customerId, now, receivableDays));
            openAr.setAmount(amt);
            openAr.setBillDirection("1");
            openAr.setReceivedAmount(BigDecimal.ZERO);
            openAr.setBadDebtAmount(BigDecimal.ZERO);
            openAr.setStatus("0");
            openAr.setBizStatus("confirmed");
            openAr.setCreateBy(StringUtils.isEmpty(operator) ? "admin" : operator);
            openAr.setRemark("客户档案期初应收余额建账");
            finReceivableMapper.insertFinReceivable(openAr);
            return openAr;
        }
        if (defaultAmount(existing.getAmount()).compareTo(amt) == 0)
        {
            return existing;
        }
        if (defaultAmount(existing.getReceivedAmount()).compareTo(BigDecimal.ZERO) != 0
            || defaultAmount(existing.getBadDebtAmount()).compareTo(BigDecimal.ZERO) != 0)
        {
            throw new ServiceException("客户期初应收单(" + existing.getReceivableNo() + ")已产生收款、核销或坏账记录，禁止直接修改期初应收余额");
        }
        assertNoDraftDependentsOnReceivable(existing.getReceivableId());
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(existing.getBusinessDate());
        }
        if (amt.compareTo(BigDecimal.ZERO) == 0)
        {
            finReceivableMapper.deleteFinReceivableById(existing.getReceivableId());
            return null;
        }
        existing.setAmount(amt);
        existing.setDueDate(resolveCustomerDueDate(customerId, existing.getBusinessDate(), receivableDays));
        existing.setUpdateBy(StringUtils.isEmpty(operator) ? "admin" : operator);
        finReceivableMapper.updateFinReceivable(existing);
        return existing;
    }

    private Date resolveCustomerDueDate(Long customerId, Date businessDate, Integer overrideDays)
    {
        Date base = businessDate == null ? new Date() : businessDate;
        int days = 30;
        if (overrideDays != null && overrideDays >= 0)
        {
            days = overrideDays;
        }
        else if (customerId != null && mdCustomerMapper != null)
        {
            MdCustomer c = mdCustomerMapper.selectMdCustomerById(customerId);
            if (c != null && c.getReceivableDays() != null && c.getReceivableDays() >= 0)
            {
                days = c.getReceivableDays();
            }
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(base);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
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
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(finReceivable.getDeptId(), finReceivable.getOwnerUserId());
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
