package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.FinReceipt;

/**
 * 收款单 数据层
 * 
 * @author canglian
 */
public interface FinReceiptMapper
{
    /**
     * 查询收款单信息
     * 
     * @param receiptId 收款单id
     * @return 收款单信息
     */
    public FinReceipt selectFinReceiptById(Long receiptId);

    /**
     * 查询收款单列表
     * 
     * @param finReceipt 收款单信息
     * @return 收款单集合
     */
    public List<FinReceipt> selectFinReceiptList(FinReceipt finReceipt);

    /**
     * 新增收款单
     * 
     * @param finReceipt 收款单信息
     * @return 结果
     */
    public int insertFinReceipt(FinReceipt finReceipt);

    /**
     * 修改收款单
     * 
     * @param finReceipt 收款单信息
     * @return 结果
     */
    public int updateFinReceipt(FinReceipt finReceipt);

    /**
     * 删除收款单
     * 
     * @param receiptId 收款单id
     * @return 结果
     */
    public int deleteFinReceiptById(Long receiptId);

    /**
     * 批量删除收款单
     * 
     * @param receiptIds 需要删除的收款单id
     * @return 结果
     */
    public int deleteFinReceiptByIds(Long[] receiptIds);
}

