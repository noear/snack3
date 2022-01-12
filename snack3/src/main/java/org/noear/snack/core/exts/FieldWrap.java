package org.noear.snack.core.exts;

import org.noear.snack.annotation.ONodeAttr;
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
    public final boolean readonly;

    private String name;
    private String format;
    private boolean serialize = true;
    private boolean deserialize = true;

    public FieldWrap(Class<?> clz, Field f, boolean isFinal) {
        field = f;
        type = f.getType();
        genericType = f.getGenericType();
        readonly = isFinal;

        NodeName anno = f.getAnnotation(NodeName.class);
        if (anno != null) {
            name = anno.value();
        }

        ONodeAttr attr = f.getAnnotation(ONodeAttr.class);
        if(attr != null){
            name = attr.name();
            format = attr.format();
            serialize = attr.serialize();
            deserialize = attr.deserialize();
        }

        if (StringUtil.isEmpty(name)) {
            name = field.getName();
        }
    }

    @Deprecated
    public String name(){
        return name;
    }

    /**
     * @since 3.2
     * */
    public String getName() {
        return name;
    }

    /**
     * @since 3.2
     * */
    public String getFormat() {
        return format;
    }

    /**
     * @since 3.2
     * */
    public boolean isDeserialize() {
        return deserialize;
    }

    /**
     * @since 3.2
     * */
    public boolean isSerialize() {
        return serialize;
    }

    public void setValue(Object tObj, Object val) {
        if(readonly){
            return;
        }

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
