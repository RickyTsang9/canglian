package com.canglian.web.controller.business;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.business.domain.ReplenishmentSuggestion;
import com.canglian.business.service.IReplenishmentService;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.page.TableDataInfo;

/**
 * 补货建议控制器
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/replenishment")
public class ReplenishmentController extends BaseController
{
    @Autowired
    private IReplenishmentService replenishmentService;

    /**
     * 查询补货建议列表
     * 
     * @param warehouseId 仓库编号
     * @return 补货建议列表
     */
    @PreAuthorize("@ss.hasPermi('business:replenishment:query')")
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) Long warehouseId)
    {
        startPage();
        List<ReplenishmentSuggestion> replenishmentSuggestionList = replenishmentService.selectReplenishmentSuggestionList(warehouseId);
        return getDataTable(replenishmentSuggestionList);
    }
}
