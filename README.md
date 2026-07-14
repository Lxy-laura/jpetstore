# JPetStore

一个基于 Spring Boot 的宠物商店电子商务系统，提供完整的商品浏览、购物车管理、订单处理等功能。

## 技术栈

- **框架**: Spring Boot 3.5.1
- **语言**: Java 21
- **数据库**: MySQL 8.0+
- **ORM**: MyBatis 3.0.4
- **前端模板**: Thymeleaf
- **构建工具**: Maven 3.9+

## 项目结构

```
jpetstore/
├── src/main/java/com/jpetstore/
│   ├── common/          # 通用组件（异常处理、结果封装、拦截器）
│   ├── config/          # 配置类（MyBatis、Web MVC）
│   ├── controller/      # REST API 控制器
│   ├── domain/          # 实体类（Account、Product、Order 等）
│   ├── mapper/          # MyBatis Mapper 接口
│   ├── service/         # 业务逻辑层
│   └── JPetStoreApplication.java
├── src/main/resources/
│   ├── mapper/          # MyBatis XML 映射文件
│   ├── sql/             # 数据库初始化脚本
│   ├── static/          # 静态资源（CSS）
│   ├── templates/       # Thymeleaf 模板
│   ├── application.yml  # 应用配置
│   └── application.properties
├── src/test/            # 单元测试
└── pom.xml
```

## 核心功能

### 用户模块
- 用户注册与登录
- 用户信息管理（查看、修改、删除）
- 会话管理

### 商品模块
- 商品分类浏览（鱼类、犬类、猫类、爬行动物、鸟类）
- 商品搜索
- 商品详情查看
- 库存管理

### 购物车模块
- 添加商品到购物车
- 修改购物车数量
- 删除购物车商品
- 清空购物车

### 订单模块
- 创建订单
- 订单列表查看
- 订单详情
- 订单状态管理

### 管理员模块
- 用户管理
- 商品管理（增删改查）
- 订单管理

## 快速开始

### 环境要求

- JDK 21+
- MySQL 8.0+
- Maven 3.9+

### 1. 数据库配置

创建数据库并初始化数据：

```sql
-- 创建数据库
CREATE DATABASE jpetstore DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 执行初始化脚本
mysql -u root -p jpetstore < src/main/resources/sql/schema.sql
```

### 2. 修改配置

编辑 `src/main/resources/application.yml`，配置数据库连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jpetstore?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_password
```

### 3. 运行项目

```bash
# 开发模式运行
./mvnw spring-boot:run

# 或打包后运行
./mvnw clean package
java -jar target/jpetstore-1.0.0.jar
```

### 4. 访问应用

启动后访问：http://localhost:8080

## API 接口

### 用户接口
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/account/login` | 用户登录 |
| POST | `/api/account/register` | 用户注册 |
| GET | `/api/account/current` | 获取当前用户 |
| GET | `/api/account/{userid}` | 获取用户信息 |
| PUT | `/api/account/{userid}` | 更新用户信息 |
| DELETE | `/api/account/{userid}` | 删除用户 |
| POST | `/api/account/logout` | 用户登出 |

### 商品接口
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/products` | 获取所有商品 |
| GET | `/api/products/category/{category}` | 按分类获取商品 |
| GET | `/api/products/search?keyword=` | 搜索商品 |
| GET | `/api/products/{productid}` | 获取商品详情 |
| POST | `/api/products` | 创建商品 |
| PUT | `/api/products/{productid}` | 更新商品 |
| DELETE | `/api/products/{productid}` | 删除商品 |

### 购物车接口
| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/cart` | 获取购物车 |
| POST | `/api/cart/add` | 添加商品到购物车 |
| PUT | `/api/cart/update` | 更新购物车数量 |
| DELETE | `/api/cart/remove/{itemid}` | 删除购物车商品 |
| DELETE | `/api/cart/clear` | 清空购物车 |

### 订单接口
| 方法 | 路径 | 描述 |
|------|------|------|
| POST | `/api/orders` | 创建订单 |
| GET | `/api/orders` | 获取当前用户订单列表 |
| GET | `/api/orders/{orderid}` | 获取订单详情 |
| PUT | `/api/orders/{orderid}/status` | 更新订单状态 |

## 测试账户

| 用户名 | 密码 | 角色 |
|--------|------|------|
| j2ee | j2ee | 用户 |
| ACID | ACID | 用户 |

## 运行测试

```bash
# 运行所有测试
./mvnw test

# 运行特定测试
./mvnw test -Dtest=AccountControllerTest

# 生成测试覆盖率报告
./mvnw jacoco:report
```

## 项目特性

- ✅ RESTful API 设计
- ✅ 基于 Session 的用户认证
- ✅ 输入参数校验（Validation）
- ✅ 全局异常处理
- ✅ 统一响应格式（Result）
- ✅ 完整的单元测试覆盖
- ✅ 代码覆盖率报告（JaCoCo）
