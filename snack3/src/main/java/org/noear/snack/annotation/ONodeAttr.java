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
    /**
     * 名称
     */
    String name() default "";

    /**
     * 格式化
     */
    String format() default "";

    /**
     * 时区
     */
    String timezone() default "";

    /**
     * 作为字符串
     */
    boolean asString() default false;

    /**
     * 忽略（相当于：serialize=false, deserialize=false）
     */
    boolean ignore() default false;

    /**
     * 序列化
     */
    boolean serialize() default true;

    /**
     * 反序列化
     */
    boolean deserialize() default true;

    /**
     * 包函null
     */
    boolean incNull() default true;

    /**
     * 翻译器
     * */
    String translator() default "";
}
