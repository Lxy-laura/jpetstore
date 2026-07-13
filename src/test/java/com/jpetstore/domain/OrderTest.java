package com.jpetstore.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(order);
        assertNotNull(order.getOrderItems());
        assertTrue(order.getOrderItems().isEmpty());
    }

    @Test
    void testAddOrderItem() {
        OrderItem item = new OrderItem();
        item.setItemid("EST-1");
        order.addOrderItem(item);
        assertEquals(1, order.getOrderItems().size());
    }

    @Test
    void testAllFields() {
        Date now = new Date();
        order.setOrderid(1);
        order.setUserid("j2ee");
        order.setOrderdate(now);
        order.setShipaddr1("123 Main St");
        order.setShipaddr2("Apt 4");
        order.setShipcity("Springfield");
        order.setShipstate("IL");
        order.setShipzip("12345");
        order.setShipcountry("USA");
        order.setBilladdr1("456 Bill St");
        order.setBilladdr2("Suite 100");
        order.setBillcity("Chicago");
        order.setBillstate("IL");
        order.setBillzip("67890");
        order.setBillcountry("USA");
        order.setCourier("UPS");
        order.setTotalprice(new BigDecimal("100.00"));
        order.setBilltofirstname("John");
        order.setBilltolastname("Doe");
        order.setShiptofirstname("Jane");
        order.setShiptolastname("Doe");
        order.setCreditcard("1234567890");
        order.setExprdate("12/25");
        order.setCardtype("Visa");
        order.setLocale("en_US");
        order.setStatus("P");

        assertEquals(1, order.getOrderid());
        assertEquals("j2ee", order.getUserid());
        assertEquals(now, order.getOrderdate());
        assertEquals("100.00", order.getTotalprice().toPlainString());
        assertEquals("P", order.getStatus());
    }
}