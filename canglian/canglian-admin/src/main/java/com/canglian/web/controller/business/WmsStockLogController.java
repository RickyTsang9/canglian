package com.canglian.web.controller.business;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;
import com.canglian.business.domain.WmsStockLog;
import com.canglian.business.service.IWmsStockLogService;

@RestController
@RequestMapping("/business/stockLog")
public class WmsStockLogController extends BaseController
{
    @Autowired
    private IWmsStockLogService wmsStockLogService;

    @PreAuthorize("@ss.hasPermi('business:stockLog:list')")
    @GetMapping("/list")
    public TableDataInfo list(WmsStockLog wmsStockLog)
    {
        startPage();
        List<WmsStockLog> wmsStockLogList = wmsStockLogService.selectWmsStockLogList(wmsStockLog);
        return getDataTable(wmsStockLogList);
    }

    @PreAuthorize("@ss.hasPermi('business:stockLog:query')")
    @GetMapping(value = "/{stockLogId}")
    public AjaxResult getInfo(@PathVariable Long stockLogId)
    {
        return success(wmsStockLogService.selectWmsStockLogById(stockLogId));
    }

    @PreAuthorize("@ss.hasPermi('business:stockLog:add')")
    @Log(title = "库存流水", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody WmsStockLog wmsStockLog)
    {
        wmsStockLog.setCreateBy(getUsername());
        return toAjax(wmsStockLogService.insertWmsStockLog(wmsStockLog));
    }

    @PreAuthorize("@ss.hasPermi('business:stockLog:edit')")
    @Log(title = "库存流水", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WmsStockLog wmsStockLog)
    {
        return toAjax(wmsStockLogService.updateWmsStockLog(wmsStockLog));
    }

    @PreAuthorize("@ss.hasPermi('business:stockLog:remove')")
    @Log(title = "库存流水", businessType = BusinessType.DELETE)
    @DeleteMapping("/{stockLogIds}")
    public AjaxResult remove(@PathVariable Long[] stockLogIds)
    {
        return toAjax(wmsStockLogService.deleteWmsStockLogByIds(stockLogIds));
    }
}

