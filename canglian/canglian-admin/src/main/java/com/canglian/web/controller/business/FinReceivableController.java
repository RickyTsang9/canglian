package com.canglian.web.controller.business;

import java.math.BigDecimal;
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
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;
import com.canglian.business.domain.FinReceivable;
import com.canglian.business.service.IFinReceivableService;

/**
 * 应收单 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/receivable")
public class FinReceivableController extends BaseController
{
    @Autowired
    private IFinReceivableService finReceivableService;

    /**
     * 获取应收单列表
     */
    @PreAuthorize("@ss.hasPermi('business:receivable:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinReceivable finReceivable)
    {
        startPage();
        List<FinReceivable> list = finReceivableService.selectFinReceivableList(finReceivable);
        return getDataTable(list);
    }

    /**
     * 根据应收单编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:receivable:query')")
    @GetMapping(value = "/{receivableId}")
    public AjaxResult getInfo(@PathVariable Long receivableId)
    {
        return success(finReceivableService.selectFinReceivableById(receivableId));
    }

    /**
     * 新增应收单
     */
    @PreAuthorize("@ss.hasPermi('business:receivable:add')")
    @Log(title = "应收单", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinReceivable finReceivable)
    {
        finReceivable.setCreateBy(getUsername());
        return toAjax(finReceivableService.insertFinReceivable(finReceivable));
    }

    /**
     * 修改应收单
     */
    @PreAuthorize("@ss.hasPermi('business:receivable:edit')")
    @Log(title = "应收单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinReceivable finReceivable)
    {
        finReceivable.setUpdateBy(getUsername());
        return toAjax(finReceivableService.updateFinReceivable(finReceivable));
    }

    /**
     * 应收单审核
     */
    @PreAuthorize("@ss.hasPermi('business:receivable:audit')")
    @Log(title = "应收审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{receivableId}")
    public AjaxResult audit(@PathVariable Long receivableId)
    {
        return toAjax(finReceivableService.auditFinReceivable(receivableId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:receivable:audit')")
    @Log(title = "应收反确认", businessType = BusinessType.UPDATE)
    @PostMapping("/unconfirm/{receivableId}")
    public AjaxResult unconfirm(@PathVariable Long receivableId)
    {
        return toAjax(finReceivableService.unconfirmFinReceivable(receivableId, getUsername()));
    }

    /**
     * 红蓝应收单互冲核销
     */
    @PreAuthorize("@ss.hasPermi('business:receivable:edit')")
    @Log(title = "应收红蓝对冲", businessType = BusinessType.UPDATE)
    @PostMapping("/offset")
    public AjaxResult offset(@RequestBody Map<String, Object> payload)
    {
        Long redId = payload.get("redReceivableId") == null ? null : Long.valueOf(String.valueOf(payload.get("redReceivableId")));
        Long targetId = payload.get("targetReceivableId") == null ? null : Long.valueOf(String.valueOf(payload.get("targetReceivableId")));
        BigDecimal amount = payload.get("offsetAmount") == null ? null : new BigDecimal(String.valueOf(payload.get("offsetAmount")));
        return toAjax(finReceivableService.offsetRedReceivable(redId, targetId, amount, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:receivable:list')")
    @GetMapping("/offsetLogs")
    public AjaxResult offsetLogs(@org.springframework.web.bind.annotation.RequestParam(required = false) Long receivableId)
    {
        return success(finReceivableService.selectOffsetLogs(receivableId));
    }

    @PreAuthorize("@ss.hasPermi('business:receivable:unoffset')")
    @Log(title = "撤销应收红蓝对冲", businessType = BusinessType.UPDATE)
    @PostMapping("/unoffset/{offsetId}")
    public AjaxResult unoffset(@PathVariable Long offsetId)
    {
        return toAjax(finReceivableService.unoffsetRedReceivable(offsetId, getUsername()));
    }

    /**
     * 删除应收单
     */
    @PreAuthorize("@ss.hasPermi('business:receivable:remove')")
    @Log(title = "应收单", businessType = BusinessType.DELETE)
    @DeleteMapping("/{receivableIds}")
    public AjaxResult remove(@PathVariable Long[] receivableIds)
    {
        return toAjax(finReceivableService.deleteFinReceivableByIds(receivableIds));
    }

    /**
     * 导出应收账款
     */
    @Log(title = "应收账款", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('business:receivable:export') or @ss.hasPermi('business:receivable:list')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinReceivable finReceivable)
    {
        List<FinReceivable> list = finReceivableService.selectFinReceivableList(finReceivable);
        ExcelUtil<FinReceivable> util = new ExcelUtil<FinReceivable>(FinReceivable.class);
        util.exportExcel(response, list, "应收账款");
    }
}

