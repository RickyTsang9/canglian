package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.WmsTransfer;

/**
 * 调拨单 数据层
 * 
 * @author canglian
 */
public interface WmsTransferMapper
{
    /**
     * 查询调拨单信息
     * 
     * @param transferId 调拨单id
     * @return 调拨单信息
     */
    public WmsTransfer selectWmsTransferById(Long transferId);

    /**
     * 查询调拨单列表
     * 
     * @param wmsTransfer 调拨单信息
     * @return 调拨单集合
     */
    public List<WmsTransfer> selectWmsTransferList(WmsTransfer wmsTransfer);

    /**
     * 新增调拨单
     * 
     * @param wmsTransfer 调拨单信息
     * @return 结果
     */
    public int insertWmsTransfer(WmsTransfer wmsTransfer);

    /**
     * 修改调拨单
     * 
     * @param wmsTransfer 调拨单信息
     * @return 结果
     */
    public int updateWmsTransfer(WmsTransfer wmsTransfer);

    /**
     * 删除调拨单
     * 
     * @param transferId 调拨单id
     * @return 结果
     */
    public int deleteWmsTransferById(Long transferId);

    /**
     * 批量删除调拨单
     * 
     * @param transferIds 需要删除的调拨单id
     * @return 结果
     */
    public int deleteWmsTransferByIds(Long[] transferIds);

    public String selectMaxTransferNoByPrefix(String noPrefix);
}

