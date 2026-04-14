package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.WmsInventoryCheck;

/**
 * 盘点单 服务层
 * 
 * @author canglian
 */
public interface IWmsInventoryCheckService
{
    /**
     * 查询盘点单信息
     * 
     * @param checkId 盘点单id
     * @return 盘点单信息
     */
    public WmsInventoryCheck selectWmsInventoryCheckById(Long checkId);

    /**
     * 查询盘点单列表
     * 
     * @param wmsInventoryCheck 盘点单信息
     * @return 盘点单集合
     */
    public List<WmsInventoryCheck> selectWmsInventoryCheckList(WmsInventoryCheck wmsInventoryCheck);

    /**
     * 新增盘点单
     * 
     * @param wmsInventoryCheck 盘点单信息
     * @return 结果
     */
    public int insertWmsInventoryCheck(WmsInventoryCheck wmsInventoryCheck);

    /**
     * 修改盘点单
     * 
     * @param wmsInventoryCheck 盘点单信息
     * @return 结果
     */
    public int updateWmsInventoryCheck(WmsInventoryCheck wmsInventoryCheck);

    /**
     * 删除盘点单信息
     * 
     * @param checkId 盘点单id
     * @return 结果
     */
    public int deleteWmsInventoryCheckById(Long checkId);

    /**
     * 批量删除盘点单
     * 
     * @param checkIds 需要删除的盘点单id
     * @return 结果
     */
    public int deleteWmsInventoryCheckByIds(Long[] checkIds);

    /**
     * 审核盘点单
     * 
     * @param checkId 盘点单id
     * @param operator 操作人
     * @return 结果
     */
    public int auditWmsInventoryCheck(Long checkId, String operator);
}

