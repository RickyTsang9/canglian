package com.canglian.web.controller.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.business.service.IBusinessWorkbenchService;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;

/**
 * 业务工作台控制器
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/business/workbench")
public class BusinessWorkbenchController extends BaseController
{
    @Autowired
    private IBusinessWorkbenchService businessWorkbenchService;

    /**
     * 查询业务工作台汇总
     * 
     * @return 工作台汇总数据
     */
    @PreAuthorize("@ss.hasPermi('business:workbench:query')")
    @GetMapping("/summary")
    public AjaxResult summary()
    {
        return success(businessWorkbenchService.selectWorkbenchSummary());
    }
}
