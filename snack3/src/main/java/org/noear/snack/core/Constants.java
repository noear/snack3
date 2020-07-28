package org.noear.snack.core;

import org.noear.snack.core.exts.Act1;

import java.text.DateFormat;
import java.util.*;

/**
 * 参数配置
 * */
public class Constants {
    public static int features_def = Feature.of(
            Feature.OrderedField,
            Feature.WriteDateUseTicks,
            Feature.StringNullAsEmpty,
            Feature.QuoteFieldNames);

    public static int features_serialize = Feature.of(
            Feature.OrderedField,
            Feature.WriteDateUseTicks,
            Feature.BrowserCompatible,
            Feature.WriteClassName,
            Feature.QuoteFieldNames);
    /**
     * 默认配置
     * */
    public static final Constants def() {
        return new Constants(features_def);
    }

    /**
     * 序列化配置
     * */
    public static final Constants serialize() {
        return new Constants(features_serialize);
    }

    public static Constants of(Feature... features) {
        return new Constants().add(features);
    }

    public Constants add(Feature... features){
        for (Feature f : features) {
            this.features = Feature.config(this.features, f, true);
        }
        return this;
    }

    public Constants sub(Feature... features){
        for (Feature f : features) {
            this.features = Feature.config(this.features, f, false);
        }
        return this;
    }

    public Constants(){}
    public Constants(int features){
        this.features = features;
    }


    /**
     * 构建自己
     */
    public Constants build(Act1<Constants> builder) {
        builder.run(this);
        return this;
    }

    //日期格式
    public DateFormat date_format = DEFAULTS.DEF_DATE_FORMAT;
    //类型key
    public String type_key = DEFAULTS.DEF_TYPE_KEY;
    //时区
    public TimeZone time_zone = DEFAULTS.DEF_TIME_ZONE;
    //地区
    public Locale locale = DEFAULTS.DEF_LOCALE;
    //特性
    public int features = DEFAULTS.DEF_FEATURES;
    //n.get(key)时，只读处理; 即不自动添加新节点
    public boolean get_readonly = false;

    //=================


    public final String dateToString(Date date) {
        return date_format.format(date);
    }

    /**
     * 检查是否有特性
     */
    public final boolean hasFeature(Feature feature) {
        return Feature.isEnabled(features, feature);
    }

    /**
     * null string 默认值
     */
    public final String null_string() {
        if (hasFeature(Feature.StringNullAsEmpty)) {
            return "";
        } else {
            return null;
        }
    }
}
