package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinPayable;
import com.canglian.business.mapper.FinPayableMapper;
import com.canglian.business.service.IFinPayableService;

/**
 * 应付单 服务层实现
 * 
 * @author canglian
 */
@Service
public class FinPayableServiceImpl implements IFinPayableService
{
    @Autowired
    private FinPayableMapper finPayableMapper;

    /**
     * 查询应付单信息
     * 
     * @param payableId 应付单id
     * @return 应付单信息
     */
    @Override
    public FinPayable selectFinPayableById(Long payableId)
    {
        return finPayableMapper.selectFinPayableById(payableId);
    }

    /**
     * 查询应付单列表
     * 
     * @param finPayable 应付单信息
     * @return 应付单集合
     */
    @Override
    public List<FinPayable> selectFinPayableList(FinPayable finPayable)
    {
        return finPayableMapper.selectFinPayableList(finPayable);
    }

    /**
     * 新增应付单
     * 
     * @param finPayable 应付单信息
     * @return 结果
     */
    @Override
    public int insertFinPayable(FinPayable finPayable)
    {
        return finPayableMapper.insertFinPayable(finPayable);
    }

    /**
     * 修改应付单
     * 
     * @param finPayable 应付单信息
     * @return 结果
     */
    @Override
    public int updateFinPayable(FinPayable finPayable)
    {
        return finPayableMapper.updateFinPayable(finPayable);
    }

    /**
     * 删除应付单
     * 
     * @param payableId 应付单id
     * @return 结果
     */
    @Override
    public int deleteFinPayableById(Long payableId)
    {
        return finPayableMapper.deleteFinPayableById(payableId);
    }

    /**
     * 批量删除应付单
     * 
     * @param payableIds 需要删除的应付单id
     * @return 结果
     */
    @Override
    public int deleteFinPayableByIds(Long[] payableIds)
    {
        return finPayableMapper.deleteFinPayableByIds(payableIds);
    }
}

