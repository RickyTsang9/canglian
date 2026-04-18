package com.canglian.business.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.business.domain.FinInvoice;
import com.canglian.business.service.IFinInvoiceService;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.business.mapper.FinInvoiceMapper;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 发票登记服务实现
 * 
 * @author canglian
 */
@Service
public class FinInvoiceServiceImpl implements IFinInvoiceService
{
    private static final DateTimeFormatter DOCUMENT_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Autowired
    private FinInvoiceMapper finInvoiceMapper;

    @Autowired
    private IFinVoucherEventService finVoucherEventService;

    /**
     * 查询发票登记详情
     * 
     * @param invoiceId 发票登记id
     * @return 发票登记详情
     */
    @Override
    public FinInvoice selectFinInvoiceById(Long invoiceId)
    {
        return finInvoiceMapper.selectFinInvoiceById(invoiceId);
    }

    /**
     * 查询发票登记列表
     * 
     * @param finInvoice 发票登记
     * @return 发票登记集合
     */
    @Override
    public List<FinInvoice> selectFinInvoiceList(FinInvoice finInvoice)
    {
        return finInvoiceMapper.selectFinInvoiceList(finInvoice);
    }

    /**
     * 新增发票登记
     * 
     * @param finInvoice 发票登记
     * @return 结果
     */
    @Override
    public int insertFinInvoice(FinInvoice finInvoice)
    {
        if (StringUtils.isEmpty(finInvoice.getInvoiceNo()))
        {
            finInvoice.setInvoiceNo(generateInvoiceNo(finInvoice.getInvoiceType()));
        }
        fillInvoiceAmount(finInvoice);
        finInvoice.setStatus("0");
        finInvoice.setBizStatus("draft");
        if (finInvoice.getBusinessDate() == null)
        {
            finInvoice.setBusinessDate(new Date());
        }
        if (finInvoice.getInvoiceDate() == null)
        {
            finInvoice.setInvoiceDate(finInvoice.getBusinessDate());
        }
        return finInvoiceMapper.insertFinInvoice(finInvoice);
    }

    /**
     * 修改发票登记
     * 
     * @param finInvoice 发票登记
     * @return 结果
     */
    @Override
    public int updateFinInvoice(FinInvoice finInvoice)
    {
        FinInvoice existingFinInvoice = getExistingInvoice(finInvoice.getInvoiceId());
        if (!"draft".equals(existingFinInvoice.getBizStatus()))
        {
            throw new ServiceException("当前发票状态不允许修改");
        }
        finInvoice.setInvoiceNo(existingFinInvoice.getInvoiceNo());
        finInvoice.setStatus(existingFinInvoice.getStatus());
        finInvoice.setBizStatus(existingFinInvoice.getBizStatus());
        finInvoice.setConfirmBy(existingFinInvoice.getConfirmBy());
        finInvoice.setConfirmTime(existingFinInvoice.getConfirmTime());
        fillInvoiceAmount(finInvoice);
        return finInvoiceMapper.updateFinInvoice(finInvoice);
    }

    /**
     * 确认发票登记
     * 
     * @param invoiceId 发票登记id
     * @param operator 操作人
     * @return 结果
     */
    @Override
    @Transactional
    public int confirmFinInvoice(Long invoiceId, String operator)
    {
        FinInvoice finInvoice = getExistingInvoice(invoiceId);
        if (!"draft".equals(finInvoice.getBizStatus()))
        {
            throw new ServiceException("当前发票状态不允许确认");
        }
        fillInvoiceAmount(finInvoice);
        finInvoice.setStatus("1");
        finInvoice.setBizStatus("confirmed");
        finInvoice.setConfirmBy(operator);
        finInvoice.setConfirmTime(new Date());
        finInvoice.setUpdateBy(operator);
        int updateRows = finInvoiceMapper.updateFinInvoice(finInvoice);
        if (updateRows > 0)
        {
            finVoucherEventService.recordVoucherEvent("invoice", finInvoice.getInvoiceId(), finInvoice.getInvoiceNo(), "invoice_confirm",
                finInvoice.getBusinessDate(), finInvoice.getInvoiceAmount(), operator, "发票确认待生成凭证");
        }
        return updateRows;
    }

    /**
     * 删除发票登记
     * 
     * @param invoiceId 发票登记id
     * @return 结果
     */
    @Override
    public int deleteFinInvoiceById(Long invoiceId)
    {
        FinInvoice finInvoice = getExistingInvoice(invoiceId);
        if (!"draft".equals(finInvoice.getBizStatus()))
        {
            throw new ServiceException("当前发票状态不允许删除");
        }
        return finInvoiceMapper.deleteFinInvoiceById(invoiceId);
    }

    /**
     * 批量删除发票登记
     * 
     * @param invoiceIds 发票登记id集合
     * @return 结果
     */
    @Override
    public int deleteFinInvoiceByIds(Long[] invoiceIds)
    {
        for (Long invoiceId : invoiceIds)
        {
            FinInvoice finInvoice = getExistingInvoice(invoiceId);
            if (!"draft".equals(finInvoice.getBizStatus()))
            {
                throw new ServiceException("当前发票状态不允许删除");
            }
        }
        return finInvoiceMapper.deleteFinInvoiceByIds(invoiceIds);
    }

    /**
     * 获取已存在发票登记
     * 
     * @param invoiceId 发票登记id
     * @return 发票登记
     */
    private FinInvoice getExistingInvoice(Long invoiceId)
    {
        FinInvoice finInvoice = finInvoiceMapper.selectFinInvoiceById(invoiceId);
        if (finInvoice == null)
        {
            throw new ServiceException("发票登记不存在");
        }
        return finInvoice;
    }

    /**
     * 回填发票金额
     * 
     * @param finInvoice 发票登记
     */
    private void fillInvoiceAmount(FinInvoice finInvoice)
    {
        BigDecimal invoiceAmount = defaultAmount(finInvoice.getInvoiceAmount());
        BigDecimal taxRate = defaultAmount(finInvoice.getTaxRate());
        if (invoiceAmount.compareTo(BigDecimal.ZERO) < 0)
        {
            throw new ServiceException("发票金额不能小于0");
        }
        finInvoice.setInvoiceAmount(invoiceAmount);
        finInvoice.setTaxRate(taxRate);
        BigDecimal untaxedAmount = invoiceAmount.divide(BigDecimal.ONE.add(taxRate), 2, BigDecimal.ROUND_HALF_UP);
        BigDecimal taxAmount = invoiceAmount.subtract(untaxedAmount);
        finInvoice.setUntaxedAmount(untaxedAmount);
        finInvoice.setTaxAmount(taxAmount);
    }

    /**
     * 生成发票登记号
     * 
     * @param invoiceType 发票类型
     * @return 发票登记号
     */
    private String generateInvoiceNo(String invoiceType)
    {
        String invoicePrefix = ("purchase".equals(invoiceType) ? "pin" : "sin") + LocalDate.now().format(DOCUMENT_DATE_FORMATTER);
        String currentMaxInvoiceNo = finInvoiceMapper.selectMaxInvoiceNoByPrefix(invoicePrefix);
        int nextSequence = 1;
        if (StringUtils.isNotEmpty(currentMaxInvoiceNo) && currentMaxInvoiceNo.length() > invoicePrefix.length())
        {
            String sequenceText = currentMaxInvoiceNo.substring(invoicePrefix.length());
            if (StringUtils.isNumeric(sequenceText))
            {
                nextSequence = Integer.parseInt(sequenceText) + 1;
            }
        }
        return invoicePrefix + String.format("%03d", nextSequence);
    }

    /**
     * 默认金额处理
     * 
     * @param amount 金额
     * @return 默认金额
     */
    private BigDecimal defaultAmount(BigDecimal amount)
    {
        return amount == null ? BigDecimal.ZERO : amount;
    }
}
