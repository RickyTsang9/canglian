package com.canglian.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 仓库级数据权限过滤注解
 * 配合 WarehouseScopeAspect 在 params.warehouseScope 中注入授权仓库 SQL 条件。
 *
 * @author canglian
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WarehouseScope
{
    /** 表别名（为空则直接使用列名） */
    String warehouseAlias() default "";

    /** 仓库列名 */
    String column() default "warehouse_id";
}
