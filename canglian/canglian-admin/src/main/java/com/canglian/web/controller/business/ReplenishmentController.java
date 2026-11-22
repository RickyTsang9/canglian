package com.canglian.web.controller.business;

import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.business.domain.ReplenishmentSuggestion;
import com.canglian.business.service.IReplenishmentService;
import com.canglian.common.constant.HttpStatus;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.page.PageDomain;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.core.page.TableSupport;

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
        List<ReplenishmentSuggestion> all = replenishmentService.selectReplenishmentSuggestionList(warehouseId);
        PageDomain pageDomain = TableSupport.buildPageRequest();
        int pageNum = pageDomain.getPageNum() == null || pageDomain.getPageNum() < 1 ? 1 : pageDomain.getPageNum();
        int pageSize = pageDomain.getPageSize() == null || pageDomain.getPageSize() < 1 ? 10 : pageDomain.getPageSize();
        int total = all == null ? 0 : all.size();
        int fromIndex = Math.min((pageNum - 1) * pageSize, total);
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<ReplenishmentSuggestion> pageRows = total == 0 ? Collections.<ReplenishmentSuggestion>emptyList() : all.subList(fromIndex, toIndex);
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg("查询成功");
        rspData.setRows(pageRows);
        rspData.setTotal(total);
        return rspData;
    }
}
