package com.canglian.web.controller.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.canglian.common.utils.poi.ExcelUtil;
import javax.servlet.http.HttpServletResponse;
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
import com.canglian.business.domain.PurOrder;
import com.canglian.business.domain.PurOrderItem;
import com.canglian.business.service.IPurOrderService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;

/**
 * 购货订单控制器
 *
 * @author canglian
 */
@RestController
@RequestMapping("/business/purOrder")
public class PurOrderController extends BaseController
{
    @Autowired
    private IPurOrderService purOrderService;

    @Autowired(required = false)
    private PrintPayloadEnricher printPayloadEnricher;

    @PreAuthorize("@ss.hasPermi('business:purOrder:list')")
    @GetMapping("/list")
    public TableDataInfo list(PurOrder purOrder)
    {
        startPage();
        List<PurOrder> purOrderList = purOrderService.selectPurOrderList(purOrder);
        return getDataTable(purOrderList);
    }

    @PreAuthorize("@ss.hasPermi('business:purOrder:query')")
    @GetMapping("/{purchaseOrderId}")
    public AjaxResult getInfo(@PathVariable Long purchaseOrderId)
    {
        return success(purOrderService.selectPurOrderById(purchaseOrderId));
    }

    @PreAuthorize("@ss.hasPermi('business:purOrder:add')")
    @Log(title = "购货订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody PurOrder purOrder)
    {
        purOrder.setCreateBy(getUsername());
        return toAjax(purOrderService.insertPurOrder(purOrder));
    }

    @PreAuthorize("@ss.hasPermi('business:purOrder:edit')")
    @Log(title = "购货订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody PurOrder purOrder)
    {
        purOrder.setUpdateBy(getUsername());
        return toAjax(purOrderService.updatePurOrder(purOrder));
    }

    @PreAuthorize("@ss.hasPermi('business:purOrder:approve')")
    @Log(title = "购货订单审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approve/{purchaseOrderId}")
    public AjaxResult approve(@PathVariable Long purchaseOrderId)
    {
        return toAjax(purOrderService.approvePurOrder(purchaseOrderId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:purOrder:close') or @ss.hasPermi('business:purOrder:approve')")
    @Log(title = "购货订单反审批", businessType = BusinessType.UPDATE)
    @PutMapping("/unapprove/{purchaseOrderId}")
    public AjaxResult unapprove(@PathVariable Long purchaseOrderId)
    {
        return toAjax(purOrderService.unapprovePurOrder(purchaseOrderId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:purOrder:close') or @ss.hasPermi('business:purOrder:approve')")
    @Log(title = "购货订单强制关闭", businessType = BusinessType.UPDATE)
    @PutMapping("/close/{purchaseOrderId}")
    public AjaxResult close(@PathVariable Long purchaseOrderId, @RequestBody(required = false) Map<String, Object> body)
    {
        String reason = body != null && body.get("closeReason") != null ? String.valueOf(body.get("closeReason")) : null;
        return toAjax(purOrderService.closePurOrder(purchaseOrderId, reason, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:purOrder:pushInbound')")
    @Log(title = "购货订单下推入库单", businessType = BusinessType.UPDATE)
    @PutMapping("/pushInbound/{purchaseOrderId}")
    public AjaxResult pushInbound(@PathVariable Long purchaseOrderId,
                                  @RequestBody(required = false) List<Map<String, Object>> pushItems)
    {
        return toAjax(purOrderService.pushPurOrderToInbound(purchaseOrderId, pushItems, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:purOrder:print')")
    @GetMapping("/print/{purchaseOrderId}")
    public AjaxResult printData(@PathVariable Long purchaseOrderId)
    {
        PurOrder purOrder = purOrderService.selectPurOrderById(purchaseOrderId);
        if (purOrder == null)
        {
            return AjaxResult.error("购货订单不存在");
        }
        Map<String, Object> printData = new HashMap<String, Object>();
        printData.put("header", purOrder);
        printData.put("items", purOrder.getOrderItemList());
        if (printPayloadEnricher != null)
        {
            List<Long> pids = new ArrayList<Long>();
            if (purOrder.getOrderItemList() != null)
            {
                for (PurOrderItem item : purOrder.getOrderItemList())
                {
                    if (item.getProductId() != null)
                    {
                        pids.add(item.getProductId());
                    }
                }
            }
            printData.put("displayNames", printPayloadEnricher.buildDisplayNames(
                printPayloadEnricher.resolveSupplierName(purOrder.getSupplierId()),
                printPayloadEnricher.resolveWarehouseName(purOrder.getWarehouseId()),
                null, null, pids, null));
        }
        return success(printData);
    }

    @PreAuthorize("@ss.hasPermi('business:purOrder:remove')")
    @Log(title = "购货订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{purchaseOrderIds}")
    public AjaxResult remove(@PathVariable Long[] purchaseOrderIds)
    {
        return toAjax(purOrderService.deletePurOrderByIds(purchaseOrderIds));
    }

    /**
     * 导出采购订单
     */
    @Log(title = "采购订单", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('business:purOrder:export') or @ss.hasPermi('business:purOrder:list')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, PurOrder purOrder)
    {
        List<PurOrder> list = purOrderService.selectPurOrderList(purOrder);
        ExcelUtil<PurOrder> util = new ExcelUtil<PurOrder>(PurOrder.class);
        util.exportExcel(response, list, "采购订单");
    }
}
