package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.ReplenishmentSuggestion;

/**
 * 补货建议服务接口
 * 
 * @author canglian
 */
public interface IReplenishmentService
{
    /**
     * 查询补货建议列表
     * 
     * @param warehouseId 仓库编号
     * @return 补货建议列表
     */
    public List<ReplenishmentSuggestion> selectReplenishmentSuggestionList(Long warehouseId);
}
