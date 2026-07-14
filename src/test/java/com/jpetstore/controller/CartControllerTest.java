package com.jpetstore.controller;

import com.jpetstore.domain.Cart;
import com.jpetstore.domain.Item;
import com.jpetstore.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = CartController.class, excludeAutoConfiguration = {DataSourceAutoConfiguration.class, MybatisAutoConfiguration.class})
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    private Item testItem;

    @BeforeEach
    void setUp() {
        testItem = new Item();
        testItem.setItemid("ITEM001");
        testItem.setProductid("PROD001");
        testItem.setListprice(new BigDecimal("50.00"));
        testItem.setQty(100);
    }

    @Test
    void testGetCart() throws Exception {
        mockMvc.perform(get("/api/cart"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testAddToCart() throws Exception {
        when(itemService.getItemById("ITEM001")).thenReturn(testItem);

        mockMvc.perform(post("/api/cart/add")
                        .param("itemId", "ITEM001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("添加成功"));
    }

    @Test
    void testAddToCartItemNotFound() throws Exception {
        when(itemService.getItemById("NOTEXIST")).thenReturn(null);

        mockMvc.perform(post("/api/cart/add")
                        .param("itemId", "NOTEXIST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("商品不存在"));
    }

    @Test
    void testRemoveFromCart() throws Exception {
        MockHttpSession session = new MockHttpSession();  // ← 添加
        when(itemService.getItemById("ITEM001")).thenReturn(testItem);
        mockMvc.perform(post("/api/cart/add")
                .param("itemId", "ITEM001")
                .session(session));  // ← 添加

        mockMvc.perform(post("/api/cart/remove")
                        .param("itemId", "ITEM001")
                        .session(session))  // ← 添加
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void testRemoveFromCartItemNotInCart() throws Exception {
        mockMvc.perform(post("/api/cart/remove")
                        .param("itemId", "NOTEXIST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("商品不在购物车中"));
    }

    @Test
    void testUpdateQuantity() throws Exception {
        when(itemService.getItemById("ITEM001")).thenReturn(testItem);
        mockMvc.perform(post("/api/cart/add").param("itemId", "ITEM001"));

        mockMvc.perform(post("/api/cart/update")
                        .param("itemId", "ITEM001")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"));
    }

    @Test
    void testClearCart() throws Exception {
        when(itemService.getItemById("ITEM001")).thenReturn(testItem);
        mockMvc.perform(post("/api/cart/add").param("itemId", "ITEM001"));

        mockMvc.perform(post("/api/cart/clear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("购物车已清空"));
    }

    // ==================== 以下为补充的测试用例 ====================

    // --- 边界值测试 ---

    /**
     * 更新购物车数量为0 -> 应移除该商品（根据 Cart.setQuantity 实现，quantity < 1 时移除）
     */
    @Test
    void testUpdateQuantityToZero() throws Exception {
        MockHttpSession session = new MockHttpSession();
        when(itemService.getItemById("ITEM001")).thenReturn(testItem);
        mockMvc.perform(post("/api/cart/add").param("itemId", "ITEM001").session(session));

        mockMvc.perform(post("/api/cart/update")
                        .param("itemId", "ITEM001")
                        .param("quantity", "0")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 更新购物车数量为负数
     */
    @Test
    void testUpdateQuantityToNegative() throws Exception {
        MockHttpSession session = new MockHttpSession();
        when(itemService.getItemById("ITEM001")).thenReturn(testItem);
        mockMvc.perform(post("/api/cart/add").param("itemId", "ITEM001").session(session));

        mockMvc.perform(post("/api/cart/update")
                        .param("itemId", "ITEM001")
                        .param("quantity", "-1")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 参数化测试：更新为不同的数量
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 5, 10, 100, 999})
    void testUpdateQuantityWithDifferentValues(int quantity) throws Exception {
        MockHttpSession session = new MockHttpSession();
        when(itemService.getItemById("ITEM001")).thenReturn(testItem);
        mockMvc.perform(post("/api/cart/add").param("itemId", "ITEM001").session(session));

        mockMvc.perform(post("/api/cart/update")
                        .param("itemId", "ITEM001")
                        .param("quantity", String.valueOf(quantity))
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"));
    }

    // --- 多商品操作测试 ---

    /**
     * 添加多个不同商品到购物车，验证购物车数量
     */
    @Test
    void testAddMultipleDifferentItems() throws Exception {
        MockHttpSession session = new MockHttpSession();

        Item item2 = new Item();
        item2.setItemid("ITEM002");
        item2.setProductid("PROD002");
        item2.setListprice(new BigDecimal("30.00"));

        when(itemService.getItemById("ITEM001")).thenReturn(testItem);
        when(itemService.getItemById("ITEM002")).thenReturn(item2);

        mockMvc.perform(post("/api/cart/add").param("itemId", "ITEM001").session(session))
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(post("/api/cart/add").param("itemId", "ITEM002").session(session))
                .andExpect(jsonPath("$.code").value(200));

        // 验证购物车有2种商品
        mockMvc.perform(get("/api/cart").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.numberOfItems").value(2));
    }

    /**
     * 重复添加同一商品 -> 数量应增加
     */
    @Test
    void testAddSameItemTwice() throws Exception {
        MockHttpSession session = new MockHttpSession();
        when(itemService.getItemById("ITEM001")).thenReturn(testItem);

        mockMvc.perform(post("/api/cart/add").param("itemId", "ITEM001").session(session));

        mockMvc.perform(post("/api/cart/add").param("itemId", "ITEM001").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    /**
     * 添加商品后清空再添加 -> 购物车应只有新商品
     */
    @Test
    void testAddClearThenAddAgain() throws Exception {
        MockHttpSession session = new MockHttpSession();
        when(itemService.getItemById("ITEM001")).thenReturn(testItem);

        mockMvc.perform(post("/api/cart/add").param("itemId", "ITEM001").session(session));
        mockMvc.perform(post("/api/cart/clear").session(session));
        mockMvc.perform(post("/api/cart/add").param("itemId", "ITEM001").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        mockMvc.perform(get("/api/cart").session(session))
                .andExpect(jsonPath("$.data.numberOfItems").value(1));
    }

    // --- 缺少参数测试 ---

    @Test
    void testAddToCartWithoutItemIdParam() throws Exception {
        mockMvc.perform(post("/api/cart/add"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void testRemoveFromCartWithoutItemIdParam() throws Exception {
        mockMvc.perform(post("/api/cart/remove"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void testUpdateQuantityWithoutQuantityParam() throws Exception {
        mockMvc.perform(post("/api/cart/update").param("itemId", "I001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    // --- 完整购物流程测试 ---

    /**
     * 完整购物流程：加购 -> 查看 -> 更新数量 -> 移除 -> 验证空购物车
     */
    @Test
    void testFullCartWorkflow() throws Exception {
        MockHttpSession session = new MockHttpSession();
        when(itemService.getItemById("ITEM001")).thenReturn(testItem);

        // 1. 添加商品
        mockMvc.perform(post("/api/cart/add").param("itemId", "ITEM001").session(session))
                .andExpect(jsonPath("$.code").value(200));

        // 2. 查看购物车
        mockMvc.perform(get("/api/cart").session(session))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.numberOfItems").value(1));

        // 3. 更新数量
        mockMvc.perform(post("/api/cart/update")
                        .param("itemId", "ITEM001")
                        .param("quantity", "3")
                        .session(session))
                .andExpect(jsonPath("$.code").value(200));

        // 4. 移除商品
        mockMvc.perform(post("/api/cart/remove")
                        .param("itemId", "ITEM001")
                        .session(session))
                .andExpect(jsonPath("$.code").value(200));

        // 5. 验证购物车为空
        mockMvc.perform(get("/api/cart").session(session))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.numberOfItems").value(0));
    }

    /**
     * 清空空购物车 -> 也应成功
     */
    @Test
    void testClearEmptyCart() throws Exception {
        mockMvc.perform(post("/api/cart/clear"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("购物车已清空"));
    }
}