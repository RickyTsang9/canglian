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
import com.canglian.business.domain.FinFundAccount;
import com.canglian.business.service.IFinFundAccountService;

/**
 * 资金账户 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/fundAccount")
public class FinFundAccountController extends BaseController
{
    @Autowired
    private IFinFundAccountService finFundAccountService;

    /**
     * 获取资金账户列表
     */
    @PreAuthorize("@ss.hasPermi('business:fundAccount:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinFundAccount finFundAccount)
    {
        startPage();
        List<FinFundAccount> list = finFundAccountService.selectFinFundAccountList(finFundAccount);
        return getDataTable(list);
    }

    /**
     * 根据资金账户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:fundAccount:query')")
    @GetMapping(value = "/{accountId}")
    public AjaxResult getInfo(@PathVariable Long accountId)
    {
        return success(finFundAccountService.selectFinFundAccountById(accountId));
    }

    /**
     * 新增资金账户
     */
    @PreAuthorize("@ss.hasPermi('business:fundAccount:add')")
    @Log(title = "资金账户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinFundAccount finFundAccount)
    {
        finFundAccount.setCreateBy(getUsername());
        return toAjax(finFundAccountService.insertFinFundAccount(finFundAccount));
    }

    /**
     * 修改资金账户
     */
    @PreAuthorize("@ss.hasPermi('business:fundAccount:edit')")
    @Log(title = "资金账户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinFundAccount finFundAccount)
    {
        finFundAccount.setUpdateBy(getUsername());
        return toAjax(finFundAccountService.updateFinFundAccount(finFundAccount));
    }

    /**
     * 删除资金账户
     */
    @PreAuthorize("@ss.hasPermi('business:fundAccount:remove')")
    @Log(title = "资金账户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{accountIds}")
    public AjaxResult remove(@PathVariable Long[] accountIds)
    {
        return toAjax(finFundAccountService.deleteFinFundAccountByIds(accountIds));
    }
}

