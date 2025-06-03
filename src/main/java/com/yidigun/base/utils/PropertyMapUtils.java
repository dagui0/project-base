package com.yidigun.base.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class PropertyMapUtils {

    /// 클래스의 메소드를 검색하여 프로퍼티 목록을 찾는다.
    /// @param clazz 프로퍼티를 검색할 클래스
    /// @return 프로퍼티 이름과 [Property] 객체를 매핑한 [Map]
    public static Map<String, Property> scanPropertiesToMap(Class<?> clazz) {

        // @Accessors(fluent = true), @Accessors(chain = true)에 의존할 것인가?
        // boolean lombokFluent = clazz.isAnnotationPresent(Accessors.class) &&
        //         clazz.getAnnotation(Accessors.class).fluent();
        // boolean lombokChain = clazz.isAnnotationPresent(Accessors.class) &&
        //         clazz.getAnnotation(Accessors.class).chain();

        Map<String, Method> getterMap = new HashMap<>();
        Map<String, Method> setterMap = new HashMap<>();
        Map<String, Field> fieldMap = Arrays.stream(clazz.getDeclaredFields())
                .filter(f ->
                        f.getDeclaringClass() != Object.class &&
                                !Modifier.isStatic(f.getModifiers()) &&
                                !f.isSynthetic())
                .collect(Collectors.toMap(
                        Field::getName,
                        Function.identity(),
                        (existing, replacement) -> existing));

        // 모든 메소드에 대해서 getter/setter를 찾는다.
        for (Method m: clazz.getMethods()) {
            if (!Modifier.isPublic(m.getModifiers()) ||
                    m.getDeclaringClass() == Object.class ||
                    Modifier.isStatic(m.getModifiers()) ||
                    m.isSynthetic())
                continue;

            // Getter
            if (m.getParameterCount() == 0 && m.getReturnType() != void.class) {

                // priority: fluent, isXXX(), then getXXX()
                Field field = fieldMap.get(m.getName());
                if (field != null && m.getReturnType() == field.getType()) { // 같은 이름의 필드가 존재
                    getterMap.merge(m.getName(), m, (e, r) -> r);
                }
                else if (m.getName().startsWith("is") && m.getReturnType() == boolean.class) {
                    tryPropertyNameFromMethodName(m, "is")
                            .ifPresent((name) ->
                                    // 기존 것이 fluent가 아니면 덮어씀
                                    getterMap.merge(name, m,
                                            (e, r) -> (e.getName().equals(name))? e: r));
                }
                else if (m.getName().startsWith("get")) {
                    tryPropertyNameFromMethodName(m, "get")
                            .ifPresent((name) ->
                                    // 없는 경우만 추가
                                    getterMap.computeIfAbsent(name, (n) -> m));
                }
            }
            // Setter
            else if (m.getParameterCount() == 1) {

                // priority: fluent, setXXX()
                Field field = fieldMap.get(m.getName());
                if (field != null && field.getType() == m.getParameterTypes()[0] &&
                        (m.getReturnType() == clazz || m.getReturnType() == void.class)) {
                    setterMap.merge(m.getName(), m, (e, r) -> r);
                }
                else if (m.getName().startsWith("set") && m.getReturnType() == void.class) {
                    tryPropertyNameFromMethodName(m, "set")
                            .ifPresent((name) ->
                                    // 없는 경우만 추가
                                    setterMap.computeIfAbsent(name, (n) -> m));
                }
            }
        }

        // combine getter and setter methods into property
        Map<String, Property> properties = new HashMap<>();
        // read-write or read-only properties
        for (Map.Entry<String, Method> entry : getterMap.entrySet()) {
            String propertyName = entry.getKey();
            Method getter = entry.getValue();
            Method setter = setterMap.get(propertyName);
            properties.put(propertyName, new Property(propertyName, getter, setter));
        }
        // for write-only properties
        for (Map.Entry<String, Method> entry : setterMap.entrySet()) {
            String propertyName = entry.getKey();
            if (properties.containsKey(propertyName))
                continue;
            Method setter = entry.getValue();
            properties.put(propertyName, new Property(propertyName, null, setter));
        }

        return Collections.unmodifiableMap(properties);
    }

    /// 메소드명 기준으로 프로퍼티명을 추정함.
    /// [java.beans.Introspector#decapitalize(String)]와 같은 로직을 사용하지만
    /// `java.desktop` 모듈에 속하므로 직접 구현함
    /// @param method 프로퍼티를 추정할 메소드
    /// @param prefix 접두사 (예: "get", "is", "set")
    /// @return 프로퍼티 이름을 포함하는 [Optional] 객체
    /// @see java.beans.Introspector#decapitalize(String)
    public static Optional<String> tryPropertyNameFromMethodName(Method method, String prefix) {
        String name = method.getName();

        // `get`, `is`, `set` 만 있는 경우 무시
        if (name.isEmpty() || name.equals(prefix))
            return Optional.empty();
            // `get`, `is`, `set` 접두사
        else if (name.startsWith(prefix)) {
            name = name.substring(prefix.length());

            // decapitalize the first letter
            if (name.length() > 1 &&
                    Character.isUpperCase(name.charAt(1)) &&
                    Character.isUpperCase(name.charAt(0)))
                return Optional.of(name);

            char[] chars = name.toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            return Optional.of(new String(chars));
        }
        // fluent style?
        else {
            return Optional.of(name);
        }
    }
}
