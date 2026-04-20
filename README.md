# Canglian Management System

<p>
  <a href="./README.md"><strong>English</strong></a>
  ·
  <a href="./README.zh-CN.md"><strong>中文</strong></a>
</p>

> A warehouse, inventory, and finance management system built as a business-oriented extension of `RuoYi` / `RuoYi-Vue`.

## Project Positioning

This repository contains the full Canglian system:

- `canglian`: backend multi-module Java project
- `canglian-vue3`: frontend Vue 3 project
- root-level documentation and repository configuration

Technically, this project is a secondary development project based on the RuoYi ecosystem. It keeps the proven RuoYi platform capabilities and extends them with warehouse execution, inventory tracking, master data, finance, and light ERP business workflows.

## Latest Update

The latest delivery diary is available at:

- [`docs/更新日记/2026-04-19_精斗云轻量化升级.md`](docs/更新日记/2026-04-19_精斗云轻量化升级.md)

This update moves the system from "warehouse execution + basic AR/AP finance" toward a light order-driven ERP model aligned with Kingdee Jingdou Cloud scenarios.

## Quick Start

### 1. Requirements

- JDK 1.8
- Maven 3.6+
- Node.js 18+
- MySQL 5.7 / 8.0
- Redis

### 2. Initialize Database

Import the SQL scripts in this order:

1. `canglian/sql/canglian_20250522.sql`
2. `canglian/sql/ec_core_20260125.sql`
3. `canglian/sql/quartz.sql`
4. `canglian/sql/sample_data.sql`

For upgraded environments, also run the upgrade scripts that match your current database version:

1. `canglian/sql/upgrade_20260417_schema_alignment.sql`
2. `canglian/sql/upgrade_20260418_jdy_light_erp.sql`

### 3. Start Backend

```bash
cd canglian
mvn package -DskipTests
```

Main application class:

```text
com.canglian.CanglianApplication
```

Default backend API:

```text
http://localhost:7565
```

### 4. Start Frontend

```bash
cd canglian-vue3
npm install
npm run dev
```

The frontend development URL depends on the port printed by `npm run dev`.

## Core Capabilities

### Master Data

- Customer management
- Supplier management
- Product management
- Warehouse management
- Location management
- Customer levels, credit limits, settlement days, and supplier payment terms
- Product purchase unit, sale unit, base unit, conversion ratio, batch control, serial control, shelf life, and stock warning thresholds

### Warehouse And Inventory

- Inbound management
- Outbound management
- Stock management
- Stock movement records
- Transfer management
- Inventory check management
- Sales return management
- Purchase return management
- Warehouse statistics
- Barcode-based product, location, stock, inventory check, transfer, and quick sale operations
- Replenishment suggestions based on recent sales and minimum stock thresholds

### Order-Driven Light ERP

- Quotations
- Sales orders
- Purchase orders
- Purchase invoice registration
- Sales invoice registration
- Source bill tracing across business documents
- Unified business date, business status, and source bill fields

### Pricing

The pricing model supports:

- Customer special prices
- Customer level prices
- Recent transaction prices
- Product default sale prices

Price resolution priority:

```text
customer special price -> customer level price -> recent transaction price -> product default sale price
```

### Finance

- Receivables
- Payables
- Receipts
- Payments
- Expense management
- Fund accounts
- Settlement and write-off management
- Bad debt management
- Cost layer management
- Cost calculation
- Finance operation logs
- Finance reports
- Voucher event reservation for later accounting voucher integration

### Collaboration And Dashboard

- Business workbench
- Low stock reminders
- Pending approval items
- Due receivable reminders
- Due payable reminders
- Responsive frontend pages for common lightweight mobile scenarios

### RuoYi Platform Capabilities

- User management
- Role management
- Menu management
- Department management
- Position management
- Dictionary management
- Parameter configuration
- Authentication and authorization
- Operation logs and login logs
- Online user monitoring
- Cache and server monitoring
- Scheduled tasks
- Code generator
- Common file upload

## Tech Stack

### Backend

- Java 8
- Spring Boot 2.5.15
- Spring Security
- MyBatis
- Druid
- Redis
- Quartz
- Maven

### Frontend

- Vue 3
- Vite
- Element Plus
- Pinia
- Vue Router
- Axios
- Sass

## Repository Structure

```text
canglian/
├─ README.md                      // English documentation
├─ README.zh-CN.md                // Chinese documentation
├─ docs                           // Delivery notes and project documents
├─ canglian/                      // Backend project
│  ├─ pom.xml                     // Backend parent project
│  ├─ canglian-admin              // Admin application and controllers
│  ├─ canglian-common             // Common utilities
│  ├─ canglian-framework          // Framework, security, and infrastructure
│  ├─ canglian-system             // System and business modules
│  ├─ canglian-quartz             // Scheduled task module
│  ├─ canglian-generator          // Code generator module
│  └─ sql                         // Database scripts
└─ canglian-vue3/                 // Frontend project
```

## Configuration

Default project configuration:

- Backend port: `7565`
- Backend context path: `/`
- Frontend development proxy prefix: `/dev-api`
- Database name: `canglian`

Backend configuration files:

- `canglian/canglian-admin/src/main/resources/application.yml`
- `canglian/canglian-admin/src/main/resources/application-druid.yml`

Frontend configuration files:

- `canglian-vue3/.env.development`
- `canglian-vue3/.env.production`

The backend supports environment variable overrides for key deployment settings such as:

- `SERVER_PORT`
- `CANGLIAN_PROFILE`
- `REDIS_HOST`
- `REDIS_PORT`
- `REDIS_PASSWORD`
- `MYSQL_URL`
- `MYSQL_USERNAME`
- `MYSQL_PASSWORD`
- `LOG_HOME`

The frontend development proxy target can be changed through `VITE_APP_PROXY_TARGET` in `canglian-vue3/.env.development`.

## Build

Backend:

```bash
cd canglian
mvn package -DskipTests
```

Frontend:

```bash
cd canglian-vue3
npm run build:prod
```

## Deployment Checklist

- Create the `canglian` database and import all required SQL scripts.
- Update MySQL connection settings for the deployment environment.
- Update Redis connection settings for the deployment environment.
- Confirm the upload directory configured by `profile` is writable.
- Confirm backend port `7565` is available or override it with `SERVER_PORT`.
- Run frontend dependency installation and production build.
- Confirm reverse proxy rules match the frontend production API prefix.
- Change or verify default account credentials before production use.
- Confirm log, upload, and cache directories have proper read/write permissions.

## Built-In Accounts

The SQL scripts include initial account data. Common accounts include:

- Administrator account: `admin`
- Test account: `ry`

Passwords may differ by imported SQL version or deployment environment. Use the current database data as the final source of truth.

## Recommended GitHub Repository Metadata

Description:

```text
Warehouse, inventory, and finance management system based on RuoYi / RuoYi-Vue
```

Topics:

```text
ruoyi, ruoyi-vue, spring-boot, java, vue3, vite, element-plus, mybatis, redis, quartz, warehouse-management, inventory-management, finance-management, erp
```

## Notes

- This README is an overview and deployment guide, not a replacement for detailed design documentation.
- For production deployment, re-check database, Redis, upload path, logging, CORS, reverse proxy, and credentials.
- New backend business features should keep the existing `controller -> service -> mapper` layering style.
- New SQL should stay consistent with the existing project convention and use lowercase SQL keywords.
