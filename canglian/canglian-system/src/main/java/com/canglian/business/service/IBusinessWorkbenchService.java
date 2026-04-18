package com.canglian.business.service;

import java.util.Map;

/**
 * 业务工作台服务接口
 * 
 * @author canglian
 */
public interface IBusinessWorkbenchService
{
    /**
     * 查询业务工作台汇总数据
     * 
     * @return 工作台汇总数据
     */
    public Map<String, Object> selectWorkbenchSummary();
}
