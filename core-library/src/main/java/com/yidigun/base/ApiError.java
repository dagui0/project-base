package com.yidigun.base;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;

/// 디버그 모드와 프로덕션 모드를 구분하여 메시지 출력을 다르게 하는 예외 클래스.
///
public class ApiError extends RuntimeException implements Serializable, ErrorCode {

    @Serial
    private static final long serialVersionUID = -3937138005793701689L;

    // TODO: 메시지 번들 적용 방안
    private static final String DEFAULT_ERROR_MESSAGE = "An error occurred. Please contact support.";

    private final ErrorCode errorCode;

    /// 알 수 없는 오류 생성
    public ApiError() {
        super();
        errorCode = ErrorCode.UNKNOWN;
    }

    /// 메시지를 이용하여 단순 오류 생성
    /// @param message 오류 메시지
    public ApiError(@NotNull String message) {
        super(message);
        errorCode = ErrorCode.of(message);
    }

    /// 메시지와 오류 코드를 이용하여 오류 생성
    /// @param message 오류 메시지
    /// @param errorCode 오류 코드
    public ApiError(@NotNull String message, @NotNull ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /// 오류 코드를 이용하여 오류 생성
    /// @param errorCode 오류 코드
    public ApiError(@NotNull ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    /// 메시지와 원인 예외를 이용하여 오류 생성
    /// @param message 오류 메시지
    /// @param cause 원인 예외
    public ApiError(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
        errorCode = ErrorCode.of(cause);
    }

    /// 원인 예외를 이용하여 오류 생성
    /// @param cause 원인 예외
    public ApiError(@NotNull Throwable cause) {
        super(cause);
        errorCode = ErrorCode.of(cause);
    }

    /// 메시지, 원인 예외, 예외 전파 설정, 쓰기 가능 여부를 이용하여 오류 생성
    /// @param message 오류 메시지
    /// @param cause 원인 예외
    /// @param enableSuppression 예외 전파 설정
    /// @param writableStackTrace 쓰기 가능 여부
    public ApiError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        errorCode = ErrorCode.of(cause);
    }

    @Override
    public String code() {
        return errorCode.code();
    }

    @Override
    public String message() {
        String message = (isDebugMode())? super.getMessage(): DEFAULT_ERROR_MESSAGE;
        return message.isEmpty()? errorCode.message() : message;
    }

    @Override
    public String getMessage() {
        return message();
    }

    @Override
    public boolean success() {
        return false;
    }

    @Override
    public String toString() {
        // TODO: JSON 형태로 포매팅
        if (isDebugMode()) {
            return super.toString();
        } else {
            return "ApiError: An error occurred. Please contact support.";
        }
    }

    /// 디버그 모드인지 확인하는 메소드.
    /// @return true: 디버그 모드, false: 프로덕션 모드
    private boolean isDebugMode() {
        // TODO: 범용적으로 활용 가능한 방안 연구
        String debugMode = System.getenv("DEBUG_MODE");
        return "true".equalsIgnoreCase(debugMode) || "1".equals(debugMode);
    }
}
