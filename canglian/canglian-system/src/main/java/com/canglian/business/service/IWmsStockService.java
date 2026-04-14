package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.WmsStock;

/**
 * 库存 服务层
 * 
 * @author canglian
 */
public interface IWmsStockService
{
    /**
     * 查询库存信息
     * 
     * @param stockId 库存id
     * @return 库存信息
     */
    public WmsStock selectWmsStockById(Long stockId);

    /**
     * 查询库存列表
     * 
     * @param wmsStock 库存信息
     * @return 库存集合
     */
    public List<WmsStock> selectWmsStockList(WmsStock wmsStock);

    /**
     * 查询库存预警列表
     * 
     * @param wmsStock 库存信息
     * @return 预警库存集合
     */
    public List<WmsStock> selectWmsStockWarningList(WmsStock wmsStock);

    /**
     * 新增库存
     * 
     * @param wmsStock 库存信息
     * @return 结果
     */
    public int insertWmsStock(WmsStock wmsStock);

    /**
     * 修改库存
     * 
     * @param wmsStock 库存信息
     * @return 结果
     */
    public int updateWmsStock(WmsStock wmsStock);

    /**
     * 删除库存
     * 
     * @param stockId 库存id
     * @return 结果
     */
    public int deleteWmsStockById(Long stockId);

    /**
     * 批量删除库存
     * 
     * @param stockIds 需要删除的库存id
     * @return 结果
     */
    public int deleteWmsStockByIds(Long[] stockIds);
}

