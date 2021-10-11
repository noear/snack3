package org.noear.snack.core;

import org.noear.snack.core.exts.Act1;
import org.noear.snack.core.utils.DateUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 参数配置
 * */
public class Options {
    public static final int features_def = Feature.of(
            Feature.OrderedField,
            Feature.WriteDateUseTicks,
            Feature.StringNullAsEmpty,
            Feature.QuoteFieldNames);

    public static final int features_serialize = Feature.of(
            Feature.OrderedField,
            Feature.WriteDateUseTicks,
            Feature.BrowserCompatible,
            Feature.WriteClassName,
            Feature.QuoteFieldNames);
    /**
     * 默认配置
     * */
    public static final Options def() {
        return new Options(features_def);
    }

    /**
     * 序列化配置
     * */
    public static final Options serialize() {
        return new Options(features_serialize);
    }


    //////////////////////////////////////////////

    public Options() {}

    public Options(int features){
        this();
        this.features = features;
    }


    public static Options of(Feature... features) {
        return new Options().add(features);
    }

    //特性
    private int features = DEFAULTS.DEF_FEATURES;

    /**
     * 添加特性
     * */
    public Options add(Feature... features){
        for (Feature f : features) {
            this.features = Feature.config(this.features, f, true);
        }
        return this;
    }

    /**
     * 移除特性
     * */
    public Options remove(Feature... features){
        for (Feature f : features) {
            this.features = Feature.config(this.features, f, false);
        }
        return this;
    }

    @Deprecated
    public Options sub(Feature... features){
        return remove(features);
    }

    /**
     * 检查是否有特性
     */
    public final boolean hasFeature(Feature feature) {
        return Feature.isEnabled(features, feature);
    }


    /**
     * 构建自己
     */
    public Options build(Act1<Options> builder) {
        builder.run(this);
        return this;
    }

    //
    //自定义编码
    //
    private final Map<Class<?>, NodeEncoderEntity> encoderMap = new LinkedHashMap<>();

    /**
     * 获取所有编码器
     * */
    public Collection<NodeEncoderEntity> encoders() {
        return Collections.unmodifiableCollection(encoderMap.values());
    }

    /**
     * 添加编码器
     * */
    public <T> void addEncoder(Class<T> clz, NodeEncoder<T> encoder) {
        encoderMap.put(clz, new NodeEncoderEntity(clz, encoder));
    }

    //
    //自定义解析
    //
    private final Map<Class<?>, NodeDecoderEntity> decoderMap = new LinkedHashMap<>();

    /**
     * 获取所有解码器
     * */
    public Collection<NodeDecoderEntity> decoders() {
        return Collections.unmodifiableCollection(decoderMap.values());
    }

    /**
     * 添加解码器
     * */
    public <T> void addDecoder(Class<T> clz, NodeDecoder<T> decoder) {
        decoderMap.put(clz, new NodeDecoderEntity(clz, decoder));
    }


    //日期格式
    private String dateFormat = DEFAULTS.DEF_DATETIME_FORMAT;

    /**
     * 获取日期格式
     * */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * 设置日期格式
     * */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }



    //类型属性名
    private String typePropertyName = DEFAULTS.DEF_TYPE_PROPERTY_NAME;

    /**
     * 获取类型属性名
     * */
    public String getTypePropertyName() {
        return typePropertyName;
    }

    /**
     * 设置类型属性名
     * */
    public void setTypePropertyName(String typePropertyName) {
        this.typePropertyName = typePropertyName;
    }

    //时区
    private TimeZone timeZone = DEFAULTS.DEF_TIME_ZONE;

    /**
     * 获取时区
     * */
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * 设置时区
     * */
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    //n.get(key)时，只读处理; 即不自动添加新节点
    public boolean get_readonly = false;

    //=================


    public String dateToString(Date date) {
        DateFormat df = new SimpleDateFormat(dateFormat, DEFAULTS.DEF_LOCALE);
        df.setTimeZone(timeZone);

        return df.format(date);
    }

    public Date stringToDate(String date) throws ParseException {
        return DateUtil.parse(date);
    }

    /**
     * null string 默认值
     */
    public final String nullString() {
        if (hasFeature(Feature.StringNullAsEmpty)) {
            return "";
        } else {
            return null;
        }
    }
}
