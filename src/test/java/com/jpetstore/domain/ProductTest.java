package com.jpetstore.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(product);
    }

    @Test
    void testAllFields() {
        product.setProductid("FI-SW-01");
        product.setCategory("FISH");
        product.setName("Angelfish");
        product.setDescription("Saltwater fish");
        product.setImage("angelfish.jpg");

        assertEquals("FI-SW-01", product.getProductid());
        assertEquals("FISH", product.getCategory());
        assertEquals("Angelfish", product.getName());
        assertEquals("Saltwater fish", product.getDescription());
        assertEquals("angelfish.jpg", product.getImage());
    }

    @Test
    void testCategoryObj() {
        Category category = new Category();
        category.setCatid("FISH");
        category.setName("Fish");
        product.setCategoryObj(category);

        assertNotNull(product.getCategoryObj());
        assertEquals("FISH", product.getCategoryObj().getCatid());
    }

    @Test
    void testItems() {
        Item item1 = new Item();
        item1.setItemid("EST-1");
        Item item2 = new Item();
        item2.setItemid("EST-2");
        List<Item> items = Arrays.asList(item1, item2);

        product.setItems(items);
        assertNotNull(product.getItems());
        assertEquals(2, product.getItems().size());
    }
}