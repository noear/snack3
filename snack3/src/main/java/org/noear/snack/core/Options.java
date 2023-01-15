package org.noear.snack.core;

import java.util.*;
import java.util.function.Consumer;

/**
 * 参数配置
 *
 * @author noear
 * @since 3.0
 * */
public class Options {
    public static int features_def = Feature.of(
            Feature.OrderedField,
            Feature.WriteDateUseTicks,
            Feature.TransferCompatible,
            //Feature.StringNullAsEmpty,
            Feature.QuoteFieldNames);

    public static int features_serialize = Feature.of(
            Feature.OrderedField,
            Feature.WriteDateUseTicks,
            Feature.BrowserCompatible,
            Feature.WriteClassName,
            Feature.QuoteFieldNames);

    /**
     * 默认配置
     */
    public static final Options def() {
        return new Options(features_def);
    }

    /**
     * 序列化配置
     */
    public static final Options serialize() {
        return new Options(features_serialize);
    }


    //////////////////////////////////////////////

    public Options() {
    }

    public Options(int features) {
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
     */
    public Options add(Feature... features) {
        for (Feature f : features) {
            this.features = Feature.config(this.features, f, true);
        }
        return this;
    }

    /**
     * 移除特性
     */
    public Options remove(Feature... features) {
        for (Feature f : features) {
            this.features = Feature.config(this.features, f, false);
        }
        return this;
    }

    @Deprecated
    public Options sub(Feature... features) {
        return remove(features);
    }

    /**
     * 获取特性码
     * */
    public final int getFeatures() {
        return features;
    }

    /**
     * 重新设置特性码
     * */
    public final void setFeatures(Feature... features) {
        this.features = Feature.of(features);
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
    public Options build(Consumer<Options> custom) {
        custom.accept(this);
        return this;
    }

    //
    //自定义编码
    //
    private final Map<Class<?>, NodeEncoderEntity> encoderMap = new LinkedHashMap<>();

    /**
     * 获取所有编码器
     */
    public Collection<NodeEncoderEntity> encoders() {
        return Collections.unmodifiableCollection(encoderMap.values());
    }

    /**
     * 添加编码器
     */
    public <T> void addEncoder(Class<T> clz, NodeEncoder<T> encoder) {
        encoderMap.put(clz, new NodeEncoderEntity(clz, encoder));
    }

    //
    //自定义解析
    //
    private final Map<Class<?>, NodeDecoderEntity> decoderMap = new LinkedHashMap<>();

    /**
     * 获取所有解码器
     */
    public Collection<NodeDecoderEntity> decoders() {
        return Collections.unmodifiableCollection(decoderMap.values());
    }

    /**
     * 添加解码器
     */
    public <T> void addDecoder(Class<T> clz, NodeDecoder<T> decoder) {
        decoderMap.put(clz, new NodeDecoderEntity(clz, decoder));
    }


    //日期格式
    private String dateFormat = DEFAULTS.DEF_DATETIME_FORMAT;

    /**
     * 获取日期格式
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * 设置日期格式
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }


    //类型属性名
    private String typePropertyName = DEFAULTS.DEF_TYPE_PROPERTY_NAME;

    /**
     * 获取类型属性名
     */
    public String getTypePropertyName() {
        return typePropertyName;
    }

    /**
     * 设置类型属性名
     */
    public void setTypePropertyName(String typePropertyName) {
        this.typePropertyName = typePropertyName;
    }

    //时区
    private TimeZone timeZone = DEFAULTS.DEF_TIME_ZONE;

    /**
     * 获取时区
     */
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * 设置时区
     */
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

}
