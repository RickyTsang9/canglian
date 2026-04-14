package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.WmsOutbound;

/**
 * 出库单 数据层
 * 
 * @author canglian
 */
public interface WmsOutboundMapper
{
    /**
     * 查询出库单信息
     * 
     * @param outboundId 出库单id
     * @return 出库单信息
     */
    public WmsOutbound selectWmsOutboundById(Long outboundId);

    /**
     * 查询出库单列表
     * 
     * @param wmsOutbound 出库单信息
     * @return 出库单集合
     */
    public List<WmsOutbound> selectWmsOutboundList(WmsOutbound wmsOutbound);

    /**
     * 新增出库单
     * 
     * @param wmsOutbound 出库单信息
     * @return 结果
     */
    public int insertWmsOutbound(WmsOutbound wmsOutbound);

    /**
     * 修改出库单
     * 
     * @param wmsOutbound 出库单信息
     * @return 结果
     */
    public int updateWmsOutbound(WmsOutbound wmsOutbound);

    /**
     * 删除出库单
     * 
     * @param outboundId 出库单id
     * @return 结果
     */
    public int deleteWmsOutboundById(Long outboundId);

    /**
     * 批量删除出库单
     * 
     * @param outboundIds 需要删除的出库单id
     * @return 结果
     */
    public int deleteWmsOutboundByIds(Long[] outboundIds);

    public String selectMaxOutboundNoByPrefix(String noPrefix);
}

