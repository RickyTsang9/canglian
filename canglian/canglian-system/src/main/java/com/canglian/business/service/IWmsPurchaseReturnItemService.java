package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.WmsPurchaseReturnItem;

/**
 * 采购退货明细 服务层
 * 
 * @author canglian
 */
public interface IWmsPurchaseReturnItemService
{
    /**
     * 查询采购退货明细信息
     * 
     * @param purchaseReturnItemId 采购退货明细id
     * @return 采购退货明细信息
     */
    public WmsPurchaseReturnItem selectWmsPurchaseReturnItemById(Long purchaseReturnItemId);

    /**
     * 查询采购退货明细列表
     * 
     * @param wmsPurchaseReturnItem 采购退货明细信息
     * @return 采购退货明细集合
     */
    public List<WmsPurchaseReturnItem> selectWmsPurchaseReturnItemList(WmsPurchaseReturnItem wmsPurchaseReturnItem);

    /**
     * 新增采购退货明细
     * 
     * @param wmsPurchaseReturnItem 采购退货明细信息
     * @return 结果
     */
    public int insertWmsPurchaseReturnItem(WmsPurchaseReturnItem wmsPurchaseReturnItem);

    /**
     * 修改采购退货明细
     * 
     * @param wmsPurchaseReturnItem 采购退货明细信息
     * @return 结果
     */
    public int updateWmsPurchaseReturnItem(WmsPurchaseReturnItem wmsPurchaseReturnItem);

    /**
     * 删除采购退货明细
     * 
     * @param purchaseReturnItemId 采购退货明细id
     * @return 结果
     */
    public int deleteWmsPurchaseReturnItemById(Long purchaseReturnItemId);

    /**
     * 批量删除采购退货明细
     * 
     * @param purchaseReturnItemIds 需要删除的采购退货明细id
     * @return 结果
     */
    public int deleteWmsPurchaseReturnItemByIds(Long[] purchaseReturnItemIds);
}

