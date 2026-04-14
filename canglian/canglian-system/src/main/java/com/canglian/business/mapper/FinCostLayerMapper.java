package com.canglian.business.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.canglian.business.domain.FinCostLayer;

/**
 * 成本分层 数据层
 * 
 * @author canglian
 */
public interface FinCostLayerMapper
{
    /**
     * 查询成本分层信息
     * 
     * @param costLayerId 成本分层id
     * @return 成本分层信息
     */
    public FinCostLayer selectFinCostLayerById(Long costLayerId);

    /**
     * 查询成本分层列表
     * 
     * @param finCostLayer 成本分层信息
     * @return 成本分层集合
     */
    public List<FinCostLayer> selectFinCostLayerList(FinCostLayer finCostLayer);

    /**
     * 查询可用成本分层
     * 
     * @param costAccountId 成本账户id
     * @return 成本分层集合
     */
    public List<FinCostLayer> selectAvailableCostLayers(@Param("costAccountId") Long costAccountId);

    /**
     * 新增成本分层
     * 
     * @param finCostLayer 成本分层信息
     * @return 结果
     */
    public int insertFinCostLayer(FinCostLayer finCostLayer);

    /**
     * 修改成本分层
     * 
     * @param finCostLayer 成本分层信息
     * @return 结果
     */
    public int updateFinCostLayer(FinCostLayer finCostLayer);

    /**
     * 删除成本分层
     * 
     * @param costLayerId 成本分层id
     * @return 结果
     */
    public int deleteFinCostLayerById(Long costLayerId);

    /**
     * 批量删除成本分层
     * 
     * @param costLayerIds 需要删除的成本分层id
     * @return 结果
     */
    public int deleteFinCostLayerByIds(Long[] costLayerIds);
}

