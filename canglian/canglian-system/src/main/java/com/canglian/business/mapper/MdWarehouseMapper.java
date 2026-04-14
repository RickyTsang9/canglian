package com.canglian.business.mapper;

import java.util.List;
import com.canglian.business.domain.MdWarehouse;

/**
 * 仓库档案 数据层
 * 
 * @author canglian
 */
public interface MdWarehouseMapper
{
    /**
     * 查询仓库档案信息
     * 
     * @param warehouseId 仓库id
     * @return 仓库档案信息
     */
    public MdWarehouse selectMdWarehouseById(Long warehouseId);

    /**
     * 查询仓库档案列表
     * 
     * @param mdWarehouse 仓库档案
     * @return 仓库档案集合
     */
    public List<MdWarehouse> selectMdWarehouseList(MdWarehouse mdWarehouse);

    /**
     * 新增仓库档案
     * 
     * @param mdWarehouse 仓库档案
     * @return 结果
     */
    public int insertMdWarehouse(MdWarehouse mdWarehouse);

    /**
     * 修改仓库档案
     * 
     * @param mdWarehouse 仓库档案
     * @return 结果
     */
    public int updateMdWarehouse(MdWarehouse mdWarehouse);

    /**
     * 删除仓库档案
     * 
     * @param warehouseId 仓库id
     * @return 结果
     */
    public int deleteMdWarehouseById(Long warehouseId);

    /**
     * 批量删除仓库档案
     * 
     * @param warehouseIds 需要删除的仓库id
     * @return 结果
     */
    public int deleteMdWarehouseByIds(Long[] warehouseIds);
}

