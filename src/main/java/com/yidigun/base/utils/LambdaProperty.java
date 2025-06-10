package com.yidigun.base.utils;

import java.lang.invoke.*;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

class LambdaProperty implements PropertyHandle {

    private final String name;
    private final Function<Object, Object> getter;
    private final BiConsumer<Object, Object> setter;

    public LambdaProperty(MethodHandleProperty mhProperty, MethodHandles.Lookup lookup) throws Throwable {
        this.name = mhProperty.name();
        this.getter = createGetter(mhProperty, lookup);
        this.setter = createSetter(mhProperty, lookup);
    }

    @SuppressWarnings("unchecked")
    private Function<Object, Object> createGetter(MethodHandleProperty mhProperty, MethodHandles.Lookup lookup) throws Throwable {
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
    private BiConsumer<Object, Object> createSetter(MethodHandleProperty mhProperty, MethodHandles.Lookup lookup) throws Throwable {
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
    public String name() { return name; }

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
