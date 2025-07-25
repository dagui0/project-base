package com.yidigun.base.fluent;

import com.yidigun.base.fluent.examples.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Calendar;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

public class FluentDomainObjectTest {

    @Test
    public void testSemanticEquality() {

        Instant now = Instant.now();
        ComplexKeyExample original = ComplexKeyExample.builder()
                .keyPart1(1)
                .keyPart2("A")
                .otherField1("field1")
                .otherField2("field2")
                .createDate(now)
                .updateDate(now)
                .build();

        Instant now2 = now.plusMillis(100);
        ComplexKeyExample modified = original.toBuilder()
                .updateDate(now2)
                .build();

        // semantic equality
        assertEquals(original, modified);
        assertEquals(original.hashCode(), modified.hashCode());

        // technical equality
        assertFalse(original.equalsAllFields(modified));
    }

    @Test
    public void testComparableKeyType() {

        // uncomparable pk
        ComplexKeyExample example1 = ComplexKeyExample.builder()
                .keyPart1(1)
                .keyPart2("A")
                .otherField1("field1")
                .otherField2("field2")
                .createDate(Instant.now())
                .updateDate(Instant.now()).build();

        // 컴파일 경고 발생하고, 실제 런타임에 예외 발생함
        assertThrows(ClassCastException.class, () -> {
            @SuppressWarnings("SortedCollectionWithNonComparableKeys")
            Map<PrimaryKey, DomainObject<?>> map1 = new TreeMap<>();
            map1.put(example1.primaryKey(), example1);
            assertTrue(map1.containsKey(example1.primaryKey()));
        });

        // comparable pk
        Instant now = Instant.now();
        SimpleKeyExample example2 = new SimpleKeyExample();
        example2.no(1);
        example2.name("example");
        example2.createDate(now);

        assertDoesNotThrow(() -> {
            // <K extends PrimaryKey & Comparable<K>, V extends BaseDomainObject<K>>
            testGenericType(example2.primaryKey(), example2);
        });
    }

    private <K extends Comparable<K> & PrimaryKey, V extends DomainObject<K>>
        void testGenericType(K key, V value) {

        Map<K, V> map = new TreeMap<>();
        map.put(key, value);
        assertTrue(map.containsKey(key));
    }

    @Test
    public void testIndependentPrimaryKeyType() {

        // session owner
        MemberKey currentMemberKey = MemberKey.of(1);

        // Master table
        Instant now = Instant.now();
        Member member = Member.builder()
                .memberKey(currentMemberKey)
                .name("member1")
                .registerDate(now)
                .createDate(now)
                .updateDate(now)
                .build();

        assertEquals(currentMemberKey.longValue(), member.primaryKey().longValue());
        assertEquals(currentMemberKey.longValue(), member.memberKey().longValue());

        // Child table
        Address address = Address.builder()
                .memberKey(currentMemberKey)
                .addressNo(1)
                .address("address1")
                .createDate(now)
                .build();

        assertEquals(currentMemberKey, address.memberKey());
        // Address.Key is also MemberKey.Aware
        assertEquals(currentMemberKey.longValue(), address.primaryKey().memberKey().longValue());

        // Address.Key type is a standalone value type.
        Address.Key key = Address.Key.of(member.primaryKey(), 1);
        assertEquals(key, address.primaryKey());

        // Post table has reference to Member table
        Post post = Post.builder()
                .postNo(1)
                .title("title")
                .content("content")
                .memberKey(currentMemberKey)
                .createDate(now)
                .updateDate(now).build();

        assertEquals(currentMemberKey, post.memberKey());
        assertEquals(currentMemberKey.longValue(), post.memberKey().longValue());
    }

    @Test
    public void testSemanticKey() {

        String residentId = "1111111111118";
        ResidentKey key1 = ResidentKey.of("1111111111118");

        Instant now = Instant.now();
        Resident resident1 = Resident.builder()
                .residentKey(key1)
                .name("홍길동")
                .address("서울시 강남구")
                .createDate(now)
                .build();

        Instant now2 = now.plusMillis(100);
        Resident resident2 = Resident.builder()
                .residentId("1111111111118")
                .name("홍길동")
                .address("서울시 강남구")
                .createDate(now2)
                .build();

        assertEquals(resident1, resident2);
        assertFalse(resident1.equalsAllFields(resident2));

        assertEquals("111111", resident1.residentKey().subSequence(0, 6));

        Calendar cal = Calendar.getInstance();
        cal.set(1911, 10, 11, 0, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        assertEquals(cal.toInstant(), resident1.residentKey().birthday());
    }
}
