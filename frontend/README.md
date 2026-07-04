# Frontend — SeckillPro

纯 HTML + 原生 JS 前端，独立于 Spring Boot 后端运行。

## 目录结构

```
frontend/
├── index.html    # 登录 / 注册页
├── home.html     # 秒杀大厅（进行中活动 + 预告）
├── admin.html    # 活动管理（创建秒杀 + 列表）
├── cache-stats.html # 缓存监控（命中率 + 失效消息）
├── orders.html   # 我的订单（订单列表 + 支付/取消）
├── user.html     # 个人中心（余额 + 充值）
└── README.md
```

## 页面总览

| 页面 | 功能 | 需登录 | 对接 API |
|------|------|--------|----------|
| `index.html` | 登录、注册（Tab 切换） | 否 | `POST /api/user/login`, `POST /api/user/register` |
| `home.html` | 秒杀大厅，进行中活动卡片、倒计时、抢购按钮 | 是 | `GET /api/seckill/list/in-progress`, `POST /api/seckill/{id}/doSeckill`, `GET /api/goods/list` |
| `admin.html` | 创建秒杀活动、查看进行中活动列表 | 是 | `POST /api/seckill`, `GET /api/seckill/list/in-progress`, `GET /api/goods/list` |
| `cache-stats.html` | 缓存监控，展示 Caffeine/Redis 命中率、Bloom 拦截、DB 查询和缓存失效消息 | 是 | `GET /api/cache/stats` |
| `orders.html` | 订单列表、立即支付、取消订单（确认弹窗） | 是 | `GET /api/order/list`, `POST /api/order/pay/{id}`, `POST /api/order/cancel/{id}` |
| `user.html` | 个人信息、余额展示、充值弹窗 | 是 | `POST /api/user/recharge` |

## 页面状态覆盖

每个页面均处理以下状态：

- **加载中** — 骨架屏/skeleton placeholder
- **空数据** — 空状态图标 + 引导文案 + 跳转按钮
- **接口错误** — 错误信息展示
- **网络异常** — 提示检查后端服务
- **未登录** — 自动重定向至 `index.html`
- **操作中** — 按钮 loading 态（spinner）

## 启动方式

### 前提：先启动后端

在 IDEA 中运行 `SeckillApplication`，确保后端跑在 `http://localhost:8028`。

---

### 方式一：VSCode Live Server（推荐）

1. 安装 VSCode 插件：**Live Server**（作者 Ritwick Dey）
2. 用 VSCode 打开 `frontend/` 文件夹
3. 右键 `index.html` → **Open with Live Server**
4. 浏览器自动打开 `http://127.0.0.1:5500/index.html`

---

### 方式二：Python http.server

```bash
cd F:\Desktop\albatross\seckilll\frontend
python -m http.server 5500
```

然后访问：`http://localhost:5500/index.html`

---

## 后端 API 地址

每个页面顶部都定义了 API 常量：

```js
const API = 'http://localhost:8028';   // home.html, admin.html, orders.html, user.html
const API_BASE = 'http://localhost:8028'; // index.html
```

如需改为生产地址，修改各文件顶部的常量即可。

## 用户会话

- 登录成功后，`UserLoginVO`（id, phone, nickname, avatar, token, balance）存入 `sessionStorage.userInfo`
- 关闭标签页后自动清除，需重新登录
- 所有需认证的请求在 `Authorization` header 携带 token
- 后端 AuthInterceptor 从 Redis 验证 token（30 分钟过期）

## 注意事项

- 后端 CORS 已配置允许所有来源，跨域请求正常工作
- 导航栏在所有页面间保持一致
- 充值后前端乐观更新 `sessionStorage` 中的余额（下次登录从数据库重新拉取）
- 秒杀下单后端接口为 `POST /api/seckill/{id}/doSeckill`，前端按钮接入在后续正确性修复任务中完成。
