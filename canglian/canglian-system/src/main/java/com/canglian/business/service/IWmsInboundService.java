package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.WmsInbound;

/**
 * 入库单 服务层
 * 
 * @author canglian
 */
public interface IWmsInboundService
{
    /**
     * 查询入库单信息
     * 
     * @param inboundId 入库单id
     * @return 入库单信息
     */
    public WmsInbound selectWmsInboundById(Long inboundId);

    /**
     * 查询入库单列表
     * 
     * @param wmsInbound 入库单信息
     * @return 入库单集合
     */
    public List<WmsInbound> selectWmsInboundList(WmsInbound wmsInbound);

    /**
     * 新增入库单
     * 
     * @param wmsInbound 入库单信息
     * @return 结果
     */
    public int insertWmsInbound(WmsInbound wmsInbound);

    /**
     * 修改入库单
     * 
     * @param wmsInbound 入库单信息
     * @return 结果
     */
    public int updateWmsInbound(WmsInbound wmsInbound);

    /**
     * 删除入库单
     * 
     * @param inboundId 入库单id
     * @return 结果
     */
    public int deleteWmsInboundById(Long inboundId);

    /**
     * 批量删除入库单
     * 
     * @param inboundIds 需要删除的入库单id
     * @return 结果
     */
    public int deleteWmsInboundByIds(Long[] inboundIds);

    /**
     * 入库单审核
     * 
     * @param inboundId 入库单id
     * @param operator 操作人
     * @return 结果
     */
    public int auditWmsInbound(Long inboundId, String operator);
}

