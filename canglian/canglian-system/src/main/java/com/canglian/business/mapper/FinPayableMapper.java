package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.FinPayable;

/**
 * 应付单 数据层
 * 
 * @author canglian
 */
public interface FinPayableMapper
{
    /**
     * 查询应付单信息
     * 
     * @param payableId 应付单id
     * @return 应付单信息
     */
    public FinPayable selectFinPayableById(Long payableId);

    /**
     * 查询应付单列表
     * 
     * @param finPayable 应付单信息
     * @return 应付单集合
     */
    public List<FinPayable> selectFinPayableList(FinPayable finPayable);

    /**
     * 查询指定前缀的最大应付单号
     * 
     * @param payableNoPrefix 应付单号前缀
     * @return 最大应付单号
     */
    public String selectMaxPayableNoByPrefix(String payableNoPrefix);

    /**
     * 新增应付单
     * 
     * @param finPayable 应付单信息
     * @return 结果
     */
    public int insertFinPayable(FinPayable finPayable);

    /**
     * 修改应付单
     * 
     * @param finPayable 应付单信息
     * @return 结果
     */
    public int updateFinPayable(FinPayable finPayable);

    /**
     * 删除应付单
     * 
     * @param payableId 应付单id
     * @return 结果
     */
    public int deleteFinPayableById(Long payableId);

    /**
     * 批量删除应付单
     * 
     * @param payableIds 需要删除的应付单id
     * @return 结果
     */
    public int deleteFinPayableByIds(Long[] payableIds);
}

