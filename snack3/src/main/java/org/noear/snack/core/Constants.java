package org.noear.snack.core;

import org.noear.snack.core.exts.Act1;
import org.noear.snack.core.utils.DateUtil;
import org.noear.snack.from.encoder.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

    public Constants() {
        addEncoder(Boolean.class, BooleanEncoder.instance);
        addEncoder(Date.class, DateEncoder.instance);
        addEncoder(LocalDateTime.class, LocalDateTimeEncoder.instance);
        addEncoder(LocalDate.class, LocalDateEncoder.instance);
        addEncoder(LocalTime.class, LocalTimeEncoder.instance);
        addEncoder(Number.class, NumberEncoder.instance);
        addEncoder(String.class, StringEncoder.instance);
    }

    public Constants(int features){
        this();
        this.features = features;
    }


    /**
     * 构建自己
     */
    public Constants build(Act1<Constants> builder) {
        builder.run(this);
        return this;
    }

    private final Map<Class<?>,NodeEncoder> encoderMap = new HashMap<>();

    public Map<Class<?>,NodeEncoder> encoderMap(){
        return Collections.unmodifiableMap(encoderMap);
    }

    public <T> void addEncoder(Class<T> clz, NodeEncoder<T> encoder){
        encoderMap.put(clz, encoder);
    }

    //日期格式
    public String date_format = DEFAULTS.DEF_DATETIME_FORMAT;
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


    public String dateToString(Date date) {
        DateFormat df = new SimpleDateFormat(date_format, DEFAULTS.DEF_LOCALE);
        df.setTimeZone(time_zone);

        return df.format(date);
    }

    public Date stringToDate(String date) throws ParseException {
        return DateUtil.parse(date);
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
