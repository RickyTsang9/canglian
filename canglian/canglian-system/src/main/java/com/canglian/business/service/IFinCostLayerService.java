package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.FinCostLayer;

/**
 * 成本分层 服务层
 * 
 * @author canglian
 */
public interface IFinCostLayerService
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

