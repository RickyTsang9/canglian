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

insert into sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
select '2475', '统一待办中心', '2000', '28', 'todoCenter', 'business/todoCenter/index', '', 1, 0, 'C', '0', '0', 'business:workbench:query', 'dashboard', 'admin', sysdate(), '', null, '统一待办中心菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = 2475 or path = 'todoCenter');

insert ignore into sys_role_menu(role_id, menu_id)
select 1, menu_id
from sys_menu
where menu_id in (2475);

drop procedure if exists add_column_if_absent;
