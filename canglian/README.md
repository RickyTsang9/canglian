<p align="center">
	<img alt="logo" src="https://oscimg.oschina.net/oscnet/up-d3d0a9303e11d522a06cd263f3079027715.png">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">仓链管理系统 v0.1.0</h1>
<h4 align="center">基于SpringBoot+Vue3前后端分离的Java快速开发框架</h4>
<p align="center">
	仓链管理系统
</p>

## 平台简介

本系统为基于仓链框架进行二次开发的业务项目。

* 前端采用 Vue3、Element Plus、Vite。
* 后端采用 Spring Boot、Spring Security、MyBatis、Redis、Jwt。
* 权限认证使用Jwt，支持多终端认证系统。
* 支持加载动态权限菜单，多方式轻松权限控制。

## 后端模块说明

1. canglian-admin：后端启动入口与控制器层，打包产物为 `canglian-admin.jar`。
2. canglian-framework：框架核心能力，包含安全、数据权限、数据源、MyBatis 配置等。
3. canglian-system：系统领域与业务领域实现，包含仓储管理、财务管理相关 domain/service/mapper。
4. canglian-quartz：定时任务模块。
5. canglian-generator：代码生成模块。
6. canglian-common：通用工具、公共注解、基础响应对象与通用组件。

## 业务扩展说明

- 本系统已扩展 `业务管理`、`仓储管理`、`财务管理` 三类业务菜单与后端接口。
- 仓储管理包含仓库档案、库存、入库单、出库单、盘点单、调拨单、库存流水、入库明细、出库明细、仓库统计等能力。
- 财务管理包含应收单、应付单、收款单、付款单、资金账户、费用单、核销单、坏账单、财务报表等能力。
- 控制器位于 `canglian-admin/src/main/java/com/canglian/web/controller/business`，业务实现位于 `canglian-system/src/main/java/com/canglian/business`。

## 后端运行

### 环境要求

- JDK 1.8
- Maven 3.6+
- MySQL 5.7+/8.0+
- Redis 6.x+

### 初始化数据库

按顺序执行 `sql` 目录脚本：

1. `canglian_20250522.sql`
2. `quartz.sql`
3. `sample_data.sql`
4. `ec_core_20260125.sql`（如需电商扩展能力）

如当前环境是较早版本升级上来的旧库，还需要补执行：

5. `upgrade_20260417_schema_alignment.sql`（补齐财务扩展表、收款/应收单号字段、仓库联系人字段）

### 开发与打包

```bash
# 在后端根目录执行
mvn clean package -Dmaven.test.skip=true
```

### 启动方式

- 方式一：使用脚本 `bin/run.bat` 启动已打包的 `canglian-admin/target/canglian-admin.jar`。
- 方式二：进入 `canglian-admin/target` 后直接执行 `java -jar canglian-admin.jar`。

### 核心配置位置

- `canglian-admin/src/main/resources/application.yml`：端口、Redis、Token、MyBatis 等基础配置。
- `canglian-admin/src/main/resources/application-druid.yml`：MySQL 数据源与 Druid 连接池配置。
- `canglian-admin/src/main/resources/logback.xml`：系统日志输出目录与日志格式配置。
- 默认端口为 `7565`，请按部署环境调整数据库与 Redis 连接参数。
- 当前版本已支持通过环境变量覆盖关键连接配置，例如 `SERVER_PORT`、`CANGLIAN_PROFILE`、`REDIS_HOST`、`REDIS_PORT`、`REDIS_PASSWORD`、`MYSQL_URL`、`MYSQL_USERNAME`、`MYSQL_PASSWORD`、`LOG_HOME`。

## 内置功能

1.  用户管理：用户是系统操作者，该功能主要完成系统用户配置。
2.  部门管理：配置系统组织机构（公司、部门、小组），树结构展现支持数据权限。
3.  岗位管理：配置系统用户所属担任职务。
4.  菜单管理：配置系统菜单，操作权限，按钮权限标识等。
5.  角色管理：角色菜单权限分配、设置角色按机构进行数据范围权限划分。
6.  字典管理：对系统中经常使用的一些较为固定的数据进行维护。
7.  参数管理：对系统动态配置常用参数。
8.  通知公告：系统通知公告信息发布维护。
9.  操作日志：系统正常操作日志记录和查询；系统异常信息日志记录和查询。
10. 登录日志：系统登录日志记录查询包含登录异常。
11. 在线用户：当前系统中活跃用户状态监控。
12. 定时任务：在线（添加、修改、删除)任务调度包含执行结果日志。
13. 代码生成：前后端代码的生成（java、html、xml、sql）支持CRUD下载 。
14. 系统接口：根据业务代码自动生成相关的api接口文档。
15. 服务监控：监视当前系统CPU、内存、磁盘、堆栈等相关信息。
16. 缓存监控：对系统的缓存信息查询，命令统计等。
17. 在线构建器：拖动表单元素生成相应的HTML代码。
18. 连接池监视：监视当前系统数据库连接池状态，可进行分析SQL找出系统性能瓶颈。

## 菜单调整说明

### 调整前菜单结构

- 首页（固定路由，不在菜单表）
- 系统管理
- 系统监控
- 系统工具
- 官网入口（已移除）
- 业务管理
  - 仓储管理
  - 财务管理

### 调整后菜单结构

- 首页（固定路由，不在菜单表）
- 系统管理
- 系统监控
- 系统工具
- 业务管理
- 仓储管理
- 财务管理

### 数据库变更脚本

```sql
update sys_menu set parent_id = 0, order_num = 6 where menu_id = 2001;
update sys_menu set parent_id = 0, order_num = 7 where menu_id = 2200;
delete from sys_role_menu where menu_id = 4;
delete from sys_menu where menu_id = 4;
```

### 系统配置与用户操作说明

- 菜单数据来源于 sys_menu 与 sys_role_menu，执行数据库脚本后重新登录生效
- 角色菜单权限由角色-菜单关系控制，删除官网入口后不会影响其他菜单权限
- 前端路由保持不变，仓储管理与财务管理仍使用原有路由路径

## 在线体验

- admin/admin123  

演示地址：http://vue.canglian.vip  
文档地址：http://doc.canglian.vip

