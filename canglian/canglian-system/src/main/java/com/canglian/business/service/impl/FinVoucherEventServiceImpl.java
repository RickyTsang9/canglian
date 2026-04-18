package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinVoucherEvent;
import com.canglian.business.mapper.FinVoucherEventMapper;
import com.canglian.business.service.IFinVoucherEventService;

/**
 * 凭证事件服务实现
 * 
 * @author canglian
 */
@Service
public class FinVoucherEventServiceImpl implements IFinVoucherEventService
{
    @Autowired
    private FinVoucherEventMapper finVoucherEventMapper;

    /**
     * 查询凭证事件
     * 
     * @param voucherEventId 凭证事件id
     * @return 凭证事件
     */
    @Override
    public FinVoucherEvent selectFinVoucherEventById(Long voucherEventId)
    {
        return finVoucherEventMapper.selectFinVoucherEventById(voucherEventId);
    }

    /**
     * 查询凭证事件列表
     * 
     * @param finVoucherEvent 凭证事件
     * @return 凭证事件集合
     */
    @Override
    public List<FinVoucherEvent> selectFinVoucherEventList(FinVoucherEvent finVoucherEvent)
    {
        return finVoucherEventMapper.selectFinVoucherEventList(finVoucherEvent);
    }

    /**
     * 新增凭证事件
     * 
     * @param finVoucherEvent 凭证事件
     * @return 结果
     */
    @Override
    public int insertFinVoucherEvent(FinVoucherEvent finVoucherEvent)
    {
        return finVoucherEventMapper.insertFinVoucherEvent(finVoucherEvent);
    }

    /**
     * 修改凭证事件
     * 
     * @param finVoucherEvent 凭证事件
     * @return 结果
     */
    @Override
    public int updateFinVoucherEvent(FinVoucherEvent finVoucherEvent)
    {
        return finVoucherEventMapper.updateFinVoucherEvent(finVoucherEvent);
    }

    /**
     * 删除凭证事件
     * 
     * @param voucherEventId 凭证事件id
     * @return 结果
     */
    @Override
    public int deleteFinVoucherEventById(Long voucherEventId)
    {
        return finVoucherEventMapper.deleteFinVoucherEventById(voucherEventId);
    }

    /**
     * 批量删除凭证事件
     * 
     * @param voucherEventIds 凭证事件id集合
     * @return 结果
     */
    @Override
    public int deleteFinVoucherEventByIds(Long[] voucherEventIds)
    {
        return finVoucherEventMapper.deleteFinVoucherEventByIds(voucherEventIds);
    }

    /**
     * 记录凭证事件
     * 
     * @param billType 单据类型
     * @param billId 单据id
     * @param billNo 单据号
     * @param eventType 事件类型
     * @param eventDate 事件日期
     * @param eventAmount 事件金额
     * @param operator 操作人
     * @param remark 备注
     */
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
        finVoucherEvent.setCreateBy(operator);
        finVoucherEvent.setRemark(remark);
        finVoucherEventMapper.insertFinVoucherEvent(finVoucherEvent);
    }
}
