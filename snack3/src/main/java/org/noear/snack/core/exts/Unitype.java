package org.noear.snack.core.exts;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * 统一类型
 *
 * @author noear 2025/2/26 created
 */
public class Unitype {
    private Class<?> type;
    private Type genericType;

    public Unitype(Class<?> type, Type genericType) {
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
        if (!(o instanceof Unitype)) return false;
        Unitype unitype = (Unitype) o;
        return Objects.equals(type, unitype.type) && Objects.equals(genericType, unitype.genericType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, genericType);
    }
}
