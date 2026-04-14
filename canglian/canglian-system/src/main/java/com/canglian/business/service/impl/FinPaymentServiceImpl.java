package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinPayment;
import com.canglian.business.mapper.FinPaymentMapper;
import com.canglian.business.service.IFinPaymentService;

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
    public int insertFinPayment(FinPayment finPayment)
    {
        return finPaymentMapper.insertFinPayment(finPayment);
    }

    /**
     * 修改付款单
     * 
     * @param finPayment 付款单信息
     * @return 结果
     */
    @Override
    public int updateFinPayment(FinPayment finPayment)
    {
        return finPaymentMapper.updateFinPayment(finPayment);
    }

    /**
     * 删除付款单
     * 
     * @param paymentId 付款单id
     * @return 结果
     */
    @Override
    public int deleteFinPaymentById(Long paymentId)
    {
        return finPaymentMapper.deleteFinPaymentById(paymentId);
    }

    /**
     * 批量删除付款单
     * 
     * @param paymentIds 需要删除的付款单id
     * @return 结果
     */
    @Override
    public int deleteFinPaymentByIds(Long[] paymentIds)
    {
        return finPaymentMapper.deleteFinPaymentByIds(paymentIds);
    }
}

