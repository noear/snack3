package org.noear.snack.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 *
 * 例：(new TypeReference<List<String>>(){}).getType()
 * */
public abstract class TypeReference<T>{
    protected final Type type;

    protected TypeReference() {
        Type superClass = this.getClass().getGenericSuperclass();
        this.type = ((ParameterizedType)superClass).getActualTypeArguments()[0];
    }

    public Type getType() {
        return this.type;
    }
}
