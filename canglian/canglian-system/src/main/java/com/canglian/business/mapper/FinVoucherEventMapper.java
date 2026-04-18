package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.FinVoucherEvent;

/**
 * 凭证事件数据层
 * 
 * @author canglian
 */
public interface FinVoucherEventMapper
{
    public FinVoucherEvent selectFinVoucherEventById(Long voucherEventId);

    public List<FinVoucherEvent> selectFinVoucherEventList(FinVoucherEvent finVoucherEvent);

    public int insertFinVoucherEvent(FinVoucherEvent finVoucherEvent);

    public int updateFinVoucherEvent(FinVoucherEvent finVoucherEvent);

    public int deleteFinVoucherEventById(Long voucherEventId);

    public int deleteFinVoucherEventByIds(Long[] voucherEventIds);
}
