package org.noear.snack.core.utils;

import org.noear.snack.exception.SnackException;

import java.io.Reader;
import java.lang.reflect.Constructor;
import java.sql.Clob;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean工具类
 * */
public class BeanUtil {

    /**
     * 检查集合是否为空
     *
     * @param s 集合
     */
    public static boolean isEmpty(Collection s) {
        return s == null || s.size() == 0;
    }

    /**
     * 检查映射是否为空
     *
     * @param s 集合
     */
    public static boolean isEmpty(Map s) {
        return s == null || s.size() == 0;
    }

    //////////////


    private static final Map<String, Class<?>> clzCached = new ConcurrentHashMap<>();

    /**
     * @deprecated 3.2.55
     */
    @Deprecated
    public static Class<?> loadClass(String clzName) {
        if (StringUtil.isEmpty(clzName)) {
            return null;
        }

        return clzCached.computeIfAbsent(clzName, k -> {
            try {
                return Class.forName(k);
            } catch (Throwable e) {
                throw new SnackException("Failed to load class: " + clzName, e);
            }
        });
    }

    /////////////////


    /**
     * 将 Clob 转为 String
     */
    public static String clobToString(Clob clob) {

        Reader reader = null;
        StringBuilder buf = new StringBuilder();

        try {
            reader = clob.getCharacterStream();

            char[] chars = new char[2048];
            for (; ; ) {
                int len = reader.read(chars, 0, chars.length);
                if (len < 0) {
                    break;
                }
                buf.append(chars, 0, len);
            }
        } catch (Throwable e) {
            throw new SnackException("Read string from reader error", e);
        }

        String text = buf.toString();

        if (reader != null) {
            try {
                reader.close();
            } catch (Throwable e) {
                throw new SnackException("Read string from reader error", e);
            }
        }

        return text;
    }

    private static final Map<Class<?>, Object> insCached = new ConcurrentHashMap<>();

    /**
     * 获取实体
     */
    public static Object getInstance(Class<?> clz) {
        return insCached.computeIfAbsent(clz, k -> newInstance(k));
    }

    /**
     * 新建实例
     */
    public static Object newInstance(Class<?> clz) throws SnackException {
        try {
            if (clz.isInterface()) {
                return null;
            } else {
                return clz.getDeclaredConstructor().newInstance();
            }
        } catch (Throwable e) {
            throw new SnackException("Instantiation failed: " + clz.getName(), e);
        }
    }

    public static Object newInstance(Constructor constructor, Object[] args) throws SnackException {
        if (constructor == null) {
            throw new IllegalArgumentException("constructor is null");
        }

        try {
            return constructor.newInstance(args);
        } catch (Throwable e) {
            throw new SnackException("Instantiation failed: " + constructor.getDeclaringClass().getName(), e);
        }
    }
}