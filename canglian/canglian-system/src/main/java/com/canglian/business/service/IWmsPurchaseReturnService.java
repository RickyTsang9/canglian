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
    /**
     * 查询采购退货信息
     * 
     * @param purchaseReturnId 采购退货id
     * @return 采购退货信息
     */
    public WmsPurchaseReturn selectWmsPurchaseReturnById(Long purchaseReturnId);

    /**
     * 查询采购退货列表
     * 
     * @param wmsPurchaseReturn 采购退货信息
     * @return 采购退货集合
     */
    public List<WmsPurchaseReturn> selectWmsPurchaseReturnList(WmsPurchaseReturn wmsPurchaseReturn);

    /**
     * 新增采购退货
     * 
     * @param wmsPurchaseReturn 采购退货信息
     * @return 结果
     */
    public int insertWmsPurchaseReturn(WmsPurchaseReturn wmsPurchaseReturn);

    /**
     * 修改采购退货
     * 
     * @param wmsPurchaseReturn 采购退货信息
     * @return 结果
     */
    public int updateWmsPurchaseReturn(WmsPurchaseReturn wmsPurchaseReturn);

    /**
     * 删除采购退货
     * 
     * @param purchaseReturnId 采购退货id
     * @return 结果
     */
    public int deleteWmsPurchaseReturnById(Long purchaseReturnId);

    /**
     * 批量删除采购退货
     * 
     * @param purchaseReturnIds 需要删除的采购退货id
     * @return 结果
     */
    public int deleteWmsPurchaseReturnByIds(Long[] purchaseReturnIds);

    /**
     * 采购退货审核
     * 
     * @param purchaseReturnId 采购退货id
     * @param operator 操作人
     * @return 结果
     */
    public int auditWmsPurchaseReturn(Long purchaseReturnId, String operator);
}

