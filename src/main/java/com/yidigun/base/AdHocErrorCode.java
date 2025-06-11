package com.yidigun.base;

import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.util.Objects;

/// 단순 오류 코드 클래스.
/// 이 클래스는 오류 코드와 메시지를 단순히 저장하는 용도로 사용됩니다.
public final class AdHocErrorCode implements ErrorCode {

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
