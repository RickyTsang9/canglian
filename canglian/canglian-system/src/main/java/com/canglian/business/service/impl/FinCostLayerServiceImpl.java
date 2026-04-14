package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinCostLayer;
import com.canglian.business.mapper.FinCostLayerMapper;
import com.canglian.business.service.IFinCostLayerService;

/**
 * 成本分层 服务层实现
 * 
 * @author canglian
 */
@Service
public class FinCostLayerServiceImpl implements IFinCostLayerService
{
    @Autowired
    private FinCostLayerMapper finCostLayerMapper;

    /**
     * 查询成本分层信息
     * 
     * @param costLayerId 成本分层id
     * @return 成本分层信息
     */
    @Override
    public FinCostLayer selectFinCostLayerById(Long costLayerId)
    {
        return finCostLayerMapper.selectFinCostLayerById(costLayerId);
    }

    /**
     * 查询成本分层列表
     * 
     * @param finCostLayer 成本分层信息
     * @return 成本分层集合
     */
    @Override
    public List<FinCostLayer> selectFinCostLayerList(FinCostLayer finCostLayer)
    {
        return finCostLayerMapper.selectFinCostLayerList(finCostLayer);
    }

    /**
     * 新增成本分层
     * 
     * @param finCostLayer 成本分层信息
     * @return 结果
     */
    @Override
    public int insertFinCostLayer(FinCostLayer finCostLayer)
    {
        return finCostLayerMapper.insertFinCostLayer(finCostLayer);
    }

    /**
     * 修改成本分层
     * 
     * @param finCostLayer 成本分层信息
     * @return 结果
     */
    @Override
    public int updateFinCostLayer(FinCostLayer finCostLayer)
    {
        return finCostLayerMapper.updateFinCostLayer(finCostLayer);
    }

    /**
     * 删除成本分层
     * 
     * @param costLayerId 成本分层id
     * @return 结果
     */
    @Override
    public int deleteFinCostLayerById(Long costLayerId)
    {
        return finCostLayerMapper.deleteFinCostLayerById(costLayerId);
    }

    /**
     * 批量删除成本分层
     * 
     * @param costLayerIds 需要删除的成本分层id
     * @return 结果
     */
    @Override
    public int deleteFinCostLayerByIds(Long[] costLayerIds)
    {
        return finCostLayerMapper.deleteFinCostLayerByIds(costLayerIds);
    }
}

