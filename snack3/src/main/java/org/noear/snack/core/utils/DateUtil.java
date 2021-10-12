package org.noear.snack.core.utils;

import org.noear.snack.core.DEFAULTS;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期操作工具
 *
 * @author noear
 * @since 3.1
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

    /**
     * 解析时间
     * */
    public static Date parse(String val) throws ParseException {
        final int len = val.length();
        String ft = null;


        if (len == 29) {
            if (val.charAt(26) == ':' && val.charAt(28) == '0') {
                ft = FORMAT_29;
            }
        } else if (len == 24) {
            if (val.charAt(10) == 'T') {
                ft = FORMAT_24_ISO08601;
            }
        } else if (len == 23) {
            if (val.charAt(19) == ',') {
                ft = FORMAT_23_a;
            } else {
                ft = FORMAT_23_b;
            }
        } else if (len == 22) {
            ft = FORMAT_22;
        } else if (len == 19) {
            if (val.charAt(10) == 'T') {
                ft = FORMAT_19_ISO;
            } else {
                ft = FORMAT_19;
            }
        } else if (len == 10) {
            ft = FORMAT_10;
        } else if (len == 8) {
            ft = FORMAT_8;
        }

        if (ft != null) {
            DateFormat df = new SimpleDateFormat(ft, DEFAULTS.DEF_LOCALE);
            df.setTimeZone(DEFAULTS.DEF_TIME_ZONE);
            return df.parse(val);
        } else {
            return null;
        }
    }


    /**
     * 格式化时间
     * */
    public static String format(Date date, String dateFormat) {
        return format(date, dateFormat, null);
    }

    /**
     * 格式化时间
     * */
    public static String format(Date date, String dateFormat, TimeZone timeZone) {
        DateFormat df = new SimpleDateFormat(dateFormat, DEFAULTS.DEF_LOCALE);
        if (timeZone != null) {
            df.setTimeZone(timeZone);
        }

        return df.format(date);
    }
}