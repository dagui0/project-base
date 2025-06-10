package com.yidigun.base.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TupleTest {

    @Test
    public void testTuple() {

        Tuple tuple = Tuple.of("Tinky Winky", "Dipsy", "Laa-Laa", "Po", "Voice Trumpets", "Sun Baby", "Noo Noo");

        assertEquals(7, tuple.size());
        assertEquals("Tinky Winky", tuple.first());
        assertEquals("Dipsy", tuple.second());
        assertEquals("Laa-Laa", tuple.third());
        assertEquals("Po", tuple.fourth());
        assertEquals("Voice Trumpets", tuple.fifth());

        assertEquals("Tinky Winky", tuple.getFirst());
        assertEquals("Dipsy", tuple.getSecond());
        assertEquals("Laa-Laa", tuple.getThird());
        assertEquals("Po", tuple.getFourth());
        assertEquals("Voice Trumpets", tuple.getFifth());

        assertNull(tuple.getFirst(Integer.class));
        assertNotNull(tuple.getFirstAs(String.class));
        assertThrows(ClassCastException.class, () -> tuple.getFirstAs(Integer.class));
        assertTrue(tuple.tryGetFirst().isPresent());
        assertFalse(tuple.tryGetFirst(Integer.class).isPresent());

        assertThrows(IndexOutOfBoundsException.class, () -> tuple.get(7));
    }

    @Test
    public void testTupleConversion() {

        Object[] source = new Object[] { "Tinky Winky", "Dipsy", "Laa-Laa", "Po", "Voice Trumpets", "Sun Baby", "Noo Noo" };

        Tuple tuple = Tuple.of(source);

        Object[] converted = tuple.toArray();

        assertArrayEquals(source, converted);
    }

    @Test
    public void testImmutable() {

        Object[] source = new Object[] { "Tinky Winky", "Dipsy", "Laa-Laa", "Po", "Voice Trumpets", "Sun Baby", "Noo Noo" };

        Tuple tuple = Tuple.of(source);

        assertEquals("Tinky Winky", tuple.first());
        source[0] = "Changed";
        assertEquals("Tinky Winky", tuple.first());


        Object[] converted = tuple.toArray();
        converted[0] = "Changed";
        assertEquals("Tinky Winky", tuple.first());
    }
}
