package org.noear.snack.annotation;

import org.noear.snack.core.Codec;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author noear 2025/3/16 created
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ONodeAttr {
    String alias() default "";
    boolean ignore() default false;
    Class<? extends Codec> codec() default Codec.class;
}
