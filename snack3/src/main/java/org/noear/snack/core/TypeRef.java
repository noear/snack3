package org.noear.snack.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeRef<T> {
    protected final Type type;

    protected TypeRef() {
        Type superClass = this.getClass().getGenericSuperclass();
        this.type = ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

    public Type getType(){
        return type;
    }
}
