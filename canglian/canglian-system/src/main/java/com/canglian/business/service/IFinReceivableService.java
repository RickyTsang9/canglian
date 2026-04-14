package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.FinReceivable;

/**
 * 应收单 服务层
 * 
 * @author canglian
 */
public interface IFinReceivableService
{
    /**
     * 查询应收单信息
     * 
     * @param receivableId 应收单id
     * @return 应收单信息
     */
    public FinReceivable selectFinReceivableById(Long receivableId);

    /**
     * 查询应收单列表
     * 
     * @param finReceivable 应收单信息
     * @return 应收单集合
     */
    public List<FinReceivable> selectFinReceivableList(FinReceivable finReceivable);

    /**
     * 新增应收单
     * 
     * @param finReceivable 应收单信息
     * @return 结果
     */
    public int insertFinReceivable(FinReceivable finReceivable);

    /**
     * 修改应收单
     * 
     * @param finReceivable 应收单信息
     * @return 结果
     */
    public int updateFinReceivable(FinReceivable finReceivable);

    /**
     * 删除应收单
     * 
     * @param receivableId 应收单id
     * @return 结果
     */
    public int deleteFinReceivableById(Long receivableId);

    /**
     * 批量删除应收单
     * 
     * @param receivableIds 需要删除的应收单id
     * @return 结果
     */
    public int deleteFinReceivableByIds(Long[] receivableIds);

}

