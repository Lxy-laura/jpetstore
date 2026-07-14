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
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
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

@WebMvcTest(value = ProductController.class, excludeAutoConfiguration = {DataSourceAutoConfiguration.class, MybatisAutoConfiguration.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ItemService itemService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setProductid("P001");
        testProduct.setCategory("FISH");
        testProduct.setName("神仙鱼");
        testProduct.setDescription("淡水神仙鱼");
        testProduct.setImage("fish1.jpg");
    }

    @Test
    void testGetAllProducts() throws Exception {
        List<Product> products = Arrays.asList(testProduct);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].productid").value("P001"))
                .andExpect(jsonPath("$.data[0].name").value("神仙鱼"));
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

    @ParameterizedTest
    @ValueSource(strings = {"FISH", "DOGS", "CATS", "REPTILES", "BIRDS"})
    void testGetProductsByDifferentCategories(String category) throws Exception {
        when(productService.getProductsByCategory(category)).thenReturn(Arrays.asList(testProduct));

        mockMvc.perform(get("/api/products/category/" + category))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testSearchProducts() throws Exception {
        when(productService.searchProducts("fish")).thenReturn(Arrays.asList(testProduct));

        mockMvc.perform(get("/api/products/search")
                        .param("keyword", "fish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].name").value("神仙鱼"));
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
                .andExpect(jsonPath("$.code").value(400));
    }

    @ParameterizedTest
    @ValueSource(strings = {"fish", "dog", "cat", "神仙", "斗牛"})
    void testSearchWithDifferentKeywords(String keyword) throws Exception {
        when(productService.searchProducts(keyword)).thenReturn(Arrays.asList(testProduct));

        mockMvc.perform(get("/api/products/search")
                        .param("keyword", keyword))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testGetProductById() throws Exception {
        when(productService.getProductById("P001")).thenReturn(testProduct);
        when(itemService.getItemsByProductId("P001")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/products/P001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.productid").value("P001"))
                .andExpect(jsonPath("$.data.name").value("神仙鱼"))
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
        item.setItemid("I001");
        item.setProductid("P001");
        item.setListprice(new BigDecimal("16.50"));

        when(productService.getProductById("P001")).thenReturn(testProduct);
        when(itemService.getItemsByProductId("P001")).thenReturn(Arrays.asList(item));

        mockMvc.perform(get("/api/products/P001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].itemid").value("I001"))
                .andExpect(jsonPath("$.data.items[0].listprice").value(16.50));
    }

    @Test
    void testGetProductItems() throws Exception {
        Item item = new Item();
        item.setItemid("I001");
        item.setProductid("P001");

        when(itemService.getItemsByProductId("P001")).thenReturn(Arrays.asList(item));

        mockMvc.perform(get("/api/products/P001/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].itemid").value("I001"));
    }

    @Test
    void testGetProductItemsEmpty() throws Exception {
        when(itemService.getItemsByProductId("P001")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/products/P001/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testCreateProduct() throws Exception {
        when(productService.insertProduct(any(Product.class))).thenReturn(1);

        String json = "{\"productid\":\"P999\",\"category\":\"FISH\",\"name\":\"测试鱼\",\"description\":\"测试\"}";

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

        String json = "{\"productid\":\"P999\",\"category\":\"FISH\",\"name\":\"测试鱼\",\"description\":\"测试\"}";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("创建失败"));
    }

    @Test
    void testCreateProductWithBlankId() throws Exception {
        String json = "{\"productid\":\"\",\"category\":\"FISH\",\"name\":\"测试鱼\"}";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void testCreateProductWithBlankCategory() throws Exception {
        String json = "{\"productid\":\"P999\",\"category\":\"\",\"name\":\"测试鱼\"}";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void testCreateProductWithBlankName() throws Exception {
        String json = "{\"productid\":\"P999\",\"category\":\"FISH\",\"name\":\"\"}";

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void testUpdateProduct() throws Exception {
        when(productService.updateProduct(any(Product.class))).thenReturn(1);

        String json = "{\"category\":\"FISH\",\"name\":\"更新鱼\",\"description\":\"更新\"}";

        mockMvc.perform(put("/api/products/P001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"));
    }

    @Test
    void testUpdateProductFailure() throws Exception {
        when(productService.updateProduct(any(Product.class))).thenReturn(0);

        String json = "{\"category\":\"FISH\",\"name\":\"更新鱼\"}";

        mockMvc.perform(put("/api/products/P001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("更新失败"));
    }

    @Test
    void testDeleteProduct() throws Exception {
        when(productService.deleteProduct("P001")).thenReturn(1);

        mockMvc.perform(delete("/api/products/P001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    void testDeleteProductFailure() throws Exception {
        when(productService.deleteProduct("P001")).thenReturn(0);

        mockMvc.perform(delete("/api/products/P001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("删除失败"));
    }
}
