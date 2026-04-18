-- 旧库结构补齐补丁
-- 适用于已上线旧库缺少财务扩展表与关键字段的场景

create table if not exists fin_fund_account (
  account_id         bigint(20)      not null auto_increment    comment '账户id',
  account_no         varchar(64)     not null                   comment '账户编号',
  account_name       varchar(200)    not null                   comment '账户名称',
  account_type       varchar(20)     default ''                 comment '账户类型',
  bank_name          varchar(200)    default ''                 comment '开户行',
  bank_account       varchar(64)     default ''                 comment '银行账号',
  currency           varchar(20)     default ''                 comment '币种',
  balance            decimal(18,2)   default 0.00               comment '余额',
  status             char(1)         default '0'                comment '状态（0正常 1停用）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (account_id),
  unique key uk_fin_fund_account_no (account_no)
) engine=innodb comment = '资金账户表';

create table if not exists fin_expense (
  expense_id         bigint(20)      not null auto_increment    comment '费用id',
  expense_no         varchar(64)     not null                   comment '费用单号',
  expense_type       varchar(20)     default ''                 comment '费用类型',
  amount             decimal(18,2)   default 0.00               comment '费用金额',
  expense_date       date                                       comment '费用日期',
  pay_channel        varchar(20)     default ''                 comment '支付方式',
  fund_account_id    bigint(20)      default null               comment '资金账户id',
  status             char(1)         default '0'                comment '状态（0草稿 1已确认）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (expense_id),
  unique key uk_fin_expense_no (expense_no)
) engine=innodb comment = '费用单表';

create table if not exists fin_expense_category (
  category_id        bigint(20)      not null auto_increment    comment '费用类别id',
  parent_id          bigint(20)      default 0                  comment '父类别id',
  ancestors          varchar(500)    default ''                 comment '祖级列表',
  category_name      varchar(200)    not null                   comment '费用类别名称',
  category_code      varchar(100)    default ''                 comment '费用类别编码',
  order_num          int(4)          default 0                  comment '显示顺序',
  status             char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag           char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (category_id)
) engine=innodb comment = '费用类别表';

create table if not exists fin_write_off (
  write_off_id       bigint(20)      not null auto_increment    comment '核销id',
  write_off_no       varchar(64)     not null                   comment '核销单号',
  write_off_type     varchar(20)     default ''                 comment '核销类型',
  receivable_id      bigint(20)      default null               comment '应收id',
  payable_id         bigint(20)      default null               comment '应付id',
  receipt_id         bigint(20)      default null               comment '收款单id',
  payment_id         bigint(20)      default null               comment '付款单id',
  amount             decimal(18,2)   default 0.00               comment '核销金额',
  write_off_date     date                                       comment '核销日期',
  status             char(1)         default '0'                comment '状态（0草稿 1已核销）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (write_off_id),
  unique key uk_fin_write_off_no (write_off_no)
) engine=innodb comment = '核销单表';

create table if not exists fin_bad_debt (
  bad_debt_id        bigint(20)      not null auto_increment    comment '坏账id',
  bad_debt_no        varchar(64)     not null                   comment '坏账单号',
  receivable_id      bigint(20)      not null                   comment '应收id',
  customer_id        bigint(20)      not null                   comment '客户id',
  amount             decimal(18,2)   default 0.00               comment '坏账金额',
  bad_debt_date      date                                       comment '坏账日期',
  reason             varchar(500)    default null               comment '原因',
  status             char(1)         default '0'                comment '状态（0草稿 1已确认）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (bad_debt_id),
  unique key uk_fin_bad_debt_no (bad_debt_no)
) engine=innodb comment = '坏账表';

create table if not exists fin_cost_account (
  cost_account_id    bigint(20)      not null auto_increment    comment '成本账户id',
  product_id         bigint(20)      not null                   comment '商品id',
  warehouse_id       bigint(20)      not null                   comment '仓库id',
  cost_method        varchar(20)     not null                   comment '成本算法',
  total_qty          decimal(18,2)   default 0.00               comment '总数量',
  total_amount       decimal(18,2)   default 0.00               comment '总金额',
  avg_cost           decimal(18,2)   default 0.00               comment '平均成本',
  last_cost_price    decimal(18,2)   default 0.00               comment '最近入库成本单价',
  last_cost_qty      decimal(18,2)   default 0.00               comment '最近入库数量',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (cost_account_id),
  unique key uk_fin_cost_account (product_id, warehouse_id, cost_method)
) engine=innodb comment = '成本账户表';

create table if not exists fin_cost_layer (
  cost_layer_id      bigint(20)      not null auto_increment    comment '成本分层id',
  cost_account_id    bigint(20)      not null                   comment '成本账户id',
  product_id         bigint(20)      not null                   comment '商品id',
  warehouse_id       bigint(20)      not null                   comment '仓库id',
  quantity           decimal(18,2)   default 0.00               comment '入库数量',
  remaining_qty      decimal(18,2)   default 0.00               comment '剩余数量',
  price              decimal(18,2)   default 0.00               comment '单价',
  amount             decimal(18,2)   default 0.00               comment '金额',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (cost_layer_id)
) engine=innodb comment = '成本分层表';

create table if not exists fin_cost_log (
  cost_log_id        bigint(20)      not null auto_increment    comment '成本流水id',
  cost_account_id    bigint(20)      not null                   comment '成本账户id',
  product_id         bigint(20)      not null                   comment '商品id',
  warehouse_id       bigint(20)      not null                   comment '仓库id',
  bill_type          varchar(20)     default ''                 comment '单据类型',
  bill_id            bigint(20)      default null               comment '单据id',
  in_out             char(1)         not null                   comment '出入库标识（1入库 2出库）',
  quantity           decimal(18,2)   default 0.00               comment '数量',
  price              decimal(18,2)   default 0.00               comment '单价',
  amount             decimal(18,2)   default 0.00               comment '金额',
  cost_method        varchar(20)     default ''                 comment '成本算法',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (cost_log_id)
) engine=innodb comment = '成本流水表';

drop procedure if exists add_column_if_absent;
delimiter $$
create procedure add_column_if_absent(
    in target_table_name varchar(64),
    in target_column_name varchar(64),
    in column_definition_text varchar(500)
)
begin
    if not exists (
        select 1
        from information_schema.columns
        where table_schema = database()
          and table_name = target_table_name
          and column_name = target_column_name
    ) then
        set @alter_column_sql = concat('alter table ', target_table_name, ' add column ', column_definition_text);
        prepare alter_column_statement from @alter_column_sql;
        execute alter_column_statement;
        deallocate prepare alter_column_statement;
    end if;
end$$
delimiter ;

drop procedure if exists add_index_if_absent;
delimiter $$
create procedure add_index_if_absent(
    in target_table_name varchar(64),
    in target_index_name varchar(64),
    in index_definition_sql varchar(500)
)
begin
    if not exists (
        select 1
        from information_schema.statistics
        where table_schema = database()
          and table_name = target_table_name
          and index_name = target_index_name
    ) then
        set @alter_index_sql = index_definition_sql;
        prepare alter_index_statement from @alter_index_sql;
        execute alter_index_statement;
        deallocate prepare alter_index_statement;
    end if;
end$$
delimiter ;

call add_column_if_absent('md_warehouse', 'contact_person', 'contact_person varchar(50) default '''' comment ''联系人'' after warehouse_name');
call add_column_if_absent('fin_receivable', 'receivable_no', 'receivable_no varchar(64) default null comment ''应收单号'' after receivable_id');
call add_column_if_absent('fin_receipt', 'receipt_no', 'receipt_no varchar(64) default null comment ''收款单号'' after receipt_id');

update fin_receivable
set receivable_no = concat('ar', date_format(coalesce(due_date, date(create_time), curdate()), '%Y%m%d'), lpad(receivable_id, 3, '0'))
where receivable_no is null
   or receivable_no = '';

update fin_receipt
set receipt_no = concat('rec', date_format(coalesce(receipt_date, date(create_time), curdate()), '%Y%m%d'), lpad(receipt_id, 3, '0'))
where receipt_no is null
   or receipt_no = '';

alter table md_warehouse modify column contact_person varchar(50) default '' comment '联系人';
alter table fin_receivable modify column receivable_no varchar(64) not null comment '应收单号';
alter table fin_receipt modify column receipt_no varchar(64) not null comment '收款单号';

call add_index_if_absent('fin_receivable', 'uk_fin_receivable_no', 'alter table fin_receivable add unique key uk_fin_receivable_no (receivable_no)');
call add_index_if_absent('fin_receipt', 'uk_fin_receipt_no', 'alter table fin_receipt add unique key uk_fin_receipt_no (receipt_no)');

drop procedure if exists add_column_if_absent;
drop procedure if exists add_index_if_absent;
