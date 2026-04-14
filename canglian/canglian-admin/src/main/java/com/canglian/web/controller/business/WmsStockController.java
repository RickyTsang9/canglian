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
import com.canglian.business.domain.WmsStock;
import com.canglian.business.service.IWmsStockService;

/**
 * 库存 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/stock")
public class WmsStockController extends BaseController
{
    @Autowired
    private IWmsStockService wmsStockService;

    /**
     * 获取库存列表
     */
    @PreAuthorize("@ss.hasPermi('business:stock:list')")
    @GetMapping("/list")
    public TableDataInfo list(WmsStock wmsStock)
    {
        startPage();
        List<WmsStock> list = wmsStockService.selectWmsStockList(wmsStock);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('business:stock:list')")
    @GetMapping("/warning")
    public TableDataInfo warning(WmsStock wmsStock)
    {
        startPage();
        List<WmsStock> list = wmsStockService.selectWmsStockWarningList(wmsStock);
        return getDataTable(list);
    }

    /**
     * 根据库存编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:stock:query')")
    @GetMapping(value = "/{stockId}")
    public AjaxResult getInfo(@PathVariable Long stockId)
    {
        return success(wmsStockService.selectWmsStockById(stockId));
    }

    /**
     * 新增库存
     */
    @PreAuthorize("@ss.hasPermi('business:stock:add')")
    @Log(title = "库存", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody WmsStock wmsStock)
    {
        wmsStock.setCreateBy(getUsername());
        return toAjax(wmsStockService.insertWmsStock(wmsStock));
    }

    /**
     * 修改库存
     */
    @PreAuthorize("@ss.hasPermi('business:stock:edit')")
    @Log(title = "库存", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WmsStock wmsStock)
    {
        wmsStock.setUpdateBy(getUsername());
        return toAjax(wmsStockService.updateWmsStock(wmsStock));
    }

    /**
     * 删除库存
     */
    @PreAuthorize("@ss.hasPermi('business:stock:remove')")
    @Log(title = "库存", businessType = BusinessType.DELETE)
    @DeleteMapping("/{stockIds}")
    public AjaxResult remove(@PathVariable Long[] stockIds)
    {
        return toAjax(wmsStockService.deleteWmsStockByIds(stockIds));
    }
}

