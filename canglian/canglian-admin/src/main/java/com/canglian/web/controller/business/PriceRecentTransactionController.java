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
import com.canglian.business.domain.PriceRecentTransaction;
import com.canglian.business.service.IPriceRecentTransactionService;
import com.canglian.common.annotation.Log;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.enums.BusinessType;

/**
 * 最近成交价格控制器
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/recentTransactionPrice")
public class PriceRecentTransactionController extends BaseController
{
    @Autowired
    private IPriceRecentTransactionService priceRecentTransactionService;

    /**
     * 查询最近成交价格列表
     * 
     * @param priceRecentTransaction 最近成交价格
     * @return 最近成交价格列表
     */
    @PreAuthorize("@ss.hasPermi('business:recentTransactionPrice:list')")
    @GetMapping("/list")
    public TableDataInfo list(PriceRecentTransaction priceRecentTransaction)
    {
        startPage();
        List<PriceRecentTransaction> priceRecentTransactionList = priceRecentTransactionService.selectPriceRecentTransactionList(priceRecentTransaction);
        return getDataTable(priceRecentTransactionList);
    }

    /**
     * 查询最近成交价格详情
     * 
     * @param recentPriceId 最近成交价格id
     * @return 最近成交价格详情
     */
    @PreAuthorize("@ss.hasPermi('business:recentTransactionPrice:query')")
    @GetMapping("/{recentPriceId}")
    public AjaxResult getInfo(@PathVariable Long recentPriceId)
    {
        return success(priceRecentTransactionService.selectPriceRecentTransactionById(recentPriceId));
    }

    /**
     * 新增最近成交价格
     * 
     * @param priceRecentTransaction 最近成交价格
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:recentTransactionPrice:add')")
    @Log(title = "最近成交价格", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody PriceRecentTransaction priceRecentTransaction)
    {
        priceRecentTransaction.setCreateBy(getUsername());
        return toAjax(priceRecentTransactionService.insertPriceRecentTransaction(priceRecentTransaction));
    }

    /**
     * 修改最近成交价格
     * 
     * @param priceRecentTransaction 最近成交价格
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:recentTransactionPrice:edit')")
    @Log(title = "最近成交价格", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody PriceRecentTransaction priceRecentTransaction)
    {
        priceRecentTransaction.setUpdateBy(getUsername());
        return toAjax(priceRecentTransactionService.updatePriceRecentTransaction(priceRecentTransaction));
    }

    /**
     * 删除最近成交价格
     * 
     * @param recentPriceIds 最近成交价格id集合
     * @return 结果
     */
    @PreAuthorize("@ss.hasPermi('business:recentTransactionPrice:remove')")
    @Log(title = "最近成交价格", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recentPriceIds}")
    public AjaxResult remove(@PathVariable Long[] recentPriceIds)
    {
        return toAjax(priceRecentTransactionService.deletePriceRecentTransactionByIds(recentPriceIds));
    }
}
