package org.noear.snack.annotation;

import java.lang.annotation.*;

/**
 * 节点属性
 *
 * @author noear
 * @since 3.2
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ONodeAttr {
    String name() default "";
    String format() default "";
    boolean serialize() default true;
    boolean deserialize() default true;
}
