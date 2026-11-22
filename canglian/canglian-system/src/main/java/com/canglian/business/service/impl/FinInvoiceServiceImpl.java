package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.FinInvoice;
import com.canglian.business.domain.FinPayable;
import com.canglian.business.domain.FinReceivable;
import com.canglian.business.domain.PurOrder;
import com.canglian.business.domain.SalOrder;
import com.canglian.business.domain.WmsInbound;
import com.canglian.business.domain.WmsOutbound;
import com.canglian.business.mapper.FinInvoiceMapper;
import com.canglian.business.mapper.FinPayableMapper;
import com.canglian.business.mapper.FinReceivableMapper;
import com.canglian.business.mapper.PurOrderMapper;
import com.canglian.business.mapper.SalOrderMapper;
import com.canglian.business.mapper.WmsInboundMapper;
import com.canglian.business.mapper.WmsOutboundMapper;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.business.service.IFinInvoiceService;
import com.canglian.business.service.IFinPayableService;
import com.canglian.business.service.IFinReceivableService;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 发票登记服务实现（支持分批开蓝票 + 红字发票冲销）
 *
 * @author canglian
 */
@Service
public class FinInvoiceServiceImpl implements IFinInvoiceService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private FinInvoiceMapper finInvoiceMapper;

    @Autowired
    private FinReceivableMapper finReceivableMapper;

    @Autowired
    private FinPayableMapper finPayableMapper;

    @Autowired(required = false)
    private WmsOutboundMapper wmsOutboundMapper;

    @Autowired(required = false)
    private WmsInboundMapper wmsInboundMapper;

    @Autowired(required = false)
    private SalOrderMapper salOrderMapper;

    @Autowired(required = false)
    private PurOrderMapper purOrderMapper;

    @Autowired
    private IFinGeneralLedgerService finGeneralLedgerService;

    @Autowired
    private IFinVoucherEventService finVoucherEventService;

    @Autowired
    private IFinReceivableService finReceivableService;

    @Autowired
    private IFinPayableService finPayableService;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Override
    public FinInvoice selectFinInvoiceById(Long invoiceId)
    {
        FinInvoice inv = finInvoiceMapper.selectFinInvoiceById(invoiceId);
        if (inv != null && bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(inv.getDeptId(), inv.getOwnerUserId(), null);
        }
        return inv;
    }

    @Override
    public List<FinInvoice> selectFinInvoiceList(FinInvoice finInvoice)
    {
        if (bizScopeHelper != null) bizScopeHelper.applyScopeFilter(finInvoice, "", true, false);
        return finInvoiceMapper.selectFinInvoiceList(finInvoice);
    }

    @Override
    public int insertFinInvoice(FinInvoice finInvoice)
    {
        if (bizScopeHelper != null)
        {
            if (finInvoice.getDeptId() == null) finInvoice.setDeptId(bizScopeHelper.currentDeptId());
            if (finInvoice.getOwnerUserId() == null) finInvoice.setOwnerUserId(bizScopeHelper.currentUserId());
        }
        if (finInvoice.getBusinessDate() == null) finInvoice.setBusinessDate(new Date());
        if (finInvoice.getInvoiceDate() == null) finInvoice.setInvoiceDate(finInvoice.getBusinessDate());
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(finInvoice.getBusinessDate());
        }
        if (StringUtils.isEmpty(finInvoice.getInvoiceKind()))
        {
            finInvoice.setInvoiceKind("blue");
        }
        if (StringUtils.isEmpty(finInvoice.getInvoiceNo()))
        {
            finInvoice.setInvoiceNo(generateInvoiceNo(finInvoice.getInvoiceType(), finInvoice.getInvoiceKind()));
        }
        fillInvoiceAmount(finInvoice);
        validateSourceInvoiceQuota(finInvoice);
        finInvoice.setStatus("0");
        finInvoice.setBizStatus("draft");
        return finInvoiceMapper.insertFinInvoice(finInvoice);
    }

    @Override
    public int updateFinInvoice(FinInvoice finInvoice)
    {
        FinInvoice existing = getExistingInvoice(finInvoice.getInvoiceId());
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(existing.getDeptId(), existing.getOwnerUserId(), null);
        }
        if (!"draft".equals(existing.getBizStatus())) throw new ServiceException("当前发票状态不允许修改");
        if (finGeneralLedgerService != null)
        {
            finGeneralLedgerService.assertPeriodOpen(existing.getBusinessDate() == null ? new Date() : existing.getBusinessDate());
            if (finInvoice.getBusinessDate() != null)
            {
                finGeneralLedgerService.assertPeriodOpen(finInvoice.getBusinessDate());
            }
        }
        finInvoice.setInvoiceNo(existing.getInvoiceNo());
        finInvoice.setInvoiceKind(StringUtils.isEmpty(existing.getInvoiceKind()) ? "blue" : existing.getInvoiceKind());
        finInvoice.setStatus(existing.getStatus());
        finInvoice.setBizStatus(existing.getBizStatus());
        finInvoice.setConfirmBy(existing.getConfirmBy());
        finInvoice.setConfirmTime(existing.getConfirmTime());
        fillInvoiceAmount(finInvoice);
        validateSourceInvoiceQuota(finInvoice);
        return finInvoiceMapper.updateFinInvoice(finInvoice);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int confirmFinInvoice(Long invoiceId, String operator)
    {
        FinInvoice finInvoice = getExistingInvoice(invoiceId);
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(finInvoice.getDeptId(), finInvoice.getOwnerUserId(), null);
        }
        if (!"draft".equals(finInvoice.getBizStatus())) throw new ServiceException("当前发票状态不允许确认");
        finGeneralLedgerService.assertPeriodOpen(finInvoice.getBusinessDate() == null ? new Date() : finInvoice.getBusinessDate());
        fillInvoiceAmount(finInvoice);
        validateSourceInvoiceQuota(finInvoice);
        finInvoice.setStatus("1");
        finInvoice.setBizStatus("confirmed");
        finInvoice.setConfirmBy(operator);
        finInvoice.setConfirmTime(new Date());
        finInvoice.setUpdateBy(operator);
        int updateRows = finInvoiceMapper.updateFinInvoice(finInvoice);
        if (updateRows > 0)
        {
            syncInvoiceFinanceBill(finInvoice, operator);
            // 仅按增值税税额生成销项税/进项税凭证，避免对含税价税合计重复确认主营业务收入
            BigDecimal taxAmt = defaultAmount(finInvoice.getTaxAmount());
            if (taxAmt.compareTo(BigDecimal.ZERO) != 0)
            {
                String eventType = "purchase".equals(finInvoice.getInvoiceType())
                    ? "purchase_invoice_confirm" : "sale_invoice_confirm";
                finVoucherEventService.recordVoucherEvent("invoice", finInvoice.getInvoiceId(), finInvoice.getInvoiceNo(),
                    eventType, finInvoice.getBusinessDate(), taxAmt, operator, "发票确认税额待生成凭证");
            }
        }
        return updateRows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinInvoice issueRedInvoice(Long originInvoiceId, BigDecimal redAmount, String remark, String operator)
    {
        FinInvoice origin = getExistingInvoice(originInvoiceId);
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertRecordAccess(origin.getDeptId(), origin.getOwnerUserId(), null);
        }
        if (!"confirmed".equals(origin.getBizStatus()))
        {
            throw new ServiceException("仅已确认状态的蓝字发票允许红冲");
        }
        if ("red".equals(origin.getInvoiceKind()) || defaultAmount(origin.getInvoiceAmount()).compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("红字发票不允许再次红冲");
        }
        Date now = new Date();
        finGeneralLedgerService.assertPeriodOpen(now);

        // 计算该蓝票已红冲累计绝对额
        FinInvoice redQuery = new FinInvoice();
        redQuery.setOriginInvoiceId(originInvoiceId);
        redQuery.setInvoiceKind("red");
        List<FinInvoice> existingReds = finInvoiceMapper.selectFinInvoiceList(redQuery);
        BigDecimal alreadyRedAbs = BigDecimal.ZERO;
        if (existingReds != null)
        {
            for (FinInvoice r : existingReds)
            {
                if ("confirmed".equals(r.getBizStatus()))
                {
                    alreadyRedAbs = alreadyRedAbs.add(defaultAmount(r.getInvoiceAmount()).abs());
                }
            }
        }
        BigDecimal maxAllowableRed = defaultAmount(origin.getInvoiceAmount()).subtract(alreadyRedAbs);
        if (maxAllowableRed.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("该蓝字发票已全额红冲，无剩余可红冲金额");
        }
        BigDecimal targetRedAbs = (redAmount == null || redAmount.compareTo(BigDecimal.ZERO) <= 0) ? maxAllowableRed : redAmount.abs();
        if (targetRedAbs.compareTo(maxAllowableRed) > 0)
        {
            throw new ServiceException("本次红冲金额(" + targetRedAbs + ")超过该蓝票剩余可红冲金额(" + maxAllowableRed + ")");
        }

        BigDecimal taxRate = defaultAmount(origin.getTaxRate());
        BigDecimal effectiveRate = normalizeEffectiveTaxRate(taxRate);
        BigDecimal negAmount = targetRedAbs.negate();
        BigDecimal negUntaxed = negAmount.divide(BigDecimal.ONE.add(effectiveRate), 2, RoundingMode.HALF_UP);
        BigDecimal negTax = negAmount.subtract(negUntaxed);

        FinInvoice redInvoice = new FinInvoice();
        redInvoice.setInvoiceNo(generateInvoiceNo(origin.getInvoiceType(), "red"));
        redInvoice.setInvoiceType(origin.getInvoiceType());
        redInvoice.setInvoiceKind("red");
        redInvoice.setOriginInvoiceId(origin.getInvoiceId());
        redInvoice.setCustomerId(origin.getCustomerId());
        redInvoice.setSupplierId(origin.getSupplierId());
        redInvoice.setDeptId(origin.getDeptId());
        redInvoice.setOwnerUserId(origin.getOwnerUserId());
        redInvoice.setSourceBillType(origin.getSourceBillType());
        redInvoice.setSourceBillId(origin.getSourceBillId());
        redInvoice.setSourceBillNo(origin.getSourceBillNo());
        redInvoice.setBusinessDate(now);
        redInvoice.setInvoiceDate(now);
        redInvoice.setInvoiceAmount(negAmount);
        redInvoice.setTaxRate(taxRate);
        redInvoice.setUntaxedAmount(negUntaxed);
        redInvoice.setTaxAmount(negTax);
        redInvoice.setStatus("1");
        redInvoice.setBizStatus("confirmed");
        redInvoice.setConfirmBy(operator);
        redInvoice.setConfirmTime(now);
        redInvoice.setCreateBy(operator);
        redInvoice.setRemark(StringUtils.isNotEmpty(remark) ? remark : ("红冲原蓝字发票：" + origin.getInvoiceNo()));
        finInvoiceMapper.insertFinInvoice(redInvoice);

        // 若原蓝字发票曾单独生成应收/应付，红冲发票时同步生成对应红字应收/红字应付冲减
        if (("sale".equals(origin.getInvoiceType()) || "sales".equals(origin.getInvoiceType()))
            && origin.getCustomerId() != null && hasDirectInvoiceReceivable(origin.getInvoiceId()))
        {
            finReceivableService.createRedReceivableForReturn(
                origin.getCustomerId(), "invoice_red", redInvoice.getInvoiceId(), redInvoice.getInvoiceNo(),
                now, targetRedAbs, operator, "红冲销售发票 " + origin.getInvoiceNo() + " 自动生成红字应收");
        }
        else if ("purchase".equals(origin.getInvoiceType())
            && origin.getSupplierId() != null && hasDirectInvoicePayable(origin.getInvoiceId()))
        {
            finPayableService.createRedPayableForReturn(
                origin.getSupplierId(), "invoice_red", redInvoice.getInvoiceId(), redInvoice.getInvoiceNo(),
                now, targetRedAbs, operator, "红冲采购发票 " + origin.getInvoiceNo() + " 自动生成红字应付");
        }

        if (negTax.compareTo(BigDecimal.ZERO) != 0)
        {
            String eventType = "purchase".equals(origin.getInvoiceType())
                ? "purchase_invoice_confirm" : "sale_invoice_confirm";
            finVoucherEventService.recordVoucherEvent("invoice", redInvoice.getInvoiceId(), redInvoice.getInvoiceNo(),
                eventType, now, negTax, operator, "红字发票税额冲销待生成冲减凭证");
        }
        return redInvoice;
    }

    @Override
    public int deleteFinInvoiceById(Long invoiceId)
    {
        FinInvoice finInvoice = getExistingInvoice(invoiceId);
        if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(finInvoice.getDeptId(), finInvoice.getOwnerUserId(), null);
        if (!"draft".equals(finInvoice.getBizStatus())) throw new ServiceException("当前发票状态不允许删除");
        if (finGeneralLedgerService != null) finGeneralLedgerService.assertPeriodOpen(finInvoice.getBusinessDate() == null ? new Date() : finInvoice.getBusinessDate());
        return finInvoiceMapper.deleteFinInvoiceById(invoiceId);
    }

    @Override
    public int deleteFinInvoiceByIds(Long[] invoiceIds)
    {
        for (Long invoiceId : invoiceIds)
        {
            FinInvoice finInvoice = getExistingInvoice(invoiceId);
            if (bizScopeHelper != null) bizScopeHelper.assertRecordAccess(finInvoice.getDeptId(), finInvoice.getOwnerUserId(), null);
            if (!"draft".equals(finInvoice.getBizStatus())) throw new ServiceException("当前发票状态不允许删除");
            if (finGeneralLedgerService != null) finGeneralLedgerService.assertPeriodOpen(finInvoice.getBusinessDate() == null ? new Date() : finInvoice.getBusinessDate());
        }
        return finInvoiceMapper.deleteFinInvoiceByIds(invoiceIds);
    }

    private void validateSourceInvoiceQuota(FinInvoice finInvoice)
    {
        if ("red".equals(finInvoice.getInvoiceKind()) || StringUtils.isEmpty(finInvoice.getSourceBillType()) || finInvoice.getSourceBillId() == null)
        {
            return;
        }
        BigDecimal sourceLimit = resolveSourceBillAmount(finInvoice.getSourceBillType(), finInvoice.getSourceBillId());
        if (sourceLimit == null || sourceLimit.compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }
        FinInvoice q = new FinInvoice();
        q.setSourceBillType(finInvoice.getSourceBillType());
        q.setSourceBillId(finInvoice.getSourceBillId());
        List<FinInvoice> related = finInvoiceMapper.selectFinInvoiceList(q);
        BigDecimal alreadyNet = BigDecimal.ZERO;
        if (related != null)
        {
            for (FinInvoice inv : related)
            {
                if (finInvoice.getInvoiceId() != null && finInvoice.getInvoiceId().equals(inv.getInvoiceId())) continue;
                if ("confirmed".equals(inv.getBizStatus()))
                {
                    alreadyNet = alreadyNet.add(defaultAmount(inv.getInvoiceAmount()));
                }
            }
        }
        BigDecimal projected = alreadyNet.add(defaultAmount(finInvoice.getInvoiceAmount()));
        if (projected.compareTo(sourceLimit) > 0)
        {
            throw new ServiceException("来源单据累计开票净额(" + projected + ")超过来源单据可开票总额(" + sourceLimit + ")");
        }
    }

    private BigDecimal resolveSourceBillAmount(String sourceBillType, Long sourceBillId)
    {
        if ("receivable".equals(sourceBillType) || "fin_receivable".equals(sourceBillType))
        {
            FinReceivable ar = finReceivableMapper.selectFinReceivableById(sourceBillId);
            if (ar == null || "2".equals(ar.getBillDirection())) return BigDecimal.ZERO;
            return ar.getAmount();
        }
        if ("payable".equals(sourceBillType) || "fin_payable".equals(sourceBillType))
        {
            FinPayable ap = finPayableMapper.selectFinPayableById(sourceBillId);
            if (ap == null || "2".equals(ap.getBillDirection())) return BigDecimal.ZERO;
            return ap.getAmount();
        }
        if (("outbound".equals(sourceBillType) || "wms_outbound".equals(sourceBillType)) && wmsOutboundMapper != null)
        {
            WmsOutbound out = wmsOutboundMapper.selectWmsOutboundById(sourceBillId);
            if (out == null || "1".equals(out.getIsReversal()) || "1".equals(out.getReversedFlag()) || "3".equals(out.getDeliveryStatus()))
            {
                throw new ServiceException("关联出库单已红冲或已整单退货，禁止开具蓝字发票");
            }
            return out.getTotalAmount();
        }
        if (("inbound".equals(sourceBillType) || "wms_inbound".equals(sourceBillType)) && wmsInboundMapper != null)
        {
            WmsInbound inb = wmsInboundMapper.selectWmsInboundById(sourceBillId);
            if (inb == null || "1".equals(inb.getIsReversal()) || "1".equals(inb.getReversedFlag()))
            {
                throw new ServiceException("关联入库单已红冲，禁止登记蓝字采购发票");
            }
            return inb.getTotalAmount();
        }
        if (("sale_order".equals(sourceBillType) || "sal_order".equals(sourceBillType)) && salOrderMapper != null)
        {
            SalOrder so = salOrderMapper.selectSalOrderById(sourceBillId);
            return so == null ? null : (so.getTaxTotalAmount() != null && so.getTaxTotalAmount().compareTo(BigDecimal.ZERO) > 0 ? so.getTaxTotalAmount() : so.getTotalAmount());
        }
        if ("pur_order".equals(sourceBillType) && purOrderMapper != null)
        {
            PurOrder po = purOrderMapper.selectPurOrderById(sourceBillId);
            return po == null ? null : (po.getTaxTotalAmount() != null && po.getTaxTotalAmount().compareTo(BigDecimal.ZERO) > 0 ? po.getTaxTotalAmount() : po.getTotalAmount());
        }
        return null;
    }

    private BigDecimal normalizeEffectiveTaxRate(BigDecimal rawTaxRate)
    {
        BigDecimal rate = defaultAmount(rawTaxRate);
        if (rate.compareTo(BigDecimal.ONE) > 0)
        {
            // 前端传入百分数（如 13 表示 13%）时统一除以 100 转换为小数税率 0.13
            return rate.divide(new BigDecimal("100"), 6, RoundingMode.HALF_UP);
        }
        return rate;
    }

    private boolean hasDirectInvoiceReceivable(Long invoiceId)
    {
        FinReceivable q = new FinReceivable();
        q.setBillType("invoice");
        List<FinReceivable> list = finReceivableMapper.selectFinReceivableList(q);
        if (list == null) return false;
        for (FinReceivable r : list)
        {
            if (invoiceId.equals(r.getBillId())) return true;
        }
        return false;
    }

    private boolean hasDirectInvoicePayable(Long invoiceId)
    {
        FinPayable q = new FinPayable();
        q.setBillType("invoice");
        List<FinPayable> list = finPayableMapper.selectFinPayableList(q);
        if (list == null) return false;
        for (FinPayable p : list)
        {
            if (invoiceId.equals(p.getBillId())) return true;
        }
        return false;
    }

    private FinInvoice getExistingInvoice(Long invoiceId)
    {
        FinInvoice finInvoice = finInvoiceMapper.selectFinInvoiceById(invoiceId);
        if (finInvoice == null) throw new ServiceException("发票登记不存在");
        return finInvoice;
    }

    private void fillInvoiceAmount(FinInvoice finInvoice)
    {
        String invoiceType = finInvoice.getInvoiceType();
        if (!"sale".equals(invoiceType) && !"sales".equals(invoiceType) && !"purchase".equals(invoiceType))
        {
            throw new ServiceException("发票类型仅支持销售发票(sale)或采购发票(purchase)");
        }
        if ("purchase".equals(invoiceType)) finInvoice.setCustomerId(null);
        else finInvoice.setSupplierId(null);

        BigDecimal invoiceAmount = defaultAmount(finInvoice.getInvoiceAmount());
        BigDecimal taxRate = defaultAmount(finInvoice.getTaxRate());
        if (!"red".equals(finInvoice.getInvoiceKind()) && invoiceAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("蓝字发票金额必须大于0");
        }
        if (taxRate.compareTo(BigDecimal.ZERO) < 0) throw new ServiceException("税率不能小于0");
        if (taxRate.compareTo(new BigDecimal("100")) > 0) throw new ServiceException("税率不能超过100%");
        finInvoice.setInvoiceAmount(invoiceAmount);
        finInvoice.setTaxRate(taxRate);
        BigDecimal effectiveRate = normalizeEffectiveTaxRate(taxRate);
        BigDecimal untaxedAmount = invoiceAmount.divide(BigDecimal.ONE.add(effectiveRate), 2, RoundingMode.HALF_UP);
        BigDecimal taxAmount = invoiceAmount.subtract(untaxedAmount);
        finInvoice.setUntaxedAmount(untaxedAmount);
        finInvoice.setTaxAmount(taxAmount);
    }

    private void syncInvoiceFinanceBill(FinInvoice finInvoice, String operator)
    {
        if ("red".equals(finInvoice.getInvoiceKind())) return;
        if ("sale".equals(finInvoice.getInvoiceType()) || "sales".equals(finInvoice.getInvoiceType()))
        {
            syncInvoiceReceivable(finInvoice, operator);
        }
        if ("purchase".equals(finInvoice.getInvoiceType()))
        {
            syncInvoicePayable(finInvoice, operator);
        }
    }

    private void syncInvoiceReceivable(FinInvoice finInvoice, String operator)
    {
        if (finInvoice.getCustomerId() == null) throw new ServiceException("销售发票必须选择客户");
        if (hasExistingReceivableForInvoice(finInvoice)) return;
        FinReceivable finReceivable = new FinReceivable();
        finReceivable.setCustomerId(finInvoice.getCustomerId());
        finReceivable.setBillType("invoice");
        finReceivable.setBillId(finInvoice.getInvoiceId());
        finReceivable.setSourceBillType("invoice");
        finReceivable.setSourceBillId(finInvoice.getInvoiceId());
        finReceivable.setSourceBillNo(StringUtils.isNotEmpty(finInvoice.getSourceBillNo()) ? finInvoice.getSourceBillNo() : finInvoice.getInvoiceNo());
        finReceivable.setBusinessDate(finInvoice.getBusinessDate());
        finReceivable.setAmount(finInvoice.getInvoiceAmount());
        finReceivable.setDueDate(finInvoice.getInvoiceDate());
        finReceivable.setCreateBy(operator);
        finReceivable.setRemark("销售发票确认自动生成应收单：" + finInvoice.getInvoiceNo());
        finReceivableService.insertFinReceivable(finReceivable);
        finReceivableService.auditFinReceivable(finReceivable.getReceivableId(), operator);
    }

    private void syncInvoicePayable(FinInvoice finInvoice, String operator)
    {
        if (finInvoice.getSupplierId() == null) throw new ServiceException("采购发票必须选择供应商");
        if (hasExistingPayableForInvoice(finInvoice)) return;
        FinPayable finPayable = new FinPayable();
        finPayable.setSupplierId(finInvoice.getSupplierId());
        finPayable.setBillType("invoice");
        finPayable.setBillId(finInvoice.getInvoiceId());
        finPayable.setSourceBillType("invoice");
        finPayable.setSourceBillId(finInvoice.getInvoiceId());
        finPayable.setSourceBillNo(StringUtils.isNotEmpty(finInvoice.getSourceBillNo()) ? finInvoice.getSourceBillNo() : finInvoice.getInvoiceNo());
        finPayable.setBusinessDate(finInvoice.getBusinessDate());
        finPayable.setAmount(finInvoice.getInvoiceAmount());
        finPayable.setDueDate(finInvoice.getInvoiceDate());
        finPayable.setCreateBy(operator);
        finPayable.setRemark("采购发票确认自动生成应付单：" + finInvoice.getInvoiceNo());
        finPayableService.insertFinPayable(finPayable);
        finPayableService.auditFinPayable(finPayable.getPayableId(), operator);
    }

    private boolean hasExistingReceivableForInvoice(FinInvoice finInvoice)
    {
        FinReceivable query = new FinReceivable();
        query.setCustomerId(finInvoice.getCustomerId());
        List<FinReceivable> list = finReceivableMapper.selectFinReceivableList(query);
        if (list == null) return false;
        for (FinReceivable ar : list)
        {
            if ("invoice".equals(ar.getBillType()) && finInvoice.getInvoiceId().equals(ar.getBillId())) return true;
            if (finInvoice.getSourceBillId() != null && StringUtils.isNotEmpty(finInvoice.getSourceBillType()))
            {
                if ((finInvoice.getSourceBillType().equals(ar.getSourceBillType()) && finInvoice.getSourceBillId().equals(ar.getSourceBillId()))
                    || (finInvoice.getSourceBillType().equals(ar.getBillType()) && finInvoice.getSourceBillId().equals(ar.getBillId())))
                {
                    return true;
                }
            }
            if (StringUtils.isNotEmpty(finInvoice.getSourceBillNo()) && finInvoice.getSourceBillNo().equals(ar.getSourceBillNo())) return true;
        }
        return false;
    }

    private boolean hasExistingPayableForInvoice(FinInvoice finInvoice)
    {
        FinPayable query = new FinPayable();
        query.setSupplierId(finInvoice.getSupplierId());
        List<FinPayable> list = finPayableMapper.selectFinPayableList(query);
        if (list == null) return false;
        for (FinPayable ap : list)
        {
            if ("invoice".equals(ap.getBillType()) && finInvoice.getInvoiceId().equals(ap.getBillId())) return true;
            if (finInvoice.getSourceBillId() != null && StringUtils.isNotEmpty(finInvoice.getSourceBillType()))
            {
                if ((finInvoice.getSourceBillType().equals(ap.getSourceBillType()) && finInvoice.getSourceBillId().equals(ap.getSourceBillId()))
                    || (finInvoice.getSourceBillType().equals(ap.getBillType()) && finInvoice.getSourceBillId().equals(ap.getBillId())))
                {
                    return true;
                }
            }
            if (StringUtils.isNotEmpty(finInvoice.getSourceBillNo()) && finInvoice.getSourceBillNo().equals(ap.getSourceBillNo())) return true;
        }
        return false;
    }

    private String generateInvoiceNo(String invoiceType, String invoiceKind)
    {
        String basePrefix = "purchase".equals(invoiceType) ? "pin" : "sin";
        if ("red".equals(invoiceKind)) basePrefix = "r" + basePrefix;
        String invoicePrefix = basePrefix + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String currentMaxInvoiceNo = finInvoiceMapper.selectMaxInvoiceNoByPrefix(invoicePrefix);
        int nextSequence = 1;
        if (StringUtils.isNotEmpty(currentMaxInvoiceNo) && currentMaxInvoiceNo.length() > invoicePrefix.length())
        {
            String sequenceText = currentMaxInvoiceNo.substring(invoicePrefix.length());
            if (StringUtils.isNumeric(sequenceText)) nextSequence = Integer.parseInt(sequenceText) + 1;
        }
        return invoicePrefix + String.format("%03d", nextSequence);
    }

    private BigDecimal defaultAmount(BigDecimal amount) { return amount == null ? BigDecimal.ZERO : amount; }
}
