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
import com.canglian.business.domain.WmsPurchaseReturnItem;
import com.canglian.business.service.IWmsPurchaseReturnItemService;

/**
 * 采购退货明细 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/purchaseReturnItem")
public class WmsPurchaseReturnItemController extends BaseController
{
    @Autowired
    private IWmsPurchaseReturnItemService wmsPurchaseReturnItemService;

    /**
     * 获取采购退货明细列表
     */
    @PreAuthorize("@ss.hasPermi('business:purchaseReturnItem:list')")
    @GetMapping("/list")
    public TableDataInfo list(WmsPurchaseReturnItem wmsPurchaseReturnItem)
    {
        startPage();
        List<WmsPurchaseReturnItem> list = wmsPurchaseReturnItemService.selectWmsPurchaseReturnItemList(wmsPurchaseReturnItem);
        return getDataTable(list);
    }

    /**
     * 获取采购退货明细详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:purchaseReturnItem:query')")
    @GetMapping(value = "/{purchaseReturnItemId}")
    public AjaxResult getInfo(@PathVariable Long purchaseReturnItemId)
    {
        return success(wmsPurchaseReturnItemService.selectWmsPurchaseReturnItemById(purchaseReturnItemId));
    }

    /**
     * 新增采购退货明细
     */
    @PreAuthorize("@ss.hasPermi('business:purchaseReturnItem:add')")
    @Log(title = "采购退货明细", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody WmsPurchaseReturnItem wmsPurchaseReturnItem)
    {
        return toAjax(wmsPurchaseReturnItemService.insertWmsPurchaseReturnItem(wmsPurchaseReturnItem));
    }

    /**
     * 修改采购退货明细
     */
    @PreAuthorize("@ss.hasPermi('business:purchaseReturnItem:edit')")
    @Log(title = "采购退货明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WmsPurchaseReturnItem wmsPurchaseReturnItem)
    {
        return toAjax(wmsPurchaseReturnItemService.updateWmsPurchaseReturnItem(wmsPurchaseReturnItem));
    }

    /**
     * 删除采购退货明细
     */
    @PreAuthorize("@ss.hasPermi('business:purchaseReturnItem:remove')")
    @Log(title = "采购退货明细", businessType = BusinessType.DELETE)
    @DeleteMapping("/{purchaseReturnItemIds}")
    public AjaxResult remove(@PathVariable Long[] purchaseReturnItemIds)
    {
        return toAjax(wmsPurchaseReturnItemService.deleteWmsPurchaseReturnItemByIds(purchaseReturnItemIds));
    }
}

