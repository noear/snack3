package org.noear.snack.annotation;

import java.lang.annotation.*;

/**
 * 节点名称
 *
 * @author noear
 * @since 3.1
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NodeName {
    String value();
}
