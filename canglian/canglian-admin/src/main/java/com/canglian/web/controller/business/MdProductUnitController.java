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
import com.canglian.business.domain.MdProductUnit;
import com.canglian.business.service.IMdProductUnitService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;

/**
 * 商品多单位管理控制器
 *
 * @author canglian
 */
@RestController
@RequestMapping("/business/productUnit")
public class MdProductUnitController extends BaseController
{
    @Autowired
    private IMdProductUnitService mdProductUnitService;

    @PreAuthorize("@ss.hasPermi('business:product:list') or @ss.hasPermi('business:purOrder:add') or @ss.hasPermi('business:purOrder:edit') or @ss.hasPermi('business:saleOrder:add') or @ss.hasPermi('business:saleOrder:edit') or @ss.hasPermi('business:inbound:add') or @ss.hasPermi('business:inbound:edit') or @ss.hasPermi('business:outbound:add') or @ss.hasPermi('business:outbound:edit') or @ss.hasPermi('business:transfer:add') or @ss.hasPermi('business:purchaseReturn:add') or @ss.hasPermi('business:saleReturn:add')")
    @GetMapping("/list")
    public TableDataInfo list(MdProductUnit query)
    {
        startPage();
        List<MdProductUnit> list = mdProductUnitService.selectMdProductUnitList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('business:product:list') or @ss.hasPermi('business:purOrder:add') or @ss.hasPermi('business:purOrder:edit') or @ss.hasPermi('business:saleOrder:add') or @ss.hasPermi('business:saleOrder:edit') or @ss.hasPermi('business:inbound:add') or @ss.hasPermi('business:inbound:edit') or @ss.hasPermi('business:outbound:add') or @ss.hasPermi('business:outbound:edit') or @ss.hasPermi('business:transfer:add') or @ss.hasPermi('business:purchaseReturn:add') or @ss.hasPermi('business:saleReturn:add')")
    @GetMapping("/byProduct/{productId}")
    public AjaxResult byProduct(@PathVariable Long productId)
    {
        return success(mdProductUnitService.selectUnitsByProductId(productId));
    }

    @PreAuthorize("@ss.hasPermi('business:product:query')")
    @GetMapping("/{unitId}")
    public AjaxResult getInfo(@PathVariable Long unitId)
    {
        return success(mdProductUnitService.selectMdProductUnitById(unitId));
    }

    @PreAuthorize("@ss.hasPermi('business:product:edit')")
    @Log(title = "商品多单位", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MdProductUnit unit)
    {
        unit.setCreateBy(getUsername());
        return toAjax(mdProductUnitService.insertMdProductUnit(unit));
    }

    @PreAuthorize("@ss.hasPermi('business:product:edit')")
    @Log(title = "商品多单位", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MdProductUnit unit)
    {
        unit.setUpdateBy(getUsername());
        return toAjax(mdProductUnitService.updateMdProductUnit(unit));
    }

    @PreAuthorize("@ss.hasPermi('business:product:edit')")
    @Log(title = "商品多单位", businessType = BusinessType.DELETE)
    @DeleteMapping("/{unitIds}")
    public AjaxResult remove(@PathVariable Long[] unitIds)
    {
        return toAjax(mdProductUnitService.deleteMdProductUnitByIds(unitIds));
    }
}
