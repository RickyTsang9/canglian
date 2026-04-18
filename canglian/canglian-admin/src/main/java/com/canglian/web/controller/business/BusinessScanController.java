package com.canglian.web.controller.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.business.domain.ScanInventoryCheckRequest;
import com.canglian.business.domain.ScanQuickSaleRequest;
import com.canglian.business.domain.ScanTransferRequest;
import com.canglian.business.service.IBusinessScanService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.enums.BusinessType;

/**
 * 业务扫码控制器
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/scan")
public class BusinessScanController extends BaseController
{
    @Autowired
    private IBusinessScanService businessScanService;

    /**
     * 扫码解析商品
     * 
     * @param barCode 商品条码
     * @return 商品扫码结果
     */
    @PreAuthorize("@ss.hasPermi('business:scan:query')")
    @GetMapping("/product/{barCode}")
    public AjaxResult product(@PathVariable String barCode)
    {
        return success(businessScanService.scanProduct(barCode));
    }

    /**
     * 扫码解析库位
     * 
     * @param locationCode 库位编码
     * @return 库位扫码结果
     */
    @PreAuthorize("@ss.hasPermi('business:scan:query')")
    @GetMapping("/location/{locationCode}")
    public AjaxResult location(@PathVariable String locationCode)
    {
        return success(businessScanService.scanLocation(locationCode));
    }

    /**
     * 扫码查询库存
     * 
     * @param barCode 商品条码
     * @param warehouseId 仓库编号
     * @param locationCode 库位编码
     * @return 库存扫码结果
     */
    @PreAuthorize("@ss.hasPermi('business:scan:query')")
    @GetMapping("/stock")
    public AjaxResult stock(@RequestParam String barCode, @RequestParam Long warehouseId, @RequestParam(required = false) String locationCode)
    {
        return success(businessScanService.scanStock(barCode, warehouseId, locationCode));
    }

    /**
     * 扫码快速开单
     * 
     * @param scanQuickSaleRequest 扫码开单请求
     * @return 销货订单
     */
    @PreAuthorize("@ss.hasPermi('business:scan:add')")
    @Log(title = "扫码开单", businessType = BusinessType.INSERT)
    @PostMapping("/quickSale")
    public AjaxResult quickSale(@Validated @RequestBody ScanQuickSaleRequest scanQuickSaleRequest)
    {
        return success(businessScanService.createQuickSaleOrder(scanQuickSaleRequest, getUsername()));
    }

    /**
     * 扫码快速生成盘点单
     * 
     * @param scanInventoryCheckRequest 扫码盘点请求
     * @return 盘点单
     */
    @PreAuthorize("@ss.hasPermi('business:scan:inventory')")
    @Log(title = "扫码盘点建单", businessType = BusinessType.INSERT)
    @PostMapping("/inventoryCheck")
    public AjaxResult inventoryCheck(@Validated @RequestBody ScanInventoryCheckRequest scanInventoryCheckRequest)
    {
        return success(businessScanService.createInventoryCheck(scanInventoryCheckRequest, getUsername()));
    }

    /**
     * 扫码快速生成调拨单
     * 
     * @param scanTransferRequest 扫码调拨请求
     * @return 调拨单
     */
    @PreAuthorize("@ss.hasPermi('business:scan:transfer')")
    @Log(title = "扫码调拨建单", businessType = BusinessType.INSERT)
    @PostMapping("/transfer")
    public AjaxResult transfer(@Validated @RequestBody ScanTransferRequest scanTransferRequest)
    {
        return success(businessScanService.createTransfer(scanTransferRequest, getUsername()));
    }
}
