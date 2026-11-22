package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.WmsPurchaseReturn;

/**
 * 采购退货 服务层
 *
 * @author canglian
 */
public interface IWmsPurchaseReturnService
{
    public WmsPurchaseReturn selectWmsPurchaseReturnById(Long purchaseReturnId);

    public List<WmsPurchaseReturn> selectWmsPurchaseReturnList(WmsPurchaseReturn wmsPurchaseReturn);

    public int insertWmsPurchaseReturn(WmsPurchaseReturn wmsPurchaseReturn);

    public int updateWmsPurchaseReturn(WmsPurchaseReturn wmsPurchaseReturn);

    public int deleteWmsPurchaseReturnById(Long purchaseReturnId);

    public int deleteWmsPurchaseReturnByIds(Long[] purchaseReturnIds);

    public int auditWmsPurchaseReturn(Long purchaseReturnId, String operator);

    /** 反审核采购退货单 */
    public int unauditWmsPurchaseReturn(Long purchaseReturnId, String operator);

    /** 一键红冲采购退货单 */
    public WmsPurchaseReturn reverseWmsPurchaseReturn(Long purchaseReturnId, String operator);
}
