-- =====================================================================
-- Canglian ERP Phase 5 & Phase 6 Complete Upgrade Script
-- Covers:
--   P5-1: Order partial push / unapprove / close / tax & discount
--   P5-2: WMS unaudit & one-click reversal (6 document types)
--   P5-3: Fund log ledger, internal transfer, receipt/payment confirm flow
--   P5-4: Bad debt recovery, red invoice & partial invoicing, unconfirm
--   P5-5: General ledger CRUD, voucher templates & double-entry voucher entries
--   P5-6: Role templates, dept/owner data scope, role-warehouse isolation
--   P6-1~5: Multi-unit conversion, batch expiry FEFO, serial number SN,
--           sale order stock lock & freeze/unfreeze, print company settings
-- =====================================================================

DROP PROCEDURE IF EXISTS sp_add_col_if_missing;
DELIMITER //
CREATE PROCEDURE sp_add_col_if_missing(
    IN p_table VARCHAR(64),
    IN p_col VARCHAR(64),
    IN p_def VARCHAR(512)
)
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = p_table AND COLUMN_NAME = p_col
    ) THEN
        SET @ddl = CONCAT('ALTER TABLE `', p_table, '` ADD COLUMN `', p_col, '` ', p_def);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END //
DELIMITER ;

-- ---------- P5-1: Purchase & Sale Order partial push, discount, tax, close ----------
CALL sp_add_col_if_missing('pur_order', 'discount_amount', "decimal(18,2) DEFAULT 0.00 COMMENT '整单优惠金额'");
CALL sp_add_col_if_missing('pur_order', 'tax_total_amount', "decimal(18,2) DEFAULT 0.00 COMMENT '税额合计'");
CALL sp_add_col_if_missing('pur_order', 'close_reason', "varchar(255) DEFAULT NULL COMMENT '强制关闭原因'");
CALL sp_add_col_if_missing('pur_order_item', 'received_qty', "decimal(18,4) DEFAULT 0.0000 COMMENT '已下推/已入库数量'");
CALL sp_add_col_if_missing('pur_order_item', 'discount_rate', "decimal(5,2) DEFAULT 100.00 COMMENT '行折扣率(%)'");
CALL sp_add_col_if_missing('pur_order_item', 'tax_rate', "decimal(5,2) DEFAULT 0.00 COMMENT '税率(%)'");
CALL sp_add_col_if_missing('pur_order_item', 'tax_amount', "decimal(18,2) DEFAULT 0.00 COMMENT '行税额'");

CALL sp_add_col_if_missing('sal_order', 'discount_amount', "decimal(18,2) DEFAULT 0.00 COMMENT '整单优惠金额'");
CALL sp_add_col_if_missing('sal_order', 'tax_total_amount', "decimal(18,2) DEFAULT 0.00 COMMENT '税额合计'");
CALL sp_add_col_if_missing('sal_order', 'close_reason', "varchar(255) DEFAULT NULL COMMENT '强制关闭原因'");
CALL sp_add_col_if_missing('sal_order_item', 'shipped_qty', "decimal(18,4) DEFAULT 0.0000 COMMENT '已下推/已出库数量'");
CALL sp_add_col_if_missing('sal_order_item', 'discount_rate', "decimal(5,2) DEFAULT 100.00 COMMENT '行折扣率(%)'");
CALL sp_add_col_if_missing('sal_order_item', 'tax_rate', "decimal(5,2) DEFAULT 0.00 COMMENT '税率(%)'");
CALL sp_add_col_if_missing('sal_order_item', 'tax_amount', "decimal(18,2) DEFAULT 0.00 COMMENT '行税额'");

CALL sp_add_col_if_missing('wms_inbound_item', 'source_item_id', "bigint DEFAULT NULL COMMENT '来源采购订单明细ID'");
CALL sp_add_col_if_missing('wms_outbound_item', 'source_item_id', "bigint DEFAULT NULL COMMENT '来源销货订单明细ID'");

-- ---------- P5-2: WMS Unaudit & One-Click Reversal ----------
CALL sp_add_col_if_missing('wms_inbound', 'is_reversal', "char(1) DEFAULT '0' COMMENT '是否红冲单(0否 1是)'");
CALL sp_add_col_if_missing('wms_inbound', 'origin_bill_id', "bigint DEFAULT NULL COMMENT '被红冲原单ID'");
CALL sp_add_col_if_missing('wms_inbound', 'reversed_flag', "char(1) DEFAULT '0' COMMENT '是否已被红冲(0否 1是)'");
CALL sp_add_col_if_missing('wms_inbound', 'unaudit_by', "varchar(64) DEFAULT NULL COMMENT '反审核人'");
CALL sp_add_col_if_missing('wms_inbound', 'unaudit_time', "datetime DEFAULT NULL COMMENT '反审核时间'");

CALL sp_add_col_if_missing('wms_outbound', 'is_reversal', "char(1) DEFAULT '0' COMMENT '是否红冲单(0否 1是)'");
CALL sp_add_col_if_missing('wms_outbound', 'origin_bill_id', "bigint DEFAULT NULL COMMENT '被红冲原单ID'");
CALL sp_add_col_if_missing('wms_outbound', 'reversed_flag', "char(1) DEFAULT '0' COMMENT '是否已被红冲(0否 1是)'");
CALL sp_add_col_if_missing('wms_outbound', 'unaudit_by', "varchar(64) DEFAULT NULL COMMENT '反审核人'");
CALL sp_add_col_if_missing('wms_outbound', 'unaudit_time', "datetime DEFAULT NULL COMMENT '反审核时间'");

CALL sp_add_col_if_missing('wms_transfer', 'is_reversal', "char(1) DEFAULT '0' COMMENT '是否红冲单(0否 1是)'");
CALL sp_add_col_if_missing('wms_transfer', 'origin_bill_id', "bigint DEFAULT NULL COMMENT '被红冲原单ID'");
CALL sp_add_col_if_missing('wms_transfer', 'reversed_flag', "char(1) DEFAULT '0' COMMENT '是否已被红冲(0否 1是)'");
CALL sp_add_col_if_missing('wms_transfer', 'unaudit_by', "varchar(64) DEFAULT NULL COMMENT '反审核人'");
CALL sp_add_col_if_missing('wms_transfer', 'unaudit_time', "datetime DEFAULT NULL COMMENT '反审核时间'");

CALL sp_add_col_if_missing('wms_inventory_check', 'is_reversal', "char(1) DEFAULT '0' COMMENT '是否红冲单(0否 1是)'");
CALL sp_add_col_if_missing('wms_inventory_check', 'origin_bill_id', "bigint DEFAULT NULL COMMENT '被红冲原单ID'");
CALL sp_add_col_if_missing('wms_inventory_check', 'reversed_flag', "char(1) DEFAULT '0' COMMENT '是否已被红冲(0否 1是)'");
CALL sp_add_col_if_missing('wms_inventory_check', 'unaudit_by', "varchar(64) DEFAULT NULL COMMENT '反审核人'");
CALL sp_add_col_if_missing('wms_inventory_check', 'unaudit_time', "datetime DEFAULT NULL COMMENT '反审核时间'");

CALL sp_add_col_if_missing('wms_purchase_return', 'is_reversal', "char(1) DEFAULT '0' COMMENT '是否红冲单(0否 1是)'");
CALL sp_add_col_if_missing('wms_purchase_return', 'origin_bill_id', "bigint DEFAULT NULL COMMENT '被红冲原单ID'");
CALL sp_add_col_if_missing('wms_purchase_return', 'reversed_flag', "char(1) DEFAULT '0' COMMENT '是否已被红冲(0否 1是)'");
CALL sp_add_col_if_missing('wms_purchase_return', 'unaudit_by', "varchar(64) DEFAULT NULL COMMENT '反审核人'");
CALL sp_add_col_if_missing('wms_purchase_return', 'unaudit_time', "datetime DEFAULT NULL COMMENT '反审核时间'");

CALL sp_add_col_if_missing('wms_sale_return', 'is_reversal', "char(1) DEFAULT '0' COMMENT '是否红冲单(0否 1是)'");
CALL sp_add_col_if_missing('wms_sale_return', 'origin_bill_id', "bigint DEFAULT NULL COMMENT '被红冲原单ID'");
CALL sp_add_col_if_missing('wms_sale_return', 'reversed_flag', "char(1) DEFAULT '0' COMMENT '是否已被红冲(0否 1是)'");
CALL sp_add_col_if_missing('wms_sale_return', 'unaudit_by', "varchar(64) DEFAULT NULL COMMENT '反审核人'");
CALL sp_add_col_if_missing('wms_sale_return', 'unaudit_time', "datetime DEFAULT NULL COMMENT '反审核时间'");

-- ---------- P5-3: Fund Log Ledger, Internal Transfer, Receipt/Payment Audit State ----------
CREATE TABLE IF NOT EXISTS fin_fund_log (
    fund_log_id bigint NOT NULL AUTO_INCREMENT COMMENT '资金流水ID',
    account_id bigint NOT NULL COMMENT '资金账户ID',
    biz_type varchar(32) NOT NULL COMMENT '业务类型(receipt/payment/expense/transfer_in/transfer_out/refund/bad_debt_recover/unconfirm)',
    bill_id bigint DEFAULT NULL COMMENT '来源单据ID',
    bill_no varchar(64) DEFAULT NULL COMMENT '来源单据号',
    direction char(1) NOT NULL COMMENT '收支方向(1收入 2支出)',
    change_amount decimal(18,2) NOT NULL COMMENT '变动金额(绝对值)',
    before_balance decimal(18,2) NOT NULL COMMENT '变动前余额',
    after_balance decimal(18,2) NOT NULL COMMENT '变动后余额',
    biz_date date DEFAULT NULL COMMENT '业务日期',
    counterparty_name varchar(128) DEFAULT NULL COMMENT '往来对象/说明',
    remark varchar(500) DEFAULT NULL COMMENT '备注',
    create_by varchar(64) DEFAULT '' COMMENT '操作人',
    create_time datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (fund_log_id),
    KEY idx_fund_log_account (account_id, create_time),
    KEY idx_fund_log_bill (biz_type, bill_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金账户逐笔流水台账';

CREATE TABLE IF NOT EXISTS fin_fund_transfer (
    transfer_id bigint NOT NULL AUTO_INCREMENT COMMENT '互转单ID',
    transfer_no varchar(64) NOT NULL COMMENT '互转单号',
    out_account_id bigint NOT NULL COMMENT '转出账户ID',
    in_account_id bigint NOT NULL COMMENT '转入账户ID',
    amount decimal(18,2) NOT NULL COMMENT '转账金额',
    fee_amount decimal(18,2) DEFAULT 0.00 COMMENT '手续费',
    transfer_date date DEFAULT NULL COMMENT '转账日期',
    status char(1) DEFAULT '0' COMMENT '状态(0草稿 1已确认)',
    audit_by varchar(64) DEFAULT NULL COMMENT '确认人',
    audit_time datetime DEFAULT NULL COMMENT '确认时间',
    remark varchar(500) DEFAULT NULL COMMENT '备注',
    create_by varchar(64) DEFAULT '' COMMENT '创建者',
    create_time datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by varchar(64) DEFAULT '' COMMENT '更新者',
    update_time datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (transfer_id),
    UNIQUE KEY uk_fund_transfer_no (transfer_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金账户内部互转单';

CALL sp_add_col_if_missing('fin_receipt', 'status', "char(1) DEFAULT '1' COMMENT '审核状态(0草稿 1已确认)'");
CALL sp_add_col_if_missing('fin_receipt', 'biz_status', "varchar(32) DEFAULT 'confirmed' COMMENT '业务状态(draft/confirmed)'");
CALL sp_add_col_if_missing('fin_receipt', 'audit_by', "varchar(64) DEFAULT NULL COMMENT '审核人'");
CALL sp_add_col_if_missing('fin_receipt', 'audit_time', "datetime DEFAULT NULL COMMENT '审核时间'");

CALL sp_add_col_if_missing('fin_payment', 'status', "char(1) DEFAULT '1' COMMENT '审核状态(0草稿 1已确认)'");
CALL sp_add_col_if_missing('fin_payment', 'biz_status', "varchar(32) DEFAULT 'confirmed' COMMENT '业务状态(draft/confirmed)'");
CALL sp_add_col_if_missing('fin_payment', 'audit_by', "varchar(64) DEFAULT NULL COMMENT '审核人'");
CALL sp_add_col_if_missing('fin_payment', 'audit_time', "datetime DEFAULT NULL COMMENT '审核时间'");

-- ---------- P5-4: Red Invoice & Bad Debt Recovery ----------
CALL sp_add_col_if_missing('fin_invoice', 'invoice_kind', "varchar(16) DEFAULT 'blue' COMMENT '发票红蓝类型(blue蓝票 red红票)'");
CALL sp_add_col_if_missing('fin_invoice', 'origin_invoice_id', "bigint DEFAULT NULL COMMENT '被红冲原蓝票ID'");
CALL sp_add_col_if_missing('fin_bad_debt', 'recovered_amount', "decimal(18,2) DEFAULT 0.00 COMMENT '已收回坏账金额'");
CALL sp_add_col_if_missing('fin_bad_debt', 'recover_time', "datetime DEFAULT NULL COMMENT '最近收回时间'");
CALL sp_add_col_if_missing('fin_bad_debt', 'recover_account_id', "bigint DEFAULT NULL COMMENT '收回入账资金账户ID'");

-- ---------- P5-5: Double-Entry Voucher Templates & Entries ----------
CREATE TABLE IF NOT EXISTS fin_voucher_template (
    template_id bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
    event_type varchar(64) NOT NULL COMMENT '业务事件类型',
    entry_seq int NOT NULL DEFAULT 1 COMMENT '分录序号',
    direction varchar(16) NOT NULL COMMENT '借贷方向(debit/credit)',
    subject_code varchar(32) NOT NULL COMMENT '会计科目编码',
    subject_name varchar(128) NOT NULL COMMENT '会计科目名称',
    auxiliary_type varchar(32) DEFAULT 'none' COMMENT '辅助核算类型(customer/supplier/warehouse/none)',
    summary_template varchar(255) DEFAULT '' COMMENT '摘要模板',
    status char(1) DEFAULT '0' COMMENT '状态(0启用 1停用)',
    create_by varchar(64) DEFAULT 'admin',
    create_time datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (template_id),
    UNIQUE KEY uk_voucher_tpl_event_seq (event_type, entry_seq)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业财凭证借贷科目映射模板';

CREATE TABLE IF NOT EXISTS fin_voucher_entry (
    entry_id bigint NOT NULL AUTO_INCREMENT COMMENT '分录ID',
    voucher_event_id bigint NOT NULL COMMENT '关联凭证事件ID',
    voucher_no varchar(64) NOT NULL COMMENT '凭证号',
    entry_seq int NOT NULL DEFAULT 1 COMMENT '分录行号',
    direction varchar(16) NOT NULL COMMENT '借贷方向(debit/credit)',
    subject_code varchar(32) NOT NULL COMMENT '科目编码',
    subject_name varchar(128) NOT NULL COMMENT '科目名称',
    auxiliary_type varchar(32) DEFAULT 'none' COMMENT '辅助类型',
    auxiliary_name varchar(128) DEFAULT '' COMMENT '辅助核算名称',
    debit_amount decimal(18,2) NOT NULL DEFAULT 0.00 COMMENT '借方金额',
    credit_amount decimal(18,2) NOT NULL DEFAULT 0.00 COMMENT '贷方金额',
    summary varchar(255) DEFAULT '' COMMENT '分录摘要',
    create_time datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (entry_id),
    KEY idx_voucher_entry_event (voucher_event_id),
    KEY idx_voucher_entry_no (voucher_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='复式记账凭证分录子表';

INSERT IGNORE INTO fin_voucher_template (event_type, entry_seq, direction, subject_code, subject_name, auxiliary_type, summary_template, status) VALUES
('inbound_complete', 1, 'debit', '1405', '库存商品', 'warehouse', '采购入库 {billNo}', '0'),
('inbound_complete', 2, 'credit', '2202', '应付账款', 'supplier', '采购入库应付 {billNo}', '0'),
('outbound_complete', 1, 'debit', '1122', '应收账款', 'customer', '销售出库应收 {billNo}', '0'),
('outbound_complete', 2, 'credit', '6001', '主营业务收入', 'customer', '销售出库收入 {billNo}', '0'),
('sale_return_complete', 1, 'debit', '6001', '主营业务收入', 'customer', '销售退货冲减收入 {billNo}', '0'),
('sale_return_complete', 2, 'credit', '1122', '应收账款', 'customer', '销售退货冲减应收 {billNo}', '0'),
('outbound_return', 1, 'debit', '6001', '主营业务收入', 'customer', '出库整单退货冲减 {billNo}', '0'),
('outbound_return', 2, 'credit', '1122', '应收账款', 'customer', '出库整单退货冲减应收 {billNo}', '0'),
('purchase_return_complete', 1, 'debit', '2202', '应付账款', 'supplier', '采购退货冲减应付 {billNo}', '0'),
('purchase_return_complete', 2, 'credit', '1405', '库存商品', 'warehouse', '采购退货冲减库存 {billNo}', '0'),
('receipt_confirm', 1, 'debit', '1002', '银行存款', 'none', '客户收款入账 {billNo}', '0'),
('receipt_confirm', 2, 'credit', '1122', '应收账款', 'customer', '核减客户应收 {billNo}', '0'),
('payment_confirm', 1, 'debit', '2202', '应付账款', 'supplier', '支付供应商货款 {billNo}', '0'),
('payment_confirm', 2, 'credit', '1002', '银行存款', 'none', '货款银行付款 {billNo}', '0'),
('expense_confirm', 1, 'debit', '6602', '管理费用', 'none', '期间费用确认 {billNo}', '0'),
('expense_confirm', 2, 'credit', '1002', '银行存款', 'none', '期间费用支出 {billNo}', '0'),
('bad_debt_confirm', 1, 'debit', '6701', '信用减值损失', 'customer', '确认坏账损失 {billNo}', '0'),
('bad_debt_confirm', 2, 'credit', '1122', '应收账款', 'customer', '核销坏账应收 {billNo}', '0'),
('bad_debt_recover', 1, 'debit', '1002', '银行存款', 'none', '已核销坏账收回 {billNo}', '0'),
('bad_debt_recover', 2, 'credit', '6701', '信用减值损失', 'customer', '坏账收回冲减损失 {billNo}', '0'),
('invoice_confirm', 1, 'debit', '1122', '应收账款', 'customer', '发票确认登记 {billNo}', '0'),
('invoice_confirm', 2, 'credit', '2221', '应交税费', 'none', '发票税额确认 {billNo}', '0'),
('fund_transfer', 1, 'debit', '1002', '银行存款', 'none', '资金内部调入 {billNo}', '0'),
('fund_transfer', 2, 'credit', '1002', '银行存款', 'none', '资金内部调出 {billNo}', '0'),
('cost_adjust', 1, 'debit', '1405', '库存商品', 'warehouse', '受控成本调价 {billNo}', '0'),
('cost_adjust', 2, 'credit', '6401', '主营业务成本', 'warehouse', '受控成本调价差额 {billNo}', '0');

-- ---------- P5-6: Dept/Owner DataScope & WarehouseScope Isolation ----------
CALL sp_add_col_if_missing('pur_order', 'dept_id', "bigint DEFAULT NULL COMMENT '所属部门ID'");
CALL sp_add_col_if_missing('pur_order', 'owner_user_id', "bigint DEFAULT NULL COMMENT '归属业务员ID'");
CALL sp_add_col_if_missing('sal_order', 'dept_id', "bigint DEFAULT NULL COMMENT '所属部门ID'");
CALL sp_add_col_if_missing('sal_order', 'owner_user_id', "bigint DEFAULT NULL COMMENT '归属业务员ID'");
CALL sp_add_col_if_missing('wms_inbound', 'dept_id', "bigint DEFAULT NULL COMMENT '所属部门ID'");
CALL sp_add_col_if_missing('wms_inbound', 'owner_user_id', "bigint DEFAULT NULL COMMENT '归属人ID'");
CALL sp_add_col_if_missing('wms_outbound', 'dept_id', "bigint DEFAULT NULL COMMENT '所属部门ID'");
CALL sp_add_col_if_missing('wms_outbound', 'owner_user_id', "bigint DEFAULT NULL COMMENT '归属人ID'");
CALL sp_add_col_if_missing('wms_transfer', 'dept_id', "bigint DEFAULT NULL COMMENT '所属部门ID'");
CALL sp_add_col_if_missing('wms_transfer', 'owner_user_id', "bigint DEFAULT NULL COMMENT '归属人ID'");
CALL sp_add_col_if_missing('wms_inventory_check', 'dept_id', "bigint DEFAULT NULL COMMENT '所属部门ID'");
CALL sp_add_col_if_missing('wms_inventory_check', 'owner_user_id', "bigint DEFAULT NULL COMMENT '归属人ID'");
CALL sp_add_col_if_missing('wms_purchase_return', 'dept_id', "bigint DEFAULT NULL COMMENT '所属部门ID'");
CALL sp_add_col_if_missing('wms_purchase_return', 'owner_user_id', "bigint DEFAULT NULL COMMENT '归属人ID'");
CALL sp_add_col_if_missing('wms_sale_return', 'dept_id', "bigint DEFAULT NULL COMMENT '所属部门ID'");
CALL sp_add_col_if_missing('wms_sale_return', 'owner_user_id', "bigint DEFAULT NULL COMMENT '归属人ID'");
CALL sp_add_col_if_missing('fin_receivable', 'dept_id', "bigint DEFAULT NULL COMMENT '所属部门ID'");
CALL sp_add_col_if_missing('fin_receivable', 'owner_user_id', "bigint DEFAULT NULL COMMENT '归属人ID'");
CALL sp_add_col_if_missing('fin_payable', 'dept_id', "bigint DEFAULT NULL COMMENT '所属部门ID'");
CALL sp_add_col_if_missing('fin_payable', 'owner_user_id', "bigint DEFAULT NULL COMMENT '归属人ID'");
CALL sp_add_col_if_missing('fin_receipt', 'dept_id', "bigint DEFAULT NULL COMMENT '所属部门ID'");
CALL sp_add_col_if_missing('fin_receipt', 'owner_user_id', "bigint DEFAULT NULL COMMENT '归属人ID'");
CALL sp_add_col_if_missing('fin_payment', 'dept_id', "bigint DEFAULT NULL COMMENT '所属部门ID'");
CALL sp_add_col_if_missing('fin_payment', 'owner_user_id', "bigint DEFAULT NULL COMMENT '归属人ID'");
CALL sp_add_col_if_missing('fin_expense', 'dept_id', "bigint DEFAULT NULL COMMENT '所属部门ID'");
CALL sp_add_col_if_missing('fin_expense', 'owner_user_id', "bigint DEFAULT NULL COMMENT '归属人ID'");
CALL sp_add_col_if_missing('fin_invoice', 'dept_id', "bigint DEFAULT NULL COMMENT '所属部门ID'");
CALL sp_add_col_if_missing('fin_invoice', 'owner_user_id', "bigint DEFAULT NULL COMMENT '归属人ID'");
CALL sp_add_col_if_missing('fin_bad_debt', 'dept_id', "bigint DEFAULT NULL COMMENT '所属部门ID'");
CALL sp_add_col_if_missing('fin_bad_debt', 'owner_user_id', "bigint DEFAULT NULL COMMENT '归属人ID'");

CALL sp_add_col_if_missing('sys_role', 'warehouse_scope', "char(1) DEFAULT '1' COMMENT '仓库数据范围(1全部仓库 2指定仓库)'");

CREATE TABLE IF NOT EXISTS sys_role_warehouse (
    role_id bigint NOT NULL COMMENT '角色ID',
    warehouse_id bigint NOT NULL COMMENT '仓库ID',
    PRIMARY KEY (role_id, warehouse_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色与授权仓库关联表';

-- 补齐标准角色 7：经营管理者（全局看板与报表查看/审批）
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, warehouse_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark)
SELECT 7, '经营管理者', 'boss_viewer', 7, '1', '1', 1, 1, '0', '0', 'admin', NOW(), '经营分析、全业务看板与审批角色'
WHERE NOT EXISTS (SELECT 1 FROM sys_role WHERE role_key = 'boss_viewer');

-- ---------- P6-1~5: Multi-Unit, Batch Expiry FEFO, Serial Number SN, Stock Lock Config ----------
CREATE TABLE IF NOT EXISTS md_product_unit (
    unit_id bigint NOT NULL AUTO_INCREMENT COMMENT '单位换算ID',
    product_id bigint NOT NULL COMMENT '商品ID',
    unit_name varchar(32) NOT NULL COMMENT '单位名称(如箱/盒/件)',
    is_base_unit char(1) DEFAULT '0' COMMENT '是否基准库存单位(1是 0否)',
    convert_ratio decimal(18,4) NOT NULL DEFAULT 1.0000 COMMENT '换算率(1当前单位=N基准单位)',
    bar_code varchar(64) DEFAULT NULL COMMENT '该包装单位条码',
    purchase_price decimal(18,2) DEFAULT NULL COMMENT '该单位采购价',
    sale_price decimal(18,2) DEFAULT NULL COMMENT '该单位销售价',
    status char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
    create_by varchar(64) DEFAULT '',
    create_time datetime DEFAULT CURRENT_TIMESTAMP,
    update_by varchar(64) DEFAULT '',
    update_time datetime DEFAULT NULL,
    PRIMARY KEY (unit_id),
    UNIQUE KEY uk_product_unit_name (product_id, unit_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品多单位与换算率表';

CALL sp_add_col_if_missing('pur_order_item', 'unit_name', "varchar(32) DEFAULT NULL COMMENT '业务单位'");
CALL sp_add_col_if_missing('pur_order_item', 'convert_ratio', "decimal(18,4) DEFAULT 1.0000 COMMENT '换算率'");
CALL sp_add_col_if_missing('pur_order_item', 'base_quantity', "decimal(18,4) DEFAULT NULL COMMENT '折合基准数量'");
CALL sp_add_col_if_missing('sal_order_item', 'unit_name', "varchar(32) DEFAULT NULL COMMENT '业务单位'");
CALL sp_add_col_if_missing('sal_order_item', 'convert_ratio', "decimal(18,4) DEFAULT 1.0000 COMMENT '换算率'");
CALL sp_add_col_if_missing('sal_order_item', 'base_quantity', "decimal(18,4) DEFAULT NULL COMMENT '折合基准数量'");
CALL sp_add_col_if_missing('wms_inbound_item', 'unit_name', "varchar(32) DEFAULT NULL COMMENT '业务单位'");
CALL sp_add_col_if_missing('wms_inbound_item', 'convert_ratio', "decimal(18,4) DEFAULT 1.0000 COMMENT '换算率'");
CALL sp_add_col_if_missing('wms_inbound_item', 'base_quantity', "decimal(18,4) DEFAULT NULL COMMENT '折合基准数量'");
CALL sp_add_col_if_missing('wms_inbound_item', 'production_date', "date DEFAULT NULL COMMENT '生产日期'");
CALL sp_add_col_if_missing('wms_inbound_item', 'expiry_date', "date DEFAULT NULL COMMENT '有效期至'");
CALL sp_add_col_if_missing('wms_outbound_item', 'unit_name', "varchar(32) DEFAULT NULL COMMENT '业务单位'");
CALL sp_add_col_if_missing('wms_outbound_item', 'convert_ratio', "decimal(18,4) DEFAULT 1.0000 COMMENT '换算率'");
CALL sp_add_col_if_missing('wms_outbound_item', 'base_quantity', "decimal(18,4) DEFAULT NULL COMMENT '折合基准数量'");
CALL sp_add_col_if_missing('wms_outbound_item', 'production_date', "date DEFAULT NULL COMMENT '生产日期'");
CALL sp_add_col_if_missing('wms_outbound_item', 'expiry_date', "date DEFAULT NULL COMMENT '有效期至'");

CALL sp_add_col_if_missing('md_product', 'warning_before_days', "int DEFAULT 30 COMMENT '临期预警天数'");
CALL sp_add_col_if_missing('wms_stock', 'production_date', "date DEFAULT NULL COMMENT '生产日期'");
CALL sp_add_col_if_missing('wms_stock', 'expiry_date', "date DEFAULT NULL COMMENT '有效期至'");

CREATE TABLE IF NOT EXISTS wms_serial_no (
    serial_id bigint NOT NULL AUTO_INCREMENT COMMENT '序列号主键',
    sn_code varchar(128) NOT NULL COMMENT '唯一序列号(SN)',
    product_id bigint NOT NULL COMMENT '商品ID',
    warehouse_id bigint DEFAULT NULL COMMENT '当前所在仓库ID',
    location_id bigint DEFAULT 0 COMMENT '当前库位ID',
    batch_no varchar(64) DEFAULT '' COMMENT '批次号',
    status varchar(20) NOT NULL DEFAULT 'in_stock' COMMENT '状态(in_stock在库/out_stock已出库/returned退货回库/scrapped报废)',
    inbound_id bigint DEFAULT NULL COMMENT '入库单ID',
    outbound_id bigint DEFAULT NULL COMMENT '出库单ID',
    customer_id bigint DEFAULT NULL COMMENT '最终售出客户ID',
    warranty_end_date date DEFAULT NULL COMMENT '质保截止日期',
    remark varchar(500) DEFAULT NULL COMMENT '备注',
    create_by varchar(64) DEFAULT '',
    create_time datetime DEFAULT CURRENT_TIMESTAMP,
    update_by varchar(64) DEFAULT '',
    update_time datetime DEFAULT NULL,
    PRIMARY KEY (serial_id),
    UNIQUE KEY uk_wms_sn_code (sn_code),
    KEY idx_sn_prod_wh (product_id, warehouse_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品序列号(SN)全生命周期档案';

CREATE TABLE IF NOT EXISTS wms_serial_no_log (
    sn_log_id bigint NOT NULL AUTO_INCREMENT COMMENT 'SN流转日志ID',
    serial_id bigint NOT NULL COMMENT '序列号ID',
    sn_code varchar(128) NOT NULL COMMENT '序列号',
    action_type varchar(32) NOT NULL COMMENT '动作类型(inbound/outbound/transfer/sale_return/purchase_return/adjust)',
    bill_type varchar(32) DEFAULT NULL COMMENT '单据类型',
    bill_id bigint DEFAULT NULL COMMENT '单据ID',
    bill_no varchar(64) DEFAULT NULL COMMENT '单据号',
    warehouse_id bigint DEFAULT NULL COMMENT '仓库ID',
    counterparty_id bigint DEFAULT NULL COMMENT '往来单位ID',
    remark varchar(500) DEFAULT NULL COMMENT '备注',
    create_by varchar(64) DEFAULT '',
    create_time datetime DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (sn_log_id),
    KEY idx_sn_log_code (sn_code, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品序列号(SN)流转履历表';

-- ---------- System Configs for Phase 5 & Phase 6 ----------
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT '库存策略-销货订单审批自动锁库', 'canglian.stock.lockOnSaleApprove', 'true', 'Y', 'admin', NOW(), '开启后销货订单审批通过时自动锁定可用库存，出库审核或关闭时自动释放'
WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'canglian.stock.lockOnSaleApprove');

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT '单据打印-企业抬头名称', 'canglian.print.companyName', '仓链商贸有限公司', 'Y', 'admin', NOW(), 'A4/三联单打印抬头公司名称'
WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'canglian.print.companyName');

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT '单据打印-联系电话', 'canglian.print.companyPhone', '400-800-8888', 'Y', 'admin', NOW(), '单据打印页眉联系电话'
WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'canglian.print.companyPhone');

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT '单据打印-企业地址', 'canglian.print.companyAddress', '上海市浦东新区仓链智慧产业园', 'Y', 'admin', NOW(), '单据打印页眉地址'
WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'canglian.print.companyAddress');

-- ---------- New Menus & Permissions (2800..2836) ----------
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2800, '序列号溯源', 2001, 14, 'serialNo', 'business/serialNo/index', '', '', 1, 0, 'C', '0', '0', 'business:serialNo:list', 'barcode', 'admin', NOW(), 'SN一物一码全生命周期追踪'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2800);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark) VALUES
(2801, '采购订单反审批/关闭', 2262, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:purOrder:close', '#', 'admin', NOW(), ''),
(2802, '销售订单反审批/关闭', 2261, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:saleOrder:close', '#', 'admin', NOW(), ''),
(2803, '入库单反审核', 2004, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inbound:unaudit', '#', 'admin', NOW(), ''),
(2804, '入库单红冲', 2004, 7, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inbound:reverse', '#', 'admin', NOW(), ''),
(2805, '出库单反审核', 2005, 8, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:outbound:unaudit', '#', 'admin', NOW(), ''),
(2806, '出库单红冲', 2005, 9, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:outbound:reverse', '#', 'admin', NOW(), ''),
(2807, '调拨单反审核/红冲', 2007, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:transfer:unaudit', '#', 'admin', NOW(), ''),
(2808, '盘点单反审核/红冲', 2006, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:inventoryCheck:unaudit', '#', 'admin', NOW(), ''),
(2809, '采购退货反审核/红冲', 2410, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:purchaseReturn:unaudit', '#', 'admin', NOW(), ''),
(2810, '销售退货反审核/红冲', 2411, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:saleReturn:unaudit', '#', 'admin', NOW(), ''),
(2811, '资金流水与互转', 2205, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:fundAccount:transfer', '#', 'admin', NOW(), ''),
(2812, '收款确认/反确认', 2203, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:receipt:confirm', '#', 'admin', NOW(), ''),
(2813, '付款确认/反确认', 2204, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:payment:confirm', '#', 'admin', NOW(), ''),
(2814, '坏账收回', 2208, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:badDebt:recover', '#', 'admin', NOW(), ''),
(2815, '红字发票冲销', 2263, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:invoice:red', '#', 'admin', NOW(), ''),
(2816, '总账科目维护与建期', 2600, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:generalLedger:edit', '#', 'admin', NOW(), ''),
(2817, '凭证复式导出', 2269, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:voucherEvent:export', '#', 'admin', NOW(), ''),
(2818, '库存冻结与解冻', 2003, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:stock:freeze', '#', 'admin', NOW(), ''),
(2819, '序列号登记与维护', 2800, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:serialNo:edit', '#', 'admin', NOW(), ''),
(2820, '价格实时解析查询', 2265, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:price:resolve', '#', 'admin', NOW(), '')
ON DUPLICATE KEY UPDATE menu_name = VALUES(menu_name);

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu WHERE menu_id BETWEEN 2800 AND 2820;

INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 2, menu_id FROM sys_menu WHERE menu_id BETWEEN 2800 AND 2820;

-- 给经营管理者角色(7)分配所有业务看板与查询/审批/报表菜单
INSERT IGNORE INTO sys_role_menu (role_id, menu_id)
SELECT 7, menu_id FROM sys_menu
WHERE menu_id IN (2000, 2001, 2200, 2011, 2210, 2270, 2475, 2601)
   OR perms LIKE 'business:%:list'
   OR perms LIKE 'business:%:query'
   OR perms LIKE 'business:%:approve'
   OR perms = 'business:report:export';

DROP PROCEDURE IF EXISTS sp_add_col_if_missing;
