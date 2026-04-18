-- 轻量业财一体化升级补丁
-- 目标：对标金蝶精斗云的轻量进销存能力

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

call add_column_if_absent('md_product', 'purchase_unit', 'purchase_unit varchar(20) default '''' comment ''采购单位'' after unit_name');
call add_column_if_absent('md_product', 'sale_unit', 'sale_unit varchar(20) default '''' comment ''销售单位'' after purchase_unit');
call add_column_if_absent('md_product', 'base_unit', 'base_unit varchar(20) default '''' comment ''基础单位'' after sale_unit');
call add_column_if_absent('md_product', 'unit_convert_ratio', 'unit_convert_ratio decimal(18,6) default 1.000000 comment ''单位换算比'' after base_unit');
call add_column_if_absent('md_product', 'enable_batch', 'enable_batch char(1) default ''1'' comment ''启用批次（0否 1是）'' after unit_convert_ratio');
call add_column_if_absent('md_product', 'shelf_life_days', 'shelf_life_days int(11) default 0 comment ''保质期天数'' after enable_batch');
call add_column_if_absent('md_product', 'enable_serial', 'enable_serial char(1) default ''0'' comment ''启用序列号（0否 1是）'' after shelf_life_days');
call add_column_if_absent('md_product', 'warning_min_qty', 'warning_min_qty decimal(18,2) default 0.00 comment ''默认最小库存'' after enable_serial');
call add_column_if_absent('md_product', 'warning_max_qty', 'warning_max_qty decimal(18,2) default 0.00 comment ''默认最大库存'' after warning_min_qty');

call add_column_if_absent('md_customer', 'customer_level', 'customer_level varchar(20) default '''' comment ''客户等级'' after customer_name');
call add_column_if_absent('md_customer', 'credit_limit', 'credit_limit decimal(18,2) default 0.00 comment ''信用额度'' after customer_level');
call add_column_if_absent('md_customer', 'receivable_days', 'receivable_days int(11) default 30 comment ''账期天数'' after credit_limit');
call add_column_if_absent('md_customer', 'price_policy_type', 'price_policy_type varchar(20) default ''default'' comment ''价格策略类型'' after receivable_days');

call add_column_if_absent('md_supplier', 'supplier_level', 'supplier_level varchar(20) default '''' comment ''供应商等级'' after supplier_name');
call add_column_if_absent('md_supplier', 'payable_days', 'payable_days int(11) default 30 comment ''账期天数'' after supplier_level');

call add_column_if_absent('wms_inbound', 'source_bill_type', 'source_bill_type varchar(30) default '''' comment ''来源单据类型'' after warehouse_id');
call add_column_if_absent('wms_inbound', 'source_bill_id', 'source_bill_id bigint(20) default null comment ''来源单据id'' after source_bill_type');
call add_column_if_absent('wms_inbound', 'source_bill_no', 'source_bill_no varchar(64) default '''' comment ''来源单据号'' after source_bill_id');
call add_column_if_absent('wms_inbound', 'business_date', 'business_date date comment ''业务日期'' after source_bill_no');
call add_column_if_absent('wms_inbound', 'biz_status', 'biz_status varchar(20) default ''draft'' comment ''业务状态'' after status');

call add_column_if_absent('wms_outbound', 'source_bill_type', 'source_bill_type varchar(30) default '''' comment ''来源单据类型'' after warehouse_id');
call add_column_if_absent('wms_outbound', 'source_bill_id', 'source_bill_id bigint(20) default null comment ''来源单据id'' after source_bill_type');
call add_column_if_absent('wms_outbound', 'source_bill_no', 'source_bill_no varchar(64) default '''' comment ''来源单据号'' after source_bill_id');
call add_column_if_absent('wms_outbound', 'business_date', 'business_date date comment ''业务日期'' after source_bill_no');
call add_column_if_absent('wms_outbound', 'biz_status', 'biz_status varchar(20) default ''draft'' comment ''业务状态'' after status');

call add_column_if_absent('wms_purchase_return', 'source_bill_type', 'source_bill_type varchar(30) default '''' comment ''来源单据类型'' after warehouse_id');
call add_column_if_absent('wms_purchase_return', 'source_bill_id', 'source_bill_id bigint(20) default null comment ''来源单据id'' after source_bill_type');
call add_column_if_absent('wms_purchase_return', 'source_bill_no', 'source_bill_no varchar(64) default '''' comment ''来源单据号'' after source_bill_id');
call add_column_if_absent('wms_purchase_return', 'business_date', 'business_date date comment ''业务日期'' after source_bill_no');
call add_column_if_absent('wms_purchase_return', 'biz_status', 'biz_status varchar(20) default ''draft'' comment ''业务状态'' after status');

call add_column_if_absent('wms_sale_return', 'source_bill_type', 'source_bill_type varchar(30) default '''' comment ''来源单据类型'' after warehouse_id');
call add_column_if_absent('wms_sale_return', 'source_bill_id', 'source_bill_id bigint(20) default null comment ''来源单据id'' after source_bill_type');
call add_column_if_absent('wms_sale_return', 'source_bill_no', 'source_bill_no varchar(64) default '''' comment ''来源单据号'' after source_bill_id');
call add_column_if_absent('wms_sale_return', 'business_date', 'business_date date comment ''业务日期'' after source_bill_no');
call add_column_if_absent('wms_sale_return', 'biz_status', 'biz_status varchar(20) default ''draft'' comment ''业务状态'' after status');

call add_column_if_absent('fin_receivable', 'source_bill_type', 'source_bill_type varchar(30) default '''' comment ''来源单据类型'' after bill_id');
call add_column_if_absent('fin_receivable', 'source_bill_id', 'source_bill_id bigint(20) default null comment ''来源单据id'' after source_bill_type');
call add_column_if_absent('fin_receivable', 'source_bill_no', 'source_bill_no varchar(64) default '''' comment ''来源单据号'' after source_bill_id');
call add_column_if_absent('fin_receivable', 'business_date', 'business_date date comment ''业务日期'' after source_bill_no');
call add_column_if_absent('fin_receivable', 'biz_status', 'biz_status varchar(20) default ''draft'' comment ''业务状态'' after status');

call add_column_if_absent('fin_payable', 'source_bill_type', 'source_bill_type varchar(30) default '''' comment ''来源单据类型'' after bill_id');
call add_column_if_absent('fin_payable', 'source_bill_id', 'source_bill_id bigint(20) default null comment ''来源单据id'' after source_bill_type');
call add_column_if_absent('fin_payable', 'source_bill_no', 'source_bill_no varchar(64) default '''' comment ''来源单据号'' after source_bill_id');
call add_column_if_absent('fin_payable', 'business_date', 'business_date date comment ''业务日期'' after source_bill_no');
call add_column_if_absent('fin_payable', 'biz_status', 'biz_status varchar(20) default ''draft'' comment ''业务状态'' after status');

update wms_inbound
set business_date = coalesce(business_date, date(create_time)),
    biz_status = case when status = '1' then 'completed' when status = '2' then 'completed' else 'draft' end
where business_date is null
   or biz_status is null
   or biz_status = '';

update wms_outbound
set business_date = coalesce(business_date, date(create_time)),
    biz_status = case
        when delivery_status = '2' then 'completed'
        when delivery_status = '1' then 'executing'
        when delivery_status = '3' then 'closed'
        when status = '1' then 'approved'
        else 'draft'
    end
where business_date is null
   or biz_status is null
   or biz_status = '';

update wms_purchase_return
set business_date = coalesce(business_date, date(create_time)),
    biz_status = case when status = '1' then 'completed' else 'draft' end
where business_date is null
   or biz_status is null
   or biz_status = '';

update wms_sale_return
set business_date = coalesce(business_date, date(create_time)),
    biz_status = case when status = '1' then 'completed' else 'draft' end
where business_date is null
   or biz_status is null
   or biz_status = '';

update fin_receivable
set business_date = coalesce(business_date, due_date, date(create_time)),
    biz_status = case when status = '1' then 'confirmed' else 'draft' end
where business_date is null
   or biz_status is null
   or biz_status = '';

update fin_payable
set business_date = coalesce(business_date, due_date, date(create_time)),
    biz_status = case when status = '1' then 'confirmed' else 'draft' end
where business_date is null
   or biz_status is null
   or biz_status = '';

update md_product
set warning_min_qty = coalesce(warning_min_qty, 0.00),
    warning_max_qty = coalesce(warning_max_qty, 0.00);

create table if not exists sal_order (
  order_id           bigint(20)      not null auto_increment    comment '销售单据id',
  order_no           varchar(64)     not null                   comment '销售单据号',
  order_type         varchar(20)     not null                   comment '单据类型（quote报价 sale销货）',
  customer_id        bigint(20)      default null               comment '客户id',
  warehouse_id       bigint(20)      default null               comment '仓库id',
  source_bill_type   varchar(30)     default ''                 comment '来源单据类型',
  source_bill_id     bigint(20)      default null               comment '来源单据id',
  source_bill_no     varchar(64)     default ''                 comment '来源单据号',
  business_date      date                                       comment '业务日期',
  expected_date      date                                       comment '预计日期',
  total_qty          decimal(18,2)   default 0.00               comment '总数量',
  total_amount       decimal(18,2)   default 0.00               comment '总金额',
  status             char(1)         default '0'                comment '状态（0草稿 1已审批 2已关闭）',
  biz_status         varchar(20)     default 'draft'            comment '业务状态',
  approve_by         varchar(64)     default ''                 comment '审批人',
  approve_time       datetime                                   comment '审批时间',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (order_id),
  unique key uk_sal_order_no (order_no)
) engine=innodb comment = '销售单据表';

create table if not exists sal_order_item (
  order_item_id      bigint(20)      not null auto_increment    comment '销售单据明细id',
  order_id           bigint(20)      not null                   comment '销售单据id',
  product_id         bigint(20)      not null                   comment '商品id',
  quantity           decimal(18,2)   default 0.00               comment '数量',
  price              decimal(18,2)   default 0.00               comment '单价',
  amount             decimal(18,2)   default 0.00               comment '金额',
  remark             varchar(500)    default null               comment '备注',
  primary key (order_item_id)
) engine=innodb comment = '销售单据明细表';

create table if not exists pur_order (
  purchase_order_id  bigint(20)      not null auto_increment    comment '购货订单id',
  purchase_order_no  varchar(64)     not null                   comment '购货订单号',
  supplier_id        bigint(20)      default null               comment '供应商id',
  warehouse_id       bigint(20)      default null               comment '仓库id',
  source_bill_type   varchar(30)     default ''                 comment '来源单据类型',
  source_bill_id     bigint(20)      default null               comment '来源单据id',
  source_bill_no     varchar(64)     default ''                 comment '来源单据号',
  business_date      date                                       comment '业务日期',
  expected_date      date                                       comment '预计日期',
  total_qty          decimal(18,2)   default 0.00               comment '总数量',
  total_amount       decimal(18,2)   default 0.00               comment '总金额',
  status             char(1)         default '0'                comment '状态（0草稿 1已审批 2已关闭）',
  biz_status         varchar(20)     default 'draft'            comment '业务状态',
  approve_by         varchar(64)     default ''                 comment '审批人',
  approve_time       datetime                                   comment '审批时间',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (purchase_order_id),
  unique key uk_pur_order_no (purchase_order_no)
) engine=innodb comment = '购货订单表';

create table if not exists pur_order_item (
  purchase_order_item_id bigint(20)  not null auto_increment    comment '购货订单明细id',
  purchase_order_id      bigint(20)  not null                   comment '购货订单id',
  product_id             bigint(20)  not null                   comment '商品id',
  quantity               decimal(18,2) default 0.00            comment '数量',
  price                  decimal(18,2) default 0.00            comment '单价',
  amount                 decimal(18,2) default 0.00            comment '金额',
  remark                 varchar(500) default null             comment '备注',
  primary key (purchase_order_item_id)
) engine=innodb comment = '购货订单明细表';

create table if not exists fin_invoice (
  invoice_id         bigint(20)      not null auto_increment    comment '发票登记id',
  invoice_no         varchar(64)     not null                   comment '发票登记号',
  invoice_type       varchar(20)     not null                   comment '发票类型（sale销售 purchase采购）',
  customer_id        bigint(20)      default null               comment '客户id',
  supplier_id        bigint(20)      default null               comment '供应商id',
  source_bill_type   varchar(30)     default ''                 comment '来源单据类型',
  source_bill_id     bigint(20)      default null               comment '来源单据id',
  source_bill_no     varchar(64)     default ''                 comment '来源单据号',
  business_date      date                                       comment '业务日期',
  invoice_date       date                                       comment '开票日期',
  invoice_amount     decimal(18,2)   default 0.00               comment '含税金额',
  tax_rate           decimal(10,4)   default 0.0000             comment '税率',
  tax_amount         decimal(18,2)   default 0.00               comment '税额',
  untaxed_amount     decimal(18,2)   default 0.00               comment '不含税金额',
  status             char(1)         default '0'                comment '状态（0草稿 1已确认 2已关闭）',
  biz_status         varchar(20)     default 'draft'            comment '业务状态',
  confirm_by         varchar(64)     default ''                 comment '确认人',
  confirm_time       datetime                                   comment '确认时间',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (invoice_id),
  unique key uk_fin_invoice_no (invoice_no)
) engine=innodb comment = '发票登记表';

create table if not exists price_customer_level (
  level_price_id     bigint(20)      not null auto_increment    comment '等级价格id',
  customer_level     varchar(20)     not null                   comment '客户等级',
  product_id         bigint(20)      not null                   comment '商品id',
  sale_price         decimal(18,2)   default 0.00               comment '销售价',
  status             char(1)         default '0'                comment '状态（0启用 1停用）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (level_price_id),
  unique key uk_price_customer_level (customer_level, product_id)
) engine=innodb comment = '客户等级价格表';

create table if not exists price_customer_special (
  special_price_id   bigint(20)      not null auto_increment    comment '专属价格id',
  customer_id        bigint(20)      not null                   comment '客户id',
  product_id         bigint(20)      not null                   comment '商品id',
  sale_price         decimal(18,2)   default 0.00               comment '销售价',
  status             char(1)         default '0'                comment '状态（0启用 1停用）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (special_price_id),
  unique key uk_price_customer_special (customer_id, product_id)
) engine=innodb comment = '客户专属价格表';

create table if not exists price_recent_transaction (
  recent_price_id    bigint(20)      not null auto_increment    comment '最近成交价格id',
  customer_id        bigint(20)      not null                   comment '客户id',
  product_id         bigint(20)      not null                   comment '商品id',
  sale_price         decimal(18,2)   default 0.00               comment '销售价',
  source_bill_type   varchar(30)     default ''                 comment '来源单据类型',
  source_bill_id     bigint(20)      default null               comment '来源单据id',
  business_date      date                                       comment '业务日期',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (recent_price_id),
  unique key uk_price_recent_transaction (customer_id, product_id)
) engine=innodb comment = '最近成交价格表';

create table if not exists fin_voucher_event (
  voucher_event_id   bigint(20)      not null auto_increment    comment '凭证事件id',
  bill_type          varchar(30)     not null                   comment '单据类型',
  bill_id            bigint(20)      not null                   comment '单据id',
  bill_no            varchar(64)     default ''                 comment '单据号',
  event_type         varchar(30)     not null                   comment '事件类型',
  event_date         date                                       comment '业务日期',
  event_amount       decimal(18,2)   default 0.00               comment '事件金额',
  status             char(1)         default '0'                comment '状态（0待生成 1已生成）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (voucher_event_id)
) engine=innodb comment = '凭证事件表';

insert into sys_menu
select '2260', '报价单', '2000', '30', 'saleQuote', 'business/saleOrder/index', '{"orderType":"quote"}', '', 1, 0, 'C', '0', '0', 'business:saleOrder:list', 'document', 'admin', sysdate(), '', null, '报价单菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = '2260');

insert into sys_menu
select '2261', '销货订单', '2000', '31', 'saleOrder', 'business/saleOrder/index', '{"orderType":"sale"}', '', 1, 0, 'C', '0', '0', 'business:saleOrder:list', 'shopping', 'admin', sysdate(), '', null, '销货订单菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = '2261');

insert into sys_menu
select '2262', '购货订单', '2000', '32', 'purOrder', 'business/purOrder/index', '', '', 1, 0, 'C', '0', '0', 'business:purOrder:list', 'table', 'admin', sysdate(), '', null, '购货订单菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = '2262');

insert into sys_menu
select '2263', '销售发票', '2200', '12', 'salesInvoice', 'business/invoice/index', '{"invoiceType":"sale"}', '', 1, 0, 'C', '0', '0', 'business:invoice:list', 'date', 'admin', sysdate(), '', null, '销售发票菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = '2263');

insert into sys_menu
select '2264', '采购发票', '2200', '13', 'purchaseInvoice', 'business/invoice/index', '{"invoiceType":"purchase"}', '', 1, 0, 'C', '0', '0', 'business:invoice:list', 'date', 'admin', sysdate(), '', null, '采购发票菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = '2264');

insert into sys_menu
select '2265', '客户等级价', '2000', '33', 'customerLevelPrice', 'business/customerLevelPrice/index', '', '', 1, 0, 'C', '0', '0', 'business:customerLevelPrice:list', 'number', 'admin', sysdate(), '', null, '客户等级价菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = '2265');

insert into sys_menu
select '2266', '客户专属价', '2000', '34', 'customerSpecialPrice', 'business/customerSpecialPrice/index', '', '', 1, 0, 'C', '0', '0', 'business:customerSpecialPrice:list', 'number', 'admin', sysdate(), '', null, '客户专属价菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = '2266');

insert into sys_menu
select '2267', '最近成交价', '2000', '35', 'recentTransactionPrice', 'business/recentTransactionPrice/index', '', '', 1, 0, 'C', '0', '0', 'business:recentTransactionPrice:list', 'number', 'admin', sysdate(), '', null, '最近成交价菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = '2267');

insert into sys_menu
select '2268', '补货建议', '2001', '11', 'replenishment', 'business/replenishment/index', '', '', 1, 0, 'C', '0', '0', 'business:replenishment:list', 'edit', 'admin', sysdate(), '', null, '补货建议菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = '2268');

insert into sys_menu
select '2269', '凭证事件', '2200', '14', 'voucherEvent', 'business/voucherEvent/index', '', '', 1, 0, 'C', '0', '0', 'business:voucherEvent:list', 'form', 'admin', sysdate(), '', null, '凭证事件菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = '2269');

insert into sys_menu
select '2270', '业务工作台', '2000', '29', 'workbench', 'business/workbench/index', '', '', 1, 0, 'C', '0', '0', 'business:workbench:list', 'dashboard', 'admin', sysdate(), '', null, '业务工作台菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = '2270');

insert into sys_menu
select '2271', '扫码开单', '2001', '12', 'scan', 'business/scan/index', '', '', 1, 0, 'C', '0', '0', 'business:scan:list', 'guide', 'admin', sysdate(), '', null, '扫码开单菜单'
from dual
where not exists (select 1 from sys_menu where menu_id = '2271');

insert into sys_menu
select '2310', '销售查询', '2261', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:saleOrder:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2310');

insert into sys_menu
select '2311', '销售新增', '2261', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:saleOrder:add', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2311');

insert into sys_menu
select '2312', '销售修改', '2261', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:saleOrder:edit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2312');

insert into sys_menu
select '2313', '销售删除', '2261', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:saleOrder:remove', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2313');

insert into sys_menu
select '2314', '销售审批', '2261', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:saleOrder:approve', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2314');

insert into sys_menu
select '2315', '下推出库', '2261', '6', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:saleOrder:pushOutbound', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2315');

insert into sys_menu
select '2316', '单据打印', '2261', '7', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:saleOrder:print', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2316');

insert into sys_menu
select '2317', '报价下推', '2260', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:saleOrder:pushSale', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2317');

insert into sys_menu
select '2320', '购货查询', '2262', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:purOrder:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2320');

insert into sys_menu
select '2321', '购货新增', '2262', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:purOrder:add', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2321');

insert into sys_menu
select '2322', '购货修改', '2262', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:purOrder:edit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2322');

insert into sys_menu
select '2323', '购货删除', '2262', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:purOrder:remove', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2323');

insert into sys_menu
select '2324', '购货审批', '2262', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:purOrder:approve', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2324');

insert into sys_menu
select '2325', '下推入库', '2262', '6', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:purOrder:pushInbound', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2325');

insert into sys_menu
select '2326', '单据打印', '2262', '7', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:purOrder:print', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2326');

insert into sys_menu
select '2330', '发票查询', '2263', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:invoice:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2330');

insert into sys_menu
select '2331', '发票新增', '2263', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:invoice:add', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2331');

insert into sys_menu
select '2332', '发票修改', '2263', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:invoice:edit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2332');

insert into sys_menu
select '2333', '发票删除', '2263', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:invoice:remove', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2333');

insert into sys_menu
select '2334', '发票确认', '2263', '5', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:invoice:confirm', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2334');

insert into sys_menu
select '2340', '等级价查询', '2265', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:customerLevelPrice:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2340');

insert into sys_menu
select '2341', '等级价新增', '2265', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:customerLevelPrice:add', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2341');

insert into sys_menu
select '2342', '等级价修改', '2265', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:customerLevelPrice:edit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2342');

insert into sys_menu
select '2343', '等级价删除', '2265', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:customerLevelPrice:remove', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2343');

insert into sys_menu
select '2350', '专属价查询', '2266', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:customerSpecialPrice:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2350');

insert into sys_menu
select '2351', '专属价新增', '2266', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:customerSpecialPrice:add', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2351');

insert into sys_menu
select '2352', '专属价修改', '2266', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:customerSpecialPrice:edit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2352');

insert into sys_menu
select '2353', '专属价删除', '2266', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:customerSpecialPrice:remove', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2353');

insert into sys_menu
select '2360', '最近价查询', '2267', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:recentTransactionPrice:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2360');

insert into sys_menu
select '2361', '最近价新增', '2267', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:recentTransactionPrice:add', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2361');

insert into sys_menu
select '2362', '最近价修改', '2267', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:recentTransactionPrice:edit', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2362');

insert into sys_menu
select '2363', '最近价删除', '2267', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:recentTransactionPrice:remove', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2363');

insert into sys_menu
select '2370', '补货查询', '2268', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:replenishment:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2370');

insert into sys_menu
select '2380', '工作台查询', '2270', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:workbench:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2380');

insert into sys_menu
select '2390', '扫码查询', '2271', '1', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:scan:query', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2390');

insert into sys_menu
select '2391', '扫码开单', '2271', '2', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:scan:add', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2391');

insert into sys_menu
select '2392', '扫码盘点', '2271', '3', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:scan:inventory', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2392');

insert into sys_menu
select '2393', '扫码调拨', '2271', '4', '#', '', '', '', 1, 0, 'F', '0', '0', 'business:scan:transfer', '#', 'admin', sysdate(), '', null, ''
from dual
where not exists (select 1 from sys_menu where menu_id = '2393');

insert ignore into sys_role_menu (role_id, menu_id)
select 1, menu_id
from sys_menu
where menu_id in (
  2260, 2261, 2262, 2263, 2264, 2265, 2266, 2267, 2268, 2269, 2270, 2271,
  2310, 2311, 2312, 2313, 2314, 2315, 2316, 2317,
  2320, 2321, 2322, 2323, 2324, 2325, 2326,
  2330, 2331, 2332, 2333, 2334,
  2340, 2341, 2342, 2343,
  2350, 2351, 2352, 2353,
  2360, 2370, 2380, 2390, 2391, 2392, 2393
);

drop procedure if exists add_column_if_absent;

