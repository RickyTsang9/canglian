package com.canglian.business.service;

import com.canglian.business.domain.WarehouseStatisticsOverview;
import com.canglian.business.domain.WarehouseStatisticsQuery;

/**
 * 仓库统计 服务层
 * 
 * @author canglian
 */
public interface IWarehouseStatisticsService
{
    /**
     * 查询仓库统计总览
     * 
     * @param warehouseStatisticsQuery 仓库统计查询参数
     * @return 仓库统计总览
     */
    public WarehouseStatisticsOverview selectWarehouseStatisticsOverview(WarehouseStatisticsQuery warehouseStatisticsQuery);
}

