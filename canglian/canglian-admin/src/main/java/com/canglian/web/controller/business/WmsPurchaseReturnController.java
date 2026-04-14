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
import com.canglian.business.domain.WmsPurchaseReturn;
import com.canglian.business.service.IWmsPurchaseReturnService;

/**
 * 采购退货 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/purchaseReturn")
public class WmsPurchaseReturnController extends BaseController
{
    @Autowired
    private IWmsPurchaseReturnService wmsPurchaseReturnService;

    /**
     * 获取采购退货列表
     */
    @PreAuthorize("@ss.hasPermi('business:purchaseReturn:list')")
    @GetMapping("/list")
    public TableDataInfo list(WmsPurchaseReturn wmsPurchaseReturn)
    {
        startPage();
        List<WmsPurchaseReturn> list = wmsPurchaseReturnService.selectWmsPurchaseReturnList(wmsPurchaseReturn);
        return getDataTable(list);
    }

    /**
     * 获取采购退货详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:purchaseReturn:query')")
    @GetMapping(value = "/{purchaseReturnId}")
    public AjaxResult getInfo(@PathVariable Long purchaseReturnId)
    {
        return success(wmsPurchaseReturnService.selectWmsPurchaseReturnById(purchaseReturnId));
    }

    /**
     * 新增采购退货
     */
    @PreAuthorize("@ss.hasPermi('business:purchaseReturn:add')")
    @Log(title = "采购退货", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody WmsPurchaseReturn wmsPurchaseReturn)
    {
        wmsPurchaseReturn.setCreateBy(getUsername());
        return toAjax(wmsPurchaseReturnService.insertWmsPurchaseReturn(wmsPurchaseReturn));
    }

    /**
     * 修改采购退货
     */
    @PreAuthorize("@ss.hasPermi('business:purchaseReturn:edit')")
    @Log(title = "采购退货", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WmsPurchaseReturn wmsPurchaseReturn)
    {
        wmsPurchaseReturn.setUpdateBy(getUsername());
        return toAjax(wmsPurchaseReturnService.updateWmsPurchaseReturn(wmsPurchaseReturn));
    }

    /**
     * 采购退货审核
     */
    @PreAuthorize("@ss.hasPermi('business:purchaseReturn:audit')")
    @Log(title = "采购退货审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{purchaseReturnId}")
    public AjaxResult audit(@PathVariable Long purchaseReturnId)
    {
        return toAjax(wmsPurchaseReturnService.auditWmsPurchaseReturn(purchaseReturnId, getUsername()));
    }

    /**
     * 删除采购退货
     */
    @PreAuthorize("@ss.hasPermi('business:purchaseReturn:remove')")
    @Log(title = "采购退货", businessType = BusinessType.DELETE)
    @DeleteMapping("/{purchaseReturnIds}")
    public AjaxResult remove(@PathVariable Long[] purchaseReturnIds)
    {
        return toAjax(wmsPurchaseReturnService.deleteWmsPurchaseReturnByIds(purchaseReturnIds));
    }
}

