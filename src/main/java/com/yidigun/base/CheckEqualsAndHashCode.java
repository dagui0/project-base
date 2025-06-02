package com.yidigun.base;

import java.lang.annotation.*;

/// 클래스가 [Object#equals(Object)] 와 [Object#hashCode()]를
/// 재정의 하지 않은 경우 컴파일러 경고를 발생시키도록 하는 어노테이션.
///
/// ## 경고 억제 방법
///
/// 특정 자식 클래스에서 컴파일러 경고를 억제하려면, [SuppressWarnings] 어노테이션을 사용할 수 있다:
///
/// ```java
/// // project/base/BaseClass.java
/// package project.base;
///
/// @CheckEqualsAndHashCode
/// public abstract class BaseClase {}
///
/// // project/domain/DerivedClass.java
/// package project.domain;
///
/// import com.yidigun.base.CheckEqualsAndHashCode;
/// import project.base.BaseClass;
///
/// @SuppressWarnings("checkEqualsAndHashCode")
/// public class DerivedClass extends BaseClase {}
/// ```
///
/// 특정 패키지 전체에서 경고를 억제하려면 `package-info.java` 파일에 [SuppressWarnings]을 추가할 수도 있다:
///
/// ```java
/// // project/dto/package-info.java
/// // project.dto 패키지 전체에 경고 억제
/// @SuppressWarnings("checkEqualsAndHashCode")
/// package project.dto;
/// ```
///
/// ## 빌드 활성화 방법
///
/// ### javac 예시:
/// ```shell
/// javac -processor com.yidigun.base.processors.CheckEqualsAndHashCodeProcessor \
///     -processorpath project-base.jar \
///     ...
/// ```
///
/// ### gradle 예시:
///
/// `build.gradle`:
///
/// ```groovy
/// dependencies {
///     implementation 'com.yidigun:project-base:1.0.0'
///     annotationProcessor 'com.yidigun:project-base:1.0.0'
/// }
/// ```
///
/// `build.gradle.kts`:
///
/// ```kotlin
/// dependencies {
///     implementation("com.yidigun:project-base:1.0.0")
///     annotationProcessor("com.yidigun:project-base:1.0.0")
/// }
/// ```
///
/// ### maven 예시:
/// ```xml
/// <build>
///     <plugins>
///         <plugin>
///             <groupId>org.apache.maven.plugins</groupId>
///             <artifactId>maven-compiler-plugin</artifactId>
///             <version>3.14.0</version>
///             <configuration>
///                 <source>23</source>
///                 <target>23</target>
///                 <annotationProcessorPaths>
///                     <path>
///                         <groupId>com.yidigun</groupId>
///                         <artifactId>project-base</artifactId>
///                         <version>1.0.0</version>
///                     </path>
///                 </annotationProcessorPaths>
///                 ...
///             </configuration>
///         </plugin>
///     </plugins>
/// </build>
/// <dependencies>
///     ...
///     <dependency>
///         <groupId>com.yidigun</groupId>
///         <artifactId>project-base</artifactId>
///         <version>1.0.0</version>
///         <scope>compile</scope>
///         <optional>true</optional>
///     </dependency>
///     ...
/// </dependencies>
/// ```
///
/// @see SuppressWarnings
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@Inherited
public @interface CheckEqualsAndHashCode {
}
