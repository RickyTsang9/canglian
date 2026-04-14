package com.canglian.business.service;

import com.canglian.business.domain.FinCostCalculate;
import com.canglian.business.domain.FinCostLog;

/**
 * 成本核算 服务层
 * 
 * @author canglian
 */
public interface IFinCostCalculateService
{
    /**
     * 计算成本
     * 
     * @param finCostCalculate 成本核算请求
     * @return 成本流水
     */
    public FinCostLog calculateCost(FinCostCalculate finCostCalculate);
}

