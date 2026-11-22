package com.canglian.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.domain.MdLocation;
import com.canglian.business.domain.MdWarehouse;
import com.canglian.business.domain.WmsStock;
import com.canglian.business.mapper.MdLocationMapper;
import com.canglian.business.mapper.MdWarehouseMapper;
import com.canglian.business.mapper.WmsStockMapper;
import com.canglian.business.service.IMdWarehouseService;
import com.canglian.common.annotation.WarehouseScope;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

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

    @Autowired
    private MdLocationMapper mdLocationMapper;

    @Autowired
    private WmsStockMapper wmsStockMapper;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    /**
     * 查询仓库档案信息
     * 
     * @param warehouseId 仓库id
     * @return 仓库档案信息
     */
    @Override
    public MdWarehouse selectMdWarehouseById(Long warehouseId)
    {
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(warehouseId);
        }
        return mdWarehouseMapper.selectMdWarehouseById(warehouseId);
    }

    /**
     * 查询仓库档案列表
     * 
     * @param mdWarehouse 仓库档案
     * @return 仓库档案集合
     */
    @Override
    @WarehouseScope(column = "warehouse_id")
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
        validateWarehouseCodeUnique(mdWarehouse);
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
        if (bizScopeHelper != null && mdWarehouse != null)
        {
            bizScopeHelper.assertWarehouseAccess(mdWarehouse.getWarehouseId());
        }
        validateWarehouseCodeUnique(mdWarehouse);
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
        if (bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(warehouseId);
        }
        validateWarehouseDeletable(warehouseId);
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
        if (warehouseIds != null)
        {
            for (Long warehouseId : warehouseIds)
            {
                if (bizScopeHelper != null)
                {
                    bizScopeHelper.assertWarehouseAccess(warehouseId);
                }
                validateWarehouseDeletable(warehouseId);
            }
        }
        return mdWarehouseMapper.deleteMdWarehouseByIds(warehouseIds);
    }

    private void validateWarehouseCodeUnique(MdWarehouse mdWarehouse)
    {
        if (mdWarehouse == null || StringUtils.isEmpty(mdWarehouse.getWarehouseCode()))
        {
            throw new ServiceException("仓库编码不能为空");
        }
        MdWarehouse query = new MdWarehouse();
        query.setWarehouseCode(mdWarehouse.getWarehouseCode());
        List<MdWarehouse> existingList = mdWarehouseMapper.selectMdWarehouseList(query);
        if (existingList != null)
        {
            for (MdWarehouse existing : existingList)
            {
                if (mdWarehouse.getWarehouseCode().equals(existing.getWarehouseCode())
                    && !existing.getWarehouseId().equals(mdWarehouse.getWarehouseId()))
                {
                    throw new ServiceException("仓库编码[" + mdWarehouse.getWarehouseCode() + "]已存在，请勿重复");
                }
            }
        }
    }

    private void validateWarehouseDeletable(Long warehouseId)
    {
        if (warehouseId == null)
        {
            return;
        }
        if (mdWarehouseMapper.countWarehouseReferences(warehouseId) > 0)
        {
            throw new ServiceException("仓库[" + warehouseId + "]已存在关联库位、库存、订单、仓储单据或成本记录，不允许删除，请改为停用");
        }
    }
}

