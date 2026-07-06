package com.jpetstore.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    private Cart cart;
    private Item item1;
    private Item item2;

    @BeforeEach
    void setUp() {
        cart = new Cart();

        item1 = new Item();
        item1.setItemid("EST-1");
        item1.setProductid("FI-SW-01");
        item1.setListprice(new BigDecimal("16.50"));
        item1.setUnitcost(new BigDecimal("10.00"));
        item1.setStatus("P");
        item1.setQty(10000);

        item2 = new Item();
        item2.setItemid("EST-2");
        item2.setProductid("FI-SW-01");
        item2.setListprice(new BigDecimal("16.50"));
        item2.setUnitcost(new BigDecimal("10.00"));
        item2.setStatus("P");
        item2.setQty(10000);
    }

    @Test
    void testAddItem() {
        cart.addItem(item1);
        assertEquals(1, cart.getNumberOfItems());
        assertEquals(1, cart.getCartItems().size());
    }

    @Test
    void testAddSameItemTwice() {
        cart.addItem(item1);
        cart.addItem(item1);
        assertEquals(1, cart.getNumberOfItems());
        // 数量应该增加
        Collection<CartItem> items = cart.getCartItems();
        CartItem cartItem = items.iterator().next();
        assertEquals(2, cartItem.getQuantity());
    }

    @Test
    void testAddDifferentItems() {
        cart.addItem(item1);
        cart.addItem(item2);
        assertEquals(2, cart.getNumberOfItems());
    }

    @Test
    void testRemoveItem() {
        cart.addItem(item1);
        cart.addItem(item2);
        Item removed = cart.removeItemById("EST-1");
        assertNotNull(removed);
        assertEquals("EST-1", removed.getItemid());
        assertEquals(1, cart.getNumberOfItems());
    }

    @Test
    void testRemoveNonExistentItem() {
        cart.addItem(item1);
        Item removed = cart.removeItemById("NOT-EXIST");
        assertNull(removed);
        assertEquals(1, cart.getNumberOfItems());
    }

    @Test
    void testIncrementQuantity() {
        cart.addItem(item1);
        cart.incrementQuantity("EST-1");
        Collection<CartItem> items = cart.getCartItems();
        CartItem cartItem = items.iterator().next();
        assertEquals(2, cartItem.getQuantity());
    }

    @Test
    void testSetQuantity() {
        cart.addItem(item1);
        cart.setQuantity("EST-1", 5);
        Collection<CartItem> items = cart.getCartItems();
        CartItem cartItem = items.iterator().next();
        assertEquals(5, cartItem.getQuantity());
    }

    @Test
    void testSetQuantityToZero() {
        cart.addItem(item1);
        cart.setQuantity("EST-1", 0);
        assertEquals(0, cart.getNumberOfItems());
    }

    @Test
    void testGetSubTotal() {
        cart.addItem(item1);
        cart.addItem(item2);
        // 16.50 * 1 + 16.50 * 1 = 33.00
        assertEquals(new BigDecimal("33.00"), cart.getSubTotal());
    }

    @Test
    void testGetSubTotalWithMultipleQuantity() {
        cart.addItem(item1);
        cart.incrementQuantity("EST-1");
        // 16.50 * 2 = 33.00
        assertEquals(new BigDecimal("33.00"), cart.getSubTotal());
    }

    @Test
    void testClear() {
        cart.addItem(item1);
        cart.addItem(item2);
        cart.clear();
        assertEquals(0, cart.getNumberOfItems());
        assertTrue(cart.isEmpty());
    }

    @Test
    void testIsEmpty() {
        assertTrue(cart.isEmpty());
        cart.addItem(item1);
        assertFalse(cart.isEmpty());
    }

    @Test
    void testCartItemGetTotalPrice() {
        CartItem cartItem = new CartItem(item1);
        cartItem.setQuantity(3);
        // 16.50 * 3 = 49.50
        assertEquals(new BigDecimal("49.50"), cartItem.getTotalPrice());
    }

    @Test
    void testCartItemIncrementQuantity() {
        CartItem cartItem = new CartItem(item1);
        assertEquals(1, cartItem.getQuantity());
        cartItem.incrementQuantity();
        assertEquals(2, cartItem.getQuantity());
    }
}