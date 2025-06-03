package com.yidigun.base.utils;

import java.io.Serial;

/// [PropertyMapAdapter]에서 Reflection API를 사용하여 값을 액세스 할때 발생하는 예외
/// @see PropertyMapAdapter
public class PropertyMapException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 2138108214498741023L;

    /// 생성자
    /// @param message 예외 메시지
    public PropertyMapException(String message) {
        super(message);
    }

    /// 생성자
    /// @param message 예외 메시지
    /// @param cause 원인 예외
    public PropertyMapException(String message, Throwable cause) {
        super(message, cause);
    }

    /// 생성자
    /// @param cause 원인 예외
    public PropertyMapException(Throwable cause) {
        super(cause);
    }

    /// 생성자
    /// @param message 예외 메시지
    /// @param cause 원인 예외
    /// @param enableSuppression 예외 억제 여부
    /// @param writableStackTrace 스택 트레이스 작성
    public PropertyMapException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
