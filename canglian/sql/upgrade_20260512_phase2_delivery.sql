-- 2026-05-12 二期落地升级脚本
-- 适用范围：统一待办中心、业务链路查询、库存预警中心、基础资料导入权限

drop procedure if exists add_column_if_absent;
delimiter $$
create procedure add_column_if_absent(
    in target_table_name varchar(64),
    in target_column_name varchar(64),
    in target_column_definition text
)
begin
    if not exists (
        select 1
        from information_schema.columns
        where table_schema = database()
          and table_name = target_table_name
          and column_name = target_column_name
    ) then
        set @alter_sql = concat('alter table ', target_table_name, ' add column ', target_column_definition);
        prepare alter_statement from @alter_sql;
        execute alter_statement;
        deallocate prepare alter_statement;
    end if;
end$$
delimiter ;

call add_column_if_absent('fin_receivable', 'amount', 'amount decimal(18,2) default 0.00 comment ''应收金额''');
call add_column_if_absent('fin_receivable', 'received_amount', 'received_amount decimal(18,2) default 0.00 comment ''已收金额''');
call add_column_if_absent('fin_payable', 'amount', 'amount decimal(18,2) default 0.00 comment ''应付金额''');
call add_column_if_absent('fin_payable', 'paid_amount', 'paid_amount decimal(18,2) default 0.00 comment ''已付金额''');
call add_column_if_absent('fin_receipt', 'amount', 'amount decimal(18,2) default 0.00 comment ''收款金额''');
call add_column_if_absent('fin_receipt', 'receipt_date', 'receipt_date datetime comment ''收款日期''');
call add_column_if_absent('fin_payment', 'amount', 'amount decimal(18,2) default 0.00 comment ''付款金额''');
call add_column_if_absent('fin_payment', 'payment_date', 'payment_date datetime comment ''付款日期''');

create table if not exists fin_expense (
  expense_id         bigint(20)      not null auto_increment    comment '费用id',
  expense_no         varchar(64)     not null                   comment '费用单号',
  expense_type       varchar(64)     default null               comment '费用类型',
  amount             decimal(18,2)   default 0.00               comment '费用金额',
  expense_date       date            default null               comment '费用日期',
  pay_channel        varchar(32)     default null               comment '支付渠道',
  fund_account_id    bigint(20)      default null               comment '资金账户id',
  status             char(1)         default '0'                comment '状态',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (expense_id),
  unique key uk_fin_expense_no (expense_no)
) engine=innodb comment = '费用单表';

call add_column_if_absent('fin_expense', 'expense_no', 'expense_no varchar(64) not null comment ''费用单号''');
call add_column_if_absent('fin_expense', 'expense_type', 'expense_type varchar(64) default null comment ''费用类型''');
call add_column_if_absent('fin_expense', 'amount', 'amount decimal(18,2) default 0.00 comment ''费用金额''');
call add_column_if_absent('fin_expense', 'expense_date', 'expense_date date default null comment ''费用日期''');
call add_column_if_absent('fin_expense', 'pay_channel', 'pay_channel varchar(32) default null comment ''支付渠道''');
call add_column_if_absent('fin_expense', 'fund_account_id', 'fund_account_id bigint(20) default null comment ''资金账户id''');
call add_column_if_absent('fin_expense', 'status', 'status char(1) default ''0'' comment ''状态''');
call add_column_if_absent('fin_expense', 'create_by', 'create_by varchar(64) default '''' comment ''创建者''');
call add_column_if_absent('fin_expense', 'create_time', 'create_time datetime comment ''创建时间''');
call add_column_if_absent('fin_expense', 'update_by', 'update_by varchar(64) default '''' comment ''更新者''');
call add_column_if_absent('fin_expense', 'update_time', 'update_time datetime comment ''更新时间''');
call add_column_if_absent('fin_expense', 'remark', 'remark varchar(500) default null comment ''备注''');

update sys_config
set config_value = 'false',
    update_by = 'admin',
    update_time = sysdate(),
    remark = '开发阶段默认关闭验证码功能（true开启，false关闭）'
where config_key = 'sys.account.captchaEnabled';

insert into sys_config(config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark)
select '账号自助-验证码开关', 'sys.account.captchaEnabled', 'false', 'Y', 'admin', sysdate(), '', null, '开发阶段默认关闭验证码功能（true开启，false关闭）'
from dual
where not exists (select 1 from sys_config where config_key = 'sys.account.captchaEnabled');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '2470', '业务链路查询', '2000', '40', '#', '', '', 1, 0, 'F', '0', '0', 'business:trace:query', '#', 'admin', sysdate(), '', null, '业务链路查询权限'
from dual
where not exists (select 1 from sys_menu where menu_id = 2470 or perms = 'business:trace:query');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '2471', '商品导入', '2402', '6', '#', '', '', 1, 0, 'F', '0', '0', 'business:product:import', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2471 or perms = 'business:product:import');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '2472', '客户导入', '2400', '6', '#', '', '', 1, 0, 'F', '0', '0', 'business:customer:import', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2472 or perms = 'business:customer:import');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '2473', '供应商导入', '2401', '6', '#', '', '', 1, 0, 'F', '0', '0', 'business:supplier:import', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2473 or perms = 'business:supplier:import');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '2474', '库存导入', '2003', '20', '#', '', '', 1, 0, 'F', '0', '0', 'business:stock:import', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2474 or perms = 'business:stock:import');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2475, '统一待办中心', 2000, 28, 'todoCenter', 'business/todoCenter/index', '', '', 1, 0, 'C', '0', '0', 'business:workbench:query', 'dashboard', 'admin', sysdate(), '', null, '统一待办中心菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = 2475 or path = 'todoCenter');

-- 补齐未注册菜单及缺失按钮权限（2700-2774）
insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2700, '库位管理', 2001, 13, 'location', 'business/location/index', '', '', 1, 0, 'C', '0', '0', 'business:location:list', 'tree', 'admin', sysdate(), '', null, '库位档案菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = 2700 or path = 'location');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2701, '库位查询', 2700, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:location:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2701 or perms = 'business:location:query');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2702, '库位新增', 2700, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:location:add', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2702 or perms = 'business:location:add');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2703, '库位修改', 2700, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:location:edit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2703 or perms = 'business:location:edit');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2704, '库位删除', 2700, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:location:remove', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2704 or perms = 'business:location:remove');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2710, '费用类别', 2200, 17, 'expenseCategory', 'business/expenseCategory/index', '', '', 1, 0, 'C', '0', '0', 'business:expenseCategory:list', 'tree-table', 'admin', sysdate(), '', null, '费用类别菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = 2710 or path = 'expenseCategory');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2711, '费用类别查询', 2710, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:expenseCategory:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2711 or perms = 'business:expenseCategory:query');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2712, '费用类别新增', 2710, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:expenseCategory:add', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2712 or perms = 'business:expenseCategory:add');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2713, '费用类别修改', 2710, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:expenseCategory:edit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2713 or perms = 'business:expenseCategory:edit');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2714, '费用类别删除', 2710, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:expenseCategory:remove', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2714 or perms = 'business:expenseCategory:remove');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2720, '成本账户', 2200, 18, 'costAccount', 'business/costAccount/index', '', '', 1, 0, 'C', '0', '0', 'business:costAccount:list', 'money', 'admin', sysdate(), '', null, '成本账户菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = 2720 or path = 'costAccount');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2721, '成本账户查询', 2720, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:costAccount:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2721 or perms = 'business:costAccount:query');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2722, '成本账户新增', 2720, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:costAccount:add', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2722 or perms = 'business:costAccount:add');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2723, '成本账户修改', 2720, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:costAccount:edit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2723 or perms = 'business:costAccount:edit');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2724, '成本账户删除', 2720, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:costAccount:remove', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2724 or perms = 'business:costAccount:remove');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2730, '成本分层', 2200, 19, 'costLayer', 'business/costLayer/index', '', '', 1, 0, 'C', '0', '0', 'business:costLayer:list', 'list', 'admin', sysdate(), '', null, '成本分层菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = 2730 or path = 'costLayer');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2731, '成本分层查询', 2730, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:costLayer:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2731 or perms = 'business:costLayer:query');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2732, '成本分层新增', 2730, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:costLayer:add', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2732 or perms = 'business:costLayer:add');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2733, '成本分层修改', 2730, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:costLayer:edit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2733 or perms = 'business:costLayer:edit');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2734, '成本分层删除', 2730, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:costLayer:remove', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2734 or perms = 'business:costLayer:remove');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2740, '成本流水', 2200, 20, 'costLog', 'business/costLog/index', '', '', 1, 0, 'C', '0', '0', 'business:costLog:list', 'log', 'admin', sysdate(), '', null, '成本流水菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = 2740 or path = 'costLog');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2741, '成本流水查询', 2740, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:costLog:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2741 or perms = 'business:costLog:query');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2742, '成本流水新增', 2740, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:costLog:add', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2742 or perms = 'business:costLog:add');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2743, '成本流水修改', 2740, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:costLog:edit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2743 or perms = 'business:costLog:edit');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2744, '成本流水删除', 2740, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:costLog:remove', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2744 or perms = 'business:costLog:remove');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2750, '数据备份', 2200, 21, 'dataBackup', 'business/dataBackup/index', '', '', 1, 0, 'C', '0', '0', 'business:dataBackup:list', 'download', 'admin', sysdate(), '', null, '数据备份说明菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = 2750 or path = 'dataBackup');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2760, '盘点审核', 2006, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inventoryCheck:audit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2760 or perms = 'business:inventoryCheck:audit');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2761, '调拨审核', 2007, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:transfer:audit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2761 or perms = 'business:transfer:audit');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2762, '应收审核', 2201, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:receivable:audit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2762 or perms = 'business:receivable:audit');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2763, '应付审核', 2202, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:payable:audit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2763 or perms = 'business:payable:audit');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2764, '出库导入', 2005, 9, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:outbound:import', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2764 or perms = 'business:outbound:import');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2765, '采购退货明细列表', 2410, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:purchaseReturnItem:list', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2765 or perms = 'business:purchaseReturnItem:list');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2766, '采购退货明细查询', 2410, 7, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:purchaseReturnItem:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2766 or perms = 'business:purchaseReturnItem:query');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2767, '采购退货明细新增', 2410, 8, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:purchaseReturnItem:add', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2767 or perms = 'business:purchaseReturnItem:add');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2768, '采购退货明细修改', 2410, 9, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:purchaseReturnItem:edit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2768 or perms = 'business:purchaseReturnItem:edit');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2769, '采购退货明细删除', 2410, 10, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:purchaseReturnItem:remove', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2769 or perms = 'business:purchaseReturnItem:remove');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2770, '销售退货明细列表', 2411, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:saleReturnItem:list', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2770 or perms = 'business:saleReturnItem:list');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2771, '销售退货明细查询', 2411, 7, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:saleReturnItem:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2771 or perms = 'business:saleReturnItem:query');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2772, '销售退货明细新增', 2411, 8, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:saleReturnItem:add', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2772 or perms = 'business:saleReturnItem:add');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2773, '销售退货明细修改', 2411, 9, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:saleReturnItem:edit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2773 or perms = 'business:saleReturnItem:edit');

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select 2774, '销售退货明细删除', 2411, 10, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:saleReturnItem:remove', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = 2774 or perms = 'business:saleReturnItem:remove');

insert ignore into sys_role_menu(role_id, menu_id)
select r.role_id, m.menu_id
from sys_role r
join sys_menu m on m.menu_id in (
  2470, 2471, 2472, 2473, 2474, 2475,
  2700, 2701, 2702, 2703, 2704,
  2710, 2711, 2712, 2713, 2714,
  2720, 2721, 2722, 2723, 2724,
  2730, 2731, 2732, 2733, 2734,
  2740, 2741, 2742, 2743, 2744,
  2750,
  2760, 2761, 2762, 2763, 2764,
  2765, 2766, 2767, 2768, 2769,
  2770, 2771, 2772, 2773, 2774
)
where r.role_id in (1, 2);

drop procedure if exists add_column_if_absent;
