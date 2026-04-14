package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.WmsStockLog;

/**
 * 库存流水 数据层
 * 
 * @author canglian
 */
public interface WmsStockLogMapper
{
    /**
     * 查询库存流水信息
     * 
     * @param stockLogId 库存流水id
     * @return 库存流水信息
     */
    public WmsStockLog selectWmsStockLogById(Long stockLogId);

    /**
     * 查询库存流水列表
     * 
     * @param wmsStockLog 库存流水信息
     * @return 库存流水集合
     */
    public List<WmsStockLog> selectWmsStockLogList(WmsStockLog wmsStockLog);

    /**
     * 新增库存流水
     * 
     * @param wmsStockLog 库存流水信息
     * @return 结果
     */
    public int insertWmsStockLog(WmsStockLog wmsStockLog);

    /**
     * 修改库存流水
     * 
     * @param wmsStockLog 库存流水信息
     * @return 结果
     */
    public int updateWmsStockLog(WmsStockLog wmsStockLog);

    /**
     * 删除库存流水
     * 
     * @param stockLogId 库存流水id
     * @return 结果
     */
    public int deleteWmsStockLogById(Long stockLogId);

    /**
     * 批量删除库存流水
     * 
     * @param stockLogIds 需要删除的库存流水id
     * @return 结果
     */
    public int deleteWmsStockLogByIds(Long[] stockLogIds);
}

