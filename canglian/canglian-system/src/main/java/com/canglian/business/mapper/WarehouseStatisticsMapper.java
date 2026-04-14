package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.WarehouseStatisticsCardData;
import com.canglian.business.domain.WarehouseStatisticsOperationItem;
import com.canglian.business.domain.WarehouseStatisticsProductRankItem;
import com.canglian.business.domain.WarehouseStatisticsQuery;
import com.canglian.business.domain.WarehouseStatisticsStockData;

/**
 * 仓库统计 数据层
 * 
 * @author canglian
 */
public interface WarehouseStatisticsMapper
{
    /**
     * 查询入库卡片统计
     * 
     * @param warehouseStatisticsQuery 仓库统计查询参数
     * @return 入库卡片统计
     */
    public WarehouseStatisticsCardData selectInboundCardData(WarehouseStatisticsQuery warehouseStatisticsQuery);

    /**
     * 查询出库卡片统计
     * 
     * @param warehouseStatisticsQuery 仓库统计查询参数
     * @return 出库卡片统计
     */
    public WarehouseStatisticsCardData selectOutboundCardData(WarehouseStatisticsQuery warehouseStatisticsQuery);

    /**
     * 查询库存调整入库卡片统计
     * 
     * @param warehouseStatisticsQuery 仓库统计查询参数
     * @return 库存调整入库卡片统计
     */
    public WarehouseStatisticsCardData selectInventoryInboundCardData(WarehouseStatisticsQuery warehouseStatisticsQuery);

    /**
     * 查询库存调整出库卡片统计
     * 
     * @param warehouseStatisticsQuery 仓库统计查询参数
     * @return 库存调整出库卡片统计
     */
    public WarehouseStatisticsCardData selectInventoryOutboundCardData(WarehouseStatisticsQuery warehouseStatisticsQuery);

    /**
     * 查询入库作业清单
     * 
     * @param warehouseStatisticsQuery 仓库统计查询参数
     * @return 入库作业清单
     */
    public List<WarehouseStatisticsOperationItem> selectInboundOperationList(WarehouseStatisticsQuery warehouseStatisticsQuery);

    /**
     * 查询出库作业清单
     * 
     * @param warehouseStatisticsQuery 仓库统计查询参数
     * @return 出库作业清单
     */
    public List<WarehouseStatisticsOperationItem> selectOutboundOperationList(WarehouseStatisticsQuery warehouseStatisticsQuery);

    /**
     * 查询库存调整入库作业清单
     * 
     * @param warehouseStatisticsQuery 仓库统计查询参数
     * @return 库存调整入库作业清单
     */
    public List<WarehouseStatisticsOperationItem> selectInventoryInboundOperationList(
            WarehouseStatisticsQuery warehouseStatisticsQuery);

    /**
     * 查询库存调整出库作业清单
     * 
     * @param warehouseStatisticsQuery 仓库统计查询参数
     * @return 库存调整出库作业清单
     */
    public List<WarehouseStatisticsOperationItem> selectInventoryOutboundOperationList(
            WarehouseStatisticsQuery warehouseStatisticsQuery);

    /**
     * 查询库存统计
     * 
     * @param warehouseStatisticsQuery 仓库统计查询参数
     * @return 库存统计
     */
    public WarehouseStatisticsStockData selectWarehouseStockData(WarehouseStatisticsQuery warehouseStatisticsQuery);

    /**
     * 查询产品可用库存排行
     * 
     * @param warehouseStatisticsQuery 仓库统计查询参数
     * @return 产品可用库存排行
     */
    public List<WarehouseStatisticsProductRankItem> selectProductRankList(WarehouseStatisticsQuery warehouseStatisticsQuery);
}

