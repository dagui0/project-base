package com.yidigun.base.fluent;

import com.yidigun.base.CheckEqualsAndHashCode;

/// 도메인 객체용 추상 클래스.
/// [CheckEqualsAndHashCode] 어노테이션을 사용하여 [Object#equals(Object)]와 [Object#hashCode()] 메소드의 재정의를 강제한다.
///
/// @param <K> 도메인 객체의 기본 키 타입
/// @see DomainObject
/// @see CheckEqualsAndHashCode
@CheckEqualsAndHashCode
public abstract class BaseDomainObject<K extends PrimaryKey> implements DomainObject<K> {
}
