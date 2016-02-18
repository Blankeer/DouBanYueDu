package io.realm.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface DefineTable {
    String query() default "";

    String row() default "";

    String table() default "";

    String view() default "";
}
