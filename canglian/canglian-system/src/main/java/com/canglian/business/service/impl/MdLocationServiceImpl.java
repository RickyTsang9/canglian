package com.canglian.business.service.impl;

import java.util.List;
import com.canglian.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.canglian.business.mapper.MdLocationMapper;
import com.canglian.business.domain.MdLocation;
import com.canglian.business.service.IMdLocationService;

/**
 * 库位档案Service业务层处理
 * 
 * @author canglian
 */
@Service
public class MdLocationServiceImpl implements IMdLocationService 
{
    @Autowired
    private MdLocationMapper mdLocationMapper;

    /**
     * 查询库位档案
     * 
     * @param locationId 库位档案主键
     * @return 库位档案
     */
    @Override
    public MdLocation selectMdLocationByLocationId(Long locationId)
    {
        return mdLocationMapper.selectMdLocationByLocationId(locationId);
    }

    /**
     * 查询库位档案列表
     * 
     * @param mdLocation 库位档案
     * @return 库位档案
     */
    @Override
    public List<MdLocation> selectMdLocationList(MdLocation mdLocation)
    {
        return mdLocationMapper.selectMdLocationList(mdLocation);
    }

    /**
     * 新增库位档案
     * 
     * @param mdLocation 库位档案
     * @return 结果
     */
    @Override
    public int insertMdLocation(MdLocation mdLocation)
    {
        mdLocation.setCreateTime(DateUtils.getNowDate());
        return mdLocationMapper.insertMdLocation(mdLocation);
    }

    /**
     * 修改库位档案
     * 
     * @param mdLocation 库位档案
     * @return 结果
     */
    @Override
    public int updateMdLocation(MdLocation mdLocation)
    {
        mdLocation.setUpdateTime(DateUtils.getNowDate());
        return mdLocationMapper.updateMdLocation(mdLocation);
    }

    /**
     * 批量删除库位档案
     * 
     * @param locationIds 需要删除的库位档案主键
     * @return 结果
     */
    @Override
    public int deleteMdLocationByLocationIds(Long[] locationIds)
    {
        return mdLocationMapper.deleteMdLocationByLocationIds(locationIds);
    }

    /**
     * 删除库位档案信息
     * 
     * @param locationId 库位档案主键
     * @return 结果
     */
    @Override
    public int deleteMdLocationByLocationId(Long locationId)
    {
        return mdLocationMapper.deleteMdLocationByLocationId(locationId);
    }
}

