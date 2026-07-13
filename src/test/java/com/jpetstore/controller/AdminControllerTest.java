package com.jpetstore.controller;

import com.jpetstore.domain.*;
import com.jpetstore.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private com.jpetstore.mapper.AccountMapper accountMapper;

    @MockitoBean
    private com.jpetstore.mapper.CategoryMapper categoryMapper;

    @MockitoBean
    private com.jpetstore.mapper.ProductMapper productMapper;

    @MockitoBean
    private com.jpetstore.mapper.ItemMapper itemMapper;

    @MockitoBean
    private com.jpetstore.mapper.OrderMapper orderMapper;

    private Account adminUser;
    private Account normalUser;

    @BeforeEach
    void setUp() {
        adminUser = new Account();
        adminUser.setUserid("admin");
        adminUser.setEmail("admin@jpetstore.com");
        adminUser.setFirstname("Admin");
        adminUser.setLastname("User");
        adminUser.setRole("ADMIN");

        normalUser = new Account();
        normalUser.setUserid("testuser");
        normalUser.setEmail("test@example.com");
        normalUser.setFirstname("Test");
        normalUser.setLastname("User");
        normalUser.setRole("USER");
    }

    // ==================== 权限测试 ====================

    @Test
    void testAccessAdminApiWithoutLogin() throws Exception {
        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/admin/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testAccessAdminApiAsNormalUser() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", normalUser);

        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/admin/categories")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 分类管理测试 ====================

    @Test
    void testGetAllCategories() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(categoryService.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/categories")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetCategoryById() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        Category category = new Category();
        category.setCatid("FISH");
        category.setName("鱼类");

        when(categoryService.getCategoryById("FISH")).thenReturn(category);

        mockMvc.perform(get("/api/admin/categories/FISH")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.catid").value("FISH"));
    }

    @Test
    void testCreateCategory() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(categoryService.insertCategory(any(Category.class))).thenReturn(1);

        String json = "{\"catid\":\"BIRDS\",\"name\":\"鸟类\",\"description\":\"各种宠物鸟\"}";

        mockMvc.perform(post("/api/admin/categories")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("创建成功"));
    }

    @Test
    void testUpdateCategory() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(categoryService.updateCategory(any(Category.class))).thenReturn(1);

        String json = "{\"name\":\"鱼类\",\"description\":\"各种观赏鱼\"}";

        mockMvc.perform(put("/api/admin/categories/FISH")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"));
    }

    @Test
    void testDeleteCategory() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(categoryService.deleteCategory("FISH")).thenReturn(1);

        mockMvc.perform(delete("/api/admin/categories/FISH")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    // ==================== 产品管理测试 ====================

    @Test
    void testGetAllProducts() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/products")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetProductById() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        Product product = new Product();
        product.setProductid("P001");
        product.setName("神仙鱼");

        when(productService.getProductById("P001")).thenReturn(product);
        when(itemService.getItemsByProductId("P001")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/products/P001")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.productid").value("P001"));
    }

    @Test
    void testCreateProduct() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(productService.insertProduct(any(Product.class))).thenReturn(1);

        String json = "{\"productid\":\"P002\",\"category\":\"FISH\",\"name\":\"虎鲨\",\"description\":\"大型海水鱼\",\"price\":99.99}";

        mockMvc.perform(post("/api/admin/products")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("创建成功"));
    }

    @Test
    void testUpdateProduct() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(productService.updateProduct(any(Product.class))).thenReturn(1);

        String json = "{\"productid\":\"P001\",\"category\":\"FISH\",\"name\":\"神仙鱼\",\"description\":\"来自澳大利亚\",\"price\":99.99}";

        mockMvc.perform(put("/api/admin/products/P001")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(productService.deleteProduct("P001")).thenReturn(1);

        mockMvc.perform(delete("/api/admin/products/P001")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    // ==================== 商品项管理测试 ====================

    @Test
    void testGetProductItems() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(itemService.getItemsByProductId("P001")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/products/P001/items")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testCreateItem() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(itemService.insertItem(any(Item.class))).thenReturn(1);

        String json = "{\"itemid\":\"EST-1\",\"productid\":\"P001\",\"listprice\":16.50,\"qty\":100}";

        mockMvc.perform(post("/api/admin/items")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("创建成功"));
    }

    @Test
    void testUpdateItem() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(itemService.updateItem(any(Item.class))).thenReturn(1);

        String json = "{\"productid\":\"P001\",\"listprice\":18.50,\"qty\":50}";

        mockMvc.perform(put("/api/admin/items/EST-1")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"));
    }

    @Test
    void testDeleteItem() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(itemService.deleteItem("EST-1")).thenReturn(1);

        mockMvc.perform(delete("/api/admin/items/EST-1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    void testUpdateInventory() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(itemService.updateInventory("EST-1", 200)).thenReturn(1);

        mockMvc.perform(put("/api/admin/items/EST-1/inventory")
                        .session(session)
                        .param("quantity", "200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("库存更新成功"));
    }

    // ==================== 订单管理测试 ====================

    @Test
    void testGetAllOrders() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(orderService.getAllOrders()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/orders")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetOrderById() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        Order order = new Order();
        order.setOrderid(1);
        order.setUserid("testuser");

        when(orderService.getOrderById(1)).thenReturn(order);
        when(orderService.getOrderItemsByOrderId(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/orders/1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orderid").value(1));
    }

    @Test
    void testUpdateOrderStatus() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(orderService.updateOrderStatus(1, "SHIPPED")).thenReturn(true);

        mockMvc.perform(put("/api/admin/orders/1/status")
                        .session(session)
                        .param("status", "SHIPPED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("状态更新成功"));
    }

    // ==================== 用户管理测试 ====================

    @Test
    void testGetAllUsers() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        mockMvc.perform(get("/api/admin/users")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetUserById() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        Account account = new Account();
        account.setUserid("testuser");
        account.setEmail("test@example.com");

        when(accountService.getAccountByUsername("testuser")).thenReturn(account);

        mockMvc.perform(get("/api/admin/users/testuser")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userid").value("testuser"));
    }

    @Test
    void testUpdateUser() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(accountService.updateAccount(any(Account.class))).thenReturn(true);

        String json = "{\"email\":\"updated@example.com\",\"firstname\":\"Updated\",\"lastname\":\"User\"," +
                "\"addr1\":\"123 St\",\"city\":\"City\",\"state\":\"ST\",\"zip\":\"12345\"," +
                "\"country\":\"USA\",\"phone\":\"555-1234\"}";

        mockMvc.perform(put("/api/admin/users/testuser")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"));
    }

    @Test
    void testDeleteUser() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(accountService.deleteAccount("testuser")).thenReturn(true);

        mockMvc.perform(delete("/api/admin/users/testuser")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    void testUpdateUserRole() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        Account account = new Account();
        account.setUserid("testuser");
        account.setRole("USER");

        when(accountService.getAccountByUsername("testuser")).thenReturn(account);
        when(accountService.updateAccount(any(Account.class))).thenReturn(true);

        mockMvc.perform(put("/api/admin/users/testuser/role")
                        .session(session)
                        .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("角色更新成功"));
    }

    // ==================== 以下为补充的测试用例 ====================

    // --- 权限测试补充 ---

    @Test
    void testAccessAdminOrdersApiWithoutLogin() throws Exception {
        when(orderService.getAllOrders()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testAccessAdminUsersApiAsNormalUser() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", normalUser);

        when(accountService.getAllUsers()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/admin/users")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testCreateCategoryAsNormalUser() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", normalUser);

        when(categoryService.insertCategory(any(Category.class))).thenReturn(1);

        mockMvc.perform(post("/api/admin/categories")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"catid\":\"TEST\",\"name\":\"Test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testDeleteUserAsNormalUser() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", normalUser);

        when(accountService.deleteAccount("testuser")).thenReturn(true);

        mockMvc.perform(delete("/api/admin/users/testuser")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // --- 分类管理测试补充 ---

    @Test
    void testGetCategoryByIdNotFound() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(categoryService.getCategoryById("NOTEXIST")).thenReturn(null);

        mockMvc.perform(get("/api/admin/categories/NOTEXIST")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("分类不存在"));
    }

    @Test
    void testCreateCategoryFailure() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(categoryService.insertCategory(any(Category.class))).thenReturn(0);

        String json = "{\"catid\":\"BIRDS\",\"name\":\"鸟类\"}";

        mockMvc.perform(post("/api/admin/categories")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("创建失败"));
    }

    @Test
    void testDeleteCategoryFailure() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(categoryService.deleteCategory("FISH")).thenReturn(0);

        mockMvc.perform(delete("/api/admin/categories/FISH")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("删除失败"));
    }

    // --- 产品管理测试补充 ---

    @Test
    void testGetAllProductsWithMultipleProducts() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        Product p1 = new Product();
        p1.setProductid("P001");
        p1.setName("Product1");
        Product p2 = new Product();
        p2.setProductid("P002");
        p2.setName("Product2");

        when(productService.getAllProducts()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/admin/products")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void testGetProductByIdNotFound() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(productService.getProductById("NOTEXIST")).thenReturn(null);

        mockMvc.perform(get("/api/admin/products/NOTEXIST")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void testCreateProductFailure() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(productService.insertProduct(any(Product.class))).thenReturn(0);

        String json = "{\"productid\":\"P002\",\"category\":\"FISH\",\"name\":\"Test\",\"price\":10.00}";

        mockMvc.perform(post("/api/admin/products")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void testDeleteProductFailure() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(productService.deleteProduct("P001")).thenReturn(0);

        mockMvc.perform(delete("/api/admin/products/P001")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // --- 商品项管理测试补充 ---

    @Test
    void testCreateItemFailure() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(itemService.insertItem(any(Item.class))).thenReturn(0);

        String json = "{\"itemid\":\"EST-1\",\"productid\":\"P001\",\"listprice\":16.50,\"qty\":100}";

        mockMvc.perform(post("/api/admin/items")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void testUpdateItemFailure() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(itemService.updateItem(any(Item.class))).thenReturn(0);

        String json = "{\"productid\":\"P001\",\"listprice\":18.50,\"qty\":50}";

        mockMvc.perform(put("/api/admin/items/EST-1")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void testDeleteItemFailure() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(itemService.deleteItem("EST-1")).thenReturn(0);

        mockMvc.perform(delete("/api/admin/items/EST-1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void testUpdateInventoryFailure() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(itemService.updateInventory("EST-1", 200)).thenReturn(0);

        mockMvc.perform(put("/api/admin/items/EST-1/inventory")
                        .session(session)
                        .param("quantity", "200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void testUpdateInventoryWithZeroQuantity() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(itemService.updateInventory("EST-1", 0)).thenReturn(1);

        mockMvc.perform(put("/api/admin/items/EST-1/inventory")
                        .session(session)
                        .param("quantity", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100, 9999})
    void testUpdateInventoryWithDifferentQuantities(int quantity) throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(itemService.updateInventory("EST-1", quantity)).thenReturn(1);

        mockMvc.perform(put("/api/admin/items/EST-1/inventory")
                        .session(session)
                        .param("quantity", String.valueOf(quantity)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // --- 订单管理测试补充 ---

    @Test
    void testGetAllOrdersWithMultipleOrders() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        Order order1 = new Order();
        order1.setOrderid(1);
        Order order2 = new Order();
        order2.setOrderid(2);

        when(orderService.getAllOrders()).thenReturn(Arrays.asList(order1, order2));

        mockMvc.perform(get("/api/admin/orders")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void testGetOrderByIdNotFound() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(orderService.getOrderById(999)).thenReturn(null);

        mockMvc.perform(get("/api/admin/orders/999")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void testUpdateOrderStatusFailure() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(orderService.updateOrderStatus(1, "SHIPPED")).thenReturn(false);

        mockMvc.perform(put("/api/admin/orders/1/status")
                        .session(session)
                        .param("status", "SHIPPED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // --- 用户管理测试补充 ---

    @Test
    void testGetUserByIdNotFound() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(accountService.getAccountByUsername("nobody")).thenReturn(null);

        mockMvc.perform(get("/api/admin/users/nobody")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void testDeleteUserFailure() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(accountService.deleteAccount("testuser")).thenReturn(false);

        mockMvc.perform(delete("/api/admin/users/testuser")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void testUpdateUserRoleUserNotFound() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        when(accountService.getAccountByUsername("nobody")).thenReturn(null);

        mockMvc.perform(put("/api/admin/users/nobody/role")
                        .session(session)
                        .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void testUpdateUserRoleFailure() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        Account account = new Account();
        account.setUserid("testuser");
        account.setRole("USER");

        when(accountService.getAccountByUsername("testuser")).thenReturn(account);
        when(accountService.updateAccount(any(Account.class))).thenReturn(false);

        mockMvc.perform(put("/api/admin/users/testuser/role")
                        .session(session)
                        .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "USER", "MODERATOR"})
    void testUpdateUserRoleWithDifferentRoles(String role) throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);

        Account account = new Account();
        account.setUserid("testuser");
        account.setRole("USER");

        when(accountService.getAccountByUsername("testuser")).thenReturn(account);
        when(accountService.updateAccount(any(Account.class))).thenReturn(true);

        mockMvc.perform(put("/api/admin/users/testuser/role")
                        .session(session)
                        .param("role", role))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }
}