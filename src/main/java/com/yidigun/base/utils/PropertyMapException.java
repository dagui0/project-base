package com.yidigun.base.utils;

import java.io.Serial;

/// [PropertyMapAdapter]에서 Reflection API를 사용하여 값을 액세스 할때 발생하는 예외
/// @see PropertyMapAdapter
public class PropertyMapException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 2138108214498741023L;

    public PropertyMapException(String message) {
        super(message);
    }

    public PropertyMapException(String message, Throwable cause) {
        super(message, cause);
    }

    public PropertyMapException(Throwable cause) {
        super(cause);
    }

    public PropertyMapException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
