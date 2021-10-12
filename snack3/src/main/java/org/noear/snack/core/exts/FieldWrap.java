package org.noear.snack.core.exts;

import org.noear.snack.exception.SnackException;
import org.noear.snack.annotation.NodeName;
import org.noear.snack.core.utils.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * 字段包装
 * */
public class FieldWrap {
    public final Field field;
    public final Class<?> type;
    public final Type genericType;

    private String _name;

    public FieldWrap(Class<?> clz, Field f) {
        field = f;
        type = f.getType();
        genericType = f.getGenericType();

        NodeName anno = f.getAnnotation(NodeName.class);
        if (anno != null) {
            _name = anno.value();
        }

        if (StringUtil.isEmpty(_name)) {
            _name = field.getName();
        }
    }

    public String name() {
        return _name;
    }

    public void setValue(Object tObj, Object val) {
        try {
            field.set(tObj, val);
        } catch (IllegalAccessException e) {
            throw new SnackException(e);
        }
    }

    public Object getValue(Object tObj) {
        try {
            return field.get(tObj);
        } catch (IllegalAccessException ex) {
            throw new SnackException(ex);
        }
    }
}
