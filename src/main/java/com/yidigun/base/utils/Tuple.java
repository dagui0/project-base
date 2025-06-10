package com.yidigun.base.utils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/// 여러 개의 값의 묶음을 나타내는 컨테이너 객체.
/// 여러 값을 묶어서 반환하거나 전달할 때 사용 가능하다.
///
public class Tuple implements Serializable {

    @Serial
    private static final long serialVersionUID = -3044263871932530732L;

    private final Object[] values;

    private Tuple(Object... values) {
        this.values = new Object[values.length];
        System.arraycopy(values, 0, this.values, 0, values.length);
    }

    /// 여러 개의 값을 묶어서 반환하는 정적 팩토리 메소드.
    /// @param values 묶을 값들
    /// @return Tuple 객체
    public static Tuple of(Object... values) {
        return new Tuple(values);
    }

    /// `index`번째 값을 반환한다.
    /// @param index 반환할 값의 인덱스 (0부터 시작)
    /// @return 반환할 값
    public Object get(int index) {
        if (index < 0 || index >= values.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + values.length);
        }
        return values[index];
    }

    /// `index`번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 `null`을 반환한다.
    /// @param index 반환할 값의 인덱스 (0부터 시작)
    /// @param type 반환할 값의 타입
    /// @return 반환할 값
    public <T> T get(int index, Class<T> type) {
        Object value = get(index);
        return (type.isInstance(value))? type.cast(value): null;
    }

    /// `index`번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 [ClassCastException]을 던진다.
    /// @param index 반환할 값의 인덱스 (0부터 시작)
    /// @param type 반환할 값의 타입
    /// @return 반환할 값
    /// @throws ClassCastException 자료형이 일치하지 않을 경우
    public <T> T getAs(int index, Class<T> type) throws ClassCastException {
        Object value = get(index);
        if (type.isInstance(value)) {
            return type.cast(value);
        } else {
            throw new ClassCastException("Cannot cast value at index " + index + " to " + type.getName());
        }
    }

    /// `index`번째 값을 감싼 Optional 객체를 반환한다.
    /// @param index 반환할 값의 인덱스 (0부터 시작)
    /// @return Optional 객체로 감싼 값
    public Optional<Object> tryGet(int index) {
        return Optional.ofNullable(get(index));
    }

    /// `index`번째 값을 감싼 Optional 객체를 반환한다.
    /// 자료형이 일치하지 않을 경우 [Optional#empty()]를 반환한다.
    /// @param index 반환할 값의 인덱스 (0부터 시작)
    /// @param type 반환할 값의 타입
    /// @return Optional 객체로 감싼 값
    public <T> Optional<T> tryGet(int index, Class<T> type) {
        Object value = get(index);
        if (type.isInstance(value)) {
            return Optional.of(type.cast(value));
        } else {
            return Optional.empty();
        }
    }

    /// 튜플을 배열로 변환한다.
    /// @return Object 배열
    public Object[] toArray() {
        return values.clone();
    }

    /// 튜플의 크기를 반환한다.
    /// @return 튜플의 크기
    public int size() {
        return values.length;
    }

    /// 튜플이 비어있는지 확인한다.
    /// @return true: 비어있음, false: 비어있지 않음
    public boolean isEmpty() {
        return values.length == 0;
    }

    /// 튜플의 값을 List로 변환한다.
    /// @return List 객체로 변환된 값들
    public List<Object> toList() {
        return Arrays.asList(values);
    }

    @Override
    public String toString() {
        return "Tuple" + Arrays.toString(values);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tuple tuple = (Tuple) obj;
        return Arrays.equals(values, tuple.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    /// 첫번째 값을 반환한다.
    /// @return 첫번째 값
    public Object first() { return get(0); }

    /// 첫번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 `null`을 반환한다.
    /// @param type 반환할 값의 타입
    /// @return 첫번째 값
    public <T> T first(Class<T> type) { return get(0, type); }

    /// 첫번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 [ClassCastException]을 던진다.
    /// @param type 반환할 값의 타입
    /// @return 첫번째 값
    /// @throws ClassCastException 자료형이 일치하지 않을 경우
    public <T> T firstAs(Class<T> type) throws ClassCastException { return getAs(0, type); }

    /// 첫번째 값을 반환한다.
    /// @return 첫번째 값을 감싼 Optional 객체
    public Optional<Object> tryFirst() { return tryGet(0); }

    /// 첫번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 [Optional#empty()]를 반환한다.
    /// @param type 반환할 값의 타입
    /// @return 첫번째 값을 감싼 Optional 객체
    public <T> Optional<T> tryFirst(Class<T> type) { return tryGet(0, type); }

    /// 첫번째 값을 반환한다.
    /// @return 첫번째 값
    public Object getFirst() { return first(); }

    /// 첫번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 `null`을 반환한다.
    /// @param type 반환할 값의 타입
    /// @return 첫번째 값
    public <T> T getFirst(Class<T> type) { return first(type); }

    /// 첫번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 [ClassCastException]을 던진다.
    /// @param type 반환할 값의 타입
    /// @return 첫번째 값
    /// @throws ClassCastException 자료형이 일치하지 않을 경우
    public <T> T getFirstAs(Class<T> type) throws ClassCastException { return firstAs(type); }

    /// 첫번째 값을 반환한다.
    /// @return 첫번째 값을 감싼 Optional 객체
    public Optional<Object> tryGetFirst() { return tryFirst(); }

    /// 첫번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 [Optional#empty()]를 반환한다.
    /// @param type 반환할 값의 타입
    /// @return 첫번째 값을 감싼 Optional 객체
    public <T> Optional<T> tryGetFirst(Class<T> type) { return tryFirst(type); }

    /// 두번째 값을 반환한다.
    /// @return 두번째 값
    public Object second() { return get(1); }

    /// 두번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 `null`을 반환한다.
    /// @param type 반환할 값의 타입
    /// @return 두번째 값
    public <T> T second(Class<T> type) { return get(1, type); }

    /// 두번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 [ClassCastException]을 던진다.
    /// @param type 반환할 값의 타입
    /// @return 두번째 값
    /// @throws ClassCastException 자료형이 일치하지 않을 경우
    public <T> T secondAs(Class<T> type) throws ClassCastException { return getAs(1, type); }

    /// 두번째 값을 반환한다.
    /// @return 두번째 값을 감싼 Optional 객체
    public Optional<Object> trySecond() { return tryGet(1); }

    /// 두번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 [Optional#empty()]를 반환한다.
    /// @param type 반환할 값의 타입
    /// @return 두번째 값을 감싼 Optional 객체
    public <T> Optional<T> trySecond(Class<T> type) { return tryGet(1, type); }

    /// 두번째 값을 반환한다.
    /// @return 두번째 값
    public Object getSecond() { return second(); }

    /// 두번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 `null`을 반환한다.
    /// @param type 반환할 값의 타입
    /// @return 두번째 값
    public <T> T getSecond(Class<T> type) { return second(type); }

    /// 두번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 [ClassCastException]을 던진다.
    /// @param type 반환할 값의 타입
    /// @return 두번째 값
    /// @throws ClassCastException 자료형이 일치하지 않을 경우
    public <T> T getSecondAs(Class<T> type) throws ClassCastException { return secondAs(type); }

    /// 두번째 값을 반환한다.
    /// @return 두번째 값을 감싼 Optional 객체
    public Optional<Object> tryGetSecond() { return trySecond(); }

    /// 두번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 [Optional#empty()]를 반환한다.
    /// @param type 반환할 값의 타입
    /// @return 두번째 값을 감싼 Optional 객체
    public <T> Optional<T> tryGetSecond(Class<T> type) { return trySecond(type); }

    /// 세번째 값을 반환한다.
    /// @return 세번째 값
    public Object third() { return get(2); }

    /// 세번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 `null`을 반환한다.
    /// @param type 반환할 값의 타입
    /// @return 세번째 값
    public <T> T third(Class<T> type) { return get(2, type); }

    /// 세번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 [ClassCastException]을 던진다.
    /// @param type 반환할 값의 타입
    /// @return 세번째 값
    ///
    public <T> T thirdAs(Class<T> type) throws ClassCastException { return getAs(2, type); }

    /// 세번째 값을 반환한다.
    /// @return 세번째 값을 감싼 Optional 객체
    public Optional<Object> tryThird() { return tryGet(2); }

    /// 세번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 [Optional#empty()]를 반환한다.
    /// @param type 반환할 값의 타입
    /// @return 세번째 값을 감싼 Optional 객체
    public <T> Optional<T> tryThird(Class<T> type) { return tryGet(2, type); }

    /// 세번째 값을 반환한다.
    /// @return 세번째 값
    public Object getThird() { return third(); }

    /// 세번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 `null`을 반환한다.
    /// @param type 반환할 값의 타입
    /// @return 세번째 값
    public <T> T getThird(Class<T> type) { return third(type); }

    /// 세번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 [ClassCastException]을 던진다.
    /// @param type 반환할 값의 타입
    /// @return 세번째 값
    /// @throws ClassCastException 자료형이 일치하지 않을 경우
    public <T> T getThirdAs(Class<T> type) throws ClassCastException { return thirdAs(type); }

    /// 세번째 값을 반환한다.
    /// @return 세번째 값을 감싼 Optional 객체
    public Optional<Object> tryGetThird() { return tryThird(); }

    /// 세번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 [Optional#empty()]를 반환한다.
    /// @param type 반환할 값의 타입
    /// @return 세번째 값을 감싼 Optional 객체
    public <T> Optional<T> tryGetThird(Class<T> type) { return tryThird(type); }

    /// 네번째 값을 반환한다.
    /// @return 네번째 값
    public Object fourth() { return get(3); }

    /// 네번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 `null`을 반환한다.
    /// @param type 반환할 값의 타입
    /// @return 네번째 값
    public <T> T fourth(Class<T> type) { return get(3, type); }

    /// 네번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 [ClassCastException]을 던진다.
    /// @param type 반환할 값의 타입
    /// @return 네번째 값
    /// @throws ClassCastException 자료형이 일치하지 않을 경우
    public <T> T fourthAs(Class<T> type) throws ClassCastException { return getAs(3, type); }

    /// 네번째 값을 반환한다.
    /// @return 네번째 값을 감싼 Optional 객체
    public Optional<Object> tryFourth() { return tryGet(3); }

    /// 네번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 [Optional#empty()]를 반환한다.
    /// @param type 반환할 값의 타입
    /// @return 네번째 값을 감싼 Optional 객체
    public <T> Optional<T> tryFourth(Class<T> type) { return tryGet(3, type); }

    /// 네번째 값을 반환한다.
    /// @return 네번째 값
    public Object getFourth() { return fourth(); }

    /// 네번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 `null`을 반환한다.
    /// @param type 반환할 값의 타입
    /// @return 네번째 값
    public <T> T getFourth(Class<T> type) { return fourth(type); }

    /// 네번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 [ClassCastException]을 던진다.
    /// @param type 반환할 값의 타입
    /// @return 네번째 값
    /// @throws ClassCastException 자료형이 일치하지 않을 경우
    public <T> T getFourthAs(Class<T> type) throws ClassCastException { return fourthAs(type); }

    /// 네번째 값을 반환한다.
    /// @return 네번째 값을 감싼 Optional 객체
    public Optional<Object> tryGetFourth() { return tryFourth(); }

    /// 네번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 [Optional#empty()]를 반환한다.
    /// @param type 반환할 값의 타입
    /// @return 네번째 값을 감싼 Optional 객체
    public <T> Optional<T> tryGetFourth(Class<T> type) { return tryFourth(type); }

    /// 다섯번째 값을 반환한다.
    /// @return 다섯번째 값
    public Object fifth() { return get(4); }

    /// 다섯번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 `null`을 반환한다.
    /// @param type 반환할 값의 타입
    /// @return 다섯번째 값
    public <T> T fifth(Class<T> type) { return get(4, type); }

    /// 다섯번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 [ClassCastException]을 던진다.
    /// @param type 반환할 값의 타입
    /// @return 다섯번째 값
    /// @throws ClassCastException 자료형이 일치하지 않을 경우
    public <T> T fifthAs(Class<T> type) throws ClassCastException { return getAs(4, type); }

    /// 다섯번째 값을 반환한다.
    /// @return 다섯번째 값을 감싼 Optional 객체
    public Optional<Object> tryFifth() { return tryGet(4); }

    /// 다섯번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 [Optional#empty()]를 반환한다.
    /// @param type 반환할 값의 타입
    /// @return 다섯번째 값을 감싼 Optional 객체
    public <T> Optional<T> tryFifth(Class<T> type) { return tryGet(4, type); }

    /// 다섯번째 값을 반환한다.
    /// @return 다섯번째 값
    public Object getFifth() { return fifth(); }

    /// 다섯번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 `null`을 반환한다.
    /// @param type 반환할 값의 타입
    /// @return 다섯번째 값
    public <T> T getFifth(Class<T> type) { return fifth(type); }

    /// 다섯번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 [ClassCastException]을 던진다.
    /// @param type 반환할 값의 타입
    /// @return 다섯번째 값
    /// @throws ClassCastException 자료형이 일치하지 않을 경우
    public <T> T getFifthAs(Class<T> type) throws ClassCastException { return fifthAs(type); }

    /// 다섯번째 값을 반환한다.
    /// @return 다섯번째 값을 감싼 Optional 객체
    public Optional<Object> tryGetFifth() { return tryFifth(); }

    /// 다섯번째 값을 반환한다.
    /// 자료형이 일치하지 않을 경우 [Optional#empty()]를 반환한다.
    /// @param type 반환할 값의 타입
    /// @return 다섯번째 값을 감싼 Optional 객체
    public <T> Optional<T> tryGetFifth(Class<T> type) { return tryFifth(type); }
}
