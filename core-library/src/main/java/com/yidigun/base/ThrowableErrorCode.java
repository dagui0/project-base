package com.yidigun.base;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;

/// 예외로 부터 오류 코드를 생성하는 클래스.
/// 이 클래스는 예외가 ErrorCode를 구현하지 않는 경우에 사용됩니다.
@SuppressWarnings("LombokGetterMayBeUsed")
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
        return cause;
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
