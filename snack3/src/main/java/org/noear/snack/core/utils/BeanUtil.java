package org.noear.snack.core.utils;

import org.noear.snack.exception.SnackException;

import java.io.Reader;
import java.sql.Clob;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bean工具类
 * */
public class BeanUtil {

    public static final Map<String, Class<?>> clzCached = new ConcurrentHashMap<>();

    public static Class<?> loadClass(String clzName) {
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
            throw new SnackException(e);
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
            throw new SnackException("read string from reader error", e);
        }

        String text = buf.toString();

        if (reader != null) {
            try {
                reader.close();
            } catch (Throwable e) {
                throw new SnackException("read string from reader error", e);
            }
        }

        return text;
    }

    public static Object newInstance(Class<?> clz) {
        try {
            if (clz.isInterface()) {
                return null;
            } else {
                return clz.newInstance();
            }
        } catch (Throwable e) {
            throw new SnackException("create instance error, class " + clz.getName(), e);
        }
    }
}
