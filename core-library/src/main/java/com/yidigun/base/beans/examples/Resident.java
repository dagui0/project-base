package com.yidigun.base.beans.examples;

import com.yidigun.base.beans.DomainObject;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

/// [ResidentKey] 를 PK로 사용하는 주민등록 도메인 클래스 예시.
///
@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
public final class Resident implements DomainObject<ResidentKey>, ResidentKey.Aware {

    private final String residentId;
    private final String name;
    private final String address;
    @EqualsAndHashCode.Exclude
    private Instant createDate;

    /// [ResidentKey] 의 PK 타입 클래스.
    public static class ResidentBuilder implements ResidentKey.Aware.Builder<ResidentBuilder> {}

    @Override
    public ResidentKey getPrimaryKey() {
        return ResidentKey.ofUnchecked(residentId);
    }
}
