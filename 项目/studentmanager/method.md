# 学生成绩管理系统 · 技术文档

> 版本：1.0  
> 技术栈：Java 11 · Swing · SQLite · Apache POI 5.2.5 · Gradle 8.14

---

## 目录

1. [项目概述](#1-项目概述)
2. [技术选型](#2-技术选型)
3. [项目结构](#3-项目结构)
4. [架构设计](#4-架构设计)
5. [数据库设计](#5-数据库设计)
6. [核心模块说明](#6-核心模块说明)
7. [Excel 导入导出实现](#7-excel-导入导出实现)
8. [SQLite 与 SQL Server 对比](#8-sqlite-与-sql-server-对比)
9. [构建与打包](#9-构建与打包)

---

## 1. 项目概述

本系统是一个基于 Java Swing 的桌面端学生成绩管理应用，支持学生信息的增删改查、多条件高级搜索、Excel 批量导入导出、成绩统计分析，以及计算器、文本编辑器等小工具。系统采用单用户本地部署方式，无需额外安装数据库服务器。

---

## 2. 技术选型

| 层次 | 技术 | 说明 |
|------|------|------|
| UI 框架 | Java Swing (JDK 11) | 原生跨平台桌面 GUI，无第三方 UI 库依赖 |
| 数据持久化 | SQLite 3.47.1 + `sqlite-jdbc` | 嵌入式数据库，零配置，单文件存储 |
| Excel 处理 | Apache POI 5.2.5 (`poi-ooxml`) | 读写 `.xlsx` 格式，支持样式设置 |
| 构建工具 | Gradle 8.14 + Shadow Plugin 8.1.1 | 生成包含所有依赖的可执行 Fat JAR |
| 镜像源 | 腾讯云（Gradle）/ 阿里云（Maven） | 解决国内网络访问 gradle.org 失败问题 |

---

## 3. 项目结构

```
studentmanager/
├── build.gradle                    # 构建脚本（依赖、Shadow JAR 配置）
├── settings.gradle                 # pluginManagement 插件镜像
├── gradle/wrapper/
│   └── gradle-wrapper.properties   # 腾讯云 Gradle 8.14 分发镜像
└── src/main/java/com/example/
    ├── Main.java                   # 程序入口，初始化 L&F，启动登录窗口
    ├── Database.java               # SQLite 连接管理，自动建表 & 列迁移
    ├── bean/
    │   ├── StudentBean.java        # 学生实体，带 dirty-flag 机制
    │   └── UserBean.java           # 用户实体
    ├── dao/
    │   ├── StudentDao.java         # 学生 CRUD + 动态搜索 + 导入辅助
    │   └── UserDao.java            # 用户登录 & 密码修改
    └── ui/
        ├── UITheme.java            # 全局样式常量 & 工厂方法
        ├── StudentManagerLogin.java# 登录界面
        ├── FrmMain.java            # 主窗口（表格、工具栏、菜单、导入导出）
        ├── StudentAddDialog.java   # 添加学生对话框
        ├── StudentEditDialog.java  # 编辑学生对话框（学号只读）
        ├── AdvancedSearchDialog.java# 高级多条件搜索
        ├── AdminChangePassword.java# 修改密码（旧密码验证）
        ├── StatisticsDialog.java   # 成绩统计分析对话框
        ├── model/
        │   └── StudentTableModel.java # JTable 的 AbstractTableModel 实现
        └── tools/
            ├── Calculator.java     # 简易计算器（递归下降表达式解析）
            └── Editor.java         # 文本编辑器（查找替换、行号、撤销）
```

---

## 4. 架构设计

系统采用经典的三层架构：

```
┌──────────────────────────────────────────────┐
│                   UI 层                        │
│  FrmMain · Dialogs · UITheme · Tools           │
│  负责用户交互、数据展示、事件响应               │
└──────────────────┬───────────────────────────┘
                   │  调用
┌──────────────────▼───────────────────────────┐
│                  DAO 层                        │
│  StudentDao · UserDao                          │
│  封装 SQL 操作，对 UI 层屏蔽数据库细节          │
└──────────────────┬───────────────────────────┘
                   │  JDBC
┌──────────────────▼───────────────────────────┐
│               数据库层                         │
│  SQLite（sqlite-jdbc 驱动，文件：students.db） │
└──────────────────────────────────────────────┘
```

### Bean 的 Dirty-Flag 机制

`StudentBean` 内部维护 `boolean updated` 字段。所有 setter 方法在值发生变化时将其置为 `true`，`StudentDao.update()` 通过 `isUpdated()` 判断是否真正执行 SQL，避免无意义的数据库写操作。

---

## 5. 数据库设计

### 5.1 连接管理

`Database.java` 采用单例模式维护一个全局 `Connection`，首次调用 `getConnection()` 时自动建库建表：

```java
// 数据库文件存储在 JAR 同级目录下
String url = "jdbc:sqlite:" + new File("students.db").getAbsolutePath();
conn = DriverManager.getConnection(url);
```

### 5.2 表结构

**students 表**

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT | 学号（可由用户指定） |
| `name` | TEXT | NOT NULL | 姓名 |
| `score` | INTEGER | DEFAULT 0 | 分数（0~150） |
| `gender` | TEXT | DEFAULT '' | 性别（男/女/空） |
| `clazz` | TEXT | DEFAULT '' | 班级 |

**users 表**

| 字段 | 类型 | 约束 | 说明 |
|------|------|------|------|
| `id` | INTEGER | PRIMARY KEY AUTOINCREMENT | 用户 ID |
| `username` | TEXT | UNIQUE NOT NULL | 登录账号 |
| `password` | TEXT | NOT NULL | 密码（明文存储，教学项目） |

### 5.3 自动列迁移

`Database.java` 在初始化时执行 `ALTER TABLE ADD COLUMN`，并捕获"列已存在"异常忽略，确保旧数据库文件在添加新字段后仍能正常使用：

```java
for (String col : new String[]{"gender TEXT DEFAULT ''", "clazz TEXT DEFAULT ''"}) {
    try (Statement s = conn.createStatement()) {
        s.execute("ALTER TABLE students ADD COLUMN " + col);
    } catch (SQLException ignored) {}   // 列已存在则忽略
}
```

---

## 6. 核心模块说明

### 6.1 动态搜索（StudentDao.search）

高级搜索通过拼接 SQL 实现多条件组合查询，避免了固定参数导致的 N 个方法重载：

```java
StringBuilder sql = new StringBuilder("SELECT * FROM students WHERE 1=1");
List<Object> params = new ArrayList<>();

if (id != null && !id.isEmpty()) {
    sql.append(" AND CAST(id AS TEXT) LIKE ?");
    params.add("%" + id + "%");
}
if (name != null && !name.isEmpty()) {
    sql.append(fuzzy ? " AND name LIKE ?" : " AND name = ?");
    params.add(fuzzy ? "%" + name + "%" : name);
}
// 分数区间、性别、班级同理...
```

模糊匹配（`LIKE '%keyword%'`）与精确匹配（`=`）通过 `fuzzy` 参数切换。

### 6.2 计算器（递归下降解析）

Calculator 实现了一个完整的递归下降表达式解析器，支持四则运算与括号，语法规则如下：

```
Expr   → Term  { ('+' | '-') Term  }
Term   → Factor { ('*' | '/' | '%') Factor }
Factor → '(' Expr ')' | '-' Factor | Number
Number → [0-9]+ ('.' [0-9]*)?
```

与 `eval()` + `ScriptEngine` 方案相比，此方案无第三方依赖，且能精确控制"除以零"等异常的处理逻辑。

### 6.3 UI 统一样式（UITheme）

所有颜色、字体、边框常量集中定义在 `UITheme`，UI 组件通过工厂方法创建（`createBtn`、`createField` 等），确保全局风格一致。

在 Windows L&F 下，`JTableHeader` 和 `JMenuBar` 的 `setBackground()` 调用会被系统主题覆盖。因此对表头采用了**自定义 `DefaultTableCellRenderer`** 强制绘制背景色：

```java
header.setDefaultRenderer(new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(...) {
        // 直接设置 opaque=true + 自定义背景，绕过 L&F 限制
        setBackground(new Color(220, 230, 245));
        setForeground(Color.BLACK);
        setOpaque(true);
        return this;
    }
});
```

---

## 7. Excel 导入导出实现

### 7.1 导出思路

导出使用 Apache POI 的 `XSSFWorkbook`（OOXML `.xlsx` 格式），核心步骤：

```
1. 创建 XSSFWorkbook + Sheet
2. 定义两种 CellStyle：
   - hStyle（表头）：粗体、居中、浅蓝背景、细边框
   - cStyle（数据）：居中、细边框
3. 写入第 0 行（表头）：学号、姓名、性别、班级、分数
4. 遍历 students 列表，逐行写入（数值列用 setCellValue(double)）
5. 用 FileOutputStream 写出到磁盘
6. try-with-resources 确保 Workbook 正确关闭
```

关键代码片段（样式设置）：

```java
CellStyle hStyle = wb.createCellStyle();
Font hFont = wb.createFont();
hFont.setBold(true);
hFont.setFontHeightInPoints((short) 14);
hStyle.setFont(hFont);
hStyle.setAlignment(HorizontalAlignment.CENTER);
hStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
hStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
setBorderThin(hStyle);   // 四边细线
```

**为什么不用 CSV？**

- CSV 在 Excel 双击打开时存在编码问题（尤其中文），需用户手动指定 UTF-8
- `.xlsx` 格式原生支持 Unicode，样式丰富，开箱即用

### 7.2 导入思路

导入使用 `FileInputStream` 包装后传入 `XSSFWorkbook`（避免 `File` 构造函数抛出受检的 `InvalidFormatException`）：

```
1. 弹出文件选择器（过滤 .xlsx）
2. 弹窗询问重复学号处理策略（覆盖 / 跳过 / 取消）
3. 打开 FileInputStream → XSSFWorkbook → 取第 0 个 Sheet
4. 从第 1 行开始遍历（跳过表头行）：
   a. 跳过 null 行和全空行
   b. 读取各列（cellLong / cellStr / cellInt 工具方法处理类型差异）
   c. 三项验证：学号 > 0、姓名非空、分数 0~150
   d. 查询 StudentDao.exists(id) 决定 insert 或 update
5. 导入完成后 loadAll() 刷新表格，弹出结果汇总
```

**类型兼容处理**：Excel 中数字列可能被存储为 `NUMERIC` 或 `STRING`（取决于单元格格式），工具方法 `cellLong / cellInt / cellStr` 统一处理两种情况：

```java
private static long cellLong(Cell cell) {
    switch (cell.getCellType()) {
        case NUMERIC: return (long) cell.getNumericCellValue();
        case STRING:  return Long.parseLong(cell.getStringCellValue().trim());
        default:      return -1;
    }
}
```

---

## 8. SQLite 与 SQL Server 对比

### 8.1 优劣对比总表

| 维度 | SQLite | SQL Server |
|------|--------|------------|
| **部署方式** | 零配置，JAR 内含驱动，DB 为单个 `.db` 文件 | 需安装服务端、配置实例、开放端口 |
| **运行依赖** | 无（纯 Java 驱动） | 需 SQL Server 服务持续运行 |
| **并发写入** | 数据库级锁，同一时刻只允许一个写操作 | 行级锁，支持高并发多用户同时写入 |
| **数据规模** | 适合百万行以内；单文件最大 281 TB | 可处理数十亿行，企业级数据量 |
| **SQL 功能** | 基础 DDL/DML，不支持存储过程、触发器有限 | 完整 T-SQL：存储过程、触发器、视图、CTE |
| **事务支持** | 支持 ACID，单连接事务 | 完整 ACID，分布式事务（MSDTC） |
| **用户认证** | 无内置认证，依赖应用层 | Windows 身份验证 + SQL 身份验证，角色权限 |
| **网络访问** | 本地文件，不支持网络共享（需应用服务器中转） | 原生 TCP/IP 连接，天然多客户端访问 |
| **备份恢复** | 直接复制 `.db` 文件 | 完整/差异/日志备份，SQL Server Agent 自动化 |
| **费用** | 完全免费开源 | Express 版免费（10 GB 上限）；Standard/Enterprise 需授权 |
| **适用场景** | 桌面端、嵌入式、单用户、低并发 | Web 应用、多用户并发、企业信息系统 |

### 8.2 本项目选用 SQLite 的理由

1. **单用户桌面应用**：本系统每次仅有一名教师操作，无并发写入需求，SQLite 的数据库级锁完全足够。
2. **零部署成本**：打包为 Fat JAR 后，用户双击即可运行，无需安装任何数据库服务，降低使用门槛。
3. **数据迁移方便**：整个数据库是一个 `students.db` 文件，备份只需复制，迁移只需粘贴。
4. **开发效率高**：无需配置连接字符串、创建服务实例、管理权限，`Database.java` 不足 60 行即可完成全部初始化。

### 8.3 若改用 SQL Server 的适用场景

若系统需扩展为以下场景，则应考虑迁移至 SQL Server：

- **多教师并发录入**：多人同时编辑成绩，需要行级锁和事务隔离
- **全校规模部署**：学生数据达到数十万条，需要查询优化器和索引策略
- **与其他系统集成**：教务系统、选课系统通过网络访问同一数据源
- **审计与权限管理**：需要列级权限控制、操作审计日志

**迁移成本评估**：由于 DAO 层完整封装了 SQL 操作，UI 层对数据库无直接依赖，理论上只需修改 `Database.java` 中的连接字符串，并将 SQLite 特有的 `AUTOINCREMENT` 替换为 SQL Server 的 `IDENTITY(1,1)`，即可完成大部分迁移工作。

---

## 9. 构建与打包

### 9.1 依赖配置（build.gradle）

```groovy
plugins {
    id 'java'
    id 'com.github.johnrengelman.shadow' version '8.1.1'
}

repositories {
    maven { url 'https://maven.aliyun.com/repository/central' }
    maven { url 'https://maven.aliyun.com/repository/public' }
    mavenCentral()
}

dependencies {
    implementation 'org.xerial:sqlite-jdbc:3.47.1.0'
    implementation 'org.apache.poi:poi:5.2.5'
    implementation 'org.apache.poi:poi-ooxml:5.2.5'
}
```

### 9.2 构建命令

> 本机系统 Java 为 25，Gradle 8.14 最高支持 Java 24，须指定 JDK 11 路径：

```bash
JAVA_HOME="C:/Program Files/Java/jdk-11" ./gradlew shadowJar --no-daemon
```

### 9.3 输出产物

```
build/libs/studentmanager-1.0-SNAPSHOT.jar   # Fat JAR，约 32 MB，含所有依赖
```

运行方式：

```bash
java -jar build/libs/studentmanager-1.0-SNAPSHOT.jar
```

数据库文件 `students.db` 自动创建于 JAR 同级目录下。
