-- Phase 3 incremental upgrade for canglian standalone database
-- 1. Add action permissions for Data Backup (under menu_id 2750)

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2791, '创建备份', 2750, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:dataBackup:add', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2791);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2792, '下载备份', 2750, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:dataBackup:download', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2792);

INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT 2793, '删除备份', 2750, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'business:dataBackup:remove', '#', 'admin', NOW(), '', NULL, ''
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2793);

INSERT IGNORE INTO sys_role_menu (role_id, menu_id) VALUES
(1, 2791), (1, 2792), (1, 2793),
(2, 2791), (2, 2792);
