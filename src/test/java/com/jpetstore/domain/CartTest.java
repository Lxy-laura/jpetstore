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

    // ==================== 以下为补充的测试用例 ====================

    // --- 边界值测试 ---

    /**
     * 设置数量为0 -> 商品应从购物车移除
     */
    @Test
    void testSetQuantityToZeroRemovesItem() {
        cart.addItem(item1);
        cart.setQuantity("EST-1", 0);
        assertEquals(0, cart.getNumberOfItems());
        assertTrue(cart.isEmpty());
    }

    /**
     * 设置数量为负数 -> 商品应从购物车移除（根据 Cart.setQuantity 实现）
     */
    @Test
    void testSetQuantityToNegativeRemovesItem() {
        cart.addItem(item1);
        cart.setQuantity("EST-1", -5);
        assertEquals(0, cart.getNumberOfItems());
    }

    /**
     * 对不存在的商品设置数量 -> 不影响购物车
     */
    @Test
    void testSetQuantityForNonExistentItem() {
        cart.addItem(item1);
        cart.setQuantity("NOT-EXIST", 5);
        assertEquals(1, cart.getNumberOfItems());
    }

    /**
     * 对不存在的商品增量 -> 不影响购物车
     */
    @Test
    void testIncrementNonExistentItem() {
        cart.addItem(item1);
        cart.incrementQuantity("NOT-EXIST");
        assertEquals(1, cart.getNumberOfItems());
    }

    // --- 金额计算测试 ---

    /**
     * 空购物车总价应为0
     */
    @Test
    void testGetSubTotalEmptyCart() {
        assertEquals(BigDecimal.ZERO, cart.getSubTotal());
    }

    /**
     * 多个商品多数量总价计算
     */
    @Test
    void testGetSubTotalMultipleItemsMultipleQuantity() {
        cart.addItem(item1);  // 16.50 * 1
        cart.addItem(item2);  // 16.50 * 1
        cart.incrementQuantity("EST-1");  // 16.50 * 2

        // 16.50 * 2 + 16.50 * 1 = 49.50
        assertEquals(new BigDecimal("49.50"), cart.getSubTotal());
    }

    /**
     * 移除商品后总价应重新计算
     */
    @Test
    void testGetSubTotalAfterRemove() {
        cart.addItem(item1);  // 16.50
        cart.addItem(item2);  // 16.50
        // 总价 = 33.00
        assertEquals(new BigDecimal("33.00"), cart.getSubTotal());

        cart.removeItemById("EST-1");
        // 总价 = 16.50
        assertEquals(new BigDecimal("16.50"), cart.getSubTotal());
    }

    /**
     * 清空后总价应为0
     */
    @Test
    void testGetSubTotalAfterClear() {
        cart.addItem(item1);
        cart.addItem(item2);
        cart.clear();
        assertEquals(BigDecimal.ZERO, cart.getSubTotal());
    }

    // --- CartItem 测试 ---

    /**
     * CartItem 设置指定数量后计算总价
     */
    @Test
    void testCartItemSetQuantityAndTotal() {
        CartItem cartItem = new CartItem(item1);
        cartItem.setQuantity(10);
        // 16.50 * 10 = 165.00
        assertEquals(new BigDecimal("165.00"), cartItem.getTotalPrice());
    }

    /**
     * CartItem 多次增量
     */
    @Test
    void testCartItemMultipleIncrements() {
        CartItem cartItem = new CartItem(item1);
        cartItem.incrementQuantity();
        cartItem.incrementQuantity();
        cartItem.incrementQuantity();
        assertEquals(4, cartItem.getQuantity());
    }

    /**
     * item 为 null 时 totalPrice 应为 0
     */
    @Test
    void testCartItemTotalPriceWithNullItem() {
        CartItem cartItem = new CartItem(item1);
        cartItem.setItem(null);
        assertEquals(BigDecimal.ZERO, cartItem.getTotalPrice());
    }

    /**
     * item 的 listprice 为 null 时 totalPrice 应为 0
     */
    @Test
    void testCartItemTotalPriceWithNullPrice() {
        Item nullPriceItem = new Item();
        nullPriceItem.setItemid("EST-NULL");
        nullPriceItem.setListprice(null);
        CartItem cartItem = new CartItem(nullPriceItem);
        assertEquals(BigDecimal.ZERO, cartItem.getTotalPrice());
    }

    // --- 购物车状态测试 ---

    /**
     * 添加后移除 -> 购物车应为空
     */
    @Test
    void testAddThenRemoveResultsEmpty() {
        cart.addItem(item1);
        assertFalse(cart.isEmpty());
        cart.removeItemById("EST-1");
        assertTrue(cart.isEmpty());
    }

    /**
     * 清空后再清空 -> 不报错
     */
    @Test
    void testClearTwice() {
        cart.clear();
        cart.clear();
        assertTrue(cart.isEmpty());
    }
}