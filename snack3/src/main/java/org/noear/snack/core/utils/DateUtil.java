package org.noear.snack.core.utils;

import org.noear.snack.core.DEFAULTS;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author noear 2021/6/13 created
 */
public class DateUtil {
    public static final DateFormat FORMAT_24_ISO08601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", DEFAULTS.DEF_LOCALE);
    public static final DateFormat FORMAT_19_ISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", DEFAULTS.DEF_LOCALE);
    public static final DateFormat FORMAT_19 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", DEFAULTS.DEF_LOCALE);
    public static final DateFormat FORMAT_22 = new SimpleDateFormat("yyyyMMddHHmmssSSSZ", DEFAULTS.DEF_LOCALE);//z: +0000
    public static final DateFormat FORMAT_10 = new SimpleDateFormat("yyyy-MM-dd", DEFAULTS.DEF_LOCALE);
    public static final DateFormat FORMAT_29 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", DEFAULTS.DEF_LOCALE);
    public static final DateFormat FORMAT_23_a = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS", DEFAULTS.DEF_LOCALE);
    public static final DateFormat FORMAT_23_b = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", DEFAULTS.DEF_LOCALE);

    public static Date parse(String strVal) throws ParseException {
        final int len = strVal.length();
        if (len == 24) {
            if (strVal.charAt(10) == 'T') {
                return FORMAT_24_ISO08601.parse(strVal);
            }
        }

        if (len == 22) {
            return FORMAT_22.parse(strVal);
        }

        if (len == 19) {
            if (strVal.charAt(10) == 'T') {
                return FORMAT_19_ISO.parse(strVal);
            } else {
                return FORMAT_19.parse(strVal);
            }
        }

        if (len == 10) {
            return FORMAT_10.parse(strVal);
        }

        if (len == 29) {
            if (strVal.charAt(26) == ':' && strVal.charAt(28) == '0') {
                return FORMAT_29.parse(strVal);
            }
        }

        if (len == 23) {
            if (strVal.charAt(19) == ',') {
                return FORMAT_23_a.parse(strVal);
            } else {
                return FORMAT_23_b.parse(strVal);
            }
        }

        return null;
    }
}
