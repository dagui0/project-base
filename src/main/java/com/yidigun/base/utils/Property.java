package com.yidigun.base.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/// 프로퍼티 정보를 담는 DTO
///
/// @see Map
record Property(String name, Method getter, Method setter) {

    public boolean containsValue(Object target, Object value) {

        // write-only 프로퍼티에 대한 확인은 false로 처리
        if (getter == null) {
            return false;
        }
        try {
            Object propertyValue = getter.invoke(target);
            return Objects.equals(propertyValue, value);
        } catch (InvocationTargetException e) {
            throw new PropertyMapException(e.getMessage(), e.getCause());
        } catch (IllegalAccessException e) {
            throw new PropertyMapException(e.getMessage(), e);
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
