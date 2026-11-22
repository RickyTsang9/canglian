package com.canglian.web.controller.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.business.service.IPriceResolverService;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;

/**
 * 实时价格策略解析控制器
 *
 * @author canglian
 */
@RestController
@RequestMapping("/business/price")
public class PriceResolveController extends BaseController
{
    @Autowired
    private IPriceResolverService priceResolverService;

    @PreAuthorize("@ss.hasPermi('business:price:resolve') or @ss.hasPermi('business:saleOrder:add') or @ss.hasPermi('business:saleOrder:edit') or @ss.hasPermi('business:outbound:add')")
    @GetMapping("/resolve")
    public AjaxResult resolve(@RequestParam(required = false) Long customerId,
                              @RequestParam Long productId,
                              @RequestParam(required = false) String unitName)
    {
        return success(priceResolverService.resolveSalePriceDetail(customerId, productId, unitName));
    }
}
