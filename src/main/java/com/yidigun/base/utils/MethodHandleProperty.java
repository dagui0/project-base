package com.yidigun.base.utils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Objects;

class MethodHandleProperty implements PropertyHandle {

    private final String name;
    private final MethodHandle getter;
    private final MethodHandle setter;

    public MethodHandleProperty(ReflectionProperty reflectionProperty, MethodHandles.Lookup lookup) throws IllegalAccessException {
        name = reflectionProperty.name();
        getter = reflectionProperty.getter() == null? null:
                        lookup.unreflect(reflectionProperty.getter());
        setter = reflectionProperty.setter() == null? null:
                        lookup.unreflect(reflectionProperty.setter());
    }

    @Override
    public String name() { return name; }

    public MethodHandle getter() { return getter; }
    public MethodHandle setter() { return setter; }

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
