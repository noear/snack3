package org.noear.snack.core.exts;

import org.noear.snack.annotation.ONodeAttr;
import org.noear.snack.exception.SnackException;
import org.noear.snack.annotation.NodeName;
import org.noear.snack.core.utils.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.time.ZoneId;
import java.util.TimeZone;

/**
 * 字段包装
 * */
public class FieldWrap {
    //所有者类
    private final TypeDecl owner;
    //字段
    private final Field field;

    public final boolean readonly;
    public final boolean hasSetter;
    public final boolean hasGetter;

    private String name;
    private String format;
    private TimeZone timeZone;
    private boolean asString = false;
    private boolean serialize = true;
    private boolean deserialize = true;
    private boolean incNull = true;
    private boolean flat = false;

    //值设置器
    private Method _setter;
    //值获取器
    private Method _getter;

    public FieldWrap(TypeDecl owner, Field f, boolean isFinal) {
        this.owner = owner;
        this.field = f;
        this.readonly = isFinal;

        NodeName anno = f.getAnnotation(NodeName.class);
        if (anno != null) {
            name = anno.value();
        }

        ONodeAttr attr = f.getAnnotation(ONodeAttr.class);
        if (attr != null) {
            name = attr.name();
            format = attr.format();
            incNull = attr.incNull();
            flat = attr.flat();
            asString = attr.asString();


            if (StringUtil.isEmpty(attr.timezone()) == false) {
                timeZone = TimeZone.getTimeZone(ZoneId.of(attr.timezone()));
            }


            if (attr.ignore()) {
                serialize = false;
                deserialize = false;
            } else {
                serialize = attr.serialize();
                deserialize = attr.deserialize();
            }
        }

        if (StringUtil.isEmpty(name)) {
            name = field.getName();
        }

        _setter = doFindSetter(owner.getType(), f);
        _getter = doFindGetter(owner.getType(), f);

        hasSetter = _setter != null;
        hasGetter = _getter != null;
    }

    @Deprecated
    public String name() {
        return name;
    }

    /**
     * 获取所有者类
     */

    public TypeDecl getOwner() {
        return owner;
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
    public TimeZone getTimeZone() {
        return timeZone;
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

    /**
     * @since 3.2
     */
    public boolean isIncNull() {
        return incNull;
    }

    /**
     *
     */
    public boolean isFlat() {
        return flat;
    }


    //字段类型包装（懒加载）
    private TypeWrap typeWrap;

    private TypeWrap getTypeWrap() {
        if (typeWrap == null) {
            typeWrap = new TypeWrap(owner.getGenericType(), field.getType(), field.getGenericType());

            if (typeWrap.isInvalid()) {
                throw new IllegalStateException("Field generic analysis failed: "
                        + field.getDeclaringClass().getName()
                        + "."
                        + field.getName());
            }
        }

        return typeWrap;
    }

    public TypeWrap typeWrapOf(Type genericInfo) {
        if (genericInfo == null || genericInfo == owner.getType()) {
            return getTypeWrap();
        } else {
            return new TypeWrap(genericInfo, field.getType(), field.getGenericType());
        }
    }

    /**
     * 获取类型
     */
    public Class<?> getType() {
        return getTypeWrap().getType();
    }

    /**
     * 获取参数类型
     */
    public Type getGenericType() {
        return getTypeWrap().getGenericType();
    }


    /**
     * @since 3.2.90
     */
    public boolean isAsString() {
        return asString;
    }

    public void setValue(Object tObj, Object val) {
        //别的地方要用，不要去掉
        setValue(tObj, val, false);
    }

    public void setValue(Object tObj, Object val, boolean useSetter) {
        if (readonly) {
            return;
        }

        try {
            if (_setter != null && useSetter) {
                _setter.invoke(tObj, new Object[]{val});
            } else {
                if (field.isAccessible() == false) {
                    field.setAccessible(true);
                }

                field.set(tObj, val);
            }
        } catch (IllegalArgumentException ex) {
            if (val == null) {
                throw new IllegalArgumentException(field.getName() + "(" + field.getType().getSimpleName() + ") Type receive failure!", ex);
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

    public Object getValue(Object tObj, boolean useGetter) {
        try {
            if (_getter != null && useGetter) {
                return _getter.invoke(tObj);
            } else {
                if (field.isAccessible() == false) {
                    field.setAccessible(true);
                }

                return field.get(tObj);
            }
        } catch (IllegalAccessException e) {
            throw new SnackException(e);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
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

    /**
     * 查找设置器
     */
    private static Method doFindGetter(Class<?> tCls, Field field) {
        String fieldName = field.getName();
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        String setMethodName = "get" + firstLetter + fieldName.substring(1);

        try {
            Method getFun = tCls.getMethod(setMethodName);
            if (getFun != null) {
                return getFun;
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