package com.yidigun.base.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

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
/// Map<String, Object> map = PropertyMap.of(fluentObj);
/// Instant createDate = (Instant) map.get("createDate");
/// ```
///
/// ## 프로퍼티 접근자 판단 기준
///
/// * [ExportProperty] 어노테이션으로 명시적으로 프로퍼티로 선언된 메소드
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
///   0. [ExportProperty] 어노테이션이 붙은 메소드
///   1. Fluid API: 필드명과 동일한 이름(`T name()`)
///   2. `boolean isFlag()`
///   3. `T getName()`
/// * Setter
///   0. [ExportProperty] 어노테이션이 붙은 메소드
///   1. Fluid API: 필드명과 동일한 이름(`자신클래스 name(T value)` 또는 `void name(T value)`)
///   2. `void setName(T value)`
///
/// ```java
/// class FluentObj {
///     private boolean valid;
///
///     @ExportProperty("valid")
///     public String thisIsTheFirstPriorityValidMethod() { ... }      // 1순위
///     public boolean valid() { ... }      // 2순위
///     public boolean isValid() { ... }    // 3순위
///     public Boolean getValid() { ... }   // 4순위
///
///     @ExportProperty("valid")
///     public void thisTheMostPopularMethodToSetValid(String valid) { ... } // 1순위
///     public FluentObj valid(boolean valid) { ... } // 2순위
///     public void      valid(boolean valid) { ... } // 2순위
///     public void setValid(Boolean valid) { ... }   // 3순위
/// }
/// ```
///
/// 위 규칙에 따라 getter와 setter 메소드를 선택한 후 두 메소드의 자료형이 호환되지 않는 경우
/// (즉 getter가 `T1 name()`이고 setter가 `void name(T2 value)`와 같은 경우),
/// setter 메소드는 무시되고 read-only 프로퍼티로 간주된다.
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
/// ## [PropertyMap.AccessMethod]에 따른 성능 차이
///
/// 동적 메소드 호출을 위해서 3가지 접근 방법을 중 하나를 선택할 수 있으며
/// 기본값은 가장 성능이 좋은 [PropertyMap.AccessMethod#LAMBDA_META_FACTORY]이다.
///
/// 속도 비교의 차이는 메소드 호출의 오버헤드를 의미하는 것이며 프로퍼티를 식별하기 위한 초기화 작업은 모두 일정정도 필요하다.
///
/// * [PropertyMap.AccessMethod#REFLECTION]: Java Reflection API를 사용하여 프로퍼티에 접근합니다.
///   * 가장 느리지만, 초기화 오버헤드가 가장 적다.
///   * 느린 이유는 매 메소드 호출마다 권한 검사 등을 처리하기 때문이다.
/// * [PropertyMap.AccessMethod#METHOD_HANDLE]: Java MethodHandle API를 사용하여 프로퍼티에 접근합니다.
///   * Reflection보다 훨씬 빠른 것으로 알려져 있다.
///   * 권한 검사가 초기화시에만 이루어지므로 실행 속도가 빠르다.
/// * [PropertyMap.AccessMethod#LAMBDA_META_FACTORY]: Java Lambda MetaFactory를 사용하여 프로퍼티에 접근합니다.
///   * getter/setter에 대한 람다를 생성하는 오버헤드가 상당히 크다. 초기화 이후에 JIT 컴파일이 되면 매우 빠르게 실행된다.
///   * 최적의 상황에서는 거의 네이티브 수준인 것으로 알려져 있다.
///   * 단, [PropertyMap]은 메소드 인자와 결과값이 [Object]로 래핑되어 실행되므로 최적은 아니다.
///
/// @see Map
/// @see lombok.experimental.Accessors#fluent
public interface PropertyMap extends Map<String, Object> {

    /// 프로퍼티 접근자(getter/setter)에 대한 호출 방법
    public enum AccessMethod {
        /// Reflection API를 사용하여 프로퍼티에 접근
        REFLECTION,
        /// MethodHandle API를 사용하여 프로퍼티에 접근
        METHOD_HANDLE,
        /// Lambda MetaFactory를 사용하여 프로퍼티에 접근
        LAMBDA_META_FACTORY
    }

    /// 원본 객체의 참조를 반환
    /// @return adaptee 객체
    Object getAdaptee();

    /// 원본 객체의 참조를 반환
    /// @return adaptee 객체
    default Object getTarget() {
        return getAdaptee();
    }

    /// [PropertyMap] 객체를 생성한다.
    /// [PropertyMap.AccessMethod#LAMBDA_META_FACTORY]를 사용하여 프로퍼티에 접근한다.
    /// @param adaptee [Map]으로 변환할 객체
    /// @return [PropertyMap] 객체
    static PropertyMap of(Object adaptee) {
        return of(adaptee, AccessMethod.LAMBDA_META_FACTORY);
    }

    /// [PropertyMap] 객체를 생성한다.
    /// @param adaptee [Map]으로 변환할 객체
    /// @param method 프로퍼티 접근 방법
    /// @return [PropertyMap] 객체
    static PropertyMap of(Object adaptee, AccessMethod method) {
        return PropertyMapAdapter.of(adaptee, method);
    }
}
