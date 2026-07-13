package com.jpetstore.controller;

import com.jpetstore.domain.Item;
import com.jpetstore.domain.Product;
import com.jpetstore.service.ItemService;
import com.jpetstore.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 产品控制器测试
 * 覆盖正常路径、异常路径、参数化测试、边界值测试
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ItemService itemService;

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

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setProductid("FI-SW-01");
        testProduct.setCategory("FISH");
        testProduct.setName("Angelfish");
        testProduct.setDescription("Freshwater angelfish");
        testProduct.setImage("fish1.jpg");
    }

    // ==================== 获取所有产品 ====================

    @Test
    void testGetAllProducts() throws Exception {
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].productid").value("FI-SW-01"))
                .andExpect(jsonPath("$.data[0].name").value("Angelfish"));
    }

    @Test
    void testGetAllProductsEmpty() throws Exception {
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // ==================== 按分类获取产品 ====================

    @Test
    void testGetProductsByCategory() throws Exception {
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getProductsByCategory("FISH")).thenReturn(products);

        mockMvc.perform(get("/api/products/category/FISH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].category").value("FISH"));
    }

    @Test
    void testGetProductsByCategoryNoResults() throws Exception {
        when(productService.getProductsByCategory("NOTEXIST")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/products/category/NOTEXIST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    /**
     * 参数化测试：用不同的分类ID测试
     */
    @ParameterizedTest
    @ValueSource(strings = {"FISH", "DOGS", "CATS", "REPTILES", "BIRDS"})
    void testGetProductsByDifferentCategories(String category) throws Exception {
        when(productService.getProductsByCategory(category)).thenReturn(Arrays.asList(testProduct));

        mockMvc.perform(get("/api/products/category/" + category))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 搜索产品 ====================

    @Test
    void testSearchProducts() throws Exception {
        when(productService.searchProducts("fish")).thenReturn(Arrays.asList(testProduct));

        mockMvc.perform(get("/api/products/search")
                        .param("keyword", "fish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].name").value("Angelfish"));
    }

    @Test
    void testSearchProductsNoResults() throws Exception {
        when(productService.searchProducts("zzzzz")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/products/search")
                        .param("keyword", "zzzzz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testSearchProductsWithoutKeyword() throws Exception {
        mockMvc.perform(get("/api/products/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    /**
     * 参数化测试：用不同的搜索关键词测试
     */
    @ParameterizedTest
    @ValueSource(strings = {"fish", "dog", "cat", "Angel", "Bulldog"})
    void testSearchWithDifferentKeywords(String keyword) throws Exception {
        when(productService.searchProducts(keyword)).thenReturn(Arrays.asList(testProduct));

        mockMvc.perform(get("/api/products/search")
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 获取产品详情 ====================

    @Test
    void testGetProductById() throws Exception {
        when(productService.getProductById("FI-SW-01")).thenReturn(testProduct);
        when(itemService.getItemsByProductId("FI-SW-01")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/products/FI-SW-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.productid").value("FI-SW-01"))
                .andExpect(jsonPath("$.data.name").value("Angelfish"))
                .andExpect(jsonPath("$.data.items").isArray());
    }

    @Test
    void testGetProductByIdNotFound() throws Exception {
        when(productService.getProductById("NOT-EXIST")).thenReturn(null);

        mockMvc.perform(get("/api/products/NOT-EXIST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("产品不存在"));
    }

    @Test
    void testGetProductByIdWithItems() throws Exception {
        Item item = new Item();
        item.setItemid("EST-1");
        item.setProductid("FI-SW-01");
        item.setListprice(new BigDecimal("16.50"));

        when(productService.getProductById("FI-SW-01")).thenReturn(testProduct);
        when(itemService.getItemsByProductId("FI-SW-01")).thenReturn(Arrays.asList(item));

        mockMvc.perform(get("/api/products/FI-SW-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].itemid").value("EST-1"))
                .andExpect(jsonPath("$.data.items[0].listprice").value(16.50));
    }

    // ==================== 获取产品下的商品项 ====================

    @Test
    void testGetProductItems() throws Exception {
        Item item = new Item();
        item.setItemid("EST-1");
        item.setProductid("FI-SW-01");

        when(itemService.getItemsByProductId("FI-SW-01")).thenReturn(Arrays.asList(item));

        mockMvc.perform(get("/api/products/FI-SW-01/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].itemid").value("EST-1"));
    }

    @Test
    void testGetProductItemsEmpty() throws Exception {
        when(itemService.getItemsByProductId("FI-SW-01")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/products/FI-SW-01/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // ==================== 创建产品 ====================

    @Test
    void testCreateProduct() throws Exception {
        when(productService.insertProduct(any(Product.class))).thenReturn(1);

        String json = "{\"productid\":\"FI-SW-99\",\"category\":\"FISH\",\"name\":\"TestFish\",\"description\":\"Test\",\"price\":19.99}";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("创建成功"));
    }

    @Test
    void testCreateProductFailure() throws Exception {
        when(productService.insertProduct(any(Product.class))).thenReturn(0);

        String json = "{\"productid\":\"FI-SW-99\",\"category\":\"FISH\",\"name\":\"TestFish\",\"description\":\"Test\",\"price\":19.99}";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("创建失败"));
    }

    @Test
    void testCreateProductWithBlankId() throws Exception {
        String json = "{\"productid\":\"\",\"category\":\"FISH\",\"name\":\"TestFish\",\"price\":19.99}";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void testCreateProductWithBlankCategory() throws Exception {
        String json = "{\"productid\":\"FI-SW-99\",\"category\":\"\",\"name\":\"TestFish\",\"price\":19.99}";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void testCreateProductWithBlankName() throws Exception {
        String json = "{\"productid\":\"FI-SW-99\",\"category\":\"FISH\",\"name\":\"\",\"price\":19.99}";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ==================== 更新产品 ====================

    @Test
    void testUpdateProduct() throws Exception {
        when(productService.updateProduct(any(Product.class))).thenReturn(1);

        String json = "{\"productid\":\"FI-SW-01\",\"category\":\"FISH\",\"name\":\"Updated Fish\",\"description\":\"Updated\",\"price\":19.99}";

        mockMvc.perform(put("/api/products/FI-SW-01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"));
    }

    @Test
    void testUpdateProductFailure() throws Exception {
        when(productService.updateProduct(any(Product.class))).thenReturn(0);

        String json = "{\"productid\":\"FI-SW-01\",\"category\":\"FISH\",\"name\":\"Updated Fish\",\"price\":19.99}";

        mockMvc.perform(put("/api/products/FI-SW-01")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("更新失败"));
    }

    // ==================== 删除产品 ====================

    @Test
    void testDeleteProduct() throws Exception {
        when(productService.deleteProduct("FI-SW-01")).thenReturn(1);

        mockMvc.perform(delete("/api/products/FI-SW-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    void testDeleteProductFailure() throws Exception {
        when(productService.deleteProduct("FI-SW-01")).thenReturn(0);

        mockMvc.perform(delete("/api/products/FI-SW-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("删除失败"));
    }
}
