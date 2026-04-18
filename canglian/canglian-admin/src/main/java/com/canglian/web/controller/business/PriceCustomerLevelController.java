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
import com.canglian.business.domain.PriceCustomerLevel;
import com.canglian.business.service.IPriceCustomerLevelService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;

/**
 * 客户等级价格控制器
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/customerLevelPrice")
public class PriceCustomerLevelController extends BaseController
{
    @Autowired
    private IPriceCustomerLevelService priceCustomerLevelService;

    /**
     * 查询客户等级价格列表
     * 
     * @param priceCustomerLevel 客户等级价格
     * @return 客户等级价格列表
     */
    @PreAuthorize("@ss.hasPermi('business:customerLevelPrice:list')")
    @GetMapping("/list")
    public TableDataInfo list(PriceCustomerLevel priceCustomerLevel)
    {
        startPage();
        List<PriceCustomerLevel> priceCustomerLevelList = priceCustomerLevelService.selectPriceCustomerLevelList(priceCustomerLevel);
        return getDataTable(priceCustomerLevelList);
    }

    /**
     * 查询客户等级价格详情
     * 
     * @param levelPriceId 等级价格id
     * @return 客户等级价格详情
     */
    @PreAuthorize("@ss.hasPermi('business:customerLevelPrice:query')")
    @GetMapping("/{levelPriceId}")
    public AjaxResult getInfo(@PathVariable Long levelPriceId)
    {
        return success(priceCustomerLevelService.selectPriceCustomerLevelById(levelPriceId));
    }

    /**
     * 新增客户等级价格
     * 
     * @param priceCustomerLevel 客户等级价格
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:customerLevelPrice:add')")
    @Log(title = "客户等级价格", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody PriceCustomerLevel priceCustomerLevel)
    {
        priceCustomerLevel.setCreateBy(getUsername());
        return toAjax(priceCustomerLevelService.insertPriceCustomerLevel(priceCustomerLevel));
    }

    /**
     * 修改客户等级价格
     * 
     * @param priceCustomerLevel 客户等级价格
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:customerLevelPrice:edit')")
    @Log(title = "客户等级价格", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody PriceCustomerLevel priceCustomerLevel)
    {
        priceCustomerLevel.setUpdateBy(getUsername());
        return toAjax(priceCustomerLevelService.updatePriceCustomerLevel(priceCustomerLevel));
    }

    /**
     * 删除客户等级价格
     * 
     * @param levelPriceIds 等级价格id集合
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:customerLevelPrice:remove')")
    @Log(title = "客户等级价格", businessType = BusinessType.DELETE)
    @DeleteMapping("/{levelPriceIds}")
    public AjaxResult remove(@PathVariable Long[] levelPriceIds)
    {
        return toAjax(priceCustomerLevelService.deletePriceCustomerLevelByIds(levelPriceIds));
    }
}
