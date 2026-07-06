package com.jpetstore.controller;

import com.jpetstore.domain.Item;
import com.jpetstore.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
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
}