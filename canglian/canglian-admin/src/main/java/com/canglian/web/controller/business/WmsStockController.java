package com.canglian.web.controller.business;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;
import com.canglian.common.utils.poi.ExcelUtil;
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

    /**
     * 导入期初库存
     *
     * @param file 导入文件
     * @param updateSupport 是否更新已存在数据
     * @return 导入结果
     * @throws Exception 导入异常
     */
    @Log(title = "库存", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('business:stock:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, boolean updateSupport) throws Exception
    {
        ExcelUtil<WmsStock> excelUtil = new ExcelUtil<WmsStock>(WmsStock.class);
        List<WmsStock> stockList = excelUtil.importExcel(file.getInputStream());
        String message = wmsStockService.importWmsStock(stockList, updateSupport, getUsername());
        return success(message);
    }

    /**
     * 下载期初库存导入模板
     *
     * @param response 响应对象
     */
    @PreAuthorize("@ss.hasPermi('business:stock:import') or @ss.hasPermi('business:stock:importTemplate')")
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<WmsStock> excelUtil = new ExcelUtil<WmsStock>(WmsStock.class);
        excelUtil.importTemplateExcel(response, "期初库存数据");
    }

    @PreAuthorize("@ss.hasPermi('business:stock:list')")
    @GetMapping("/warning")
    public TableDataInfo warning(WmsStock wmsStock)
    {
        startPage();
        List<WmsStock> list = wmsStockService.selectWmsStockRiskList(wmsStock);
        return getDataTable(list);
    }

    /**
     * 查询效期预警库存（已过期与临期批次）
     */
    @PreAuthorize("@ss.hasPermi('business:stock:list')")
    @GetMapping("/expiryWarning")
    public TableDataInfo expiryWarning(WmsStock wmsStock)
    {
        startPage();
        List<WmsStock> list = wmsStockService.selectExpiryWarningList(wmsStock);
        return getDataTable(list);
    }

    /**
     * 查询 FEFO（先到期先出）推荐拣货批次
     */
    @PreAuthorize("@ss.hasPermi('business:stock:list')")
    @GetMapping("/fefo")
    public AjaxResult fefo(@RequestParam Long warehouseId, @RequestParam Long productId)
    {
        return success(wmsStockService.selectFefoBatchList(warehouseId, productId));
    }

    /**
     * 手工冻结库存
     */
    @PreAuthorize("@ss.hasPermi('business:stock:freeze')")
    @Log(title = "库存冻结", businessType = BusinessType.UPDATE)
    @PostMapping("/freeze")
    public AjaxResult freeze(@RequestBody Map<String, Object> payload)
    {
        Long stockId = payload.get("stockId") == null ? null : Long.valueOf(String.valueOf(payload.get("stockId")));
        BigDecimal qty = payload.get("quantity") == null ? null : new BigDecimal(String.valueOf(payload.get("quantity")));
        String reason = payload.get("reason") == null ? null : String.valueOf(payload.get("reason"));
        return success(wmsStockService.freezeStock(stockId, qty, reason, getUsername()));
    }

    /**
     * 手工解冻库存
     */
    @PreAuthorize("@ss.hasPermi('business:stock:freeze')")
    @Log(title = "库存解冻", businessType = BusinessType.UPDATE)
    @PostMapping("/unfreeze")
    public AjaxResult unfreeze(@RequestBody Map<String, Object> payload)
    {
        Long stockId = payload.get("stockId") == null ? null : Long.valueOf(String.valueOf(payload.get("stockId")));
        BigDecimal qty = payload.get("quantity") == null ? null : new BigDecimal(String.valueOf(payload.get("quantity")));
        String reason = payload.get("reason") == null ? null : String.valueOf(payload.get("reason"));
        return success(wmsStockService.unfreezeStock(stockId, qty, reason, getUsername()));
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

