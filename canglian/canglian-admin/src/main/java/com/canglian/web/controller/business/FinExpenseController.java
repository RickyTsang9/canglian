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
import com.canglian.business.domain.FinExpense;
import com.canglian.business.service.IFinExpenseService;

/**
 * 费用 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/expense")
public class FinExpenseController extends BaseController
{
    @Autowired
    private IFinExpenseService finExpenseService;

    /**
     * 获取费用列表
     */
    @PreAuthorize("@ss.hasPermi('business:expense:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinExpense finExpense)
    {
        startPage();
        List<FinExpense> list = finExpenseService.selectFinExpenseList(finExpense);
        return getDataTable(list);
    }

    /**
     * 根据费用编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:expense:query')")
    @GetMapping(value = "/{expenseId}")
    public AjaxResult getInfo(@PathVariable Long expenseId)
    {
        return success(finExpenseService.selectFinExpenseById(expenseId));
    }

    /**
     * 新增费用
     */
    @PreAuthorize("@ss.hasPermi('business:expense:add')")
    @Log(title = "费用", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinExpense finExpense)
    {
        finExpense.setCreateBy(getUsername());
        return toAjax(finExpenseService.insertFinExpense(finExpense));
    }

    /**
     * 修改费用
     */
    @PreAuthorize("@ss.hasPermi('business:expense:edit')")
    @Log(title = "费用", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinExpense finExpense)
    {
        finExpense.setUpdateBy(getUsername());
        return toAjax(finExpenseService.updateFinExpense(finExpense));
    }

    @PreAuthorize("@ss.hasPermi('business:expense:confirm')")
    @Log(title = "费用确认", businessType = BusinessType.UPDATE)
    @PutMapping("/confirm/{expenseId}")
    public AjaxResult confirm(@PathVariable Long expenseId)
    {
        FinExpense expense = finExpenseService.selectFinExpenseById(expenseId);
        if (expense == null)
        {
            return AjaxResult.error("费用单不存在");
        }
        expense.setStatus("1");
        expense.setUpdateBy(getUsername());
        return toAjax(finExpenseService.updateFinExpense(expense));
    }

    /**
     * 删除费用
     */
    @PreAuthorize("@ss.hasPermi('business:expense:remove')")
    @Log(title = "费用", businessType = BusinessType.DELETE)
    @DeleteMapping("/{expenseIds}")
    public AjaxResult remove(@PathVariable Long[] expenseIds)
    {
        return toAjax(finExpenseService.deleteFinExpenseByIds(expenseIds));
    }
}

