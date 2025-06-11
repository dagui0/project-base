package com.yidigun.base;

import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.PACKAGE})
public @interface GenerateErrorCodeEnumTasks {
    GenerateErrorCodeEnum[] value() default {};
}
