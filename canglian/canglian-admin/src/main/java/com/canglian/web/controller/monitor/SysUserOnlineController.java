package com.canglian.web.controller.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson2.JSON;
import com.canglian.common.annotation.Log;
import com.canglian.common.constant.CacheConstants;
import com.canglian.common.core.controller.BaseController;
import com.canglian.common.core.domain.AjaxResult;
import com.canglian.common.core.domain.model.LoginUser;
import com.canglian.common.core.page.TableDataInfo;
import com.canglian.common.core.redis.RedisCache;
import com.canglian.common.enums.BusinessType;
import com.canglian.common.utils.StringUtils;
import com.canglian.system.domain.SysUserOnline;
import com.canglian.system.service.ISysUserOnlineService;

/**
 * 在线用户监控
 * 
 * @author canglian
 */
@RestController
@RequestMapping("/monitor/online")
public class SysUserOnlineController extends BaseController
{
    @Autowired
    private ISysUserOnlineService userOnlineService;

    @Autowired
    private RedisCache redisCache;

    @PreAuthorize("@ss.hasPermi('monitor:online:list')")
    @GetMapping("/list")
    public TableDataInfo list(String ipaddr, String userName)
    {
        Collection<String> keys = redisCache.keys(CacheConstants.LOGIN_TOKEN_KEY + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<SysUserOnline>();
        for (String cacheKey : keys)
        {
            LoginUser loginUser = getLoginUserFromCache(cacheKey);
            if (StringUtils.isNull(loginUser))
            {
                continue;
            }
            if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName))
            {
                userOnlineList.add(userOnlineService.selectOnlineByInfo(ipaddr, userName, loginUser));
            }
            else if (StringUtils.isNotEmpty(ipaddr))
            {
                userOnlineList.add(userOnlineService.selectOnlineByIpaddr(ipaddr, loginUser));
            }
            else if (StringUtils.isNotEmpty(userName) && StringUtils.isNotNull(loginUser.getUser()))
            {
                userOnlineList.add(userOnlineService.selectOnlineByUserName(userName, loginUser));
            }
            else
            {
                userOnlineList.add(userOnlineService.loginUserToUserOnline(loginUser));
            }
        }
        Collections.reverse(userOnlineList);
        userOnlineList.removeAll(Collections.singleton(null));
        return getDataTable(userOnlineList);
    }

    /**
     * 获取在线用户缓存并兼容FastJson反序列化后的对象类型
     *
     * @param cacheKey 缓存键
     * @return 登录用户信息
     */
    private LoginUser getLoginUserFromCache(String cacheKey)
    {
        Object cacheObject = redisCache.getCacheObject(cacheKey);
        if (StringUtils.isNull(cacheObject))
        {
            return null;
        }
        if (cacheObject instanceof LoginUser)
        {
            return (LoginUser) cacheObject;
        }
        try
        {
            return JSON.parseObject(JSON.toJSONString(cacheObject), LoginUser.class);
        }
        catch (Exception exception)
        {
            return null;
        }
    }

    /**
     * 强退用户
     */
    @PreAuthorize("@ss.hasPermi('monitor:online:forceLogout')")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    public AjaxResult forceLogout(@PathVariable String tokenId)
    {
        redisCache.deleteObject(CacheConstants.LOGIN_TOKEN_KEY + tokenId);
        return success();
    }
}

