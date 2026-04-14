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
import com.canglian.business.domain.FinCostAccount;
import com.canglian.business.service.IFinCostAccountService;

/**
 * 成本账户 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/costAccount")
public class FinCostAccountController extends BaseController
{
    @Autowired
    private IFinCostAccountService finCostAccountService;

    /**
     * 获取成本账户列表
     */
    @PreAuthorize("@ss.hasPermi('business:costAccount:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinCostAccount finCostAccount)
    {
        startPage();
        List<FinCostAccount> list = finCostAccountService.selectFinCostAccountList(finCostAccount);
        return getDataTable(list);
    }

    /**
     * 根据成本账户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:costAccount:query')")
    @GetMapping(value = "/{costAccountId}")
    public AjaxResult getInfo(@PathVariable Long costAccountId)
    {
        return success(finCostAccountService.selectFinCostAccountById(costAccountId));
    }

    /**
     * 新增成本账户
     */
    @PreAuthorize("@ss.hasPermi('business:costAccount:add')")
    @Log(title = "成本账户", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody FinCostAccount finCostAccount)
    {
        finCostAccount.setCreateBy(getUsername());
        return toAjax(finCostAccountService.insertFinCostAccount(finCostAccount));
    }

    /**
     * 修改成本账户
     */
    @PreAuthorize("@ss.hasPermi('business:costAccount:edit')")
    @Log(title = "成本账户", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody FinCostAccount finCostAccount)
    {
        finCostAccount.setUpdateBy(getUsername());
        return toAjax(finCostAccountService.updateFinCostAccount(finCostAccount));
    }

    /**
     * 删除成本账户
     */
    @PreAuthorize("@ss.hasPermi('business:costAccount:remove')")
    @Log(title = "成本账户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{costAccountIds}")
    public AjaxResult remove(@PathVariable Long[] costAccountIds)
    {
        return toAjax(finCostAccountService.deleteFinCostAccountByIds(costAccountIds));
    }
}

