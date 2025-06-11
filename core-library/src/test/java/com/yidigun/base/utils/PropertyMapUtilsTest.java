package com.yidigun.base.utils;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PropertyMapUtilsTest {

    @Test
    public void testGetAllInterfacesAndSuperClasses() {

        Class<?> clazz = ArrayList.class;
        Set<Class<?>> expected = Set.of(
                java.lang.Iterable.class,
                java.util.Collection.class,
                java.util.AbstractCollection.class,
                java.util.List.class,
                java.lang.Cloneable.class,
                java.io.Serializable.class,
                java.util.RandomAccess.class,
                //java.util.SequencedCollection.class,  // java 21+
                java.util.AbstractList.class
        );

        Set<Class<?>> actual = PropertyMapUtils.getAllInterfacesAndSuperClasses(clazz);
        assertTrue(actual.containsAll(expected));
    }

    @Test
    public void testGetAllInterfacesAndSuperClasses2() {
        Class<?> clazz = Dd.class;
        Set<Class<?>> expected = Set.of(
                IfDd.class,
                IfCc.class,
                IfBb.class,
                IfAa.class,
                Aa.class,
                Cc.class
        );
        assertEquals(expected, PropertyMapUtils.getAllInterfacesAndSuperClasses(clazz));
    }
}

interface IfAa {
    void aa();
}
interface IfBb {
    void bb();
}
interface IfCc extends IfAa, IfBb {
    void cc();
}
class Aa implements IfAa {
    @Override
    public void aa() {}
}
class Cc extends Aa implements IfCc {
    @Override
    public void cc() {}

    @Override
    public void bb() {}
}
interface IfDd extends IfCc {
    void dd();
}
class Dd extends Cc implements IfDd {
    @Override
    public void dd() {
    }
}
