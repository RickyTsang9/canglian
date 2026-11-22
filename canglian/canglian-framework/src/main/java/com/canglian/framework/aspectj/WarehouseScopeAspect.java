package com.canglian.framework.aspectj;

import java.util.Set;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.canglian.business.service.impl.BizScopeHelper;
import com.canglian.common.annotation.WarehouseScope;
import com.canglian.common.core.domain.BaseEntity;
import com.canglian.common.utils.StringUtils;

/**
 * 仓库级数据权限 AOP 切面
 *
 * @author canglian
 */
@Aspect
@Component
public class WarehouseScopeAspect
{
    @Autowired
    private BizScopeHelper bizScopeHelper;

    @Before("@annotation(warehouseScope)")
    public void doBefore(JoinPoint point, WarehouseScope warehouseScope)
    {
        Object[] args = point.getArgs();
        if (args == null || args.length == 0 || !(args[0] instanceof BaseEntity))
        {
            return;
        }
        BaseEntity entity = (BaseEntity) args[0];
        entity.getParams().put("warehouseScope", "");
        Set<Long> allowed = bizScopeHelper.getRestrictedWarehouseIds();
        if (allowed == null)
        {
            return;
        }
        if (allowed.isEmpty())
        {
            entity.getParams().put("warehouseScope", " AND 1 = 0 ");
            return;
        }
        String alias = warehouseScope.warehouseAlias();
        String col = StringUtils.isEmpty(alias) ? warehouseScope.column() : alias + "." + warehouseScope.column();
        StringBuilder inSql = new StringBuilder();
        for (Long id : allowed)
        {
            if (inSql.length() > 0)
            {
                inSql.append(",");
            }
            inSql.append(id);
        }
        entity.getParams().put("warehouseScope", " AND " + col + " IN (" + inSql + ") ");
    }
}
