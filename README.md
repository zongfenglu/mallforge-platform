# MallForge Platform

> 多租户 SaaS 商城平台 · Spring Boot 3 + Vue 3 + uni-app

---

## 快速开始

### 前提条件

- JDK 17+
- Node.js 20+
- Docker & Docker Compose
- Maven 3.9+

### 一键启动（Docker Compose）

```bash
# 1. 复制并填写环境变量
cp .env.example .env

# 2. 启动所有服务
docker-compose up -d

# 3. 查看服务状态
docker-compose ps
```

服务启动后：
- 管理后台：http://localhost:3000
- API 网关：http://localhost:8080
- 默认账号：admin / Admin@123

---

## 项目结构

```
mallforge-platform/
├── admin-web/           # Vue 3 + Nuxt 3 管理后台
├── miniapp/             # uni-app 微信小程序
├── server/              # Spring Boot 多模块后端
│   ├── mall-common/     # 公共模块（Result、Exception、TenantContext）
│   ├── mall-framework/  # 框架模块（JWT、MyBatis 多租户拦截器）
│   ├── mall-gateway/    # API 网关（路由、JWT鉴权过滤器）
│   ├── mall-system/     # 系统模块（租户、用户、RBAC、店铺装修）
│   ├── mall-product/    # 商品模块（分类、商品、SKU）
│   ├── mall-order/      # 订单模块（下单、发货、物流）
│   ├── mall-marketing/  # 营销模块（优惠券、活动）
│   └── mall-payment/    # 支付模块（多通道配置、预支付、回调）
├── docker/              # SQL 初始化、nginx 配置
├── docker-compose.yml   # 服务编排
└── .env.example         # 环境变量模板
```

---

## 核心架构

### 多租户隔离

```
HTTP Request (JWT)
  → Gateway 解析 JWT → 提取 tenant_id / user_id
  → 写入下游请求 Header (X-Tenant-Id / X-User-Id)
  → TenantContextFilter 写入 ThreadLocal
  → MyBatis TenantLineInnerInterceptor 自动追加 WHERE tenant_id = ?
```

### 端口分配

| 服务          | 端口  |
|---------------|-------|
| nginx         | 80    |
| mall-gateway  | 8080  |
| mall-system   | 8081  |
| mall-product  | 8082  |
| mall-order    | 8083  |
| mall-marketing| 8084  |
| mall-payment  | 8085  |
| admin-web     | 3000  |
| MySQL         | 3306  |
| Redis         | 6379  |

---

## 本地开发

### 后端

```bash
cd server

# 启动基础设施
docker-compose up -d mysql redis

# 单独启动某个服务（IDEA 或命令行）
cd mall-system
mvn spring-boot:run
```

### 管理后台

```bash
cd admin-web
npm install
npm run dev       # http://localhost:3000
```

### 微信小程序

```bash
cd miniapp
npm install
npm run dev:mp-weixin
# 用微信开发者工具打开 dist/dev/mp-weixin
```

---

## 支付通道接入

在管理后台 → 支付设置 中配置各通道参数：

| 通道   | 所需参数 |
|--------|---------|
| 微信   | AppID、MchId、API v3 密钥、私钥证书 |
| 支付宝 | AppID、应用私钥、支付宝公钥 |
| Stripe | PublishableKey、SecretKey、WebhookSecret |
| PayPal | ClientId、ClientSecret、WebhookId |
| 银联   | 商户号、签名证书 |

所有敏感参数使用 AES-256 加密后存入数据库。

---

## 店铺装修

管理后台 → 店铺装修：

1. 从左侧组件库拖拽组件到手机预览区
2. 点击组件在右侧编辑属性
3. 支持撤销/重做（最多20步）
4. 点击「保存草稿」或「发布上线」
5. 小程序端自动拉取已发布的 JSON 进行动态渲染

---

## 开发顺序建议

1. 基础底座 → 2. 商品 → 3. 订单 → 4. 支付 → 5. 营销 → 6. 管理后台 → 7. 小程序 → 8. 部署

---

## 技术栈

| 层 | 技术 |
|----|------|
| 管理后台 | Vue 3 · Nuxt 3 · Element Plus · Tailwind CSS · Pinia |
| 小程序 | uni-app (Vue 3) · uView Plus 3 |
| 后端 | Java 17 · Spring Boot 3.2 · MyBatis Plus 3.5 |
| 网关 | Spring Cloud Gateway |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis 7 |
| 部署 | Docker Compose |
