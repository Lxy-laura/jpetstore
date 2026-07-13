package com.jpetstore.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    private Item item;

    @BeforeEach
    void setUp() {
        item = new Item();
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(item);
    }

    @Test
    void testAllFields() {
        item.setItemid("EST-1");
        item.setProductid("FI-SW-01");
        item.setListprice(new BigDecimal("19.95"));
        item.setUnitcost(new BigDecimal("10.00"));
        item.setSupplier(1);
        item.setStatus("P");
        item.setAttr1("Attr1");
        item.setAttr2("Attr2");
        item.setAttr3("Attr3");
        item.setAttr4("Attr4");
        item.setAttr5("Attr5");
        item.setQty(1000);

        assertEquals("EST-1", item.getItemid());
        assertEquals("FI-SW-01", item.getProductid());
        assertEquals(new BigDecimal("19.95"), item.getListprice());
        assertEquals(Integer.valueOf(1000), item.getQty());
    }

    @Test
    void testSetAndGetProduct() {
        Product product = new Product();
        product.setProductid("FI-SW-01");
        item.setProduct(product);
        assertNotNull(item.getProduct());
        assertEquals("FI-SW-01", item.getProduct().getProductid());
    }
}