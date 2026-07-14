package com.jpetstore.common;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ResultSupplementTest {
    @Test
    void testForbidden() {
        Result<String> result = Result.forbidden("forbidden");
        assertEquals(403, result.getCode());
        assertEquals("forbidden", result.getMessage());
        assertNull(result.getData());
    }
}