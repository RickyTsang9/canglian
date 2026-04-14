package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.FinCostLog;

/**
 * 成本流水 数据层
 * 
 * @author canglian
 */
public interface FinCostLogMapper
{
    /**
     * 查询成本流水信息
     * 
     * @param costLogId 成本流水id
     * @return 成本流水信息
     */
    public FinCostLog selectFinCostLogById(Long costLogId);

    /**
     * 查询成本流水列表
     * 
     * @param finCostLog 成本流水信息
     * @return 成本流水集合
     */
    public List<FinCostLog> selectFinCostLogList(FinCostLog finCostLog);

    /**
     * 新增成本流水
     * 
     * @param finCostLog 成本流水信息
     * @return 结果
     */
    public int insertFinCostLog(FinCostLog finCostLog);

    /**
     * 修改成本流水
     * 
     * @param finCostLog 成本流水信息
     * @return 结果
     */
    public int updateFinCostLog(FinCostLog finCostLog);

    /**
     * 删除成本流水
     * 
     * @param costLogId 成本流水id
     * @return 结果
     */
    public int deleteFinCostLogById(Long costLogId);

    /**
     * 批量删除成本流水
     * 
     * @param costLogIds 需要删除的成本流水id
     * @return 结果
     */
    public int deleteFinCostLogByIds(Long[] costLogIds);
}

