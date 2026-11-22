package com.canglian.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 角色与仓库数据权限关联 数据层
 *
 * @author canglian
 */
public interface SysRoleWarehouseMapper
{
    public List<Long> selectWarehouseIdsByRoleId(@Param("roleId") Long roleId);

    public List<Long> selectAuthorizedWarehouseIdsByUserId(@Param("userId") Long userId);

    public int deleteRoleWarehouseByRoleId(@Param("roleId") Long roleId);

    public int batchInsertRoleWarehouse(@Param("roleId") Long roleId, @Param("warehouseIds") Long[] warehouseIds);
}
