package com.yidigun.base.utils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Objects;

/// MethodHandle API를 이용한 [PropertyHandle] 구현체
record MethodHandleProperty(String name, MethodHandle getter, MethodHandle setter) implements PropertyHandle {

    public static MethodHandleProperty of(ReflectionProperty reflectionProperty, MethodHandles.Lookup lookup) {
        try {
            return new MethodHandleProperty(reflectionProperty.name(),
                        reflectionProperty.getter() == null? null:
                            lookup.unreflect(reflectionProperty.getter()),
                        reflectionProperty.setter() == null? null:
                            lookup.unreflect(reflectionProperty.setter()));
        } catch (IllegalAccessException e) {
            throw new PropertyMapException(e);
        }
    }

    @Override
    public boolean containsValue(Object target, Object value) {
        if (getter == null)
            return false;
        else
            return Objects.equals(getValue(target), value);
    }

    @Override
    public Object getValue(Object target) {
        if (getter == null)
            return null;
        try {
            return getter.invoke(target);
        } catch (Throwable e) {
            throw new PropertyMapException(e);
        }
    }

    @Override
    public Object setValue(Object target, Object value) {
        if (setter == null)
            return null;
        try {
            Object oldValue = null;
            if (getter != null) {
                oldValue = getter.invoke(target);
            }
            setter.invoke(target, value);
            return oldValue;
        } catch (Throwable e) {
            throw new PropertyMapException(e);
        }
    }
}
