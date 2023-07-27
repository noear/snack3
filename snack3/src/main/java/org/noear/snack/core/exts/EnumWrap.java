package org.noear.snack.core.exts;

import org.noear.snack.annotation.ONodeAttr;
import org.noear.snack.exception.SnackException;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum 包装器
 */
public class EnumWrap {
    protected final Map<String, Enum> enumMap = new HashMap<>();
    protected final Map<String, Enum> enumCustomMap = new HashMap<>();

    protected final Enum[] enumOrdinal;
    protected final Class<?> enumClass;

    protected Field oNodeAttrFiled;

    public EnumWrap(Class<?> enumClass) {
        this.enumClass = enumClass;
        this.enumOrdinal = (Enum[]) enumClass.getEnumConstants();

        for (int i = 0; i < enumOrdinal.length; ++i) {
            Enum e = enumOrdinal[i];
            if (enumMap.containsKey(e.name().toLowerCase())) {
                continue;
            }

            //添加name
            enumMap.put(e.name().toLowerCase(), e);

            //添另自定义 code
            for (Field field : e.getClass().getDeclaredFields()) {
                if (!field.isAnnotationPresent(ONodeAttr.class)) {
                    continue;
                }
                field.setAccessible(true);

                try {
                    Object custom = field.get(e);
                    oNodeAttrFiled=field;
                    enumCustomMap.put(enumClass.getName() + "#" + custom, e);
                } catch (IllegalAccessException ex) {
                    throw new SnackException(ex);
                }
            }
        }
    }

    /**
     * 根据顺序位获取
     * */
    public Enum get(int ordinal) {
        return enumOrdinal[ordinal];
    }

    /**
     * 根据名字获取
     * */
    public Enum get(String name) {
        return enumMap.get(name.toLowerCase());
    }

    /**
     * 根据自定义获取
     * */
    public Enum getCustom(String custom) {
        return enumCustomMap.get(enumClass.getName() + "#" + custom);
    }

    /**
     * 获取该枚举所被标记的字段的值
     * @return 如果没有被ONodeAttr标记则返回空，否则返回对应值
     */
    public Object getCodeFiledValue(Object o){
        try {
            if(oNodeAttrFiled == null){
                return null;
            }
            return oNodeAttrFiled.get(o);
        } catch (IllegalAccessException e) {
            throw new SnackException(e);
        }
    }
}
