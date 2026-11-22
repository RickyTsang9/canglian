package com.canglian.web.controller.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.canglian.business.domain.SalOrder;
import com.canglian.business.domain.SalOrderItem;
import com.canglian.business.service.ISalOrderService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;

/**
 * 销售单据控制器
 *
 * @author canglian
 */
@RestController
@RequestMapping("/business/saleOrder")
public class SalOrderController extends BaseController
{
    @Autowired
    private ISalOrderService salOrderService;

    @Autowired(required = false)
    private PrintPayloadEnricher printPayloadEnricher;

    @PreAuthorize("@ss.hasPermi('business:saleOrder:list')")
    @GetMapping("/list")
    public TableDataInfo list(SalOrder salOrder)
    {
        startPage();
        List<SalOrder> salOrderList = salOrderService.selectSalOrderList(salOrder);
        return getDataTable(salOrderList);
    }

    @PreAuthorize("@ss.hasPermi('business:saleOrder:query')")
    @GetMapping("/{orderId}")
    public AjaxResult getInfo(@PathVariable Long orderId)
    {
        return success(salOrderService.selectSalOrderById(orderId));
    }

    @PreAuthorize("@ss.hasPermi('business:saleOrder:add')")
    @Log(title = "销售单据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SalOrder salOrder)
    {
        salOrder.setCreateBy(getUsername());
        return toAjax(salOrderService.insertSalOrder(salOrder));
    }

    @PreAuthorize("@ss.hasPermi('business:saleOrder:edit')")
    @Log(title = "销售单据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SalOrder salOrder)
    {
        salOrder.setUpdateBy(getUsername());
        return toAjax(salOrderService.updateSalOrder(salOrder));
    }

    @PreAuthorize("@ss.hasPermi('business:saleOrder:approve')")
    @Log(title = "销售单据审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approve/{orderId}")
    public AjaxResult approve(@PathVariable Long orderId)
    {
        return toAjax(salOrderService.approveSalOrder(orderId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:saleOrder:close') or @ss.hasPermi('business:saleOrder:approve')")
    @Log(title = "销售单据反审批", businessType = BusinessType.UPDATE)
    @PutMapping("/unapprove/{orderId}")
    public AjaxResult unapprove(@PathVariable Long orderId)
    {
        return toAjax(salOrderService.unapproveSalOrder(orderId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:saleOrder:close') or @ss.hasPermi('business:saleOrder:approve')")
    @Log(title = "销售单据强制关闭", businessType = BusinessType.UPDATE)
    @PutMapping("/close/{orderId}")
    public AjaxResult close(@PathVariable Long orderId, @RequestBody(required = false) Map<String, Object> body)
    {
        String reason = body != null && body.get("closeReason") != null ? String.valueOf(body.get("closeReason")) : null;
        return toAjax(salOrderService.closeSalOrder(orderId, reason, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:saleOrder:pushSale')")
    @Log(title = "报价单下推销货订单", businessType = BusinessType.UPDATE)
    @PutMapping("/pushSale/{orderId}")
    public AjaxResult pushSale(@PathVariable Long orderId)
    {
        return toAjax(salOrderService.pushQuoteToSale(orderId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:saleOrder:pushOutbound')")
    @Log(title = "销货订单下推出库单", businessType = BusinessType.UPDATE)
    @PutMapping("/pushOutbound/{orderId}")
    public AjaxResult pushOutbound(@PathVariable Long orderId,
                                   @RequestBody(required = false) List<Map<String, Object>> pushItems)
    {
        return toAjax(salOrderService.pushSaleOrderToOutbound(orderId, pushItems, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:saleOrder:print')")
    @GetMapping("/print/{orderId}")
    public AjaxResult printData(@PathVariable Long orderId)
    {
        SalOrder salOrder = salOrderService.selectSalOrderById(orderId);
        if (salOrder == null)
        {
            return AjaxResult.error("销售单据不存在");
        }
        Map<String, Object> printData = new HashMap<String, Object>();
        printData.put("header", salOrder);
        printData.put("items", salOrder.getOrderItemList());
        if (printPayloadEnricher != null)
        {
            List<Long> pids = new ArrayList<Long>();
            if (salOrder.getOrderItemList() != null)
            {
                for (SalOrderItem item : salOrder.getOrderItemList())
                {
                    if (item.getProductId() != null)
                    {
                        pids.add(item.getProductId());
                    }
                }
            }
            printData.put("displayNames", printPayloadEnricher.buildDisplayNames(
                printPayloadEnricher.resolveCustomerName(salOrder.getCustomerId()),
                printPayloadEnricher.resolveWarehouseName(salOrder.getWarehouseId()),
                null, null, pids, null));
        }
        return success(printData);
    }

    @PreAuthorize("@ss.hasPermi('business:saleOrder:remove')")
    @Log(title = "销售单据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(salOrderService.deleteSalOrderByIds(orderIds));
    }
}
