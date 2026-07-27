package com.jpetstore.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyBatisPlusConfigTest {
    @Test
    void testInstantiation() {
        MyBatisPlusConfig config = new MyBatisPlusConfig();
        assertNotNull(config);
    }
}