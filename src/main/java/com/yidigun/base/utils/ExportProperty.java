package com.yidigun.base.utils;

import java.lang.annotation.*;

/// [PropertyMapAdapter]에서 Fluent 스타일 메소드를 public 프로퍼티로 간주하도록 지시한다.
///
/// 메소드명과 동일한 이름의 프로퍼티를 만든다.
/// 기본적으로 getter메소드에 지정하는 것을 원칙으로 하며,
/// getter가 지정된 경우 동일한 이름을 가진 setter 메소드가 존재할 경우 자동으로 선택한다.
///
/// write-only 프로퍼티라면 setter 메소드에 지정하는 것도 가능하다.
///
/// ```java
/// enum Sex { MALE, FEMALE }
/// enum State { ACTIVE, INACTIVE }
/// public class Example {
///     private String name;
///     private Sex sex;
///     private State state;
///
///     public String name() { return name; }
///     public Example name(String name) { this.name = name; return this; }
///     public Sex sex() { return sex; }
///     public Example sex(Sex sex) { this.sex = sex; return this; }
///
///     // read-only
///     @ExportProperty
///     public String nameLowerCase() { return name.toLowerCase(); }
///
///     // read-write
///     @ExportProperty
///     public String sexString() { return sex.toString(); }
///     public Example sexString(String sexString) {
///         this.sex = Sex.valueOf(sexString.toUpperCase());
///         return this;
///     }
///
///     // write-only
///     @ExportProperty
///     public Example activated(boolean activated) {
///         this.state = activated ? State.ACTIVE : State.INACTIVE;
///         return this;
///     }
/// }
/// ```
///
/// ### 클래스 상속 또는 인터페이스 구현
///
/// 원래 메소드에 붙는 어노테이션은 상속된 클래스에서 오버라이드한 메소드에 적용되지 않는다.
/// 하지만 [PropertyMapAdapter]는 [ExportProperty]에 대해서
/// 오버라이드한 메소드의 원래 정의된 지점(상위 클래스 또는 인터페이스)를 모두 추적해서 적용 여부를 확인한다.
///
/// @see PropertyMapAdapter
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExportProperty {
    /// 프로퍼티 이름, 지정하지 않은 경우 메소드를 사용.
    /// @return 프로퍼티 이름
    String value() default "";
}
