package com.yidigun.base.processors;

import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CsvParserTest {

    @Test
    public void testParseCsv() {

        String name = "com/yidigun/base/processors/test-status.txt";
        URL url = getClass().getClassLoader().getResource(name);
        assertNotNull(url, "Resource not found: " + name);
    }
}

