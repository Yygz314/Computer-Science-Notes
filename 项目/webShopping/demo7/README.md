# webShopping demo7 项目说明

## 项目简介

`webShopping demo7` 是一个基于 JSP + Servlet + JavaBean + SQL Server 的网上购物商城项目。项目以“爱尚网扇品”为主题，围绕商品展示、商品分类、商品详情、购物车、订单、留言板和后台管理等功能实现了一套完整的 Web 购物流程。

项目采用传统 Java Web 分层结构开发，前台页面使用 JSP 渲染，业务操作由 Servlet 处理，数据库访问封装在 DAO 层中，实体数据使用 Model/JavaBean 表示。项目最终打包为 WAR 文件，可部署到 Tomcat 10 运行。

## 技术栈

| 类型 | 技术 |
| --- | --- |
| 后端语言 | Java |
| Web 技术 | JSP、Servlet、JSTL 风格脚本片段 |
| Servlet 规范 | Jakarta Servlet 5.0 |
| 构建工具 | Maven |
| 数据库 | SQL Server |
| 数据库驱动 | Microsoft JDBC Driver for SQL Server |
| 安全相关 | BCrypt、CSRF Token、HTML 转义 |
| 部署方式 | WAR 包部署到 Tomcat |
| 前端 | HTML、CSS、JavaScript |

## 主要功能

### 1. 商品浏览

项目提供完整的商品展示能力：

- 首页展示最新商品和轮播图。
- 商品列表页支持分页浏览。
- 商品分类页可按扇子类型筛选商品。
- 商品详情页展示商品图片、价格、销量、点击量、商品参数和详情图。
- 商品点击量会在访问详情页时自动增加。

### 2. 商品搜索

顶部导航栏带有搜索框，用户可以输入关键字搜索商品。搜索结果会显示当前关键字、结果数量，并保留分页能力。

### 3. 购物车

购物车是本项目的核心功能之一，支持：

- 从商品详情页加入购物车。
- 未登录用户也可以使用临时购物车。
- 修改购物车商品数量。
- 移除单个商品。
- 清空购物车。
- 显示商品单价、小计和总金额。
- 登录后可将购物车结算为订单。

### 4. 订单管理

用户登录后可以进入“我的订单”页面查看订单：

- 查看全部订单。
- 按状态筛选订单。
- 支持待支付、已支付、已取消等订单状态。
- 查看订单商品明细。
- 待支付订单支持支付。
- 待支付订单支持取消。

### 5. 用户登录与注册

项目支持用户注册和登录：

- 用户名、密码基础校验。
- 注册后自动登录。
- 登录后在顶部和左侧栏显示用户信息。
- 默认管理员账号会自动初始化。

默认管理员账号：

```text
用户名：admin
密码：admin123
```

### 6. 在线留言板

留言板支持用户和游客提交留言：

- 游客可填写称呼、联系方式和留言内容。
- 登录用户会自动使用当前用户名作为留言人。
- 留言内容在页面展示前会进行 HTML 转义，减少 XSS 风险。
- 管理员可以在后台回复留言、隐藏留言或标记已回复。

### 7. 后台管理

管理员登录后可以进入后台管理页面，主要功能包括：

- 用户角色管理：可将普通用户设置为管理员，也可恢复为普通用户。
- 商品管理：可修改商品名称、售价、原价、销量、分类 ID。
- 图片管理：支持上传商品封面图和详情图。
- 留言管理：可查看用户留言、填写管理员回复、修改留言状态。

### 8. 站点统计和统一顶部布局

顶部区域展示了站点运行信息：

- 当前时间。
- 网站运行时长。
- 当前在线人数。
- 总访客数。
- 总浏览量。

顶部信息使用居中多行布局，避免不同页面出现顶格或字体不一致的问题。

## 项目特色

### 1. 前台购物流程完整

项目不是单纯的静态商品展示，而是实现了“浏览商品 -> 查看详情 -> 加入购物车 -> 修改数量 -> 结算 -> 生成订单 -> 支付/取消订单”的完整购物链路。

### 2. 支持游客购物车

未登录用户也可以先加入购物车，系统会根据 Session 生成临时购物车标识。这样用户可以先浏览和选择商品，真正结算时再登录。

### 3. 后台管理功能较完整

后台不仅能查看数据，还能实际修改用户角色、商品信息和留言状态，并支持商品图片上传，具备基本商城后台的雏形。

### 4. 自动建表和基础数据初始化

项目启动和访问 DAO 时会通过 `DbSchemaManager` 检查并初始化数据库结构，包括用户表、商品表、购物车表、订单表、订单明细表和留言表。首次运行时会自动插入默认商品和默认管理员账号。

### 5. 安全性处理

项目做了多处基础安全处理：

- 表单提交使用 CSRF Token 校验。
- 页面输出使用 `HtmlUtil.escape` 进行 HTML 转义。
- 密码工具类支持 BCrypt 加密校验。
- 后台管理会校验管理员权限。
- 上传图片限制为 JPG、PNG、GIF、WEBP 等图片类型。

### 6. 乱码和布局统一优化

项目页面统一使用 UTF-8，并对容易出现乱码的页面文案进行了整理。公共顶部、商品详情、购物车、订单、后台和留言板页面都重新调整了宽度和布局，减少不同页面之间风格不一致的问题。

## 目录结构

```text
demo7
├── pom.xml
├── README.md
├── src
│   └── main
│       ├── Java
│       │   └── com
│       │       └── webshopping
│       │           ├── controller   # Servlet 控制器
│       │           ├── dao          # 数据库访问层
│       │           ├── db           # 数据库配置和建表初始化
│       │           ├── filter       # 请求统计过滤器
│       │           ├── listener     # 站点统计监听器
│       │           ├── model        # JavaBean/实体类
│       │           ├── stats        # 访问统计
│       │           └── util         # 工具类
│       └── webapp
│           ├── css                  # 页面样式
│           ├── images               # 页面图标和背景图
│           ├── Picture              # 商品图片和详情图
│           ├── WEB-INF
│           │   └── web.xml
│           ├── index.jsp            # 首页
│           ├── product.jsp          # 全部商品
│           ├── sort.jsp             # 分类商品
│           ├── item.jsp             # 商品详情
│           ├── cart_view.jsp        # 购物车
│           ├── orders.jsp           # 我的订单
│           ├── messages.jsp         # 在线留言
│           ├── admin.jsp            # 后台管理
│           ├── login.jsp            # 登录
│           └── reg.jsp              # 注册
└── target                           # Maven 构建输出目录
```

## 核心页面说明

| 页面 | 说明 |
| --- | --- |
| `index.jsp` | 商城首页，包含顶部导航、左侧分类、轮播图和最新商品 |
| `product.jsp` | 全部商品列表，支持搜索和分页 |
| `sort.jsp` | 按分类展示商品 |
| `item.jsp` | 商品详细介绍页，可加入购物车 |
| `cart_view.jsp` | 购物车页面，可更新、移除、清空和结算 |
| `orders.jsp` | 我的订单页面，可筛选、支付、取消订单 |
| `messages.jsp` | 在线留言板 |
| `admin.jsp` | 管理员后台 |
| `login.jsp` | 用户登录 |
| `reg.jsp` | 用户注册 |

## 核心 Servlet 说明

| Servlet | 路径 | 说明 |
| --- | --- | --- |
| `LoginServlet` | `/login` | 处理用户登录 |
| `CartServlet` | `/cart` | 处理购物车新增、更新、删除、清空和结算 |
| `OrderServlet` | `/order` | 处理订单支付和取消 |
| `MessageServlet` | `/message` | 处理留言提交 |
| `AdminServlet` | `/admin` | 处理后台用户、商品、留言管理 |

## 数据库说明

默认数据库连接配置位于：

```text
src/main/Java/com/webshopping/db/DbConfig.java
```

默认连接信息：

```text
数据库类型：SQL Server
数据库名：shopping
地址：localhost:1433
用户名：sa
密码：不在源码中保存，请使用环境变量或 JVM 参数配置
```

项目不会在 GitHub 版本中保存真实数据库密码。运行前请通过环境变量或 JVM 参数配置：

| 配置项 | 环境变量 | JVM 参数 |
| --- | --- | --- |
| 数据库 URL | `JSP_DB_URL` | `jsp.db.url` |
| 数据库用户名 | `JSP_DB_USER` | `jsp.db.user` |
| 数据库密码 | `JSP_DB_PASSWORD` | `jsp.db.password` |

本地配置示例可以参考 `.env.example`，但不要提交包含真实密码的 `.env` 文件。

主要数据表：

- `userinfo`：用户信息和角色。
- `pruduct`：商品信息。
- `cart`：购物车。
- `orders`：订单主表。
- `order_item`：订单明细。
- `message_board`：留言板。

## 本地运行方式

### 1. 准备环境

需要提前安装：

- JDK 8 或以上。
- Maven。
- SQL Server。
- Tomcat 10。

### 2. 确认数据库

确保 SQL Server 已启动，并创建数据库：

```sql
CREATE DATABASE shopping;
```

如果数据库账号、密码和默认配置不一致，需要修改 `DbConfig.java` 或使用环境变量覆盖。

### 3. 打包项目

在项目根目录执行：

```powershell
mvn -DskipTests clean package
```

打包成功后会生成：

```text
target/demo7.war
```

### 4. 部署到 Tomcat

可以将 WAR 包复制到 Tomcat 的 `webapps` 目录。部署示例：

```powershell
Copy-Item "target/demo7.war" "$env:TOMCAT_HOME\webapps\webShopping.war" -Force
```

Tomcat 自动解包后，访问：

```text
http://127.0.0.1:8080/webShopping/
```

## 常用访问地址

| 功能 | 地址 |
| --- | --- |
| 首页 | `http://127.0.0.1:8080/webShopping/` |
| 全部商品 | `http://127.0.0.1:8080/webShopping/product.jsp` |
| 商品详情 | `http://127.0.0.1:8080/webShopping/item.jsp?id=1` |
| 购物车 | `http://127.0.0.1:8080/webShopping/cart_view.jsp` |
| 我的订单 | `http://127.0.0.1:8080/webShopping/orders.jsp` |
| 留言板 | `http://127.0.0.1:8080/webShopping/messages.jsp` |
| 登录 | `http://127.0.0.1:8080/webShopping/login.jsp` |
| 注册 | `http://127.0.0.1:8080/webShopping/reg.jsp` |
| 后台管理 | `http://127.0.0.1:8080/webShopping/admin.jsp` |

## 测试情况

本项目已完成以下基础验证：

- Maven 构建成功。
- WAR 包可部署到 Tomcat。
- 首页、商品页、商品详情、购物车、订单、留言板、登录、注册、后台页面可正常访问。
- 商品加入购物车、修改数量、移除商品流程可用。
- 页面响应中未发现明显乱码。
- 统一顶部布局和主要页面面板宽度已修复。

