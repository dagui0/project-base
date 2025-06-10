package com.yidigun.base.utils;

import java.io.Serial;
import java.util.*;
import java.util.regex.Pattern;

public class NamedTuple extends Tuple {

    @Serial
    private static final long serialVersionUID = 5589351559217078374L;

    private static final Pattern INDEX_NAME_PATTERN = Pattern.compile("index\\[(\\d+)\\]");

    private final String[] names;

    protected NamedTuple(String[] names, Object... values) {
        super(values);
        // names 가 values 보다 더 짧은 경우는 허용됨
        // names 가 values 보다 더 긴 경우는 values 까지만 사용하고 나머지는 버림
        this.names = (names.length > values.length)?
                            Arrays.copyOf(names, values.length):
                            Arrays.copyOf(names, names.length);
    }

    public static NamedTuple of(String[] names, Object... values) {
        return new NamedTuple(names, values);
    }

    public static NamedTuple of(String names, String delimiter, Object... values) {
        return new NamedTuple(splitNames(names, delimiter), values);
    }

    public static NamedTuple of(Tuple tuple, String... names) {
        return new NamedTuple(names, tuple.values);
    }

    public static NamedTuple of(Tuple tuple, String names, String delimiter) {
        return new NamedTuple(splitNames(names, delimiter), tuple.values);
    }

    private static String[] splitNames(String names, String delimiter) {
        if (StringUtils.isEmpty(names))
            return new String[0];
        else if (StringUtils.isEmpty(delimiter))
            return new String[] { names };
        else
            return StringUtils.compileDelimiterPattern(delimiter).split(names);
    }

    @Override
    public NamedTuple append(Object... values) {
        Object[] newValues = Arrays.copyOf(this.values, this.values.length + values.length);
        System.arraycopy(values, 0, newValues, this.values.length, values.length);
        return new NamedTuple(names, newValues);
    }

    @Override
    public NamedTuple append(Tuple tuple) {
        if (tuple instanceof NamedTuple namedTuple) {
            return append(namedTuple);
        }
        return append(tuple.values);
    }

    public NamedTuple append(NamedTuple tuple) {
        if (tuple == null || tuple.values.length == 0) {
            return this;
        }
        String[] newNames = Arrays.copyOf(this.names, this.values.length + tuple.names.length);
        System.arraycopy(tuple.names, 0, newNames, this.values.length, tuple.names.length);
        Object[] newValues = Arrays.copyOf(this.values, this.values.length + tuple.values.length);
        System.arraycopy(tuple.values, 0, newValues, this.values.length, tuple.values.length);
        return new NamedTuple(newNames, newValues);
    }

    @Override
    public NamedTuple subTuple(int startIndex, int endIndex) {
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

    public Object get(String name) {
        int index = indexOf(name);
        return (index >= 0)? get(index): null;
    }

    public <R> R get(String name, Class<R> type) {
        Object value = get(name);
        return (type.isInstance(value))? type.cast(value): null;
    }

    public <R> R getAs(String name, Class<R> type) throws ClassCastException {
        Object value = get(name);
        if (type.isInstance(value))
            return type.cast(value);
        else
            throw new ClassCastException("Cannot cast " + value.getClass().getName()
                    + " to " + type.getName() + " for name: " + name);
    }

    public Optional<Object> tryGet(String name) {
        return Optional.ofNullable(get(name));
    }

    public <R> Optional<R> tryGet(String name, Class<R> type) {
        Object value = get(name);
        return (type.isInstance(value))? Optional.of(type.cast(value)): Optional.empty();
    }

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
