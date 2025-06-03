package com.yidigun.base.utils;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

/// [Property]를 활용한 [Map#entrySet()] 구현체
/// @see Map#entrySet()
class PropertyEntrySet implements Set<Map.Entry<String, Object>> {

    final Object adaptee;
    final Map<String, Property> properties;
    final Function<Property, Map.Entry<String, Object>> mapper;

    PropertyEntrySet(Object adaptee, Map<String, Property> properties) {
        this.adaptee = adaptee;
        this.properties = properties;
        this.mapper = p -> new PropertyEntry(adaptee, p);
    }

    /// 클래스에 정의된 프로퍼티의 개수를 반환
    /// @see Collection#size()
    @Override
    public int size() {
        return properties.size();
    }

    /// 클래스에 식별 가능한 프로퍼티가 존재하는지 여부
    /// @see Collection#isEmpty()
    @Override
    public boolean isEmpty() {
        return properties.isEmpty();
    }

    /// 모든 프로퍼티를 순회하는 [Iterator]를 반환
    /// @see Collection#iterator()
    @Override
    public @NotNull Iterator<Map.Entry<String, Object>> iterator() {
        return new Iterator<>() {

            private final Iterator<Property> iterator = properties.values().iterator();

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Map.Entry<String, Object> next() {
                Property property = iterator.next();
                return mapper.apply(property);
            }
        };
    }

    /// 모든 프로퍼티를 배열로 변환
    /// @see Collection#toArray()
    @Override
    public Object @NotNull [] toArray() {
        return properties.values().stream()
                .map(mapper)
                .toArray();
    }

    /// 모든 프로퍼티를 배열로 변환
    /// @see Collection#toArray(T[])
    @Override
    public <T> T @NotNull [] toArray(T @NotNull [] a) {
        return properties.values().stream()
                .map(mapper)
                .toList().toArray(a);
    }

    /// @see Collection#contains(Object)
    @Override
    public boolean contains(Object o) {
        return properties.values().stream()
                .anyMatch(p -> Objects.equals(mapper.apply(p), o));
    }

    /// @see Collection#containsAll(Collection)
    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return c.stream()
                .allMatch(o ->
                        properties.values().stream()
                                .anyMatch(p -> Objects.equals(mapper.apply(p), o)));
    }

    /// adaptee 객체의 클래스에 대한 값을 업데이트한다.
    /// 만약 제공된 [Map.Entry]의 키가 adaptee 클래스에 정의된 프로퍼티가 아닌 경우
    /// 아무런 일도 하지 않는다.
    /// @param entry 업데이트할 [Map.Entry] 객체
    /// @return 프로퍼티 값이 변경되었으면 `true`, 그렇지 않으면 `false`
    /// @see Collection#add(Object))
    @Override
    public boolean add(Map.Entry<String, Object> entry) {
        Property p = properties.get(entry.getKey());
        if (p != null) {
            Object oldValue = p.getValue(adaptee);
            if (!Objects.equals(oldValue, entry.getValue())) {
                p.setValue(adaptee, entry.getValue());
                return true; // 값이 변경되었으므로 true 반환
            }
        }
        return false;
    }

    /// adaptee 객체의 클래스에 대한 값을 일괄 업데이트한다.
    /// 만약 제공된 [Map.Entry]의 키가 adaptee 클래스에 정의된 프로퍼티가 아닌 경우
    /// 아무런 일도 하지 않는다.
    /// @param c 업데이트할 [Map.Entry]의 [Collection] 객체
    /// @return 프로퍼티 값이 하나라도 변경되었으면 `true`, 그렇지 않으면 `false`
    /// @see Collection#addAll(Collection)
    @Override
    public boolean addAll(@NotNull Collection<? extends Map.Entry<String, Object>> c) {
        return c.stream()
                .map(this::add)
                .reduce(false, Boolean::logicalOr);
    }

    /// 클래스의 프로퍼티를 삭제할 방법은 없으므로 지원하지 않음
    /// @throws UnsupportedOperationException 항상 발생
    /// @see Collection#remove(Object)
    @Override
    public boolean remove(Object o) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Remove operation is not supported");
    }

    /// 클래스의 프로퍼티를 삭제할 방법은 없으므로 지원하지 않음
    /// @throws UnsupportedOperationException 항상 발생
    /// @see Collection#removeAll(Collection)
    @Override
    public boolean removeAll(@NotNull Collection<?> c) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("RemoveAll operation is not supported");
    }

    /// 클래스의 프로퍼티를 삭제할 방법은 없으므로 지원하지 않음
    /// @throws UnsupportedOperationException 항상 발생
    /// @see Collection#retainAll(Collection)
    @Override
    public boolean retainAll(@NotNull Collection<?> c) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("RetainAll operation is not supported");
    }

    /// 클래스의 프로퍼티를 삭제할 방법은 없으므로 지원하지 않음
    /// @throws UnsupportedOperationException 항상 발생
    /// @see Collection#clear()
    @Override
    public void clear() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Clear operation is not supported");
    }

    /// 같은 [Set] 인지 검사한다.
    /// @param o 비교할 객체
    /// @return 같은 adaptee 클래스에 대한 entrySet이면 `true`, 그렇지 않으면 `false`
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Set<?> set)) return false;
        if (set.size() != size()) return false;
        return containsAll(set);
    }

    /// [Set]의 해시코드를 계산한다.
    /// @return adaptee 객체의 해시코드를 반환
    @Override
    public int hashCode() {
        return properties.values().stream()
                .map(mapper)
                .map(Map.Entry::hashCode)
                .reduce(0, Integer::sum);
    }
}
