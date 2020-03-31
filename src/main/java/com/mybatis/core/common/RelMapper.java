package com.mybatis.core.common;

import java.lang.annotation.*;

@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)

@Documented
public @interface RelMapper {
    String value() default "";
    String type() default "";
}
