package com.brightspot.utils;

import java.util.HashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtilsTest {

    @Test
    public void testNumericValue_Integer() {
        int input = 1234;
        Integer result = Utils.numericValue(String.valueOf(input), Integer.class);
        Assertions.assertEquals(input, result);
    }

    @Test
    public void testNumericValue_Double() {
        double input = 1234;
        Double result = Utils.numericValue(String.valueOf(input), Double.class);
        Assertions.assertEquals(input, result);
    }

    @Test
    public void testNumericValue_Null() {
        Integer result = Utils.numericValue(null, Integer.class);
        Assertions.assertNull(result);
    }

    @Test
    public void testFindMapEntry_String() {
        String key = "key";
        String value = "text";
        String fallback = "fallback";

        HashMap<String, Object> map = new HashMap<>();
        map.put(key, value);

        String result = Utils.findMapEntry(key, map, String.class, fallback);

        Assertions.assertEquals(value, result);
    }

    @Test
    public void testFindMapEntry_Integer() {
        String key = "key";
        Integer value = 1234;
        Integer fallback = 0;

        HashMap<String, Object> map = new HashMap<>();
        map.put(key, value);

        Integer result = Utils.findMapEntry(key, map, Integer.class, fallback);

        Assertions.assertEquals(value, result);
    }

    @Test
    public void testFindMapEntry_Fallback() {
        String key = "key";
        String value = "text";
        Integer fallback = 0;

        HashMap<String, Object> map = new HashMap<>();
        map.put(key, value);

        Integer result = Utils.findMapEntry(key, map, Integer.class, fallback);

        Assertions.assertEquals(fallback, result);
    }

    @Test
    public void testAddQueryParameters_AppendToBase() {
        String uri = "https://example.com";

        String result = Utils.addQueryParameters(uri,
            "param1", "value1",
            "param2", "value2"
        );

        Assertions.assertEquals("https://example.com?param1=value1&param2=value2", result);
    }

    @Test
    public void testAddQueryParameters_AppendToExisting() {
        String uri = "https://example.com?param1=value1&param2=value2";

        String result = Utils.addQueryParameters(uri,
            "param3", "value3"
        );

        Assertions.assertEquals("https://example.com?param1=value1&param2=value2&param3=value3", result);
    }

    @Test
    public void testAddQueryParameters_AppendDuplicates() {
        String uri = "https://example.com?param1=value1&param2=value2";

        String result = Utils.addQueryParameters(uri,
            "param2", "value2"
        );

        Assertions.assertEquals("https://example.com?param1=value1&param2=value2", result);
    }

    @Test
    public void testAddQueryParameters_Null() {
        String result = Utils.addQueryParameters(null, "param1", "value1");
        Assertions.assertNull(result);
    }
}
