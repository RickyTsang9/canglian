package com.canglian.business.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
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
     * 生成凭证
     *
     * @param voucherEventId 凭证事件id
     * @param operator 操作人
     * @return 结果
     */
    public int generateVoucher(Long voucherEventId, String operator);

    /**
     * 回写凭证状态
     *
     * @param voucherEventId 凭证事件id
     * @param operator 操作人
     * @return 结果
     */
    public int writebackVoucher(Long voucherEventId, String operator);

    /**
     * 冲销凭证
     *
     * @param voucherEventId 凭证事件id
     * @param operator 操作人
     * @return 结果
     */
    public int reverseVoucher(Long voucherEventId, String operator);
}
