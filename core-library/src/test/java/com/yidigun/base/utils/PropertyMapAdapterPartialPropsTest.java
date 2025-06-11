package com.yidigun.base.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PropertyMapAdapterPartialPropsTest {

    @Test
    public void testReadOnlyProperty() {
        ReadOnlyProperty property = new ReadOnlyProperty("test");

        PropertyMap adapter = PropertyMaps.of(property);

        assertTrue(adapter.containsKey("name"));
        adapter.put("name", "newName");
        assertEquals("test", adapter.get("name"));
    }

    @Test
    public void testWriteOnlyProperty() {
        WriteOnlyProperty property = new WriteOnlyProperty("test");

        PropertyMap adapter = PropertyMaps.of(property);

        assertTrue(adapter.containsKey("name"));
        adapter.put("name", "newName");
        assertNull(adapter.get("name"));
        assertTrue(property.checkNameEqualsTo("newName"));
    }
}

@SuppressWarnings("LombokSetterMayBeUsed")
class WriteOnlyProperty {
    private String name;

    public WriteOnlyProperty(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean checkNameEqualsTo(String name) {
        return this.name.equals(name);
    }
}

@SuppressWarnings({"LombokGetterMayBeUsed", "ClassCanBeRecord"})
class ReadOnlyProperty {
    private final String name;

    public ReadOnlyProperty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
