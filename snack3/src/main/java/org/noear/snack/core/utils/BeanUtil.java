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

    private static final Map<String, Class<?>> clzCached = new ConcurrentHashMap<>();

    /**
     * @deprecated 3.2.55
     */
    @Deprecated
    public static Class<?> loadClass(String clzName) {
        if (StringUtil.isEmpty(clzName)) {
            return null;
        }

        try {
            Class<?> clz = clzCached.get(clzName);
            if (clz == null) {
                clz = Class.forName(clzName);
                clzCached.put(clzName, clz);
            }

            return clz;
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new SnackException("Failed to load class: " + clzName, e);
        }
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
