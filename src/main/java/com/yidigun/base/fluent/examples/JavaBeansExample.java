package com.yidigun.base.fluent.examples;

import com.yidigun.base.fluent.DomainObject;
import com.yidigun.base.fluent.PrimaryKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

@Accessors(fluent = false)
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class JavaBeansExample implements DomainObject<JavaBeansExample.Key> {

    private final String key;
    private final String name;
    @EqualsAndHashCode.Exclude
    private final Instant createdDate;

    @Override
    public Key primaryKey() { return new Key(key); }

    public Key getPrimaryKey() { return primaryKey(); }

    public record Key(String key) implements PrimaryKey {
        @Override
        public @NotNull String toString() { return key; }
    }
}
