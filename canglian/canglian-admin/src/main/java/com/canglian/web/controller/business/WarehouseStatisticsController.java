package com.canglian.web.controller.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.business.domain.WarehouseStatisticsOverview;
import com.canglian.business.domain.WarehouseStatisticsQuery;
import com.canglian.business.service.IWarehouseStatisticsService;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.utils.DateUtils;
import com.canglian.common.utils.StringUtils;

/**
 * 仓库统计 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/warehouseStatistics")
public class WarehouseStatisticsController extends BaseController
{
    @Autowired
    private IWarehouseStatisticsService warehouseStatisticsService;

    /**
     * 查询仓库统计总览
     */
    @PreAuthorize("@ss.hasPermi('business:warehouseStatistics:query')")
    @GetMapping("/overview")
    public AjaxResult overview(@RequestParam(required = false) Long warehouseId,
            @RequestParam(required = false) String statisticsView,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Integer operationLimit,
            @RequestParam(required = false) Integer rankLimit)
    {
        WarehouseStatisticsQuery warehouseStatisticsQuery = new WarehouseStatisticsQuery();
        warehouseStatisticsQuery.setWarehouseId(warehouseId);
        warehouseStatisticsQuery.setStatisticsView(statisticsView);
        warehouseStatisticsQuery.setOperationLimit(operationLimit);
        warehouseStatisticsQuery.setRankLimit(rankLimit);
        if (StringUtils.isNotEmpty(startTime))
        {
            warehouseStatisticsQuery.setStartTime(DateUtils.parseDate(startTime));
        }
        if (StringUtils.isNotEmpty(endTime))
        {
            warehouseStatisticsQuery.setEndTime(DateUtils.parseDate(endTime));
        }
        WarehouseStatisticsOverview warehouseStatisticsOverview = warehouseStatisticsService
                .selectWarehouseStatisticsOverview(warehouseStatisticsQuery);
        return success(warehouseStatisticsOverview);
    }
}

