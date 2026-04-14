package com.canglian.web.controller.business;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
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
import com.canglian.business.domain.WmsTransfer;
import com.canglian.business.domain.WmsTransferItem;
import com.canglian.business.service.IWmsTransferItemService;
import com.canglian.business.service.IWmsTransferService;

@RestController
@RequestMapping("/business/transfer")
public class WmsTransferController extends BaseController
{
    @Autowired
    private IWmsTransferService wmsTransferService;

    @Autowired
    private IWmsTransferItemService wmsTransferItemService;

    @PreAuthorize("@ss.hasPermi('business:transfer:list')")
    @GetMapping("/list")
    public TableDataInfo list(WmsTransfer wmsTransfer)
    {
        startPage();
        List<WmsTransfer> wmsTransferList = wmsTransferService.selectWmsTransferList(wmsTransfer);
        return getDataTable(wmsTransferList);
    }

    @PreAuthorize("@ss.hasPermi('business:transfer:query')")
    @GetMapping(value = "/{transferId}")
    public AjaxResult getInfo(@PathVariable Long transferId)
    {
        return success(wmsTransferService.selectWmsTransferById(transferId));
    }

    @PreAuthorize("@ss.hasPermi('business:transfer:print')")
    @GetMapping("/print/{transferId}")
    public AjaxResult printData(@PathVariable Long transferId)
    {
        WmsTransfer transfer = wmsTransferService.selectWmsTransferById(transferId);
        if (transfer == null)
        {
            return AjaxResult.error("调拨单不存在");
        }
        WmsTransferItem transferItemQuery = new WmsTransferItem();
        transferItemQuery.setTransferId(transferId);
        List<WmsTransferItem> transferItemList = wmsTransferItemService.selectWmsTransferItemList(transferItemQuery);
        Map<String, Object> printData = new HashMap<String, Object>();
        printData.put("header", transfer);
        printData.put("items", transferItemList);
        return success(printData);
    }

    @PreAuthorize("@ss.hasPermi('business:transfer:add')")
    @Log(title = "调拨单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody WmsTransfer wmsTransfer)
    {
        wmsTransfer.setCreateBy(getUsername());
        return toAjax(wmsTransferService.insertWmsTransfer(wmsTransfer));
    }

    @PreAuthorize("@ss.hasPermi('business:transfer:edit')")
    @Log(title = "调拨单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WmsTransfer wmsTransfer)
    {
        wmsTransfer.setUpdateBy(getUsername());
        return toAjax(wmsTransferService.updateWmsTransfer(wmsTransfer));
    }

    @PreAuthorize("@ss.hasPermi('business:transfer:remove')")
    @Log(title = "调拨单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{transferIds}")
    public AjaxResult remove(@PathVariable Long[] transferIds)
    {
        return toAjax(wmsTransferService.deleteWmsTransferByIds(transferIds));
    }

    /**
     * 调拨单审核
     */
    @PreAuthorize("@ss.hasPermi('business:transfer:audit')")
    @Log(title = "调拨审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{transferId}")
    public AjaxResult audit(@PathVariable Long transferId)
    {
        return toAjax(wmsTransferService.auditWmsTransfer(transferId, getUsername()));
    }
}

