package com.yidigun.base.utils;

import java.util.Map;

public final class PropertyMaps {

    /// [PropertyMap] 객체를 생성한다.
    /// [PropertyMap.AccessMethod#LAMBDA_META_FACTORY]를 사용하여 프로퍼티에 접근한다.
    /// @param adaptee [Map]으로 변환할 객체
    /// @return [PropertyMap] 객체
    public static PropertyMap of(Object adaptee) {
        return of(adaptee, PropertyMap.AccessMethod.LAMBDA_META_FACTORY);
    }

    /// [PropertyMap] 객체를 생성한다.
    /// @param adaptee [Map]으로 변환할 객체
    /// @param method 프로퍼티 접근 방법
    /// @return [PropertyMap] 객체
    public static PropertyMap of(Object adaptee, PropertyMap.AccessMethod method) {
        return PropertyMapAdapter.of(adaptee, method);
    }
}
