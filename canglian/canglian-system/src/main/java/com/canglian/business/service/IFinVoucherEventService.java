package com.canglian.business.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.canglian.business.domain.FinVoucherEntry;
import com.canglian.business.domain.FinVoucherEvent;

/**
 * 凭证事件服务接口
 *
 * @author canglian
 */
public interface IFinVoucherEventService
{
    public FinVoucherEvent selectFinVoucherEventById(Long voucherEventId);

    public List<FinVoucherEvent> selectFinVoucherEventList(FinVoucherEvent finVoucherEvent);

    public int insertFinVoucherEvent(FinVoucherEvent finVoucherEvent);

    public int updateFinVoucherEvent(FinVoucherEvent finVoucherEvent);

    public int deleteFinVoucherEventById(Long voucherEventId);

    public int deleteFinVoucherEventByIds(Long[] voucherEventIds);

    public void recordVoucherEvent(String billType, Long billId, String billNo, String eventType, Date eventDate, BigDecimal eventAmount, String operator, String remark);

    /**
     * 业务单据反审核时清理未生成凭证的待处理事件；若已生成凭证则拒绝反审核
     */
    public void cancelPendingVoucherEvents(String billType, Long billId);

    public int generateVoucher(Long voucherEventId, String operator);

    public int writebackVoucher(Long voucherEventId, String operator);

    public int reverseVoucher(Long voucherEventId, String operator);

    /**
     * 查询凭证事件对应的复式借贷分录明细
     */
    public List<FinVoucherEntry> selectVoucherEntries(Long voucherEventId);

    /**
     * 查询用于导出(标准/金蝶/用友格式)的复式凭证分录集合
     */
    public List<FinVoucherEntry> selectVoucherEntriesForExport(FinVoucherEvent query, String format);

    /**
     * 补全已审核业务单据缺失的待生成凭证事件（幂等去重）
     */
    public java.util.Map<String, Object> regenerateMissingVoucherEvents(String operator);
}
