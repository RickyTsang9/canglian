package com.canglian.web.controller.business;

import java.util.List;
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
import com.canglian.business.domain.FinCostAccount;
import com.canglian.business.service.IFinCostAccountService;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.exception.ServiceException;

/**
 * 成本账户 信息操作处理（只读账簿，由业务单据与成本引擎自动维护）
 *
 * @author canglian
 */
@RestController
@RequestMapping("/business/costAccount")
public class FinCostAccountController extends BaseController
{
    private static final String READONLY_MSG = "成本账户为只读财务台账，由出入库单据与成本核算引擎自动维护，禁止手工增删改；如需调价请使用「成本核算 -> 受控成本调价」";

    @Autowired
    private IFinCostAccountService finCostAccountService;

    @PreAuthorize("@ss.hasPermi('business:costAccount:list')")
    @GetMapping("/list")
    public TableDataInfo list(FinCostAccount finCostAccount)
    {
        startPage();
        List<FinCostAccount> list = finCostAccountService.selectFinCostAccountList(finCostAccount);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('business:costAccount:query')")
    @GetMapping(value = "/{costAccountId}")
    public AjaxResult getInfo(@PathVariable Long costAccountId)
    {
        return success(finCostAccountService.selectFinCostAccountById(costAccountId));
    }

    @PreAuthorize("@ss.hasPermi('business:costAccount:add')")
    @PostMapping
    public AjaxResult add(@RequestBody(required = false) FinCostAccount finCostAccount)
    {
        throw new ServiceException(READONLY_MSG);
    }

    @PreAuthorize("@ss.hasPermi('business:costAccount:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody(required = false) FinCostAccount finCostAccount)
    {
        throw new ServiceException(READONLY_MSG);
    }

    @PreAuthorize("@ss.hasPermi('business:costAccount:remove')")
    @DeleteMapping("/{costAccountIds}")
    public AjaxResult remove(@PathVariable Long[] costAccountIds)
    {
        throw new ServiceException(READONLY_MSG);
    }
}
