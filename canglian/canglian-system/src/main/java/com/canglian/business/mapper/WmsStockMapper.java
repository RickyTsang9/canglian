package com.canglian.business.mapper;

import java.util.List;
import java.math.BigDecimal;
import org.apache.ibatis.annotations.Param;
import com.canglian.business.domain.WmsStock;

/**
 * 库存 数据层
 * 
 * @author canglian
 */
public interface WmsStockMapper
{
    /**
     * 查询库存信息
     * 
     * @param stockId 库存id
     * @return 库存信息
     */
    public WmsStock selectWmsStockById(Long stockId);

    public WmsStock selectWmsStockByKey(WmsStock wmsStock);

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
     * 按商品同步库存预警阈值
     * 
     * @param productId 商品id
     * @param originalWarningMinQty 原最小库存
     * @param originalWarningMaxQty 原最大库存
     * @param warningMinQty 最新最小库存
     * @param warningMaxQty 最新最大库存
     * @return 结果
     */
    public int updateWarningQtyByProductId(@Param("productId") Long productId,
        @Param("originalWarningMinQty") BigDecimal originalWarningMinQty,
        @Param("originalWarningMaxQty") BigDecimal originalWarningMaxQty,
        @Param("warningMinQty") BigDecimal warningMinQty,
        @Param("warningMaxQty") BigDecimal warningMaxQty);

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

