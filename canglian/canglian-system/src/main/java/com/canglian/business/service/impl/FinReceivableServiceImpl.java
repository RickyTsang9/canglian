package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinReceivable;
import com.canglian.business.mapper.FinReceivableMapper;
import com.canglian.business.service.IFinReceivableService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 应收单 服务层实现
 * 
 * @author canglian
 */
@Service
public class FinReceivableServiceImpl implements IFinReceivableService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private FinReceivableMapper finReceivableMapper;

    /**
     * 查询应收单信息
     * 
     * @param receivableId 应收单id
     * @return 应收单信息
     */
    @Override
    public FinReceivable selectFinReceivableById(Long receivableId)
    {
        return finReceivableMapper.selectFinReceivableById(receivableId);
    }

    /**
     * 查询应收单列表
     * 
     * @param finReceivable 应收单信息
     * @return 应收单集合
     */
    @Override
    public List<FinReceivable> selectFinReceivableList(FinReceivable finReceivable)
    {
        return finReceivableMapper.selectFinReceivableList(finReceivable);
    }

    /**
     * 新增应收单
     * 
     * @param finReceivable 应收单信息
     * @return 结果
     */
    @Override
    public int insertFinReceivable(FinReceivable finReceivable)
    {
        if (StringUtils.isEmpty(finReceivable.getReceivableNo()))
        {
            finReceivable.setReceivableNo(generateReceivableNo());
        }
        finReceivable.setReceivedAmount(BigDecimal.ZERO);
        finReceivable.setStatus(calculateCollectionStatus(finReceivable.getReceivedAmount(), finReceivable.getAmount()));
        finReceivable.setBizStatus("draft");
        validateReceivableAmount(finReceivable);
        return finReceivableMapper.insertFinReceivable(finReceivable);
    }

    /**
     * 修改应收单
     * 
     * @param finReceivable 应收单信息
     * @return 结果
     */
    @Override
    public int updateFinReceivable(FinReceivable finReceivable)
    {
        FinReceivable existingFinReceivable = checkReceivableEditable(finReceivable.getReceivableId());
        finReceivable.setReceivableNo(existingFinReceivable.getReceivableNo());
        finReceivable.setStatus(existingFinReceivable.getStatus());
        finReceivable.setBizStatus(existingFinReceivable.getBizStatus());
        finReceivable.setReceivedAmount(existingFinReceivable.getReceivedAmount());
        validateReceivableAmount(finReceivable);
        return finReceivableMapper.updateFinReceivable(finReceivable);
    }

    /**
     * 删除应收单
     * 
     * @param receivableId 应收单id
     * @return 结果
     */
    @Override
    public int deleteFinReceivableById(Long receivableId)
    {
        checkReceivableDeletable(receivableId);
        return finReceivableMapper.deleteFinReceivableById(receivableId);
    }

    /**
     * 批量删除应收单
     * 
     * @param receivableIds 需要删除的应收单id
     * @return 结果
     */
    @Override
    public int deleteFinReceivableByIds(Long[] receivableIds)
    {
        for (Long receivableId : receivableIds)
        {
            checkReceivableDeletable(receivableId);
        }
        return finReceivableMapper.deleteFinReceivableByIds(receivableIds);
    }

    /**
     * 审核应收单
     * 
     * @param receivableId 应收单id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    public int auditFinReceivable(Long receivableId, String operator)
    {
        FinReceivable finReceivable = getExistingFinReceivable(receivableId);
        if ("confirmed".equals(finReceivable.getBizStatus()))
        {
            throw new ServiceException("应收单已审核，请勿重复操作");
        }
        validateReceivableAmount(finReceivable);
        finReceivable.setStatus(calculateCollectionStatus(finReceivable.getReceivedAmount(), finReceivable.getAmount()));
        finReceivable.setBizStatus("confirmed");
        finReceivable.setUpdateBy(operator);
        return finReceivableMapper.updateFinReceivable(finReceivable);
    }

    /**
     * 校验应收金额
     * 
     * @param finReceivable 应收单信息
     */
    private void validateReceivableAmount(FinReceivable finReceivable)
    {
        if (finReceivable.getAmount() == null || finReceivable.getAmount().compareTo(BigDecimal.ZERO) <= 0)
        {
            throw new ServiceException("应收金额必须大于0");
        }
        if (defaultAmount(finReceivable.getReceivedAmount()).compareTo(finReceivable.getAmount()) > 0)
        {
            throw new ServiceException("应收单已收金额不能大于应收金额");
        }
    }

    /**
     * 获取存在的应收单
     * 
     * @param receivableId 应收单id
     * @return 应收单信息
     */
    private FinReceivable getExistingFinReceivable(Long receivableId)
    {
        FinReceivable finReceivable = finReceivableMapper.selectFinReceivableById(receivableId);
        if (finReceivable == null)
        {
            throw new ServiceException("应收单不存在");
        }
        return finReceivable;
    }

    /**
     * 校验应收单是否允许修改
     * 
     * @param receivableId 应收单id
     * @return 应收单信息
     */
    private FinReceivable checkReceivableEditable(Long receivableId)
    {
        FinReceivable finReceivable = getExistingFinReceivable(receivableId);
        if ("confirmed".equals(finReceivable.getBizStatus()))
        {
            throw new ServiceException("应收单已审核，无法修改");
        }
        return finReceivable;
    }

    /**
     * 校验应收单是否允许删除
     * 
     * @param receivableId 应收单id
     */
    private void checkReceivableDeletable(Long receivableId)
    {
        FinReceivable finReceivable = getExistingFinReceivable(receivableId);
        if ("confirmed".equals(finReceivable.getBizStatus()))
        {
            throw new ServiceException("应收单已审核，无法删除");
        }
        if (defaultAmount(finReceivable.getReceivedAmount()).compareTo(BigDecimal.ZERO) > 0)
        {
            throw new ServiceException("应收单已存在收款记录，无法删除");
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
     * 生成应收单号
     * 
     * @return 应收单号
     */
    private String generateReceivableNo()
    {
        String receivableNoPrefix = "ar" + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String currentMaxReceivableNo = finReceivableMapper.selectMaxReceivableNoByPrefix(receivableNoPrefix);
        int nextSequence = 1;
        if (StringUtils.isNotEmpty(currentMaxReceivableNo) && currentMaxReceivableNo.length() > receivableNoPrefix.length())
        {
            String sequenceText = currentMaxReceivableNo.substring(receivableNoPrefix.length());
            if (StringUtils.isNumeric(sequenceText))
            {
                nextSequence = Integer.parseInt(sequenceText) + 1;
            }
        }
        return receivableNoPrefix + String.format("%03d", nextSequence);
    }

    /**
     * 计算收款状态
     * 
     * @param receivedAmount 已收金额
     * @param receivableAmount 应收金额
     * @return 收款状态
     */
    private String calculateCollectionStatus(BigDecimal receivedAmount, BigDecimal receivableAmount)
    {
        BigDecimal receivedAmountValue = defaultAmount(receivedAmount);
        BigDecimal receivableAmountValue = defaultAmount(receivableAmount);
        if (receivedAmountValue.compareTo(BigDecimal.ZERO) <= 0)
        {
            return "0";
        }
        if (receivedAmountValue.compareTo(receivableAmountValue) >= 0)
        {
            return "2";
        }
        return "1";
    }
}

