package org.near.snack3.core.exts;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

public class FieldWrap {
    public Field field;
    public Class<?> clz;
    public Type type;

    public FieldWrap(Field f) {
        field = f;
        clz = f.getType();
        type = f.getGenericType();
    }

    public String name(){
        return field.getName();
    }

    public void set(Object obj,Object val){
        try {
            field.set(obj, val);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public Object get(Object obj) {
        try {
            return field.get(obj);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
