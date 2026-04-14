-- ----------------------------
-- 1、部门表
-- ----------------------------
drop table if exists sys_dept;
create table sys_dept (
  dept_id           bigint(20)      not null auto_increment    comment '部门id',
  parent_id         bigint(20)      default 0                  comment '父部门id',
  ancestors         varchar(50)     default ''                 comment '祖级列表',
  dept_name         varchar(30)     default ''                 comment '部门名称',
  order_num         int(4)          default 0                  comment '显示顺序',
  leader            varchar(20)     default null               comment '负责人',
  phone             varchar(11)     default null               comment '联系电话',
  email             varchar(50)     default null               comment '邮箱',
  status            char(1)         default '0'                comment '部门状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time 	    datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  primary key (dept_id)
) engine=innodb auto_increment=200 comment = '部门表';

-- ----------------------------
-- 初始化-部门表数据
-- ----------------------------
insert into sys_dept values(100,  0,   '0',          '仓链科技',   0, '仓链', '15888888888', 'ry@qq.com', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(101,  100, '0,100',      '深圳总公司', 1, '仓链', '15888888888', 'ry@qq.com', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(102,  100, '0,100',      '长沙分公司', 2, '仓链', '15888888888', 'ry@qq.com', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(103,  101, '0,100,101',  '研发部门',   1, '仓链', '15888888888', 'ry@qq.com', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(104,  101, '0,100,101',  '市场部门',   2, '仓链', '15888888888', 'ry@qq.com', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(105,  101, '0,100,101',  '测试部门',   3, '仓链', '15888888888', 'ry@qq.com', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(106,  101, '0,100,101',  '财务部门',   4, '仓链', '15888888888', 'ry@qq.com', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(107,  101, '0,100,101',  '运维部门',   5, '仓链', '15888888888', 'ry@qq.com', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(108,  102, '0,100,102',  '市场部门',   1, '仓链', '15888888888', 'ry@qq.com', '0', '0', 'admin', sysdate(), '', null);
insert into sys_dept values(109,  102, '0,100,102',  '财务部门',   2, '仓链', '15888888888', 'ry@qq.com', '0', '0', 'admin', sysdate(), '', null);


-- ----------------------------
-- 2、用户信息表
-- ----------------------------
drop table if exists sys_user;
create table sys_user (
  user_id           bigint(20)      not null auto_increment    comment '用户ID',
  dept_id           bigint(20)      default null               comment '部门ID',
  user_name         varchar(30)     not null                   comment '用户账号',
  nick_name         varchar(30)     not null                   comment '用户昵称',
  user_type         varchar(2)      default '00'               comment '用户类型（00系统用户）',
  email             varchar(50)     default ''                 comment '用户邮箱',
  phonenumber       varchar(11)     default ''                 comment '手机号码',
  sex               char(1)         default '0'                comment '用户性别（0男 1女 2未知）',
  avatar            varchar(100)    default ''                 comment '头像地址',
  password          varchar(100)    default ''                 comment '密码',
  status            char(1)         default '0'                comment '账号状态（0正常 1停用）',
  del_flag          char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  login_ip          varchar(128)    default ''                 comment '最后登录IP',
  login_date        datetime                                   comment '最后登录时间',
  pwd_update_date   datetime                                   comment '密码最后更新时间',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (user_id)
) engine=innodb auto_increment=100 comment = '用户信息表';

-- ----------------------------
-- 初始化-用户信息表数据
-- ----------------------------
insert into sys_user values(1,  103, 'admin', '仓链', '00', 'ry@163.com', '15888888888', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '管理员');
insert into sys_user values(2,  105, 'ry',    '仓链', '00', 'ry@qq.com',  '15666666666', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), sysdate(), 'admin', sysdate(), '', null, '测试员');


-- ----------------------------
-- 3、岗位信息表
-- ----------------------------
drop table if exists sys_post;
create table sys_post
(
  post_id       bigint(20)      not null auto_increment    comment '岗位ID',
  post_code     varchar(64)     not null                   comment '岗位编码',
  post_name     varchar(50)     not null                   comment '岗位名称',
  post_sort     int(4)          not null                   comment '显示顺序',
  status        char(1)         not null                   comment '状态（0正常 1停用）',
  create_by     varchar(64)     default ''                 comment '创建者',
  create_time   datetime                                   comment '创建时间',
  update_by     varchar(64)     default ''			       comment '更新者',
  update_time   datetime                                   comment '更新时间',
  remark        varchar(500)    default null               comment '备注',
  primary key (post_id)
) engine=innodb comment = '岗位信息表';

-- ----------------------------
-- 初始化-岗位信息表数据
-- ----------------------------
insert into sys_post values(1, 'ceo',  '董事长',    1, '0', 'admin', sysdate(), '', null, '');
insert into sys_post values(2, 'se',   '项目经理',  2, '0', 'admin', sysdate(), '', null, '');
insert into sys_post values(3, 'hr',   '人力资源',  3, '0', 'admin', sysdate(), '', null, '');
insert into sys_post values(4, 'user', '普通员工',  4, '0', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 4、角色信息表
-- ----------------------------
drop table if exists sys_role;
create table sys_role (
  role_id              bigint(20)      not null auto_increment    comment '角色ID',
  role_name            varchar(30)     not null                   comment '角色名称',
  role_key             varchar(100)    not null                   comment '角色权限字符串',
  role_sort            int(4)          not null                   comment '显示顺序',
  data_scope           char(1)         default '1'                comment '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  menu_check_strictly  tinyint(1)      default 1                  comment '菜单树选择项是否关联显示',
  dept_check_strictly  tinyint(1)      default 1                  comment '部门树选择项是否关联显示',
  status               char(1)         not null                   comment '角色状态（0正常 1停用）',
  del_flag             char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by            varchar(64)     default ''                 comment '创建者',
  create_time          datetime                                   comment '创建时间',
  update_by            varchar(64)     default ''                 comment '更新者',
  update_time          datetime                                   comment '更新时间',
  remark               varchar(500)    default null               comment '备注',
  primary key (role_id)
) engine=innodb auto_increment=100 comment = '角色信息表';

-- ----------------------------
-- 初始化-角色信息表数据
-- ----------------------------
insert into sys_role values('1', '超级管理员',  'admin',  1, 1, 1, 1, '0', '0', 'admin', sysdate(), '', null, '超级管理员');
insert into sys_role values('2', '普通角色',    'common', 2, 2, 1, 1, '0', '0', 'admin', sysdate(), '', null, '普通角色');
insert into sys_role values('3', '仓管',        'warehouse', 3, 2, 1, 1, '0', '0', 'admin', sysdate(), '', null, '仓储管理角色');
insert into sys_role values('4', '采购',        'purchase',  4, 2, 1, 1, '0', '0', 'admin', sysdate(), '', null, '采购管理角色');
insert into sys_role values('5', '销售',        'sales',     5, 2, 1, 1, '0', '0', 'admin', sysdate(), '', null, '销售管理角色');
insert into sys_role values('6', '财务',        'finance',   6, 2, 1, 1, '0', '0', 'admin', sysdate(), '', null, '财务管理角色');


-- ----------------------------
-- 5、菜单权限表
-- ----------------------------
drop table if exists sys_menu;
create table sys_menu (
  menu_id           bigint(20)      not null auto_increment    comment '菜单ID',
  menu_name         varchar(50)     not null                   comment '菜单名称',
  parent_id         bigint(20)      default 0                  comment '父菜单ID',
  order_num         int(4)          default 0                  comment '显示顺序',
  path              varchar(200)    default ''                 comment '路由地址',
  component         varchar(255)    default null               comment '组件路径',
  query             varchar(255)    default null               comment '路由参数',
  route_name        varchar(50)     default ''                 comment '路由名称',
  is_frame          int(1)          default 1                  comment '是否为外链（0是 1否）',
  is_cache          int(1)          default 0                  comment '是否缓存（0缓存 1不缓存）',
  menu_type         char(1)         default ''                 comment '菜单类型（M目录 C菜单 F按钮）',
  visible           char(1)         default 0                  comment '菜单状态（0显示 1隐藏）',
  status            char(1)         default 0                  comment '菜单状态（0正常 1停用）',
  perms             varchar(100)    default null               comment '权限标识',
  icon              varchar(100)    default '#'                comment '菜单图标',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default ''                 comment '备注',
  primary key (menu_id)
) engine=innodb auto_increment=2000 comment = '菜单权限表';

-- ----------------------------
-- 初始化-菜单信息表数据
-- ----------------------------
-- 一级菜单
insert into sys_menu values('1', '系统管理', '0', '1', 'system',           null, '', '', 1, 0, 'M', '0', '0', '', 'system',   'admin', sysdate(), '', null, '系统管理目录');
insert into sys_menu values('2', '系统监控', '0', '2', 'monitor',          null, '', '', 1, 0, 'M', '0', '0', '', 'monitor',  'admin', sysdate(), '', null, '系统监控目录');
insert into sys_menu values('3', '系统工具', '0', '3', 'tool',             null, '', '', 1, 0, 'M', '0', '0', '', 'tool',     'admin', sysdate(), '', null, '系统工具目录');
-- 二级菜单
insert into sys_menu values('100',  '用户管理', '1',   '1', 'user',       'system/user/index',        '', '', 1, 0, 'C', '0', '0', 'system:user:list',        'user',          'admin', sysdate(), '', null, '用户管理菜单');
insert into sys_menu values('101',  '角色管理', '1',   '2', 'role',       'system/role/index',        '', '', 1, 0, 'C', '0', '0', 'system:role:list',        'peoples',       'admin', sysdate(), '', null, '角色管理菜单');
insert into sys_menu values('102',  '菜单管理', '1',   '3', 'menu',       'system/menu/index',        '', '', 1, 0, 'C', '0', '0', 'system:menu:list',        'tree-table',    'admin', sysdate(), '', null, '菜单管理菜单');
insert into sys_menu values('103',  '部门管理', '1',   '4', 'dept',       'system/dept/index',        '', '', 1, 0, 'C', '0', '0', 'system:dept:list',        'tree',          'admin', sysdate(), '', null, '部门管理菜单');
insert into sys_menu values('104',  '岗位管理', '1',   '5', 'post',       'system/post/index',        '', '', 1, 0, 'C', '0', '0', 'system:post:list',        'post',          'admin', sysdate(), '', null, '岗位管理菜单');
insert into sys_menu values('105',  '字典管理', '1',   '6', 'dict',       'system/dict/index',        '', '', 1, 0, 'C', '0', '0', 'system:dict:list',        'dict',          'admin', sysdate(), '', null, '字典管理菜单');
insert into sys_menu values('106',  '参数设置', '1',   '7', 'config',     'system/config/index',      '', '', 1, 0, 'C', '0', '0', 'system:config:list',      'edit',          'admin', sysdate(), '', null, '参数设置菜单');
insert into sys_menu values('107',  '通知公告', '1',   '8', 'notice',     'system/notice/index',      '', '', 1, 0, 'C', '0', '0', 'system:notice:list',      'message',       'admin', sysdate(), '', null, '通知公告菜单');
insert into sys_menu values('108',  '日志管理', '1',   '9', 'log',        '',                         '', '', 1, 0, 'M', '0', '0', '',                        'log',           'admin', sysdate(), '', null, '日志管理菜单');
insert into sys_menu values('109',  '在线用户', '2',   '1', 'online',     'monitor/online/index',     '', '', 1, 0, 'C', '0', '0', 'monitor:online:list',     'online',        'admin', sysdate(), '', null, '在线用户菜单');
insert into sys_menu values('110',  '定时任务', '2',   '2', 'job',        'monitor/job/index',        '', '', 1, 0, 'C', '0', '0', 'monitor:job:list',        'job',           'admin', sysdate(), '', null, '定时任务菜单');
insert into sys_menu values('111',  '数据监控', '2',   '3', 'druid',      'monitor/druid/index',      '', '', 1, 0, 'C', '0', '0', 'monitor:druid:list',      'druid',         'admin', sysdate(), '', null, '数据监控菜单');
insert into sys_menu values('112',  '服务监控', '2',   '4', 'server',     'monitor/server/index',     '', '', 1, 0, 'C', '0', '0', 'monitor:server:list',     'server',        'admin', sysdate(), '', null, '服务监控菜单');
insert into sys_menu values('113',  '缓存监控', '2',   '5', 'cache',      'monitor/cache/index',      '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list',      'redis',         'admin', sysdate(), '', null, '缓存监控菜单');
insert into sys_menu values('114',  '缓存列表', '2',   '6', 'cacheList',  'monitor/cache/list',       '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list',      'redis-list',    'admin', sysdate(), '', null, '缓存列表菜单');
insert into sys_menu values('115',  '表单构建', '3',   '1', 'build',      'tool/build/index',         '', '', 1, 0, 'C', '0', '0', 'tool:build:list',         'build',         'admin', sysdate(), '', null, '表单构建菜单');
insert into sys_menu values('116',  '代码生成', '3',   '2', 'gen',        'tool/gen/index',           '', '', 1, 0, 'C', '0', '0', 'tool:gen:list',           'code',          'admin', sysdate(), '', null, '代码生成菜单');
insert into sys_menu values('117',  '系统接口', '3',   '3', 'swagger',    'tool/swagger/index',       '', '', 1, 0, 'C', '0', '0', 'tool:swagger:list',       'swagger',       'admin', sysdate(), '', null, '系统接口菜单');
-- 三级菜单
insert into sys_menu values('500',  '操作日志', '108', '1', 'operlog',    'monitor/operlog/index',    '', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list',    'form',          'admin', sysdate(), '', null, '操作日志菜单');
insert into sys_menu values('501',  '登录日志', '108', '2', 'logininfor', 'monitor/logininfor/index', '', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor',    'admin', sysdate(), '', null, '登录日志菜单');
-- 用户管理按钮
insert into sys_menu values('1000', '用户查询', '100', '1',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:query',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1001', '用户新增', '100', '2',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:add',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1002', '用户修改', '100', '3',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1003', '用户删除', '100', '4',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1004', '用户导出', '100', '5',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:export',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1005', '用户导入', '100', '6',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:import',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1006', '重置密码', '100', '7',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd',       '#', 'admin', sysdate(), '', null, '');
-- 角色管理按钮
insert into sys_menu values('1007', '角色查询', '101', '1',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:query',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1008', '角色新增', '101', '2',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:add',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1009', '角色修改', '101', '3',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1010', '角色删除', '101', '4',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1011', '角色导出', '101', '5',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:export',         '#', 'admin', sysdate(), '', null, '');
-- 菜单管理按钮
insert into sys_menu values('1012', '菜单查询', '102', '1',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1013', '菜单新增', '102', '2',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1014', '菜单修改', '102', '3',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1015', '菜单删除', '102', '4',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove',         '#', 'admin', sysdate(), '', null, '');
-- 部门管理按钮
insert into sys_menu values('1016', '部门查询', '103', '1',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1017', '部门新增', '103', '2',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1018', '部门修改', '103', '3',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1019', '部门删除', '103', '4',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove',         '#', 'admin', sysdate(), '', null, '');
-- 岗位管理按钮
insert into sys_menu values('1020', '岗位查询', '104', '1',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:query',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1021', '岗位新增', '104', '2',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:add',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1022', '岗位修改', '104', '3',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1023', '岗位删除', '104', '4',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1024', '岗位导出', '104', '5',  '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:export',         '#', 'admin', sysdate(), '', null, '');
-- 字典管理按钮
insert into sys_menu values('1025', '字典查询', '105', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:query',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1026', '字典新增', '105', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:add',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1027', '字典修改', '105', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1028', '字典删除', '105', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1029', '字典导出', '105', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:export',         '#', 'admin', sysdate(), '', null, '');
-- 参数设置按钮
insert into sys_menu values('1030', '参数查询', '106', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:query',        '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1031', '参数新增', '106', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:add',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1032', '参数修改', '106', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:edit',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1033', '参数删除', '106', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:remove',       '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1034', '参数导出', '106', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:export',       '#', 'admin', sysdate(), '', null, '');
-- 通知公告按钮
insert into sys_menu values('1035', '公告查询', '107', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:query',        '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1036', '公告新增', '107', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:add',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1037', '公告修改', '107', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1038', '公告删除', '107', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove',       '#', 'admin', sysdate(), '', null, '');
-- 操作日志按钮
insert into sys_menu values('1039', '操作查询', '500', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query',      '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1040', '操作删除', '500', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1041', '日志导出', '500', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export',     '#', 'admin', sysdate(), '', null, '');
-- 登录日志按钮
insert into sys_menu values('1042', '登录查询', '501', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1043', '登录删除', '501', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1044', '日志导出', '501', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1045', '账户解锁', '501', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock',  '#', 'admin', sysdate(), '', null, '');
-- 在线用户按钮
insert into sys_menu values('1046', '在线查询', '109', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query',       '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1047', '批量强退', '109', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1048', '单条强退', '109', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', sysdate(), '', null, '');
-- 定时任务按钮
insert into sys_menu values('1049', '任务查询', '110', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:query',          '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1050', '任务新增', '110', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:add',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1051', '任务修改', '110', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:edit',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1052', '任务删除', '110', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:remove',         '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1053', '状态修改', '110', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:changeStatus',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1054', '任务导出', '110', '6', '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:export',         '#', 'admin', sysdate(), '', null, '');
-- 代码生成按钮
insert into sys_menu values('1055', '生成查询', '116', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:query',             '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1056', '生成修改', '116', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:edit',              '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1057', '生成删除', '116', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:remove',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1058', '导入代码', '116', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:import',            '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1059', '预览代码', '116', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:preview',           '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('1060', '生成代码', '116', '6', '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:code',              '#', 'admin', sysdate(), '', null, '');

-- 业务管理菜单
insert into sys_menu values('2000', '业务管理', '0', '5', 'business',        null,                       '', '', 1, 0, 'M', '0', '0', '',                         'shopping', 'admin', sysdate(), '', null, '业务管理目录');
insert into sys_menu values('2001', '仓储管理', '0', '6', 'wms',             null,                       '', '', 1, 0, 'M', '0', '0', '',                         'table',    'admin', sysdate(), '', null, '仓储管理目录');
insert into sys_menu values('2002', '仓库档案', '2001', '1', 'warehouse',    'business/warehouse/index', '', '', 1, 0, 'C', '0', '0', 'business:warehouse:list', 'table',    'admin', sysdate(), '', null, '仓库档案菜单');
insert into sys_menu values('2003', '库存',     '2001', '2', 'stock',        'business/stock/index',     '', '', 1, 0, 'C', '0', '0', 'business:stock:list',     'list',     'admin', sysdate(), '', null, '库存菜单');
insert into sys_menu values('2004', '入库单',   '2001', '3', 'inbound',      'business/inbound/index',   '', '', 1, 0, 'C', '0', '0', 'business:inbound:list',   'date',     'admin', sysdate(), '', null, '入库单菜单');
insert into sys_menu values('2005', '出库单',   '2001', '4', 'outbound',     'business/outbound/index',  '', '', 1, 0, 'C', '0', '0', 'business:outbound:list',  'date',     'admin', sysdate(), '', null, '出库单菜单');
insert into sys_menu values('2006', '盘点单',   '2001', '5', 'inventoryCheck', 'business/inventoryCheck/index', '', '', 1, 0, 'C', '0', '0', 'business:inventoryCheck:list', 'form', 'admin', sysdate(), '', null, '盘点单菜单');
insert into sys_menu values('2007', '调拨单',   '2001', '6', 'transfer',     'business/transfer/index',  '', '', 1, 0, 'C', '0', '0', 'business:transfer:list',  'list',     'admin', sysdate(), '', null, '调拨单菜单');
insert into sys_menu values('2008', '库存流水', '2001', '7', 'stockLog',     'business/stockLog/index',  '', '', 1, 0, 'C', '0', '0', 'business:stockLog:list',  'log',      'admin', sysdate(), '', null, '库存流水菜单');
insert into sys_menu values('2009', '入库明细', '2001', '8', 'inboundItem',  'business/inboundItem/index', '', '', 1, 0, 'C', '0', '0', 'business:inboundItem:list', 'number', 'admin', sysdate(), '', null, '入库明细菜单');
insert into sys_menu values('2010', '出库明细', '2001', '9', 'outboundItem', 'business/outboundItem/index', '', '', 1, 0, 'C', '0', '0', 'business:outboundItem:list', 'number', 'admin', sysdate(), '', null, '出库明细菜单');
insert into sys_menu values('2011', '仓库统计', '2001', '1', 'warehouseStatistics', 'business/warehouseStatistics/index', '', '', 1, 0, 'C', '0', '0', 'business:warehouseStatistics:list', 'chart', 'admin', sysdate(), '', null, '仓库统计菜单');
insert into sys_menu values('2400', '客户管理', '2000', '1', 'customer',    'business/customer/index', '', '', 1, 0, 'C', '0', '0', 'business:customer:list',   'user',     'admin', sysdate(), '', null, '客户档案菜单');
insert into sys_menu values('2401', '供应商管理', '2000', '2', 'supplier',  'business/supplier/index', '', '', 1, 0, 'C', '0', '0', 'business:supplier:list',  'peoples',  'admin', sysdate(), '', null, '供应商档案菜单');
insert into sys_menu values('2402', '商品管理', '2000', '3', 'product',     'business/product/index',  '', '', 1, 0, 'C', '0', '0', 'business:product:list',   'shopping', 'admin', sysdate(), '', null, '商品档案菜单');
insert into sys_menu values('2410', '采购退货', '2001', '10', 'purchaseReturn', 'business/purchaseReturn/index', '', '', 1, 0, 'C', '0', '0', 'business:purchaseReturn:list', 'date', 'admin', sysdate(), '', null, '采购退货菜单');
insert into sys_menu values('2411', '销售退货', '2001', '11', 'saleReturn', 'business/saleReturn/index', '', '', 1, 0, 'C', '0', '0', 'business:saleReturn:list', 'date', 'admin', sysdate(), '', null, '销售退货菜单');

-- 仓库档案按钮
insert into sys_menu values('2100', '仓库查询', '2002', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:warehouse:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2101', '仓库新增', '2002', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:warehouse:add',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2102', '仓库修改', '2002', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:warehouse:edit',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2103', '仓库删除', '2002', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:warehouse:remove','#', 'admin', sysdate(), '', null, '');

-- 库存按钮
insert into sys_menu values('2110', '库存查询', '2003', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:stock:query',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2111', '库存新增', '2003', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:stock:add',       '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2112', '库存修改', '2003', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:stock:edit',      '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2113', '库存删除', '2003', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:stock:remove',    '#', 'admin', sysdate(), '', null, '');

-- 入库单按钮
insert into sys_menu values('2120', '入库查询', '2004', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inbound:query',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2121', '入库新增', '2004', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inbound:add',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2122', '入库修改', '2004', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inbound:edit',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2123', '入库删除', '2004', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inbound:remove',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2124', '入库审核', '2004', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inbound:audit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2125', '入库退货', '2004', '6', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inbound:return',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2126', '入库打印', '2004', '7', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inbound:print',   '#', 'admin', sysdate(), '', null, '');

-- 出库单按钮
insert into sys_menu values('2130', '出库查询', '2005', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:outbound:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2131', '出库新增', '2005', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:outbound:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2132', '出库修改', '2005', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:outbound:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2133', '出库删除', '2005', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:outbound:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2134', '出库审核', '2005', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:outbound:audit',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2135', '出库打印', '2005', '8', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:outbound:print',  '#', 'admin', sysdate(), '', null, '');

-- 盘点单按钮
insert into sys_menu values('2140', '盘点查询', '2006', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inventoryCheck:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2141', '盘点新增', '2006', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inventoryCheck:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2142', '盘点修改', '2006', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inventoryCheck:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2143', '盘点删除', '2006', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inventoryCheck:remove', '#', 'admin', sysdate(), '', null, '');

-- 调拨单按钮
insert into sys_menu values('2150', '调拨查询', '2007', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:transfer:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2151', '调拨新增', '2007', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:transfer:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2152', '调拨修改', '2007', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:transfer:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2153', '调拨删除', '2007', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:transfer:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2154', '调拨打印', '2007', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:transfer:print',  '#', 'admin', sysdate(), '', null, '');

-- 库存流水按钮
insert into sys_menu values('2160', '流水查询', '2008', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:stockLog:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2161', '流水新增', '2008', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:stockLog:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2162', '流水修改', '2008', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:stockLog:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2163', '流水删除', '2008', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:stockLog:remove', '#', 'admin', sysdate(), '', null, '');

-- 入库明细按钮
insert into sys_menu values('2170', '明细查询', '2009', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inboundItem:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2171', '明细新增', '2009', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inboundItem:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2172', '明细修改', '2009', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inboundItem:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2173', '明细删除', '2009', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inboundItem:remove', '#', 'admin', sysdate(), '', null, '');

-- 出库明细按钮
insert into sys_menu values('2180', '明细查询', '2010', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:outboundItem:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2181', '明细新增', '2010', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:outboundItem:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2182', '明细修改', '2010', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:outboundItem:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2183', '明细删除', '2010', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:outboundItem:remove', '#', 'admin', sysdate(), '', null, '');

-- 出库单电商动作按钮
insert into sys_menu values('2184', '出库发货', '2005', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:outbound:ship',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2185', '出库签收', '2005', '6', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:outbound:sign',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2186', '出库退货', '2005', '7', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:outbound:return', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2187', '仓库统计查询', '2011', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:warehouseStatistics:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2420', '客户查询', '2400', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:customer:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2421', '客户新增', '2400', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:customer:add',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2422', '客户修改', '2400', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:customer:edit',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2423', '客户删除', '2400', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:customer:remove','#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2430', '供应商查询', '2401', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:supplier:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2431', '供应商新增', '2401', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:supplier:add',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2432', '供应商修改', '2401', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:supplier:edit',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2433', '供应商删除', '2401', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:supplier:remove','#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2440', '商品查询', '2402', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:product:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2441', '商品新增', '2402', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:product:add',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2442', '商品修改', '2402', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:product:edit',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2443', '商品删除', '2402', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:product:remove','#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2450', '采购退货查询', '2410', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:purchaseReturn:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2451', '采购退货新增', '2410', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:purchaseReturn:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2452', '采购退货修改', '2410', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:purchaseReturn:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2453', '采购退货删除', '2410', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:purchaseReturn:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2454', '采购退货审核', '2410', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:purchaseReturn:audit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2460', '销售退货查询', '2411', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:saleReturn:query', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2461', '销售退货新增', '2411', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:saleReturn:add', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2462', '销售退货修改', '2411', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:saleReturn:edit', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2463', '销售退货删除', '2411', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:saleReturn:remove', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2464', '销售退货审核', '2411', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:saleReturn:audit', '#', 'admin', sysdate(), '', null, '');

-- 财务管理菜单
insert into sys_menu values('2200', '财务管理', '0', '7', 'finance',          null,                         '', '', 1, 0, 'M', '0', '0', '',                         'money',    'admin', sysdate(), '', null, '财务管理目录');
insert into sys_menu values('2201', '应收单',   '2200', '1', 'receivable',    'business/receivable/index',  '', '', 1, 0, 'C', '0', '0', 'business:receivable:list', 'money',    'admin', sysdate(), '', null, '应收单菜单');
insert into sys_menu values('2202', '应付单',   '2200', '2', 'payable',       'business/payable/index',     '', '', 1, 0, 'C', '0', '0', 'business:payable:list',    'money',    'admin', sysdate(), '', null, '应付单菜单');
insert into sys_menu values('2203', '收款单',   '2200', '3', 'receipt',       'business/receipt/index',     '', '', 1, 0, 'C', '0', '0', 'business:receipt:list',    'money',    'admin', sysdate(), '', null, '收款单菜单');
insert into sys_menu values('2204', '付款单',   '2200', '4', 'payment',       'business/payment/index',     '', '', 1, 0, 'C', '0', '0', 'business:payment:list',    'money',    'admin', sysdate(), '', null, '付款单菜单');
insert into sys_menu values('2205', '资金账户', '2200', '5', 'fundAccount',   'business/fundAccount/index', '', '', 1, 0, 'C', '0', '0', 'business:fundAccount:list','table',    'admin', sysdate(), '', null, '资金账户菜单');
insert into sys_menu values('2206', '费用单',   '2200', '6', 'expense',       'business/expense/index',     '', '', 1, 0, 'C', '0', '0', 'business:expense:list',    'list',     'admin', sysdate(), '', null, '费用单菜单');
insert into sys_menu values('2207', '核销单',   '2200', '7', 'writeOff',      'business/writeOff/index',    '', '', 1, 0, 'C', '0', '0', 'business:writeOff:list',   'form',     'admin', sysdate(), '', null, '核销单菜单');
insert into sys_menu values('2208', '坏账单',   '2200', '8', 'badDebt',       'business/badDebt/index',     '', '', 1, 0, 'C', '0', '0', 'business:badDebt:list',    'question', 'admin', sysdate(), '', null, '坏账单菜单');
insert into sys_menu values('2209', '成本核算', '2200', '9', 'costCalculate', 'business/costCalculate/index','', '', 1, 0, 'C', '0', '0', 'business:costCalculate:list','number',  'admin', sysdate(), '', null, '成本核算菜单');
insert into sys_menu values('2210', '财务报表', '2200', '10','report',        'business/report/index',      '', '', 1, 0, 'C', '0', '0', 'business:report:list',     'chart',    'admin', sysdate(), '', null, '财务报表菜单');

-- 财务管理按钮
insert into sys_menu values('2300', '应收查询',   '2201', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:receivable:query',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2301', '应收新增',   '2201', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:receivable:add',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2302', '应收修改',   '2201', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:receivable:edit',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2303', '应收删除',   '2201', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:receivable:remove',  '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values('2310', '应付查询',   '2202', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:payable:query',      '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2311', '应付新增',   '2202', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:payable:add',        '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2312', '应付修改',   '2202', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:payable:edit',       '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2313', '应付删除',   '2202', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:payable:remove',     '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values('2320', '收款查询',   '2203', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:receipt:query',      '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2321', '收款新增',   '2203', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:receipt:add',        '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2322', '收款修改',   '2203', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:receipt:edit',       '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2323', '收款删除',   '2203', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:receipt:remove',     '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values('2330', '付款查询',   '2204', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:payment:query',      '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2331', '付款新增',   '2204', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:payment:add',        '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2332', '付款修改',   '2204', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:payment:edit',       '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2333', '付款删除',   '2204', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:payment:remove',     '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values('2340', '账户查询',   '2205', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:fundAccount:query',  '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2341', '账户新增',   '2205', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:fundAccount:add',    '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2342', '账户修改',   '2205', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:fundAccount:edit',   '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2343', '账户删除',   '2205', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:fundAccount:remove', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values('2350', '费用查询',   '2206', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:expense:query',      '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2351', '费用新增',   '2206', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:expense:add',        '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2352', '费用修改',   '2206', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:expense:edit',       '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2353', '费用删除',   '2206', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:expense:remove',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2354', '费用确认',   '2206', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:expense:confirm',    '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values('2360', '核销查询',   '2207', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:writeOff:query',     '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2361', '核销新增',   '2207', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:writeOff:add',       '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2362', '核销修改',   '2207', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:writeOff:edit',      '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2363', '核销删除',   '2207', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:writeOff:remove',    '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values('2370', '坏账查询',   '2208', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:badDebt:query',      '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2371', '坏账新增',   '2208', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:badDebt:add',        '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2372', '坏账修改',   '2208', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:badDebt:edit',       '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2373', '坏账删除',   '2208', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:badDebt:remove',     '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values('2380', '核算计算',   '2209', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:costCalculate:calculate', '#', 'admin', sysdate(), '', null, '');
insert into sys_menu values('2381', '核算查询',   '2209', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:costCalculate:query',     '#', 'admin', sysdate(), '', null, '');

insert into sys_menu values('2390', '报表查询',   '2210', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:report:query',      '#', 'admin', sysdate(), '', null, '');

-- ----------------------------
-- 6、用户和角色关联表  用户N-1角色
-- ----------------------------
drop table if exists sys_user_role;
create table sys_user_role (
  user_id   bigint(20) not null comment '用户ID',
  role_id   bigint(20) not null comment '角色ID',
  primary key(user_id, role_id)
) engine=innodb comment = '用户和角色关联表';

-- ----------------------------
-- 初始化-用户和角色关联表数据
-- ----------------------------
insert into sys_user_role values ('1', '1');
insert into sys_user_role values ('2', '2');


-- ----------------------------
-- 7、角色和菜单关联表  角色1-N菜单
-- ----------------------------
drop table if exists sys_role_menu;
create table sys_role_menu (
  role_id   bigint(20) not null comment '角色ID',
  menu_id   bigint(20) not null comment '菜单ID',
  primary key(role_id, menu_id)
) engine=innodb comment = '角色和菜单关联表';

-- ----------------------------
-- 初始化-角色和菜单关联表数据
-- ----------------------------
insert into sys_role_menu values ('2', '1');
insert into sys_role_menu values ('2', '2');
insert into sys_role_menu values ('2', '3');
insert into sys_role_menu values ('2', '100');
insert into sys_role_menu values ('2', '101');
insert into sys_role_menu values ('2', '102');
insert into sys_role_menu values ('2', '103');
insert into sys_role_menu values ('2', '104');
insert into sys_role_menu values ('2', '105');
insert into sys_role_menu values ('2', '106');
insert into sys_role_menu values ('2', '107');
insert into sys_role_menu values ('2', '108');
insert into sys_role_menu values ('2', '109');
insert into sys_role_menu values ('2', '110');
insert into sys_role_menu values ('2', '111');
insert into sys_role_menu values ('2', '112');
insert into sys_role_menu values ('2', '113');
insert into sys_role_menu values ('2', '114');
insert into sys_role_menu values ('2', '115');
insert into sys_role_menu values ('2', '116');
insert into sys_role_menu values ('2', '117');
insert into sys_role_menu values ('2', '500');
insert into sys_role_menu values ('2', '501');
insert into sys_role_menu values ('2', '1000');
insert into sys_role_menu values ('2', '1001');
insert into sys_role_menu values ('2', '1002');
insert into sys_role_menu values ('2', '1003');
insert into sys_role_menu values ('2', '1004');
insert into sys_role_menu values ('2', '1005');
insert into sys_role_menu values ('2', '1006');
insert into sys_role_menu values ('2', '1007');
insert into sys_role_menu values ('2', '1008');
insert into sys_role_menu values ('2', '1009');
insert into sys_role_menu values ('2', '1010');
insert into sys_role_menu values ('2', '1011');
insert into sys_role_menu values ('2', '1012');
insert into sys_role_menu values ('2', '1013');
insert into sys_role_menu values ('2', '1014');
insert into sys_role_menu values ('2', '1015');
insert into sys_role_menu values ('2', '1016');
insert into sys_role_menu values ('2', '1017');
insert into sys_role_menu values ('2', '1018');
insert into sys_role_menu values ('2', '1019');
insert into sys_role_menu values ('2', '1020');
insert into sys_role_menu values ('2', '1021');
insert into sys_role_menu values ('2', '1022');
insert into sys_role_menu values ('2', '1023');
insert into sys_role_menu values ('2', '1024');
insert into sys_role_menu values ('2', '1025');
insert into sys_role_menu values ('2', '1026');
insert into sys_role_menu values ('2', '1027');
insert into sys_role_menu values ('2', '1028');
insert into sys_role_menu values ('2', '1029');
insert into sys_role_menu values ('2', '1030');
insert into sys_role_menu values ('2', '1031');
insert into sys_role_menu values ('2', '1032');
insert into sys_role_menu values ('2', '1033');
insert into sys_role_menu values ('2', '1034');
insert into sys_role_menu values ('2', '1035');
insert into sys_role_menu values ('2', '1036');
insert into sys_role_menu values ('2', '1037');
insert into sys_role_menu values ('2', '1038');
insert into sys_role_menu values ('2', '1039');
insert into sys_role_menu values ('2', '1040');
insert into sys_role_menu values ('2', '1041');
insert into sys_role_menu values ('2', '1042');
insert into sys_role_menu values ('2', '1043');
insert into sys_role_menu values ('2', '1044');
insert into sys_role_menu values ('2', '1045');
insert into sys_role_menu values ('2', '1046');
insert into sys_role_menu values ('2', '1047');
insert into sys_role_menu values ('2', '1048');
insert into sys_role_menu values ('2', '1049');
insert into sys_role_menu values ('2', '1050');
insert into sys_role_menu values ('2', '1051');
insert into sys_role_menu values ('2', '1052');
insert into sys_role_menu values ('2', '1053');
insert into sys_role_menu values ('2', '1054');
insert into sys_role_menu values ('2', '1055');
insert into sys_role_menu values ('2', '1056');
insert into sys_role_menu values ('2', '1057');
insert into sys_role_menu values ('2', '1058');
insert into sys_role_menu values ('2', '1059');
insert into sys_role_menu values ('2', '1060');
insert into sys_role_menu values ('2', '2000');
insert into sys_role_menu values ('2', '2001');
insert into sys_role_menu values ('2', '2002');
insert into sys_role_menu values ('2', '2003');
insert into sys_role_menu values ('2', '2004');
insert into sys_role_menu values ('2', '2005');
insert into sys_role_menu values ('2', '2006');
insert into sys_role_menu values ('2', '2007');
insert into sys_role_menu values ('2', '2008');
insert into sys_role_menu values ('2', '2009');
insert into sys_role_menu values ('2', '2010');
insert into sys_role_menu values ('2', '2011');
insert into sys_role_menu values ('2', '2100');
insert into sys_role_menu values ('2', '2101');
insert into sys_role_menu values ('2', '2102');
insert into sys_role_menu values ('2', '2103');
insert into sys_role_menu values ('2', '2110');
insert into sys_role_menu values ('2', '2111');
insert into sys_role_menu values ('2', '2112');
insert into sys_role_menu values ('2', '2113');
insert into sys_role_menu values ('2', '2120');
insert into sys_role_menu values ('2', '2121');
insert into sys_role_menu values ('2', '2122');
insert into sys_role_menu values ('2', '2123');
insert into sys_role_menu values ('2', '2124');
insert into sys_role_menu values ('2', '2126');
insert into sys_role_menu values ('2', '2130');
insert into sys_role_menu values ('2', '2131');
insert into sys_role_menu values ('2', '2132');
insert into sys_role_menu values ('2', '2133');
insert into sys_role_menu values ('2', '2134');
insert into sys_role_menu values ('2', '2135');
insert into sys_role_menu values ('2', '2140');
insert into sys_role_menu values ('2', '2141');
insert into sys_role_menu values ('2', '2142');
insert into sys_role_menu values ('2', '2143');
insert into sys_role_menu values ('2', '2150');
insert into sys_role_menu values ('2', '2151');
insert into sys_role_menu values ('2', '2152');
insert into sys_role_menu values ('2', '2153');
insert into sys_role_menu values ('2', '2154');
insert into sys_role_menu values ('2', '2160');
insert into sys_role_menu values ('2', '2161');
insert into sys_role_menu values ('2', '2162');
insert into sys_role_menu values ('2', '2163');
insert into sys_role_menu values ('2', '2170');
insert into sys_role_menu values ('2', '2171');
insert into sys_role_menu values ('2', '2172');
insert into sys_role_menu values ('2', '2173');
insert into sys_role_menu values ('2', '2180');
insert into sys_role_menu values ('2', '2181');
insert into sys_role_menu values ('2', '2182');
insert into sys_role_menu values ('2', '2183');
insert into sys_role_menu values ('2', '2184');
insert into sys_role_menu values ('2', '2185');
insert into sys_role_menu values ('2', '2186');
insert into sys_role_menu values ('2', '2187');
insert into sys_role_menu values ('2', '2400');
insert into sys_role_menu values ('2', '2401');
insert into sys_role_menu values ('2', '2402');
insert into sys_role_menu values ('2', '2410');
insert into sys_role_menu values ('2', '2411');
insert into sys_role_menu values ('2', '2125');
insert into sys_role_menu values ('2', '2420');
insert into sys_role_menu values ('2', '2421');
insert into sys_role_menu values ('2', '2422');
insert into sys_role_menu values ('2', '2423');
insert into sys_role_menu values ('2', '2430');
insert into sys_role_menu values ('2', '2431');
insert into sys_role_menu values ('2', '2432');
insert into sys_role_menu values ('2', '2433');
insert into sys_role_menu values ('2', '2440');
insert into sys_role_menu values ('2', '2441');
insert into sys_role_menu values ('2', '2442');
insert into sys_role_menu values ('2', '2443');
insert into sys_role_menu values ('2', '2450');
insert into sys_role_menu values ('2', '2453');
insert into sys_role_menu values ('2', '2460');
insert into sys_role_menu values ('2', '2463');
insert into sys_role_menu values ('2', '2200');
insert into sys_role_menu values ('2', '2201');
insert into sys_role_menu values ('2', '2202');
insert into sys_role_menu values ('2', '2203');
insert into sys_role_menu values ('2', '2204');
insert into sys_role_menu values ('2', '2205');
insert into sys_role_menu values ('2', '2206');
insert into sys_role_menu values ('2', '2207');
insert into sys_role_menu values ('2', '2208');
insert into sys_role_menu values ('2', '2209');
insert into sys_role_menu values ('2', '2210');
insert into sys_role_menu values ('2', '2300');
insert into sys_role_menu values ('2', '2301');
insert into sys_role_menu values ('2', '2302');
insert into sys_role_menu values ('2', '2303');
insert into sys_role_menu values ('2', '2310');
insert into sys_role_menu values ('2', '2311');
insert into sys_role_menu values ('2', '2312');
insert into sys_role_menu values ('2', '2313');
insert into sys_role_menu values ('2', '2320');
insert into sys_role_menu values ('2', '2321');
insert into sys_role_menu values ('2', '2322');
insert into sys_role_menu values ('2', '2323');
insert into sys_role_menu values ('2', '2330');
insert into sys_role_menu values ('2', '2331');
insert into sys_role_menu values ('2', '2332');
insert into sys_role_menu values ('2', '2333');
insert into sys_role_menu values ('2', '2340');
insert into sys_role_menu values ('2', '2341');
insert into sys_role_menu values ('2', '2342');
insert into sys_role_menu values ('2', '2343');
insert into sys_role_menu values ('2', '2350');
insert into sys_role_menu values ('2', '2351');
insert into sys_role_menu values ('2', '2352');
insert into sys_role_menu values ('2', '2353');
insert into sys_role_menu values ('2', '2354');
insert into sys_role_menu values ('2', '2360');
insert into sys_role_menu values ('2', '2361');
insert into sys_role_menu values ('2', '2362');
insert into sys_role_menu values ('2', '2363');
insert into sys_role_menu values ('2', '2370');
insert into sys_role_menu values ('2', '2371');
insert into sys_role_menu values ('2', '2372');
insert into sys_role_menu values ('2', '2373');
insert into sys_role_menu values ('2', '2380');
insert into sys_role_menu values ('2', '2381');
insert into sys_role_menu values ('2', '2390');

-- 仓管角色菜单
insert into sys_role_menu values ('3', '2000');
insert into sys_role_menu values ('3', '2001');
insert into sys_role_menu values ('3', '2002');
insert into sys_role_menu values ('3', '2003');
insert into sys_role_menu values ('3', '2004');
insert into sys_role_menu values ('3', '2005');
insert into sys_role_menu values ('3', '2006');
insert into sys_role_menu values ('3', '2007');
insert into sys_role_menu values ('3', '2008');
insert into sys_role_menu values ('3', '2009');
insert into sys_role_menu values ('3', '2010');
insert into sys_role_menu values ('3', '2011');
insert into sys_role_menu values ('3', '2100');
insert into sys_role_menu values ('3', '2101');
insert into sys_role_menu values ('3', '2102');
insert into sys_role_menu values ('3', '2103');
insert into sys_role_menu values ('3', '2110');
insert into sys_role_menu values ('3', '2111');
insert into sys_role_menu values ('3', '2112');
insert into sys_role_menu values ('3', '2113');
insert into sys_role_menu values ('3', '2120');
insert into sys_role_menu values ('3', '2121');
insert into sys_role_menu values ('3', '2122');
insert into sys_role_menu values ('3', '2123');
insert into sys_role_menu values ('3', '2124');
insert into sys_role_menu values ('3', '2130');
insert into sys_role_menu values ('3', '2131');
insert into sys_role_menu values ('3', '2132');
insert into sys_role_menu values ('3', '2133');
insert into sys_role_menu values ('3', '2134');
insert into sys_role_menu values ('3', '2140');
insert into sys_role_menu values ('3', '2141');
insert into sys_role_menu values ('3', '2142');
insert into sys_role_menu values ('3', '2143');
insert into sys_role_menu values ('3', '2150');
insert into sys_role_menu values ('3', '2151');
insert into sys_role_menu values ('3', '2152');
insert into sys_role_menu values ('3', '2153');
insert into sys_role_menu values ('3', '2160');
insert into sys_role_menu values ('3', '2161');
insert into sys_role_menu values ('3', '2162');
insert into sys_role_menu values ('3', '2163');
insert into sys_role_menu values ('3', '2170');
insert into sys_role_menu values ('3', '2171');
insert into sys_role_menu values ('3', '2172');
insert into sys_role_menu values ('3', '2173');
insert into sys_role_menu values ('3', '2180');
insert into sys_role_menu values ('3', '2181');
insert into sys_role_menu values ('3', '2182');
insert into sys_role_menu values ('3', '2183');
insert into sys_role_menu values ('3', '2184');
insert into sys_role_menu values ('3', '2185');
insert into sys_role_menu values ('3', '2186');
insert into sys_role_menu values ('3', '2187');
insert into sys_role_menu values ('3', '2400');
insert into sys_role_menu values ('3', '2401');
insert into sys_role_menu values ('3', '2402');
insert into sys_role_menu values ('3', '2410');
insert into sys_role_menu values ('3', '2411');
insert into sys_role_menu values ('3', '2125');
insert into sys_role_menu values ('3', '2420');
insert into sys_role_menu values ('3', '2421');
insert into sys_role_menu values ('3', '2422');
insert into sys_role_menu values ('3', '2423');
insert into sys_role_menu values ('3', '2430');
insert into sys_role_menu values ('3', '2431');
insert into sys_role_menu values ('3', '2432');
insert into sys_role_menu values ('3', '2433');
insert into sys_role_menu values ('3', '2440');
insert into sys_role_menu values ('3', '2441');
insert into sys_role_menu values ('3', '2442');
insert into sys_role_menu values ('3', '2443');
insert into sys_role_menu values ('3', '2450');
insert into sys_role_menu values ('3', '2453');
insert into sys_role_menu values ('3', '2460');
insert into sys_role_menu values ('3', '2463');

-- 采购角色菜单
insert into sys_role_menu values ('4', '2000');
insert into sys_role_menu values ('4', '2001');
insert into sys_role_menu values ('4', '2004');
insert into sys_role_menu values ('4', '2009');
insert into sys_role_menu values ('4', '2120');
insert into sys_role_menu values ('4', '2121');
insert into sys_role_menu values ('4', '2122');
insert into sys_role_menu values ('4', '2123');
insert into sys_role_menu values ('4', '2124');
insert into sys_role_menu values ('4', '2170');
insert into sys_role_menu values ('4', '2171');
insert into sys_role_menu values ('4', '2172');
insert into sys_role_menu values ('4', '2173');
insert into sys_role_menu values ('4', '2410');
insert into sys_role_menu values ('4', '2450');
insert into sys_role_menu values ('4', '2453');
insert into sys_role_menu values ('4', '2125');

-- 销售角色菜单
insert into sys_role_menu values ('5', '2000');
insert into sys_role_menu values ('5', '2001');
insert into sys_role_menu values ('5', '2005');
insert into sys_role_menu values ('5', '2200');
insert into sys_role_menu values ('5', '2201');
insert into sys_role_menu values ('5', '2203');
insert into sys_role_menu values ('5', '2210');
insert into sys_role_menu values ('5', '2130');
insert into sys_role_menu values ('5', '2131');
insert into sys_role_menu values ('5', '2132');
insert into sys_role_menu values ('5', '2133');
insert into sys_role_menu values ('5', '2134');
insert into sys_role_menu values ('5', '2184');
insert into sys_role_menu values ('5', '2185');
insert into sys_role_menu values ('5', '2186');
insert into sys_role_menu values ('5', '2411');
insert into sys_role_menu values ('5', '2460');
insert into sys_role_menu values ('5', '2463');
insert into sys_role_menu values ('5', '2300');
insert into sys_role_menu values ('5', '2301');
insert into sys_role_menu values ('5', '2302');
insert into sys_role_menu values ('5', '2303');
insert into sys_role_menu values ('5', '2320');
insert into sys_role_menu values ('5', '2321');
insert into sys_role_menu values ('5', '2322');
insert into sys_role_menu values ('5', '2323');
insert into sys_role_menu values ('5', '2390');

-- 财务角色菜单
insert into sys_role_menu values ('6', '2000');
insert into sys_role_menu values ('6', '2200');
insert into sys_role_menu values ('6', '2201');
insert into sys_role_menu values ('6', '2202');
insert into sys_role_menu values ('6', '2203');
insert into sys_role_menu values ('6', '2204');
insert into sys_role_menu values ('6', '2205');
insert into sys_role_menu values ('6', '2206');
insert into sys_role_menu values ('6', '2207');
insert into sys_role_menu values ('6', '2208');
insert into sys_role_menu values ('6', '2209');
insert into sys_role_menu values ('6', '2210');
insert into sys_role_menu values ('6', '2300');
insert into sys_role_menu values ('6', '2301');
insert into sys_role_menu values ('6', '2302');
insert into sys_role_menu values ('6', '2303');
insert into sys_role_menu values ('6', '2310');
insert into sys_role_menu values ('6', '2311');
insert into sys_role_menu values ('6', '2312');
insert into sys_role_menu values ('6', '2313');
insert into sys_role_menu values ('6', '2320');
insert into sys_role_menu values ('6', '2321');
insert into sys_role_menu values ('6', '2322');
insert into sys_role_menu values ('6', '2323');
insert into sys_role_menu values ('6', '2330');
insert into sys_role_menu values ('6', '2331');
insert into sys_role_menu values ('6', '2332');
insert into sys_role_menu values ('6', '2333');
insert into sys_role_menu values ('6', '2340');
insert into sys_role_menu values ('6', '2341');
insert into sys_role_menu values ('6', '2342');
insert into sys_role_menu values ('6', '2343');
insert into sys_role_menu values ('6', '2350');
insert into sys_role_menu values ('6', '2351');
insert into sys_role_menu values ('6', '2352');
insert into sys_role_menu values ('6', '2353');
insert into sys_role_menu values ('6', '2354');
insert into sys_role_menu values ('6', '2360');
insert into sys_role_menu values ('6', '2361');
insert into sys_role_menu values ('6', '2362');
insert into sys_role_menu values ('6', '2363');
insert into sys_role_menu values ('6', '2370');
insert into sys_role_menu values ('6', '2371');
insert into sys_role_menu values ('6', '2372');
insert into sys_role_menu values ('6', '2373');
insert into sys_role_menu values ('6', '2380');
insert into sys_role_menu values ('6', '2381');
insert into sys_role_menu values ('6', '2390');

-- ----------------------------
-- 8、角色和部门关联表  角色1-N部门
-- ----------------------------
drop table if exists sys_role_dept;
create table sys_role_dept (
  role_id   bigint(20) not null comment '角色ID',
  dept_id   bigint(20) not null comment '部门ID',
  primary key(role_id, dept_id)
) engine=innodb comment = '角色和部门关联表';

-- ----------------------------
-- 初始化-角色和部门关联表数据
-- ----------------------------
insert into sys_role_dept values ('2', '100');
insert into sys_role_dept values ('2', '101');
insert into sys_role_dept values ('2', '105');


-- ----------------------------
-- 9、用户与岗位关联表  用户1-N岗位
-- ----------------------------
drop table if exists sys_user_post;
create table sys_user_post
(
  user_id   bigint(20) not null comment '用户ID',
  post_id   bigint(20) not null comment '岗位ID',
  primary key (user_id, post_id)
) engine=innodb comment = '用户与岗位关联表';

-- ----------------------------
-- 初始化-用户与岗位关联表数据
-- ----------------------------
insert into sys_user_post values ('1', '1');
insert into sys_user_post values ('2', '2');


-- ----------------------------
-- 10、操作日志记录
-- ----------------------------
drop table if exists sys_oper_log;
create table sys_oper_log (
  oper_id           bigint(20)      not null auto_increment    comment '日志主键',
  title             varchar(50)     default ''                 comment '模块标题',
  business_type     int(2)          default 0                  comment '业务类型（0其它 1新增 2修改 3删除）',
  method            varchar(200)    default ''                 comment '方法名称',
  request_method    varchar(10)     default ''                 comment '请求方式',
  operator_type     int(1)          default 0                  comment '操作类别（0其它 1后台用户 2手机端用户）',
  oper_name         varchar(50)     default ''                 comment '操作人员',
  dept_name         varchar(50)     default ''                 comment '部门名称',
  oper_url          varchar(255)    default ''                 comment '请求URL',
  oper_ip           varchar(128)    default ''                 comment '主机地址',
  oper_location     varchar(255)    default ''                 comment '操作地点',
  oper_param        varchar(2000)   default ''                 comment '请求参数',
  json_result       varchar(2000)   default ''                 comment '返回参数',
  status            int(1)          default 0                  comment '操作状态（0正常 1异常）',
  error_msg         varchar(2000)   default ''                 comment '错误消息',
  oper_time         datetime                                   comment '操作时间',
  cost_time         bigint(20)      default 0                  comment '消耗时间',
  primary key (oper_id),
  key idx_sys_oper_log_bt (business_type),
  key idx_sys_oper_log_s  (status),
  key idx_sys_oper_log_ot (oper_time)
) engine=innodb auto_increment=100 comment = '操作日志记录';


-- ----------------------------
-- 11、字典类型表
-- ----------------------------
drop table if exists sys_dict_type;
create table sys_dict_type
(
  dict_id          bigint(20)      not null auto_increment    comment '字典主键',
  dict_name        varchar(100)    default ''                 comment '字典名称',
  dict_type        varchar(100)    default ''                 comment '字典类型',
  status           char(1)         default '0'                comment '状态（0正常 1停用）',
  create_by        varchar(64)     default ''                 comment '创建者',
  create_time      datetime                                   comment '创建时间',
  update_by        varchar(64)     default ''                 comment '更新者',
  update_time      datetime                                   comment '更新时间',
  remark           varchar(500)    default null               comment '备注',
  primary key (dict_id),
  unique (dict_type)
) engine=innodb auto_increment=100 comment = '字典类型表';

insert into sys_dict_type values(1,  '用户性别', 'sys_user_sex',        '0', 'admin', sysdate(), '', null, '用户性别列表');
insert into sys_dict_type values(2,  '菜单状态', 'sys_show_hide',       '0', 'admin', sysdate(), '', null, '菜单状态列表');
insert into sys_dict_type values(3,  '系统开关', 'sys_normal_disable',  '0', 'admin', sysdate(), '', null, '系统开关列表');
insert into sys_dict_type values(4,  '任务状态', 'sys_job_status',      '0', 'admin', sysdate(), '', null, '任务状态列表');
insert into sys_dict_type values(5,  '任务分组', 'sys_job_group',       '0', 'admin', sysdate(), '', null, '任务分组列表');
insert into sys_dict_type values(6,  '系统是否', 'sys_yes_no',          '0', 'admin', sysdate(), '', null, '系统是否列表');
insert into sys_dict_type values(7,  '通知类型', 'sys_notice_type',     '0', 'admin', sysdate(), '', null, '通知类型列表');
insert into sys_dict_type values(8,  '通知状态', 'sys_notice_status',   '0', 'admin', sysdate(), '', null, '通知状态列表');
insert into sys_dict_type values(9,  '操作类型', 'sys_oper_type',       '0', 'admin', sysdate(), '', null, '操作类型列表');
insert into sys_dict_type values(10, '系统状态', 'sys_common_status',   '0', 'admin', sysdate(), '', null, '登录状态列表');


-- ----------------------------
-- 12、字典数据表
-- ----------------------------
drop table if exists sys_dict_data;
create table sys_dict_data
(
  dict_code        bigint(20)      not null auto_increment    comment '字典编码',
  dict_sort        int(4)          default 0                  comment '字典排序',
  dict_label       varchar(100)    default ''                 comment '字典标签',
  dict_value       varchar(100)    default ''                 comment '字典键值',
  dict_type        varchar(100)    default ''                 comment '字典类型',
  css_class        varchar(100)    default null               comment '样式属性（其他样式扩展）',
  list_class       varchar(100)    default null               comment '表格回显样式',
  is_default       char(1)         default 'N'                comment '是否默认（Y是 N否）',
  status           char(1)         default '0'                comment '状态（0正常 1停用）',
  create_by        varchar(64)     default ''                 comment '创建者',
  create_time      datetime                                   comment '创建时间',
  update_by        varchar(64)     default ''                 comment '更新者',
  update_time      datetime                                   comment '更新时间',
  remark           varchar(500)    default null               comment '备注',
  primary key (dict_code)
) engine=innodb auto_increment=100 comment = '字典数据表';

insert into sys_dict_data values(1,  1,  '男',       '0',       'sys_user_sex',        '',   '',        'Y', '0', 'admin', sysdate(), '', null, '性别男');
insert into sys_dict_data values(2,  2,  '女',       '1',       'sys_user_sex',        '',   '',        'N', '0', 'admin', sysdate(), '', null, '性别女');
insert into sys_dict_data values(3,  3,  '未知',     '2',       'sys_user_sex',        '',   '',        'N', '0', 'admin', sysdate(), '', null, '性别未知');
insert into sys_dict_data values(4,  1,  '显示',     '0',       'sys_show_hide',       '',   'primary', 'Y', '0', 'admin', sysdate(), '', null, '显示菜单');
insert into sys_dict_data values(5,  2,  '隐藏',     '1',       'sys_show_hide',       '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '隐藏菜单');
insert into sys_dict_data values(6,  1,  '正常',     '0',       'sys_normal_disable',  '',   'primary', 'Y', '0', 'admin', sysdate(), '', null, '正常状态');
insert into sys_dict_data values(7,  2,  '停用',     '1',       'sys_normal_disable',  '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '停用状态');
insert into sys_dict_data values(8,  1,  '正常',     '0',       'sys_job_status',      '',   'primary', 'Y', '0', 'admin', sysdate(), '', null, '正常状态');
insert into sys_dict_data values(9,  2,  '暂停',     '1',       'sys_job_status',      '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '停用状态');
insert into sys_dict_data values(10, 1,  '默认',     'DEFAULT', 'sys_job_group',       '',   '',        'Y', '0', 'admin', sysdate(), '', null, '默认分组');
insert into sys_dict_data values(11, 2,  '系统',     'SYSTEM',  'sys_job_group',       '',   '',        'N', '0', 'admin', sysdate(), '', null, '系统分组');
insert into sys_dict_data values(12, 1,  '是',       'Y',       'sys_yes_no',          '',   'primary', 'Y', '0', 'admin', sysdate(), '', null, '系统默认是');
insert into sys_dict_data values(13, 2,  '否',       'N',       'sys_yes_no',          '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '系统默认否');
insert into sys_dict_data values(14, 1,  '通知',     '1',       'sys_notice_type',     '',   'warning', 'Y', '0', 'admin', sysdate(), '', null, '通知');
insert into sys_dict_data values(15, 2,  '公告',     '2',       'sys_notice_type',     '',   'success', 'N', '0', 'admin', sysdate(), '', null, '公告');
insert into sys_dict_data values(16, 1,  '正常',     '0',       'sys_notice_status',   '',   'primary', 'Y', '0', 'admin', sysdate(), '', null, '正常状态');
insert into sys_dict_data values(17, 2,  '关闭',     '1',       'sys_notice_status',   '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '关闭状态');
insert into sys_dict_data values(18, 99, '其他',     '0',       'sys_oper_type',       '',   'info',    'N', '0', 'admin', sysdate(), '', null, '其他操作');
insert into sys_dict_data values(19, 1,  '新增',     '1',       'sys_oper_type',       '',   'info',    'N', '0', 'admin', sysdate(), '', null, '新增操作');
insert into sys_dict_data values(20, 2,  '修改',     '2',       'sys_oper_type',       '',   'info',    'N', '0', 'admin', sysdate(), '', null, '修改操作');
insert into sys_dict_data values(21, 3,  '删除',     '3',       'sys_oper_type',       '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '删除操作');
insert into sys_dict_data values(22, 4,  '授权',     '4',       'sys_oper_type',       '',   'primary', 'N', '0', 'admin', sysdate(), '', null, '授权操作');
insert into sys_dict_data values(23, 5,  '导出',     '5',       'sys_oper_type',       '',   'warning', 'N', '0', 'admin', sysdate(), '', null, '导出操作');
insert into sys_dict_data values(24, 6,  '导入',     '6',       'sys_oper_type',       '',   'warning', 'N', '0', 'admin', sysdate(), '', null, '导入操作');
insert into sys_dict_data values(25, 7,  '强退',     '7',       'sys_oper_type',       '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '强退操作');
insert into sys_dict_data values(26, 8,  '生成代码', '8',       'sys_oper_type',       '',   'warning', 'N', '0', 'admin', sysdate(), '', null, '生成操作');
insert into sys_dict_data values(27, 9,  '清空数据', '9',       'sys_oper_type',       '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '清空操作');
insert into sys_dict_data values(28, 1,  '成功',     '0',       'sys_common_status',   '',   'primary', 'N', '0', 'admin', sysdate(), '', null, '正常状态');
insert into sys_dict_data values(29, 2,  '失败',     '1',       'sys_common_status',   '',   'danger',  'N', '0', 'admin', sysdate(), '', null, '停用状态');


-- ----------------------------
-- 13、参数配置表
-- ----------------------------
drop table if exists sys_config;
create table sys_config (
  config_id         int(5)          not null auto_increment    comment '参数主键',
  config_name       varchar(100)    default ''                 comment '参数名称',
  config_key        varchar(100)    default ''                 comment '参数键名',
  config_value      varchar(500)    default ''                 comment '参数键值',
  config_type       char(1)         default 'N'                comment '系统内置（Y是 N否）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (config_id)
) engine=innodb auto_increment=100 comment = '参数配置表';

insert into sys_config values(1, '主框架页-默认皮肤样式名称',     'sys.index.skinName',               'skin-blue',     'Y', 'admin', sysdate(), '', null, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow' );
insert into sys_config values(2, '用户管理-账号初始密码',         'sys.user.initPassword',            '123456',        'Y', 'admin', sysdate(), '', null, '初始化密码 123456' );
insert into sys_config values(3, '主框架页-侧边栏主题',           'sys.index.sideTheme',              'theme-dark',    'Y', 'admin', sysdate(), '', null, '深色主题theme-dark，浅色主题theme-light' );
insert into sys_config values(4, '账号自助-验证码开关',           'sys.account.captchaEnabled',       'true',          'Y', 'admin', sysdate(), '', null, '是否开启验证码功能（true开启，false关闭）');
insert into sys_config values(5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser',         'false',         'Y', 'admin', sysdate(), '', null, '是否开启注册用户功能（true开启，false关闭）');
insert into sys_config values(6, '用户登录-黑名单列表',           'sys.login.blackIPList',            '',              'Y', 'admin', sysdate(), '', null, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');
insert into sys_config values(7, '用户管理-初始密码修改策略',     'sys.account.initPasswordModify',   '1',             'Y', 'admin', sysdate(), '', null, '0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框');
insert into sys_config values(8, '用户管理-账号密码更新周期',     'sys.account.passwordValidateDays', '0',             'Y', 'admin', sysdate(), '', null, '密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框');


-- ----------------------------
-- 14、系统访问记录
-- ----------------------------
drop table if exists sys_logininfor;
create table sys_logininfor (
  info_id        bigint(20)     not null auto_increment   comment '访问ID',
  user_name      varchar(50)    default ''                comment '用户账号',
  ipaddr         varchar(128)   default ''                comment '登录IP地址',
  login_location varchar(255)   default ''                comment '登录地点',
  browser        varchar(50)    default ''                comment '浏览器类型',
  os             varchar(50)    default ''                comment '操作系统',
  status         char(1)        default '0'               comment '登录状态（0成功 1失败）',
  msg            varchar(255)   default ''                comment '提示消息',
  login_time     datetime                                 comment '访问时间',
  primary key (info_id),
  key idx_sys_logininfor_s  (status),
  key idx_sys_logininfor_lt (login_time)
) engine=innodb auto_increment=100 comment = '系统访问记录';


-- ----------------------------
-- 15、定时任务调度表
-- ----------------------------
drop table if exists sys_job;
create table sys_job (
  job_id              bigint(20)    not null auto_increment    comment '任务ID',
  job_name            varchar(64)   default ''                 comment '任务名称',
  job_group           varchar(64)   default 'DEFAULT'          comment '任务组名',
  invoke_target       varchar(500)  not null                   comment '调用目标字符串',
  cron_expression     varchar(255)  default ''                 comment 'cron执行表达式',
  misfire_policy      varchar(20)   default '3'                comment '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  concurrent          char(1)       default '1'                comment '是否并发执行（0允许 1禁止）',
  status              char(1)       default '0'                comment '状态（0正常 1暂停）',
  create_by           varchar(64)   default ''                 comment '创建者',
  create_time         datetime                                 comment '创建时间',
  update_by           varchar(64)   default ''                 comment '更新者',
  update_time         datetime                                 comment '更新时间',
  remark              varchar(500)  default ''                 comment '备注信息',
  primary key (job_id, job_name, job_group)
) engine=innodb auto_increment=100 comment = '定时任务调度表';

insert into sys_job values(1, '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams',        '0/10 * * * * ?', '3', '1', '1', 'admin', sysdate(), '', null, '');
insert into sys_job values(2, '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(\'ry\')',  '0/15 * * * * ?', '3', '1', '1', 'admin', sysdate(), '', null, '');
insert into sys_job values(3, '系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)',  '0/20 * * * * ?', '3', '1', '1', 'admin', sysdate(), '', null, '');


-- ----------------------------
-- 16、定时任务调度日志表
-- ----------------------------
drop table if exists sys_job_log;
create table sys_job_log (
  job_log_id          bigint(20)     not null auto_increment    comment '任务日志ID',
  job_name            varchar(64)    not null                   comment '任务名称',
  job_group           varchar(64)    not null                   comment '任务组名',
  invoke_target       varchar(500)   not null                   comment '调用目标字符串',
  job_message         varchar(500)                              comment '日志信息',
  status              char(1)        default '0'                comment '执行状态（0正常 1失败）',
  exception_info      varchar(2000)  default ''                 comment '异常信息',
  create_time         datetime                                  comment '创建时间',
  primary key (job_log_id)
) engine=innodb comment = '定时任务调度日志表';


-- ----------------------------
-- 17、通知公告表
-- ----------------------------
drop table if exists sys_notice;
create table sys_notice (
  notice_id         int(4)          not null auto_increment    comment '公告ID',
  notice_title      varchar(50)     not null                   comment '公告标题',
  notice_type       char(1)         not null                   comment '公告类型（1通知 2公告）',
  notice_content    longblob        default null               comment '公告内容',
  status            char(1)         default '0'                comment '公告状态（0正常 1关闭）',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time       datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(255)    default null               comment '备注',
  primary key (notice_id)
) engine=innodb auto_increment=10 comment = '通知公告表';

-- ----------------------------
-- 初始化-公告信息表数据
-- ----------------------------
insert into sys_notice values('1', '温馨提醒：2018-07-01 仓链新版本发布啦', '2', '新版本内容', '0', 'admin', sysdate(), '', null, '管理员');
insert into sys_notice values('2', '维护通知：2018-07-01 仓链系统凌晨维护', '1', '维护内容',   '0', 'admin', sysdate(), '', null, '管理员');


-- ----------------------------
-- 18、代码生成业务表
-- ----------------------------
drop table if exists gen_table;
create table gen_table (
  table_id          bigint(20)      not null auto_increment    comment '编号',
  table_name        varchar(200)    default ''                 comment '表名称',
  table_comment     varchar(500)    default ''                 comment '表描述',
  sub_table_name    varchar(64)     default null               comment '关联子表的表名',
  sub_table_fk_name varchar(64)     default null               comment '子表关联的外键名',
  class_name        varchar(100)    default ''                 comment '实体类名称',
  tpl_category      varchar(200)    default 'crud'             comment '使用的模板（crud单表操作 tree树表操作）',
  tpl_web_type      varchar(30)     default ''                 comment '前端模板类型（element-ui模版 element-plus模版）',
  package_name      varchar(100)                               comment '生成包路径',
  module_name       varchar(30)                                comment '生成模块名',
  business_name     varchar(30)                                comment '生成业务名',
  function_name     varchar(50)                                comment '生成功能名',
  function_author   varchar(50)                                comment '生成功能作者',
  gen_type          char(1)         default '0'                comment '生成代码方式（0zip压缩包 1自定义路径）',
  gen_path          varchar(200)    default '/'                comment '生成路径（不填默认项目路径）',
  options           varchar(1000)                              comment '其它生成选项',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time 	    datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  remark            varchar(500)    default null               comment '备注',
  primary key (table_id)
) engine=innodb auto_increment=1 comment = '代码生成业务表';


-- ----------------------------
-- 19、代码生成业务表字段
-- ----------------------------
drop table if exists gen_table_column;
create table gen_table_column (
  column_id         bigint(20)      not null auto_increment    comment '编号',
  table_id          bigint(20)                                 comment '归属表编号',
  column_name       varchar(200)                               comment '列名称',
  column_comment    varchar(500)                               comment '列描述',
  column_type       varchar(100)                               comment '列类型',
  java_type         varchar(500)                               comment 'JAVA类型',
  java_field        varchar(200)                               comment 'JAVA字段名',
  is_pk             char(1)                                    comment '是否主键（1是）',
  is_increment      char(1)                                    comment '是否自增（1是）',
  is_required       char(1)                                    comment '是否必填（1是）',
  is_insert         char(1)                                    comment '是否为插入字段（1是）',
  is_edit           char(1)                                    comment '是否编辑字段（1是）',
  is_list           char(1)                                    comment '是否列表字段（1是）',
  is_query          char(1)                                    comment '是否查询字段（1是）',
  query_type        varchar(200)    default 'EQ'               comment '查询方式（等于、不等于、大于、小于、范围）',
  html_type         varchar(200)                               comment '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  dict_type         varchar(200)    default ''                 comment '字典类型',
  sort              int                                        comment '排序',
  create_by         varchar(64)     default ''                 comment '创建者',
  create_time 	    datetime                                   comment '创建时间',
  update_by         varchar(64)     default ''                 comment '更新者',
  update_time       datetime                                   comment '更新时间',
  primary key (column_id)
) engine=innodb auto_increment=1 comment = '代码生成业务表字段';

