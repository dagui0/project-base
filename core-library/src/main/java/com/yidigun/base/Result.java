package com.yidigun.base;

import org.jetbrains.annotations.NotNull;

import java.io.NotSerializableException;
import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/// 서비스 메소드 또는 API 결과값으로 사용할 수 있는 DTO 클래스.
///
/// 이 객체는 [Serializable]을 구현하여 직렬화가 가능하지만,
/// 저장된 결과 값과 부가정보 데이터의 타입이 직렬화 가능하지 않은 경우
/// [NotSerializableException]이 발생할 수 있다.
///
/// @param <T> 성공시 반환되는 값의 타입
@SuppressWarnings({"LombokGetterMayBeUsed", "serial"})
public final class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 3296269115794774158L;

    /// 처리 결과값
    /// 성공인 경우라도 `null`일 수 있다.
    private final T value;

    /// 처리 중 발생한 오류
    /// 성공인 경우 `null`이 된다.
    private final ApiError error;

    /// API응답에 추가할 부가 정보 [Map].
    /// 이 필드는 선택적으로 사용되며, 필요에 따라 추가 정보를 담을 수 있다.
    private Map<String, Object> additionalData;

    private Result(T value, ApiError error) {
        this.value = value;
        this.error = error;
    }

    /// 단순히 처리가 성공했다는 정보만을 전달하는 결과 객체를 생성
    ///
    /// ```java
    /// Result<Void> result = someService.updateValues(values);
    /// ```
    ///
    /// @return 성공 결과 객체
    /// @param <T> 반환되는 값의 타입
    public static <T> Result<T> of() {
        return new Result<>(null, null);
    }

    /// 결과값을 포함한 성공 결과 객체를 생성
    /// @param value 성공시 반환되는 값
    /// @return 성공 결과 객체
    /// @param <T> 반환되는 값의 타입
    public static <T> Result<T> of(@NotNull T value) {
        return new Result<>(value, null);
    }

    /// 결과값이 존재하면 성공, 존재하지 않으면 실패 결과 객체를 생성
    /// @param value 성공시 반환되는 값
    /// @return 성공 결과 객체 또는 실패 결과 객체
    /// @param <T> 반환되는 값의 타입
    public static <T> Result<T> ofNullable(T value) {
        return (value != null)?
                of(value):
                failure(ErrorCode.NOT_FOUND);
    }

    /// 단순 실패 결과 객체를 생성
    /// @return 실패 결과 객체
    /// @param <T> 반환되는 값의 타입
    public static <T> Result<T> failure() {
        return failure(new ApiError(ErrorCode.UNKNOWN));
    }

    /// 실패 결과 객체를 생성
    /// @param message 오류 메시지
    /// @return 실패 결과 객체
    /// @param <T> 반환되는 값의 타입
    public static <T> Result<T> failure(@NotNull String message) {
        return failure(new ApiError(message));
    }

    /// 실패 결과 객체를 생성
    /// @param message 오류 메시지
    /// @param cause 예외 원인
    /// @return 실패 결과 객체
    /// @param <T> 반환되는 값의 타입
    public static <T> Result<T> failure(@NotNull String message, @NotNull Throwable cause) {
        return failure(new ApiError(message, cause));
    }

    /// 실패 결과 객체를 생성
    /// @param message 오류 메시지
    /// @param errorCode 오류 코드
    /// @return 실패 결과 객체
    /// @param <T> 반환되는 값의 타입
    public static <T> Result<T> failure(@NotNull String message, @NotNull ErrorCode errorCode) {
        return failure(new ApiError(message, errorCode));
    }

    /// 실패 결과 객체를 생성
    /// @param error 오류 코드
    /// @return 실패 결과 객체
    /// @param <T> 반환되는 값의 타입
    public static <T> Result<T> failure(@NotNull ErrorCode error) {
        return failure((error instanceof ApiError apiError)? apiError: new ApiError(error));
    }

    /// 실패 결과 객체를 생성
    /// @param error 예외 원인
    /// @return 실패 결과 객체
    /// @param <T> 반환되는 값의 타입
    public static <T> Result<T> failure(@NotNull Throwable error) {
        return failure((error instanceof ApiError apiError)? apiError: new ApiError(error));
    }

    /// 실패 결과 객체를 생성
    /// @param error ApiError 객체
    /// @return 실패 결과 객체
    /// @param <T> 반환되는 값의 타입
    public static <T> Result<T> failure(@NotNull ApiError error) {
        return new Result<>(null, error);
    }

    /// 성공 여부
    /// @return 성공 여부
    public boolean success() { return (error == null); }

    /// 성공 여부
    /// @return 성공 여부
    public boolean isSuccess() { return success(); }

    /// 처리 결과값. 성공인 경우도 `null`일 수 있다.
    /// @return 성공시 반환되는 값
    public T value() { return value; }

    /// 처리 결과값. 성공인 경우도 `null`일 수 있다.
    /// @return 성공시 반환되는 값
    public T getValue() { return value; }

    /// 처리 중 발생한 오류. 성공인 경우 `null`이 된다.
    /// @return 실패시 발생한 오류
    public ApiError error() { return error; }

    /// 처리 중 발생한 오류. 성공인 경우 `null`이 된다.
    /// @return 실패시 발생한 오류
    public ApiError getError() { return error; }

    /// API 응답에 추가할 부가 정보 [Map].
    /// 부가정보가 추가되지 않았다면 `null`이 될 수 있다.
    /// @return 부가 정보 [Map]
    public Map<String, Object> additionalData() {
        return additionalData;
    }

    /// API 응답에 추가할 부가 정보 [Map].
    /// 부가정보가 추가되지 않았다면 `null`이 될 수 있다.
    /// @return 부가 정보 [Map]
    public Map<String, Object> getAdditionalData() {
        return additionalData;
    }

    /// API 응답에 포함할 부가 정보를 조회한다.
    /// @param key 부가 정보의 키
    /// @return 부가정보 또는 `null`
    public Object getAdditionalData(String key) {
        return (additionalData == null)? null : additionalData.get(key);
    }

    /// API 응답에 포함할 부가 정보를 조회한다.
    /// 만약 지정한 키의 데이터가 타입과 일치하지 않을 경우 `null`을 반환한다.
    /// @param key 부가 정보의 키
    /// @param type 부가 정보의 타입
    /// @return 부가정보 또는 `null`
    public <R> R getAdditionalData(String key, Class<R> type) {
        Object value = getAdditionalData(key);
        return (type.isInstance(value))? type.cast(value): null;
    }

    /// API 응답에 포함할 부가 정보를 조회한다.
    /// 만약 지정한 키의 데이터가 타입과 일치하지 않을 경우 [ClassCastException]을 던진다.
    /// @param key 부가 정보의 키
    /// @param type 부가 정보의 타입
    /// @return 부가정보 데이터
    /// @throws ClassCastException 지정한 키의 데이터가 타입과 일치하지 않을 경우
    public <R> R getAdditionalDataAs(String key, Class<R> type) throws ClassCastException {
        Object value = getAdditionalData(key);
        if (!type.isInstance(value))
            throw new ClassCastException("Value for key '" + key + "' is not of type " + type.getName());
        return type.cast(value);
    }

    /// API 응답에 포함할 부가 정보를 조회한다.
    /// @param key 부가 정보의 키
    /// @return 부가정보를 감싼 [Optional] 객체
    public Optional<Object> tryGetAdditionalData(String key) {
        return Optional.ofNullable(getAdditionalData(key));
    }

    /// API 응답에 포함할 부가 정보를 조회한다.
    /// 만약 지정한 키의 데이터가 타입과 일치하지 않을 경우 빈 [Optional]을 반환한다.
    /// @param key 부가 정보의 키
    /// @param type 부가 정보의 타입
    /// @return 부가정보를 감싼 [Optional] 객체
    public <R> Optional<R> tryGetAdditionalData(String key, Class<R> type) {
        return Optional.ofNullable(getAdditionalData(key, type));
    }

    /// API 응답에 포함할 부가 정보를 추가한다.
    /// @param key 부가 정보의 키
    /// @param value 부가 정보의 값
    /// @return 현재 객체
    public Result<T> addAdditionalData(String key, Object value) {
        if (additionalData == null) {
            additionalData = new TreeMap<>();
        }
        additionalData.put(key, value);
        return this;
    }

    /// API 응답에 포함할 부가 정보를 삭제한다.
    /// @param key 삭제할 부가 정보의 키
    /// @return 현재 객체
    public Result<T> removeAdditionalData(String key) {
        if (additionalData != null) {
            additionalData.remove(key);
        }
        return this;
    }

    @Override
    public String toString() {
        // TODO: JSON 형식으로 포매팅
        return "Result{" +
                "value=" + value +
                ", error=" + error +
                ", additionalData=" + additionalData +
                '}';
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (error != null ? error.hashCode() : 0);
        result = 31 * result + (additionalData != null ? additionalData.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Result<?> result = (Result<?>) obj;
        return Objects.equals(value, result.value) &&
            Objects.equals(error, result.error) &&
            Objects.equals(additionalData, result.additionalData);
    }

    //
    // Stream compatibility methods
    //

    /// 성공인 경우 결과값을 포함하는 [Stream]을 반환한다.
    /// @return 성공인 경우 결과값을 포함하는 [Stream], 실패인 경우 빈 [Stream]
    public Stream<T> successful() {
        return success()? Stream.ofNullable(this.value): Stream.empty();
    }

    /// 실패인 경우 [ApiError]를 포함하는 [Stream]을 반환한다.
    /// @return 실패인 경우 [ApiError]를 포함하는 [Stream], 성공인 경우 빈 [Stream]
    public Stream<ApiError> failed() {
        return Stream.ofNullable(error);
    }

    /// 성공인 경우 조건식을 만족하는 결과값을 포함하는 [Stream]을 반환한다.
    /// @param predicate 조건식
    /// @return 성공이고 조건식을 만족하는 경우 결과값을 포함하는 [Stream], 그렇지 않으면 빈 [Stream]
    public Stream<T> filter(Function<? super T, Boolean> predicate) {
        return (success() && predicate.apply(this.value))? Stream.of(this.value): Stream.empty();
    }

    /// 성공인 경우 결과값을 변환하여 [Stream]을 반환한다.
    /// @param mapper 변환 함수
    /// @return 성공인 경우 변환된 결과값을 포함하는 [Stream], 실패인 경우 빈 [Stream]
    public <R> Stream<R> map(Function<? super T, ? extends R> mapper) {
        return success()? Stream.of(mapper.apply(this.value)): Stream.empty();
    }

    /// 성공인 경우 결과값을 변환하여 [Stream]을 반환한다.
    /// @param mapper 변환 함수
    /// @return 성공인 경우 변환된 결과값을 포함하는 [Stream], 실패인 경우 빈 [Stream]
    public <R> Stream<R> flatMap(Function<? super T, Stream<R>> mapper) {
        return success()? mapper.apply(this.value): Stream.empty();
    }

    //
    // Optional style methods
    //

    /// 성공인 경우 결과값에 대해 지정한 동작을 수행한다.
    /// @param successAction 성공시 수행할 동작
    public void ifSuccess(Consumer<? super T> successAction) {
        if (success()) {
            successAction.accept(this.value);
        }
    }

    /// 실패인 경우 발생한 오류에 대해 지정한 동작을 수행한다.
    /// @param failureAction 실패시 수행할 동작
    public void ifFailure(Consumer<? super ApiError> failureAction) {
        if (!success()) {
            failureAction.accept(this.error);
        }
    }

    /// 성공인 경우 결과값에 대해 지정한 동작을 수행하고, 실패인 경우 오류에 대해 다른 동작을 수행한다.
    /// @param successAction 성공시 수행할 동작
    /// @param failureAction 실패시 수행할 동작
    public void ifSuccessOrElse(
            Consumer<? super T> successAction,
            Consumer<? super ApiError> failureAction) {
        if (success()) {
            successAction.accept(this.value);
        } else {
            failureAction.accept(this.error);
        }
    }

    /// 성공인 경우 결과값을 반환하고, 실패인 경우 대체 값을 반환한다.
    /// @param other 실패시 반환할 대체 값
    /// @return 성공시 반환되는 값, 실패시 대체 값
    public T orElse(T other) {
        return success()? this.value: other;
    }

    /// 성공인 경우 결과값을 반환하고, 실패인 경우 대체 값을 반환하는 함수형 인터페이스를 사용한다.
    /// @param recoveryFunction 실패시 반환할 대체 값을 생성하는 함수
    /// @return 성공시 반환되는 값, 실패시 대체 값
    public T orElseGet(Function<? super ApiError, ? extends T> recoveryFunction) {
        return success()? this.value: recoveryFunction.apply(this.error);
    }

    /// 실패인 경우 [ApiError]를 던진다.
    /// @return 성공시 반환되는 값
    /// @throws ApiError 실패시 발생하는 예외
    public T orElseThrow() {
        if (success()) {
            return this.value;
        } else {
            throw this.error;
        }
    }

    /// 실패인 경우 지정한 예외를 던진다.
    /// @param exceptionSupplier 실패시 던질 예외를 생성하는 함수
    /// @return 성공시 반환되는 값
    /// @throws X 실패시 발생하는 예외
    /// @param <X> 예외 타입
    public <X extends Throwable> T orElseThrow(Function<? super ApiError, ? extends X> exceptionSupplier) throws X {
        if (success()) {
            return this.value;
        } else {
            throw exceptionSupplier.apply(this.error);
        }
    }
}
