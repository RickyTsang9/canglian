package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.FinPayable;
import com.canglian.business.domain.FinPayment;
import com.canglian.business.domain.FinWriteOff;
import com.canglian.business.mapper.FinPayableMapper;
import com.canglian.business.mapper.FinPaymentMapper;
import com.canglian.business.mapper.FinWriteOffMapper;
import com.canglian.business.service.IFinFundLogService;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IFinPaymentService;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 付款单 服务层实现（草稿 -> 确认记账 -> 反确认 状态机 + 资金流水）
 *
 * @author canglian
 */
@Service
public class FinPaymentServiceImpl implements IFinPaymentService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private FinPaymentMapper finPaymentMapper;

    @Autowired
    private FinPayableMapper finPayableMapper;

    @Autowired
    private FinWriteOffMapper finWriteOffMapper;

    @Autowired
    private IFinFundLogService finFundLogService;

    @Autowired
    private IFinGeneralLedgerService finGeneralLedgerService;

    @Autowired(required = false)
    private IFinVoucherEventService finVoucherEventService;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Override
    public FinPayment selectFinPaymentById(Long paymentId)
    {
        FinPayment payment = finPaymentMapper.selectFinPaymentById(paymentId);
        if (payment != null && bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(payment.getDeptId(), payment.getOwnerUserId(), null);
        }
        return payment;
    }

    @Override
    public List<FinPayment> selectFinPaymentList(FinPayment finPayment)
    {
        if (bizScopeHelper != null)
        {
            bizScopeHelper.applyScopeFilter(finPayment, "", true, false);
        }
        return finPaymentMapper.selectFinPaymentList(finPayment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinPayment(FinPayment finPayment)
    {
        if (bizScopeHelper != null)
        {
            if (finPayment.getDeptId() == null) finPayment.setDeptId(bizScopeHelper.currentDeptId());
            if (finPayment.getOwnerUserId() == null) finPayment.setOwnerUserId(bizScopeHelper.currentUserId());
        }
        if (StringUtils.isEmpty(finPayment.getPaymentNo()))
        {
            finPayment.setPaymentNo(generatePaymentNo());
        }
        if (finPayment.getPaymentDate() == null)
        {
            finPayment.setPaymentDate(new Date());
        }
        finGeneralLedgerService.assertPeriodOpen(finPayment.getPaymentDate());
        validatePaymentAmount(finPayment.getAmount());
        if (finPayment.getPayableId() != null)
        {
            FinPayable ap = getExistingPayable(finPayment.getPayableId());
            validatePayableRelation(ap, finPayment);
        }
        finPayment.setStatus("0");
        finPayment.setBizStatus("draft");
        finPayment.setAuditBy(null);
        finPayment.setAuditTime(null);
        return finPaymentMapper.insertFinPayment(finPayment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinPayment(FinPayment finPayment)
    {
        FinPayment existing = getExistingPayment(finPayment.getPaymentId());
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(existing.getDeptId(), existing.getOwnerUserId(), null);
        if ("1".equals(existing.getStatus()) || "confirmed".equals(existing.getBizStatus()))
        {
            throw new ServiceException("付款单已确认入账，禁止直接修改，请先反确认");
        }
        finGeneralLedgerService.assertPeriodOpen(existing.getPaymentDate() == null ? new Date() : existing.getPaymentDate());
        if (finPayment.getPaymentDate() != null) finGeneralLedgerService.assertPeriodOpen(finPayment.getPaymentDate());
        validatePaymentAmount(finPayment.getAmount());
        if (finPayment.getPayableId() != null)
        {
            FinPayable ap = getExistingPayable(finPayment.getPayableId());
            validatePayableRelation(ap, finPayment);
        }
        finPayment.setPaymentNo(existing.getPaymentNo());
        finPayment.setStatus("0");
        finPayment.setBizStatus("draft");
        finPayment.setAuditBy(null);
        finPayment.setAuditTime(null);
        return finPaymentMapper.updateFinPayment(finPayment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirmFinPayment(Long paymentId, String operator)
    {
        FinPayment payment = getExistingPayment(paymentId);
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(payment.getDeptId(), payment.getOwnerUserId(), null);
        if ("1".equals(payment.getStatus()) || "confirmed".equals(payment.getBizStatus()))
        {
            throw new ServiceException("付款单已确认入账，请勿重复确认");
        }
        if (payment.getFundAccountId() == null)
        {
            throw new ServiceException("确认付款出账必须选择资金账户");
        }
        Date bizDate = payment.getPaymentDate() == null ? new Date() : payment.getPaymentDate();
        finGeneralLedgerService.assertPeriodOpen(bizDate);
        validatePaymentAmount(payment.getAmount());

        if (payment.getPayableId() != null)
        {
            FinPayable ap = getExistingPayable(payment.getPayableId());
            validatePayableRelation(ap, payment);
            adjustPayablePaidAmount(ap, payment.getAmount(), operator);
        }
        finFundLogService.recordBalanceChange(payment.getFundAccountId(), "payment",
            payment.getPaymentId(), payment.getPaymentNo(), defaultAmount(payment.getAmount()).negate(),
            bizDate, "供应商#" + payment.getSupplierId() + " 付款确认", payment.getRemark(), operator);

        payment.setStatus("1");
        payment.setBizStatus("confirmed");
        payment.setAuditBy(operator);
        payment.setAuditTime(new Date());
        payment.setUpdateBy(operator);
        int rows = finPaymentMapper.updateFinPayment(payment);
        if (rows > 0 && finVoucherEventService != null)
        {
            finVoucherEventService.recordVoucherEvent("payment", payment.getPaymentId(), payment.getPaymentNo(),
                "payment_confirm", bizDate, payment.getAmount(), operator, "付款确认出账待生成凭证");
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unconfirmFinPayment(Long paymentId, String operator)
    {
        FinPayment payment = getExistingPayment(paymentId);
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(payment.getDeptId(), payment.getOwnerUserId(), null);
        if (!"1".equals(payment.getStatus()) && !"confirmed".equals(payment.getBizStatus()))
        {
            throw new ServiceException("仅已确认状态的付款单允许反确认");
        }
        Date bizDate = payment.getPaymentDate() == null ? new Date() : payment.getPaymentDate();
        finGeneralLedgerService.assertPeriodOpen(bizDate);
        FinWriteOff woQ = new FinWriteOff();
        woQ.setPaymentId(paymentId);
        List<FinWriteOff> linkedWos = finWriteOffMapper.selectFinWriteOffList(woQ);
        if (linkedWos != null && !linkedWos.isEmpty())
        {
            throw new ServiceException("该付款单存在关联核销单(" + linkedWos.get(0).getWriteOffNo() + ")，请先反确认/删除核销单后再反确认付款单");
        }
        if (finVoucherEventService != null)
        {
            finVoucherEventService.cancelPendingVoucherEvents("payment", paymentId);
        }

        if (payment.getPayableId() != null)
        {
            FinPayable ap = getExistingPayable(payment.getPayableId());
            adjustPayablePaidAmount(ap, defaultAmount(payment.getAmount()).negate(), operator);
        }
        if (payment.getFundAccountId() != null)
        {
            finFundLogService.recordBalanceChange(payment.getFundAccountId(), "unconfirm",
                payment.getPaymentId(), payment.getPaymentNo(), defaultAmount(payment.getAmount()),
                new Date(), "付款单反确认冲回", "反确认原付款单：" + payment.getPaymentNo(), operator);
        }

        payment.setStatus("0");
        payment.setBizStatus("draft");
        payment.setAuditBy("");
        payment.setAuditTime(null);
        payment.setUpdateBy(operator);
        return finPaymentMapper.updateFinPayment(payment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinPaymentById(Long paymentId)
    {
        FinPayment p = getExistingPayment(paymentId);
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(p.getDeptId(), p.getOwnerUserId(), null);
        checkPaymentDeletable(p);
        finGeneralLedgerService.assertPeriodOpen(p.getPaymentDate() == null ? new Date() : p.getPaymentDate());
        return finPaymentMapper.deleteFinPaymentById(paymentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinPaymentByIds(Long[] paymentIds)
    {
        Set<Long> uniqueIds = new LinkedHashSet<Long>();
        for (Long id : paymentIds)
        {
            if (id != null) uniqueIds.add(id);
        }
        for (Long id : uniqueIds)
        {
            FinPayment p = getExistingPayment(id);
            if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(p.getDeptId(), p.getOwnerUserId(), null);
            checkPaymentDeletable(p);
            finGeneralLedgerService.assertPeriodOpen(p.getPaymentDate() == null ? new Date() : p.getPaymentDate());
        }
        return finPaymentMapper.deleteFinPaymentByIds(uniqueIds.toArray(new Long[0]));
    }

    private FinPayment getExistingPayment(Long paymentId)
    {
        FinPayment finPayment = finPaymentMapper.selectFinPaymentById(paymentId);
        if (finPayment == null) throw new ServiceException("付款单不存在");
        return finPayment;
    }

    private void validatePaymentAmount(BigDecimal paymentAmount)
    {
        if (paymentAmount == null || paymentAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("付款金额必须大于0");
        }
    }

    private void checkPaymentDeletable(FinPayment finPayment)
    {
        if ("1".equals(finPayment.getStatus()) || "confirmed".equals(finPayment.getBizStatus()))
        {
            throw new ServiceException("付款单已确认出账，禁止直接删除，请先执行反确认");
        }
        if (getConfirmedWriteOffAmount(finPayment.getPaymentId()).compareTo(BigDecimal.ZERO) > 0)
        {
            throw new ServiceException("付款单已存在确认核销记录，不允许删除");
        }
    }

    private FinPayable getExistingPayable(Long payableId)
    {
        FinPayable finPayable = finPayableMapper.selectFinPayableById(payableId);
        if (finPayable == null) throw new ServiceException("关联应付单不存在");
        return finPayable;
    }

    private void validatePayableRelation(FinPayable finPayable, FinPayment finPayment)
    {
        if (!"confirmed".equals(finPayable.getBizStatus()))
        {
            throw new ServiceException("仅已审核的应付单允许登记付款");
        }
        if (finPayment.getSupplierId() == null)
        {
            throw new ServiceException("供应商编号不能为空");
        }
        if (!Objects.equals(finPayable.getSupplierId(), finPayment.getSupplierId()))
        {
            throw new ServiceException("付款单供应商与应付单供应商不一致");
        }
    }

    private void adjustPayablePaidAmount(FinPayable finPayable, BigDecimal changeAmount, String operator)
    {
        BigDecimal currentPaidAmount = defaultAmount(finPayable.getPaidAmount());
        BigDecimal totalPayableAmount = defaultAmount(finPayable.getAmount());
        BigDecimal updatedPaidAmount = currentPaidAmount.add(defaultAmount(changeAmount));
        if (updatedPaidAmount.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("应付单已付金额不能小于0");
        }
        if (updatedPaidAmount.compareTo(totalPayableAmount) > 0)
        {
            throw new ServiceException("付款金额不能大于应付单未付金额");
        }
        finPayable.setPaidAmount(updatedPaidAmount);
        finPayable.setStatus(calculatePaymentStatus(updatedPaidAmount, totalPayableAmount));
        finPayable.setUpdateBy(operator);
        finPayableMapper.updateFinPayable(finPayable);
    }

    private BigDecimal getConfirmedWriteOffAmount(Long paymentId)
    {
        FinWriteOff q = new FinWriteOff();
        q.setPaymentId(paymentId);
        q.setStatus("1");
        List<FinWriteOff> list = finWriteOffMapper.selectFinWriteOffList(q);
        BigDecimal total = BigDecimal.ZERO;
        if (list != null)
        {
            for (FinWriteOff w : list) total = total.add(defaultAmount(w.getAmount()));
        }
        return total;
    }

    private String generatePaymentNo()
    {
        String prefix = "pay" + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String maxNo = finPaymentMapper.selectMaxPaymentNoByPrefix(prefix);
        int nextSeq = 1;
        if (StringUtils.isNotEmpty(maxNo) && maxNo.length() > prefix.length())
        {
            String tail = maxNo.substring(prefix.length());
            if (StringUtils.isNumeric(tail)) nextSeq = Integer.parseInt(tail) + 1;
        }
        return prefix + String.format("%03d", nextSeq);
    }

    private BigDecimal defaultAmount(BigDecimal amount) { return amount == null ? BigDecimal.ZERO : amount; }

    private String calculatePaymentStatus(BigDecimal paid, BigDecimal total)
    {
        if (defaultAmount(paid).compareTo(BigDecimal.ZERO) <= 0) return "0";
        if (defaultAmount(paid).compareTo(defaultAmount(total)) >= 0) return "2";
        return "1";
    }
}
