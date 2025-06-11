package com.yidigun.base.utils;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static java.util.stream.Collectors.*;

final class PropertyMapUtils {

    /// 프로퍼티 접근자 후보의 근거
    enum Basis {
        /// 근거 없음(짝이되는 메소드일 경우를 위해 임시로 추가됨)
        NONE,
        /// [ExportProperty] 어노테이션이 지정된 메소드
        EXPORTED,
        /// 필드명과 동일한 이름의 메소드(Fluent API)
        FLUENT,
        /// JavaBeans 스타일의 프로퍼티 접근자
        JAVABEANS
    }

    /// 프로퍼티 접근자 후보 DTO
    record Accessor(String propertyName, Method method, Basis basis) {

        public static Optional<Accessor> ofNullable(Class<?> clazz, Method method) {
            if (!isPossibleGetter(method) && !isPossibleSetter(method)) {
                return Optional.empty();
            }

            ExportProperty property = getExportPropertyAnnotation(clazz, method);
            if (property != null) {
                return Optional.of(new Accessor(
                        property.value().isEmpty() ? method.getName(): property.value(),
                        method, Basis.EXPORTED));
            }
            else if (hasFieldWithSameName(clazz, method)) {
                return Optional.of(new Accessor(method.getName(), method, Basis.FLUENT));
            }
            else {
                String propertyName = getPossiblePropertyName(method);
                return (propertyName == null)?
                        Optional.of(new Accessor(method.getName(), method, Basis.NONE)):
                        Optional.of(new Accessor(propertyName, method, Basis.JAVABEANS));
            }
        }

        public boolean getter() {
            return isPossibleGetter(method);
        }

        public boolean setter() {
            return isPossibleSetter(method);
        }

        public int priority() {
            return switch (basis) {
                case NONE -> Integer.MAX_VALUE;
                case EXPORTED -> 1;
                case FLUENT -> 2;
                case JAVABEANS -> method.getName().startsWith("is")? 3: 4;
            };
        }

        public Class<?> propertyType() {
            return getter()? method.getReturnType(): method.getParameterTypes()[0];
        }
    }

    /// 클래스의 메소드를 검색하여 프로퍼티 목록을 찾는다.
    ///
    /// getter priority:
    /// 0. match signature: public R methodName(void)
    /// 1. @ExportProperty annotated
    /// 2. same with field name(fluent)
    /// 3. boolean isName()
    /// 4. R getName()
    ///
    /// setter priority:
    /// 0. match signature: public {EnclosingClass|void} methodName(R value)
    /// 2. same with getter name
    /// 3. setName(R value)
    /// 4. @ExportProperty annotated (write-only)
    ///
    /// @param clazz 프로퍼티를 검색할 클래스
    /// @return 프로퍼티 이름과 [PropertyHandle] 객체를 매핑한 [Map]
    public static Map<String, PropertyHandle> scanPropertiesToMap(Class<?> clazz, Class<? extends PropertyHandle> handleType) {

        Map<String, List<Accessor>> candidates = Arrays.stream(clazz.getMethods())
                .filter(m ->
                        Modifier.isPublic(m.getModifiers()) &&
                        !Modifier.isStatic(m.getModifiers()) &&
                        !m.isSynthetic() &&
                        m.getDeclaringClass() != Object.class &&
                        (isPossibleGetter(m) || isPossibleSetter(m)))
                .filter(m -> !m.getName().equals("hashCode") &&
                        !m.getName().equals("toString"))
                .flatMap(m -> Accessor.ofNullable(clazz, m).stream())
                .collect(groupingBy(Accessor::propertyName));

        Map<String, PropertyHandle> propertyMap = candidates.entrySet().stream()
                .map(e -> {
                    String propertyName = e.getKey();
                    List<Accessor> accessors = e.getValue();

                    Optional<Accessor> getter = accessors.stream()
                            .filter(Accessor::getter)
                            .min(Comparator.comparingInt(Accessor::priority));
                    Optional<Accessor> setter = accessors.stream()
                            .filter(Accessor::setter)
                            .min(Comparator.comparingInt(Accessor::priority));

                    if (getter.isEmpty() && setter.isEmpty()) {
                        return null;
                    }
                    else if (getter.map(Accessor::basis).orElse(Basis.NONE) == Basis.NONE &&
                            setter.map(Accessor::basis).orElse(Basis.NONE) == Basis.NONE) {
                        return null;
                    }

                    // getter와 setter의 타입이 다르면
                    Class<?> getType = getter.map(Accessor::propertyType).orElse(null);
                    Class<?> setType = setter.map(Accessor::propertyType).orElse(null);
                    if (getType != null && setType != null) {
                        Class<?> wrappedGetType = boxedType(getType);
                        Class<?> wrappedSetType = boxedType(setType);
                        if (!wrappedGetType.isAssignableFrom(wrappedSetType)) {
                            // setter는 무시하고 getter만 사용
                            // TODO: 경고 로깅
                            setter = Optional.empty();
                        }
                    }

                    Method getterMethod = getter.map(Accessor::method).orElse(null);
                    Method setterMethod = setter.map(Accessor::method).orElse(null);
                    return new ReflectionProperty(propertyName, getterMethod, setterMethod);
                })
                .filter(Objects::nonNull)
                .collect(toMap(
                        PropertyHandle::name,
                        Function.identity(),
                        (e, r) -> e));

        clearReflectionCaches(clazz);
        return propertyMap;
    }

    public static Class<?> boxedType(Class<?> type) {
        return (!type.isPrimitive())? type:
                switch (type.getName()) {
                    case "int" -> Integer.class;
                    case "long" -> Long.class;
                    case "double" -> Double.class;
                    case "float" -> Float.class;
                    case "boolean" -> Boolean.class;
                    case "char" -> Character.class;
                    case "byte" -> Byte.class;
                    case "short" -> Short.class;
                    default -> type; // should not happen
                };
    }

    public static Class<?> unboxedType(Class<?> type) {
        return (type == Integer.class)? int.class:
                (type == Long.class)? long.class:
                (type == Double.class)? double.class:
                (type == Float.class)? float.class:
                (type == Boolean.class)? boolean.class:
                (type == Character.class)? char.class:
                (type == Byte.class)? byte.class:
                (type == Short.class)? short.class: type;
    }

    public static Map<String, Field> getFieldMap(Class<?> clazz) {
        return fieldMapCache.computeIfAbsent(clazz, k ->
            Arrays.stream(k.getDeclaredFields())
                    .filter(f ->
                            f.getDeclaringClass() != Object.class &&
                                    !Modifier.isStatic(f.getModifiers()) &&
                                    !f.isSynthetic())
                    .collect(toMap(
                            Field::getName,
                            Function.identity(),
                            (existing, replacement) -> existing))
        );
    }

    /// 자기 자신을 제외한 모든 상위 클래스와 인터페이스를 재귀적으로 찾는다.
    /// @param clazz 검색할 클래스
    /// @return 클래스와 그 상위 클래스, 인터페이스를 포함하는 [Set] 객체
    public static Set<Class<?>> getAllInterfacesAndSuperClasses(Class<?> clazz) {
        return allIfAndSuperCache.computeIfAbsent(clazz, k -> {;
            Set<Class<?>> found = new HashSet<>();
            getAllInterfacesAndSuperClasses(found, k);
            return found;
        });
    }

    /// 자기 자신을 제외한 모든 상위 클래스와 인터페이스를 재귀적으로 찾는다.
    /// 이 메소드는 재귀적으로 호출되어 클래스의 모든 상위 클래스와 인터페이스를 찾는다.
    /// @param found 찾은 클래스와 인터페이스를 저장할 [Set] 객체
    /// @param clazz 검색할 클래스
    private static void getAllInterfacesAndSuperClasses(Set<Class<?>> found, Class<?> clazz) {
        if (clazz == null || clazz == Object.class)
            return;

        for (Class<?> iface : clazz.getInterfaces()) {
            if (iface.isSynthetic())
                continue;
            found.add(iface);
            getAllInterfacesAndSuperClasses(found, iface);
        }

        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            found.add(clazz.getSuperclass());
            getAllInterfacesAndSuperClasses(found, clazz.getSuperclass());
        }
    }

    /// 메소드의 시그니쳐가 getter로 사용될 수 있는지 확인한다.
    /// @param method 확인할 메소드
    /// @return 메소드가 getter로 사용될 수 있으면 `true`, 그렇지 않으면 `false`
    public static boolean isPossibleGetter(Method method) {
        return method.getParameterCount() == 0 &&
                method.getReturnType() != void.class;
    }

    /// 메소드의 시그니쳐가 setter로 사용될 수 있는지 확인한다.
    /// @param method 확인할 메소드
    /// @return 메소드가 setter로 사용될 수 있으면 `true`, 그렇지 않으면 `false`
    public static boolean isPossibleSetter(Method method) {
        return method.getParameterCount() == 1 &&
                (method.getReturnType() == void.class || method.getReturnType() == method.getDeclaringClass());
    }

    /// 메소드명 기준으로 프로퍼티명을 추정함.
    ///
    /// [java.beans.Introspector#decapitalize(String)]와 같은 로직을 사용하지만
    /// `java.desktop` 모듈에 속하므로 직접 구현함
    ///
    /// @param method 프로퍼티를 추정할 메소드
    /// @return 추정된 프로퍼티명, 접두사가 없는 경우 `null`
    /// @see java.beans.Introspector#decapitalize(String)
    public static String getPossiblePropertyName(Method method) {
        String name = method.getName();
        if (name.startsWith("get")) {
            return getPossiblePropertyName(method, "get");
        }
        else if (name.startsWith("is")) {
            return getPossiblePropertyName(method, "is");
        }
        else if (name.startsWith("set")) {
            return getPossiblePropertyName(method, "set");
        }
        else {
            return null;
        }
    }

    /// 메소드명 기준으로 프로퍼티명을 추정함.
    ///
    /// [java.beans.Introspector#decapitalize(String)]와 같은 로직을 사용하지만
    /// `java.desktop` 모듈에 속하므로 직접 구현함
    ///
    /// @param method 프로퍼티를 추정할 메소드
    /// @param prefix 접두사 (예: "get", "is", "set")
    /// @return 추정된 프로퍼티명, 접두사가 없는 경우 `null`
    /// @see java.beans.Introspector#decapitalize(String)
    private static String getPossiblePropertyName(Method method, String prefix) {
        String name = method.getName();

        // `get`, `is`, `set` 만 있는 경우 무시
        if (name.isEmpty() || name.equals(prefix))
            return null;
            // `get`, `is`, `set` 접두사
        else if (name.startsWith(prefix)) {
            name = name.substring(prefix.length());

            // decapitalize the first letter
            if (name.length() > 1 &&
                    Character.isUpperCase(name.charAt(1)) &&
                    Character.isUpperCase(name.charAt(0)))
                return name;

            char[] chars = name.toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            return new String(chars);
        }
        else {
            return null;
        }
    }

    /// 메소드에 지정된 [ExportProperty] 어노테이션을 찾는다.
    ///
    /// 원래 메소드에 지정된 어노테이션은 상속되지 않지만,
    /// 메소드가 속한 클래스와 모든 상위 클래스, 인터페이스를 검색하여
    /// [ExportProperty] 어노테이션을 찾는다.
    ///
    /// @param clazz 메소드가 속한 클래스
    /// @param method 검색할 메소드
    /// @return [ExportProperty] 어노테이션, 없으면 `null`
    /// @see #getAllInterfacesAndSuperClasses(Class)
    public static ExportProperty getExportPropertyAnnotation(Class<?> clazz, Method method) {
        return getExportPropertyAnnotation(getAllInterfacesAndSuperClasses(clazz), method);
    }

    /// 메소드에 지정된 [ExportProperty] 어노테이션을 찾는다.
    ///
    /// 원래 메소드에 지정된 어노테이션은 상속되지 않지만,
    /// 메소드가 속한 클래스와 모든 상위 클래스, 인터페이스를 검색하여
    /// [ExportProperty] 어노테이션을 찾는다.
    ///
    /// @param allIfAndSuper 메소드가 속한 클래스와 그 상위 클래스, 인터페이스를 포함하는 [Set] 객체
    /// @param method 검색할 메소드
    /// @return [ExportProperty] 어노테이션, 없으면 `null`
    private static ExportProperty getExportPropertyAnnotation(Set<Class<?>> allIfAndSuper, Method method) {
        ExportProperty property = method.getAnnotation(ExportProperty.class);
        if (property != null)
            return property;

        return allIfAndSuper.stream()
                .map(iface -> {
                    try {
                        Method declaredMethod = iface.getMethod(method.getName(), method.getParameterTypes());
                        return declaredMethod.getAnnotation(ExportProperty.class);
                    } catch (NoSuchMethodException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

    }

    /// 메소드와 동일한 이름의 필드가 존재하는지 확인한다.
    ///
    /// @param clazz 메소드가 속한 클래스
    /// @param method 검색할 메소드
    /// @return 동일한 이름의 필드가 존재하면 `true`, 그렇지 않으면 `false`
    /// @see #getFieldMap(Class)
    public static boolean hasFieldWithSameName(Class<?> clazz, Method method) {
        Map<String, Field> fieldMap = getFieldMap(clazz);
        Field field = fieldMap.get(method.getName());
        return field != null &&
                ((isPossibleGetter(method) && field.getType() == method.getReturnType()) ||
                (isPossibleSetter(method) && field.getType() == method.getParameterTypes()[0]));
    }

    /// adaptee 클래스에 정의된 프로퍼티를 찾는다.
    ///
    /// [ConcurrentHashMap]을 활용한 내부 캐시를 사용한다.
    ///
    /// [PropertyMap.AccessMethod#REFLECTION] <-
    /// [PropertyMap.AccessMethod#METHOD_HANDLE] <-
    /// [PropertyMap.AccessMethod#LAMBDA_META_FACTORY] 순으로 의존 관계가 있기 때문에,
    /// 만약 [PropertyMap.AccessMethod#LAMBDA_META_FACTORY]을 사용했다면 3가지 캐시가 모두 생성된다.
    ///
    /// @param clazz 프로퍼티를 검색할 클래스
    /// @param method 프로퍼티 접근 방식
    /// @return 프로퍼티 이름과 [PropertyHandle] 객체를 매핑한 [Map]
    public static Map<String, PropertyHandle> findProperties(Class<?> clazz, PropertyMap.AccessMethod method) {
        return switch (method) {
            case REFLECTION -> findReflectionProperties(clazz);
            case METHOD_HANDLE -> findMethodHandleProperties(clazz);
            case LAMBDA_META_FACTORY -> findLambdaProperties(clazz);
        };
    }

    private static Map<String, PropertyHandle> findReflectionProperties(Class<?> clazz) {
        return reflectionPropertiesCache.computeIfAbsent(clazz, k -> {
            Map<String, PropertyHandle> properties = scanPropertiesToMap(k, ReflectionProperty.class);
            return (properties.isEmpty())? Collections.emptyMap():
                                Collections.unmodifiableMap(properties);
        });
    }

    private static Map<String, PropertyHandle> findMethodHandleProperties(Class<?> clazz) {
        return methodHandlePropertiesCache.computeIfAbsent(clazz, k -> {
            return findReflectionProperties(clazz).values().stream()
                .map(property -> {
                    return (property instanceof ReflectionProperty reflectionProperty) ?
                            MethodHandleProperty.of(reflectionProperty, getPrivateLookup(clazz)) : null;
                })
                .filter(Objects::nonNull)
                .collect(toMap(
                        PropertyHandle::name,
                        Function.identity(),
                        (e, r) -> e));
        });
    }

    private static Map<String, PropertyHandle> findLambdaProperties(Class<?> clazz) {
        return lambdaPropertiesCache.computeIfAbsent(clazz, k -> {
            return findMethodHandleProperties(clazz).values().stream()
                    .map(property -> {
                        return (property instanceof MethodHandleProperty mhProperty) ?
                                LambdaProperty.of(mhProperty, getPrivateLookup(clazz)) : null;
                    })
                    .filter(Objects::nonNull)
                    .collect(toMap(
                            PropertyHandle::name,
                            Function.identity(),
                            (e, r) -> e));
        });
    }

    private static final MethodHandles.Lookup lookup = MethodHandles.lookup();

    private static MethodHandles.Lookup getPrivateLookup(Class<?> clazz) {
        try {
            return MethodHandles.privateLookupIn(clazz, lookup);
        } catch (IllegalAccessException e) {
            throw new PropertyMapException(e);
        }
    }

    private static final Map<Class<?>, Map<String, Field>> fieldMapCache = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Set<Class<?>>> allIfAndSuperCache = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Map<String, PropertyHandle>> reflectionPropertiesCache = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Map<String, PropertyHandle>> methodHandlePropertiesCache = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Map<String, PropertyHandle>> lambdaPropertiesCache = new ConcurrentHashMap<>();

    /// 모든 클래스에 대한 임시 리플렉션 API 캐시를 지운다.
    /// 클래스 분석이 끝나면 해당 클래스에 대한 캐시를 자동으로 지우므로
    /// 이 메소드를 별도로 호출할 필요는 없다.
    public static void clearReflectionCaches() {
        fieldMapCache.clear();
        allIfAndSuperCache.clear();
    }

    /// 지정한 클레스에 대한 임시 리플렉션 API 캐시를 지운다.
    /// 이 메소드는 분석이 끝나면 자동으로 호출되므로 별도로 호출 할 필요는 없다.
    /// @param clazz 분석 중 사용된 리플렉션 API 캐시를 지울 클래스
    public static void clearReflectionCaches(Class<?> clazz) {
        fieldMapCache.remove(clazz);
        allIfAndSuperCache.remove(clazz);
    }

    /// 모든 클래스에 대한 프로퍼티 캐시를 지운다.
    /// 같은 클래스에 대해서 [PropertyMap]을 새로 생성할 경우 이 캐시가 필요하므로
    /// 함부로 지우면 성능이 떨어질 수 있다.
    /// 이 메소드는 메모리 이슈가 있는 경우 사용할 수 있으나 큰 효과가 있지는 않을 것이다.
    public static void clearPropertiesCaches() {
        reflectionPropertiesCache.clear();
        methodHandlePropertiesCache.clear();
        lambdaPropertiesCache.clear();
    }

    /// 지정한 클래스에 대한 프로퍼티 캐시를 지운다.
    /// 같은 클래스에 대해서 [PropertyMap]을 새로 생성할 경우 이 캐시가 필요하므로
    /// 함부로 지우면 성능이 떨어질 수 있다.
    /// 이 메소드는 메모리 이슈가 있는 경우 사용할 수 있으나 큰 효과가 있지는 않을 것이다.
    /// @param clazz 분석 중 사용된 프로퍼티 캐시를 지울 클래스
    public static void clearPropertiesCaches(Class<?> clazz) {
        reflectionPropertiesCache.remove(clazz);
        methodHandlePropertiesCache.remove(clazz);
        lambdaPropertiesCache.remove(clazz);
    }
}
