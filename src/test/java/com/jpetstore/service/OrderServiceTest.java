package com.jpetstore.service;

import com.jpetstore.domain.*;
import com.jpetstore.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 订单服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private OrderService orderService;

    private Order testOrder;
    private Cart testCart;
    private Item testItem;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setOrderid(1);
        testOrder.setUserid("testuser");
        testOrder.setTotalprice(new BigDecimal("100.00"));

        testItem = new Item();
        testItem.setItemid("ITEM001");
        testItem.setListprice(new BigDecimal("50.00"));

        testCart = new Cart();
        testCart.addItem(testItem);
        testCart.addItem(testItem);
    }

    @Test
    void testCreateOrder() {
        when(orderMapper.insertOrder(any(Order.class))).thenReturn(1);
        when(orderMapper.insertOrderItem(any(OrderItem.class))).thenReturn(1);
        when(orderMapper.insertOrderStatus(any(OrderStatus.class))).thenReturn(1);
        when(itemService.updateInventory(anyString(), anyInt())).thenReturn(1);

        Order result = orderService.createOrder("testuser", testCart, testOrder);

        assertNotNull(result);
        assertEquals("testuser", result.getUserid());
        assertNotNull(result.getOrderdate());
        verify(orderMapper, times(1)).insertOrder(any(Order.class));
        verify(orderMapper, times(1)).insertOrderItem(any(OrderItem.class));
        verify(orderMapper, times(1)).insertOrderStatus(any(OrderStatus.class));
        verify(itemService, times(1)).updateInventory(eq("ITEM001"), anyInt());
    }

    @Test
    void testCreateOrderFailure() {
        when(orderMapper.insertOrder(any(Order.class))).thenThrow(new RuntimeException("Database error"));

        Order result = orderService.createOrder("testuser", testCart, testOrder);

        assertNull(result);
    }

    @Test
    void testGetOrderById() {
        when(orderMapper.getOrderById(1)).thenReturn(testOrder);

        Order result = orderService.getOrderById(1);

        assertNotNull(result);
        assertEquals(1, result.getOrderid());
        verify(orderMapper, times(1)).getOrderById(1);
    }

    @Test
    void testGetOrderByIdNotFound() {
        when(orderMapper.getOrderById(999)).thenReturn(null);

        Order result = orderService.getOrderById(999);

        assertNull(result);
        verify(orderMapper, times(1)).getOrderById(999);
    }

    @Test
    void testGetOrdersByUserId() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderMapper.getOrdersByUserId("testuser")).thenReturn(orders);

        List<Order> result = orderService.getOrdersByUserId("testuser");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUserid());
        verify(orderMapper, times(1)).getOrdersByUserId("testuser");
    }

    @Test
    void testGetAllOrders() {
        List<Order> orders = Arrays.asList(testOrder);
        when(orderMapper.getAllOrders()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderMapper, times(1)).getAllOrders();
    }

    @Test
    void testUpdateOrderStatus() {
        when(orderMapper.updateOrderStatus(1, "SHIPPED")).thenReturn(1);
        when(orderMapper.insertOrderStatus(any(OrderStatus.class))).thenReturn(1);

        boolean result = orderService.updateOrderStatus(1, "SHIPPED");

        assertTrue(result);
        verify(orderMapper, times(1)).updateOrderStatus(1, "SHIPPED");
        verify(orderMapper, times(1)).insertOrderStatus(any(OrderStatus.class));
    }

    @Test
    void testUpdateOrderStatusFailure() {
        when(orderMapper.updateOrderStatus(1, "SHIPPED")).thenThrow(new RuntimeException("Database error"));

        boolean result = orderService.updateOrderStatus(1, "SHIPPED");

        assertFalse(result);
    }

    @Test
    void testGetOrderItemsByOrderId() {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderid(1);
        orderItem.setLinenum(1);
        orderItem.setItemid("ITEM001");
        List<OrderItem> orderItems = Arrays.asList(orderItem);
        when(orderMapper.getOrderItemsByOrderId(1)).thenReturn(orderItems);

        List<OrderItem> result = orderService.getOrderItemsByOrderId(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderMapper, times(1)).getOrderItemsByOrderId(1);
    }

    @Test
    void testGetOrderStatusByOrderId() {
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderid(1);
        orderStatus.setStatus("P");
        List<OrderStatus> statusList = Arrays.asList(orderStatus);
        when(orderMapper.getOrderStatusByOrderId(1)).thenReturn(statusList);

        List<OrderStatus> result = orderService.getOrderStatusByOrderId(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(orderMapper, times(1)).getOrderStatusByOrderId(1);
    }
}