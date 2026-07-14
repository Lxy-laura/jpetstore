# JPetStore 宠物商店系统 - 测试方案与测试用例

## 一、项目测试架构概览

### 1.1 技术栈

| 分类 | 技术 | 版本 |
|------|------|------|
| 语言 | Java | 21 |
| 框架 | Spring Boot | 3.5.1 |
| ORM | MyBatis | 3.0.4 |
| 数据库 | MySQL（生产）/ H2（测试） | - |
| 测试框架 | Spring Boot Test + MockMvc | - |
| Mock工具 | Mockito | - |
| 代码覆盖率 | JaCoCo | 0.8.11 |

### 1.2 测试层次

```
┌─────────────────────────────────────────────────────┐
│                  E2E 端到端测试                       │
├─────────────────────────────────────────────────────┤
│              集成测试 (Controller层)                  │
│     @WebMvcTest + MockMvc + Mockito Mock            │
├─────────────────────────────────────────────────────┤
│              单元测试 (Service/Mapper层)              │
│     @Test + Mockito Mock                            │
├─────────────────────────────────────────────────────┤
│              单元测试 (Domain层)                      │
│     @Test 纯对象测试                                 │
└─────────────────────────────────────────────────────┘
```

### 1.3 测试运行方式

```bash
# 运行所有测试
./mvnw test

# 运行指定模块测试
./mvnw test -Dtest=AccountControllerTest

# 生成覆盖率报告
./mvnw jacoco:report
# 报告位置: target/site/jacoco/index.html
```

---

## 二、接口测试详解

### 2.1 接口测试方法

项目使用 **MockMvc** 进行接口测试，这是一种"模拟HTTP请求"的测试方式：

```java
// 核心流程：构造请求 → 执行 → 断言响应
mockMvc.perform(post("/api/account/login")        // 构造请求
        .param("username", "testuser")            // 请求参数
        .param("password", "password"))
    .andExpect(status().isOk())                   // 断言HTTP状态码
    .andExpect(jsonPath("$.code").value(200))     // 断言响应JSON字段
    .andExpect(jsonPath("$.message").value("登录成功"));
```

### 2.2 响应格式规范

所有接口统一返回 `Result<T>` 格式：

```json
{
    "code": 200,           // 状态码：200成功, 400参数错误, 401未授权, 404不存在, 500服务器错误
    "message": "操作成功",  // 提示信息
    "data": {...},         // 数据内容
    "timestamp": 123456789 // 时间戳
}
```

---

## 三、功能测试详解

### 3.1 功能模块划分

| 模块 | Controller | 核心功能 |
|------|-----------|----------|
| 用户账户 | AccountController | 注册、登录、查询、更新、删除 |
| 购物车 | CartController | 添加、删除、更新数量、清空 |
| 订单 | OrderController | 创建订单、查询订单、更新状态 |
| 产品 | ProductController | CRUD、分类查询、搜索 |
| 分类 | CategoryController | CRUD |
| 商品项 | ItemController | CRUD |
| 管理员 | AdminController | 后台管理所有资源 |

### 3.2 测试用例表格

---

### 模块：用户账户管理 (AccountController)

| 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---------|---------|---------|---------|---------|--------|---------|
| 用户登录成功 | 系统中存在用户 testuser/password | 1. 调用 POST /api/account/login | username=testuser, password=password | code=200, message="登录成功", data包含用户信息 | P0 | 用户账户 |
| 用户登录失败-密码错误 | 系统中存在用户 testuser/password | 1. 调用 POST /api/account/login | username=testuser, password=wrongpassword | code=401, message="用户名或密码错误" | P0 | 用户账户 |
| 用户登录失败-用户不存在 | 系统中无此用户 | 1. 调用 POST /api/account/login | username=nonexistent, password=password | code=401, message="用户名或密码错误" | P0 | 用户账户 |
| 用户登录失败-空用户名 | 无 | 1. 调用 POST /api/account/login | password=password | code=400, 提示参数错误 | P1 | 用户账户 |
| 用户登录失败-空密码 | 无 | 1. 调用 POST /api/account/login | username=testuser | code=400, 提示参数错误 | P1 | 用户账户 |
| 用户登录失败-SQL注入 | 无 | 1. 调用 POST /api/account/login | username=admin' OR '1'='1, password=any | code=401, 不执行SQL注入 | P1 | 用户账户 |
| 用户注册成功 | 系统中无 testuser2 | 1. 调用 POST /api/account/register | JSON: {"userid":"testuser2","email":"test@test.com","firstname":"Test","lastname":"User","addr1":"123 St","city":"City","state":"ST","zip":"12345","country":"USA","phone":"555-1234"} | code=200, message="注册成功" | P0 | 用户账户 |
| 用户注册失败-缺少必填字段 | 无 | 1. 调用 POST /api/account/register | JSON: {"userid":"testuser3","email":"test@test.com"} | code=400, 提示字段校验错误 | P1 | 用户账户 |
| 用户注册失败-邮箱格式错误 | 无 | 1. 调用 POST /api/account/register | JSON: {"userid":"testuser4","email":"notanemail",...} | code=400, 提示邮箱格式错误 | P1 | 用户账户 |
| 用户注册失败-邮箱为空 | 无 | 1. 调用 POST /api/account/register | JSON: {"userid":"testuser5","email":"",...} | code=400, 提示邮箱不能为空 | P1 | 用户账户 |
| 查询当前登录用户 | 用户已登录 | 1. 调用 GET /api/account/current | 无（从Session获取） | code=200, data包含当前用户信息 | P0 | 用户账户 |
| 查询当前登录用户-未登录 | 用户未登录 | 1. 调用 GET /api/account/current | 无 | code=401, message="未登录" | P0 | 用户账户 |
| 根据用户名查询用户 | 系统中存在 testuser | 1. 调用 GET /api/account/testuser | 无 | code=200, data包含用户完整信息 | P0 | 用户账户 |
| 根据用户名查询用户-不存在 | 系统中无此用户 | 1. 调用 GET /api/account/notexist | 无 | code=404, message="用户不存在" | P1 | 用户账户 |
| 更新用户信息成功 | 用户已登录 | 1. 调用 PUT /api/account/testuser | JSON: {"email":"new@test.com","firstname":"NewName",...} | code=200, message="更新成功" | P0 | 用户账户 |
| 更新用户信息失败 | 用户已登录 | 1. 调用 PUT /api/account/testuser | JSON: {"email":"new@test.com",...} | code=500, message="更新失败" | P1 | 用户账户 |
| 删除用户成功 | 用户存在 | 1. 调用 DELETE /api/account/testuser | 无 | code=200, message="删除成功" | P1 | 用户账户 |
| 删除用户失败 | 用户不存在 | 1. 调用 DELETE /api/account/notexist | 无 | code=500, message="删除失败" | P2 | 用户账户 |
| 用户登出 | 用户已登录 | 1. 调用 POST /api/account/logout | 无 | code=200, message="登出成功" | P0 | 用户账户 |

---

### 模块：购物车管理 (CartController)

| 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---------|---------|---------|---------|---------|--------|---------|
| 获取购物车-空购物车 | 用户未添加商品 | 1. 调用 GET /api/cart | 无 | code=200, data.numberOfItems=0 | P0 | 购物车 |
| 添加商品到购物车成功 | 商品 ITEM001 存在 | 1. 调用 POST /api/cart/add | itemId=ITEM001 | code=200, message="添加成功" | P0 | 购物车 |
| 添加商品到购物车失败-商品不存在 | 商品 NOTEXIST 不存在 | 1. 调用 POST /api/cart/add | itemId=NOTEXIST | code=404, message="商品不存在" | P0 | 购物车 |
| 添加商品到购物车失败-缺少参数 | 无 | 1. 调用 POST /api/cart/add | 无 | code=400, 提示缺少参数 | P1 | 购物车 |
| 重复添加同一商品 | 商品 ITEM001 已在购物车 | 1. 调用 POST /api/cart/add | itemId=ITEM001 | code=200, 该商品数量+1 | P0 | 购物车 |
| 添加多个不同商品 | 无 | 1. 添加ITEM001 2. 添加ITEM002 | itemId=ITEM001, itemId=ITEM002 | code=200, numberOfItems=2 | P0 | 购物车 |
| 从购物车移除商品成功 | 商品 ITEM001 在购物车 | 1. 添加ITEM001 2. 调用 POST /api/cart/remove | itemId=ITEM001 | code=200, message="移除成功" | P0 | 购物车 |
| 从购物车移除商品失败-商品不在购物车 | 购物车为空 | 1. 调用 POST /api/cart/remove | itemId=ITEM001 | code=404, message="商品不在购物车中" | P1 | 购物车 |
| 从购物车移除商品失败-缺少参数 | 无 | 1. 调用 POST /api/cart/remove | 无 | code=400, 提示缺少参数 | P1 | 购物车 |
| 更新购物车商品数量成功 | 商品 ITEM001 在购物车 | 1. 添加ITEM001 2. 调用 POST /api/cart/update | itemId=ITEM001, quantity=5 | code=200, message="更新成功" | P0 | 购物车 |
| 更新购物车商品数量为0 | 商品 ITEM001 在购物车 | 1. 添加ITEM001 2. 调用 POST /api/cart/update | itemId=ITEM001, quantity=0 | code=200, 商品被移除 | P1 | 购物车 |
| 更新购物车商品数量为负数 | 商品 ITEM001 在购物车 | 1. 添加ITEM001 2. 调用 POST /api/cart/update | itemId=ITEM001, quantity=-1 | code=200, 商品被移除 | P1 | 购物车 |
| 更新购物车商品数量失败-缺少参数 | 无 | 1. 调用 POST /api/cart/update | itemId=ITEM001 | code=400, 提示缺少参数 | P1 | 购物车 |
| 清空购物车成功 | 购物车有商品 | 1. 添加ITEM001 2. 调用 POST /api/cart/clear | 无 | code=200, message="购物车已清空" | P0 | 购物车 |
| 清空空购物车 | 购物车为空 | 1. 调用 POST /api/cart/clear | 无 | code=200, message="购物车已清空" | P1 | 购物车 |
| 完整购物流程 | 无 | 1.添加ITEM001 2.查看购物车 3.更新数量为3 4.移除商品 5.查看购物车 | itemId=ITEM001, quantity=3 | 步骤5时 numberOfItems=0 | P0 | 购物车 |

---

### 模块：订单管理 (OrderController)

| 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---------|---------|---------|---------|---------|--------|---------|
| 创建订单成功 | 用户已登录，购物车有商品 | 1. 调用 POST /api/orders | JSON: {"shipaddr1":"123 St","shipcity":"City","shipstate":"SS","shipzip":"11111","shipcountry":"USA","billaddr1":"123 St","billcity":"City","billstate":"BS","billzip":"22222","billcountry":"USA","courier":"FedEx","creditcard":"1234567890123456","exprdate":"12/2025","cardtype":"Visa","locale":"en_US"} | code=200, message="订单创建成功", data包含订单信息 | P0 | 订单 |
| 创建订单失败-未登录 | 用户未登录 | 1. 调用 POST /api/orders | 同上JSON | code=401, message="请先登录" | P0 | 订单 |
| 创建订单失败-购物车为空 | 用户已登录，购物车为空 | 1. 调用 POST /api/orders | 同上JSON | code=400, message="购物车为空" | P0 | 订单 |
| 查询所有订单 | 系统存在订单 | 1. 调用 GET /api/orders | 无 | code=200, data为订单数组 | P1 | 订单 |
| 查询所有订单-空列表 | 系统无订单 | 1. 调用 GET /api/orders | 无 | code=200, data为空数组 | P2 | 订单 |
| 根据订单ID查询订单 | 订单1存在 | 1. 调用 GET /api/orders/1 | 无 | code=200, data包含订单详情 | P0 | 订单 |
| 根据订单ID查询订单-不存在 | 订单999不存在 | 1. 调用 GET /api/orders/999 | 无 | code=404, message="订单不存在" | P1 | 订单 |
| 查询当前用户订单 | 用户已登录 | 1. 调用 GET /api/orders/my | 无 | code=200, data为当前用户订单列表 | P0 | 订单 |
| 查询当前用户订单-未登录 | 用户未登录 | 1. 调用 GET /api/orders/my | 无 | code=401, message="请先登录" | P0 | 订单 |
| 根据用户ID查询订单 | 用户testuser存在 | 1. 调用 GET /api/orders/user/testuser | 无 | code=200, data为该用户订单列表 | P1 | 订单 |
| 更新订单状态成功 | 订单1存在 | 1. 调用 PUT /api/orders/1/status | status=SHIPPED | code=200, message="状态更新成功" | P0 | 订单 |
| 更新订单状态失败 | 订单1不存在 | 1. 调用 PUT /api/orders/999/status | status=SHIPPED | code=500, message="状态更新失败" | P1 | 订单 |
| 查询订单商品详情 | 订单1存在且有商品 | 1. 调用 GET /api/orders/1/items | 无 | code=200, data为订单商品列表 | P1 | 订单 |
| 查询订单状态历史 | 订单1存在 | 1. 调用 GET /api/orders/1/status/history | 无 | code=200, data为状态历史列表 | P1 | 订单 |

---

### 模块：产品管理 (ProductController)

| 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---------|---------|---------|---------|---------|--------|---------|
| 查询所有产品 | 系统存在产品 | 1. 调用 GET /api/products | 无 | code=200, data为产品数组 | P0 | 产品 |
| 查询所有产品-空列表 | 系统无产品 | 1. 调用 GET /api/products | 无 | code=200, data为空数组 | P2 | 产品 |
| 根据分类查询产品 | 分类FISH存在 | 1. 调用 GET /api/products/category/FISH | 无 | code=200, data为该分类产品列表 | P0 | 产品 |
| 根据分类查询产品-无结果 | 分类NOTEXIST不存在 | 1. 调用 GET /api/products/category/NOTEXIST | 无 | code=200, data为空数组 | P1 | 产品 |
| 搜索产品 | 存在含"fish"的产品 | 1. 调用 GET /api/products/search | keyword=fish | code=200, data为搜索结果 | P0 | 产品 |
| 搜索产品-无结果 | 无含"zzzzz"的产品 | 1. 调用 GET /api/products/search | keyword=zzzzz | code=200, data为空数组 | P1 | 产品 |
| 搜索产品-缺少参数 | 无 | 1. 调用 GET /api/products/search | 无 | code=400, 提示缺少参数 | P1 | 产品 |
| 根据产品ID查询产品 | 产品P001存在 | 1. 调用 GET /api/products/P001 | 无 | code=200, data包含产品详情和items | P0 | 产品 |
| 根据产品ID查询产品-不存在 | 产品NOTEXIST不存在 | 1. 调用 GET /api/products/NOTEXIST | 无 | code=404, message="产品不存在" | P0 | 产品 |
| 查询产品的商品项 | 产品P001存在 | 1. 调用 GET /api/products/P001/items | 无 | code=200, data为商品项列表 | P1 | 产品 |
| 查询产品的商品项-空列表 | 产品P001无商品项 | 1. 调用 GET /api/products/P001/items | 无 | code=200, data为空数组 | P2 | 产品 |
| 创建产品成功 | 无 | 1. 调用 POST /api/products | JSON: {"productid":"P999","category":"FISH","name":"测试鱼","description":"测试"} | code=200, message="创建成功" | P0 | 产品 |
| 创建产品失败 | 无 | 1. 调用 POST /api/products | 同上JSON | code=500, message="创建失败" | P1 | 产品 |
| 创建产品失败-缺少必填字段 | 无 | 1. 调用 POST /api/products | JSON: {"productid":"","category":"FISH","name":"测试鱼"} | code=400, 提示字段校验错误 | P1 | 产品 |
| 更新产品成功 | 产品P001存在 | 1. 调用 PUT /api/products/P001 | JSON: {"category":"FISH","name":"更新鱼","description":"更新"} | code=200, message="更新成功" | P0 | 产品 |
| 更新产品失败 | 产品P001不存在 | 1. 调用 PUT /api/products/P001 | 同上JSON | code=500, message="更新失败" | P1 | 产品 |
| 删除产品成功 | 产品P001存在 | 1. 调用 DELETE /api/products/P001 | 无 | code=200, message="删除成功" | P0 | 产品 |
| 删除产品失败 | 产品P001不存在 | 1. 调用 DELETE /api/products/P001 | 无 | code=500, message="删除失败" | P1 | 产品 |

---

### 模块：分类管理 (CategoryController)

| 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---------|---------|---------|---------|---------|--------|---------|
| 查询所有分类 | 系统存在分类 | 1. 调用 GET /api/categories | 无 | code=200, data为分类数组 | P0 | 分类 |
| 根据分类ID查询分类 | 分类FISH存在 | 1. 调用 GET /api/categories/FISH | 无 | code=200, data包含分类详情 | P0 | 分类 |
| 根据分类ID查询分类-不存在 | 分类NOTEXIST不存在 | 1. 调用 GET /api/categories/NOTEXIST | 无 | code=404, message="分类不存在" | P1 | 分类 |
| 创建分类成功 | 无 | 1. 调用 POST /api/categories | JSON: {"catid":"BIRDS","name":"鸟类","description":"各种宠物鸟"} | code=200, message="创建成功" | P0 | 分类 |
| 创建分类失败 | 无 | 1. 调用 POST /api/categories | 同上JSON | code=503, message="创建失败" | P1 | 分类 |
| 更新分类成功 | 分类FISH存在 | 1. 调用 PUT /api/categories/FISH | JSON: {"name":"鱼类","description":"各种观赏鱼"} | code=200, message="更新成功" | P0 | 分类 |
| 更新分类失败 | 分类FISH不存在 | 1. 调用 PUT /api/categories/FISH | 同上JSON | code=503, message="更新失败" | P1 | 分类 |
| 删除分类成功 | 分类FISH存在 | 1. 调用 DELETE /api/categories/FISH | 无 | code=200, message="删除成功" | P0 | 分类 |
| 删除分类失败 | 分类FISH不存在 | 1. 调用 DELETE /api/categories/FISH | 无 | code=503, message="删除失败" | P1 | 分类 |

---

### 模块：商品项管理 (ItemController)

| 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---------|---------|---------|---------|---------|--------|---------|
| 查询所有商品项 | 系统存在商品项 | 1. 调用 GET /api/items | 无 | code=200, data为商品项数组 | P0 | 商品项 |
| 根据商品项ID查询 | 商品项ITEM001存在 | 1. 调用 GET /api/items/ITEM001 | 无 | code=200, data包含商品项详情 | P0 | 商品项 |
| 根据商品项ID查询-不存在 | 商品项NOTEXIST不存在 | 1. 调用 GET /api/items/NOTEXIST | 无 | code=404, message="商品项不存在" | P1 | 商品项 |
| 创建商品项成功 | 无 | 1. 调用 POST /api/items | JSON: {"itemid":"ITEM999","productid":"P001","listprice":10.00,"qty":100} | code=200, message="创建成功" | P0 | 商品项 |
| 创建商品项失败 | 无 | 1. 调用 POST /api/items | 同上JSON | code=500, message="创建失败" | P1 | 商品项 |
| 更新商品项成功 | 商品项ITEM001存在 | 1. 调用 PUT /api/items/ITEM001 | JSON: {"productid":"P001","listprice":15.00,"qty":200} | code=200, message="更新成功" | P0 | 商品项 |
| 更新商品项失败 | 商品项ITEM001不存在 | 1. 调用 PUT /api/items/ITEM001 | 同上JSON | code=500, message="更新失败" | P1 | 商品项 |
| 删除商品项成功 | 商品项ITEM001存在 | 1. 调用 DELETE /api/items/ITEM001 | 无 | code=200, message="删除成功" | P0 | 商品项 |
| 删除商品项失败 | 商品项ITEM001不存在 | 1. 调用 DELETE /api/items/ITEM001 | 无 | code=500, message="删除失败" | P1 | 商品项 |

---

### 模块：管理员管理 (AdminController)

| 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---------|---------|---------|---------|---------|--------|---------|
| 管理员查询所有分类 | 管理员已登录 | 1. 调用 GET /api/admin/categories | 无 | code=200, data为分类数组 | P0 | 管理员 |
| 管理员查询分类详情 | 管理员已登录，分类FISH存在 | 1. 调用 GET /api/admin/categories/FISH | 无 | code=200, data包含分类详情 | P0 | 管理员 |
| 管理员创建分类 | 管理员已登录 | 1. 调用 POST /api/admin/categories | JSON: {"catid":"BIRDS","name":"鸟类"} | code=200, message="创建成功" | P0 | 管理员 |
| 管理员更新分类 | 管理员已登录 | 1. 调用 PUT /api/admin/categories/FISH | JSON: {"name":"鱼类","description":"更新"} | code=200, message="更新成功" | P0 | 管理员 |
| 管理员删除分类 | 管理员已登录 | 1. 调用 DELETE /api/admin/categories/FISH | 无 | code=200, message="删除成功" | P0 | 管理员 |
| 管理员查询所有产品 | 管理员已登录 | 1. 调用 GET /api/admin/products | 无 | code=200, data为产品数组 | P0 | 管理员 |
| 管理员创建产品 | 管理员已登录 | 1. 调用 POST /api/admin/products | form: productid=P002, category=FISH, name=虎鲨 | code=200, message="创建成功" | P0 | 管理员 |
| 管理员更新产品 | 管理员已登录 | 1. 调用 PUT /api/admin/products/P001 | form: category=FISH, name=神仙鱼 | code=200, message="更新成功" | P0 | 管理员 |
| 管理员删除产品 | 管理员已登录 | 1. 调用 DELETE /api/admin/products/P001 | 无 | code=200, message="删除成功" | P0 | 管理员 |
| 管理员更新产品状态 | 管理员已登录 | 1. 调用 PUT /api/admin/products/P001/status | status=OFF_SALE | code=200, message="状态更新成功" | P0 | 管理员 |
| 管理员更新库存 | 管理员已登录 | 1. 调用 PUT /api/admin/items/ITEM001/inventory | quantity=50 | code=200, message="库存更新成功" | P0 | 管理员 |
| 管理员查询所有订单 | 管理员已登录 | 1. 调用 GET /api/admin/orders | 无 | code=200, data为订单数组 | P0 | 管理员 |
| 管理员查询订单详情 | 管理员已登录 | 1. 调用 GET /api/admin/orders/1 | 无 | code=200, data包含订单和商品项 | P0 | 管理员 |
| 管理员更新订单状态 | 管理员已登录 | 1. 调用 PUT /api/admin/orders/1/status | status=SHIPPED | code=200, message="状态更新成功" | P0 | 管理员 |
| 管理员查询所有用户 | 管理员已登录 | 1. 调用 GET /api/admin/users | 无 | code=200, data为用户数组 | P0 | 管理员 |
| 管理员更新用户信息 | 管理员已登录 | 1. 调用 PUT /api/admin/users/testuser | JSON: {"email":"new@test.com"} | code=200, message="更新成功" | P0 | 管理员 |
| 管理员删除用户 | 管理员已登录 | 1. 调用 DELETE /api/admin/users/testuser | 无 | code=200, message="删除成功" | P0 | 管理员 |
| 管理员更新用户角色 | 管理员已登录 | 1. 调用 PUT /api/admin/users/testuser/role | role=ADMIN | code=200, message="角色更新成功" | P0 | 管理员 |

---

## 四、性能测试详解

### 4.1 性能测试方案

由于当前项目使用的是 Spring Boot Test 框架，主要进行的是单元测试和集成测试。对于性能测试，建议使用以下方案：

#### 方案一：使用 JMeter 进行性能测试

**测试场景：**

| 场景 | 并发用户数 | 持续时间 | 目标指标 |
|------|-----------|---------|---------|
| 正常负载 | 50 | 5分钟 | 响应时间 < 500ms |
| 高负载 | 200 | 10分钟 | 响应时间 < 1s，错误率 < 1% |
| 压力测试 | 500 | 5分钟 | 系统不崩溃 |

**测试接口：**

| 接口 | 方法 | 测试数据 |
|------|------|---------|
| /api/products | GET | 无 |
| /api/products/search | GET | keyword=fish |
| /api/account/login | POST | username=test, password=pass |
| /api/cart/add | POST | itemId=ITEM001 |
| /api/orders | POST | 完整订单JSON |

#### 方案二：使用 Spring Boot Actuator 监控

```xml
<!-- pom.xml 添加依赖 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```yaml
# application.yml 配置
management:
  endpoints:
    web:
      exposure:
        include: health,metrics,info
```

### 4.2 性能优化建议

1. **数据库索引优化**：为常用查询字段（如 userid、productid、category）添加索引
2. **缓存优化**：使用 Redis 缓存热门商品和分类数据
3. **连接池优化**：配置合理的数据库连接池大小
4. **分页查询**：列表查询接口添加分页参数

---

## 五、自动化测试详解

### 5.1 当前自动化测试架构

```
src/test/java/com/jpetstore/
├── controller/           # Controller层测试（集成测试）
│   ├── AccountControllerTest.java
│   ├── CartControllerTest.java
│   ├── OrderControllerTest.java
│   ├── ProductControllerTest.java
│   ├── CategoryControllerTest.java
│   ├── ItemControllerTest.java
│   └── AdminControllerTest.java
├── service/              # Service层测试（单元测试）
│   ├── AccountServiceTest.java
│   ├── ProductServiceTest.java
│   └── ...
├── domain/               # Domain层测试（单元测试）
│   ├── AccountTest.java
│   ├── CartTest.java
│   └── ...
├── common/               # 公共类测试
│   └── ResultTest.java
└── config/               # 配置类测试
    └── MyBatisConfigTest.java
```

### 5.2 测试工具与注解说明

| 工具/注解 | 用途 | 示例 |
|----------|------|------|
| @WebMvcTest | 仅加载指定Controller进行测试 | @WebMvcTest(AccountController.class) |
| @MockBean | Mock一个Bean（替换真实实现） | @MockBean AccountService accountService |
| MockMvc | 模拟HTTP请求 | mockMvc.perform(get("/api/...")) |
| @BeforeEach | 每个测试方法执行前初始化 | setUp()方法 |
| @ParameterizedTest | 参数化测试，一组数据跑多次 | @ValueSource(strings = {"a", "b"}) |
| @Test | 普通单元测试 | @Test void testXXX() |

### 5.3 Mockito Mock 示例

```java
// Mock Service层，控制返回值
when(accountService.login("testuser", "password")).thenReturn(testAccount);
when(accountService.login("testuser", "wrong")).thenReturn(null);

// 验证方法是否被调用
verify(accountService, times(1)).login("testuser", "password");
```

### 5.4 测试数据管理

```java
// 每个测试方法前初始化测试数据
@BeforeEach
void setUp() {
    testAccount = new Account();
    testAccount.setUserid("testuser");
    testAccount.setEmail("test@example.com");
    // ... 设置更多字段
}
```

### 5.5 持续集成配置建议

在项目根目录创建 `.github/workflows/test.yml`：

```yaml
name: Test
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      - run: ./mvnw test
```

---

## 六、测试用例设计技巧总结

### 6.1 测试用例设计方法

1. **等价类划分**：将输入数据分为有效等价类和无效等价类
2. **边界值分析**：测试边界情况（如空字符串、0、负数、最大值）
3. **因果图法**：分析输入条件组合与输出结果的关系
4. **错误推测法**：根据经验猜测可能的错误点

### 6.2 测试覆盖要点

| 覆盖类型 | 说明 | 示例 |
|---------|------|------|
| 语句覆盖 | 执行所有代码语句 | 测试 if-else 的两个分支 |
| 分支覆盖 | 执行所有条件分支 | 测试成功和失败场景 |
| 路径覆盖 | 执行所有可能路径 | 测试各种参数组合 |

### 6.3 优先级定义

| 优先级 | 说明 | 示例 |
|--------|------|------|
| P0 | 核心功能，阻塞性问题 | 用户登录、创建订单 |
| P1 | 重要功能，影响用户体验 | 更新用户信息、搜索产品 |
| P2 | 次要功能，不影响主流程 | 删除用户、清空购物车 |

---

## 七、测试执行流程

```
1. 准备阶段
   └── 初始化测试数据 (@BeforeEach)

2. 执行阶段
   └── 调用接口 (mockMvc.perform(...))

3. 断言阶段
   └── 验证响应状态码、JSON字段、数据完整性

4. 清理阶段
   └── Spring自动清理（无需手动）
```

---

## 八、现有测试文件清单

| 测试文件 | 位置 | 测试类型 |
|---------|------|---------|
| AccountControllerTest.java | src/test/java/com/jpetstore/controller/ | 集成测试 |
| CartControllerTest.java | src/test/java/com/jpetstore/controller/ | 集成测试 |
| OrderControllerTest.java | src/test/java/com/jpetstore/controller/ | 集成测试 |
| ProductControllerTest.java | src/test/java/com/jpetstore/controller/ | 集成测试 |
| CategoryControllerTest.java | src/test/java/com/jpetstore/controller/ | 集成测试 |
| ItemControllerTest.java | src/test/java/com/jpetstore/controller/ | 集成测试 |
| AdminControllerTest.java | src/test/java/com/jpetstore/controller/ | 集成测试 |
| AccountServiceTest.java | src/test/java/com/jpetstore/service/ | 单元测试 |
| ProductServiceTest.java | src/test/java/com/jpetstore/service/ | 单元测试 |
| AccountTest.java | src/test/java/com/jpetstore/domain/ | 单元测试 |
| ResultTest.java | src/test/java/com/jpetstore/common/ | 单元测试 |

---

## 九、初学者入门指南

### 9.1 运行第一个测试

```bash
# 进入项目目录
cd /workspace

# 运行AccountController的所有测试
./mvnw test -Dtest=AccountControllerTest

# 运行单个测试方法
./mvnw test -Dtest=AccountControllerTest#testLoginSuccess
```

### 9.2 测试输出解读

```
[INFO] Tests run: 15, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

- **Tests run**: 运行的测试数量
- **Failures**: 断言失败的测试数量
- **Errors**: 运行时错误的测试数量
- **Skipped**: 跳过的测试数量

### 9.3 常见测试问题

| 问题 | 原因 | 解决方案 |
|------|------|---------|
| 404 Not Found | 路径写错 | 检查Controller的@RequestMapping |
| 400 Bad Request | 参数缺少或格式错误 | 检查@RequestParam/@RequestBody |
| MockitoException | Mock方法签名不匹配 | 检查when()中的参数类型 |
| JSONPath异常 | JSON路径错误 | 打印响应体检查实际结构 |
