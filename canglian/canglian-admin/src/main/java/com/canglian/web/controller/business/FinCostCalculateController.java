package com.canglian.web.controller.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.business.domain.FinCostCalculate;
import com.canglian.business.domain.FinCostLog;
import com.canglian.business.service.IFinCostCalculateService;

@RestController
@RequestMapping("/business/costCalculate")
public class FinCostCalculateController extends BaseController
{
    @Autowired
    private IFinCostCalculateService finCostCalculateService;

    @PreAuthorize("@ss.hasPermi('business:costCalculate:calculate')")
    @PostMapping("/calculate")
    public AjaxResult calculate(@RequestBody FinCostCalculate finCostCalculate)
    {
        FinCostLog finCostLog = finCostCalculateService.calculateCost(finCostCalculate);
        return success(finCostLog);
    }
}

