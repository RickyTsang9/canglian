package com.canglian.business.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.canglian.common.core.domain.BaseEntity;
import com.canglian.common.core.domain.entity.SysDept;
import com.canglian.common.core.domain.entity.SysRole;
import com.canglian.common.core.domain.entity.SysUser;
import com.canglian.common.core.domain.model.LoginUser;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.SecurityUtils;
import com.canglian.common.utils.StringUtils;
import com.canglian.system.mapper.SysDeptMapper;
import com.canglian.system.mapper.SysRoleDeptMapper;
import com.canglian.system.mapper.SysRoleWarehouseMapper;

/**
 * 业务数据权限（部门/本人 + 仓库隔离）统一辅助器
 *
 * @author canglian
 */
@Component
public class BizScopeHelper
{
    @Autowired(required = false)
    private SysRoleWarehouseMapper sysRoleWarehouseMapper;

    @Autowired(required = false)
    private SysRoleDeptMapper sysRoleDeptMapper;

    @Autowired(required = false)
    private SysDeptMapper sysDeptMapper;

    public Long currentDeptId()
    {
        try
        {
            return SecurityUtils.getDeptId();
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public Long getCurrentDeptId()
    {
        return currentDeptId();
    }

    public Long currentUserId()
    {
        try
        {
            return SecurityUtils.getUserId();
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public Long getCurrentUserId()
    {
        return currentUserId();
    }

    public boolean isRestrictedUser()
    {
        SysUser user = currentUser();
        return user != null && !user.isAdmin() && user.getRoles() != null && !user.getRoles().isEmpty();
    }

    public void assertRecordAccess(Long deptId, Long ownerUserId)
    {
        assertRecordAccess(deptId, ownerUserId, null);
    }

    public boolean canAccessRecord(Long deptId, Long ownerUserId)
    {
        return canAccessRecord(deptId, ownerUserId, null);
    }

    public SysUser currentUser()
    {
        try
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            return loginUser == null ? null : loginUser.getUser();
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    /**
     * 查询当前登录用户是否受仓库级隔离限制；若返回 null 表示拥有全部仓库权限
     */
    public Set<Long> getRestrictedWarehouseIds()
    {
        SysUser user = currentUser();
        if (user == null || user.isAdmin() || user.getRoles() == null || user.getRoles().isEmpty())
        {
            return null;
        }
        boolean hasAll = false;
        boolean hasRestricted = false;
        for (SysRole role : user.getRoles())
        {
            if ("1".equals(role.getStatus()))
            {
                continue;
            }
            if ("2".equals(role.getWarehouseScope()))
            {
                hasRestricted = true;
            }
            else
            {
                hasAll = true;
            }
        }
        if (hasAll || !hasRestricted)
        {
            return null;
        }
        List<Long> ids = sysRoleWarehouseMapper == null ? new ArrayList<Long>()
            : sysRoleWarehouseMapper.selectAuthorizedWarehouseIdsByUserId(user.getUserId());
        return new HashSet<Long>(ids == null ? new ArrayList<Long>() : ids);
    }

    /**
     * 校验当前用户是否允许访问/操作指定仓库（防止越权 IDOR）
     */
    public void assertWarehouseAccess(Long warehouseId)
    {
        if (warehouseId == null)
        {
            return;
        }
        Set<Long> allowed = getRestrictedWarehouseIds();
        if (allowed != null && !allowed.contains(warehouseId))
        {
            throw new ServiceException("当前角色无权访问或操作仓库 #" + warehouseId + " 的业务数据");
        }
    }

    /**
     * 判断当前登录用户是否可查看指定业务记录（用于工作台/内存聚合过滤）
     */
    public boolean canAccessRecord(Long deptId, Long ownerUserId, Long warehouseId)
    {
        try
        {
            assertRecordAccess(deptId, ownerUserId, warehouseId);
            return true;
        }
        catch (ServiceException ex)
        {
            return false;
        }
    }

    /**
     * 为业务查询对象注入数据权限与仓库隔离 SQL（存入 params.dataScope 与 params.warehouseScope）
     */
    public void applyScopeFilter(BaseEntity query, String alias, boolean filterDeptUser, boolean filterWarehouse)
    {
        if (query == null)
        {
            return;
        }
        query.getParams().put("dataScope", "");
        query.getParams().put("warehouseScope", "");
        SysUser user = currentUser();
        if (user == null || user.isAdmin() || user.getRoles() == null || user.getRoles().isEmpty())
        {
            return;
        }
        String prefix = StringUtils.isEmpty(alias) ? "" : alias + ".";
        if (filterDeptUser)
        {
            StringBuilder sql = new StringBuilder();
            List<String> seen = new ArrayList<String>();
            boolean hasDeptScope = false;
            for (SysRole role : user.getRoles())
            {
                if ("1".equals(role.getStatus()))
                {
                    continue;
                }
                String ds = role.getDataScope();
                // 自定义部门(2)按角色维度区分，不能仅按 ds 去重丢弃第二角色的授权部门
                String dedupKey = "2".equals(ds) ? ("2:" + role.getRoleId()) : ds;
                if (seen.contains(dedupKey))
                {
                    continue;
                }
                seen.add(dedupKey);
                if ("1".equals(ds))
                {
                    sql.setLength(0);
                    break;
                }
                else if ("2".equals(ds))
                {
                    hasDeptScope = true;
                    sql.append(" OR ").append(prefix).append("dept_id IN (SELECT dept_id FROM sys_role_dept WHERE role_id = ")
                        .append(role.getRoleId()).append(")");
                }
                else if ("3".equals(ds))
                {
                    hasDeptScope = true;
                    sql.append(" OR ").append(prefix).append("dept_id = ").append(user.getDeptId());
                }
                else if ("4".equals(ds))
                {
                    hasDeptScope = true;
                    sql.append(" OR ").append(prefix).append("dept_id IN (SELECT dept_id FROM sys_dept WHERE dept_id = ")
                        .append(user.getDeptId()).append(" OR find_in_set(").append(user.getDeptId()).append(", ancestors))");
                }
                else if ("5".equals(ds))
                {
                    sql.append(" OR ").append(prefix).append("owner_user_id = ").append(user.getUserId());
                }
            }
            if (sql.length() > 0)
            {
                // 仅本人数据权限(5)严格按 owner_user_id 隔离，杜绝 OR dept_id IS NULL 越权泄漏历史/其他单据
                if (hasDeptScope)
                {
                    query.getParams().put("dataScope", " AND (" + sql.substring(4) + " OR (" + prefix + "dept_id IS NULL AND " + prefix + "owner_user_id IS NULL))");
                }
                else
                {
                    query.getParams().put("dataScope", " AND (" + sql.substring(4) + ")");
                }
            }
        }
        if (filterWarehouse)
        {
            Set<Long> allowed = getRestrictedWarehouseIds();
            if (allowed != null)
            {
                if (allowed.isEmpty())
                {
                    query.getParams().put("warehouseScope", " AND 1 = 0 ");
                }
                else
                {
                    StringBuilder inSql = new StringBuilder();
                    for (Long whId : allowed)
                    {
                        if (inSql.length() > 0)
                        {
                            inSql.append(",");
                        }
                        inSql.append(whId);
                    }
                    query.getParams().put("warehouseScope", " AND " + prefix + "warehouse_id IN (" + inSql + ") ");
                }
            }
        }
    }

    /**
     * 详情/审核/编辑防水平越权校验（严格核验自定义部门与本部门及以下权限）
     */
    public void assertRecordAccess(Long deptId, Long ownerUserId, Long warehouseId)
    {
        assertWarehouseAccess(warehouseId);
        SysUser user = currentUser();
        if (user == null || user.isAdmin() || user.getRoles() == null || user.getRoles().isEmpty())
        {
            return;
        }
        if (deptId == null && ownerUserId == null)
        {
            return;
        }
        for (SysRole role : user.getRoles())
        {
            if ("1".equals(role.getStatus()))
            {
                continue;
            }
            String ds = role.getDataScope();
            if ("1".equals(ds))
            {
                return;
            }
            if ("2".equals(ds))
            {
                if (deptId == null)
                {
                    return;
                }
                if (sysRoleDeptMapper != null && role.getRoleId() != null)
                {
                    List<Long> allowedDepts = sysRoleDeptMapper.selectDeptIdsByRoleId(role.getRoleId());
                    if (allowedDepts != null && allowedDepts.contains(deptId))
                    {
                        return;
                    }
                }
            }
            else if ("3".equals(ds))
            {
                if (deptId == null || (user.getDeptId() != null && deptId.equals(user.getDeptId())))
                {
                    return;
                }
            }
            else if ("4".equals(ds))
            {
                if (deptId == null || (user.getDeptId() != null && deptId.equals(user.getDeptId())))
                {
                    return;
                }
                if (sysDeptMapper != null && user.getDeptId() != null)
                {
                    List<SysDept> children = sysDeptMapper.selectChildrenDeptById(user.getDeptId());
                    if (children != null)
                    {
                        for (SysDept child : children)
                        {
                            if (child != null && deptId.equals(child.getDeptId()))
                            {
                                return;
                            }
                        }
                    }
                }
            }
            else if ("5".equals(ds))
            {
                if (ownerUserId != null && ownerUserId.equals(user.getUserId()))
                {
                    return;
                }
            }
        }
        throw new ServiceException("当前账号无权访问或操作他人/跨部门业务单据");
    }
}
