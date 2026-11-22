package com.canglian.web.controller.business;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.canglian.business.domain.FinFundAccount;
import com.canglian.business.domain.FinFundLog;
import com.canglian.business.domain.FinFundTransfer;
import com.canglian.business.service.IFinFundAccountService;
import com.canglian.business.service.IFinFundLogService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;
import com.canglian.common.utils.poi.ExcelUtil;

/**
 * 资金账户与资金流水/内部互转控制器
 *
 * @author canglian
 */
@RestController
@RequestMapping("/business/fundAccount")
public class FinFundAccountController extends BaseController
{
    @Autowired
    private IFinFundAccountService finFundAccountService;

    @Autowired
    private IFinFundLogService finFundLogService;

    @PreAuthorize("@ss.hasPermi('business:fundAccount:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinFundAccount finFundAccount)
    {
        startPage();
        List<FinFundAccount> list = finFundAccountService.selectFinFundAccountList(finFundAccount);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('business:fundAccount:query')")
    @GetMapping(value = "/{accountId}")
    public AjaxResult getInfo(@PathVariable Long accountId)
    {
        return success(finFundAccountService.selectFinFundAccountById(accountId));
    }

    @PreAuthorize("@ss.hasPermi('business:fundAccount:add')")
    @Log(title = "资金账户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinFundAccount finFundAccount)
    {
        finFundAccount.setCreateBy(getUsername());
        return toAjax(finFundAccountService.insertFinFundAccount(finFundAccount));
    }

    @PreAuthorize("@ss.hasPermi('business:fundAccount:edit')")
    @Log(title = "资金账户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinFundAccount finFundAccount)
    {
        finFundAccount.setUpdateBy(getUsername());
        return toAjax(finFundAccountService.updateFinFundAccount(finFundAccount));
    }

    @PreAuthorize("@ss.hasPermi('business:fundAccount:remove')")
    @Log(title = "资金账户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{accountIds}")
    public AjaxResult remove(@PathVariable Long[] accountIds)
    {
        return toAjax(finFundAccountService.deleteFinFundAccountByIds(accountIds));
    }

    @PreAuthorize("@ss.hasPermi('business:fundAccount:list') or @ss.hasPermi('business:fundAccount:transfer')")
    @GetMapping("/logs")
    public TableDataInfo logs(FinFundLog query)
    {
        startPage();
        List<FinFundLog> list = finFundLogService.selectFinFundLogList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('business:fundAccount:list') or @ss.hasPermi('business:fundAccount:transfer')")
    @Log(title = "资金流水导出", businessType = BusinessType.EXPORT)
    @PostMapping("/logs/export")
    public void exportLogs(HttpServletResponse response, FinFundLog query)
    {
        List<FinFundLog> list = finFundLogService.selectFinFundLogList(query);
        ExcelUtil<FinFundLog> util = new ExcelUtil<FinFundLog>(FinFundLog.class);
        util.exportExcel(response, list, "资金流水台账");
    }

    @PreAuthorize("@ss.hasPermi('business:fundAccount:list') or @ss.hasPermi('business:fundAccount:transfer')")
    @GetMapping("/transfer/list")
    public TableDataInfo transferList(FinFundTransfer query)
    {
        startPage();
        List<FinFundTransfer> list = finFundLogService.selectFinFundTransferList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('business:fundAccount:transfer') or @ss.hasPermi('business:fundAccount:edit')")
    @Log(title = "资金内部互转", businessType = BusinessType.INSERT)
    @PostMapping("/transfer")
    public AjaxResult createTransfer(@RequestBody FinFundTransfer transfer)
    {
        return toAjax(finFundLogService.insertFinFundTransfer(transfer, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:fundAccount:transfer') or @ss.hasPermi('business:fundAccount:edit')")
    @Log(title = "资金内部互转确认", businessType = BusinessType.UPDATE)
    @PutMapping("/transfer/confirm/{transferId}")
    public AjaxResult confirmTransfer(@PathVariable Long transferId)
    {
        return toAjax(finFundLogService.confirmFinFundTransfer(transferId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:fundAccount:transfer') or @ss.hasPermi('business:fundAccount:edit')")
    @Log(title = "资金内部互转反确认", businessType = BusinessType.UPDATE)
    @PutMapping("/transfer/unconfirm/{transferId}")
    public AjaxResult unconfirmTransfer(@PathVariable Long transferId)
    {
        return toAjax(finFundLogService.unconfirmFinFundTransfer(transferId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('business:fundAccount:transfer') or @ss.hasPermi('business:fundAccount:edit')")
    @Log(title = "资金内部互转删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/transfer/{transferId}")
    public AjaxResult removeTransfer(@PathVariable Long transferId)
    {
        return toAjax(finFundLogService.deleteFinFundTransferById(transferId));
    }
}
