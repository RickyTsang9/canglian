package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.FinPayment;

/**
 * 付款单 数据层
 * 
 * @author canglian
 */
public interface FinPaymentMapper
{
    /**
     * 查询付款单信息
     * 
     * @param paymentId 付款单id
     * @return 付款单信息
     */
    public FinPayment selectFinPaymentById(Long paymentId);

    /**
     * 查询付款单列表
     * 
     * @param finPayment 付款单信息
     * @return 付款单集合
     */
    public List<FinPayment> selectFinPaymentList(FinPayment finPayment);

    /**
     * 新增付款单
     * 
     * @param finPayment 付款单信息
     * @return 结果
     */
    public int insertFinPayment(FinPayment finPayment);

    /**
     * 修改付款单
     * 
     * @param finPayment 付款单信息
     * @return 结果
     */
    public int updateFinPayment(FinPayment finPayment);

    /**
     * 删除付款单
     * 
     * @param paymentId 付款单id
     * @return 结果
     */
    public int deleteFinPaymentById(Long paymentId);

    /**
     * 批量删除付款单
     * 
     * @param paymentIds 需要删除的付款单id
     * @return 结果
     */
    public int deleteFinPaymentByIds(Long[] paymentIds);
}

