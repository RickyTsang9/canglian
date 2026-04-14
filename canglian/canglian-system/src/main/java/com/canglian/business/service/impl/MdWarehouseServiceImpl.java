package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.MdWarehouse;
import com.canglian.business.mapper.MdWarehouseMapper;
import com.canglian.business.service.IMdWarehouseService;

/**
 * 仓库档案 服务层实现
 * 
 * @author canglian
 */
@Service
public class MdWarehouseServiceImpl implements IMdWarehouseService
{
    @Autowired
    private MdWarehouseMapper mdWarehouseMapper;

    /**
     * 查询仓库档案信息
     * 
     * @param warehouseId 仓库id
     * @return 仓库档案信息
     */
    @Override
    public MdWarehouse selectMdWarehouseById(Long warehouseId)
    {
        return mdWarehouseMapper.selectMdWarehouseById(warehouseId);
    }

    /**
     * 查询仓库档案列表
     * 
     * @param mdWarehouse 仓库档案
     * @return 仓库档案集合
     */
    @Override
    public List<MdWarehouse> selectMdWarehouseList(MdWarehouse mdWarehouse)
    {
        return mdWarehouseMapper.selectMdWarehouseList(mdWarehouse);
    }

    /**
     * 新增仓库档案
     * 
     * @param mdWarehouse 仓库档案
     * @return 结果
     */
    @Override
    public int insertMdWarehouse(MdWarehouse mdWarehouse)
    {
        return mdWarehouseMapper.insertMdWarehouse(mdWarehouse);
    }

    /**
     * 修改仓库档案
     * 
     * @param mdWarehouse 仓库档案
     * @return 结果
     */
    @Override
    public int updateMdWarehouse(MdWarehouse mdWarehouse)
    {
        return mdWarehouseMapper.updateMdWarehouse(mdWarehouse);
    }

    /**
     * 删除仓库档案
     * 
     * @param warehouseId 仓库id
     * @return 结果
     */
    @Override
    public int deleteMdWarehouseById(Long warehouseId)
    {
        return mdWarehouseMapper.deleteMdWarehouseById(warehouseId);
    }

    /**
     * 批量删除仓库档案
     * 
     * @param warehouseIds 需要删除的仓库id
     * @return 结果
     */
    @Override
    public int deleteMdWarehouseByIds(Long[] warehouseIds)
    {
        return mdWarehouseMapper.deleteMdWarehouseByIds(warehouseIds);
    }
}

