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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;
import com.canglian.business.domain.WmsInbound;
import com.canglian.business.domain.WmsInboundItem;
import com.canglian.business.service.IWmsInboundItemService;
import com.canglian.business.service.IWmsInboundService;

/**
 * 入库单 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/inbound")
public class WmsInboundController extends BaseController
{
    @Autowired
    private IWmsInboundService wmsInboundService;

    @Autowired
    private IWmsInboundItemService wmsInboundItemService;

    /**
     * 获取入库单列表
     */
    @PreAuthorize("@ss.hasPermi('business:inbound:list')")
    @GetMapping("/list")
    public TableDataInfo list(WmsInbound wmsInbound)
    {
        startPage();
        List<WmsInbound> list = wmsInboundService.selectWmsInboundList(wmsInbound);
        return getDataTable(list);
    }

    /**
     * 根据入库单编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:inbound:query')")
    @GetMapping(value = "/{inboundId}")
    public AjaxResult getInfo(@PathVariable Long inboundId)
    {
        return success(wmsInboundService.selectWmsInboundById(inboundId));
    }

    @PreAuthorize("@ss.hasPermi('business:inbound:print')")
    @GetMapping("/print/{inboundId}")
    public AjaxResult printData(@PathVariable Long inboundId)
    {
        WmsInbound inbound = wmsInboundService.selectWmsInboundById(inboundId);
        if (inbound == null)
        {
            return AjaxResult.error("入库单不存在");
        }
        WmsInboundItem inboundItemQuery = new WmsInboundItem();
        inboundItemQuery.setInboundId(inboundId);
        List<WmsInboundItem> inboundItemList = wmsInboundItemService.selectWmsInboundItemList(inboundItemQuery);
        Map<String, Object> printData = new HashMap<String, Object>();
        printData.put("header", inbound);
        printData.put("items", inboundItemList);
        return success(printData);
    }

    /**
     * 新增入库单
     */
    @PreAuthorize("@ss.hasPermi('business:inbound:add')")
    @Log(title = "入库单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody WmsInbound wmsInbound)
    {
        wmsInbound.setCreateBy(getUsername());
        return toAjax(wmsInboundService.insertWmsInbound(wmsInbound));
    }

    /**
     * 修改入库单
     */
    @PreAuthorize("@ss.hasPermi('business:inbound:edit')")
    @Log(title = "入库单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WmsInbound wmsInbound)
    {
        wmsInbound.setUpdateBy(getUsername());
        return toAjax(wmsInboundService.updateWmsInbound(wmsInbound));
    }

    /**
     * 入库单审核
     */
    @PreAuthorize("@ss.hasPermi('business:inbound:audit')")
    @Log(title = "入库审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{inboundId}")
    public AjaxResult audit(@PathVariable Long inboundId)
    {
        return toAjax(wmsInboundService.auditWmsInbound(inboundId, getUsername()));
    }

    /**
     * 删除入库单
     */
    @PreAuthorize("@ss.hasPermi('business:inbound:remove')")
    @Log(title = "入库单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{inboundIds}")
    public AjaxResult remove(@PathVariable Long[] inboundIds)
    {
        return toAjax(wmsInboundService.deleteWmsInboundByIds(inboundIds));
    }
}

