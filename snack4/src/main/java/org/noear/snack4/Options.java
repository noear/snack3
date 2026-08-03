/*
 * Copyright 2005-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.snack4;

import org.noear.snack4.codec.*;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * JSON 处理选项（线程安全配置）
 *
 * @author noear 2019/2/12 created
 * @since 4.0
 */
public final class Options {
    //默认类型的key
    public static final String DEF_TYPE_PROPERTY_NAME = "@type";
    //默认时区
    public static final ZoneId DEF_ZONE = ZoneId.systemDefault();
    public static final TimeZone DEF_TIME_ZONE = TimeZone.getDefault();
    //默认偏移时区
    public static final ZoneOffset DEF_OFFSET = OffsetDateTime.now().getOffset();
    //默认地区
    public static final Locale DEF_LOCALE = Locale.getDefault();
    //默认时间格式器
    public static final String DEF_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    //默认特性
    public static final int DEF_FEATURES = 0;

    //默认选项（私有）
    public static final Options DEF_OPTIONS = new Options(true);
    public static final String DEF_UNSUPPORTED_HINT = "Read-only mode does not support modification.";

    //编码仓库
    private final CodecLib codecLib = CodecLib.newInstance();
    //特性开关（使用位掩码存储）
    private long featuresValue = DEF_FEATURES;
    //时间格式
    private String dateFormat = DEF_DATETIME_FORMAT;
    //书写缩进
    private String writeIndent = "  ";
    //类型属性名
    private String typePropertyName = DEF_TYPE_PROPERTY_NAME;
    //类加载器
    private ClassLoader classLoader;
    //允许安全类
    private Locale locale = DEF_LOCALE;
    //时区
    private ZoneId zoneId = DEF_ZONE;
    private TimeZone timeZone = DEF_TIME_ZONE;

    private Supplier<Map> mapFactory = LinkedHashMap::new;
    private Supplier<List> listFactory = ArrayList::new;

    //JSON 嵌套深度上限
    private int maxNestingDepth = 1000;


    private boolean readonly;

    private Options(boolean readonly) {
        this.readonly = readonly;
    }

    /**
     * 转为只读
     */
    public Options readonly(){
        this.readonly = true;
        return this;
    }

    /**
     * 克隆一个新的选项（非只读）
     */
    public Options copy() {
        Options tmp = new Options(false); // 内部执行了 codecLib = CodecLib.newInstance()

        // 复制基础属性
        tmp.featuresValue = this.featuresValue;
        tmp.dateFormat = this.dateFormat;
        tmp.writeIndent = this.writeIndent;
        tmp.typePropertyName = this.typePropertyName;
        tmp.classLoader = this.classLoader;
        tmp.locale = this.locale;
        tmp.zoneId = this.zoneId;
        tmp.timeZone = this.timeZone;
        tmp.mapFactory = this.mapFactory;
        tmp.listFactory = this.listFactory;
        tmp.maxNestingDepth = this.maxNestingDepth;

        // 将旧 options 的特有编解码器填充到新 options 的 codecLib 中
        tmp.codecLib.fill(this.codecLib);

        return tmp;
    }

    /**
     * 用于链式构建
     */
    public Options then(Consumer<Options> consumer) {
        consumer.accept(this);
        return this;
    }

    /**
     * 加载类
     */
    public Class<?> loadClass(String className) throws SnackException{
        return loadClass(className, false);
    }

    /**
     * 加载类
     */
    public Class<?> loadClass(String className, boolean disallowedThrow) {
        try {
            if (classLoader == null) {
                return Class.forName(className);
            } else {
                return classLoader.loadClass(className);
            }
        } catch (ClassNotFoundException e) {
            if (disallowedThrow) {
                return null;
            } else {
                throw new SnackException("Failed to load class: " + className, e);
            }
        }
    }

    /**
     * 是否启用指定特性
     */
    public boolean hasFeature(Feature feature) {
        return Feature.hasFeature(this.featuresValue, feature);
    }

    public long getFeatures() {
        return featuresValue;
    }

    public Locale getLocale() {
        return locale;
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    /**
     * @deprecated 4.0
     *
     */
    @Deprecated
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * 获取日期格式
     */
    public String getDateFormat() {
        return dateFormat;
    }

    public String getTypePropertyName() {
        return typePropertyName;
    }

    /**
     * 获取解码器
     */
    public ObjectDecoder<?> getDecoder(Class<?> clazz) {
        return codecLib.getDecoder(clazz);
    }

    /**
     * 获取编码器
     */
    public ObjectEncoder<?> getEncoder(Object value) {
        return codecLib.getEncoder(value);
    }

    /**
     * 获取创建器
     */
    public ObjectCreator<?> getCreator(Class<?> clazz) {
        return codecLib.getCreator(clazz);
    }

    /**
     * 获取缩进字符串
     */
    public String getWriteIndent() {
        return writeIndent;
    }

    /**
     * 获取 JSON 嵌套深度上限
     */
    public int getMaxNestingDepth() {
        return maxNestingDepth;
    }

    public <T> Map<String, T> createMap() {
        return mapFactory.get();
    }

    public <T> List<T> createList() {
        return listFactory.get();
    }


    /// /////////////

    /**
     * 设置 JSON 嵌套深度上限
     */
    public Options maxNestingDepth(int maxNestingDepth) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        this.maxNestingDepth = maxNestingDepth;
        return this;
    }

    /**
     * 设置日期格式
     */
    public Options dateFormat(String format) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        this.dateFormat = format;
        return this;
    }

    /**
     * 设置地区
     */
    public Options locale(Locale locale) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        this.locale = locale;
        return this;
    }

    /**
     * 设置时区
     */
    public Options timeZone(TimeZone timeZone) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        this.zoneId = timeZone.toZoneId();
        this.timeZone = timeZone;
        return this;
    }

    /**
     * 设置时区
     */
    public Options timeZone(ZoneId zoneId) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        this.zoneId = zoneId;
        this.timeZone = TimeZone.getTimeZone(zoneId);
        return this;
    }

    /**
     * 设置缩进字符串
     */
    public Options writeIndent(String indent) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        this.writeIndent = indent;
        return this;
    }

    /**
     * 设置类加载器
     */
    public Options classLoader(ClassLoader classLoader) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        this.classLoader = classLoader;
        return this;
    }

    /**
     * 添加特性
     */
    public Options addFeatures(Feature... features) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        this.featuresValue = Feature.addFeatures(this.featuresValue, features);
        return this;
    }

    public Options setFeatures(Feature... features) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        this.featuresValue = Feature.addFeatures(0L, features);
        return this;
    }

    /**
     * 移除特性
     */
    public Options removeFeatures(Feature... features) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        this.featuresValue = Feature.removeFeatures(this.featuresValue, features);
        return this;
    }

    /**
     * 注册自定义解码器
     */
    public <T> Options addDecoder(Class<T> type, ObjectDecoder<T> decoder) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        codecLib.addDecoder(type, decoder);
        return this;
    }

    /**
     * 注册自定义解码器
     */
    public <T> Options addDecoder(ObjectPatternDecoder<T> decoder) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        codecLib.addDecoder(decoder);
        return this;
    }

    /**
     * 注册自定义编码器
     */
    public <T> Options addEncoder(Class<T> type, ObjectEncoder<T> encoder) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        codecLib.addEncoder(type, encoder);
        return this;
    }

    /**
     * 注册自定义编码器
     */
    public <T> Options addEncoder(ObjectPatternEncoder<T> encoder) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        codecLib.addEncoder(encoder);
        return this;
    }

    /**
     * 注册自定义创建器
     */
    public <T> Options addCreator(Class<T> type, ObjectCreator<T> creator) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        codecLib.addCreator(type, creator);
        return this;
    }

    /**
     * 注册自定义创建器
     */
    public <T> Options addCreator(ObjectPatternCreator<T> creator) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        codecLib.addCreator(creator);
        return this;
    }

    //////////////////
    // 类型安全检测（双层配置：全局 + 实例）
    //////////////////

    /**
     * 添加类型安全检测器（对当前实例有效）
     *
     * @param checker 检测器（{@link TypeBlacklist} 或自定义策略）
     */
    public Options addTypeChecker(TypeChecker checker) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        if (checker != null) {
            codecLib.addChecker(checker);
        }
        return this;
    }

    /**
     * 判断类名是否应被拦截（供解码器在动态类加载前调用）
     *
     * @param className 完整类名
     * @return true 表示拦截（拒绝加载），false 表示放行
     */
    public boolean isTypeBlocked(String className) {
        // 协议注入防护（含 ":" 或 "!" 的类名一律拒绝，不可关闭）
        if (className == null || className.contains(":") || className.contains("!")) {
            return true;
        }

        return codecLib.isTypeBlocked(className);
    }

    public Options mapFactory(Supplier<Map> mapFactory) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        this.mapFactory = mapFactory;
        return this;
    }

    public Options listFactory(Supplier<List> listFactory) {
        if (readonly) {
            throw new UnsupportedOperationException(DEF_UNSUPPORTED_HINT);
        }

        this.listFactory = listFactory;
        return this;
    }

    public static Options of(Feature... features) {
        Options tmp = new Options(false);
        for (Feature f : features) {
            tmp.addFeatures(f);
        }
        return tmp;
    }
}