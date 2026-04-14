package com.canglian.web.controller.business;

import java.util.List;
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
import com.canglian.business.domain.FinCostLog;
import com.canglian.business.service.IFinCostLogService;

/**
 * 成本流水 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/costLog")
public class FinCostLogController extends BaseController
{
    @Autowired
    private IFinCostLogService finCostLogService;

    /**
     * 获取成本流水列表
     */
    @PreAuthorize("@ss.hasPermi('business:costLog:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinCostLog finCostLog)
    {
        startPage();
        List<FinCostLog> list = finCostLogService.selectFinCostLogList(finCostLog);
        return getDataTable(list);
    }

    /**
     * 根据成本流水编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:costLog:query')")
    @GetMapping(value = "/{costLogId}")
    public AjaxResult getInfo(@PathVariable Long costLogId)
    {
        return success(finCostLogService.selectFinCostLogById(costLogId));
    }

    /**
     * 新增成本流水
     */
    @PreAuthorize("@ss.hasPermi('business:costLog:add')")
    @Log(title = "成本流水", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinCostLog finCostLog)
    {
        finCostLog.setCreateBy(getUsername());
        return toAjax(finCostLogService.insertFinCostLog(finCostLog));
    }

    /**
     * 修改成本流水
     */
    @PreAuthorize("@ss.hasPermi('business:costLog:edit')")
    @Log(title = "成本流水", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinCostLog finCostLog)
    {
        return toAjax(finCostLogService.updateFinCostLog(finCostLog));
    }

    /**
     * 删除成本流水
     */
    @PreAuthorize("@ss.hasPermi('business:costLog:remove')")
    @Log(title = "成本流水", businessType = BusinessType.DELETE)
    @DeleteMapping("/{costLogIds}")
    public AjaxResult remove(@PathVariable Long[] costLogIds)
    {
        return toAjax(finCostLogService.deleteFinCostLogByIds(costLogIds));
    }
}

