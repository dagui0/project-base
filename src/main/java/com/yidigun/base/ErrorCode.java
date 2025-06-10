package com.yidigun.base;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/// 오류 코드를 정의하기 위한 기본 인터페이스
///
/// @see com.yidigun.base.examples.HttpStatus
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

    /// 예외로 부터 오류 코드를 생성하는 클래스.
    /// 이 클래스는 예외가 ErrorCode를 구현하지 않는 경우에 사용됩니다.
    class ThrowableErrorCode implements ErrorCode {

        private final Throwable cause;

        private ThrowableErrorCode(@NotNull Throwable cause) {
            this.cause = cause;
        }

        @Override
        public String code() {
            return cause.getClass().getSimpleName();
        }

        @Override
        public String message() {
            return cause.getMessage();
        }

        @Override
        public boolean success() {
            return false;
        }
    }

    /// 단순 오류 코드 클래스.
    /// 이 클래스는 오류 코드와 메시지를 단순히 저장하는 용도로 사용됩니다.
    class AdHocErrorCode implements ErrorCode {

        private final String code;
        private final String message;

        private AdHocErrorCode() {
            this.code = "UNKNOWN_ERROR";
            this.message = null;
        }

        private AdHocErrorCode(String message) {
            this.code = "UNKNOWN_ERROR";
            this.message = message;
        }

        private AdHocErrorCode(@NotNull String code, String message) {
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
    }
}
