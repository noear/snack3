package org.noear.snack.core;

/**
 * 特性
 * */
public enum  Feature {

    /** 为字段名加引号 */
    QuoteFieldNames,

    /** 排序字段 */
    OrderedField,

    /** 序列化时，写入类名。反序列化是需用到 */
    WriteClassName,
    WriteArrayClassName,
    WriteDateUseTicks,
    WriteDateUseFormat,
    WriteBoolUse01,
    /**
     * 对斜杠’/’进行转义
     */
    WriteSlashAsSpecial,

    /*
    * 浏览器安全处理（不输出<>）
    * */
    BrowserSecure,
    /**
     * 浏览器兼容处理（将中文都会序列化为\\uXXXX格式，字节数会多一些）
     */
    BrowserCompatible,

    /**
     *
     * */
    EnumUsingName,

    /*
    * 字符串Null时输出为空(get时用)
    * */
    StringNullAsEmpty,

    /**
     * 字符串了段初始化为家
     * */
    StringFieldInitEmpty,

    ;

    Feature(){
        code = (1 << ordinal());
    }

    /** 特性代码值 */
    public final int code;

    /** 特性启用情况 */
    public static boolean isEnabled(int features, Feature feature) {
        return (features & feature.code) != 0;
    }

    /** 特性配置：开启或禁用 */
    public static int config(int features, Feature feature, boolean enable) {
        if (enable) {
            features |= feature.code;
        } else {
            features &= ~feature.code;
        }

        return features;
    }

    /** 特性合并生成 */
    public static int of(Feature... features) {
        if (features == null) {
            return 0;
        }

        int value = 0;

        for (Feature feature: features) {
            value |= feature.code;
        }

        return value;
    }
}
