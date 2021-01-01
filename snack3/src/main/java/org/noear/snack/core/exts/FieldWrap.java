package org.noear.snack.core.exts;

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

    private Method _getter;
    private Method _setter;

    private String _name;

    public FieldWrap(Class<?> clz, Field f) {
        field = f;
        type = f.getType();
        genericType = f.getGenericType();

        _getter = findGetter(clz, f);
        _setter = findSetter(clz, f);

        NodeName anno = f.getAnnotation(NodeName.class);
        if (anno != null) {
            _name = anno.value();
        }

        if (StringUtil.isEmpty(_name)) {
            _name = field.getName();
        }
    }

    public String name(){
        return _name;
    }

    public void getValue(Object tObj, Object val){
        try {
            if (_setter == null) {
                field.set(tObj, val);
            } else {
                _setter.invoke(tObj, new Object[]{val});
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public Object setValue(Object tObj) {
        try {
            if(_getter == null){
                return field.get(tObj);
            }else{
                return _getter.invoke(tObj);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static Method findGetter(Class<?> tCls,Field field) {
        String fieldName = field.getName();
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        String setMethodName = "get" + firstLetter + fieldName.substring(1);

        try {
            Method getFun = tCls.getMethod(setMethodName);
            if (getFun != null) {
                return getFun;
            }
        } catch (NoSuchMethodException ex) {

        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private static Method findSetter(Class<?> tCls, Field field) {
        String fieldName = field.getName();
        String firstLetter = fieldName.substring(0, 1).toUpperCase();
        String setMethodName = "set" + firstLetter + fieldName.substring(1);

        try {
            Method setFun = tCls.getMethod(setMethodName, new Class[]{field.getType()});
            if (setFun != null) {
                return setFun;
            }
        } catch (NoSuchMethodException ex) {

        } catch (Throwable ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
