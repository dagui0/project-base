package com.yidigun.base.utils;

import java.util.Map;

public interface PropertyMap extends Map<String, Object> {

    public enum AccessMethod {
        REFLECTION,
        METHOD_HANDLE,
        LAMBDA_META_FACTORY
    }

    Object getAdaptee();

    static PropertyMap of(Object bean) {
        return of(bean, AccessMethod.LAMBDA_META_FACTORY);
    }

    static PropertyMap of(Object bean, AccessMethod method) {
        return PropertyMapAdapter.of(bean, method);
    }
}
