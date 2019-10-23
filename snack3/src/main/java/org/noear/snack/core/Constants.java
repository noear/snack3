package org.noear.snack.core;

import org.noear.snack.core.exts.Act1;
import org.noear.snack.from.Fromer;
import org.noear.snack.to.Toer;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 参数配置
 * */
public class Constants {
    /** 默认配置 */
    public static final Constants def = of(
            Feature.WriteDateUseTicks).build(c->{
                c.null_string = "";
    });

    public static final Constants serialize = of(
            Feature.OrderedField,
            Feature.BrowserCompatible,
            Feature.WriteClassName).build(c-> {
                c.null_string = null;
    });

    public static Constants of(Feature... features) {
        Constants l = new Constants();

        for (Feature f : features) {
            l.features = Feature.config(l.features, f, true);
        }

        return l;
    }

    public Constants build(Act1<Constants> builder){
        builder.run(this);
        return this;
    }



    private SimpleDateFormat _date_format;

    public String   null_string = DEFAULTS.DEF_NULL_STRING; //默府null字符串

    public String   date_format = DEFAULTS.DEF_DATE_FORMAT; //日期格式
    public String   type_key    = DEFAULTS.DEF_TYPE_KEY;    //类型key
    public TimeZone time_zone   = DEFAULTS.DEF_TIME_ZONE;   //时区
    public Locale   locale      = DEFAULTS.DEF_LOCALE;      //地区
    public int      features    = DEFAULTS.DEF_FEATURES;    //特性

    //=================


    /** 字符来源器 */
    public Fromer stringFromer = DEFAULTS.DEF_JSON_FROMER;
    /** 字符去处器 */
    public Toer stringToer = DEFAULTS.DEF_JSON_TOER;
    /** 对象来源器 */
    public Fromer objectFromer  = DEFAULTS.DEF_OBJECT_FROMER;
    /** 对象去处器 */
    public Toer objectToer      = DEFAULTS.DEF_OBJECT_TOER;


    public Constants(){
        initialize();
    }

    protected void initialize(){
        _date_format = new SimpleDateFormat(date_format, locale);
        features = Feature.config(features, Feature.QuoteFieldNames,true);
    }

    public final String dateToString(Date date){
        return _date_format.format(date);
    }

    public final boolean hasFeature(Feature feature) {
        return Feature.isEnabled(features, feature);
    }
}
