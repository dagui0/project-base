package com.yidigun.base.examples.fluent;

import com.yidigun.base.fluent.DomainObject;
import com.yidigun.base.fluent.PrimaryKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

/// JavaBeans 스타일의 도메인 객체 예시.
/// `@Accessors(fluent = false)`를 설정하여 기본 설정과 달리 JavaBeans 스타일로 getter 메소드를 생성한다.
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

    /// 기본 키를 조회한다
    /// @return 기본 키
    public Key getPrimaryKey() { return primaryKey(); }

    /// 기본 키 클래스
    /// @param key 기본 키 값
    public record Key(String key) implements PrimaryKey {
        @Override
        public @NotNull String toString() { return key; }
    }
}
