package com.yidigun.base.utils;

import java.lang.invoke.*;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

/// [LambdaMetafactory]I를 이용한 [PropertyHandle] 구현체
record LambdaProperty(String name, Function<Object, Object> getter, BiConsumer<Object, Object> setter) implements PropertyHandle {

    public static LambdaProperty of(MethodHandleProperty mhProperty, MethodHandles.Lookup lookup) {
        try {
            return new LambdaProperty(mhProperty.name(),
                    createGetter(mhProperty, lookup),
                    createSetter(mhProperty, lookup));
        } catch (Throwable e) {
            throw new PropertyMapException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Function<Object, Object> createGetter(MethodHandleProperty mhProperty, MethodHandles.Lookup lookup) throws Throwable {
        if (mhProperty.getter() == null) {
            return null;
        }

        MethodHandle getterHandle = mhProperty.getter();
        MethodType targetType = MethodType.methodType(Object.class, Object.class);
        CallSite site = LambdaMetafactory.metafactory(
                lookup,
                "apply",
                MethodType.methodType(Function.class),
                targetType,
                getterHandle,
                getterHandle.type().changeReturnType(Object.class)
        );
        return (Function<Object, Object>)site.getTarget().invoke();
    }

    @SuppressWarnings("unchecked")
    private static BiConsumer<Object, Object> createSetter(MethodHandleProperty mhProperty, MethodHandles.Lookup lookup) throws Throwable {
        if (mhProperty.setter() == null) {
            return null;
        }

        MethodHandle setterHandle = mhProperty.setter();
        MethodType targetType = MethodType.methodType(void.class, Object.class, Object.class);

        CallSite site = LambdaMetafactory.metafactory(
                lookup,
                "accept",
                MethodType.methodType(BiConsumer.class),
                targetType,
                setterHandle,
                setterHandle.type().wrap()
                        .changeReturnType(void.class)
        );
        return (BiConsumer<Object, Object>)site.getTarget().invoke();
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
        else
            return getter.apply(target);
    }

    @Override
    public Object setValue(Object target, Object value) {
        if (setter == null) {
            return null;
        }

        Object oldValue = null;
        if (getter != null)
            oldValue = getter.apply(target);
        setter.accept(target, value);
        return oldValue;
    }
}
