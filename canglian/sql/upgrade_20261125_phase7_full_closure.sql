-- =====================================================================
-- 仓链管理系统 (Canglian ERP) Phase 7 全模块深度闭环修复升级脚本
-- Date: 2026-11-25
-- 覆盖全部 58 项审计问题 (P0/P1/P2/P3) 所需的表结构、凭证模板、科目、配置与权限修复
-- =====================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 幂等加列辅助存储过程
DROP PROCEDURE IF EXISTS cl_add_column_if_missing;
DELIMITER $$
CREATE PROCEDURE cl_add_column_if_missing(
    IN p_table VARCHAR(64),
    IN p_column VARCHAR(64),
    IN p_def TEXT
)
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = p_table AND COLUMN_NAME = p_column
    ) THEN
        SET @ddl = CONCAT('ALTER TABLE `', p_table, '` ADD COLUMN `', p_column, '` ', p_def);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$
DELIMITER ;

-- =====================================================================
-- 1. C-P0-04: 拓宽 bill_type 字段宽度至 varchar(40)，根治反审/红冲 SQL 截断崩溃
-- =====================================================================
ALTER TABLE `wms_stock_log` MODIFY COLUMN `bill_type` VARCHAR(40) NOT NULL COMMENT '单据类型';
ALTER TABLE `fin_cost_log` MODIFY COLUMN `bill_type` VARCHAR(40) NOT NULL COMMENT '单据类型';

-- =====================================================================
-- 2. C-P0-10: 新增订单级库存锁库明细台账表 wms_stock_lock + sal_order.stock_locked_flag
-- =====================================================================
CREATE TABLE IF NOT EXISTS `wms_stock_lock` (
  `lock_id` bigint NOT NULL AUTO_INCREMENT COMMENT '锁库台账ID',
  `order_id` bigint NOT NULL COMMENT '销售订单ID',
  `order_item_id` bigint DEFAULT NULL COMMENT '销售订单明细ID',
  `warehouse_id` bigint NOT NULL COMMENT '仓库ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `location_id` bigint NOT NULL DEFAULT 0 COMMENT '库位ID',
  `batch_no` varchar(64) NOT NULL DEFAULT '' COMMENT '批次号',
  `stock_id` bigint NOT NULL COMMENT '对应库存行ID',
  `locked_qty` decimal(16,4) NOT NULL DEFAULT 0.0000 COMMENT '原始锁定数量(基准单位)',
  `released_qty` decimal(16,4) NOT NULL DEFAULT 0.0000 COMMENT '已释放数量(基准单位)',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态(0锁定中 1已全部释放)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`lock_id`),
  KEY `idx_wms_stock_lock_order` (`order_id`, `product_id`, `status`),
  KEY `idx_wms_stock_lock_stock` (`stock_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='销售订单库存锁库明细台账';

CALL cl_add_column_if_missing('sal_order', 'stock_locked_flag', "CHAR(1) DEFAULT '0' COMMENT '是否已锁定库存(0否 1是)'");

-- =====================================================================
-- 3. C-P1-13: 新增往来红蓝对冲台账表 fin_offset_log（支持撤销对冲）
-- =====================================================================
CREATE TABLE IF NOT EXISTS `fin_offset_log` (
  `offset_id` bigint NOT NULL AUTO_INCREMENT COMMENT '对冲流水ID',
  `offset_type` varchar(16) NOT NULL COMMENT '类型(receivable应收/payable应付)',
  `positive_bill_id` bigint NOT NULL COMMENT '正向往来单ID',
  `red_bill_id` bigint NOT NULL COMMENT '红字往来单ID',
  `offset_amount` decimal(16,2) NOT NULL COMMENT '对冲金额',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态(0生效 1已撤销)',
  `create_by` varchar(64) DEFAULT '' COMMENT '操作人',
  `create_time` datetime DEFAULT NULL COMMENT '对冲时间',
  `reverse_by` varchar(64) DEFAULT NULL COMMENT '撤销人',
  `reverse_time` datetime DEFAULT NULL COMMENT '撤销时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`offset_id`),
  KEY `idx_fin_offset_pos` (`offset_type`, `positive_bill_id`, `status`),
  KEY `idx_fin_offset_red` (`offset_type`, `red_bill_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='往来红蓝对冲台账';

-- =====================================================================
-- 4. C-P0-15 / C-P1-04 / C-P0-13 / C-P1-06 / C-P1-08 / C-P1-15 / C-P1-16 / C-P2-03 / C-P3-05: 补齐单据闭环列
-- =====================================================================
-- 4.1 入库/出库税率折扣 + SN码 + 扫码复核标记
CALL cl_add_column_if_missing('wms_inbound', 'discount_amount', "DECIMAL(16,2) DEFAULT 0.00 COMMENT '整单折让金额'");
CALL cl_add_column_if_missing('wms_inbound', 'tax_total_amount', "DECIMAL(16,2) DEFAULT 0.00 COMMENT '税额合计'");
CALL cl_add_column_if_missing('wms_inbound_item', 'discount_rate', "DECIMAL(10,4) DEFAULT 100.0000 COMMENT '行折扣率(%)'");
CALL cl_add_column_if_missing('wms_inbound_item', 'tax_rate', "DECIMAL(10,4) DEFAULT 0.0000 COMMENT '税率(%)'");
CALL cl_add_column_if_missing('wms_inbound_item', 'tax_amount', "DECIMAL(16,2) DEFAULT 0.00 COMMENT '行税额'");
CALL cl_add_column_if_missing('wms_inbound_item', 'sn_codes', "TEXT DEFAULT NULL COMMENT '序列号SN清单(逗号或换行分隔)'");

CALL cl_add_column_if_missing('wms_outbound', 'discount_amount', "DECIMAL(16,2) DEFAULT 0.00 COMMENT '整单折让金额'");
CALL cl_add_column_if_missing('wms_outbound', 'tax_total_amount', "DECIMAL(16,2) DEFAULT 0.00 COMMENT '税额合计'");
CALL cl_add_column_if_missing('wms_outbound', 'verified_flag', "CHAR(1) DEFAULT '0' COMMENT '扫码复核状态(0未复核 1已复核)'");
CALL cl_add_column_if_missing('wms_outbound', 'verified_by', "VARCHAR(64) DEFAULT NULL COMMENT '复核人'");
CALL cl_add_column_if_missing('wms_outbound', 'verified_time', "DATETIME DEFAULT NULL COMMENT '复核时间'");
CALL cl_add_column_if_missing('wms_outbound_item', 'discount_rate', "DECIMAL(10,4) DEFAULT 100.0000 COMMENT '行折扣率(%)'");
CALL cl_add_column_if_missing('wms_outbound_item', 'tax_rate', "DECIMAL(10,4) DEFAULT 0.0000 COMMENT '税率(%)'");
CALL cl_add_column_if_missing('wms_outbound_item', 'tax_amount', "DECIMAL(16,2) DEFAULT 0.00 COMMENT '行税额'");
CALL cl_add_column_if_missing('wms_outbound_item', 'sn_codes', "TEXT DEFAULT NULL COMMENT '序列号SN清单(逗号或换行分隔)'");

-- 4.2 调拨/盘点补齐业务日期、多单位、效期与SN
CALL cl_add_column_if_missing('wms_transfer', 'business_date', "DATE DEFAULT NULL COMMENT '业务日期'");
UPDATE `wms_transfer` SET `business_date` = DATE(`create_time`) WHERE `business_date` IS NULL AND `create_time` IS NOT NULL;
CALL cl_add_column_if_missing('wms_transfer_item', 'unit_name', "VARCHAR(32) DEFAULT NULL COMMENT '业务单位'");
CALL cl_add_column_if_missing('wms_transfer_item', 'convert_ratio', "DECIMAL(16,4) DEFAULT 1.0000 COMMENT '单位换算率'");
CALL cl_add_column_if_missing('wms_transfer_item', 'base_quantity', "DECIMAL(16,4) DEFAULT 0.0000 COMMENT '基准单位数量'");
CALL cl_add_column_if_missing('wms_transfer_item', 'production_date', "DATE DEFAULT NULL COMMENT '生产日期'");
CALL cl_add_column_if_missing('wms_transfer_item', 'expiry_date', "DATE DEFAULT NULL COMMENT '有效期至'");
CALL cl_add_column_if_missing('wms_transfer_item', 'sn_codes', "TEXT DEFAULT NULL COMMENT '序列号SN清单'");

CALL cl_add_column_if_missing('wms_inventory_check', 'business_date', "DATE DEFAULT NULL COMMENT '业务日期'");
UPDATE `wms_inventory_check` SET `business_date` = DATE(`create_time`) WHERE `business_date` IS NULL AND `create_time` IS NOT NULL;
CALL cl_add_column_if_missing('wms_inventory_check_item', 'unit_name', "VARCHAR(32) DEFAULT NULL COMMENT '业务单位'");
CALL cl_add_column_if_missing('wms_inventory_check_item', 'convert_ratio', "DECIMAL(16,4) DEFAULT 1.0000 COMMENT '单位换算率'");
CALL cl_add_column_if_missing('wms_inventory_check_item', 'base_diff_qty', "DECIMAL(16,4) DEFAULT 0.0000 COMMENT '基准单位差异量'");
CALL cl_add_column_if_missing('wms_inventory_check_item', 'production_date', "DATE DEFAULT NULL COMMENT '生产日期'");
CALL cl_add_column_if_missing('wms_inventory_check_item', 'expiry_date', "DATE DEFAULT NULL COMMENT '有效期至'");

-- 4.3 采购退货/销售退货补齐多单位、效期、SN及精准扣减轨迹
CALL cl_add_column_if_missing('wms_purchase_return_item', 'unit_name', "VARCHAR(32) DEFAULT NULL COMMENT '业务单位'");
CALL cl_add_column_if_missing('wms_purchase_return_item', 'convert_ratio', "DECIMAL(16,4) DEFAULT 1.0000 COMMENT '单位换算率'");
CALL cl_add_column_if_missing('wms_purchase_return_item', 'base_quantity', "DECIMAL(16,4) DEFAULT 0.0000 COMMENT '基准单位数量'");
CALL cl_add_column_if_missing('wms_purchase_return_item', 'production_date', "DATE DEFAULT NULL COMMENT '生产日期'");
CALL cl_add_column_if_missing('wms_purchase_return_item', 'expiry_date', "DATE DEFAULT NULL COMMENT '有效期至'");
CALL cl_add_column_if_missing('wms_purchase_return_item', 'sn_codes', "TEXT DEFAULT NULL COMMENT '序列号SN清单'");
CALL cl_add_column_if_missing('wms_purchase_return_item', 'deduct_trace', "TEXT DEFAULT NULL COMMENT '整仓扣减库存轨迹JSON'");

CALL cl_add_column_if_missing('wms_sale_return_item', 'unit_name', "VARCHAR(32) DEFAULT NULL COMMENT '业务单位'");
CALL cl_add_column_if_missing('wms_sale_return_item', 'convert_ratio', "DECIMAL(16,4) DEFAULT 1.0000 COMMENT '单位换算率'");
CALL cl_add_column_if_missing('wms_sale_return_item', 'base_quantity', "DECIMAL(16,4) DEFAULT 0.0000 COMMENT '基准单位数量'");
CALL cl_add_column_if_missing('wms_sale_return_item', 'production_date', "DATE DEFAULT NULL COMMENT '生产日期'");
CALL cl_add_column_if_missing('wms_sale_return_item', 'expiry_date', "DATE DEFAULT NULL COMMENT '有效期至'");
CALL cl_add_column_if_missing('wms_sale_return_item', 'sn_codes', "TEXT DEFAULT NULL COMMENT '序列号SN清单'");

-- 4.4 价格有效期、最近成交价单位、客户/供应商期初余额、应收坏账独立列
CALL cl_add_column_if_missing('price_customer_special', 'effective_date', "DATE DEFAULT NULL COMMENT '生效日期'");
CALL cl_add_column_if_missing('price_customer_special', 'expire_date', "DATE DEFAULT NULL COMMENT '失效日期'");
CALL cl_add_column_if_missing('price_customer_level', 'effective_date', "DATE DEFAULT NULL COMMENT '生效日期'");
CALL cl_add_column_if_missing('price_customer_level', 'expire_date', "DATE DEFAULT NULL COMMENT '失效日期'");
CALL cl_add_column_if_missing('price_recent_transaction', 'unit_name', "VARCHAR(32) DEFAULT NULL COMMENT '基准单位'");

CALL cl_add_column_if_missing('md_customer', 'opening_receivable', "DECIMAL(16,2) DEFAULT 0.00 COMMENT '期初应收余额'");
CALL cl_add_column_if_missing('md_supplier', 'opening_payable', "DECIMAL(16,2) DEFAULT 0.00 COMMENT '期初应付余额'");

CALL cl_add_column_if_missing('fin_receivable', 'bad_debt_amount', "DECIMAL(16,2) DEFAULT 0.00 COMMENT '已核销坏账金额'");

-- =====================================================================
-- 5. C-P0-12 / C-P0-16 / C-P1-14: 补齐标准会计科目与全部双边复式凭证模板
-- =====================================================================
INSERT IGNORE INTO `fin_account_subject` (`subject_code`, `subject_name`, `subject_type`, `balance_direction`, `status`, `create_by`, `create_time`, `remark`) VALUES
('1405', '库存商品', 'asset', 'debit', '0', 'admin', NOW(), '仓链标准科目'),
('2221', '应交税费', 'liability', 'credit', '0', 'admin', NOW(), '仓链标准科目'),
('5301', '营业外收入', 'profit_loss', 'credit', '0', 'admin', NOW(), '仓链标准科目-盘盈等'),
('5711', '营业外支出', 'profit_loss', 'debit', '0', 'admin', NOW(), '仓链标准科目-盘亏等'),
('6602', '管理费用', 'profit_loss', 'debit', '0', 'admin', NOW(), '仓链标准科目'),
('6603', '财务费用', 'profit_loss', 'debit', '0', 'admin', NOW(), '仓链标准科目-银行手续费等'),
('6701', '信用减值损失', 'profit_loss', 'debit', '0', 'admin', NOW(), '仓链标准科目-坏账核销与收回');

INSERT IGNORE INTO `fin_voucher_template` (`event_type`, `entry_seq`, `direction`, `subject_code`, `subject_name`, `auxiliary_type`, `summary_template`, `status`, `create_by`, `create_time`) VALUES
('outbound_cogs', 1, 'debit', '6401', '主营业务成本', 'warehouse', '销售签收结转主营成本 {billNo}', '0', 'admin', NOW()),
('outbound_cogs', 2, 'credit', '1405', '库存商品', 'warehouse', '销售签收核减库存商品 {billNo}', '0', 'admin', NOW()),
('outbound_cogs_return', 1, 'debit', '1405', '库存商品', 'warehouse', '销售退回/红冲冲回库存商品 {billNo}', '0', 'admin', NOW()),
('outbound_cogs_return', 2, 'credit', '6401', '主营业务成本', 'warehouse', '销售退回/红冲冲减主营成本 {billNo}', '0', 'admin', NOW()),
('inventory_check_gain', 1, 'debit', '1405', '库存商品', 'warehouse', '盘点盘盈增加库存商品 {billNo}', '0', 'admin', NOW()),
('inventory_check_gain', 2, 'credit', '5301', '营业外收入', 'warehouse', '盘点盘盈确认收益 {billNo}', '0', 'admin', NOW()),
('inventory_check_loss', 1, 'debit', '5711', '营业外支出', 'warehouse', '盘点盘亏确认损失 {billNo}', '0', 'admin', NOW()),
('inventory_check_loss', 2, 'credit', '1405', '库存商品', 'warehouse', '盘点盘亏核减库存商品 {billNo}', '0', 'admin', NOW()),
('fund_transfer_fee', 1, 'debit', '6603', '财务费用', 'none', '资金内部互转银行手续费 {billNo}', '0', 'admin', NOW()),
('fund_transfer_fee', 2, 'credit', '1002', '银行存款', 'none', '扣划互转手续费 {billNo}', '0', 'admin', NOW()),
('refund_receipt', 1, 'debit', '1122', '应收账款', 'customer', '销售退货现金退款冲回 {billNo}', '0', 'admin', NOW()),
('refund_receipt', 2, 'credit', '1002', '银行存款', 'none', '支付客户退货退款 {billNo}', '0', 'admin', NOW()),
('sale_invoice_confirm', 1, 'debit', '1122', '应收账款', 'customer', '销售发票确认销项税额 {billNo}', '0', 'admin', NOW()),
('sale_invoice_confirm', 2, 'credit', '2221', '应交税费', 'customer', '应交增值税-销项税额 {billNo}', '0', 'admin', NOW()),
('purchase_invoice_confirm', 1, 'debit', '2221', '应交税费', 'supplier', '采购发票确认进项税额 {billNo}', '0', 'admin', NOW()),
('purchase_invoice_confirm', 2, 'credit', '2202', '应付账款', 'supplier', '应交增值税-进项税额应付 {billNo}', '0', 'admin', NOW()),
('manual_receivable', 1, 'debit', '1122', '应收账款', 'customer', '手工应收单确认 {billNo}', '0', 'admin', NOW()),
('manual_receivable', 2, 'credit', '6001', '主营业务收入', 'customer', '手工应收确认收入 {billNo}', '0', 'admin', NOW()),
('manual_payable', 1, 'debit', '1405', '库存商品', 'supplier', '手工应付单确认 {billNo}', '0', 'admin', NOW()),
('manual_payable', 2, 'credit', '2202', '应付账款', 'supplier', '手工应付确认负债 {billNo}', '0', 'admin', NOW());

-- =====================================================================
-- 6. C-P3-06 / C-P2-06 / C-P3-04: 系统参数、常用费用类别、种子数据自洽修复
-- =====================================================================
INSERT INTO `sys_config` (`config_id`, `config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `remark`) VALUES
(106, '库存策略-批次临期预警默认天数', 'canglian.stock.expiryWarningDays', '30', 'Y', 'admin', NOW(), '商品未单独设置预警天数时默认按提前30天预警'),
(107, '库存策略-是否允许负库存出库', 'canglian.stock.allowNegative', 'false', 'Y', 'admin', NOW(), '生产环境默认关闭负库存(false)，开启(true)仅适用于特殊先出后补场景')
ON DUPLICATE KEY UPDATE `config_name` = VALUES(`config_name`), `remark` = VALUES(`remark`);

-- 补齐常用费用类别，配合费用单下拉
INSERT IGNORE INTO `fin_expense_category` (`category_id`, `parent_id`, `ancestors`, `category_name`, `category_code`, `order_num`, `status`, `del_flag`, `create_by`, `create_time`, `remark`) VALUES
(2, 0, '0', '办公费', 'office', 2, '0', '0', 'admin', NOW(), '日常办公耗材及用品'),
(3, 0, '0', '差旅费', 'travel', 3, '0', '0', 'admin', NOW(), '业务差旅及交通住宿'),
(4, 0, '0', '房租水电', 'rent', 4, '0', '0', 'admin', NOW(), '仓库与办公场地租金水电'),
(5, 0, '0', '推广费', 'marketing', 5, '0', '0', 'admin', NOW(), '销售渠道推广与广告费'),
(6, 0, '0', '其他费用', 'other', 6, '0', '0', 'admin', NOW(), '其他期间费用');

-- 种子数据自洽修复：补齐基准单位 + 持久化 md_product_unit 基准单位 + 对齐全局成本算法 + 补齐样例费用单资金流水
UPDATE `md_product` SET `base_unit` = `unit_name` WHERE (`base_unit` IS NULL OR `base_unit` = '') AND `unit_name` IS NOT NULL AND `unit_name` <> '';

INSERT IGNORE INTO `md_product_unit` (`product_id`, `unit_name`, `is_base_unit`, `convert_ratio`, `bar_code`, `purchase_price`, `sale_price`, `status`, `create_by`, `create_time`)
SELECT p.`product_id`, COALESCE(NULLIF(p.`base_unit`, ''), NULLIF(p.`unit_name`, ''), '件'), '1', 1.0000, NULL, p.`cost_price`, p.`sale_price`, '0', 'admin', NOW()
FROM `md_product` p
WHERE NOT EXISTS (
    SELECT 1 FROM `md_product_unit` u WHERE u.`product_id` = p.`product_id` AND u.`is_base_unit` = '1'
);

-- 对齐样例成本记录至全局默认算法 moving
UPDATE `fin_cost_account` SET `cost_method` = 'moving' WHERE `cost_account_id` = 1 AND `cost_method` = 'fifo';
UPDATE `fin_cost_log` SET `cost_method` = 'moving' WHERE `cost_log_id` = 1 AND `cost_method` = 'fifo';

-- 为样例已确认费用单 ex20260101001 补齐资金台账（若缺失）
INSERT INTO `fin_fund_log` (`account_id`, `biz_type`, `bill_id`, `bill_no`, `direction`, `change_amount`, `before_balance`, `after_balance`, `biz_date`, `counterparty_name`, `remark`, `create_by`, `create_time`)
SELECT 1, 'expense', 1, 'ex20260101001', '2', 120.00, 50120.00, 50000.00, e.`expense_date`, '运费', '样例费用支出入账流水', 'admin', e.`create_time`
FROM `fin_expense` e
WHERE e.`expense_id` = 1
  AND NOT EXISTS (SELECT 1 FROM `fin_fund_log` l WHERE l.`biz_type` = 'expense' AND l.`bill_id` = 1);

-- =====================================================================
-- 7. C-P0-02 / C-P3-03: 菜单归位、命名统一、新按钮权限、角色部门与角色菜单全面修复
-- =====================================================================
-- 7.1 将数据备份从财务管理(2200)迁移到系统工具(3)，统一订单命名
UPDATE `sys_menu` SET `parent_id` = 3, `order_num` = 4 WHERE `menu_id` = 2750;
UPDATE `sys_menu` SET `menu_name` = '销售订单' WHERE `menu_id` = 2261;
UPDATE `sys_menu` SET `menu_name` = '采购订单' WHERE `menu_id` = 2262;

-- 7.2 新增 Phase 7 内控按钮权限 (2821 ~ 2828)
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `route_name`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`) VALUES
(2821, '核销反确认', 2207, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:writeOff:unconfirm', '#', 'admin', NOW(), ''),
(2822, '坏账反确认', 2208, 7, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:badDebt:unconfirm', '#', 'admin', NOW(), ''),
(2823, '互转反确认', 2205, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:fundAccount:transferUnconfirm', '#', 'admin', NOW(), ''),
(2824, '应收撤销对冲', 2201, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:receivable:unoffset', '#', 'admin', NOW(), ''),
(2825, '应付撤销对冲', 2202, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:payable:unoffset', '#', 'admin', NOW(), ''),
(2826, '一键补生成凭证', 2269, 7, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:voucherEvent:regenerate', '#', 'admin', NOW(), ''),
(2827, '出库确认与退货', 2005, 11, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:outbound:verify', '#', 'admin', NOW(), ''),
(2828, '库存导入模板', 2003, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:stock:importTemplate', '#', 'admin', NOW(), '')
ON DUPLICATE KEY UPDATE `menu_name` = VALUES(`menu_name`), `perms` = VALUES(`perms`);

-- 7.3 修复业务角色 3(仓管)/4(采购)/5(销售)/6(财务) 的 sys_role_dept 空表锁死问题
-- 将业务部门(100~109)写入 3/4/5/6，避免 data_scope='2' 在默认演示环境查不到任何数据
INSERT IGNORE INTO `sys_role_dept` (`role_id`, `dept_id`)
SELECT r.`role_id`, d.`dept_id`
FROM `sys_role` r
CROSS JOIN `sys_dept` d
WHERE r.`role_id` IN (3, 4, 5, 6) AND d.`del_flag` = '0';

-- 7.4 收紧 Role 2 (common 普通角色) 越权：移除系统管理(1)、系统监控(2)、系统工具(3)及子菜单、数据备份(2750,2791-2795)、总账结账/编辑(2621,2622,2816)
DELETE FROM `sys_role_menu`
WHERE `role_id` = 2
  AND (
    `menu_id` IN (1, 2, 3, 2750, 2791, 2792, 2793, 2794, 2795, 2621, 2622, 2816)
    OR `menu_id` BETWEEN 100 AND 117
    OR `menu_id` BETWEEN 500 AND 501
    OR `menu_id` BETWEEN 1000 AND 1061
  );

-- 7.5 补全 Role 1 (admin) 完整菜单关联，使角色管理界面查看清晰一致
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, `menu_id` FROM `sys_menu`;

-- 7.6 补全 Role 3 (仓管 warehouse) 菜单与按钮权限
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(3, 2000), (3, 2402), (3, 2440), (3, 2270), (3, 2475), (3, 2580), (3, 2470),
(3, 2001), (3, 2002), (3, 2100), (3, 2101), (3, 2102),
(3, 2003), (3, 2110), (3, 2474), (3, 2818), (3, 2828),
(3, 2004), (3, 2120), (3, 2121), (3, 2122), (3, 2123), (3, 2124), (3, 2125), (3, 2126), (3, 2803), (3, 2804),
(3, 2005), (3, 2130), (3, 2131), (3, 2132), (3, 2133), (3, 2134), (3, 2135), (3, 2184), (3, 2185), (3, 2186), (3, 2764), (3, 2805), (3, 2806), (3, 2827),
(3, 2006), (3, 2140), (3, 2141), (3, 2142), (3, 2143), (3, 2760), (3, 2786), (3, 2787), (3, 2808),
(3, 2007), (3, 2150), (3, 2151), (3, 2152), (3, 2153), (3, 2154), (3, 2761), (3, 2781), (3, 2782), (3, 2807),
(3, 2008), (3, 2160), (3, 2009), (3, 2170), (3, 2010), (3, 2180), (3, 2011), (3, 2187),
(3, 2268), (3, 2570), (3, 2271), (3, 2590), (3, 2591), (3, 2592), (3, 2593),
(3, 2410), (3, 2450), (3, 2451), (3, 2452), (3, 2453), (3, 2454), (3, 2765), (3, 2766), (3, 2809),
(3, 2411), (3, 2460), (3, 2461), (3, 2462), (3, 2463), (3, 2464), (3, 2770), (3, 2771), (3, 2810),
(3, 2700), (3, 2701), (3, 2702), (3, 2703), (3, 2704),
(3, 2800), (3, 2819);

-- 7.7 补全 Role 4 (采购 purchase) 菜单与按钮权限
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(4, 2000), (4, 2270), (4, 2475), (4, 2580), (4, 2470),
(4, 2401), (4, 2430), (4, 2431), (4, 2432), (4, 2433), (4, 2473),
(4, 2402), (4, 2440), (4, 2441), (4, 2442), (4, 2471),
(4, 2262), (4, 2520), (4, 2521), (4, 2522), (4, 2523), (4, 2524), (4, 2525), (4, 2526), (4, 2801),
(4, 2001), (4, 2002), (4, 2100), (4, 2003), (4, 2110), (4, 2268), (4, 2570),
(4, 2004), (4, 2120), (4, 2121), (4, 2122), (4, 2126),
(4, 2410), (4, 2450), (4, 2451), (4, 2452), (4, 2453), (4, 2454), (4, 2765), (4, 2766), (4, 2809),
(4, 2200), (4, 2202), (4, 2310), (4, 2264), (4, 2530), (4, 2531), (4, 2532);

-- 7.8 补全 Role 5 (销售 sales) 菜单与按钮权限
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(5, 2000), (5, 2270), (5, 2475), (5, 2580), (5, 2470),
(5, 2400), (5, 2420), (5, 2421), (5, 2422), (5, 2423), (5, 2472),
(5, 2402), (5, 2440),
(5, 2260), (5, 2517),
(5, 2261), (5, 2510), (5, 2511), (5, 2512), (5, 2513), (5, 2514), (5, 2515), (5, 2516), (5, 2802),
(5, 2265), (5, 2540), (5, 2541), (5, 2542), (5, 2543), (5, 2820),
(5, 2266), (5, 2550), (5, 2551), (5, 2552), (5, 2553),
(5, 2267), (5, 2560),
(5, 2001), (5, 2002), (5, 2100), (5, 2003), (5, 2110),
(5, 2005), (5, 2130), (5, 2131), (5, 2132), (5, 2135),
(5, 2411), (5, 2460), (5, 2461), (5, 2462), (5, 2463), (5, 2464), (5, 2770), (5, 2771), (5, 2810),
(5, 2200), (5, 2201), (5, 2300), (5, 2203), (5, 2320), (5, 2321), (5, 2812),
(5, 2263), (5, 2530), (5, 2531), (5, 2532);

-- 7.9 补全 Role 6 (财务 finance) 菜单与按钮权限
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(6, 2000), (6, 2270), (6, 2475), (6, 2580), (6, 2470), (6, 2400), (6, 2420), (6, 2401), (6, 2430), (6, 2402), (6, 2440),
(6, 2001), (6, 2002), (6, 2100), (6, 2003), (6, 2110), (6, 2004), (6, 2120), (6, 2005), (6, 2130),
(6, 2200),
(6, 2201), (6, 2300), (6, 2301), (6, 2302), (6, 2303), (6, 2762), (6, 2824),
(6, 2202), (6, 2310), (6, 2311), (6, 2312), (6, 2313), (6, 2763), (6, 2825),
(6, 2203), (6, 2320), (6, 2321), (6, 2322), (6, 2323), (6, 2812),
(6, 2204), (6, 2330), (6, 2331), (6, 2332), (6, 2333), (6, 2813),
(6, 2205), (6, 2340), (6, 2341), (6, 2342), (6, 2343), (6, 2811), (6, 2823),
(6, 2206), (6, 2350), (6, 2351), (6, 2352), (6, 2353), (6, 2354),
(6, 2207), (6, 2360), (6, 2361), (6, 2362), (6, 2363), (6, 2364), (6, 2821),
(6, 2208), (6, 2370), (6, 2371), (6, 2372), (6, 2373), (6, 2780), (6, 2814), (6, 2822),
(6, 2209), (6, 2380), (6, 2381), (6, 2797), (6, 2798),
(6, 2210), (6, 2390), (6, 2796), (6, 2601),
(6, 2263), (6, 2264), (6, 2530), (6, 2531), (6, 2532), (6, 2533), (6, 2534), (6, 2815),
(6, 2269), (6, 2610), (6, 2611), (6, 2612), (6, 2613), (6, 2614), (6, 2817), (6, 2826),
(6, 2600), (6, 2620), (6, 2621), (6, 2622), (6, 2816),
(6, 2710), (6, 2711), (6, 2712), (6, 2713), (6, 2714),
(6, 2720), (6, 2721), (6, 2730), (6, 2731), (6, 2740), (6, 2741);

-- 7.10 补齐 Role 7 (boss_viewer) 缺失的 2 个只读/导出权限
INSERT IGNORE INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES
(7, 2817), (7, 2820);

DROP PROCEDURE IF EXISTS cl_add_column_if_missing;
SET FOREIGN_KEY_CHECKS = 1;
