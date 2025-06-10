package com.yidigun.base.utils.otherpkg;

import com.yidigun.base.utils.ExportProperty;
import com.yidigun.base.utils.PropertyMap;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class PropertyMapAdapterTest {

    @Test
    public void testOnlyJavaBeansStyle() {

        OnlyJavaBeans bean = new OnlyJavaBeans();
        bean.setName("John Doe");
        bean.setAge(30);
        bean.setActive(true);
        bean.setScore(100);

        PropertyMap adapter = PropertyMap.of(bean);

        testEqualsAndHashCode(adapter);
        testToString(adapter);
        testKeySet(adapter);
        testContainsValue(adapter);
        testValuesList(adapter);

        testGetAndPut(adapter);
        assertEquals("Jane Doe", bean.getName());
        assertEquals(25, bean.getAge());
        assertFalse(bean.isActive());
        assertEquals(90, bean.getScore());

        bean.setName("John Doe");
        bean.setAge(30);
        bean.setActive(true);
        bean.setScore(100);

        testEntrySet(adapter);
        assertEquals("Jane Doe", bean.getName());
        assertEquals(25, bean.getAge());
        assertFalse(bean.isActive());
        assertEquals(90, bean.getScore());

        bean.setName("John Doe");
        bean.setAge(30);
        bean.setActive(true);
        bean.setScore(100);

        testPutAll(adapter);
        assertEquals("Jane Doe", bean.getName());
        assertEquals(25, bean.getAge());
        assertFalse(bean.isActive());
        assertEquals(90, bean.getScore());
    }

    @Test
    public void testOnlyFluentApi() {

        OnlyFluentApi bean = new OnlyFluentApi().
                name("John Doe").
                age(30).
                active(true).
                score(100);

        PropertyMap adapter = PropertyMap.of(bean);

        testEqualsAndHashCode(adapter);
        testToString(adapter);
        testKeySet(adapter);
        testContainsValue(adapter);
        testValuesList(adapter);

        testGetAndPut(adapter);
        assertEquals("Jane Doe", bean.name());
        assertEquals(25, bean.age());
        assertFalse(bean.active());
        assertEquals(90, bean.score());

        bean
                .name("John Doe")
                .age(30)
                .active(true)
                .score(100);

        testEntrySet(adapter);
        assertEquals("Jane Doe", bean.name());
        assertEquals(25, bean.age());
        assertFalse(bean.active());
        assertEquals(90, bean.score());

        bean
                .name("John Doe")
                .age(30)
                .active(true)
                .score(100);

        testPutAll(adapter);
        assertEquals("Jane Doe", bean.name());
        assertEquals(25, bean.age());
        assertFalse(bean.active());
        assertEquals(90, bean.score());
    }

    @Test
    public void testMixedStyle() {

        MixedStyle bean = new MixedStyle().active(true).
                score(100);
        bean.setName("John Doe");
        bean.setAge(30);

        PropertyMap adapter = PropertyMap.of(bean);

        testEqualsAndHashCode(adapter);
        testToString(adapter);
        testKeySet(adapter);
        testContainsValue(adapter);
        testValuesList(adapter);

        testGetAndPut(adapter);
        assertEquals("Jane Doe", bean.getName());
        assertEquals(25, bean.getAge());
        assertFalse(bean.active());
        assertEquals(90, bean.score());

        bean
                .active(true)
                .score(100);
        bean.setName("John Doe");
        bean.setAge(30);

        testEntrySet(adapter);
        assertEquals("Jane Doe", bean.getName());
        assertEquals(25, bean.getAge());
        assertFalse(bean.active());
        assertEquals(90, bean.score());

        bean
                .active(true)
                .score(100);
        bean.setName("John Doe");
        bean.setAge(30);

        testPutAll(adapter);
        assertEquals("Jane Doe", bean.getName());
        assertEquals(25, bean.getAge());
        assertFalse(bean.active());
        assertEquals(90, bean.score());
    }

    @Test
    public void testDualStyle() {

        DualStyle bean = new DualStyle()
                .name("John Doe")
                .age(30)
                .active(true)
                .score(100);

        PropertyMap adapter = PropertyMap.of(bean);

        testEqualsAndHashCode(adapter);
        testToString(adapter);
        testKeySet(adapter);
        testContainsValue(adapter);
        testValuesList(adapter);

        testGetAndPut(adapter);
        assertEquals("Jane Doe", bean.name());
        assertEquals(25, bean.age());
        assertFalse(bean.active());
        assertEquals(90, bean.score());

        bean
                .name("John Doe")
                .age(30)
                .active(true)
                .score(100);

        testEntrySet(adapter);
        assertEquals("Jane Doe", bean.name());
        assertEquals(25, bean.age());
        assertFalse(bean.active());
        assertEquals(90, bean.score());

        bean
                .name("John Doe")
                .age(30)
                .active(true)
                .score(100);

        testPutAll(adapter);
        assertEquals("Jane Doe", bean.name());
        assertEquals(25, bean.age());
        assertFalse(bean.active());
        assertEquals(90, bean.score());
    }

    @Test
    public void testSubClass() {

        Calendar cal = Calendar.getInstance();
        cal.set(1973, Calendar.AUGUST, 21, 8, 20, 10);
        Date createdAt = cal.getTime();

        SubClass bean = new SubClass();
        bean.setName("John Doe");
        bean.setAge(30);
        bean.setActive(true);
        bean.setScore(100);
        bean.createdAt(createdAt);

        PropertyMap adapter = PropertyMap.of(bean);

        Set<String> expectedKeys = Set.of("name", "age", "active", "score", "createdAt");
        assertEquals(expectedKeys, adapter.keySet());

        Map<String, Object> expectedMap = Map.of(
                "name", "John Doe",
                "age", 30,
                "active", true,
                "score", 100,
                "createdAt", createdAt
        );
        assertEquals(expectedMap, adapter);


        cal.set(2025, Calendar.JUNE, 9, 13, 51, 22);
        Date newCreatedAt = cal.getTime();
        Map<String, Object> newValues = Map.of(
                "name", "Jane Doe",
                "age", 25,
                "active", false,
                "score", 90,
                "createdAt", newCreatedAt
        );
        adapter.putAll(newValues);

        assertEquals("Jane Doe", bean.getName());
        assertEquals(25, bean.getAge());
        assertFalse(bean.isActive());
        assertEquals(90, bean.getScore());
        assertEquals(newCreatedAt, bean.createdAt());
    }

    private void testKeySet(PropertyMap adapter) {
        Set<String> expectedKeys = Set.of("name", "age", "active", "score");
        assertEquals(expectedKeys, adapter.keySet());
    }

    private void testGetAndPut(PropertyMap adapter) {

        // 프로퍼티 값을 올바르게 읽을 수 있는가?
        assertEquals("John Doe", adapter.get("name"));
        assertEquals(30, adapter.get("age"));
        assertEquals(true, adapter.get("active"));
        assertEquals(100, adapter.get("score"));

        // 프로퍼티 값을 올바르게 설정할 수 있는가?
        adapter.put("name", "Jane Doe");
        adapter.put("age", 25);
        adapter.put("active", false);
        adapter.put("score", 90);
        adapter.forEach((key, value) -> {
            switch (key) {
                case "name" -> assertEquals("Jane Doe", value);
                case "age" -> assertEquals(25, value);
                case "active" -> assertEquals(false, value);
                case "score" -> assertEquals(90, value);
                default -> throw new AssertionError("Unexpected key: " + key);
            }
        });
    }

    private void testEntrySet(PropertyMap adapter) {

        // entrySet()을 통해 프로퍼티를 올바르게 읽을 수 있는가?
        for (Map.Entry<String,Object> entry: adapter.entrySet()) {
            switch (entry.getKey()) {
                case "name" -> assertEquals("John Doe", entry.getValue());
                case "age" -> assertEquals(30, entry.getValue());
                case "active" -> assertEquals(true, entry.getValue());
                case "score" -> assertEquals(100, entry.getValue());
                default -> throw new AssertionError("Unexpected key: " + entry.getKey());
            }
        }

        // entrySet()을 통해 프로퍼티 값을 올바르게 설정할 수 있는가?
        for (Map.Entry<String, Object> entry : adapter.entrySet()) {
            switch (entry.getKey()) {
                case "name" -> entry.setValue("Jane Doe");
                case "age" -> entry.setValue(25);
                case "active" -> entry.setValue(false);
                case "score" -> entry.setValue(90);
                default -> throw new AssertionError("Unexpected key: " + entry.getKey());
            }
        }
        adapter.forEach((key, value) -> {
            switch (key) {
                case "name" -> assertEquals("Jane Doe", value);
                case "age" -> assertEquals(25, value);
                case "active" -> assertEquals(false, value);
                case "score" -> assertEquals(90, value);
                default -> throw new AssertionError("Unexpected key: " + key);
            }
        });
    }

    private void testEqualsAndHashCode(PropertyMap adapter) {

        Map<String,Object> map = Map.of(
                "name", "John Doe",
                "age", 30,
                "active", true,
                "score", 100
        );
        assertEquals(adapter, map);

        int expectedHashCode = entryHashCode("name", "John Doe") +
                entryHashCode("age", 30) +
                entryHashCode("active", true) +
                entryHashCode("score", 100);

        assertEquals(expectedHashCode, adapter.hashCode());
    }

    private void testContainsValue(PropertyMap adapter) {
        assertTrue(adapter.containsValue("John Doe"));
        assertTrue(adapter.containsValue(30));
        assertTrue(adapter.containsValue(true));
        assertTrue(adapter.containsValue(100));
        assertFalse(adapter.containsValue("NonExistentValue"));
    }

    private void testValuesList(PropertyMap adapter) {
        List<Object> expectedValues = List.of(
                "John Doe",
                30,
                true,
                100
        );

        Map<Object, Long> expectedFreqMap = expectedValues.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<Object, Long> actualFreqMap = adapter.values().stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        assertEquals(expectedFreqMap, actualFreqMap);
    }

    private int entryHashCode(String key, Object value) {
        return (key == null ? 0 : key.hashCode()) ^
               (value == null ? 0 : value.hashCode());
    }

    private void testToString(PropertyMap adapter) {

        Object adaptee = adapter.getAdaptee();
        String expectedString = adaptee.getClass().getSimpleName() + "{" +
                "name='John Doe', " +
                "age=30, " +
                "active=true, " +
                "score=100" +
                '}';
        assertEquals(expectedString, adapter.toString());
    }

    private void testPutAll(PropertyMap adapter) {
        Map<String, Object> newValues = Map.of(
                "name", "Jane Doe",
                "age", 25,
                "active", false,
                "score", 90,
                "createdAt", new Date()
        );

        adapter.putAll(newValues);

        assertEquals("Jane Doe", adapter.get("name"));
        assertEquals(25, adapter.get("age"));
        assertFalse((Boolean) adapter.get("active"));
        assertEquals(90, adapter.get("score"));
        assertNull(adapter.get("createdAt"));
    }

    @Test
    public void testJavaBeansVirtualProperties() {

        JavaBeansVirtual bean = new JavaBeansVirtual();
        bean.setVirtualName("John Doe");
        bean.setVirtualAge(30);
        bean.setVirtualActive(true);
        bean.setVirtualScore(100);

        PropertyMap adapter = PropertyMap.of(bean);

        assertEquals("John Doe", adapter.get("virtualName"));
        assertEquals(30, adapter.get("virtualAge"));
        assertEquals(true, adapter.get("virtualActive"));
        assertEquals(100, adapter.get("virtualScore"));

        adapter.put("virtualName", "Jane Doe");
        adapter.put("virtualAge", 25);
        adapter.put("virtualActive", false);
        adapter.put("virtualScore", 90);

        assertEquals("Jane Doe", bean.getVirtualName());
        assertEquals(25, bean.getVirtualAge());
        assertFalse(bean.isVirtualActive());
        assertEquals(90, bean.getVirtualScore());
    }

    @Test
    public void testFluentApiVirtualProperties() {

        FluentApiVirtual bean = new FluentApiVirtual()
                .virtualName("John Doe")
                .virtualAge(30)
                .virtualActive(true)
                .virtualScore(100);

        PropertyMap adapter = PropertyMap.of(bean);
        Set<String> expectedKeys = Set.of("virtualName", "virtualAge", "virtualActive", "virtualScore");
        assertEquals(expectedKeys, adapter.keySet());

        assertEquals("John Doe", adapter.get("virtualName"));
        assertEquals(30, adapter.get("virtualAge"));
        assertEquals(true, adapter.get("virtualActive"));
        assertEquals(100, adapter.get("virtualScore"));

        adapter.put("virtualName", "Jane Doe");
        adapter.put("virtualAge", 25);
        adapter.put("virtualActive", false);
        adapter.put("virtualScore", 90);

        assertEquals("Jane Doe", bean.virtualName());
        assertEquals(25, bean.virtualAge());
        assertFalse(bean.virtualActive());
        assertEquals(90, bean.virtualScore());
    }

    @Test
    public void testNullValueHandling() {
        OnlyJavaBeans bean = new OnlyJavaBeans(); // score 필드는 null로 시작
        bean.setName("Test");
        bean.setAge(1);
        bean.setActive(false);

        PropertyMap adapter = PropertyMap.of(bean);

        assertNull(adapter.get("score"));
        assertTrue(adapter.containsKey("score"));
        assertTrue(adapter.containsValue(null)); // containsValue 구현 검증

        // null 값을 put 하는 경우
        adapter.put("name", null);
        assertNull(bean.getName());

        // Map과 equals 비교
        Map<String, Object> mapWithNull = new HashMap<>();
        mapWithNull.put("name", null);
        mapWithNull.put("age", 1);
        mapWithNull.put("active", false);
        mapWithNull.put("score", null);

        assertEquals(adapter, mapWithNull);
        assertEquals(mapWithNull.hashCode(), adapter.hashCode());
    }
}

@SuppressWarnings({"LombokGetterMayBeUsed", "LombokSetterMayBeUsed"})
class OnlyJavaBeans {
    private String name;
    private int age;
    private boolean active;
    private Integer score;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }

    @Override
    public String toString() {
        return "OnlyJavaBeans{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", active=" + active +
                ", score=" + score +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OnlyJavaBeans that)) return false;
        return age == that.age &&
                active == that.active &&
                name.equals(that.name) &&
                score.equals(that.score);
    }
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + age;
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (score != null ? score.hashCode() : 0);
        return result;
    }
}

class OnlyFluentApi {
    private String name;
    private int age;
    private boolean active;
    private Integer score;

    public String name() { return name; }
    public OnlyFluentApi name(String name) { this.name = name; return this; }
    public int age() { return age; }
    public OnlyFluentApi age(int age) { this.age = age; return this; }
    public boolean active() { return active; }
    public OnlyFluentApi active(boolean active) { this.active = active; return this; }
    public Integer score() { return score; }
    public OnlyFluentApi score(Integer score) { this.score = score; return this; }

    @Override
    public String toString() {
        return "OnlyFluentApi{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", active=" + active +
                ", score=" + score +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OnlyFluentApi that)) return false;
        return age == that.age &&
                active == that.active &&
                name.equals(that.name) &&
                score.equals(that.score);
    }
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + age;
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (score != null ? score.hashCode() : 0);
        return result;
    }
}

@SuppressWarnings({"LombokGetterMayBeUsed", "LombokSetterMayBeUsed"})
class MixedStyle {
    private String name;
    private int age;
    private boolean active;
    private Integer score;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public boolean active() { return active; }
    public MixedStyle active(boolean active) { this.active = active; return this; }
    public Integer score() { return score; }
    public MixedStyle score(Integer score) { this.score = score; return this; }

    @Override
    public String toString() {
        return "MixedStyle{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", active=" + active +
                ", score=" + score +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MixedStyle that)) return false;
        return age == that.age &&
                active == that.active &&
                name.equals(that.name) &&
                score.equals(that.score);
    }
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + age;
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (score != null ? score.hashCode() : 0);
        return result;
    }
}

/// JavaBeans 스타일은 동작 안하고, Fluent API 스타일은 동작함
class DualStyle {
    private String name;
    private int age;
    private boolean active;
    private Integer score;

    public String getName() { return null; }
    public void setName(String name) { }
    public int getAge() { return -1; }
    public void setAge(int age) { }
    public boolean isActive() { return false; }
    public void setActive(boolean active) { }
    public Integer getScore() { return null; }
    public void setScore(Integer score) { }

    public String name() { return name; }
    public DualStyle name(String name) { this.name = name; return this; }
    public int age() { return age; }
    public DualStyle age(int age) { this.age = age; return this; }
    public boolean active() { return active; }
    public DualStyle active(boolean active) { this.active = active; return this; }
    public Integer score() { return score; }
    public DualStyle score(Integer score) { this.score = score; return this; }

    @Override
    public String toString() {
        return "DualStyle{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", active=" + active +
                ", score=" + score +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DualStyle that)) return false;
        return age == that.age &&
                active == that.active &&
                name.equals(that.name) &&
                score.equals(that.score);
    }
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + age;
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (score != null ? score.hashCode() : 0);
        return result;
    }
}

class JavaBeansVirtual {

    private String name;
    private int age;
    private boolean active;
    private Integer score;

    public String getVirtualName() { return name; }
    public void setVirtualName(String name) { this.name = name; }
    public int getVirtualAge() { return age; }
    public void setVirtualAge(int age) { this.age = age; }
    public boolean isVirtualActive() { return active; }
    public void setVirtualActive(boolean active) { this.active = active; }
    public Integer getVirtualScore() { return score; }
    public void setVirtualScore(Integer score) { this.score = score; }

    @Override
    public String toString() {
        return "VirtualBeans{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", active=" + active +
                ", score=" + score +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JavaBeansVirtual that)) return false;
        return age == that.age &&
                active == that.active &&
                name.equals(that.name) &&
                score.equals(that.score);
    }
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + age;
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (score != null ? score.hashCode() : 0);
        return result;
    }
}

class FluentApiVirtual {

    private String name;
    private int age;
    private boolean active;
    private Integer score;

    @ExportProperty
    public String virtualName() { return name; }
    public FluentApiVirtual virtualName(String name) { this.name = name; return this; }
    @ExportProperty
    public int virtualAge() { return age; }
    public FluentApiVirtual virtualAge(int age) { this.age = age; return this; }
    @ExportProperty
    public boolean virtualActive() { return active; }
    public FluentApiVirtual virtualActive(boolean active) { this.active = active; return this; }
    @ExportProperty
    public Integer virtualScore() { return score; }
    public FluentApiVirtual virtualScore(Integer score) { this.score = score; return this; }

    @Override
    public String toString() {
        return "FluentApiVirtual{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", active=" + active +
                ", score=" + score +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FluentApiVirtual that)) return false;
        return age == that.age &&
                active == that.active &&
                name.equals(that.name) &&
                score.equals(that.score);
    }
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + age;
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (score != null ? score.hashCode() : 0);
        return result;
    }
}

class SubClass extends OnlyJavaBeans {
    private Date createdAt;

    public Date createdAt() { return createdAt; }
    public void createdAt(Date createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "SubClass{" +
                "name='" + getName() + '\'' +
                ", age=" + getAge() +
                ", active=" + isActive() +
                ", score=" + getScore() +
                ", createdAt=" + createdAt +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubClass that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(createdAt, that.createdAt);
    }
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), createdAt);
    }
}
