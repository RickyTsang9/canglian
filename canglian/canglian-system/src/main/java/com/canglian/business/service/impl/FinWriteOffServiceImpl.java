package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.FinWriteOff;
import com.canglian.business.mapper.FinWriteOffMapper;
import com.canglian.business.service.IFinWriteOffService;

/**
 * 核销 服务层实现
 * 
 * @author canglian
 */
@Service
public class FinWriteOffServiceImpl implements IFinWriteOffService
{
    @Autowired
    private FinWriteOffMapper finWriteOffMapper;

    /**
     * 查询核销信息
     * 
     * @param writeOffId 核销id
     * @return 核销信息
     */
    @Override
    public FinWriteOff selectFinWriteOffById(Long writeOffId)
    {
        return finWriteOffMapper.selectFinWriteOffById(writeOffId);
    }

    /**
     * 查询核销列表
     * 
     * @param finWriteOff 核销信息
     * @return 核销集合
     */
    @Override
    public List<FinWriteOff> selectFinWriteOffList(FinWriteOff finWriteOff)
    {
        return finWriteOffMapper.selectFinWriteOffList(finWriteOff);
    }

    /**
     * 新增核销
     * 
     * @param finWriteOff 核销信息
     * @return 结果
     */
    @Override
    public int insertFinWriteOff(FinWriteOff finWriteOff)
    {
        return finWriteOffMapper.insertFinWriteOff(finWriteOff);
    }

    /**
     * 修改核销
     * 
     * @param finWriteOff 核销信息
     * @return 结果
     */
    @Override
    public int updateFinWriteOff(FinWriteOff finWriteOff)
    {
        return finWriteOffMapper.updateFinWriteOff(finWriteOff);
    }

    /**
     * 删除核销
     * 
     * @param writeOffId 核销id
     * @return 结果
     */
    @Override
    public int deleteFinWriteOffById(Long writeOffId)
    {
        return finWriteOffMapper.deleteFinWriteOffById(writeOffId);
    }

    /**
     * 批量删除核销
     * 
     * @param writeOffIds 需要删除的核销id
     * @return 结果
     */
    @Override
    public int deleteFinWriteOffByIds(Long[] writeOffIds)
    {
        return finWriteOffMapper.deleteFinWriteOffByIds(writeOffIds);
    }
}

