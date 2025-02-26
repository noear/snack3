package org.noear.snack.core.utils;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author noear 2025/2/26 created
 */
public class GenericArrayTypeImpl implements GenericArrayType {
    private final Type genericComponentType;

    public GenericArrayTypeImpl(Type componentType) {
        this.genericComponentType = componentType;
    }

    public Type getGenericComponentType() {
        return this.genericComponentType;
    }

    public String toString() {
        Type typeTmp = this.getGenericComponentType();
        StringBuilder buf = new StringBuilder();
        if (typeTmp instanceof Class) {
            buf.append(((Class)typeTmp).getName());
        } else {
            buf.append(typeTmp);
        }

        buf.append("[]");
        return buf.toString();
    }

    public boolean equals(Object var1) {
        if (var1 instanceof GenericArrayType) {
            GenericArrayType var2 = (GenericArrayType)var1;
            return Objects.equals(this.genericComponentType, var2.getGenericComponentType());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hashCode(this.genericComponentType);
    }
}