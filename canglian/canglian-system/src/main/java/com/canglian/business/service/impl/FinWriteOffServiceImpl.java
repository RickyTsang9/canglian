package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinPayable;
import com.canglian.business.domain.FinPayment;
import com.canglian.business.domain.FinReceipt;
import com.canglian.business.domain.FinReceivable;
import com.canglian.business.domain.FinWriteOff;
import com.canglian.business.mapper.FinPayableMapper;
import com.canglian.business.mapper.FinPaymentMapper;
import com.canglian.business.mapper.FinReceiptMapper;
import com.canglian.business.mapper.FinReceivableMapper;
import com.canglian.business.mapper.FinWriteOffMapper;
import com.canglian.business.service.IFinWriteOffService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 核销 服务层实现
 * 
 * @author canglian
 */
@Service
public class FinWriteOffServiceImpl implements IFinWriteOffService
{
    @Autowired
    private FinWriteOffMapper finWriteOffMapper;

    @Autowired
    private FinReceivableMapper finReceivableMapper;

    @Autowired
    private FinReceiptMapper finReceiptMapper;

    @Autowired
    private FinPayableMapper finPayableMapper;

    @Autowired
    private FinPaymentMapper finPaymentMapper;

    /**
     * 查询核销信息
     * 
     * @param writeOffId 核销id
     * @return 核销信息
     */
    @Override
    public FinWriteOff selectFinWriteOffById(Long writeOffId)
    {
        return finWriteOffMapper.selectFinWriteOffById(writeOffId);
    }

    /**
     * 查询核销列表
     * 
     * @param finWriteOff 核销信息
     * @return 核销集合
     */
    @Override
    public List<FinWriteOff> selectFinWriteOffList(FinWriteOff finWriteOff)
    {
        return finWriteOffMapper.selectFinWriteOffList(finWriteOff);
    }

    /**
     * 新增核销
     * 
     * @param finWriteOff 核销信息
     * @return 结果
     */
    @Override
    public int insertFinWriteOff(FinWriteOff finWriteOff)
    {
        validateWriteOff(finWriteOff);
        finWriteOff.setStatus("0");
        return finWriteOffMapper.insertFinWriteOff(finWriteOff);
    }

    /**
     * 修改核销
     * 
     * @param finWriteOff 核销信息
     * @return 结果
     */
    @Override
    public int updateFinWriteOff(FinWriteOff finWriteOff)
    {
        FinWriteOff existingFinWriteOff = checkWriteOffEditable(finWriteOff.getWriteOffId());
        validateWriteOff(finWriteOff);
        finWriteOff.setStatus(existingFinWriteOff.getStatus());
        return finWriteOffMapper.updateFinWriteOff(finWriteOff);
    }

    /**
     * 确认核销
     * 
     * @param writeOffId 核销id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    public int confirmFinWriteOff(Long writeOffId, String operator)
    {
        FinWriteOff finWriteOff = getExistingWriteOff(writeOffId);
        if ("1".equals(finWriteOff.getStatus()))
        {
            throw new ServiceException("核销单已确认，请勿重复操作");
        }
        validateWriteOff(finWriteOff);
        finWriteOff.setStatus("1");
        finWriteOff.setUpdateBy(operator);
        return finWriteOffMapper.updateFinWriteOff(finWriteOff);
    }

    /**
     * 删除核销
     * 
     * @param writeOffId 核销id
     * @return 结果
     */
    @Override
    public int deleteFinWriteOffById(Long writeOffId)
    {
        checkWriteOffEditable(writeOffId);
        return finWriteOffMapper.deleteFinWriteOffById(writeOffId);
    }

    /**
     * 批量删除核销
     * 
     * @param writeOffIds 需要删除的核销id
     * @return 结果
     */
    @Override
    public int deleteFinWriteOffByIds(Long[] writeOffIds)
    {
        for (Long writeOffId : writeOffIds)
        {
            checkWriteOffEditable(writeOffId);
        }
        return finWriteOffMapper.deleteFinWriteOffByIds(writeOffIds);
    }

    /**
     * 获取已存在的核销单
     * 
     * @param writeOffId 核销id
     * @return 核销单信息
     */
    private FinWriteOff getExistingWriteOff(Long writeOffId)
    {
        FinWriteOff finWriteOff = finWriteOffMapper.selectFinWriteOffById(writeOffId);
        if (finWriteOff == null)
        {
            throw new ServiceException("核销单不存在");
        }
        return finWriteOff;
    }

    /**
     * 校验核销单是否允许编辑
     * 
     * @param writeOffId 核销id
     * @return 核销单信息
     */
    private FinWriteOff checkWriteOffEditable(Long writeOffId)
    {
        FinWriteOff finWriteOff = getExistingWriteOff(writeOffId);
        if ("1".equals(finWriteOff.getStatus()))
        {
            throw new ServiceException("核销单已确认，不允许修改或删除");
        }
        return finWriteOff;
    }

    /**
     * 校验核销单信息
     * 
     * @param finWriteOff 核销单信息
     */
    private void validateWriteOff(FinWriteOff finWriteOff)
    {
        if (StringUtils.isEmpty(finWriteOff.getWriteOffType()))
        {
            throw new ServiceException("核销类型不能为空");
        }
        if (finWriteOff.getAmount() == null || finWriteOff.getAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("核销金额必须大于0");
        }
        if ("receivable".equals(finWriteOff.getWriteOffType()))
        {
            validateReceivableWriteOff(finWriteOff);
            return;
        }
        if ("payable".equals(finWriteOff.getWriteOffType()))
        {
            validatePayableWriteOff(finWriteOff);
            return;
        }
        throw new ServiceException("核销类型仅支持receivable或payable");
    }

    /**
     * 校验应收核销信息
     * 
     * @param finWriteOff 核销单信息
     */
    private void validateReceivableWriteOff(FinWriteOff finWriteOff)
    {
        if (finWriteOff.getReceivableId() == null)
        {
            throw new ServiceException("应收核销必须选择应收单");
        }
        if (finWriteOff.getReceiptId() == null)
        {
            throw new ServiceException("应收核销必须选择收款单");
        }
        if (finWriteOff.getPayableId() != null || finWriteOff.getPaymentId() != null)
        {
            throw new ServiceException("应收核销不能关联应付单或付款单");
        }
        FinReceivable finReceivable = getExistingReceivable(finWriteOff.getReceivableId());
        if (!"1".equals(finReceivable.getStatus()))
        {
            throw new ServiceException("仅已审核的应收单允许核销");
        }
        FinReceipt finReceipt = getExistingReceipt(finWriteOff.getReceiptId());
        if (finReceipt.getAmount() == null || finReceipt.getAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("应收核销仅允许关联正向收款单");
        }
        if (!Objects.equals(finReceivable.getCustomerId(), finReceipt.getCustomerId()))
        {
            throw new ServiceException("核销单关联的应收单与收款单客户不一致");
        }
        if (finReceipt.getReceivableId() != null && !Objects.equals(finReceipt.getReceivableId(), finWriteOff.getReceivableId()))
        {
            throw new ServiceException("收款单关联的应收单与当前核销单不一致");
        }
        if (finWriteOff.getAmount().compareTo(finReceipt.getAmount()) > 0)
        {
            throw new ServiceException("核销金额不能大于收款单金额");
        }
        if (finWriteOff.getAmount().compareTo(defaultAmount(finReceivable.getReceivedAmount())) > 0)
        {
            throw new ServiceException("核销金额不能大于应收单已收金额");
        }
        validateReceivableWriteOffAmount(finWriteOff);
    }

    /**
     * 校验应付核销信息
     * 
     * @param finWriteOff 核销单信息
     */
    private void validatePayableWriteOff(FinWriteOff finWriteOff)
    {
        if (finWriteOff.getPayableId() == null)
        {
            throw new ServiceException("应付核销必须选择应付单");
        }
        if (finWriteOff.getPaymentId() == null)
        {
            throw new ServiceException("应付核销必须选择付款单");
        }
        if (finWriteOff.getReceivableId() != null || finWriteOff.getReceiptId() != null)
        {
            throw new ServiceException("应付核销不能关联应收单或收款单");
        }
        FinPayable finPayable = getExistingPayable(finWriteOff.getPayableId());
        if (!"1".equals(finPayable.getStatus()))
        {
            throw new ServiceException("仅已审核的应付单允许核销");
        }
        FinPayment finPayment = getExistingPayment(finWriteOff.getPaymentId());
        if (finPayment.getAmount() == null || finPayment.getAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("应付核销仅允许关联正向付款单");
        }
        if (!Objects.equals(finPayable.getSupplierId(), finPayment.getSupplierId()))
        {
            throw new ServiceException("核销单关联的应付单与付款单供应商不一致");
        }
        if (finPayment.getPayableId() != null && !Objects.equals(finPayment.getPayableId(), finWriteOff.getPayableId()))
        {
            throw new ServiceException("付款单关联的应付单与当前核销单不一致");
        }
        if (finWriteOff.getAmount().compareTo(finPayment.getAmount()) > 0)
        {
            throw new ServiceException("核销金额不能大于付款单金额");
        }
        if (finWriteOff.getAmount().compareTo(defaultAmount(finPayable.getPaidAmount())) > 0)
        {
            throw new ServiceException("核销金额不能大于应付单已付金额");
        }
        validatePayableWriteOffAmount(finWriteOff);
    }

    /**
     * 校验应收核销累计金额
     * 
     * @param finWriteOff 核销单信息
     */
    private void validateReceivableWriteOffAmount(FinWriteOff finWriteOff)
    {
        BigDecimal confirmedReceiptWriteOffAmount = sumWriteOffAmount(listConfirmedWriteOffByReceipt(finWriteOff.getReceiptId()));
        BigDecimal confirmedReceivableWriteOffAmount = sumWriteOffAmount(listConfirmedWriteOffByReceivable(finWriteOff.getReceivableId()));
        BigDecimal currentWriteOffAmount = finWriteOff.getAmount();
        FinReceipt finReceipt = getExistingReceipt(finWriteOff.getReceiptId());
        FinReceivable finReceivable = getExistingReceivable(finWriteOff.getReceivableId());
        if (confirmedReceiptWriteOffAmount.add(currentWriteOffAmount).compareTo(finReceipt.getAmount()) > 0)
        {
            throw new ServiceException("当前收款单可核销金额不足");
        }
        if (confirmedReceivableWriteOffAmount.add(currentWriteOffAmount).compareTo(defaultAmount(finReceivable.getReceivedAmount())) > 0)
        {
            throw new ServiceException("当前应收单可核销金额不足");
        }
    }

    /**
     * 校验应付核销累计金额
     * 
     * @param finWriteOff 核销单信息
     */
    private void validatePayableWriteOffAmount(FinWriteOff finWriteOff)
    {
        BigDecimal confirmedPaymentWriteOffAmount = sumWriteOffAmount(listConfirmedWriteOffByPayment(finWriteOff.getPaymentId()));
        BigDecimal confirmedPayableWriteOffAmount = sumWriteOffAmount(listConfirmedWriteOffByPayable(finWriteOff.getPayableId()));
        BigDecimal currentWriteOffAmount = finWriteOff.getAmount();
        FinPayment finPayment = getExistingPayment(finWriteOff.getPaymentId());
        FinPayable finPayable = getExistingPayable(finWriteOff.getPayableId());
        if (confirmedPaymentWriteOffAmount.add(currentWriteOffAmount).compareTo(finPayment.getAmount()) > 0)
        {
            throw new ServiceException("当前付款单可核销金额不足");
        }
        if (confirmedPayableWriteOffAmount.add(currentWriteOffAmount).compareTo(defaultAmount(finPayable.getPaidAmount())) > 0)
        {
            throw new ServiceException("当前应付单可核销金额不足");
        }
    }

    /**
     * 查询指定收款单已确认核销记录
     * 
     * @param receiptId 收款单id
     * @return 核销记录集合
     */
    private List<FinWriteOff> listConfirmedWriteOffByReceipt(Long receiptId)
    {
        FinWriteOff queryFinWriteOff = new FinWriteOff();
        queryFinWriteOff.setReceiptId(receiptId);
        queryFinWriteOff.setStatus("1");
        return finWriteOffMapper.selectFinWriteOffList(queryFinWriteOff);
    }

    /**
     * 查询指定应收单已确认核销记录
     * 
     * @param receivableId 应收单id
     * @return 核销记录集合
     */
    private List<FinWriteOff> listConfirmedWriteOffByReceivable(Long receivableId)
    {
        FinWriteOff queryFinWriteOff = new FinWriteOff();
        queryFinWriteOff.setReceivableId(receivableId);
        queryFinWriteOff.setStatus("1");
        return finWriteOffMapper.selectFinWriteOffList(queryFinWriteOff);
    }

    /**
     * 查询指定付款单已确认核销记录
     * 
     * @param paymentId 付款单id
     * @return 核销记录集合
     */
    private List<FinWriteOff> listConfirmedWriteOffByPayment(Long paymentId)
    {
        FinWriteOff queryFinWriteOff = new FinWriteOff();
        queryFinWriteOff.setPaymentId(paymentId);
        queryFinWriteOff.setStatus("1");
        return finWriteOffMapper.selectFinWriteOffList(queryFinWriteOff);
    }

    /**
     * 查询指定应付单已确认核销记录
     * 
     * @param payableId 应付单id
     * @return 核销记录集合
     */
    private List<FinWriteOff> listConfirmedWriteOffByPayable(Long payableId)
    {
        FinWriteOff queryFinWriteOff = new FinWriteOff();
        queryFinWriteOff.setPayableId(payableId);
        queryFinWriteOff.setStatus("1");
        return finWriteOffMapper.selectFinWriteOffList(queryFinWriteOff);
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
     * 获取已存在的应收单
     * 
     * @param receivableId 应收单id
     * @return 应收单信息
     */
    private FinReceivable getExistingReceivable(Long receivableId)
    {
        FinReceivable finReceivable = finReceivableMapper.selectFinReceivableById(receivableId);
        if (finReceivable == null)
        {
            throw new ServiceException("关联应收单不存在");
        }
        return finReceivable;
    }

    /**
     * 获取已存在的收款单
     * 
     * @param receiptId 收款单id
     * @return 收款单信息
     */
    private FinReceipt getExistingReceipt(Long receiptId)
    {
        FinReceipt finReceipt = finReceiptMapper.selectFinReceiptById(receiptId);
        if (finReceipt == null)
        {
            throw new ServiceException("关联收款单不存在");
        }
        return finReceipt;
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
            throw new ServiceException("关联付款单不存在");
        }
        return finPayment;
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

