package com.jpetstore.controller;

import com.jpetstore.domain.*;
import com.jpetstore.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    private Order testOrder;
    private Account testUser;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setOrderid(1);
        testOrder.setUserid("testuser");
        testOrder.setTotalprice(new BigDecimal("100.00"));

        testUser = new Account();
        testUser.setUserid("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFirstname("Test");
        testUser.setLastname("User");

        Item testItem = new Item();
        testItem.setItemid("ITEM001");
        testItem.setListprice(new BigDecimal("50.00"));

        testCart = new Cart();
        testCart.addItem(testItem);
    }

    @Test
    void testGetAllOrders() throws Exception {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].orderid").value(1));
    }

    @Test
    void testGetOrderById() throws Exception {
        when(orderService.getOrderById(1)).thenReturn(testOrder);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orderid").value(1));
    }

    @Test
    void testGetOrderByIdNotFound() throws Exception {
        when(orderService.getOrderById(999)).thenReturn(null);

        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("订单不存在"));
    }

    @Test
    void testGetOrdersByUserId() throws Exception {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getOrdersByUserId("testuser")).thenReturn(orders);

        mockMvc.perform(get("/api/orders/user/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].userid").value("testuser"));
    }

    @Test
    void testUpdateOrderStatus() throws Exception {
        when(orderService.updateOrderStatus(eq(1), eq("SHIPPED"))).thenReturn(true);

        mockMvc.perform(put("/api/orders/1/status")
                        .param("status", "SHIPPED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("状态更新成功"));
    }

    @Test
    void testUpdateOrderStatusFailure() throws Exception {
        when(orderService.updateOrderStatus(eq(1), eq("SHIPPED"))).thenReturn(false);

        mockMvc.perform(put("/api/orders/1/status")
                        .param("status", "SHIPPED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("状态更新失败"));
    }

    @Test
    void testGetOrderItems() throws Exception {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderid(1);
        orderItem.setLinenum(1);
        orderItem.setItemid("ITEM001");
        List<OrderItem> orderItems = Arrays.asList(orderItem);
        when(orderService.getOrderItemsByOrderId(1)).thenReturn(orderItems);

        mockMvc.perform(get("/api/orders/1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].itemid").value("ITEM001"));
    }

    @Test
    void testGetOrderStatusHistory() throws Exception {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderid(1);
        orderStatus.setStatus("P");
        List<OrderStatus> statusList = Arrays.asList(orderStatus);
        when(orderService.getOrderStatusByOrderId(1)).thenReturn(statusList);

        mockMvc.perform(get("/api/orders/1/status/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].status").value("P"));
    }

    @Test
    void testCreateOrderNotLoggedIn() throws Exception {
        String orderJson = "{\"shipaddr1\":\"123 Ship St\",\"shipcity\":\"Ship City\",\"shipstate\":\"SS\",\"shipzip\":\"11111\",\"shipcountry\":\"USA\",\"billaddr1\":\"123 Bill St\",\"billcity\":\"Bill City\",\"billstate\":\"BS\",\"billzip\":\"22222\",\"billcountry\":\"USA\",\"courier\":\"FedEx\",\"creditcard\":\"1234567890123456\",\"exprdate\":\"12/2025\",\"cardtype\":\"Visa\",\"locale\":\"en_US\"}";

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("请先登录"));
    }

    @Test
    void testCreateOrderEmptyCart() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testUser);
        session.setAttribute("cart", new Cart());

        String orderJson = "{\"shipaddr1\":\"123 Ship St\",\"shipcity\":\"Ship City\",\"shipstate\":\"SS\",\"shipzip\":\"11111\",\"shipcountry\":\"USA\",\"billaddr1\":\"123 Bill St\",\"billcity\":\"Bill City\",\"billstate\":\"BS\",\"billzip\":\"22222\",\"billcountry\":\"USA\",\"courier\":\"FedEx\",\"creditcard\":\"1234567890123456\",\"exprdate\":\"12/2025\",\"cardtype\":\"Visa\",\"locale\":\"en_US\"}";

        mockMvc.perform(post("/api/orders")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("购物车为空"));
    }

    @Test
    void testCreateOrderSuccess() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testUser);
        session.setAttribute("cart", testCart);

        when(orderService.createOrder(eq("testuser"), any(Cart.class), any(Order.class))).thenReturn(testOrder);

        String orderJson = "{\"shipaddr1\":\"123 Ship St\",\"shipcity\":\"Ship City\",\"shipstate\":\"SS\",\"shipzip\":\"11111\",\"shipcountry\":\"USA\",\"billaddr1\":\"123 Bill St\",\"billcity\":\"Bill City\",\"billstate\":\"BS\",\"billzip\":\"22222\",\"billcountry\":\"USA\",\"courier\":\"FedEx\",\"creditcard\":\"1234567890123456\",\"exprdate\":\"12/2025\",\"cardtype\":\"Visa\",\"locale\":\"en_US\"}";

        mockMvc.perform(post("/api/orders")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("订单创建成功"))
                .andExpect(jsonPath("$.data.orderid").value(1));
    }

    @Test
    void testGetMyOrdersNotLoggedIn() throws Exception {
        mockMvc.perform(get("/api/orders/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("请先登录"));
    }

    @Test
    void testGetMyOrdersSuccess() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testUser);

        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getOrdersByUserId("testuser")).thenReturn(orders);

        mockMvc.perform(get("/api/orders/my")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].userid").value("testuser"));
    }
}