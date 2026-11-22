-- =====================================================================
-- Canglian ERP Phase 4 Production-Readiness Incremental Upgrade Script
-- Covers: P4-1 (Security Config), P4-2 (Auto Backup Job & Perms),
--         P4-3 (Return Red AR/AP Columns & Indexes),
--         P4-4 (Report Export Perm), P4-5 (Cost Method Config & Perms)
-- =====================================================================

-- 1. P4-3: AR/AP bill_direction & source uniqueness + return link columns
UPDATE fin_receivable SET source_bill_type = NULL WHERE source_bill_type = '';
UPDATE fin_payable SET source_bill_type = NULL WHERE source_bill_type = '';

SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'fin_receivable' AND COLUMN_NAME = 'bill_direction');
SET @sql = IF(@col_exists = 0, "ALTER TABLE fin_receivable ADD COLUMN bill_direction char(1) NOT NULL DEFAULT '1' COMMENT '单据方向(1正向应收 2红字退货冲减)' AFTER amount", 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'fin_payable' AND COLUMN_NAME = 'bill_direction');
SET @sql = IF(@col_exists = 0, "ALTER TABLE fin_payable ADD COLUMN bill_direction char(1) NOT NULL DEFAULT '1' COMMENT '单据方向(1正向应付 2红字退货冲减)' AFTER amount", 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists = (SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'fin_receivable' AND INDEX_NAME = 'uk_fin_receivable_source');
SET @sql = IF(@idx_exists = 0, 'ALTER TABLE fin_receivable ADD UNIQUE KEY uk_fin_receivable_source (source_bill_type, source_bill_id)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists = (SELECT COUNT(*) FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'fin_payable' AND INDEX_NAME = 'uk_fin_payable_source');
SET @sql = IF(@idx_exists = 0, 'ALTER TABLE fin_payable ADD UNIQUE KEY uk_fin_payable_source (source_bill_type, source_bill_id)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'wms_sale_return' AND COLUMN_NAME = 'receivable_id');
SET @sql = IF(@col_exists = 0, "ALTER TABLE wms_sale_return ADD COLUMN receivable_id bigint DEFAULT NULL COMMENT '关联红字应收单ID' AFTER total_amount", 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'wms_purchase_return' AND COLUMN_NAME = 'payable_id');
SET @sql = IF(@col_exists = 0, "ALTER TABLE wms_purchase_return ADD COLUMN payable_id bigint DEFAULT NULL COMMENT '关联红字应付单ID' AFTER total_amount", 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 2. P4-1 & P4-5: System configuration parameters
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT '成本核算-全局默认算法', 'canglian.cost.defaultMethod', 'moving', 'Y', 'admin', NOW(), '全局库存成本核算算法：moving(移动加权平均) / fifo(先进先出)'
WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'canglian.cost.defaultMethod');

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
SELECT '安全策略-首次登录强制修改初始密码', 'canglian.security.forcePasswordChange', 'false', 'Y', 'admin', NOW(), '设为true时未修改过初始密码的账号登录后将强制跳转改密（生产环境建议开启）'
WHERE NOT EXISTS (SELECT 1 FROM sys_config WHERE config_key = 'canglian.security.forcePasswordChange');

-- 3. P4-2: Quartz daily auto backup job (02:00 AM daily, retain 30 days)
INSERT INTO sys_job (job_id, job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, remark)
SELECT 4, '仓链数据库每日自动备份', 'SYSTEM', 'canglianBackupTask.autoBackup(30)', '0 0 2 * * ?', '3', '1', '0', 'admin', NOW(), '每日凌晨2点自动执行全库逻辑SQL备份并清理30天前过期备份'
WHERE NOT EXISTS (SELECT 1 FROM sys_job WHERE invoke_target LIKE 'canglianBackupTask.autoBackup%');

-- 4. P4-2 / P4-4 / P4-5: Action permissions
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2794, '校验备份', 2750, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:dataBackup:verify', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2794);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2795, '受控恢复备份', 2750, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:dataBackup:restore', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2795);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2796, '报表导出', 2210, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:report:export', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2796);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2797, '成本期末重算', 2209, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:costCalculate:recalculate', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2797);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2798, '受控成本调价', 2209, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:costCalculate:adjust', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2798);

INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES
(1, 2794), (1, 2795), (1, 2796), (1, 2797), (1, 2798),
(2, 2794), (2, 2796), (2, 2797);
