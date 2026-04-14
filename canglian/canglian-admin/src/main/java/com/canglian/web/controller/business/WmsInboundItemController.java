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
import com.canglian.business.domain.WmsInboundItem;
import com.canglian.business.service.IWmsInboundItemService;

@RestController
@RequestMapping("/business/inboundItem")
public class WmsInboundItemController extends BaseController
{
    @Autowired
    private IWmsInboundItemService wmsInboundItemService;

    @PreAuthorize("@ss.hasPermi('business:inboundItem:list')")
    @GetMapping("/list")
    public TableDataInfo list(WmsInboundItem wmsInboundItem)
    {
        startPage();
        List<WmsInboundItem> wmsInboundItemList = wmsInboundItemService.selectWmsInboundItemList(wmsInboundItem);
        return getDataTable(wmsInboundItemList);
    }

    @PreAuthorize("@ss.hasPermi('business:inboundItem:query')")
    @GetMapping(value = "/{inboundItemId}")
    public AjaxResult getInfo(@PathVariable Long inboundItemId)
    {
        return success(wmsInboundItemService.selectWmsInboundItemById(inboundItemId));
    }

    @PreAuthorize("@ss.hasPermi('business:inboundItem:add')")
    @Log(title = "入库单明细", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody WmsInboundItem wmsInboundItem)
    {
        return toAjax(wmsInboundItemService.insertWmsInboundItem(wmsInboundItem));
    }

    @PreAuthorize("@ss.hasPermi('business:inboundItem:edit')")
    @Log(title = "入库单明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WmsInboundItem wmsInboundItem)
    {
        return toAjax(wmsInboundItemService.updateWmsInboundItem(wmsInboundItem));
    }

    @PreAuthorize("@ss.hasPermi('business:inboundItem:remove')")
    @Log(title = "入库单明细", businessType = BusinessType.DELETE)
    @DeleteMapping("/{inboundItemIds}")
    public AjaxResult remove(@PathVariable Long[] inboundItemIds)
    {
        return toAjax(wmsInboundItemService.deleteWmsInboundItemByIds(inboundItemIds));
    }
}

