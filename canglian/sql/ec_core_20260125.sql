drop table if exists md_product;
create table md_product (
  product_id         bigint(20)      not null auto_increment    comment '商品id',
  product_code       varchar(64)     not null                   comment '商品编码',
  product_name       varchar(200)    not null                   comment '商品名称',
  product_spec       varchar(200)    default ''                 comment '规格型号',
  unit_name          varchar(20)     default ''                 comment '单位',
  bar_code           varchar(64)     default ''                 comment '条码',
  category_name      varchar(100)    default ''                 comment '分类',
  brand_name         varchar(100)    default ''                 comment '品牌',
  product_image      varchar(500)    default null               comment '图片',
  cost_price         decimal(18,2)   default 0.00               comment '成本价',
  sale_price         decimal(18,2)   default 0.00               comment '销售价',
  status             char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag           char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (product_id),
  unique key uk_md_product_code (product_code)
) engine=innodb comment = '商品档案表';

drop table if exists md_warehouse;
create table md_warehouse (
  warehouse_id       bigint(20)      not null auto_increment    comment '仓库id',
  warehouse_code     varchar(64)     not null                   comment '仓库编码',
  warehouse_name     varchar(100)    not null                   comment '仓库名称',
  contact_person     varchar(50)     default ''                 comment '联系人',
  contact_phone      varchar(20)     default ''                 comment '联系电话',
  address            varchar(200)    default ''                 comment '地址',
  status             char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag           char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (warehouse_id),
  unique key uk_md_warehouse_code (warehouse_code)
) engine=innodb comment = '仓库档案表';

drop table if exists md_supplier;
create table md_supplier (
  supplier_id        bigint(20)      not null auto_increment    comment '供应商id',
  supplier_code      varchar(64)     not null                   comment '供应商编码',
  supplier_name      varchar(200)    not null                   comment '供应商名称',
  contact_person     varchar(50)     default ''                 comment '联系人',
  contact_phone      varchar(20)     default ''                 comment '联系电话',
  contact_email      varchar(100)    default ''                 comment '邮箱',
  address            varchar(200)    default ''                 comment '地址',
  status             char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag           char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (supplier_id),
  unique key uk_md_supplier_code (supplier_code)
) engine=innodb comment = '供应商档案表';

drop table if exists md_customer;
create table md_customer (
  customer_id        bigint(20)      not null auto_increment    comment '客户id',
  customer_code      varchar(64)     not null                   comment '客户编码',
  customer_name      varchar(200)    not null                   comment '客户名称',
  contact_person     varchar(50)     default ''                 comment '联系人',
  contact_phone      varchar(20)     default ''                 comment '联系电话',
  contact_email      varchar(100)    default ''                 comment '邮箱',
  address            varchar(200)    default ''                 comment '地址',
  status             char(1)         default '0'                comment '状态（0正常 1停用）',
  del_flag           char(1)         default '0'                comment '删除标志（0代表存在 2代表删除）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (customer_id),
  unique key uk_md_customer_code (customer_code)
) engine=innodb comment = '客户档案表';

drop table if exists wms_stock;
create table wms_stock (
  stock_id           bigint(20)      not null auto_increment    comment '库存id',
  warehouse_id       bigint(20)      not null                   comment '仓库id',
  product_id         bigint(20)      not null                   comment '商品id',
  location_id        bigint(20)      not null default 0         comment '库位id',
  batch_no           varchar(64)     not null default ''        comment '批次号',
  quantity           decimal(18,2)   default 0.00               comment '库存数量',
  locked_quantity    decimal(18,2)   default 0.00               comment '锁定数量',
  frozen_quantity    decimal(18,2)   default 0.00               comment '冻结数量',
  warning_min_qty    decimal(18,2)   default 0.00               comment '预警最小库存',
  warning_max_qty    decimal(18,2)   default 0.00               comment '预警最大库存',
  version            bigint(20)      default 0                  comment '版本号',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  primary key (stock_id),
  unique key uk_wms_stock_warehouse_product (warehouse_id, product_id, location_id, batch_no)
) engine=innodb comment = '库存表';

drop table if exists wms_stock_log;
create table wms_stock_log (
  stock_log_id       bigint(20)      not null auto_increment    comment '库存流水id',
  warehouse_id       bigint(20)      not null                   comment '仓库id',
  product_id         bigint(20)      not null                   comment '商品id',
  location_id        bigint(20)      not null default 0         comment '库位id',
  batch_no           varchar(64)     not null default ''        comment '批次号',
  bill_type          varchar(20)     not null                   comment '单据类型',
  bill_id            bigint(20)      not null                   comment '单据id',
  in_out             char(1)         not null                   comment '出入库标识（1入库 2出库 3调整）',
  quantity           decimal(18,2)   default 0.00               comment '数量',
  price              decimal(18,2)   default 0.00               comment '单价',
  amount             decimal(18,2)   default 0.00               comment '金额',
  before_qty         decimal(18,2)   default 0.00               comment '变动前数量',
  after_qty          decimal(18,2)   default 0.00               comment '变动后数量',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  primary key (stock_log_id)
) engine=innodb comment = '库存流水表';

drop table if exists wms_inbound;
create table wms_inbound (
  inbound_id         bigint(20)      not null auto_increment    comment '入库单id',
  inbound_no         varchar(64)     not null                   comment '入库单号',
  inbound_type       varchar(20)     default ''                 comment '入库类型',
  supplier_id        bigint(20)      default null               comment '供应商id',
  warehouse_id       bigint(20)      not null                   comment '仓库id',
  total_qty          decimal(18,2)   default 0.00               comment '总数量',
  total_amount       decimal(18,2)   default 0.00               comment '总金额',
  status             char(1)         default '0'                comment '状态（0草稿 1已审核 2已完成）',
  audit_by           varchar(64)     default ''                 comment '审核人',
  audit_time         datetime                                   comment '审核时间',
  platform_type      varchar(20)     default ''                 comment '平台类型',
  store_id           bigint(20)      default null               comment '店铺id',
  source_order_no    varchar(64)     default ''                 comment '平台订单号',
  carrier            varchar(100)    default ''                 comment '承运商',
  waybill_no         varchar(64)     default ''                 comment '运单号',
  freight_cost       decimal(18,2)   default 0.00               comment '运费成本',
  delivery_status    char(1)         default '0'                comment '配送状态（0待发货 1已发货 2已签收 3退货）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (inbound_id),
  unique key uk_wms_inbound_no (inbound_no)
) engine=innodb comment = '入库单表';

drop table if exists wms_inbound_item;
create table wms_inbound_item (
  inbound_item_id    bigint(20)      not null auto_increment    comment '入库明细id',
  inbound_id         bigint(20)      not null                   comment '入库单id',
  product_id         bigint(20)      not null                   comment '商品id',
  location_id        bigint(20)      not null default 0         comment '库位id',
  batch_no           varchar(64)     not null default ''        comment '批次号',
  quantity           decimal(18,2)   default 0.00               comment '数量',
  price              decimal(18,2)   default 0.00               comment '单价',
  amount             decimal(18,2)   default 0.00               comment '金额',
  primary key (inbound_item_id)
) engine=innodb comment = '入库单明细表';

drop table if exists wms_outbound;
create table wms_outbound (
  outbound_id        bigint(20)      not null auto_increment    comment '出库单id',
  outbound_no        varchar(64)     not null                   comment '出库单号',
  outbound_type      varchar(20)     default ''                 comment '出库类型',
  customer_id        bigint(20)      default null               comment '客户id',
  warehouse_id       bigint(20)      not null                   comment '仓库id',
  total_qty          decimal(18,2)   default 0.00               comment '总数量',
  total_amount       decimal(18,2)   default 0.00               comment '总金额',
  status             char(1)         default '0'                comment '状态（0草稿 1已审核 2已完成）',
  audit_by           varchar(64)     default ''                 comment '审核人',
  audit_time         datetime                                   comment '审核时间',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (outbound_id),
  unique key uk_wms_outbound_no (outbound_no)
) engine=innodb comment = '出库单表';

drop table if exists wms_outbound_item;
create table wms_outbound_item (
  outbound_item_id   bigint(20)      not null auto_increment    comment '出库明细id',
  outbound_id        bigint(20)      not null                   comment '出库单id',
  product_id         bigint(20)      not null                   comment '商品id',
  location_id        bigint(20)      not null default 0         comment '库位id',
  batch_no           varchar(64)     not null default ''        comment '批次号',
  quantity           decimal(18,2)   default 0.00               comment '数量',
  price              decimal(18,2)   default 0.00               comment '单价',
  amount             decimal(18,2)   default 0.00               comment '金额',
  primary key (outbound_item_id)
) engine=innodb comment = '出库单明细表';

drop table if exists wms_purchase_return;
create table wms_purchase_return (
  purchase_return_id bigint(20)      not null auto_increment    comment '采购退货id',
  return_no          varchar(64)     not null                   comment '退货单号',
  return_type        varchar(20)     default ''                 comment '退货类型',
  supplier_id        bigint(20)      default null               comment '供应商id',
  warehouse_id       bigint(20)      not null                   comment '仓库id',
  total_qty          decimal(18,2)   default 0.00               comment '总数量',
  total_amount       decimal(18,2)   default 0.00               comment '总金额',
  status             char(1)         default '0'                comment '状态（0草稿 1已审核）',
  audit_by           varchar(64)     default ''                 comment '审核人',
  audit_time         datetime                                   comment '审核时间',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (purchase_return_id),
  unique key uk_wms_purchase_return_no (return_no)
) engine=innodb comment = '采购退货单表';

drop table if exists wms_purchase_return_item;
create table wms_purchase_return_item (
  purchase_return_item_id bigint(20) not null auto_increment    comment '采购退货明细id',
  purchase_return_id      bigint(20) not null                   comment '采购退货id',
  product_id              bigint(20) not null                   comment '商品id',
  quantity                decimal(18,2) default 0.00            comment '数量',
  price                   decimal(18,2) default 0.00            comment '单价',
  amount                  decimal(18,2) default 0.00            comment '金额',
  primary key (purchase_return_item_id)
) engine=innodb comment = '采购退货明细表';

drop table if exists wms_sale_return;
create table wms_sale_return (
  sale_return_id   bigint(20)      not null auto_increment      comment '销售退货id',
  return_no        varchar(64)     not null                     comment '退货单号',
  return_type      varchar(20)     default ''                   comment '退货类型',
  customer_id      bigint(20)      default null                 comment '客户id',
  warehouse_id     bigint(20)      not null                     comment '仓库id',
  total_qty        decimal(18,2)   default 0.00                 comment '总数量',
  total_amount     decimal(18,2)   default 0.00                 comment '总金额',
  status           char(1)         default '0'                  comment '状态（0草稿 1已审核）',
  audit_by         varchar(64)     default ''                   comment '审核人',
  audit_time       datetime                                     comment '审核时间',
  create_by        varchar(64)     default ''                   comment '创建者',
  create_time      datetime                                     comment '创建时间',
  update_by        varchar(64)     default ''                   comment '更新者',
  update_time      datetime                                     comment '更新时间',
  remark           varchar(500)    default null                 comment '备注',
  primary key (sale_return_id),
  unique key uk_wms_sale_return_no (return_no)
) engine=innodb comment = '销售退货单表';

drop table if exists wms_sale_return_item;
create table wms_sale_return_item (
  sale_return_item_id bigint(20)   not null auto_increment      comment '销售退货明细id',
  sale_return_id      bigint(20)   not null                     comment '销售退货id',
  product_id          bigint(20)   not null                     comment '商品id',
  quantity            decimal(18,2) default 0.00                comment '数量',
  price               decimal(18,2) default 0.00                comment '单价',
  amount              decimal(18,2) default 0.00                comment '金额',
  primary key (sale_return_item_id)
) engine=innodb comment = '销售退货明细表';

drop table if exists wms_inventory_check;
create table wms_inventory_check (
  check_id           bigint(20)      not null auto_increment    comment '盘点单id',
  check_no           varchar(64)     not null                   comment '盘点单号',
  warehouse_id       bigint(20)      not null                   comment '仓库id',
  status             char(1)         default '0'                comment '状态（0草稿 1已审核 2已完成）',
  total_diff_qty     decimal(18,2)   default 0.00               comment '差异数量合计',
  total_diff_amount  decimal(18,2)   default 0.00               comment '差异金额合计',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (check_id),
  unique key uk_wms_inventory_check_no (check_no)
) engine=innodb comment = '盘点单表';

drop table if exists wms_inventory_check_item;
create table wms_inventory_check_item (
  check_item_id      bigint(20)      not null auto_increment    comment '盘点明细id',
  check_id           bigint(20)      not null                   comment '盘点单id',
  product_id         bigint(20)      not null                   comment '商品id',
  location_id        bigint(20)      not null default 0         comment '库位id',
  batch_no           varchar(64)     not null default ''        comment '批次号',
  stock_qty          decimal(18,2)   default 0.00               comment '账面数量',
  actual_qty         decimal(18,2)   default 0.00               comment '实盘数量',
  diff_qty           decimal(18,2)   default 0.00               comment '差异数量',
  price              decimal(18,2)   default 0.00               comment '单价',
  diff_amount        decimal(18,2)   default 0.00               comment '差异金额',
  primary key (check_item_id)
) engine=innodb comment = '盘点单明细表';

drop table if exists wms_transfer;
create table wms_transfer (
  transfer_id        bigint(20)      not null auto_increment    comment '调拨单id',
  transfer_no        varchar(64)     not null                   comment '调拨单号',
  out_warehouse_id   bigint(20)      not null                   comment '调出仓库id',
  in_warehouse_id    bigint(20)      not null                   comment '调入仓库id',
  total_qty          decimal(18,2)   default 0.00               comment '总数量',
  total_amount       decimal(18,2)   default 0.00               comment '总金额',
  status             char(1)         default '0'                comment '状态（0草稿 1已审核 2已完成）',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (transfer_id),
  unique key uk_wms_transfer_no (transfer_no)
) engine=innodb comment = '调拨单表';

drop table if exists wms_transfer_item;
create table wms_transfer_item (
  transfer_item_id   bigint(20)      not null auto_increment    comment '调拨明细id',
  transfer_id        bigint(20)      not null                   comment '调拨单id',
  product_id         bigint(20)      not null                   comment '商品id',
  out_location_id    bigint(20)      not null default 0         comment '调出库位id',
  in_location_id     bigint(20)      not null default 0         comment '调入库位id',
  batch_no           varchar(64)     not null default ''        comment '批次号',
  quantity           decimal(18,2)   default 0.00               comment '数量',
  price              decimal(18,2)   default 0.00               comment '单价',
  amount             decimal(18,2)   default 0.00               comment '金额',
  primary key (transfer_item_id)
) engine=innodb comment = '调拨单明细表';

drop table if exists fin_receivable;
create table fin_receivable (
  receivable_id      bigint(20)      not null auto_increment    comment '应收id',
  receivable_no      varchar(64)     not null                   comment '应收单号',
  customer_id        bigint(20)      not null                   comment '客户id',
  bill_type          varchar(20)     default ''                 comment '单据类型',
  bill_id            bigint(20)      default null               comment '单据id',
  amount             decimal(18,2)   default 0.00               comment '应收金额',
  received_amount    decimal(18,2)   default 0.00               comment '已收金额',
  status             char(1)         default '0'                comment '状态（0未收 1部分 2已收）',
  due_date           date                                       comment '到期日期',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (receivable_id),
  unique key uk_fin_receivable_no (receivable_no)
) engine=innodb comment = '应收单表';

drop table if exists fin_payable;
create table fin_payable (
  payable_id         bigint(20)      not null auto_increment    comment '应付id',
  payable_no         varchar(64)     not null                   comment '应付单号',
  supplier_id        bigint(20)      not null                   comment '供应商id',
  bill_type          varchar(20)     default ''                 comment '单据类型',
  bill_id            bigint(20)      default null               comment '单据id',
  amount             decimal(18,2)   default 0.00               comment '应付金额',
  paid_amount        decimal(18,2)   default 0.00               comment '已付金额',
  status             char(1)         default '0'                comment '状态（0未付 1部分 2已付）',
  due_date           date                                       comment '到期日期',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (payable_id),
  unique key uk_fin_payable_no (payable_no)
) engine=innodb comment = '应付单表';

drop table if exists fin_receipt;
create table fin_receipt (
  receipt_id         bigint(20)      not null auto_increment    comment '收款单id',
  receipt_no         varchar(64)     not null                   comment '收款单号',
  customer_id        bigint(20)      not null                   comment '客户id',
  receivable_id      bigint(20)      default null               comment '应收id',
  amount             decimal(18,2)   default 0.00               comment '收款金额',
  receipt_date       date                                       comment '收款日期',
  pay_channel        varchar(20)     default ''                 comment '收款方式',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (receipt_id),
  unique key uk_fin_receipt_no (receipt_no)
) engine=innodb comment = '收款单表';

drop table if exists fin_payment;
create table fin_payment (
  payment_id         bigint(20)      not null auto_increment    comment '付款单id',
  payment_no         varchar(64)     not null                   comment '付款单号',
  supplier_id        bigint(20)      not null                   comment '供应商id',
  payable_id         bigint(20)      default null               comment '应付id',
  amount             decimal(18,2)   default 0.00               comment '付款金额',
  payment_date       date                                       comment '付款日期',
  pay_channel        varchar(20)     default ''                 comment '付款方式',
  create_by          varchar(64)     default ''                 comment '创建者',
  create_time        datetime                                   comment '创建时间',
  update_by          varchar(64)     default ''                 comment '更新者',
  update_time        datetime                                   comment '更新时间',
  remark             varchar(500)    default null               comment '备注',
  primary key (payment_id),
  unique key uk_fin_payment_no (payment_no)
) engine=innodb comment = '付款单表';

drop table if exists fin_fund_account;
create table fin_fund_account (
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

drop table if exists fin_expense;
create table fin_expense (
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

drop table if exists fin_expense_category;
create table fin_expense_category (
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

drop table if exists fin_write_off;
create table fin_write_off (
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

drop table if exists fin_bad_debt;
create table fin_bad_debt (
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

drop table if exists fin_cost_account;
create table fin_cost_account (
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

drop table if exists fin_cost_layer;
create table fin_cost_layer (
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

drop table if exists fin_cost_log;
create table fin_cost_log (
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

-- ----------------------------
-- 样例数据
-- ----------------------------
insert into md_product (product_id, product_code, product_name, product_spec, unit_name, bar_code, category_name, brand_name, cost_price, sale_price, status, del_flag, create_by, create_time, update_by, update_time, remark)
values
(1, 'p001', '螺丝刀', '6寸', '把', '690000000001', '五金', '工具', 15.00, 28.00, '0', '0', 'admin', sysdate(), 'admin', sysdate(), '样例商品'),
(2, 'p002', '扳手', '8寸', '把', '690000000002', '五金', '工具', 20.00, 35.00, '0', '0', 'admin', sysdate(), 'admin', sysdate(), '样例商品'),
(3, 'p003', '纸箱', '60x40', '个', '690000000003', '包装', '耗材', 2.00, 5.00, '0', '0', 'admin', sysdate(), 'admin', sysdate(), '样例商品');

insert into md_warehouse (warehouse_id, warehouse_code, warehouse_name, contact_person, contact_phone, address, status, del_flag, create_by, create_time, update_by, update_time, remark)
values
(1, 'wh001', '主仓库', '张三', '13800000001', '上海市浦东新区', '0', '0', 'admin', sysdate(), 'admin', sysdate(), '样例仓库'),
(2, 'wh002', '华东仓', '李四', '13800000002', '江苏省苏州市', '0', '0', 'admin', sysdate(), 'admin', sysdate(), '样例仓库');

insert into md_supplier (supplier_id, supplier_code, supplier_name, contact_person, contact_phone, contact_email, address, status, del_flag, create_by, create_time, update_by, update_time, remark)
values
(1, 'sup001', '海川供应商', '王五', '13900000001', 'sup1@example.com', '浙江省宁波市', '0', '0', 'admin', sysdate(), 'admin', sysdate(), '样例供应商'),
(2, 'sup002', '恒星供应商', '赵六', '13900000002', 'sup2@example.com', '安徽省合肥市', '0', '0', 'admin', sysdate(), 'admin', sysdate(), '样例供应商');

insert into md_customer (customer_id, customer_code, customer_name, contact_person, contact_phone, contact_email, address, status, del_flag, create_by, create_time, update_by, update_time, remark)
values
(1, 'cus001', '星火客户', '钱七', '13700000001', 'cus1@example.com', '上海市徐汇区', '0', '0', 'admin', sysdate(), 'admin', sysdate(), '样例客户'),
(2, 'cus002', '晨光客户', '孙八', '13700000002', 'cus2@example.com', '浙江省杭州市', '0', '0', 'admin', sysdate(), 'admin', sysdate(), '样例客户');

insert into wms_stock (stock_id, warehouse_id, product_id, quantity, locked_quantity, warning_min_qty, warning_max_qty, create_by, create_time, update_by, update_time)
values
(1, 1, 1, 100.00, 0.00, 10.00, 500.00, 'admin', sysdate(), 'admin', sysdate()),
(2, 1, 2, 80.00, 0.00, 10.00, 300.00, 'admin', sysdate(), 'admin', sysdate()),
(3, 2, 3, 200.00, 0.00, 50.00, 800.00, 'admin', sysdate(), 'admin', sysdate());

insert into wms_inbound (inbound_id, inbound_no, inbound_type, supplier_id, warehouse_id, total_qty, total_amount, status, audit_by, audit_time, platform_type, store_id, source_order_no, carrier, waybill_no, freight_cost, delivery_status, create_by, create_time, update_by, update_time, remark)
values
(1, 'inb20260101001', 'purchase', 1, 1, 50.00, 750.00, '1', 'admin', sysdate(), '', null, '', '', '', 0.00, '0', 'admin', sysdate(), 'admin', sysdate(), '样例入库单');

insert into wms_inbound_item (inbound_item_id, inbound_id, product_id, quantity, price, amount)
values
(1, 1, 1, 50.00, 15.00, 750.00);

insert into wms_outbound (outbound_id, outbound_no, outbound_type, customer_id, warehouse_id, total_qty, total_amount, status, audit_by, audit_time, create_by, create_time, update_by, update_time, remark)
values
(1, 'out20260101001', 'sale', 1, 1, 20.00, 560.00, '1', 'admin', sysdate(), 'admin', sysdate(), 'admin', sysdate(), '样例出库单');

insert into wms_outbound_item (outbound_item_id, outbound_id, product_id, quantity, price, amount)
values
(1, 1, 1, 20.00, 28.00, 560.00);

insert into wms_inventory_check (check_id, check_no, warehouse_id, status, total_diff_qty, total_diff_amount, create_by, create_time, update_by, update_time, remark)
values
(1, 'chk20260101001', 1, '1', 2.00, 30.00, 'admin', sysdate(), 'admin', sysdate(), '样例盘点单');

insert into wms_inventory_check_item (check_item_id, check_id, product_id, stock_qty, actual_qty, diff_qty, price, diff_amount)
values
(1, 1, 1, 100.00, 102.00, 2.00, 15.00, 30.00);

insert into wms_transfer (transfer_id, transfer_no, out_warehouse_id, in_warehouse_id, total_qty, total_amount, status, create_by, create_time, update_by, update_time, remark)
values
(1, 'trf20260101001', 1, 2, 10.00, 200.00, '1', 'admin', sysdate(), 'admin', sysdate(), '样例调拨单');

insert into wms_transfer_item (transfer_item_id, transfer_id, product_id, quantity, price, amount)
values
(1, 1, 2, 10.00, 20.00, 200.00);

insert into wms_stock_log (stock_log_id, warehouse_id, product_id, bill_type, bill_id, in_out, quantity, price, amount, before_qty, after_qty, create_by, create_time)
values
(1, 1, 1, 'inbound', 1, '1', 50.00, 15.00, 750.00, 50.00, 100.00, 'admin', sysdate()),
(2, 1, 1, 'outbound', 1, '2', 20.00, 28.00, 560.00, 100.00, 80.00, 'admin', sysdate());

insert into fin_receivable (receivable_id, receivable_no, customer_id, bill_type, bill_id, amount, received_amount, status, due_date, create_by, create_time, update_by, update_time, remark)
values
(1, 'ar20260101001', 1, 'outbound', 1, 560.00, 200.00, '1', date_add(curdate(), interval 30 day), 'admin', sysdate(), 'admin', sysdate(), '样例应收');

insert into fin_payable (payable_id, payable_no, supplier_id, bill_type, bill_id, amount, paid_amount, status, due_date, create_by, create_time, update_by, update_time, remark)
values
(1, 'ap20260101001', 1, 'inbound', 1, 750.00, 300.00, '1', date_add(curdate(), interval 15 day), 'admin', sysdate(), 'admin', sysdate(), '样例应付');

insert into fin_receipt (receipt_id, receipt_no, customer_id, receivable_id, amount, receipt_date, pay_channel, create_by, create_time, update_by, update_time, remark)
values
(1, 'rc20260101001', 1, 1, 200.00, curdate(), 'bank', 'admin', sysdate(), 'admin', sysdate(), '样例收款');

insert into fin_payment (payment_id, payment_no, supplier_id, payable_id, amount, payment_date, pay_channel, create_by, create_time, update_by, update_time, remark)
values
(1, 'pm20260101001', 1, 1, 300.00, curdate(), 'bank', 'admin', sysdate(), 'admin', sysdate(), '样例付款');

insert into fin_fund_account (account_id, account_no, account_name, account_type, bank_name, bank_account, currency, balance, status, create_by, create_time, update_by, update_time, remark)
values
(1, 'fa001', '基本户', 'bank', '工商银行', '6222000000000000', 'cny', 50000.00, '0', 'admin', sysdate(), 'admin', sysdate(), '样例资金账户');

insert into fin_expense (expense_id, expense_no, expense_type, amount, expense_date, pay_channel, fund_account_id, status, create_by, create_time, update_by, update_time, remark)
values
(1, 'ex20260101001', 'logistics', 120.00, curdate(), 'bank', 1, '1', 'admin', sysdate(), 'admin', sysdate(), '样例费用');

insert into fin_expense_category (category_id, parent_id, ancestors, category_name, category_code, order_num, status, del_flag, create_by, create_time, update_by, update_time, remark)
values
(1, 0, '0', '运费', 'logistics', 1, '0', '0', 'admin', sysdate(), 'admin', sysdate(), '样例费用类别');

insert into fin_write_off (write_off_id, write_off_no, write_off_type, receivable_id, payable_id, receipt_id, payment_id, amount, write_off_date, status, create_by, create_time, update_by, update_time, remark)
values
(1, 'wo20260101001', 'receivable', 1, null, 1, null, 200.00, curdate(), '1', 'admin', sysdate(), 'admin', sysdate(), '样例核销');

insert into fin_bad_debt (bad_debt_id, bad_debt_no, receivable_id, customer_id, amount, bad_debt_date, reason, status, create_by, create_time, update_by, update_time, remark)
values
(1, 'bd20260101001', 1, 1, 50.00, curdate(), '长期未收', '1', 'admin', sysdate(), 'admin', sysdate(), '样例坏账');

insert into fin_cost_account (cost_account_id, product_id, warehouse_id, cost_method, total_qty, total_amount, avg_cost, last_cost_price, last_cost_qty, create_by, create_time, update_by, update_time, remark)
values
(1, 1, 1, 'fifo', 30.00, 450.00, 15.00, 15.00, 50.00, 'admin', sysdate(), 'admin', sysdate(), '样例成本账户');

insert into fin_cost_layer (cost_layer_id, cost_account_id, product_id, warehouse_id, quantity, remaining_qty, price, amount, create_by, create_time, remark)
values
(1, 1, 1, 1, 50.00, 30.00, 15.00, 750.00, 'admin', sysdate(), '样例成本分层');

insert into fin_cost_log (cost_log_id, cost_account_id, product_id, warehouse_id, bill_type, bill_id, in_out, quantity, price, amount, cost_method, create_by, create_time, remark)
values
(1, 1, 1, 1, 'inbound', 1, '1', 50.00, 15.00, 750.00, 'fifo', 'admin', sysdate(), '样例成本流水');
