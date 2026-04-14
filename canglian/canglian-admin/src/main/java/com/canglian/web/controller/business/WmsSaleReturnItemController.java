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
import com.canglian.business.domain.WmsSaleReturnItem;
import com.canglian.business.service.IWmsSaleReturnItemService;

/**
 * 销售退货明细 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/saleReturnItem")
public class WmsSaleReturnItemController extends BaseController
{
    @Autowired
    private IWmsSaleReturnItemService wmsSaleReturnItemService;

    /**
     * 获取销售退货明细列表
     */
    @PreAuthorize("@ss.hasPermi('business:saleReturnItem:list')")
    @GetMapping("/list")
    public TableDataInfo list(WmsSaleReturnItem wmsSaleReturnItem)
    {
        startPage();
        List<WmsSaleReturnItem> list = wmsSaleReturnItemService.selectWmsSaleReturnItemList(wmsSaleReturnItem);
        return getDataTable(list);
    }

    /**
     * 获取销售退货明细详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:saleReturnItem:query')")
    @GetMapping(value = "/{saleReturnItemId}")
    public AjaxResult getInfo(@PathVariable Long saleReturnItemId)
    {
        return success(wmsSaleReturnItemService.selectWmsSaleReturnItemById(saleReturnItemId));
    }

    /**
     * 新增销售退货明细
     */
    @PreAuthorize("@ss.hasPermi('business:saleReturnItem:add')")
    @Log(title = "销售退货明细", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody WmsSaleReturnItem wmsSaleReturnItem)
    {
        return toAjax(wmsSaleReturnItemService.insertWmsSaleReturnItem(wmsSaleReturnItem));
    }

    /**
     * 修改销售退货明细
     */
    @PreAuthorize("@ss.hasPermi('business:saleReturnItem:edit')")
    @Log(title = "销售退货明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WmsSaleReturnItem wmsSaleReturnItem)
    {
        return toAjax(wmsSaleReturnItemService.updateWmsSaleReturnItem(wmsSaleReturnItem));
    }

    /**
     * 删除销售退货明细
     */
    @PreAuthorize("@ss.hasPermi('business:saleReturnItem:remove')")
    @Log(title = "销售退货明细", businessType = BusinessType.DELETE)
    @DeleteMapping("/{saleReturnItemIds}")
    public AjaxResult remove(@PathVariable Long[] saleReturnItemIds)
    {
        return toAjax(wmsSaleReturnItemService.deleteWmsSaleReturnItemByIds(saleReturnItemIds));
    }
}

