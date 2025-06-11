package com.yidigun.base.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/// [PropertyMap] 인터페이스를 구현하는 어댑터 클래스.
@SuppressWarnings("LombokGetterMayBeUsed")
final class PropertyMapAdapter implements PropertyMap {

    /// [Map] 인터페이스로 변환할 객체
    private final Object adaptee;

    /// adaptee 클래스에 정의된 프로퍼티 정보를 담는 맵
    private final Map<String, PropertyHandle> properties;

    /// 생성자
    private PropertyMapAdapter(Object adaptee, AccessMethod method) {
        this.adaptee = adaptee;
        this.properties = PropertyMapUtils.findProperties(adaptee.getClass(), method);
    }

    /// 어댑터 객체를 생성하는 팩토리 메소드
    /// @param adaptee [Map] 인터페이스로 변환할 객체
    /// @return PropertyMapAdapter 인스턴스
    public static PropertyMap of(Object adaptee, AccessMethod method) {
        if (adaptee == null) {
            throw new IllegalArgumentException("Adaptee cannot be null");
        }
        return new PropertyMapAdapter(adaptee, method);
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
