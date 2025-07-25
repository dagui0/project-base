package com.yidigun.base.utils;

import java.util.Map;
import java.util.Objects;

/// 객체 프로퍼티를 이용한 [Map.Entry] 구현체
/// @see Map.Entry
final class PropertyEntry implements Map.Entry<String, Object> {
    private final Object target;
    private final PropertyHandle property;

    PropertyEntry(Object target, PropertyHandle property) {
        this.target = target;
        this.property = property;
    }

    @Override
    public String getKey() {
        return property.name();
    }

    @Override
    public Object getValue() {
        return property.getValue(target);
    }

    @Override
    public Object setValue(Object value) {
        return property.setValue(target, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Map.Entry<?, ?> entry)) return false;
        return Objects.equals(getKey(), entry.getKey()) &&
                Objects.equals(getValue(), entry.getValue());
    }

    @Override
    public int hashCode() {
        return (getKey()==null   ? 0 : getKey().hashCode()) ^
               (getValue()==null ? 0 : getValue().hashCode());
    }
}
