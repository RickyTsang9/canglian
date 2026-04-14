package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.FinWriteOff;

/**
 * 核销 服务层
 * 
 * @author canglian
 */
public interface IFinWriteOffService
{
    /**
     * 查询核销信息
     * 
     * @param writeOffId 核销id
     * @return 核销信息
     */
    public FinWriteOff selectFinWriteOffById(Long writeOffId);

    /**
     * 查询核销列表
     * 
     * @param finWriteOff 核销信息
     * @return 核销集合
     */
    public List<FinWriteOff> selectFinWriteOffList(FinWriteOff finWriteOff);

    /**
     * 新增核销
     * 
     * @param finWriteOff 核销信息
     * @return 结果
     */
    public int insertFinWriteOff(FinWriteOff finWriteOff);

    /**
     * 修改核销
     * 
     * @param finWriteOff 核销信息
     * @return 结果
     */
    public int updateFinWriteOff(FinWriteOff finWriteOff);

    /**
     * 删除核销
     * 
     * @param writeOffId 核销id
     * @return 结果
     */
    public int deleteFinWriteOffById(Long writeOffId);

    /**
     * 批量删除核销
     * 
     * @param writeOffIds 需要删除的核销id
     * @return 结果
     */
    public int deleteFinWriteOffByIds(Long[] writeOffIds);
}

