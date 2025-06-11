package com.yidigun.base;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/// 오류 코드를 정의하기 위한 기본 인터페이스
///
/// ```java
/// public enum DbErrorCode implements ErrorCode {
///
///
/// }
/// ```
///
/// @see ApiError
public interface ErrorCode extends Serializable {

    /// 단순 오류 코드 생성 팩토리 메서드
    /// @param message 오류 메시지
    /// @return 오류 코드 객체
    static ErrorCode of(@NotNull String message) {
        return new AdHocErrorCode(message);
    }

    /// 단순 오류 코드 생성 팩토리 메서드
    /// @param code 오류 코드
    /// @param message 오류 메시지
    /// @return 오류 코드 객체
    static ErrorCode of(@NotNull String code, String message) {
        return new AdHocErrorCode(code, message);
    }

    /// 단순 오류 코드 생성 팩토리 메서드
    /// @param cause 오류를 발생시킨 원인
    /// @return 오류 코드 객체
    static ErrorCode of(@NotNull Throwable cause) {
        return (cause instanceof ErrorCode errorCode)?
                        errorCode : new ThrowableErrorCode(cause);
    }

    /// 알 수 없는 오류
    ErrorCode UNKNOWN = new AdHocErrorCode();

    /// 항목을 찾을 수 없는 오류
    // TODO: 메시지 번들 적용 방안
    ErrorCode NOT_FOUND = new AdHocErrorCode("NOT_FOUND", "Requested resource not found.");

    /// 오류 코드
    /// @return 오류 코드
    String code();

    /// 오류 코드
    /// @return 오류 코드
    default String getCode() {
        return code();
    }

    /// 오류 메시지
    /// @return 오류 메시지
    String message();

    /// 오류 메시지
    /// @return 오류 메시지
    default String getMessage() {
        return message();
    }

    /// 해당 오류 코드가 성공으로 간주될지 여부
    /// @return 성공으로 간주되어야 할 경우 true, 그렇지 않으면 false
    boolean success();

    /// 해당 오류 코드가 성공으로 간주될지 여부
    /// @return 성공으로 간주되어야 할 경우 true, 그렇지 않으면 false
    default boolean isSuccess() {
        return success();
    }
}

/// 단순 오류 코드 클래스.
/// 이 클래스는 오류 코드와 메시지를 단순히 저장하는 용도로 사용됩니다.
final class AdHocErrorCode implements ErrorCode {

    @Serial
    private static final long serialVersionUID = 65540329464005665L;

    private final String code;
    private final String message;

    public AdHocErrorCode() {
        this.code = "UNKNOWN_ERROR";
        this.message = null;
    }

    public AdHocErrorCode(String message) {
        this.code = "UNKNOWN_ERROR";
        this.message = message;
    }

    public AdHocErrorCode(@NotNull String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return (message != null) ? message : "An unknown error occurred.";
    }

    @Override
    public boolean success() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdHocErrorCode that)) return false;
        return code.equals(that.code()) &&
                Objects.requireNonNull(message).equals(that.message());
    }

    @Override
    public int hashCode() {
        int result = code.hashCode();
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AdHocErrorCode{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}

/// 예외로 부터 오류 코드를 생성하는 클래스.
/// 이 클래스는 예외가 ErrorCode를 구현하지 않는 경우에 사용됩니다.
final class ThrowableErrorCode implements ErrorCode {

    @Serial
    private static final long serialVersionUID = -7513959506284503465L;

    private final Throwable cause;

    public ThrowableErrorCode(@NotNull Throwable cause) {
        this.cause = cause;
    }

    @Override
    public String code() {
        return cause.getClass().getSimpleName();
    }

    public Throwable cause() {
        return cause;
    }

    public Throwable getCause() {
        return cause();
    }

    @Override
    public String message() {
        return cause.getMessage();
    }

    @Override
    public boolean success() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ThrowableErrorCode that)) return false;
        return cause.equals(that.cause());
    }

    @Override
    public int hashCode() {
        return cause.hashCode();
    }

    @Override
    public String toString() {
        return "ThrowableErrorCode{" +
                "cause=" + cause +
                '}';
    }
}
