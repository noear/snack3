package org.noear.snack.annotation;

import java.lang.annotation.*;

/**
 * @author noear 2021/1/1 created
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NodeName {
    String value();
}
