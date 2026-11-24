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
import com.canglian.business.domain.FinBadDebt;
import com.canglian.business.service.IFinBadDebtService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;

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

    @PreAuthorize("@ss.hasPermi('business:badDebt:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinBadDebt finBadDebt)
    {
        startPage();
        List<FinBadDebt> list = finBadDebtService.selectFinBadDebtList(finBadDebt);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('business:badDebt:query')")
    @GetMapping(value = "/{badDebtId}")
    public AjaxResult getInfo(@PathVariable Long badDebtId)
    {
        return success(finBadDebtService.selectFinBadDebtById(badDebtId));
    }

    @PreAuthorize("@ss.hasPermi('business:badDebt:add')")
    @Log(title = "坏账", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinBadDebt finBadDebt)
    {
        finBadDebt.setCreateBy(getUsername());
        return toAjax(finBadDebtService.insertFinBadDebt(finBadDebt));
    }

    @PreAuthorize("@ss.hasPermi('business:badDebt:edit')")
    @Log(title = "坏账", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinBadDebt finBadDebt)
    {
        finBadDebt.setUpdateBy(getUsername());
        return toAjax(finBadDebtService.updateFinBadDebt(finBadDebt));
    }

    @PreAuthorize("@ss.hasPermi('business:badDebt:confirm')")
    @Log(title = "坏账确认", businessType = BusinessType.UPDATE)
    @PutMapping("/confirm/{badDebtId}")
    public AjaxResult confirm(@PathVariable Long badDebtId)
    {
        return toAjax(finBadDebtService.confirmFinBadDebt(badDebtId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:badDebt:unconfirm')")
    @Log(title = "坏账反确认", businessType = BusinessType.UPDATE)
    @PostMapping("/unconfirm/{badDebtId}")
    public AjaxResult unconfirm(@PathVariable Long badDebtId)
    {
        return toAjax(finBadDebtService.unconfirmFinBadDebt(badDebtId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:badDebt:recover') or @ss.hasPermi('business:badDebt:confirm')")
    @Log(title = "坏账收回", businessType = BusinessType.UPDATE)
    @PostMapping("/recover/{badDebtId}")
    public AjaxResult recover(@PathVariable Long badDebtId, @RequestBody Map<String, Object> body)
    {
        Object amtObj = body.get("recoverAmount") != null ? body.get("recoverAmount") : body.get("amount");
        Object accObj = body.get("fundAccountId") != null ? body.get("fundAccountId") : body.get("accountId");
        BigDecimal recoverAmount = amtObj == null || "".equals(String.valueOf(amtObj)) ? null : new BigDecimal(String.valueOf(amtObj));
        Long fundAccountId = accObj == null || "".equals(String.valueOf(accObj)) ? null : Long.valueOf(String.valueOf(accObj));
        String remark = body.get("remark") == null ? null : String.valueOf(body.get("remark"));
        return toAjax(finBadDebtService.recoverFinBadDebt(badDebtId, recoverAmount, fundAccountId, remark, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:badDebt:remove')")
    @Log(title = "坏账", businessType = BusinessType.DELETE)
    @DeleteMapping("/{badDebtIds}")
    public AjaxResult remove(@PathVariable Long[] badDebtIds)
    {
        return toAjax(finBadDebtService.deleteFinBadDebtByIds(badDebtIds));
    }

    /**
     * 导出坏账单
     */
    @Log(title = "坏账单", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('business:badDebt:export') or @ss.hasPermi('business:badDebt:list')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, FinBadDebt finBadDebt)
    {
        List<FinBadDebt> list = finBadDebtService.selectFinBadDebtList(finBadDebt);
        ExcelUtil<FinBadDebt> util = new ExcelUtil<FinBadDebt>(FinBadDebt.class);
        util.exportExcel(response, list, "坏账单");
    }
}
