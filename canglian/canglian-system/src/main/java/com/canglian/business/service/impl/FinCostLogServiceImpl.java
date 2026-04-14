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

    /**
     * 查询成本流水信息
     * 
     * @param costLogId 成本流水id
     * @return 成本流水信息
     */
    @Override
    public FinCostLog selectFinCostLogById(Long costLogId)
    {
        return finCostLogMapper.selectFinCostLogById(costLogId);
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
        return finCostLogMapper.selectFinCostLogList(finCostLog);
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
        return finCostLogMapper.deleteFinCostLogByIds(costLogIds);
    }
}

