package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinReceivable;
import com.canglian.business.mapper.FinReceivableMapper;
import com.canglian.business.service.IFinReceivableService;

/**
 * 应收单 服务层实现
 * 
 * @author canglian
 */
@Service
public class FinReceivableServiceImpl implements IFinReceivableService
{
    @Autowired
    private FinReceivableMapper finReceivableMapper;

    /**
     * 查询应收单信息
     * 
     * @param receivableId 应收单id
     * @return 应收单信息
     */
    @Override
    public FinReceivable selectFinReceivableById(Long receivableId)
    {
        return finReceivableMapper.selectFinReceivableById(receivableId);
    }

    /**
     * 查询应收单列表
     * 
     * @param finReceivable 应收单信息
     * @return 应收单集合
     */
    @Override
    public List<FinReceivable> selectFinReceivableList(FinReceivable finReceivable)
    {
        return finReceivableMapper.selectFinReceivableList(finReceivable);
    }

    /**
     * 新增应收单
     * 
     * @param finReceivable 应收单信息
     * @return 结果
     */
    @Override
    public int insertFinReceivable(FinReceivable finReceivable)
    {
        return finReceivableMapper.insertFinReceivable(finReceivable);
    }

    /**
     * 修改应收单
     * 
     * @param finReceivable 应收单信息
     * @return 结果
     */
    @Override
    public int updateFinReceivable(FinReceivable finReceivable)
    {
        return finReceivableMapper.updateFinReceivable(finReceivable);
    }

    /**
     * 删除应收单
     * 
     * @param receivableId 应收单id
     * @return 结果
     */
    @Override
    public int deleteFinReceivableById(Long receivableId)
    {
        return finReceivableMapper.deleteFinReceivableById(receivableId);
    }

    /**
     * 批量删除应收单
     * 
     * @param receivableIds 需要删除的应收单id
     * @return 结果
     */
    @Override
    public int deleteFinReceivableByIds(Long[] receivableIds)
    {
        return finReceivableMapper.deleteFinReceivableByIds(receivableIds);
    }
}

