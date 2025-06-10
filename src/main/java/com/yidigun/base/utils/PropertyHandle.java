package com.yidigun.base.utils;

/// 프로퍼티 접근자 메소드 정보를 담고 있는 DTO 인터페이스.
///
/// 실제 접근자 메소드 실행 또한 담당하며, [PropertyMap.AccessMethod]에 따라 별도 구현체가 있다.
///
/// @see PropertyMap
interface PropertyHandle {

    /// 프로퍼티 이름을 반환합니다.
    /// @return 프로퍼티 이름
    String name();

    /// 프로퍼티 값이 주어진 값과 일치하는지 확인합니다.
    /// @param target 프로퍼티가 속한 객체
    /// @param value 비교할 값
    /// @return 주어진 값이 프로퍼티 값과 일치하면 true, 그렇지 않으면 false
    boolean containsValue(Object target, Object value);

    /// 프로퍼티 값을 반환합니다.
    /// @param target 프로퍼티가 속한 객체
    /// @return 프로퍼티 값
    Object getValue(Object target);

    /// 프로퍼티 값을 설정하고, 이전 값을 반환합니다.
    /// @param target 프로퍼티가 속한 객체
    /// @param value 설정할 값
    /// @return 이전 프로퍼티 값
    Object setValue(Object target, Object value);
}
