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
import com.canglian.business.domain.PurOrder;
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

    /**
     * 查询购货订单列表
     * 
     * @param purOrder 购货订单
     * @return 购货订单列表
     */
    @PreAuthorize("@ss.hasPermi('business:purOrder:list')")
    @GetMapping("/list")
    public TableDataInfo list(PurOrder purOrder)
    {
        startPage();
        List<PurOrder> purOrderList = purOrderService.selectPurOrderList(purOrder);
        return getDataTable(purOrderList);
    }

    /**
     * 查询购货订单详情
     * 
     * @param purchaseOrderId 购货订单id
     * @return 购货订单详情
     */
    @PreAuthorize("@ss.hasPermi('business:purOrder:query')")
    @GetMapping("/{purchaseOrderId}")
    public AjaxResult getInfo(@PathVariable Long purchaseOrderId)
    {
        return success(purOrderService.selectPurOrderById(purchaseOrderId));
    }

    /**
     * 新增购货订单
     * 
     * @param purOrder 购货订单
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:purOrder:add')")
    @Log(title = "购货订单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody PurOrder purOrder)
    {
        purOrder.setCreateBy(getUsername());
        return toAjax(purOrderService.insertPurOrder(purOrder));
    }

    /**
     * 修改购货订单
     * 
     * @param purOrder 购货订单
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:purOrder:edit')")
    @Log(title = "购货订单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody PurOrder purOrder)
    {
        purOrder.setUpdateBy(getUsername());
        return toAjax(purOrderService.updatePurOrder(purOrder));
    }

    /**
     * 审批购货订单
     * 
     * @param purchaseOrderId 购货订单id
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:purOrder:approve')")
    @Log(title = "购货订单审批", businessType = BusinessType.UPDATE)
    @PutMapping("/approve/{purchaseOrderId}")
    public AjaxResult approve(@PathVariable Long purchaseOrderId)
    {
        return toAjax(purOrderService.approvePurOrder(purchaseOrderId, getUsername()));
    }

    /**
     * 下推入库单
     * 
     * @param purchaseOrderId 购货订单id
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:purOrder:pushInbound')")
    @Log(title = "购货订单下推入库单", businessType = BusinessType.UPDATE)
    @PutMapping("/pushInbound/{purchaseOrderId}")
    public AjaxResult pushInbound(@PathVariable Long purchaseOrderId)
    {
        return toAjax(purOrderService.pushPurOrderToInbound(purchaseOrderId, getUsername()));
    }

    /**
     * 查询打印数据
     * 
     * @param purchaseOrderId 购货订单id
     * @return 打印数据
     */
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
        return success(printData);
    }

    /**
     * 删除购货订单
     * 
     * @param purchaseOrderIds 购货订单id集合
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:purOrder:remove')")
    @Log(title = "购货订单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{purchaseOrderIds}")
    public AjaxResult remove(@PathVariable Long[] purchaseOrderIds)
    {
        return toAjax(purOrderService.deletePurOrderByIds(purchaseOrderIds));
    }
}
