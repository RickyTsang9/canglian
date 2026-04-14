package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.WmsOutboundItem;

/**
 * 出库单明细 数据层
 * 
 * @author canglian
 */
public interface WmsOutboundItemMapper
{
    /**
     * 查询出库单明细信息
     * 
     * @param outboundItemId 出库明细id
     * @return 出库单明细信息
     */
    public WmsOutboundItem selectWmsOutboundItemById(Long outboundItemId);

    /**
     * 查询出库单明细列表
     * 
     * @param wmsOutboundItem 出库单明细信息
     * @return 出库单明细集合
     */
    public List<WmsOutboundItem> selectWmsOutboundItemList(WmsOutboundItem wmsOutboundItem);

    /**
     * 新增出库单明细
     * 
     * @param wmsOutboundItem 出库单明细信息
     * @return 结果
     */
    public int insertWmsOutboundItem(WmsOutboundItem wmsOutboundItem);

    /**
     * 修改出库单明细
     * 
     * @param wmsOutboundItem 出库单明细信息
     * @return 结果
     */
    public int updateWmsOutboundItem(WmsOutboundItem wmsOutboundItem);

    /**
     * 删除出库单明细
     * 
     * @param outboundItemId 出库明细id
     * @return 结果
     */
    public int deleteWmsOutboundItemById(Long outboundItemId);

    /**
     * 批量删除出库单明细
     * 
     * @param outboundItemIds 需要删除的出库明细id
     * @return 结果
     */
    public int deleteWmsOutboundItemByIds(Long[] outboundItemIds);
}

