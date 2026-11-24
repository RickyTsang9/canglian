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
import com.canglian.business.domain.FinOffsetLog;
import com.canglian.business.domain.FinPayable;
import com.canglian.business.domain.FinPayment;
import com.canglian.business.domain.FinWriteOff;
import com.canglian.business.domain.MdSupplier;
import com.canglian.business.mapper.FinOffsetLogMapper;
import com.canglian.business.mapper.FinPayableMapper;
import com.canglian.business.mapper.FinPaymentMapper;
import com.canglian.business.mapper.FinWriteOffMapper;
import com.canglian.business.mapper.MdSupplierMapper;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IFinPayableService;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 应付单 服务层实现
 * 
 * @author canglian
 */
@Service
public class FinPayableServiceImpl implements IFinPayableService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private FinPayableMapper finPayableMapper;

    @Autowired
    private IFinGeneralLedgerService finGeneralLedgerService;

    @Autowired(required = false)
    private FinOffsetLogMapper finOffsetLogMapper;

    @Autowired(required = false)
    private IFinVoucherEventService finVoucherEventService;

    @Autowired(required = false)
    private FinPaymentMapper finPaymentMapper;

    @Autowired(required = false)
    private FinWriteOffMapper finWriteOffMapper;

    @Autowired(required = false)
    private MdSupplierMapper mdSupplierMapper;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Override
    public FinPayable selectFinPayableById(Long payableId)
    {
        FinPayable ap = finPayableMapper.selectFinPayableById(payableId);
        if (ap != null && bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(ap.getDeptId(), ap.getOwnerUserId());
        }
        return ap;
    }

    @Override
    public List<FinPayable> selectFinPayableList(FinPayable finPayable)
    {
        if (bizScopeHelper != null)
        {
            bizScopeHelper.applyScopeFilter(finPayable, "", true, false);
        }
        return finPayableMapper.selectFinPayableList(finPayable);
    }

    @Override
    public int insertFinPayable(FinPayable finPayable)
    {
        if (bizScopeHelper != null)
        {
            if (finPayable.getDeptId() == null) finPayable.setDeptId(bizScopeHelper.getCurrentDeptId());
            if (finPayable.getOwnerUserId() == null) finPayable.setOwnerUserId(bizScopeHelper.getCurrentUserId());
        }
        if (finPayable.getBusinessDate() == null)
        {
            finPayable.setBusinessDate(new Date());
        }
        if (finPayable.getDueDate() == null)
        {
            finPayable.setDueDate(resolveSupplierDueDate(finPayable.getSupplierId(), finPayable.getBusinessDate(), null));
        }
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(finPayable.getBusinessDate());
        }
        if (StringUtils.isEmpty(finPayable.getPayableNo()))
        {
            finPayable.setPayableNo(generatePayableNo());
        }
        if (StringUtils.isEmpty(finPayable.getSourceBillType()))
        {
            finPayable.setSourceBillType(null);
        }
        finPayable.setBillDirection("1");
        finPayable.setPaidAmount(BigDecimal.ZERO);
        finPayable.setStatus(calculatePaymentStatus(finPayable.getPaidAmount(), finPayable.getAmount()));
        finPayable.setBizStatus("draft");
        validatePayableAmount(finPayable);
        return finPayableMapper.insertFinPayable(finPayable);
    }

    @Override
    public int updateFinPayable(FinPayable finPayable)
    {
        FinPayable existingFinPayable = checkPayableEditable(finPayable.getPayableId());
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(existingFinPayable.getBusinessDate() == null ? new Date() : existingFinPayable.getBusinessDate());
            if (finPayable.getBusinessDate() != null)
            {
                finGeneralLedgerService.assertPeriodOpen(finPayable.getBusinessDate());
            }
        }
        if (StringUtils.isEmpty(finPayable.getSourceBillType()))
        {
            finPayable.setSourceBillType(null);
        }
        finPayable.setPayableNo(existingFinPayable.getPayableNo());
        finPayable.setBillDirection(StringUtils.isEmpty(existingFinPayable.getBillDirection()) ? "1" : existingFinPayable.getBillDirection());
        finPayable.setStatus(existingFinPayable.getStatus());
        finPayable.setBizStatus(existingFinPayable.getBizStatus());
        finPayable.setPaidAmount(existingFinPayable.getPaidAmount());
        validatePayableAmount(finPayable);
        return finPayableMapper.updateFinPayable(finPayable);
    }

    @Override
    public int deleteFinPayableById(Long payableId)
    {
        FinPayable ap = checkPayableDeletable(payableId);
        if (finGeneralLedgerService != null) finGeneralLedgerService.assertPeriodOpen(ap.getBusinessDate());
        return finPayableMapper.deleteFinPayableById(payableId);
    }

    @Override
    public int deleteFinPayableByIds(Long[] payableIds)
    {
        for (Long payableId : payableIds)
        {
            FinPayable ap = checkPayableDeletable(payableId);
            if (finGeneralLedgerService != null) finGeneralLedgerService.assertPeriodOpen(ap.getBusinessDate());
        }
        return finPayableMapper.deleteFinPayableByIds(payableIds);
    }

    @Override
    public int auditFinPayable(Long payableId, String operator)
    {
        FinPayable finPayable = getExistingFinPayable(payableId);
        if ("confirmed".equals(finPayable.getBizStatus()))
        {
            throw new ServiceException("应付单已审核，请勿重复操作");
        }
        finGeneralLedgerService.assertPeriodOpen(finPayable.getBusinessDate());
        validatePayableAmount(finPayable);
        finPayable.setStatus(calculatePaymentStatus(finPayable.getPaidAmount(), finPayable.getAmount()));
        finPayable.setBizStatus("confirmed");
        finPayable.setUpdateBy(operator);
        int rows = finPayableMapper.updateFinPayable(finPayable);
        if (rows > 0 && finVoucherEventService != null
            && (StringUtils.isEmpty(finPayable.getBillType()) || "manual".equals(finPayable.getBillType()) || "invoice".equals(finPayable.getBillType())))
        {
            finVoucherEventService.recordVoucherEvent("payable", finPayable.getPayableId(), finPayable.getPayableNo(),
                "manual_payable", finPayable.getBusinessDate(), finPayable.getAmount(), operator,
                "invoice".equals(finPayable.getBillType()) ? "独立采购发票自动应付待生成凭证" : "手工应付单审核待生成凭证");
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unconfirmFinPayable(Long payableId, String operator)
    {
        FinPayable finPayable = getExistingFinPayable(payableId);
        if (!"confirmed".equals(finPayable.getBizStatus()))
        {
            throw new ServiceException("仅已审核确认的应付单允许反确认");
        }
        if ("2".equals(finPayable.getBillDirection()))
        {
            throw new ServiceException("退货自动生成的红字应付冲减单不允许直接反确认，请操作原退货单");
        }
        if ("inbound".equals(finPayable.getBillType()) || "invoice".equals(finPayable.getBillType()) || "opening".equals(finPayable.getBillType()))
        {
            throw new ServiceException("由业务单据或供应商期初自动生成的应付单不允许直接反确认，请操作来源单据或供应商档案");
        }
        finGeneralLedgerService.assertPeriodOpen(finPayable.getBusinessDate());
        if (defaultAmount(finPayable.getPaidAmount()).compareTo(BigDecimal.ZERO) != 0)
        {
            throw new ServiceException("应付单已存在付款或对冲记录，禁止反确认");
        }
        assertNoDraftDependentsOnPayable(payableId);
        if (finVoucherEventService != null)
        {
            finVoucherEventService.cancelPendingVoucherEvents("payable", payableId);
        }
        finPayable.setBizStatus("draft");
        finPayable.setStatus("0");
        finPayable.setUpdateBy(operator);
        return finPayableMapper.updateFinPayable(finPayable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinPayable createRedPayableForReturn(Long supplierId, String sourceBillType, Long sourceBillId,
        String sourceBillNo, Date businessDate, BigDecimal returnAmount, String operator, String remark)
    {
        if (supplierId == null || sourceBillId == null || StringUtils.isEmpty(sourceBillType))
        {
            return null;
        }
        if (returnAmount == null || returnAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            return null;
        }
        FinPayable existing = finPayableMapper.selectFinPayableBySource(sourceBillType, sourceBillId);
        if (existing != null)
        {
            return existing;
        }
        Date bizDate = businessDate == null ? new Date() : businessDate;
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(bizDate);
        }
        FinPayable redAp = new FinPayable();
        redAp.setPayableNo(generatePayableNo());
        redAp.setSupplierId(supplierId);
        redAp.setBillType(sourceBillType);
        redAp.setBillId(sourceBillId);
        redAp.setSourceBillType(sourceBillType);
        redAp.setSourceBillId(sourceBillId);
        redAp.setSourceBillNo(sourceBillNo);
        redAp.setBusinessDate(bizDate);
        redAp.setDueDate(bizDate);
        redAp.setAmount(returnAmount.abs().negate());
        redAp.setBillDirection("2");
        redAp.setPaidAmount(BigDecimal.ZERO);
        redAp.setStatus("0");
        redAp.setBizStatus("confirmed");
        redAp.setCreateBy(operator);
        if (bizScopeHelper != null)
        {
            redAp.setDeptId(bizScopeHelper.getCurrentDeptId());
            redAp.setOwnerUserId(bizScopeHelper.getCurrentUserId());
        }
        redAp.setRemark(StringUtils.isEmpty(remark) ? "采购退货自动生成红字应付冲减单" : remark);
        finPayableMapper.insertFinPayable(redAp);
        return redAp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int offsetRedPayable(Long redPayableId, Long targetPayableId, BigDecimal offsetAmount, String operator)
    {
        if (redPayableId == null || targetPayableId == null)
        {
            throw new ServiceException("红字应付单与目标正向应付单均不能为空");
        }
        if (Objects.equals(redPayableId, targetPayableId))
        {
            throw new ServiceException("红字应付单与目标正向应付单不能为同一单据");
        }
        if (offsetAmount == null || offsetAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("红蓝对冲金额必须大于0");
        }
        finGeneralLedgerService.assertPeriodOpen(new Date());

        FinPayable redAp = getExistingFinPayable(redPayableId);
        FinPayable blueAp = getExistingFinPayable(targetPayableId);
        finGeneralLedgerService.assertPeriodOpen(redAp.getBusinessDate());
        finGeneralLedgerService.assertPeriodOpen(blueAp.getBusinessDate());
        if (!"2".equals(redAp.getBillDirection()) || defaultAmount(redAp.getAmount()).compareTo(BigDecimal.ZERO) >= 0)
        {
            throw new ServiceException("来源单据不是有效的红字应付冲减单");
        }
        if ("2".equals(blueAp.getBillDirection()) || defaultAmount(blueAp.getAmount()).compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("目标单据必须是正向应付单");
        }
        if (!"confirmed".equals(redAp.getBizStatus()) || !"confirmed".equals(blueAp.getBizStatus()))
        {
            throw new ServiceException("仅已审核确认的红蓝应付单允许执行对冲");
        }
        if (!Objects.equals(redAp.getSupplierId(), blueAp.getSupplierId()))
        {
            throw new ServiceException("红字应付单与目标正向应付单供应商不一致，无法对冲");
        }

        BigDecimal redRemainingCredit = defaultAmount(redAp.getAmount()).abs().subtract(defaultAmount(redAp.getPaidAmount()).abs());
        if (offsetAmount.compareTo(redRemainingCredit) > 0)
        {
            throw new ServiceException("对冲金额超过红字应付单剩余可抵扣余额(" + redRemainingCredit + ")");
        }
        BigDecimal blueUnpaid = defaultAmount(blueAp.getAmount()).subtract(defaultAmount(blueAp.getPaidAmount()));
        if (offsetAmount.compareTo(blueUnpaid) > 0)
        {
            throw new ServiceException("对冲金额超过目标正向应付单未付余额(" + blueUnpaid + ")");
        }

        BigDecimal newBluePaid = defaultAmount(blueAp.getPaidAmount()).add(offsetAmount);
        blueAp.setPaidAmount(newBluePaid);
        blueAp.setStatus(calculatePaymentStatus(newBluePaid, blueAp.getAmount()));
        blueAp.setUpdateBy(operator);
        finPayableMapper.updateFinPayable(blueAp);

        BigDecimal newRedPaid = defaultAmount(redAp.getPaidAmount()).subtract(offsetAmount);
        redAp.setPaidAmount(newRedPaid);
        redAp.setStatus(calculateRedOffsetStatus(newRedPaid, redAp.getAmount()));
        redAp.setUpdateBy(operator);
        int rows = finPayableMapper.updateFinPayable(redAp);
        if (rows > 0 && finOffsetLogMapper != null)
        {
            FinOffsetLog log = new FinOffsetLog();
            log.setOffsetType("payable");
            log.setPositiveBillId(targetPayableId);
            log.setRedBillId(redPayableId);
            log.setOffsetAmount(offsetAmount);
            log.setStatus("0");
            log.setCreateBy(operator);
            log.setRemark("应付红蓝对冲：" + redAp.getPayableNo() + " -> " + blueAp.getPayableNo());
            finOffsetLogMapper.insertFinOffsetLog(log);
        }
        return rows;
    }

    @Override
    public List<FinOffsetLog> selectOffsetLogs(Long payableId)
    {
        if (finOffsetLogMapper == null) return new ArrayList<FinOffsetLog>();
        FinOffsetLog q = new FinOffsetLog();
        q.setOffsetType("payable");
        List<FinOffsetLog> all = finOffsetLogMapper.selectFinOffsetLogList(q);
        if (payableId == null || all == null) return all == null ? new ArrayList<FinOffsetLog>() : all;
        List<FinOffsetLog> filtered = new ArrayList<FinOffsetLog>();
        for (FinOffsetLog row : all)
        {
            if (payableId.equals(row.getPositiveBillId()) || payableId.equals(row.getRedBillId()))
            {
                filtered.add(row);
            }
        }
        return filtered;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unoffsetRedPayable(Long offsetId, String operator)
    {
        if (finOffsetLogMapper == null || offsetId == null) throw new ServiceException("对冲流水不存在");
        FinOffsetLog log = finOffsetLogMapper.selectFinOffsetLogById(offsetId);
        if (log == null || !"payable".equals(log.getOffsetType())) throw new ServiceException("应付对冲记录不存在");
        if (!"0".equals(log.getStatus())) throw new ServiceException("该对冲记录已撤销，请勿重复操作");
        finGeneralLedgerService.assertPeriodOpen(new Date());

        FinPayable blueAp = getExistingFinPayable(log.getPositiveBillId());
        FinPayable redAp = getExistingFinPayable(log.getRedBillId());
        finGeneralLedgerService.assertPeriodOpen(redAp.getBusinessDate());
        finGeneralLedgerService.assertPeriodOpen(blueAp.getBusinessDate());
        BigDecimal amt = defaultAmount(log.getOffsetAmount());

        BigDecimal nextBluePaid = defaultAmount(blueAp.getPaidAmount()).subtract(amt);
        if (nextBluePaid.compareTo(BigDecimal.ZERO) < 0) nextBluePaid = BigDecimal.ZERO;
        blueAp.setPaidAmount(nextBluePaid);
        blueAp.setStatus(calculatePaymentStatus(nextBluePaid, blueAp.getAmount()));
        blueAp.setUpdateBy(operator);
        finPayableMapper.updateFinPayable(blueAp);

        BigDecimal nextRedPaid = defaultAmount(redAp.getPaidAmount()).add(amt);
        if (nextRedPaid.compareTo(BigDecimal.ZERO) > 0) nextRedPaid = BigDecimal.ZERO;
        redAp.setPaidAmount(nextRedPaid);
        redAp.setStatus(calculateRedOffsetStatus(nextRedPaid, redAp.getAmount()));
        redAp.setUpdateBy(operator);
        finPayableMapper.updateFinPayable(redAp);

        log.setStatus("1");
        log.setReverseBy(operator);
        log.setReverseTime(new Date());
        return finOffsetLogMapper.updateFinOffsetLog(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinPayable syncOpeningPayable(Long supplierId, String supplierCode, BigDecimal openingAmount, Integer payableDays, String operator)
    {
        if (supplierId == null)
        {
            return null;
        }
        BigDecimal amt = defaultAmount(openingAmount);
        if (amt.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("期初应付余额不能小于0");
        }
        FinPayable existing = finPayableMapper.selectFinPayableBySource("supplier_opening", supplierId);
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
            FinPayable openAp = new FinPayable();
            openAp.setPayableNo(generatePayableNo());
            openAp.setSupplierId(supplierId);
            openAp.setBillType("opening");
            openAp.setBillId(supplierId);
            openAp.setSourceBillType("supplier_opening");
            openAp.setSourceBillId(supplierId);
            openAp.setSourceBillNo(StringUtils.isNotEmpty(supplierCode) ? supplierCode : ("SUP-" + supplierId));
            openAp.setBusinessDate(now);
            openAp.setDueDate(resolveSupplierDueDate(supplierId, now, payableDays));
            openAp.setAmount(amt);
            openAp.setBillDirection("1");
            openAp.setPaidAmount(BigDecimal.ZERO);
            openAp.setStatus("0");
            openAp.setBizStatus("confirmed");
            openAp.setCreateBy(StringUtils.isEmpty(operator) ? "admin" : operator);
            openAp.setRemark("供应商档案期初应付余额建账");
            finPayableMapper.insertFinPayable(openAp);
            return openAp;
        }
        if (defaultAmount(existing.getAmount()).compareTo(amt) == 0)
        {
            return existing;
        }
        if (defaultAmount(existing.getPaidAmount()).compareTo(BigDecimal.ZERO) != 0)
        {
            throw new ServiceException("供应商期初应付单(" + existing.getPayableNo() + ")已产生付款或核销记录，禁止直接修改期初应付余额");
        }
        assertNoDraftDependentsOnPayable(existing.getPayableId());
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(existing.getBusinessDate());
        }
        if (amt.compareTo(BigDecimal.ZERO) == 0)
        {
            finPayableMapper.deleteFinPayableById(existing.getPayableId());
            return null;
        }
        existing.setAmount(amt);
        existing.setDueDate(resolveSupplierDueDate(supplierId, existing.getBusinessDate(), payableDays));
        existing.setUpdateBy(StringUtils.isEmpty(operator) ? "admin" : operator);
        finPayableMapper.updateFinPayable(existing);
        return existing;
    }

    private Date resolveSupplierDueDate(Long supplierId, Date businessDate, Integer overrideDays)
    {
        Date base = businessDate == null ? new Date() : businessDate;
        int days = 30;
        if (overrideDays != null && overrideDays >= 0)
        {
            days = overrideDays;
        }
        else if (supplierId != null && mdSupplierMapper != null)
        {
            MdSupplier s = mdSupplierMapper.selectMdSupplierById(supplierId);
            if (s != null && s.getPayableDays() != null && s.getPayableDays() >= 0)
            {
                days = s.getPayableDays();
            }
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(base);
        cal.add(Calendar.DAY_OF_MONTH, days);
        return cal.getTime();
    }

    private void assertNoDraftDependentsOnPayable(Long payableId)
    {
        if (finPaymentMapper != null)
        {
            FinPayment pq = new FinPayment();
            pq.setPayableId(payableId);
            List<FinPayment> list = finPaymentMapper.selectFinPaymentList(pq);
            if (list != null && !list.isEmpty()) throw new ServiceException("该应付单存在关联付款单(" + list.get(0).getPaymentNo() + ")，禁止反确认");
        }
        if (finWriteOffMapper != null)
        {
            FinWriteOff wq = new FinWriteOff();
            wq.setPayableId(payableId);
            List<FinWriteOff> list = finWriteOffMapper.selectFinWriteOffList(wq);
            if (list != null && !list.isEmpty()) throw new ServiceException("该应付单存在关联核销单(" + list.get(0).getWriteOffNo() + ")，禁止反确认");
        }
    }

    private void validatePayableAmount(FinPayable finPayable)
    {
        if ("2".equals(finPayable.getBillDirection()))
        {
            if (finPayable.getAmount() == null || finPayable.getAmount().compareTo(BigDecimal.ZERO) >= 0)
            {
                throw new ServiceException("红字应付冲减单金额必须小于0");
            }
            return;
        }
        if (finPayable.getAmount() == null || finPayable.getAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("应付金额必须大于0");
        }
        if (defaultAmount(finPayable.getPaidAmount()).compareTo(finPayable.getAmount()) > 0)
        {
            throw new ServiceException("应付单已付金额不能大于应付金额");
        }
    }

    private FinPayable getExistingFinPayable(Long payableId)
    {
        FinPayable finPayable = finPayableMapper.selectFinPayableById(payableId);
        if (finPayable == null)
        {
            throw new ServiceException("应付单不存在");
        }
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(finPayable.getDeptId(), finPayable.getOwnerUserId());
        }
        return finPayable;
    }

    private FinPayable checkPayableEditable(Long payableId)
    {
        FinPayable finPayable = getExistingFinPayable(payableId);
        if ("confirmed".equals(finPayable.getBizStatus()))
        {
            throw new ServiceException("应付单已审核，无法修改");
        }
        return finPayable;
    }

    private FinPayable checkPayableDeletable(Long payableId)
    {
        FinPayable finPayable = getExistingFinPayable(payableId);
        if ("confirmed".equals(finPayable.getBizStatus()))
        {
            throw new ServiceException("应付单已审核，无法删除");
        }
        if (defaultAmount(finPayable.getPaidAmount()).compareTo(BigDecimal.ZERO) != 0)
        {
            throw new ServiceException("应付单已存在付款或对冲记录，无法删除");
        }
        return finPayable;
    }

    private BigDecimal defaultAmount(BigDecimal amount)
    {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private String generatePayableNo()
    {
        String payableNoPrefix = "ap" + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String currentMaxPayableNo = finPayableMapper.selectMaxPayableNoByPrefix(payableNoPrefix);
        int nextSequence = 1;
        if (StringUtils.isNotEmpty(currentMaxPayableNo) && currentMaxPayableNo.length() > payableNoPrefix.length())
        {
            String sequenceText = currentMaxPayableNo.substring(payableNoPrefix.length());
            if (StringUtils.isNumeric(sequenceText))
            {
                nextSequence = Integer.parseInt(sequenceText) + 1;
            }
        }
        return payableNoPrefix + String.format("%03d", nextSequence);
    }

    private String calculatePaymentStatus(BigDecimal paidAmount, BigDecimal payableAmount)
    {
        BigDecimal paidAmountValue = defaultAmount(paidAmount);
        BigDecimal payableAmountValue = defaultAmount(payableAmount);
        if (paidAmountValue.compareTo(BigDecimal.ZERO) <= 0)
        {
            return "0";
        }
        if (paidAmountValue.compareTo(payableAmountValue) >= 0)
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
