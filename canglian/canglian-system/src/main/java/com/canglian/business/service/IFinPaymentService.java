package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.FinPayment;

/**
 * 付款单 服务层
 *
 * @author canglian
 */
public interface IFinPaymentService
{
    public FinPayment selectFinPaymentById(Long paymentId);

    public List<FinPayment> selectFinPaymentList(FinPayment finPayment);

    public int insertFinPayment(FinPayment finPayment);

    public int updateFinPayment(FinPayment finPayment);

    /** 确认付款单（记入资金流水、同步应付已付、触发凭证事件） */
    public int confirmFinPayment(Long paymentId, String operator);

    /** 反确认付款单（开放账期且无后续核销/未生成凭证，逆向冲回资金与应付并恢复草稿） */
    public int unconfirmFinPayment(Long paymentId, String operator);

    public int deleteFinPaymentById(Long paymentId);

    public int deleteFinPaymentByIds(Long[] paymentIds);
}
