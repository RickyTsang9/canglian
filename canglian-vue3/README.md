<p align="center">
	<img alt="logo" src="https://oscimg.oschina.net/oscnet/up-d3d0a9303e11d522a06cd263f3079027715.png">
</p>
<h1 align="center" style="margin: 30px 0 30px; font-weight: bold;">仓链管理系统 v0.1.0</h1>
<h4 align="center">基于SpringBoot+Vue3前后端分离的Java快速开发框架</h4>
<p align="center">
	仓链管理系统
</p>

## 平台简介

* 本仓库为前端技术栈 [Vue3](https://v3.cn.vuejs.org) + [Element Plus](https://element-plus.org/zh-CN) + [Vite](https://cn.vitejs.dev) 版本。
* 配套后端代码仓库地址[Canglian-Vue](https://gitee.com/y_project/Canglian-Vue) 或 [Canglian-Vue-fast](https://gitcode.com/yangzongzhuan/Canglian-Vue-fast) 版本。

## 前端运行

```bash
# 克隆项目
git clone https://github.com/yangzongzhuan/Canglian-Vue3.git

# 进入项目目录
cd Canglian-Vue3

# 安装依赖
yarn --registry=https://registry.npmmirror.com

# 启动服务
yarn dev

# 构建测试环境 yarn build:stage
# 构建生产环境 yarn build:prod
# 前端访问地址 http://localhost:80
```

## 前端参考说明（结合 Canglian-Geek-App 思路）

- 当前项目已采用 Vue3 + Vite 架构，并使用 Pinia 进行状态管理，适合作为 Web 管理端长期迭代基础。
- 建议本地开发环境使用 node 18 及以上版本，确保依赖安装与 Vite 构建过程稳定。
- 当前仓库默认提供 `yarn dev`、`yarn build:stage`、`yarn build:prod` 三类常用命令，可覆盖日常开发与发布场景。
- 若后续需要增强工程化能力，可按业务模块逐步引入 TypeScript 与更细粒度的分包策略，不影响现有功能运行。
- 样式层建议统一采用 scss 规范管理公共变量与页面主题，保持后台管理系统风格一致性。
- 该系统当前定位为后台管理端，若后续扩展移动端能力，建议独立维护 uniapp 工程并复用后端接口与权限模型。

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

## 在线体验

- admin/admin123  

框架演示地址：http://vue.canglian.vip  
框架文档地址：http://doc.canglian.vip

