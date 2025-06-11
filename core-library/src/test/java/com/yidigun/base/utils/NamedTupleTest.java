package com.yidigun.base.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NamedTupleTest {

    @Test
    public void testNamedTupleCreation() {
        NamedTuple tuple1 = NamedTuple.of(new String[] {"name", "age"}, "Alice", 30);
        NamedTuple tuple2 = NamedTuple.of("name, age", ",", "Alice", 30);
        NamedTuple tuple3 = NamedTuple.of("name; age", ";", "Alice", 30);
        Tuple unnamed = Tuple.of("Alice", 30);
        NamedTuple tuple4 = NamedTuple.of(unnamed, "name: age", ":");

        assertEquals(tuple1, tuple2);
        assertEquals(tuple1, tuple3);
        assertEquals(tuple1, tuple4);
    }

}
