/// 프로젝트 공통 컨벤션 구축용 인터페이스 및 클래스 (Fluent API 스타일).
///
/// 이 패키지는 [com.yidigun.base.beans] 패키지와 동일한 기능을 제공하지만,
/// 프로젝트의 기본 객체 컨벤션을 Fluent API 스타일로 설정한 경우 사용할 수 있다.
///
/// ## Fluent API 스타일 객체 설계
///
/// ```java
/// class FluentObj {
///
///     private String name;
///     private int age;
///     private String address;
///
///     public String name() { return name; }
///     public FluentObj name(String name) {
///         this.name = name;
///         return this;
///     }
///
///     public int age() { return age; }
///     public FluentObj age(int age) {
///         this.age = age;
///         return this;
///     }
///
///     public String address() { return address; }
///     public FluentObj address(String address) {
///         this.address = address;
///         return this;
///     }
/// }
///
/// FluentObj obj = new FluentObj();
/// obj.name("이름").age(20).address("주소");
/// ```
///
/// ## Lombok 설정
///
/// 객체들의 getter/setter 메소드를 Fluent API 스타일로 생성하기 위해서
/// 다음과 같이 Lombok 설정을 할 수 있다.
///
/// * `${projectDir}/lombok.config` 파일에 설정할 경우 프로젝트 전체의 기본 설정이 된다.
///   ```properties
///   lombok.accessors.fluent = true
///   ```
/// * `lombok.config`를 소스 패키지 디렉토리에 위치시킨 경우 해당 패키지 이하의 모든 패키지들의 기본 설정이 된다.
///   * Lombok은 컴파일시에 적용되므로 `lombok.config` 파일을 최종 패키지에 포함시킬 필요는 없다.
///     따라서 `resources` 디렉토리에 위치시킬 필요 없으며 `src`에 위치시키면 된다.
/// * 클래스 단위로 `@Accessors(fluent = true)` 어노테이션을 사용하여 설정할 수 있다.
///   * [lombok.Getter]와 달리 [lombok.experimental.Accessors]는 클래스에만 붙일 수 있다.
///   * [lombok.experimental.Accessors]는 아직 시험 단계인 것으로 보임 (1.18.38 기준)
///
/// ## [com.yidigun.base.utils.PropertyMapAdapter]
///
/// 프로젝트 컨벤션을 Fluent API 스타일로 설정한 경우,
/// 특정한 상황에서 JavaBeans 스타일의 프로퍼티 접근자가 필요하면
/// [com.yidigun.base.utils.PropertyMapAdapter]를 사용하여 [java.util.Map] 인터페이스로 변환할 수 있다.
/// (JavaBeans 스타일의 프로퍼티가 필요한 라이브러리의 경우 대체로 [java.util.Map] 인터페이스를 지원한다.)
///
/// ```java
/// FluentObj fluentObj = new FluentObj();
/// fluentObj.name("이름");
///
/// Map<String, Object> fluentObjMap = PropertyMapAdapter.of(fluentObj);
/// fluentObjMap.put("name", "새 이름");
///
/// System.out.println(fluentObjMap.get("name"));   // "새 이름"
/// System.out.println(fluentObj.name());           // "새 이름"
/// ```
///
/// @see com.yidigun.base.utils.PropertyMapAdapter
package com.yidigun.base.fluent;
