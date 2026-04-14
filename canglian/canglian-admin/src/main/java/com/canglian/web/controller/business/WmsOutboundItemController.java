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
import com.canglian.business.domain.WmsOutboundItem;
import com.canglian.business.service.IWmsOutboundItemService;

@RestController
@RequestMapping("/business/outboundItem")
public class WmsOutboundItemController extends BaseController
{
    @Autowired
    private IWmsOutboundItemService wmsOutboundItemService;

    @PreAuthorize("@ss.hasPermi('business:outboundItem:list')")
    @GetMapping("/list")
    public TableDataInfo list(WmsOutboundItem wmsOutboundItem)
    {
        startPage();
        List<WmsOutboundItem> wmsOutboundItemList = wmsOutboundItemService.selectWmsOutboundItemList(wmsOutboundItem);
        return getDataTable(wmsOutboundItemList);
    }

    @PreAuthorize("@ss.hasPermi('business:outboundItem:query')")
    @GetMapping(value = "/{outboundItemId}")
    public AjaxResult getInfo(@PathVariable Long outboundItemId)
    {
        return success(wmsOutboundItemService.selectWmsOutboundItemById(outboundItemId));
    }

    @PreAuthorize("@ss.hasPermi('business:outboundItem:add')")
    @Log(title = "出库单明细", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody WmsOutboundItem wmsOutboundItem)
    {
        return toAjax(wmsOutboundItemService.insertWmsOutboundItem(wmsOutboundItem));
    }

    @PreAuthorize("@ss.hasPermi('business:outboundItem:edit')")
    @Log(title = "出库单明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WmsOutboundItem wmsOutboundItem)
    {
        return toAjax(wmsOutboundItemService.updateWmsOutboundItem(wmsOutboundItem));
    }

    @PreAuthorize("@ss.hasPermi('business:outboundItem:remove')")
    @Log(title = "出库单明细", businessType = BusinessType.DELETE)
    @DeleteMapping("/{outboundItemIds}")
    public AjaxResult remove(@PathVariable Long[] outboundItemIds)
    {
        return toAjax(wmsOutboundItemService.deleteWmsOutboundItemByIds(outboundItemIds));
    }
}

