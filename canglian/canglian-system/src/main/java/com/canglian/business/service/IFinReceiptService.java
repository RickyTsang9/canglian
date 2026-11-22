package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.FinReceipt;

/**
 * 收款单 服务层
 *
 * @author canglian
 */
public interface IFinReceiptService
{
    public FinReceipt selectFinReceiptById(Long receiptId);

    public List<FinReceipt> selectFinReceiptList(FinReceipt finReceipt);

    public int insertFinReceipt(FinReceipt finReceipt);

    /** 仅限退货流程内部调用的红字退款收款单（自动确认入账） */
    public int insertRefundReceipt(FinReceipt finReceipt);

    public int updateFinReceipt(FinReceipt finReceipt);

    /** 确认收款单（记入资金流水、同步应收已收、触发凭证事件） */
    public int confirmFinReceipt(Long receiptId, String operator);

    /** 反确认收款单（开放账期且无后续核销/未生成凭证，逆向冲回资金与应收并恢复草稿） */
    public int unconfirmFinReceipt(Long receiptId, String operator);

    public int deleteFinReceiptById(Long receiptId);

    public int deleteFinReceiptByIds(Long[] receiptIds);
}
