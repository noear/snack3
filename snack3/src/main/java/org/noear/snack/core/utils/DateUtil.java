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
    public static final String FORMAT_29 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static final String FORMAT_24_ISO08601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String FORMAT_23_a = "yyyy-MM-dd HH:mm:ss,SSS";
    public static final String FORMAT_23_b = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FORMAT_22 = "yyyyMMddHHmmssSSSZ";//z: +0000
    public static final String FORMAT_19_ISO = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String FORMAT_19 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_10 = "yyyy-MM-dd";

    public static final String FORMAT_8 = "HH:mm:ss";

    public static Date parse(String strVal) throws ParseException {
        final int len = strVal.length();
        String format = null;


        if (len == 29) {
            if (strVal.charAt(26) == ':' && strVal.charAt(28) == '0') {
                format = FORMAT_29;
            }
        } else if (len == 24) {
            if (strVal.charAt(10) == 'T') {
                format = FORMAT_24_ISO08601;
            }
        } else if (len == 23) {
            if (strVal.charAt(19) == ',') {
                format = FORMAT_23_a;
            } else {
                format = FORMAT_23_b;
            }
        } else if (len == 22) {
            format = FORMAT_22;
        } else if (len == 19) {
            if (strVal.charAt(10) == 'T') {
                format = FORMAT_19_ISO;
            } else {
                format = FORMAT_19;
            }
        } else if (len == 10) {
            format = FORMAT_10;
        } else if (len == 8) {
            format = FORMAT_8;
        }

        if (format != null) {
            DateFormat df = new SimpleDateFormat(format, DEFAULTS.DEF_LOCALE);
            df.setTimeZone(DEFAULTS.DEF_TIME_ZONE);
            return df.parse(strVal);
        } else {
            return null;
        }
    }
}