package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.mapper.WmsInventoryCheckItemMapper;
import com.canglian.business.domain.WmsInventoryCheckItem;
import com.canglian.business.service.IWmsInventoryCheckItemService;

/**
 * 盘点单明细 服务层实现
 * 
 * @author canglian
 */
@Service
public class WmsInventoryCheckItemServiceImpl implements IWmsInventoryCheckItemService
{
    @Autowired
    private WmsInventoryCheckItemMapper wmsInventoryCheckItemMapper;

    /**
     * 查询盘点单明细信息
     * 
     * @param checkItemId 盘点明细id
     * @return 盘点单明细信息
     */
    @Override
    public WmsInventoryCheckItem selectWmsInventoryCheckItemById(Long checkItemId)
    {
        return wmsInventoryCheckItemMapper.selectWmsInventoryCheckItemById(checkItemId);
    }

    /**
     * 查询盘点单明细列表
     * 
     * @param wmsInventoryCheckItem 盘点单明细信息
     * @return 盘点单明细集合
     */
    @Override
    public List<WmsInventoryCheckItem> selectWmsInventoryCheckItemList(WmsInventoryCheckItem wmsInventoryCheckItem)
    {
        return wmsInventoryCheckItemMapper.selectWmsInventoryCheckItemList(wmsInventoryCheckItem);
    }

    /**
     * 新增盘点单明细
     * 
     * @param wmsInventoryCheckItem 盘点单明细信息
     * @return 结果
     */
    @Override
    public int insertWmsInventoryCheckItem(WmsInventoryCheckItem wmsInventoryCheckItem)
    {
        return wmsInventoryCheckItemMapper.insertWmsInventoryCheckItem(wmsInventoryCheckItem);
    }

    /**
     * 修改盘点单明细
     * 
     * @param wmsInventoryCheckItem 盘点单明细信息
     * @return 结果
     */
    @Override
    public int updateWmsInventoryCheckItem(WmsInventoryCheckItem wmsInventoryCheckItem)
    {
        return wmsInventoryCheckItemMapper.updateWmsInventoryCheckItem(wmsInventoryCheckItem);
    }

    /**
     * 删除盘点单明细信息
     * 
     * @param checkItemId 盘点明细id
     * @return 结果
     */
    @Override
    public int deleteWmsInventoryCheckItemById(Long checkItemId)
    {
        return wmsInventoryCheckItemMapper.deleteWmsInventoryCheckItemById(checkItemId);
    }

    /**
     * 批量删除盘点单明细
     * 
     * @param checkItemIds 需要删除的盘点明细id
     * @return 结果
     */
    @Override
    public int deleteWmsInventoryCheckItemByIds(Long[] checkItemIds)
    {
        return wmsInventoryCheckItemMapper.deleteWmsInventoryCheckItemByIds(checkItemIds);
    }
}

