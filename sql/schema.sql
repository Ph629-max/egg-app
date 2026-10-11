-- ============================================================
-- 蛋品库存与订单管理系统 数据库初始化脚本
-- 数据库：inventory_db（MySQL 5.7+ / 8.0）
-- ============================================================

CREATE DATABASE IF NOT EXISTS inventory_db DEFAULT CHARSET utf8mb4;
USE inventory_db;

-- 库存表
CREATE TABLE IF NOT EXISTS inventory (
    id             INT PRIMARY KEY AUTO_INCREMENT,
    name           VARCHAR(100)  NOT NULL,
    quantity       INT           NOT NULL DEFAULT 0,
    safety_stock   INT           NOT NULL DEFAULT 0,
    reorder_point  INT           NOT NULL DEFAULT 0,
    egg_type_id    INT           NOT NULL DEFAULT 1,
    cost_price     DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    packaging_type VARCHAR(50)            DEFAULT NULL,
    is_virtual     TINYINT(1)    NOT NULL DEFAULT 0
);

-- 分拣规则表
CREATE TABLE IF NOT EXISTS sorting_rules (
    id             INT PRIMARY KEY AUTO_INCREMENT,
    egg_type_id    INT           NOT NULL,
    packaging_type VARCHAR(50)   NOT NULL,
    damage_ratio   DECIMAL(5,4)  NOT NULL DEFAULT 0.0000,
    packing_cost   DECIMAL(10,2) NOT NULL DEFAULT 0.00
);

-- 订单表（通用订单）
CREATE TABLE IF NOT EXISTS orders (
    id                INT PRIMARY KEY AUTO_INCREMENT,
    shop_id           INT           NOT NULL DEFAULT 0,
    inventory_id      INT           NOT NULL,
    quantity          INT           NOT NULL,
    price             DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    cost_price        DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    order_type        VARCHAR(20)            DEFAULT NULL,
    order_date        DATETIME      NOT NULL,
    status            VARCHAR(30)            DEFAULT NULL,
    paid              TINYINT(1)    NOT NULL DEFAULT 0,
    payment_method    VARCHAR(30)            DEFAULT NULL,
    packaging_type_id INT           NOT NULL DEFAULT 0,
    packaging_type    VARCHAR(50)            DEFAULT NULL,
    type              VARCHAR(20)            DEFAULT NULL,
    total_price       DECIMAL(10,2) NOT NULL DEFAULT 0.00
);

-- 采购订单表
CREATE TABLE IF NOT EXISTS purchase_orders (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    supplier_id  INT           NOT NULL,
    inventory_id INT           NOT NULL,
    quantity     INT           NOT NULL,
    price        DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    order_date   DATETIME      NOT NULL,
    status       VARCHAR(30)   NOT NULL DEFAULT 'PENDING'
);

-- 销售订单表
CREATE TABLE IF NOT EXISTS sales_orders (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    customer_id  INT           NOT NULL,
    inventory_id INT           NOT NULL,
    quantity     INT           NOT NULL,
    price        DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    order_date   DATETIME      NOT NULL,
    status       VARCHAR(30)   NOT NULL DEFAULT 'PENDING'
);

-- 店铺表（界面中的订单店铺下拉使用）
CREATE TABLE IF NOT EXISTS shops (
    id   INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL
);

-- 库存预警记录表
CREATE TABLE IF NOT EXISTS stock_alerts (
    id           INT PRIMARY KEY AUTO_INCREMENT,
    inventory_id INT         NOT NULL,
    alert_type   VARCHAR(30) NOT NULL,
    alert_level  INT         NOT NULL DEFAULT 1,
    created_at   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------------- 示例数据 ----------------
INSERT INTO shops (name) VALUES ('一号门店'), ('二号门店'), ('三号门店');

INSERT INTO inventory (name, quantity, safety_stock, reorder_point, egg_type_id, cost_price, packaging_type, is_virtual)
VALUES ('鲜鸡蛋', 1200, 200, 500, 1, 0.55, '散装', 0),
       ('土鸡蛋', 600,  150, 300, 1, 0.90, '盒装', 0),
       ('鸭蛋',   300,  100, 200, 2, 0.80, '散装', 0);

INSERT INTO sorting_rules (egg_type_id, packaging_type, damage_ratio, packing_cost)
VALUES (1, '散装', 0.0300, 0.05),
       (1, '盒装', 0.0200, 0.30),
       (2, '散装', 0.0400, 0.06);
