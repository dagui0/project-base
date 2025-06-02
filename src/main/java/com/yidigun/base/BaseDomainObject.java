package com.yidigun.base;

/// 도메인 객체용 추상 클래스.
/// [CheckEqualsAndHashCode] 어노테이션을 사용하여 [Object#equals(Object)]와 [Object#hashCode()] 메소드의 재정의를 강제한다.
///
/// @see DomainObject
/// @see CheckEqualsAndHashCode
@CheckEqualsAndHashCode
public abstract class BaseDomainObject<K extends PrimaryKey> implements DomainObject<K> {
}
