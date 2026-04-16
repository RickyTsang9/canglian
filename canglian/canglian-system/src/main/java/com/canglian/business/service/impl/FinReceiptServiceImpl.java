package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinReceipt;
import com.canglian.business.domain.FinReceivable;
import com.canglian.business.domain.FinWriteOff;
import com.canglian.business.mapper.FinReceiptMapper;
import com.canglian.business.mapper.FinReceivableMapper;
import com.canglian.business.mapper.FinWriteOffMapper;
import com.canglian.business.service.IFinReceiptService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * 收款单 服务层实现
 * 
 * @author canglian
 */
@Service
public class FinReceiptServiceImpl implements IFinReceiptService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private FinReceiptMapper finReceiptMapper;

    @Autowired
    private FinReceivableMapper finReceivableMapper;

    @Autowired
    private FinWriteOffMapper finWriteOffMapper;

    /**
     * 查询收款单信息
     * 
     * @param receiptId 收款单id
     * @return 收款单信息
     */
    @Override
    public FinReceipt selectFinReceiptById(Long receiptId)
    {
        return finReceiptMapper.selectFinReceiptById(receiptId);
    }

    /**
     * 查询收款单列表
     * 
     * @param finReceipt 收款单信息
     * @return 收款单集合
     */
    @Override
    public List<FinReceipt> selectFinReceiptList(FinReceipt finReceipt)
    {
        return finReceiptMapper.selectFinReceiptList(finReceipt);
    }

    /**
     * 新增收款单
     * 
     * @param finReceipt 收款单信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertFinReceipt(FinReceipt finReceipt)
    {
        if (StringUtils.isEmpty(finReceipt.getReceiptNo()))
        {
            finReceipt.setReceiptNo(generateReceiptNo());
        }
        validateReceiptAmount(finReceipt.getAmount());
        syncReceivableReceivedAmount(null, finReceipt, finReceipt.getCreateBy());
        return finReceiptMapper.insertFinReceipt(finReceipt);
    }

    /**
     * 修改收款单
     * 
     * @param finReceipt 收款单信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateFinReceipt(FinReceipt finReceipt)
    {
        FinReceipt existingFinReceipt = getExistingReceipt(finReceipt.getReceiptId());
        validateReceiptAmount(finReceipt.getAmount());
        validateWriteOffImpact(existingFinReceipt, finReceipt);
        syncReceivableReceivedAmount(existingFinReceipt, finReceipt, finReceipt.getUpdateBy());
        return finReceiptMapper.updateFinReceipt(finReceipt);
    }

    /**
     * 删除收款单
     * 
     * @param receiptId 收款单id
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteFinReceiptById(Long receiptId)
    {
        FinReceipt finReceipt = getExistingReceipt(receiptId);
        checkReceiptDeletable(finReceipt);
        rollbackReceivableReceivedAmount(finReceipt, finReceipt.getUpdateBy());
        return finReceiptMapper.deleteFinReceiptById(receiptId);
    }

    /**
     * 批量删除收款单
     * 
     * @param receiptIds 需要删除的收款单id
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteFinReceiptByIds(Long[] receiptIds)
    {
        for (Long receiptId : receiptIds)
        {
            FinReceipt finReceipt = getExistingReceipt(receiptId);
            checkReceiptDeletable(finReceipt);
            rollbackReceivableReceivedAmount(finReceipt, finReceipt.getUpdateBy());
        }
        return finReceiptMapper.deleteFinReceiptByIds(receiptIds);
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
            throw new ServiceException("收款单不存在");
        }
        return finReceipt;
    }

    /**
     * 校验收款金额
     * 
     * @param receiptAmount 收款金额
     */
    private void validateReceiptAmount(BigDecimal receiptAmount)
    {
        if (receiptAmount == null || receiptAmount.compareTo(BigDecimal.ZERO) == 0)
        {
            throw new ServiceException("收款金额不能为0");
        }
    }

    /**
     * 校验收款单修改是否影响已确认核销
     * 
     * @param existingFinReceipt 已存在的收款单信息
     * @param newFinReceipt 新收款单信息
     */
    private void validateWriteOffImpact(FinReceipt existingFinReceipt, FinReceipt newFinReceipt)
    {
        BigDecimal confirmedWriteOffAmount = getConfirmedWriteOffAmount(existingFinReceipt.getReceiptId());
        if (confirmedWriteOffAmount.compareTo(BigDecimal.ZERO) <= 0)
        {
            return;
        }
        if (!Objects.equals(existingFinReceipt.getCustomerId(), newFinReceipt.getCustomerId()))
        {
            throw new ServiceException("收款单已存在确认核销记录，不允许修改客户");
        }
        if (!Objects.equals(existingFinReceipt.getReceivableId(), newFinReceipt.getReceivableId()))
        {
            throw new ServiceException("收款单已存在确认核销记录，不允许修改关联应收单");
        }
        if (defaultAmount(newFinReceipt.getAmount()).compareTo(confirmedWriteOffAmount) < 0)
        {
            throw new ServiceException("收款单金额不能小于已确认核销金额");
        }
    }

    /**
     * 校验收款单是否允许删除
     * 
     * @param finReceipt 收款单信息
     */
    private void checkReceiptDeletable(FinReceipt finReceipt)
    {
        BigDecimal confirmedWriteOffAmount = getConfirmedWriteOffAmount(finReceipt.getReceiptId());
        if (confirmedWriteOffAmount.compareTo(BigDecimal.ZERO) > 0)
        {
            throw new ServiceException("收款单已存在确认核销记录，不允许删除");
        }
    }

    /**
     * 同步应收单已收金额
     * 
     * @param oldFinReceipt 原收款单信息
     * @param newFinReceipt 新收款单信息
     * @param operator 操作人
     */
    private void syncReceivableReceivedAmount(FinReceipt oldFinReceipt, FinReceipt newFinReceipt, String operator)
    {
        if (oldFinReceipt != null)
        {
            rollbackReceivableReceivedAmount(oldFinReceipt, operator);
        }
        if (newFinReceipt.getReceivableId() != null)
        {
            FinReceivable finReceivable = getExistingReceivable(newFinReceipt.getReceivableId());
            validateReceivableRelation(finReceivable, newFinReceipt);
            adjustReceivableReceivedAmount(finReceivable, newFinReceipt.getAmount(), operator);
        }
    }

    /**
     * 回滚应收单已收金额
     * 
     * @param finReceipt 收款单信息
     * @param operator 操作人
     */
    private void rollbackReceivableReceivedAmount(FinReceipt finReceipt, String operator)
    {
        if (finReceipt.getReceivableId() == null)
        {
            return;
        }
        FinReceivable finReceivable = getExistingReceivable(finReceipt.getReceivableId());
        adjustReceivableReceivedAmount(finReceivable, defaultAmount(finReceipt.getAmount()).negate(), operator);
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
     * 校验收款单与应收单关联关系
     * 
     * @param finReceivable 应收单信息
     * @param finReceipt 收款单信息
     */
    private void validateReceivableRelation(FinReceivable finReceivable, FinReceipt finReceipt)
    {
        if (!"1".equals(finReceivable.getStatus()))
        {
            throw new ServiceException("仅已审核的应收单允许登记收款");
        }
        if (finReceipt.getCustomerId() == null)
        {
            throw new ServiceException("客户编号不能为空");
        }
        if (!Objects.equals(finReceivable.getCustomerId(), finReceipt.getCustomerId()))
        {
            throw new ServiceException("收款单客户与应收单客户不一致");
        }
    }

    /**
     * 调整应收单已收金额
     * 
     * @param finReceivable 应收单信息
     * @param changeAmount 调整金额
     * @param operator 操作人
     */
    private void adjustReceivableReceivedAmount(FinReceivable finReceivable, BigDecimal changeAmount, String operator)
    {
        BigDecimal currentReceivedAmount = defaultAmount(finReceivable.getReceivedAmount());
        BigDecimal totalReceivableAmount = defaultAmount(finReceivable.getAmount());
        BigDecimal updatedReceivedAmount = currentReceivedAmount.add(defaultAmount(changeAmount));
        if (updatedReceivedAmount.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("应收单已收金额不能小于0");
        }
        if (updatedReceivedAmount.compareTo(totalReceivableAmount) > 0)
        {
            throw new ServiceException("收款金额不能大于应收单未收金额");
        }
        finReceivable.setReceivedAmount(updatedReceivedAmount);
        finReceivable.setUpdateBy(operator);
        finReceivableMapper.updateFinReceivable(finReceivable);
    }

    /**
     * 查询收款单已确认核销金额
     * 
     * @param receiptId 收款单id
     * @return 已确认核销金额
     */
    private BigDecimal getConfirmedWriteOffAmount(Long receiptId)
    {
        FinWriteOff queryFinWriteOff = new FinWriteOff();
        queryFinWriteOff.setReceiptId(receiptId);
        queryFinWriteOff.setStatus("1");
        return sumWriteOffAmount(finWriteOffMapper.selectFinWriteOffList(queryFinWriteOff));
    }

    /**
     * 生成收款单号
     * 
     * @return 收款单号
     */
    private String generateReceiptNo()
    {
        String receiptNoPrefix = "rec" + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String currentMaxReceiptNo = finReceiptMapper.selectMaxReceiptNoByPrefix(receiptNoPrefix);
        int nextSequence = 1;
        if (StringUtils.isNotEmpty(currentMaxReceiptNo) && currentMaxReceiptNo.length() > receiptNoPrefix.length())
        {
            String sequenceText = currentMaxReceiptNo.substring(receiptNoPrefix.length());
            if (StringUtils.isNumeric(sequenceText))
            {
                nextSequence = Integer.parseInt(sequenceText) + 1;
            }
        }
        return receiptNoPrefix + String.format("%03d", nextSequence);
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

