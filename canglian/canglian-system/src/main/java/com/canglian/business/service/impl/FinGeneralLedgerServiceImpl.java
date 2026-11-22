package com.canglian.business.service.impl;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.FinVoucherTemplate;
import com.canglian.business.mapper.FinGeneralLedgerMapper;
import com.canglian.business.mapper.FinVoucherEntryMapper;
import com.canglian.business.service.IFinGeneralLedgerService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 基础总账接口服务实现
 * 
 * @author canglian
 */
@Service
public class FinGeneralLedgerServiceImpl implements IFinGeneralLedgerService
{
    @Autowired
    private FinGeneralLedgerMapper finGeneralLedgerMapper;

    @Autowired
    private FinVoucherEntryMapper finVoucherEntryMapper;

    @Override
    public List<Map<String, Object>> selectAccountSubjectList(String status)
    {
        return finGeneralLedgerMapper.selectAccountSubjectList(status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertAccountSubject(Map<String, Object> subject, String operator)
    {
        if (subject == null)
        {
            throw new ServiceException("会计科目参数不能为空");
        }
        String subjectCode = trimStr(subject.get("subjectCode"));
        String subjectName = trimStr(subject.get("subjectName"));
        if (StringUtils.isEmpty(subjectCode) || StringUtils.isEmpty(subjectName))
        {
            throw new ServiceException("科目编码与科目名称不能为空");
        }
        if (finGeneralLedgerMapper.countAccountSubjectByCode(subjectCode, null) > 0)
        {
            throw new ServiceException("科目编码[" + subjectCode + "]已存在");
        }
        subject.put("subjectCode", subjectCode);
        subject.put("subjectName", subjectName);
        subject.put("createBy", operator);
        return finGeneralLedgerMapper.insertAccountSubject(subject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateAccountSubject(Map<String, Object> subject, String operator)
    {
        if (subject == null || subject.get("subjectId") == null)
        {
            throw new ServiceException("缺少科目ID");
        }
        Long subjectId = Long.valueOf(String.valueOf(subject.get("subjectId")));
        Map<String, Object> existing = finGeneralLedgerMapper.selectAccountSubjectById(subjectId);
        if (existing == null)
        {
            throw new ServiceException("会计科目不存在");
        }
        String subjectCode = trimStr(subject.get("subjectCode"));
        if (StringUtils.isNotEmpty(subjectCode) && finGeneralLedgerMapper.countAccountSubjectByCode(subjectCode, subjectId) > 0)
        {
            throw new ServiceException("科目编码[" + subjectCode + "]已存在");
        }
        subject.put("subjectId", subjectId);
        subject.put("updateBy", operator);
        return finGeneralLedgerMapper.updateAccountSubject(subject);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteAccountSubjectById(Long subjectId)
    {
        Map<String, Object> existing = finGeneralLedgerMapper.selectAccountSubjectById(subjectId);
        if (existing == null)
        {
            throw new ServiceException("会计科目不存在");
        }
        String subjectCode = String.valueOf(existing.get("subjectCode"));
        if (finVoucherEntryMapper.countTemplateBySubjectCode(subjectCode) > 0)
        {
            throw new ServiceException("科目[" + subjectCode + "]已被凭证模板引用，禁止删除");
        }
        if (finVoucherEntryMapper.countEntriesBySubjectCode(subjectCode) > 0)
        {
            throw new ServiceException("科目[" + subjectCode + "]已发生凭证分录，禁止删除");
        }
        return finGeneralLedgerMapper.deleteAccountSubjectById(subjectId);
    }

    @Override
    public List<Map<String, Object>> selectAuxiliaryItemList(String auxiliaryType)
    {
        return finGeneralLedgerMapper.selectAuxiliaryItemList(auxiliaryType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncAuxiliaryItems(String operator)
    {
        int customers = syncAuxiliaryGroup("customer", finGeneralLedgerMapper.selectActiveCustomersForAux(), operator);
        int suppliers = syncAuxiliaryGroup("supplier", finGeneralLedgerMapper.selectActiveSuppliersForAux(), operator);
        int warehouses = syncAuxiliaryGroup("warehouse", finGeneralLedgerMapper.selectActiveWarehousesForAux(), operator);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("syncedCustomers", customers);
        result.put("syncedSuppliers", suppliers);
        result.put("syncedWarehouses", warehouses);
        result.put("totalSynced", customers + suppliers + warehouses);
        return result;
    }

    private int syncAuxiliaryGroup(String auxType, List<Map<String, Object>> rows, String operator)
    {
        int count = 0;
        if (rows == null)
        {
            return 0;
        }
        for (Map<String, Object> row : rows)
        {
            if (row.get("relationId") == null)
            {
                continue;
            }
            Long relationId = Long.valueOf(String.valueOf(row.get("relationId")));
            String code = trimStr(row.get("itemCode"));
            String name = trimStr(row.get("itemName"));
            String status = trimStr(row.get("status"));
            if (StringUtils.isEmpty(code))
            {
                code = auxType.toUpperCase() + "-" + relationId;
            }
            if (finGeneralLedgerMapper.countAuxiliaryItem(auxType, relationId) > 0)
            {
                finGeneralLedgerMapper.updateAuxiliaryItem(auxType, code, name, relationId, status, operator);
            }
            else
            {
                finGeneralLedgerMapper.insertAuxiliaryItem(auxType, code, name, relationId, status, operator);
            }
            count++;
        }
        return count;
    }

    @Override
    public List<Map<String, Object>> selectFiscalPeriodList(String closeStatus)
    {
        return finGeneralLedgerMapper.selectFiscalPeriodList(closeStatus);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> initFiscalPeriodsByYear(Integer year, String operator)
    {
        if (year == null || year < 2000 || year > 2100)
        {
            throw new ServiceException("年份必须在2000至2100之间");
        }
        int created = 0;
        int skipped = 0;
        for (int month = 1; month <= 12; month++)
        {
            YearMonth ym = YearMonth.of(year, month);
            String periodCode = String.format("%04d-%02d", year, month);
            if (finGeneralLedgerMapper.countFiscalPeriodByCode(periodCode) > 0)
            {
                skipped++;
                continue;
            }
            LocalDate start = ym.atDay(1);
            LocalDate end = ym.atEndOfMonth();
            Date startDate = Date.from(start.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date endDate = Date.from(end.atStartOfDay(ZoneId.systemDefault()).toInstant());
            finGeneralLedgerMapper.insertFiscalPeriod(periodCode, startDate, endDate, operator, year + "年度批量初始化");
            created++;
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("year", year);
        result.put("createdCount", created);
        result.put("skippedCount", skipped);
        return result;
    }

    @Override
    public Map<String, Object> checkPeriodCloseReadiness(Long periodId)
    {
        Map<String, Object> period = finGeneralLedgerMapper.selectFiscalPeriodById(periodId);
        if (period == null)
        {
            throw new ServiceException("会计期间不存在");
        }
        Date startDate = (Date) period.get("startDate");
        Date endDate = (Date) period.get("endDate");
        String periodCode = String.valueOf(period.get("periodCode"));

        List<Map<String, Object>> items = new ArrayList<>();
        int earlierOpen = finGeneralLedgerMapper.countEarlierOpenPeriods(periodCode);
        addCheckItem(items, "earlier_open_periods", "更早未结账会计期间", earlierOpen, true);
        addCheckItem(items, "draft_inbound", "草稿入库单", finGeneralLedgerMapper.countDraftInboundInPeriod(startDate, endDate), true);
        addCheckItem(items, "draft_outbound", "草稿出库单", finGeneralLedgerMapper.countDraftOutboundInPeriod(startDate, endDate), true);
        addCheckItem(items, "draft_transfer", "草稿调拨单", finGeneralLedgerMapper.countDraftTransferInPeriod(startDate, endDate), true);
        addCheckItem(items, "draft_inventory_check", "草稿盘点单", finGeneralLedgerMapper.countDraftInventoryCheckInPeriod(startDate, endDate), true);
        addCheckItem(items, "draft_purchase_return", "草稿采购退货单", finGeneralLedgerMapper.countDraftPurchaseReturnInPeriod(startDate, endDate), true);
        addCheckItem(items, "draft_sale_return", "草稿销售退货单", finGeneralLedgerMapper.countDraftSaleReturnInPeriod(startDate, endDate), true);
        addCheckItem(items, "draft_receipt", "未确认收款单", finGeneralLedgerMapper.countDraftReceiptInPeriod(startDate, endDate), true);
        addCheckItem(items, "draft_payment", "未确认付款单", finGeneralLedgerMapper.countDraftPaymentInPeriod(startDate, endDate), true);
        addCheckItem(items, "draft_expense", "未确认费用单", finGeneralLedgerMapper.countDraftExpenseInPeriod(startDate, endDate), true);
        addCheckItem(items, "draft_invoice", "未确认发票", finGeneralLedgerMapper.countDraftInvoiceInPeriod(startDate, endDate), true);
        addCheckItem(items, "draft_write_off", "未确认核销单", finGeneralLedgerMapper.countDraftWriteOffInPeriod(startDate, endDate), true);
        addCheckItem(items, "draft_bad_debt", "未确认坏账单", finGeneralLedgerMapper.countDraftBadDebtInPeriod(startDate, endDate), true);
        addCheckItem(items, "draft_fund_transfer", "未确认资金互转单", finGeneralLedgerMapper.countDraftFundTransferInPeriod(startDate, endDate), true);
        addCheckItem(items, "pending_voucher_events", "待生成凭证事件", finGeneralLedgerMapper.countPendingVoucherEventInPeriod(startDate, endDate), true);
        addCheckItem(items, "unwritten_back_vouchers", "已生成未回写凭证", finGeneralLedgerMapper.countUnwrittenBackVoucherInPeriod(startDate, endDate), true);

        int totalBlocking = 0;
        for (Map<String, Object> item : items)
        {
            if (Boolean.TRUE.equals(item.get("blocking")))
            {
                totalBlocking += ((Number) item.get("count")).intValue();
            }
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("periodId", periodId);
        result.put("periodCode", periodCode);
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("ready", totalBlocking == 0);
        result.put("totalBlockingCount", totalBlocking);
        result.put("items", items);
        return result;
    }

    private void addCheckItem(List<Map<String, Object>> items, String key, String label, int count, boolean blocking)
    {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("key", key);
        item.put("label", label);
        item.put("count", count);
        item.put("blocking", blocking && count > 0);
        item.put("passed", count == 0);
        items.add(item);
    }

    @Override
    public int closeFiscalPeriod(Long periodId, String operator)
    {
        Map<String, Object> period = finGeneralLedgerMapper.selectFiscalPeriodById(periodId);
        if (period == null)
        {
            throw new ServiceException("会计期间不存在");
        }
        String closeStatus = String.valueOf(period.get("closeStatus"));
        if ("closed".equals(closeStatus))
        {
            throw new ServiceException("会计期间已结账，请勿重复结账");
        }
        String periodCode = String.valueOf(period.get("periodCode"));
        if (finGeneralLedgerMapper.countEarlierOpenPeriods(periodCode) > 0)
        {
            throw new ServiceException("存在更早的未结账会计期间，请按期间顺序依次结账");
        }
        Map<String, Object> readiness = checkPeriodCloseReadiness(periodId);
        int blockingCount = ((Number) readiness.get("totalBlockingCount")).intValue();
        if (blockingCount > 0)
        {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> checkItems = (List<Map<String, Object>>) readiness.get("items");
            StringBuilder detail = new StringBuilder();
            for (Map<String, Object> item : checkItems)
            {
                if (Boolean.TRUE.equals(item.get("blocking")))
                {
                    if (detail.length() > 0) detail.append("、");
                    detail.append(item.get("label")).append("(").append(item.get("count")).append(")");
                }
            }
            throw new ServiceException("会计期间[" + periodCode + "]尚有 " + blockingCount + " 项阻塞事项未处理完成：" + detail + "，禁止结账");
        }
        return finGeneralLedgerMapper.updateFiscalPeriodCloseStatus(periodId, "closed", operator);
    }

    @Override
    public int reopenFiscalPeriod(Long periodId, String operator)
    {
        Map<String, Object> period = finGeneralLedgerMapper.selectFiscalPeriodById(periodId);
        if (period == null)
        {
            throw new ServiceException("会计期间不存在");
        }
        String closeStatus = String.valueOf(period.get("closeStatus"));
        if (!"closed".equals(closeStatus))
        {
            throw new ServiceException("仅已结账的会计期间允许反结账");
        }
        String periodCode = String.valueOf(period.get("periodCode"));
        if (finGeneralLedgerMapper.countLaterClosedPeriods(periodCode) > 0)
        {
            throw new ServiceException("存在更晚的已结账会计期间，请先反结账后续期间");
        }
        return finGeneralLedgerMapper.updateFiscalPeriodCloseStatus(periodId, "open", operator);
    }

    @Override
    public void assertPeriodOpen(Date businessDate)
    {
        Date checkDate = businessDate == null ? new Date() : businessDate;
        String closedPeriodCode = finGeneralLedgerMapper.selectClosedPeriodCodeByDate(checkDate);
        if (StringUtils.isNotEmpty(closedPeriodCode))
        {
            throw new ServiceException("会计期间[" + closedPeriodCode + "]已结账，不允许执行该业务操作");
        }
    }

    @Override
    public List<FinVoucherTemplate> selectVoucherTemplateList(FinVoucherTemplate template)
    {
        return finVoucherEntryMapper.selectTemplateList(template == null ? new FinVoucherTemplate() : template);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertVoucherTemplate(FinVoucherTemplate template, String operator)
    {
        validateVoucherTemplate(template, null);
        template.setCreateBy(operator);
        if (StringUtils.isEmpty(template.getStatus()))
        {
            template.setStatus("0");
        }
        return finVoucherEntryMapper.insertTemplate(template);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateVoucherTemplate(FinVoucherTemplate template)
    {
        if (template == null || template.getTemplateId() == null)
        {
            throw new ServiceException("缺少模板ID");
        }
        FinVoucherTemplate existing = finVoucherEntryMapper.selectTemplateById(template.getTemplateId());
        if (existing == null)
        {
            throw new ServiceException("凭证模板不存在");
        }
        validateVoucherTemplate(template, template.getTemplateId());
        return finVoucherEntryMapper.updateTemplate(template);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteVoucherTemplateById(Long templateId)
    {
        FinVoucherTemplate existing = finVoucherEntryMapper.selectTemplateById(templateId);
        if (existing == null)
        {
            throw new ServiceException("凭证模板不存在");
        }
        return finVoucherEntryMapper.deleteTemplateById(templateId);
    }

    private void validateVoucherTemplate(FinVoucherTemplate template, Long excludeId)
    {
        if (template == null || StringUtils.isEmpty(template.getEventType())
            || StringUtils.isEmpty(template.getDirection())
            || StringUtils.isEmpty(template.getSubjectCode())
            || StringUtils.isEmpty(template.getSubjectName()))
        {
            throw new ServiceException("事件类型、借贷方向、科目编码和科目名称不能为空");
        }
        if (!"debit".equals(template.getDirection()) && !"credit".equals(template.getDirection()))
        {
            throw new ServiceException("借贷方向仅支持 debit 或 credit");
        }
        if (template.getEntrySeq() == null || template.getEntrySeq() <= 0)
        {
            template.setEntrySeq(1);
        }
        if (finVoucherEntryMapper.countTemplateByEventAndSeq(template.getEventType(), template.getEntrySeq(), excludeId) > 0)
        {
            throw new ServiceException("事件[" + template.getEventType() + "]的分录序号[" + template.getEntrySeq() + "]已存在");
        }
    }

    private String trimStr(Object value)
    {
        return value == null ? null : String.valueOf(value).trim();
    }
}
