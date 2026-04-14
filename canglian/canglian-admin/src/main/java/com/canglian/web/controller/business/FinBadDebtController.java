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
import com.canglian.business.domain.FinBadDebt;
import com.canglian.business.service.IFinBadDebtService;

/**
 * 坏账 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/badDebt")
public class FinBadDebtController extends BaseController
{
    @Autowired
    private IFinBadDebtService finBadDebtService;

    /**
     * 获取坏账列表
     */
    @PreAuthorize("@ss.hasPermi('business:badDebt:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinBadDebt finBadDebt)
    {
        startPage();
        List<FinBadDebt> list = finBadDebtService.selectFinBadDebtList(finBadDebt);
        return getDataTable(list);
    }

    /**
     * 根据坏账编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:badDebt:query')")
    @GetMapping(value = "/{badDebtId}")
    public AjaxResult getInfo(@PathVariable Long badDebtId)
    {
        return success(finBadDebtService.selectFinBadDebtById(badDebtId));
    }

    /**
     * 新增坏账
     */
    @PreAuthorize("@ss.hasPermi('business:badDebt:add')")
    @Log(title = "坏账", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinBadDebt finBadDebt)
    {
        finBadDebt.setCreateBy(getUsername());
        return toAjax(finBadDebtService.insertFinBadDebt(finBadDebt));
    }

    /**
     * 修改坏账
     */
    @PreAuthorize("@ss.hasPermi('business:badDebt:edit')")
    @Log(title = "坏账", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinBadDebt finBadDebt)
    {
        finBadDebt.setUpdateBy(getUsername());
        return toAjax(finBadDebtService.updateFinBadDebt(finBadDebt));
    }

    /**
     * 删除坏账
     */
    @PreAuthorize("@ss.hasPermi('business:badDebt:remove')")
    @Log(title = "坏账", businessType = BusinessType.DELETE)
    @DeleteMapping("/{badDebtIds}")
    public AjaxResult remove(@PathVariable Long[] badDebtIds)
    {
        return toAjax(finBadDebtService.deleteFinBadDebtByIds(badDebtIds));
    }
}

