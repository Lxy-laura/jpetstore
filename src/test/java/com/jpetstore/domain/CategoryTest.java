package com.jpetstore.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryTest {

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(category);
    }

    @Test
    void testAllFields() {
        category.setCatid("FISH");
        category.setName("Fish");
        category.setDescription("All kinds of fish");

        assertEquals("FISH", category.getCatid());
        assertEquals("Fish", category.getName());
        assertEquals("All kinds of fish", category.getDescription());
    }

    @Test
    void testAllArgsConstructor() {
        Category cat = new Category("DOGS", "Dogs", "All kinds of dogs");
        assertEquals("DOGS", cat.getCatid());
        assertEquals("Dogs", cat.getName());
        assertEquals("All kinds of dogs", cat.getDescription());
    }
}