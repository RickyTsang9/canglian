-- 金蝶云星辰轻量对标升级脚本
-- 适用范围：凭证生成/回写/冲销、基础总账接口、经营分析报表入口

drop procedure if exists add_column_if_absent;
delimiter //
create procedure add_column_if_absent(
    in table_name_value varchar(64),
    in column_name_value varchar(64),
    in column_definition_value text
)
begin
    if not exists (
        select 1
        from information_schema.columns
        where table_schema = database()
          and table_name = table_name_value
          and column_name = column_name_value
    ) then
        set @sql_text = concat('alter table ', table_name_value, ' add column ', column_name_value, ' ', column_definition_value);
        prepare statement_text from @sql_text;
        execute statement_text;
        deallocate prepare statement_text;
    end if;
end//
delimiter ;

call add_column_if_absent('fin_voucher_event', 'voucher_no', "varchar(64) null comment '凭证号'");
call add_column_if_absent('fin_voucher_event', 'voucher_status', "varchar(32) null comment '凭证状态'");
call add_column_if_absent('fin_voucher_event', 'voucher_date', "datetime null comment '凭证日期'");
call add_column_if_absent('fin_voucher_event', 'generated_by', "varchar(64) null comment '生成凭证人'");
call add_column_if_absent('fin_voucher_event', 'generated_time', "datetime null comment '生成凭证时间'");
call add_column_if_absent('fin_voucher_event', 'writeback_status', "varchar(32) null comment '回写状态'");
call add_column_if_absent('fin_voucher_event', 'writeback_message', "varchar(500) null comment '回写消息'");
call add_column_if_absent('fin_voucher_event', 'reverse_voucher_no', "varchar(64) null comment '冲销凭证号'");
call add_column_if_absent('fin_voucher_event', 'reversed_by', "varchar(64) null comment '冲销人'");
call add_column_if_absent('fin_voucher_event', 'reversed_time', "datetime null comment '冲销时间'");

create table if not exists fin_account_subject (
  subject_id bigint(20) not null auto_increment comment '科目id',
  subject_code varchar(64) not null comment '科目编码',
  subject_name varchar(128) not null comment '科目名称',
  subject_type varchar(32) not null comment '科目类型',
  balance_direction varchar(16) not null comment '余额方向',
  status char(1) default '0' comment '状态',
  create_by varchar(64) default '' comment '创建者',
  create_time datetime default null comment '创建时间',
  update_by varchar(64) default '' comment '更新者',
  update_time datetime default null comment '更新时间',
  remark varchar(500) default null comment '备注',
  primary key (subject_id),
  unique key uk_fin_account_subject_code (subject_code)
) engine=innodb auto_increment=1 default charset=utf8mb4 comment='会计科目';

create table if not exists fin_auxiliary_item (
  auxiliary_id bigint(20) not null auto_increment comment '辅助核算id',
  auxiliary_type varchar(32) not null comment '辅助核算类型',
  item_code varchar(64) not null comment '项目编码',
  item_name varchar(128) not null comment '项目名称',
  relation_id bigint(20) default null comment '关联业务id',
  status char(1) default '0' comment '状态',
  create_by varchar(64) default '' comment '创建者',
  create_time datetime default null comment '创建时间',
  update_by varchar(64) default '' comment '更新者',
  update_time datetime default null comment '更新时间',
  remark varchar(500) default null comment '备注',
  primary key (auxiliary_id),
  unique key uk_fin_auxiliary_item (auxiliary_type, item_code)
) engine=innodb auto_increment=1 default charset=utf8mb4 comment='辅助核算项目';

create table if not exists fin_fiscal_period (
  period_id bigint(20) not null auto_increment comment '会计期间id',
  period_code varchar(20) not null comment '期间编码',
  start_date date not null comment '开始日期',
  end_date date not null comment '结束日期',
  close_status varchar(20) default 'open' comment '结账状态',
  close_by varchar(64) default null comment '结账人',
  close_time datetime default null comment '结账时间',
  create_by varchar(64) default '' comment '创建者',
  create_time datetime default null comment '创建时间',
  update_by varchar(64) default '' comment '更新者',
  update_time datetime default null comment '更新时间',
  remark varchar(500) default null comment '备注',
  primary key (period_id),
  unique key uk_fin_fiscal_period_code (period_code)
) engine=innodb auto_increment=1 default charset=utf8mb4 comment='会计期间';

insert into fin_account_subject(subject_code, subject_name, subject_type, balance_direction, status, create_by, create_time, remark)
select '1001', '库存现金', 'asset', 'debit', '0', 'admin', sysdate(), '星辰轻量总账预置科目'
from dual where not exists (select 1 from fin_account_subject where subject_code = '1001');

insert into fin_account_subject(subject_code, subject_name, subject_type, balance_direction, status, create_by, create_time, remark)
select '1002', '银行存款', 'asset', 'debit', '0', 'admin', sysdate(), '星辰轻量总账预置科目'
from dual where not exists (select 1 from fin_account_subject where subject_code = '1002');

insert into fin_account_subject(subject_code, subject_name, subject_type, balance_direction, status, create_by, create_time, remark)
select '1122', '应收账款', 'asset', 'debit', '0', 'admin', sysdate(), '星辰轻量总账预置科目'
from dual where not exists (select 1 from fin_account_subject where subject_code = '1122');

insert into fin_account_subject(subject_code, subject_name, subject_type, balance_direction, status, create_by, create_time, remark)
select '2202', '应付账款', 'liability', 'credit', '0', 'admin', sysdate(), '星辰轻量总账预置科目'
from dual where not exists (select 1 from fin_account_subject where subject_code = '2202');

insert into fin_account_subject(subject_code, subject_name, subject_type, balance_direction, status, create_by, create_time, remark)
select '6001', '主营业务收入', 'income', 'credit', '0', 'admin', sysdate(), '星辰轻量总账预置科目'
from dual where not exists (select 1 from fin_account_subject where subject_code = '6001');

insert into fin_account_subject(subject_code, subject_name, subject_type, balance_direction, status, create_by, create_time, remark)
select '6401', '主营业务成本', 'cost', 'debit', '0', 'admin', sysdate(), '星辰轻量总账预置科目'
from dual where not exists (select 1 from fin_account_subject where subject_code = '6401');

insert into fin_auxiliary_item(auxiliary_type, item_code, item_name, relation_id, status, create_by, create_time, remark)
select 'customer', customer_code, customer_name, customer_id, status, 'admin', sysdate(), '客户辅助核算'
from md_customer c
where not exists (
  select 1 from fin_auxiliary_item a where a.auxiliary_type = 'customer' and a.item_code = c.customer_code
);

insert into fin_auxiliary_item(auxiliary_type, item_code, item_name, relation_id, status, create_by, create_time, remark)
select 'supplier', supplier_code, supplier_name, supplier_id, status, 'admin', sysdate(), '供应商辅助核算'
from md_supplier s
where not exists (
  select 1 from fin_auxiliary_item a where a.auxiliary_type = 'supplier' and a.item_code = s.supplier_code
);

insert into fin_auxiliary_item(auxiliary_type, item_code, item_name, relation_id, status, create_by, create_time, remark)
select 'product', product_code, product_name, product_id, status, 'admin', sysdate(), '商品辅助核算'
from md_product p
where not exists (
  select 1 from fin_auxiliary_item a where a.auxiliary_type = 'product' and a.item_code = p.product_code
);

insert into fin_fiscal_period(period_code, start_date, end_date, close_status, create_by, create_time, remark)
select '2026-01', '2026-01-01', '2026-01-31', 'open', 'admin', sysdate(), '星辰轻量总账预置期间'
from dual where not exists (select 1 from fin_fiscal_period where period_code = '2026-01');

insert into fin_fiscal_period(period_code, start_date, end_date, close_status, create_by, create_time, remark)
select '2026-02', '2026-02-01', '2026-02-28', 'open', 'admin', sysdate(), '星辰轻量总账预置期间'
from dual where not exists (select 1 from fin_fiscal_period where period_code = '2026-02');

insert into fin_fiscal_period(period_code, start_date, end_date, close_status, create_by, create_time, remark)
select '2026-03', '2026-03-01', '2026-03-31', 'open', 'admin', sysdate(), '星辰轻量总账预置期间'
from dual where not exists (select 1 from fin_fiscal_period where period_code = '2026-03');

insert into fin_fiscal_period(period_code, start_date, end_date, close_status, create_by, create_time, remark)
select '2026-04', '2026-04-01', '2026-04-30', 'open', 'admin', sysdate(), '星辰轻量总账预置期间'
from dual where not exists (select 1 from fin_fiscal_period where period_code = '2026-04');

insert into sys_menu
select '2400', '基础总账接口', '2200', '15', 'generalLedger', 'business/generalLedger/index', '', '', 1, 0, 'C', '0', '0', 'business:generalLedger:query', 'form', 'admin', sysdate(), '', null, '基础总账接口菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = '2400');

insert into sys_menu
select '2401', '经营分析报表', '2200', '16', 'operationReport', 'business/operationReport/index', '', '', 1, 0, 'C', '0', '0', 'business:report:query', 'chart', 'admin', sysdate(), '', null, '经营分析报表菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = '2401');

insert into sys_menu
select '2410', '凭证生成', '2269', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:voucherEvent:generate', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2410');

insert into sys_menu
select '2411', '凭证回写', '2269', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:voucherEvent:writeback', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2411');

insert into sys_menu
select '2412', '凭证冲销', '2269', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:voucherEvent:reverse', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2412');

insert into sys_menu
select '2420', '总账查询', '2400', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:generalLedger:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2420');

insert into sys_menu
select '2421', '期间结账', '2400', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:generalLedger:close', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2421');

insert into sys_menu
select '2422', '期间反结账', '2400', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:generalLedger:reopen', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2422');

insert ignore into sys_role_menu (role_id, menu_id)
select 1, menu_id
from sys_menu
where menu_id in (2400, 2401, 2410, 2411, 2412, 2420, 2421, 2422);

drop procedure if exists add_column_if_absent;
