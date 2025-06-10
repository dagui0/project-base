package com.yidigun.base.utils;

import java.lang.reflect.Method;
import java.util.Map;

/// 프로퍼티 정보를 담는 DTO
///
/// @see Map
interface PropertyHandle {

    static <H extends PropertyHandle> H of(String propertyName, Method getterMethod, Method setterMethod, Class<H> handleType) {
        return handleType.cast(new ReflectionProperty(propertyName, getterMethod, setterMethod));
    }

    String name();

    boolean containsValue(Object target, Object value);

    Object getValue(Object target);

    Object setValue(Object target, Object value);
}
