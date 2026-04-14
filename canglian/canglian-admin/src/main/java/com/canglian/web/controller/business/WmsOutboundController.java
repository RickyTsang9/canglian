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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;
import com.canglian.business.domain.WmsOutbound;
import com.canglian.business.domain.WmsOutboundItem;
import com.canglian.business.service.IWmsOutboundService;
import com.canglian.business.service.IWmsOutboundItemService;
import com.canglian.business.domain.FinReceipt;
import com.canglian.business.service.IFinReceiptService;
import java.math.BigDecimal;

/**
 * 出库单 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/outbound")
public class WmsOutboundController extends BaseController
{
    @Autowired
    private IWmsOutboundService wmsOutboundService;

    @Autowired
    private IWmsOutboundItemService wmsOutboundItemService;

    @Autowired
    private IFinReceiptService finReceiptService;

    /**
     * 获取出库单列表
     */
    @PreAuthorize("@ss.hasPermi('business:outbound:list')")
    @GetMapping("/list")
    public TableDataInfo list(WmsOutbound wmsOutbound)
    {
        startPage();
        List<WmsOutbound> list = wmsOutboundService.selectWmsOutboundList(wmsOutbound);
        return getDataTable(list);
    }

    /**
     * 根据出库单编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:outbound:query')")
    @GetMapping(value = "/{outboundId}")
    public AjaxResult getInfo(@PathVariable Long outboundId)
    {
        return success(wmsOutboundService.selectWmsOutboundById(outboundId));
    }

    @PreAuthorize("@ss.hasPermi('business:outbound:print')")
    @GetMapping("/print/{outboundId}")
    public AjaxResult printData(@PathVariable Long outboundId)
    {
        WmsOutbound outbound = wmsOutboundService.selectWmsOutboundById(outboundId);
        if (outbound == null)
        {
            return AjaxResult.error("出库单不存在");
        }
        WmsOutboundItem outboundItemQuery = new WmsOutboundItem();
        outboundItemQuery.setOutboundId(outboundId);
        List<WmsOutboundItem> outboundItemList = wmsOutboundItemService.selectWmsOutboundItemList(outboundItemQuery);
        Map<String, Object> printData = new HashMap<String, Object>();
        printData.put("header", outbound);
        printData.put("items", outboundItemList);
        return success(printData);
    }

    /**
     * 新增出库单
     */
    @PreAuthorize("@ss.hasPermi('business:outbound:add')")
    @Log(title = "出库单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody WmsOutbound wmsOutbound)
    {
        wmsOutbound.setCreateBy(getUsername());
        return toAjax(wmsOutboundService.insertWmsOutbound(wmsOutbound));
    }

    /**
     * 修改出库单
     */
    @PreAuthorize("@ss.hasPermi('business:outbound:edit')")
    @Log(title = "出库单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WmsOutbound wmsOutbound)
    {
        wmsOutbound.setUpdateBy(getUsername());
        return toAjax(wmsOutboundService.updateWmsOutbound(wmsOutbound));
    }

    /**
     * 出库单审核
     */
    @PreAuthorize("@ss.hasPermi('business:outbound:audit')")
    @Log(title = "出库审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{outboundId}")
    public AjaxResult audit(@PathVariable Long outboundId)
    {
        return toAjax(wmsOutboundService.auditWmsOutbound(outboundId, getUsername()));
    }

    /**
     * 删除出库单
     */
    @PreAuthorize("@ss.hasPermi('business:outbound:remove')")
    @Log(title = "出库单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{outboundIds}")
    public AjaxResult remove(@PathVariable Long[] outboundIds)
    {
        return toAjax(wmsOutboundService.deleteWmsOutboundByIds(outboundIds));
    }

    /**
     * 出库单导入
     */
    @PreAuthorize("@ss.hasPermi('business:outbound:import')")
    @Log(title = "出库单导入", businessType = BusinessType.INSERT)
    @PostMapping("/import")
    public AjaxResult importOrder(@Validated @RequestBody WmsOutbound wmsOutbound)
    {
        wmsOutbound.setCreateBy(getUsername());
        return toAjax(wmsOutboundService.insertWmsOutbound(wmsOutbound));
    }

    /**
     * 出库单发货
     */
    @PreAuthorize("@ss.hasPermi('business:outbound:ship')")
    @Log(title = "出库发货", businessType = BusinessType.UPDATE)
    @PutMapping("/ship/{outboundId}")
    public AjaxResult ship(@PathVariable Long outboundId, @RequestBody WmsOutbound payload)
    {
        WmsOutbound outbound = wmsOutboundService.selectWmsOutboundById(outboundId);
        if (outbound == null)
        {
            return AjaxResult.error("出库单不存在");
        }
        outbound.setCarrier(payload.getCarrier());
        outbound.setWaybillNo(payload.getWaybillNo());
        outbound.setFreightCost(payload.getFreightCost());
        outbound.setDeliveryStatus("1");
        outbound.setUpdateBy(getUsername());
        return toAjax(wmsOutboundService.updateWmsOutbound(outbound));
    }

    /**
     * 出库单签收
     */
    @PreAuthorize("@ss.hasPermi('business:outbound:sign')")
    @Log(title = "出库签收", businessType = BusinessType.UPDATE)
    @PutMapping("/sign/{outboundId}")
    public AjaxResult sign(@PathVariable Long outboundId)
    {
        WmsOutbound outbound = wmsOutboundService.selectWmsOutboundById(outboundId);
        if (outbound == null)
        {
            return AjaxResult.error("出库单不存在");
        }
        outbound.setDeliveryStatus("2");
        outbound.setUpdateBy(getUsername());
        return toAjax(wmsOutboundService.updateWmsOutbound(outbound));
    }

    /**
     * 出库单退货
     */
    @PreAuthorize("@ss.hasPermi('business:outbound:return')")
    @Log(title = "出库退货", businessType = BusinessType.UPDATE)
    @PutMapping("/return/{outboundId}")
    public AjaxResult returnOrder(@PathVariable Long outboundId, @RequestParam(required = false) BigDecimal refundAmount, @RequestParam(required = false) Long receivableId)
    {
        WmsOutbound outbound = wmsOutboundService.selectWmsOutboundById(outboundId);
        if (outbound == null)
        {
            return AjaxResult.error("出库单不存在");
        }
        outbound.setDeliveryStatus("3");
        outbound.setUpdateBy(getUsername());
        int rows = wmsOutboundService.updateWmsOutbound(outbound);
        if (rows > 0 && refundAmount != null && refundAmount.compareTo(BigDecimal.ZERO) > 0 && outbound.getCustomerId() != null)
        {
            FinReceipt finReceipt = new FinReceipt();
            finReceipt.setReceiptNo("RF-" + System.currentTimeMillis());
            finReceipt.setCustomerId(outbound.getCustomerId());
            finReceipt.setReceivableId(receivableId);
            finReceipt.setAmount(refundAmount.negate());
            finReceipt.setReceiptDate(new java.sql.Date(System.currentTimeMillis()));
            finReceipt.setPayChannel("refund");
            finReceipt.setCreateBy(getUsername());
            finReceiptService.insertFinReceipt(finReceipt);
        }
        return toAjax(rows);
    }
}

