package com.yidigun.base.beans.examples;

import com.yidigun.base.beans.DomainObject;
import com.yidigun.base.beans.PrimaryKey;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.time.Instant;

/// [MemberKey] 를 PK/FK로 사용하는 테이블용 도메인 클래스.
/// [Member]와 1:N의 식별 관계(Identifying Relationship)를 가진다.
/// 
@Getter
@Builder(toBuilder = true)
@EqualsAndHashCode
public class Address implements DomainObject<Address.Key>, MemberKey.Aware {

    /// primary key from Member
    private final long memberNo;
    /// primary key
    private final long addressNo;
    /// semantic field
    private final String address;
    /// logging field
    @EqualsAndHashCode.Exclude
    private final Instant createDate;   // logging field

    @Override
    public Key getPrimaryKey() {
        return new Key(memberNo, addressNo);
    }

    /// lombok @Builder 용 빌더클래스
    public static class AddressBuilder implements MemberKey.Aware.Builder<AddressBuilder> {
        AddressBuilder primaryKey(Address.Key key) {
            return memberNo(key.memberNo())
                  .addressNo(key.addressNo());
        }
    }

    /// PrimaryKey 클래스
    /// @param memberNo 회원번호
    /// @param addressNo 주소 일련번호
    @SuppressWarnings("LombokGetterMayBeUsed")
    public record Key(long memberNo, long addressNo) implements PrimaryKey, MemberKey.Aware {

        Key(MemberKey memberKey, long addressNo) {
            this(memberKey.longValue(), addressNo);
        }

        /// 팩토리 메소드
        /// @param memberKey 회원 키
        /// @param addressNo 주소 일련번호
        /// @return 주소 키
        public static Key of(MemberKey memberKey, long addressNo) {
            return new Key(memberKey.longValue(), addressNo);
        }
        @Override
        public long getMemberNo() {
            return memberNo;
        }

        /// 주소 일련번호를 반환한다.
        /// @return 주소 일련번호
        public long getAddressNo() {
            return addressNo;
        }
    }
}
