package com.yidigun.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResultTest {

    @Test
    public void testVoidResult() {

        Result<Void> result = Result.of();

        assertTrue(result.success());
        assertNull(result.value());
    }
}
