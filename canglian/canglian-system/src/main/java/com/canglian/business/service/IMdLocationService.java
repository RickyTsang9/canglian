package com.canglian.business.service;

import java.util.List;
import com.canglian.business.domain.MdLocation;

/**
 * 库位档案Service接口
 * 
 * @author canglian
 */
public interface IMdLocationService 
{
    /**
     * 查询库位档案
     * 
     * @param locationId 库位档案主键
     * @return 库位档案
     */
    public MdLocation selectMdLocationByLocationId(Long locationId);

    /**
     * 查询库位档案列表
     * 
     * @param mdLocation 库位档案
     * @return 库位档案集合
     */
    public List<MdLocation> selectMdLocationList(MdLocation mdLocation);

    /**
     * 新增库位档案
     * 
     * @param mdLocation 库位档案
     * @return 结果
     */
    public int insertMdLocation(MdLocation mdLocation);

    /**
     * 修改库位档案
     * 
     * @param mdLocation 库位档案
     * @return 结果
     */
    public int updateMdLocation(MdLocation mdLocation);

    /**
     * 批量删除库位档案
     * 
     * @param locationIds 需要删除的库位档案主键集合
     * @return 结果
     */
    public int deleteMdLocationByLocationIds(Long[] locationIds);

    /**
     * 删除库位档案信息
     * 
     * @param locationId 库位档案主键
     * @return 结果
     */
    public int deleteMdLocationByLocationId(Long locationId);
}

