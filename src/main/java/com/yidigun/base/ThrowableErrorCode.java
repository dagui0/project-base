package com.yidigun.base;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;

/// 예외로 부터 오류 코드를 생성하는 클래스.
/// 이 클래스는 예외가 ErrorCode를 구현하지 않는 경우에 사용됩니다.
class ThrowableErrorCode implements ErrorCode {

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

    @Override
    public String message() {
        return cause.getMessage();
    }

    @Override
    public boolean success() {
        return false;
    }
}
