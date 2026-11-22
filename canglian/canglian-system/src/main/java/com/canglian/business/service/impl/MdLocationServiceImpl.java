package com.canglian.business.service.impl;

import java.util.List;
import com.canglian.common.annotation.WarehouseScope;
import com.canglian.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.mapper.MdLocationMapper;
import com.canglian.business.domain.MdLocation;
import com.canglian.business.service.IMdLocationService;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.StringUtils;

/**
 * 库位档案Service业务层处理（含仓库级数据权限隔离与全表引用删除防护）
 * 
 * @author canglian
 */
@Service
public class MdLocationServiceImpl implements IMdLocationService 
{
    @Autowired
    private MdLocationMapper mdLocationMapper;

    @Autowired(required = false)
    private BizScopeHelper bizScopeHelper;

    @Override
    public MdLocation selectMdLocationByLocationId(Long locationId)
    {
        MdLocation loc = mdLocationMapper.selectMdLocationByLocationId(locationId);
        if (loc != null && bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(loc.getWarehouseId());
        }
        return loc;
    }

    @Override
    @WarehouseScope(column = "warehouse_id")
    public List<MdLocation> selectMdLocationList(MdLocation mdLocation)
    {
        return mdLocationMapper.selectMdLocationList(mdLocation);
    }

    @Override
    public int insertMdLocation(MdLocation mdLocation)
    {
        if (mdLocation != null && bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(mdLocation.getWarehouseId());
        }
        validateLocationCodeUnique(mdLocation);
        mdLocation.setCreateTime(DateUtils.getNowDate());
        return mdLocationMapper.insertMdLocation(mdLocation);
    }

    @Override
    public int updateMdLocation(MdLocation mdLocation)
    {
        if (mdLocation != null && bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(mdLocation.getWarehouseId());
            if (mdLocation.getLocationId() != null)
            {
                MdLocation old = mdLocationMapper.selectMdLocationByLocationId(mdLocation.getLocationId());
                if (old != null)
                {
                    bizScopeHelper.assertWarehouseAccess(old.getWarehouseId());
                }
            }
        }
        validateLocationCodeUnique(mdLocation);
        mdLocation.setUpdateTime(DateUtils.getNowDate());
        return mdLocationMapper.updateMdLocation(mdLocation);
    }

    @Override
    public int deleteMdLocationByLocationIds(Long[] locationIds)
    {
        if (locationIds != null)
        {
            for (Long locationId : locationIds)
            {
                validateLocationDeletable(locationId);
            }
        }
        return mdLocationMapper.deleteMdLocationByLocationIds(locationIds);
    }

    @Override
    public int deleteMdLocationByLocationId(Long locationId)
    {
        validateLocationDeletable(locationId);
        return mdLocationMapper.deleteMdLocationByLocationId(locationId);
    }

    private void validateLocationCodeUnique(MdLocation mdLocation)
    {
        if (mdLocation == null || mdLocation.getWarehouseId() == null)
        {
            throw new ServiceException("所属仓库不能为空");
        }
        if (StringUtils.isEmpty(mdLocation.getLocationCode()))
        {
            throw new ServiceException("库位编码不能为空");
        }
        MdLocation query = new MdLocation();
        query.setWarehouseId(mdLocation.getWarehouseId());
        query.setLocationCode(mdLocation.getLocationCode());
        List<MdLocation> existingList = mdLocationMapper.selectMdLocationList(query);
        if (existingList != null)
        {
            for (MdLocation existing : existingList)
            {
                if (mdLocation.getLocationCode().equals(existing.getLocationCode())
                    && mdLocation.getWarehouseId().equals(existing.getWarehouseId())
                    && !existing.getLocationId().equals(mdLocation.getLocationId()))
                {
                    throw new ServiceException("同仓库下库位编码[" + mdLocation.getLocationCode() + "]已存在，请勿重复");
                }
            }
        }
    }

    private void validateLocationDeletable(Long locationId)
    {
        if (locationId == null)
        {
            return;
        }
        MdLocation loc = mdLocationMapper.selectMdLocationByLocationId(locationId);
        if (loc != null && bizScopeHelper != null)
        {
            bizScopeHelper.assertWarehouseAccess(loc.getWarehouseId());
        }
        if (mdLocationMapper.countLocationReferences(locationId) > 0)
        {
            throw new ServiceException("库位[" + locationId + "]已存在关联库存、仓储单据明细或序列号记录，不允许删除，请改为停用");
        }
    }
}
