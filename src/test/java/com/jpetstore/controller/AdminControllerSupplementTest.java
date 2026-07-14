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
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = AdminController.class, excludeAutoConfiguration = {DataSourceAutoConfiguration.class, MybatisAutoConfiguration.class})
class AdminControllerSupplementTest {
    @Autowired private MockMvc mockMvc;
    @MockitoBean private CategoryService categoryService;
    @MockitoBean private ProductService productService;
    @MockitoBean private ItemService itemService;
    @MockitoBean private OrderService orderService;
    @MockitoBean private AccountService accountService;

    private MockHttpSession s;
    private Product p;
    private Item i;
    private Order o;
    private Account admin;

    @BeforeEach
    void setUp() {
        admin = new Account(); admin.setUserid("admin"); admin.setRole("ADMIN");
        s = new MockHttpSession(); s.setAttribute("user", admin);
        p = new Product(); p.setProductid("P001"); p.setCategory("FISH");
        i = new Item(); i.setItemid("I001"); i.setProductid("P001"); i.setListprice(new BigDecimal("10.00"));
        o = new Order(); o.setOrderid(1); o.setUserid("user"); o.setTotalprice(new BigDecimal("20.00")); o.setStatus("P");
    }

    @Test void testDeleteProduct_Success() throws Exception {
        when(productService.deleteProduct("P001")).thenReturn(1);
        mockMvc.perform(delete("/api/admin/products/P001").session(s)).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
    }
    @Test void testDeleteProduct_Failure() throws Exception {
        when(productService.deleteProduct("P001")).thenReturn(0);
        mockMvc.perform(delete("/api/admin/products/P001").session(s)).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(500));
    }
    @Test void testGetProductItems() throws Exception {
        when(itemService.getItemsByProductId("P001")).thenReturn(Arrays.asList(i));
        mockMvc.perform(get("/api/admin/products/P001/items").session(s)).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].itemid").value("I001"));
    }
    @Test void testCreateItem_Success() throws Exception {
        when(itemService.insertItem(any(Item.class))).thenReturn(1);
        mockMvc.perform(post("/api/admin/items").session(s).contentType(MediaType.APPLICATION_JSON).content("{\"itemid\":\"I001\",\"productid\":\"P001\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
    }
    @Test void testCreateItem_Failure() throws Exception {
        when(itemService.insertItem(any(Item.class))).thenReturn(0);
        mockMvc.perform(post("/api/admin/items").session(s).contentType(MediaType.APPLICATION_JSON).content("{\"itemid\":\"I001\",\"productid\":\"P001\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(500));
    }
    @Test void testUpdateItem_Success() throws Exception {
        when(itemService.updateItem(any(Item.class))).thenReturn(1);
        mockMvc.perform(put("/api/admin/items/I001").session(s).contentType(MediaType.APPLICATION_JSON).content("{\"itemid\":\"I001\",\"productid\":\"P001\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
    }
    @Test void testUpdateItem_Failure() throws Exception {
        when(itemService.updateItem(any(Item.class))).thenReturn(0);
        mockMvc.perform(put("/api/admin/items/I001").session(s).contentType(MediaType.APPLICATION_JSON).content("{\"itemid\":\"I001\",\"productid\":\"P001\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(500));
    }
    @Test void testDeleteItem_Success() throws Exception {
        when(itemService.deleteItem("I001")).thenReturn(1);
        mockMvc.perform(delete("/api/admin/items/I001").session(s)).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
    }
    @Test void testDeleteItem_Failure() throws Exception {
        when(itemService.deleteItem("I001")).thenReturn(0);
        mockMvc.perform(delete("/api/admin/items/I001").session(s)).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(500));
    }
    @Test void testUpdateInventory_Success() throws Exception {
        when(itemService.updateInventory(eq("I001"), eq(50))).thenReturn(1);
        mockMvc.perform(put("/api/admin/items/I001/inventory").session(s).param("quantity","50")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
    }
    @Test void testUpdateInventory_Failure() throws Exception {
        when(itemService.updateInventory(eq("I001"), eq(50))).thenReturn(0);
        mockMvc.perform(put("/api/admin/items/I001/inventory").session(s).param("quantity","50")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(500));
    }
    @Test void testUpdateProductStatus_On() throws Exception {
        when(productService.updateProductStatus("P001","ON_SALE")).thenReturn(1);
        mockMvc.perform(put("/api/admin/products/P001/status").session(s).param("status","ON_SALE")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
    }
    @Test void testUpdateProductStatus_Off() throws Exception {
        when(productService.updateProductStatus("P001","OFF_SALE")).thenReturn(1);
        mockMvc.perform(put("/api/admin/products/P001/status").session(s).param("status","OFF_SALE")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
    }
    @Test void testUpdateProductStatus_Invalid() throws Exception {
        mockMvc.perform(put("/api/admin/products/P001/status").session(s).param("status","X")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(500));
    }
    @Test void testUpdateProductStatus_Failure() throws Exception {
        when(productService.updateProductStatus("P001","ON_SALE")).thenReturn(0);
        mockMvc.perform(put("/api/admin/products/P001/status").session(s).param("status","ON_SALE")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(500));
    }
    @Test void testGetAllOrders() throws Exception {
        when(orderService.getAllOrders()).thenReturn(Arrays.asList(o));
        mockMvc.perform(get("/api/admin/orders").session(s)).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].orderid").value(1));
    }
    @Test void testGetOrderById() throws Exception {
        when(orderService.getOrderById(1)).thenReturn(o);
        when(orderService.getOrderItemsByOrderId(1)).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/admin/orders/1").session(s)).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
    }
    @Test void testGetOrderById_NotFound() throws Exception {
        when(orderService.getOrderById(999)).thenReturn(null);
        mockMvc.perform(get("/api/admin/orders/999").session(s)).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(404));
    }
    @Test void testUpdateOrderStatus_Success() throws Exception {
        when(orderService.updateOrderStatus(1,"S")).thenReturn(true);
        mockMvc.perform(put("/api/admin/orders/1/status").session(s).param("status","S")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
    }
    @Test void testUpdateOrderStatus_Failure() throws Exception {
        when(orderService.updateOrderStatus(1,"S")).thenReturn(false);
        mockMvc.perform(put("/api/admin/orders/1/status").session(s).param("status","S")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(500));
    }
    @Test void testGetAllUsers() throws Exception {
        when(accountService.getAllUsers()).thenReturn(Arrays.asList(admin));
        mockMvc.perform(get("/api/admin/users").session(s)).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].userid").value("admin"));
    }
    @Test void testGetUserById() throws Exception {
        when(accountService.getAccountByUsername("admin")).thenReturn(admin);
        mockMvc.perform(get("/api/admin/users/admin").session(s)).andExpect(status().isOk()).andExpect(jsonPath("$.data.userid").value("admin"));
    }
    @Test void testGetUserById_NotFound() throws Exception {
        when(accountService.getAccountByUsername("x")).thenReturn(null);
        mockMvc.perform(get("/api/admin/users/x").session(s)).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(404));
    }
    @Test void testUpdateUser_Success() throws Exception {
        when(accountService.updateAccount(any(Account.class))).thenReturn(true);
        mockMvc.perform(put("/api/admin/users/admin").session(s).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
    }
    @Test void testUpdateUser_Failure() throws Exception {
        when(accountService.updateAccount(any(Account.class))).thenReturn(false);
        mockMvc.perform(put("/api/admin/users/admin").session(s).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(500));
    }
    @Test void testDeleteUser_Success() throws Exception {
        when(accountService.deleteAccount("admin")).thenReturn(true);
        mockMvc.perform(delete("/api/admin/users/admin").session(s)).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
    }
    @Test void testDeleteUser_Failure() throws Exception {
        when(accountService.deleteAccount("admin")).thenReturn(false);
        mockMvc.perform(delete("/api/admin/users/admin").session(s)).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(500));
    }
    @Test void testUpdateUserRole() throws Exception {
        when(accountService.getAccountByUsername("admin")).thenReturn(admin);
        when(accountService.updateAccount(any(Account.class))).thenReturn(true);
        mockMvc.perform(put("/api/admin/users/admin/role").session(s).param("role","ADMIN")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
    }
    @Test void testUpdateUserRole_NotFound() throws Exception {
        when(accountService.getAccountByUsername("x")).thenReturn(null);
        mockMvc.perform(put("/api/admin/users/x/role").session(s).param("role","ADMIN")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(404));
    }
    @Test void testUpdateUserRole_Failure() throws Exception {
        when(accountService.getAccountByUsername("admin")).thenReturn(admin);
        when(accountService.updateAccount(any(Account.class))).thenReturn(false);
        mockMvc.perform(put("/api/admin/users/admin/role").session(s).param("role","ADMIN")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(500));
    }
}