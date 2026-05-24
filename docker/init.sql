-- ============================================================
-- MallForge Platform - Database Schema
-- MySQL 8.0  |  Charset: utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS mallforge DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mallforge;

-- ─────────────────────────────────────────────────────────────
-- 1. 系统模块 (mall-system)
-- ─────────────────────────────────────────────────────────────

CREATE TABLE tenant (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL COMMENT '租户名称',
    contact_name  VARCHAR(50),
    contact_phone VARCHAR(20),
    contact_email VARCHAR(100),
    status       TINYINT      NOT NULL DEFAULT 1 COMMENT '0=禁用 1=正常',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expired_at   DATETIME     COMMENT '到期时间，NULL 表示永久'
) COMMENT='租户表';

CREATE TABLE `user` (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id     BIGINT       COMMENT 'NULL 表示超级管理员',
    username      VARCHAR(50)  NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    real_name     VARCHAR(50),
    phone         VARCHAR(20),
    email         VARCHAR(100),
    avatar        VARCHAR(500),
    status        TINYINT      NOT NULL DEFAULT 1,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at DATETIME,
    UNIQUE KEY uk_username (username),
    KEY idx_tenant_id (tenant_id)
) COMMENT='用户表';

CREATE TABLE role (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id   BIGINT      NOT NULL,
    name        VARCHAR(50) NOT NULL,
    code        VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    KEY idx_tenant_id (tenant_id)
) COMMENT='角色表';

CREATE TABLE permission (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id BIGINT      DEFAULT 0,
    name      VARCHAR(50) NOT NULL,
    code      VARCHAR(100) NOT NULL UNIQUE,
    type      VARCHAR(20) COMMENT 'menu / button / api'
) COMMENT='权限表';

CREATE TABLE user_role (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id)
) COMMENT='用户角色关联';

CREATE TABLE role_permission (
    role_id       BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id)
) COMMENT='角色权限关联';

CREATE TABLE user_address (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id        BIGINT      NOT NULL,
    receiver_name  VARCHAR(50) NOT NULL,
    receiver_phone VARCHAR(20) NOT NULL,
    province       VARCHAR(50),
    city           VARCHAR(50),
    district       VARCHAR(50),
    detail         VARCHAR(200),
    is_default     TINYINT NOT NULL DEFAULT 0,
    KEY idx_user_id (user_id)
) COMMENT='用户收货地址';

-- 店铺装修页面
CREATE TABLE store_page (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id  BIGINT       NOT NULL,
    name       VARCHAR(100) NOT NULL,
    type       VARCHAR(20)  NOT NULL DEFAULT 'home' COMMENT 'home/activity/custom',
    page_json  MEDIUMTEXT   COMMENT '页面组件 JSON',
    status     VARCHAR(20)  NOT NULL DEFAULT 'draft' COMMENT 'draft/published',
    version    INT          NOT NULL DEFAULT 1,
    is_default TINYINT      NOT NULL DEFAULT 0,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_tenant_id (tenant_id)
) COMMENT='店铺装修页面';

-- ─────────────────────────────────────────────────────────────
-- 2. 商品模块 (mall-product)
-- ─────────────────────────────────────────────────────────────

CREATE TABLE category (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT      NOT NULL,
    parent_id BIGINT      NOT NULL DEFAULT 0,
    name      VARCHAR(50) NOT NULL,
    icon      VARCHAR(500),
    sort      INT         NOT NULL DEFAULT 0,
    status    TINYINT     NOT NULL DEFAULT 1,
    KEY idx_tenant_parent (tenant_id, parent_id)
) COMMENT='商品分类';

CREATE TABLE product (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id    BIGINT         NOT NULL,
    category_id  BIGINT,
    name         VARCHAR(200)   NOT NULL,
    brand        VARCHAR(100),
    cover_image  VARCHAR(500),
    images       JSON           COMMENT '多图 URL 数组',
    description  MEDIUMTEXT,
    sale_price   DECIMAL(12,2)  NOT NULL DEFAULT 0,
    market_price DECIMAL(12,2),
    total_stock  INT            NOT NULL DEFAULT 0,
    unit         VARCHAR(20)    DEFAULT '件',
    sales_count  BIGINT         NOT NULL DEFAULT 0,
    sort         INT            NOT NULL DEFAULT 0,
    status       TINYINT        NOT NULL DEFAULT 0 COMMENT '0=草稿 1=上架 2=下架',
    created_at   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_tenant_status (tenant_id, status),
    KEY idx_tenant_category (tenant_id, category_id)
) COMMENT='商品主表';

CREATE TABLE sku (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT         NOT NULL,
    spec_json  JSON           COMMENT '规格键值对 {"颜色":"红色","尺码":"XL"}',
    price      DECIMAL(12,2)  NOT NULL,
    stock      INT            NOT NULL DEFAULT 0,
    code       VARCHAR(100)   COMMENT 'SKU 编码',
    image      VARCHAR(500),
    status     TINYINT        NOT NULL DEFAULT 1,
    KEY idx_product_id (product_id)
) COMMENT='SKU 规格表';

CREATE TABLE spec_template (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id  BIGINT      NOT NULL,
    name       VARCHAR(50) NOT NULL,
    spec_items JSON        COMMENT '[{"name":"颜色","values":["红色","蓝色"]}]'
) COMMENT='规格模板';

-- ─────────────────────────────────────────────────────────────
-- 3. 订单模块 (mall-order)
-- ─────────────────────────────────────────────────────────────

CREATE TABLE `order` (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id          BIGINT         NOT NULL,
    user_id            BIGINT         NOT NULL,
    order_no           VARCHAR(50)    NOT NULL UNIQUE,
    status             VARCHAR(30)    NOT NULL DEFAULT 'PENDING_PAYMENT',
    total_amount       DECIMAL(12,2)  NOT NULL,
    discount_amount    DECIMAL(12,2)  NOT NULL DEFAULT 0,
    freight            DECIMAL(12,2)  NOT NULL DEFAULT 0,
    pay_amount         DECIMAL(12,2)  NOT NULL,
    pay_method         VARCHAR(30),
    pay_time           DATETIME,
    pay_transaction_no VARCHAR(100),
    receiver_name      VARCHAR(50),
    receiver_phone     VARCHAR(20),
    receiver_address   VARCHAR(500),
    logistics_company  VARCHAR(50),
    logistics_no       VARCHAR(100),
    remark             VARCHAR(500),
    created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_tenant_status (tenant_id, status),
    KEY idx_user_id (user_id),
    KEY idx_order_no (order_no)
) COMMENT='订单主表';

CREATE TABLE order_item (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id      BIGINT         NOT NULL,
    product_id    BIGINT,
    sku_id        BIGINT,
    product_name  VARCHAR(200)   NOT NULL,
    product_image VARCHAR(500),
    spec_desc     VARCHAR(200),
    price         DECIMAL(12,2)  NOT NULL,
    quantity      INT            NOT NULL,
    KEY idx_order_id (order_id)
) COMMENT='订单明细';

CREATE TABLE order_timeline (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id    BIGINT      NOT NULL,
    status      VARCHAR(30),
    description VARCHAR(200),
    created_at  DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_order_id (order_id)
) COMMENT='订单状态轨迹';

-- 物流轨迹
CREATE TABLE logistics_track (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id      BIGINT       NOT NULL,
    provider_code VARCHAR(20),
    logistics_no  VARCHAR(100),
    track_data    JSON         COMMENT '[{time,status,location,desc}]',
    last_query_at DATETIME,
    state         VARCHAR(30)  COMMENT 'in_transit/delivered/exception',
    is_sign       TINYINT      NOT NULL DEFAULT 0,
    signed_at     DATETIME,
    KEY idx_order_id (order_id)
) COMMENT='物流轨迹';

CREATE TABLE logistics_provider (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id     BIGINT      NOT NULL,
    code          VARCHAR(20) NOT NULL COMMENT 'SF/YT/YD/ZT/EMS/JD/DB',
    name          VARCHAR(50) NOT NULL,
    api_key       VARCHAR(200),
    api_secret    VARCHAR(200),
    customer_name VARCHAR(100),
    customer_pwd  VARCHAR(100),
    monthly_card  VARCHAR(100),
    send_site     VARCHAR(100),
    send_staff    VARCHAR(50),
    template_url  VARCHAR(500),
    status        TINYINT NOT NULL DEFAULT 1,
    tested_at     DATETIME,
    KEY idx_tenant_id (tenant_id)
) COMMENT='物流服务商配置';

CREATE TABLE waybill (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id     BIGINT      NOT NULL,
    order_id      BIGINT      NOT NULL,
    provider_code VARCHAR(20),
    waybill_no    VARCHAR(100),
    print_data    MEDIUMTEXT  COMMENT '面单打印数据 JSON/Base64',
    print_status  VARCHAR(20) DEFAULT 'pending',
    print_count   INT         NOT NULL DEFAULT 0,
    created_at    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_order_id (order_id)
) COMMENT='电子面单';

CREATE TABLE ship_address (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id     BIGINT      NOT NULL,
    contact_name  VARCHAR(50) NOT NULL,
    contact_phone VARCHAR(20) NOT NULL,
    province      VARCHAR(50),
    city          VARCHAR(50),
    district      VARCHAR(50),
    detail        VARCHAR(200),
    zip_code      VARCHAR(20),
    is_default    TINYINT NOT NULL DEFAULT 0,
    status        TINYINT NOT NULL DEFAULT 1,
    KEY idx_tenant_id (tenant_id)
) COMMENT='发货地址';

-- ─────────────────────────────────────────────────────────────
-- 4. 营销模块 (mall-marketing)
-- ─────────────────────────────────────────────────────────────

CREATE TABLE coupon (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id        BIGINT         NOT NULL,
    name             VARCHAR(100)   NOT NULL,
    type             VARCHAR(30)    NOT NULL COMMENT 'full_reduce/discount/direct',
    threshold_amount DECIMAL(12,2)  DEFAULT 0,
    reduce_amount    DECIMAL(12,2)  DEFAULT 0,
    discount_rate    DECIMAL(4,2)   COMMENT '0.0~1.0',
    total_qty        INT            NOT NULL,
    user_limit       INT            NOT NULL DEFAULT 1,
    valid_days       INT            NOT NULL DEFAULT 7,
    start_time       DATETIME,
    end_time         DATETIME,
    status           VARCHAR(20)    NOT NULL DEFAULT 'active',
    created_at       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_tenant_status (tenant_id, status)
) COMMENT='优惠券模板';

CREATE TABLE user_coupon (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT   NOT NULL,
    coupon_id    BIGINT   NOT NULL,
    status       VARCHAR(20) NOT NULL DEFAULT 'unused',
    used_order_id BIGINT,
    used_at      DATETIME,
    expire_at    DATETIME,
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_user_id (user_id),
    KEY idx_coupon_id (coupon_id)
) COMMENT='用户领券记录';

CREATE TABLE activity (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id  BIGINT      NOT NULL,
    name       VARCHAR(100) NOT NULL,
    type       VARCHAR(30)  NOT NULL COMMENT 'flash_sale/group_buy/points',
    rule_json  JSON,
    start_time DATETIME,
    end_time   DATETIME,
    status     VARCHAR(20)  NOT NULL DEFAULT 'draft',
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_tenant_type (tenant_id, type)
) COMMENT='营销活动';

-- ─────────────────────────────────────────────────────────────
-- 5. 支付模块 (mall-payment)
-- ─────────────────────────────────────────────────────────────

CREATE TABLE payment_config (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id    BIGINT      NOT NULL,
    channel      VARCHAR(30) NOT NULL COMMENT 'wechat/alipay/unionpay/stripe/paypal',
    channel_name VARCHAR(50),
    config_json  TEXT        COMMENT 'AES-256 加密存储',
    enable_test  TINYINT     NOT NULL DEFAULT 0,
    status       VARCHAR(20) NOT NULL DEFAULT 'disabled',
    tested_at    DATETIME,
    last_error   VARCHAR(500),
    created_at   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_tenant_channel (tenant_id, channel)
) COMMENT='支付通道配置';

CREATE TABLE payment_log (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id      BIGINT         NOT NULL,
    order_id       BIGINT         NOT NULL,
    channel        VARCHAR(30),
    amount         DECIMAL(12,2),
    transaction_no VARCHAR(100),
    status         VARCHAR(20)    NOT NULL DEFAULT 'pending',
    raw_notify     MEDIUMTEXT,
    created_at     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_order_id (order_id),
    KEY idx_transaction_no (transaction_no)
) COMMENT='支付记录';

CREATE TABLE refund_log (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id      BIGINT         NOT NULL,
    order_id       BIGINT         NOT NULL,
    payment_log_id BIGINT,
    refund_no      VARCHAR(100),
    amount         DECIMAL(12,2),
    reason         VARCHAR(200),
    status         VARCHAR(20)    NOT NULL DEFAULT 'pending',
    transaction_no VARCHAR(100),
    created_at     DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_order_id (order_id)
) COMMENT='退款记录';

-- ─────────────────────────────────────────────────────────────
-- 初始化数据
-- ─────────────────────────────────────────────────────────────

-- 默认租户
INSERT INTO tenant (id, name, contact_name, contact_email, status)
VALUES (1, 'Demo 商户', '管理员', 'admin@mallforge.com', 1);

-- 超级管理员（tenant_id=NULL）密码: Admin@123 (bcrypt)
INSERT INTO `user` (tenant_id, username, password_hash, real_name, status)
VALUES (NULL, 'superadmin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '超级管理员', 1);

-- 演示商户管理员
INSERT INTO `user` (tenant_id, username, password_hash, real_name, status)
VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '商户管理员', 1);

-- 默认权限
INSERT INTO permission (parent_id, name, code, type) VALUES
(0, '商品管理', 'product:manage', 'menu'),
(0, '订单管理', 'order:manage',   'menu'),
(0, '营销中心', 'marketing:manage', 'menu'),
(0, '店铺装修', 'decorator:manage', 'menu'),
(0, '支付设置', 'payment:manage',  'menu'),
(0, '系统设置', 'system:manage',   'menu');
