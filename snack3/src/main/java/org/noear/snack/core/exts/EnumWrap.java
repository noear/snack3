package org.noear.snack.core.exts;

import org.noear.snack.annotation.EnumCode;
import org.noear.snack.exception.SnackException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum 包装器
 */
public class EnumWrap {
    protected final Map<String, Enum> enumMap = new HashMap<>();

    protected final Map<String, Enum> enumCode = new HashMap<>();

    protected final Enum[] enumOrdinal;

    public EnumWrap(Class<?> enumClass) {
        enumOrdinal = (Enum[]) enumClass.getEnumConstants();

        for (int i = 0; i < enumOrdinal.length; ++i) {
            Enum e = enumOrdinal[i];
            if (enumMap.containsKey(e.name().toLowerCase())) {
                continue;
            }
            enumMap.put(e.name().toLowerCase(), e);
            for (Field field : e.getClass().getDeclaredFields()) {
                if (!field.isAnnotationPresent(EnumCode.class)) {
                    continue;
                }
                field.setAccessible(true);
                try {
                    Object o = field.get(e);
                    enumCode.put(enumClass.getName().toLowerCase() + o, e);
                } catch (IllegalAccessException ex) {
                    throw new SnackException(ex);
                }
            }
        }
//        for (Field field : enumClass.getDeclaredFields()) {
//            if (!field.isAnnotationPresent(EnumCode.class)) {
//                continue;
//            }
//            field.setAccessible(true);
//            try {
//                Object o =  field.get(enumClass);
////                enumEnum.put()
//            } catch (IllegalAccessException ex) {
//                throw new RuntimeException(ex);
//            }
//        }
    }

    public Enum get(int ordinal) {
        return enumOrdinal[ordinal];
    }

    public Enum get(String name) {
        return enumMap.get(name.toLowerCase());
    }

    public Enum getCode(String name) {

        return enumCode.get(name.toLowerCase());
    }

}
