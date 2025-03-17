package org.noear.snack.core.util;

import org.noear.snack.annotation.ONodeAttr;
import org.noear.snack.core.Codec;
import org.noear.snack.exception.AnnotationProcessException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * @author noear 2025/3/16 created
 */
public class FieldWrapper {
    private final Field field;
    private final ONodeAttr attr;

    private String alias;
    private boolean ignore;
    private Codec codec;

    public FieldWrapper(Field field) {
        this.field = field;
        this.attr = field.getAnnotation(ONodeAttr.class);

        field.setAccessible(true);

        if (attr != null) {
            alias = attr.alias();
            ignore = attr.ignore();

            codec = ReflectionUtil.newInstance(attr.codec(), e -> new AnnotationProcessException("Failed to create codec for field: " + field.getName(), e));
        }

        if (Modifier.isTransient(field.getModifiers())) {
            ignore = true;
        }
    }

    public Field getField() {
        return field;
    }

    public String getAliasName() {
        if (alias == null) {
            return field.getName();
        } else {
            return alias;
        }
    }

    public Codec getCodec() {
        return codec;
    }

    public boolean isIgnore() {
        return ignore;
    }
}