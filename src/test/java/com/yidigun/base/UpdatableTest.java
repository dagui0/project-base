package com.yidigun.base;

import org.junit.jupiter.api.Test;

public class UpdatableTest {

    @Test
    public void testUpdatableInterface() {
    }
}

interface Updatable<T> {

    void updateFrom(T source);
}

//interface DataTransferObject { }
//record PersonA(String name, int age) implements DataTransferObject {}
//record PersonB(String name, int height) implements DataTransferObject {}
//
//class PersonDto implements Updatable<DataTransferObject> {
//
//    private String name;
//    private int age;
//    private int height;
//
//    public void updateFrom(PersonA source) {
//        if (source != null) {
//            this.name = source.name();
//            this.age = source.age();
//        }
//    }
//
//    public void updateFrom(PersonB source) {
//        if (source != null) {
//            this.name = source.name();
//            this.age = source.height();
//        }
//    }
//
//    @Override
//    public void updateFrom(DataTransferObject source) {
//
//    }
//}
