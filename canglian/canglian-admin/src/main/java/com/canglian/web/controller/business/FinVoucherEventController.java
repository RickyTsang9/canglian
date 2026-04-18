package com.canglian.web.controller.business;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.business.domain.FinVoucherEvent;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;

/**
 * 凭证事件控制器
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/voucherEvent")
public class FinVoucherEventController extends BaseController
{
    @Autowired
    private IFinVoucherEventService finVoucherEventService;

    /**
     * 查询凭证事件列表
     * 
     * @param finVoucherEvent 凭证事件
     * @return 凭证事件列表
     */
    @PreAuthorize("@ss.hasPermi('business:voucherEvent:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinVoucherEvent finVoucherEvent)
    {
        startPage();
        List<FinVoucherEvent> finVoucherEventList = finVoucherEventService.selectFinVoucherEventList(finVoucherEvent);
        return getDataTable(finVoucherEventList);
    }

    /**
     * 查询凭证事件详情
     * 
     * @param voucherEventId 凭证事件id
     * @return 凭证事件详情
     */
    @PreAuthorize("@ss.hasPermi('business:voucherEvent:query')")
    @GetMapping("/{voucherEventId}")
    public AjaxResult getInfo(@PathVariable Long voucherEventId)
    {
        return success(finVoucherEventService.selectFinVoucherEventById(voucherEventId));
    }

    /**
     * 删除凭证事件
     * 
     * @param voucherEventIds 凭证事件id集合
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:voucherEvent:remove')")
    @Log(title = "凭证事件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{voucherEventIds}")
    public AjaxResult remove(@PathVariable Long[] voucherEventIds)
    {
        return toAjax(finVoucherEventService.deleteFinVoucherEventByIds(voucherEventIds));
    }
}
