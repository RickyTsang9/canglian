package com.canglian.web.controller.business;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.business.domain.FinVoucherEntry;
import com.canglian.business.domain.FinVoucherEvent;
import com.canglian.business.service.IFinVoucherEventService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;
import com.canglian.common.utils.poi.ExcelUtil;

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

    @PreAuthorize("@ss.hasPermi('business:voucherEvent:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinVoucherEvent finVoucherEvent)
    {
        startPage();
        List<FinVoucherEvent> finVoucherEventList = finVoucherEventService.selectFinVoucherEventList(finVoucherEvent);
        return getDataTable(finVoucherEventList);
    }

    @PreAuthorize("@ss.hasPermi('business:voucherEvent:query')")
    @GetMapping("/{voucherEventId}")
    public AjaxResult getInfo(@PathVariable Long voucherEventId)
    {
        return success(finVoucherEventService.selectFinVoucherEventById(voucherEventId));
    }

    @PreAuthorize("@ss.hasPermi('business:voucherEvent:query')")
    @GetMapping("/{voucherEventId}/entries")
    public AjaxResult getEntries(@PathVariable Long voucherEventId)
    {
        return success(finVoucherEventService.selectVoucherEntries(voucherEventId));
    }

    @PreAuthorize("@ss.hasPermi('business:voucherEvent:generate')")
    @Log(title = "凭证生成", businessType = BusinessType.UPDATE)
    @PutMapping("/generate/{voucherEventId}")
    public AjaxResult generate(@PathVariable Long voucherEventId)
    {
        return toAjax(finVoucherEventService.generateVoucher(voucherEventId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:voucherEvent:writeback')")
    @Log(title = "凭证回写", businessType = BusinessType.UPDATE)
    @PutMapping("/writeback/{voucherEventId}")
    public AjaxResult writeback(@PathVariable Long voucherEventId)
    {
        return toAjax(finVoucherEventService.writebackVoucher(voucherEventId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:voucherEvent:reverse')")
    @Log(title = "凭证冲销", businessType = BusinessType.UPDATE)
    @PutMapping("/reverse/{voucherEventId}")
    public AjaxResult reverse(@PathVariable Long voucherEventId)
    {
        return toAjax(finVoucherEventService.reverseVoucher(voucherEventId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:voucherEvent:export')")
    @Log(title = "凭证复式分录导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinVoucherEvent finVoucherEvent,
                       @RequestParam(defaultValue = "standard") String format)
    {
        List<FinVoucherEntry> list = finVoucherEventService.selectVoucherEntriesForExport(finVoucherEvent, format);
        ExcelUtil<FinVoucherEntry> util = new ExcelUtil<>(FinVoucherEntry.class);
        String sheetName = "kingdee".equalsIgnoreCase(format) ? "金蝶KIS凭证分录"
            : ("yonyou".equalsIgnoreCase(format) ? "用友T+凭证分录" : "标准复式凭证分录");
        util.exportExcel(response, list, sheetName);
    }

    @PreAuthorize("@ss.hasPermi('business:voucherEvent:generate')")
    @Log(title = "补生成缺失凭证事件", businessType = BusinessType.INSERT)
    @PostMapping("/regenerate")
    public AjaxResult regenerate()
    {
        return success(finVoucherEventService.regenerateMissingVoucherEvents(getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:voucherEvent:remove')")
    @Log(title = "凭证事件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{voucherEventIds}")
    public AjaxResult remove(@PathVariable Long[] voucherEventIds)
    {
        return toAjax(finVoucherEventService.deleteFinVoucherEventByIds(voucherEventIds));
    }
}
