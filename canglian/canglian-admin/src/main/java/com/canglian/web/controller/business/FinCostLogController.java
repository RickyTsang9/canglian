package com.canglian.web.controller.business;

import java.util.List;
import com.canglian.common.utils.poi.ExcelUtil;
import com.canglian.common.enums.BusinessType;
import com.canglian.common.annotation.Log;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.business.domain.FinCostLog;
import com.canglian.business.service.IFinCostLogService;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.exception.ServiceException;

/**
 * 成本流水 信息操作处理（不可篡改只读财务流水）
 *
 * @author canglian
 */
@RestController
@RequestMapping("/business/costLog")
public class FinCostLogController extends BaseController
{
    private static final String READONLY_MSG = "成本流水为不可篡改审计凭证，由出入库与成本核算引擎自动生成，禁止手工增删改";

    @Autowired
    private IFinCostLogService finCostLogService;

    @PreAuthorize("@ss.hasPermi('business:costLog:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinCostLog finCostLog)
    {
        startPage();
        List<FinCostLog> list = finCostLogService.selectFinCostLogList(finCostLog);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('business:costLog:query')")
    @GetMapping(value = "/{costLogId}")
    public AjaxResult getInfo(@PathVariable Long costLogId)
    {
        return success(finCostLogService.selectFinCostLogById(costLogId));
    }

    @PreAuthorize("@ss.hasPermi('business:costLog:add')")
    @PostMapping
    public AjaxResult add(@RequestBody(required = false) FinCostLog finCostLog)
    {
        throw new ServiceException(READONLY_MSG);
    }

    @PreAuthorize("@ss.hasPermi('business:costLog:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody(required = false) FinCostLog finCostLog)
    {
        throw new ServiceException(READONLY_MSG);
    }

    @PreAuthorize("@ss.hasPermi('business:costLog:remove')")
    @DeleteMapping("/{costLogIds}")
    public AjaxResult remove(@PathVariable Long[] costLogIds)
    {
        throw new ServiceException(READONLY_MSG);
    }

    /**
     * 导出成本流水
     */
    @Log(title = "成本流水", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('business:costLog:export') or @ss.hasPermi('business:costLog:list')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinCostLog finCostLog)
    {
        List<FinCostLog> list = finCostLogService.selectFinCostLogList(finCostLog);
        ExcelUtil<FinCostLog> util = new ExcelUtil<FinCostLog>(FinCostLog.class);
        util.exportExcel(response, list, "成本流水");
    }
}
