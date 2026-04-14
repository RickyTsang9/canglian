package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.WmsTransferItem;

/**
 * 调拨单明细 数据层
 * 
 * @author canglian
 */
public interface WmsTransferItemMapper
{
    /**
     * 查询调拨单明细信息
     * 
     * @param transferItemId 调拨明细id
     * @return 调拨单明细信息
     */
    public WmsTransferItem selectWmsTransferItemById(Long transferItemId);

    /**
     * 查询调拨单明细列表
     * 
     * @param wmsTransferItem 调拨单明细信息
     * @return 调拨单明细集合
     */
    public List<WmsTransferItem> selectWmsTransferItemList(WmsTransferItem wmsTransferItem);

    /**
     * 新增调拨单明细
     * 
     * @param wmsTransferItem 调拨单明细信息
     * @return 结果
     */
    public int insertWmsTransferItem(WmsTransferItem wmsTransferItem);

    /**
     * 修改调拨单明细
     * 
     * @param wmsTransferItem 调拨单明细信息
     * @return 结果
     */
    public int updateWmsTransferItem(WmsTransferItem wmsTransferItem);

    /**
     * 删除调拨单明细
     * 
     * @param transferItemId 调拨明细id
     * @return 结果
     */
    public int deleteWmsTransferItemById(Long transferItemId);

    /**
     * 批量删除调拨单明细
     * 
     * @param transferItemIds 需要删除的调拨明细id
     * @return 结果
     */
    public int deleteWmsTransferItemByIds(Long[] transferItemIds);
}

