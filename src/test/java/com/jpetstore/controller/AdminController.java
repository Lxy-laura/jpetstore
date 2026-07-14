package com.jpetstore.controller;

import com.jpetstore.domain.*;
import com.jpetstore.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = AdminController.class, excludeAutoConfiguration = {DataSourceAutoConfiguration.class, MybatisAutoConfiguration.class})
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
        mockMvc.perform(get("/api/admin/categories"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("请先登录"));
    }

    @Test
    void testAccessAdminApiAsNormalUser() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", normalUser);

        mockMvc.perform(get("/api/admin/categories")
                        .session(session))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value(403))
                .andExpect(jsonPath("$.message").value("需要管理员权限"));
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

        String json = "{\"productid\":\"P002\",\"category\":\"FISH\",\"name\":\"虎鲨\",\"description\":\"大型海水鱼\"}";

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

        String json = "{\"category\":\"FISH\",\"name\":\"神仙鱼\",\"description\":\"来自澳大利亚\"}";

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
}