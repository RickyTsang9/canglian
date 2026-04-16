package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinPayable;
import com.canglian.business.mapper.FinPayableMapper;
import com.canglian.business.service.IFinPayableService;
import com.canglian.common.exception.ServiceException;

/**
 * 应付单 服务层实现
 * 
 * @author canglian
 */
@Service
public class FinPayableServiceImpl implements IFinPayableService
{
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
        finPayable.setStatus("0");
        finPayable.setPaidAmount(BigDecimal.ZERO);
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
        finPayable.setStatus(existingFinPayable.getStatus());
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
        if ("1".equals(finPayable.getStatus()))
        {
            throw new ServiceException("应付单已审核，请勿重复操作");
        }
        validatePayableAmount(finPayable);
        finPayable.setStatus("1");
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
        if ("1".equals(finPayable.getStatus()))
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
        if ("1".equals(finPayable.getStatus()))
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
}

