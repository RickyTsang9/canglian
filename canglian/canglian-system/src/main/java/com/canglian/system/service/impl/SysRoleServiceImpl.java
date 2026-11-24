package com.canglian.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.canglian.common.annotation.DataScope;
import com.canglian.common.constant.UserConstants;
import com.canglian.common.core.domain.entity.SysMenu;
import com.canglian.common.core.domain.entity.SysRole;
import com.canglian.common.core.domain.entity.SysUser;
import com.canglian.common.exception.ServiceException;
import com.canglian.common.utils.SecurityUtils;
import com.canglian.common.utils.StringUtils;
import com.canglian.common.utils.spring.SpringUtils;
import com.canglian.system.domain.SysRoleDept;
import com.canglian.system.domain.SysRoleMenu;
import com.canglian.system.domain.SysUserRole;
import com.canglian.system.mapper.SysMenuMapper;
import com.canglian.system.mapper.SysRoleDeptMapper;
import com.canglian.system.mapper.SysRoleMapper;
import com.canglian.system.mapper.SysRoleMenuMapper;
import com.canglian.system.mapper.SysRoleWarehouseMapper;
import com.canglian.system.mapper.SysUserRoleMapper;
import com.canglian.system.service.ISysRoleService;

/**
 * 角色 业务层处理
 * 
 * @author canglian
 */
@Service
public class SysRoleServiceImpl implements ISysRoleService
{
    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysRoleDeptMapper roleDeptMapper;

    @Autowired
    private SysRoleWarehouseMapper roleWarehouseMapper;

    @Autowired(required = false)
    private SysMenuMapper menuMapper;

    /**
     * 根据条件分页查询角色数据
     * 
     * @param role 角色信息
     * @return 角色数据集合信息
     */
    @Override
    @DataScope(deptAlias = "d")
    public List<SysRole> selectRoleList(SysRole role)
    {
        return roleMapper.selectRoleList(role);
    }

    /**
     * 根据用户ID查询角色
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<SysRole> selectRolesByUserId(Long userId)
    {
        List<SysRole> userRoles = roleMapper.selectRolePermissionByUserId(userId);
        List<SysRole> roles = selectRoleAll();
        for (SysRole role : roles)
        {
            for (SysRole userRole : userRoles)
            {
                if (role.getRoleId().longValue() == userRole.getRoleId().longValue())
                {
                    role.setFlag(true);
                    break;
                }
            }
        }
        return roles;
    }

    /**
     * 根据用户ID查询权限
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectRolePermissionByUserId(Long userId)
    {
        List<SysRole> perms = roleMapper.selectRolePermissionByUserId(userId);
        Set<String> permsSet = new HashSet<>();
        for (SysRole perm : perms)
        {
            if (StringUtils.isNotNull(perm))
            {
                permsSet.addAll(Arrays.asList(perm.getRoleKey().trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 查询所有角色
     * 
     * @return 角色列表
     */
    @Override
    public List<SysRole> selectRoleAll()
    {
        return SpringUtils.getAopProxy(this).selectRoleList(new SysRole());
    }

    /**
     * 根据用户ID获取角色选择框列表
     * 
     * @param userId 用户ID
     * @return 选中角色ID列表
     */
    @Override
    public List<Long> selectRoleListByUserId(Long userId)
    {
        return roleMapper.selectRoleListByUserId(userId);
    }

    /**
     * 通过角色ID查询角色
     * 
     * @param roleId 角色ID
     * @return 角色对象信息
     */
    @Override
    public SysRole selectRoleById(Long roleId)
    {
        return roleMapper.selectRoleById(roleId);
    }

    /**
     * 校验角色名称是否唯一
     * 
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public boolean checkRoleNameUnique(SysRole role)
    {
        Long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        SysRole info = roleMapper.checkRoleNameUnique(role.getRoleName());
        if (StringUtils.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验角色权限是否唯一
     * 
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public boolean checkRoleKeyUnique(SysRole role)
    {
        Long roleId = StringUtils.isNull(role.getRoleId()) ? -1L : role.getRoleId();
        SysRole info = roleMapper.checkRoleKeyUnique(role.getRoleKey());
        if (StringUtils.isNotNull(info) && info.getRoleId().longValue() != roleId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验角色是否允许操作
     * 
     * @param role 角色信息
     */
    @Override
    public void checkRoleAllowed(SysRole role)
    {
        if (StringUtils.isNotNull(role.getRoleId()) && role.isAdmin())
        {
            throw new ServiceException("不允许操作超级管理员角色");
        }
    }

    /**
     * 校验角色是否有数据权限
     * 
     * @param roleIds 角色id
     */
    @Override
    public void checkRoleDataScope(Long... roleIds)
    {
        if (!SecurityUtils.isAdmin())
        {
            for (Long roleId : roleIds)
            {
                SysRole role = new SysRole();
                role.setRoleId(roleId);
                List<SysRole> roles = SpringUtils.getAopProxy(this).selectRoleList(role);
                if (StringUtils.isEmpty(roles))
                {
                    throw new ServiceException("没有权限访问角色数据！");
                }
            }
        }
    }

    /**
     * 通过角色ID查询角色使用数量
     * 
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    public int countUserRoleByRoleId(Long roleId)
    {
        return userRoleMapper.countUserRoleByRoleId(roleId);
    }

    /**
     * 新增保存角色信息
     * 
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public int insertRole(SysRole role)
    {
        assertRolePrivilegeNotEscalated(role);
        // 新增角色信息
        roleMapper.insertRole(role);
        return insertRoleMenu(role);
    }

    /**
     * 修改保存角色信息
     * 
     * @param role 角色信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateRole(SysRole role)
    {
        assertRolePrivilegeNotEscalated(role);
        // 修改角色信息
        roleMapper.updateRole(role);
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenuByRoleId(role.getRoleId());
        return insertRoleMenu(role);
    }

    /**
     * 修改角色状态
     * 
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public int updateRoleStatus(SysRole role)
    {
        return roleMapper.updateRole(role);
    }

    /**
     * 修改数据权限信息
     * 
     * @param role 角色信息
     * @return 结果
     */
    @Override
    public List<Long> selectWarehouseIdsByRoleId(Long roleId)
    {
        return roleWarehouseMapper.selectWarehouseIdsByRoleId(roleId);
    }

    @Override
    @Transactional
    public int authDataScope(SysRole role)
    {
        assertRoleScopeNotEscalated(role);
        // 修改角色信息（含 warehouseScope）
        roleMapper.updateRole(role);
        // 删除角色与部门关联
        roleDeptMapper.deleteRoleDeptByRoleId(role.getRoleId());
        // 同步角色与仓库数据权限关联
        roleWarehouseMapper.deleteRoleWarehouseByRoleId(role.getRoleId());
        if ("2".equals(role.getWarehouseScope()) && role.getWarehouseIds() != null && role.getWarehouseIds().length > 0)
        {
            roleWarehouseMapper.batchInsertRoleWarehouse(role.getRoleId(), role.getWarehouseIds());
        }
        // 新增角色和部门信息（数据权限）
        return insertRoleDept(role);
    }

    private void assertRolePrivilegeNotEscalated(SysRole role)
    {
        if (role == null) return;
        SysUser current;
        try
        {
            current = SecurityUtils.getLoginUser() == null ? null : SecurityUtils.getLoginUser().getUser();
        }
        catch (Exception ex)
        {
            return;
        }
        if (current == null || current.isAdmin()) return;
        if ("admin".equalsIgnoreCase(role.getRoleKey()))
        {
            throw new ServiceException("禁止创建或修改超级管理员权限字符");
        }
        if (role.getMenuIds() != null && role.getMenuIds().length > 0 && menuMapper != null)
        {
            SysMenu q = new SysMenu();
            q.getParams().put("userId", current.getUserId());
            List<SysMenu> ownedMenus = menuMapper.selectMenuListByUserId(q);
            Set<Long> ownedIds = new HashSet<Long>();
            if (ownedMenus != null)
            {
                for (SysMenu m : ownedMenus)
                {
                    if (m != null && m.getMenuId() != null) ownedIds.add(m.getMenuId());
                }
            }
            for (Long menuId : role.getMenuIds())
            {
                if (menuId != null && !ownedIds.contains(menuId))
                {
                    throw new ServiceException("权限越级拦截：禁止为角色授予当前账号未持有的菜单权限(#" + menuId + ")");
                }
            }
        }
        assertRoleScopeNotEscalated(role);
    }

    private void assertRoleScopeNotEscalated(SysRole role)
    {
        if (role == null) return;
        SysUser current;
        try
        {
            current = SecurityUtils.getLoginUser() == null ? null : SecurityUtils.getLoginUser().getUser();
        }
        catch (Exception ex)
        {
            return;
        }
        if (current == null || current.isAdmin()) return;
        boolean callerHasAllData = false;
        boolean callerHasAllWarehouse = false;
        if (current.getRoles() != null)
        {
            for (SysRole r : current.getRoles())
            {
                if (r == null || "1".equals(r.getStatus())) continue;
                if ("1".equals(r.getDataScope())) callerHasAllData = true;
                if (!"2".equals(r.getWarehouseScope())) callerHasAllWarehouse = true;
            }
        }
        if ("1".equals(role.getDataScope()) && !callerHasAllData)
        {
            throw new ServiceException("权限越级拦截：当前账号无全部数据权限，禁止为角色授予全部数据权限");
        }
        if ("1".equals(role.getWarehouseScope()) && !callerHasAllWarehouse)
        {
            throw new ServiceException("权限越级拦截：当前账号受仓库隔离限制，禁止为角色授予全部仓库权限");
        }
        if ("2".equals(role.getWarehouseScope()) && !callerHasAllWarehouse && role.getWarehouseIds() != null && roleWarehouseMapper != null)
        {
            List<Long> ownedWh = roleWarehouseMapper.selectAuthorizedWarehouseIdsByUserId(current.getUserId());
            Set<Long> ownedSet = new HashSet<Long>(ownedWh == null ? new ArrayList<Long>() : ownedWh);
            for (Long whId : role.getWarehouseIds())
            {
                if (whId != null && !ownedSet.contains(whId))
                {
                    throw new ServiceException("权限越级拦截：禁止授予当前账号未授权的仓库(#" + whId + ")");
                }
            }
        }
    }

    /**
     * 新增角色菜单信息
     * 
     * @param role 角色对象
     */
    public int insertRoleMenu(SysRole role)
    {
        int rows = 1;
        // 新增用户与角色管理
        List<SysRoleMenu> list = new ArrayList<SysRoleMenu>();
        for (Long menuId : role.getMenuIds())
        {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(role.getRoleId());
            rm.setMenuId(menuId);
            list.add(rm);
        }
        if (list.size() > 0)
        {
            rows = roleMenuMapper.batchRoleMenu(list);
        }
        return rows;
    }

    /**
     * 新增角色部门信息(数据权限)
     *
     * @param role 角色对象
     */
    public int insertRoleDept(SysRole role)
    {
        int rows = 1;
        // 新增角色与部门（数据权限）管理
        List<SysRoleDept> list = new ArrayList<SysRoleDept>();
        for (Long deptId : role.getDeptIds())
        {
            SysRoleDept rd = new SysRoleDept();
            rd.setRoleId(role.getRoleId());
            rd.setDeptId(deptId);
            list.add(rd);
        }
        if (list.size() > 0)
        {
            rows = roleDeptMapper.batchRoleDept(list);
        }
        return rows;
    }

    /**
     * 通过角色ID删除角色
     * 
     * @param roleId 角色ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteRoleById(Long roleId)
    {
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenuByRoleId(roleId);
        // 删除角色与部门关联
        roleDeptMapper.deleteRoleDeptByRoleId(roleId);
        // 删除角色与仓库关联
        roleWarehouseMapper.deleteRoleWarehouseByRoleId(roleId);
        return roleMapper.deleteRoleById(roleId);
    }

    /**
     * 批量删除角色信息
     * 
     * @param roleIds 需要删除的角色ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteRoleByIds(Long[] roleIds)
    {
        for (Long roleId : roleIds)
        {
            checkRoleAllowed(new SysRole(roleId));
            checkRoleDataScope(roleId);
            SysRole role = selectRoleById(roleId);
            if (countUserRoleByRoleId(roleId) > 0)
            {
                throw new ServiceException(String.format("%1$s已分配,不能删除", role.getRoleName()));
            }
        }
        // 删除角色与菜单关联
        roleMenuMapper.deleteRoleMenu(roleIds);
        // 删除角色与部门关联
        roleDeptMapper.deleteRoleDept(roleIds);
        // 删除角色与仓库关联
        for (Long roleId : roleIds)
        {
            roleWarehouseMapper.deleteRoleWarehouseByRoleId(roleId);
        }
        return roleMapper.deleteRoleByIds(roleIds);
    }

    /**
     * 取消授权用户角色
     * 
     * @param userRole 用户和角色关联信息
     * @return 结果
     */
    @Override
    public int deleteAuthUser(SysUserRole userRole)
    {
        return userRoleMapper.deleteUserRoleInfo(userRole);
    }

    /**
     * 批量取消授权用户角色
     * 
     * @param roleId 角色ID
     * @param userIds 需要取消授权的用户数据ID
     * @return 结果
     */
    @Override
    public int deleteAuthUsers(Long roleId, Long[] userIds)
    {
        return userRoleMapper.deleteUserRoleInfos(roleId, userIds);
    }

    /**
     * 批量选择授权用户角色
     * 
     * @param roleId 角色ID
     * @param userIds 需要授权的用户数据ID
     * @return 结果
     */
    @Override
    public int insertAuthUsers(Long roleId, Long[] userIds)
    {
        // 新增用户与角色管理
        List<SysUserRole> list = new ArrayList<SysUserRole>();
        for (Long userId : userIds)
        {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            list.add(ur);
        }
        return userRoleMapper.batchUserRole(list);
    }
}

