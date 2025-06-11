# Project Base

프로젝트 공통 컨벤션 구축용 인터페이스 및 클래스 패키지

## Java 버전

* Java 17 기준
  * Stream API, `record` 등
* Java 23 (javadoc only)
  * Markdown javadoc 지원

## 의존성 라이브러리 목록

* compileOnlyApi
  * `org.jetbrains:annotations` - `@NotNull` 선언
  * TODO: 최종 배포 전 의존 버전을 고려할 것
* implementation
  * `com.google.guava:guava` (아직 사용 안했지만 사용할 수도)
  * `org.apache.commons:commons-lang3`
  * `org.slf4j:slf4j-api`
  * `com.squareup:javapoet` (annotation-processors)
  * `com.google.auto.service:auto-service` (annotation-processors)
* compileOnly
  * `org.projectlombok:lombok` (javadoc 참조)

## 적용 방법

```kotlin
implementation("com.yidigun:project-base:0.0.1")
annotationProcessor("com.yidigun:project-base:0.0.1")
testAnnotationProcessor("com.yidigun:project-base:0.0.1") // 선택 사항
```
