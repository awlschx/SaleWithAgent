# 接口清单与后端开发指南

> 配套前端：`src/main/resources/static/`（拾光百货，Vue3 无构建版）
> 后端：`com.chx.saleswithagent`，Spring Boot 4.0.9 / Java 21
> 当前进度：仅 `GET /api/products` 已打通，其余功能仍在前端 localStorage。

---

## 1. 全局约定（先定，后面不用返工）

### 1.1 统一响应体

```json
{ "code": 0, "message": "ok", "data": { } }
```

| code | 含义 |
|---|---|
| 0 | 成功 |
| 40000 | 参数错误 |
| 40100 | 未登录 |
| 40300 | 无权限 |
| 40400 | 资源不存在 |
| 40900 | 业务冲突（库存不足 / 重复领取） |
| 50000 | 系统异常 |

> 建议把现有的 `/api/products` 也包一层，前端 `api.js` 里改成取 `body.data` 即可（3 行改动）。

### 1.2 身份

- **第一阶段（推荐先做）**：固定演示用户 `userId = 1`，拦截器/参数解析器直接注入，前端零改动；
- **第二阶段**：加登录注册 + JWT，前端加登录页；
- 任何接口**永远不信任前端传的 userId**，只从令牌或服务端会话取。

### 1.3 其他约定

- 路径统一 `/api/**`；静态页面由 Spring Boot 直接托管，同源无跨域；
- 金额：数据库 `DECIMAL(10,2)`，Java `BigDecimal`，JSON 输出数字；
- 时间：`LocalDateTime`，JSON 输出 ISO 字符串；
- 分页参数：`page`（从 1 开始）、`size`；
- 写操作支持幂等：下单/领券带 `Idempotency-Key` 请求头（第二阶段加）。

---

## 2. 前端功能 → 后端接口映射

### 2.1 首页（HomeView）

| 前端动作 | 方法 | 路径 | 请求 | 返回 | 备注 |
|---|---|---|---|---|---|
| 加载商品 | GET | `/api/products` | `cat`,`q`,`sort=def\|sales\|price` | `List<Product>` | 已实现；建议加查询参数，前端改为服务端筛选 |
| 分类列表 | GET | `/api/categories` | — | `[{name, desc}]` | 可先写死，后续做成表 |
| 轮播图 | GET | `/api/banners` | — | `[{title, desc, buttonText, bgColor, category}]` | 可选，先写死 |
| 秒杀商品 | GET | `/api/products/flash` | — | `List<Product>` | 或列表接口带 `flash=true` |
| 领券中心 | GET | `/api/coupons/available` | — | `List<CouponTemplate>` | 模板列表 |
| 领取优惠券 | POST | `/api/coupons/{templateId}/claim` | — | `UserCoupon` | 重复领取返回 40900 |
| 我的订单/地址/券数量 | GET | `/api/me/summary` | — | `{orderCount, addressCount, couponCount}` | 可选，用于首页右侧卡片 |

### 2.2 商品详情（Detail）

| 前端动作 | 方法 | 路径 | 请求 | 返回 |
|---|---|---|---|---|
| 商品详情 | GET | `/api/products/{id}` | — | `Product`（含库存） |
| 规格选项 | GET | `/api/products/{id}/specs` | — | `["黑色 / M", ...]` |
| 评价列表 | GET | `/api/products/{id}/reviews` | `filter=all\|good\|mid\|bad` | `List<Review>` |
| 评价汇总 | GET | `/api/products/{id}/reviews/summary` | — | `{avg, goodRate, total}` |
| 收藏 / 取消收藏 | POST | `/api/favorites/{productId}/toggle` | — | `{favorited: bool}` |
| 我的收藏 | GET | `/api/favorites` | — | `List<Product>` |
| 加入购物车 | POST | `/api/cart/items` | `{productId, spec, quantity}` | `CartService` |
| 立即购买 | — | 直接走 `POST /api/orders` | 见 2.4 | — |

### 2.3 购物车（CartDrawer）

| 前端动作 | 方法 | 路径 | 请求 | 返回 |
|---|---|---|---|---|
| 购物车列表 | GET | `/api/cart` | — | `CartService` |
| 加入购物车 | POST | `/api/cart/items` | `{productId, spec, quantity}` | `CartService` |
| 修改数量 | PUT | `/api/cart/items/{itemId}` | `{quantity}` | `CartService` |
| 勾选 / 取消勾选 | PUT | `/api/cart/items/{itemId}/selected` | `{selected}` | `CartService` |
| 全选 / 取消全选 | PUT | `/api/cart/selected` | `{selected}` | `CartService` |
| 删除一件 | DELETE | `/api/cart/items/{itemId}` | — | `CartService` |
| 删除选中 | DELETE | `/api/cart/selected` | — | `CartService` |
| 购物车数量角标 | GET | `/api/cart/count` | — | `{count}` |

**Cart 返回结构**（前端已经按这个形状用）：

```json
{
  "items": [
    { "id": 1, "productId": 3, "name": "无线蓝牙耳机…", "spec": "标准版",
      "price": 169, "quantity": 2, "selected": true, "stock": 215,
      "subtotal": 338 }
  ],
  "totalQuantity": 2,
  "selectedAmount": 338
}
```

**合并规则**：同一 `productId + spec` 视为同一条，数量累加，上限 99。

### 2.4 结算与下单（Checkout）

| 前端动作 | 方法 | 路径 | 请求 | 返回 |
|---|---|---|---|---|
| 地址列表 | GET | `/api/addresses` | — | `List<Address>` |
| 新增地址 | POST | `/api/addresses` | `{name, phone, region, detail, isDefault}` | `AddressService` |
| 可用优惠券 | GET | `/api/coupons/usable` | `?items=productId:spec:qty,...` 或 POST 传 items | `List<UserCoupon>` |
| 金额预览（可选） | POST | `/api/orders/preview` | `{addressId, couponId, items[]}` | `{itemAmount, shipFee, discount, payAmount}` |
| 提交订单 | POST | `/api/orders` | `{addressId, couponId, items[], source: "cart"\|"buyNow"}` | `Order` |

**下单必须由服务端完成的事**（前端算的金额一概不认）：

1. 校验商品存在、上架、库存足够；
2. 校验券属于当前用户、未使用、未过期、品类匹配、门槛满足；
3. 重新计算：`itemAmount`、`shipFee`（满 99 包邮，否则 8）、`discount`、`payAmount`；
4. 扣库存（条件 UPDATE，见第 5 节）；
5. 写 `orders` + `order_items`（价格、名称做快照）；
6. 标记券 `used=1`；
7. `source=cart` 时删除购物车中已选中的条目；
8. 以上在同一事务内，失败整体回滚。

### 2.5 我的订单（OrderPanel）

| 前端动作 | 方法 | 路径 | 请求 | 返回 |
|---|---|---|---|---|
| 订单列表 | GET | `/api/orders` | `status=全部\|待发货\|待收货\|已完成\|已取消` | `List<Order>` |
| 订单详情 | GET | `/api/orders/{orderNo}` | — | `Order` |
| 取消订单 | POST | `/api/orders/{orderNo}/cancel` | — | `Order` |
| 模拟发货 | POST | `/api/orders/{orderNo}/ship` | — | `Order` |
| 确认收货 | POST | `/api/orders/{orderNo}/receive` | — | `Order` |
| 再次购买 | POST | `/api/cart/reorder/{orderNo}` | — | `CartService` |
| 申请售后（接 Agent） | POST | `/api/orders/{orderNo}/after-sale` | `{reason}` | `{ticketId, status}` |

**状态机**：`待发货 → 待收货 → 已完成`，`待发货` 可取消；取消时**退券**。

### 2.6 收货地址（AddressPanel）

| 前端动作 | 方法 | 路径 | 请求 | 返回 |
|---|---|---|---|---|
| 地址列表 | GET | `/api/addresses` | — | `List<Address>` |
| 新增 | POST | `/api/addresses` | `{name, phone, region, detail, isDefault}` | `AddressService` |
| 编辑 | PUT | `/api/addresses/{id}` | 同上 | `AddressService` |
| 删除 | DELETE | `/api/addresses/{id}` | — | `{ok: true}` |
| 设为默认 | PUT | `/api/addresses/{id}/default` | — | `List<Address>` |

规则：每个用户最多 20 条；设默认时把其他条置 0；删除默认后自动选第一条为默认；手机号 11 位校验。

### 2.7 优惠券（CouponPanel）

| 前端动作 | 方法 | 路径 | 请求 | 返回 |
|---|---|---|---|---|
| 我的券 | GET | `/api/coupons/mine` | `status=ok\|dead\|all` | `List<UserCoupon>` |
| 领券中心 | GET | `/api/coupons/available` | — | `List<CouponTemplate>` |
| 领取 | POST | `/api/coupons/{templateId}/claim` | — | `UserCoupon` |

可用规则（服务端判定）：`used=0` 且 `expire_at > now` 且（券无品类或订单中该品类金额 ≥ 门槛）。

### 2.8 认证（第二阶段）

| 方法 | 路径 | 请求 | 返回 |
|---|---|---|---|
| POST | `/api/auth/register` | `{phone, password, nickname}` | `{token, userId, nickname}` |
| POST | `/api/auth/login` | `{phone, password}` | `{token, userId, nickname}` |
| GET | `/api/auth/me` | Header `Authorization: Bearer` | `{userId, nickname, phone}` |
| POST | `/api/auth/logout` | — | `{ok: true}` |

---

## 3. 数据表设计（MySQL 草案）

```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  phone VARCHAR(20) NOT NULL UNIQUE,
  password_hash VARCHAR(128) NOT NULL,
  nickname VARCHAR(64) NOT NULL DEFAULT '',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(256) NOT NULL,
  category VARCHAR(32) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  original_price DECIMAL(10,2) NOT NULL,
  sales BIGINT NOT NULL DEFAULT 0,
  hue INT NOT NULL DEFAULT 0,
  flash_price DECIMAL(10,2) NOT NULL DEFAULT 0,
  flash_percent INT NOT NULL DEFAULT 0,
  stock INT NOT NULL DEFAULT 0,
  status VARCHAR(16) NOT NULL DEFAULT 'ON_SALE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_category (category)
);

CREATE TABLE product_specs (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_id BIGINT NOT NULL,
  spec VARCHAR(64) NOT NULL,
  KEY idx_product (product_id)
);

CREATE TABLE cart_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  spec VARCHAR(64) NOT NULL,
  quantity INT NOT NULL,
  selected TINYINT NOT NULL DEFAULT 1,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_product_spec (user_id, product_id, spec)
);

CREATE TABLE addresses (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  name VARCHAR(64) NOT NULL,
  phone VARCHAR(20) NOT NULL,
  region VARCHAR(128) NOT NULL,
  detail VARCHAR(256) NOT NULL,
  is_default TINYINT NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_user (user_id)
);

CREATE TABLE coupon_templates (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(64) NOT NULL,
  min_amount DECIMAL(10,2) NOT NULL,
  off_amount DECIMAL(10,2) NOT NULL,
  category VARCHAR(32) NULL,          -- NULL = 全场通用
  valid_days INT NOT NULL DEFAULT 7,
  stock INT NOT NULL DEFAULT 0,
  status VARCHAR(16) NOT NULL DEFAULT 'ON'
);

CREATE TABLE user_coupons (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  template_id BIGINT NOT NULL,
  title VARCHAR(64) NOT NULL,
  min_amount DECIMAL(10,2) NOT NULL,
  off_amount DECIMAL(10,2) NOT NULL,
  category VARCHAR(32) NULL,
  used TINYINT NOT NULL DEFAULT 0,
  expire_at DATETIME NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_user_template (user_id, template_id),
  KEY idx_user_used (user_id, used)
);

CREATE TABLE orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(32) NOT NULL UNIQUE,
  user_id BIGINT NOT NULL,
  status VARCHAR(16) NOT NULL DEFAULT '待发货',
  item_amount DECIMAL(10,2) NOT NULL,
  ship_fee DECIMAL(10,2) NOT NULL,
  discount DECIMAL(10,2) NOT NULL,
  pay_amount DECIMAL(10,2) NOT NULL,
  address_snapshot VARCHAR(512) NOT NULL,
  user_coupon_id BIGINT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  ship_time DATETIME NULL,
  receive_time DATETIME NULL,
  KEY idx_user_status (user_id, status)
);

CREATE TABLE order_items (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  product_name VARCHAR(256) NOT NULL,
  spec VARCHAR(64) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  quantity INT NOT NULL,
  KEY idx_order (order_id)
);

CREATE TABLE favorites (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  UNIQUE KEY uk_user_product (user_id, product_id)
);

CREATE TABLE reviews (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  rating TINYINT NOT NULL,
  content VARCHAR(512) NOT NULL,
  spec VARCHAR(64) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_product (product_id)
);
```

---

## 4. 后端实现顺序（建议 7 天）

| 天 | 任务 | 产出 |
|---|---|---|
| D1 上午 | 通用层：`ApiResponse`、`BizException`、`GlobalExceptionHandler`、错误码 | 所有接口统一返回格式 |
| D1 下午 | 用户体系：先固定 `userId=1`（`CurrentUser` 工具类），或直接做登录 | 有"当前用户"概念 |
| D2 | 商品：查询参数（分类/搜索/排序/分页）+ 库存字段 + 分类/规格/评价 | `/api/products` 完整 |
| D3 | 购物车：增删改查、合并规则、选中、数量角标 | 前端购物车可用 |
| D4 | 地址 + 收藏 | 结算前置条件齐 |
| D5 | 优惠券：模板、领取、我的、可用判定 | 结算能选券 |
| D6 | 订单：预览、下单（事务/扣库存/用券/清购物车）、状态机 | 全链路闭环 |
| D7 | MySQL 持久化 + 联调 + 异常场景测试 | 重启不丢数据 |

**先内存、后数据库**：D1–D6 用 `Map<Long, ...>` 或内存 `List` 实现，接口完整跑通、前端联调通过后，D7 再替换成 MySQL。这样能保证"每天都有一点可演示的东西"，不会卡在建表上。

---

## 5. 必须由服务端实现的规则（前端算的一概不认）

1. **价格**：从数据库取，前端只传 `productId/spec/quantity`；
2. **库存**：`UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?`，影响行数为 0 就抛"库存不足"；
3. **优惠券**：归属、未使用、未过期、品类匹配、门槛按**符合品类**的金额计算；
4. **运费**：商品金额 ≥ 99 免运费，否则 8；
5. **金额**：`payAmount = itemAmount + shipFee - discount`，不允许为负；
6. **下单事务**：扣库存 + 写订单 + 写明细 + 核销券 + 清购物车，全成功或全回滚；
7. **状态机**：只能 `待发货→待收货→已完成`，取消仅限 `待发货`，非法流转返回 40900；
8. **幂等**：同一 `Idempotency-Key` 重复提交只生成一笔订单；
9. **归属校验**：订单、地址、券都必须校验 `user_id`，防止越权。

---

## 6. 包结构与类清单

```
com.chx.saleswithagent
├── common/
│   ├── ApiResponse.java
│   ├── BizException.java
│   ├── GlobalExceptionHandler.java
│   └── CurrentUser.java          // 第一阶段返回固定 userId=1
├── product/                      // 已有
│   ├── Product.java
│   ├── ProductService.java
│   └── ProductController.java
├── cart/
│   ├── CartItem.java
│   ├── CartService.java
│   ├── CartController.java
│   └── dto/CartView.java
├── address/ …
├── coupon/ …
├── order/ …
└── favorite/ …
```

---

## 7. 前端需要配合的改动（后端写好后）

1. `js/api.js`：给每个接口加一个方法（`getCart`、`addCart`、`submitOrder`…）；
2. `js/store.js`：`CartService`、`CouponsService`、`Addr`、`Store.orders` 从"直接改数组"改成 `await API.xxx()`，本地只保留 UI 状态；
3. `js/app.js`：启动时并行拉商品 + 购物车 + 地址 + 券；
4. `js/components/Checkout.js`：提交订单改为调接口，不再自己算金额；
5. 第二阶段加登录页和 `Authorization` 头。

---

## 8. Spring Boot 4.0.9 注意事项

1. **Jackson 3**：包名是 `tools.jackson.databind.*`，不是 `com.fasterxml.jackson.*`；
2. **Web starter 名**：`spring-boot-starter-webmvc`（你的 pom 已经是对的）；
3. **MyBatis 兼容性**：`mybatis-spring-boot-starter` 对 Boot 4 的支持可能滞后，若报错就别硬上，改用：
   - `spring-boot-starter-jdbc` + `JdbcTemplate`（最稳），或
   - `spring-boot-starter-data-jdbc`（Boot 官方维护，Boot 4 兼容性最好）；
4. **MySQL 连接**：加 `spring-boot-starter-jdbc` 后必须配 `spring.datasource.*`，否则启动失败；
5. **验证**：`spring-boot-starter-validation` 已引入，DTO 上直接用 `@NotBlank`、`@Pattern`。

---

## 9. 联调验收清单（curl）

```bash
# 商品
curl http://localhost:8080/api/products
curl http://localhost:8080/api/products/3

# 购物车
curl -X POST http://localhost:8080/api/cart/items \
  -H "Content-Type: application/json" \
  -d '{"productId":3,"spec":"标准版","quantity":2}'
curl http://localhost:8080/api/cart

# 地址
curl -X POST http://localhost:8080/api/addresses \
  -H "Content-Type: application/json" \
  -d '{"name":"张三","phone":"13800000000","region":"浙江省 杭州市 西湖区","detail":"文三路 100 号","isDefault":true}'

# 优惠券
curl http://localhost:8080/api/coupons/available
curl -X POST http://localhost:8080/api/coupons/1/claim
curl http://localhost:8080/api/coupons/mine

# 下单
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: test-001" \
  -d '{"addressId":1,"couponId":null,"items":[{"productId":3,"spec":"标准版","quantity":2}],"source":"cart"}'

# 订单状态机
curl -X POST http://localhost:8080/api/orders/{orderNo}/ship
curl -X POST http://localhost:8080/api/orders/{orderNo}/receive
```

**每天收工前的验收标准**：新写的接口用 curl 能过，且前端对应页面点击不报错。
