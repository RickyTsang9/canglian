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
import com.canglian.business.domain.WmsInventoryCheckItem;
import com.canglian.business.service.IWmsInventoryCheckItemService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;

@RestController
@RequestMapping("/business/inventoryCheckItem")
public class WmsInventoryCheckItemController extends BaseController
{
    @Autowired
    private IWmsInventoryCheckItemService wmsInventoryCheckItemService;

    @PreAuthorize("@ss.hasPermi('business:inventoryCheckItem:list')")
    @GetMapping("/list")
    public TableDataInfo list(WmsInventoryCheckItem query)
    {
        startPage();
        List<WmsInventoryCheckItem> rows = wmsInventoryCheckItemService.selectWmsInventoryCheckItemList(query);
        return getDataTable(rows);
    }

    @PreAuthorize("@ss.hasPermi('business:inventoryCheckItem:query')")
    @GetMapping("/{checkItemId}")
    public AjaxResult getInfo(@PathVariable Long checkItemId)
    {
        return success(wmsInventoryCheckItemService.selectWmsInventoryCheckItemById(checkItemId));
    }

    @PreAuthorize("@ss.hasPermi('business:inventoryCheckItem:add')")
    @Log(title = "盘点单明细", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody WmsInventoryCheckItem item)
    {
        return toAjax(wmsInventoryCheckItemService.insertWmsInventoryCheckItem(item));
    }

    @PreAuthorize("@ss.hasPermi('business:inventoryCheckItem:edit')")
    @Log(title = "盘点单明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WmsInventoryCheckItem item)
    {
        return toAjax(wmsInventoryCheckItemService.updateWmsInventoryCheckItem(item));
    }

    @PreAuthorize("@ss.hasPermi('business:inventoryCheckItem:remove')")
    @Log(title = "盘点单明细", businessType = BusinessType.DELETE)
    @DeleteMapping("/{checkItemIds}")
    public AjaxResult remove(@PathVariable Long[] checkItemIds)
    {
        return toAjax(wmsInventoryCheckItemService.deleteWmsInventoryCheckItemByIds(checkItemIds));
    }
}
