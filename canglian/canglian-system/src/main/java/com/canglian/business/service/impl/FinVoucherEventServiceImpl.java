package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinVoucherEvent;
import com.canglian.business.mapper.FinVoucherEventMapper;
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
        finVoucherEvent.setVoucherStatus("pending");
        finVoucherEvent.setCreateBy(operator);
        finVoucherEvent.setRemark(remark);
        finVoucherEventMapper.insertFinVoucherEvent(finVoucherEvent);
    }

    /**
     * 生成凭证
     *
     * @param voucherEventId 凭证事件id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    public int generateVoucher(Long voucherEventId, String operator)
    {
        FinVoucherEvent finVoucherEvent = getExistingVoucherEvent(voucherEventId);
        if ("3".equals(finVoucherEvent.getStatus()) || "reversed".equals(finVoucherEvent.getVoucherStatus()))
        {
            throw new ServiceException("已冲销凭证事件不能重新生成凭证");
        }
        if (StringUtils.isEmpty(finVoucherEvent.getVoucherNo()))
        {
            finVoucherEvent.setVoucherNo(generateVoucherNo());
        }
        finVoucherEvent.setStatus("1");
        finVoucherEvent.setVoucherStatus("generated");
        finVoucherEvent.setVoucherDate(new Date());
        finVoucherEvent.setGeneratedBy(operator);
        finVoucherEvent.setGeneratedTime(new Date());
        finVoucherEvent.setUpdateBy(operator);
        finVoucherEvent.setRemark(appendRemark(finVoucherEvent.getRemark(), "凭证已生成"));
        return finVoucherEventMapper.updateFinVoucherEvent(finVoucherEvent);
    }

    /**
     * 回写凭证状态
     *
     * @param voucherEventId 凭证事件id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    public int writebackVoucher(Long voucherEventId, String operator)
    {
        FinVoucherEvent finVoucherEvent = getExistingVoucherEvent(voucherEventId);
        if (StringUtils.isEmpty(finVoucherEvent.getVoucherNo()))
        {
            throw new ServiceException("请先生成凭证后再回写");
        }
        if ("3".equals(finVoucherEvent.getStatus()) || "reversed".equals(finVoucherEvent.getVoucherStatus()))
        {
            throw new ServiceException("已冲销凭证不能回写");
        }
        finVoucherEvent.setStatus("2");
        finVoucherEvent.setVoucherStatus("written_back");
        finVoucherEvent.setWritebackStatus("success");
        finVoucherEvent.setWritebackMessage("业务单据已回写凭证号：" + finVoucherEvent.getVoucherNo());
        finVoucherEvent.setUpdateBy(operator);
        finVoucherEvent.setRemark(appendRemark(finVoucherEvent.getRemark(), "凭证已回写业务单据"));
        return finVoucherEventMapper.updateFinVoucherEvent(finVoucherEvent);
    }

    /**
     * 冲销凭证
     *
     * @param voucherEventId 凭证事件id
     * @param operator 操作人
     * @return 结果
     */
    @Override
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
        finVoucherEvent.setStatus("3");
        finVoucherEvent.setVoucherStatus("reversed");
        finVoucherEvent.setReverseVoucherNo(generateReverseVoucherNo());
        finVoucherEvent.setReversedBy(operator);
        finVoucherEvent.setReversedTime(new Date());
        finVoucherEvent.setUpdateBy(operator);
        finVoucherEvent.setRemark(appendRemark(finVoucherEvent.getRemark(), "凭证已冲销"));
        return finVoucherEventMapper.updateFinVoucherEvent(finVoucherEvent);
    }

    /**
     * 查询已存在凭证事件
     *
     * @param voucherEventId 凭证事件id
     * @return 凭证事件
     */
    private FinVoucherEvent getExistingVoucherEvent(Long voucherEventId)
    {
        FinVoucherEvent finVoucherEvent = finVoucherEventMapper.selectFinVoucherEventById(voucherEventId);
        if (finVoucherEvent == null)
        {
            throw new ServiceException("凭证事件不存在");
        }
        return finVoucherEvent;
    }

    /**
     * 生成凭证号
     *
     * @return 凭证号
     */
    private String generateVoucherNo()
    {
        return generateVoucherNoByPrefix("vch" + LocalDate.now().format(VOUCHER_DATE_FORMATTER));
    }

    /**
     * 生成冲销凭证号
     *
     * @return 冲销凭证号
     */
    private String generateReverseVoucherNo()
    {
        return generateVoucherNoByPrefix("rvch" + LocalDate.now().format(VOUCHER_DATE_FORMATTER));
    }

    /**
     * 按前缀生成凭证号
     *
     * @param voucherNoPrefix 凭证号前缀
     * @return 凭证号
     */
    private String generateVoucherNoByPrefix(String voucherNoPrefix)
    {
        String currentMaxVoucherNo = finVoucherEventMapper.selectMaxVoucherNoByPrefix(voucherNoPrefix);
        int nextSequence = 1;
        if (StringUtils.isNotEmpty(currentMaxVoucherNo) && currentMaxVoucherNo.length() > voucherNoPrefix.length())
        {
            String sequenceText = currentMaxVoucherNo.substring(voucherNoPrefix.length());
            if (StringUtils.isNumeric(sequenceText))
            {
                nextSequence = Integer.parseInt(sequenceText) + 1;
            }
        }
        return voucherNoPrefix + String.format("%03d", nextSequence);
    }

    /**
     * 追加备注
     *
     * @param originalRemark 原备注
     * @param newRemark 新备注
     * @return 合并备注
     */
    private String appendRemark(String originalRemark, String newRemark)
    {
        if (StringUtils.isEmpty(originalRemark))
        {
            return newRemark;
        }
        return originalRemark + "；" + newRemark;
    }
}
