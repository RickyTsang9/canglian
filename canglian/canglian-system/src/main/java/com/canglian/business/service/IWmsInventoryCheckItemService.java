package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.WmsInventoryCheckItem;

/**
 * 盘点单明细 服务层
 * 
 * @author canglian
 */
public interface IWmsInventoryCheckItemService
{
    /**
     * 查询盘点单明细信息
     * 
     * @param checkItemId 盘点明细id
     * @return 盘点单明细信息
     */
    public WmsInventoryCheckItem selectWmsInventoryCheckItemById(Long checkItemId);

    /**
     * 查询盘点单明细列表
     * 
     * @param wmsInventoryCheckItem 盘点单明细信息
     * @return 盘点单明细集合
     */
    public List<WmsInventoryCheckItem> selectWmsInventoryCheckItemList(WmsInventoryCheckItem wmsInventoryCheckItem);

    /**
     * 新增盘点单明细
     * 
     * @param wmsInventoryCheckItem 盘点单明细信息
     * @return 结果
     */
    public int insertWmsInventoryCheckItem(WmsInventoryCheckItem wmsInventoryCheckItem);

    /**
     * 修改盘点单明细
     * 
     * @param wmsInventoryCheckItem 盘点单明细信息
     * @return 结果
     */
    public int updateWmsInventoryCheckItem(WmsInventoryCheckItem wmsInventoryCheckItem);

    /**
     * 删除盘点单明细信息
     * 
     * @param checkItemId 盘点明细id
     * @return 结果
     */
    public int deleteWmsInventoryCheckItemById(Long checkItemId);

    /**
     * 批量删除盘点单明细
     * 
     * @param checkItemIds 需要删除的盘点明细id
     * @return 结果
     */
    public int deleteWmsInventoryCheckItemByIds(Long[] checkItemIds);
}

