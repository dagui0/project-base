package com.yidigun.base.utils;

import org.junit.jupiter.api.Test;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PropertyMapAdapterPropManipulationTest {

    @Test
    public void testNewProperty() {
        SomeBean bean = new SomeBean();
        bean.setName("John");
        bean.setAge(30);

        Map<String, Object> adapter = PropertyMap.of(bean);

        adapter.put("email", "a@b.com");
        assertFalse(adapter.containsKey("email"));
        assertNull(adapter.get("email"));

        Map.Entry<String, Object> emailEntry = new AbstractMap.SimpleEntry<>("email", "a@b.com");
        adapter.entrySet().add(emailEntry);
        assertFalse(adapter.containsKey("email"));
        assertNull(adapter.get("email"));

        Set<Map.Entry<String, Object>> entries = Set.of(
                emailEntry,
                new AbstractMap.SimpleEntry<>("name", "Jane"),
                new AbstractMap.SimpleEntry<>("age", 25)
        );
        adapter.entrySet().addAll(entries);
        assertEquals("Jane", adapter.get("name"));
        assertEquals(25, adapter.get("age"));
        assertFalse(adapter.containsKey("email"));
        assertNull(adapter.get("email"));
    }

    @Test
    public void testDeleteProperty() {
        SomeBean bean = new SomeBean();
        bean.setName("John");
        bean.setAge(30);

        Map<String, Object> adapter = PropertyMap.of(bean);

        assertThrows(UnsupportedOperationException.class, () -> adapter.remove("name"));

        assertTrue(adapter.containsKey("name"));
        assertEquals("John", adapter.get("name"));
    }

    @Test
    public void testClear() {
        SomeBean bean = new SomeBean();
        bean.setName("John");
        bean.setAge(30);

        Map<String, Object> adapter = PropertyMap.of(bean);
        assertThrows(UnsupportedOperationException.class, adapter::clear);
        assertThrows(UnsupportedOperationException.class, adapter.entrySet()::clear);
    }

    @Test
    public void testRemoveAllAndRetainAll() {
        SomeBean bean = new SomeBean();
        bean.setName("John");
        bean.setAge(30);

        Map<String, Object> adapter = PropertyMap.of(bean);

        Set<Map.Entry<String, Object>> toRemove = Set.of(
                new AbstractMap.SimpleEntry<>("name", "John"),
                new AbstractMap.SimpleEntry<>("age", 30)
        );
        assertThrows(UnsupportedOperationException.class, () -> {
            adapter.entrySet().removeAll(toRemove);
        });
        assertThrows(UnsupportedOperationException.class, () -> {
            adapter.entrySet().retainAll(toRemove);
        });
    }
}

@SuppressWarnings({"LombokGetterMayBeUsed", "LombokSetterMayBeUsed"})
class SomeBean {
    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
