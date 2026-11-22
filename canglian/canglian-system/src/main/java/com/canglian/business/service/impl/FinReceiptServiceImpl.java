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
import com.canglian.business.domain.FinFundAccount;
import com.canglian.business.domain.FinReceipt;
import com.canglian.business.domain.FinReceivable;
import com.canglian.business.domain.FinWriteOff;
import com.canglian.business.mapper.FinFundAccountMapper;
import com.canglian.business.mapper.FinReceiptMapper;
import com.canglian.business.mapper.FinReceivableMapper;
import com.canglian.business.mapper.FinWriteOffMapper;
import com.canglian.business.service.IFinFundLogService;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IFinReceiptService;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.SecurityUtils;
import com.canglian.common.utils.StringUtils;

/**
 * 收款单 服务层实现（草稿 -> 确认记账 -> 反确认 状态机 + 资金流水）
 *
 * @author canglian
 */
@Service
public class FinReceiptServiceImpl implements IFinReceiptService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private FinReceiptMapper finReceiptMapper;

    @Autowired
    private FinReceivableMapper finReceivableMapper;

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

    @Autowired(required = false)
    private FinFundAccountMapper finFundAccountMapper;

    @Override
    public FinReceipt selectFinReceiptById(Long receiptId)
    {
        FinReceipt receipt = finReceiptMapper.selectFinReceiptById(receiptId);
        if (receipt != null && bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(receipt.getDeptId(), receipt.getOwnerUserId(), null);
        }
        return receipt;
    }

    @Override
    public List<FinReceipt> selectFinReceiptList(FinReceipt finReceipt)
    {
        if (bizScopeHelper != null)
        {
            bizScopeHelper.applyScopeFilter(finReceipt, "", true, false);
        }
        return finReceiptMapper.selectFinReceiptList(finReceipt);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFinReceipt(FinReceipt finReceipt)
    {
        if (bizScopeHelper != null)
        {
            if (finReceipt.getDeptId() == null) finReceipt.setDeptId(bizScopeHelper.currentDeptId());
            if (finReceipt.getOwnerUserId() == null) finReceipt.setOwnerUserId(bizScopeHelper.currentUserId());
        }
        if (StringUtils.isEmpty(finReceipt.getReceiptNo()))
        {
            finReceipt.setReceiptNo(generateReceiptNo());
        }
        if (finReceipt.getReceiptDate() == null)
        {
            finReceipt.setReceiptDate(new Date());
        }
        finGeneralLedgerService.assertPeriodOpen(finReceipt.getReceiptDate());
        validateReceiptAmount(finReceipt.getAmount());
        if (finReceipt.getReceivableId() != null)
        {
            FinReceivable ar = getExistingReceivable(finReceipt.getReceivableId());
            validateReceivableRelation(ar, finReceipt);
        }
        finReceipt.setStatus("0");
        finReceipt.setBizStatus("draft");
        finReceipt.setAuditBy(null);
        finReceipt.setAuditTime(null);
        return finReceiptMapper.insertFinReceipt(finReceipt);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertRefundReceipt(FinReceipt finReceipt)
    {
        if (bizScopeHelper != null)
        {
            if (finReceipt.getDeptId() == null) finReceipt.setDeptId(bizScopeHelper.currentDeptId());
            if (finReceipt.getOwnerUserId() == null) finReceipt.setOwnerUserId(bizScopeHelper.currentUserId());
        }
        if (StringUtils.isEmpty(finReceipt.getReceiptNo()))
        {
            finReceipt.setReceiptNo(generateReceiptNo());
        }
        if (finReceipt.getReceiptDate() == null)
        {
            finReceipt.setReceiptDate(new Date());
        }
        finGeneralLedgerService.assertPeriodOpen(finReceipt.getReceiptDate());
        if (finReceipt.getAmount() == null || finReceipt.getAmount().compareTo(BigDecimal.ZERO) >= 0)
        {
            throw new ServiceException("红字退款单金额必须小于0");
        }
        // 自动兜底默认启用资金账户，确保退款真实扣减账户余额并留下资金流水
        if (finReceipt.getFundAccountId() == null && finFundAccountMapper != null)
        {
            FinFundAccount q = new FinFundAccount();
            q.setStatus("0");
            List<FinFundAccount> activeAccs = finFundAccountMapper.selectFinFundAccountList(q);
            if (activeAccs != null && !activeAccs.isEmpty())
            {
                finReceipt.setFundAccountId(activeAccs.get(0).getAccountId());
            }
        }
        String operator = resolveOperator(finReceipt.getCreateBy());
        finReceipt.setStatus("1");
        finReceipt.setBizStatus("confirmed");
        finReceipt.setAuditBy(operator);
        finReceipt.setAuditTime(new Date());
        int rows = finReceiptMapper.insertFinReceipt(finReceipt);
        if (rows > 0)
        {
            if (finReceipt.getReceivableId() != null)
            {
                FinReceivable ar = getExistingReceivable(finReceipt.getReceivableId());
                validateReceivableRelation(ar, finReceipt);
                adjustReceivableReceivedAmount(ar, finReceipt.getAmount(), operator);
            }
            if (finReceipt.getFundAccountId() != null)
            {
                finFundLogService.recordBalanceChange(finReceipt.getFundAccountId(), "refund",
                    finReceipt.getReceiptId(), finReceipt.getReceiptNo(), finReceipt.getAmount(),
                    finReceipt.getReceiptDate(), "客户#" + finReceipt.getCustomerId() + " 退货退款",
                    finReceipt.getRemark(), operator);
            }
            if (finVoucherEventService != null)
            {
                finVoucherEventService.recordVoucherEvent("receipt", finReceipt.getReceiptId(), finReceipt.getReceiptNo(),
                    "refund_receipt", finReceipt.getReceiptDate(), finReceipt.getAmount().abs(), operator, "客户退货退款待生成凭证");
            }
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFinReceipt(FinReceipt finReceipt)
    {
        FinReceipt existing = getExistingReceipt(finReceipt.getReceiptId());
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(existing.getDeptId(), existing.getOwnerUserId(), null);
        if ("1".equals(existing.getStatus()) || "confirmed".equals(existing.getBizStatus()))
        {
            throw new ServiceException("收款单已确认入账，禁止直接修改，请先反确认");
        }
        finGeneralLedgerService.assertPeriodOpen(existing.getReceiptDate() == null ? new Date() : existing.getReceiptDate());
        if (finReceipt.getReceiptDate() != null) finGeneralLedgerService.assertPeriodOpen(finReceipt.getReceiptDate());
        validateReceiptAmount(finReceipt.getAmount());
        if (finReceipt.getReceivableId() != null)
        {
            FinReceivable ar = getExistingReceivable(finReceipt.getReceivableId());
            validateReceivableRelation(ar, finReceipt);
        }
        finReceipt.setReceiptNo(existing.getReceiptNo());
        finReceipt.setStatus("0");
        finReceipt.setBizStatus("draft");
        finReceipt.setAuditBy(null);
        finReceipt.setAuditTime(null);
        return finReceiptMapper.updateFinReceipt(finReceipt);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirmFinReceipt(Long receiptId, String operator)
    {
        FinReceipt receipt = getExistingReceipt(receiptId);
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(receipt.getDeptId(), receipt.getOwnerUserId(), null);
        if ("1".equals(receipt.getStatus()) || "confirmed".equals(receipt.getBizStatus()))
        {
            throw new ServiceException("收款单已确认入账，请勿重复确认");
        }
        if (receipt.getFundAccountId() == null)
        {
            throw new ServiceException("确认收款入账必须选择资金账户");
        }
        Date bizDate = receipt.getReceiptDate() == null ? new Date() : receipt.getReceiptDate();
        finGeneralLedgerService.assertPeriodOpen(bizDate);
        validateReceiptAmount(receipt.getAmount());

        if (receipt.getReceivableId() != null)
        {
            FinReceivable ar = getExistingReceivable(receipt.getReceivableId());
            validateReceivableRelation(ar, receipt);
            adjustReceivableReceivedAmount(ar, receipt.getAmount(), operator);
        }
        finFundLogService.recordBalanceChange(receipt.getFundAccountId(), "receipt",
            receipt.getReceiptId(), receipt.getReceiptNo(), receipt.getAmount(),
            bizDate, "客户#" + receipt.getCustomerId() + " 收款确认", receipt.getRemark(), operator);

        receipt.setStatus("1");
        receipt.setBizStatus("confirmed");
        receipt.setAuditBy(operator);
        receipt.setAuditTime(new Date());
        receipt.setUpdateBy(operator);
        int rows = finReceiptMapper.updateFinReceipt(receipt);
        if (rows > 0 && finVoucherEventService != null)
        {
            finVoucherEventService.recordVoucherEvent("receipt", receipt.getReceiptId(), receipt.getReceiptNo(),
                "receipt_confirm", bizDate, receipt.getAmount(), operator, "收款确认入账待生成凭证");
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int unconfirmFinReceipt(Long receiptId, String operator)
    {
        FinReceipt receipt = getExistingReceipt(receiptId);
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(receipt.getDeptId(), receipt.getOwnerUserId(), null);
        if (!"1".equals(receipt.getStatus()) && !"confirmed".equals(receipt.getBizStatus()))
        {
            throw new ServiceException("仅已确认状态的收款单允许反确认");
        }
        if (defaultAmount(receipt.getAmount()).compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("退货自动生成的负数退款单禁止反确认");
        }
        Date bizDate = receipt.getReceiptDate() == null ? new Date() : receipt.getReceiptDate();
        finGeneralLedgerService.assertPeriodOpen(bizDate);
        FinWriteOff woQ = new FinWriteOff();
        woQ.setReceiptId(receiptId);
        List<FinWriteOff> linkedWos = finWriteOffMapper.selectFinWriteOffList(woQ);
        if (linkedWos != null && !linkedWos.isEmpty())
        {
            throw new ServiceException("该收款单存在关联核销单(" + linkedWos.get(0).getWriteOffNo() + ")，请先反确认/删除核销单后再反确认收款单");
        }
        if (finVoucherEventService != null)
        {
            finVoucherEventService.cancelPendingVoucherEvents("receipt", receiptId);
        }

        if (receipt.getReceivableId() != null)
        {
            FinReceivable ar = getExistingReceivable(receipt.getReceivableId());
            adjustReceivableReceivedAmount(ar, defaultAmount(receipt.getAmount()).negate(), operator);
        }
        if (receipt.getFundAccountId() != null)
        {
            finFundLogService.recordBalanceChange(receipt.getFundAccountId(), "unconfirm",
                receipt.getReceiptId(), receipt.getReceiptNo(), defaultAmount(receipt.getAmount()).negate(),
                new Date(), "收款单反确认冲回", "反确认原收款单：" + receipt.getReceiptNo(), operator);
        }

        receipt.setStatus("0");
        receipt.setBizStatus("draft");
        receipt.setAuditBy("");
        receipt.setAuditTime(null);
        receipt.setUpdateBy(operator);
        return finReceiptMapper.updateFinReceipt(receipt);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinReceiptById(Long receiptId)
    {
        FinReceipt finReceipt = getExistingReceipt(receiptId);
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(finReceipt.getDeptId(), finReceipt.getOwnerUserId(), null);
        checkReceiptDeletable(finReceipt);
        finGeneralLedgerService.assertPeriodOpen(finReceipt.getReceiptDate() == null ? new Date() : finReceipt.getReceiptDate());
        return finReceiptMapper.deleteFinReceiptById(receiptId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFinReceiptByIds(Long[] receiptIds)
    {
        Set<Long> uniqueIds = new LinkedHashSet<Long>();
        for (Long id : receiptIds)
        {
            if (id != null) uniqueIds.add(id);
        }
        for (Long id : uniqueIds)
        {
            FinReceipt r = getExistingReceipt(id);
            if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(r.getDeptId(), r.getOwnerUserId(), null);
            checkReceiptDeletable(r);
            finGeneralLedgerService.assertPeriodOpen(r.getReceiptDate() == null ? new Date() : r.getReceiptDate());
        }
        return finReceiptMapper.deleteFinReceiptByIds(uniqueIds.toArray(new Long[0]));
    }

    private FinReceipt getExistingReceipt(Long receiptId)
    {
        FinReceipt finReceipt = finReceiptMapper.selectFinReceiptById(receiptId);
        if (finReceipt == null) throw new ServiceException("收款单不存在");
        return finReceipt;
    }

    private void validateReceiptAmount(BigDecimal receiptAmount)
    {
        if (receiptAmount == null || receiptAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("收款金额必须大于0");
        }
    }

    private void checkReceiptDeletable(FinReceipt finReceipt)
    {
        if ("1".equals(finReceipt.getStatus()) || "confirmed".equals(finReceipt.getBizStatus()))
        {
            throw new ServiceException("收款单已确认入账，禁止直接删除，请先执行反确认");
        }
        if (getConfirmedWriteOffAmount(finReceipt.getReceiptId()).compareTo(BigDecimal.ZERO) > 0)
        {
            throw new ServiceException("收款单已存在确认核销记录，不允许删除");
        }
    }

    private FinReceivable getExistingReceivable(Long receivableId)
    {
        FinReceivable finReceivable = finReceivableMapper.selectFinReceivableById(receivableId);
        if (finReceivable == null) throw new ServiceException("关联应收单不存在");
        return finReceivable;
    }

    private void validateReceivableRelation(FinReceivable finReceivable, FinReceipt finReceipt)
    {
        if (!"confirmed".equals(finReceivable.getBizStatus()))
        {
            throw new ServiceException("仅已审核的应收单允许登记收款");
        }
        if (finReceipt.getCustomerId() == null)
        {
            throw new ServiceException("客户编号不能为空");
        }
        if (!Objects.equals(finReceivable.getCustomerId(), finReceipt.getCustomerId()))
        {
            throw new ServiceException("收款单客户与应收单客户不一致");
        }
    }

    private void adjustReceivableReceivedAmount(FinReceivable finReceivable, BigDecimal changeAmount, String operator)
    {
        BigDecimal currentReceivedAmount = defaultAmount(finReceivable.getReceivedAmount());
        BigDecimal badDebtAmount = defaultAmount(finReceivable.getBadDebtAmount());
        BigDecimal totalReceivableAmount = defaultAmount(finReceivable.getAmount());
        BigDecimal updatedReceivedAmount = currentReceivedAmount.add(defaultAmount(changeAmount));
        if (updatedReceivedAmount.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("应收单已收金额不能小于0");
        }
        if (updatedReceivedAmount.add(badDebtAmount).compareTo(totalReceivableAmount) > 0)
        {
            throw new ServiceException("收款金额不能大于应收单未收余额");
        }
        finReceivable.setReceivedAmount(updatedReceivedAmount);
        finReceivable.setStatus(calculateCollectionStatus(updatedReceivedAmount.add(badDebtAmount), totalReceivableAmount));
        finReceivable.setUpdateBy(operator);
        finReceivableMapper.updateFinReceivable(finReceivable);
    }

    private BigDecimal getConfirmedWriteOffAmount(Long receiptId)
    {
        FinWriteOff q = new FinWriteOff();
        q.setReceiptId(receiptId);
        q.setStatus("1");
        List<FinWriteOff> list = finWriteOffMapper.selectFinWriteOffList(q);
        BigDecimal total = BigDecimal.ZERO;
        if (list != null)
        {
            for (FinWriteOff w : list) total = total.add(defaultAmount(w.getAmount()));
        }
        return total;
    }

    private String generateReceiptNo()
    {
        String prefix = "rec" + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String maxNo = finReceiptMapper.selectMaxReceiptNoByPrefix(prefix);
        int nextSeq = 1;
        if (StringUtils.isNotEmpty(maxNo) && maxNo.length() > prefix.length())
        {
            String tail = maxNo.substring(prefix.length());
            if (StringUtils.isNumeric(tail)) nextSeq = Integer.parseInt(tail) + 1;
        }
        return prefix + String.format("%03d", nextSeq);
    }

    private BigDecimal defaultAmount(BigDecimal amount) { return amount == null ? BigDecimal.ZERO : amount; }

    private String calculateCollectionStatus(BigDecimal received, BigDecimal total)
    {
        if (defaultAmount(received).compareTo(BigDecimal.ZERO) <= 0) return "0";
        if (defaultAmount(received).compareTo(defaultAmount(total)) >= 0) return "2";
        return "1";
    }

    private String resolveOperator(String fallbackOperator)
    {
        try { return SecurityUtils.getUsername(); } catch (Exception e) { return fallbackOperator; }
    }
}
