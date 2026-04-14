package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.WmsInboundItem;

/**
 * 入库单明细 数据层
 * 
 * @author canglian
 */
public interface WmsInboundItemMapper
{
    /**
     * 查询入库单明细信息
     * 
     * @param inboundItemId 入库明细id
     * @return 入库单明细信息
     */
    public WmsInboundItem selectWmsInboundItemById(Long inboundItemId);

    /**
     * 查询入库单明细列表
     * 
     * @param wmsInboundItem 入库单明细信息
     * @return 入库单明细集合
     */
    public List<WmsInboundItem> selectWmsInboundItemList(WmsInboundItem wmsInboundItem);

    /**
     * 新增入库单明细
     * 
     * @param wmsInboundItem 入库单明细信息
     * @return 结果
     */
    public int insertWmsInboundItem(WmsInboundItem wmsInboundItem);

    /**
     * 修改入库单明细
     * 
     * @param wmsInboundItem 入库单明细信息
     * @return 结果
     */
    public int updateWmsInboundItem(WmsInboundItem wmsInboundItem);

    /**
     * 删除入库单明细
     * 
     * @param inboundItemId 入库明细id
     * @return 结果
     */
    public int deleteWmsInboundItemById(Long inboundItemId);

    /**
     * 批量删除入库单明细
     * 
     * @param inboundItemIds 需要删除的入库明细id
     * @return 结果
     */
    public int deleteWmsInboundItemByIds(Long[] inboundItemIds);
}

