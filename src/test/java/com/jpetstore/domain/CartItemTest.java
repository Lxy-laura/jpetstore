package com.jpetstore.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    private Item item;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        item = new Item();
        item.setItemid("EST-1");
        item.setListprice(new BigDecimal("19.95"));
        cartItem = new CartItem(item);
    }

    @Test
    void testConstructorWithItem() {
        assertEquals(item, cartItem.getItem());
        assertEquals(1, cartItem.getQuantity());
        assertTrue(cartItem.isInStock());
    }

    @Test
    void testNoArgsConstructor() {
        CartItem emptyCartItem = new CartItem();
        assertNull(emptyCartItem.getItem());
        assertEquals(0, emptyCartItem.getQuantity());
        assertFalse(emptyCartItem.isInStock());
    }

    @Test
    void testIncrementQuantity() {
        assertEquals(1, cartItem.getQuantity());
        cartItem.incrementQuantity();
        assertEquals(2, cartItem.getQuantity());
    }

    @Test
    void testGetTotalPriceWithItemAndPrice() {
        BigDecimal total = cartItem.getTotalPrice();
        assertEquals(new BigDecimal("19.95"), total);
    }

    @Test
    void testGetTotalPriceMultipleQuantity() {
        cartItem.incrementQuantity();
        cartItem.incrementQuantity();
        BigDecimal total = cartItem.getTotalPrice();
        assertEquals(new BigDecimal("59.85"), total);
    }

    @Test
    void testGetTotalPriceNullItem() {
        CartItem emptyCartItem = new CartItem();
        BigDecimal total = emptyCartItem.getTotalPrice();
        assertEquals(BigDecimal.ZERO, total);
    }

    @Test
    void testGetTotalPriceNullListprice() {
        Item noPriceItem = new Item();
        noPriceItem.setItemid("EST-2");
        CartItem noPriceCartItem = new CartItem(noPriceItem);
        BigDecimal total = noPriceCartItem.getTotalPrice();
        assertEquals(BigDecimal.ZERO, total);
    }

    @Test
    void testSetItem() {
        Item newItem = new Item();
        newItem.setItemid("EST-3");
        cartItem.setItem(newItem);
        assertEquals(newItem, cartItem.getItem());
    }

    @Test
    void testSetQuantity() {
        cartItem.setQuantity(5);
        assertEquals(5, cartItem.getQuantity());
    }

    @Test
    void testSetInStock() {
        cartItem.setInStock(false);
        assertFalse(cartItem.isInStock());
    }
}