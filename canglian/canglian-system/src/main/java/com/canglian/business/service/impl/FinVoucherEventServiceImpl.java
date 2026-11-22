package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.FinExpense;
import com.canglian.business.domain.FinInvoice;
import com.canglian.business.domain.FinPayable;
import com.canglian.business.domain.FinPayment;
import com.canglian.business.domain.FinReceivable;
import com.canglian.business.domain.FinReceipt;
import com.canglian.business.domain.FinVoucherEntry;
import com.canglian.business.domain.FinVoucherEvent;
import com.canglian.business.domain.FinVoucherTemplate;
import com.canglian.business.domain.MdCustomer;
import com.canglian.business.domain.MdSupplier;
import com.canglian.business.domain.MdWarehouse;
import com.canglian.business.domain.WmsInbound;
import com.canglian.business.domain.WmsOutbound;
import com.canglian.business.domain.WmsPurchaseReturn;
import com.canglian.business.domain.WmsSaleReturn;
import com.canglian.business.mapper.FinExpenseMapper;
import com.canglian.business.mapper.FinInvoiceMapper;
import com.canglian.business.mapper.FinPayableMapper;
import com.canglian.business.mapper.FinPaymentMapper;
import com.canglian.business.mapper.FinReceivableMapper;
import com.canglian.business.mapper.FinReceiptMapper;
import com.canglian.business.mapper.FinVoucherEntryMapper;
import com.canglian.business.mapper.FinVoucherEventMapper;
import com.canglian.business.mapper.MdCustomerMapper;
import com.canglian.business.mapper.MdSupplierMapper;
import com.canglian.business.mapper.MdWarehouseMapper;
import com.canglian.business.mapper.WmsInboundMapper;
import com.canglian.business.mapper.WmsOutboundMapper;
import com.canglian.business.mapper.WmsPurchaseReturnMapper;
import com.canglian.business.mapper.WmsSaleReturnMapper;
import com.canglian.business.service.IFinCostCalculateService;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 凭证事件服务实现
 *
 * @author canglian
 */
@Service
public class FinVoucherEventServiceImpl implements IFinVoucherEventService
{
    private static final DateTimeFormatter VOUCHER_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private FinVoucherEventMapper finVoucherEventMapper;

    @Autowired
    private FinVoucherEntryMapper finVoucherEntryMapper;

    @Autowired
    private IFinGeneralLedgerService finGeneralLedgerService;

    @Autowired(required = false)
    private MdCustomerMapper mdCustomerMapper;

    @Autowired(required = false)
    private MdSupplierMapper mdSupplierMapper;

    @Autowired(required = false)
    private MdWarehouseMapper mdWarehouseMapper;

    @Autowired(required = false)
    private WmsInboundMapper wmsInboundMapper;

    @Autowired(required = false)
    private WmsOutboundMapper wmsOutboundMapper;

    @Autowired(required = false)
    private WmsPurchaseReturnMapper wmsPurchaseReturnMapper;

    @Autowired(required = false)
    private WmsSaleReturnMapper wmsSaleReturnMapper;

    @Autowired(required = false)
    private FinReceivableMapper finReceivableMapper;

    @Autowired(required = false)
    private FinPayableMapper finPayableMapper;

    @Autowired(required = false)
    private FinReceiptMapper finReceiptMapper;

    @Autowired(required = false)
    private FinPaymentMapper finPaymentMapper;

    @Autowired(required = false)
    private FinExpenseMapper finExpenseMapper;

    @Autowired(required = false)
    private FinInvoiceMapper finInvoiceMapper;

    @Autowired(required = false)
    private IFinCostCalculateService finCostCalculateService;

    @Override
    public FinVoucherEvent selectFinVoucherEventById(Long voucherEventId)
    {
        return finVoucherEventMapper.selectFinVoucherEventById(voucherEventId);
    }

    @Override
    public List<FinVoucherEvent> selectFinVoucherEventList(FinVoucherEvent finVoucherEvent)
    {
        return finVoucherEventMapper.selectFinVoucherEventList(finVoucherEvent);
    }

    @Override
    public int insertFinVoucherEvent(FinVoucherEvent finVoucherEvent)
    {
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(finVoucherEvent.getEventDate() == null ? new Date() : finVoucherEvent.getEventDate());
        }
        return finVoucherEventMapper.insertFinVoucherEvent(finVoucherEvent);
    }

    @Override
    public int updateFinVoucherEvent(FinVoucherEvent finVoucherEvent)
    {
        FinVoucherEvent existing = getExistingVoucherEvent(finVoucherEvent.getVoucherEventId());
        if (!"0".equals(existing.getStatus()))
        {
            throw new ServiceException("仅待生成状态的凭证事件允许修改");
        }
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(existing.getEventDate() == null ? new Date() : existing.getEventDate());
        }
        return finVoucherEventMapper.updateFinVoucherEvent(finVoucherEvent);
    }

    @Override
    public int deleteFinVoucherEventById(Long voucherEventId)
    {
        FinVoucherEvent existing = getExistingVoucherEvent(voucherEventId);
        if (!"0".equals(existing.getStatus()))
        {
            throw new ServiceException("仅待生成状态的凭证事件允许删除");
        }
        if (existing.getBillId() != null && StringUtils.isNotEmpty(existing.getBillType()) && !"manual".equals(existing.getBillType()))
        {
            throw new ServiceException("业务单据自动生成的凭证事件禁止直接删除，请先反审核对应业务单据");
        }
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(existing.getEventDate() == null ? new Date() : existing.getEventDate());
        }
        finVoucherEntryMapper.deleteEntriesByEventId(voucherEventId);
        return finVoucherEventMapper.deleteFinVoucherEventById(voucherEventId);
    }

    @Override
    public int deleteFinVoucherEventByIds(Long[] voucherEventIds)
    {
        for (Long voucherEventId : voucherEventIds)
        {
            FinVoucherEvent existing = getExistingVoucherEvent(voucherEventId);
            if (!"0".equals(existing.getStatus()))
            {
                throw new ServiceException("仅待生成状态的凭证事件允许删除");
            }
            if (existing.getBillId() != null && StringUtils.isNotEmpty(existing.getBillType()) && !"manual".equals(existing.getBillType()))
            {
                throw new ServiceException("业务单据自动生成的凭证事件(" + existing.getBillNo() + ")禁止直接删除，请先反审核对应业务单据");
            }
            if (finGeneralLedgerService != null)
            {
                finGeneralLedgerService.assertPeriodOpen(existing.getEventDate() == null ? new Date() : existing.getEventDate());
            }
        }
        for (Long voucherEventId : voucherEventIds)
        {
            finVoucherEntryMapper.deleteEntriesByEventId(voucherEventId);
        }
        return finVoucherEventMapper.deleteFinVoucherEventByIds(voucherEventIds);
    }

    @Override
    public void recordVoucherEvent(String billType, Long billId, String billNo, String eventType, Date eventDate, BigDecimal eventAmount, String operator, String remark)
    {
        FinVoucherEvent finVoucherEvent = new FinVoucherEvent();
        finVoucherEvent.setBillType(billType);
        finVoucherEvent.setBillId(billId);
        finVoucherEvent.setBillNo(billNo);
        finVoucherEvent.setEventType(eventType);
        finVoucherEvent.setEventDate(eventDate == null ? new Date() : eventDate);
        finVoucherEvent.setEventAmount(eventAmount == null ? BigDecimal.ZERO : eventAmount);
        finVoucherEvent.setStatus("0");
        finVoucherEvent.setVoucherStatus("pending");
        finVoucherEvent.setCreateBy(operator);
        finVoucherEvent.setRemark(remark);
        finVoucherEventMapper.insertFinVoucherEvent(finVoucherEvent);
    }

    @Override
    public void cancelPendingVoucherEvents(String billType, Long billId)
    {
        if (StringUtils.isEmpty(billType) || billId == null)
        {
            return;
        }
        FinVoucherEvent query = new FinVoucherEvent();
        query.setBillType(billType);
        query.setBillId(billId);
        List<FinVoucherEvent> events = finVoucherEventMapper.selectFinVoucherEventList(query);
        if (events == null || events.isEmpty())
        {
            return;
        }
        for (FinVoucherEvent ev : events)
        {
            if (!billId.equals(ev.getBillId()))
            {
                continue;
            }
            if ("1".equals(ev.getStatus()) || "2".equals(ev.getStatus()))
            {
                throw new ServiceException("关联单据已生成会计凭证(" + ev.getVoucherNo() + ")，禁止反审核，请使用红冲功能");
            }
        }
        for (FinVoucherEvent ev : events)
        {
            if (!billId.equals(ev.getBillId()))
            {
                continue;
            }
            if ("0".equals(ev.getStatus()))
            {
                finVoucherEntryMapper.deleteEntriesByEventId(ev.getVoucherEventId());
                finVoucherEventMapper.deleteFinVoucherEventById(ev.getVoucherEventId());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int generateVoucher(Long voucherEventId, String operator)
    {
        FinVoucherEvent finVoucherEvent = getExistingVoucherEvent(voucherEventId);
        if (!"0".equals(finVoucherEvent.getStatus()))
        {
            throw new ServiceException("当前凭证事件状态不允许生成凭证");
        }
        finGeneralLedgerService.assertPeriodOpen(finVoucherEvent.getEventDate() == null ? new Date() : finVoucherEvent.getEventDate());
        if (StringUtils.isEmpty(finVoucherEvent.getVoucherNo()))
        {
            finVoucherEvent.setVoucherNo(generateVoucherNo());
        }
        List<FinVoucherEntry> entries = buildDoubleEntries(finVoucherEvent);
        assertEntriesBalanced(entries);
        finVoucherEntryMapper.deleteEntriesByEventId(voucherEventId);
        for (FinVoucherEntry entry : entries)
        {
            finVoucherEntryMapper.insertVoucherEntry(entry);
        }
        finVoucherEvent.setStatus("1");
        finVoucherEvent.setVoucherStatus("generated");
        finVoucherEvent.setVoucherDate(finVoucherEvent.getEventDate() != null ? finVoucherEvent.getEventDate() : new Date());
        finVoucherEvent.setGeneratedBy(operator);
        finVoucherEvent.setGeneratedTime(new Date());
        finVoucherEvent.setUpdateBy(operator);
        finVoucherEvent.setRemark(appendRemark(finVoucherEvent.getRemark(), "凭证已生成(复式分录" + entries.size() + "行)"));
        return finVoucherEventMapper.updateFinVoucherEvent(finVoucherEvent);
    }

    @Override
    public int writebackVoucher(Long voucherEventId, String operator)
    {
        FinVoucherEvent finVoucherEvent = getExistingVoucherEvent(voucherEventId);
        if (StringUtils.isEmpty(finVoucherEvent.getVoucherNo()))
        {
            throw new ServiceException("请先生成凭证后再回写");
        }
        if (!"1".equals(finVoucherEvent.getStatus()))
        {
            throw new ServiceException("仅已生成状态的凭证允许回写");
        }
        finGeneralLedgerService.assertPeriodOpen(finVoucherEvent.getEventDate() == null ? new Date() : finVoucherEvent.getEventDate());
        finVoucherEvent.setStatus("2");
        finVoucherEvent.setVoucherStatus("written_back");
        finVoucherEvent.setWritebackStatus("success");
        finVoucherEvent.setWritebackMessage("业务单据已回写凭证号：" + finVoucherEvent.getVoucherNo());
        finVoucherEvent.setUpdateBy(operator);
        finVoucherEvent.setRemark(appendRemark(finVoucherEvent.getRemark(), "凭证已回写业务单据"));
        return finVoucherEventMapper.updateFinVoucherEvent(finVoucherEvent);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int reverseVoucher(Long voucherEventId, String operator)
    {
        FinVoucherEvent finVoucherEvent = getExistingVoucherEvent(voucherEventId);
        if (StringUtils.isEmpty(finVoucherEvent.getVoucherNo()))
        {
            throw new ServiceException("请先生成凭证后再冲销");
        }
        if ("3".equals(finVoucherEvent.getStatus()) || "reversed".equals(finVoucherEvent.getVoucherStatus()))
        {
            throw new ServiceException("凭证已冲销，请勿重复操作");
        }
        finGeneralLedgerService.assertPeriodOpen(new Date());
        String reverseNo = generateReverseVoucherNo();

        List<FinVoucherEntry> originalEntries = finVoucherEntryMapper.selectEntriesByEventAndVoucherNo(voucherEventId, finVoucherEvent.getVoucherNo());
        if (originalEntries == null || originalEntries.isEmpty())
        {
            originalEntries = buildDoubleEntries(finVoucherEvent);
        }
        List<FinVoucherEntry> reverseEntries = new ArrayList<>();
        int seq = 1;
        for (FinVoucherEntry orig : originalEntries)
        {
            FinVoucherEntry rev = new FinVoucherEntry();
            rev.setVoucherEventId(voucherEventId);
            rev.setVoucherNo(reverseNo);
            rev.setEntrySeq(seq++);
            rev.setDirection(orig.getDirection());
            rev.setSubjectCode(orig.getSubjectCode());
            rev.setSubjectName(orig.getSubjectName());
            rev.setAuxiliaryType(orig.getAuxiliaryType());
            rev.setAuxiliaryName(orig.getAuxiliaryName());
            rev.setDebitAmount(orig.getDebitAmount() == null ? BigDecimal.ZERO : orig.getDebitAmount().negate());
            rev.setCreditAmount(orig.getCreditAmount() == null ? BigDecimal.ZERO : orig.getCreditAmount().negate());
            rev.setSummary("红字冲销：" + StringUtils.defaultString(orig.getSummary()));
            reverseEntries.add(rev);
        }
        assertEntriesBalanced(reverseEntries);
        for (FinVoucherEntry rev : reverseEntries)
        {
            finVoucherEntryMapper.insertVoucherEntry(rev);
        }

        finVoucherEvent.setStatus("3");
        finVoucherEvent.setVoucherStatus("reversed");
        finVoucherEvent.setReverseVoucherNo(reverseNo);
        finVoucherEvent.setReversedBy(operator);
        finVoucherEvent.setReversedTime(new Date());
        finVoucherEvent.setUpdateBy(operator);
        finVoucherEvent.setRemark(appendRemark(finVoucherEvent.getRemark(), "凭证已红字冲销(" + reverseNo + ")"));
        return finVoucherEventMapper.updateFinVoucherEvent(finVoucherEvent);
    }

    @Override
    public List<FinVoucherEntry> selectVoucherEntries(Long voucherEventId)
    {
        return finVoucherEntryMapper.selectEntriesByEventId(voucherEventId);
    }

    @Override
    public List<FinVoucherEntry> selectVoucherEntriesForExport(FinVoucherEvent query, String format)
    {
        String billType = query == null ? null : query.getBillType();
        String eventType = query == null ? null : query.getEventType();
        String status = query == null ? null : query.getStatus();
        String voucherNo = query == null ? null : query.getVoucherNo();
        List<FinVoucherEntry> list = finVoucherEntryMapper.selectEntriesForExport(billType, eventType, status, voucherNo);
        String fmt = StringUtils.isEmpty(format) ? "standard" : format.trim().toLowerCase();
        String wordLabel;
        if ("kingdee".equals(fmt))
        {
            wordLabel = "金蝶KIS-记";
        }
        else if ("yonyou".equals(fmt))
        {
            wordLabel = "用友T+-记";
        }
        else
        {
            wordLabel = "标准复式-记";
        }
        for (FinVoucherEntry entry : list)
        {
            entry.setVoucherWord(wordLabel);
        }
        return list;
    }

    private List<FinVoucherEntry> buildDoubleEntries(FinVoucherEvent event)
    {
        List<FinVoucherTemplate> templates = finVoucherEntryMapper.selectEnabledTemplatesByEventType(event.getEventType());
        if (templates == null || templates.size() < 2)
        {
            templates = buildFallbackTemplates(event);
        }
        BigDecimal rawAmount = event.getEventAmount() == null ? BigDecimal.ZERO : event.getEventAmount().setScale(2, RoundingMode.HALF_UP);
        boolean negative = rawAmount.compareTo(BigDecimal.ZERO) < 0;
        BigDecimal absAmount = rawAmount.abs();
        String billNo = StringUtils.defaultString(event.getBillNo());

        List<FinVoucherEntry> entries = new ArrayList<>();
        int seq = 1;
        for (FinVoucherTemplate tpl : templates)
        {
            FinVoucherEntry entry = new FinVoucherEntry();
            entry.setVoucherEventId(event.getVoucherEventId());
            entry.setVoucherNo(event.getVoucherNo());
            entry.setEntrySeq(seq++);
            String effDir = tpl.getDirection();
            if (negative)
            {
                effDir = "debit".equals(effDir) ? "credit" : "debit";
            }
            entry.setDirection(effDir);
            entry.setSubjectCode(tpl.getSubjectCode());
            entry.setSubjectName(tpl.getSubjectName());
            entry.setAuxiliaryType(StringUtils.isEmpty(tpl.getAuxiliaryType()) ? "none" : tpl.getAuxiliaryType());
            entry.setAuxiliaryName(resolveAuxiliaryLabel(entry.getAuxiliaryType(), event));
            if ("debit".equals(effDir))
            {
                entry.setDebitAmount(absAmount);
                entry.setCreditAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            }
            else
            {
                entry.setDebitAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                entry.setCreditAmount(absAmount);
            }
            String summary = StringUtils.isEmpty(tpl.getSummaryTemplate())
                ? (StringUtils.defaultString(event.getEventType()) + " " + billNo)
                : tpl.getSummaryTemplate().replace("{billNo}", billNo);
            if (negative)
            {
                summary = "[红字/冲减] " + summary;
            }
            entry.setSummary(summary);
            entries.add(entry);
        }
        return entries;
    }

    private List<FinVoucherTemplate> buildFallbackTemplates(FinVoucherEvent event)
    {
        List<FinVoucherTemplate> list = new ArrayList<>();
        FinVoucherTemplate debit = new FinVoucherTemplate();
        debit.setEntrySeq(1);
        debit.setDirection("debit");
        debit.setSubjectCode("1221");
        debit.setSubjectName("其他应收款");
        debit.setAuxiliaryType("none");
        debit.setSummaryTemplate(StringUtils.defaultString(event.getEventType()) + " {billNo}");
        list.add(debit);

        FinVoucherTemplate credit = new FinVoucherTemplate();
        credit.setEntrySeq(2);
        credit.setDirection("credit");
        credit.setSubjectCode("2241");
        credit.setSubjectName("其他应付款");
        credit.setAuxiliaryType("none");
        credit.setSummaryTemplate(StringUtils.defaultString(event.getEventType()) + " {billNo}");
        list.add(credit);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> regenerateMissingVoucherEvents(String operator)
    {
        int createdCount = 0;
        String op = StringUtils.isEmpty(operator) ? "admin" : operator;
        if (wmsInboundMapper != null)
        {
            WmsInbound q = new WmsInbound();
            q.setStatus("1");
            List<WmsInbound> list = wmsInboundMapper.selectWmsInboundList(q);
            if (list != null)
            {
                for (WmsInbound doc : list)
                {
                    if ("1".equals(doc.getIsReversal()) || doc.getTotalAmount() == null || doc.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) continue;
                    if (!hasVoucherEvent("inbound", doc.getInboundId(), "inbound_complete"))
                    {
                        recordVoucherEvent("inbound", doc.getInboundId(), doc.getInboundNo(), "inbound_complete",
                            doc.getBusinessDate(), doc.getTotalAmount(), op, "系统补生成采购入库凭证事件");
                        createdCount++;
                    }
                }
            }
        }
        if (wmsOutboundMapper != null)
        {
            WmsOutbound q = new WmsOutbound();
            q.setStatus("1");
            q.setDeliveryStatus("2");
            List<WmsOutbound> list = wmsOutboundMapper.selectWmsOutboundList(q);
            if (list != null)
            {
                for (WmsOutbound doc : list)
                {
                    if ("1".equals(doc.getIsReversal()) || "1".equals(doc.getReversedFlag())) continue;
                    if (doc.getTotalAmount() != null && doc.getTotalAmount().compareTo(BigDecimal.ZERO) > 0
                        && !hasVoucherEvent("outbound", doc.getOutboundId(), "outbound_complete"))
                    {
                        recordVoucherEvent("outbound", doc.getOutboundId(), doc.getOutboundNo(), "outbound_complete",
                            doc.getBusinessDate(), doc.getTotalAmount(), op, "系统补生成签收出库收入凭证事件");
                        createdCount++;
                    }
                    if (finCostCalculateService != null && !hasVoucherEvent("outbound_cogs", doc.getOutboundId(), "outbound_cogs"))
                    {
                        BigDecimal cogs = finCostCalculateService.resolveOutboundCogsAmount(doc.getOutboundId());
                        if (cogs.compareTo(BigDecimal.ZERO) > 0)
                        {
                            recordVoucherEvent("outbound_cogs", doc.getOutboundId(), doc.getOutboundNo(), "outbound_cogs",
                                doc.getBusinessDate(), cogs, op, "系统补生成签收出库成本凭证事件");
                            createdCount++;
                        }
                    }
                }
            }
        }
        if (finReceiptMapper != null)
        {
            FinReceipt q = new FinReceipt();
            q.setStatus("1");
            List<FinReceipt> list = finReceiptMapper.selectFinReceiptList(q);
            if (list != null)
            {
                for (FinReceipt doc : list)
                {
                    if (doc.getAmount() == null || doc.getAmount().compareTo(BigDecimal.ZERO) == 0) continue;
                    String evType = doc.getAmount().compareTo(BigDecimal.ZERO) < 0 ? "refund_receipt" : "receipt_confirm";
                    if (!hasVoucherEvent("receipt", doc.getReceiptId(), evType))
                    {
                        recordVoucherEvent("receipt", doc.getReceiptId(), doc.getReceiptNo(), evType,
                            doc.getReceiptDate(), doc.getAmount().abs(), op, "系统补生成收款/退款凭证事件");
                        createdCount++;
                    }
                }
            }
        }
        if (finPaymentMapper != null)
        {
            FinPayment q = new FinPayment();
            q.setStatus("1");
            List<FinPayment> list = finPaymentMapper.selectFinPaymentList(q);
            if (list != null)
            {
                for (FinPayment doc : list)
                {
                    if (doc.getAmount() == null || doc.getAmount().compareTo(BigDecimal.ZERO) <= 0) continue;
                    if (!hasVoucherEvent("payment", doc.getPaymentId(), "payment_confirm"))
                    {
                        recordVoucherEvent("payment", doc.getPaymentId(), doc.getPaymentNo(), "payment_confirm",
                            doc.getPaymentDate(), doc.getAmount(), op, "系统补生成付款凭证事件");
                        createdCount++;
                    }
                }
            }
        }
        if (finExpenseMapper != null)
        {
            FinExpense q = new FinExpense();
            q.setStatus("1");
            List<FinExpense> list = finExpenseMapper.selectFinExpenseList(q);
            if (list != null)
            {
                for (FinExpense doc : list)
                {
                    if (doc.getAmount() == null || doc.getAmount().compareTo(BigDecimal.ZERO) <= 0) continue;
                    if (!hasVoucherEvent("expense", doc.getExpenseId(), "expense_confirm"))
                    {
                        recordVoucherEvent("expense", doc.getExpenseId(), doc.getExpenseNo(), "expense_confirm",
                            doc.getExpenseDate(), doc.getAmount(), op, "系统补生成费用凭证事件");
                        createdCount++;
                    }
                }
            }
        }
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("createdCount", createdCount);
        return result;
    }

    private boolean hasVoucherEvent(String billType, Long billId, String eventType)
    {
        FinVoucherEvent q = new FinVoucherEvent();
        q.setBillType(billType);
        q.setBillId(billId);
        q.setEventType(eventType);
        List<FinVoucherEvent> existing = finVoucherEventMapper.selectFinVoucherEventList(q);
        return existing != null && !existing.isEmpty();
    }

    private String resolveAuxiliaryLabel(String auxiliaryType, FinVoucherEvent event)
    {
        if (StringUtils.isEmpty(auxiliaryType) || "none".equals(auxiliaryType))
        {
            return "";
        }
        String bt = StringUtils.defaultString(event.getBillType());
        Long bid = event.getBillId();
        if ("customer".equals(auxiliaryType))
        {
            Long cid = resolveCustomerId(bt, bid);
            if (cid != null && mdCustomerMapper != null)
            {
                MdCustomer c = mdCustomerMapper.selectMdCustomerById(cid);
                if (c != null && StringUtils.isNotEmpty(c.getCustomerName()))
                {
                    return (StringUtils.isNotEmpty(c.getCustomerCode()) ? c.getCustomerCode() + " " : "") + c.getCustomerName();
                }
            }
            return "客户往来(" + StringUtils.defaultString(event.getBillNo()) + ")";
        }
        if ("supplier".equals(auxiliaryType))
        {
            Long sid = resolveSupplierId(bt, bid);
            if (sid != null && mdSupplierMapper != null)
            {
                MdSupplier s = mdSupplierMapper.selectMdSupplierById(sid);
                if (s != null && StringUtils.isNotEmpty(s.getSupplierName()))
                {
                    return (StringUtils.isNotEmpty(s.getSupplierCode()) ? s.getSupplierCode() + " " : "") + s.getSupplierName();
                }
            }
            return "供应商往来(" + StringUtils.defaultString(event.getBillNo()) + ")";
        }
        if ("warehouse".equals(auxiliaryType))
        {
            Long wid = resolveWarehouseId(bt, bid);
            if (wid != null && mdWarehouseMapper != null)
            {
                MdWarehouse w = mdWarehouseMapper.selectMdWarehouseById(wid);
                if (w != null && StringUtils.isNotEmpty(w.getWarehouseName()))
                {
                    return (StringUtils.isNotEmpty(w.getWarehouseCode()) ? w.getWarehouseCode() + " " : "") + w.getWarehouseName();
                }
            }
            return "仓库存货(" + StringUtils.defaultString(event.getBillNo()) + ")";
        }
        return "";
    }

    private Long resolveCustomerId(String bt, Long bid)
    {
        if (bid == null) return null;
        if (("outbound".equals(bt) || "outbound_cogs".equals(bt)) && wmsOutboundMapper != null)
        {
            WmsOutbound d = wmsOutboundMapper.selectWmsOutboundById(bid);
            return d == null ? null : d.getCustomerId();
        }
        if (("sale_return".equals(bt) || "sale_return_cogs".equals(bt)) && wmsSaleReturnMapper != null)
        {
            WmsSaleReturn d = wmsSaleReturnMapper.selectWmsSaleReturnById(bid);
            return d == null ? null : d.getCustomerId();
        }
        if ("receipt".equals(bt) && finReceiptMapper != null)
        {
            FinReceipt d = finReceiptMapper.selectFinReceiptById(bid);
            return d == null ? null : d.getCustomerId();
        }
        if ("receivable".equals(bt) && finReceivableMapper != null)
        {
            FinReceivable d = finReceivableMapper.selectFinReceivableById(bid);
            return d == null ? null : d.getCustomerId();
        }
        if ("invoice".equals(bt) && finInvoiceMapper != null)
        {
            FinInvoice d = finInvoiceMapper.selectFinInvoiceById(bid);
            return d == null ? null : d.getCustomerId();
        }
        return null;
    }

    private Long resolveSupplierId(String bt, Long bid)
    {
        if (bid == null) return null;
        if ("inbound".equals(bt) && wmsInboundMapper != null)
        {
            WmsInbound d = wmsInboundMapper.selectWmsInboundById(bid);
            return d == null ? null : d.getSupplierId();
        }
        if ("purchase_return".equals(bt) && wmsPurchaseReturnMapper != null)
        {
            WmsPurchaseReturn d = wmsPurchaseReturnMapper.selectWmsPurchaseReturnById(bid);
            return d == null ? null : d.getSupplierId();
        }
        if ("payment".equals(bt) && finPaymentMapper != null)
        {
            FinPayment d = finPaymentMapper.selectFinPaymentById(bid);
            return d == null ? null : d.getSupplierId();
        }
        if ("payable".equals(bt) && finPayableMapper != null)
        {
            FinPayable d = finPayableMapper.selectFinPayableById(bid);
            return d == null ? null : d.getSupplierId();
        }
        if ("invoice".equals(bt) && finInvoiceMapper != null)
        {
            FinInvoice d = finInvoiceMapper.selectFinInvoiceById(bid);
            return d == null ? null : d.getSupplierId();
        }
        return null;
    }

    private Long resolveWarehouseId(String bt, Long bid)
    {
        if (bid == null) return null;
        if ("inbound".equals(bt) && wmsInboundMapper != null)
        {
            WmsInbound d = wmsInboundMapper.selectWmsInboundById(bid);
            return d == null ? null : d.getWarehouseId();
        }
        if (("outbound".equals(bt) || "outbound_cogs".equals(bt)) && wmsOutboundMapper != null)
        {
            WmsOutbound d = wmsOutboundMapper.selectWmsOutboundById(bid);
            return d == null ? null : d.getWarehouseId();
        }
        if ("purchase_return".equals(bt) && wmsPurchaseReturnMapper != null)
        {
            WmsPurchaseReturn d = wmsPurchaseReturnMapper.selectWmsPurchaseReturnById(bid);
            return d == null ? null : d.getWarehouseId();
        }
        if (("sale_return".equals(bt) || "sale_return_cogs".equals(bt)) && wmsSaleReturnMapper != null)
        {
            WmsSaleReturn d = wmsSaleReturnMapper.selectWmsSaleReturnById(bid);
            return d == null ? null : d.getWarehouseId();
        }
        return null;
    }

    private void assertEntriesBalanced(List<FinVoucherEntry> entries)
    {
        if (entries == null || entries.size() < 2)
        {
            throw new ServiceException("复式凭证至少需要借贷2条分录");
        }
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;
        for (FinVoucherEntry e : entries)
        {
            totalDebit = totalDebit.add(e.getDebitAmount() == null ? BigDecimal.ZERO : e.getDebitAmount());
            totalCredit = totalCredit.add(e.getCreditAmount() == null ? BigDecimal.ZERO : e.getCreditAmount());
        }
        if (totalDebit.compareTo(totalCredit) != 0)
        {
            throw new ServiceException("凭证分录借贷不平衡（借方=" + totalDebit + "，贷方=" + totalCredit + "），拒绝生成凭证");
        }
    }

    private FinVoucherEvent getExistingVoucherEvent(Long voucherEventId)
    {
        FinVoucherEvent finVoucherEvent = finVoucherEventMapper.selectFinVoucherEventById(voucherEventId);
        if (finVoucherEvent == null)
        {
            throw new ServiceException("凭证事件不存在");
        }
        return finVoucherEvent;
    }

    private String generateVoucherNo()
    {
        String prefix = "vch" + LocalDate.now().format(VOUCHER_DATE_FORMATTER);
        return formatNextSequence(prefix, finVoucherEventMapper.selectMaxVoucherNoByPrefix(prefix));
    }

    private String generateReverseVoucherNo()
    {
        String prefix = "rvch" + LocalDate.now().format(VOUCHER_DATE_FORMATTER);
        return formatNextSequence(prefix, finVoucherEventMapper.selectMaxReverseVoucherNoByPrefix(prefix));
    }

    private String formatNextSequence(String prefix, String currentMaxNo)
    {
        int nextSequence = 1;
        if (StringUtils.isNotEmpty(currentMaxNo) && currentMaxNo.length() > prefix.length())
        {
            String sequenceText = currentMaxNo.substring(prefix.length());
            if (StringUtils.isNumeric(sequenceText))
            {
                nextSequence = Integer.parseInt(sequenceText) + 1;
            }
        }
        return prefix + String.format("%03d", nextSequence);
    }

    private String appendRemark(String originalRemark, String newRemark)
    {
        if (StringUtils.isEmpty(originalRemark))
        {
            return newRemark;
        }
        return originalRemark + "；" + newRemark;
    }
}
