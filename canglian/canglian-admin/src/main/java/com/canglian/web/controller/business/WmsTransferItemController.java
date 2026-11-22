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
import com.canglian.business.domain.WmsTransferItem;
import com.canglian.business.service.IWmsTransferItemService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;

@RestController
@RequestMapping("/business/transferItem")
public class WmsTransferItemController extends BaseController
{
    @Autowired
    private IWmsTransferItemService wmsTransferItemService;

    @PreAuthorize("@ss.hasPermi('business:transferItem:list')")
    @GetMapping("/list")
    public TableDataInfo list(WmsTransferItem query)
    {
        startPage();
        List<WmsTransferItem> rows = wmsTransferItemService.selectWmsTransferItemList(query);
        return getDataTable(rows);
    }

    @PreAuthorize("@ss.hasPermi('business:transferItem:query')")
    @GetMapping("/{transferItemId}")
    public AjaxResult getInfo(@PathVariable Long transferItemId)
    {
        return success(wmsTransferItemService.selectWmsTransferItemById(transferItemId));
    }

    @PreAuthorize("@ss.hasPermi('business:transferItem:add')")
    @Log(title = "调拨单明细", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody WmsTransferItem item)
    {
        return toAjax(wmsTransferItemService.insertWmsTransferItem(item));
    }

    @PreAuthorize("@ss.hasPermi('business:transferItem:edit')")
    @Log(title = "调拨单明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WmsTransferItem item)
    {
        return toAjax(wmsTransferItemService.updateWmsTransferItem(item));
    }

    @PreAuthorize("@ss.hasPermi('business:transferItem:remove')")
    @Log(title = "调拨单明细", businessType = BusinessType.DELETE)
    @DeleteMapping("/{transferItemIds}")
    public AjaxResult remove(@PathVariable Long[] transferItemIds)
    {
        return toAjax(wmsTransferItemService.deleteWmsTransferItemByIds(transferItemIds));
    }
}
