package com.yidigun.base.utils;

import java.io.Serial;
import java.util.*;
import java.util.regex.Pattern;

/// 이름(키)를 지정 가능한 [Tuple] 클래스.
/// 이 클래스는 [Tuple]과 마찬가지로 여러 값을 묶어서 반환하거나 전달할 때 [Map]보다 가볍게 사용할 수 있다.
///
/// 이름은 앞에서 부터 지정된 갯수만큼 적용되며, 이름이 부족한 경우에는 숫자 index 또는 `index[n]` 형식으로 사용 가능하다.
///
/// ```java
/// NamedTuple processSomething() {
///     ...
///     return NamedTuple.of("name,age,sex", ",", "John Doe", 30, Sex.MALE, "Seoul, Korea", friends);
/// }
///
/// NamedTuple result = processSomething();
/// String name = result.get("name", String.class);
/// int age = result.get("age", Integer.class);
/// String address = result.get("index[3]", String.class); // 이름 대신에 "index[n]" 형식 지원
/// Person[] friends = result.get(4, Person[].class);  // index 로도 접근 가능
/// Map<String, Object> map = result.toMap(); // Map 으로 변환 가능
/// ```
///
/// 만약 값의 갯수 보다 이름 개수가 많은 경우 필요 없는 이름은 저장되지 않고 버려진다.
/// [#append(Object...)] 메소드를 사용하여 새로운 값을 추가하더라도 버려진 이름은 사용할 수 없다.
///
/// 만약 이름 목록에 중복이 있는 경우, 이름으로 조회할때는 앞에 있는 값이 조회되며, 뒤쪽의 값들은 index로만 접근 가능하다.
///
/// @see Tuple
public class NamedTuple extends Tuple {

    @Serial
    private static final long serialVersionUID = 5589351559217078374L;

    /// `index[n]` 형식의 이름을 찾기 위한 정규 표현식
    private static final Pattern INDEX_NAME_PATTERN = Pattern.compile("index\\[(\\d+)\\]");

    /// 이름 배열
    private final String[] names;

    /// 생성자
    protected NamedTuple(String[] names, Object... values) {
        super(values);
        // names 가 values 보다 더 짧은 경우는 허용됨
        // names 가 values 보다 더 긴 경우는 values 까지만 사용하고 나머지는 버림
        this.names = (names.length > values.length)?
                            Arrays.copyOf(names, values.length):
                            Arrays.copyOf(names, names.length);
    }

    /// 이름과 값 배열을 이용해서 생성
    /// @param names 이름 배열
    /// @param values 값 배열
    /// @return NamedTuple 객체
    public static NamedTuple of(String[] names, Object... values) {
        return new NamedTuple(names, values);
    }

    /// 이름 목록과 구분자, 값 배열을 이용하여 생성
    /// @param names 이름 목록 문자열
    /// @param delimiter 이름 목록을 구분하는 문자열
    /// @param values 값 배열
    /// @return NamedTuple 객체
    public static NamedTuple of(String names, String delimiter, Object... values) {
        return new NamedTuple(splitNames(names, delimiter), values);
    }

    /// [Tuple] 객체에 이름 목록을 지정하여 생성
    /// @param tuple 이름 목록을 지정할 [Tuple] 객체
    /// @param names 이름 배열
    /// @return NamedTuple 객체
    public static NamedTuple of(Tuple tuple, String... names) {
        return new NamedTuple(names, tuple.values);
    }

    /// [Tuple] 객체에 이름 목록과 구분자를 지정하여 생성
    /// @param tuple 이름 목록을 지정할 [Tuple] 객체
    /// @param names 이름 목록 문자열
    /// @param delimiter 이름 목록을 구분하는 문자열
    public static NamedTuple of(Tuple tuple, String names, String delimiter) {
        return new NamedTuple(splitNames(names, delimiter), tuple.values);
    }

    /// 이름 목록을 구분자로 분할
    /// @param names 이름 목록 문자열
    /// @param delimiter 이름 목록을 구분하는 문자열
    /// @return 분할된 이름 배열
    private static String[] splitNames(String names, String delimiter) {
        if (StringUtils.isEmpty(names))
            return new String[0];
        else if (StringUtils.isEmpty(delimiter))
            return new String[] { names };
        else
            return StringUtils.compileDelimiterPattern(delimiter).split(names);
    }

    /// 현재 튜플 뒤에 새로운 값을 추가하여 새로운 튜플을 반환한다.
    /// 이 메소드는 현재 튜플을 변경하지 않고, 새로운 튜플을 반환한다.
    /// 추가할 값이 없으면 현재 튜플을 그대로 반환한다.
    ///
    /// 이름은 추가되지 않으며, 새로 추가된 값은 index로만 접근 가능하다.
    ///
    /// @param values 추가할 값들
    /// @return 새로운 Tuple 객체
    @Override
    public NamedTuple append(Object... values) {
        if (values == null || values.length == 0) {
            return this; // 추가할 값이 없으면 현재 튜플을 그대로 반환
        }
        Object[] newValues = Arrays.copyOf(this.values, this.values.length + values.length);
        System.arraycopy(values, 0, newValues, this.values.length, values.length);
        return new NamedTuple(names, newValues);
    }

    /// 현재 튜플 뒤에 다른 튜플의 값을 추가하여 새로운 튜플을 반환한다.
    /// 이 메소드는 현재 튜플을 변경하지 않고, 새로운 튜플을 반환한다.
    /// 추가할 값이 없으면 현재 튜플을 그대로 반환한다.
    ///
    /// 추가할 튜플이 [NamedTuple]인 경우, 이름도 함께 추가된다.
    ///
    /// @param tuple 추가할 튜플
    /// @return 새로운 Tuple 객체
    /// @see #append(NamedTuple)
    @Override
    public NamedTuple append(Tuple tuple) {
        if (tuple == null || tuple.isEmpty()) {
            return this; // 추가할 값이 없으면 현재 튜플을 그대로 반환
        }
        if (tuple instanceof NamedTuple namedTuple) {
            return append(namedTuple);
        }
        return append(tuple.values);
    }

    /// 현재 튜플 뒤에 다른 [NamedTuple]의 값을 추가하여 새로운 튜플을 반환한다.
    /// 이 메소드는 현재 튜플을 변경하지 않고, 새로운 튜플을 반환한다.
    /// 추가할 값이 없으면 현재 튜플을 그대로 반환한다.
    ///
    /// 두 [NamedTuple]의 이름-값 매핑은 보존된다.
    /// 만약 현재 튜플의 이름이 부족한 경우, 원래 이름이 없던 값들은 계속 `index[n]` 또는 `index`로 접근해야 한다.
    /// 새로 추가된 값들의 이름은 중복되지 않는다면 전달된 [NamedTuple]의 이름을 그대로 사용 가능하다.
    ///
    /// @param tuple 추가할 튜플
    /// @return 새로운 Tuple 객체
    public NamedTuple append(NamedTuple tuple) {
        if (tuple == null || tuple.isEmpty()) {
            return this;
        }
        String[] newNames = Arrays.copyOf(this.names, this.values.length + tuple.names.length);
        System.arraycopy(tuple.names, 0, newNames, this.values.length, tuple.names.length);
        Object[] newValues = Arrays.copyOf(this.values, this.values.length + tuple.values.length);
        System.arraycopy(tuple.values, 0, newValues, this.values.length, tuple.values.length);
        return new NamedTuple(newNames, newValues);
    }

    /// 현재 튜플의 일부를 잘라내어 새로운 튜플을 반환한다.
    /// 지정한 인덱스 범위가 전체 범위일 경우 현재 튜플을 그대로 반환한다.
    /// @param startIndex 잘라낼 시작 인덱스 (0부터 시작)
    /// @param endIndex 잘라낼 끝 인덱스 (exclusive, 즉 endIndex는 포함되지 않음)
    /// @return 새로운 Tuple 객체
    @Override
    public NamedTuple subTuple(int startIndex, int endIndex) {
        if (startIndex == 0 && endIndex == values.length) {
            return this; // 전체 튜플을 반환
        }
        Tuple subTuple = super.subTuple(startIndex, endIndex);
        String[] subNames;
        if (endIndex > names.length) {
            endIndex = names.length; // prevent ArrayIndexOutOfBoundsException
        }
        subNames = (startIndex > names.length)? new String[0]: Arrays.copyOfRange(names, startIndex, endIndex);
        return new NamedTuple(subNames, subTuple.values);
    }

    /// 이름에 해당하는 index를 찾는다.
    /// 이름이 지정되지 않은 값을 위해 `index[n]` 형식의 이름을 지원한다.
    private int indexOf(String name) {
        if (StringUtils.isEmpty(name)) {
            return -1; // 이름이 없으면 -1 반환
        }
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(name)) {
                return i;
            }
        }
        if (!name.startsWith("index[")) {
            return -1;
        }
        return INDEX_NAME_PATTERN.matcher(name).results()
                .findFirst()
                .map(m -> {
                    int index = Integer.parseInt(m.group(1));
                    return (index >= 0 && index < names.length)? index: -1;
                })
                .orElse(-1);
    }

    /// 이름 또는 `index`로 문자열 이름(key)을 만든다.
    /// 이름이 지정되지 않을 경우 `index[n]` 형식을 반환한다.
    private String nameOrIndex(String name, int index) {
        return (StringUtils.isEmpty(name))? "index[" + index + "]": name;
    }

    /// 지정된 이름에 해당하는 값을 반환한다.
    /// 이름 대신에 `index[n]` 형식의 문자열을 사용할 수 있다.
    /// @param name 이름 또는 `index[n]` 형식의 문자열
    /// @return 해당 이름에 매핑된 값, 없으면 null
    public Object get(String name) {
        int index = indexOf(name);
        return (index >= 0)? get(index): null;
    }

    /// 지정된 이름에 해당하는 값을 반환한다.
    /// 이름 대신에 `index[n]` 형식의 문자열을 사용할 수 있다.
    /// @param name 이름 또는 `index[n]` 형식의 문자열
    /// @param type 반환할 값의 타입
    /// @return 해당 이름에 매핑된 값, 없으면 null
    /// @param <R> 반환할 값의 타입
    public <R> R get(String name, Class<R> type) {
        Object value = get(name);
        return (type.isInstance(value))? type.cast(value): null;
    }

    /// 지정된 이름에 해당하는 값을 반환한다.
    /// 이름 대신에 `index[n]` 형식의 문자열을 사용할 수 있다.
    /// @param name 이름 또는 `index[n]` 형식의 문자열
    /// @return 해당 이름에 매핑된 값
    /// @throws ClassCastException 해당 이름에 매핑된 값이 지정한 타입과 호환되지 않는 경우
    /// @param <R> 반환할 값의 타입
    public <R> R getAs(String name, Class<R> type) throws ClassCastException {
        Object value = get(name);
        if (type.isInstance(value))
            return type.cast(value);
        else
            throw new ClassCastException("Cannot cast " + value.getClass().getName()
                    + " to " + type.getName() + " for name: " + name);
    }

    /// 지정된 이름에 해당하는 값을 Optional로 반환한다.
    /// 이름 대신에 `index[n]` 형식의 문자열을 사용할 수 있다.
    /// @param name 이름 또는 `index[n]` 형식의 문자열
    /// @return 해당 이름에 매핑된 값을 감싼 Optional 객체, 없으면 빈 Optional
    public Optional<Object> tryGet(String name) {
        return Optional.ofNullable(get(name));
    }

    /// 지정된 이름에 해당하는 값을 Optional로 반환한다.
    /// 이름 대신에 `index[n]` 형식의 문자열을 사용할 수 있다.
    /// @param name 이름 또는 `index[n]` 형식의 문자열
    /// @param type 반환할 값의 타입
    /// @return 해당 이름에 매핑된 값을 감싼 Optional 객체, 없으면 빈 Optional
    /// @param <R> 반환할 값의 타입
    public <R> Optional<R> tryGet(String name, Class<R> type) {
        Object value = get(name);
        return (type.isInstance(value))? Optional.of(type.cast(value)): Optional.empty();
    }

    /// [Map] 형태로 변환한다.
    /// 이름이 지정된 값들은 이름을 키로 사용하고, 이름이 지정되지 않은 값들은 `index[n]` 형식의 키를 사용한다.
    /// @return 변환된 [Map] 객체
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        int i = 0;
        for (; i < names.length; i++) {
            map.put(nameOrIndex(names[i], i), values[i]);
        }
        for (; i < values.length; i++) {
            map.put(nameOrIndex(null, i), values[i]);
        }
        return Collections.unmodifiableMap(map);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("NamedTuple{");
        int i = 0;
        for (; i < names.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(nameOrIndex(names[i], i)).append("=").append(values[i]);
        }
        for (; i < values.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(nameOrIndex(null, i)).append("=").append(values[i]);
        }
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NamedTuple that)) return false;
        return Arrays.equals(this.names, that.names) && super.equals(that);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(names);
        result = 31 * result + super.hashCode();
        return result;
    }
}
