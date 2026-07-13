package com.jpetstore;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JPetStoreApplicationTest {

    @Test
    void testClassExists() {
        assertNotNull(JPetStoreApplication.class);
    }

    @Test
    void testMainMethodExists() throws NoSuchMethodException {
        assertNotNull(JPetStoreApplication.class.getMethod("main", String[].class));
    }
}