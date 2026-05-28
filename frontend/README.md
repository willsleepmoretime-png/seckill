# Frontend — SeckillPro

纯 HTML + 原生 JS 前端，独立于 Spring Boot 后端运行。

## 目录结构

```
frontend/
├── index.html   # 登录 / 注册页
├── home.html    # 秒杀大厅（商品列表）
└── README.md
```

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

`index.html` 和 `home.html` 顶部都定义了：

```js
const API_BASE = 'http://localhost:8028';
```

如需改为生产地址，只修改这一个常量即可。

## 注意事项

- 后端 CORS 已配置允许所有来源，跨域请求正常工作
- 用户登录信息存储在 `sessionStorage`，关闭标签页后自动清除
