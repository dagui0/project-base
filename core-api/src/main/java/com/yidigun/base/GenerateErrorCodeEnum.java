package com.yidigun.base;

import java.lang.annotation.*;

/// 오류 코드 열거 타입을 생성하기 위한 어노테이션.
///
/// CSV 파일에서 자동으로 열거 타입을 생성한다.
///
/// ## 사용 예시
///
/// 패키지에 지정하는 경우
///
/// ```java
/// @GenerateErrorCodeEnum("SQLStateClass", from="sql_error_codes.csv", implement=SQLStateClassIf.class)
/// @GenerateErrorCodeEnum("HttpStatus", from="http_status.csv", toPackage="com.yidigun.base.utils")
/// package com.yidigun.base;
/// ```
///
/// * `public enum com.yidigun.base.SQLStateClass implements SQLStateClassIf { ... }`
/// * `public enum com.yidigun.base.utils.HttpStatus implements ErrorCode { ... }`
///
/// 인터페이스에 지정하는 경우
///
/// ```java
/// package com.yidigun.base.sql;
///
/// @GenerateErrorCodeEnum("DbErrorCode", from="db_error_codes.csv")
/// public interface DbErrorCode extends ErrorCode {
///
///    // default, static 메서드 등을 정의할 수 있다.
/// }
/// ```
///
/// * `public enum com.yidigun.base.sql.DbErrorCode implements ErrorCode { ... }`
///
/// ## 인터페이스 상속을 통해서 확장하는 방법
///
/// ```java
/// @GenerateErrorCodeEnum("SQLStateClass",
///                        from="sql_error_codes.csv",
///                        dataFields="name,code,message,standard")
/// public interface SQLStateClassIf extends ErrorCode {
///
///     String standard();
///
///     default String getStandard() {
///         return standard();
///     }
///
///     default boolean timeout() {
///         return code().endsWith("~");
///     }
///     default boolean isTimeout() {
///         return timeout();
///     }
/// }
/// ```
///
/// `default`가 아닌 추가된 getter 가상 메서드는 필드 추가로 간주된다.
/// 이렇게 추가 필드를 지정할 경우 [#dataFields()]을 반드시 지정해야 한다.
///
/// ```csv
/// # SQLState 클래스 정의
/// UNKNOWN,__,Unknown SQL State Class (maybe DBMS specific),SQLStateClass
/// CONNECTION_TIMEOUT,C~,Connection Timeout,SQLStateClass
/// QUERY_TIMEOUT,Q~,Query Timeout,SQLStateClass
/// SUCCESS,00,Success,SQL99
/// INTERNAL_ERROR,XX,Internal Error,PostgreSQL
/// ```
///
/// 인터페이스의 필드와 CSV파일의 필드가 일치하지 않는 경우에는 처리가 중단된다.
///
/// ## 필드명 및 CSV 파일 형식
///
/// * 기본 파일 형식은 `name,code,message` 형식으로 정의되어 있다.
///   이 3가지 필드는 필수 필드([ErrorCode]의 요건)이므로 반드시 제공되어야 한다.
/// * 추가 필드를 추가할 경우 필드명은 Java 네이밍 규칙에 맞는 문자열로 되어 있어야 한다.
///   `^[a-zA-Z_$][a-zA-Z0-9_$]*$` 형식이어야 하며, 대소문자를 구분한다.
/// * 필드명 정의 구분자는 `\s*,\s*`를 사용한다.
/// * 데이터 파일의 구분자는 `\s*,\s*`를 사용하고 인용부호(`"` 또는 `'`)는 지원하지 않는다.
///   TODO: 추후 구분자 변경, 인용부호 등 추가 기능 고려
///
/// TODO: 복수 파일 지정 기능 추가 고려
///
/// ### 중복 데이터 발생시
///
/// 제공되는 데이터의 name과 code 필드의 값들은 반드시 unique 해야 한다.
/// 만약 중복되는 데이터가 발생할 경우, 처리가 중단된다.
///
/// ## 데이터 타입 지원
///
/// TODO: 일단은 [String] 타입만 지원하고, 추후 기본 자료형 지원 고려
///
/// ## 정적 팩토리 메소드를 추가하는 방법
///
/// 기본적으로 생성된 열거타입에는 `public static E of(String)` 팩토리 메소드가 추가된다.
///
/// ```java
/// public enum SQLStateClass implements SQLStateClassIf {
///     static SQLStateClass of(String code) {
///         ...
///     }
/// }
///
/// SQLStateClass clazz = SQLStateClass.of("__");
/// ```
///
/// 만약 인터페이스 확장을 통해서 `static String codeOf(T, ...)` 메소드들을 정의하면,
/// 이 정적 메소드들을 이용한 `public static E of(T, ...)` 팩토리 메소드들이 생성된다.
///
/// ```java
/// @GenerateErrorCodeEnum("SQLStateClass", from="sql_error_codes.csv")
/// public interface SQLStateClassIf extends ErrorCode {
///     static String codeOf(SQLException e) {
///         ...
///         return code;
///     }
/// }
///
/// public enum SQLStateClass implements SQLStateClassIf {
///     public static SQLStateClass of(SQLException e) {
///         return of(codeOf(e));
///     }
/// }
///
/// try {
///     ...
/// }
/// catch (SQLException e) {
///     SQLStateClass clazz = SQLStateClass.of(e);
///     ...
/// }
/// ```
///
@Target({ElementType.TYPE, ElementType.PACKAGE})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(GenerateErrorCodeEnumTasks.class)
public @interface GenerateErrorCodeEnum {

    /// 생성될 오류 코드 열거 타입의 이름.
    String value();

    /// 생성될 오류 코드 열거 타입의 패키지
    /// 지정되지 않은 경우 어노테이션이 지정된 패키지(또는 클래스의 패키지)가 사용된다.
    String toPackage() default "";

    /// 데이터 소스 파일 경로.
    /// CSV 형식이어야 하며, 클래스 경로 기준의 상대 경로를 지정한다.
    /// 소스 파일이 지정되지 않은 경우 처리는 중단된다.
    String from();

    /// CSV 파일의 사용할 필드 순서와 이름을 정의
    /// 이 필드 목록이 최종 인터페이스와 일치하지 않는 경우 오류가 발생할 수 있다.
    String dataFields() default "name,code,message";

    /// 생성된 오류코드 열거타입이 구현할 인터페이스를 지정한다.
    /// 만약 어노테이션이 클래스([ElementType#TYPE])에 적용되었고,
    /// [ErrorCode]를 구현한 인터페이스라면 해당 클래스를 먼저 시도한다.
    /// 인터페이스를 찾을 수 없는 경우 기본값은 [ErrorCode]이다.
    Class<? extends ErrorCode> implement() default ErrorCode.class;
}
