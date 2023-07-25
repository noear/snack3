package org.noear.snack.annotation;


import java.lang.annotation.*;

/**
 * 枚举序列化/反序列字段
 *
 * @author hans
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnumCode {
}
