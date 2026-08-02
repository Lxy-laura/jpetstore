# JPetStore 系统测试用例文档

> **项目说明**：基于 Spring Boot 3.5.1 + MyBatis-Plus + JWT 的宠物电商系统
> **测试账号**：管理员 `j2ee/j2ee`(ADMIN)；普通用户 `ACID/ACID`(USER)
> **统一响应**：`Result<T>` 包含 `code/message/data/timestamp` 字段
> **优先级定义**：P0=阻塞级 / P1=高 / P2=中 / P3=低

---

## 一、账户模块（Account）

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| ACC-001 | 用户名密码正确登录成功 | 数据库存在账号 j2ee | 1. 发送 POST /api/account/login | username=j2ee, password=j2ee | HTTP 200；Result.code=200；message="登录成功"；data 含 token 与 user 对象；token 非空 | P0 | Account |
| ACC-002 | 密码错误登录失败 | 账号 j2ee 存在 | 1. 发送 POST /api/account/login | username=j2ee, password=wrong | HTTP 200；Result.code=401；message="用户名或密码错误"；data=null | P0 | Account |
| ACC-003 | 用户名不存在登录失败 | 数据库无该用户 | 1. 发送 POST /api/account/login | username=ghost, password=123 | HTTP 200；Result.code=401；message="用户名或密码错误" | P1 | Account |
| ACC-004 | 缺少 username 参数 | 无 | 1. 发送 POST /api/account/login | password=j2ee | HTTP 400；Result.code=400；message 含"缺少必需参数: username" | P1 | Account |
| ACC-005 | 缺少 password 参数 | 无 | 1. 发送 POST /api/account/login | username=j2ee | HTTP 400；Result.code=400；message 含"缺少必需参数: password" | P1 | Account |
| ACC-006 | 合法数据注册成功 | 数据库无该 userid | 1. 发送 POST /api/account/register，body 为完整 Account | userid=newuser, email=test@qq.com, firstname=张, lastname=三, addr1=北京, city=北京, phone=13800000000, signOn={username:newuser,password:123}, profile={userid:newuser,langpref:zh,favcategory:DOGS} | HTTP 200；code=200；message="注册成功" | P0 | Account |
| ACC-007 | 注册时 email 格式非法 | 无 | 1. POST /api/account/register | email=not-an-email | HTTP 400；code=400；message 含"参数校验失败"且包含 email 字段 | P1 | Account |
| ACC-008 | 注册时缺少必填字段 firstname | 无 | 1. POST /api/account/register | 缺少 firstname | HTTP 400；code=400；message 含 firstname 字段校验失败信息 | P1 | Account |
| ACC-009 | 注册时缺少必填字段 phone | 无 | 1. POST /api/account/register | 缺少 phone | HTTP 400；code=400；校验失败含 phone | P2 | Account |
| ACC-010 | 注册失败返回 500 | service.register 返回 false | 1. POST /api/account/register（mock mapper insert=0） | 合法 Account | HTTP 200；code=500；message="注册失败" | P1 | Account |
| ACC-011 | 已登录用户获取当前信息 | 请求头带有效 JWT | 1. GET /api/account/current | Authorization: Bearer {validToken}；currentUser 属性已注入 | code=200；data 为 Account 对象 | P0 | Account |
| ACC-012 | 未登录用户获取当前信息 | 请求未携带 token | 1. GET /api/account/current | 无 Authorization 头 | code=401；message="未登录" | P1 | Account |
| ACC-013 | 无效 token 获取当前信息 | token 已被篡改 | 1. GET /api/account/current | Authorization: Bearer invalid.token.here | jwtUtil.validate 返回 false；currentUser 为 null；code=401；message="未登录" | P2 | Account |
| ACC-014 | 按 userid 查询用户存在 | j2ee 存在 | 1. GET /api/account/j2ee | path: j2ee | code=200；data.userid=j2ee | P1 | Account |
| ACC-015 | 按 userid 查询用户不存在 | 无 | 1. GET /api/account/ghost | path: ghost | code=404；message="用户不存在" | P1 | Account |
| ACC-016 | 更新账户信息成功 | 用户已登录 | 1. PUT /api/account/{userid} | path=j2ee, body=新 Account（含合法 email 等） | code=200；message="更新成功"；service.updateAccount 被调用且参数 userid 来源于 path | P0 | Account |
| ACC-017 | 更新账户时 body userid 被覆盖 | 用户已登录 | 1. PUT /api/account/j2ee，body 中 userid=other | body.userid=other | 控制器强制 account.setUserid("j2ee")；实际更新 j2ee；code=200 | P2 | Account |
| ACC-018 | 更新账户失败 | service.updateAccount 返回 false | 1. PUT /api/account/j2ee | 合法 body | code=500；message="更新失败" | P1 | Account |
| ACC-019 | 删除账户成功 | 账号存在 | 1. DELETE /api/account/ACID | path=ACID | code=200；message="删除成功"；service.deleteAccount 被调用 | P1 | Account |
| ACC-020 | 删除账户失败 | service.deleteAccount 返回 false | 1. DELETE /api/account/ACID | path=ACID | code=500；message="删除失败" | P2 | Account |
| ACC-021 | 登出成功 | 无 | 1. POST /api/account/logout | 无 | code=200；message="登出成功"；data="登出成功" | P2 | Account |
| ACC-022 | 注册时 signOn 子对象校验 | 无 | 1. POST /api/account/register | signOn.password 为空 | code=400；校验失败含 password | P2 | Account |
| ACC-023 | 注册时 profile 子对象校验 | 无 | 1. POST /api/account/register | profile.favcategory 为空 | code=400；校验失败含 favcategory | P3 | Account |
| ACC-024 | 更新账户时 @Valid 校验 | 已登录 | 1. PUT /api/account/j2ee | email 非法 | code=400；校验失败 | P2 | Account |

---

## 二、通用组件模块（Common）

### 2.1 Result 统一响应

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| COM-001 | success() 默认成功 | 无 | 1. 调用 Result.success() | 无 | code=200；message="操作成功"；data=null；timestamp>0 | P1 | Result |
| COM-002 | success(data) 带数据 | 无 | 1. 调用 Result.success(obj) | obj=任意对象 | code=200；data=obj | P1 | Result |
| COM-003 | success(message,data) 自定义消息 | 无 | 1. 调用 Result.success("自定义",obj) | 自定义消息 | code=200；message="自定义" | P2 | Result |
| COM-004 | badRequest 返回 400 | 无 | 1. 调用 Result.badRequest("错误") | "错误" | code=400；message="错误" | P1 | Result |
| COM-005 | unauthorized 返回 401 | 无 | 1. 调用 Result.unauthorized("未授权") | "未授权" | code=401 | P1 | Result |
| COM-006 | forbidden 返回 403 | 无 | 1. 调用 Result.forbidden("禁止") | "禁止" | code=403 | P1 | Result |
| COM-007 | notFound 返回 404 | 无 | 1. 调用 Result.notFound("未找到") | "未找到" | code=404 | P1 | Result |
| COM-008 | error 自定义 code | 无 | 1. 调用 Result.error(503,"服务异常") | code=503 | code=503；message="服务异常" | P2 | Result |

### 2.2 JwtUtil

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| JWT-001 | 生成 token 成功 | JwtUtil 已初始化 | 1. 调用 generateToken | username=j2ee, role=ADMIN | 返回非空字符串 token | P0 | JwtUtil |
| JWT-002 | 解析 username 正确 | 已生成 token | 1. 调用 getUsername | 上一步 token | 返回 "j2ee" | P0 | JwtUtil |
| JWT-003 | 解析 role 正确 | 已生成 token | 1. 调用 getRole | 上一步 token | 返回 "ADMIN" | P0 | JwtUtil |
| JWT-004 | validate 合法 token | 已生成 token | 1. 调用 validate | 合法 token | 返回 true | P0 | JwtUtil |
| JWT-005 | validate 篡改 token | 无 | 1. 调用 validate | "abc.def.ghi" | 返回 false（不抛异常） | P1 | JwtUtil |
| JWT-006 | validate null token | 无 | 1. 调用 validate | null | 返回 false | P1 | JwtUtil |
| JWT-007 | validate 空字符串 | 无 | 1. 调用 validate | "" | 返回 false | P2 | JwtUtil |
| JWT-008 | 过期 token 验证失败 | 生成 expiration=1ms 的 token | 1. 等待 10ms 2. 调用 validate | 过期 token | 返回 false | P2 | JwtUtil |

### 2.3 JwtAuthFilter

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| FIL-001 | 有效 Bearer token 注入 currentUser | 账号 j2ee 存在 | 1. 发送请求带 Authorization 头 2. 验证 request 属性 | Authorization: Bearer {validToken} | request.setAttribute("currentUser", account) 被调用；filterChain.doFilter 被调用 | P0 | JwtAuthFilter |
| FIL-002 | 无 Authorization 头放行 | 无 | 1. 不带 Authorization 头请求 /api/* | 无 header | currentUser 不被设置；请求放行 | P1 | JwtAuthFilter |
| FIL-003 | 非 Bearer 前缀放行 | 无 | 1. 带 Authorization: Basic xxx | Authorization: Basic xxx | currentUser 不设置；放行 | P2 | JwtAuthFilter |
| FIL-004 | 无效 token 放行 | 无 | 1. 带 Bearer invalid | Authorization: Bearer invalid | validate=false；currentUser 不设置；放行 | P1 | JwtAuthFilter |
| FIL-005 | token 有效但用户不存在 | mock getAccountByUsername 返回 null | 1. 带 Bearer validToken | token 合法但用户已被删除 | currentUser 不设置；放行 | P2 | JwtAuthFilter |

### 2.4 AdminInterceptor

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| INT-001 | 已登录管理员访问 /api/admin/** | currentUser 为 ADMIN 账号 | 1. 请求 /api/admin/users | currentUser.role=ADMIN | preHandle 返回 true；请求继续 | P0 | AdminInterceptor |
| INT-002 | 未登录访问 /api/admin/** | currentUser 为 null 且 session 无 user | 1. 请求 /api/admin/users | 无认证 | 返回 false；响应 JSON code=401 message="请先登录"；Content-Type=application/json;charset=utf-8 | P0 | AdminInterceptor |
| INT-003 | 普通用户访问 /api/admin/** | currentUser.role=USER | 1. 请求 /api/admin/users | role=USER | 返回 false；响应 JSON code=403 message="需要管理员权限" | P0 | AdminInterceptor |
| INT-004 | 未登录访问 /admin 页面 | session 无 user | 1. GET /admin | path=/admin（非 /api/） | sendRedirect("/login")；返回 false | P1 | AdminInterceptor |
| INT-005 | 普通用户访问 /admin 页面 | session user.role=USER | 1. GET /admin | role=USER | sendRedirect("/")；返回 false | P1 | AdminInterceptor |
| INT-006 | 通过 session 登录态访问 | currentUser=null 但 session 有 ADMIN user | 1. 请求 /api/admin/users | session.user.role=ADMIN | preHandle 返回 true | P2 | AdminInterceptor |

### 2.5 GlobalExceptionHandler

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| EXC-001 | @Valid 校验失败处理 | 控制器方法有 @Valid @RequestBody | 1. 提交非法 body | email=bad | HTTP 400；code=400；message 含"参数校验失败" | P0 | GlobalExceptionHandler |
| EXC-002 | 缺少 @RequestParam 处理 | 控制器有必填 @RequestParam | 1. 不传该参数 | 无 | HTTP 400；code=400；message 含"缺少必需参数" | P0 | GlobalExceptionHandler |
| EXC-003 | IllegalArgumentException 处理 | 业务抛 IllegalArgumentException | 1. 触发异常 | msg="非法参数" | HTTP 200；code=400；message="参数错误：非法参数" | P1 | GlobalExceptionHandler |
| EXC-004 | RuntimeException 兜底 | 业务抛 RuntimeException | 1. 触发异常 | msg="db error" | HTTP 200；code=503；message="运行时错误：db error" | P1 | GlobalExceptionHandler |
| EXC-005 | Exception 兜底 | 抛非运行时异常 | 1. 触发 Exception | msg="unknown" | HTTP 200；code=503；message="系统错误：unknown" | P2 | GlobalExceptionHandler |
| EXC-006 | 校验失败 message 包含字段名 | body 多字段非法 | 1. 提交多个非法字段 | email+firstname 都非法 | message 包含两个字段名及错误信息 | P2 | GlobalExceptionHandler |

---

## 三、管理员模块（Admin）

> **公共前置条件**：所有 /api/admin/** 接口需通过 AdminInterceptor 校验（ADMIN 角色）。测试时需 mock currentUser 为 ADMIN 账号或导入 WebMvcConfig。

### 3.1 分类管理（Admin）

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| ADM-001 | 获取全部分类列表 | 管理员已登录 | 1. GET /api/admin/categories | 无 | code=200；data 为分类列表 | P1 | Admin-Category |
| ADM-002 | 按 catid 查询分类存在 | 分类 FISH 存在 | 1. GET /api/admin/categories/FISH | catid=FISH | code=200；data.catid=FISH | P1 | Admin-Category |
| ADM-003 | 按 catid 查询分类不存在 | 无 | 1. GET /api/admin/categories/XXX | catid=XXX | code=404；message="分类不存在" | P2 | Admin-Category |
| ADM-004 | 创建分类成功 | 管理员已登录 | 1. POST /api/admin/categories | body={catid:TEST,name:测试分类} | code=200；message="创建成功" | P1 | Admin-Category |
| ADM-005 | 创建分类失败 | insert=0 | 1. POST /api/admin/categories | 合法 body | code=500；message="创建失败" | P2 | Admin-Category |
| ADM-006 | 更新分类成功 | 分类存在 | 1. PUT /api/admin/categories/FISH | catid=FISH, body=新分类 | code=200；message="更新成功" | P1 | Admin-Category |
| ADM-007 | 删除分类成功 | 分类存在 | 1. DELETE /api/admin/categories/FISH | catid=FISH | code=200 | P1 | Admin-Category |
| ADM-008 | 更新分类时 catid 强制覆盖 | 已登录 | 1. PUT /api/admin/categories/FISH，body.catid=other | body.catid=other | 强制 setCatid("FISH")；code=200 | P3 | Admin-Category |

### 3.2 产品管理（Admin）

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| ADM-010 | 获取全部产品 | 管理员已登录 | 1. GET /api/admin/products | 无 | code=200；data 为产品列表 | P1 | Admin-Product |
| ADM-011 | 按 productid 查询产品 | 产品 FI-SW-01 存在 | 1. GET /api/admin/products/FI-SW-01 | productid=FI-SW-01 | code=200；data 含 items 字段 | P1 | Admin-Product |
| ADM-012 | 查询产品不存在 | 无 | 1. GET /api/admin/products/XXX | productid=XXX | code=404；message="产品不存在" | P2 | Admin-Product |
| ADM-013 | 创建产品（无图片） | 已登录 | 1. POST /api/admin/products | productid=NEW-01, category=FISH, name=新产品 | code=200；message="创建成功" | P1 | Admin-Product |
| ADM-014 | 创建产品（带图片） | 已登录 | 1. POST multipart/form-data | productid, category, name + MockMultipartFile image | code=200；图片存到 uploads/UUID.ext；product.image="/uploads/xxx" | P2 | Admin-Product |
| ADM-015 | 创建产品缺 productid 参数 | 已登录 | 1. POST /api/admin/products | 缺 productid | HTTP 400；code=400；message 含"缺少必需参数: productid" | P2 | Admin-Product |
| ADM-016 | 创建产品缺 category 参数 | 已登录 | 1. POST /api/admin/products | 缺 category | HTTP 400；code=400；message 含"缺少必需参数: category" | P2 | Admin-Product |
| ADM-017 | 创建产品缺 name 参数 | 已登录 | 1. POST /api/admin/products | 缺 name | HTTP 400；code=400；message 含"缺少必需参数: name" | P2 | Admin-Product |
| ADM-018 | 创建产品图片上传异常 | Files.copy 抛 IOException | 1. POST multipart | 合法参数 + 图片 | code=500；message 含"图片上传失败" | P2 | Admin-Product |
| ADM-019 | 创建产品 insert 失败 | insert=0 | 1. POST /api/admin/products | 合法参数 | code=500；message="创建失败" | P2 | Admin-Product |
| ADM-020 | 更新产品成功 | 产品存在 | 1. PUT /api/admin/products/FI-SW-01 | category=FISH, name=更新名 | code=200；message="更新成功" | P1 | Admin-Product |
| ADM-021 | 更新产品不存在 | 产品不存在 | 1. PUT /api/admin/products/XXX | 合法参数 | code=404；message="产品不存在" | P2 | Admin-Product |
| ADM-022 | 更新产品 price 为 null 保留原值 | 产品存在 price=10.00 | 1. PUT 不传 price | price=null | 使用 existing.price；code=200 | P3 | Admin-Product |
| ADM-023 | 更新产品 status 为 null 保留原值 | 产品存在 status=ON_SALE | 1. PUT 不传 status | status=null | 使用 existing.status | P3 | Admin-Product |
| ADM-024 | 更新产品替换图片 | 产品已存在图片 | 1. PUT multipart 带新图片 | 新 image 文件 | 旧图片不删；新图片路径写入；code=200 | P3 | Admin-Product |
| ADM-025 | 删除产品成功 | 产品存在 | 1. DELETE /api/admin/products/FI-SW-01 | productid=FI-SW-01 | code=200 | P1 | Admin-Product |
| ADM-026 | 删除产品失败 | delete=0 | 1. DELETE /api/admin/products/XXX | productid=XXX | code=500；message="删除失败" | P2 | Admin-Product |
| ADM-027 | 更新产品状态为 ON_SALE | 已登录 | 1. PUT /api/admin/products/FI-SW-01/status | status=ON_SALE | code=200 | P1 | Admin-Product |
| ADM-028 | 更新产品状态为 OFF_SALE | 已登录 | 1. PUT /api/admin/products/FI-SW-01/status | status=OFF_SALE | code=200 | P1 | Admin-Product |
| ADM-029 | 更新产品状态值无效 | 已登录 | 1. PUT /api/admin/products/FI-SW-01/status | status=INVALID | code=500；message="状态值无效" | P2 | Admin-Product |
| ADM-030 | 更新产品状态失败 | updateProductStatus=0 | 1. PUT status | status=ON_SALE | code=500 | P3 | Admin-Product |
| ADM-031 | 获取产品商品项列表 | 产品存在 | 1. GET /api/admin/products/FI-SW-01/items | productid=FI-SW-01 | code=200；data 为 Item 列表 | P2 | Admin-Product |

### 3.3 商品项管理（Admin）

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| ADM-040 | 创建商品项成功 | 已登录 | 1. POST /api/admin/items | body={itemid:EST-99,productid:FI-SW-01,qty:50} | code=200；message="创建成功" | P1 | Admin-Item |
| ADM-041 | 创建商品项 qty 为负 | 已登录 | 1. POST /api/admin/items | qty=-5 | code=400；校验失败含 qty（@Min(0)） | P2 | Admin-Item |
| ADM-042 | 创建商品项缺 itemid | 已登录 | 1. POST /api/admin/items | 缺 itemid | code=400 | P2 | Admin-Item |
| ADM-043 | 创建商品项缺 productid | 已登录 | 1. POST /api/admin/items | 缺 productid | code=400 | P2 | Admin-Item |
| ADM-044 | 创建商品项失败 | insert=0 | 1. POST /api/admin/items | 合法 body | code=500；message="创建失败" | P2 | Admin-Item |
| ADM-045 | 更新商品项成功 | 已登录 | 1. PUT /api/admin/items/EST-1 | itemid=EST-1, body=新 Item | code=200；强制 setItemid | P1 | Admin-Item |
| ADM-046 | 更新商品项失败 | update=0 | 1. PUT /api/admin/items/EST-1 | 合法 body | code=500 | P2 | Admin-Item |
| ADM-047 | 删除商品项成功 | 已登录 | 1. DELETE /api/admin/items/EST-1 | itemid=EST-1 | code=200 | P1 | Admin-Item |
| ADM-048 | 删除商品项失败 | delete=0 | 1. DELETE /api/admin/items/XXX | itemid=XXX | code=500 | P2 | Admin-Item |
| ADM-049 | 更新库存成功 | 已登录 | 1. PUT /api/admin/items/EST-1/inventory | quantity=200 | code=200；message="库存更新成功" | P1 | Admin-Item |
| ADM-050 | 更新库存失败 | update=0 | 1. PUT /api/admin/items/EST-1/inventory | quantity=200 | code=500 | P2 | Admin-Item |

### 3.4 订单管理（Admin）

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| ADM-060 | 获取全部订单 | 已登录 | 1. GET /api/admin/orders | 无 | code=200；data 为订单列表 | P1 | Admin-Order |
| ADM-061 | 按 orderid 查询订单 | 订单存在 | 1. GET /api/admin/orders/1001 | orderid=1001 | code=200；data 含 orderItems | P1 | Admin-Order |
| ADM-062 | 查询订单不存在 | 无 | 1. GET /api/admin/orders/9999 | orderid=9999 | code=404；message="订单不存在" | P2 | Admin-Order |
| ADM-063 | 更新订单状态成功 | 已登录 | 1. PUT /api/admin/orders/1001/status | status=S | code=200 | P1 | Admin-Order |
| ADM-064 | 更新订单状态失败 | updateOrderStatus 返回 false | 1. PUT /api/admin/orders/1001/status | status=S | code=500 | P2 | Admin-Order |

### 3.5 用户管理（Admin）

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| ADM-070 | 获取全部用户 | 已登录 | 1. GET /api/admin/users | 无 | code=200；data 为用户列表 | P1 | Admin-User |
| ADM-071 | 按 userid 查询用户 | 用户存在 | 1. GET /api/admin/users/ACID | userid=ACID | code=200 | P1 | Admin-User |
| ADM-072 | 查询用户不存在 | 无 | 1. GET /api/admin/users/ghost | userid=ghost | code=404；message="用户不存在" | P2 | Admin-User |
| ADM-073 | 更新用户成功 | 已登录 | 1. PUT /api/admin/users/ACID | body=新 Account | code=200；强制 setUserid | P1 | Admin-User |
| ADM-074 | 更新用户失败 | updateAccount 返回 false | 1. PUT /api/admin/users/ACID | 合法 body | code=500 | P2 | Admin-User |
| ADM-075 | 删除用户成功 | 用户存在 | 1. DELETE /api/admin/users/ACID | userid=ACID | code=200 | P1 | Admin-User |
| ADM-076 | 删除用户失败 | deleteAccount 返回 false | 1. DELETE /api/admin/users/ACID | userid=ACID | code=500 | P2 | Admin-User |
| ADM-077 | 更新用户角色成功 | 用户存在 | 1. PUT /api/admin/users/ACID/role | role=ADMIN | code=200；message="角色更新成功" | P1 | Admin-User |
| ADM-078 | 更新用户角色用户不存在 | 用户不存在 | 1. PUT /api/admin/users/ghost/role | role=ADMIN | code=404；message="用户不存在" | P2 | Admin-User |
| ADM-079 | 更新用户角色失败 | updateAccount 返回 false | 1. PUT /api/admin/users/ACID/role | role=ADMIN | code=500 | P2 | Admin-User |

---

## 四、购物车模块（Cart）

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| CRT-001 | 获取空购物车 | session 无 cart | 1. GET /api/cart/ | 无 session 属性 | code=200；data 为新建空 Cart 对象；itemMap 为空 | P1 | Cart |
| CRT-002 | 获取已有购物车 | session 有 cart | 1. GET /api/cart/ | session.cart 已含商品 | code=200；data 为原 cart | P1 | Cart |
| CRT-003 | 添加商品到购物车 | 商品项 EST-1 存在 | 1. POST /api/cart/add | itemId=EST-1 | code=200；message="添加成功"；cart 含该商品 quantity=1 | P0 | Cart |
| CRT-004 | 重复添加商品数量+1 | cart 已含 EST-1 (qty=1) | 1. POST /api/cart/add | itemId=EST-1 | code=200；quantity=2 | P1 | Cart |
| CRT-005 | 添加不存在的商品 | itemService 返回 null | 1. POST /api/cart/add | itemId=EST-999 | code=404；message="商品不存在" | P1 | Cart |
| CRT-006 | 添加商品缺 itemId 参数 | 无 | 1. POST /api/cart/add | 无 itemId | HTTP 400；code=400；message 含"缺少必需参数: itemId" | P2 | Cart |
| CRT-007 | 从购物车移除商品 | cart 含 EST-1 | 1. POST /api/cart/remove | itemId=EST-1 | code=200；message="移除成功"；cart 不含该商品 | P1 | Cart |
| CRT-008 | 移除不存在的商品 | cart 不含 EST-2 | 1. POST /api/cart/remove | itemId=EST-2 | code=404；message="商品不在购物车中" | P2 | Cart |
| CRT-009 | 更新商品数量 | cart 含 EST-1 qty=1 | 1. POST /api/cart/update | itemId=EST-1, quantity=5 | code=200；message="更新成功"；quantity=5 | P1 | Cart |
| CRT-010 | 更新数量为 0 移除商品 | cart 含 EST-1 | 1. POST /api/cart/update | itemId=EST-1, quantity=0 | code=200；商品被移除 | P2 | Cart |
| CRT-011 | 更新数量为负数移除商品 | cart 含 EST-1 | 1. POST /api/cart/update | itemId=EST-1, quantity=-1 | code=200；商品被移除（quantity<1 触发移除） | P2 | Cart |
| CRT-012 | 更新数量缺 quantity 参数 | 无 | 1. POST /api/cart/update | itemId=EST-1 | HTTP 400；code=400 | P3 | Cart |
| CRT-013 | 清空购物车 | cart 含多个商品 | 1. POST /api/cart/clear | 无 | code=200；message="购物车已清空"；cart.itemMap 为空 | P1 | Cart |
| CRT-014 | 购物车小计计算正确 | cart 含 EST-1(qty=2,listprice=10) + EST-2(qty=1,listprice=20) | 1. GET /api/cart/ | 无 | getSubTotal()=40.00 | P2 | Cart |
| CRT-015 | 购物车 isEmpty 判断 | 空购物车 | 1. GET /api/cart/ | 无 | cart.isEmpty()=true | P3 | Cart |

---

## 五、订单模块（Order）

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| ORD-001 | 创建订单成功 | 已登录；cart 含商品 | 1. POST /api/orders | body=Order（含收货地址、信用卡等）；session.user 与 cart 已设置 | code=200；message="订单创建成功"；data 为新 Order；cart 被清空；库存被扣减；初始状态 P | P0 | Order |
| ORD-002 | 未登录创建订单 | session 无 user | 1. POST /api/orders | 合法 body | code=401；message="请先登录" | P0 | Order |
| ORD-003 | 购物车为空创建订单 | 已登录但 cart 为空 | 1. POST /api/orders | 合法 body | code=400；message="购物车为空" | P0 | Order |
| ORD-004 | 购物车不存在创建订单 | 已登录 session 无 cart | 1. POST /api/orders | 合法 body | code=400；message="购物车为空" | P1 | Order |
| ORD-005 | 订单创建失败 | orderService.createOrder 返回 null | 1. POST /api/orders | 合法 body | code=500；message="订单创建失败"；cart 未被清空 | P1 | Order |
| ORD-006 | 创建订单 userid 来自 session | 已登录 user.userid=ACID | 1. POST /api/orders | body.userid=other | order.userid=ACID（来自 session）；不信任前端 | P2 | Order |
| ORD-007 | 创建订单 totalprice 来自 cart | cart 小计=100.00 | 1. POST /api/orders | body.totalprice=999 | order.totalprice=100.00（来自 cart.getSubTotal） | P2 | Order |
| ORD-008 | 获取全部订单 | 无 | 1. GET /api/orders/ | 无 | code=200；data 为列表 | P1 | Order |
| ORD-009 | 按 orderid 查询订单 | 订单存在 | 1. GET /api/orders/1001 | orderid=1001 | code=200；data 为 Order | P1 | Order |
| ORD-010 | 查询订单不存在 | 无 | 1. GET /api/orders/9999 | orderid=9999 | code=404；message="订单不存在" | P2 | Order |
| ORD-011 | 查询我的订单 | 已登录 | 1. GET /api/orders/my | session.user.userid=ACID | code=200；data 为该用户订单列表 | P1 | Order |
| ORD-012 | 未登录查询我的订单 | session 无 user | 1. GET /api/orders/my | 无 | code=401；message="请先登录" | P1 | Order |
| ORD-013 | 按用户 id 查询订单 | 无登录校验 | 1. GET /api/orders/user/ACID | userid=ACID | code=200；data 为列表 | P2 | Order |
| ORD-014 | 更新订单状态成功 | 已登录 | 1. PUT /api/orders/1001/status | status=S | code=200 | P1 | Order |
| ORD-015 | 更新订单状态失败 | updateOrderStatus 返回 false | 1. PUT /api/orders/1001/status | status=S | code=500 | P2 | Order |
| ORD-016 | 获取订单商品项列表 | 订单存在 | 1. GET /api/orders/1001/items | orderid=1001 | code=200；data 为 OrderItem 列表 | P2 | Order |
| ORD-017 | 获取订单状态历史 | 订单存在 | 1. GET /api/orders/1001/status/history | orderid=1001 | code=200；data 为 OrderStatus 列表 | P2 | Order |
| ORD-018 | 订单状态码映射 | 创建订单后查询历史 | 1. 查询 status | 初始 status="P" | 状态历史含一条 P 记录 | P3 | Order |
| ORD-019 | OrderItem linenum 自增 | cart 含 3 个商品 | 1. 创建订单 | 无 | OrderItem.linenum 分别为 1,2,3 | P3 | Order |
| ORD-020 | OrderItem unitprice 来自 item | item.listprice=10.00 | 1. 创建订单 | 无 | OrderItem.unitprice=10.00 | P3 | Order |

---

## 六、心愿单模块（Wishlist）

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| WSH-001 | 获取空心愿单 | session 无 wishlist | 1. GET /api/wishlist/ | 无 | code=200；data 为空 Wishlist | P1 | Wishlist |
| WSH-002 | 添加商品到心愿单 | 产品 FI-SW-01 存在 | 1. POST /api/wishlist/add/FI-SW-01 | productId=FI-SW-01 | code=200；message="已添加到心愿单"；wishlist 含该产品 | P0 | Wishlist |
| WSH-003 | 重复添加不覆盖 | wishlist 已含 FI-SW-01 | 1. POST /api/wishlist/add/FI-SW-01 | productId=FI-SW-01 | code=200；putIfAbsent 不覆盖；wishlist 仍只有一条 | P2 | Wishlist |
| WSH-004 | 添加不存在商品 | productService 返回 null | 1. POST /api/wishlist/add/XXX | productId=XXX | code=404；message="商品不存在" | P1 | Wishlist |
| WSH-005 | 从心愿单移除商品 | wishlist 含 FI-SW-01 | 1. POST /api/wishlist/remove/FI-SW-01 | productId=FI-SW-01 | code=200；message="已从心愿单移除" | P1 | Wishlist |
| WSH-006 | 移除不存在的商品 | wishlist 不含该商品 | 1. POST /api/wishlist/remove/XXX | productId=XXX | code=200；message="已从心愿单移除"（仍返回成功） | P2 | Wishlist |
| WSH-007 | 清空心愿单 | wishlist 含多个商品 | 1. POST /api/wishlist/clear | 无 | code=200；message="心愿单已清空"；productMap 为空 | P1 | Wishlist |

---

## 七、分类模块（Category）

> **注意**：CategoryController 是公开接口，但失败时返回 code=**503**（与其他控制器 500 不同）

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| CAT-001 | 获取全部分类 | 无 | 1. GET /api/categories/ | 无 | code=200；data 为分类列表 | P1 | Category |
| CAT-002 | 按 catid 查询分类存在 | FISH 存在 | 1. GET /api/categories/FISH | catid=FISH | code=200；data.catid=FISH | P1 | Category |
| CAT-003 | 按 catid 查询分类不存在 | 无 | 1. GET /api/categories/XXX | catid=XXX | code=404；message="分类不存在" | P2 | Category |
| CAT-004 | 创建分类成功 | 无 | 1. POST /api/categories/ | body={catid:TEST,name:测试} | code=200；message="创建成功" | P1 | Category |
| CAT-005 | 创建分类缺 catid | 无 | 1. POST /api/categories/ | 缺 catid | code=400；message 含 catid（"分类ID不能为空"） | P2 | Category |
| CAT-006 | 创建分类缺 name | 无 | 1. POST /api/categories/ | 缺 name | code=400；message 含 name（"分类名称不能为空"） | P2 | Category |
| CAT-007 | 创建分类失败返回 503 | insert=0 | 1. POST /api/categories/ | 合法 body | code=503；message="创建失败" | P1 | Category |
| CAT-008 | 更新分类成功 | 分类存在 | 1. PUT /api/categories/FISH | catid=FISH, body=新分类 | code=200；message="更新成功"；强制 setCatid | P1 | Category |
| CAT-009 | 更新分类失败返回 503 | update=0 | 1. PUT /api/categories/FISH | 合法 body | code=503；message="更新失败" | P2 | Category |
| CAT-010 | 删除分类成功 | 分类存在 | 1. DELETE /api/categories/FISH | catid=FISH | code=200；message="删除成功" | P1 | Category |
| CAT-011 | 删除分类失败返回 503 | delete=0 | 1. DELETE /api/categories/XXX | catid=XXX | code=503；message="删除失败" | P2 | Category |
| CAT-012 | 更新分类 @Valid 校验 | 无 | 1. PUT /api/categories/FISH | name 为空 | code=400；校验失败 | P3 | Category |

---

## 八、产品模块（Product）

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| PRD-001 | 获取全部产品 | 无 | 1. GET /api/products/ | 无 | code=200；data 为产品列表 | P1 | Product |
| PRD-002 | 按分类获取产品 | 分类 FISH 存在 | 1. GET /api/products/category/FISH | category=FISH | code=200；data 为该分类产品 | P1 | Product |
| PRD-003 | 搜索产品有关键字 | 数据库含匹配产品 | 1. GET /api/products/search | keyword=fish | code=200；data 为匹配列表 | P1 | Product |
| PRD-004 | 搜索产品缺 keyword | 无 | 1. GET /api/products/search | 无 keyword | HTTP 400；code=400；message 含"缺少必需参数: keyword" | P2 | Product |
| PRD-005 | 搜索产品无匹配 | 无匹配数据 | 1. GET /api/products/search | keyword=zzz | code=200；data 为空列表 | P2 | Product |
| PRD-006 | 按 productid 查询产品 | 产品存在 | 1. GET /api/products/FI-SW-01 | productid=FI-SW-01 | code=200；data 含 items 字段 | P1 | Product |
| PRD-007 | 查询产品不存在 | 无 | 1. GET /api/products/XXX | productid=XXX | code=404；message="产品不存在" | P2 | Product |
| PRD-008 | 获取产品商品项列表 | 产品存在 | 1. GET /api/products/FI-SW-01/items | productid=FI-SW-01 | code=200；data 为 Item 列表 | P2 | Product |
| PRD-009 | 创建产品成功 | 无 | 1. POST /api/products/ | body={productid:NEW,category:FISH,name:新品} | code=200；message="创建成功" | P1 | Product |
| PRD-010 | 创建产品缺 productid | 无 | 1. POST /api/products/ | 缺 productid | code=400；message 含 productid（"产品ID不能为空"） | P2 | Product |
| PRD-011 | 创建产品缺 category | 无 | 1. POST /api/products/ | 缺 category | code=400；message 含 category（"分类ID不能为空"） | P2 | Product |
| PRD-012 | 创建产品缺 name | 无 | 1. POST /api/products/ | 缺 name | code=400；message 含 name（"产品名称不能为空"） | P2 | Product |
| PRD-013 | 创建产品失败 | insert=0 | 1. POST /api/products/ | 合法 body | code=500；message="创建失败" | P2 | Product |
| PRD-014 | 创建产品默认状态 ON_SALE | 无 | 1. POST /api/products/ | 不传 status | 实体默认 status="ON_SALE" | P3 | Product |
| PRD-015 | 更新产品成功 | 产品存在 | 1. PUT /api/products/FI-SW-01 | productid=FI-SW-01, body=新产品 | code=200；强制 setProductid | P1 | Product |
| PRD-016 | 更新产品失败 | update=0 | 1. PUT /api/products/FI-SW-01 | 合法 body | code=500 | P2 | Product |
| PRD-017 | 删除产品成功 | 产品存在 | 1. DELETE /api/products/FI-SW-01 | productid=FI-SW-01 | code=200 | P1 | Product |
| PRD-018 | 删除产品失败 | delete=0 | 1. DELETE /api/products/XXX | productid=XXX | code=500 | P2 | Product |

---

## 九、商品项模块（Item）

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| ITM-001 | 获取全部商品项 | 无 | 1. GET /api/items/ | 无 | code=200；data 为列表 | P1 | Item |
| ITM-002 | 按 itemid 查询商品项 | EST-1 存在 | 1. GET /api/items/EST-1 | itemid=EST-1 | code=200；data.itemid=EST-1 | P1 | Item |
| ITM-003 | 查询商品项不存在 | 无 | 1. GET /api/items/XXX | itemid=XXX | code=404；message="商品项不存在" | P2 | Item |
| ITM-004 | 创建商品项成功 | 无 | 1. POST /api/items/ | body={itemid:EST-99,productid:FI-SW-01,qty:50} | code=200；message="创建成功" | P1 | Item |
| ITM-005 | 创建商品项缺 itemid | 无 | 1. POST /api/items/ | 缺 itemid | code=400；message 含 itemid | P2 | Item |
| ITM-006 | 创建商品项缺 productid | 无 | 1. POST /api/items/ | 缺 productid | code=400；message 含 productid | P2 | Item |
| ITM-007 | 创建商品项 qty 为负 | 无 | 1. POST /api/items/ | qty=-1 | code=400；校验失败含 qty（@Min(0) "库存数量不能为负数"） | P2 | Item |
| ITM-008 | 创建商品项失败 | insert=0 | 1. POST /api/items/ | 合法 body | code=500 | P2 | Item |
| ITM-009 | 更新商品项成功 | 已存在 | 1. PUT /api/items/EST-1 | itemid=EST-1, body=新 Item | code=200；强制 setItemid | P1 | Item |
| ITM-010 | 更新商品项 @Valid 校验 | 无 | 1. PUT /api/items/EST-1 | productid 为空 | code=400 | P3 | Item |
| ITM-011 | 更新商品项失败 | update=0 | 1. PUT /api/items/EST-1 | 合法 body | code=500 | P2 | Item |
| ITM-012 | 删除商品项成功 | 已存在 | 1. DELETE /api/items/EST-1 | itemid=EST-1 | code=200 | P1 | Item |
| ITM-013 | 删除商品项失败 | delete=0 | 1. DELETE /api/items/XXX | itemid=XXX | code=500 | P2 | Item |

---

## 十、轮播图模块（Banner）

> **注意**：BannerController 路径 /api/banners 不在 /api/admin/** 范围，不受 AdminInterceptor 保护

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| BNR-001 | 获取全部轮播图 | 无 | 1. GET /api/banners/ | 无 | code=200；data 为列表 | P1 | Banner |
| BNR-002 | 获取激活轮播图 | 无 | 1. GET /api/banners/active | 无 | code=200；data 仅含 active=true 的记录，按 sort_order 排序 | P1 | Banner |
| BNR-003 | 创建轮播图（无图片） | 无 | 1. POST /api/banners/ | title=促销, sortOrder=1, active=true | code=200；message="创建成功" | P1 | Banner |
| BNR-004 | 创建轮播图（带图片） | 无 | 1. POST multipart/form-data | title=促销 + MockMultipartFile image | code=200；图片存到 uploads/banner_UUID.ext | P2 | Banner |
| BNR-005 | 创建轮播图缺 title | 无 | 1. POST /api/banners/ | 缺 title | HTTP 400；code=400；message 含"缺少必需参数: title" | P2 | Banner |
| BNR-006 | 创建轮播图默认值 | 无 | 1. POST /api/banners/ | 只传 title | sortOrder 默认 0；active 默认 true | P3 | Banner |
| BNR-007 | 创建轮播图失败 | insert=0 | 1. POST /api/banners/ | 合法参数 | code=500 | P2 | Banner |
| BNR-008 | 创建轮播图图片上传异常 | IO 异常 | 1. POST multipart | title + 图片 | code=500；message 含"图片上传失败" | P2 | Banner |
| BNR-009 | 更新轮播图成功 | 轮播图存在 | 1. PUT /api/banners/1 | id=1, title=新标题 | code=200 | P1 | Banner |
| BNR-010 | 更新轮播图不存在 | 无 | 1. PUT /api/banners/999 | id=999 | code=404；message="轮播图不存在" | P2 | Banner |
| BNR-011 | 更新轮播图替换图片 | 已存在图片 | 1. PUT multipart | 新 image | 新图片路径写入；code=200 | P3 | Banner |
| BNR-012 | 删除轮播图成功 | 已存在 | 1. DELETE /api/banners/1 | id=1 | code=200 | P1 | Banner |
| BNR-013 | 删除轮播图失败 | delete=0 | 1. DELETE /api/banners/999 | id=999 | code=500 | P2 | Banner |

---

## 十一、聊天机器人模块（Chatbot）

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| CHAT-001 | 发送消息成功（规则模式） | chatbotConfig.apiEnabled=false 或无 apiKey | 1. POST /api/chatbot/message | message=你好 | code=200；data 为 ChatMessage（role=assistant）；识别 greeting 意图；history 含 user 与 assistant 两条 | P0 | Chatbot |
| CHAT-002 | 发送消息为空 | 无 | 1. POST /api/chatbot/message | message="" | code=400；message="消息不能为空哦~" | P1 | Chatbot |
| CHAT-003 | 发送消息为纯空格 | 无 | 1. POST /api/chatbot/message | message="   " | code=400；message="消息不能为空哦~" | P2 | Chatbot |
| CHAT-004 | 缺少 message 参数 | 无 | 1. POST /api/chatbot/message | 无 message | HTTP 400；code=400；message 含"缺少必需参数: message" | P2 | Chatbot |
| CHAT-005 | 发送 null 消息 | 无 | 1. POST /api/chatbot/message | message=null | code=400；message="消息不能为空哦~" | P2 | Chatbot |
| CHAT-006 | 聊天历史超过 20 条截断 | history 已含 20 条 | 1. POST /api/chatbot/message | message=新消息 | history 截断为最后 20 条；新增消息正常加入 | P2 | Chatbot |
| CHAT-007 | 识别 greeting 意图 | 无 | 1. 发送多种问候语 | message=你好/hi/hello/嗨 | 返回 greeting 类响应 | P2 | Chatbot |
| CHAT-008 | 识别 farewell 意图 | 无 | 1. 发送再见 | message=再见/拜拜/bye | 返回 farewell 类响应 | P2 | Chatbot |
| CHAT-009 | 识别 thanks 意图 | 无 | 1. 发送感谢 | message=谢谢/感谢 | 返回 thanks 类响应 | P3 | Chatbot |
| CHAT-010 | 识别 order_query 意图（含订单号） | 订单 1001 存在 | 1. 发送查询 | message=我的订单 1001 怎么样了 | 调用 orderService.getOrderById；返回订单详情；状态文案映射 P→⏳待处理 | P2 | Chatbot |
| CHAT-011 | 识别 order_query 订单不存在 | 订单 9999 不存在 | 1. 发送查询 | message=查下订单 9999 | 返回查不到该订单的提示 | P2 | Chatbot |
| CHAT-012 | 识别 order_query 无订单号 | 无 | 1. 发送查询 | message=我的订单 | 返回引导用户提供订单号的提示 | P3 | Chatbot |
| CHAT-013 | 识别 product_info 意图 | 无 | 1. 发送咨询 | message=有什么特价商品 | 返回 product_info 类响应 | P2 | Chatbot |
| CHAT-014 | 识别 inventory_query 意图 | 无 | 1. 发送咨询 | message=有现货吗 | 返回库存相关响应 | P3 | Chatbot |
| CHAT-015 | 识别 shipping_info 意图 | 无 | 1. 发送咨询 | message=运费多少 | 返回运费相关响应 | P3 | Chatbot |
| CHAT-016 | 识别 return_policy 意图 | 无 | 1. 发送咨询 | message=可以退货吗 | 返回退货政策响应 | P3 | Chatbot |
| CHAT-017 | 识别 account_help 意图 | 无 | 1. 发送咨询 | message=忘记密码 | 返回账号帮助响应 | P3 | Chatbot |
| CHAT-018 | 识别 pet_care 意图 | 无 | 1. 发送咨询 | message=怎么养鱼 | 返回宠物饲养响应 | P3 | Chatbot |
| CHAT-019 | 识别 dog_care 意图 | 无 | 1. 发送咨询 | message=狗狗生病了 | 返回狗狗护理响应 | P3 | Chatbot |
| CHAT-020 | 识别 cat_care 意图 | 无 | 1. 发送咨询 | message=猫咪呕吐 | 返回猫咪护理响应 | P3 | Chatbot |
| CHAT-021 | unknown 意图兜底 | 无 | 1. 发送无意义消息 | message=asdfgh | 返回 unknown 兜底响应 | P3 | Chatbot |
| CHAT-022 | 获取聊天历史 | session 有 history | 1. GET /api/chatbot/history | 无 | code=200；data 为历史消息列表 | P1 | Chatbot |
| CHAT-023 | 获取空聊天历史 | session 无 history | 1. GET /api/chatbot/history | 无 | code=200；data 为空列表 | P2 | Chatbot |
| CHAT-024 | 清空聊天历史 | session 有 history | 1. POST /api/chatbot/clear | 无 | code=200；message="聊天记录已清空"；session.removeAttribute 被调用 | P1 | Chatbot |
| CHAT-025 | API 模式调用 OpenAI | apiEnabled=true 且 apiKey 有效 | 1. 发送消息（mock RestTemplate） | message=你好 | 调用 callOpenAI；返回 AI 响应 | P2 | Chatbot |
| CHAT-026 | API 模式异常降级到规则 | callOpenAI 抛异常 | 1. 发送消息 | message=你好 | 降级到规则引擎；返回规则响应 | P2 | Chatbot |
| CHAT-027 | getCategoryKnowledgeTips 已知分类 | 无 | 1. 调用方法 | categoryId=FISH | 返回鱼类知识列表 | P3 | Chatbot |
| CHAT-028 | getCategoryKnowledgeTips 未知分类 | 无 | 1. 调用方法 | categoryId=UNKNOWN | 返回空列表 | P3 | Chatbot |

---

## 十二、页面路由模块（Page）

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| PAGE-001 | 访问首页 | 无 | 1. GET / | 无 | 视图名 "index"；model 含 categories、products、recommendations(8)、recentViews | P1 | Page |
| PAGE-002 | 访问分类页 | 分类 FISH 存在 | 1. GET /category/FISH | catid=FISH | 视图名 "category"；model 含 category、products | P1 | Page |
| PAGE-003 | 访问产品详情页 | 产品存在 | 1. GET /product/FI-SW-01 | productid=FI-SW-01 | 视图名 "product"；product 含 items；recordView 被调用；relatedProducts(4) | P1 | Page |
| PAGE-004 | 访问不存在的产品详情页 | 产品不存在 | 1. GET /product/XXX | productid=XXX | 视图名 "product"；product 为 null（不抛异常） | P2 | Page |
| PAGE-005 | 访问探索页 | 无 | 1. GET /explore | 无 | 视图名 "explore"；含 recommendations、remaining、totalCount、recentViews | P2 | Page |
| PAGE-006 | 访问登录页 | 无 | 1. GET /login | 无 | 视图名 "login" | P1 | Page |
| PAGE-007 | 访问注册页 | 无 | 1. GET /register | 无 | 视图名 "register" | P1 | Page |
| PAGE-008 | 访问购物车页 | session 无 cart | 1. GET /cart | 无 | 视图名 "cart"；model 塞入 new Cart() | P2 | Page |
| PAGE-009 | 访问结算页已登录 | session 有 user 和 cart | 1. GET /checkout | user 与 cart 已设置 | 视图名 "checkout" | P1 | Page |
| PAGE-010 | 访问结算页未登录 | session 无 user | 1. GET /checkout | 无 user | 重定向 redirect:/login | P1 | Page |
| PAGE-011 | 访问结算页购物车空 | session 有 user 但 cart 空 | 1. GET /checkout | cart.isEmpty()=true | 重定向 redirect:/cart | P2 | Page |
| PAGE-012 | 访问订单列表页已登录 | session 有 user | 1. GET /orders | user 已设置 | 视图名 "orders" | P1 | Page |
| PAGE-013 | 访问订单列表页未登录 | session 无 user | 1. GET /orders | 无 user | 重定向 redirect:/login | P1 | Page |
| PAGE-014 | 访问订单详情页已登录 | 订单存在 | 1. GET /order/1001 | orderid=1001, user 已设置 | 视图名 "order-detail"；order 含 orderItems | P1 | Page |
| PAGE-015 | 访问订单详情页未登录 | session 无 user | 1. GET /order/1001 | 无 user | 重定向 redirect:/login | P1 | Page |
| PAGE-016 | 访问个人中心已登录 | session 有 user | 1. GET /profile | user 已设置 | 视图名 "profile" | P1 | Page |
| PAGE-017 | 访问个人中心未登录 | session 无 user | 1. GET /profile | 无 user | 重定向 redirect:/login | P1 | Page |
| PAGE-018 | 搜索页 | 无 | 1. GET /search | keyword=fish | 视图名 "search"；调用 productService.searchProducts | P2 | Page |
| PAGE-019 | 访问管理后台管理员 | session user.role=ADMIN | 1. GET /admin | user 是 ADMIN | 视图名 "admin" | P1 | Page |
| PAGE-020 | 访问管理后台未登录 | session 无 user | 1. GET /admin | 无 user | 重定向 redirect:/login | P1 | Page |
| PAGE-021 | 访问管理后台普通用户 | session user.role=USER | 1. GET /admin | role=USER | 重定向 redirect:/ | P1 | Page |
| PAGE-022 | 登出 | session 已登录 | 1. GET /logout | 无 | 重定向 redirect:/login；session.invalidate() 被调用 | P1 | Page |
| PAGE-023 | 浏览历史记录去重前移 | 已浏览 FI-SW-01 | 1. 再次访问 /product/FI-SW-01 | 同一产品 | view_history 中该产品移到最前；不重复 | P3 | Page |

---

## 十三、Service 层模块

### 13.1 AccountService

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| SVC-001 | login 成功 | mapper 返回 Account | 1. 调用 accountService.login | username=j2ee, password=j2ee | 返回 Account 对象；mapper.getAccountByUsernameAndPassword 被调用 | P1 | AccountService |
| SVC-002 | login 失败 | mapper 返回 null | 1. 调用 login | username=j2ee, password=wrong | 返回 null | P1 | AccountService |
| SVC-003 | register 成功 | mapper.insert 都返回 1 | 1. 调用 register | 完整 Account（含 signOn、profile） | 返回 true；insertAccount/insertSignOn/insertProfile 均被调用 | P1 | AccountService |
| SVC-004 | register 异常回滚 | insertSignOn 抛异常 | 1. 调用 register | 完整 Account | catch 异常返回 false；事务回滚 | P2 | AccountService |
| SVC-005 | register 无 signOn | account.signOn=null | 1. 调用 register | 仅 Account | insertSignOn 不被调用；返回 true | P3 | AccountService |
| SVC-006 | register 无 profile | account.profile=null | 1. 调用 register | 仅 Account | insertProfile 不被调用；返回 true | P3 | AccountService |
| SVC-007 | updateAccount 成功 | mapper.update 都返回 1 | 1. 调用 updateAccount | Account | 返回 true；updateAccount/updateSignOn/updateProfile 被调用 | P1 | AccountService |
| SVC-008 | updateAccount 异常 | updateSignOn 抛异常 | 1. 调用 updateAccount | Account | 返回 false | P2 | AccountService |
| SVC-009 | deleteAccount 成功 | mapper.delete 都返回 1 | 1. 调用 deleteAccount | userid=ACID | 返回 true；deleteSignOn/deleteProfile/deleteAccount 被调用 | P1 | AccountService |
| SVC-010 | deleteAccount 异常 | deleteProfile 抛异常 | 1. 调用 deleteAccount | userid=ACID | 返回 false | P2 | AccountService |
| SVC-011 | getAccountByUsername | mapper 返回 Account | 1. 调用 getAccountByUsername | userid=j2ee | 返回 Account | P2 | AccountService |
| SVC-012 | getAllUsers | 无 | 1. 调用 getAllUsers | 无 | 调用 selectList(null)；返回列表 | P3 | AccountService |

### 13.2 OrderService

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| SVC-020 | createOrder 成功 | cart 含商品 | 1. 调用 createOrder | userid, cart, order | 返回 Order；order.orderid 非空；order.status="P"；cart 中每个 item 调用 updateInventory 扣库存；insertOrderStatus(status="P") | P0 | OrderService |
| SVC-021 | createOrder 设置 totalprice | cart.getSubTotal=100 | 1. 调用 createOrder | userid, cart, order | order.totalprice=100.00 | P2 | OrderService |
| SVC-022 | createOrder 设置 orderdate | 无 | 1. 调用 createOrder | userid, cart, order | order.orderdate 为 new Date()（当前时间附近） | P3 | OrderService |
| SVC-023 | createOrder linenum 自增 | cart 含 3 个商品 | 1. 调用 createOrder | userid, cart, order | OrderItem.linenum 为 1,2,3 | P3 | OrderService |
| SVC-024 | createOrder unitprice 取自 item | item.listprice=10.00 | 1. 调用 createOrder | userid, cart, order | OrderItem.unitprice=10.00 | P3 | OrderService |
| SVC-025 | createOrder 异常返回 null | insertOrder 抛异常 | 1. 调用 createOrder | userid, cart, order | 返回 null；事务回滚 | P1 | OrderService |
| SVC-026 | updateOrderStatus 成功 | 无 | 1. 调用 updateOrderStatus | orderid, status="S" | 返回 true；updateOrderStatus + insertOrderStatus 被调用 | P1 | OrderService |
| SVC-027 | updateOrderStatus 异常 | insertOrderStatus 抛异常 | 1. 调用 updateOrderStatus | orderid, status | 返回 false | P2 | OrderService |
| SVC-028 | getOrderById | 无 | 1. 调用 getOrderById | orderid | 委托 mapper | P2 | OrderService |
| SVC-029 | getAllOrders | 无 | 1. 调用 getAllOrders | 无 | 委托 mapper | P3 | OrderService |

### 13.3 ProductService / CategoryService / ItemService / BannerService

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| SVC-040 | ProductService.searchProducts | 无 | 1. 调用 searchProducts | keyword=fish | 委托 productMapper.searchProducts | P2 | ProductService |
| SVC-041 | ProductService.updateProductStatus | 无 | 1. 调用 updateProductStatus | productid, status | 委托 mapper；@Transactional | P2 | ProductService |
| SVC-042 | ItemService.updateInventory | 无 | 1. 调用 updateInventory | itemid, quantity | 委托 mapper；@Transactional | P2 | ItemService |
| SVC-043 | BannerService.getActiveBanners | 无 | 1. 调用 getActiveBanners | 无 | 执行 @Select 查询 active=true 按 sort_order 排序 | P2 | BannerService |
| SVC-044 | BannerService CRUD | 无 | 1. 调用 insert/updateById/deleteById | Banner | 委托 MyBatis-Plus 通用方法 | P3 | BannerService |
| SVC-045 | CategoryService CRUD | 无 | 1. 调用各方法 | Category | 委托 mapper | P3 | CategoryService |

### 13.4 RecommendationService

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| SVC-060 | getRecommendations 排除 cart 商品 | session.cart 含 FI-SW-01 | 1. 调用 getRecommendations | session, limit=8 | 推荐结果不含 cart 中的商品 | P2 | RecommendationService |
| SVC-061 | getRecommendations 排除 wishlist 商品 | session.wishlist 含 FI-SW-02 | 1. 调用 getRecommendations | session, limit=8 | 推荐结果不含 wishlist 中的商品 | P2 | RecommendationService |
| SVC-062 | getRecommendations 按浏览历史分类 | view_history 含多个 FISH 产品 | 1. 调用 getRecommendations | session, limit=8 | 优先推荐 FISH 分类商品 | P3 | RecommendationService |
| SVC-063 | getRecommendations 不足随机补充 | 浏览历史为空 | 1. 调用 getRecommendations | session, limit=8 | 随机补充至 8 条 | P3 | RecommendationService |
| SVC-064 | getRelatedProducts 同分类 | 产品 FI-SW-01 分类 FISH | 1. 调用 getRelatedProducts | productId, limit=4 | 返回同分类其他产品；不含自己 | P2 | RecommendationService |
| SVC-065 | getRelatedProducts 不足补充 | 同分类仅 1 个产品 | 1. 调用 getRelatedProducts | productId, limit=4 | 随机补充至 4 条 | P3 | RecommendationService |
| SVC-066 | recordView 添加浏览历史 | session 无 view_history | 1. 调用 recordView | session, product | view_history 含该产品 | P2 | RecommendationService |
| SVC-067 | recordView LRU 前移 | view_history 已含该产品 | 1. 再次 recordView | 同一 product | 该产品移到最前；不重复 | P3 | RecommendationService |
| SVC-068 | recordView 超过 20 条截断 | view_history 已 20 条 | 1. recordView 新产品 | 新 product | 截断为最后 20 条 | P3 | RecommendationService |
| SVC-069 | getRecentViews | session 有 view_history | 1. 调用 getRecentViews | session | 返回 view_history 列表 | P3 | RecommendationService |

---

## 十四、Domain 实体模块

### 14.1 Account

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| DOM-001 | isAdmin 角色为 ADMIN | account.role=ADMIN | 1. 调用 isAdmin() | 无 | 返回 true | P2 | Account |
| DOM-002 | isAdmin 角色为 USER | account.role=USER | 1. 调用 isAdmin() | 无 | 返回 false | P2 | Account |
| DOM-003 | isAdmin 角色为 null | account.role=null | 1. 调用 isAdmin() | 无 | 返回 false | P3 | Account |
| DOM-004 | setAdmin(true) 设置 ADMIN | 无 | 1. 调用 setAdmin(true) | true | role="ADMIN" | P3 | Account |
| DOM-005 | setAdmin(false) 设置 USER | 无 | 1. 调用 setAdmin(false) | false | role="USER" | P3 | Account |
| DOM-006 | getFullName 拼接 | firstname=张, lastname=三 | 1. 调用 getFullName() | 无 | 返回 "张 三" | P3 | Account |

### 14.2 Cart / CartItem

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| DOM-010 | Cart.addItem 新商品 | 空 cart | 1. 调用 addItem | item(EST-1) | itemMap 含 EST-1；quantity=1 | P1 | Cart |
| DOM-011 | Cart.addItem 重复商品数量+1 | cart 含 EST-1 qty=1 | 1. 调用 addItem | 同一 item | quantity=2 | P1 | Cart |
| DOM-012 | Cart.removeItemById 存在 | cart 含 EST-1 | 1. 调用 removeItemById | "EST-1" | 返回被移除的 CartItem；itemMap 不含 EST-1 | P2 | Cart |
| DOM-013 | Cart.removeItemById 不存在 | cart 不含 EST-2 | 1. 调用 removeItemById | "EST-2" | 返回 null | P2 | Cart |
| DOM-014 | Cart.setQuantity 正常 | cart 含 EST-1 qty=1 | 1. 调用 setQuantity | "EST-1", 5 | quantity=5 | P2 | Cart |
| DOM-015 | Cart.setQuantity 小于 1 移除 | cart 含 EST-1 | 1. 调用 setQuantity | "EST-1", 0 | 该商品被移除 | P2 | Cart |
| DOM-016 | Cart.getSubTotal 计算 | cart 含 EST-1(qty=2,listprice=10) | 1. 调用 getSubTotal() | 无 | 返回 20.00 | P2 | Cart |
| DOM-017 | Cart.isEmpty 空购物车 | 空 cart | 1. 调用 isEmpty() | 无 | 返回 true | P3 | Cart |
| DOM-018 | Cart.isEmpty 非空 | cart 含商品 | 1. 调用 isEmpty() | 无 | 返回 false | P3 | Cart |
| DOM-019 | Cart.clear 清空 | cart 含商品 | 1. 调用 clear() | 无 | itemMap 为空 | P2 | Cart |
| DOM-020 | CartItem.getTotalPrice | qty=2, listprice=10 | 1. 调用 getTotalPrice() | 无 | 返回 20.00 | P3 | CartItem |

### 14.3 Wishlist

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| DOM-030 | Wishlist.addProduct 新增 | 空 wishlist | 1. 调用 addProduct | product(FI-SW-01) | productMap 含 FI-SW-01 | P2 | Wishlist |
| DOM-031 | Wishlist.addProduct putIfAbsent 不覆盖 | 已含 FI-SW-01 | 1. 再次 addProduct | 新 product 同 id | 不覆盖原值 | P3 | Wishlist |
| DOM-032 | Wishlist.removeProduct | 含 FI-SW-01 | 1. 调用 removeProduct | "FI-SW-01" | productMap 不含 | P3 | Wishlist |
| DOM-033 | Wishlist.clear | 含商品 | 1. 调用 clear() | 无 | productMap 为空 | P3 | Wishlist |

### 14.4 其他实体校验

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| DOM-040 | Product 默认 status ON_SALE | new Product() | 1. 检查 status 字段 | 无 | status="ON_SALE" | P3 | Product |
| DOM-041 | Order @NotBlank 校验 | 无 | 1. @Valid 校验 | shipaddr1 为空 | 校验失败 | P3 | Order |
| DOM-042 | Order totalprice @NotNull | 无 | 1. @Valid 校验 | totalprice=null | 校验失败 | P3 | Order |
| DOM-043 | OrderItem quantity @Min(1) | 无 | 1. @Valid 校验 | quantity=0 | 校验失败 | P3 | OrderItem |
| DOM-044 | OrderItem unitprice @NotNull | 无 | 1. @Valid 校验 | unitprice=null | 校验失败 | P3 | OrderItem |
| DOM-045 | Item qty @Min(0) | 无 | 1. @Valid 校验 | qty=-1 | 校验失败含"库存数量不能为负数" | P3 | Item |
| DOM-046 | Category @NotBlank catid | 无 | 1. @Valid 校验 | catid 为空 | 校验失败"分类ID不能为空" | P3 | Category |

---

## 十五、配置与启动模块

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| CFG-001 | Spring 容器启动成功 | 数据库可连接 | 1. 运行 @SpringBootTest | 无 | ApplicationContext 加载完成；无异常 | P0 | Application |
| CFG-002 | MyBatis-Plus 配置生效 | 无 | 1. 检查配置 | 无 | id-type=auto；map-underscore-to-camel-case=true | P2 | Config |
| CFG-003 | JwtUtil Bean 加载 | 无 | 1. 启动应用 | 无 | jwtUtil Bean 存在；secret 与 expiration 已注入 | P1 | Config |
| CFG-004 | JwtAuthFilter 注册 /api/* | 无 | 1. 检查 FilterRegistrationBean | 无 | filter 注册 URL 模式 /api/* | P2 | Config |
| CFG-005 | AdminInterceptor 拦截 /api/admin/** | 无 | 1. 检查 WebMvcConfig | 无 | interceptor addPathPatterns("/api/admin/**") | P2 | Config |
| CFG-006 | 静态资源映射 /uploads/** | 无 | 1. 检查 WebMvcConfig | 无 | file:///workspace/uploads/ 映射到 /uploads/** | P3 | Config |
| CFG-007 | 测试环境 H2 数据库 | 测试 profile | 1. 运行测试 | 无 | 使用 H2 内存库 jdbc:h2:mem:testdb;MODE=MYSQL | P1 | Config |
| CFG-008 | 测试加载 schema-h2.sql | 测试 profile | 1. 运行集成测试 | 无 | spring.sql.init.mode=always；加载 schema-locations | P1 | Config |

---

## 十六、集成与安全测试

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| INT-001 | 完整购物下单流程 | 已注册用户 | 1. 登录 2. 浏览商品 3. 加购物车 4. 下单 5. 查看订单 | 全流程数据 | 各步骤均成功；订单创建；库存扣减；购物车清空 | P0 | Integration |
| INT-002 | 未登录访问受保护 API | 无 | 1. POST /api/orders 无 session | 无 | code=401 | P1 | Security |
| INT-003 | 普通用户访问 Admin API | USER 登录 | 1. GET /api/admin/users | currentUser.role=USER | code=403；message="需要管理员权限" | P0 | Security |
| INT-004 | JWT token 跨请求保持 | 已登录获取 token | 1. 登录获取 token 2. 带 token 访问 /api/account/current | token | 第二步返回当前用户信息 | P1 | Security |
| INT-005 | 篡改 token 拒绝 | 已登录 | 1. 篡改 token payload 2. 访问受保护接口 | 篡改 token | validate 返回 false；currentUser 为 null；接口返回 401 | P1 | Security |
| INT-006 | /api/banners 未保护安全风险 | 无 | 1. 未登录 POST /api/banners/ | 合法参数 | 接口可被访问（已知设计缺陷，需关注） | P2 | Security |
| INT-007 | /api/orders/user/{userid} 无鉴权 | 无 | 1. 未登录 GET /api/orders/user/ACID | userid | 可查询任意用户订单（已知设计缺陷） | P2 | Security |
| INT-008 | SQL 注入防护 | 无 | 1. 搜索接口输入 SQL 关键字 | keyword=' OR 1=1 -- | 使用 MyBatis 参数化查询；无注入 | P2 | Security |
| INT-009 | XSS 防护（Thymeleaf 转义） | 无 | 1. 注册时 firstname 含 script 标签 | firstname=&lt;script&gt;alert(1)&lt;/script&gt; | 页面渲染时被转义；不执行脚本 | P2 | Security |
| INT-010 | 会话隔离 | 两个不同用户 | 1. 用户 A 加购物车 2. 用户 B 加购物车 3. 各自查询 | 不同 session | 各自购物车独立；不串数据 | P1 | Integration |
| INT-011 | 库存不足下单 | item.qty=1 | 1. 加 2 件到购物车 2. 下单 | qty=2 > 库存 | 下单成功但库存扣减为负（需关注业务规则）或失败 | P2 | Integration |
| INT-012 | 并发添加购物车 | 同一用户多请求 | 1. 并发 POST /api/cart/add | 多个请求 | ConcurrentHashMap 线程安全；无数据丢失 | P3 | Integration |

---

## 十七、异常与边界测试

| 用例编号 | 用例标题 | 前置条件 | 操作步骤 | 输入数据 | 预期结果 | 优先级 | 模块名称 |
|---|---|---|---|---|---|---|---|
| BND-001 | orderid 非数字 | 无 | 1. GET /api/orders/abc | orderid=abc | 触发类型转换异常；HTTP 400 | P2 | Boundary |
| BND-002 | quantity 非数字 | 无 | 1. POST /api/cart/update quantity=abc | quantity=abc | 类型转换异常；HTTP 400 | P2 | Boundary |
| BND-003 | 超长字符串输入 | 无 | 1. 注册时 username 超过 25 字符 | username=256 字符串 | 数据库截断或校验失败 | P3 | Boundary |
| BND-004 | SQL 特殊字符 | 无 | 1. 注册时 firstname 含特殊字符 | firstname=O'Brien | 正常存储；无 SQL 错误 | P3 | Boundary |
| BND-005 | 价格精度 | listprice=10.999 | 1. 创建 item | listprice=10.999 | DECIMAL(10,2) 截断为 11.00 或 10.99 | P3 | Boundary |
| BND-006 | 空请求体 | 无 | 1. POST /api/account/register 无 body | 无 body | HTTP 400；code=400 | P2 | Boundary |
| BND-007 | 错误 Content-Type | 无 | 1. POST application/xml 到 @RequestBody 接口 | XML 内容 | HTTP 415 或 400 | P3 | Boundary |
| BND-008 | 大文件上传 | 无 | 1. POST /api/admin/products 带超大图片 | 100MB 图片 | 受 spring.servlet.multipart 限制；返回错误 | P3 | Boundary |
| BND-009 | 并发下单同一商品 | 库存=1 | 1. 两个用户同时下单 | 并发请求 | 库存扣减可能超卖（需关注） | P2 | Boundary |
| BND-010 | 删除被引用的分类 | 分类下有产品 | 1. DELETE /api/admin/categories/FISH | catid=FISH | 无外键约束；分类被删除；产品仍存在但孤立 | P3 | Boundary |

---

## 附录 A：用例统计

| 模块 | 用例数 | P0 | P1 | P2 | P3 |
|---|---|---|---|---|---|
| Account | 24 | 3 | 9 | 9 | 3 |
| Common(Result/Jwt/Filter/Interceptor/ExceptionHandler) | 25 | 4 | 10 | 8 | 3 |
| Admin(分类/产品/商品项/订单/用户) | 47 | 0 | 18 | 21 | 8 |
| Cart | 15 | 1 | 6 | 6 | 2 |
| Order | 20 | 3 | 6 | 7 | 4 |
| Wishlist | 7 | 1 | 3 | 3 | 0 |
| Category | 12 | 0 | 6 | 5 | 1 |
| Product | 18 | 0 | 8 | 8 | 2 |
| Item | 13 | 0 | 5 | 7 | 1 |
| Banner | 13 | 0 | 5 | 6 | 2 |
| Chatbot | 28 | 1 | 3 | 9 | 15 |
| Page | 23 | 0 | 11 | 8 | 4 |
| Service 层 | 30 | 1 | 7 | 9 | 13 |
| Domain 实体 | 17 | 0 | 1 | 7 | 9 |
| 配置与启动 | 8 | 1 | 3 | 3 | 1 |
| 集成与安全 | 12 | 2 | 3 | 6 | 1 |
| 异常与边界 | 10 | 0 | 0 | 5 | 5 |
| **合计** | **322** | **17** | **100** | **120** | **74** |

---

## 附录 B：测试环境与数据准备

### B.1 测试账号
| 角色 | 用户名 | 密码 | role |
|---|---|---|---|
| 管理员 | j2ee | j2ee | ADMIN |
| 普通用户 | ACID | ACID | USER |

### B.2 测试数据（来自 schema.sql）
- **分类**：FISH, DOGS, CATS, REPTILES, BIRDS, SNACKS, SUPPLIES（共 7 个）
- **产品**：FI-SW-01...SP-005（共 29 个）
- **商品项**：EST-1...EST-32（共 32 个，qty=100/200/150...）
- **轮播图**：3 个

### B.3 测试技术栈
- JUnit 5 + Mockito + MockMvc + AssertJ
- H2 内存数据库（MODE=MYSQL）
- `@WebMvcTest` + `@MockitoBean` 模拟 Service 层
- `@SpringBootTest` 用于集成测试

### B.4 关键注意点
1. **CategoryController 失败返回 503**，其他控制器返回 500
2. **AdminInterceptor 仅拦截 /api/admin/****，/api/banners 未受保护
3. **JwtAuthFilter 不阻断请求**，仅设置 currentUser 属性
4. **Session 属性 key**：cart、wishlist、user、view_history、chat_history
5. **Order.createOrder 的 userid 来自 session**，不信任前端 body
6. **测试目录需创建 schema-h2.sql**（将 MySQL DDL 转 H2 兼容语法）
7. **OrderItem.linenum 从 1 开始自增**
8. **RecommendationService 用 shuffle**，断言用 contains 而非顺序
