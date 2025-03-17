package org.noear.snack.core;

import org.noear.snack.ONode;
import org.noear.snack.schema.SchemaValidator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * JSON 处理选项（线程安全配置）
 */
public final class Options {
    /**
     * 默认选项实例
     */
    private static final Options DEFAULT = new Builder().build();

    // 特性开关（使用位掩码存储）
    private final int enabledFeatures;

    // 通用配置
    private final DateFormat _dateFormat;
    private final Map<Class<?>, Codec<?>> _codecRegistry;
    private final SchemaValidator _schemaValidator;

    // 输入配置
    private final int _maxDepth;

    // 输出配置
    private final String _indent;


    private Set<Class<?>> allowedClasses = new HashSet<>();

    public void allowClass(Class<?> clazz) {
        allowedClasses.add(clazz);
    }

    private Options(Builder builder) {
        // 合并特性开关
        int features = 0;
        for (Feature feat : Feature.values()) {
            if (builder.features.getOrDefault(feat, feat.enabledByDefault())) {
                features |= feat.mask();
            }
        }
        this.enabledFeatures = features;

        // 通用配置
        this._dateFormat = builder.dateFormat;
        this._codecRegistry = Collections.unmodifiableMap(builder.codecRegistry);
        this._schemaValidator = builder.schemaValidator;

        // 输入配置
        this._maxDepth = builder.maxDepth;

        // 输出配置
        this._indent = builder.indent;
    }

    /**
     * 是否启用指定特性
     */
    public boolean isFeatureEnabled(Feature feature) {
        return (enabledFeatures & feature.mask()) != 0;
    }

    /**
     * 获取日期格式
     */
    public DateFormat getDateFormat() {
        return _dateFormat;
    }

    /**
     * 获取自定义编解码器注册表
     */
    public Map<Class<?>, Codec<?>> getCodecRegistry() {
        return _codecRegistry;
    }

    /**
     * 获取验证器
     */
    public SchemaValidator getSchemaValidator() {
        return _schemaValidator;
    }

    /**
     * 获取最大解析深度
     */
    public int getMaxDepth() {
        return _maxDepth;
    }

    /**
     * 获取缩进字符串
     */
    public String getIndent() {
        return _indent;
    }

    /**
     * 获取默认选项
     */
    public static Options def() {
        return DEFAULT;
    }

    public static Options of(Feature... features) {
        Builder tmp = new Builder();
        for (Feature f : features) {
            tmp.enable(f);
        }
        return tmp.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 选项建造者
     */
    public static class Builder {
        private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 特性开关存储
        private final EnumMap<Feature, Boolean> features = new EnumMap<>(Feature.class);

        // 通用配置
        private DateFormat dateFormat = DEFAULT_DATE_FORMAT;
        private final Map<Class<?>, Codec<?>> codecRegistry = new HashMap<>();
        private SchemaValidator schemaValidator;

        // 输入配置
        private int maxDepth = 512;

        // 输出配置
        private String indent = "  ";

        public Builder() {
            // 初始化默认特性
            for (Feature feat : Feature.values()) {
                features.put(feat, feat.enabledByDefault());
            }
        }

        /**
         * 启用/禁用指定特性
         */
        public Builder enable(Feature feature) {
            return enable(feature, true);
        }

        public Builder disable(Feature feature) {
            return enable(feature, false);
        }

        public Builder enable(Feature feature, boolean state) {
            features.put(feature, state);
            return this;
        }

        /**
         * 设置日期格式
         */
        public Builder dateFormat(DateFormat format) {
            this.dateFormat = format;
            return this;
        }

        /**
         * 注册自定义编解码器
         */
        public <T> Builder addCodec(Class<T> type, Codec<T> codec) {
            codecRegistry.put(type, codec);
            return this;
        }

        /**
         * 设置验证器
         */
        public Builder schema(ONode schema) {
            this.schemaValidator = new SchemaValidator(schema);
            return this;
        }

        /**
         * 设置最大解析深度
         */
        public Builder maxDepth(int depth) {
            this.maxDepth = depth;
            return this;
        }

        /**
         * 设置缩进字符串
         */
        public Builder indent(String indent) {
            this.indent = indent;
            return this;
        }

        /**
         * 构建最终选项
         */
        public Options build() {
            return new Options(this);
        }
    }
}