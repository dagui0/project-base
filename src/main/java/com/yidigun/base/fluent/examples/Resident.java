package com.yidigun.base.fluent.examples;

import com.yidigun.base.fluent.DomainObject;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
public final class Resident implements DomainObject<ResidentKey>, ResidentKey.Aware {

    private final String residentId;
    private final String name;
    private final String address;
    @EqualsAndHashCode.Exclude
    private Instant createDate;

    public static class ResidentBuilder implements Builder<ResidentBuilder> {}

    @Override
    public ResidentKey primaryKey() {
        return ResidentKey.ofUnchecked(residentId);
    }
}
