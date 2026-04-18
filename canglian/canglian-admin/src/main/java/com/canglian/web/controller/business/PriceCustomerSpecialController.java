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
import com.canglian.business.domain.PriceCustomerSpecial;
import com.canglian.business.service.IPriceCustomerSpecialService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;

/**
 * 客户专属价格控制器
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/customerSpecialPrice")
public class PriceCustomerSpecialController extends BaseController
{
    @Autowired
    private IPriceCustomerSpecialService priceCustomerSpecialService;

    /**
     * 查询客户专属价格列表
     * 
     * @param priceCustomerSpecial 客户专属价格
     * @return 客户专属价格列表
     */
    @PreAuthorize("@ss.hasPermi('business:customerSpecialPrice:list')")
    @GetMapping("/list")
    public TableDataInfo list(PriceCustomerSpecial priceCustomerSpecial)
    {
        startPage();
        List<PriceCustomerSpecial> priceCustomerSpecialList = priceCustomerSpecialService.selectPriceCustomerSpecialList(priceCustomerSpecial);
        return getDataTable(priceCustomerSpecialList);
    }

    /**
     * 查询客户专属价格详情
     * 
     * @param specialPriceId 专属价格id
     * @return 客户专属价格详情
     */
    @PreAuthorize("@ss.hasPermi('business:customerSpecialPrice:query')")
    @GetMapping("/{specialPriceId}")
    public AjaxResult getInfo(@PathVariable Long specialPriceId)
    {
        return success(priceCustomerSpecialService.selectPriceCustomerSpecialById(specialPriceId));
    }

    /**
     * 新增客户专属价格
     * 
     * @param priceCustomerSpecial 客户专属价格
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:customerSpecialPrice:add')")
    @Log(title = "客户专属价格", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody PriceCustomerSpecial priceCustomerSpecial)
    {
        priceCustomerSpecial.setCreateBy(getUsername());
        return toAjax(priceCustomerSpecialService.insertPriceCustomerSpecial(priceCustomerSpecial));
    }

    /**
     * 修改客户专属价格
     * 
     * @param priceCustomerSpecial 客户专属价格
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:customerSpecialPrice:edit')")
    @Log(title = "客户专属价格", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody PriceCustomerSpecial priceCustomerSpecial)
    {
        priceCustomerSpecial.setUpdateBy(getUsername());
        return toAjax(priceCustomerSpecialService.updatePriceCustomerSpecial(priceCustomerSpecial));
    }

    /**
     * 删除客户专属价格
     * 
     * @param specialPriceIds 专属价格id集合
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:customerSpecialPrice:remove')")
    @Log(title = "客户专属价格", businessType = BusinessType.DELETE)
    @DeleteMapping("/{specialPriceIds}")
    public AjaxResult remove(@PathVariable Long[] specialPriceIds)
    {
        return toAjax(priceCustomerSpecialService.deletePriceCustomerSpecialByIds(specialPriceIds));
    }
}
