package com.jpetstore.controller;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = ItemController.class, excludeAutoConfiguration = {DataSourceAutoConfiguration.class, MybatisAutoConfiguration.class})
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    private Item testItem;

    @BeforeEach
    void setUp() {
        testItem = new Item();
        testItem.setItemid("EST-1");
        testItem.setProductid("FI-SW-01");
        testItem.setListprice(new BigDecimal("16.50"));
        testItem.setUnitcost(new BigDecimal("10.00"));
        testItem.setSupplier(1);
        testItem.setStatus("P");
        testItem.setAttr1("Large");
        testItem.setQty(10000);
    }

    // ==================== 获取所有商品项 ====================

    @Test
    void testGetAllItems() throws Exception {
        when(itemService.getAllItems()).thenReturn(Arrays.asList(testItem));

        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].itemid").value("EST-1"));
    }

    @Test
    void testGetAllItemsEmpty() throws Exception {
        when(itemService.getAllItems()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // ==================== 根据ID获取商品项 ====================

    @Test
    void testGetItemById() throws Exception {
        when(itemService.getItemById("EST-1")).thenReturn(testItem);

        mockMvc.perform(get("/api/items/EST-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.itemid").value("EST-1"))
                .andExpect(jsonPath("$.data.listprice").value(16.50))
                .andExpect(jsonPath("$.data.attr1").value("Large"));
    }

    @Test
    void testGetItemByIdNotFound() throws Exception {
        when(itemService.getItemById("EST-999")).thenReturn(null);

        mockMvc.perform(get("/api/items/EST-999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("商品项不存在"));
    }

    /**
     * 参数化测试：用不同的商品项ID测试
     */
    @ParameterizedTest
    @ValueSource(strings = {"EST-1", "EST-2", "EST-3"})
    void testGetItemByDifferentIds(String itemid) throws Exception {
        when(itemService.getItemById(itemid)).thenReturn(testItem);

        mockMvc.perform(get("/api/items/" + itemid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 创建商品项 ====================

    @Test
    void testCreateItem() throws Exception {
        when(itemService.insertItem(any(Item.class))).thenReturn(1);

        String json = "{\"itemid\":\"EST-99\",\"productid\":\"FI-SW-01\",\"listprice\":20.00,\"unitcost\":12.00,\"qty\":100}";

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("创建成功"));
    }

    @Test
    void testCreateItemFailure() throws Exception {
        when(itemService.insertItem(any(Item.class))).thenReturn(0);

        String json = "{\"itemid\":\"EST-99\",\"productid\":\"FI-SW-01\",\"listprice\":20.00}";

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    @Test
    void testCreateItemWithBlankItemId() throws Exception {
        String json = "{\"itemid\":\"\",\"productid\":\"P001\"}";

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void testCreateItemWithBlankProductId() throws Exception {
        String json = "{\"itemid\":\"I999\",\"productid\":\"\"}";

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void testCreateItemWithNegativeQty() throws Exception {
        String json = "{\"itemid\":\"I999\",\"productid\":\"P001\",\"qty\":-5}";

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    /**
     * 边界值测试：库存为0应允许（边界值）
     */
    @Test
    void testCreateItemWithZeroQty() throws Exception {
        when(itemService.insertItem(any(Item.class))).thenReturn(1);

        String json = "{\"itemid\":\"EST-99\",\"productid\":\"FI-SW-01\",\"qty\":0}";

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 更新商品项 ====================

    @Test
    void testUpdateItem() throws Exception {
        when(itemService.updateItem(any(Item.class))).thenReturn(1);

        String json = "{\"itemid\":\"EST-1\",\"productid\":\"FI-SW-01\",\"listprice\":19.99,\"qty\":500}";

        mockMvc.perform(put("/api/items/EST-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"));
    }

    @Test
    void testUpdateItemFailure() throws Exception {
        when(itemService.updateItem(any(Item.class))).thenReturn(0);

        String json = "{\"itemid\":\"EST-1\",\"productid\":\"FI-SW-01\",\"listprice\":19.99}";

        mockMvc.perform(put("/api/items/EST-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ==================== 删除商品项 ====================

    @Test
    void testDeleteItem() throws Exception {
        when(itemService.deleteItem("EST-1")).thenReturn(1);

        mockMvc.perform(delete("/api/items/EST-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    void testDeleteItemFailure() throws Exception {
        when(itemService.deleteItem("EST-1")).thenReturn(0);

        mockMvc.perform(delete("/api/items/EST-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }
}
