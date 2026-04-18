package com.canglian.web.controller.business;

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

    /**
     * 查询销售单据列表
     * 
     * @param salOrder 销售单据
     * @return 销售单据列表
     */
    @PreAuthorize("@ss.hasPermi('business:saleOrder:list')")
    @GetMapping("/list")
    public TableDataInfo list(SalOrder salOrder)
    {
        startPage();
        List<SalOrder> salOrderList = salOrderService.selectSalOrderList(salOrder);
        return getDataTable(salOrderList);
    }

    /**
     * 查询销售单据详情
     * 
     * @param orderId 销售单据id
     * @return 销售单据详情
     */
    @PreAuthorize("@ss.hasPermi('business:saleOrder:query')")
    @GetMapping("/{orderId}")
    public AjaxResult getInfo(@PathVariable Long orderId)
    {
        return success(salOrderService.selectSalOrderById(orderId));
    }

    /**
     * 新增销售单据
     * 
     * @param salOrder 销售单据
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:saleOrder:add')")
    @Log(title = "销售单据", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SalOrder salOrder)
    {
        salOrder.setCreateBy(getUsername());
        return toAjax(salOrderService.insertSalOrder(salOrder));
    }

    /**
     * 修改销售单据
     * 
     * @param salOrder 销售单据
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:saleOrder:edit')")
    @Log(title = "销售单据", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SalOrder salOrder)
    {
        salOrder.setUpdateBy(getUsername());
        return toAjax(salOrderService.updateSalOrder(salOrder));
    }

    /**
     * 审批销售单据
     * 
     * @param orderId 销售单据id
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:saleOrder:approve')")
    @Log(title = "销售单据审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approve/{orderId}")
    public AjaxResult approve(@PathVariable Long orderId)
    {
        return toAjax(salOrderService.approveSalOrder(orderId, getUsername()));
    }

    /**
     * 报价单下推销货订单
     * 
     * @param orderId 销售单据id
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:saleOrder:pushSale')")
    @Log(title = "报价单下推销货订单", businessType = BusinessType.UPDATE)
    @PutMapping("/pushSale/{orderId}")
    public AjaxResult pushSale(@PathVariable Long orderId)
    {
        return toAjax(salOrderService.pushQuoteToSale(orderId, getUsername()));
    }

    /**
     * 销货订单下推出库单
     * 
     * @param orderId 销售单据id
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:saleOrder:pushOutbound')")
    @Log(title = "销货订单下推出库单", businessType = BusinessType.UPDATE)
    @PutMapping("/pushOutbound/{orderId}")
    public AjaxResult pushOutbound(@PathVariable Long orderId)
    {
        return toAjax(salOrderService.pushSaleOrderToOutbound(orderId, getUsername()));
    }

    /**
     * 查询打印数据
     * 
     * @param orderId 销售单据id
     * @return 打印数据
     */
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
        return success(printData);
    }

    /**
     * 删除销售单据
     * 
     * @param orderIds 销售单据id集合
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:saleOrder:remove')")
    @Log(title = "销售单据", businessType = BusinessType.DELETE)
    @DeleteMapping("/{orderIds}")
    public AjaxResult remove(@PathVariable Long[] orderIds)
    {
        return toAjax(salOrderService.deleteSalOrderByIds(orderIds));
    }
}
