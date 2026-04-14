-- ----------------------------
-- 样例数据脚本 (Sample Data)
-- 包含：仓库、库位、客户、供应商、商品
-- 业务：入库、出库、库存、盘点、调拨
-- 财务：应收、应付
-- 注意：脚本包含删除语句，请谨慎执行
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 0;

-- 1. 仓库档案 (MdWarehouse)
-- 删除可能存在的冲突数据（按编码）
DELETE FROM md_warehouse WHERE warehouse_code IN ('WH001', 'WH002');
INSERT INTO md_warehouse (warehouse_id, warehouse_code, warehouse_name, status, del_flag, create_by, create_time, remark) VALUES 
(100, 'WH001', '主仓库', '0', '0', 'admin', NOW(), '系统默认主仓库'),
(101, 'WH002', '门店仓', '0', '0', 'admin', NOW(), '分店仓库');

-- 2. 库位档案 (MdLocation)
DELETE FROM md_location WHERE location_code IN ('A-01-01', 'A-01-02', 'B-01-01', 'S-01-01');
INSERT INTO md_location (location_id, warehouse_id, location_code, location_name, status, del_flag, create_by, create_time, remark) VALUES
(1000, 100, 'A-01-01', 'A区1排1层', '0', '0', 'admin', NOW(), '标准库位'),
(1001, 100, 'A-01-02', 'A区1排2层', '0', '0', 'admin', NOW(), '标准库位'),
(1002, 100, 'B-01-01', 'B区1排1层', '0', '0', 'admin', NOW(), '冷藏库位'),
(2000, 101, 'S-01-01', '门店货架1', '0', '0', 'admin', NOW(), '门店展示区');

-- 3. 客户档案 (MdCustomer)
DELETE FROM md_customer WHERE customer_code IN ('CUST001');
INSERT INTO md_customer (customer_id, customer_code, customer_name, contact_person, contact_phone, address, status, del_flag, create_by, create_time) VALUES
(100, 'CUST001', '示例客户有限公司', '张三', '13800138000', '北京市朝阳区科技园', '0', '0', 'admin', NOW());

-- 4. 供应商档案 (MdSupplier)
DELETE FROM md_supplier WHERE supplier_code IN ('SUPP001');
INSERT INTO md_supplier (supplier_id, supplier_code, supplier_name, contact_person, contact_phone, address, status, del_flag, create_by, create_time) VALUES
(100, 'SUPP001', '示例供应商有限公司', '李四', '13900139000', '上海市浦东新区工业园', '0', '0', 'admin', NOW());

-- 5. 商品档案 (MdProduct)
DELETE FROM md_product WHERE product_code IN ('PROD001', 'PROD002');
INSERT INTO md_product (product_id, product_code, product_name, unit_name, category_name, sale_price, cost_price, status, del_flag, create_by, create_time, remark) VALUES
(100, 'PROD001', '无线鼠标', '个', '电子产品', 99.00, 50.00, '0', '0', 'admin', NOW(), '罗技无线鼠标'),
(101, 'PROD002', '机械键盘', '把', '电子产品', 299.00, 150.00, '0', '0', 'admin', NOW(), '樱桃轴机械键盘');

-- 6. 入库单 (WmsInbound)
DELETE FROM wms_inbound WHERE inbound_no IN ('IN202310010001');
INSERT INTO wms_inbound (inbound_id, inbound_no, inbound_type, supplier_id, warehouse_id, total_qty, total_amount, status, create_by, create_time, remark) VALUES
(100, 'IN202310010001', 'purchase', 100, 100, 100, 5000.00, '1', 'admin', NOW(), '初始采购入库');

-- 7. 入库单明细 (WmsInboundItem)
DELETE FROM wms_inbound_item WHERE inbound_item_id IN (100);
INSERT INTO wms_inbound_item (inbound_item_id, inbound_id, product_id, location_id, batch_no, quantity, price, amount) VALUES
(100, 100, 100, 1000, 'BATCH20231001', 100, 50.00, 5000.00);

-- 8. 库存 (WmsStock)
-- 先按ID删除，再按唯一键删除可能存在的冲突数据
DELETE FROM wms_stock WHERE stock_id IN (100, 101);
DELETE FROM wms_stock WHERE warehouse_id = 100 AND product_id = 100 AND location_id = 1000 AND batch_no = 'BATCH20231001';
DELETE FROM wms_stock WHERE warehouse_id = 101 AND product_id = 100 AND location_id = 2000 AND batch_no = 'BATCH20231001';

INSERT INTO wms_stock (stock_id, warehouse_id, product_id, location_id, batch_no, quantity, locked_quantity, warning_min_qty, warning_max_qty, create_by, create_time) VALUES
(100, 100, 100, 1000, 'BATCH20231001', 85, 0, 10, 1000, 'admin', NOW());
INSERT INTO wms_stock (stock_id, warehouse_id, product_id, location_id, batch_no, quantity, locked_quantity, warning_min_qty, warning_max_qty, create_by, create_time) VALUES
(101, 101, 100, 2000, 'BATCH20231001', 5, 0, 0, 100, 'admin', NOW());

-- 9. 出库单 (WmsOutbound)
DELETE FROM wms_outbound WHERE outbound_no IN ('OUT202310020001');
INSERT INTO wms_outbound (outbound_id, outbound_no, outbound_type, customer_id, warehouse_id, total_qty, total_amount, status, create_by, create_time, remark) VALUES
(100, 'OUT202310020001', 'sale', 100, 100, 10, 990.00, '1', 'admin', NOW(), '销售出库测试');

-- 10. 出库单明细 (WmsOutboundItem)
DELETE FROM wms_outbound_item WHERE outbound_item_id IN (100);
INSERT INTO wms_outbound_item (outbound_item_id, outbound_id, product_id, location_id, batch_no, quantity, price, amount) VALUES
(100, 100, 100, 1000, 'BATCH20231001', 10, 99.00, 990.00);

-- 11. 调拨单 (WmsTransfer)
DELETE FROM wms_transfer WHERE transfer_no IN ('TR202310040001');
INSERT INTO wms_transfer (transfer_id, transfer_no, out_warehouse_id, in_warehouse_id, total_qty, total_amount, status, create_by, create_time, remark) VALUES
(100, 'TR202310040001', 100, 101, 5, 250.00, '1', 'admin', NOW(), '总仓调门店');

-- 12. 调拨明细 (WmsTransferItem)
DELETE FROM wms_transfer_item WHERE transfer_item_id IN (100);
INSERT INTO wms_transfer_item (transfer_item_id, transfer_id, product_id, out_location_id, in_location_id, batch_no, quantity, price, amount) VALUES
(100, 100, 100, 1000, 2000, 'BATCH20231001', 5, 50.00, 250.00);

-- 13. 盘点单 (WmsInventoryCheck)
DELETE FROM wms_inventory_check WHERE check_no IN ('CHK202310030001');
INSERT INTO wms_inventory_check (check_id, check_no, warehouse_id, check_status, remark, create_by, create_time) VALUES
(100, 'CHK202310030001', 100, '0', '月度盘点', 'admin', NOW());

-- 14. 盘点明细 (WmsInventoryCheckItem)
DELETE FROM wms_inventory_check_item WHERE check_item_id IN (100);
INSERT INTO wms_inventory_check_item (check_item_id, check_id, product_id, location_id, batch_no, stock_qty, actual_qty, diff_qty) VALUES
(100, 100, 100, 1000, 'BATCH20231001', 85, 85, 0);

-- 15. 应付账款 (FinPayable)
DELETE FROM fin_payable WHERE payable_no IN ('PAY202310010001');
INSERT INTO fin_payable (payable_id, payable_no, supplier_id, bill_type, bill_id, amount, paid_amount, status, due_date, create_by, create_time) VALUES
(100, 'PAY202310010001', 100, 'inbound', 100, 5000.00, 0.00, '0', DATE_ADD(NOW(), INTERVAL 30 DAY), 'admin', NOW());

-- 16. 应收账款 (FinReceivable)
DELETE FROM fin_receivable WHERE receivable_no IN ('REC202310020001');
INSERT INTO fin_receivable (receivable_id, receivable_no, customer_id, bill_type, bill_id, amount, received_amount, status, due_date, create_by, create_time) VALUES
(100, 'REC202310020001', 100, 'outbound', 100, 990.00, 0.00, '0', DATE_ADD(NOW(), INTERVAL 30 DAY), 'admin', NOW());

SET FOREIGN_KEY_CHECKS = 1;
