-- MySQL dump 10.13  Distrib 8.0.30, for Win64 (x86_64)
--
-- Host: 192.168.2.155    Database: canglian
-- ------------------------------------------------------
-- Server version	8.0.32

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `fin_account_subject`
--

DROP TABLE IF EXISTS `fin_account_subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_account_subject` (
  `subject_id` bigint NOT NULL AUTO_INCREMENT COMMENT '科目id',
  `subject_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '科目编码',
  `subject_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '科目名称',
  `subject_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '科目类型',
  `balance_direction` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '余额方向',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`subject_id`),
  UNIQUE KEY `uk_fin_account_subject_code` (`subject_code`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会计科目';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_account_subject`
--

LOCK TABLES `fin_account_subject` WRITE;
/*!40000 ALTER TABLE `fin_account_subject` DISABLE KEYS */;
INSERT INTO `fin_account_subject` VALUES (1,'1001','库存现金','asset','debit','0','admin','2026-11-22 10:36:54','',NULL,'星辰轻量总账预置科目'),(2,'1002','银行存款','asset','debit','0','admin','2026-11-22 10:36:54','',NULL,'星辰轻量总账预置科目'),(3,'1122','应收账款','asset','debit','0','admin','2026-11-22 10:36:54','',NULL,'星辰轻量总账预置科目'),(4,'2202','应付账款','liability','credit','0','admin','2026-11-22 10:36:54','',NULL,'星辰轻量总账预置科目'),(5,'6001','主营业务收入','income','credit','0','admin','2026-11-22 10:36:54','',NULL,'星辰轻量总账预置科目'),(6,'6401','主营业务成本','cost','debit','0','admin','2026-11-22 10:36:54','',NULL,'星辰轻量总账预置科目'),(9,'1405','库存商品','asset','debit','0','admin','2026-11-22 20:55:28','',NULL,'仓链标准科目'),(10,'2221','应交税费','liability','credit','0','admin','2026-11-22 20:55:28','',NULL,'仓链标准科目'),(11,'5301','营业外收入','profit_loss','credit','0','admin','2026-11-22 20:55:28','',NULL,'仓链标准科目-盘盈等'),(12,'5711','营业外支出','profit_loss','debit','0','admin','2026-11-22 20:55:28','',NULL,'仓链标准科目-盘亏等'),(13,'6602','管理费用','profit_loss','debit','0','admin','2026-11-22 20:55:28','',NULL,'仓链标准科目'),(14,'6603','财务费用','profit_loss','debit','0','admin','2026-11-22 20:55:28','',NULL,'仓链标准科目-银行手续费等'),(15,'6701','信用减值损失','profit_loss','debit','0','admin','2026-11-22 20:55:28','',NULL,'仓链标准科目-坏账核销与收回');
/*!40000 ALTER TABLE `fin_account_subject` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_auxiliary_item`
--

DROP TABLE IF EXISTS `fin_auxiliary_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_auxiliary_item` (
  `auxiliary_id` bigint NOT NULL AUTO_INCREMENT COMMENT '辅助核算id',
  `auxiliary_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '辅助核算类型',
  `item_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目编码',
  `item_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '项目名称',
  `relation_id` bigint DEFAULT NULL COMMENT '关联业务id',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`auxiliary_id`),
  UNIQUE KEY `uk_fin_auxiliary_item` (`auxiliary_type`,`item_code`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='辅助核算项目';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_auxiliary_item`
--

LOCK TABLES `fin_auxiliary_item` WRITE;
/*!40000 ALTER TABLE `fin_auxiliary_item` DISABLE KEYS */;
INSERT INTO `fin_auxiliary_item` VALUES (1,'customer','cus001','星火客户',1,'0','admin','2026-11-22 10:36:54','admin','2026-11-22 19:02:32','客户辅助核算'),(2,'customer','cus002','晨光客户',2,'0','admin','2026-11-22 10:36:54','admin','2026-11-22 19:02:32','客户辅助核算'),(3,'customer','CUST001','示例客户有限公司',100,'0','admin','2026-11-22 10:36:54','admin','2026-11-22 19:02:32','客户辅助核算'),(4,'supplier','sup001','海川供应商',1,'0','admin','2026-11-22 10:36:54','admin','2026-11-22 19:02:32','供应商辅助核算'),(5,'supplier','sup002','恒星供应商',2,'0','admin','2026-11-22 10:36:54','admin','2026-11-22 19:02:32','供应商辅助核算'),(6,'supplier','SUPP001','示例供应商有限公司',100,'0','admin','2026-11-22 10:36:54','admin','2026-11-22 19:02:32','供应商辅助核算'),(7,'product','p001','螺丝刀',1,'0','admin','2026-11-22 10:36:54','',NULL,'商品辅助核算'),(8,'product','p002','扳手',2,'0','admin','2026-11-22 10:36:54','',NULL,'商品辅助核算'),(9,'product','p003','纸箱',3,'0','admin','2026-11-22 10:36:54','',NULL,'商品辅助核算'),(10,'product','PROD001','无线鼠标',100,'0','admin','2026-11-22 10:36:54','',NULL,'商品辅助核算'),(11,'product','PROD002','机械键盘',101,'0','admin','2026-11-22 10:36:54','',NULL,'商品辅助核算'),(14,'warehouse','wh001','主仓库',1,'0','admin','2026-11-22 18:59:52','admin','2026-11-22 19:02:32','主数据自动同步'),(15,'warehouse','wh002','华东仓',2,'0','admin','2026-11-22 18:59:52','admin','2026-11-22 19:02:32','主数据自动同步'),(16,'warehouse','WH100','示例主仓库',100,'0','admin','2026-11-22 18:59:52','admin','2026-11-22 19:02:32','主数据自动同步'),(17,'warehouse','WH101','示例门店仓',101,'0','admin','2026-11-22 18:59:52','admin','2026-11-22 19:02:32','主数据自动同步');
/*!40000 ALTER TABLE `fin_auxiliary_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_bad_debt`
--

DROP TABLE IF EXISTS `fin_bad_debt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_bad_debt` (
  `bad_debt_id` bigint NOT NULL AUTO_INCREMENT COMMENT '坏账id',
  `bad_debt_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '坏账单号',
  `receivable_id` bigint NOT NULL COMMENT '应收id',
  `customer_id` bigint NOT NULL COMMENT '客户id',
  `amount` decimal(18,2) DEFAULT '0.00' COMMENT '坏账金额',
  `bad_debt_date` date DEFAULT NULL COMMENT '坏账日期',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '原因',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0草稿 1已确认）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `recovered_amount` decimal(18,2) DEFAULT '0.00' COMMENT '已收回坏账金额',
  `recover_time` datetime DEFAULT NULL COMMENT '最近收回时间',
  `recover_account_id` bigint DEFAULT NULL COMMENT '收回入账资金账户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '所属部门ID',
  `owner_user_id` bigint DEFAULT NULL COMMENT '归属人ID',
  PRIMARY KEY (`bad_debt_id`),
  UNIQUE KEY `uk_fin_bad_debt_no` (`bad_debt_no`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='坏账表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_bad_debt`
--

LOCK TABLES `fin_bad_debt` WRITE;
/*!40000 ALTER TABLE `fin_bad_debt` DISABLE KEYS */;
INSERT INTO `fin_bad_debt` VALUES (1,'bd20260101001',1,1,50.00,'2026-11-22','长期未收','1','admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例坏账',0.00,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `fin_bad_debt` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_cost_account`
--

DROP TABLE IF EXISTS `fin_cost_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_cost_account` (
  `cost_account_id` bigint NOT NULL AUTO_INCREMENT COMMENT '成本账户id',
  `product_id` bigint NOT NULL COMMENT '商品id',
  `warehouse_id` bigint NOT NULL COMMENT '仓库id',
  `cost_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '成本算法',
  `total_qty` decimal(18,2) DEFAULT '0.00' COMMENT '总数量',
  `total_amount` decimal(18,2) DEFAULT '0.00' COMMENT '总金额',
  `avg_cost` decimal(18,2) DEFAULT '0.00' COMMENT '平均成本',
  `last_cost_price` decimal(18,2) DEFAULT '0.00' COMMENT '最近入库成本单价',
  `last_cost_qty` decimal(18,2) DEFAULT '0.00' COMMENT '最近入库数量',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`cost_account_id`),
  UNIQUE KEY `uk_fin_cost_account` (`product_id`,`warehouse_id`,`cost_method`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='成本账户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_cost_account`
--

LOCK TABLES `fin_cost_account` WRITE;
/*!40000 ALTER TABLE `fin_cost_account` DISABLE KEYS */;
INSERT INTO `fin_cost_account` VALUES (1,1,1,'moving',30.00,450.00,15.00,15.00,50.00,'admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例成本账户');
/*!40000 ALTER TABLE `fin_cost_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_cost_layer`
--

DROP TABLE IF EXISTS `fin_cost_layer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_cost_layer` (
  `cost_layer_id` bigint NOT NULL AUTO_INCREMENT COMMENT '成本分层id',
  `cost_account_id` bigint NOT NULL COMMENT '成本账户id',
  `product_id` bigint NOT NULL COMMENT '商品id',
  `warehouse_id` bigint NOT NULL COMMENT '仓库id',
  `quantity` decimal(18,2) DEFAULT '0.00' COMMENT '入库数量',
  `remaining_qty` decimal(18,2) DEFAULT '0.00' COMMENT '剩余数量',
  `price` decimal(18,2) DEFAULT '0.00' COMMENT '单价',
  `amount` decimal(18,2) DEFAULT '0.00' COMMENT '金额',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`cost_layer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='成本分层表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_cost_layer`
--

LOCK TABLES `fin_cost_layer` WRITE;
/*!40000 ALTER TABLE `fin_cost_layer` DISABLE KEYS */;
INSERT INTO `fin_cost_layer` VALUES (1,1,1,1,50.00,30.00,15.00,750.00,'admin','2026-11-22 10:36:50','样例成本分层');
/*!40000 ALTER TABLE `fin_cost_layer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_cost_log`
--

DROP TABLE IF EXISTS `fin_cost_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_cost_log` (
  `cost_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '成本流水id',
  `cost_account_id` bigint NOT NULL COMMENT '成本账户id',
  `product_id` bigint NOT NULL COMMENT '商品id',
  `warehouse_id` bigint NOT NULL COMMENT '仓库id',
  `bill_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '单据类型',
  `bill_id` bigint DEFAULT NULL COMMENT '单据id',
  `in_out` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '出入库标识（1入库 2出库）',
  `quantity` decimal(18,2) DEFAULT '0.00' COMMENT '数量',
  `price` decimal(18,2) DEFAULT '0.00' COMMENT '单价',
  `amount` decimal(18,2) DEFAULT '0.00' COMMENT '金额',
  `cost_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '成本算法',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`cost_log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='成本流水表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_cost_log`
--

LOCK TABLES `fin_cost_log` WRITE;
/*!40000 ALTER TABLE `fin_cost_log` DISABLE KEYS */;
INSERT INTO `fin_cost_log` VALUES (1,1,1,1,'inbound',1,'1',50.00,15.00,750.00,'moving','admin','2026-11-22 10:36:50','样例成本流水');
/*!40000 ALTER TABLE `fin_cost_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_expense`
--

DROP TABLE IF EXISTS `fin_expense`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_expense` (
  `expense_id` bigint NOT NULL AUTO_INCREMENT COMMENT '费用id',
  `expense_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '费用单号',
  `expense_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '费用类型',
  `amount` decimal(18,2) DEFAULT '0.00' COMMENT '费用金额',
  `expense_date` date DEFAULT NULL COMMENT '费用日期',
  `pay_channel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '支付方式',
  `fund_account_id` bigint DEFAULT NULL COMMENT '资金账户id',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0草稿 1已确认）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `dept_id` bigint DEFAULT NULL COMMENT '所属部门ID',
  `owner_user_id` bigint DEFAULT NULL COMMENT '归属人ID',
  PRIMARY KEY (`expense_id`),
  UNIQUE KEY `uk_fin_expense_no` (`expense_no`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='费用单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_expense`
--

LOCK TABLES `fin_expense` WRITE;
/*!40000 ALTER TABLE `fin_expense` DISABLE KEYS */;
INSERT INTO `fin_expense` VALUES (1,'ex20260101001','logistics',120.00,'2026-11-22','bank',1,'1','admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例费用',NULL,NULL);
/*!40000 ALTER TABLE `fin_expense` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_expense_category`
--

DROP TABLE IF EXISTS `fin_expense_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_expense_category` (
  `category_id` bigint NOT NULL AUTO_INCREMENT COMMENT '费用类别id',
  `parent_id` bigint DEFAULT '0' COMMENT '父类别id',
  `ancestors` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '祖级列表',
  `category_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '费用类别名称',
  `category_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '费用类别编码',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='费用类别表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_expense_category`
--

LOCK TABLES `fin_expense_category` WRITE;
/*!40000 ALTER TABLE `fin_expense_category` DISABLE KEYS */;
INSERT INTO `fin_expense_category` VALUES (1,0,'0','运费','logistics',1,'0','0','admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例费用类别'),(2,0,'0','办公费','office',2,'0','0','admin','2026-11-22 20:55:28','',NULL,'日常办公耗材及用品'),(3,0,'0','差旅费','travel',3,'0','0','admin','2026-11-22 20:55:28','',NULL,'业务差旅及交通住宿'),(4,0,'0','房租水电','rent',4,'0','0','admin','2026-11-22 20:55:28','',NULL,'仓库与办公场地租金水电'),(5,0,'0','推广费','marketing',5,'0','0','admin','2026-11-22 20:55:28','',NULL,'销售渠道推广与广告费'),(6,0,'0','其他费用','other',6,'0','0','admin','2026-11-22 20:55:28','',NULL,'其他期间费用');
/*!40000 ALTER TABLE `fin_expense_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_fiscal_period`
--

DROP TABLE IF EXISTS `fin_fiscal_period`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_fiscal_period` (
  `period_id` bigint NOT NULL AUTO_INCREMENT COMMENT '会计期间id',
  `period_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '期间编码',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `close_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'open' COMMENT '结账状态',
  `close_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '结账人',
  `close_time` datetime DEFAULT NULL COMMENT '结账时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`period_id`),
  UNIQUE KEY `uk_fin_fiscal_period_code` (`period_code`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会计期间';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_fiscal_period`
--

LOCK TABLES `fin_fiscal_period` WRITE;
/*!40000 ALTER TABLE `fin_fiscal_period` DISABLE KEYS */;
INSERT INTO `fin_fiscal_period` VALUES (1,'2026-01','2026-01-01','2026-01-31','open',NULL,NULL,'admin','2026-11-22 10:36:54','admin','2026-11-22 11:43:34','星辰轻量总账预置期间'),(2,'2026-02','2026-02-01','2026-02-28','open',NULL,NULL,'admin','2026-11-22 10:36:54','',NULL,'星辰轻量总账预置期间'),(3,'2026-03','2026-03-01','2026-03-31','open',NULL,NULL,'admin','2026-11-22 10:36:54','',NULL,'星辰轻量总账预置期间'),(4,'2026-04','2026-04-01','2026-04-30','open',NULL,NULL,'admin','2026-11-22 10:36:54','',NULL,'星辰轻量总账预置期间'),(5,'2026-05','2026-05-01','2026-05-31','open',NULL,NULL,'admin','2026-11-22 10:36:54','',NULL,'星辰轻量总账预置期间'),(6,'2026-06','2026-06-01','2026-06-30','open',NULL,NULL,'admin','2026-11-22 10:36:54','',NULL,'星辰轻量总账预置期间'),(7,'2026-07','2026-07-01','2026-07-31','open',NULL,NULL,'admin','2026-11-22 10:36:54','',NULL,'星辰轻量总账预置期间'),(8,'2026-08','2026-08-01','2026-08-31','open',NULL,NULL,'admin','2026-11-22 10:36:54','',NULL,'星辰轻量总账预置期间'),(9,'2026-09','2026-09-01','2026-09-30','open',NULL,NULL,'admin','2026-11-22 10:36:54','',NULL,'星辰轻量总账预置期间'),(10,'2026-10','2026-10-01','2026-10-31','open',NULL,NULL,'admin','2026-11-22 10:36:54','',NULL,'星辰轻量总账预置期间'),(11,'2026-11','2026-11-01','2026-11-30','open',NULL,NULL,'admin','2026-11-22 10:36:54','',NULL,'星辰轻量总账预置期间'),(12,'2026-12','2026-12-01','2026-12-31','open',NULL,NULL,'admin','2026-11-22 10:36:54','',NULL,'星辰轻量总账预置期间');
/*!40000 ALTER TABLE `fin_fiscal_period` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_fund_account`
--

DROP TABLE IF EXISTS `fin_fund_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_fund_account` (
  `account_id` bigint NOT NULL AUTO_INCREMENT COMMENT '账户id',
  `account_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账户编号',
  `account_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '账户名称',
  `account_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '账户类型',
  `bank_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '开户行',
  `bank_account` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '银行账号',
  `currency` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '币种',
  `balance` decimal(18,2) DEFAULT '0.00' COMMENT '余额',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `uk_fin_fund_account_no` (`account_no`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='资金账户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_fund_account`
--

LOCK TABLES `fin_fund_account` WRITE;
/*!40000 ALTER TABLE `fin_fund_account` DISABLE KEYS */;
INSERT INTO `fin_fund_account` VALUES (1,'fa001','基本户','bank','工商银行','6222000000000000','cny',50000.00,'0','admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例资金账户');
/*!40000 ALTER TABLE `fin_fund_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_fund_log`
--

DROP TABLE IF EXISTS `fin_fund_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_fund_log` (
  `fund_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '资金流水ID',
  `account_id` bigint NOT NULL COMMENT '资金账户ID',
  `biz_type` varchar(32) NOT NULL COMMENT '业务类型(receipt/payment/expense/transfer_in/transfer_out/refund/bad_debt_recover/unconfirm)',
  `bill_id` bigint DEFAULT NULL COMMENT '来源单据ID',
  `bill_no` varchar(64) DEFAULT NULL COMMENT '来源单据号',
  `direction` char(1) NOT NULL COMMENT '收支方向(1收入 2支出)',
  `change_amount` decimal(18,2) NOT NULL COMMENT '变动金额(绝对值)',
  `before_balance` decimal(18,2) NOT NULL COMMENT '变动前余额',
  `after_balance` decimal(18,2) NOT NULL COMMENT '变动后余额',
  `biz_date` date DEFAULT NULL COMMENT '业务日期',
  `counterparty_name` varchar(128) DEFAULT NULL COMMENT '往来对象/说明',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '操作人',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`fund_log_id`),
  KEY `idx_fund_log_account` (`account_id`,`create_time`),
  KEY `idx_fund_log_bill` (`biz_type`,`bill_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='资金账户逐笔流水台账';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_fund_log`
--

LOCK TABLES `fin_fund_log` WRITE;
/*!40000 ALTER TABLE `fin_fund_log` DISABLE KEYS */;
INSERT INTO `fin_fund_log` VALUES (10,1,'expense',1,'ex20260101001','2',120.00,50120.00,50000.00,'2026-11-22','运费','样例费用支出入账流水','admin','2026-11-22 10:36:50');
/*!40000 ALTER TABLE `fin_fund_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_fund_transfer`
--

DROP TABLE IF EXISTS `fin_fund_transfer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_fund_transfer` (
  `transfer_id` bigint NOT NULL AUTO_INCREMENT COMMENT '互转单ID',
  `transfer_no` varchar(64) NOT NULL COMMENT '互转单号',
  `out_account_id` bigint NOT NULL COMMENT '转出账户ID',
  `in_account_id` bigint NOT NULL COMMENT '转入账户ID',
  `amount` decimal(18,2) NOT NULL COMMENT '转账金额',
  `fee_amount` decimal(18,2) DEFAULT '0.00' COMMENT '手续费',
  `transfer_date` date DEFAULT NULL COMMENT '转账日期',
  `status` char(1) DEFAULT '0' COMMENT '状态(0草稿 1已确认)',
  `audit_by` varchar(64) DEFAULT NULL COMMENT '确认人',
  `audit_time` datetime DEFAULT NULL COMMENT '确认时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`transfer_id`),
  UNIQUE KEY `uk_fund_transfer_no` (`transfer_no`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='资金账户内部互转单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_fund_transfer`
--

LOCK TABLES `fin_fund_transfer` WRITE;
/*!40000 ALTER TABLE `fin_fund_transfer` DISABLE KEYS */;
/*!40000 ALTER TABLE `fin_fund_transfer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_invoice`
--

DROP TABLE IF EXISTS `fin_invoice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_invoice` (
  `invoice_id` bigint NOT NULL AUTO_INCREMENT COMMENT '发票登记id',
  `invoice_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发票登记号',
  `invoice_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '发票类型（sale销售 purchase采购）',
  `customer_id` bigint DEFAULT NULL COMMENT '客户id',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商id',
  `source_bill_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '来源单据类型',
  `source_bill_id` bigint DEFAULT NULL COMMENT '来源单据id',
  `source_bill_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '来源单据号',
  `business_date` date DEFAULT NULL COMMENT '业务日期',
  `invoice_date` date DEFAULT NULL COMMENT '开票日期',
  `invoice_amount` decimal(18,2) DEFAULT '0.00' COMMENT '含税金额',
  `tax_rate` decimal(10,4) DEFAULT '0.0000' COMMENT '税率',
  `tax_amount` decimal(18,2) DEFAULT '0.00' COMMENT '税额',
  `untaxed_amount` decimal(18,2) DEFAULT '0.00' COMMENT '不含税金额',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0草稿 1已确认 2已关闭）',
  `biz_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'draft' COMMENT '业务状态',
  `confirm_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '确认人',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `invoice_kind` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'blue' COMMENT '发票红蓝类型(blue蓝票 red红票)',
  `origin_invoice_id` bigint DEFAULT NULL COMMENT '被红冲原蓝票ID',
  `dept_id` bigint DEFAULT NULL COMMENT '所属部门ID',
  `owner_user_id` bigint DEFAULT NULL COMMENT '归属人ID',
  PRIMARY KEY (`invoice_id`),
  UNIQUE KEY `uk_fin_invoice_no` (`invoice_no`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='发票登记表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_invoice`
--

LOCK TABLES `fin_invoice` WRITE;
/*!40000 ALTER TABLE `fin_invoice` DISABLE KEYS */;
/*!40000 ALTER TABLE `fin_invoice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_offset_log`
--

DROP TABLE IF EXISTS `fin_offset_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_offset_log` (
  `offset_id` bigint NOT NULL AUTO_INCREMENT COMMENT '对冲流水ID',
  `offset_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型(receivable应收/payable应付)',
  `positive_bill_id` bigint NOT NULL COMMENT '正向往来单ID',
  `red_bill_id` bigint NOT NULL COMMENT '红字往来单ID',
  `offset_amount` decimal(16,2) NOT NULL COMMENT '对冲金额',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '状态(0生效 1已撤销)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作人',
  `create_time` datetime DEFAULT NULL COMMENT '对冲时间',
  `reverse_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '撤销人',
  `reverse_time` datetime DEFAULT NULL COMMENT '撤销时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`offset_id`),
  KEY `idx_fin_offset_pos` (`offset_type`,`positive_bill_id`,`status`),
  KEY `idx_fin_offset_red` (`offset_type`,`red_bill_id`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='往来红蓝对冲台账';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_offset_log`
--

LOCK TABLES `fin_offset_log` WRITE;
/*!40000 ALTER TABLE `fin_offset_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `fin_offset_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_payable`
--

DROP TABLE IF EXISTS `fin_payable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_payable` (
  `payable_id` bigint NOT NULL AUTO_INCREMENT COMMENT '应付id',
  `payable_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应付单号',
  `supplier_id` bigint NOT NULL COMMENT '供应商id',
  `bill_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '单据类型',
  `bill_id` bigint DEFAULT NULL COMMENT '单据id',
  `source_bill_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '来源单据类型',
  `source_bill_id` bigint DEFAULT NULL COMMENT '来源单据id',
  `source_bill_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '来源单据号',
  `business_date` date DEFAULT NULL COMMENT '业务日期',
  `amount` decimal(18,2) DEFAULT '0.00' COMMENT '应付金额',
  `bill_direction` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '单据方向(1正向应付 2红字退货冲减)',
  `paid_amount` decimal(18,2) DEFAULT '0.00' COMMENT '已付金额',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0未付 1部分 2已付）',
  `biz_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'draft' COMMENT '业务状态',
  `due_date` date DEFAULT NULL COMMENT '到期日期',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `dept_id` bigint DEFAULT NULL COMMENT '所属部门ID',
  `owner_user_id` bigint DEFAULT NULL COMMENT '归属人ID',
  PRIMARY KEY (`payable_id`),
  UNIQUE KEY `uk_fin_payable_no` (`payable_no`),
  UNIQUE KEY `uk_fin_payable_source` (`source_bill_type`,`source_bill_id`)
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='应付单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_payable`
--

LOCK TABLES `fin_payable` WRITE;
/*!40000 ALTER TABLE `fin_payable` DISABLE KEYS */;
INSERT INTO `fin_payable` VALUES (1,'ap20260101001',1,'inbound',1,NULL,NULL,'','2026-12-07',750.00,'1',300.00,'1','confirmed','2026-12-07','admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例应付',NULL,NULL),(100,'PAY202310010001',100,'inbound',100,NULL,NULL,'','2026-12-22',5000.00,'1',0.00,'0','confirmed','2026-12-22','admin','2026-11-22 10:36:51','',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `fin_payable` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_payment`
--

DROP TABLE IF EXISTS `fin_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_payment` (
  `payment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '付款单id',
  `payment_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '付款单号',
  `supplier_id` bigint NOT NULL COMMENT '供应商id',
  `payable_id` bigint DEFAULT NULL COMMENT '应付id',
  `amount` decimal(18,2) DEFAULT '0.00' COMMENT '付款金额',
  `payment_date` date DEFAULT NULL COMMENT '付款日期',
  `pay_channel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '付款方式',
  `fund_account_id` bigint DEFAULT NULL COMMENT '资金账户id',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '1' COMMENT '审核状态(0草稿 1已确认)',
  `biz_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'confirmed' COMMENT '业务状态(draft/confirmed)',
  `audit_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '审核人',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `dept_id` bigint DEFAULT NULL COMMENT '所属部门ID',
  `owner_user_id` bigint DEFAULT NULL COMMENT '归属人ID',
  PRIMARY KEY (`payment_id`),
  UNIQUE KEY `uk_fin_payment_no` (`payment_no`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='付款单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_payment`
--

LOCK TABLES `fin_payment` WRITE;
/*!40000 ALTER TABLE `fin_payment` DISABLE KEYS */;
INSERT INTO `fin_payment` VALUES (1,'pm20260101001',1,1,300.00,'2026-11-22','bank',NULL,'admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例付款','1','confirmed',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `fin_payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_receipt`
--

DROP TABLE IF EXISTS `fin_receipt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_receipt` (
  `receipt_id` bigint NOT NULL AUTO_INCREMENT COMMENT '收款单id',
  `receipt_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '收款单号',
  `customer_id` bigint NOT NULL COMMENT '客户id',
  `receivable_id` bigint DEFAULT NULL COMMENT '应收id',
  `amount` decimal(18,2) DEFAULT '0.00' COMMENT '收款金额',
  `receipt_date` date DEFAULT NULL COMMENT '收款日期',
  `pay_channel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '收款方式',
  `fund_account_id` bigint DEFAULT NULL COMMENT '资金账户id',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '1' COMMENT '审核状态(0草稿 1已确认)',
  `biz_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'confirmed' COMMENT '业务状态(draft/confirmed)',
  `audit_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '审核人',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `dept_id` bigint DEFAULT NULL COMMENT '所属部门ID',
  `owner_user_id` bigint DEFAULT NULL COMMENT '归属人ID',
  PRIMARY KEY (`receipt_id`),
  UNIQUE KEY `uk_fin_receipt_no` (`receipt_no`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='收款单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_receipt`
--

LOCK TABLES `fin_receipt` WRITE;
/*!40000 ALTER TABLE `fin_receipt` DISABLE KEYS */;
INSERT INTO `fin_receipt` VALUES (1,'rc20260101001',1,1,200.00,'2026-11-22','bank',NULL,'admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例收款','1','confirmed',NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `fin_receipt` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_receivable`
--

DROP TABLE IF EXISTS `fin_receivable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_receivable` (
  `receivable_id` bigint NOT NULL AUTO_INCREMENT COMMENT '应收id',
  `receivable_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '应收单号',
  `customer_id` bigint NOT NULL COMMENT '客户id',
  `bill_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '单据类型',
  `bill_id` bigint DEFAULT NULL COMMENT '单据id',
  `source_bill_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '来源单据类型',
  `source_bill_id` bigint DEFAULT NULL COMMENT '来源单据id',
  `source_bill_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '来源单据号',
  `business_date` date DEFAULT NULL COMMENT '业务日期',
  `amount` decimal(18,2) DEFAULT '0.00' COMMENT '应收金额',
  `bill_direction` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '1' COMMENT '单据方向(1正向应收 2红字退货冲减)',
  `received_amount` decimal(18,2) DEFAULT '0.00' COMMENT '已收金额',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0未收 1部分 2已收）',
  `biz_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'draft' COMMENT '业务状态',
  `due_date` date DEFAULT NULL COMMENT '到期日期',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `dept_id` bigint DEFAULT NULL COMMENT '所属部门ID',
  `owner_user_id` bigint DEFAULT NULL COMMENT '归属人ID',
  `bad_debt_amount` decimal(16,2) DEFAULT '0.00' COMMENT '已核销坏账金额',
  PRIMARY KEY (`receivable_id`),
  UNIQUE KEY `uk_fin_receivable_no` (`receivable_no`),
  UNIQUE KEY `uk_fin_receivable_source` (`source_bill_type`,`source_bill_id`)
) ENGINE=InnoDB AUTO_INCREMENT=115 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='应收单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_receivable`
--

LOCK TABLES `fin_receivable` WRITE;
/*!40000 ALTER TABLE `fin_receivable` DISABLE KEYS */;
INSERT INTO `fin_receivable` VALUES (1,'ar20260101001',1,'outbound',1,NULL,NULL,'','2026-12-22',560.00,'1',200.00,'1','confirmed','2026-12-22','admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例应收',NULL,NULL,0.00),(100,'REC202310020001',100,'outbound',100,NULL,NULL,'','2026-12-22',990.00,'1',0.00,'0','confirmed','2026-12-22','admin','2026-11-22 10:36:51','',NULL,NULL,NULL,NULL,0.00);
/*!40000 ALTER TABLE `fin_receivable` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_voucher_entry`
--

DROP TABLE IF EXISTS `fin_voucher_entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_voucher_entry` (
  `entry_id` bigint NOT NULL AUTO_INCREMENT COMMENT '分录ID',
  `voucher_event_id` bigint NOT NULL COMMENT '关联凭证事件ID',
  `voucher_no` varchar(64) NOT NULL COMMENT '凭证号',
  `entry_seq` int NOT NULL DEFAULT '1' COMMENT '分录行号',
  `direction` varchar(16) NOT NULL COMMENT '借贷方向(debit/credit)',
  `subject_code` varchar(32) NOT NULL COMMENT '科目编码',
  `subject_name` varchar(128) NOT NULL COMMENT '科目名称',
  `auxiliary_type` varchar(32) DEFAULT 'none' COMMENT '辅助类型',
  `auxiliary_name` varchar(128) DEFAULT '' COMMENT '辅助核算名称',
  `debit_amount` decimal(18,2) NOT NULL DEFAULT '0.00' COMMENT '借方金额',
  `credit_amount` decimal(18,2) NOT NULL DEFAULT '0.00' COMMENT '贷方金额',
  `summary` varchar(255) DEFAULT '' COMMENT '分录摘要',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`entry_id`),
  KEY `idx_voucher_entry_event` (`voucher_event_id`),
  KEY `idx_voucher_entry_no` (`voucher_no`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='复式记账凭证分录子表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_voucher_entry`
--

LOCK TABLES `fin_voucher_entry` WRITE;
/*!40000 ALTER TABLE `fin_voucher_entry` DISABLE KEYS */;
/*!40000 ALTER TABLE `fin_voucher_entry` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_voucher_event`
--

DROP TABLE IF EXISTS `fin_voucher_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_voucher_event` (
  `voucher_event_id` bigint NOT NULL AUTO_INCREMENT COMMENT '凭证事件id',
  `bill_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '单据类型',
  `bill_id` bigint NOT NULL COMMENT '单据id',
  `bill_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '单据号',
  `event_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '事件类型',
  `event_date` date DEFAULT NULL COMMENT '业务日期',
  `event_amount` decimal(18,2) DEFAULT '0.00' COMMENT '事件金额',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0待生成 1已生成）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `voucher_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '凭证号',
  `voucher_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '凭证状态',
  `voucher_date` datetime DEFAULT NULL COMMENT '凭证日期',
  `generated_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '生成凭证人',
  `generated_time` datetime DEFAULT NULL COMMENT '生成凭证时间',
  `writeback_status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '回写状态',
  `writeback_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '回写消息',
  `reverse_voucher_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '冲销凭证号',
  `reversed_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '冲销人',
  `reversed_time` datetime DEFAULT NULL COMMENT '冲销时间',
  PRIMARY KEY (`voucher_event_id`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='凭证事件表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_voucher_event`
--

LOCK TABLES `fin_voucher_event` WRITE;
/*!40000 ALTER TABLE `fin_voucher_event` DISABLE KEYS */;
INSERT INTO `fin_voucher_event` VALUES (56,'inbound',100,'IN202310010001','inbound_complete','2026-11-22',5000.00,'0','admin','2026-11-22 22:45:19','',NULL,'系统补生成采购入库凭证事件',NULL,'pending',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(57,'inbound',1,'inb20260101001','inbound_complete','2026-11-22',750.00,'0','admin','2026-11-22 22:45:19','',NULL,'系统补生成采购入库凭证事件',NULL,'pending',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(58,'receipt',1,'rc20260101001','receipt_confirm','2026-11-22',200.00,'0','admin','2026-11-22 22:45:19','',NULL,'系统补生成收款/退款凭证事件',NULL,'pending',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(59,'payment',1,'pm20260101001','payment_confirm','2026-11-22',300.00,'0','admin','2026-11-22 22:45:19','',NULL,'系统补生成付款凭证事件',NULL,'pending',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(60,'expense',1,'ex20260101001','expense_confirm','2026-11-22',120.00,'0','admin','2026-11-22 22:45:19','',NULL,'系统补生成费用凭证事件',NULL,'pending',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(61,'outbound',100,'OUT202310020001','outbound_complete','2026-11-22',990.00,'0','admin','2026-11-22 22:46:16','',NULL,'系统补生成签收出库收入凭证事件',NULL,'pending',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(62,'outbound',1,'out20260101001','outbound_complete','2026-11-22',560.00,'0','admin','2026-11-22 22:46:16','',NULL,'系统补生成签收出库收入凭证事件',NULL,'pending',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `fin_voucher_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_voucher_template`
--

DROP TABLE IF EXISTS `fin_voucher_template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_voucher_template` (
  `template_id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `event_type` varchar(64) NOT NULL COMMENT '业务事件类型',
  `entry_seq` int NOT NULL DEFAULT '1' COMMENT '分录序号',
  `direction` varchar(16) NOT NULL COMMENT '借贷方向(debit/credit)',
  `subject_code` varchar(32) NOT NULL COMMENT '会计科目编码',
  `subject_name` varchar(128) NOT NULL COMMENT '会计科目名称',
  `auxiliary_type` varchar(32) DEFAULT 'none' COMMENT '辅助核算类型(customer/supplier/warehouse/none)',
  `summary_template` varchar(255) DEFAULT '' COMMENT '摘要模板',
  `status` char(1) DEFAULT '0' COMMENT '状态(0启用 1停用)',
  `create_by` varchar(64) DEFAULT 'admin',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`template_id`),
  UNIQUE KEY `uk_voucher_tpl_event_seq` (`event_type`,`entry_seq`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='业财凭证借贷科目映射模板';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_voucher_template`
--

LOCK TABLES `fin_voucher_template` WRITE;
/*!40000 ALTER TABLE `fin_voucher_template` DISABLE KEYS */;
INSERT INTO `fin_voucher_template` VALUES (1,'inbound_complete',1,'debit','1405','库存商品','warehouse','采购入库 {billNo}','0','admin','2026-11-22 17:43:28'),(2,'inbound_complete',2,'credit','2202','应付账款','supplier','采购入库应付 {billNo}','0','admin','2026-11-22 17:43:28'),(3,'outbound_complete',1,'debit','1122','应收账款','customer','销售出库应收 {billNo}','0','admin','2026-11-22 17:43:28'),(4,'outbound_complete',2,'credit','6001','主营业务收入','customer','销售出库收入 {billNo}','0','admin','2026-11-22 17:43:28'),(5,'sale_return_complete',1,'debit','6001','主营业务收入','customer','销售退货冲减收入 {billNo}','0','admin','2026-11-22 17:43:28'),(6,'sale_return_complete',2,'credit','1122','应收账款','customer','销售退货冲减应收 {billNo}','0','admin','2026-11-22 17:43:28'),(7,'outbound_return',1,'debit','6001','主营业务收入','customer','出库整单退货冲减 {billNo}','0','admin','2026-11-22 17:43:28'),(8,'outbound_return',2,'credit','1122','应收账款','customer','出库整单退货冲减应收 {billNo}','0','admin','2026-11-22 17:43:28'),(9,'purchase_return_complete',1,'debit','2202','应付账款','supplier','采购退货冲减应付 {billNo}','0','admin','2026-11-22 17:43:28'),(10,'purchase_return_complete',2,'credit','1405','库存商品','warehouse','采购退货冲减库存 {billNo}','0','admin','2026-11-22 17:43:28'),(11,'receipt_confirm',1,'debit','1002','银行存款','none','客户收款入账 {billNo}','0','admin','2026-11-22 17:43:28'),(12,'receipt_confirm',2,'credit','1122','应收账款','customer','核减客户应收 {billNo}','0','admin','2026-11-22 17:43:28'),(13,'payment_confirm',1,'debit','2202','应付账款','supplier','支付供应商货款 {billNo}','0','admin','2026-11-22 17:43:28'),(14,'payment_confirm',2,'credit','1002','银行存款','none','货款银行付款 {billNo}','0','admin','2026-11-22 17:43:28'),(15,'expense_confirm',1,'debit','6602','管理费用','none','期间费用确认 {billNo}','0','admin','2026-11-22 17:43:28'),(16,'expense_confirm',2,'credit','1002','银行存款','none','期间费用支出 {billNo}','0','admin','2026-11-22 17:43:28'),(17,'bad_debt_confirm',1,'debit','6701','信用减值损失','customer','确认坏账损失 {billNo}','0','admin','2026-11-22 17:43:28'),(18,'bad_debt_confirm',2,'credit','1122','应收账款','customer','核销坏账应收 {billNo}','0','admin','2026-11-22 17:43:28'),(19,'bad_debt_recover',1,'debit','1002','银行存款','none','已核销坏账收回 {billNo}','0','admin','2026-11-22 17:43:28'),(20,'bad_debt_recover',2,'credit','6701','信用减值损失','customer','坏账收回冲减损失 {billNo}','0','admin','2026-11-22 17:43:28'),(21,'invoice_confirm',1,'debit','1122','应收账款','customer','发票确认登记 {billNo}','0','admin','2026-11-22 17:43:28'),(22,'invoice_confirm',2,'credit','2221','应交税费','none','发票税额确认 {billNo}','0','admin','2026-11-22 17:43:28'),(23,'fund_transfer',1,'debit','1002','银行存款','none','资金内部调入 {billNo}','0','admin','2026-11-22 17:43:28'),(24,'fund_transfer',2,'credit','1002','银行存款','none','资金内部调出 {billNo}','0','admin','2026-11-22 17:43:28'),(25,'cost_adjust',1,'debit','1405','库存商品','warehouse','受控成本调价 {billNo}','0','admin','2026-11-22 17:43:28'),(26,'cost_adjust',2,'credit','6401','主营业务成本','warehouse','受控成本调价差额 {billNo}','0','admin','2026-11-22 17:43:28'),(27,'outbound_cogs',1,'debit','6401','主营业务成本','warehouse','销售签收结转主营成本 {billNo}','0','admin','2026-11-22 20:55:28'),(28,'outbound_cogs',2,'credit','1405','库存商品','warehouse','销售签收核减库存商品 {billNo}','0','admin','2026-11-22 20:55:28'),(29,'outbound_cogs_return',1,'debit','1405','库存商品','warehouse','销售退回/红冲冲回库存商品 {billNo}','0','admin','2026-11-22 20:55:28'),(30,'outbound_cogs_return',2,'credit','6401','主营业务成本','warehouse','销售退回/红冲冲减主营成本 {billNo}','0','admin','2026-11-22 20:55:28'),(31,'inventory_check_gain',1,'debit','1405','库存商品','warehouse','盘点盘盈增加库存商品 {billNo}','0','admin','2026-11-22 20:55:28'),(32,'inventory_check_gain',2,'credit','5301','营业外收入','warehouse','盘点盘盈确认收益 {billNo}','0','admin','2026-11-22 20:55:28'),(33,'inventory_check_loss',1,'debit','5711','营业外支出','warehouse','盘点盘亏确认损失 {billNo}','0','admin','2026-11-22 20:55:28'),(34,'inventory_check_loss',2,'credit','1405','库存商品','warehouse','盘点盘亏核减库存商品 {billNo}','0','admin','2026-11-22 20:55:28'),(35,'fund_transfer_fee',1,'debit','6603','财务费用','none','资金内部互转银行手续费 {billNo}','0','admin','2026-11-22 20:55:28'),(36,'fund_transfer_fee',2,'credit','1002','银行存款','none','扣划互转手续费 {billNo}','0','admin','2026-11-22 20:55:28'),(37,'refund_receipt',1,'debit','1122','应收账款','customer','销售退货现金退款冲回 {billNo}','0','admin','2026-11-22 20:55:28'),(38,'refund_receipt',2,'credit','1002','银行存款','none','支付客户退货退款 {billNo}','0','admin','2026-11-22 20:55:28'),(39,'sale_invoice_confirm',1,'debit','1122','应收账款','customer','销售发票确认销项税额 {billNo}','0','admin','2026-11-22 20:55:28'),(40,'sale_invoice_confirm',2,'credit','2221','应交税费','customer','应交增值税-销项税额 {billNo}','0','admin','2026-11-22 20:55:28'),(41,'purchase_invoice_confirm',1,'debit','2221','应交税费','supplier','采购发票确认进项税额 {billNo}','0','admin','2026-11-22 20:55:28'),(42,'purchase_invoice_confirm',2,'credit','2202','应付账款','supplier','应交增值税-进项税额应付 {billNo}','0','admin','2026-11-22 20:55:28'),(43,'manual_receivable',1,'debit','1122','应收账款','customer','手工应收单确认 {billNo}','0','admin','2026-11-22 20:55:28'),(44,'manual_receivable',2,'credit','6001','主营业务收入','customer','手工应收确认收入 {billNo}','0','admin','2026-11-22 20:55:28'),(45,'manual_payable',1,'debit','1405','库存商品','supplier','手工应付单确认 {billNo}','0','admin','2026-11-22 20:55:28'),(46,'manual_payable',2,'credit','2202','应付账款','supplier','手工应付确认负债 {billNo}','0','admin','2026-11-22 20:55:28');
/*!40000 ALTER TABLE `fin_voucher_template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fin_write_off`
--

DROP TABLE IF EXISTS `fin_write_off`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fin_write_off` (
  `write_off_id` bigint NOT NULL AUTO_INCREMENT COMMENT '核销id',
  `write_off_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '核销单号',
  `write_off_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '核销类型',
  `receivable_id` bigint DEFAULT NULL COMMENT '应收id',
  `payable_id` bigint DEFAULT NULL COMMENT '应付id',
  `receipt_id` bigint DEFAULT NULL COMMENT '收款单id',
  `payment_id` bigint DEFAULT NULL COMMENT '付款单id',
  `amount` decimal(18,2) DEFAULT '0.00' COMMENT '核销金额',
  `write_off_date` date DEFAULT NULL COMMENT '核销日期',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0草稿 1已核销）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`write_off_id`),
  UNIQUE KEY `uk_fin_write_off_no` (`write_off_no`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='核销单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fin_write_off`
--

LOCK TABLES `fin_write_off` WRITE;
/*!40000 ALTER TABLE `fin_write_off` DISABLE KEYS */;
INSERT INTO `fin_write_off` VALUES (1,'wo20260101001','receivable',1,NULL,1,NULL,200.00,'2026-11-22','1','admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例核销');
/*!40000 ALTER TABLE `fin_write_off` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_table`
--

DROP TABLE IF EXISTS `gen_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table` (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `tpl_web_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
  `package_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='代码生成业务表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table`
--

LOCK TABLES `gen_table` WRITE;
/*!40000 ALTER TABLE `gen_table` DISABLE KEYS */;
/*!40000 ALTER TABLE `gen_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gen_table_column`
--

DROP TABLE IF EXISTS `gen_table_column`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `gen_table_column` (
  `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典类型',
  `sort` int DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='代码生成业务表字段';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gen_table_column`
--

LOCK TABLES `gen_table_column` WRITE;
/*!40000 ALTER TABLE `gen_table_column` DISABLE KEYS */;
/*!40000 ALTER TABLE `gen_table_column` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `md_customer`
--

DROP TABLE IF EXISTS `md_customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_customer` (
  `customer_id` bigint NOT NULL AUTO_INCREMENT COMMENT '客户id',
  `customer_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户编码',
  `customer_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户名称',
  `customer_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '客户等级',
  `credit_limit` decimal(18,2) DEFAULT '0.00' COMMENT '信用额度',
  `receivable_days` int DEFAULT '30' COMMENT '账期天数',
  `price_policy_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'default' COMMENT '价格策略类型',
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '联系电话',
  `contact_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '邮箱',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '地址',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `opening_receivable` decimal(16,2) DEFAULT '0.00' COMMENT '期初应收余额',
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `uk_md_customer_code` (`customer_code`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='客户档案表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_customer`
--

LOCK TABLES `md_customer` WRITE;
/*!40000 ALTER TABLE `md_customer` DISABLE KEYS */;
INSERT INTO `md_customer` VALUES (1,'cus001','星火客户','',0.00,30,'default','钱七','13700000001','cus1@example.com','上海市徐汇区','0','0','admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例客户',0.00),(2,'cus002','晨光客户','',0.00,30,'default','孙八','13700000002','cus2@example.com','浙江省杭州市','0','0','admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例客户',0.00),(100,'CUST001','示例客户有限公司','',0.00,30,'default','张三','13800138000','','北京市朝阳区科技园','0','0','admin','2026-11-22 10:36:51','',NULL,NULL,0.00);
/*!40000 ALTER TABLE `md_customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `md_location`
--

DROP TABLE IF EXISTS `md_location`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_location` (
  `location_id` bigint NOT NULL AUTO_INCREMENT COMMENT '库位id',
  `warehouse_id` bigint NOT NULL COMMENT '仓库id',
  `location_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '库位编码',
  `location_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '库位名称',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`location_id`),
  UNIQUE KEY `uk_md_location_code` (`warehouse_id`,`location_code`)
) ENGINE=InnoDB AUTO_INCREMENT=2009 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='库位档案表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_location`
--

LOCK TABLES `md_location` WRITE;
/*!40000 ALTER TABLE `md_location` DISABLE KEYS */;
INSERT INTO `md_location` VALUES (1000,100,'A-01-01','A区1排1层','0','0','admin','2026-11-22 10:36:51','',NULL,'标准库位'),(1001,100,'A-01-02','A区1排2层','0','0','admin','2026-11-22 10:36:51','',NULL,'标准库位'),(1002,100,'B-01-01','B区1排1层','0','0','admin','2026-11-22 10:36:51','',NULL,'冷藏库位'),(2000,101,'S-01-01','门店货架1','0','0','admin','2026-11-22 10:36:51','',NULL,'门店展示区');
/*!40000 ALTER TABLE `md_location` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `md_product`
--

DROP TABLE IF EXISTS `md_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_product` (
  `product_id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `product_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品编码',
  `product_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品名称',
  `product_spec` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '规格型号',
  `unit_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '单位',
  `purchase_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '采购单位',
  `sale_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '销售单位',
  `base_unit` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '基础单位',
  `unit_convert_ratio` decimal(18,6) DEFAULT '1.000000' COMMENT '单位换算比',
  `enable_batch` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '1' COMMENT '启用批次（0否 1是）',
  `shelf_life_days` int DEFAULT '0' COMMENT '保质期天数',
  `enable_serial` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '启用序列号（0否 1是）',
  `warning_min_qty` decimal(18,2) DEFAULT '0.00' COMMENT '默认最小库存',
  `warning_max_qty` decimal(18,2) DEFAULT '0.00' COMMENT '默认最大库存',
  `bar_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '条码',
  `category_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '分类',
  `brand_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '品牌',
  `product_image` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '图片',
  `cost_price` decimal(18,2) DEFAULT '0.00' COMMENT '成本价',
  `sale_price` decimal(18,2) DEFAULT '0.00' COMMENT '销售价',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `warning_before_days` int DEFAULT '30' COMMENT '临期预警天数',
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `uk_md_product_code` (`product_code`)
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='商品档案表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_product`
--

LOCK TABLES `md_product` WRITE;
/*!40000 ALTER TABLE `md_product` DISABLE KEYS */;
INSERT INTO `md_product` VALUES (1,'p001','螺丝刀','6寸','把','','','把',1.000000,'1',0,'0',0.00,0.00,'690000000001','五金','工具',NULL,15.00,28.00,'0','0','admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例商品',30),(2,'p002','扳手','8寸','把','','','把',1.000000,'1',0,'0',0.00,0.00,'690000000002','五金','工具',NULL,20.00,35.00,'0','0','admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例商品',30),(3,'p003','纸箱','60x40','个','','','个',1.000000,'1',0,'0',0.00,0.00,'690000000003','包装','耗材',NULL,2.00,5.00,'0','0','admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例商品',30),(100,'PROD001','无线鼠标','','个','','','个',1.000000,'1',0,'0',0.00,0.00,'','电子产品','',NULL,50.00,99.00,'0','0','admin','2026-11-22 10:36:51','',NULL,'罗技无线鼠标',30),(101,'PROD002','机械键盘','','把','','','把',1.000000,'1',0,'0',0.00,0.00,'','电子产品','',NULL,150.00,299.00,'0','0','admin','2026-11-22 10:36:51','',NULL,'樱桃轴机械键盘',30);
/*!40000 ALTER TABLE `md_product` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `md_product_unit`
--

DROP TABLE IF EXISTS `md_product_unit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_product_unit` (
  `unit_id` bigint NOT NULL AUTO_INCREMENT COMMENT '单位换算ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `unit_name` varchar(32) NOT NULL COMMENT '单位名称(如箱/盒/件)',
  `is_base_unit` char(1) DEFAULT '0' COMMENT '是否基准库存单位(1是 0否)',
  `convert_ratio` decimal(18,4) NOT NULL DEFAULT '1.0000' COMMENT '换算率(1当前单位=N基准单位)',
  `bar_code` varchar(64) DEFAULT NULL COMMENT '该包装单位条码',
  `purchase_price` decimal(18,2) DEFAULT NULL COMMENT '该单位采购价',
  `sale_price` decimal(18,2) DEFAULT NULL COMMENT '该单位销售价',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) DEFAULT '',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` varchar(64) DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`unit_id`),
  UNIQUE KEY `uk_product_unit_name` (`product_id`,`unit_name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品多单位与换算率表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_product_unit`
--

LOCK TABLES `md_product_unit` WRITE;
/*!40000 ALTER TABLE `md_product_unit` DISABLE KEYS */;
INSERT INTO `md_product_unit` VALUES (4,1,'把','1',1.0000,NULL,15.00,28.00,'0','admin','2026-11-22 20:56:10','',NULL),(5,2,'把','1',1.0000,NULL,20.00,35.00,'0','admin','2026-11-22 20:56:10','',NULL),(6,3,'个','1',1.0000,NULL,2.00,5.00,'0','admin','2026-11-22 20:56:10','',NULL),(7,100,'个','1',1.0000,NULL,50.00,99.00,'0','admin','2026-11-22 20:56:10','',NULL),(8,101,'把','1',1.0000,NULL,150.00,299.00,'0','admin','2026-11-22 20:56:10','',NULL);
/*!40000 ALTER TABLE `md_product_unit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `md_supplier`
--

DROP TABLE IF EXISTS `md_supplier`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_supplier` (
  `supplier_id` bigint NOT NULL AUTO_INCREMENT COMMENT '供应商id',
  `supplier_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '供应商编码',
  `supplier_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '供应商名称',
  `supplier_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '供应商等级',
  `payable_days` int DEFAULT '30' COMMENT '账期天数',
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '联系电话',
  `contact_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '邮箱',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '地址',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `opening_payable` decimal(16,2) DEFAULT '0.00' COMMENT '期初应付余额',
  PRIMARY KEY (`supplier_id`),
  UNIQUE KEY `uk_md_supplier_code` (`supplier_code`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='供应商档案表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_supplier`
--

LOCK TABLES `md_supplier` WRITE;
/*!40000 ALTER TABLE `md_supplier` DISABLE KEYS */;
INSERT INTO `md_supplier` VALUES (1,'sup001','海川供应商','',30,'王五','13900000001','sup1@example.com','浙江省宁波市','0','0','admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例供应商',0.00),(2,'sup002','恒星供应商','',30,'赵六','13900000002','sup2@example.com','安徽省合肥市','0','0','admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例供应商',0.00),(100,'SUPP001','示例供应商有限公司','',30,'李四','13900139000','','上海市浦东新区工业园','0','0','admin','2026-11-22 10:36:51','',NULL,NULL,0.00);
/*!40000 ALTER TABLE `md_supplier` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `md_warehouse`
--

DROP TABLE IF EXISTS `md_warehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `md_warehouse` (
  `warehouse_id` bigint NOT NULL AUTO_INCREMENT COMMENT '仓库id',
  `warehouse_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '仓库编码',
  `warehouse_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '仓库名称',
  `contact_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '联系人',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '联系电话',
  `address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '地址',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`warehouse_id`),
  UNIQUE KEY `uk_md_warehouse_code` (`warehouse_code`)
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='仓库档案表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `md_warehouse`
--

LOCK TABLES `md_warehouse` WRITE;
/*!40000 ALTER TABLE `md_warehouse` DISABLE KEYS */;
INSERT INTO `md_warehouse` VALUES (1,'wh001','主仓库','张三','13800000001','上海市浦东新区','0','0','admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例仓库'),(2,'wh002','华东仓','李四','13800000002','江苏省苏州市','0','0','admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例仓库'),(100,'WH100','示例主仓库','系统管理员','','','0','0','admin','2026-11-22 10:36:51','',NULL,'系统默认主仓库'),(101,'WH101','示例门店仓','门店管理员','','','0','0','admin','2026-11-22 10:36:51','',NULL,'分店仓库');
/*!40000 ALTER TABLE `md_warehouse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `price_customer_level`
--

DROP TABLE IF EXISTS `price_customer_level`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `price_customer_level` (
  `level_price_id` bigint NOT NULL AUTO_INCREMENT COMMENT '等级价格id',
  `customer_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '客户等级',
  `product_id` bigint NOT NULL COMMENT '商品id',
  `sale_price` decimal(18,2) DEFAULT '0.00' COMMENT '销售价',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0启用 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `effective_date` date DEFAULT NULL COMMENT '生效日期',
  `expire_date` date DEFAULT NULL COMMENT '失效日期',
  PRIMARY KEY (`level_price_id`),
  UNIQUE KEY `uk_price_customer_level` (`customer_level`,`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='客户等级价格表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `price_customer_level`
--

LOCK TABLES `price_customer_level` WRITE;
/*!40000 ALTER TABLE `price_customer_level` DISABLE KEYS */;
/*!40000 ALTER TABLE `price_customer_level` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `price_customer_special`
--

DROP TABLE IF EXISTS `price_customer_special`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `price_customer_special` (
  `special_price_id` bigint NOT NULL AUTO_INCREMENT COMMENT '专属价格id',
  `customer_id` bigint NOT NULL COMMENT '客户id',
  `product_id` bigint NOT NULL COMMENT '商品id',
  `sale_price` decimal(18,2) DEFAULT '0.00' COMMENT '销售价',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0启用 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `effective_date` date DEFAULT NULL COMMENT '生效日期',
  `expire_date` date DEFAULT NULL COMMENT '失效日期',
  PRIMARY KEY (`special_price_id`),
  UNIQUE KEY `uk_price_customer_special` (`customer_id`,`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='客户专属价格表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `price_customer_special`
--

LOCK TABLES `price_customer_special` WRITE;
/*!40000 ALTER TABLE `price_customer_special` DISABLE KEYS */;
/*!40000 ALTER TABLE `price_customer_special` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `price_recent_transaction`
--

DROP TABLE IF EXISTS `price_recent_transaction`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `price_recent_transaction` (
  `recent_price_id` bigint NOT NULL AUTO_INCREMENT COMMENT '最近成交价格id',
  `customer_id` bigint NOT NULL COMMENT '客户id',
  `product_id` bigint NOT NULL COMMENT '商品id',
  `sale_price` decimal(18,2) DEFAULT '0.00' COMMENT '销售价',
  `source_bill_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '来源单据类型',
  `source_bill_id` bigint DEFAULT NULL COMMENT '来源单据id',
  `business_date` date DEFAULT NULL COMMENT '业务日期',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `unit_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '基准单位',
  PRIMARY KEY (`recent_price_id`),
  UNIQUE KEY `uk_price_recent_transaction` (`customer_id`,`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='最近成交价格表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `price_recent_transaction`
--

LOCK TABLES `price_recent_transaction` WRITE;
/*!40000 ALTER TABLE `price_recent_transaction` DISABLE KEYS */;
INSERT INTO `price_recent_transaction` VALUES (1,104,105,25.00,'outbound',102,'2026-11-22','admin','2026-11-22 11:43:33','',NULL,'销货出库完成自动更新',NULL);
/*!40000 ALTER TABLE `price_recent_transaction` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pur_order`
--

DROP TABLE IF EXISTS `pur_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pur_order` (
  `purchase_order_id` bigint NOT NULL AUTO_INCREMENT COMMENT '购货订单id',
  `purchase_order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '购货订单号',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商id',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库id',
  `source_bill_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '来源单据类型',
  `source_bill_id` bigint DEFAULT NULL COMMENT '来源单据id',
  `source_bill_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '来源单据号',
  `business_date` date DEFAULT NULL COMMENT '业务日期',
  `expected_date` date DEFAULT NULL COMMENT '预计日期',
  `total_qty` decimal(18,2) DEFAULT '0.00' COMMENT '总数量',
  `total_amount` decimal(18,2) DEFAULT '0.00' COMMENT '总金额',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0草稿 1已审批 2已关闭）',
  `biz_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'draft' COMMENT '业务状态',
  `approve_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '审批人',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `discount_amount` decimal(18,2) DEFAULT '0.00' COMMENT '整单优惠金额',
  `tax_total_amount` decimal(18,2) DEFAULT '0.00' COMMENT '税额合计',
  `close_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '强制关闭原因',
  `dept_id` bigint DEFAULT NULL COMMENT '所属部门ID',
  `owner_user_id` bigint DEFAULT NULL COMMENT '归属业务员ID',
  PRIMARY KEY (`purchase_order_id`),
  UNIQUE KEY `uk_pur_order_no` (`purchase_order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='购货订单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pur_order`
--

LOCK TABLES `pur_order` WRITE;
/*!40000 ALTER TABLE `pur_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `pur_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pur_order_item`
--

DROP TABLE IF EXISTS `pur_order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pur_order_item` (
  `purchase_order_item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '购货订单明细id',
  `purchase_order_id` bigint NOT NULL COMMENT '购货订单id',
  `product_id` bigint NOT NULL COMMENT '商品id',
  `quantity` decimal(18,2) DEFAULT '0.00' COMMENT '数量',
  `price` decimal(18,2) DEFAULT '0.00' COMMENT '单价',
  `amount` decimal(18,2) DEFAULT '0.00' COMMENT '金额',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `received_qty` decimal(18,4) DEFAULT '0.0000' COMMENT '已下推/已入库数量',
  `discount_rate` decimal(5,2) DEFAULT '100.00' COMMENT '行折扣率(%)',
  `tax_rate` decimal(5,2) DEFAULT '0.00' COMMENT '税率(%)',
  `tax_amount` decimal(18,2) DEFAULT '0.00' COMMENT '行税额',
  `unit_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '业务单位',
  `convert_ratio` decimal(18,4) DEFAULT '1.0000' COMMENT '换算率',
  `base_quantity` decimal(18,4) DEFAULT NULL COMMENT '折合基准数量',
  PRIMARY KEY (`purchase_order_item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='购货订单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pur_order_item`
--

LOCK TABLES `pur_order_item` WRITE;
/*!40000 ALTER TABLE `pur_order_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `pur_order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_blob_triggers`
--

DROP TABLE IF EXISTS `qrtz_blob_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_blob_triggers` (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Blob类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_blob_triggers`
--

LOCK TABLES `qrtz_blob_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_blob_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_blob_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_calendars`
--

DROP TABLE IF EXISTS `qrtz_calendars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_calendars` (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`,`calendar_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='日历信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_calendars`
--

LOCK TABLES `qrtz_calendars` WRITE;
/*!40000 ALTER TABLE `qrtz_calendars` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_calendars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_cron_triggers`
--

DROP TABLE IF EXISTS `qrtz_cron_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_cron_triggers` (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Cron类型的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_cron_triggers`
--

LOCK TABLES `qrtz_cron_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_cron_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_cron_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_fired_triggers`
--

DROP TABLE IF EXISTS `qrtz_fired_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_fired_triggers` (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint NOT NULL COMMENT '触发的时间',
  `sched_time` bigint NOT NULL COMMENT '定时器制定的时间',
  `priority` int NOT NULL COMMENT '优先级',
  `state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`,`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='已触发的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_fired_triggers`
--

LOCK TABLES `qrtz_fired_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_fired_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_fired_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_job_details`
--

DROP TABLE IF EXISTS `qrtz_job_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_job_details` (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务组名',
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`job_name`,`job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_job_details`
--

LOCK TABLES `qrtz_job_details` WRITE;
/*!40000 ALTER TABLE `qrtz_job_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_job_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_locks`
--

DROP TABLE IF EXISTS `qrtz_locks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_locks` (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`,`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='存储的悲观锁信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_locks`
--

LOCK TABLES `qrtz_locks` WRITE;
/*!40000 ALTER TABLE `qrtz_locks` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_locks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_paused_trigger_grps`
--

DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_paused_trigger_grps` (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`,`trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='暂停的触发器表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_paused_trigger_grps`
--

LOCK TABLES `qrtz_paused_trigger_grps` WRITE;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_scheduler_state`
--

DROP TABLE IF EXISTS `qrtz_scheduler_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_scheduler_state` (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`,`instance_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='调度器状态表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_scheduler_state`
--

LOCK TABLES `qrtz_scheduler_state` WRITE;
/*!40000 ALTER TABLE `qrtz_scheduler_state` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_scheduler_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_simple_triggers`
--

DROP TABLE IF EXISTS `qrtz_simple_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_simple_triggers` (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `repeat_count` bigint NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint NOT NULL COMMENT '已经触发的次数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='简单触发器的信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_simple_triggers`
--

LOCK TABLES `qrtz_simple_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_simple_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_simple_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_simprop_triggers`
--

DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_simprop_triggers` (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `str_prop_1` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
  `str_prop_2` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
  `str_prop_3` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
  `int_prop_1` int DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
  `dec_prop_1` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
  `dec_prop_2` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
  `bool_prop_1` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
  `bool_prop_2` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='同步机制的行锁表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_simprop_triggers`
--

LOCK TABLES `qrtz_simprop_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_simprop_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_simprop_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_triggers`
--

DROP TABLE IF EXISTS `qrtz_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `qrtz_triggers` (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器的类型',
  `start_time` bigint NOT NULL COMMENT '开始时间',
  `end_time` bigint DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  KEY `sched_name` (`sched_name`,`job_name`,`job_group`),
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='触发器详细信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_triggers`
--

LOCK TABLES `qrtz_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sal_order`
--

DROP TABLE IF EXISTS `sal_order`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sal_order` (
  `order_id` bigint NOT NULL AUTO_INCREMENT COMMENT '销售单据id',
  `order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '销售单据号',
  `order_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '单据类型（quote报价 sale销货）',
  `customer_id` bigint DEFAULT NULL COMMENT '客户id',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库id',
  `source_bill_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '来源单据类型',
  `source_bill_id` bigint DEFAULT NULL COMMENT '来源单据id',
  `source_bill_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '来源单据号',
  `business_date` date DEFAULT NULL COMMENT '业务日期',
  `expected_date` date DEFAULT NULL COMMENT '预计日期',
  `total_qty` decimal(18,2) DEFAULT '0.00' COMMENT '总数量',
  `total_amount` decimal(18,2) DEFAULT '0.00' COMMENT '总金额',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0草稿 1已审批 2已关闭）',
  `biz_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'draft' COMMENT '业务状态',
  `approve_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '审批人',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `discount_amount` decimal(18,2) DEFAULT '0.00' COMMENT '整单优惠金额',
  `tax_total_amount` decimal(18,2) DEFAULT '0.00' COMMENT '税额合计',
  `close_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '强制关闭原因',
  `dept_id` bigint DEFAULT NULL COMMENT '所属部门ID',
  `owner_user_id` bigint DEFAULT NULL COMMENT '归属业务员ID',
  `stock_locked_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '是否已锁定库存(0否 1是)',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `uk_sal_order_no` (`order_no`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='销售单据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sal_order`
--

LOCK TABLES `sal_order` WRITE;
/*!40000 ALTER TABLE `sal_order` DISABLE KEYS */;
/*!40000 ALTER TABLE `sal_order` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sal_order_item`
--

DROP TABLE IF EXISTS `sal_order_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sal_order_item` (
  `order_item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '销售单据明细id',
  `order_id` bigint NOT NULL COMMENT '销售单据id',
  `product_id` bigint NOT NULL COMMENT '商品id',
  `quantity` decimal(18,2) DEFAULT '0.00' COMMENT '数量',
  `price` decimal(18,2) DEFAULT '0.00' COMMENT '单价',
  `amount` decimal(18,2) DEFAULT '0.00' COMMENT '金额',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `shipped_qty` decimal(18,4) DEFAULT '0.0000' COMMENT '已下推/已出库数量',
  `discount_rate` decimal(5,2) DEFAULT '100.00' COMMENT '行折扣率(%)',
  `tax_rate` decimal(5,2) DEFAULT '0.00' COMMENT '税率(%)',
  `tax_amount` decimal(18,2) DEFAULT '0.00' COMMENT '行税额',
  `unit_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '业务单位',
  `convert_ratio` decimal(18,4) DEFAULT '1.0000' COMMENT '换算率',
  `base_quantity` decimal(18,4) DEFAULT NULL COMMENT '折合基准数量',
  PRIMARY KEY (`order_item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='销售单据明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sal_order_item`
--

LOCK TABLES `sal_order_item` WRITE;
/*!40000 ALTER TABLE `sal_order_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `sal_order_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='参数配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (1,'主框架页-默认皮肤样式名称','sys.index.skinName','skin-blue','Y','admin','2026-11-22 10:36:47','',NULL,'蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow'),(2,'用户管理-账号初始密码','sys.user.initPassword','123456','Y','admin','2026-11-22 10:36:47','',NULL,'初始化密码 123456'),(3,'主框架页-侧边栏主题','sys.index.sideTheme','theme-dark','Y','admin','2026-11-22 10:36:47','',NULL,'深色主题theme-dark，浅色主题theme-light'),(4,'账号自助-验证码开关','sys.account.captchaEnabled','false','Y','admin','2026-11-22 10:36:47','admin','2026-11-22 10:36:55','开发阶段默认关闭验证码功能（true开启，false关闭）'),(5,'账号自助-是否开启用户注册功能','sys.account.registerUser','false','Y','admin','2026-11-22 10:36:47','',NULL,'是否开启注册用户功能（true开启，false关闭）'),(6,'用户登录-黑名单列表','sys.login.blackIPList','','Y','admin','2026-11-22 10:36:47','',NULL,'设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）'),(7,'用户管理-初始密码修改策略','sys.account.initPasswordModify','1','Y','admin','2026-11-22 10:36:47','',NULL,'0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框'),(8,'用户管理-账号密码更新周期','sys.account.passwordValidateDays','0','Y','admin','2026-11-22 10:36:47','',NULL,'密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框'),(100,'成本核算-全局默认算法','canglian.cost.defaultMethod','moving','Y','admin','2026-11-22 16:46:36','',NULL,'全局库存成本核算算法：moving(移动加权平均) / fifo(先进先出)'),(101,'安全策略-首次登录强制修改初始密码','canglian.security.forcePasswordChange','false','Y','admin','2026-11-22 16:46:36','',NULL,'设为true时未修改过初始密码的账号登录后将强制跳转改密（生产环境建议开启）'),(102,'库存策略-销货订单审批自动锁库','canglian.stock.lockOnSaleApprove','true','Y','admin','2026-11-22 17:43:31','',NULL,'开启后销货订单审批通过时自动锁定可用库存，出库审核或关闭时自动释放'),(103,'单据打印-企业抬头名称','canglian.print.companyName','仓链商贸有限公司','Y','admin','2026-11-22 17:43:31','',NULL,'A4/三联单打印抬头公司名称'),(104,'单据打印-联系电话','canglian.print.companyPhone','400-800-8888','Y','admin','2026-11-22 17:43:31','',NULL,'单据打印页眉联系电话'),(105,'单据打印-企业地址','canglian.print.companyAddress','上海市浦东新区仓链智慧产业园','Y','admin','2026-11-22 17:43:31','',NULL,'单据打印页眉地址'),(106,'库存策略-批次临期预警默认天数','canglian.stock.expiryWarningDays','30','Y','admin','2026-11-22 20:55:28','',NULL,'商品未单独设置预警天数时默认按提前30天预警'),(107,'库存策略-是否允许负库存出库','canglian.stock.allowNegative','false','Y','admin','2026-11-22 20:55:28','',NULL,'生产环境默认关闭负库存(false)，开启(true)仅适用于特殊先出后补场景');
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dept`
--

DROP TABLE IF EXISTS `sys_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dept` (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '部门名称',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='部门表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dept`
--

LOCK TABLES `sys_dept` WRITE;
/*!40000 ALTER TABLE `sys_dept` DISABLE KEYS */;
INSERT INTO `sys_dept` VALUES (100,0,'0','仓链科技',0,'仓链','15888888888','ry@qq.com','0','0','admin','2026-11-22 10:36:43','',NULL),(101,100,'0,100','深圳总公司',1,'仓链','15888888888','ry@qq.com','0','0','admin','2026-11-22 10:36:43','',NULL),(102,100,'0,100','长沙分公司',2,'仓链','15888888888','ry@qq.com','0','0','admin','2026-11-22 10:36:43','',NULL),(103,101,'0,100,101','研发部门',1,'仓链','15888888888','ry@qq.com','0','0','admin','2026-11-22 10:36:43','',NULL),(104,101,'0,100,101','市场部门',2,'仓链','15888888888','ry@qq.com','0','0','admin','2026-11-22 10:36:43','',NULL),(105,101,'0,100,101','测试部门',3,'仓链','15888888888','ry@qq.com','0','0','admin','2026-11-22 10:36:43','',NULL),(106,101,'0,100,101','财务部门',4,'仓链','15888888888','ry@qq.com','0','0','admin','2026-11-22 10:36:43','',NULL),(107,101,'0,100,101','运维部门',5,'仓链','15888888888','ry@qq.com','0','0','admin','2026-11-22 10:36:43','',NULL),(108,102,'0,100,102','市场部门',1,'仓链','15888888888','ry@qq.com','0','0','admin','2026-11-22 10:36:43','',NULL),(109,102,'0,100,102','财务部门',2,'仓链','15888888888','ry@qq.com','0','0','admin','2026-11-22 10:36:43','',NULL);
/*!40000 ALTER TABLE `sys_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_data`
--

DROP TABLE IF EXISTS `sys_dict_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_data`
--

LOCK TABLES `sys_dict_data` WRITE;
/*!40000 ALTER TABLE `sys_dict_data` DISABLE KEYS */;
INSERT INTO `sys_dict_data` VALUES (1,1,'男','0','sys_user_sex','','','Y','0','admin','2026-11-22 10:36:47','',NULL,'性别男'),(2,2,'女','1','sys_user_sex','','','N','0','admin','2026-11-22 10:36:47','',NULL,'性别女'),(3,3,'未知','2','sys_user_sex','','','N','0','admin','2026-11-22 10:36:47','',NULL,'性别未知'),(4,1,'显示','0','sys_show_hide','','primary','Y','0','admin','2026-11-22 10:36:47','',NULL,'显示菜单'),(5,2,'隐藏','1','sys_show_hide','','danger','N','0','admin','2026-11-22 10:36:47','',NULL,'隐藏菜单'),(6,1,'正常','0','sys_normal_disable','','primary','Y','0','admin','2026-11-22 10:36:47','',NULL,'正常状态'),(7,2,'停用','1','sys_normal_disable','','danger','N','0','admin','2026-11-22 10:36:47','',NULL,'停用状态'),(8,1,'正常','0','sys_job_status','','primary','Y','0','admin','2026-11-22 10:36:47','',NULL,'正常状态'),(9,2,'暂停','1','sys_job_status','','danger','N','0','admin','2026-11-22 10:36:47','',NULL,'停用状态'),(10,1,'默认','DEFAULT','sys_job_group','','','Y','0','admin','2026-11-22 10:36:47','',NULL,'默认分组'),(11,2,'系统','SYSTEM','sys_job_group','','','N','0','admin','2026-11-22 10:36:47','',NULL,'系统分组'),(12,1,'是','Y','sys_yes_no','','primary','Y','0','admin','2026-11-22 10:36:47','',NULL,'系统默认是'),(13,2,'否','N','sys_yes_no','','danger','N','0','admin','2026-11-22 10:36:47','',NULL,'系统默认否'),(14,1,'通知','1','sys_notice_type','','warning','Y','0','admin','2026-11-22 10:36:47','',NULL,'通知'),(15,2,'公告','2','sys_notice_type','','success','N','0','admin','2026-11-22 10:36:47','',NULL,'公告'),(16,1,'正常','0','sys_notice_status','','primary','Y','0','admin','2026-11-22 10:36:47','',NULL,'正常状态'),(17,2,'关闭','1','sys_notice_status','','danger','N','0','admin','2026-11-22 10:36:47','',NULL,'关闭状态'),(18,99,'其他','0','sys_oper_type','','info','N','0','admin','2026-11-22 10:36:47','',NULL,'其他操作'),(19,1,'新增','1','sys_oper_type','','info','N','0','admin','2026-11-22 10:36:47','',NULL,'新增操作'),(20,2,'修改','2','sys_oper_type','','info','N','0','admin','2026-11-22 10:36:47','',NULL,'修改操作'),(21,3,'删除','3','sys_oper_type','','danger','N','0','admin','2026-11-22 10:36:47','',NULL,'删除操作'),(22,4,'授权','4','sys_oper_type','','primary','N','0','admin','2026-11-22 10:36:47','',NULL,'授权操作'),(23,5,'导出','5','sys_oper_type','','warning','N','0','admin','2026-11-22 10:36:47','',NULL,'导出操作'),(24,6,'导入','6','sys_oper_type','','warning','N','0','admin','2026-11-22 10:36:47','',NULL,'导入操作'),(25,7,'强退','7','sys_oper_type','','danger','N','0','admin','2026-11-22 10:36:47','',NULL,'强退操作'),(26,8,'生成代码','8','sys_oper_type','','warning','N','0','admin','2026-11-22 10:36:47','',NULL,'生成操作'),(27,9,'清空数据','9','sys_oper_type','','danger','N','0','admin','2026-11-22 10:36:47','',NULL,'清空操作'),(28,1,'成功','0','sys_common_status','','primary','N','0','admin','2026-11-22 10:36:47','',NULL,'正常状态'),(29,2,'失败','1','sys_common_status','','danger','N','0','admin','2026-11-22 10:36:47','',NULL,'停用状态');
/*!40000 ALTER TABLE `sys_dict_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_type`
--

DROP TABLE IF EXISTS `sys_dict_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_type`
--

LOCK TABLES `sys_dict_type` WRITE;
/*!40000 ALTER TABLE `sys_dict_type` DISABLE KEYS */;
INSERT INTO `sys_dict_type` VALUES (1,'用户性别','sys_user_sex','0','admin','2026-11-22 10:36:46','',NULL,'用户性别列表'),(2,'菜单状态','sys_show_hide','0','admin','2026-11-22 10:36:46','',NULL,'菜单状态列表'),(3,'系统开关','sys_normal_disable','0','admin','2026-11-22 10:36:47','',NULL,'系统开关列表'),(4,'任务状态','sys_job_status','0','admin','2026-11-22 10:36:47','',NULL,'任务状态列表'),(5,'任务分组','sys_job_group','0','admin','2026-11-22 10:36:47','',NULL,'任务分组列表'),(6,'系统是否','sys_yes_no','0','admin','2026-11-22 10:36:47','',NULL,'系统是否列表'),(7,'通知类型','sys_notice_type','0','admin','2026-11-22 10:36:47','',NULL,'通知类型列表'),(8,'通知状态','sys_notice_status','0','admin','2026-11-22 10:36:47','',NULL,'通知状态列表'),(9,'操作类型','sys_oper_type','0','admin','2026-11-22 10:36:47','',NULL,'操作类型列表'),(10,'系统状态','sys_common_status','0','admin','2026-11-22 10:36:47','',NULL,'登录状态列表');
/*!40000 ALTER TABLE `sys_dict_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_job`
--

DROP TABLE IF EXISTS `sys_job`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_job` (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`,`job_name`,`job_group`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='定时任务调度表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job`
--

LOCK TABLES `sys_job` WRITE;
/*!40000 ALTER TABLE `sys_job` DISABLE KEYS */;
INSERT INTO `sys_job` VALUES (1,'系统默认（无参）','DEFAULT','ryTask.ryNoParams','0/10 * * * * ?','3','1','1','admin','2026-11-22 10:36:47','',NULL,''),(2,'系统默认（有参）','DEFAULT','ryTask.ryParams(\'ry\')','0/15 * * * * ?','3','1','1','admin','2026-11-22 10:36:47','',NULL,''),(3,'系统默认（多参）','DEFAULT','ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)','0/20 * * * * ?','3','1','1','admin','2026-11-22 10:36:47','',NULL,''),(4,'仓链数据库每日自动备份','SYSTEM','canglianBackupTask.autoBackup(30)','0 0 2 * * ?','3','1','0','admin','2026-11-22 16:46:36','',NULL,'每日凌晨2点自动执行全库逻辑SQL备份并清理30天前过期备份');
/*!40000 ALTER TABLE `sys_job` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_job_log`
--

DROP TABLE IF EXISTS `sys_job_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_job_log` (
  `job_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日志信息',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '异常信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='定时任务调度日志表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_job_log`
--

LOCK TABLES `sys_job_log` WRITE;
/*!40000 ALTER TABLE `sys_job_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_job_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_logininfor`
--

DROP TABLE IF EXISTS `sys_logininfor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_logininfor` (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作系统',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`),
  KEY `idx_sys_logininfor_s` (`status`),
  KEY `idx_sys_logininfor_lt` (`login_time`)
) ENGINE=InnoDB AUTO_INCREMENT=131 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统访问记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_logininfor`
--

LOCK TABLES `sys_logininfor` WRITE;
/*!40000 ALTER TABLE `sys_logininfor` DISABLE KEYS */;
INSERT INTO `sys_logininfor` VALUES (100,'admin','127.0.0.1','内网IP','Antigravity 2.34.0','Windows 10.0','0','登录成功','2026-11-22 10:38:55'),(101,'admin','127.0.0.1','内网IP','WindowsPowerShell 5.1.19041.6456','Windows 10.0','0','登录成功','2026-11-22 11:38:04'),(102,'admin','127.0.0.1','内网IP','WindowsPowerShell 5.1.19041.6456','Windows 10.0','0','登录成功','2026-11-22 11:40:09'),(103,'admin','127.0.0.1','内网IP','WindowsPowerShell 5.1.19041.6456','Windows 10.0','0','登录成功','2026-11-22 11:41:55'),(104,'admin','127.0.0.1','内网IP','WindowsPowerShell 5.1.19041.6456','Windows 10.0','0','登录成功','2026-11-22 11:43:02'),(105,'admin','127.0.0.1','内网IP','WindowsPowerShell 5.1.19041.6456','Windows 10.0','0','登录成功','2026-11-22 11:43:32'),(106,'admin','127.0.0.1','内网IP','Antigravity 2.34.0','Windows 10.0','0','登录成功','2026-11-22 11:45:55'),(107,'admin','127.0.0.1','内网IP','WindowsPowerShell 5.1.19041.6456','Windows 10.0','0','登录成功','2026-11-22 15:27:00'),(108,'admin','127.0.0.1','内网IP','Antigravity 2.34.0','Windows 10.0','0','登录成功','2026-11-22 15:27:40'),(109,'admin','127.0.0.1','内网IP','Antigravity 2.34.0','Windows 10.0','0','登录成功','2026-11-22 15:28:45'),(110,'admin','127.0.0.1','内网IP','Antigravity 2.34.0','Windows 10.0','0','登录成功','2026-11-22 15:29:22'),(111,'admin','127.0.0.1','内网IP','Antigravity 2.34.0','Windows 10.0','0','登录成功','2026-11-22 15:30:38'),(112,'admin','127.0.0.1','内网IP','Antigravity 2.34.0','Windows 10.0','0','登录成功','2026-11-22 15:32:20'),(113,'admin','127.0.0.1','内网IP','Antigravity 2.34.0','Windows 10.0','0','登录成功','2026-11-22 15:34:11'),(114,'admin','127.0.0.1','内网IP','Antigravity 2.34.0','Windows 10.0','0','登录成功','2026-11-22 15:35:34'),(115,'admin','127.0.0.1','内网IP','Antigravity 2.34.0','Windows 10.0','0','登录成功','2026-11-22 15:37:54'),(116,'admin','127.0.0.1','内网IP','Antigravity 2.34.0','Windows 10.0','0','登录成功','2026-11-22 15:39:11'),(117,'admin','127.0.0.1','内网IP','WindowsPowerShell 5.1.19041.6456','Windows 10.0','0','登录成功','2026-11-22 15:42:36'),(118,'admin','127.0.0.1','内网IP','Antigravity 2.34.0','Windows 10.0','0','登录成功','2026-11-22 15:43:11'),(119,'admin','127.0.0.1','内网IP','Python-urllib 3.13','','0','登录成功','2026-11-22 17:12:50'),(120,'admin','127.0.0.1','内网IP','Python-urllib 3.13','','0','登录成功','2026-11-22 17:14:20'),(121,'admin','127.0.0.1','内网IP','Python-urllib 3.13','','0','登录成功','2026-11-22 17:15:08'),(122,'admin','127.0.0.1','内网IP','Antigravity 2.34.0','Windows 10.0','0','登录成功','2026-11-22 17:15:42'),(123,'admin','127.0.0.1','内网IP','Python-urllib 3.13','','0','登录成功','2026-11-22 18:58:26'),(124,'admin','127.0.0.1','内网IP','Python-urllib 3.13','','0','登录成功','2026-11-22 18:59:50'),(125,'admin','127.0.0.1','内网IP','Python-urllib 3.13','','0','登录成功','2026-11-22 19:02:30'),(126,'admin','127.0.0.1','内网IP','Antigravity 2.34.0','Windows 10.0','0','登录成功','2026-11-22 19:04:15'),(127,'admin','127.0.0.1','内网IP','Antigravity 2.34.0','Windows 10.0','0','登录成功','2026-11-22 19:51:46'),(128,'admin','127.0.0.1','内网IP','Python-urllib 3.13','','1','用户不存在/密码错误','2026-11-22 22:45:19'),(129,'admin','127.0.0.1','内网IP','Python-urllib 3.13','','0','登录成功','2026-11-22 22:45:19'),(130,'admin','127.0.0.1','内网IP','Python-urllib 3.13','','0','登录成功','2026-11-22 22:46:16');
/*!40000 ALTER TABLE `sys_logininfor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_menu` (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '路由名称',
  `is_frame` int DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2829 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1,'系统管理',0,1,'system',NULL,'','',1,0,'M','0','0','','system','admin','2026-11-22 10:36:44','',NULL,'系统管理目录'),(2,'系统监控',0,2,'monitor',NULL,'','',1,0,'M','0','0','','monitor','admin','2026-11-22 10:36:44','',NULL,'系统监控目录'),(3,'系统工具',0,3,'tool',NULL,'','',1,0,'M','0','0','','tool','admin','2026-11-22 10:36:44','',NULL,'系统工具目录'),(100,'用户管理',1,1,'user','system/user/index','','',1,0,'C','0','0','system:user:list','user','admin','2026-11-22 10:36:44','',NULL,'用户管理菜单'),(101,'角色管理',1,2,'role','system/role/index','','',1,0,'C','0','0','system:role:list','peoples','admin','2026-11-22 10:36:44','',NULL,'角色管理菜单'),(102,'菜单管理',1,3,'menu','system/menu/index','','',1,0,'C','0','0','system:menu:list','tree-table','admin','2026-11-22 10:36:44','',NULL,'菜单管理菜单'),(103,'部门管理',1,4,'dept','system/dept/index','','',1,0,'C','0','0','system:dept:list','tree','admin','2026-11-22 10:36:44','',NULL,'部门管理菜单'),(104,'岗位管理',1,5,'post','system/post/index','','',1,0,'C','0','0','system:post:list','post','admin','2026-11-22 10:36:44','',NULL,'岗位管理菜单'),(105,'字典管理',1,6,'dict','system/dict/index','','',1,0,'C','0','0','system:dict:list','dict','admin','2026-11-22 10:36:44','',NULL,'字典管理菜单'),(106,'参数设置',1,7,'config','system/config/index','','',1,0,'C','0','0','system:config:list','edit','admin','2026-11-22 10:36:44','',NULL,'参数设置菜单'),(107,'通知公告',1,8,'notice','system/notice/index','','',1,0,'C','0','0','system:notice:list','message','admin','2026-11-22 10:36:44','',NULL,'通知公告菜单'),(108,'日志管理',1,9,'log','','','',1,0,'M','0','0','','log','admin','2026-11-22 10:36:44','',NULL,'日志管理菜单'),(109,'在线用户',2,1,'online','monitor/online/index','','',1,0,'C','0','0','monitor:online:list','online','admin','2026-11-22 10:36:44','',NULL,'在线用户菜单'),(110,'定时任务',2,2,'job','monitor/job/index','','',1,0,'C','0','0','monitor:job:list','job','admin','2026-11-22 10:36:44','',NULL,'定时任务菜单'),(111,'数据监控',2,3,'druid','monitor/druid/index','','',1,0,'C','0','0','monitor:druid:list','druid','admin','2026-11-22 10:36:44','',NULL,'数据监控菜单'),(112,'服务监控',2,4,'server','monitor/server/index','','',1,0,'C','0','0','monitor:server:list','server','admin','2026-11-22 10:36:44','',NULL,'服务监控菜单'),(113,'缓存监控',2,5,'cache','monitor/cache/index','','',1,0,'C','0','0','monitor:cache:list','redis','admin','2026-11-22 10:36:44','',NULL,'缓存监控菜单'),(114,'缓存列表',2,6,'cacheList','monitor/cache/list','','',1,0,'C','0','0','monitor:cache:list','redis-list','admin','2026-11-22 10:36:44','',NULL,'缓存列表菜单'),(115,'表单构建',3,1,'build','tool/build/index','','',1,0,'C','0','0','tool:build:list','build','admin','2026-11-22 10:36:44','',NULL,'表单构建菜单'),(116,'代码生成',3,2,'gen','tool/gen/index','','',1,0,'C','0','0','tool:gen:list','code','admin','2026-11-22 10:36:44','',NULL,'代码生成菜单'),(117,'系统接口',3,3,'swagger','tool/swagger/index','','',1,0,'C','0','0','tool:swagger:list','swagger','admin','2026-11-22 10:36:44','',NULL,'系统接口菜单'),(500,'操作日志',108,1,'operlog','monitor/operlog/index','','',1,0,'C','0','0','monitor:operlog:list','form','admin','2026-11-22 10:36:44','',NULL,'操作日志菜单'),(501,'登录日志',108,2,'logininfor','monitor/logininfor/index','','',1,0,'C','0','0','monitor:logininfor:list','logininfor','admin','2026-11-22 10:36:44','',NULL,'登录日志菜单'),(1000,'用户查询',100,1,'','','','',1,0,'F','0','0','system:user:query','#','admin','2026-11-22 10:36:44','',NULL,''),(1001,'用户新增',100,2,'','','','',1,0,'F','0','0','system:user:add','#','admin','2026-11-22 10:36:44','',NULL,''),(1002,'用户修改',100,3,'','','','',1,0,'F','0','0','system:user:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(1003,'用户删除',100,4,'','','','',1,0,'F','0','0','system:user:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(1004,'用户导出',100,5,'','','','',1,0,'F','0','0','system:user:export','#','admin','2026-11-22 10:36:44','',NULL,''),(1005,'用户导入',100,6,'','','','',1,0,'F','0','0','system:user:import','#','admin','2026-11-22 10:36:44','',NULL,''),(1006,'重置密码',100,7,'','','','',1,0,'F','0','0','system:user:resetPwd','#','admin','2026-11-22 10:36:44','',NULL,''),(1007,'角色查询',101,1,'','','','',1,0,'F','0','0','system:role:query','#','admin','2026-11-22 10:36:44','',NULL,''),(1008,'角色新增',101,2,'','','','',1,0,'F','0','0','system:role:add','#','admin','2026-11-22 10:36:44','',NULL,''),(1009,'角色修改',101,3,'','','','',1,0,'F','0','0','system:role:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(1010,'角色删除',101,4,'','','','',1,0,'F','0','0','system:role:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(1011,'角色导出',101,5,'','','','',1,0,'F','0','0','system:role:export','#','admin','2026-11-22 10:36:44','',NULL,''),(1012,'菜单查询',102,1,'','','','',1,0,'F','0','0','system:menu:query','#','admin','2026-11-22 10:36:44','',NULL,''),(1013,'菜单新增',102,2,'','','','',1,0,'F','0','0','system:menu:add','#','admin','2026-11-22 10:36:44','',NULL,''),(1014,'菜单修改',102,3,'','','','',1,0,'F','0','0','system:menu:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(1015,'菜单删除',102,4,'','','','',1,0,'F','0','0','system:menu:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(1016,'部门查询',103,1,'','','','',1,0,'F','0','0','system:dept:query','#','admin','2026-11-22 10:36:44','',NULL,''),(1017,'部门新增',103,2,'','','','',1,0,'F','0','0','system:dept:add','#','admin','2026-11-22 10:36:44','',NULL,''),(1018,'部门修改',103,3,'','','','',1,0,'F','0','0','system:dept:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(1019,'部门删除',103,4,'','','','',1,0,'F','0','0','system:dept:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(1020,'岗位查询',104,1,'','','','',1,0,'F','0','0','system:post:query','#','admin','2026-11-22 10:36:44','',NULL,''),(1021,'岗位新增',104,2,'','','','',1,0,'F','0','0','system:post:add','#','admin','2026-11-22 10:36:44','',NULL,''),(1022,'岗位修改',104,3,'','','','',1,0,'F','0','0','system:post:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(1023,'岗位删除',104,4,'','','','',1,0,'F','0','0','system:post:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(1024,'岗位导出',104,5,'','','','',1,0,'F','0','0','system:post:export','#','admin','2026-11-22 10:36:44','',NULL,''),(1025,'字典查询',105,1,'#','','','',1,0,'F','0','0','system:dict:query','#','admin','2026-11-22 10:36:44','',NULL,''),(1026,'字典新增',105,2,'#','','','',1,0,'F','0','0','system:dict:add','#','admin','2026-11-22 10:36:44','',NULL,''),(1027,'字典修改',105,3,'#','','','',1,0,'F','0','0','system:dict:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(1028,'字典删除',105,4,'#','','','',1,0,'F','0','0','system:dict:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(1029,'字典导出',105,5,'#','','','',1,0,'F','0','0','system:dict:export','#','admin','2026-11-22 10:36:44','',NULL,''),(1030,'参数查询',106,1,'#','','','',1,0,'F','0','0','system:config:query','#','admin','2026-11-22 10:36:44','',NULL,''),(1031,'参数新增',106,2,'#','','','',1,0,'F','0','0','system:config:add','#','admin','2026-11-22 10:36:44','',NULL,''),(1032,'参数修改',106,3,'#','','','',1,0,'F','0','0','system:config:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(1033,'参数删除',106,4,'#','','','',1,0,'F','0','0','system:config:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(1034,'参数导出',106,5,'#','','','',1,0,'F','0','0','system:config:export','#','admin','2026-11-22 10:36:44','',NULL,''),(1035,'公告查询',107,1,'#','','','',1,0,'F','0','0','system:notice:query','#','admin','2026-11-22 10:36:44','',NULL,''),(1036,'公告新增',107,2,'#','','','',1,0,'F','0','0','system:notice:add','#','admin','2026-11-22 10:36:44','',NULL,''),(1037,'公告修改',107,3,'#','','','',1,0,'F','0','0','system:notice:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(1038,'公告删除',107,4,'#','','','',1,0,'F','0','0','system:notice:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(1039,'操作查询',500,1,'#','','','',1,0,'F','0','0','monitor:operlog:query','#','admin','2026-11-22 10:36:44','',NULL,''),(1040,'操作删除',500,2,'#','','','',1,0,'F','0','0','monitor:operlog:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(1041,'日志导出',500,3,'#','','','',1,0,'F','0','0','monitor:operlog:export','#','admin','2026-11-22 10:36:44','',NULL,''),(1042,'登录查询',501,1,'#','','','',1,0,'F','0','0','monitor:logininfor:query','#','admin','2026-11-22 10:36:44','',NULL,''),(1043,'登录删除',501,2,'#','','','',1,0,'F','0','0','monitor:logininfor:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(1044,'日志导出',501,3,'#','','','',1,0,'F','0','0','monitor:logininfor:export','#','admin','2026-11-22 10:36:44','',NULL,''),(1045,'账户解锁',501,4,'#','','','',1,0,'F','0','0','monitor:logininfor:unlock','#','admin','2026-11-22 10:36:44','',NULL,''),(1046,'在线查询',109,1,'#','','','',1,0,'F','0','0','monitor:online:query','#','admin','2026-11-22 10:36:44','',NULL,''),(1047,'批量强退',109,2,'#','','','',1,0,'F','0','0','monitor:online:batchLogout','#','admin','2026-11-22 10:36:44','',NULL,''),(1048,'单条强退',109,3,'#','','','',1,0,'F','0','0','monitor:online:forceLogout','#','admin','2026-11-22 10:36:44','',NULL,''),(1049,'任务查询',110,1,'#','','','',1,0,'F','0','0','monitor:job:query','#','admin','2026-11-22 10:36:44','',NULL,''),(1050,'任务新增',110,2,'#','','','',1,0,'F','0','0','monitor:job:add','#','admin','2026-11-22 10:36:44','',NULL,''),(1051,'任务修改',110,3,'#','','','',1,0,'F','0','0','monitor:job:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(1052,'任务删除',110,4,'#','','','',1,0,'F','0','0','monitor:job:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(1053,'状态修改',110,5,'#','','','',1,0,'F','0','0','monitor:job:changeStatus','#','admin','2026-11-22 10:36:44','',NULL,''),(1054,'任务导出',110,6,'#','','','',1,0,'F','0','0','monitor:job:export','#','admin','2026-11-22 10:36:44','',NULL,''),(1055,'生成查询',116,1,'#','','','',1,0,'F','0','0','tool:gen:query','#','admin','2026-11-22 10:36:44','',NULL,''),(1056,'生成修改',116,2,'#','','','',1,0,'F','0','0','tool:gen:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(1057,'生成删除',116,3,'#','','','',1,0,'F','0','0','tool:gen:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(1058,'导入代码',116,4,'#','','','',1,0,'F','0','0','tool:gen:import','#','admin','2026-11-22 10:36:44','',NULL,''),(1059,'预览代码',116,5,'#','','','',1,0,'F','0','0','tool:gen:preview','#','admin','2026-11-22 10:36:44','',NULL,''),(1060,'生成代码',116,6,'#','','','',1,0,'F','0','0','tool:gen:code','#','admin','2026-11-22 10:36:44','',NULL,''),(2000,'业务管理',0,5,'business',NULL,'','',1,0,'M','0','0','','shopping','admin','2026-11-22 10:36:44','',NULL,'业务管理目录'),(2001,'仓储管理',0,6,'wms',NULL,'','',1,0,'M','0','0','','table','admin','2026-11-22 10:36:44','',NULL,'仓储管理目录'),(2002,'仓库档案',2001,1,'warehouse','business/warehouse/index','','',1,0,'C','0','0','business:warehouse:list','table','admin','2026-11-22 10:36:44','',NULL,'仓库档案菜单'),(2003,'库存',2001,2,'stock','business/stock/index','','',1,0,'C','0','0','business:stock:list','list','admin','2026-11-22 10:36:44','',NULL,'库存菜单'),(2004,'入库单',2001,3,'inbound','business/inbound/index','','',1,0,'C','0','0','business:inbound:list','date','admin','2026-11-22 10:36:44','',NULL,'入库单菜单'),(2005,'出库单',2001,4,'outbound','business/outbound/index','','',1,0,'C','0','0','business:outbound:list','date','admin','2026-11-22 10:36:44','',NULL,'出库单菜单'),(2006,'盘点单',2001,5,'inventoryCheck','business/inventoryCheck/index','','',1,0,'C','0','0','business:inventoryCheck:list','form','admin','2026-11-22 10:36:44','',NULL,'盘点单菜单'),(2007,'调拨单',2001,6,'transfer','business/transfer/index','','',1,0,'C','0','0','business:transfer:list','list','admin','2026-11-22 10:36:44','',NULL,'调拨单菜单'),(2008,'库存流水',2001,7,'stockLog','business/stockLog/index','','',1,0,'C','0','0','business:stockLog:list','log','admin','2026-11-22 10:36:44','',NULL,'库存流水菜单'),(2009,'入库明细',2001,8,'inboundItem','business/inboundItem/index','','',1,0,'C','0','0','business:inboundItem:list','number','admin','2026-11-22 10:36:44','',NULL,'入库明细菜单'),(2010,'出库明细',2001,9,'outboundItem','business/outboundItem/index','','',1,0,'C','0','0','business:outboundItem:list','number','admin','2026-11-22 10:36:44','',NULL,'出库明细菜单'),(2011,'仓库统计',2001,1,'warehouseStatistics','business/warehouseStatistics/index','','',1,0,'C','0','0','business:warehouseStatistics:list','chart','admin','2026-11-22 10:36:44','',NULL,'仓库统计菜单'),(2100,'仓库查询',2002,1,'#','','','',1,0,'F','0','0','business:warehouse:query','#','admin','2026-11-22 10:36:44','',NULL,''),(2101,'仓库新增',2002,2,'#','','','',1,0,'F','0','0','business:warehouse:add','#','admin','2026-11-22 10:36:44','',NULL,''),(2102,'仓库修改',2002,3,'#','','','',1,0,'F','0','0','business:warehouse:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(2103,'仓库删除',2002,4,'#','','','',1,0,'F','0','0','business:warehouse:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(2110,'库存查询',2003,1,'#','','','',1,0,'F','0','0','business:stock:query','#','admin','2026-11-22 10:36:44','',NULL,''),(2111,'库存新增',2003,2,'#','','','',1,0,'F','0','0','business:stock:add','#','admin','2026-11-22 10:36:44','',NULL,''),(2112,'库存修改',2003,3,'#','','','',1,0,'F','0','0','business:stock:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(2113,'库存删除',2003,4,'#','','','',1,0,'F','0','0','business:stock:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(2120,'入库查询',2004,1,'#','','','',1,0,'F','0','0','business:inbound:query','#','admin','2026-11-22 10:36:44','',NULL,''),(2121,'入库新增',2004,2,'#','','','',1,0,'F','0','0','business:inbound:add','#','admin','2026-11-22 10:36:44','',NULL,''),(2122,'入库修改',2004,3,'#','','','',1,0,'F','0','0','business:inbound:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(2123,'入库删除',2004,4,'#','','','',1,0,'F','0','0','business:inbound:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(2124,'入库审核',2004,5,'#','','','',1,0,'F','0','0','business:inbound:audit','#','admin','2026-11-22 10:36:44','',NULL,''),(2125,'入库退货',2004,6,'#','','','',1,0,'F','0','0','business:inbound:return','#','admin','2026-11-22 10:36:44','',NULL,''),(2126,'入库打印',2004,7,'#','','','',1,0,'F','0','0','business:inbound:print','#','admin','2026-11-22 10:36:44','',NULL,''),(2130,'出库查询',2005,1,'#','','','',1,0,'F','0','0','business:outbound:query','#','admin','2026-11-22 10:36:44','',NULL,''),(2131,'出库新增',2005,2,'#','','','',1,0,'F','0','0','business:outbound:add','#','admin','2026-11-22 10:36:44','',NULL,''),(2132,'出库修改',2005,3,'#','','','',1,0,'F','0','0','business:outbound:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(2133,'出库删除',2005,4,'#','','','',1,0,'F','0','0','business:outbound:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(2134,'出库审核',2005,5,'#','','','',1,0,'F','0','0','business:outbound:audit','#','admin','2026-11-22 10:36:44','',NULL,''),(2135,'出库打印',2005,8,'#','','','',1,0,'F','0','0','business:outbound:print','#','admin','2026-11-22 10:36:44','',NULL,''),(2140,'盘点查询',2006,1,'#','','','',1,0,'F','0','0','business:inventoryCheck:query','#','admin','2026-11-22 10:36:44','',NULL,''),(2141,'盘点新增',2006,2,'#','','','',1,0,'F','0','0','business:inventoryCheck:add','#','admin','2026-11-22 10:36:44','',NULL,''),(2142,'盘点修改',2006,3,'#','','','',1,0,'F','0','0','business:inventoryCheck:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(2143,'盘点删除',2006,4,'#','','','',1,0,'F','0','0','business:inventoryCheck:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(2150,'调拨查询',2007,1,'#','','','',1,0,'F','0','0','business:transfer:query','#','admin','2026-11-22 10:36:44','',NULL,''),(2151,'调拨新增',2007,2,'#','','','',1,0,'F','0','0','business:transfer:add','#','admin','2026-11-22 10:36:44','',NULL,''),(2152,'调拨修改',2007,3,'#','','','',1,0,'F','0','0','business:transfer:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(2153,'调拨删除',2007,4,'#','','','',1,0,'F','0','0','business:transfer:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(2154,'调拨打印',2007,5,'#','','','',1,0,'F','0','0','business:transfer:print','#','admin','2026-11-22 10:36:44','',NULL,''),(2160,'流水查询',2008,1,'#','','','',1,0,'F','0','0','business:stockLog:query','#','admin','2026-11-22 10:36:44','',NULL,''),(2161,'流水新增',2008,2,'#','','','',1,0,'F','0','0','business:stockLog:add','#','admin','2026-11-22 10:36:44','',NULL,''),(2162,'流水修改',2008,3,'#','','','',1,0,'F','0','0','business:stockLog:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(2163,'流水删除',2008,4,'#','','','',1,0,'F','0','0','business:stockLog:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(2170,'明细查询',2009,1,'#','','','',1,0,'F','0','0','business:inboundItem:query','#','admin','2026-11-22 10:36:44','',NULL,''),(2171,'明细新增',2009,2,'#','','','',1,0,'F','0','0','business:inboundItem:add','#','admin','2026-11-22 10:36:44','',NULL,''),(2172,'明细修改',2009,3,'#','','','',1,0,'F','0','0','business:inboundItem:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(2173,'明细删除',2009,4,'#','','','',1,0,'F','0','0','business:inboundItem:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(2180,'明细查询',2010,1,'#','','','',1,0,'F','0','0','business:outboundItem:query','#','admin','2026-11-22 10:36:44','',NULL,''),(2181,'明细新增',2010,2,'#','','','',1,0,'F','0','0','business:outboundItem:add','#','admin','2026-11-22 10:36:44','',NULL,''),(2182,'明细修改',2010,3,'#','','','',1,0,'F','0','0','business:outboundItem:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(2183,'明细删除',2010,4,'#','','','',1,0,'F','0','0','business:outboundItem:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(2184,'出库发货',2005,5,'#','','','',1,0,'F','0','0','business:outbound:ship','#','admin','2026-11-22 10:36:44','',NULL,''),(2185,'出库签收',2005,6,'#','','','',1,0,'F','0','0','business:outbound:sign','#','admin','2026-11-22 10:36:44','',NULL,''),(2186,'出库退货',2005,7,'#','','','',1,0,'F','0','0','business:outbound:return','#','admin','2026-11-22 10:36:44','',NULL,''),(2187,'仓库统计查询',2011,1,'#','','','',1,0,'F','0','0','business:warehouseStatistics:query','#','admin','2026-11-22 10:36:44','',NULL,''),(2200,'财务管理',0,7,'finance',NULL,'','',1,0,'M','0','0','','money','admin','2026-11-22 10:36:45','',NULL,'财务管理目录'),(2201,'应收单',2200,1,'receivable','business/receivable/index','','',1,0,'C','0','0','business:receivable:list','money','admin','2026-11-22 10:36:45','',NULL,'应收单菜单'),(2202,'应付单',2200,2,'payable','business/payable/index','','',1,0,'C','0','0','business:payable:list','money','admin','2026-11-22 10:36:45','',NULL,'应付单菜单'),(2203,'收款单',2200,3,'receipt','business/receipt/index','','',1,0,'C','0','0','business:receipt:list','money','admin','2026-11-22 10:36:45','',NULL,'收款单菜单'),(2204,'付款单',2200,4,'payment','business/payment/index','','',1,0,'C','0','0','business:payment:list','money','admin','2026-11-22 10:36:45','',NULL,'付款单菜单'),(2205,'资金账户',2200,5,'fundAccount','business/fundAccount/index','','',1,0,'C','0','0','business:fundAccount:list','table','admin','2026-11-22 10:36:45','',NULL,'资金账户菜单'),(2206,'费用单',2200,6,'expense','business/expense/index','','',1,0,'C','0','0','business:expense:list','list','admin','2026-11-22 10:36:45','',NULL,'费用单菜单'),(2207,'核销单',2200,7,'writeOff','business/writeOff/index','','',1,0,'C','0','0','business:writeOff:list','form','admin','2026-11-22 10:36:45','',NULL,'核销单菜单'),(2208,'坏账单',2200,8,'badDebt','business/badDebt/index','','',1,0,'C','0','0','business:badDebt:list','question','admin','2026-11-22 10:36:45','',NULL,'坏账单菜单'),(2209,'成本核算',2200,9,'costCalculate','business/costCalculate/index','','',1,0,'C','0','0','business:costCalculate:list','number','admin','2026-11-22 10:36:45','',NULL,'成本核算菜单'),(2210,'财务报表',2200,10,'report','business/report/index','','',1,0,'C','0','0','business:report:list','chart','admin','2026-11-22 10:36:45','',NULL,'财务报表菜单'),(2260,'报价单',2000,30,'saleQuote','business/saleOrder/index','{\"orderType\":\"quote\"}','',1,0,'C','0','0','business:saleOrder:list','documentation','admin','2026-11-22 10:36:53','',NULL,'报价单菜单'),(2261,'销售订单',2000,31,'saleOrder','business/saleOrder/index','{\"orderType\":\"sale\"}','',1,0,'C','0','0','business:saleOrder:list','shopping','admin','2026-11-22 10:36:53','',NULL,'销货订单菜单'),(2262,'采购订单',2000,32,'purOrder','business/purOrder/index','','',1,0,'C','0','0','business:purOrder:list','table','admin','2026-11-22 10:36:53','',NULL,'购货订单菜单'),(2263,'销售发票',2200,12,'salesInvoice','business/invoice/index','{\"invoiceType\":\"sale\"}','',1,0,'C','0','0','business:invoice:list','date','admin','2026-11-22 10:36:53','',NULL,'销售发票菜单'),(2264,'采购发票',2200,13,'purchaseInvoice','business/invoice/index','{\"invoiceType\":\"purchase\"}','',1,0,'C','0','0','business:invoice:list','date','admin','2026-11-22 10:36:53','',NULL,'采购发票菜单'),(2265,'客户等级价',2000,33,'customerLevelPrice','business/customerLevelPrice/index','','',1,0,'C','0','0','business:customerLevelPrice:list','number','admin','2026-11-22 10:36:53','',NULL,'客户等级价菜单'),(2266,'客户专属价',2000,34,'customerSpecialPrice','business/customerSpecialPrice/index','','',1,0,'C','0','0','business:customerSpecialPrice:list','number','admin','2026-11-22 10:36:53','',NULL,'客户专属价菜单'),(2267,'最近成交价',2000,35,'recentTransactionPrice','business/recentTransactionPrice/index','','',1,0,'C','0','0','business:recentTransactionPrice:list','number','admin','2026-11-22 10:36:53','',NULL,'最近成交价菜单'),(2268,'补货建议',2001,11,'replenishment','business/replenishment/index','','',1,0,'C','0','0','business:replenishment:list','edit','admin','2026-11-22 10:36:53','',NULL,'补货建议菜单'),(2269,'凭证事件',2200,14,'voucherEvent','business/voucherEvent/index','','',1,0,'C','0','0','business:voucherEvent:list','form','admin','2026-11-22 10:36:53','',NULL,'凭证事件菜单'),(2270,'业务工作台',2000,29,'workbench','business/workbench/index','','',1,0,'C','0','0','business:workbench:list','dashboard','admin','2026-11-22 10:36:53','',NULL,'业务工作台菜单'),(2271,'扫码开单',2001,12,'scan','business/scan/index','','',1,0,'C','0','0','business:scan:list','guide','admin','2026-11-22 10:36:53','',NULL,'扫码开单菜单'),(2300,'应收查询',2201,1,'#','','','',1,0,'F','0','0','business:receivable:query','#','admin','2026-11-22 10:36:45','',NULL,''),(2301,'应收新增',2201,2,'#','','','',1,0,'F','0','0','business:receivable:add','#','admin','2026-11-22 10:36:45','',NULL,''),(2302,'应收修改',2201,3,'#','','','',1,0,'F','0','0','business:receivable:edit','#','admin','2026-11-22 10:36:45','',NULL,''),(2303,'应收删除',2201,4,'#','','','',1,0,'F','0','0','business:receivable:remove','#','admin','2026-11-22 10:36:45','',NULL,''),(2310,'应付查询',2202,1,'#','','','',1,0,'F','0','0','business:payable:query','#','admin','2026-11-22 10:36:45','',NULL,''),(2311,'应付新增',2202,2,'#','','','',1,0,'F','0','0','business:payable:add','#','admin','2026-11-22 10:36:45','',NULL,''),(2312,'应付修改',2202,3,'#','','','',1,0,'F','0','0','business:payable:edit','#','admin','2026-11-22 10:36:45','',NULL,''),(2313,'应付删除',2202,4,'#','','','',1,0,'F','0','0','business:payable:remove','#','admin','2026-11-22 10:36:45','',NULL,''),(2320,'收款查询',2203,1,'#','','','',1,0,'F','0','0','business:receipt:query','#','admin','2026-11-22 10:36:45','',NULL,''),(2321,'收款新增',2203,2,'#','','','',1,0,'F','0','0','business:receipt:add','#','admin','2026-11-22 10:36:45','',NULL,''),(2322,'收款修改',2203,3,'#','','','',1,0,'F','0','0','business:receipt:edit','#','admin','2026-11-22 10:36:45','',NULL,''),(2323,'收款删除',2203,4,'#','','','',1,0,'F','0','0','business:receipt:remove','#','admin','2026-11-22 10:36:45','',NULL,''),(2330,'付款查询',2204,1,'#','','','',1,0,'F','0','0','business:payment:query','#','admin','2026-11-22 10:36:45','',NULL,''),(2331,'付款新增',2204,2,'#','','','',1,0,'F','0','0','business:payment:add','#','admin','2026-11-22 10:36:45','',NULL,''),(2332,'付款修改',2204,3,'#','','','',1,0,'F','0','0','business:payment:edit','#','admin','2026-11-22 10:36:45','',NULL,''),(2333,'付款删除',2204,4,'#','','','',1,0,'F','0','0','business:payment:remove','#','admin','2026-11-22 10:36:45','',NULL,''),(2340,'账户查询',2205,1,'#','','','',1,0,'F','0','0','business:fundAccount:query','#','admin','2026-11-22 10:36:45','',NULL,''),(2341,'账户新增',2205,2,'#','','','',1,0,'F','0','0','business:fundAccount:add','#','admin','2026-11-22 10:36:45','',NULL,''),(2342,'账户修改',2205,3,'#','','','',1,0,'F','0','0','business:fundAccount:edit','#','admin','2026-11-22 10:36:45','',NULL,''),(2343,'账户删除',2205,4,'#','','','',1,0,'F','0','0','business:fundAccount:remove','#','admin','2026-11-22 10:36:45','',NULL,''),(2350,'费用查询',2206,1,'#','','','',1,0,'F','0','0','business:expense:query','#','admin','2026-11-22 10:36:45','',NULL,''),(2351,'费用新增',2206,2,'#','','','',1,0,'F','0','0','business:expense:add','#','admin','2026-11-22 10:36:45','',NULL,''),(2352,'费用修改',2206,3,'#','','','',1,0,'F','0','0','business:expense:edit','#','admin','2026-11-22 10:36:45','',NULL,''),(2353,'费用删除',2206,4,'#','','','',1,0,'F','0','0','business:expense:remove','#','admin','2026-11-22 10:36:45','',NULL,''),(2354,'费用确认',2206,5,'#','','','',1,0,'F','0','0','business:expense:confirm','#','admin','2026-11-22 10:36:45','',NULL,''),(2360,'核销查询',2207,1,'#','','','',1,0,'F','0','0','business:writeOff:query','#','admin','2026-11-22 10:36:45','',NULL,''),(2361,'核销新增',2207,2,'#','','','',1,0,'F','0','0','business:writeOff:add','#','admin','2026-11-22 10:36:45','',NULL,''),(2362,'核销修改',2207,3,'#','','','',1,0,'F','0','0','business:writeOff:edit','#','admin','2026-11-22 10:36:45','',NULL,''),(2363,'核销删除',2207,4,'#','','','',1,0,'F','0','0','business:writeOff:remove','#','admin','2026-11-22 10:36:45','',NULL,''),(2364,'核销确认',2207,5,'#','','','',1,0,'F','0','0','business:writeOff:confirm','#','admin','2026-11-22 10:36:45','',NULL,''),(2370,'坏账查询',2208,1,'#','','','',1,0,'F','0','0','business:badDebt:query','#','admin','2026-11-22 10:36:45','',NULL,''),(2371,'坏账新增',2208,2,'#','','','',1,0,'F','0','0','business:badDebt:add','#','admin','2026-11-22 10:36:45','',NULL,''),(2372,'坏账修改',2208,3,'#','','','',1,0,'F','0','0','business:badDebt:edit','#','admin','2026-11-22 10:36:45','',NULL,''),(2373,'坏账删除',2208,4,'#','','','',1,0,'F','0','0','business:badDebt:remove','#','admin','2026-11-22 10:36:45','',NULL,''),(2380,'核算计算',2209,1,'#','','','',1,0,'F','0','0','business:costCalculate:calculate','#','admin','2026-11-22 10:36:45','',NULL,''),(2381,'核算查询',2209,2,'#','','','',1,0,'F','0','0','business:costCalculate:query','#','admin','2026-11-22 10:36:45','',NULL,''),(2390,'报表查询',2210,1,'#','','','',1,0,'F','0','0','business:report:query','#','admin','2026-11-22 10:36:45','',NULL,''),(2400,'客户管理',2000,1,'customer','business/customer/index','','',1,0,'C','0','0','business:customer:list','user','admin','2026-11-22 10:36:44','',NULL,'客户档案菜单'),(2401,'供应商管理',2000,2,'supplier','business/supplier/index','','',1,0,'C','0','0','business:supplier:list','peoples','admin','2026-11-22 10:36:44','',NULL,'供应商档案菜单'),(2402,'商品管理',2000,3,'product','business/product/index','','',1,0,'C','0','0','business:product:list','shopping','admin','2026-11-22 10:36:44','',NULL,'商品档案菜单'),(2410,'采购退货',2001,10,'purchaseReturn','business/purchaseReturn/index','','',1,0,'C','0','0','business:purchaseReturn:list','date','admin','2026-11-22 10:36:44','',NULL,'采购退货菜单'),(2411,'销售退货',2001,11,'saleReturn','business/saleReturn/index','','',1,0,'C','0','0','business:saleReturn:list','date','admin','2026-11-22 10:36:44','',NULL,'销售退货菜单'),(2420,'客户查询',2400,1,'#','','','',1,0,'F','0','0','business:customer:query','#','admin','2026-11-22 10:36:44','',NULL,''),(2421,'客户新增',2400,2,'#','','','',1,0,'F','0','0','business:customer:add','#','admin','2026-11-22 10:36:44','',NULL,''),(2422,'客户修改',2400,3,'#','','','',1,0,'F','0','0','business:customer:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(2423,'客户删除',2400,4,'#','','','',1,0,'F','0','0','business:customer:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(2430,'供应商查询',2401,1,'#','','','',1,0,'F','0','0','business:supplier:query','#','admin','2026-11-22 10:36:44','',NULL,''),(2431,'供应商新增',2401,2,'#','','','',1,0,'F','0','0','business:supplier:add','#','admin','2026-11-22 10:36:44','',NULL,''),(2432,'供应商修改',2401,3,'#','','','',1,0,'F','0','0','business:supplier:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(2433,'供应商删除',2401,4,'#','','','',1,0,'F','0','0','business:supplier:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(2440,'商品查询',2402,1,'#','','','',1,0,'F','0','0','business:product:query','#','admin','2026-11-22 10:36:44','',NULL,''),(2441,'商品新增',2402,2,'#','','','',1,0,'F','0','0','business:product:add','#','admin','2026-11-22 10:36:44','',NULL,''),(2442,'商品修改',2402,3,'#','','','',1,0,'F','0','0','business:product:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(2443,'商品删除',2402,4,'#','','','',1,0,'F','0','0','business:product:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(2450,'采购退货查询',2410,1,'#','','','',1,0,'F','0','0','business:purchaseReturn:query','#','admin','2026-11-22 10:36:44','',NULL,''),(2451,'采购退货新增',2410,2,'#','','','',1,0,'F','0','0','business:purchaseReturn:add','#','admin','2026-11-22 10:36:44','',NULL,''),(2452,'采购退货修改',2410,3,'#','','','',1,0,'F','0','0','business:purchaseReturn:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(2453,'采购退货删除',2410,4,'#','','','',1,0,'F','0','0','business:purchaseReturn:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(2454,'采购退货审核',2410,5,'#','','','',1,0,'F','0','0','business:purchaseReturn:audit','#','admin','2026-11-22 10:36:44','',NULL,''),(2460,'销售退货查询',2411,1,'#','','','',1,0,'F','0','0','business:saleReturn:query','#','admin','2026-11-22 10:36:44','',NULL,''),(2461,'销售退货新增',2411,2,'#','','','',1,0,'F','0','0','business:saleReturn:add','#','admin','2026-11-22 10:36:44','',NULL,''),(2462,'销售退货修改',2411,3,'#','','','',1,0,'F','0','0','business:saleReturn:edit','#','admin','2026-11-22 10:36:44','',NULL,''),(2463,'销售退货删除',2411,4,'#','','','',1,0,'F','0','0','business:saleReturn:remove','#','admin','2026-11-22 10:36:44','',NULL,''),(2464,'销售退货审核',2411,5,'#','','','',1,0,'F','0','0','business:saleReturn:audit','#','admin','2026-11-22 10:36:44','',NULL,''),(2470,'业务链路查询',2000,40,'#','','','',1,0,'F','0','0','business:trace:query','#','admin','2026-11-22 10:36:54','',NULL,'业务链路查询权限'),(2471,'商品导入',2402,6,'#','','','',1,0,'F','0','0','business:product:import','#','admin','2026-11-22 10:36:54','',NULL,''),(2472,'客户导入',2400,6,'#','','','',1,0,'F','0','0','business:customer:import','#','admin','2026-11-22 10:36:54','',NULL,''),(2473,'供应商导入',2401,6,'#','','','',1,0,'F','0','0','business:supplier:import','#','admin','2026-11-22 10:36:54','',NULL,''),(2474,'库存导入',2003,20,'#','','','',1,0,'F','0','0','business:stock:import','#','admin','2026-11-22 10:36:54','',NULL,''),(2475,'统一待办中心',2000,28,'todoCenter','business/todoCenter/index','','',1,0,'C','0','0','business:workbench:query','dashboard','admin','2026-11-22 10:36:54','',NULL,'统一待办中心菜单'),(2510,'销售查询',2261,1,'#','','','',1,0,'F','0','0','business:saleOrder:query','#','admin','2026-11-22 10:36:53','',NULL,''),(2511,'销售新增',2261,2,'#','','','',1,0,'F','0','0','business:saleOrder:add','#','admin','2026-11-22 10:36:53','',NULL,''),(2512,'销售修改',2261,3,'#','','','',1,0,'F','0','0','business:saleOrder:edit','#','admin','2026-11-22 10:36:53','',NULL,''),(2513,'销售删除',2261,4,'#','','','',1,0,'F','0','0','business:saleOrder:remove','#','admin','2026-11-22 10:36:53','',NULL,''),(2514,'销售审批',2261,5,'#','','','',1,0,'F','0','0','business:saleOrder:approve','#','admin','2026-11-22 10:36:53','',NULL,''),(2515,'下推出库',2261,6,'#','','','',1,0,'F','0','0','business:saleOrder:pushOutbound','#','admin','2026-11-22 10:36:53','',NULL,''),(2516,'单据打印',2261,7,'#','','','',1,0,'F','0','0','business:saleOrder:print','#','admin','2026-11-22 10:36:53','',NULL,''),(2517,'报价下推',2260,5,'#','','','',1,0,'F','0','0','business:saleOrder:pushSale','#','admin','2026-11-22 10:36:53','',NULL,''),(2520,'购货查询',2262,1,'#','','','',1,0,'F','0','0','business:purOrder:query','#','admin','2026-11-22 10:36:53','',NULL,''),(2521,'购货新增',2262,2,'#','','','',1,0,'F','0','0','business:purOrder:add','#','admin','2026-11-22 10:36:53','',NULL,''),(2522,'购货修改',2262,3,'#','','','',1,0,'F','0','0','business:purOrder:edit','#','admin','2026-11-22 10:36:53','',NULL,''),(2523,'购货删除',2262,4,'#','','','',1,0,'F','0','0','business:purOrder:remove','#','admin','2026-11-22 10:36:53','',NULL,''),(2524,'购货审批',2262,5,'#','','','',1,0,'F','0','0','business:purOrder:approve','#','admin','2026-11-22 10:36:53','',NULL,''),(2525,'下推入库',2262,6,'#','','','',1,0,'F','0','0','business:purOrder:pushInbound','#','admin','2026-11-22 10:36:53','',NULL,''),(2526,'单据打印',2262,7,'#','','','',1,0,'F','0','0','business:purOrder:print','#','admin','2026-11-22 10:36:53','',NULL,''),(2530,'发票查询',2263,1,'#','','','',1,0,'F','0','0','business:invoice:query','#','admin','2026-11-22 10:36:53','',NULL,''),(2531,'发票新增',2263,2,'#','','','',1,0,'F','0','0','business:invoice:add','#','admin','2026-11-22 10:36:53','',NULL,''),(2532,'发票修改',2263,3,'#','','','',1,0,'F','0','0','business:invoice:edit','#','admin','2026-11-22 10:36:53','',NULL,''),(2533,'发票删除',2263,4,'#','','','',1,0,'F','0','0','business:invoice:remove','#','admin','2026-11-22 10:36:53','',NULL,''),(2534,'发票确认',2263,5,'#','','','',1,0,'F','0','0','business:invoice:confirm','#','admin','2026-11-22 10:36:53','',NULL,''),(2540,'等级价查询',2265,1,'#','','','',1,0,'F','0','0','business:customerLevelPrice:query','#','admin','2026-11-22 10:36:53','',NULL,''),(2541,'等级价新增',2265,2,'#','','','',1,0,'F','0','0','business:customerLevelPrice:add','#','admin','2026-11-22 10:36:53','',NULL,''),(2542,'等级价修改',2265,3,'#','','','',1,0,'F','0','0','business:customerLevelPrice:edit','#','admin','2026-11-22 10:36:53','',NULL,''),(2543,'等级价删除',2265,4,'#','','','',1,0,'F','0','0','business:customerLevelPrice:remove','#','admin','2026-11-22 10:36:53','',NULL,''),(2550,'专属价查询',2266,1,'#','','','',1,0,'F','0','0','business:customerSpecialPrice:query','#','admin','2026-11-22 10:36:53','',NULL,''),(2551,'专属价新增',2266,2,'#','','','',1,0,'F','0','0','business:customerSpecialPrice:add','#','admin','2026-11-22 10:36:53','',NULL,''),(2552,'专属价修改',2266,3,'#','','','',1,0,'F','0','0','business:customerSpecialPrice:edit','#','admin','2026-11-22 10:36:53','',NULL,''),(2553,'专属价删除',2266,4,'#','','','',1,0,'F','0','0','business:customerSpecialPrice:remove','#','admin','2026-11-22 10:36:53','',NULL,''),(2560,'最近价查询',2267,1,'#','','','',1,0,'F','0','0','business:recentTransactionPrice:query','#','admin','2026-11-22 10:36:53','',NULL,''),(2561,'最近价新增',2267,2,'#','','','',1,0,'F','0','0','business:recentTransactionPrice:add','#','admin','2026-11-22 10:36:53','',NULL,''),(2562,'最近价修改',2267,3,'#','','','',1,0,'F','0','0','business:recentTransactionPrice:edit','#','admin','2026-11-22 10:36:53','',NULL,''),(2563,'最近价删除',2267,4,'#','','','',1,0,'F','0','0','business:recentTransactionPrice:remove','#','admin','2026-11-22 10:36:53','',NULL,''),(2570,'补货查询',2268,1,'#','','','',1,0,'F','0','0','business:replenishment:query','#','admin','2026-11-22 10:36:53','',NULL,''),(2580,'工作台查询',2270,1,'#','','','',1,0,'F','0','0','business:workbench:query','#','admin','2026-11-22 10:36:53','',NULL,''),(2590,'扫码查询',2271,1,'#','','','',1,0,'F','0','0','business:scan:query','#','admin','2026-11-22 10:36:53','',NULL,''),(2591,'扫码开单',2271,2,'#','','','',1,0,'F','0','0','business:scan:add','#','admin','2026-11-22 10:36:53','',NULL,''),(2592,'扫码盘点',2271,3,'#','','','',1,0,'F','0','0','business:scan:inventory','#','admin','2026-11-22 10:36:53','',NULL,''),(2593,'扫码调拨',2271,4,'#','','','',1,0,'F','0','0','business:scan:transfer','#','admin','2026-11-22 10:36:53','',NULL,''),(2600,'基础总账接口',2200,15,'generalLedger','business/generalLedger/index','','',1,0,'C','0','0','business:generalLedger:query','form','admin','2026-11-22 10:36:54','',NULL,'基础总账接口菜单'),(2601,'经营分析报表',2200,16,'operationReport','business/operationReport/index','','',1,0,'C','0','0','business:report:query','chart','admin','2026-11-22 10:36:54','',NULL,'经营分析报表菜单'),(2610,'凭证生成',2269,1,'#','','','',1,0,'F','0','0','business:voucherEvent:generate','#','admin','2026-11-22 10:36:54','',NULL,''),(2611,'凭证回写',2269,2,'#','','','',1,0,'F','0','0','business:voucherEvent:writeback','#','admin','2026-11-22 10:36:54','',NULL,''),(2612,'凭证冲销',2269,3,'#','','','',1,0,'F','0','0','business:voucherEvent:reverse','#','admin','2026-11-22 10:36:54','',NULL,''),(2613,'凭证查询',2269,4,'#','','','',1,0,'F','0','0','business:voucherEvent:query','#','admin','2026-11-22 10:36:54','',NULL,''),(2614,'凭证删除',2269,5,'#','','','',1,0,'F','0','0','business:voucherEvent:remove','#','admin','2026-11-22 10:36:54','',NULL,''),(2620,'总账查询',2600,1,'#','','','',1,0,'F','0','0','business:generalLedger:query','#','admin','2026-11-22 10:36:54','',NULL,''),(2621,'期间结账',2600,2,'#','','','',1,0,'F','0','0','business:generalLedger:close','#','admin','2026-11-22 10:36:54','',NULL,''),(2622,'期间反结账',2600,3,'#','','','',1,0,'F','0','0','business:generalLedger:reopen','#','admin','2026-11-22 10:36:54','',NULL,''),(2700,'库位管理',2001,13,'location','business/location/index','','',1,0,'C','0','0','business:location:list','tree','admin','2026-11-22 10:36:54','',NULL,'库位档案菜单'),(2701,'库位查询',2700,1,'#','','','',1,0,'F','0','0','business:location:query','#','admin','2026-11-22 10:36:54','',NULL,''),(2702,'库位新增',2700,2,'#','','','',1,0,'F','0','0','business:location:add','#','admin','2026-11-22 10:36:54','',NULL,''),(2703,'库位修改',2700,3,'#','','','',1,0,'F','0','0','business:location:edit','#','admin','2026-11-22 10:36:54','',NULL,''),(2704,'库位删除',2700,4,'#','','','',1,0,'F','0','0','business:location:remove','#','admin','2026-11-22 10:36:54','',NULL,''),(2710,'费用类别',2200,17,'expenseCategory','business/expenseCategory/index','','',1,0,'C','0','0','business:expenseCategory:list','tree-table','admin','2026-11-22 10:36:54','',NULL,'费用类别菜单'),(2711,'费用类别查询',2710,1,'#','','','',1,0,'F','0','0','business:expenseCategory:query','#','admin','2026-11-22 10:36:54','',NULL,''),(2712,'费用类别新增',2710,2,'#','','','',1,0,'F','0','0','business:expenseCategory:add','#','admin','2026-11-22 10:36:54','',NULL,''),(2713,'费用类别修改',2710,3,'#','','','',1,0,'F','0','0','business:expenseCategory:edit','#','admin','2026-11-22 10:36:54','',NULL,''),(2714,'费用类别删除',2710,4,'#','','','',1,0,'F','0','0','business:expenseCategory:remove','#','admin','2026-11-22 10:36:54','',NULL,''),(2720,'成本账户',2200,18,'costAccount','business/costAccount/index','','',1,0,'C','0','0','business:costAccount:list','money','admin','2026-11-22 10:36:54','',NULL,'成本账户菜单'),(2721,'成本账户查询',2720,1,'#','','','',1,0,'F','0','0','business:costAccount:query','#','admin','2026-11-22 10:36:54','',NULL,''),(2722,'成本账户新增',2720,2,'#','','','',1,0,'F','0','0','business:costAccount:add','#','admin','2026-11-22 10:36:54','',NULL,''),(2723,'成本账户修改',2720,3,'#','','','',1,0,'F','0','0','business:costAccount:edit','#','admin','2026-11-22 10:36:54','',NULL,''),(2724,'成本账户删除',2720,4,'#','','','',1,0,'F','0','0','business:costAccount:remove','#','admin','2026-11-22 10:36:54','',NULL,''),(2730,'成本分层',2200,19,'costLayer','business/costLayer/index','','',1,0,'C','0','0','business:costLayer:list','list','admin','2026-11-22 10:36:54','',NULL,'成本分层菜单'),(2731,'成本分层查询',2730,1,'#','','','',1,0,'F','0','0','business:costLayer:query','#','admin','2026-11-22 10:36:54','',NULL,''),(2732,'成本分层新增',2730,2,'#','','','',1,0,'F','0','0','business:costLayer:add','#','admin','2026-11-22 10:36:54','',NULL,''),(2733,'成本分层修改',2730,3,'#','','','',1,0,'F','0','0','business:costLayer:edit','#','admin','2026-11-22 10:36:54','',NULL,''),(2734,'成本分层删除',2730,4,'#','','','',1,0,'F','0','0','business:costLayer:remove','#','admin','2026-11-22 10:36:54','',NULL,''),(2740,'成本流水',2200,20,'costLog','business/costLog/index','','',1,0,'C','0','0','business:costLog:list','log','admin','2026-11-22 10:36:54','',NULL,'成本流水菜单'),(2741,'成本流水查询',2740,1,'#','','','',1,0,'F','0','0','business:costLog:query','#','admin','2026-11-22 10:36:54','',NULL,''),(2742,'成本流水新增',2740,2,'#','','','',1,0,'F','0','0','business:costLog:add','#','admin','2026-11-22 10:36:54','',NULL,''),(2743,'成本流水修改',2740,3,'#','','','',1,0,'F','0','0','business:costLog:edit','#','admin','2026-11-22 10:36:54','',NULL,''),(2744,'成本流水删除',2740,4,'#','','','',1,0,'F','0','0','business:costLog:remove','#','admin','2026-11-22 10:36:54','',NULL,''),(2750,'数据备份',3,4,'dataBackup','business/dataBackup/index','','',1,0,'C','0','0','business:dataBackup:list','download','admin','2026-11-22 10:36:54','',NULL,'数据备份说明菜单'),(2760,'盘点审核',2006,5,'#','','','',1,0,'F','0','0','business:inventoryCheck:audit','#','admin','2026-11-22 10:36:54','',NULL,''),(2761,'调拨审核',2007,6,'#','','','',1,0,'F','0','0','business:transfer:audit','#','admin','2026-11-22 10:36:54','',NULL,''),(2762,'应收审核',2201,5,'#','','','',1,0,'F','0','0','business:receivable:audit','#','admin','2026-11-22 10:36:54','',NULL,''),(2763,'应付审核',2202,5,'#','','','',1,0,'F','0','0','business:payable:audit','#','admin','2026-11-22 10:36:54','',NULL,''),(2764,'出库导入',2005,9,'#','','','',1,0,'F','0','0','business:outbound:import','#','admin','2026-11-22 10:36:54','',NULL,''),(2765,'采购退货明细列表',2410,6,'#','','','',1,0,'F','0','0','business:purchaseReturnItem:list','#','admin','2026-11-22 10:36:54','',NULL,''),(2766,'采购退货明细查询',2410,7,'#','','','',1,0,'F','0','0','business:purchaseReturnItem:query','#','admin','2026-11-22 10:36:54','',NULL,''),(2767,'采购退货明细新增',2410,8,'#','','','',1,0,'F','0','0','business:purchaseReturnItem:add','#','admin','2026-11-22 10:36:54','',NULL,''),(2768,'采购退货明细修改',2410,9,'#','','','',1,0,'F','0','0','business:purchaseReturnItem:edit','#','admin','2026-11-22 10:36:54','',NULL,''),(2769,'采购退货明细删除',2410,10,'#','','','',1,0,'F','0','0','business:purchaseReturnItem:remove','#','admin','2026-11-22 10:36:54','',NULL,''),(2770,'销售退货明细列表',2411,6,'#','','','',1,0,'F','0','0','business:saleReturnItem:list','#','admin','2026-11-22 10:36:54','',NULL,''),(2771,'销售退货明细查询',2411,7,'#','','','',1,0,'F','0','0','business:saleReturnItem:query','#','admin','2026-11-22 10:36:54','',NULL,''),(2772,'销售退货明细新增',2411,8,'#','','','',1,0,'F','0','0','business:saleReturnItem:add','#','admin','2026-11-22 10:36:54','',NULL,''),(2773,'销售退货明细修改',2411,9,'#','','','',1,0,'F','0','0','business:saleReturnItem:edit','#','admin','2026-11-22 10:36:54','',NULL,''),(2774,'销售退货明细删除',2411,10,'#','','','',1,0,'F','0','0','business:saleReturnItem:remove','#','admin','2026-11-22 10:36:54','',NULL,''),(2780,'坏账确认',2208,5,'#','','','',1,0,'F','0','0','business:badDebt:confirm','#','admin','2026-11-22 10:57:18','',NULL,''),(2781,'调拨明细列表',2007,7,'#','','','',1,0,'F','0','0','business:transferItem:list','#','admin','2026-11-22 10:57:18','',NULL,''),(2782,'调拨明细查询',2007,8,'#','','','',1,0,'F','0','0','business:transferItem:query','#','admin','2026-11-22 10:57:18','',NULL,''),(2783,'调拨明细新增',2007,9,'#','','','',1,0,'F','0','0','business:transferItem:add','#','admin','2026-11-22 10:57:18','',NULL,''),(2784,'调拨明细修改',2007,10,'#','','','',1,0,'F','0','0','business:transferItem:edit','#','admin','2026-11-22 10:57:18','',NULL,''),(2785,'调拨明细删除',2007,11,'#','','','',1,0,'F','0','0','business:transferItem:remove','#','admin','2026-11-22 10:57:18','',NULL,''),(2786,'盘点明细列表',2006,6,'#','','','',1,0,'F','0','0','business:inventoryCheckItem:list','#','admin','2026-11-22 10:57:18','',NULL,''),(2787,'盘点明细查询',2006,7,'#','','','',1,0,'F','0','0','business:inventoryCheckItem:query','#','admin','2026-11-22 10:57:18','',NULL,''),(2788,'盘点明细新增',2006,8,'#','','','',1,0,'F','0','0','business:inventoryCheckItem:add','#','admin','2026-11-22 10:57:19','',NULL,''),(2789,'盘点明细修改',2006,9,'#','','','',1,0,'F','0','0','business:inventoryCheckItem:edit','#','admin','2026-11-22 10:57:19','',NULL,''),(2790,'盘点明细删除',2006,10,'#','','','',1,0,'F','0','0','business:inventoryCheckItem:remove','#','admin','2026-11-22 10:57:19','',NULL,''),(2791,'创建备份',2750,1,'#','','','',1,0,'F','0','0','business:dataBackup:add','#','admin','2026-11-22 15:09:40','',NULL,''),(2792,'下载备份',2750,2,'#','','','',1,0,'F','0','0','business:dataBackup:download','#','admin','2026-11-22 15:09:40','',NULL,''),(2793,'删除备份',2750,3,'#','','','',1,0,'F','0','0','business:dataBackup:remove','#','admin','2026-11-22 15:09:40','',NULL,''),(2794,'校验备份',2750,4,'#','','','',1,0,'F','0','0','business:dataBackup:verify','#','admin','2026-11-22 16:46:36','',NULL,''),(2795,'受控恢复备份',2750,5,'#','','','',1,0,'F','0','0','business:dataBackup:restore','#','admin','2026-11-22 16:46:36','',NULL,''),(2796,'报表导出',2210,2,'#','','','',1,0,'F','0','0','business:report:export','#','admin','2026-11-22 16:46:36','',NULL,''),(2797,'成本期末重算',2209,3,'#','','','',1,0,'F','0','0','business:costCalculate:recalculate','#','admin','2026-11-22 16:46:36','',NULL,''),(2798,'受控成本调价',2209,4,'#','','','',1,0,'F','0','0','business:costCalculate:adjust','#','admin','2026-11-22 16:46:36','',NULL,''),(2800,'序列号溯源',2001,14,'serialNo','business/serialNo/index','','',1,0,'C','0','0','business:serialNo:list','barcode','admin','2026-11-22 17:43:31','',NULL,'SN一物一码全生命周期追踪'),(2801,'采购订单反审批/关闭',2262,6,'#','','','',1,0,'F','0','0','business:purOrder:close','#','admin','2026-11-22 17:43:31','',NULL,''),(2802,'销售订单反审批/关闭',2261,6,'#','','','',1,0,'F','0','0','business:saleOrder:close','#','admin','2026-11-22 17:43:31','',NULL,''),(2803,'入库单反审核',2004,6,'#','','','',1,0,'F','0','0','business:inbound:unaudit','#','admin','2026-11-22 17:43:31','',NULL,''),(2804,'入库单红冲',2004,7,'#','','','',1,0,'F','0','0','business:inbound:reverse','#','admin','2026-11-22 17:43:31','',NULL,''),(2805,'出库单反审核',2005,8,'#','','','',1,0,'F','0','0','business:outbound:unaudit','#','admin','2026-11-22 17:43:31','',NULL,''),(2806,'出库单红冲',2005,9,'#','','','',1,0,'F','0','0','business:outbound:reverse','#','admin','2026-11-22 17:43:31','',NULL,''),(2807,'调拨单反审核/红冲',2007,6,'#','','','',1,0,'F','0','0','business:transfer:unaudit','#','admin','2026-11-22 17:43:31','',NULL,''),(2808,'盘点单反审核/红冲',2006,6,'#','','','',1,0,'F','0','0','business:inventoryCheck:unaudit','#','admin','2026-11-22 17:43:31','',NULL,''),(2809,'采购退货反审核/红冲',2410,6,'#','','','',1,0,'F','0','0','business:purchaseReturn:unaudit','#','admin','2026-11-22 17:43:31','',NULL,''),(2810,'销售退货反审核/红冲',2411,6,'#','','','',1,0,'F','0','0','business:saleReturn:unaudit','#','admin','2026-11-22 17:43:31','',NULL,''),(2811,'资金流水与互转',2205,6,'#','','','',1,0,'F','0','0','business:fundAccount:transfer','#','admin','2026-11-22 17:43:31','',NULL,''),(2812,'收款确认/反确认',2203,6,'#','','','',1,0,'F','0','0','business:receipt:confirm','#','admin','2026-11-22 17:43:31','',NULL,''),(2813,'付款确认/反确认',2204,6,'#','','','',1,0,'F','0','0','business:payment:confirm','#','admin','2026-11-22 17:43:31','',NULL,''),(2814,'坏账收回',2208,6,'#','','','',1,0,'F','0','0','business:badDebt:recover','#','admin','2026-11-22 17:43:31','',NULL,''),(2815,'红字发票冲销',2263,6,'#','','','',1,0,'F','0','0','business:invoice:red','#','admin','2026-11-22 17:43:31','',NULL,''),(2816,'总账科目维护与建期',2600,5,'#','','','',1,0,'F','0','0','business:generalLedger:edit','#','admin','2026-11-22 17:43:31','',NULL,''),(2817,'凭证复式导出',2269,6,'#','','','',1,0,'F','0','0','business:voucherEvent:export','#','admin','2026-11-22 17:43:31','',NULL,''),(2818,'库存冻结与解冻',2003,6,'#','','','',1,0,'F','0','0','business:stock:freeze','#','admin','2026-11-22 17:43:31','',NULL,''),(2819,'序列号登记与维护',2800,1,'#','','','',1,0,'F','0','0','business:serialNo:edit','#','admin','2026-11-22 17:43:31','',NULL,''),(2820,'价格实时解析查询',2265,6,'#','','','',1,0,'F','0','0','business:price:resolve','#','admin','2026-11-22 17:43:31','',NULL,''),(2821,'核销反确认',2207,6,'#','','','',1,0,'F','0','0','business:writeOff:unconfirm','#','admin','2026-11-22 20:56:10','',NULL,''),(2822,'坏账反确认',2208,7,'#','','','',1,0,'F','0','0','business:badDebt:unconfirm','#','admin','2026-11-22 20:56:10','',NULL,''),(2823,'互转反确认',2205,6,'#','','','',1,0,'F','0','0','business:fundAccount:transferUnconfirm','#','admin','2026-11-22 20:56:10','',NULL,''),(2824,'应收撤销对冲',2201,6,'#','','','',1,0,'F','0','0','business:receivable:unoffset','#','admin','2026-11-22 20:56:10','',NULL,''),(2825,'应付撤销对冲',2202,6,'#','','','',1,0,'F','0','0','business:payable:unoffset','#','admin','2026-11-22 20:56:10','',NULL,''),(2826,'一键补生成凭证',2269,7,'#','','','',1,0,'F','0','0','business:voucherEvent:regenerate','#','admin','2026-11-22 20:56:10','',NULL,''),(2827,'出库确认与退货',2005,11,'#','','','',1,0,'F','0','0','business:outbound:verify','#','admin','2026-11-22 20:56:10','',NULL,''),(2828,'库存导入模板',2003,6,'#','','','',1,0,'F','0','0','business:stock:importTemplate','#','admin','2026-11-22 20:56:10','',NULL,'');
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_notice`
--

DROP TABLE IF EXISTS `sys_notice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_notice` (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告标题',
  `notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='通知公告表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_notice`
--

LOCK TABLES `sys_notice` WRITE;
/*!40000 ALTER TABLE `sys_notice` DISABLE KEYS */;
INSERT INTO `sys_notice` VALUES (1,'温馨提醒：2018-07-01 仓链新版本发布啦','2',_binary '新版本内容','0','admin','2026-11-22 10:36:47','',NULL,'管理员'),(2,'维护通知：2018-07-01 仓链系统凌晨维护','1',_binary '维护内容','0','admin','2026-11-22 10:36:47','',NULL,'管理员');
/*!40000 ALTER TABLE `sys_notice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_oper_log`
--

DROP TABLE IF EXISTS `sys_oper_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_oper_log` (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '模块标题',
  `business_type` int DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '请求方式',
  `operator_type` int DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '返回参数',
  `status` int DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`),
  KEY `idx_sys_oper_log_bt` (`business_type`),
  KEY `idx_sys_oper_log_s` (`status`),
  KEY `idx_sys_oper_log_ot` (`oper_time`)
) ENGINE=InnoDB AUTO_INCREMENT=543 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_oper_log`
--

LOCK TABLES `sys_oper_log` WRITE;
/*!40000 ALTER TABLE `sys_oper_log` DISABLE KEYS */;
INSERT INTO `sys_oper_log` VALUES (100,'仓库档案',1,'com.canglian.web.controller.business.MdWarehouseController.add()','POST',1,'admin','研发部门','/business/warehouse','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"params\":{},\"status\":\"0\",\"warehouseCode\":\"WH1_E2E_114010\",\"warehouseId\":102,\"warehouseName\":\"E2E主仓\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:09',17),(101,'仓库档案',1,'com.canglian.web.controller.business.MdWarehouseController.add()','POST',1,'admin','研发部门','/business/warehouse','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"params\":{},\"status\":\"0\",\"warehouseCode\":\"WH2_E2E_114010\",\"warehouseId\":103,\"warehouseName\":\"E2E分仓\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:09',7),(102,'库位档案',1,'com.canglian.web.controller.business.MdLocationController.add()','POST',1,'admin','研发部门','/business/location','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2026-11-22 11:40:11\",\"locationCode\":\"L1_E2E_114010\",\"locationId\":2001,\"locationName\":\"A1库位\",\"params\":{},\"status\":\"0\",\"warehouseId\":102} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:09',13),(103,'库位档案',1,'com.canglian.web.controller.business.MdLocationController.add()','POST',1,'admin','研发部门','/business/location','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2026-11-22 11:40:11\",\"locationCode\":\"L2_E2E_114010\",\"locationId\":2002,\"locationName\":\"A2库位\",\"params\":{},\"status\":\"0\",\"warehouseId\":102} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:09',8),(104,'商品档案',1,'com.canglian.web.controller.business.MdProductController.add()','POST',1,'admin','研发部门','/business/product','127.0.0.1','内网IP','{\"barCode\":\"BAR_E2E_114010\",\"costPrice\":10,\"createBy\":\"admin\",\"params\":{},\"productCode\":\"P_E2E_114010\",\"productId\":102,\"productName\":\"E2E测试商品\",\"salePrice\":25,\"status\":\"0\",\"warningMaxQty\":0,\"warningMinQty\":5} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:09',15),(105,'客户档案',1,'com.canglian.web.controller.business.MdCustomerController.add()','POST',1,'admin','研发部门','/business/customer','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"creditLimit\":5000,\"customerCode\":\"CUS_E2E_114010\",\"customerId\":101,\"customerName\":\"E2E客户\",\"params\":{},\"receivableDays\":30,\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:09',12),(106,'供应商档案',1,'com.canglian.web.controller.business.MdSupplierController.add()','POST',1,'admin','研发部门','/business/supplier','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"params\":{},\"payableDays\":30,\"status\":\"0\",\"supplierCode\":\"SUP_E2E_114010\",\"supplierId\":101,\"supplierName\":\"E2E供应商\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:09',13),(107,'资金账户',1,'com.canglian.web.controller.business.FinFundAccountController.add()','POST',1,'admin','研发部门','/business/fundAccount','127.0.0.1','内网IP','{\"accountId\":2,\"accountName\":\"E2E测试户\",\"accountNo\":\"FA_E2E_114010\",\"accountType\":\"bank\",\"balance\":10000,\"createBy\":\"admin\",\"params\":{},\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:09',11),(108,'购货订单',1,'com.canglian.web.controller.business.PurOrderController.add()','POST',1,'admin','研发部门','/business/purOrder','127.0.0.1','内网IP','{\"approveBy\":\"\",\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"params\":{},\"purchaseOrderNo\":\"pur20261122001\",\"status\":\"0\",\"supplierId\":101,\"warehouseId\":102} ',NULL,1,'购货订单明细不能为空','2026-11-22 11:40:09',25),(109,'销售单据',1,'com.canglian.web.controller.business.SalOrderController.add()','POST',1,'admin','研发部门','/business/saleOrder','127.0.0.1','内网IP','{\"approveBy\":\"\",\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"customerId\":101,\"orderNo\":\"SO_E2E_114010\",\"orderType\":\"order\",\"params\":{},\"status\":\"0\",\"warehouseId\":102} ',NULL,1,'销售单据明细不能为空','2026-11-22 11:40:10',8),(110,'发票登记',1,'com.canglian.web.controller.business.FinInvoiceController.add()','POST',1,'admin','研发部门','/business/invoice','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"customerId\":101,\"invoiceAmount\":1000,\"invoiceDate\":\"2026-11-22 00:00:00\",\"invoiceId\":1,\"invoiceNo\":\"sin20261122001\",\"invoiceType\":\"sale\",\"params\":{},\"sourceBillNo\":\"SO_E2E_114010\",\"sourceBillType\":\"sal_order\",\"status\":\"0\",\"taxAmount\":115.04,\"taxRate\":0.13,\"untaxedAmount\":884.96} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:10',22),(111,'发票登记确认',2,'com.canglian.web.controller.business.FinInvoiceController.confirm()','PUT',1,'admin','研发部门','/business/invoice/confirm/1','127.0.0.1','内网IP','1 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:10',46),(112,'收款单',1,'com.canglian.web.controller.business.FinReceiptController.add()','POST',1,'admin','研发部门','/business/receipt','127.0.0.1','内网IP','{\"amount\":-10,\"createBy\":\"admin\",\"customerId\":101,\"fundAccountId\":2,\"params\":{},\"receiptDate\":\"2026-11-22 00:00:00\",\"receiptNo\":\"rec20261122001\"} ',NULL,1,'收款金额必须大于0','2026-11-22 11:40:10',20),(113,'收款单',1,'com.canglian.web.controller.business.FinReceiptController.add()','POST',1,'admin','研发部门','/business/receipt','127.0.0.1','内网IP','{\"amount\":300,\"createBy\":\"admin\",\"customerId\":101,\"fundAccountId\":2,\"params\":{},\"receiptDate\":\"2026-11-22 00:00:00\",\"receiptId\":2,\"receiptNo\":\"rec20261122001\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:10',29),(114,'费用',1,'com.canglian.web.controller.business.FinExpenseController.add()','POST',1,'admin','研发部门','/business/expense','127.0.0.1','内网IP','{\"amount\":150,\"createBy\":\"admin\",\"expenseDate\":\"2026-11-22 00:00:00\",\"expenseId\":2,\"expenseNo\":\"exp202611222080\",\"fundAccountId\":2,\"params\":{},\"remark\":\"E2E_114010\",\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:10',13),(115,'费用确认',2,'com.canglian.web.controller.business.FinExpenseController.confirm()','PUT',1,'admin','研发部门','/business/expense/confirm/2','127.0.0.1','内网IP','2 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:10',30),(116,'资金账户',3,'com.canglian.web.controller.business.FinFundAccountController.remove()','DELETE',1,'admin','研发部门','/business/fundAccount/2','127.0.0.1','内网IP','[2] ',NULL,1,'资金账户[E2E测试户]余额不为0，不允许删除','2026-11-22 11:40:10',5),(117,'收款单',1,'com.canglian.web.controller.business.FinReceiptController.add()','POST',1,'admin','研发部门','/business/receipt','127.0.0.1','内网IP','{\"amount\":200,\"createBy\":\"admin\",\"customerId\":101,\"fundAccountId\":2,\"params\":{},\"receiptDate\":\"2026-11-22 00:00:00\",\"receiptId\":3,\"receiptNo\":\"rec20261122002\",\"remark\":\"ADV_E2E_114010\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:10',25),(118,'核销',1,'com.canglian.web.controller.business.FinWriteOffController.add()','POST',1,'admin','研发部门','/business/writeOff','127.0.0.1','内网IP','{\"amount\":200,\"createBy\":\"admin\",\"params\":{},\"receiptId\":3,\"writeOffDate\":\"2026-11-22 00:00:00\",\"writeOffNo\":\"wof202611222269\",\"writeOffType\":\"receivable\"} ',NULL,1,'应收核销必须选择应收单','2026-11-22 11:40:10',6),(119,'核销确认',2,'com.canglian.web.controller.business.FinWriteOffController.confirm()','PUT',1,'admin','研发部门','/business/writeOff/confirm/1','127.0.0.1','内网IP','1 ',NULL,1,'核销单已确认，请勿重复操作','2026-11-22 11:40:10',5),(120,'坏账',1,'com.canglian.web.controller.business.FinBadDebtController.add()','POST',1,'admin','研发部门','/business/badDebt','127.0.0.1','内网IP','{\"amount\":50,\"badDebtDate\":\"2026-11-22 00:00:00\",\"badDebtNo\":\"bd202611222327\",\"createBy\":\"admin\",\"customerId\":101,\"params\":{},\"reason\":\"E2E坏账测试\"} ',NULL,1,'关联应收单不能为空','2026-11-22 11:40:10',5),(121,'坏账确认',2,'com.canglian.web.controller.business.FinBadDebtController.confirm()','PUT',1,'admin','研发部门','/business/badDebt/confirm/1','127.0.0.1','内网IP','1 ',NULL,1,'坏账单已确认，请勿重复操作','2026-11-22 11:40:10',6),(122,'坏账',3,'com.canglian.web.controller.business.FinBadDebtController.remove()','DELETE',1,'admin','研发部门','/business/badDebt/1','127.0.0.1','内网IP','[1] ',NULL,1,'坏账单已确认，不允许修改或删除','2026-11-22 11:40:10',4),(123,'调拨单',1,'com.canglian.web.controller.business.WmsTransferController.add()','POST',1,'admin','研发部门','/business/transfer','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"inWarehouseId\":103,\"itemList\":[{\"amount\":0,\"batchNo\":\"\",\"inLocationId\":0,\"outLocationId\":0,\"productId\":102,\"quantity\":10,\"transferId\":101,\"transferItemId\":101}],\"outWarehouseId\":102,\"params\":{},\"status\":\"0\",\"totalAmount\":0,\"totalQty\":10,\"transferId\":101,\"transferNo\":\"trf20261122001\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:10',29),(124,'调拨审核',2,'com.canglian.web.controller.business.WmsTransferController.audit()','PUT',1,'admin','研发部门','/business/transfer/audit/101','127.0.0.1','内网IP','101 ',NULL,1,'库存不足，无法调出','2026-11-22 11:40:10',21),(125,'调拨单',1,'com.canglian.web.controller.business.WmsTransferController.add()','POST',1,'admin','研发部门','/business/transfer','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"inWarehouseId\":102,\"itemList\":[{\"amount\":0,\"batchNo\":\"\",\"inLocationId\":2001,\"outLocationId\":0,\"productId\":102,\"quantity\":5,\"transferId\":102,\"transferItemId\":102}],\"outWarehouseId\":102,\"params\":{},\"status\":\"0\",\"totalAmount\":0,\"totalQty\":5,\"transferId\":102,\"transferNo\":\"trf20261122002\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:10',20),(126,'调拨审核',2,'com.canglian.web.controller.business.WmsTransferController.audit()','PUT',1,'admin','研发部门','/business/transfer/audit/102','127.0.0.1','内网IP','102 ',NULL,1,'库存不足，无法调出','2026-11-22 11:40:11',19),(127,'盘点单',1,'com.canglian.web.controller.business.WmsInventoryCheckController.add()','POST',1,'admin','研发部门','/business/inventoryCheck','127.0.0.1','内网IP','{\"checkId\":101,\"checkNo\":\"chk20261122001\",\"createBy\":\"admin\",\"itemList\":[{\"batchNo\":\"\",\"checkId\":101,\"checkItemId\":101,\"diffAmount\":0,\"diffQty\":0,\"locationId\":0,\"productId\":102,\"stockQty\":0}],\"params\":{},\"status\":\"0\",\"totalDiffAmount\":0,\"totalDiffQty\":0,\"warehouseId\":103} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:11',34),(128,'盘点审核',2,'com.canglian.web.controller.business.WmsInventoryCheckController.audit()','PUT',1,'admin','研发部门','/business/inventoryCheck/audit/101','127.0.0.1','内网IP','101 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:11',21),(129,'销售退货',1,'com.canglian.web.controller.business.WmsSaleReturnController.add()','POST',1,'admin','研发部门','/business/saleReturn','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 11:40:12.684\",\"createBy\":\"admin\",\"customerId\":101,\"itemList\":[{\"amount\":0,\"batchNo\":\"\",\"locationId\":0,\"params\":{},\"productId\":102,\"quantity\":5,\"saleReturnId\":1,\"saleReturnItemId\":1}],\"params\":{},\"returnNo\":\"srt20261122001\",\"saleReturnId\":1,\"status\":\"0\",\"totalAmount\":0,\"totalQty\":5,\"warehouseId\":102} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:11',29),(130,'销售退货审核',2,'com.canglian.web.controller.business.WmsSaleReturnController.audit()','PUT',1,'admin','研发部门','/business/saleReturn/audit/1','127.0.0.1','内网IP','1 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:11',60),(131,'采购退货',1,'com.canglian.web.controller.business.WmsPurchaseReturnController.add()','POST',1,'admin','研发部门','/business/purchaseReturn','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 11:40:12.809\",\"createBy\":\"admin\",\"itemList\":[{\"amount\":0,\"batchNo\":\"\",\"locationId\":0,\"params\":{},\"productId\":102,\"purchaseReturnId\":1,\"purchaseReturnItemId\":1,\"quantity\":5}],\"params\":{},\"purchaseReturnId\":1,\"returnNo\":\"prt20261122001\",\"status\":\"0\",\"supplierId\":101,\"totalAmount\":0,\"totalQty\":5,\"warehouseId\":102} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:11',29),(132,'采购退货审核',2,'com.canglian.web.controller.business.WmsPurchaseReturnController.audit()','PUT',1,'admin','研发部门','/business/purchaseReturn/audit/1','127.0.0.1','内网IP','1 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:40:11',49),(133,'仓库档案',1,'com.canglian.web.controller.business.MdWarehouseController.add()','POST',1,'admin','研发部门','/business/warehouse','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"params\":{},\"status\":\"0\",\"warehouseCode\":\"WH1_E2E_114156\",\"warehouseId\":104,\"warehouseName\":\"E2E主仓\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:55',8),(134,'仓库档案',1,'com.canglian.web.controller.business.MdWarehouseController.add()','POST',1,'admin','研发部门','/business/warehouse','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"params\":{},\"status\":\"0\",\"warehouseCode\":\"WH2_E2E_114156\",\"warehouseId\":105,\"warehouseName\":\"E2E分仓\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:55',5),(135,'库位档案',1,'com.canglian.web.controller.business.MdLocationController.add()','POST',1,'admin','研发部门','/business/location','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2026-11-22 11:41:56\",\"locationCode\":\"L1_E2E_114156\",\"locationId\":2003,\"locationName\":\"A1库位\",\"params\":{},\"status\":\"0\",\"warehouseId\":104} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:55',5),(136,'库位档案',1,'com.canglian.web.controller.business.MdLocationController.add()','POST',1,'admin','研发部门','/business/location','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2026-11-22 11:41:56\",\"locationCode\":\"L2_E2E_114156\",\"locationId\":2004,\"locationName\":\"A2库位\",\"params\":{},\"status\":\"0\",\"warehouseId\":104} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:55',6),(137,'商品档案',1,'com.canglian.web.controller.business.MdProductController.add()','POST',1,'admin','研发部门','/business/product','127.0.0.1','内网IP','{\"barCode\":\"BAR_E2E_114156\",\"costPrice\":10,\"createBy\":\"admin\",\"params\":{},\"productCode\":\"P_E2E_114156\",\"productId\":103,\"productName\":\"E2E测试商品\",\"salePrice\":25,\"status\":\"0\",\"warningMaxQty\":0,\"warningMinQty\":5} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:55',7),(138,'客户档案',1,'com.canglian.web.controller.business.MdCustomerController.add()','POST',1,'admin','研发部门','/business/customer','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"creditLimit\":5000,\"customerCode\":\"CUS_E2E_114156\",\"customerId\":102,\"customerName\":\"E2E客户\",\"params\":{},\"receivableDays\":30,\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:55',7),(139,'供应商档案',1,'com.canglian.web.controller.business.MdSupplierController.add()','POST',1,'admin','研发部门','/business/supplier','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"params\":{},\"payableDays\":30,\"status\":\"0\",\"supplierCode\":\"SUP_E2E_114156\",\"supplierId\":102,\"supplierName\":\"E2E供应商\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:55',6),(140,'资金账户',1,'com.canglian.web.controller.business.FinFundAccountController.add()','POST',1,'admin','研发部门','/business/fundAccount','127.0.0.1','内网IP','{\"accountId\":3,\"accountName\":\"E2E测试户\",\"accountNo\":\"FA_E2E_114156\",\"accountType\":\"bank\",\"balance\":10000,\"createBy\":\"admin\",\"params\":{},\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:55',6),(141,'购货订单',1,'com.canglian.web.controller.business.PurOrderController.add()','POST',1,'admin','研发部门','/business/purOrder','127.0.0.1','内网IP','{\"approveBy\":\"\",\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"orderItemList\":[{\"amount\":1000,\"price\":10,\"productId\":103,\"purchaseOrderId\":1,\"purchaseOrderItemId\":1,\"quantity\":100}],\"params\":{},\"purchaseOrderId\":1,\"purchaseOrderNo\":\"PO_E2E_114156\",\"status\":\"0\",\"supplierId\":102,\"totalAmount\":1000,\"totalQty\":100,\"warehouseId\":104} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:55',15),(142,'购货订单审批',2,'com.canglian.web.controller.business.PurOrderController.approve()','PUT',1,'admin','研发部门','/business/purOrder/approve/1','127.0.0.1','内网IP','1 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:55',22),(143,'购货订单下推入库单',2,'com.canglian.web.controller.business.PurOrderController.pushInbound()','PUT',1,'admin','研发部门','/business/purOrder/pushInbound/1','127.0.0.1','内网IP','1 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:55',37),(144,'购货订单下推入库单',2,'com.canglian.web.controller.business.PurOrderController.pushInbound()','PUT',1,'admin','研发部门','/business/purOrder/pushInbound/1','127.0.0.1','内网IP','1 ',NULL,1,'购货订单审批通过后才允许下推','2026-11-22 11:41:55',5),(145,'入库审核',2,'com.canglian.web.controller.business.WmsInboundController.audit()','PUT',1,'admin','研发部门','/business/inbound/audit/101','127.0.0.1','内网IP','101 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:55',81),(146,'销售单据',1,'com.canglian.web.controller.business.SalOrderController.add()','POST',1,'admin','研发部门','/business/saleOrder','127.0.0.1','内网IP','{\"approveBy\":\"\",\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"customerId\":102,\"orderId\":1,\"orderItemList\":[{\"amount\":1000,\"orderId\":1,\"orderItemId\":1,\"price\":25,\"productId\":103,\"quantity\":40}],\"orderNo\":\"SO_E2E_114156\",\"orderType\":\"order\",\"params\":{},\"status\":\"0\",\"totalAmount\":1000,\"totalQty\":40,\"warehouseId\":104} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:55',15),(147,'销货订单下推出库单',2,'com.canglian.web.controller.business.SalOrderController.pushOutbound()','PUT',1,'admin','研发部门','/business/saleOrder/pushOutbound/1','127.0.0.1','内网IP','1 ',NULL,1,'仅销货订单允许下推出库单','2026-11-22 11:41:55',5),(148,'销售单据审批',2,'com.canglian.web.controller.business.SalOrderController.approve()','PUT',1,'admin','研发部门','/business/saleOrder/approve/1','127.0.0.1','内网IP','1 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:55',20),(149,'销货订单下推出库单',2,'com.canglian.web.controller.business.SalOrderController.pushOutbound()','PUT',1,'admin','研发部门','/business/saleOrder/pushOutbound/1','127.0.0.1','内网IP','1 ',NULL,1,'仅销货订单允许下推出库单','2026-11-22 11:41:55',5),(150,'发票登记',1,'com.canglian.web.controller.business.FinInvoiceController.add()','POST',1,'admin','研发部门','/business/invoice','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"customerId\":102,\"invoiceAmount\":1000,\"invoiceDate\":\"2026-11-22 00:00:00\",\"invoiceId\":2,\"invoiceNo\":\"sin20261122002\",\"invoiceType\":\"sale\",\"params\":{},\"sourceBillId\":1,\"sourceBillNo\":\"SO_E2E_114156\",\"sourceBillType\":\"sal_order\",\"status\":\"0\",\"taxAmount\":115.04,\"taxRate\":0.13,\"untaxedAmount\":884.96} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:55',11),(151,'发票登记确认',2,'com.canglian.web.controller.business.FinInvoiceController.confirm()','PUT',1,'admin','研发部门','/business/invoice/confirm/2','127.0.0.1','内网IP','2 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:55',41),(152,'收款单',1,'com.canglian.web.controller.business.FinReceiptController.add()','POST',1,'admin','研发部门','/business/receipt','127.0.0.1','内网IP','{\"amount\":-10,\"createBy\":\"admin\",\"customerId\":102,\"fundAccountId\":3,\"params\":{},\"receiptDate\":\"2026-11-22 00:00:00\",\"receiptNo\":\"rec20261122003\"} ',NULL,1,'收款金额必须大于0','2026-11-22 11:41:55',10),(153,'收款单',1,'com.canglian.web.controller.business.FinReceiptController.add()','POST',1,'admin','研发部门','/business/receipt','127.0.0.1','内网IP','{\"amount\":300,\"createBy\":\"admin\",\"customerId\":102,\"fundAccountId\":3,\"params\":{},\"receiptDate\":\"2026-11-22 00:00:00\",\"receiptId\":4,\"receiptNo\":\"rec20261122003\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:55',51),(154,'费用',1,'com.canglian.web.controller.business.FinExpenseController.add()','POST',1,'admin','研发部门','/business/expense','127.0.0.1','内网IP','{\"amount\":150,\"createBy\":\"admin\",\"expenseDate\":\"2026-11-22 00:00:00\",\"expenseId\":3,\"expenseNo\":\"exp202611227406\",\"fundAccountId\":3,\"params\":{},\"remark\":\"E2E_114156\",\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:55',7),(155,'费用确认',2,'com.canglian.web.controller.business.FinExpenseController.confirm()','PUT',1,'admin','研发部门','/business/expense/confirm/3','127.0.0.1','内网IP','3 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:55',30),(156,'资金账户',3,'com.canglian.web.controller.business.FinFundAccountController.remove()','DELETE',1,'admin','研发部门','/business/fundAccount/3','127.0.0.1','内网IP','[3] ',NULL,1,'资金账户[E2E测试户]余额不为0，不允许删除','2026-11-22 11:41:55',6),(157,'收款单',1,'com.canglian.web.controller.business.FinReceiptController.add()','POST',1,'admin','研发部门','/business/receipt','127.0.0.1','内网IP','{\"amount\":200,\"createBy\":\"admin\",\"customerId\":102,\"fundAccountId\":3,\"params\":{},\"receiptDate\":\"2026-11-22 00:00:00\",\"receiptId\":5,\"receiptNo\":\"rec20261122004\",\"remark\":\"ADV_E2E_114156\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:55',23),(158,'核销',1,'com.canglian.web.controller.business.FinWriteOffController.add()','POST',1,'admin','研发部门','/business/writeOff','127.0.0.1','内网IP','{\"amount\":200,\"createBy\":\"admin\",\"params\":{},\"receiptId\":5,\"writeOffDate\":\"2026-11-22 00:00:00\",\"writeOffNo\":\"wof202611227541\",\"writeOffType\":\"receivable\"} ',NULL,1,'应收核销必须选择应收单','2026-11-22 11:41:56',1),(159,'坏账',1,'com.canglian.web.controller.business.FinBadDebtController.add()','POST',1,'admin','研发部门','/business/badDebt','127.0.0.1','内网IP','{\"amount\":50,\"badDebtDate\":\"2026-11-22 00:00:00\",\"badDebtNo\":\"bd202611227581\",\"createBy\":\"admin\",\"customerId\":102,\"params\":{},\"reason\":\"E2E坏账测试\"} ',NULL,1,'关联应收单不能为空','2026-11-22 11:41:56',1),(160,'核销确认',2,'com.canglian.web.controller.business.FinWriteOffController.confirm()','PUT',1,'admin','研发部门','/business/writeOff/confirm/1','127.0.0.1','内网IP','1 ',NULL,1,'核销单已确认，请勿重复操作','2026-11-22 11:41:56',6),(161,'坏账确认',2,'com.canglian.web.controller.business.FinBadDebtController.confirm()','PUT',1,'admin','研发部门','/business/badDebt/confirm/1','127.0.0.1','内网IP','1 ',NULL,1,'坏账单已确认，请勿重复操作','2026-11-22 11:41:56',8),(162,'坏账',3,'com.canglian.web.controller.business.FinBadDebtController.remove()','DELETE',1,'admin','研发部门','/business/badDebt/1','127.0.0.1','内网IP','[1] ',NULL,1,'坏账单已确认，不允许修改或删除','2026-11-22 11:41:56',5),(163,'调拨单',1,'com.canglian.web.controller.business.WmsTransferController.add()','POST',1,'admin','研发部门','/business/transfer','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"inWarehouseId\":105,\"itemList\":[{\"amount\":0,\"batchNo\":\"\",\"inLocationId\":0,\"outLocationId\":0,\"productId\":103,\"quantity\":10,\"transferId\":103,\"transferItemId\":103}],\"outWarehouseId\":104,\"params\":{},\"status\":\"0\",\"totalAmount\":0,\"totalQty\":10,\"transferId\":103,\"transferNo\":\"trf20261122003\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:56',21),(164,'调拨审核',2,'com.canglian.web.controller.business.WmsTransferController.audit()','PUT',1,'admin','研发部门','/business/transfer/audit/103','127.0.0.1','内网IP','103 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:56',54),(165,'调拨单',1,'com.canglian.web.controller.business.WmsTransferController.add()','POST',1,'admin','研发部门','/business/transfer','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"inWarehouseId\":104,\"itemList\":[{\"amount\":0,\"batchNo\":\"\",\"inLocationId\":2003,\"outLocationId\":0,\"productId\":103,\"quantity\":5,\"transferId\":104,\"transferItemId\":104}],\"outWarehouseId\":104,\"params\":{},\"status\":\"0\",\"totalAmount\":0,\"totalQty\":5,\"transferId\":104,\"transferNo\":\"trf20261122004\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:56',19),(166,'调拨审核',2,'com.canglian.web.controller.business.WmsTransferController.audit()','PUT',1,'admin','研发部门','/business/transfer/audit/104','127.0.0.1','内网IP','104 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:56',41),(167,'盘点单',1,'com.canglian.web.controller.business.WmsInventoryCheckController.add()','POST',1,'admin','研发部门','/business/inventoryCheck','127.0.0.1','内网IP','{\"checkId\":102,\"checkNo\":\"chk20261122002\",\"createBy\":\"admin\",\"itemList\":[{\"actualQty\":12,\"batchNo\":\"\",\"checkId\":102,\"checkItemId\":102,\"diffAmount\":0.00,\"diffQty\":2.00,\"locationId\":0,\"productId\":103,\"stockQty\":10.00}],\"params\":{},\"status\":\"0\",\"totalDiffAmount\":0.00,\"totalDiffQty\":2.00,\"warehouseId\":105} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:56',25),(168,'盘点审核',2,'com.canglian.web.controller.business.WmsInventoryCheckController.audit()','PUT',1,'admin','研发部门','/business/inventoryCheck/audit/102','127.0.0.1','内网IP','102 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:56',29),(169,'销售退货',1,'com.canglian.web.controller.business.WmsSaleReturnController.add()','POST',1,'admin','研发部门','/business/saleReturn','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 11:41:57.953\",\"createBy\":\"admin\",\"customerId\":102,\"itemList\":[{\"amount\":125,\"batchNo\":\"\",\"locationId\":0,\"params\":{},\"price\":25,\"productId\":103,\"quantity\":5,\"saleReturnId\":2,\"saleReturnItemId\":2}],\"params\":{},\"returnNo\":\"srt20261122002\",\"saleReturnId\":2,\"status\":\"0\",\"totalAmount\":125,\"totalQty\":5,\"warehouseId\":104} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:56',20),(170,'销售退货审核',2,'com.canglian.web.controller.business.WmsSaleReturnController.audit()','PUT',1,'admin','研发部门','/business/saleReturn/audit/2','127.0.0.1','内网IP','2 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:56',41),(171,'采购退货',1,'com.canglian.web.controller.business.WmsPurchaseReturnController.add()','POST',1,'admin','研发部门','/business/purchaseReturn','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 11:41:58.035\",\"createBy\":\"admin\",\"itemList\":[{\"amount\":50,\"batchNo\":\"\",\"locationId\":0,\"params\":{},\"price\":10,\"productId\":103,\"purchaseReturnId\":2,\"purchaseReturnItemId\":2,\"quantity\":5}],\"params\":{},\"purchaseReturnId\":2,\"returnNo\":\"prt20261122002\",\"status\":\"0\",\"supplierId\":102,\"totalAmount\":50,\"totalQty\":5,\"warehouseId\":104} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:56',20),(172,'采购退货审核',2,'com.canglian.web.controller.business.WmsPurchaseReturnController.audit()','PUT',1,'admin','研发部门','/business/purchaseReturn/audit/2','127.0.0.1','内网IP','2 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:56',43),(173,'凭证生成',2,'com.canglian.web.controller.business.FinVoucherEventController.generate()','PUT',1,'admin','研发部门','/business/voucherEvent/generate/8','127.0.0.1','内网IP','8 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:56',19),(174,'凭证事件',3,'com.canglian.web.controller.business.FinVoucherEventController.remove()','DELETE',1,'admin','研发部门','/business/voucherEvent/8','127.0.0.1','内网IP','[8] ',NULL,1,'仅待生成状态的凭证事件允许删除','2026-11-22 11:41:56',3),(175,'凭证回写',2,'com.canglian.web.controller.business.FinVoucherEventController.writeback()','PUT',1,'admin','研发部门','/business/voucherEvent/writeback/8','127.0.0.1','内网IP','8 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:56',12),(176,'凭证冲销',2,'com.canglian.web.controller.business.FinVoucherEventController.reverse()','PUT',1,'admin','研发部门','/business/voucherEvent/reverse/8','127.0.0.1','内网IP','8 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:56',17),(177,'凭证生成',2,'com.canglian.web.controller.business.FinVoucherEventController.generate()','PUT',1,'admin','研发部门','/business/voucherEvent/generate/9','127.0.0.1','内网IP','9 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:56',16),(178,'凭证冲销',2,'com.canglian.web.controller.business.FinVoucherEventController.reverse()','PUT',1,'admin','研发部门','/business/voucherEvent/reverse/9','127.0.0.1','内网IP','9 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:56',15),(179,'会计期间结账',2,'com.canglian.web.controller.business.FinGeneralLedgerController.closePeriod()','PUT',1,'admin','研发部门','/business/generalLedger/period/close/11','127.0.0.1','内网IP','11 ',NULL,1,'存在更早的未结账会计期间，请按期间顺序依次结账','2026-11-22 11:41:56',8),(180,'会计期间结账',2,'com.canglian.web.controller.business.FinGeneralLedgerController.closePeriod()','PUT',1,'admin','研发部门','/business/generalLedger/period/close/1','127.0.0.1','内网IP','1 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:56',13),(181,'费用',1,'com.canglian.web.controller.business.FinExpenseController.add()','POST',1,'admin','研发部门','/business/expense','127.0.0.1','内网IP','{\"amount\":10,\"createBy\":\"admin\",\"expenseDate\":\"2026-01-15 00:00:00\",\"expenseId\":4,\"expenseNo\":\"exp202611228334\",\"fundAccountId\":3,\"params\":{},\"remark\":\"LOCK_E2E_114156\",\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:56',6),(182,'费用确认',2,'com.canglian.web.controller.business.FinExpenseController.confirm()','PUT',1,'admin','研发部门','/business/expense/confirm/4','127.0.0.1','内网IP','4 ',NULL,1,'会计期间[2026-01]已结账，不允许执行该业务操作','2026-11-22 11:41:56',9),(183,'费用',3,'com.canglian.web.controller.business.FinExpenseController.remove()','DELETE',1,'admin','研发部门','/business/expense/4','127.0.0.1','内网IP','[4] ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:56',10),(184,'会计期间反结账',2,'com.canglian.web.controller.business.FinGeneralLedgerController.reopenPeriod()','PUT',1,'admin','研发部门','/business/generalLedger/period/reopen/1','127.0.0.1','内网IP','1 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:41:56',15),(185,'仓库档案',1,'com.canglian.web.controller.business.MdWarehouseController.add()','POST',1,'admin','研发部门','/business/warehouse','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"params\":{},\"status\":\"0\",\"warehouseCode\":\"WH1_E2E_114303\",\"warehouseId\":106,\"warehouseName\":\"E2E主仓\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:02',7),(186,'仓库档案',1,'com.canglian.web.controller.business.MdWarehouseController.add()','POST',1,'admin','研发部门','/business/warehouse','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"params\":{},\"status\":\"0\",\"warehouseCode\":\"WH2_E2E_114303\",\"warehouseId\":107,\"warehouseName\":\"E2E分仓\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:02',5),(187,'库位档案',1,'com.canglian.web.controller.business.MdLocationController.add()','POST',1,'admin','研发部门','/business/location','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2026-11-22 11:43:03\",\"locationCode\":\"L1_E2E_114303\",\"locationId\":2005,\"locationName\":\"A1库位\",\"params\":{},\"status\":\"0\",\"warehouseId\":106} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:02',6),(188,'库位档案',1,'com.canglian.web.controller.business.MdLocationController.add()','POST',1,'admin','研发部门','/business/location','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2026-11-22 11:43:03\",\"locationCode\":\"L2_E2E_114303\",\"locationId\":2006,\"locationName\":\"A2库位\",\"params\":{},\"status\":\"0\",\"warehouseId\":106} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:02',7),(189,'商品档案',1,'com.canglian.web.controller.business.MdProductController.add()','POST',1,'admin','研发部门','/business/product','127.0.0.1','内网IP','{\"barCode\":\"BAR_E2E_114303\",\"costPrice\":10,\"createBy\":\"admin\",\"params\":{},\"productCode\":\"P_E2E_114303\",\"productId\":104,\"productName\":\"E2E测试商品\",\"salePrice\":25,\"status\":\"0\",\"warningMaxQty\":0,\"warningMinQty\":5} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:02',7),(190,'客户档案',1,'com.canglian.web.controller.business.MdCustomerController.add()','POST',1,'admin','研发部门','/business/customer','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"creditLimit\":5000,\"customerCode\":\"CUS_E2E_114303\",\"customerId\":103,\"customerName\":\"E2E客户\",\"params\":{},\"receivableDays\":30,\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:02',7),(191,'供应商档案',1,'com.canglian.web.controller.business.MdSupplierController.add()','POST',1,'admin','研发部门','/business/supplier','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"params\":{},\"payableDays\":30,\"status\":\"0\",\"supplierCode\":\"SUP_E2E_114303\",\"supplierId\":103,\"supplierName\":\"E2E供应商\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:02',6),(192,'资金账户',1,'com.canglian.web.controller.business.FinFundAccountController.add()','POST',1,'admin','研发部门','/business/fundAccount','127.0.0.1','内网IP','{\"accountId\":4,\"accountName\":\"E2E测试户\",\"accountNo\":\"FA_E2E_114303\",\"accountType\":\"bank\",\"balance\":10000,\"createBy\":\"admin\",\"params\":{},\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:02',7),(193,'购货订单',1,'com.canglian.web.controller.business.PurOrderController.add()','POST',1,'admin','研发部门','/business/purOrder','127.0.0.1','内网IP','{\"approveBy\":\"\",\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"orderItemList\":[{\"amount\":1000,\"price\":10,\"productId\":104,\"purchaseOrderId\":2,\"purchaseOrderItemId\":3,\"quantity\":100}],\"params\":{},\"purchaseOrderId\":2,\"purchaseOrderNo\":\"PO_E2E_114303\",\"status\":\"0\",\"supplierId\":103,\"totalAmount\":1000,\"totalQty\":100,\"warehouseId\":106} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:02',11),(194,'购货订单审批',2,'com.canglian.web.controller.business.PurOrderController.approve()','PUT',1,'admin','研发部门','/business/purOrder/approve/2','127.0.0.1','内网IP','2 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:02',25),(195,'购货订单下推入库单',2,'com.canglian.web.controller.business.PurOrderController.pushInbound()','PUT',1,'admin','研发部门','/business/purOrder/pushInbound/2','127.0.0.1','内网IP','2 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:02',38),(196,'购货订单下推入库单',2,'com.canglian.web.controller.business.PurOrderController.pushInbound()','PUT',1,'admin','研发部门','/business/purOrder/pushInbound/2','127.0.0.1','内网IP','2 ',NULL,1,'购货订单审批通过后才允许下推','2026-11-22 11:43:02',5),(197,'入库审核',2,'com.canglian.web.controller.business.WmsInboundController.audit()','PUT',1,'admin','研发部门','/business/inbound/audit/102','127.0.0.1','内网IP','102 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:02',78),(198,'销售单据',1,'com.canglian.web.controller.business.SalOrderController.add()','POST',1,'admin','研发部门','/business/saleOrder','127.0.0.1','内网IP','{\"approveBy\":\"\",\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"customerId\":103,\"orderId\":2,\"orderItemList\":[{\"amount\":1000,\"orderId\":2,\"orderItemId\":3,\"price\":25,\"productId\":104,\"quantity\":40}],\"orderNo\":\"SO_E2E_114303\",\"orderType\":\"sale\",\"params\":{},\"status\":\"0\",\"totalAmount\":1000,\"totalQty\":40,\"warehouseId\":106} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:02',10),(199,'销售单据审批',2,'com.canglian.web.controller.business.SalOrderController.approve()','PUT',1,'admin','研发部门','/business/saleOrder/approve/2','127.0.0.1','内网IP','2 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:02',30),(200,'销货订单下推出库单',2,'com.canglian.web.controller.business.SalOrderController.pushOutbound()','PUT',1,'admin','研发部门','/business/saleOrder/pushOutbound/2','127.0.0.1','内网IP','2 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:02',36),(201,'销货订单下推出库单',2,'com.canglian.web.controller.business.SalOrderController.pushOutbound()','PUT',1,'admin','研发部门','/business/saleOrder/pushOutbound/2','127.0.0.1','内网IP','2 ',NULL,1,'销货订单审批通过后才允许下推','2026-11-22 11:43:02',6),(202,'发票登记',1,'com.canglian.web.controller.business.FinInvoiceController.add()','POST',1,'admin','研发部门','/business/invoice','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"customerId\":103,\"invoiceAmount\":1000,\"invoiceDate\":\"2026-11-22 00:00:00\",\"invoiceId\":3,\"invoiceNo\":\"sin20261122003\",\"invoiceType\":\"sale\",\"params\":{},\"sourceBillId\":2,\"sourceBillNo\":\"SO_E2E_114303\",\"sourceBillType\":\"sal_order\",\"status\":\"0\",\"taxAmount\":115.04,\"taxRate\":0.13,\"untaxedAmount\":884.96} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:02',10),(203,'发票登记确认',2,'com.canglian.web.controller.business.FinInvoiceController.confirm()','PUT',1,'admin','研发部门','/business/invoice/confirm/3','127.0.0.1','内网IP','3 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:02',35),(204,'收款单',1,'com.canglian.web.controller.business.FinReceiptController.add()','POST',1,'admin','研发部门','/business/receipt','127.0.0.1','内网IP','{\"amount\":-10,\"createBy\":\"admin\",\"customerId\":103,\"fundAccountId\":4,\"params\":{},\"receiptDate\":\"2026-11-22 00:00:00\",\"receiptNo\":\"rec20261122005\"} ',NULL,1,'收款金额必须大于0','2026-11-22 11:43:02',10),(205,'收款单',1,'com.canglian.web.controller.business.FinReceiptController.add()','POST',1,'admin','研发部门','/business/receipt','127.0.0.1','内网IP','{\"amount\":300,\"createBy\":\"admin\",\"customerId\":103,\"fundAccountId\":4,\"params\":{},\"receiptDate\":\"2026-11-22 00:00:00\",\"receiptId\":6,\"receiptNo\":\"rec20261122005\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:02',19),(206,'费用',1,'com.canglian.web.controller.business.FinExpenseController.add()','POST',1,'admin','研发部门','/business/expense','127.0.0.1','内网IP','{\"amount\":150,\"createBy\":\"admin\",\"expenseDate\":\"2026-11-22 00:00:00\",\"expenseId\":5,\"expenseNo\":\"exp202611224424\",\"fundAccountId\":4,\"params\":{},\"remark\":\"E2E_114303\",\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:02',6),(207,'费用确认',2,'com.canglian.web.controller.business.FinExpenseController.confirm()','PUT',1,'admin','研发部门','/business/expense/confirm/5','127.0.0.1','内网IP','5 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:02',30),(208,'资金账户',3,'com.canglian.web.controller.business.FinFundAccountController.remove()','DELETE',1,'admin','研发部门','/business/fundAccount/4','127.0.0.1','内网IP','[4] ',NULL,1,'资金账户[E2E测试户]余额不为0，不允许删除','2026-11-22 11:43:02',4),(209,'收款单',1,'com.canglian.web.controller.business.FinReceiptController.add()','POST',1,'admin','研发部门','/business/receipt','127.0.0.1','内网IP','{\"amount\":200,\"createBy\":\"admin\",\"customerId\":103,\"fundAccountId\":4,\"params\":{},\"receiptDate\":\"2026-11-22 00:00:00\",\"receiptId\":7,\"receiptNo\":\"rec20261122006\",\"remark\":\"ADV_E2E_114303\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:03',57),(210,'核销',1,'com.canglian.web.controller.business.FinWriteOffController.add()','POST',1,'admin','研发部门','/business/writeOff','127.0.0.1','内网IP','{\"amount\":200,\"createBy\":\"admin\",\"params\":{},\"receiptId\":7,\"writeOffDate\":\"2026-11-22 00:00:00\",\"writeOffNo\":\"wof202611224583\",\"writeOffType\":\"receivable\"} ',NULL,1,'应收核销必须选择应收单','2026-11-22 11:43:03',1),(211,'核销确认',2,'com.canglian.web.controller.business.FinWriteOffController.confirm()','PUT',1,'admin','研发部门','/business/writeOff/confirm/1','127.0.0.1','内网IP','1 ',NULL,1,'核销单已确认，请勿重复操作','2026-11-22 11:43:03',6),(212,'坏账',1,'com.canglian.web.controller.business.FinBadDebtController.add()','POST',1,'admin','研发部门','/business/badDebt','127.0.0.1','内网IP','{\"amount\":50,\"badDebtDate\":\"2026-11-22 00:00:00\",\"badDebtNo\":\"bd202611224620\",\"createBy\":\"admin\",\"customerId\":103,\"params\":{},\"reason\":\"E2E坏账测试\"} ',NULL,1,'关联应收单不能为空','2026-11-22 11:43:03',0),(213,'坏账',3,'com.canglian.web.controller.business.FinBadDebtController.remove()','DELETE',1,'admin','研发部门','/business/badDebt/1','127.0.0.1','内网IP','[1] ',NULL,1,'坏账单已确认，不允许修改或删除','2026-11-22 11:43:03',3),(214,'坏账确认',2,'com.canglian.web.controller.business.FinBadDebtController.confirm()','PUT',1,'admin','研发部门','/business/badDebt/confirm/1','127.0.0.1','内网IP','1 ',NULL,1,'坏账单已确认，请勿重复操作','2026-11-22 11:43:03',5),(215,'调拨单',1,'com.canglian.web.controller.business.WmsTransferController.add()','POST',1,'admin','研发部门','/business/transfer','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"inWarehouseId\":107,\"itemList\":[{\"amount\":0,\"batchNo\":\"\",\"inLocationId\":0,\"outLocationId\":0,\"productId\":104,\"quantity\":10,\"transferId\":105,\"transferItemId\":105}],\"outWarehouseId\":106,\"params\":{},\"status\":\"0\",\"totalAmount\":0,\"totalQty\":10,\"transferId\":105,\"transferNo\":\"trf20261122005\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:03',18),(216,'调拨审核',2,'com.canglian.web.controller.business.WmsTransferController.audit()','PUT',1,'admin','研发部门','/business/transfer/audit/105','127.0.0.1','内网IP','105 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:03',39),(217,'调拨单',1,'com.canglian.web.controller.business.WmsTransferController.add()','POST',1,'admin','研发部门','/business/transfer','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"inWarehouseId\":106,\"itemList\":[{\"amount\":0,\"batchNo\":\"\",\"inLocationId\":2005,\"outLocationId\":0,\"productId\":104,\"quantity\":5,\"transferId\":106,\"transferItemId\":106}],\"outWarehouseId\":106,\"params\":{},\"status\":\"0\",\"totalAmount\":0,\"totalQty\":5,\"transferId\":106,\"transferNo\":\"trf20261122006\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:03',18),(218,'调拨审核',2,'com.canglian.web.controller.business.WmsTransferController.audit()','PUT',1,'admin','研发部门','/business/transfer/audit/106','127.0.0.1','内网IP','106 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:03',39),(219,'盘点单',1,'com.canglian.web.controller.business.WmsInventoryCheckController.add()','POST',1,'admin','研发部门','/business/inventoryCheck','127.0.0.1','内网IP','{\"checkId\":103,\"checkNo\":\"chk20261122003\",\"createBy\":\"admin\",\"itemList\":[{\"actualQty\":12,\"batchNo\":\"\",\"checkId\":103,\"checkItemId\":103,\"diffAmount\":0.00,\"diffQty\":2.00,\"locationId\":0,\"productId\":104,\"stockQty\":10.00}],\"params\":{},\"status\":\"0\",\"totalDiffAmount\":0.00,\"totalDiffQty\":2.00,\"warehouseId\":107} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:03',20),(220,'盘点审核',2,'com.canglian.web.controller.business.WmsInventoryCheckController.audit()','PUT',1,'admin','研发部门','/business/inventoryCheck/audit/103','127.0.0.1','内网IP','103 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:03',26),(221,'销售退货',1,'com.canglian.web.controller.business.WmsSaleReturnController.add()','POST',1,'admin','研发部门','/business/saleReturn','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 11:43:04.938\",\"createBy\":\"admin\",\"customerId\":103,\"itemList\":[{\"amount\":125,\"batchNo\":\"\",\"locationId\":0,\"params\":{},\"price\":25,\"productId\":104,\"quantity\":5,\"saleReturnId\":3,\"saleReturnItemId\":3}],\"params\":{},\"returnNo\":\"srt20261122003\",\"saleReturnId\":3,\"status\":\"0\",\"totalAmount\":125,\"totalQty\":5,\"warehouseId\":106} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:03',22),(222,'销售退货审核',2,'com.canglian.web.controller.business.WmsSaleReturnController.audit()','PUT',1,'admin','研发部门','/business/saleReturn/audit/3','127.0.0.1','内网IP','3 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:03',40),(223,'采购退货',1,'com.canglian.web.controller.business.WmsPurchaseReturnController.add()','POST',1,'admin','研发部门','/business/purchaseReturn','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 11:43:05.022\",\"createBy\":\"admin\",\"itemList\":[{\"amount\":50,\"batchNo\":\"\",\"locationId\":0,\"params\":{},\"price\":10,\"productId\":104,\"purchaseReturnId\":3,\"purchaseReturnItemId\":3,\"quantity\":5}],\"params\":{},\"purchaseReturnId\":3,\"returnNo\":\"prt20261122003\",\"status\":\"0\",\"supplierId\":103,\"totalAmount\":50,\"totalQty\":5,\"warehouseId\":106} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:03',19),(224,'采购退货审核',2,'com.canglian.web.controller.business.WmsPurchaseReturnController.audit()','PUT',1,'admin','研发部门','/business/purchaseReturn/audit/3','127.0.0.1','内网IP','3 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:03',40),(225,'凭证生成',2,'com.canglian.web.controller.business.FinVoucherEventController.generate()','PUT',1,'admin','研发部门','/business/voucherEvent/generate/13','127.0.0.1','内网IP','13 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:03',20),(226,'凭证事件',3,'com.canglian.web.controller.business.FinVoucherEventController.remove()','DELETE',1,'admin','研发部门','/business/voucherEvent/13','127.0.0.1','内网IP','[13] ',NULL,1,'仅待生成状态的凭证事件允许删除','2026-11-22 11:43:03',4),(227,'凭证回写',2,'com.canglian.web.controller.business.FinVoucherEventController.writeback()','PUT',1,'admin','研发部门','/business/voucherEvent/writeback/13','127.0.0.1','内网IP','13 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:03',11),(228,'凭证冲销',2,'com.canglian.web.controller.business.FinVoucherEventController.reverse()','PUT',1,'admin','研发部门','/business/voucherEvent/reverse/13','127.0.0.1','内网IP','13 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:03',15),(229,'凭证生成',2,'com.canglian.web.controller.business.FinVoucherEventController.generate()','PUT',1,'admin','研发部门','/business/voucherEvent/generate/14','127.0.0.1','内网IP','14 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:03',16),(230,'凭证冲销',2,'com.canglian.web.controller.business.FinVoucherEventController.reverse()','PUT',1,'admin','研发部门','/business/voucherEvent/reverse/14','127.0.0.1','内网IP','14 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:03',15),(231,'会计期间结账',2,'com.canglian.web.controller.business.FinGeneralLedgerController.closePeriod()','PUT',1,'admin','研发部门','/business/generalLedger/period/close/11','127.0.0.1','内网IP','11 ',NULL,1,'存在更早的未结账会计期间，请按期间顺序依次结账','2026-11-22 11:43:03',8),(232,'费用',1,'com.canglian.web.controller.business.FinExpenseController.add()','POST',1,'admin','研发部门','/business/expense','127.0.0.1','内网IP','{\"amount\":10,\"createBy\":\"admin\",\"expenseDate\":\"2026-01-15 00:00:00\",\"expenseId\":6,\"expenseNo\":\"exp202611225294\",\"fundAccountId\":4,\"params\":{},\"remark\":\"LOCK_E2E_114303\",\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:03',6),(233,'会计期间结账',2,'com.canglian.web.controller.business.FinGeneralLedgerController.closePeriod()','PUT',1,'admin','研发部门','/business/generalLedger/period/close/1','127.0.0.1','内网IP','1 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:03',14),(234,'费用确认',2,'com.canglian.web.controller.business.FinExpenseController.confirm()','PUT',1,'admin','研发部门','/business/expense/confirm/6','127.0.0.1','内网IP','6 ',NULL,1,'会计期间[2026-01]已结账，不允许执行该业务操作','2026-11-22 11:43:03',9),(235,'费用',3,'com.canglian.web.controller.business.FinExpenseController.remove()','DELETE',1,'admin','研发部门','/business/expense/6','127.0.0.1','内网IP','[6] ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:03',7),(236,'会计期间反结账',2,'com.canglian.web.controller.business.FinGeneralLedgerController.reopenPeriod()','PUT',1,'admin','研发部门','/business/generalLedger/period/reopen/1','127.0.0.1','内网IP','1 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:03',12),(237,'仓库档案',1,'com.canglian.web.controller.business.MdWarehouseController.add()','POST',1,'admin','研发部门','/business/warehouse','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"params\":{},\"status\":\"0\",\"warehouseCode\":\"WH1_E2E_114333\",\"warehouseId\":108,\"warehouseName\":\"E2E主仓\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:32',5),(238,'仓库档案',1,'com.canglian.web.controller.business.MdWarehouseController.add()','POST',1,'admin','研发部门','/business/warehouse','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"params\":{},\"status\":\"0\",\"warehouseCode\":\"WH2_E2E_114333\",\"warehouseId\":109,\"warehouseName\":\"E2E分仓\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:32',6),(239,'库位档案',1,'com.canglian.web.controller.business.MdLocationController.add()','POST',1,'admin','研发部门','/business/location','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2026-11-22 11:43:34\",\"locationCode\":\"L1_E2E_114333\",\"locationId\":2007,\"locationName\":\"A1库位\",\"params\":{},\"status\":\"0\",\"warehouseId\":108} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:32',7),(240,'库位档案',1,'com.canglian.web.controller.business.MdLocationController.add()','POST',1,'admin','研发部门','/business/location','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"createTime\":\"2026-11-22 11:43:34\",\"locationCode\":\"L2_E2E_114333\",\"locationId\":2008,\"locationName\":\"A2库位\",\"params\":{},\"status\":\"0\",\"warehouseId\":108} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:32',6),(241,'商品档案',1,'com.canglian.web.controller.business.MdProductController.add()','POST',1,'admin','研发部门','/business/product','127.0.0.1','内网IP','{\"barCode\":\"BAR_E2E_114333\",\"costPrice\":10,\"createBy\":\"admin\",\"params\":{},\"productCode\":\"P_E2E_114333\",\"productId\":105,\"productName\":\"E2E测试商品\",\"salePrice\":25,\"status\":\"0\",\"warningMaxQty\":0,\"warningMinQty\":5} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:32',6),(242,'客户档案',1,'com.canglian.web.controller.business.MdCustomerController.add()','POST',1,'admin','研发部门','/business/customer','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"creditLimit\":5000,\"customerCode\":\"CUS_E2E_114333\",\"customerId\":104,\"customerName\":\"E2E客户\",\"params\":{},\"receivableDays\":30,\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:32',4),(243,'供应商档案',1,'com.canglian.web.controller.business.MdSupplierController.add()','POST',1,'admin','研发部门','/business/supplier','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"params\":{},\"payableDays\":30,\"status\":\"0\",\"supplierCode\":\"SUP_E2E_114333\",\"supplierId\":104,\"supplierName\":\"E2E供应商\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:32',6),(244,'资金账户',1,'com.canglian.web.controller.business.FinFundAccountController.add()','POST',1,'admin','研发部门','/business/fundAccount','127.0.0.1','内网IP','{\"accountId\":5,\"accountName\":\"E2E测试户\",\"accountNo\":\"FA_E2E_114333\",\"accountType\":\"bank\",\"balance\":10000,\"createBy\":\"admin\",\"params\":{},\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:32',5),(245,'购货订单',1,'com.canglian.web.controller.business.PurOrderController.add()','POST',1,'admin','研发部门','/business/purOrder','127.0.0.1','内网IP','{\"approveBy\":\"\",\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"orderItemList\":[{\"amount\":1000,\"price\":10,\"productId\":105,\"purchaseOrderId\":3,\"purchaseOrderItemId\":5,\"quantity\":100}],\"params\":{},\"purchaseOrderId\":3,\"purchaseOrderNo\":\"PO_E2E_114333\",\"status\":\"0\",\"supplierId\":104,\"totalAmount\":1000,\"totalQty\":100,\"warehouseId\":108} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:32',9),(246,'购货订单审批',2,'com.canglian.web.controller.business.PurOrderController.approve()','PUT',1,'admin','研发部门','/business/purOrder/approve/3','127.0.0.1','内网IP','3 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:32',20),(247,'购货订单下推入库单',2,'com.canglian.web.controller.business.PurOrderController.pushInbound()','PUT',1,'admin','研发部门','/business/purOrder/pushInbound/3','127.0.0.1','内网IP','3 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:32',28),(248,'购货订单下推入库单',2,'com.canglian.web.controller.business.PurOrderController.pushInbound()','PUT',1,'admin','研发部门','/business/purOrder/pushInbound/3','127.0.0.1','内网IP','3 ',NULL,1,'购货订单审批通过后才允许下推','2026-11-22 11:43:32',5),(249,'入库审核',2,'com.canglian.web.controller.business.WmsInboundController.audit()','PUT',1,'admin','研发部门','/business/inbound/audit/103','127.0.0.1','内网IP','103 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:32',73),(250,'销售单据',1,'com.canglian.web.controller.business.SalOrderController.add()','POST',1,'admin','研发部门','/business/saleOrder','127.0.0.1','内网IP','{\"approveBy\":\"\",\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"customerId\":104,\"orderId\":3,\"orderItemList\":[{\"amount\":1000,\"orderId\":3,\"orderItemId\":5,\"price\":25,\"productId\":105,\"quantity\":40}],\"orderNo\":\"SO_E2E_114333\",\"orderType\":\"sale\",\"params\":{},\"status\":\"0\",\"totalAmount\":1000,\"totalQty\":40,\"warehouseId\":108} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:32',10),(251,'销售单据审批',2,'com.canglian.web.controller.business.SalOrderController.approve()','PUT',1,'admin','研发部门','/business/saleOrder/approve/3','127.0.0.1','内网IP','3 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:32',26),(252,'销货订单下推出库单',2,'com.canglian.web.controller.business.SalOrderController.pushOutbound()','PUT',1,'admin','研发部门','/business/saleOrder/pushOutbound/3','127.0.0.1','内网IP','3 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:32',33),(253,'销货订单下推出库单',2,'com.canglian.web.controller.business.SalOrderController.pushOutbound()','PUT',1,'admin','研发部门','/business/saleOrder/pushOutbound/3','127.0.0.1','内网IP','3 ',NULL,1,'销货订单审批通过后才允许下推','2026-11-22 11:43:32',6),(254,'出库审核',2,'com.canglian.web.controller.business.WmsOutboundController.audit()','PUT',1,'admin','研发部门','/business/outbound/audit/102','127.0.0.1','内网IP','102 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:33',39),(255,'出库发货',2,'com.canglian.web.controller.business.WmsOutboundController.ship()','PUT',1,'admin','研发部门','/business/outbound/ship/102','127.0.0.1','内网IP','102 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:33',10),(256,'出库签收',2,'com.canglian.web.controller.business.WmsOutboundController.sign()','PUT',1,'admin','研发部门','/business/outbound/sign/102','127.0.0.1','内网IP','102 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:33',58),(257,'发票登记',1,'com.canglian.web.controller.business.FinInvoiceController.add()','POST',1,'admin','研发部门','/business/invoice','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"customerId\":104,\"invoiceAmount\":1000,\"invoiceDate\":\"2026-11-22 00:00:00\",\"invoiceId\":4,\"invoiceNo\":\"sin20261122004\",\"invoiceType\":\"sale\",\"params\":{},\"sourceBillId\":3,\"sourceBillNo\":\"SO_E2E_114333\",\"sourceBillType\":\"sale_order\",\"status\":\"0\",\"taxAmount\":115.04,\"taxRate\":0.13,\"untaxedAmount\":884.96} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:33',10),(258,'发票登记确认',2,'com.canglian.web.controller.business.FinInvoiceController.confirm()','PUT',1,'admin','研发部门','/business/invoice/confirm/4','127.0.0.1','内网IP','4 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:33',19),(259,'收款单',1,'com.canglian.web.controller.business.FinReceiptController.add()','POST',1,'admin','研发部门','/business/receipt','127.0.0.1','内网IP','{\"amount\":-10,\"createBy\":\"admin\",\"customerId\":104,\"fundAccountId\":5,\"params\":{},\"receiptDate\":\"2026-11-22 00:00:00\",\"receiptNo\":\"rec20261122007\",\"receivableId\":104} ',NULL,1,'收款金额必须大于0','2026-11-22 11:43:33',10),(260,'收款单',1,'com.canglian.web.controller.business.FinReceiptController.add()','POST',1,'admin','研发部门','/business/receipt','127.0.0.1','内网IP','{\"amount\":300,\"createBy\":\"admin\",\"customerId\":104,\"fundAccountId\":5,\"params\":{},\"receiptDate\":\"2026-11-22 00:00:00\",\"receiptId\":8,\"receiptNo\":\"rec20261122007\",\"receivableId\":104} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:33',29),(261,'费用',1,'com.canglian.web.controller.business.FinExpenseController.add()','POST',1,'admin','研发部门','/business/expense','127.0.0.1','内网IP','{\"amount\":150,\"createBy\":\"admin\",\"expenseDate\":\"2026-11-22 00:00:00\",\"expenseId\":7,\"expenseNo\":\"exp202611224878\",\"fundAccountId\":5,\"params\":{},\"remark\":\"E2E_114333\",\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:33',5),(262,'费用确认',2,'com.canglian.web.controller.business.FinExpenseController.confirm()','PUT',1,'admin','研发部门','/business/expense/confirm/7','127.0.0.1','内网IP','7 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:33',23),(263,'资金账户',3,'com.canglian.web.controller.business.FinFundAccountController.remove()','DELETE',1,'admin','研发部门','/business/fundAccount/5','127.0.0.1','内网IP','[5] ',NULL,1,'资金账户[E2E测试户]余额不为0，不允许删除','2026-11-22 11:43:33',4),(264,'收款单',1,'com.canglian.web.controller.business.FinReceiptController.add()','POST',1,'admin','研发部门','/business/receipt','127.0.0.1','内网IP','{\"amount\":200,\"createBy\":\"admin\",\"customerId\":104,\"fundAccountId\":5,\"params\":{},\"receiptDate\":\"2026-11-22 00:00:00\",\"receiptId\":9,\"receiptNo\":\"rec20261122008\",\"remark\":\"ADV_E2E_114333\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:33',21),(265,'核销',1,'com.canglian.web.controller.business.FinWriteOffController.add()','POST',1,'admin','研发部门','/business/writeOff','127.0.0.1','内网IP','{\"amount\":200,\"createBy\":\"admin\",\"params\":{},\"receiptId\":9,\"receivableId\":104,\"status\":\"0\",\"writeOffDate\":\"2026-11-22 00:00:00\",\"writeOffId\":2,\"writeOffNo\":\"wof202611225011\",\"writeOffType\":\"receivable\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:33',24),(266,'核销确认',2,'com.canglian.web.controller.business.FinWriteOffController.confirm()','PUT',1,'admin','研发部门','/business/writeOff/confirm/2','127.0.0.1','内网IP','2 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:33',27),(267,'坏账',1,'com.canglian.web.controller.business.FinBadDebtController.add()','POST',1,'admin','研发部门','/business/badDebt','127.0.0.1','内网IP','{\"amount\":50,\"badDebtDate\":\"2026-11-22 00:00:00\",\"badDebtId\":2,\"badDebtNo\":\"bd202611225090\",\"createBy\":\"admin\",\"customerId\":104,\"params\":{},\"reason\":\"E2E坏账测试\",\"receivableId\":104,\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:33',10),(268,'坏账确认',2,'com.canglian.web.controller.business.FinBadDebtController.confirm()','PUT',1,'admin','研发部门','/business/badDebt/confirm/2','127.0.0.1','内网IP','2 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:33',26),(269,'坏账',3,'com.canglian.web.controller.business.FinBadDebtController.remove()','DELETE',1,'admin','研发部门','/business/badDebt/2','127.0.0.1','内网IP','[2] ',NULL,1,'坏账单已确认，不允许修改或删除','2026-11-22 11:43:33',29),(270,'调拨单',1,'com.canglian.web.controller.business.WmsTransferController.add()','POST',1,'admin','研发部门','/business/transfer','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"inWarehouseId\":109,\"itemList\":[{\"amount\":0,\"batchNo\":\"\",\"inLocationId\":0,\"outLocationId\":0,\"productId\":105,\"quantity\":10,\"transferId\":107,\"transferItemId\":107}],\"outWarehouseId\":108,\"params\":{},\"status\":\"0\",\"totalAmount\":0,\"totalQty\":10,\"transferId\":107,\"transferNo\":\"trf20261122007\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:33',16),(271,'调拨审核',2,'com.canglian.web.controller.business.WmsTransferController.audit()','PUT',1,'admin','研发部门','/business/transfer/audit/107','127.0.0.1','内网IP','107 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:33',41),(272,'调拨单',1,'com.canglian.web.controller.business.WmsTransferController.add()','POST',1,'admin','研发部门','/business/transfer','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"inWarehouseId\":108,\"itemList\":[{\"amount\":0,\"batchNo\":\"\",\"inLocationId\":2007,\"outLocationId\":0,\"productId\":105,\"quantity\":5,\"transferId\":108,\"transferItemId\":108}],\"outWarehouseId\":108,\"params\":{},\"status\":\"0\",\"totalAmount\":0,\"totalQty\":5,\"transferId\":108,\"transferNo\":\"trf20261122008\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:33',16),(273,'调拨审核',2,'com.canglian.web.controller.business.WmsTransferController.audit()','PUT',1,'admin','研发部门','/business/transfer/audit/108','127.0.0.1','内网IP','108 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:33',37),(274,'盘点单',1,'com.canglian.web.controller.business.WmsInventoryCheckController.add()','POST',1,'admin','研发部门','/business/inventoryCheck','127.0.0.1','内网IP','{\"checkId\":104,\"checkNo\":\"chk20261122004\",\"createBy\":\"admin\",\"itemList\":[{\"actualQty\":12,\"batchNo\":\"\",\"checkId\":104,\"checkItemId\":104,\"diffAmount\":0.00,\"diffQty\":2.00,\"locationId\":0,\"productId\":105,\"stockQty\":10.00}],\"params\":{},\"status\":\"0\",\"totalDiffAmount\":0.00,\"totalDiffQty\":2.00,\"warehouseId\":109} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:33',23),(275,'盘点审核',2,'com.canglian.web.controller.business.WmsInventoryCheckController.audit()','PUT',1,'admin','研发部门','/business/inventoryCheck/audit/104','127.0.0.1','内网IP','104 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:33',25),(276,'销售退货',1,'com.canglian.web.controller.business.WmsSaleReturnController.add()','POST',1,'admin','研发部门','/business/saleReturn','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 11:43:35.462\",\"createBy\":\"admin\",\"customerId\":104,\"itemList\":[{\"amount\":125,\"batchNo\":\"\",\"locationId\":0,\"params\":{},\"price\":25,\"productId\":105,\"quantity\":5,\"saleReturnId\":4,\"saleReturnItemId\":4}],\"params\":{},\"returnNo\":\"srt20261122004\",\"saleReturnId\":4,\"status\":\"0\",\"totalAmount\":125,\"totalQty\":5,\"warehouseId\":108} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:33',18),(277,'销售退货审核',2,'com.canglian.web.controller.business.WmsSaleReturnController.audit()','PUT',1,'admin','研发部门','/business/saleReturn/audit/4','127.0.0.1','内网IP','4 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:34',43),(278,'采购退货',1,'com.canglian.web.controller.business.WmsPurchaseReturnController.add()','POST',1,'admin','研发部门','/business/purchaseReturn','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 11:43:35.542\",\"createBy\":\"admin\",\"itemList\":[{\"amount\":50,\"batchNo\":\"\",\"locationId\":0,\"params\":{},\"price\":10,\"productId\":105,\"purchaseReturnId\":4,\"purchaseReturnItemId\":4,\"quantity\":5}],\"params\":{},\"purchaseReturnId\":4,\"returnNo\":\"prt20261122004\",\"status\":\"0\",\"supplierId\":104,\"totalAmount\":50,\"totalQty\":5,\"warehouseId\":108} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:34',17),(279,'采购退货审核',2,'com.canglian.web.controller.business.WmsPurchaseReturnController.audit()','PUT',1,'admin','研发部门','/business/purchaseReturn/audit/4','127.0.0.1','内网IP','4 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:34',40),(280,'凭证事件',3,'com.canglian.web.controller.business.FinVoucherEventController.remove()','DELETE',1,'admin','研发部门','/business/voucherEvent/21','127.0.0.1','内网IP','[21] ',NULL,1,'仅待生成状态的凭证事件允许删除','2026-11-22 11:43:34',4),(281,'凭证生成',2,'com.canglian.web.controller.business.FinVoucherEventController.generate()','PUT',1,'admin','研发部门','/business/voucherEvent/generate/21','127.0.0.1','内网IP','21 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:34',17),(282,'凭证回写',2,'com.canglian.web.controller.business.FinVoucherEventController.writeback()','PUT',1,'admin','研发部门','/business/voucherEvent/writeback/21','127.0.0.1','内网IP','21 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:34',12),(283,'凭证冲销',2,'com.canglian.web.controller.business.FinVoucherEventController.reverse()','PUT',1,'admin','研发部门','/business/voucherEvent/reverse/21','127.0.0.1','内网IP','21 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:34',15),(284,'凭证生成',2,'com.canglian.web.controller.business.FinVoucherEventController.generate()','PUT',1,'admin','研发部门','/business/voucherEvent/generate/16','127.0.0.1','内网IP','16 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:34',15),(285,'凭证冲销',2,'com.canglian.web.controller.business.FinVoucherEventController.reverse()','PUT',1,'admin','研发部门','/business/voucherEvent/reverse/16','127.0.0.1','内网IP','16 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:34',16),(286,'会计期间结账',2,'com.canglian.web.controller.business.FinGeneralLedgerController.closePeriod()','PUT',1,'admin','研发部门','/business/generalLedger/period/close/11','127.0.0.1','内网IP','11 ',NULL,1,'存在更早的未结账会计期间，请按期间顺序依次结账','2026-11-22 11:43:34',8),(287,'会计期间结账',2,'com.canglian.web.controller.business.FinGeneralLedgerController.closePeriod()','PUT',1,'admin','研发部门','/business/generalLedger/period/close/1','127.0.0.1','内网IP','1 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:34',14),(288,'费用',1,'com.canglian.web.controller.business.FinExpenseController.add()','POST',1,'admin','研发部门','/business/expense','127.0.0.1','内网IP','{\"amount\":10,\"createBy\":\"admin\",\"expenseDate\":\"2026-01-15 00:00:00\",\"expenseId\":8,\"expenseNo\":\"exp202611225812\",\"fundAccountId\":5,\"params\":{},\"remark\":\"LOCK_E2E_114333\",\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:34',6),(289,'费用确认',2,'com.canglian.web.controller.business.FinExpenseController.confirm()','PUT',1,'admin','研发部门','/business/expense/confirm/8','127.0.0.1','内网IP','8 ',NULL,1,'会计期间[2026-01]已结账，不允许执行该业务操作','2026-11-22 11:43:34',10),(290,'费用',3,'com.canglian.web.controller.business.FinExpenseController.remove()','DELETE',1,'admin','研发部门','/business/expense/8','127.0.0.1','内网IP','[8] ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:34',8),(291,'会计期间反结账',2,'com.canglian.web.controller.business.FinGeneralLedgerController.reopenPeriod()','PUT',1,'admin','研发部门','/business/generalLedger/period/reopen/1','127.0.0.1','内网IP','1 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 11:43:34',14),(292,'数据备份',1,'com.canglian.web.controller.business.BizDataBackupController.create()','POST',1,'admin','研发部门','/business/dataBackup/create','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"createTime\":\"2026-11-22 15:27:02\",\"fileName\":\"canglian_backup_20261122_152701.sql\",\"fileSize\":265942,\"fileSizeText\":\"259.71 KB\",\"rowCount\":1336,\"tableCount\":73}}',0,NULL,'2026-11-22 15:27:00',427),(293,'数据备份下载',5,'com.canglian.web.controller.business.BizDataBackupController.download()','GET',1,'admin','研发部门','/business/dataBackup/download/canglian_backup_20261122_152701.sql','127.0.0.1','内网IP','{}',NULL,0,NULL,'2026-11-22 15:27:00',9),(294,'数据备份',3,'com.canglian.web.controller.business.BizDataBackupController.remove()','DELETE',1,'admin','研发部门','/business/dataBackup/bad..name.sql','127.0.0.1','内网IP','\"bad..name.sql\" ',NULL,1,'非法的备份文件名','2026-11-22 15:27:00',1),(295,'数据备份',3,'com.canglian.web.controller.business.BizDataBackupController.remove()','DELETE',1,'admin','研发部门','/business/dataBackup/canglian_backup_20261122_152701.sql','127.0.0.1','内网IP','\"canglian_backup_20261122_152701.sql\" ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 15:27:00',1),(296,'库存流水',1,'com.canglian.web.controller.business.WmsStockLogController.add()','POST',1,'admin','研发部门','/business/stockLog','127.0.0.1','内网IP','{\"params\":{},\"productId\":1,\"quantity\":5,\"warehouseId\":1} ',NULL,1,'库存流水为系统审计台账，不允许手工新增','2026-11-22 15:27:00',4),(297,'库存流水',2,'com.canglian.web.controller.business.WmsStockLogController.edit()','PUT',1,'admin','研发部门','/business/stockLog','127.0.0.1','内网IP','{\"params\":{},\"quantity\":5,\"stockLogId\":1} ',NULL,1,'库存流水为系统审计台账，不允许手工修改','2026-11-22 15:27:00',1),(298,'库存流水',3,'com.canglian.web.controller.business.WmsStockLogController.remove()','DELETE',1,'admin','研发部门','/business/stockLog/1','127.0.0.1','内网IP','[1] ',NULL,1,'库存流水为系统审计台账，不允许删除','2026-11-22 15:27:00',1),(299,'商品档案',1,'com.canglian.web.controller.business.MdProductController.add()','POST',1,'admin','研发部门','/business/product','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"params\":{},\"productCode\":\"p001\",\"productName\":\"重复测试商品\",\"status\":\"0\"} ',NULL,1,'商品编码[p001]已存在，请勿重复','2026-11-22 15:27:00',12),(300,'客户档案',1,'com.canglian.web.controller.business.MdCustomerController.add()','POST',1,'admin','研发部门','/business/customer','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"customerCode\":\"cus001\",\"customerName\":\"重复测试客户\",\"params\":{},\"status\":\"0\"} ',NULL,1,'客户编码[cus001]已存在，请勿重复','2026-11-22 15:27:00',8),(301,'供应商档案',1,'com.canglian.web.controller.business.MdSupplierController.add()','POST',1,'admin','研发部门','/business/supplier','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"params\":{},\"status\":\"0\",\"supplierCode\":\"sup001\",\"supplierName\":\"重复测试供应商\"} ',NULL,1,'供应商编码[sup001]已存在，请勿重复','2026-11-22 15:27:00',7),(302,'仓库档案',1,'com.canglian.web.controller.business.MdWarehouseController.add()','POST',1,'admin','研发部门','/business/warehouse','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"params\":{},\"status\":\"0\",\"warehouseCode\":\"wh001\",\"warehouseName\":\"重复测试仓库\"} ',NULL,1,'仓库编码[wh001]已存在，请勿重复','2026-11-22 15:27:00',8),(303,'商品档案',3,'com.canglian.web.controller.business.MdProductController.remove()','DELETE',1,'admin','研发部门','/business/product/1','127.0.0.1','内网IP','[1] ',NULL,1,'商品[1]已存在库存台账记录，不允许删除，请改为停用','2026-11-22 15:27:00',4),(304,'仓库档案',3,'com.canglian.web.controller.business.MdWarehouseController.remove()','DELETE',1,'admin','研发部门','/business/warehouse/1','127.0.0.1','内网IP','[1] ',NULL,1,'仓库[1]已存在库存记录，不允许删除，请改为停用','2026-11-22 15:27:00',3),(305,'客户档案',3,'com.canglian.web.controller.business.MdCustomerController.remove()','DELETE',1,'admin','研发部门','/business/customer/1','127.0.0.1','内网IP','[1] ',NULL,1,'客户[1]存在关联应收单据，不允许删除，请改为停用','2026-11-22 15:27:00',7),(306,'供应商档案',3,'com.canglian.web.controller.business.MdSupplierController.remove()','DELETE',1,'admin','研发部门','/business/supplier/1','127.0.0.1','内网IP','[1] ',NULL,1,'供应商[1]存在关联应付单据，不允许删除，请改为停用','2026-11-22 15:27:01',8),(307,'入库单',1,'com.canglian.web.controller.business.WmsInboundController.add()','POST',1,'admin','研发部门','/business/inbound','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 15:27:02.523\",\"createBy\":\"admin\",\"inboundId\":104,\"inboundNo\":\"IN_P3_152702\",\"inboundType\":\"purchase\",\"itemList\":[{\"amount\":45,\"batchNo\":\"\",\"inboundId\":104,\"inboundItemId\":104,\"locationId\":0,\"price\":15,\"productId\":1,\"quantity\":3}],\"params\":{},\"status\":\"0\",\"supplierId\":1,\"totalAmount\":45,\"totalQty\":3,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 15:27:01',27),(308,'入库单',3,'com.canglian.web.controller.business.WmsInboundController.remove()','DELETE',1,'admin','研发部门','/business/inbound/104','127.0.0.1','内网IP','[104] ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 15:27:01',13),(309,'出库单',1,'com.canglian.web.controller.business.WmsOutboundController.add()','POST',1,'admin','研发部门','/business/outbound','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 15:27:02.672\",\"createBy\":\"admin\",\"customerId\":1,\"deliveryStatus\":\"0\",\"itemList\":[{\"amount\":56,\"batchNo\":\"\",\"locationId\":0,\"outboundId\":103,\"outboundItemId\":103,\"price\":28,\"productId\":1,\"quantity\":2}],\"outboundId\":103,\"outboundNo\":\"OUT_P3_152702\",\"outboundType\":\"sale\",\"params\":{},\"status\":\"0\",\"totalAmount\":56,\"totalQty\":2,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 15:27:01',41),(310,'出库单',3,'com.canglian.web.controller.business.WmsOutboundController.remove()','DELETE',1,'admin','研发部门','/business/outbound/103','127.0.0.1','内网IP','[103] ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 15:27:01',22),(311,'数据备份',1,'com.canglian.web.controller.business.BizDataBackupController.create()','POST',1,'admin','研发部门','/business/dataBackup/create','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"createTime\":\"2026-11-22 15:43:24\",\"fileName\":\"canglian_backup_20261122_154324.sql\",\"fileSize\":274272,\"fileSizeText\":\"267.84 KB\",\"rowCount\":1366,\"tableCount\":73}}',0,NULL,'2026-11-22 15:43:23',309),(312,'数据备份',3,'com.canglian.web.controller.business.BizDataBackupController.remove()','DELETE',1,'admin','研发部门','/business/dataBackup/canglian_backup_20261122_154324.sql','127.0.0.1','内网IP','\"canglian_backup_20261122_154324.sql\" ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 15:43:36',11),(313,'数据备份',1,'com.canglian.web.controller.business.BizDataBackupController.create()','POST',1,'admin','研发部门','/business/dataBackup/create','127.0.0.1','内网IP','{\"compress\":\"false\"}','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"checksumStatus\":\"valid\",\"compressed\":false,\"createTime\":\"2026-11-22 17:12:53\",\"fileName\":\"canglian_backup_20261122_171252.sql\",\"fileSize\":277387,\"fileSizeText\":\"270.89 KB\",\"rowCount\":1385,\"sha256\":\"0e53b19ed7415feaa0151fe5ced5f8815755771440568d13e815705187972cab\",\"tableCount\":73,\"verified\":true}}',0,NULL,'2026-11-22 17:12:51',351),(314,'数据备份',1,'com.canglian.web.controller.business.BizDataBackupController.create()','POST',1,'admin','研发部门','/business/dataBackup/create','127.0.0.1','内网IP','{\"compress\":\"true\"}','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"checksumStatus\":\"valid\",\"compressed\":true,\"createTime\":\"2026-11-22 17:12:53\",\"fileName\":\"canglian_backup_20261122_171253.sql.gz\",\"fileSize\":34649,\"fileSizeText\":\"33.84 KB\",\"rowCount\":1386,\"sha256\":\"e30152daed3d2b9300df707c59d07e62c2dc809447714e37c03cfe255bf20339\",\"tableCount\":73,\"verified\":true}}',0,NULL,'2026-11-22 17:12:51',311),(315,'数据备份受控恢复',2,'com.canglian.web.controller.business.BizDataBackupController.restore()','POST',1,'admin','研发部门','/business/dataBackup/restore','127.0.0.1','内网IP','{\"fileName\":\"canglian_backup_20261122_171252.sql\",\"confirmPhrase\":\"WRONG\"} ',NULL,1,'二次确认口令错误，请输入：RESTORE:canglian_backup_20261122_171252.sql','2026-11-22 17:12:51',1),(316,'数据备份',3,'com.canglian.web.controller.business.BizDataBackupController.remove()','DELETE',1,'admin','研发部门','/business/dataBackup/canglian_backup_20261122_171252.sql','127.0.0.1','内网IP','\"canglian_backup_20261122_171252.sql\" ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:12:51',1),(317,'数据备份',3,'com.canglian.web.controller.business.BizDataBackupController.remove()','DELETE',1,'admin','研发部门','/business/dataBackup/canglian_backup_20261122_171253.sql.gz','127.0.0.1','内网IP','\"canglian_backup_20261122_171253.sql.gz\" ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:12:51',1),(318,'全局成本算法配置',2,'com.canglian.web.controller.business.FinCostCalculateController.updateConfig()','PUT',1,'admin','研发部门','/business/costCalculate/config','127.0.0.1','内网IP','{\"costMethod\":\"fifo\"} ',NULL,1,'系统已存在 1 条历史成本流水，禁止跨算法切换；请在新账套启用前配置核算算法','2026-11-22 17:12:52',14),(319,'商品档案',1,'com.canglian.web.controller.business.MdProductController.add()','POST',1,'admin','研发部门','/business/product','127.0.0.1','内网IP','{\"costPrice\":10.0,\"createBy\":\"admin\",\"params\":{},\"productCode\":\"P4TEST338773\",\"productId\":106,\"productName\":\"P4验证商品338773\",\"salePrice\":25.0,\"status\":\"0\",\"warningMaxQty\":0,\"warningMinQty\":0} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:12:52',19),(320,'客户档案',1,'com.canglian.web.controller.business.MdCustomerController.add()','POST',1,'admin','研发部门','/business/customer','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"customerCode\":\"C4T338773\",\"customerId\":105,\"customerName\":\"P4验证客户338773\",\"params\":{},\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:12:52',15),(321,'供应商档案',1,'com.canglian.web.controller.business.MdSupplierController.add()','POST',1,'admin','研发部门','/business/supplier','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"params\":{},\"status\":\"0\",\"supplierCode\":\"S4T338773\",\"supplierId\":105,\"supplierName\":\"P4验证供应商338773\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:12:52',13),(322,'入库单',1,'com.canglian.web.controller.business.WmsInboundController.add()','POST',1,'admin','研发部门','/business/inbound','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"inboundId\":105,\"inboundNo\":\"inb20261122001\",\"inboundType\":\"purchase\",\"itemList\":[{\"amount\":100.0,\"batchNo\":\"\",\"inboundId\":105,\"inboundItemId\":105,\"locationId\":0,\"price\":10.0,\"productId\":106,\"quantity\":10}],\"params\":{},\"status\":\"0\",\"supplierId\":105,\"totalAmount\":100.0,\"totalQty\":10,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:12:52',30),(323,'入库审核',2,'com.canglian.web.controller.business.WmsInboundController.audit()','PUT',1,'admin','研发部门','/business/inbound/audit/105','127.0.0.1','内网IP','105 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:12:52',66),(324,'应付审核',2,'com.canglian.web.controller.business.FinPayableController.audit()','PUT',1,'admin','研发部门','/business/payable/audit/104','127.0.0.1','内网IP','104 ',NULL,1,'应付单已审核，请勿重复操作','2026-11-22 17:12:52',3),(325,'商品档案',2,'com.canglian.web.controller.business.MdProductController.edit()','PUT',1,'admin','研发部门','/business/product','127.0.0.1','内网IP','{\"costPrice\":99.0,\"params\":{},\"productCode\":\"P4TEST338773\",\"productId\":106,\"productName\":\"P4验证商品338773\",\"salePrice\":25.0,\"status\":\"0\",\"updateBy\":\"admin\",\"warningMaxQty\":0,\"warningMinQty\":0} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:12:52',14),(326,'出库单',1,'com.canglian.web.controller.business.WmsOutboundController.add()','POST',1,'admin','研发部门','/business/outbound','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"customerId\":105,\"deliveryStatus\":\"0\",\"itemList\":[{\"amount\":100.0,\"batchNo\":\"\",\"locationId\":0,\"outboundId\":104,\"outboundItemId\":104,\"price\":25.0,\"productId\":106,\"quantity\":4}],\"outboundId\":104,\"outboundNo\":\"out20261122001\",\"outboundType\":\"sale\",\"params\":{},\"status\":\"0\",\"totalAmount\":100.0,\"totalQty\":4,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:12:52',22),(327,'出库审核',2,'com.canglian.web.controller.business.WmsOutboundController.audit()','PUT',1,'admin','研发部门','/business/outbound/audit/104','127.0.0.1','内网IP','104 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:12:52',36),(328,'出库发货',2,'com.canglian.web.controller.business.WmsOutboundController.ship()','PUT',1,'admin','研发部门','/business/outbound/ship/104','127.0.0.1','内网IP','104 {\"carrier\":\"顺丰\",\"freightCost\":0,\"params\":{},\"waybillNo\":\"SF338773\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:12:52',10),(329,'出库签收',2,'com.canglian.web.controller.business.WmsOutboundController.sign()','PUT',1,'admin','研发部门','/business/outbound/sign/104','127.0.0.1','内网IP','104 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:12:52',52),(330,'应收审核',2,'com.canglian.web.controller.business.FinReceivableController.audit()','PUT',1,'admin','研发部门','/business/receivable/audit/105','127.0.0.1','内网IP','105 ',NULL,1,'应收单已审核，请勿重复操作','2026-11-22 17:12:53',4),(331,'销售退货',1,'com.canglian.web.controller.business.WmsSaleReturnController.add()','POST',1,'admin','研发部门','/business/saleReturn','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"customerId\":105,\"itemList\":[{\"amount\":25.0,\"batchNo\":\"\",\"locationId\":0,\"params\":{},\"price\":25.0,\"productId\":106,\"quantity\":1,\"saleReturnId\":5,\"saleReturnItemId\":5}],\"params\":{},\"returnNo\":\"srt20261122001\",\"returnType\":\"sale_return\",\"saleReturnId\":5,\"sourceBillId\":104,\"sourceBillNo\":\"out20261122001\",\"sourceBillType\":\"outbound\",\"status\":\"0\",\"totalAmount\":25.0,\"totalQty\":1,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:12:53',20),(332,'销售退货审核',2,'com.canglian.web.controller.business.WmsSaleReturnController.audit()','PUT',1,'admin','研发部门','/business/saleReturn/audit/5','127.0.0.1','内网IP','5 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:12:53',54),(333,'应收红蓝对冲',2,'com.canglian.web.controller.business.FinReceivableController.offset()','POST',1,'admin','研发部门','/business/receivable/offset','127.0.0.1','内网IP','{\"redReceivableId\":106,\"targetReceivableId\":105,\"offsetAmount\":25.0} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:12:53',24),(334,'采购退货',1,'com.canglian.web.controller.business.WmsPurchaseReturnController.add()','POST',1,'admin','研发部门','/business/purchaseReturn','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"itemList\":[{\"amount\":20.0,\"batchNo\":\"\",\"locationId\":0,\"params\":{},\"price\":10.0,\"productId\":106,\"purchaseReturnId\":5,\"purchaseReturnItemId\":5,\"quantity\":2}],\"params\":{},\"purchaseReturnId\":5,\"returnNo\":\"prt20261122001\",\"returnType\":\"purchase_return\",\"status\":\"0\",\"supplierId\":105,\"totalAmount\":20.0,\"totalQty\":2,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:12:53',23),(335,'采购退货审核',2,'com.canglian.web.controller.business.WmsPurchaseReturnController.audit()','PUT',1,'admin','研发部门','/business/purchaseReturn/audit/5','127.0.0.1','内网IP','5 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:12:53',47),(336,'应付红蓝对冲',2,'com.canglian.web.controller.business.FinPayableController.offset()','POST',1,'admin','研发部门','/business/payable/offset','127.0.0.1','内网IP','{\"redPayableId\":105,\"targetPayableId\":104,\"offsetAmount\":20.0} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:12:53',16),(337,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"receivableAging\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:12:54',746),(338,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"payableAging\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:12:54',51),(339,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"receivableReconciliation\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:12:54',30),(340,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"payableReconciliation\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:12:54',23),(341,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"profitLoss\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:12:54',28),(342,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"revenueExpense\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:12:54',25),(343,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"costStructure\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:12:54',25),(344,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"salesGrossProfit\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:12:54',28),(345,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"customerContribution\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:12:54',10),(346,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"productTurnover\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:12:54',13),(347,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"stockBalance\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:12:54',14),(348,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"fundAnalysis\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:12:54',9),(349,'成本期末重算',2,'com.canglian.web.controller.business.FinCostCalculateController.recalculate()','POST',1,'admin','研发部门','/business/costCalculate/recalculate','127.0.0.1','内网IP','{\"warehouseId\":1,\"productId\":106} ','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"costMethod\":\"moving\",\"recalculatedAccounts\":1,\"replayedLogs\":4,\"details\":[{\"warehouseId\":1,\"productId\":106,\"costMethod\":\"moving\",\"beforeTotalQty\":5.00,\"beforeAvgCost\":10.00,\"afterTotalQty\":5.00,\"afterTotalAmount\":50.00,\"afterAvgCost\":10.00}]}}',0,NULL,'2026-11-22 17:12:54',48),(350,'受控成本调价',2,'com.canglian.web.controller.business.FinCostCalculateController.adjust()','POST',1,'admin','研发部门','/business/costCalculate/adjust','127.0.0.1','内网IP','{\"warehouseId\":1,\"productId\":106,\"newAvgCost\":12.0,\"reason\":\"Phase4 E2E运费分摊验证\"} ','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"amount\":10.00,\"billId\":6,\"billType\":\"cost_adjust\",\"costAccountId\":6,\"costLogId\":22,\"costMethod\":\"moving\",\"createBy\":\"admin\",\"inOut\":\"1\",\"params\":{},\"price\":12.00,\"productId\":106,\"quantity\":5.00,\"remark\":\"成本调价(50.00->60.00)：Phase4 E2E运费分摊验证\",\"warehouseId\":1}}',0,NULL,'2026-11-22 17:12:54',20),(351,'数据备份',1,'com.canglian.web.controller.business.BizDataBackupController.create()','POST',1,'admin','研发部门','/business/dataBackup/create','127.0.0.1','内网IP','{\"compress\":\"false\"}','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"checksumStatus\":\"valid\",\"compressed\":false,\"createTime\":\"2026-11-22 17:14:23\",\"fileName\":\"canglian_backup_20261122_171422.sql\",\"fileSize\":292584,\"fileSizeText\":\"285.73 KB\",\"rowCount\":1424,\"sha256\":\"91b3b60ba5285d254e412721431ce6554fe81a2555abdecc12417d82d18f067e\",\"tableCount\":73,\"verified\":true}}',0,NULL,'2026-11-22 17:14:21',349),(352,'数据备份',1,'com.canglian.web.controller.business.BizDataBackupController.create()','POST',1,'admin','研发部门','/business/dataBackup/create','127.0.0.1','内网IP','{\"compress\":\"true\"}','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"checksumStatus\":\"valid\",\"compressed\":true,\"createTime\":\"2026-11-22 17:14:23\",\"fileName\":\"canglian_backup_20261122_171423.sql.gz\",\"fileSize\":36452,\"fileSizeText\":\"35.60 KB\",\"rowCount\":1425,\"sha256\":\"9f2cf922e04791f831d6e2fd297ef23fcc40f694861f6c0af8a9758ff337a152\",\"tableCount\":73,\"verified\":true}}',0,NULL,'2026-11-22 17:14:21',336),(353,'数据备份受控恢复',2,'com.canglian.web.controller.business.BizDataBackupController.restore()','POST',1,'admin','研发部门','/business/dataBackup/restore','127.0.0.1','内网IP','{\"fileName\":\"canglian_backup_20261122_171422.sql\",\"confirmPhrase\":\"WRONG\"} ',NULL,1,'二次确认口令错误，请输入：RESTORE:canglian_backup_20261122_171422.sql','2026-11-22 17:14:21',2),(354,'数据备份',3,'com.canglian.web.controller.business.BizDataBackupController.remove()','DELETE',1,'admin','研发部门','/business/dataBackup/canglian_backup_20261122_171422.sql','127.0.0.1','内网IP','\"canglian_backup_20261122_171422.sql\" ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:14:21',1),(355,'数据备份',3,'com.canglian.web.controller.business.BizDataBackupController.remove()','DELETE',1,'admin','研发部门','/business/dataBackup/canglian_backup_20261122_171423.sql.gz','127.0.0.1','内网IP','\"canglian_backup_20261122_171423.sql.gz\" ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:14:21',1),(356,'全局成本算法配置',2,'com.canglian.web.controller.business.FinCostCalculateController.updateConfig()','PUT',1,'admin','研发部门','/business/costCalculate/config','127.0.0.1','内网IP','{\"costMethod\":\"fifo\"} ',NULL,1,'系统已存在 1 条历史成本流水，禁止跨算法切换；请在新账套启用前配置核算算法','2026-11-22 17:14:22',15),(357,'商品档案',1,'com.canglian.web.controller.business.MdProductController.add()','POST',1,'admin','研发部门','/business/product','127.0.0.1','内网IP','{\"costPrice\":10.0,\"createBy\":\"admin\",\"params\":{},\"productCode\":\"P4TEST338863\",\"productId\":107,\"productName\":\"P4验证商品338863\",\"salePrice\":25.0,\"status\":\"0\",\"warningMaxQty\":0,\"warningMinQty\":0} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:14:22',24),(358,'客户档案',1,'com.canglian.web.controller.business.MdCustomerController.add()','POST',1,'admin','研发部门','/business/customer','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"customerCode\":\"C4T338863\",\"customerId\":106,\"customerName\":\"P4验证客户338863\",\"params\":{},\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:14:22',22),(359,'供应商档案',1,'com.canglian.web.controller.business.MdSupplierController.add()','POST',1,'admin','研发部门','/business/supplier','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"params\":{},\"status\":\"0\",\"supplierCode\":\"S4T338863\",\"supplierId\":106,\"supplierName\":\"P4验证供应商338863\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:14:22',22),(360,'入库单',1,'com.canglian.web.controller.business.WmsInboundController.add()','POST',1,'admin','研发部门','/business/inbound','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"inboundId\":106,\"inboundNo\":\"inb20261122001\",\"inboundType\":\"purchase\",\"itemList\":[{\"amount\":100.0,\"batchNo\":\"\",\"inboundId\":106,\"inboundItemId\":106,\"locationId\":0,\"price\":10.0,\"productId\":107,\"quantity\":10}],\"params\":{},\"status\":\"0\",\"supplierId\":106,\"totalAmount\":100.0,\"totalQty\":10,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:14:23',47),(361,'入库审核',2,'com.canglian.web.controller.business.WmsInboundController.audit()','PUT',1,'admin','研发部门','/business/inbound/audit/106','127.0.0.1','内网IP','106 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:14:23',88),(362,'应付审核',2,'com.canglian.web.controller.business.FinPayableController.audit()','PUT',1,'admin','研发部门','/business/payable/audit/106','127.0.0.1','内网IP','106 ',NULL,1,'应付单已审核，请勿重复操作','2026-11-22 17:14:23',4),(363,'商品档案',2,'com.canglian.web.controller.business.MdProductController.edit()','PUT',1,'admin','研发部门','/business/product','127.0.0.1','内网IP','{\"costPrice\":99.0,\"params\":{},\"productCode\":\"P4TEST338863\",\"productId\":107,\"productName\":\"P4验证商品338863\",\"salePrice\":25.0,\"status\":\"0\",\"updateBy\":\"admin\",\"warningMaxQty\":0,\"warningMinQty\":0} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:14:23',21),(364,'出库单',1,'com.canglian.web.controller.business.WmsOutboundController.add()','POST',1,'admin','研发部门','/business/outbound','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"customerId\":106,\"deliveryStatus\":\"0\",\"itemList\":[{\"amount\":100.0,\"batchNo\":\"\",\"locationId\":0,\"outboundId\":105,\"outboundItemId\":105,\"price\":25.0,\"productId\":107,\"quantity\":4}],\"outboundId\":105,\"outboundNo\":\"out20261122001\",\"outboundType\":\"sale\",\"params\":{},\"status\":\"0\",\"totalAmount\":100.0,\"totalQty\":4,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:14:23',40),(365,'出库审核',2,'com.canglian.web.controller.business.WmsOutboundController.audit()','PUT',1,'admin','研发部门','/business/outbound/audit/105','127.0.0.1','内网IP','105 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:14:23',52),(366,'出库发货',2,'com.canglian.web.controller.business.WmsOutboundController.ship()','PUT',1,'admin','研发部门','/business/outbound/ship/105','127.0.0.1','内网IP','105 {\"carrier\":\"顺丰\",\"freightCost\":0,\"params\":{},\"waybillNo\":\"SF338863\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:14:23',15),(367,'出库签收',2,'com.canglian.web.controller.business.WmsOutboundController.sign()','PUT',1,'admin','研发部门','/business/outbound/sign/105','127.0.0.1','内网IP','105 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:14:23',72),(368,'应收审核',2,'com.canglian.web.controller.business.FinReceivableController.audit()','PUT',1,'admin','研发部门','/business/receivable/audit/107','127.0.0.1','内网IP','107 ',NULL,1,'应收单已审核，请勿重复操作','2026-11-22 17:14:23',6),(369,'销售退货',1,'com.canglian.web.controller.business.WmsSaleReturnController.add()','POST',1,'admin','研发部门','/business/saleReturn','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"customerId\":106,\"itemList\":[{\"amount\":25.0,\"batchNo\":\"\",\"locationId\":0,\"params\":{},\"price\":25.0,\"productId\":107,\"quantity\":1,\"saleReturnId\":6,\"saleReturnItemId\":6}],\"params\":{},\"returnNo\":\"srt20261122001\",\"returnType\":\"sale_return\",\"saleReturnId\":6,\"sourceBillId\":105,\"sourceBillNo\":\"out20261122001\",\"sourceBillType\":\"outbound\",\"status\":\"0\",\"totalAmount\":25.0,\"totalQty\":1,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:14:23',39),(370,'销售退货审核',2,'com.canglian.web.controller.business.WmsSaleReturnController.audit()','PUT',1,'admin','研发部门','/business/saleReturn/audit/6','127.0.0.1','内网IP','6 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:14:23',69),(371,'应收红蓝对冲',2,'com.canglian.web.controller.business.FinReceivableController.offset()','POST',1,'admin','研发部门','/business/receivable/offset','127.0.0.1','内网IP','{\"redReceivableId\":108,\"targetReceivableId\":107,\"offsetAmount\":25.0} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:14:23',31),(372,'采购退货',1,'com.canglian.web.controller.business.WmsPurchaseReturnController.add()','POST',1,'admin','研发部门','/business/purchaseReturn','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"itemList\":[{\"amount\":20.0,\"batchNo\":\"\",\"locationId\":0,\"params\":{},\"price\":10.0,\"productId\":107,\"purchaseReturnId\":6,\"purchaseReturnItemId\":6,\"quantity\":2}],\"params\":{},\"purchaseReturnId\":6,\"returnNo\":\"prt20261122001\",\"returnType\":\"purchase_return\",\"status\":\"0\",\"supplierId\":106,\"totalAmount\":20.0,\"totalQty\":2,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:14:23',42),(373,'采购退货审核',2,'com.canglian.web.controller.business.WmsPurchaseReturnController.audit()','PUT',1,'admin','研发部门','/business/purchaseReturn/audit/6','127.0.0.1','内网IP','6 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:14:24',77),(374,'应付红蓝对冲',2,'com.canglian.web.controller.business.FinPayableController.offset()','POST',1,'admin','研发部门','/business/payable/offset','127.0.0.1','内网IP','{\"redPayableId\":107,\"targetPayableId\":106,\"offsetAmount\":20.0} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:14:24',27),(375,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"receivableAging\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:14:24',424),(376,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"payableAging\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:14:24',40),(377,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"receivableReconciliation\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:14:24',26),(378,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"payableReconciliation\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:14:24',24),(379,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"profitLoss\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:14:24',22),(380,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"revenueExpense\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:14:24',20),(381,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"costStructure\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:14:24',21),(382,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"salesGrossProfit\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:14:24',28),(383,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"customerContribution\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:14:24',11),(384,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"productTurnover\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:14:24',12),(385,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"stockBalance\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:14:24',18),(386,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"fundAnalysis\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:14:25',11),(387,'成本期末重算',2,'com.canglian.web.controller.business.FinCostCalculateController.recalculate()','POST',1,'admin','研发部门','/business/costCalculate/recalculate','127.0.0.1','内网IP','{\"warehouseId\":1,\"productId\":107} ','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"costMethod\":\"moving\",\"recalculatedAccounts\":1,\"replayedLogs\":4,\"details\":[{\"warehouseId\":1,\"productId\":107,\"costMethod\":\"moving\",\"beforeTotalQty\":5.00,\"beforeAvgCost\":10.00,\"afterTotalQty\":5.00,\"afterTotalAmount\":50.00,\"afterAvgCost\":10.00}]}}',0,NULL,'2026-11-22 17:14:25',52),(388,'受控成本调价',2,'com.canglian.web.controller.business.FinCostCalculateController.adjust()','POST',1,'admin','研发部门','/business/costCalculate/adjust','127.0.0.1','内网IP','{\"warehouseId\":1,\"productId\":107,\"newAvgCost\":12.0,\"reason\":\"Phase4 E2E运费分摊验证\"} ','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"amount\":10.00,\"billId\":7,\"billType\":\"cost_adjust\",\"costAccountId\":7,\"costLogId\":31,\"costMethod\":\"moving\",\"createBy\":\"admin\",\"inOut\":\"1\",\"params\":{},\"price\":12.00,\"productId\":107,\"quantity\":5.00,\"remark\":\"成本调价(50.00->60.00)：Phase4 E2E运费分摊验证\",\"warehouseId\":1}}',0,NULL,'2026-11-22 17:14:25',20),(389,'数据备份',1,'com.canglian.web.controller.business.BizDataBackupController.create()','POST',1,'admin','研发部门','/business/dataBackup/create','127.0.0.1','内网IP','{\"compress\":\"false\"}','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"checksumStatus\":\"valid\",\"compressed\":false,\"createTime\":\"2026-11-22 17:15:10\",\"fileName\":\"canglian_backup_20261122_171510.sql\",\"fileSize\":307782,\"fileSizeText\":\"300.57 KB\",\"rowCount\":1463,\"sha256\":\"727018e7bc3697e8e5dd803ec4f6c2c11ca3b59e5e329b3ed5673e5b6532b9fd\",\"tableCount\":73,\"verified\":true}}',0,NULL,'2026-11-22 17:15:09',254),(390,'数据备份',1,'com.canglian.web.controller.business.BizDataBackupController.create()','POST',1,'admin','研发部门','/business/dataBackup/create','127.0.0.1','内网IP','{\"compress\":\"true\"}','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"checksumStatus\":\"valid\",\"compressed\":true,\"createTime\":\"2026-11-22 17:15:10\",\"fileName\":\"canglian_backup_20261122_171510.sql.gz\",\"fileSize\":37383,\"fileSizeText\":\"36.51 KB\",\"rowCount\":1464,\"sha256\":\"4222f981c1fea4bbec5da79bbe3353ca440691e72e30432876aa4d67da4e73ad\",\"tableCount\":73,\"verified\":true}}',0,NULL,'2026-11-22 17:15:09',281),(391,'数据备份受控恢复',2,'com.canglian.web.controller.business.BizDataBackupController.restore()','POST',1,'admin','研发部门','/business/dataBackup/restore','127.0.0.1','内网IP','{\"fileName\":\"canglian_backup_20261122_171510.sql\",\"confirmPhrase\":\"WRONG\"} ',NULL,1,'二次确认口令错误，请输入：RESTORE:canglian_backup_20261122_171510.sql','2026-11-22 17:15:09',0),(392,'数据备份',3,'com.canglian.web.controller.business.BizDataBackupController.remove()','DELETE',1,'admin','研发部门','/business/dataBackup/canglian_backup_20261122_171510.sql','127.0.0.1','内网IP','\"canglian_backup_20261122_171510.sql\" ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:15:09',1),(393,'数据备份',3,'com.canglian.web.controller.business.BizDataBackupController.remove()','DELETE',1,'admin','研发部门','/business/dataBackup/canglian_backup_20261122_171510.sql.gz','127.0.0.1','内网IP','\"canglian_backup_20261122_171510.sql.gz\" ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:15:09',1),(394,'全局成本算法配置',2,'com.canglian.web.controller.business.FinCostCalculateController.updateConfig()','PUT',1,'admin','研发部门','/business/costCalculate/config','127.0.0.1','内网IP','{\"costMethod\":\"fifo\"} ',NULL,1,'系统已存在 1 条历史成本流水，禁止跨算法切换；请在新账套启用前配置核算算法','2026-11-22 17:15:09',11),(395,'商品档案',1,'com.canglian.web.controller.business.MdProductController.add()','POST',1,'admin','研发部门','/business/product','127.0.0.1','内网IP','{\"costPrice\":10.0,\"createBy\":\"admin\",\"params\":{},\"productCode\":\"P4TEST338911\",\"productId\":108,\"productName\":\"P4验证商品338911\",\"salePrice\":25.0,\"status\":\"0\",\"warningMaxQty\":0,\"warningMinQty\":0} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:15:09',8),(396,'客户档案',1,'com.canglian.web.controller.business.MdCustomerController.add()','POST',1,'admin','研发部门','/business/customer','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"customerCode\":\"C4T338911\",\"customerId\":107,\"customerName\":\"P4验证客户338911\",\"params\":{},\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:15:09',9),(397,'供应商档案',1,'com.canglian.web.controller.business.MdSupplierController.add()','POST',1,'admin','研发部门','/business/supplier','127.0.0.1','内网IP','{\"createBy\":\"admin\",\"params\":{},\"status\":\"0\",\"supplierCode\":\"S4T338911\",\"supplierId\":107,\"supplierName\":\"P4验证供应商338911\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:15:09',6),(398,'入库单',1,'com.canglian.web.controller.business.WmsInboundController.add()','POST',1,'admin','研发部门','/business/inbound','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"inboundId\":107,\"inboundNo\":\"inb20261122001\",\"inboundType\":\"purchase\",\"itemList\":[{\"amount\":100.0,\"batchNo\":\"\",\"inboundId\":107,\"inboundItemId\":107,\"locationId\":0,\"price\":10.0,\"productId\":108,\"quantity\":10}],\"params\":{},\"status\":\"0\",\"supplierId\":107,\"totalAmount\":100.0,\"totalQty\":10,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:15:09',14),(399,'入库审核',2,'com.canglian.web.controller.business.WmsInboundController.audit()','PUT',1,'admin','研发部门','/business/inbound/audit/107','127.0.0.1','内网IP','107 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:15:10',48),(400,'应付审核',2,'com.canglian.web.controller.business.FinPayableController.audit()','PUT',1,'admin','研发部门','/business/payable/audit/108','127.0.0.1','内网IP','108 ',NULL,1,'应付单已审核，请勿重复操作','2026-11-22 17:15:10',2),(401,'商品档案',2,'com.canglian.web.controller.business.MdProductController.edit()','PUT',1,'admin','研发部门','/business/product','127.0.0.1','内网IP','{\"costPrice\":99.0,\"params\":{},\"productCode\":\"P4TEST338911\",\"productId\":108,\"productName\":\"P4验证商品338911\",\"salePrice\":25.0,\"status\":\"0\",\"updateBy\":\"admin\",\"warningMaxQty\":0,\"warningMinQty\":0} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:15:10',14),(402,'出库单',1,'com.canglian.web.controller.business.WmsOutboundController.add()','POST',1,'admin','研发部门','/business/outbound','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"customerId\":107,\"deliveryStatus\":\"0\",\"itemList\":[{\"amount\":100.0,\"batchNo\":\"\",\"locationId\":0,\"outboundId\":106,\"outboundItemId\":106,\"price\":25.0,\"productId\":108,\"quantity\":4}],\"outboundId\":106,\"outboundNo\":\"out20261122001\",\"outboundType\":\"sale\",\"params\":{},\"status\":\"0\",\"totalAmount\":100.0,\"totalQty\":4,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:15:10',14),(403,'出库审核',2,'com.canglian.web.controller.business.WmsOutboundController.audit()','PUT',1,'admin','研发部门','/business/outbound/audit/106','127.0.0.1','内网IP','106 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:15:10',36),(404,'出库发货',2,'com.canglian.web.controller.business.WmsOutboundController.ship()','PUT',1,'admin','研发部门','/business/outbound/ship/106','127.0.0.1','内网IP','106 {\"carrier\":\"顺丰\",\"freightCost\":0,\"params\":{},\"waybillNo\":\"SF338911\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:15:10',7),(405,'出库签收',2,'com.canglian.web.controller.business.WmsOutboundController.sign()','PUT',1,'admin','研发部门','/business/outbound/sign/106','127.0.0.1','内网IP','106 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:15:10',36),(406,'应收审核',2,'com.canglian.web.controller.business.FinReceivableController.audit()','PUT',1,'admin','研发部门','/business/receivable/audit/109','127.0.0.1','内网IP','109 ',NULL,1,'应收单已审核，请勿重复操作','2026-11-22 17:15:10',3),(407,'销售退货',1,'com.canglian.web.controller.business.WmsSaleReturnController.add()','POST',1,'admin','研发部门','/business/saleReturn','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"customerId\":107,\"itemList\":[{\"amount\":25.0,\"batchNo\":\"\",\"locationId\":0,\"params\":{},\"price\":25.0,\"productId\":108,\"quantity\":1,\"saleReturnId\":7,\"saleReturnItemId\":7}],\"params\":{},\"returnNo\":\"srt20261122001\",\"returnType\":\"sale_return\",\"saleReturnId\":7,\"sourceBillId\":106,\"sourceBillNo\":\"out20261122001\",\"sourceBillType\":\"outbound\",\"status\":\"0\",\"totalAmount\":25.0,\"totalQty\":1,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:15:10',13),(408,'销售退货审核',2,'com.canglian.web.controller.business.WmsSaleReturnController.audit()','PUT',1,'admin','研发部门','/business/saleReturn/audit/7','127.0.0.1','内网IP','7 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:15:10',45),(409,'应收红蓝对冲',2,'com.canglian.web.controller.business.FinReceivableController.offset()','POST',1,'admin','研发部门','/business/receivable/offset','127.0.0.1','内网IP','{\"redReceivableId\":110,\"targetReceivableId\":109,\"offsetAmount\":25.0} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:15:10',18),(410,'采购退货',1,'com.canglian.web.controller.business.WmsPurchaseReturnController.add()','POST',1,'admin','研发部门','/business/purchaseReturn','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"itemList\":[{\"amount\":20.0,\"batchNo\":\"\",\"locationId\":0,\"params\":{},\"price\":10.0,\"productId\":108,\"purchaseReturnId\":7,\"purchaseReturnItemId\":7,\"quantity\":2}],\"params\":{},\"purchaseReturnId\":7,\"returnNo\":\"prt20261122001\",\"returnType\":\"purchase_return\",\"status\":\"0\",\"supplierId\":107,\"totalAmount\":20.0,\"totalQty\":2,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:15:10',15),(411,'采购退货审核',2,'com.canglian.web.controller.business.WmsPurchaseReturnController.audit()','PUT',1,'admin','研发部门','/business/purchaseReturn/audit/7','127.0.0.1','内网IP','7 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:15:10',44),(412,'应付红蓝对冲',2,'com.canglian.web.controller.business.FinPayableController.offset()','POST',1,'admin','研发部门','/business/payable/offset','127.0.0.1','内网IP','{\"redPayableId\":109,\"targetPayableId\":108,\"offsetAmount\":20.0} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 17:15:10',17),(413,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"receivableAging\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:15:10',22),(414,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"payableAging\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:15:10',20),(415,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"receivableReconciliation\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:15:10',22),(416,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"payableReconciliation\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:15:10',21),(417,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"profitLoss\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:15:10',20),(418,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"revenueExpense\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:15:11',22),(419,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"costStructure\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:15:11',16),(420,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"salesGrossProfit\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:15:11',14),(421,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"customerContribution\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:15:11',8),(422,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"productTurnover\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:15:11',20),(423,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"stockBalance\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:15:11',9),(424,'财务经营报表导出',5,'com.canglian.web.controller.business.FinReportController.exportReport()','POST',1,'admin','研发部门','/business/report/export','127.0.0.1','内网IP','{\"reportType\":\"fundAnalysis\",\"endDate\":\"2026-11-22\",\"startDate\":\"2026-11-22\"}',NULL,0,NULL,'2026-11-22 17:15:11',7),(425,'成本期末重算',2,'com.canglian.web.controller.business.FinCostCalculateController.recalculate()','POST',1,'admin','研发部门','/business/costCalculate/recalculate','127.0.0.1','内网IP','{\"warehouseId\":1,\"productId\":108} ','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"costMethod\":\"moving\",\"recalculatedAccounts\":1,\"replayedLogs\":4,\"details\":[{\"warehouseId\":1,\"productId\":108,\"costMethod\":\"moving\",\"beforeTotalQty\":5.00,\"beforeAvgCost\":10.00,\"afterTotalQty\":5.00,\"afterTotalAmount\":50.00,\"afterAvgCost\":10.00}]}}',0,NULL,'2026-11-22 17:15:11',55),(426,'受控成本调价',2,'com.canglian.web.controller.business.FinCostCalculateController.adjust()','POST',1,'admin','研发部门','/business/costCalculate/adjust','127.0.0.1','内网IP','{\"warehouseId\":1,\"productId\":108,\"newAvgCost\":12.0,\"reason\":\"Phase4 E2E运费分摊验证\"} ','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"amount\":10.00,\"billId\":8,\"billType\":\"cost_adjust\",\"costAccountId\":8,\"costLogId\":40,\"costMethod\":\"moving\",\"createBy\":\"admin\",\"inOut\":\"1\",\"params\":{},\"price\":12.00,\"productId\":108,\"quantity\":5.00,\"remark\":\"成本调价(50.00->60.00)：Phase4 E2E运费分摊验证\",\"warehouseId\":1}}',0,NULL,'2026-11-22 17:15:11',18),(427,'商品多单位',1,'com.canglian.web.controller.business.MdProductUnitController.add()','POST',1,'admin','研发部门','/business/productUnit','127.0.0.1','内网IP','{\"barCode\":\"E2E-BOX-888\",\"convertRatio\":12,\"createBy\":\"admin\",\"isBaseUnit\":\"0\",\"params\":{},\"productId\":1,\"purchasePrice\":120,\"salePrice\":240,\"status\":\"0\",\"unitId\":1,\"unitName\":\"箱E2E\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:58:26',20),(428,'购货订单',1,'com.canglian.web.controller.business.PurOrderController.add()','POST',1,'admin','研发部门','/business/purOrder','127.0.0.1','内网IP','{\"approveBy\":\"\",\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"deptId\":103,\"discountAmount\":10,\"orderItemList\":[{\"amount\":1017.00,\"baseQuantity\":10.0000,\"convertRatio\":1,\"discountRate\":90,\"price\":100,\"productId\":1,\"purchaseOrderId\":4,\"purchaseOrderItemId\":7,\"quantity\":10,\"receivedQty\":0,\"taxAmount\":117.00,\"taxRate\":13,\"unitName\":\"件\"}],\"ownerUserId\":1,\"params\":{},\"purchaseOrderId\":4,\"purchaseOrderNo\":\"pur20261122001\",\"status\":\"0\",\"supplierId\":1,\"taxTotalAmount\":117.00,\"totalAmount\":1007.00,\"totalQty\":10,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:58:27',388),(429,'购货订单审批',2,'com.canglian.web.controller.business.PurOrderController.approve()','PUT',1,'admin','研发部门','/business/purOrder/approve/4','127.0.0.1','内网IP','4 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:58:27',27),(430,'购货订单反审批',2,'com.canglian.web.controller.business.PurOrderController.unapprove()','PUT',1,'admin','研发部门','/business/purOrder/unapprove/4','127.0.0.1','内网IP','4 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:58:27',19),(431,'购货订单审批',2,'com.canglian.web.controller.business.PurOrderController.approve()','PUT',1,'admin','研发部门','/business/purOrder/approve/4','127.0.0.1','内网IP','4 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:58:27',22),(432,'购货订单下推入库单',2,'com.canglian.web.controller.business.PurOrderController.pushInbound()','PUT',1,'admin','研发部门','/business/purOrder/pushInbound/4','127.0.0.1','内网IP','4 [{\"purchaseOrderItemId\":9,\"pushQty\":4}] ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:58:27',37),(433,'购货订单下推入库单',2,'com.canglian.web.controller.business.PurOrderController.pushInbound()','PUT',1,'admin','研发部门','/business/purOrder/pushInbound/4','127.0.0.1','内网IP','4 [{\"purchaseOrderItemId\":9,\"pushQty\":7}] ',NULL,1,'商品 #1 本次下推数量(7)超过剩余可下推数量(6.0000)','2026-11-22 18:58:27',9),(434,'购货订单强制关闭',2,'com.canglian.web.controller.business.PurOrderController.close()','PUT',1,'admin','研发部门','/business/purOrder/close/4','127.0.0.1','内网IP','4 {\"closeReason\":\"E2E测试关闭余单\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:58:27',12),(435,'入库单',1,'com.canglian.web.controller.business.WmsInboundController.add()','POST',1,'admin','研发部门','/business/inbound','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"deptId\":103,\"inboundId\":109,\"inboundNo\":\"inb20261122002\",\"inboundType\":\"purchase\",\"isReversal\":\"0\",\"itemList\":[{\"amount\":1000,\"baseQuantity\":20.0000,\"batchNo\":\"E2E-FEFO-1\",\"convertRatio\":1,\"expiryDate\":\"2026-12-07\",\"inboundId\":109,\"inboundItemId\":109,\"locationId\":0,\"price\":50,\"productId\":1,\"productionDate\":\"2026-11-12\",\"quantity\":20,\"unitName\":\"把\"}],\"ownerUserId\":1,\"params\":{},\"reversedFlag\":\"0\",\"status\":\"0\",\"supplierId\":1,\"totalAmount\":1000,\"totalQty\":20,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:58:27',26),(436,'入库审核',2,'com.canglian.web.controller.business.WmsInboundController.audit()','PUT',1,'admin','研发部门','/business/inbound/audit/109','127.0.0.1','内网IP','109 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:58:27',73),(437,'入库反审核',2,'com.canglian.web.controller.business.WmsInboundController.unaudit()','PUT',1,'admin','研发部门','/business/inbound/unaudit/109','127.0.0.1','内网IP','109 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:58:27',53),(438,'入库审核',2,'com.canglian.web.controller.business.WmsInboundController.audit()','PUT',1,'admin','研发部门','/business/inbound/audit/109','127.0.0.1','内网IP','109 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:58:27',59),(439,'商品多单位',1,'com.canglian.web.controller.business.MdProductUnitController.add()','POST',1,'admin','研发部门','/business/productUnit','127.0.0.1','内网IP','{\"barCode\":\"E2E-BOX-888\",\"convertRatio\":12,\"createBy\":\"admin\",\"isBaseUnit\":\"0\",\"params\":{},\"productId\":1,\"purchasePrice\":120,\"salePrice\":240,\"status\":\"0\",\"unitId\":2,\"unitName\":\"箱E2E\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:50',9),(440,'购货订单',1,'com.canglian.web.controller.business.PurOrderController.add()','POST',1,'admin','研发部门','/business/purOrder','127.0.0.1','内网IP','{\"approveBy\":\"\",\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"deptId\":103,\"discountAmount\":10,\"orderItemList\":[{\"amount\":1017.00,\"baseQuantity\":10.0000,\"convertRatio\":1,\"discountRate\":90,\"price\":100,\"productId\":1,\"purchaseOrderId\":5,\"purchaseOrderItemId\":10,\"quantity\":10,\"receivedQty\":0,\"taxAmount\":117.00,\"taxRate\":13,\"unitName\":\"件\"}],\"ownerUserId\":1,\"params\":{},\"purchaseOrderId\":5,\"purchaseOrderNo\":\"pur20261122001\",\"status\":\"0\",\"supplierId\":1,\"taxTotalAmount\":117.00,\"totalAmount\":1007.00,\"totalQty\":10,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:50',20),(441,'购货订单审批',2,'com.canglian.web.controller.business.PurOrderController.approve()','PUT',1,'admin','研发部门','/business/purOrder/approve/5','127.0.0.1','内网IP','5 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:50',25),(442,'购货订单反审批',2,'com.canglian.web.controller.business.PurOrderController.unapprove()','PUT',1,'admin','研发部门','/business/purOrder/unapprove/5','127.0.0.1','内网IP','5 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:50',15),(443,'购货订单审批',2,'com.canglian.web.controller.business.PurOrderController.approve()','PUT',1,'admin','研发部门','/business/purOrder/approve/5','127.0.0.1','内网IP','5 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:50',19),(444,'购货订单下推入库单',2,'com.canglian.web.controller.business.PurOrderController.pushInbound()','PUT',1,'admin','研发部门','/business/purOrder/pushInbound/5','127.0.0.1','内网IP','5 [{\"purchaseOrderItemId\":12,\"pushQty\":4}] ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:50',25),(445,'购货订单下推入库单',2,'com.canglian.web.controller.business.PurOrderController.pushInbound()','PUT',1,'admin','研发部门','/business/purOrder/pushInbound/5','127.0.0.1','内网IP','5 [{\"purchaseOrderItemId\":12,\"pushQty\":7}] ',NULL,1,'商品 #1 本次下推数量(7)超过剩余可下推数量(6.0000)','2026-11-22 18:59:50',8),(446,'购货订单强制关闭',2,'com.canglian.web.controller.business.PurOrderController.close()','PUT',1,'admin','研发部门','/business/purOrder/close/5','127.0.0.1','内网IP','5 {\"closeReason\":\"E2E测试关闭余单\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:50',9),(447,'入库单',1,'com.canglian.web.controller.business.WmsInboundController.add()','POST',1,'admin','研发部门','/business/inbound','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"deptId\":103,\"inboundId\":111,\"inboundNo\":\"inb20261122002\",\"inboundType\":\"purchase\",\"isReversal\":\"0\",\"itemList\":[{\"amount\":1000,\"baseQuantity\":20.0000,\"batchNo\":\"E2E-FEFO-1\",\"convertRatio\":1,\"expiryDate\":\"2026-12-07\",\"inboundId\":111,\"inboundItemId\":111,\"locationId\":0,\"price\":50,\"productId\":1,\"productionDate\":\"2026-11-12\",\"quantity\":20,\"unitName\":\"把\"}],\"ownerUserId\":1,\"params\":{},\"reversedFlag\":\"0\",\"status\":\"0\",\"supplierId\":1,\"totalAmount\":1000,\"totalQty\":20,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:50',19),(448,'入库审核',2,'com.canglian.web.controller.business.WmsInboundController.audit()','PUT',1,'admin','研发部门','/business/inbound/audit/111','127.0.0.1','内网IP','111 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:50',53),(449,'入库反审核',2,'com.canglian.web.controller.business.WmsInboundController.unaudit()','PUT',1,'admin','研发部门','/business/inbound/unaudit/111','127.0.0.1','内网IP','111 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:50',42),(450,'入库审核',2,'com.canglian.web.controller.business.WmsInboundController.audit()','PUT',1,'admin','研发部门','/business/inbound/audit/111','127.0.0.1','内网IP','111 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:50',51),(451,'库存冻结',2,'com.canglian.web.controller.business.WmsStockController.freeze()','POST',1,'admin','研发部门','/business/stock/freeze','127.0.0.1','内网IP','{\"stockId\":116,\"quantity\":5,\"reason\":\"E2E质检冻结\"} ','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"availableQuantity\":15.00,\"batchNo\":\"E2E-FEFO-1\",\"createBy\":\"admin\",\"createTime\":\"2026-11-22 18:59:50\",\"expiryDate\":\"2026-12-07 00:00:00\",\"expiryStatus\":\"expiring\",\"frozenQuantity\":5.00,\"locationId\":0,\"lockedQuantity\":0.00,\"params\":{},\"productId\":1,\"productionDate\":\"2026-11-12 00:00:00\",\"quantity\":20.00,\"remainDays\":14,\"stockId\":116,\"updateBy\":\"admin\",\"updateTime\":\"2026-11-22 18:59:50\",\"version\":2,\"warehouseId\":1,\"warningMaxQty\":0.00,\"warningMinQty\":0.00}}',0,NULL,'2026-11-22 18:59:50',13),(452,'库存解冻',2,'com.canglian.web.controller.business.WmsStockController.unfreeze()','POST',1,'admin','研发部门','/business/stock/unfreeze','127.0.0.1','内网IP','{\"stockId\":116,\"quantity\":5,\"reason\":\"E2E质检放行\"} ','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"availableQuantity\":20.00,\"batchNo\":\"E2E-FEFO-1\",\"createBy\":\"admin\",\"createTime\":\"2026-11-22 18:59:50\",\"expiryDate\":\"2026-12-07 00:00:00\",\"expiryStatus\":\"expiring\",\"frozenQuantity\":0.00,\"locationId\":0,\"lockedQuantity\":0.00,\"params\":{},\"productId\":1,\"productionDate\":\"2026-11-12 00:00:00\",\"quantity\":20.00,\"remainDays\":14,\"stockId\":116,\"updateBy\":\"admin\",\"updateTime\":\"2026-11-22 18:59:50\",\"version\":3,\"warehouseId\":1,\"warningMaxQty\":0.00,\"warningMinQty\":0.00}}',0,NULL,'2026-11-22 18:59:50',13),(453,'入库红冲',1,'com.canglian.web.controller.business.WmsInboundController.reverse()','POST',1,'admin','研发部门','/business/inbound/reverse/111','127.0.0.1','内网IP','111 ','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"auditBy\":\"admin\",\"auditTime\":\"2026-11-22 18:59:52\",\"bizStatus\":\"completed\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"createTime\":\"2026-11-22 18:59:50\",\"deptId\":103,\"inboundId\":112,\"inboundNo\":\"inb20261122003\",\"inboundType\":\"purchase\",\"isReversal\":\"1\",\"itemList\":[{\"amount\":-1000.00,\"baseQuantity\":-20.0000,\"batchNo\":\"E2E-FEFO-1\",\"convertRatio\":1.0000,\"expiryDate\":\"2026-12-07\",\"inboundId\":112,\"inboundItemId\":112,\"locationId\":0,\"price\":50.00,\"productId\":1,\"productionDate\":\"2026-11-12\",\"quantity\":-20.00,\"unitName\":\"把\"}],\"originBillId\":111,\"ownerUserId\":1,\"params\":{},\"remark\":\"红冲原入库单：inb20261122002\",\"reversedFlag\":\"0\",\"sourceBillId\":111,\"sourceBillNo\":\"inb20261122002\",\"sourceBillType\":\"inbound_reverse\",\"status\":\"1\",\"supplierId\":1,\"totalAmount\":-1000.00,\"totalQty\":-20.00,\"warehouseId\":1}}',0,NULL,'2026-11-22 18:59:50',52),(454,'出库单',1,'com.canglian.web.controller.business.WmsOutboundController.add()','POST',1,'admin','研发部门','/business/outbound','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"customerId\":1,\"deliveryStatus\":\"0\",\"deptId\":103,\"isReversal\":\"0\",\"itemList\":[{\"amount\":160,\"baseQuantity\":2.0000,\"batchNo\":\"E2E-EXPIRED\",\"convertRatio\":1,\"locationId\":0,\"outboundId\":107,\"outboundItemId\":107,\"price\":80,\"productId\":1,\"quantity\":2,\"unitName\":\"把\"}],\"outboundId\":107,\"outboundNo\":\"out20261122001\",\"outboundType\":\"sale\",\"ownerUserId\":1,\"params\":{},\"reversedFlag\":\"0\",\"status\":\"0\",\"totalAmount\":160,\"totalQty\":2,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:51',25),(455,'出库审核',2,'com.canglian.web.controller.business.WmsOutboundController.audit()','PUT',1,'admin','研发部门','/business/outbound/audit/107','127.0.0.1','内网IP','107 ',NULL,1,'商品 #1 批次[E2E-EXPIRED] 已过有效期(2026-11-20)，禁止出库','2026-11-22 18:59:51',11),(456,'扫码快速入库',1,'com.canglian.web.controller.business.BusinessScanController.quickInbound()','POST',1,'admin','研发部门','/business/scan/quickInbound','127.0.0.1','内网IP','{\"warehouseId\":1,\"supplierId\":1,\"remark\":\"E2E扫码入库\",\"items\":[{\"barCode\":\"E2E-BOX-888\",\"quantity\":1,\"batchNo\":\"E2E-SCAN-B1\"}]} ','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"inbound\":{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"createTime\":\"2026-11-22 18:59:51\",\"deptId\":103,\"inboundId\":113,\"inboundNo\":\"inb20261122004\",\"inboundType\":\"purchase\",\"isReversal\":\"0\",\"itemList\":[{\"amount\":120.00,\"baseQuantity\":12.0000,\"batchNo\":\"E2E-SCAN-B1\",\"convertRatio\":12.0000,\"inboundId\":113,\"inboundItemId\":113,\"locationId\":0,\"price\":120.00,\"productId\":1,\"quantity\":1.00,\"unitName\":\"箱E2E\"}],\"ownerUserId\":1,\"params\":{},\"remark\":\"E2E扫码入库\",\"reversedFlag\":\"0\",\"status\":\"0\",\"supplierId\":1,\"totalAmount\":120.00,\"totalQty\":1.00,\"updateTime\":\"2026-11-22 18:59:51\",\"warehouseId\":1}}}',0,NULL,'2026-11-22 18:59:51',27),(457,'销售单据',1,'com.canglian.web.controller.business.SalOrderController.add()','POST',1,'admin','研发部门','/business/saleOrder','127.0.0.1','内网IP','{\"approveBy\":\"\",\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"customerId\":1,\"deptId\":103,\"discountAmount\":0,\"orderId\":4,\"orderItemList\":[{\"amount\":678.00,\"baseQuantity\":5.0000,\"convertRatio\":1,\"discountRate\":100,\"orderId\":4,\"orderItemId\":7,\"price\":120,\"productId\":1,\"quantity\":5,\"shippedQty\":0,\"taxAmount\":78.00,\"taxRate\":13,\"unitName\":\"件\"}],\"orderNo\":\"sal20261122001\",\"orderType\":\"sale\",\"ownerUserId\":1,\"params\":{},\"status\":\"0\",\"taxTotalAmount\":78.00,\"totalAmount\":678.00,\"totalQty\":5,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:51',21),(458,'销售单据审批',2,'com.canglian.web.controller.business.SalOrderController.approve()','PUT',1,'admin','研发部门','/business/saleOrder/approve/4','127.0.0.1','内网IP','4 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:51',32),(459,'销货订单下推出库单',2,'com.canglian.web.controller.business.SalOrderController.pushOutbound()','PUT',1,'admin','研发部门','/business/saleOrder/pushOutbound/4','127.0.0.1','内网IP','4 [{\"orderItemId\":8,\"pushQty\":2}] ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:51',25),(460,'销售单据强制关闭',2,'com.canglian.web.controller.business.SalOrderController.close()','PUT',1,'admin','研发部门','/business/saleOrder/close/4','127.0.0.1','内网IP','4 {\"closeReason\":\"E2E关闭释锁\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:51',20),(461,'序列号批量登记',1,'com.canglian.web.controller.business.WmsSerialNoController.batchRegister()','POST',1,'admin','研发部门','/business/serialNo/batchRegister','127.0.0.1','内网IP','{\"productId\":1,\"warehouseId\":1,\"locationId\":0,\"batchNo\":\"E2E-SN-B\",\"billNo\":\"RK-E2E-SN\",\"snCodes\":[\"E2E-SN-0001\",\"E2E-SN-0002\"]} ','{\"msg\":\"操作成功\",\"code\":200,\"data\":[{\"batchNo\":\"E2E-SN-B\",\"createBy\":\"admin\",\"locationId\":0,\"params\":{},\"productId\":1,\"serialId\":1,\"snCode\":\"E2E-SN-0001\",\"status\":\"in_stock\",\"warehouseId\":1},{\"batchNo\":\"E2E-SN-B\",\"createBy\":\"admin\",\"locationId\":0,\"params\":{},\"productId\":1,\"serialId\":2,\"snCode\":\"E2E-SN-0002\",\"status\":\"in_stock\",\"warehouseId\":1}]}',0,NULL,'2026-11-22 18:59:51',22),(462,'序列号批量登记',1,'com.canglian.web.controller.business.WmsSerialNoController.batchRegister()','POST',1,'admin','研发部门','/business/serialNo/batchRegister','127.0.0.1','内网IP','{\"productId\":1,\"warehouseId\":1,\"snCodes\":[\"E2E-SN-0001\"]} ',NULL,1,'序列号已存在，禁止重复登记：E2E-SN-0001','2026-11-22 18:59:51',6),(463,'序列号出库核销',2,'com.canglian.web.controller.business.WmsSerialNoController.consume()','POST',1,'admin','研发部门','/business/serialNo/consume','127.0.0.1','内网IP','{\"customerId\":1,\"billNo\":\"CK-E2E-SN\",\"snCodes\":[\"E2E-SN-0001\"]} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:51',11),(464,'序列号退货回库',2,'com.canglian.web.controller.business.WmsSerialNoController.returnBack()','POST',1,'admin','研发部门','/business/serialNo/returnBack','127.0.0.1','内网IP','{\"warehouseId\":1,\"billNo\":\"TH-E2E-SN\",\"snCodes\":[\"E2E-SN-0001\"]} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:51',10),(465,'资金账户',1,'com.canglian.web.controller.business.FinFundAccountController.add()','POST',1,'admin','研发部门','/business/fundAccount','127.0.0.1','内网IP','{\"accountId\":6,\"accountName\":\"E2E测试户1\",\"accountNo\":\"E2E-ACC-1\",\"accountType\":\"bank\",\"balance\":5000,\"createBy\":\"admin\",\"params\":{},\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:51',8),(466,'资金账户',1,'com.canglian.web.controller.business.FinFundAccountController.add()','POST',1,'admin','研发部门','/business/fundAccount','127.0.0.1','内网IP','{\"accountId\":7,\"accountName\":\"E2E测试户2\",\"accountNo\":\"E2E-ACC-2\",\"accountType\":\"cash\",\"balance\":1000,\"createBy\":\"admin\",\"params\":{},\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:51',5),(467,'收款单',1,'com.canglian.web.controller.business.FinReceiptController.add()','POST',1,'admin','研发部门','/business/receipt','127.0.0.1','内网IP','{\"amount\":300,\"bizStatus\":\"draft\",\"createBy\":\"admin\",\"customerId\":1,\"deptId\":103,\"fundAccountId\":6,\"ownerUserId\":1,\"params\":{},\"payChannel\":\"bank\",\"receiptDate\":\"2026-11-22\",\"receiptId\":10,\"receiptNo\":\"rec20261122001\",\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:51',22),(468,'收款单确认',2,'com.canglian.web.controller.business.FinReceiptController.confirm()','POST',1,'admin','研发部门','/business/receipt/confirm/10','127.0.0.1','内网IP','10 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:51',35),(469,'资金账户',2,'com.canglian.web.controller.business.FinFundAccountController.edit()','PUT',1,'admin','研发部门','/business/fundAccount','127.0.0.1','内网IP','{\"accountId\":6,\"accountName\":\"E2E测试户1\",\"accountNo\":\"E2E-ACC-1\",\"balance\":99999,\"params\":{},\"updateBy\":\"admin\"} ',NULL,1,'资金账户[E2E测试户1]已产生资金流水，禁止手工直接修改余额，请通过收付款或内部互转入账','2026-11-22 18:59:51',6),(470,'收款单反确认',2,'com.canglian.web.controller.business.FinReceiptController.unconfirm()','POST',1,'admin','研发部门','/business/receipt/unconfirm/10','127.0.0.1','内网IP','10 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:51',29),(471,'资金内部互转',1,'com.canglian.web.controller.business.FinFundAccountController.createTransfer()','POST',1,'admin','研发部门','/business/fundAccount/transfer','127.0.0.1','内网IP','{\"amount\":500,\"createBy\":\"admin\",\"feeAmount\":10,\"inAccountId\":7,\"outAccountId\":6,\"params\":{},\"remark\":\"E2E互转\",\"status\":\"0\",\"transferDate\":\"2026-11-22\",\"transferId\":1,\"transferNo\":\"ftr20261122001\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:51',16),(472,'资金内部互转确认',2,'com.canglian.web.controller.business.FinFundAccountController.confirmTransfer()','PUT',1,'admin','研发部门','/business/fundAccount/transfer/confirm/1','127.0.0.1','内网IP','1 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:51',33),(473,'应收单',1,'com.canglian.web.controller.business.FinReceivableController.add()','POST',1,'admin','研发部门','/business/receivable','127.0.0.1','内网IP','{\"amount\":600,\"billDirection\":\"1\",\"billType\":\"manual\",\"bizStatus\":\"draft\",\"createBy\":\"admin\",\"customerId\":1,\"dueDate\":\"2026-11-22 00:00:00\",\"params\":{},\"receivableId\":111,\"receivableNo\":\"ar20261122001\",\"receivedAmount\":0,\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:51',12),(474,'应收审核',2,'com.canglian.web.controller.business.FinReceivableController.audit()','PUT',1,'admin','研发部门','/business/receivable/audit/111','127.0.0.1','内网IP','111 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:51',8),(475,'应收反确认',2,'com.canglian.web.controller.business.FinReceivableController.unconfirm()','POST',1,'admin','研发部门','/business/receivable/unconfirm/111','127.0.0.1','内网IP','111 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:51',11),(476,'应收审核',2,'com.canglian.web.controller.business.FinReceivableController.audit()','PUT',1,'admin','研发部门','/business/receivable/audit/111','127.0.0.1','内网IP','111 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:51',8),(477,'坏账',1,'com.canglian.web.controller.business.FinBadDebtController.add()','POST',1,'admin','研发部门','/business/badDebt','127.0.0.1','内网IP','{\"amount\":200,\"badDebtDate\":\"2026-11-22\",\"badDebtId\":3,\"badDebtNo\":\"bd202611223443\",\"createBy\":\"admin\",\"customerId\":1,\"deptId\":103,\"ownerUserId\":1,\"params\":{},\"reason\":\"E2E坏账\",\"receivableId\":111,\"recoveredAmount\":0,\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:51',10),(478,'坏账确认',2,'com.canglian.web.controller.business.FinBadDebtController.confirm()','PUT',1,'admin','研发部门','/business/badDebt/confirm/3','127.0.0.1','内网IP','3 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:52',20),(479,'坏账收回',2,'com.canglian.web.controller.business.FinBadDebtController.recover()','POST',1,'admin','研发部门','/business/badDebt/recover/3','127.0.0.1','内网IP','3 {\"amount\":200,\"accountId\":7,\"remark\":\"E2E坏账收回\"} ',NULL,1,'收回金额必须大于0','2026-11-22 18:59:52',6),(480,'发票登记',1,'com.canglian.web.controller.business.FinInvoiceController.add()','POST',1,'admin','研发部门','/business/invoice','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22\",\"createBy\":\"admin\",\"customerId\":1,\"deptId\":103,\"invoiceAmount\":1130,\"invoiceDate\":\"2026-11-22\",\"invoiceId\":5,\"invoiceKind\":\"blue\",\"invoiceNo\":\"sin20261122001\",\"invoiceType\":\"sale\",\"ownerUserId\":1,\"params\":{},\"status\":\"0\",\"taxAmount\":1049.29,\"taxRate\":13,\"untaxedAmount\":80.71} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:52',11),(481,'发票登记确认',2,'com.canglian.web.controller.business.FinInvoiceController.confirm()','PUT',1,'admin','研发部门','/business/invoice/confirm/5','127.0.0.1','内网IP','5 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:52',28),(482,'红字发票冲销',1,'com.canglian.web.controller.business.FinInvoiceController.issueRed()','POST',1,'admin','研发部门','/business/invoice/red/5','127.0.0.1','内网IP','5 {\"amount\":500,\"remark\":\"E2E部分红冲\"} ','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"bizStatus\":\"confirmed\",\"businessDate\":\"2026-11-22\",\"confirmBy\":\"admin\",\"confirmTime\":\"2026-11-22 18:59:53\",\"createBy\":\"admin\",\"customerId\":1,\"deptId\":103,\"invoiceAmount\":-1130.00,\"invoiceDate\":\"2026-11-22\",\"invoiceId\":6,\"invoiceKind\":\"red\",\"invoiceNo\":\"rsin20261122001\",\"invoiceType\":\"sale\",\"originInvoiceId\":5,\"ownerUserId\":1,\"params\":{},\"remark\":\"E2E部分红冲\",\"status\":\"1\",\"taxAmount\":-1049.29,\"taxRate\":13.0000,\"untaxedAmount\":-80.71}}',0,NULL,'2026-11-22 18:59:52',22),(483,'会计科目',1,'com.canglian.web.controller.business.FinGeneralLedgerController.addSubject()','POST',1,'admin','研发部门','/business/generalLedger/subject','127.0.0.1','内网IP','{\"subjectCode\":\"9999\",\"subjectName\":\"E2E临时科目\",\"subjectType\":\"asset\",\"balanceDirection\":\"debit\",\"status\":\"0\",\"createBy\":\"admin\",\"subjectId\":7} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:52',10),(484,'会计科目',3,'com.canglian.web.controller.business.FinGeneralLedgerController.removeSubject()','DELETE',1,'admin','研发部门','/business/generalLedger/subject/7','127.0.0.1','内网IP','7 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:52',11),(485,'辅助核算同步',2,'com.canglian.web.controller.business.FinGeneralLedgerController.syncAuxiliary()','POST',1,'admin','研发部门','/business/generalLedger/auxiliary/sync','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"syncedCustomers\":3,\"syncedSuppliers\":3,\"syncedWarehouses\":4,\"totalSynced\":10}}',0,NULL,'2026-11-22 18:59:52',57),(486,'初始化年度会计期间',1,'com.canglian.web.controller.business.FinGeneralLedgerController.initPeriodYear()','POST',1,'admin','研发部门','/business/generalLedger/period/initYear/2031','127.0.0.1','内网IP','2031 ','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"year\":2031,\"createdCount\":12,\"skippedCount\":0}}',0,NULL,'2026-11-22 18:59:52',59),(487,'凭证生成',2,'com.canglian.web.controller.business.FinVoucherEventController.generate()','PUT',1,'admin','研发部门','/business/voucherEvent/generate/46','127.0.0.1','内网IP','46 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:52',25),(488,'凭证冲销',2,'com.canglian.web.controller.business.FinVoucherEventController.reverse()','PUT',1,'admin','研发部门','/business/voucherEvent/reverse/46','127.0.0.1','内网IP','46 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 18:59:52',19),(489,'凭证复式分录导出',5,'com.canglian.web.controller.business.FinVoucherEventController.export()','POST',1,'admin','研发部门','/business/voucherEvent/export','127.0.0.1','内网IP','{\"format\":\"kingdee\"}',NULL,0,NULL,'2026-11-22 18:59:52',371),(490,'商品多单位',1,'com.canglian.web.controller.business.MdProductUnitController.add()','POST',1,'admin','研发部门','/business/productUnit','127.0.0.1','内网IP','{\"barCode\":\"E2E-BOX-888\",\"convertRatio\":12,\"createBy\":\"admin\",\"isBaseUnit\":\"0\",\"params\":{},\"productId\":1,\"purchasePrice\":120,\"salePrice\":240,\"status\":\"0\",\"unitId\":3,\"unitName\":\"箱E2E\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:30',18),(491,'购货订单',1,'com.canglian.web.controller.business.PurOrderController.add()','POST',1,'admin','研发部门','/business/purOrder','127.0.0.1','内网IP','{\"approveBy\":\"\",\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"deptId\":103,\"discountAmount\":10,\"orderItemList\":[{\"amount\":1017.00,\"baseQuantity\":10.0000,\"convertRatio\":1,\"discountRate\":90,\"price\":100,\"productId\":1,\"purchaseOrderId\":6,\"purchaseOrderItemId\":13,\"quantity\":10,\"receivedQty\":0,\"taxAmount\":117.00,\"taxRate\":13,\"unitName\":\"件\"}],\"ownerUserId\":1,\"params\":{},\"purchaseOrderId\":6,\"purchaseOrderNo\":\"pur20261122001\",\"status\":\"0\",\"supplierId\":1,\"taxTotalAmount\":117.00,\"totalAmount\":1007.00,\"totalQty\":10,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:30',354),(492,'购货订单审批',2,'com.canglian.web.controller.business.PurOrderController.approve()','PUT',1,'admin','研发部门','/business/purOrder/approve/6','127.0.0.1','内网IP','6 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:30',27),(493,'购货订单反审批',2,'com.canglian.web.controller.business.PurOrderController.unapprove()','PUT',1,'admin','研发部门','/business/purOrder/unapprove/6','127.0.0.1','内网IP','6 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:30',19),(494,'购货订单审批',2,'com.canglian.web.controller.business.PurOrderController.approve()','PUT',1,'admin','研发部门','/business/purOrder/approve/6','127.0.0.1','内网IP','6 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:31',21),(495,'购货订单下推入库单',2,'com.canglian.web.controller.business.PurOrderController.pushInbound()','PUT',1,'admin','研发部门','/business/purOrder/pushInbound/6','127.0.0.1','内网IP','6 [{\"purchaseOrderItemId\":15,\"pushQty\":4}] ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:31',42),(496,'购货订单下推入库单',2,'com.canglian.web.controller.business.PurOrderController.pushInbound()','PUT',1,'admin','研发部门','/business/purOrder/pushInbound/6','127.0.0.1','内网IP','6 [{\"purchaseOrderItemId\":15,\"pushQty\":7}] ',NULL,1,'商品 #1 本次下推数量(7)超过剩余可下推数量(6.0000)','2026-11-22 19:02:31',12),(497,'购货订单强制关闭',2,'com.canglian.web.controller.business.PurOrderController.close()','PUT',1,'admin','研发部门','/business/purOrder/close/6','127.0.0.1','内网IP','6 {\"closeReason\":\"E2E测试关闭余单\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:31',10),(498,'入库单',1,'com.canglian.web.controller.business.WmsInboundController.add()','POST',1,'admin','研发部门','/business/inbound','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"deptId\":103,\"inboundId\":115,\"inboundNo\":\"inb20261122002\",\"inboundType\":\"purchase\",\"isReversal\":\"0\",\"itemList\":[{\"amount\":1000,\"baseQuantity\":20.0000,\"batchNo\":\"E2E-FEFO-1\",\"convertRatio\":1,\"expiryDate\":\"2026-12-07\",\"inboundId\":115,\"inboundItemId\":115,\"locationId\":0,\"price\":50,\"productId\":1,\"productionDate\":\"2026-11-12\",\"quantity\":20,\"unitName\":\"把\"}],\"ownerUserId\":1,\"params\":{},\"reversedFlag\":\"0\",\"status\":\"0\",\"supplierId\":1,\"totalAmount\":1000,\"totalQty\":20,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:31',27),(499,'入库审核',2,'com.canglian.web.controller.business.WmsInboundController.audit()','PUT',1,'admin','研发部门','/business/inbound/audit/115','127.0.0.1','内网IP','115 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:31',71),(500,'入库反审核',2,'com.canglian.web.controller.business.WmsInboundController.unaudit()','PUT',1,'admin','研发部门','/business/inbound/unaudit/115','127.0.0.1','内网IP','115 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:31',45),(501,'入库审核',2,'com.canglian.web.controller.business.WmsInboundController.audit()','PUT',1,'admin','研发部门','/business/inbound/audit/115','127.0.0.1','内网IP','115 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:31',51),(502,'库存冻结',2,'com.canglian.web.controller.business.WmsStockController.freeze()','POST',1,'admin','研发部门','/business/stock/freeze','127.0.0.1','内网IP','{\"stockId\":118,\"quantity\":5,\"reason\":\"E2E质检冻结\"} ','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"availableQuantity\":15.00,\"batchNo\":\"E2E-FEFO-1\",\"createBy\":\"admin\",\"createTime\":\"2026-11-22 19:02:31\",\"expiryDate\":\"2026-12-07 00:00:00\",\"expiryStatus\":\"expiring\",\"frozenQuantity\":5.00,\"locationId\":0,\"lockedQuantity\":0.00,\"params\":{},\"productId\":1,\"productionDate\":\"2026-11-12 00:00:00\",\"quantity\":20.00,\"remainDays\":14,\"stockId\":118,\"updateBy\":\"admin\",\"updateTime\":\"2026-11-22 19:02:31\",\"version\":2,\"warehouseId\":1,\"warningMaxQty\":0.00,\"warningMinQty\":0.00}}',0,NULL,'2026-11-22 19:02:31',14),(503,'库存解冻',2,'com.canglian.web.controller.business.WmsStockController.unfreeze()','POST',1,'admin','研发部门','/business/stock/unfreeze','127.0.0.1','内网IP','{\"stockId\":118,\"quantity\":5,\"reason\":\"E2E质检放行\"} ','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"availableQuantity\":20.00,\"batchNo\":\"E2E-FEFO-1\",\"createBy\":\"admin\",\"createTime\":\"2026-11-22 19:02:31\",\"expiryDate\":\"2026-12-07 00:00:00\",\"expiryStatus\":\"expiring\",\"frozenQuantity\":0.00,\"locationId\":0,\"lockedQuantity\":0.00,\"params\":{},\"productId\":1,\"productionDate\":\"2026-11-12 00:00:00\",\"quantity\":20.00,\"remainDays\":14,\"stockId\":118,\"updateBy\":\"admin\",\"updateTime\":\"2026-11-22 19:02:31\",\"version\":3,\"warehouseId\":1,\"warningMaxQty\":0.00,\"warningMinQty\":0.00}}',0,NULL,'2026-11-22 19:02:31',11),(504,'入库红冲',1,'com.canglian.web.controller.business.WmsInboundController.reverse()','POST',1,'admin','研发部门','/business/inbound/reverse/115','127.0.0.1','内网IP','115 ','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"auditBy\":\"admin\",\"auditTime\":\"2026-11-22 19:02:33\",\"bizStatus\":\"completed\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"createTime\":\"2026-11-22 19:02:31\",\"deptId\":103,\"inboundId\":116,\"inboundNo\":\"inb20261122003\",\"inboundType\":\"purchase\",\"isReversal\":\"1\",\"itemList\":[{\"amount\":-1000.00,\"baseQuantity\":-20.0000,\"batchNo\":\"E2E-FEFO-1\",\"convertRatio\":1.0000,\"expiryDate\":\"2026-12-07\",\"inboundId\":116,\"inboundItemId\":116,\"locationId\":0,\"price\":50.00,\"productId\":1,\"productionDate\":\"2026-11-12\",\"quantity\":-20.00,\"unitName\":\"把\"}],\"originBillId\":115,\"ownerUserId\":1,\"params\":{},\"remark\":\"红冲原入库单：inb20261122002\",\"reversedFlag\":\"0\",\"sourceBillId\":115,\"sourceBillNo\":\"inb20261122002\",\"sourceBillType\":\"inbound_reverse\",\"status\":\"1\",\"supplierId\":1,\"totalAmount\":-1000.00,\"totalQty\":-20.00,\"warehouseId\":1}}',0,NULL,'2026-11-22 19:02:31',56),(505,'出库单',1,'com.canglian.web.controller.business.WmsOutboundController.add()','POST',1,'admin','研发部门','/business/outbound','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"customerId\":1,\"deliveryStatus\":\"0\",\"deptId\":103,\"isReversal\":\"0\",\"itemList\":[{\"amount\":160,\"baseQuantity\":2.0000,\"batchNo\":\"E2E-EXPIRED\",\"convertRatio\":1,\"locationId\":0,\"outboundId\":109,\"outboundItemId\":109,\"price\":80,\"productId\":1,\"quantity\":2,\"unitName\":\"把\"}],\"outboundId\":109,\"outboundNo\":\"out20261122001\",\"outboundType\":\"sale\",\"ownerUserId\":1,\"params\":{},\"reversedFlag\":\"0\",\"status\":\"0\",\"totalAmount\":160,\"totalQty\":2,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:31',27),(506,'出库审核',2,'com.canglian.web.controller.business.WmsOutboundController.audit()','PUT',1,'admin','研发部门','/business/outbound/audit/109','127.0.0.1','内网IP','109 ',NULL,1,'商品 #1 批次[E2E-EXPIRED] 已过有效期(2026-11-20)，禁止出库','2026-11-22 19:02:31',11),(507,'扫码快速入库',1,'com.canglian.web.controller.business.BusinessScanController.quickInbound()','POST',1,'admin','研发部门','/business/scan/quickInbound','127.0.0.1','内网IP','{\"warehouseId\":1,\"supplierId\":1,\"remark\":\"E2E扫码入库\",\"items\":[{\"barCode\":\"E2E-BOX-888\",\"quantity\":1,\"batchNo\":\"E2E-SCAN-B1\"}]} ','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"inbound\":{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"createTime\":\"2026-11-22 19:02:31\",\"deptId\":103,\"inboundId\":117,\"inboundNo\":\"inb20261122004\",\"inboundType\":\"purchase\",\"isReversal\":\"0\",\"itemList\":[{\"amount\":120.00,\"baseQuantity\":12.0000,\"batchNo\":\"E2E-SCAN-B1\",\"convertRatio\":12.0000,\"inboundId\":117,\"inboundItemId\":117,\"locationId\":0,\"price\":120.00,\"productId\":1,\"quantity\":1.00,\"unitName\":\"箱E2E\"}],\"ownerUserId\":1,\"params\":{},\"remark\":\"E2E扫码入库\",\"reversedFlag\":\"0\",\"status\":\"0\",\"supplierId\":1,\"totalAmount\":120.00,\"totalQty\":1.00,\"updateTime\":\"2026-11-22 19:02:31\",\"warehouseId\":1}}}',0,NULL,'2026-11-22 19:02:31',27),(508,'销售单据',1,'com.canglian.web.controller.business.SalOrderController.add()','POST',1,'admin','研发部门','/business/saleOrder','127.0.0.1','内网IP','{\"approveBy\":\"\",\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22 00:00:00\",\"createBy\":\"admin\",\"customerId\":1,\"deptId\":103,\"discountAmount\":0,\"orderId\":5,\"orderItemList\":[{\"amount\":678.00,\"baseQuantity\":5.0000,\"convertRatio\":1,\"discountRate\":100,\"orderId\":5,\"orderItemId\":9,\"price\":120,\"productId\":1,\"quantity\":5,\"shippedQty\":0,\"taxAmount\":78.00,\"taxRate\":13,\"unitName\":\"件\"}],\"orderNo\":\"sal20261122001\",\"orderType\":\"sale\",\"ownerUserId\":1,\"params\":{},\"status\":\"0\",\"taxTotalAmount\":78.00,\"totalAmount\":678.00,\"totalQty\":5,\"warehouseId\":1} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:31',23),(509,'销售单据审批',2,'com.canglian.web.controller.business.SalOrderController.approve()','PUT',1,'admin','研发部门','/business/saleOrder/approve/5','127.0.0.1','内网IP','5 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:31',30),(510,'销货订单下推出库单',2,'com.canglian.web.controller.business.SalOrderController.pushOutbound()','PUT',1,'admin','研发部门','/business/saleOrder/pushOutbound/5','127.0.0.1','内网IP','5 [{\"orderItemId\":10,\"pushQty\":2}] ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:31',27),(511,'销售单据强制关闭',2,'com.canglian.web.controller.business.SalOrderController.close()','PUT',1,'admin','研发部门','/business/saleOrder/close/5','127.0.0.1','内网IP','5 {\"closeReason\":\"E2E关闭释锁\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:31',20),(512,'序列号批量登记',1,'com.canglian.web.controller.business.WmsSerialNoController.batchRegister()','POST',1,'admin','研发部门','/business/serialNo/batchRegister','127.0.0.1','内网IP','{\"productId\":1,\"warehouseId\":1,\"locationId\":0,\"batchNo\":\"E2E-SN-B\",\"billNo\":\"RK-E2E-SN\",\"snCodes\":[\"E2E-SN-0001\",\"E2E-SN-0002\"]} ','{\"msg\":\"操作成功\",\"code\":200,\"data\":[{\"batchNo\":\"E2E-SN-B\",\"createBy\":\"admin\",\"locationId\":0,\"params\":{},\"productId\":1,\"serialId\":3,\"snCode\":\"E2E-SN-0001\",\"status\":\"in_stock\",\"warehouseId\":1},{\"batchNo\":\"E2E-SN-B\",\"createBy\":\"admin\",\"locationId\":0,\"params\":{},\"productId\":1,\"serialId\":4,\"snCode\":\"E2E-SN-0002\",\"status\":\"in_stock\",\"warehouseId\":1}]}',0,NULL,'2026-11-22 19:02:31',23),(513,'序列号批量登记',1,'com.canglian.web.controller.business.WmsSerialNoController.batchRegister()','POST',1,'admin','研发部门','/business/serialNo/batchRegister','127.0.0.1','内网IP','{\"productId\":1,\"warehouseId\":1,\"snCodes\":[\"E2E-SN-0001\"]} ',NULL,1,'序列号已存在，禁止重复登记：E2E-SN-0001','2026-11-22 19:02:31',5),(514,'序列号出库核销',2,'com.canglian.web.controller.business.WmsSerialNoController.consume()','POST',1,'admin','研发部门','/business/serialNo/consume','127.0.0.1','内网IP','{\"customerId\":1,\"billNo\":\"CK-E2E-SN\",\"snCodes\":[\"E2E-SN-0001\"]} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:31',11),(515,'序列号退货回库',2,'com.canglian.web.controller.business.WmsSerialNoController.returnBack()','POST',1,'admin','研发部门','/business/serialNo/returnBack','127.0.0.1','内网IP','{\"warehouseId\":1,\"billNo\":\"TH-E2E-SN\",\"snCodes\":[\"E2E-SN-0001\"]} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:32',12),(516,'资金账户',1,'com.canglian.web.controller.business.FinFundAccountController.add()','POST',1,'admin','研发部门','/business/fundAccount','127.0.0.1','内网IP','{\"accountId\":8,\"accountName\":\"E2E测试户1\",\"accountNo\":\"E2E-ACC-1\",\"accountType\":\"bank\",\"balance\":5000,\"createBy\":\"admin\",\"params\":{},\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:32',9),(517,'资金账户',1,'com.canglian.web.controller.business.FinFundAccountController.add()','POST',1,'admin','研发部门','/business/fundAccount','127.0.0.1','内网IP','{\"accountId\":9,\"accountName\":\"E2E测试户2\",\"accountNo\":\"E2E-ACC-2\",\"accountType\":\"cash\",\"balance\":1000,\"createBy\":\"admin\",\"params\":{},\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:32',5),(518,'收款单',1,'com.canglian.web.controller.business.FinReceiptController.add()','POST',1,'admin','研发部门','/business/receipt','127.0.0.1','内网IP','{\"amount\":300,\"bizStatus\":\"draft\",\"createBy\":\"admin\",\"customerId\":1,\"deptId\":103,\"fundAccountId\":8,\"ownerUserId\":1,\"params\":{},\"payChannel\":\"bank\",\"receiptDate\":\"2026-11-22\",\"receiptId\":11,\"receiptNo\":\"rec20261122001\",\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:32',21),(519,'收款单确认',2,'com.canglian.web.controller.business.FinReceiptController.confirm()','POST',1,'admin','研发部门','/business/receipt/confirm/11','127.0.0.1','内网IP','11 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:32',38),(520,'资金账户',2,'com.canglian.web.controller.business.FinFundAccountController.edit()','PUT',1,'admin','研发部门','/business/fundAccount','127.0.0.1','内网IP','{\"accountId\":8,\"accountName\":\"E2E测试户1\",\"accountNo\":\"E2E-ACC-1\",\"balance\":99999,\"params\":{},\"updateBy\":\"admin\"} ',NULL,1,'资金账户[E2E测试户1]已产生资金流水，禁止手工直接修改余额，请通过收付款或内部互转入账','2026-11-22 19:02:32',8),(521,'收款单反确认',2,'com.canglian.web.controller.business.FinReceiptController.unconfirm()','POST',1,'admin','研发部门','/business/receipt/unconfirm/11','127.0.0.1','内网IP','11 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:32',41),(522,'资金内部互转',1,'com.canglian.web.controller.business.FinFundAccountController.createTransfer()','POST',1,'admin','研发部门','/business/fundAccount/transfer','127.0.0.1','内网IP','{\"amount\":500,\"createBy\":\"admin\",\"feeAmount\":10,\"inAccountId\":9,\"outAccountId\":8,\"params\":{},\"remark\":\"E2E互转\",\"status\":\"0\",\"transferDate\":\"2026-11-22\",\"transferId\":2,\"transferNo\":\"ftr20261122001\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:32',11),(523,'资金内部互转确认',2,'com.canglian.web.controller.business.FinFundAccountController.confirmTransfer()','PUT',1,'admin','研发部门','/business/fundAccount/transfer/confirm/2','127.0.0.1','内网IP','2 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:32',26),(524,'应收单',1,'com.canglian.web.controller.business.FinReceivableController.add()','POST',1,'admin','研发部门','/business/receivable','127.0.0.1','内网IP','{\"amount\":600,\"billDirection\":\"1\",\"billType\":\"manual\",\"bizStatus\":\"draft\",\"createBy\":\"admin\",\"customerId\":1,\"dueDate\":\"2026-11-22 00:00:00\",\"params\":{},\"receivableId\":113,\"receivableNo\":\"ar20261122001\",\"receivedAmount\":0,\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:32',14),(525,'应收审核',2,'com.canglian.web.controller.business.FinReceivableController.audit()','PUT',1,'admin','研发部门','/business/receivable/audit/113','127.0.0.1','内网IP','113 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:32',9),(526,'应收反确认',2,'com.canglian.web.controller.business.FinReceivableController.unconfirm()','POST',1,'admin','研发部门','/business/receivable/unconfirm/113','127.0.0.1','内网IP','113 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:32',10),(527,'应收审核',2,'com.canglian.web.controller.business.FinReceivableController.audit()','PUT',1,'admin','研发部门','/business/receivable/audit/113','127.0.0.1','内网IP','113 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:32',8),(528,'坏账',1,'com.canglian.web.controller.business.FinBadDebtController.add()','POST',1,'admin','研发部门','/business/badDebt','127.0.0.1','内网IP','{\"amount\":200,\"badDebtDate\":\"2026-11-22\",\"badDebtId\":4,\"badDebtNo\":\"bd202611223985\",\"createBy\":\"admin\",\"customerId\":1,\"deptId\":103,\"ownerUserId\":1,\"params\":{},\"reason\":\"E2E坏账\",\"receivableId\":113,\"recoveredAmount\":0,\"status\":\"0\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:32',14),(529,'坏账确认',2,'com.canglian.web.controller.business.FinBadDebtController.confirm()','PUT',1,'admin','研发部门','/business/badDebt/confirm/4','127.0.0.1','内网IP','4 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:32',19),(530,'坏账收回',2,'com.canglian.web.controller.business.FinBadDebtController.recover()','POST',1,'admin','研发部门','/business/badDebt/recover/4','127.0.0.1','内网IP','4 {\"recoverAmount\":200,\"fundAccountId\":9,\"remark\":\"E2E坏账收回\"} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:32',20),(531,'发票登记',1,'com.canglian.web.controller.business.FinInvoiceController.add()','POST',1,'admin','研发部门','/business/invoice','127.0.0.1','内网IP','{\"bizStatus\":\"draft\",\"businessDate\":\"2026-11-22\",\"createBy\":\"admin\",\"customerId\":1,\"deptId\":103,\"invoiceAmount\":1130,\"invoiceDate\":\"2026-11-22\",\"invoiceId\":7,\"invoiceKind\":\"blue\",\"invoiceNo\":\"sin20261122001\",\"invoiceType\":\"sale\",\"ownerUserId\":1,\"params\":{},\"status\":\"0\",\"taxAmount\":1049.29,\"taxRate\":13,\"untaxedAmount\":80.71} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:32',11),(532,'发票登记确认',2,'com.canglian.web.controller.business.FinInvoiceController.confirm()','PUT',1,'admin','研发部门','/business/invoice/confirm/7','127.0.0.1','内网IP','7 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:32',28),(533,'红字发票冲销',1,'com.canglian.web.controller.business.FinInvoiceController.issueRed()','POST',1,'admin','研发部门','/business/invoice/red/7','127.0.0.1','内网IP','7 {\"redAmount\":500,\"remark\":\"E2E部分红冲\"} ','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"bizStatus\":\"confirmed\",\"businessDate\":\"2026-11-22\",\"confirmBy\":\"admin\",\"confirmTime\":\"2026-11-22 19:02:34\",\"createBy\":\"admin\",\"customerId\":1,\"deptId\":103,\"invoiceAmount\":-500,\"invoiceDate\":\"2026-11-22\",\"invoiceId\":8,\"invoiceKind\":\"red\",\"invoiceNo\":\"rsin20261122001\",\"invoiceType\":\"sale\",\"originInvoiceId\":7,\"ownerUserId\":1,\"params\":{},\"remark\":\"E2E部分红冲\",\"status\":\"1\",\"taxAmount\":-464.29,\"taxRate\":13.0000,\"untaxedAmount\":-35.71}}',0,NULL,'2026-11-22 19:02:32',19),(534,'会计科目',1,'com.canglian.web.controller.business.FinGeneralLedgerController.addSubject()','POST',1,'admin','研发部门','/business/generalLedger/subject','127.0.0.1','内网IP','{\"subjectCode\":\"9999\",\"subjectName\":\"E2E临时科目\",\"subjectType\":\"asset\",\"balanceDirection\":\"debit\",\"status\":\"0\",\"createBy\":\"admin\",\"subjectId\":8} ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:32',9),(535,'会计科目',3,'com.canglian.web.controller.business.FinGeneralLedgerController.removeSubject()','DELETE',1,'admin','研发部门','/business/generalLedger/subject/8','127.0.0.1','内网IP','8 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:32',12),(536,'辅助核算同步',2,'com.canglian.web.controller.business.FinGeneralLedgerController.syncAuxiliary()','POST',1,'admin','研发部门','/business/generalLedger/auxiliary/sync','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"syncedCustomers\":3,\"syncedSuppliers\":3,\"syncedWarehouses\":4,\"totalSynced\":10}}',0,NULL,'2026-11-22 19:02:32',64),(537,'初始化年度会计期间',1,'com.canglian.web.controller.business.FinGeneralLedgerController.initPeriodYear()','POST',1,'admin','研发部门','/business/generalLedger/period/initYear/2031','127.0.0.1','内网IP','2031 ','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"year\":2031,\"createdCount\":12,\"skippedCount\":0}}',0,NULL,'2026-11-22 19:02:32',54),(538,'凭证生成',2,'com.canglian.web.controller.business.FinVoucherEventController.generate()','PUT',1,'admin','研发部门','/business/voucherEvent/generate/55','127.0.0.1','内网IP','55 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:33',24),(539,'凭证冲销',2,'com.canglian.web.controller.business.FinVoucherEventController.reverse()','PUT',1,'admin','研发部门','/business/voucherEvent/reverse/55','127.0.0.1','内网IP','55 ','{\"msg\":\"操作成功\",\"code\":200}',0,NULL,'2026-11-22 19:02:33',19),(540,'凭证复式分录导出',5,'com.canglian.web.controller.business.FinVoucherEventController.export()','POST',1,'admin','研发部门','/business/voucherEvent/export','127.0.0.1','内网IP','{\"format\":\"kingdee\"}',NULL,0,NULL,'2026-11-22 19:02:33',347),(541,'补生成缺失凭证事件',1,'com.canglian.web.controller.business.FinVoucherEventController.regenerate()','POST',1,'admin','研发部门','/business/voucherEvent/regenerate','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"createdCount\":5}}',0,NULL,'2026-11-22 22:45:19',71),(542,'补生成缺失凭证事件',1,'com.canglian.web.controller.business.FinVoucherEventController.regenerate()','POST',1,'admin','研发部门','/business/voucherEvent/regenerate','127.0.0.1','内网IP','','{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"createdCount\":2}}',0,NULL,'2026-11-22 22:46:16',62);
/*!40000 ALTER TABLE `sys_oper_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_post`
--

DROP TABLE IF EXISTS `sys_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_post` (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='岗位信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_post`
--

LOCK TABLES `sys_post` WRITE;
/*!40000 ALTER TABLE `sys_post` DISABLE KEYS */;
INSERT INTO `sys_post` VALUES (1,'ceo','董事长',1,'0','admin','2026-11-22 10:36:44','',NULL,''),(2,'se','项目经理',2,'0','admin','2026-11-22 10:36:44','',NULL,''),(3,'hr','人力资源',3,'0','admin','2026-11-22 10:36:44','',NULL,''),(4,'user','普通员工',4,'0','admin','2026-11-22 10:36:44','',NULL,'');
/*!40000 ALTER TABLE `sys_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '部门树选择项是否关联显示',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `warehouse_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '1' COMMENT '仓库数据范围(1全部仓库 2指定仓库)',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (1,'超级管理员','admin',1,'1',1,1,'0','0','admin','2026-11-22 10:36:44','',NULL,'超级管理员','1'),(2,'普通角色','common',2,'2',1,1,'0','0','admin','2026-11-22 10:36:44','',NULL,'普通角色','1'),(3,'仓管','warehouse',3,'2',1,1,'0','0','admin','2026-11-22 10:36:44','',NULL,'仓储管理角色','1'),(4,'采购','purchase',4,'2',1,1,'0','0','admin','2026-11-22 10:36:44','',NULL,'采购管理角色','1'),(5,'销售','sales',5,'2',1,1,'0','0','admin','2026-11-22 10:36:44','',NULL,'销售管理角色','1'),(6,'财务','finance',6,'2',1,1,'0','0','admin','2026-11-22 10:36:44','',NULL,'财务管理角色','1'),(7,'经营管理者','boss_viewer',7,'1',1,1,'0','0','admin','2026-11-22 17:43:30','',NULL,'经营分析、全业务看板与审批角色','1');
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_dept`
--

DROP TABLE IF EXISTS `sys_role_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色和部门关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_dept`
--

LOCK TABLES `sys_role_dept` WRITE;
/*!40000 ALTER TABLE `sys_role_dept` DISABLE KEYS */;
INSERT INTO `sys_role_dept` VALUES (2,100),(2,101),(2,105),(3,100),(3,101),(3,102),(3,103),(3,104),(3,105),(3,106),(3,107),(3,108),(3,109),(4,100),(4,101),(4,102),(4,103),(4,104),(4,105),(4,106),(4,107),(4,108),(4,109),(5,100),(5,101),(5,102),(5,103),(5,104),(5,105),(5,106),(5,107),(5,108),(5,109),(6,100),(6,101),(6,102),(6,103),(6,104),(6,105),(6,106),(6,107),(6,108),(6,109);
/*!40000 ALTER TABLE `sys_role_dept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色和菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES (1,1),(1,2),(1,3),(1,100),(1,101),(1,102),(1,103),(1,104),(1,105),(1,106),(1,107),(1,108),(1,109),(1,110),(1,111),(1,112),(1,113),(1,114),(1,115),(1,116),(1,117),(1,500),(1,501),(1,1000),(1,1001),(1,1002),(1,1003),(1,1004),(1,1005),(1,1006),(1,1007),(1,1008),(1,1009),(1,1010),(1,1011),(1,1012),(1,1013),(1,1014),(1,1015),(1,1016),(1,1017),(1,1018),(1,1019),(1,1020),(1,1021),(1,1022),(1,1023),(1,1024),(1,1025),(1,1026),(1,1027),(1,1028),(1,1029),(1,1030),(1,1031),(1,1032),(1,1033),(1,1034),(1,1035),(1,1036),(1,1037),(1,1038),(1,1039),(1,1040),(1,1041),(1,1042),(1,1043),(1,1044),(1,1045),(1,1046),(1,1047),(1,1048),(1,1049),(1,1050),(1,1051),(1,1052),(1,1053),(1,1054),(1,1055),(1,1056),(1,1057),(1,1058),(1,1059),(1,1060),(1,2000),(1,2001),(1,2002),(1,2003),(1,2004),(1,2005),(1,2006),(1,2007),(1,2008),(1,2009),(1,2010),(1,2011),(1,2100),(1,2101),(1,2102),(1,2103),(1,2110),(1,2111),(1,2112),(1,2113),(1,2120),(1,2121),(1,2122),(1,2123),(1,2124),(1,2125),(1,2126),(1,2130),(1,2131),(1,2132),(1,2133),(1,2134),(1,2135),(1,2140),(1,2141),(1,2142),(1,2143),(1,2150),(1,2151),(1,2152),(1,2153),(1,2154),(1,2160),(1,2161),(1,2162),(1,2163),(1,2170),(1,2171),(1,2172),(1,2173),(1,2180),(1,2181),(1,2182),(1,2183),(1,2184),(1,2185),(1,2186),(1,2187),(1,2200),(1,2201),(1,2202),(1,2203),(1,2204),(1,2205),(1,2206),(1,2207),(1,2208),(1,2209),(1,2210),(1,2260),(1,2261),(1,2262),(1,2263),(1,2264),(1,2265),(1,2266),(1,2267),(1,2268),(1,2269),(1,2270),(1,2271),(1,2300),(1,2301),(1,2302),(1,2303),(1,2310),(1,2311),(1,2312),(1,2313),(1,2320),(1,2321),(1,2322),(1,2323),(1,2330),(1,2331),(1,2332),(1,2333),(1,2340),(1,2341),(1,2342),(1,2343),(1,2350),(1,2351),(1,2352),(1,2353),(1,2354),(1,2360),(1,2361),(1,2362),(1,2363),(1,2364),(1,2370),(1,2371),(1,2372),(1,2373),(1,2380),(1,2381),(1,2390),(1,2400),(1,2401),(1,2402),(1,2410),(1,2411),(1,2420),(1,2421),(1,2422),(1,2423),(1,2430),(1,2431),(1,2432),(1,2433),(1,2440),(1,2441),(1,2442),(1,2443),(1,2450),(1,2451),(1,2452),(1,2453),(1,2454),(1,2460),(1,2461),(1,2462),(1,2463),(1,2464),(1,2470),(1,2471),(1,2472),(1,2473),(1,2474),(1,2475),(1,2510),(1,2511),(1,2512),(1,2513),(1,2514),(1,2515),(1,2516),(1,2517),(1,2520),(1,2521),(1,2522),(1,2523),(1,2524),(1,2525),(1,2526),(1,2530),(1,2531),(1,2532),(1,2533),(1,2534),(1,2540),(1,2541),(1,2542),(1,2543),(1,2550),(1,2551),(1,2552),(1,2553),(1,2560),(1,2561),(1,2562),(1,2563),(1,2570),(1,2580),(1,2590),(1,2591),(1,2592),(1,2593),(1,2600),(1,2601),(1,2610),(1,2611),(1,2612),(1,2613),(1,2614),(1,2620),(1,2621),(1,2622),(1,2700),(1,2701),(1,2702),(1,2703),(1,2704),(1,2710),(1,2711),(1,2712),(1,2713),(1,2714),(1,2720),(1,2721),(1,2722),(1,2723),(1,2724),(1,2730),(1,2731),(1,2732),(1,2733),(1,2734),(1,2740),(1,2741),(1,2742),(1,2743),(1,2744),(1,2750),(1,2760),(1,2761),(1,2762),(1,2763),(1,2764),(1,2765),(1,2766),(1,2767),(1,2768),(1,2769),(1,2770),(1,2771),(1,2772),(1,2773),(1,2774),(1,2780),(1,2781),(1,2782),(1,2783),(1,2784),(1,2785),(1,2786),(1,2787),(1,2788),(1,2789),(1,2790),(1,2791),(1,2792),(1,2793),(1,2794),(1,2795),(1,2796),(1,2797),(1,2798),(1,2800),(1,2801),(1,2802),(1,2803),(1,2804),(1,2805),(1,2806),(1,2807),(1,2808),(1,2809),(1,2810),(1,2811),(1,2812),(1,2813),(1,2814),(1,2815),(1,2816),(1,2817),(1,2818),(1,2819),(1,2820),(1,2821),(1,2822),(1,2823),(1,2824),(1,2825),(1,2826),(1,2827),(1,2828),(2,2000),(2,2001),(2,2002),(2,2003),(2,2004),(2,2005),(2,2006),(2,2007),(2,2008),(2,2009),(2,2010),(2,2011),(2,2100),(2,2101),(2,2102),(2,2103),(2,2110),(2,2111),(2,2112),(2,2113),(2,2120),(2,2121),(2,2122),(2,2123),(2,2124),(2,2125),(2,2126),(2,2130),(2,2131),(2,2132),(2,2133),(2,2134),(2,2135),(2,2140),(2,2141),(2,2142),(2,2143),(2,2150),(2,2151),(2,2152),(2,2153),(2,2154),(2,2160),(2,2161),(2,2162),(2,2163),(2,2170),(2,2171),(2,2172),(2,2173),(2,2180),(2,2181),(2,2182),(2,2183),(2,2184),(2,2185),(2,2186),(2,2187),(2,2200),(2,2201),(2,2202),(2,2203),(2,2204),(2,2205),(2,2206),(2,2207),(2,2208),(2,2209),(2,2210),(2,2260),(2,2261),(2,2262),(2,2263),(2,2264),(2,2265),(2,2266),(2,2267),(2,2268),(2,2269),(2,2270),(2,2271),(2,2300),(2,2301),(2,2302),(2,2303),(2,2310),(2,2311),(2,2312),(2,2313),(2,2320),(2,2321),(2,2322),(2,2323),(2,2330),(2,2331),(2,2332),(2,2333),(2,2340),(2,2341),(2,2342),(2,2343),(2,2350),(2,2351),(2,2352),(2,2353),(2,2354),(2,2360),(2,2361),(2,2362),(2,2363),(2,2364),(2,2370),(2,2371),(2,2372),(2,2373),(2,2380),(2,2381),(2,2390),(2,2400),(2,2401),(2,2402),(2,2410),(2,2411),(2,2420),(2,2421),(2,2422),(2,2423),(2,2430),(2,2431),(2,2432),(2,2433),(2,2440),(2,2441),(2,2442),(2,2443),(2,2450),(2,2453),(2,2460),(2,2463),(2,2470),(2,2471),(2,2472),(2,2473),(2,2474),(2,2475),(2,2510),(2,2511),(2,2512),(2,2513),(2,2514),(2,2515),(2,2516),(2,2517),(2,2520),(2,2521),(2,2522),(2,2523),(2,2524),(2,2525),(2,2526),(2,2530),(2,2531),(2,2532),(2,2533),(2,2534),(2,2540),(2,2541),(2,2542),(2,2543),(2,2550),(2,2551),(2,2552),(2,2553),(2,2560),(2,2561),(2,2562),(2,2563),(2,2570),(2,2580),(2,2590),(2,2591),(2,2592),(2,2593),(2,2600),(2,2601),(2,2610),(2,2611),(2,2612),(2,2613),(2,2614),(2,2620),(2,2700),(2,2701),(2,2702),(2,2703),(2,2704),(2,2710),(2,2711),(2,2712),(2,2713),(2,2714),(2,2720),(2,2721),(2,2722),(2,2723),(2,2724),(2,2730),(2,2731),(2,2732),(2,2733),(2,2734),(2,2740),(2,2741),(2,2742),(2,2743),(2,2744),(2,2760),(2,2761),(2,2762),(2,2763),(2,2764),(2,2765),(2,2766),(2,2767),(2,2768),(2,2769),(2,2770),(2,2771),(2,2772),(2,2773),(2,2774),(2,2780),(2,2781),(2,2782),(2,2783),(2,2784),(2,2785),(2,2786),(2,2787),(2,2788),(2,2789),(2,2790),(2,2796),(2,2797),(2,2800),(2,2801),(2,2802),(2,2803),(2,2804),(2,2805),(2,2806),(2,2807),(2,2808),(2,2809),(2,2810),(2,2811),(2,2812),(2,2813),(2,2814),(2,2815),(2,2817),(2,2818),(2,2819),(2,2820),(3,2000),(3,2001),(3,2002),(3,2003),(3,2004),(3,2005),(3,2006),(3,2007),(3,2008),(3,2009),(3,2010),(3,2011),(3,2100),(3,2101),(3,2102),(3,2103),(3,2110),(3,2111),(3,2112),(3,2113),(3,2120),(3,2121),(3,2122),(3,2123),(3,2124),(3,2125),(3,2126),(3,2130),(3,2131),(3,2132),(3,2133),(3,2134),(3,2135),(3,2140),(3,2141),(3,2142),(3,2143),(3,2150),(3,2151),(3,2152),(3,2153),(3,2154),(3,2160),(3,2161),(3,2162),(3,2163),(3,2170),(3,2171),(3,2172),(3,2173),(3,2180),(3,2181),(3,2182),(3,2183),(3,2184),(3,2185),(3,2186),(3,2187),(3,2268),(3,2270),(3,2271),(3,2400),(3,2401),(3,2402),(3,2410),(3,2411),(3,2420),(3,2421),(3,2422),(3,2423),(3,2430),(3,2431),(3,2432),(3,2433),(3,2440),(3,2441),(3,2442),(3,2443),(3,2450),(3,2451),(3,2452),(3,2453),(3,2454),(3,2460),(3,2461),(3,2462),(3,2463),(3,2464),(3,2470),(3,2474),(3,2475),(3,2570),(3,2580),(3,2590),(3,2591),(3,2592),(3,2593),(3,2700),(3,2701),(3,2702),(3,2703),(3,2704),(3,2760),(3,2761),(3,2764),(3,2765),(3,2766),(3,2770),(3,2771),(3,2781),(3,2782),(3,2786),(3,2787),(3,2800),(3,2803),(3,2804),(3,2805),(3,2806),(3,2807),(3,2808),(3,2809),(3,2810),(3,2818),(3,2819),(3,2827),(3,2828),(4,2000),(4,2001),(4,2002),(4,2003),(4,2004),(4,2009),(4,2100),(4,2110),(4,2120),(4,2121),(4,2122),(4,2123),(4,2124),(4,2125),(4,2126),(4,2170),(4,2171),(4,2172),(4,2173),(4,2200),(4,2202),(4,2262),(4,2264),(4,2268),(4,2270),(4,2310),(4,2401),(4,2402),(4,2410),(4,2430),(4,2431),(4,2432),(4,2433),(4,2440),(4,2441),(4,2442),(4,2450),(4,2451),(4,2452),(4,2453),(4,2454),(4,2470),(4,2471),(4,2473),(4,2475),(4,2520),(4,2521),(4,2522),(4,2523),(4,2524),(4,2525),(4,2526),(4,2530),(4,2531),(4,2532),(4,2570),(4,2580),(4,2765),(4,2766),(4,2801),(4,2809),(5,2000),(5,2001),(5,2002),(5,2003),(5,2005),(5,2100),(5,2110),(5,2130),(5,2131),(5,2132),(5,2133),(5,2134),(5,2135),(5,2184),(5,2185),(5,2186),(5,2200),(5,2201),(5,2203),(5,2210),(5,2260),(5,2261),(5,2263),(5,2265),(5,2266),(5,2267),(5,2270),(5,2300),(5,2301),(5,2302),(5,2303),(5,2320),(5,2321),(5,2322),(5,2323),(5,2390),(5,2400),(5,2402),(5,2411),(5,2420),(5,2421),(5,2422),(5,2423),(5,2440),(5,2460),(5,2461),(5,2462),(5,2463),(5,2464),(5,2470),(5,2472),(5,2475),(5,2510),(5,2511),(5,2512),(5,2513),(5,2514),(5,2515),(5,2516),(5,2517),(5,2530),(5,2531),(5,2532),(5,2540),(5,2541),(5,2542),(5,2543),(5,2550),(5,2551),(5,2552),(5,2553),(5,2560),(5,2580),(5,2770),(5,2771),(5,2802),(5,2810),(5,2812),(5,2820),(6,2000),(6,2001),(6,2002),(6,2003),(6,2004),(6,2005),(6,2100),(6,2110),(6,2120),(6,2130),(6,2200),(6,2201),(6,2202),(6,2203),(6,2204),(6,2205),(6,2206),(6,2207),(6,2208),(6,2209),(6,2210),(6,2263),(6,2264),(6,2269),(6,2270),(6,2300),(6,2301),(6,2302),(6,2303),(6,2310),(6,2311),(6,2312),(6,2313),(6,2320),(6,2321),(6,2322),(6,2323),(6,2330),(6,2331),(6,2332),(6,2333),(6,2340),(6,2341),(6,2342),(6,2343),(6,2350),(6,2351),(6,2352),(6,2353),(6,2354),(6,2360),(6,2361),(6,2362),(6,2363),(6,2364),(6,2370),(6,2371),(6,2372),(6,2373),(6,2380),(6,2381),(6,2390),(6,2400),(6,2401),(6,2402),(6,2420),(6,2430),(6,2440),(6,2470),(6,2475),(6,2530),(6,2531),(6,2532),(6,2533),(6,2534),(6,2580),(6,2600),(6,2601),(6,2610),(6,2611),(6,2612),(6,2613),(6,2614),(6,2620),(6,2621),(6,2622),(6,2710),(6,2711),(6,2712),(6,2713),(6,2714),(6,2720),(6,2721),(6,2730),(6,2731),(6,2740),(6,2741),(6,2762),(6,2763),(6,2780),(6,2796),(6,2797),(6,2798),(6,2811),(6,2812),(6,2813),(6,2814),(6,2815),(6,2816),(6,2817),(6,2821),(6,2822),(6,2823),(6,2824),(6,2825),(6,2826),(7,2000),(7,2001),(7,2002),(7,2003),(7,2004),(7,2005),(7,2006),(7,2007),(7,2008),(7,2009),(7,2010),(7,2011),(7,2100),(7,2110),(7,2120),(7,2130),(7,2140),(7,2150),(7,2160),(7,2170),(7,2180),(7,2187),(7,2200),(7,2201),(7,2202),(7,2203),(7,2204),(7,2205),(7,2206),(7,2207),(7,2208),(7,2209),(7,2210),(7,2260),(7,2261),(7,2262),(7,2263),(7,2264),(7,2265),(7,2266),(7,2267),(7,2268),(7,2269),(7,2270),(7,2271),(7,2300),(7,2310),(7,2320),(7,2330),(7,2340),(7,2350),(7,2360),(7,2370),(7,2381),(7,2390),(7,2400),(7,2401),(7,2402),(7,2410),(7,2411),(7,2420),(7,2430),(7,2440),(7,2450),(7,2460),(7,2470),(7,2475),(7,2510),(7,2514),(7,2520),(7,2524),(7,2530),(7,2540),(7,2550),(7,2560),(7,2570),(7,2580),(7,2590),(7,2600),(7,2601),(7,2613),(7,2620),(7,2700),(7,2701),(7,2710),(7,2711),(7,2720),(7,2721),(7,2730),(7,2731),(7,2740),(7,2741),(7,2750),(7,2765),(7,2766),(7,2770),(7,2771),(7,2781),(7,2782),(7,2786),(7,2787),(7,2796),(7,2800),(7,2817),(7,2820);
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_warehouse`
--

DROP TABLE IF EXISTS `sys_role_warehouse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_warehouse` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `warehouse_id` bigint NOT NULL COMMENT '仓库ID',
  PRIMARY KEY (`role_id`,`warehouse_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色与授权仓库关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_warehouse`
--

LOCK TABLES `sys_role_warehouse` WRITE;
/*!40000 ALTER TABLE `sys_role_warehouse` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_role_warehouse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user`
--

DROP TABLE IF EXISTS `sys_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user` (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '密码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user`
--

LOCK TABLES `sys_user` WRITE;
/*!40000 ALTER TABLE `sys_user` DISABLE KEYS */;
INSERT INTO `sys_user` VALUES (1,103,'admin','仓链','00','ry@163.com','15888888888','1','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','127.0.0.1','2026-11-22 22:46:18','2026-11-22 10:36:44','admin','2026-11-22 10:36:44','',NULL,'管理员'),(2,105,'ry','仓链','00','ry@qq.com','15666666666','1','','$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2','0','0','127.0.0.1','2026-11-22 10:36:44','2026-11-22 10:36:44','admin','2026-11-22 10:36:44','',NULL,'测试员');
/*!40000 ALTER TABLE `sys_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_post`
--

DROP TABLE IF EXISTS `sys_user_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_post` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户与岗位关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_post`
--

LOCK TABLES `sys_user_post` WRITE;
/*!40000 ALTER TABLE `sys_user_post` DISABLE KEYS */;
INSERT INTO `sys_user_post` VALUES (1,1),(2,2);
/*!40000 ALTER TABLE `sys_user_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_user_role`
--

DROP TABLE IF EXISTS `sys_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_user_role` (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户和角色关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_user_role`
--

LOCK TABLES `sys_user_role` WRITE;
/*!40000 ALTER TABLE `sys_user_role` DISABLE KEYS */;
INSERT INTO `sys_user_role` VALUES (1,1),(2,2);
/*!40000 ALTER TABLE `sys_user_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_inbound`
--

DROP TABLE IF EXISTS `wms_inbound`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_inbound` (
  `inbound_id` bigint NOT NULL AUTO_INCREMENT COMMENT '入库单id',
  `inbound_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '入库单号',
  `inbound_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '入库类型',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商id',
  `warehouse_id` bigint NOT NULL COMMENT '仓库id',
  `source_bill_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '来源单据类型',
  `source_bill_id` bigint DEFAULT NULL COMMENT '来源单据id',
  `source_bill_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '来源单据号',
  `business_date` date DEFAULT NULL COMMENT '业务日期',
  `total_qty` decimal(18,2) DEFAULT '0.00' COMMENT '总数量',
  `total_amount` decimal(18,2) DEFAULT '0.00' COMMENT '总金额',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0草稿 1已审核 2已完成）',
  `biz_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'draft' COMMENT '业务状态',
  `audit_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '审核人',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `is_reversal` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '是否红冲单(0否 1是)',
  `origin_bill_id` bigint DEFAULT NULL COMMENT '被红冲原单ID',
  `reversed_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '是否已被红冲(0否 1是)',
  `unaudit_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '反审核人',
  `unaudit_time` datetime DEFAULT NULL COMMENT '反审核时间',
  `dept_id` bigint DEFAULT NULL COMMENT '所属部门ID',
  `owner_user_id` bigint DEFAULT NULL COMMENT '归属人ID',
  `discount_amount` decimal(16,2) DEFAULT '0.00' COMMENT '整单折让金额',
  `tax_total_amount` decimal(16,2) DEFAULT '0.00' COMMENT '税额合计',
  PRIMARY KEY (`inbound_id`),
  UNIQUE KEY `uk_wms_inbound_no` (`inbound_no`)
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='入库单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_inbound`
--

LOCK TABLES `wms_inbound` WRITE;
/*!40000 ALTER TABLE `wms_inbound` DISABLE KEYS */;
INSERT INTO `wms_inbound` VALUES (1,'inb20260101001','purchase',1,1,'',NULL,'','2026-11-22',50.00,750.00,'1','completed','admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例入库单','0',NULL,'0',NULL,NULL,NULL,NULL,0.00,0.00),(100,'IN202310010001','purchase',100,100,'',NULL,'','2026-11-22',100.00,5000.00,'1','completed','',NULL,'admin','2026-11-22 10:36:51','',NULL,'初始采购入库','0',NULL,'0',NULL,NULL,NULL,NULL,0.00,0.00);
/*!40000 ALTER TABLE `wms_inbound` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_inbound_item`
--

DROP TABLE IF EXISTS `wms_inbound_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_inbound_item` (
  `inbound_item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '入库明细id',
  `inbound_id` bigint NOT NULL COMMENT '入库单id',
  `product_id` bigint NOT NULL COMMENT '商品id',
  `location_id` bigint NOT NULL DEFAULT '0' COMMENT '库位id',
  `batch_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '批次号',
  `quantity` decimal(18,2) DEFAULT '0.00' COMMENT '数量',
  `price` decimal(18,2) DEFAULT '0.00' COMMENT '单价',
  `amount` decimal(18,2) DEFAULT '0.00' COMMENT '金额',
  `source_item_id` bigint DEFAULT NULL COMMENT '来源采购订单明细ID',
  `unit_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '业务单位',
  `convert_ratio` decimal(18,4) DEFAULT '1.0000' COMMENT '换算率',
  `base_quantity` decimal(18,4) DEFAULT NULL COMMENT '折合基准数量',
  `production_date` date DEFAULT NULL COMMENT '生产日期',
  `expiry_date` date DEFAULT NULL COMMENT '有效期至',
  `discount_rate` decimal(10,4) DEFAULT '100.0000' COMMENT '行折扣率(%)',
  `tax_rate` decimal(10,4) DEFAULT '0.0000' COMMENT '税率(%)',
  `tax_amount` decimal(16,2) DEFAULT '0.00' COMMENT '行税额',
  `sn_codes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '序列号SN清单(逗号或换行分隔)',
  PRIMARY KEY (`inbound_item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=118 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='入库单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_inbound_item`
--

LOCK TABLES `wms_inbound_item` WRITE;
/*!40000 ALTER TABLE `wms_inbound_item` DISABLE KEYS */;
INSERT INTO `wms_inbound_item` VALUES (1,1,1,0,'',50.00,15.00,750.00,NULL,NULL,1.0000,NULL,NULL,NULL,100.0000,0.0000,0.00,NULL),(100,100,100,1000,'BATCH20231001',100.00,50.00,5000.00,NULL,NULL,1.0000,NULL,NULL,NULL,100.0000,0.0000,0.00,NULL);
/*!40000 ALTER TABLE `wms_inbound_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_inventory_check`
--

DROP TABLE IF EXISTS `wms_inventory_check`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_inventory_check` (
  `check_id` bigint NOT NULL AUTO_INCREMENT COMMENT '盘点单id',
  `check_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '盘点单号',
  `warehouse_id` bigint NOT NULL COMMENT '仓库id',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0草稿 1已审核 2已完成）',
  `total_diff_qty` decimal(18,2) DEFAULT '0.00' COMMENT '差异数量合计',
  `total_diff_amount` decimal(18,2) DEFAULT '0.00' COMMENT '差异金额合计',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `is_reversal` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '是否红冲单(0否 1是)',
  `origin_bill_id` bigint DEFAULT NULL COMMENT '被红冲原单ID',
  `reversed_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '是否已被红冲(0否 1是)',
  `unaudit_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '反审核人',
  `unaudit_time` datetime DEFAULT NULL COMMENT '反审核时间',
  `dept_id` bigint DEFAULT NULL COMMENT '所属部门ID',
  `owner_user_id` bigint DEFAULT NULL COMMENT '归属人ID',
  `business_date` date DEFAULT NULL COMMENT '业务日期',
  PRIMARY KEY (`check_id`),
  UNIQUE KEY `uk_wms_inventory_check_no` (`check_no`)
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='盘点单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_inventory_check`
--

LOCK TABLES `wms_inventory_check` WRITE;
/*!40000 ALTER TABLE `wms_inventory_check` DISABLE KEYS */;
INSERT INTO `wms_inventory_check` VALUES (1,'chk20260101001',1,'1',2.00,30.00,'admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例盘点单','0',NULL,'0',NULL,NULL,NULL,NULL,'2026-11-22'),(100,'CHK202310030001',100,'0',0.00,0.00,'admin','2026-11-22 10:36:51','',NULL,'月度盘点','0',NULL,'0',NULL,NULL,NULL,NULL,'2026-11-22');
/*!40000 ALTER TABLE `wms_inventory_check` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_inventory_check_item`
--

DROP TABLE IF EXISTS `wms_inventory_check_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_inventory_check_item` (
  `check_item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '盘点明细id',
  `check_id` bigint NOT NULL COMMENT '盘点单id',
  `product_id` bigint NOT NULL COMMENT '商品id',
  `location_id` bigint NOT NULL DEFAULT '0' COMMENT '库位id',
  `batch_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '批次号',
  `stock_qty` decimal(18,2) DEFAULT '0.00' COMMENT '账面数量',
  `actual_qty` decimal(18,2) DEFAULT '0.00' COMMENT '实盘数量',
  `diff_qty` decimal(18,2) DEFAULT '0.00' COMMENT '差异数量',
  `price` decimal(18,2) DEFAULT '0.00' COMMENT '单价',
  `diff_amount` decimal(18,2) DEFAULT '0.00' COMMENT '差异金额',
  `unit_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '业务单位',
  `convert_ratio` decimal(16,4) DEFAULT '1.0000' COMMENT '单位换算率',
  `base_diff_qty` decimal(16,4) DEFAULT '0.0000' COMMENT '基准单位差异量',
  `production_date` date DEFAULT NULL COMMENT '生产日期',
  `expiry_date` date DEFAULT NULL COMMENT '有效期至',
  PRIMARY KEY (`check_item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='盘点单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_inventory_check_item`
--

LOCK TABLES `wms_inventory_check_item` WRITE;
/*!40000 ALTER TABLE `wms_inventory_check_item` DISABLE KEYS */;
INSERT INTO `wms_inventory_check_item` VALUES (1,1,1,0,'',100.00,102.00,2.00,15.00,30.00,NULL,1.0000,0.0000,NULL,NULL),(100,100,100,1000,'BATCH20231001',85.00,85.00,0.00,0.00,0.00,NULL,1.0000,0.0000,NULL,NULL);
/*!40000 ALTER TABLE `wms_inventory_check_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_outbound`
--

DROP TABLE IF EXISTS `wms_outbound`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_outbound` (
  `outbound_id` bigint NOT NULL AUTO_INCREMENT COMMENT '出库单id',
  `outbound_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '出库单号',
  `outbound_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '出库类型',
  `customer_id` bigint DEFAULT NULL COMMENT '客户id',
  `warehouse_id` bigint NOT NULL COMMENT '仓库id',
  `source_bill_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '来源单据类型',
  `source_bill_id` bigint DEFAULT NULL COMMENT '来源单据id',
  `source_bill_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '来源单据号',
  `business_date` date DEFAULT NULL COMMENT '业务日期',
  `total_qty` decimal(18,2) DEFAULT '0.00' COMMENT '总数量',
  `total_amount` decimal(18,2) DEFAULT '0.00' COMMENT '总金额',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0草稿 1已审核 2已完成）',
  `biz_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'draft' COMMENT '业务状态',
  `audit_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '审核人',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `platform_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '平台类型',
  `store_id` bigint DEFAULT NULL COMMENT '店铺id',
  `source_order_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '平台订单号',
  `carrier` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '承运商',
  `waybill_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '运单号',
  `freight_cost` decimal(18,2) DEFAULT '0.00' COMMENT '运费成本',
  `delivery_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '配送状态（0待发货 1已发货 2已签收 3退货）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `is_reversal` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '是否红冲单(0否 1是)',
  `origin_bill_id` bigint DEFAULT NULL COMMENT '被红冲原单ID',
  `reversed_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '是否已被红冲(0否 1是)',
  `unaudit_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '反审核人',
  `unaudit_time` datetime DEFAULT NULL COMMENT '反审核时间',
  `dept_id` bigint DEFAULT NULL COMMENT '所属部门ID',
  `owner_user_id` bigint DEFAULT NULL COMMENT '归属人ID',
  `discount_amount` decimal(16,2) DEFAULT '0.00' COMMENT '整单折让金额',
  `tax_total_amount` decimal(16,2) DEFAULT '0.00' COMMENT '税额合计',
  `verified_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '扫码复核状态(0未复核 1已复核)',
  `verified_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '复核人',
  `verified_time` datetime DEFAULT NULL COMMENT '复核时间',
  PRIMARY KEY (`outbound_id`),
  UNIQUE KEY `uk_wms_outbound_no` (`outbound_no`)
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='出库单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_outbound`
--

LOCK TABLES `wms_outbound` WRITE;
/*!40000 ALTER TABLE `wms_outbound` DISABLE KEYS */;
INSERT INTO `wms_outbound` VALUES (1,'out20260101001','sale',1,1,'',NULL,'','2026-11-22',20.00,560.00,'1','approved','admin','2026-11-22 10:36:50','',NULL,'','','',0.00,'2','admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例出库单','0',NULL,'0',NULL,NULL,NULL,NULL,0.00,0.00,'0',NULL,NULL),(100,'OUT202310020001','sale',100,100,'',NULL,'','2026-11-22',10.00,990.00,'1','approved','',NULL,'',NULL,'','','',0.00,'2','admin','2026-11-22 10:36:51','',NULL,'销售出库测试','0',NULL,'0',NULL,NULL,NULL,NULL,0.00,0.00,'0',NULL,NULL);
/*!40000 ALTER TABLE `wms_outbound` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_outbound_item`
--

DROP TABLE IF EXISTS `wms_outbound_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_outbound_item` (
  `outbound_item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '出库明细id',
  `outbound_id` bigint NOT NULL COMMENT '出库单id',
  `product_id` bigint NOT NULL COMMENT '商品id',
  `location_id` bigint NOT NULL DEFAULT '0' COMMENT '库位id',
  `batch_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '批次号',
  `quantity` decimal(18,2) DEFAULT '0.00' COMMENT '数量',
  `price` decimal(18,2) DEFAULT '0.00' COMMENT '单价',
  `amount` decimal(18,2) DEFAULT '0.00' COMMENT '金额',
  `source_item_id` bigint DEFAULT NULL COMMENT '来源销货订单明细ID',
  `unit_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '业务单位',
  `convert_ratio` decimal(18,4) DEFAULT '1.0000' COMMENT '换算率',
  `base_quantity` decimal(18,4) DEFAULT NULL COMMENT '折合基准数量',
  `production_date` date DEFAULT NULL COMMENT '生产日期',
  `expiry_date` date DEFAULT NULL COMMENT '有效期至',
  `discount_rate` decimal(10,4) DEFAULT '100.0000' COMMENT '行折扣率(%)',
  `tax_rate` decimal(10,4) DEFAULT '0.0000' COMMENT '税率(%)',
  `tax_amount` decimal(16,2) DEFAULT '0.00' COMMENT '行税额',
  `sn_codes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '序列号SN清单(逗号或换行分隔)',
  PRIMARY KEY (`outbound_item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='出库单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_outbound_item`
--

LOCK TABLES `wms_outbound_item` WRITE;
/*!40000 ALTER TABLE `wms_outbound_item` DISABLE KEYS */;
INSERT INTO `wms_outbound_item` VALUES (1,1,1,0,'',20.00,28.00,560.00,NULL,NULL,1.0000,NULL,NULL,NULL,100.0000,0.0000,0.00,NULL),(100,100,100,1000,'BATCH20231001',10.00,99.00,990.00,NULL,NULL,1.0000,NULL,NULL,NULL,100.0000,0.0000,0.00,NULL);
/*!40000 ALTER TABLE `wms_outbound_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_purchase_return`
--

DROP TABLE IF EXISTS `wms_purchase_return`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_purchase_return` (
  `purchase_return_id` bigint NOT NULL AUTO_INCREMENT COMMENT '采购退货id',
  `return_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '退货单号',
  `return_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '退货类型',
  `supplier_id` bigint DEFAULT NULL COMMENT '供应商id',
  `warehouse_id` bigint NOT NULL COMMENT '仓库id',
  `source_bill_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '来源单据类型',
  `source_bill_id` bigint DEFAULT NULL COMMENT '来源单据id',
  `source_bill_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '来源单据号',
  `business_date` date DEFAULT NULL COMMENT '业务日期',
  `total_qty` decimal(18,2) DEFAULT '0.00' COMMENT '总数量',
  `total_amount` decimal(18,2) DEFAULT '0.00' COMMENT '总金额',
  `payable_id` bigint DEFAULT NULL COMMENT '关联红字应付单ID',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0草稿 1已审核）',
  `biz_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'draft' COMMENT '业务状态',
  `audit_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '审核人',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `is_reversal` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '是否红冲单(0否 1是)',
  `origin_bill_id` bigint DEFAULT NULL COMMENT '被红冲原单ID',
  `reversed_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '是否已被红冲(0否 1是)',
  `unaudit_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '反审核人',
  `unaudit_time` datetime DEFAULT NULL COMMENT '反审核时间',
  `dept_id` bigint DEFAULT NULL COMMENT '所属部门ID',
  `owner_user_id` bigint DEFAULT NULL COMMENT '归属人ID',
  PRIMARY KEY (`purchase_return_id`),
  UNIQUE KEY `uk_wms_purchase_return_no` (`return_no`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='采购退货单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_purchase_return`
--

LOCK TABLES `wms_purchase_return` WRITE;
/*!40000 ALTER TABLE `wms_purchase_return` DISABLE KEYS */;
/*!40000 ALTER TABLE `wms_purchase_return` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_purchase_return_item`
--

DROP TABLE IF EXISTS `wms_purchase_return_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_purchase_return_item` (
  `purchase_return_item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '采购退货明细id',
  `purchase_return_id` bigint NOT NULL COMMENT '采购退货id',
  `product_id` bigint NOT NULL COMMENT '商品id',
  `location_id` bigint NOT NULL DEFAULT '0' COMMENT '库位id',
  `batch_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '批次号',
  `quantity` decimal(18,2) DEFAULT '0.00' COMMENT '数量',
  `price` decimal(18,2) DEFAULT '0.00' COMMENT '单价',
  `amount` decimal(18,2) DEFAULT '0.00' COMMENT '金额',
  `unit_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '业务单位',
  `convert_ratio` decimal(16,4) DEFAULT '1.0000' COMMENT '单位换算率',
  `base_quantity` decimal(16,4) DEFAULT '0.0000' COMMENT '基准单位数量',
  `production_date` date DEFAULT NULL COMMENT '生产日期',
  `expiry_date` date DEFAULT NULL COMMENT '有效期至',
  `sn_codes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '序列号SN清单',
  `deduct_trace` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '整仓扣减库存轨迹JSON',
  PRIMARY KEY (`purchase_return_item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='采购退货明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_purchase_return_item`
--

LOCK TABLES `wms_purchase_return_item` WRITE;
/*!40000 ALTER TABLE `wms_purchase_return_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `wms_purchase_return_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_sale_return`
--

DROP TABLE IF EXISTS `wms_sale_return`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_sale_return` (
  `sale_return_id` bigint NOT NULL AUTO_INCREMENT COMMENT '销售退货id',
  `return_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '退货单号',
  `return_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '退货类型',
  `customer_id` bigint DEFAULT NULL COMMENT '客户id',
  `warehouse_id` bigint NOT NULL COMMENT '仓库id',
  `source_bill_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '来源单据类型',
  `source_bill_id` bigint DEFAULT NULL COMMENT '来源单据id',
  `source_bill_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '来源单据号',
  `business_date` date DEFAULT NULL COMMENT '业务日期',
  `total_qty` decimal(18,2) DEFAULT '0.00' COMMENT '总数量',
  `total_amount` decimal(18,2) DEFAULT '0.00' COMMENT '总金额',
  `receivable_id` bigint DEFAULT NULL COMMENT '关联红字应收单ID',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0草稿 1已审核）',
  `biz_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'draft' COMMENT '业务状态',
  `audit_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '审核人',
  `audit_time` datetime DEFAULT NULL COMMENT '审核时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `is_reversal` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '是否红冲单(0否 1是)',
  `origin_bill_id` bigint DEFAULT NULL COMMENT '被红冲原单ID',
  `reversed_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '是否已被红冲(0否 1是)',
  `unaudit_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '反审核人',
  `unaudit_time` datetime DEFAULT NULL COMMENT '反审核时间',
  `dept_id` bigint DEFAULT NULL COMMENT '所属部门ID',
  `owner_user_id` bigint DEFAULT NULL COMMENT '归属人ID',
  PRIMARY KEY (`sale_return_id`),
  UNIQUE KEY `uk_wms_sale_return_no` (`return_no`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='销售退货单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_sale_return`
--

LOCK TABLES `wms_sale_return` WRITE;
/*!40000 ALTER TABLE `wms_sale_return` DISABLE KEYS */;
/*!40000 ALTER TABLE `wms_sale_return` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_sale_return_item`
--

DROP TABLE IF EXISTS `wms_sale_return_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_sale_return_item` (
  `sale_return_item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '销售退货明细id',
  `sale_return_id` bigint NOT NULL COMMENT '销售退货id',
  `product_id` bigint NOT NULL COMMENT '商品id',
  `location_id` bigint NOT NULL DEFAULT '0' COMMENT '库位id',
  `batch_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '批次号',
  `quantity` decimal(18,2) DEFAULT '0.00' COMMENT '数量',
  `price` decimal(18,2) DEFAULT '0.00' COMMENT '单价',
  `amount` decimal(18,2) DEFAULT '0.00' COMMENT '金额',
  `unit_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '业务单位',
  `convert_ratio` decimal(16,4) DEFAULT '1.0000' COMMENT '单位换算率',
  `base_quantity` decimal(16,4) DEFAULT '0.0000' COMMENT '基准单位数量',
  `production_date` date DEFAULT NULL COMMENT '生产日期',
  `expiry_date` date DEFAULT NULL COMMENT '有效期至',
  `sn_codes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '序列号SN清单',
  PRIMARY KEY (`sale_return_item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='销售退货明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_sale_return_item`
--

LOCK TABLES `wms_sale_return_item` WRITE;
/*!40000 ALTER TABLE `wms_sale_return_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `wms_sale_return_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_serial_no`
--

DROP TABLE IF EXISTS `wms_serial_no`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_serial_no` (
  `serial_id` bigint NOT NULL AUTO_INCREMENT COMMENT '序列号主键',
  `sn_code` varchar(128) NOT NULL COMMENT '唯一序列号(SN)',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `warehouse_id` bigint DEFAULT NULL COMMENT '当前所在仓库ID',
  `location_id` bigint DEFAULT '0' COMMENT '当前库位ID',
  `batch_no` varchar(64) DEFAULT '' COMMENT '批次号',
  `status` varchar(20) NOT NULL DEFAULT 'in_stock' COMMENT '状态(in_stock在库/out_stock已出库/returned退货回库/scrapped报废)',
  `inbound_id` bigint DEFAULT NULL COMMENT '入库单ID',
  `outbound_id` bigint DEFAULT NULL COMMENT '出库单ID',
  `customer_id` bigint DEFAULT NULL COMMENT '最终售出客户ID',
  `warranty_end_date` date DEFAULT NULL COMMENT '质保截止日期',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_by` varchar(64) DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`serial_id`),
  UNIQUE KEY `uk_wms_sn_code` (`sn_code`),
  KEY `idx_sn_prod_wh` (`product_id`,`warehouse_id`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品序列号(SN)全生命周期档案';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_serial_no`
--

LOCK TABLES `wms_serial_no` WRITE;
/*!40000 ALTER TABLE `wms_serial_no` DISABLE KEYS */;
/*!40000 ALTER TABLE `wms_serial_no` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_serial_no_log`
--

DROP TABLE IF EXISTS `wms_serial_no_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_serial_no_log` (
  `sn_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT 'SN流转日志ID',
  `serial_id` bigint NOT NULL COMMENT '序列号ID',
  `sn_code` varchar(128) NOT NULL COMMENT '序列号',
  `action_type` varchar(32) NOT NULL COMMENT '动作类型(inbound/outbound/transfer/sale_return/purchase_return/adjust)',
  `bill_type` varchar(32) DEFAULT NULL COMMENT '单据类型',
  `bill_id` bigint DEFAULT NULL COMMENT '单据ID',
  `bill_no` varchar(64) DEFAULT NULL COMMENT '单据号',
  `warehouse_id` bigint DEFAULT NULL COMMENT '仓库ID',
  `counterparty_id` bigint DEFAULT NULL COMMENT '往来单位ID',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`sn_log_id`),
  KEY `idx_sn_log_code` (`sn_code`,`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品序列号(SN)流转履历表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_serial_no_log`
--

LOCK TABLES `wms_serial_no_log` WRITE;
/*!40000 ALTER TABLE `wms_serial_no_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `wms_serial_no_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_stock`
--

DROP TABLE IF EXISTS `wms_stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_stock` (
  `stock_id` bigint NOT NULL AUTO_INCREMENT COMMENT '库存id',
  `warehouse_id` bigint NOT NULL COMMENT '仓库id',
  `product_id` bigint NOT NULL COMMENT '商品id',
  `location_id` bigint NOT NULL DEFAULT '0' COMMENT '库位id',
  `batch_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '批次号',
  `quantity` decimal(18,2) DEFAULT '0.00' COMMENT '库存数量',
  `locked_quantity` decimal(18,2) DEFAULT '0.00' COMMENT '锁定数量',
  `frozen_quantity` decimal(18,2) DEFAULT '0.00' COMMENT '冻结数量',
  `warning_min_qty` decimal(18,2) DEFAULT '0.00' COMMENT '预警最小库存',
  `warning_max_qty` decimal(18,2) DEFAULT '0.00' COMMENT '预警最大库存',
  `version` bigint DEFAULT '0' COMMENT '版本号',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `production_date` date DEFAULT NULL COMMENT '生产日期',
  `expiry_date` date DEFAULT NULL COMMENT '有效期至',
  PRIMARY KEY (`stock_id`),
  UNIQUE KEY `uk_wms_stock_warehouse_product` (`warehouse_id`,`product_id`,`location_id`,`batch_no`)
) ENGINE=InnoDB AUTO_INCREMENT=120 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='库存表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_stock`
--

LOCK TABLES `wms_stock` WRITE;
/*!40000 ALTER TABLE `wms_stock` DISABLE KEYS */;
INSERT INTO `wms_stock` VALUES (1,1,1,0,'',100.00,0.00,0.00,10.00,500.00,4,'admin','2026-11-22 10:36:50','admin','2026-11-22 19:02:31',NULL,NULL),(2,1,2,0,'',80.00,0.00,0.00,10.00,300.00,0,'admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50',NULL,NULL),(3,2,3,0,'',200.00,0.00,0.00,50.00,800.00,0,'admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50',NULL,NULL),(100,100,100,1000,'BATCH20231001',85.00,0.00,0.00,10.00,1000.00,0,'admin','2026-11-22 10:36:51','',NULL,NULL,NULL),(101,101,100,2000,'BATCH20231001',5.00,0.00,0.00,0.00,100.00,0,'admin','2026-11-22 10:36:51','',NULL,NULL,NULL);
/*!40000 ALTER TABLE `wms_stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_stock_lock`
--

DROP TABLE IF EXISTS `wms_stock_lock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_stock_lock` (
  `lock_id` bigint NOT NULL AUTO_INCREMENT COMMENT '锁库台账ID',
  `order_id` bigint NOT NULL COMMENT '销售订单ID',
  `order_item_id` bigint DEFAULT NULL COMMENT '销售订单明细ID',
  `warehouse_id` bigint NOT NULL COMMENT '仓库ID',
  `product_id` bigint NOT NULL COMMENT '商品ID',
  `location_id` bigint NOT NULL DEFAULT '0' COMMENT '库位ID',
  `batch_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '批次号',
  `stock_id` bigint NOT NULL COMMENT '对应库存行ID',
  `locked_qty` decimal(16,4) NOT NULL DEFAULT '0.0000' COMMENT '原始锁定数量(基准单位)',
  `released_qty` decimal(16,4) NOT NULL DEFAULT '0.0000' COMMENT '已释放数量(基准单位)',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '状态(0锁定中 1已全部释放)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`lock_id`),
  KEY `idx_wms_stock_lock_order` (`order_id`,`product_id`,`status`),
  KEY `idx_wms_stock_lock_stock` (`stock_id`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='销售订单库存锁库明细台账';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_stock_lock`
--

LOCK TABLES `wms_stock_lock` WRITE;
/*!40000 ALTER TABLE `wms_stock_lock` DISABLE KEYS */;
/*!40000 ALTER TABLE `wms_stock_lock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_stock_log`
--

DROP TABLE IF EXISTS `wms_stock_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_stock_log` (
  `stock_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '库存流水id',
  `warehouse_id` bigint NOT NULL COMMENT '仓库id',
  `product_id` bigint NOT NULL COMMENT '商品id',
  `location_id` bigint NOT NULL DEFAULT '0' COMMENT '库位id',
  `batch_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '批次号',
  `bill_type` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '单据类型',
  `bill_id` bigint NOT NULL COMMENT '单据id',
  `in_out` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '出入库标识（1入库 2出库 3调整）',
  `quantity` decimal(18,2) DEFAULT '0.00' COMMENT '数量',
  `price` decimal(18,2) DEFAULT '0.00' COMMENT '单价',
  `amount` decimal(18,2) DEFAULT '0.00' COMMENT '金额',
  `before_qty` decimal(18,2) DEFAULT '0.00' COMMENT '变动前数量',
  `after_qty` decimal(18,2) DEFAULT '0.00' COMMENT '变动后数量',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`stock_log_id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='库存流水表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_stock_log`
--

LOCK TABLES `wms_stock_log` WRITE;
/*!40000 ALTER TABLE `wms_stock_log` DISABLE KEYS */;
INSERT INTO `wms_stock_log` VALUES (1,1,1,0,'','inbound',1,'1',50.00,15.00,750.00,50.00,100.00,'admin','2026-11-22 10:36:50'),(2,1,1,0,'','outbound',1,'2',20.00,28.00,560.00,100.00,80.00,'admin','2026-11-22 10:36:50');
/*!40000 ALTER TABLE `wms_stock_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_transfer`
--

DROP TABLE IF EXISTS `wms_transfer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_transfer` (
  `transfer_id` bigint NOT NULL AUTO_INCREMENT COMMENT '调拨单id',
  `transfer_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调拨单号',
  `out_warehouse_id` bigint NOT NULL COMMENT '调出仓库id',
  `in_warehouse_id` bigint NOT NULL COMMENT '调入仓库id',
  `total_qty` decimal(18,2) DEFAULT '0.00' COMMENT '总数量',
  `total_amount` decimal(18,2) DEFAULT '0.00' COMMENT '总金额',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0草稿 1已审核 2已完成）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `is_reversal` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '是否红冲单(0否 1是)',
  `origin_bill_id` bigint DEFAULT NULL COMMENT '被红冲原单ID',
  `reversed_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '是否已被红冲(0否 1是)',
  `unaudit_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '反审核人',
  `unaudit_time` datetime DEFAULT NULL COMMENT '反审核时间',
  `dept_id` bigint DEFAULT NULL COMMENT '所属部门ID',
  `owner_user_id` bigint DEFAULT NULL COMMENT '归属人ID',
  `business_date` date DEFAULT NULL COMMENT '业务日期',
  PRIMARY KEY (`transfer_id`),
  UNIQUE KEY `uk_wms_transfer_no` (`transfer_no`)
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='调拨单表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_transfer`
--

LOCK TABLES `wms_transfer` WRITE;
/*!40000 ALTER TABLE `wms_transfer` DISABLE KEYS */;
INSERT INTO `wms_transfer` VALUES (1,'trf20260101001',1,2,10.00,200.00,'1','admin','2026-11-22 10:36:50','admin','2026-11-22 10:36:50','样例调拨单','0',NULL,'0',NULL,NULL,NULL,NULL,'2026-11-22'),(100,'TR202310040001',100,101,5.00,250.00,'1','admin','2026-11-22 10:36:51','',NULL,'总仓调门店','0',NULL,'0',NULL,NULL,NULL,NULL,'2026-11-22');
/*!40000 ALTER TABLE `wms_transfer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wms_transfer_item`
--

DROP TABLE IF EXISTS `wms_transfer_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wms_transfer_item` (
  `transfer_item_id` bigint NOT NULL AUTO_INCREMENT COMMENT '调拨明细id',
  `transfer_id` bigint NOT NULL COMMENT '调拨单id',
  `product_id` bigint NOT NULL COMMENT '商品id',
  `out_location_id` bigint NOT NULL DEFAULT '0' COMMENT '调出库位id',
  `in_location_id` bigint NOT NULL DEFAULT '0' COMMENT '调入库位id',
  `batch_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '批次号',
  `quantity` decimal(18,2) DEFAULT '0.00' COMMENT '数量',
  `price` decimal(18,2) DEFAULT '0.00' COMMENT '单价',
  `amount` decimal(18,2) DEFAULT '0.00' COMMENT '金额',
  `unit_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '业务单位',
  `convert_ratio` decimal(16,4) DEFAULT '1.0000' COMMENT '单位换算率',
  `base_quantity` decimal(16,4) DEFAULT '0.0000' COMMENT '基准单位数量',
  `production_date` date DEFAULT NULL COMMENT '生产日期',
  `expiry_date` date DEFAULT NULL COMMENT '有效期至',
  `sn_codes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci COMMENT '序列号SN清单',
  PRIMARY KEY (`transfer_item_id`)
) ENGINE=InnoDB AUTO_INCREMENT=109 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='调拨单明细表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wms_transfer_item`
--

LOCK TABLES `wms_transfer_item` WRITE;
/*!40000 ALTER TABLE `wms_transfer_item` DISABLE KEYS */;
INSERT INTO `wms_transfer_item` VALUES (1,1,2,0,0,'',10.00,20.00,200.00,NULL,1.0000,0.0000,NULL,NULL,NULL),(100,100,100,1000,2000,'BATCH20231001',5.00,50.00,250.00,NULL,1.0000,0.0000,NULL,NULL,NULL);
/*!40000 ALTER TABLE `wms_transfer_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'canglian'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-22 22:56:26
