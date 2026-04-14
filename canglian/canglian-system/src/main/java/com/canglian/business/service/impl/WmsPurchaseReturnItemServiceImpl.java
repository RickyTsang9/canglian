package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.WmsPurchaseReturnItem;
import com.canglian.business.mapper.WmsPurchaseReturnItemMapper;
import com.canglian.business.service.IWmsPurchaseReturnItemService;

/**
 * 采购退货明细 服务层实现
 * 
 * @author canglian
 */
@Service
public class WmsPurchaseReturnItemServiceImpl implements IWmsPurchaseReturnItemService
{
    @Autowired
    private WmsPurchaseReturnItemMapper wmsPurchaseReturnItemMapper;

    /**
     * 查询采购退货明细信息
     * 
     * @param purchaseReturnItemId 采购退货明细id
     * @return 采购退货明细信息
     */
    @Override
    public WmsPurchaseReturnItem selectWmsPurchaseReturnItemById(Long purchaseReturnItemId)
    {
        return wmsPurchaseReturnItemMapper.selectWmsPurchaseReturnItemById(purchaseReturnItemId);
    }

    /**
     * 查询采购退货明细列表
     * 
     * @param wmsPurchaseReturnItem 采购退货明细信息
     * @return 采购退货明细集合
     */
    @Override
    public List<WmsPurchaseReturnItem> selectWmsPurchaseReturnItemList(WmsPurchaseReturnItem wmsPurchaseReturnItem)
    {
        return wmsPurchaseReturnItemMapper.selectWmsPurchaseReturnItemList(wmsPurchaseReturnItem);
    }

    /**
     * 新增采购退货明细
     * 
     * @param wmsPurchaseReturnItem 采购退货明细信息
     * @return 结果
     */
    @Override
    public int insertWmsPurchaseReturnItem(WmsPurchaseReturnItem wmsPurchaseReturnItem)
    {
        return wmsPurchaseReturnItemMapper.insertWmsPurchaseReturnItem(wmsPurchaseReturnItem);
    }

    /**
     * 修改采购退货明细
     * 
     * @param wmsPurchaseReturnItem 采购退货明细信息
     * @return 结果
     */
    @Override
    public int updateWmsPurchaseReturnItem(WmsPurchaseReturnItem wmsPurchaseReturnItem)
    {
        return wmsPurchaseReturnItemMapper.updateWmsPurchaseReturnItem(wmsPurchaseReturnItem);
    }

    /**
     * 删除采购退货明细
     * 
     * @param purchaseReturnItemId 采购退货明细id
     * @return 结果
     */
    @Override
    public int deleteWmsPurchaseReturnItemById(Long purchaseReturnItemId)
    {
        return wmsPurchaseReturnItemMapper.deleteWmsPurchaseReturnItemById(purchaseReturnItemId);
    }

    /**
     * 批量删除采购退货明细
     * 
     * @param purchaseReturnItemIds 需要删除的采购退货明细id
     * @return 结果
     */
    @Override
    public int deleteWmsPurchaseReturnItemByIds(Long[] purchaseReturnItemIds)
    {
        return wmsPurchaseReturnItemMapper.deleteWmsPurchaseReturnItemByIds(purchaseReturnItemIds);
    }
}

