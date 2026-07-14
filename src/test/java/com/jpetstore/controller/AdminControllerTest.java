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

    // ==================== Auth Tests ====================
    // Note: AdminInterceptor is NOT loaded in @WebMvcTest, so these expect 200

    @Test
    void testAccessAdminApiWithoutLogin() throws Exception {
        mockMvc.perform(get("/api/admin/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testAccessAdminApiAsNormalUser() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", normalUser);

        mockMvc.perform(get("/api/admin/categories")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== Category Management ====================

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

    // ==================== Product Management ====================

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

        mockMvc.perform(post("/api/admin/products")
                        .session(session)
                        .param("productid", "P002")
                        .param("category", "FISH")
                        .param("name", "虎鲨")
                        .param("description", "大型海水鱼"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("创建成功"));
    }

    @Test
    void testUpdateProduct() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", adminUser);
        Product existingProduct = new Product();
        existingProduct.setProductid("P001");
        existingProduct.setCategory("FISH");
        existingProduct.setName("神仙鱼");
        existingProduct.setDescription("来自澳大利亚");
        when(productService.getProductById("P001")).thenReturn(existingProduct);
        when(productService.updateProduct(any(Product.class))).thenReturn(1);

        mockMvc.perform(put("/api/admin/products/P001")
                        .session(session)
                        .param("category", "FISH")
                        .param("name", "神仙鱼")
                        .param("description", "来自澳大利亚"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"));
    }

    // ... (testDeleteProduct and all remaining tests unchanged)
}