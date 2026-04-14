package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinReceipt;
import com.canglian.business.mapper.FinReceiptMapper;
import com.canglian.business.service.IFinReceiptService;

/**
 * 收款单 服务层实现
 * 
 * @author canglian
 */
@Service
public class FinReceiptServiceImpl implements IFinReceiptService
{
    @Autowired
    private FinReceiptMapper finReceiptMapper;

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
    public int insertFinReceipt(FinReceipt finReceipt)
    {
        return finReceiptMapper.insertFinReceipt(finReceipt);
    }

    /**
     * 修改收款单
     * 
     * @param finReceipt 收款单信息
     * @return 结果
     */
    @Override
    public int updateFinReceipt(FinReceipt finReceipt)
    {
        return finReceiptMapper.updateFinReceipt(finReceipt);
    }

    /**
     * 删除收款单
     * 
     * @param receiptId 收款单id
     * @return 结果
     */
    @Override
    public int deleteFinReceiptById(Long receiptId)
    {
        return finReceiptMapper.deleteFinReceiptById(receiptId);
    }

    /**
     * 批量删除收款单
     * 
     * @param receiptIds 需要删除的收款单id
     * @return 结果
     */
    @Override
    public int deleteFinReceiptByIds(Long[] receiptIds)
    {
        return finReceiptMapper.deleteFinReceiptByIds(receiptIds);
    }
}

