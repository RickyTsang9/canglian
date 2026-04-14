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
import com.canglian.business.domain.WmsSaleReturn;
import com.canglian.business.service.IWmsSaleReturnService;

/**
 * 销售退货 信息操作处理
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/saleReturn")
public class WmsSaleReturnController extends BaseController
{
    @Autowired
    private IWmsSaleReturnService wmsSaleReturnService;

    /**
     * 获取销售退货列表
     */
    @PreAuthorize("@ss.hasPermi('business:saleReturn:list')")
    @GetMapping("/list")
    public TableDataInfo list(WmsSaleReturn wmsSaleReturn)
    {
        startPage();
        List<WmsSaleReturn> list = wmsSaleReturnService.selectWmsSaleReturnList(wmsSaleReturn);
        return getDataTable(list);
    }

    /**
     * 获取销售退货详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:saleReturn:query')")
    @GetMapping(value = "/{saleReturnId}")
    public AjaxResult getInfo(@PathVariable Long saleReturnId)
    {
        return success(wmsSaleReturnService.selectWmsSaleReturnById(saleReturnId));
    }

    /**
     * 新增销售退货
     */
    @PreAuthorize("@ss.hasPermi('business:saleReturn:add')")
    @Log(title = "销售退货", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody WmsSaleReturn wmsSaleReturn)
    {
        wmsSaleReturn.setCreateBy(getUsername());
        return toAjax(wmsSaleReturnService.insertWmsSaleReturn(wmsSaleReturn));
    }

    /**
     * 修改销售退货
     */
    @PreAuthorize("@ss.hasPermi('business:saleReturn:edit')")
    @Log(title = "销售退货", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody WmsSaleReturn wmsSaleReturn)
    {
        wmsSaleReturn.setUpdateBy(getUsername());
        return toAjax(wmsSaleReturnService.updateWmsSaleReturn(wmsSaleReturn));
    }

    /**
     * 销售退货审核
     */
    @PreAuthorize("@ss.hasPermi('business:saleReturn:audit')")
    @Log(title = "销售退货审核", businessType = BusinessType.UPDATE)
    @PutMapping("/audit/{saleReturnId}")
    public AjaxResult audit(@PathVariable Long saleReturnId)
    {
        return toAjax(wmsSaleReturnService.auditWmsSaleReturn(saleReturnId, getUsername()));
    }

    /**
     * 删除销售退货
     */
    @PreAuthorize("@ss.hasPermi('business:saleReturn:remove')")
    @Log(title = "销售退货", businessType = BusinessType.DELETE)
    @DeleteMapping("/{saleReturnIds}")
    public AjaxResult remove(@PathVariable Long[] saleReturnIds)
    {
        return toAjax(wmsSaleReturnService.deleteWmsSaleReturnByIds(saleReturnIds));
    }
}

