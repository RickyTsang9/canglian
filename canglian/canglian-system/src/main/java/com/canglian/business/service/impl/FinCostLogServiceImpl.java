package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinCostLog;
import com.canglian.business.mapper.FinCostLogMapper;
import com.canglian.business.service.IFinCostLogService;

/**
 * 成本流水 服务层实现
 * 
 * @author canglian
 */
@Service
public class FinCostLogServiceImpl implements IFinCostLogService
{
    @Autowired
    private FinCostLogMapper finCostLogMapper;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    /**
     * 查询成本流水信息
     * 
     * @param costLogId 成本流水id
     * @return 成本流水信息
     */
    @Override
    public FinCostLog selectFinCostLogById(Long costLogId)
    {
        FinCostLog log = finCostLogMapper.selectFinCostLogById(costLogId);
        if (log != null && bizScopeHelper != null) bizScopeHelper.assertWarehouseAccess(log.getWarehouseId());
        return log;
    }

    /**
     * 查询成本流水列表
     * 
     * @param finCostLog 成本流水信息
     * @return 成本流水集合
     */
    @Override
    public List<FinCostLog> selectFinCostLogList(FinCostLog finCostLog)
    {
        List<FinCostLog> list = finCostLogMapper.selectFinCostLogList(finCostLog);
        java.util.Set<Long> allowed = bizScopeHelper == null ? null : bizScopeHelper.getRestrictedWarehouseIds();
        if (allowed == null || list == null) return list;
        java.util.List<FinCostLog> filtered = new java.util.ArrayList<FinCostLog>();
        for (FinCostLog l : list)
        {
            if (allowed.contains(l.getWarehouseId())) filtered.add(l);
        }
        return filtered;
    }

    /**
     * 新增成本流水
     * 
     * @param finCostLog 成本流水信息
     * @return 结果
     */
    @Override
    public int insertFinCostLog(FinCostLog finCostLog)
    {
        if (bizScopeHelper != null && finCostLog != null) bizScopeHelper.assertWarehouseAccess(finCostLog.getWarehouseId());
        return finCostLogMapper.insertFinCostLog(finCostLog);
    }

    /**
     * 修改成本流水
     * 
     * @param finCostLog 成本流水信息
     * @return 结果
     */
    @Override
    public int updateFinCostLog(FinCostLog finCostLog)
    {
        if (bizScopeHelper != null && finCostLog != null) bizScopeHelper.assertWarehouseAccess(finCostLog.getWarehouseId());
        return finCostLogMapper.updateFinCostLog(finCostLog);
    }

    /**
     * 删除成本流水
     * 
     * @param costLogId 成本流水id
     * @return 结果
     */
    @Override
    public int deleteFinCostLogById(Long costLogId)
    {
        if (bizScopeHelper != null && costLogId != null)
        {
            FinCostLog log = finCostLogMapper.selectFinCostLogById(costLogId);
            if (log != null) bizScopeHelper.assertWarehouseAccess(log.getWarehouseId());
        }
        return finCostLogMapper.deleteFinCostLogById(costLogId);
    }

    /**
     * 批量删除成本流水
     * 
     * @param costLogIds 需要删除的成本流水id
     * @return 结果
     */
    @Override
    public int deleteFinCostLogByIds(Long[] costLogIds)
    {
        if (bizScopeHelper != null && costLogIds != null)
        {
            for (Long id : costLogIds)
            {
                FinCostLog log = finCostLogMapper.selectFinCostLogById(id);
                if (log != null) bizScopeHelper.assertWarehouseAccess(log.getWarehouseId());
            }
        }
        return finCostLogMapper.deleteFinCostLogByIds(costLogIds);
    }
}

