package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinPayment;
import com.canglian.business.domain.FinPayable;
import com.canglian.business.domain.FinWriteOff;
import com.canglian.business.mapper.FinPaymentMapper;
import com.canglian.business.mapper.FinPayableMapper;
import com.canglian.business.mapper.FinWriteOffMapper;
import com.canglian.business.service.IFinPaymentService;
import com.canglian.common.exception.ServiceException;
import org.springframework.transaction.annotation.Transactional;

/**
 * 付款单 服务层实现
 * 
 * @author canglian
 */
@Service
public class FinPaymentServiceImpl implements IFinPaymentService
{
    @Autowired
    private FinPaymentMapper finPaymentMapper;

    @Autowired
    private FinPayableMapper finPayableMapper;

    @Autowired
    private FinWriteOffMapper finWriteOffMapper;

    /**
     * 查询付款单信息
     * 
     * @param paymentId 付款单id
     * @return 付款单信息
     */
    @Override
    public FinPayment selectFinPaymentById(Long paymentId)
    {
        return finPaymentMapper.selectFinPaymentById(paymentId);
    }

    /**
     * 查询付款单列表
     * 
     * @param finPayment 付款单信息
     * @return 付款单集合
     */
    @Override
    public List<FinPayment> selectFinPaymentList(FinPayment finPayment)
    {
        return finPaymentMapper.selectFinPaymentList(finPayment);
    }

    /**
     * 新增付款单
     * 
     * @param finPayment 付款单信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertFinPayment(FinPayment finPayment)
    {
        validatePaymentAmount(finPayment.getAmount());
        syncPayablePaidAmount(null, finPayment, finPayment.getCreateBy());
        return finPaymentMapper.insertFinPayment(finPayment);
    }

    /**
     * 修改付款单
     * 
     * @param finPayment 付款单信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateFinPayment(FinPayment finPayment)
    {
        FinPayment existingFinPayment = getExistingPayment(finPayment.getPaymentId());
        validatePaymentAmount(finPayment.getAmount());
        validateWriteOffImpact(existingFinPayment, finPayment);
        syncPayablePaidAmount(existingFinPayment, finPayment, finPayment.getUpdateBy());
        return finPaymentMapper.updateFinPayment(finPayment);
    }

    /**
     * 删除付款单
     * 
     * @param paymentId 付款单id
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteFinPaymentById(Long paymentId)
    {
        FinPayment finPayment = getExistingPayment(paymentId);
        checkPaymentDeletable(finPayment);
        rollbackPayablePaidAmount(finPayment, finPayment.getUpdateBy());
        return finPaymentMapper.deleteFinPaymentById(paymentId);
    }

    /**
     * 批量删除付款单
     * 
     * @param paymentIds 需要删除的付款单id
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteFinPaymentByIds(Long[] paymentIds)
    {
        for (Long paymentId : paymentIds)
        {
            FinPayment finPayment = getExistingPayment(paymentId);
            checkPaymentDeletable(finPayment);
            rollbackPayablePaidAmount(finPayment, finPayment.getUpdateBy());
        }
        return finPaymentMapper.deleteFinPaymentByIds(paymentIds);
    }

    /**
     * 获取已存在的付款单
     * 
     * @param paymentId 付款单id
     * @return 付款单信息
     */
    private FinPayment getExistingPayment(Long paymentId)
    {
        FinPayment finPayment = finPaymentMapper.selectFinPaymentById(paymentId);
        if (finPayment == null)
        {
            throw new ServiceException("付款单不存在");
        }
        return finPayment;
    }

    /**
     * 校验付款金额
     * 
     * @param paymentAmount 付款金额
     */
    private void validatePaymentAmount(BigDecimal paymentAmount)
    {
        if (paymentAmount == null || paymentAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("付款金额必须大于0");
        }
    }

    /**
     * 校验付款单修改是否影响已确认核销
     * 
     * @param existingFinPayment 已存在的付款单信息
     * @param newFinPayment 新付款单信息
     */
    private void validateWriteOffImpact(FinPayment existingFinPayment, FinPayment newFinPayment)
    {
        BigDecimal confirmedWriteOffAmount = getConfirmedWriteOffAmount(existingFinPayment.getPaymentId());
        if (confirmedWriteOffAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }
        if (!Objects.equals(existingFinPayment.getSupplierId(), newFinPayment.getSupplierId()))
        {
            throw new ServiceException("付款单已存在确认核销记录，不允许修改供应商");
        }
        if (!Objects.equals(existingFinPayment.getPayableId(), newFinPayment.getPayableId()))
        {
            throw new ServiceException("付款单已存在确认核销记录，不允许修改关联应付单");
        }
        if (defaultAmount(newFinPayment.getAmount()).compareTo(confirmedWriteOffAmount) < 0)
        {
            throw new ServiceException("付款单金额不能小于已确认核销金额");
        }
    }

    /**
     * 校验付款单是否允许删除
     * 
     * @param finPayment 付款单信息
     */
    private void checkPaymentDeletable(FinPayment finPayment)
    {
        BigDecimal confirmedWriteOffAmount = getConfirmedWriteOffAmount(finPayment.getPaymentId());
        if (confirmedWriteOffAmount.compareTo(BigDecimal.ZERO) > 0)
        {
            throw new ServiceException("付款单已存在确认核销记录，不允许删除");
        }
    }

    /**
     * 同步应付单已付金额
     * 
     * @param oldFinPayment 原付款单信息
     * @param newFinPayment 新付款单信息
     * @param operator 操作人
     */
    private void syncPayablePaidAmount(FinPayment oldFinPayment, FinPayment newFinPayment, String operator)
    {
        if (oldFinPayment != null)
        {
            rollbackPayablePaidAmount(oldFinPayment, operator);
        }
        if (newFinPayment.getPayableId() != null)
        {
            FinPayable finPayable = getExistingPayable(newFinPayment.getPayableId());
            validatePayableRelation(finPayable, newFinPayment);
            adjustPayablePaidAmount(finPayable, newFinPayment.getAmount(), operator);
        }
    }

    /**
     * 回滚应付单已付金额
     * 
     * @param finPayment 付款单信息
     * @param operator 操作人
     */
    private void rollbackPayablePaidAmount(FinPayment finPayment, String operator)
    {
        if (finPayment.getPayableId() == null)
        {
            return;
        }
        FinPayable finPayable = getExistingPayable(finPayment.getPayableId());
        adjustPayablePaidAmount(finPayable, defaultAmount(finPayment.getAmount()).negate(), operator);
    }

    /**
     * 获取已存在的应付单
     * 
     * @param payableId 应付单id
     * @return 应付单信息
     */
    private FinPayable getExistingPayable(Long payableId)
    {
        FinPayable finPayable = finPayableMapper.selectFinPayableById(payableId);
        if (finPayable == null)
        {
            throw new ServiceException("关联应付单不存在");
        }
        return finPayable;
    }

    /**
     * 校验付款单与应付单关联关系
     * 
     * @param finPayable 应付单信息
     * @param finPayment 付款单信息
     */
    private void validatePayableRelation(FinPayable finPayable, FinPayment finPayment)
    {
        if (!"1".equals(finPayable.getStatus()))
        {
            throw new ServiceException("仅已审核的应付单允许登记付款");
        }
        if (finPayment.getSupplierId() == null)
        {
            throw new ServiceException("供应商编号不能为空");
        }
        if (!Objects.equals(finPayable.getSupplierId(), finPayment.getSupplierId()))
        {
            throw new ServiceException("付款单供应商与应付单供应商不一致");
        }
    }

    /**
     * 调整应付单已付金额
     * 
     * @param finPayable 应付单信息
     * @param changeAmount 调整金额
     * @param operator 操作人
     */
    private void adjustPayablePaidAmount(FinPayable finPayable, BigDecimal changeAmount, String operator)
    {
        BigDecimal currentPaidAmount = defaultAmount(finPayable.getPaidAmount());
        BigDecimal totalPayableAmount = defaultAmount(finPayable.getAmount());
        BigDecimal updatedPaidAmount = currentPaidAmount.add(defaultAmount(changeAmount));
        if (updatedPaidAmount.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("应付单已付金额不能小于0");
        }
        if (updatedPaidAmount.compareTo(totalPayableAmount) > 0)
        {
            throw new ServiceException("付款金额不能大于应付单未付金额");
        }
        finPayable.setPaidAmount(updatedPaidAmount);
        finPayable.setUpdateBy(operator);
        finPayableMapper.updateFinPayable(finPayable);
    }

    /**
     * 查询付款单已确认核销金额
     * 
     * @param paymentId 付款单id
     * @return 已确认核销金额
     */
    private BigDecimal getConfirmedWriteOffAmount(Long paymentId)
    {
        FinWriteOff queryFinWriteOff = new FinWriteOff();
        queryFinWriteOff.setPaymentId(paymentId);
        queryFinWriteOff.setStatus("1");
        return sumWriteOffAmount(finWriteOffMapper.selectFinWriteOffList(queryFinWriteOff));
    }

    /**
     * 汇总核销金额
     * 
     * @param finWriteOffList 核销记录集合
     * @return 核销金额
     */
    private BigDecimal sumWriteOffAmount(List<FinWriteOff> finWriteOffList)
    {
        BigDecimal totalWriteOffAmount = BigDecimal.ZERO;
        for (FinWriteOff finWriteOff : finWriteOffList)
        {
            totalWriteOffAmount = totalWriteOffAmount.add(defaultAmount(finWriteOff.getAmount()));
        }
        return totalWriteOffAmount;
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
}

