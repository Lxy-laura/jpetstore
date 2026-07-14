package com.jpetstore.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyBatisConfigTest {
    @Test
    void testInstantiation() {
        MyBatisConfig config = new MyBatisConfig();
        assertNotNull(config);
    }
}