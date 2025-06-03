package com.yidigun.base.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/// 값 객체를 [Map] 인터페이스로 전환하는 어댑터.
///
/// 이 어댑터는 Fluent 스타일의 프로퍼티로 설계된 클래스를
/// JavaBeans 스타일의 프로퍼티를 요구하는 상황에 간단하게 변환할 목적으로 만들어졌다.
/// (대부분 JavaBeans 객체를 요구하는 상황에서 [Map] 인터페이스는 호환되기 때문임)
///
/// 이 클래스는 Reflection API를 사용하므로 자주 사용되면 성능 문제를 야기할 수 있다.
/// 그런 경우 JavaBeans 스타일의 프로퍼티를 지원하도록 하거나
/// 클래스별로 개별적으로 어댑터를 개발하는 것이 바람직하다.
///
/// ## 사용 예시
///
/// ```java
/// if (fluentObj.createDate() == null)
///     fluentObj.createDate(new Instant());
/// Map<String, Object> map = PropertyMapAdapter.of(fluentObj);
/// Instant createDate = (Instant) map.get("createDate");
/// ```
///
/// ## 프로퍼티 접근자 판단 기준
///
/// * Fluent API
///   * 메소드명이 필드명과 같은 경우
///   * 인수가 없고, 반환 자료형이 필드와 동일하면 getter
///   * 인수가 하나이고, 인수 타입이 필드와 동일하며,
///     반환 자료형이 `void` 또는 adaptee 클래스와 동일하면 setter
/// * JavaBeans 스타일
///   * 메소드명에서 `get`, `is`, `set` 접두사를 제거
///   * 첫 두글자가 모두 대문자인 경우 그대로 사용 (`getSName()` -> `SName`)
///   * 그렇지 않은경우 첫 글자를 소문자로 변경 (`getName()` -> `name`, `isValid()` -> `valid`)
///   * 메소드명에서 프로퍼티명 결정하는 기준은 [java.beans.Introspector#decapitalize(String)]와 같지만,
///     직접 구현되어 있다. 이는 `java.desktop` 모듈에 속하는 클래스에 대한 의존성을 배제하기 위해서임.
///
/// 동일한 프로퍼티로 인식되는 메소드가 여러개 있을 경우 다음과 같은 우선순위로 선택한다.
///
/// * Getter
///   1. Fluid API: 필드명과 동일한 이름(`T name()`)
///   2. `boolean isFlag()`
///   3. `T getName()`
/// * Setter
///   1. Fluid API: 필드명과 동일한 이름(`자신클래스 name(T value)` 또는 `void name(T value)`)
///   2. `void setName(T value)`
///
/// ```java
/// class FluentObj {
///     private boolean valid;
///
///     public boolean valid() { ... }      // 1순위
///     public boolean isValid() { ... }    // 2순위
///     public Boolean getValid() { ... }   // 3순위
///
///     public FluentObj valid(boolean valid) { ... } // 1순위
///     public void      valid(boolean valid) { ... } // 1순위
///     public void setValid(Boolean valid) { ... }   // 2순위
/// }
/// ```
///
/// ## 프로퍼티 접근자의 유무에 따른 [Map] 메서드 동작
///
/// * 존재하지 않는 프로퍼티에 대한 접근
///   * [Map#get(Object)], [Map.Entry#getValue()]은 `null`을 반환
///   * [Map#put(Object, Object)], [Map.Entry#setValue(Object)]는 무시 (무조건 `null`을 반환)
/// * read-write 프로퍼티: getter와 setter가 모두 존재하는 경우
///   * [Map#get(Object)], [Map#put(Object, Object)], [Map#containsValue(Object)],
///     [Map.Entry#getValue()], [Map.Entry#setValue(Object)] 모두 [Map] 규약에 맞게 동작함
/// * read-only 프로퍼티: getter만 존재하는 경우
///   * [Map#put(Object, Object)], [Map.Entry#setValue(Object)]는 무시 (무조건 `null`을 반환)
/// * write-only 프로퍼티: setter만 존재하는 경우
///   * [Map#get(Object)], [Map.Entry#getValue()]은 `null`을 반환
///   * [Map#put(Object, Object)], [Map.Entry#setValue(Object)]의 경우 기존 값의 존재여부와 무관하게 항상 `null`을 반환
///   * [Map#containsValue(Object)]는 확인이 불가능하므로 매치되는 속성값이 있더라도 `false` 반환
/// * 다음 메소드들에 대해서는 프로퍼티가 존재하는 경우 값을 변경하고(writable 하면), 그렇지 않은 경우 무시한다.
///   * [Map#putAll(Map)]
///   * [Map#entrySet()]의 [Set#add(Object)]
///   * [Map#entrySet()]의 [Set#addAll(Collection)]
///
/// ## Lombok과의 호환성
///
/// `lombok.config`에서 `lombok.accessors.fluent = true`로 설정하거나,
/// `@Accessors(fluent = true)` 같이 설정해서 fluent API 방식의 컨벤션을 사용할 경우
/// 이 어댑터와 함께 사용할 수 있다.
///
/// 하지만 `@Accessors`는 아직 experimental 기능인 것으로 보여서(1.18.38 기준),
/// 직접적으로 해당 어노테이션을 사용하여 fluent 접근자를 확인하는데 기준으로 사용하지 않는다.
///
/// @see Map
/// @see lombok.experimental.Accessors#fluent
@SuppressWarnings("LombokGetterMayBeUsed")
public final class PropertyMapAdapter implements Map<String, Object> {

    /// [Map] 인터페이스로 변환할 객체
    private final Object adaptee;

    /// adaptee 클래스에 정의된 프로퍼티 정보를 담는 맵
    private final Map<String, Property> properties;

    /// 클래스별 프로퍼티 정보를 캐싱하기 위한 [Map]
    private static final Map<Class<?>, Map<String, Property>> propertiesCache = new ConcurrentHashMap<>();

    /// 생성자
    private PropertyMapAdapter(Object adaptee) {
        this.adaptee = adaptee;
        this.properties = findProperties(adaptee.getClass());
    }

    /// 어댑터 객체를 생성하는 팩토리 메소드
    /// @param adaptee [Map] 인터페이스로 변환할 객체
    /// @return PropertyMapAdapter 인스턴스
    public static PropertyMapAdapter of(Object adaptee) {
        if (adaptee == null) {
            throw new IllegalArgumentException("Adaptee cannot be null");
        }
        return new PropertyMapAdapter(adaptee);
    }

    /// 원본 객체의 참조를 반환
    /// @return adaptee 객체
    public Object getAdaptee() {
        return adaptee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Map<?, ?> map)) return false;
        if (size() != map.size()) return false;
        try {
            return entrySet().equals(map.entrySet());
        }
        catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return entrySet().hashCode();
    }

    @Override
    public String toString() {
        return adaptee.toString();
    }

    /// adaptee 클래스에 정의된 프로퍼티를 찾는다.
    /// [ConcurrentHashMap]을 활용한 내부 캐시를 사용한다.
    /// @param clazz 프로퍼티를 검색할 클래스
    /// @return 프로퍼티 이름과 [Property] 객체를 매핑한 [Map]
    private static Map<String, Property> findProperties(Class<?> clazz) {
        return propertiesCache.computeIfAbsent(clazz, PropertyMapUtils::scanPropertiesToMap);
    }

    /*
     * java.util.Map
     */

    /// adaptee 클래스에 정의된 프로퍼티 개수를 반환
    /// @return 프로퍼티 개수
    /// @see Map#size()
    @Override
    public int size() {
        return properties.size();
    }

    /// adaptee 객체의 프로퍼티가 존재하는지 여부.
    /// @return 프로퍼티가 존재하면 `true`, 그렇지 않으면 `false`
    /// @see Map#isEmpty()
    @Override
    public boolean isEmpty() {
        return properties.isEmpty();
    }

    /// adaptee 객체의 프로퍼티 이름이 존재하는지 여부.
    /// @param key 조회할 프로퍼티 이름
    /// @return 프로퍼티 이름이 존재하면 `true`, 그렇지 않으면 `false`
    /// @see Map#containsKey(Object)
    @Override
    public boolean containsKey(Object key) {
        return (key instanceof String strKey) &&
                properties.containsKey(strKey);
    }

    /// adaptee 객체의 속성 값을 조회한다.
    /// adaptee 클래스에 정의되지 않은 속성에 대한 조회는 `null`을 반환한다.
    /// @param key 조회할 프로퍼티 이름
    /// @return 프로퍼티 값 또는 `null`
    /// @see Map#get(Object)
    @Override
    public Object get(Object key) {
        return (key instanceof String strKey && properties.containsKey(strKey))?
                properties.get(strKey).getValue(adaptee):
                null;
    }

    /// adaptee 객체의 프로퍼티 값을 변경한다.
    /// adaptee 클래스에 정의되지 않은 속성에 대한 설정은 무시된다.
    /// @param key 변경할 프로퍼티 이름
    /// @param value 변경할 프로퍼티 값
    /// @return 이전 프로퍼티 값 또는 `null`
    /// @see Map#put(Object, Object)
    @Override
    public @Nullable Object put(String key, Object value) {
        return (properties.containsKey(key))?
                properties.get(key).setValue(adaptee, value):
                null;
    }

    /// 지정한 맵의 키가 adaptee 클래스에 존재하는 프로퍼티와 키가 일치하는 값만 변경된다.
    /// 클래스에 없는 프로퍼티를 추가할 방법이 없으므로,
    /// adaptee 클래스에 없는 프로퍼티는 무시된다.
    /// @see Map#putAll(Map)
    @Override
    public void putAll(@NotNull Map<? extends String, ?> m) {
        for (Map.Entry<? extends String, ?> entry : properties.entrySet()) {
            String key = entry.getKey();
            if (m.containsKey(key)) {
                put(key, m.get(key));
            }
        }
    }

    /// Adaptee 객체의 속성 중에 지정한 값을 가지고 있는지 여부.
    /// 이 메서드는 [Map] 인터페이스의 일부분이기 때문에 구현했지만
    /// 모든 프로퍼티를 Reflection API로 확인해야 하므로
    /// 성능에 문제가 있을 수 있음.
    /// @param value 확인할 값
    /// @return 프로퍼티 중에 지정한 값을 가지고 있으면 `true`, 그렇지 않으면 `false`
    /// @see Map#containsValue(Object)
    @Override
    public boolean containsValue(Object value) {
        return properties.values().stream()
                .anyMatch(property -> property.containsValue(adaptee, value));
    }

    /// 클래스에 존재하는 프로퍼티를 삭제할 방법은 없으므로 이 메소드는 지원하지 않는다.
    /// @param key 삭제할 프로퍼티 이름
    /// @throws UnsupportedOperationException 항상 발생
    /// @see Map#remove(Object)
    @Override
    public Object remove(Object key) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Remove operation is not supported");
    }

    /// 클래스에 존재하는 프로퍼티를 삭제할 방법은 없으므로 이 메소드는 지원하지 않는다.
    /// @throws UnsupportedOperationException 항상 발생
    /// @see Map#clear()
    @Override
    public void clear() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Clear operation is not supported");
    }

    /// 프로퍼티명들을 원소로 하는 [Set]을 반환.
    /// 반환되는 [Set]은 불변 컬렉션으로 수정이 불가능하다.
    /// @return 프로퍼티 이름을 포함하는 불변 [Set]
    /// @see Map#keySet()
    @Override
    public @NotNull Set<String> keySet() {
        return Collections.unmodifiableSet(properties.keySet());
    }

    /// 프로퍼티 값들을 원소로 하는 [List]를 반환.
    /// 이 컬렉션은 스냅샷으로 adaptee 객체의 프로퍼티 값이 변경되어도 동기화되지 않는다.
    /// @return 프로퍼티 값을 포함하는 [List] 스냅샷
    /// @see Map#values()
    @Override
    public @NotNull Collection<Object> values() {
        return properties.values().stream()
                .map(property -> property.getValue(adaptee))
                .toList();
    }

    /// [Map.Entry] 객체들을 원소로 하는 [Set]을 반환.
    /// 이 [Set]은 adaptee 객체의 프로퍼티를 기반으로 하며,
    /// [Map.Entry#setValue(Object)] 메소드를 통해 프로퍼티 값을 변경할 수 있다.
    /// @return 프로퍼티를 포함하는 [Set] 스냅샷
    /// @see Map#entrySet()
    @Override
    public @NotNull Set<Map.Entry<String, Object>> entrySet() {
        return new PropertyEntrySet(adaptee, properties);
    }
}
