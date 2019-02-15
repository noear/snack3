package org.noear.snack.core;

import org.noear.snack.from.Fromer;
import org.noear.snack.from.JsonFromer;
import org.noear.snack.from.ObjectFromer;
import org.noear.snack.to.JsonToer;
import org.noear.snack.to.DataToer;
import org.noear.snack.to.ObjectToer;
import org.noear.snack.to.Toer;

import java.util.Locale;
import java.util.TimeZone;

/**
 * 默认值
 * */
public class DEFAULTS {
    /** 默认时间格式 */
    public static final String DEF_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /** 默认类型的key */
    public static final String DEF_TYPE_KEY = "@type";
    /** 默认时区 */
    public static final TimeZone  DEF_TIME_ZONE = TimeZone.getDefault();
    /** 默认地区 */
    public static final Locale    DEF_LOCALE    = Locale.getDefault();
    /** 默认特性 */
    public static final int DEF_FEATURES = Feature.QuoteFieldNames.code;

    /** 默认对象来源器 */
    public static final Fromer DEF_OBJECT_FROMER = new ObjectFromer();
    /** 默认对象去处器 */
    public static final Toer DEF_OBJECT_TOER   = new ObjectToer();
    /** 默认数据去处器 */
    public static final Toer DEF_DATA_TOER   = new DataToer();

    /** 默认JSON来源器 */
    public static final Fromer DEF_JSON_FROMER = new JsonFromer();
    /** 默认JSON去处器 */
    public static final Toer DEF_JSON_TOER = new JsonToer();


}
