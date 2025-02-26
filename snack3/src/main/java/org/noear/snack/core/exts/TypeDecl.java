package org.noear.snack.core.exts;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * 类型申明
 *
 * @author noear 2025/2/26 created
 */
public class TypeDecl {
    private Class<?> type;
    private Type genericType;

    public TypeDecl(Class<?> type, Type genericType) {
        this.type = type;
        if (genericType == null) {
            this.genericType = type;
        } else {
            this.genericType = genericType;
        }
    }

    public Class<?> getType() {
        return type;
    }

    public Type getGenericType() {
        return genericType;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TypeDecl)) return false;
        TypeDecl typeDecl = (TypeDecl) o;
        return Objects.equals(type, typeDecl.type) && Objects.equals(genericType, typeDecl.genericType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, genericType);
    }
}
