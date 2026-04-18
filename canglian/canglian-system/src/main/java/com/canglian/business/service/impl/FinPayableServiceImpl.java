package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinPayable;
import com.canglian.business.mapper.FinPayableMapper;
import com.canglian.business.service.IFinPayableService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 应付单 服务层实现
 * 
 * @author canglian
 */
@Service
public class FinPayableServiceImpl implements IFinPayableService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private FinPayableMapper finPayableMapper;

    /**
     * 查询应付单信息
     * 
     * @param payableId 应付单id
     * @return 应付单信息
     */
    @Override
    public FinPayable selectFinPayableById(Long payableId)
    {
        return finPayableMapper.selectFinPayableById(payableId);
    }

    /**
     * 查询应付单列表
     * 
     * @param finPayable 应付单信息
     * @return 应付单集合
     */
    @Override
    public List<FinPayable> selectFinPayableList(FinPayable finPayable)
    {
        return finPayableMapper.selectFinPayableList(finPayable);
    }

    /**
     * 新增应付单
     * 
     * @param finPayable 应付单信息
     * @return 结果
     */
    @Override
    public int insertFinPayable(FinPayable finPayable)
    {
        if (StringUtils.isEmpty(finPayable.getPayableNo()))
        {
            finPayable.setPayableNo(generatePayableNo());
        }
        finPayable.setPaidAmount(BigDecimal.ZERO);
        finPayable.setStatus(calculatePaymentStatus(finPayable.getPaidAmount(), finPayable.getAmount()));
        finPayable.setBizStatus("draft");
        validatePayableAmount(finPayable);
        return finPayableMapper.insertFinPayable(finPayable);
    }

    /**
     * 修改应付单
     * 
     * @param finPayable 应付单信息
     * @return 结果
     */
    @Override
    public int updateFinPayable(FinPayable finPayable)
    {
        FinPayable existingFinPayable = checkPayableEditable(finPayable.getPayableId());
        finPayable.setPayableNo(existingFinPayable.getPayableNo());
        finPayable.setStatus(existingFinPayable.getStatus());
        finPayable.setBizStatus(existingFinPayable.getBizStatus());
        finPayable.setPaidAmount(existingFinPayable.getPaidAmount());
        validatePayableAmount(finPayable);
        return finPayableMapper.updateFinPayable(finPayable);
    }

    /**
     * 删除应付单
     * 
     * @param payableId 应付单id
     * @return 结果
     */
    @Override
    public int deleteFinPayableById(Long payableId)
    {
        checkPayableDeletable(payableId);
        return finPayableMapper.deleteFinPayableById(payableId);
    }

    /**
     * 批量删除应付单
     * 
     * @param payableIds 需要删除的应付单id
     * @return 结果
     */
    @Override
    public int deleteFinPayableByIds(Long[] payableIds)
    {
        for (Long payableId : payableIds)
        {
            checkPayableDeletable(payableId);
        }
        return finPayableMapper.deleteFinPayableByIds(payableIds);
    }

    /**
     * 审核应付单
     * 
     * @param payableId 应付单id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    public int auditFinPayable(Long payableId, String operator)
    {
        FinPayable finPayable = getExistingFinPayable(payableId);
        if ("confirmed".equals(finPayable.getBizStatus()))
        {
            throw new ServiceException("应付单已审核，请勿重复操作");
        }
        validatePayableAmount(finPayable);
        finPayable.setStatus(calculatePaymentStatus(finPayable.getPaidAmount(), finPayable.getAmount()));
        finPayable.setBizStatus("confirmed");
        finPayable.setUpdateBy(operator);
        return finPayableMapper.updateFinPayable(finPayable);
    }

    /**
     * 校验应付金额
     * 
     * @param finPayable 应付单信息
     */
    private void validatePayableAmount(FinPayable finPayable)
    {
        if (finPayable.getAmount() == null || finPayable.getAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("应付金额必须大于0");
        }
        if (defaultAmount(finPayable.getPaidAmount()).compareTo(finPayable.getAmount()) > 0)
        {
            throw new ServiceException("应付单已付金额不能大于应付金额");
        }
    }

    /**
     * 获取存在的应付单
     * 
     * @param payableId 应付单id
     * @return 应付单信息
     */
    private FinPayable getExistingFinPayable(Long payableId)
    {
        FinPayable finPayable = finPayableMapper.selectFinPayableById(payableId);
        if (finPayable == null)
        {
            throw new ServiceException("应付单不存在");
        }
        return finPayable;
    }

    /**
     * 校验应付单是否允许修改
     * 
     * @param payableId 应付单id
     * @return 应付单信息
     */
    private FinPayable checkPayableEditable(Long payableId)
    {
        FinPayable finPayable = getExistingFinPayable(payableId);
        if ("confirmed".equals(finPayable.getBizStatus()))
        {
            throw new ServiceException("应付单已审核，无法修改");
        }
        return finPayable;
    }

    /**
     * 校验应付单是否允许删除
     * 
     * @param payableId 应付单id
     */
    private void checkPayableDeletable(Long payableId)
    {
        FinPayable finPayable = getExistingFinPayable(payableId);
        if ("confirmed".equals(finPayable.getBizStatus()))
        {
            throw new ServiceException("应付单已审核，无法删除");
        }
        if (defaultAmount(finPayable.getPaidAmount()).compareTo(BigDecimal.ZERO) > 0)
        {
            throw new ServiceException("应付单已存在付款记录，无法删除");
        }
    }

    /**
     * 空金额转默认值
     * 
     * @param amount 金额
     * @return 默认金额
     */
    private BigDecimal defaultAmount(BigDecimal amount)
    {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    /**
     * 生成应付单号
     * 
     * @return 应付单号
     */
    private String generatePayableNo()
    {
        String payableNoPrefix = "ap" + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String currentMaxPayableNo = finPayableMapper.selectMaxPayableNoByPrefix(payableNoPrefix);
        int nextSequence = 1;
        if (StringUtils.isNotEmpty(currentMaxPayableNo) && currentMaxPayableNo.length() > payableNoPrefix.length())
        {
            String sequenceText = currentMaxPayableNo.substring(payableNoPrefix.length());
            if (StringUtils.isNumeric(sequenceText))
            {
                nextSequence = Integer.parseInt(sequenceText) + 1;
            }
        }
        return payableNoPrefix + String.format("%03d", nextSequence);
    }

    /**
     * 计算付款状态
     * 
     * @param paidAmount 已付金额
     * @param payableAmount 应付金额
     * @return 付款状态
     */
    private String calculatePaymentStatus(BigDecimal paidAmount, BigDecimal payableAmount)
    {
        BigDecimal paidAmountValue = defaultAmount(paidAmount);
        BigDecimal payableAmountValue = defaultAmount(payableAmount);
        if (paidAmountValue.compareTo(BigDecimal.ZERO) <= 0)
        {
            return "0";
        }
        if (paidAmountValue.compareTo(payableAmountValue) >= 0)
        {
            return "2";
        }
        return "1";
    }
}

