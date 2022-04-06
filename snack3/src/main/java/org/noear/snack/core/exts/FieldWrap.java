package org.noear.snack.core.exts;

import org.noear.snack.annotation.ONodeAttr;
import org.noear.snack.exception.SnackException;
import org.noear.snack.annotation.NodeName;
import org.noear.snack.core.utils.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

    /**
     * 值设置器
     */
    private Method _setter;

    public FieldWrap(Class<?> clz, Field f, boolean isFinal) {
        field = f;
        type = f.getType();
        genericType = f.getGenericType();
        readonly = isFinal;

        field.setAccessible(true);

        NodeName anno = f.getAnnotation(NodeName.class);
        if (anno != null) {
            name = anno.value();
        }

        ONodeAttr attr = f.getAnnotation(ONodeAttr.class);
        if (attr != null) {
            name = attr.name();
            format = attr.format();
            serialize = attr.serialize();
            deserialize = attr.deserialize();
        }

        if (StringUtil.isEmpty(name)) {
            name = field.getName();
        }

        _setter = doFindSetter(clz, f);
    }

    @Deprecated
    public String name() {
        return name;
    }

    /**
     * @since 3.2
     */
    public String getName() {
        return name;
    }

    /**
     * @since 3.2
     */
    public String getFormat() {
        return format;
    }

    /**
     * @since 3.2
     */
    public boolean isDeserialize() {
        return deserialize;
    }

    /**
     * @since 3.2
     */
    public boolean isSerialize() {
        return serialize;
    }

    public void setValue(Object tObj, Object val) {
        //别的地方要用，不要去掉
        setValue(tObj, val, true);
    }

    public void setValue(Object tObj, Object val, boolean disFun) {
        if (readonly) {
            return;
        }

        try {
            if (_setter == null || disFun) {
                field.set(tObj, val);
            } else {
                _setter.invoke(tObj, new Object[]{val});
            }
        } catch (IllegalArgumentException ex) {
            if (val == null) {
                throw new IllegalArgumentException(field.getName() + "(" + field.getType().getSimpleName() + ") Type receive failur!", ex);
            }

            throw new IllegalArgumentException(
                    field.getName() + "(" + field.getType().getSimpleName() +
                            ") Type receive failure ：val(" + val.getClass().getSimpleName() + ")", ex);
        } catch (IllegalAccessException e) {
            throw new SnackException(e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public Object getValue(Object tObj) {
        try {
            return field.get(tObj);
        } catch (IllegalAccessException ex) {
            throw new SnackException(ex);
        }
    }

    /**
     * 查找设置器
     */
    private static Method doFindSetter(Class<?> tCls, Field field) {
        String fieldName = field.getName();
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        String setMethodName = "set" + firstLetter + fieldName.substring(1);

        try {
            Method setFun = tCls.getMethod(setMethodName, new Class[]{field.getType()});
            if (setFun != null) {
                return setFun;
            }
        } catch (NoSuchMethodException e) {
            //正常情况，不用管
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
