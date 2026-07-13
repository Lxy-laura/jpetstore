package com.jpetstore.controller;

import com.jpetstore.domain.*;
import com.jpetstore.service.OrderService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 订单控制器测试
 * 覆盖正常路径、异常路径、参数化测试、边界值测试、完整业务流程
 */
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

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

    private Order testOrder;
    private Account testUser;
    private Cart testCart;
    private Item testItem;

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

        testItem = new Item();
        testItem.setItemid("ITEM001");
        testItem.setProductid("PROD001");
        testItem.setListprice(new BigDecimal("50.00"));

        testCart = new Cart();
        testCart.addItem(testItem);
    }

    // ==================== 获取所有订单 ====================

    @Test
    void testGetAllOrders() throws Exception {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].orderid").value(1))
                .andExpect(jsonPath("$.data[0].userid").value("testuser"))
                .andExpect(jsonPath("$.data[0].totalprice").value(100.00));
    }

    @Test
    void testGetAllOrdersEmpty() throws Exception {
        when(orderService.getAllOrders()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // ==================== 根据订单ID获取订单 ====================

    @Test
    void testGetOrderById() throws Exception {
        when(orderService.getOrderById(1)).thenReturn(testOrder);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orderid").value(1))
                .andExpect(jsonPath("$.data.userid").value("testuser"));
    }

    @Test
    void testGetOrderByIdNotFound() throws Exception {
        when(orderService.getOrderById(999)).thenReturn(null);

        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("订单不存在"));
    }

    /**
     * 参数化测试：用不同的订单ID测试
     */
    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 100, 9999})
    void testGetOrderByIdWithDifferentIds(int orderid) throws Exception {
        when(orderService.getOrderById(orderid)).thenReturn(testOrder);

        mockMvc.perform(get("/api/orders/" + orderid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 创建订单 ====================

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
    void testCreateOrderNotLoggedIn() throws Exception {
        String orderJson = "{\"shipaddr1\":\"123 Ship St\",\"shipcity\":\"Ship City\"}";

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

        String orderJson = "{\"shipaddr1\":\"123 Ship St\",\"shipcity\":\"Ship City\"}";

        mockMvc.perform(post("/api/orders")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("购物车为空"));
    }

    @Test
    void testCreateOrderWithNullCart() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testUser);
        // 不设置 cart 属性

        String orderJson = "{\"shipaddr1\":\"123 Ship St\"}";

        mockMvc.perform(post("/api/orders")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("购物车为空"));
    }

    @Test
    void testCreateOrderServiceFailure() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testUser);
        session.setAttribute("cart", testCart);

        when(orderService.createOrder(eq("testuser"), any(Cart.class), any(Order.class))).thenReturn(null);

        String orderJson = "{\"shipaddr1\":\"123 Ship St\",\"shipcity\":\"Ship City\"}";

        mockMvc.perform(post("/api/orders")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("订单创建失败"));
    }

    @Test
    void testCreateOrderWithEmptyBody() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testUser);
        session.setAttribute("cart", testCart);

        mockMvc.perform(post("/api/orders")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // ==================== 获取我的订单 ====================

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

    @Test
    void testGetMyOrdersNotLoggedIn() throws Exception {
        mockMvc.perform(get("/api/orders/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("请先登录"));
    }

    @Test
    void testGetMyOrdersEmpty() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testUser);

        when(orderService.getOrdersByUserId("testuser")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/orders/my")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // ==================== 根据用户ID获取订单 ====================

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
    void testGetOrdersByUserIdNoOrders() throws Exception {
        when(orderService.getOrdersByUserId("nouser")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/orders/user/nouser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"testuser", "j2ee", "ACID"})
    void testGetOrdersByDifferentUserIds(String userid) throws Exception {
        when(orderService.getOrdersByUserId(userid)).thenReturn(Arrays.asList(testOrder));

        mockMvc.perform(get("/api/orders/user/" + userid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 更新订单状态 ====================

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
    void testUpdateOrderStatusWithoutStatusParam() throws Exception {
        mockMvc.perform(put("/api/orders/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    /**
     * 参数化测试：用不同的订单状态测试
     */
    @ParameterizedTest
    @ValueSource(strings = {"P", "SHIPPED", "DELIVERED", "CANCELLED"})
    void testUpdateOrderStatusWithDifferentStatuses(String status) throws Exception {
        when(orderService.updateOrderStatus(eq(1), eq(status))).thenReturn(true);

        mockMvc.perform(put("/api/orders/1/status")
                        .param("status", status))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ==================== 获取订单的商品项 ====================

    @Test
    void testGetOrderItems() throws Exception {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderid(1);
        orderItem.setLinenum(1);
        orderItem.setItemid("ITEM001");
        orderItem.setQuantity(2);
        orderItem.setUnitprice(new BigDecimal("50.00"));

        List<OrderItem> orderItems = Arrays.asList(orderItem);
        when(orderService.getOrderItemsByOrderId(1)).thenReturn(orderItems);

        mockMvc.perform(get("/api/orders/1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].itemid").value("ITEM001"))
                .andExpect(jsonPath("$.data[0].quantity").value(2));
    }

    @Test
    void testGetOrderItemsEmpty() throws Exception {
        when(orderService.getOrderItemsByOrderId(999)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/orders/999/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testGetOrderItemsMultipleItems() throws Exception {
        OrderItem item1 = new OrderItem();
        item1.setOrderid(1);
        item1.setLinenum(1);
        item1.setItemid("ITEM001");

        OrderItem item2 = new OrderItem();
        item2.setOrderid(1);
        item2.setLinenum(2);
        item2.setItemid("ITEM002");

        when(orderService.getOrderItemsByOrderId(1)).thenReturn(Arrays.asList(item1, item2));

        mockMvc.perform(get("/api/orders/1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].itemid").value("ITEM001"))
                .andExpect(jsonPath("$.data[1].itemid").value("ITEM002"));
    }

    // ==================== 获取订单状态历史 ====================

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
    void testGetOrderStatusHistoryEmpty() throws Exception {
        when(orderService.getOrderStatusByOrderId(999)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/orders/999/status/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void testGetOrderStatusHistoryMultipleStatuses() throws Exception {
        OrderStatus status1 = new OrderStatus();
        status1.setOrderid(1);
        status1.setStatus("P");

        OrderStatus status2 = new OrderStatus();
        status2.setOrderid(1);
        status2.setStatus("SHIPPED");

        when(orderService.getOrderStatusByOrderId(1)).thenReturn(Arrays.asList(status1, status2));

        mockMvc.perform(get("/api/orders/1/status/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].status").value("P"))
                .andExpect(jsonPath("$.data[1].status").value("SHIPPED"));
    }

    // ==================== 完整订单流程测试 ====================

    /**
     * 完整订单流程：登录 -> 加购 -> 下单 -> 查看订单 -> 查看订单项 -> 查看状态历史
     */
    @Test
    void testFullOrderWorkflow() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user", testUser);
        session.setAttribute("cart", testCart);

        // 1. 创建订单
        when(orderService.createOrder(eq("testuser"), any(Cart.class), any(Order.class))).thenReturn(testOrder);

        String orderJson = "{\"shipaddr1\":\"123 Ship St\",\"shipcity\":\"Ship City\",\"shipstate\":\"SS\",\"shipzip\":\"11111\",\"shipcountry\":\"USA\",\"billaddr1\":\"123 Bill St\",\"billcity\":\"Bill City\",\"billstate\":\"BS\",\"billzip\":\"22222\",\"billcountry\":\"USA\",\"courier\":\"FedEx\",\"creditcard\":\"1234567890123456\",\"exprdate\":\"12/2025\",\"cardtype\":\"Visa\",\"locale\":\"en_US\"}";

        mockMvc.perform(post("/api/orders")
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderJson))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orderid").value(1));

        // 2. 查看我的订单
        when(orderService.getOrdersByUserId("testuser")).thenReturn(Arrays.asList(testOrder));
        mockMvc.perform(get("/api/orders/my").session(session))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].orderid").value(1));

        // 3. 查看订单详情
        when(orderService.getOrderById(1)).thenReturn(testOrder);
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.orderid").value(1));

        // 4. 查看订单项
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderid(1);
        orderItem.setItemid("ITEM001");
        when(orderService.getOrderItemsByOrderId(1)).thenReturn(Arrays.asList(orderItem));
        mockMvc.perform(get("/api/orders/1/items"))
                .andExpect(jsonPath("$.data[0].itemid").value("ITEM001"));

        // 5. 查看状态历史
        OrderStatus status = new OrderStatus();
        status.setOrderid(1);
        status.setStatus("P");
        when(orderService.getOrderStatusByOrderId(1)).thenReturn(Arrays.asList(status));
        mockMvc.perform(get("/api/orders/1/status/history"))
                .andExpect(jsonPath("$.data[0].status").value("P"));
    }
}
