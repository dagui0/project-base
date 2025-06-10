package com.yidigun.base.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/// Reflection API를 이용한 [PropertyHandle] 구현체
record ReflectionProperty(String name, Method getter, Method setter) implements PropertyHandle {

    public ReflectionProperty(String name, Method getter, Method setter) {
        this.name = name;
        this.getter = getter;
        this.setter = setter;

        try {
            if (getter != null)
                getter.setAccessible(true);
            if (setter != null)
                setter.setAccessible(true);
        }
        catch (SecurityException e) {
            throw new PropertyMapException("Cannot access property: " + name, e);
        }
    }

    public boolean containsValue(Object target, Object value) {

        // write-only 프로퍼티에 대한 확인은 false로 처리
        if (getter == null) {
            return false;
        }
        else {
            return Objects.equals(getValue(target), value);
        }
    }

    public Object getValue(Object target) {

        // write-only 프로퍼티에 대한 접근은 null로 처리
        if (getter == null) {
            return null;
        }
        try {
            return getter.invoke(target);
        } catch (InvocationTargetException e) {
            throw new PropertyMapException(e.getMessage(), e.getCause());
        } catch (IllegalAccessException e) {
            throw new PropertyMapException(e.getMessage(), e);
        }
    }

    public Object setValue(Object target, Object value) {

        // read-only 프로퍼티에 대한 접근은 무시
        if (setter == null) {
            return null;
        }
        try {
            Object oldValue = null;
            if (getter != null)
                oldValue = getter.invoke(target);
            setter.invoke(target, value);
            return oldValue;
        } catch (InvocationTargetException e) {
            throw new PropertyMapException(e.getMessage(), e.getCause());
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new PropertyMapException(e.getMessage(), e);
        }
    }
}
