package org.noear.snack.core.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

// file: GenericUtil.java
public class GenericUtil {
    // 解析泛型类型
    public static Class<?> resolveRawType(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }
        return Object.class;
    }

    // 获取泛型参数
    public static Type[] resolveTypeArguments(Type type) {
        return (type instanceof ParameterizedType) ?
                ((ParameterizedType) type).getActualTypeArguments() :
                new Type[0];
    }

    // 判断是否为集合类型
    public static boolean isCollectionType(Type type) {
        Class<?> rawType = resolveRawType(type);
        return Collection.class.isAssignableFrom(rawType);
    }

    // 判断是否为Map类型
    public static boolean isMapType(Type type) {
        Class<?> rawType = resolveRawType(type);
        return Map.class.isAssignableFrom(rawType);
    }

    // 创建泛型集合实例
    public static <T> Collection<T> createCollection(Type type) {
        Class<?> rawType = resolveRawType(type);
        if (List.class.isAssignableFrom(rawType)) return new ArrayList<>();
        if (Set.class.isAssignableFrom(rawType)) return new HashSet<>();
        throw new IllegalArgumentException("Unsupported collection type: " + type);
    }
}