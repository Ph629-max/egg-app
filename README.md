# 鸡蛋库存与订单管理系统（egg-app）

> Java Swing + MySQL + Redis 的蛋品库存 / 订单一体化管理系统

## 📖 项目简介

`egg-app` 是一个面向蛋品贸易场景的**桌面端库存与订单管理系统**。系统以 Java Swing 构建界面，通过 JDBC 访问 MySQL 存储业务数据，并使用 Redis 缓存**分拣规则**；同时提供**安全库存预警、邮件通知、数据导出**等能力。

## ✨ 功能特性

- **库存管理**：库存项维护（数量、安全库存、补货点、成本价、包装类型等）。
- **分拣规则**：按蛋品类型配置包装方式、破损率、包装成本，规则加载后缓存至 Redis，并支持**规则变更监听**自动刷新。
- **订单管理**：采购订单 / 销售订单的创建与流转（订单类型、订单状态、订单流水）。
- **预警通知**：库存低于安全库存时触发警报，并通过通知服务（邮件）推送。
- **数据导出**：业务数据导出（`DataExporter`）。
- **日志**：统一日志工具 `LogUtil`。

## 🛠 技术栈

- **语言**：Java（Swing 桌面应用）
- **数据库**：MySQL（JDBC）
- **缓存**：Redis（Jedis）
- **JSON**：Jackson（`ObjectMapper`）
- **框架注解**：Spring（`@Service` / `@Autowired`）

## 📂 目录结构

```
egg-app/
└── eggapp/
    ├── MainApplication.java        # 程序入口（Swing 启动）
    ├── config/RedisConfig.java     # Redis 配置
    ├── dao/DatabaseHelper.java     # 数据库访问
    ├── model/                      # 实体：InventoryItem、Order、OrderStatus、
    │                               #       OrderType、PurchaseOrder、SalesOrder、
    │                               #       OrderTransaction、SortingRule
    ├── service/                    # InventoryManager、OrderService(+Impl)、
    │                               #   SortingRuleManager、AlertService
    ├── ui/                         # InventorySystem、InventoryPanel、
    │                               #   DataViewDialog、SortingRuleDialog
    └── utils/                      # DataExporter、LogUtil
```

## 🚀 运行准备

1. **数据库**：创建 MySQL 库并建表，至少包含库存表、订单表与分拣规则表
   （`sorting_rules` 字段示例：`egg_type_id`、`packaging_type`、`damage_ratio`、`packing_cost`）。
2. **Redis**：启动 Redis 服务，按需修改 `RedisConfig`。
3. **连接配置**：在 `DatabaseHelper` 中填写 JDBC URL、用户名与密码。
4. **依赖**：仓库未包含构建文件，请在 IDE 中手动引入
   `mysql-connector-java`、`jedis`、`jackson-databind`、`spring-context` 等依赖后编译运行 `MainApplication`。

## ⚠️ 说明

- 本仓库为源码快照，**未附带 `pom.xml` / `build.gradle`**；如需完整构建，建议补充 Maven 配置。
- `MainApplication` 引用了部分界面与通知类，若仓库中缺失（如 `MainFrame`、`NotificationService` / `EmailNotificationService`），请按接口自行补齐。
