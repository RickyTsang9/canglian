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

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    /**
     * 查询成本分层信息
     * 
     * @param costLayerId 成本分层id
     * @return 成本分层信息
     */
    @Override
    public FinCostLayer selectFinCostLayerById(Long costLayerId)
    {
        FinCostLayer layer = finCostLayerMapper.selectFinCostLayerById(costLayerId);
        if (layer != null && bizScopeHelper != null) bizScopeHelper.assertWarehouseAccess(layer.getWarehouseId());
        return layer;
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
        List<FinCostLayer> list = finCostLayerMapper.selectFinCostLayerList(finCostLayer);
        java.util.Set<Long> allowed = bizScopeHelper == null ? null : bizScopeHelper.getRestrictedWarehouseIds();
        if (allowed == null || list == null) return list;
        java.util.List<FinCostLayer> filtered = new java.util.ArrayList<FinCostLayer>();
        for (FinCostLayer l : list)
        {
            if (allowed.contains(l.getWarehouseId())) filtered.add(l);
        }
        return filtered;
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
        if (bizScopeHelper != null && finCostLayer != null) bizScopeHelper.assertWarehouseAccess(finCostLayer.getWarehouseId());
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
        if (bizScopeHelper != null && finCostLayer != null) bizScopeHelper.assertWarehouseAccess(finCostLayer.getWarehouseId());
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
        if (bizScopeHelper != null && costLayerId != null)
        {
            FinCostLayer layer = finCostLayerMapper.selectFinCostLayerById(costLayerId);
            if (layer != null) bizScopeHelper.assertWarehouseAccess(layer.getWarehouseId());
        }
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
        if (bizScopeHelper != null && costLayerIds != null)
        {
            for (Long id : costLayerIds)
            {
                FinCostLayer layer = finCostLayerMapper.selectFinCostLayerById(id);
                if (layer != null) bizScopeHelper.assertWarehouseAccess(layer.getWarehouseId());
            }
        }
        return finCostLayerMapper.deleteFinCostLayerByIds(costLayerIds);
    }
}

