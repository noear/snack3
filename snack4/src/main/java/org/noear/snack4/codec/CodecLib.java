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
package org.noear.snack4.codec;

import org.noear.snack4.codec.decode.*;
import org.noear.snack4.codec.encode.*;
import org.noear.snack4.codec.create.*;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * 编解码库
 *
 * @author noear 2025/10/3 created
 * @since 4.0
 */
public class CodecLib {
    private static final CodecLib DEFAULT = new CodecLib(null).loadDefault();

    private final Map<Class<?>, ObjectCreator<?>> creators = new HashMap<>();
    private final Map<Class<?>, ObjectPatternCreator<?>> patternCreators = new LinkedHashMap<>();

    private final Map<Class<?>, ObjectDecoder<?>> decoders = new HashMap<>();
    private final Map<Class<?>, ObjectPatternDecoder<?>> patternDecoders = new LinkedHashMap<>();

    private final Map<Class<?>, ObjectEncoder<?>> encoders = new HashMap<>();
    private final Map<Class<?>, ObjectPatternEncoder<?>> patternEncoders = new LinkedHashMap<>();

    private final List<TypeChecker> checkers = new ArrayList<>();

    private final CodecLib parent;

    private CodecLib(CodecLib parent) {
        this.parent = parent;
    }

    public static CodecLib newInstance() {
        return new CodecLib(DEFAULT);
    }

    public void fill(CodecLib source) {
        // 填充当前实例特有的配置（不影响 parent）
        this.checkers.addAll(source.checkers);

        this.creators.putAll(source.creators);
        this.patternCreators.putAll(source.patternCreators);

        this.decoders.putAll(source.decoders);
        this.patternDecoders.putAll(source.patternDecoders);

        this.encoders.putAll(source.encoders);
        this.patternEncoders.putAll(source.patternEncoders);
    }

    /**
     * 添加检测器
     */
    public void addChecker(TypeChecker checker) {
        checkers.add(checker);
    }

    /**
     * 添加创建器
     */
    public <T> void addCreator(Class<T> type, ObjectCreator<T> creator) {
        creators.put(type, creator);
    }

    /**
     * 添加创建器
     */
    public void addCreator(ObjectPatternCreator creator) {
        patternCreators.put(creator.getClass(), creator);
    }

    /**
     * 添加解码器
     */
    public void addDecoder(ObjectPatternDecoder decoder) {
        patternDecoders.put(decoder.getClass(), decoder);
    }

    /**
     * 添加解码器
     */
    public <T> void addDecoder(Class<T> type, ObjectDecoder<T> decoder) {
        if (decoder instanceof ObjectPatternDecoder<?>) {
            patternDecoders.put(decoder.getClass(), (ObjectPatternDecoder<?>) decoder);
        }

        decoders.put(type, decoder);
    }

    /**
     * 添加编码器
     */
    public void addEncoder(ObjectPatternEncoder encoder) {
        patternEncoders.put(encoder.getClass(), encoder);
    }

    /**
     * 添加编码器
     */
    public <T> void addEncoder(Class<T> type, ObjectEncoder<T> encoder) {
        if (encoder instanceof ObjectPatternEncoder) {
            patternEncoders.put(encoder.getClass(), (ObjectPatternEncoder<T>) encoder);
        }

        encoders.put(type, encoder);
    }

    //------------

    public boolean isTypeBlocked(String className) {
        if (className == null) {
            return true; //拒绝
        }

        for (TypeChecker c : checkers) {
            TypeChecker.Result rst = c.check(className);
            if (TypeChecker.DENY == rst) {
                return true; //拒绝
            } else if (TypeChecker.ALLOW == rst) {
                return false; //允许
            }
        }

        if (parent != null) {
            return parent.isTypeBlocked(className);
        }

        // 默认放行（由 AutoType 开关兜底）
        return false;
    }

    public ObjectCreator getCreator(Class<?> clazz) {
        ObjectCreator tmp = creators.get(clazz);

        if (tmp == null) {
            for (ObjectPatternCreator<?> creator1 : patternCreators.values()) {
                if (creator1.calCreate(clazz)) {
                    return creator1;
                }
            }

            if (parent != null) {
                return parent.getCreator(clazz);
            }
        }

        return tmp;
    }

    public ObjectDecoder getDecoder(Class<?> clazz) {
        ObjectDecoder tmp = decoders.get(clazz);

        if (tmp == null) {
            for (ObjectPatternDecoder decoder1 : patternDecoders.values()) {
                if (decoder1.canDecode(clazz)) {
                    return decoder1;
                }
            }

            if (parent != null) {
                return parent.getDecoder(clazz);
            }
        }

        return tmp;
    }

    public ObjectEncoder getEncoder(Object value) {
        ObjectEncoder encoder = encoders.get(value.getClass());

        if (encoder == null) {
            for (ObjectPatternEncoder encoder1 : patternEncoders.values()) {
                if (encoder1.canEncode(value)) {
                    return encoder1;
                }
            }

            if (parent != null) {
                return parent.getEncoder(value);
            }
        }

        return encoder;
    }

    /// //////////////////////

    private void loadDefaultCheckers() {
        addChecker(TypeSafelist.GLOBAL);
    }

    private void loadDefaultCreators() {
        addCreator(new _ThrowablePatternCreator());

        addCreator(HashMap.class, ((opts, node, clazz) -> new HashMap()));
        addCreator(LinkedHashMap.class, ((opts, node, clazz) -> new LinkedHashMap()));
        addCreator(Map.class, ((opts, node, clazz) -> new LinkedHashMap()));

        addCreator(ArrayList.class, ((opts, node, clazz) -> new ArrayList()));
        addCreator(Collection.class, ((opts, node, clazz) -> new ArrayList()));
        addCreator(List.class, ((opts, node, clazz) -> new ArrayList()));

        addCreator(Set.class, ((opts, node, clazz) -> new HashSet()));
        addCreator(HashSet.class, ((opts, node, clazz) -> new HashSet()));
    }

    private void loadDefaultDecoders() {
        addDecoder(new _ArrayPatternDecoder());
        addDecoder(new _EnumPatternDecoder());
        addDecoder(new _PropertiesPatternDecoder());

        addDecoder(Optional.class, new OptionalDecoder());

        addDecoder(Charset.class, new _CharsetPatternDecoder());
        addDecoder(TimeZone.class, new _TimeZonePatternDecoder());
        addDecoder(Currency.class, new _CurrencytPatternDecoder());
        addDecoder(Path.class, new _PathPatternDecoder());
        addDecoder(Duration.class, new DurationDecoder());
        addDecoder(Instant.class, (c, o) -> Instant.parse(o.getString()));
        addDecoder(Period.class, (c, o) -> Period.parse(o.getString()));
        addDecoder(Year.class, (c, o) -> Year.parse(o.getString()));
        addDecoder(YearMonth.class, (c, o) -> YearMonth.parse(o.getString()));

        addDecoder(StackTraceElement.class, new StackTraceElementDecoder());
        addDecoder(InetSocketAddress.class, new InetSocketAddressDecoder());
        addDecoder(SimpleDateFormat.class, new SimpleDateFormatDecoder());
        addDecoder(File.class, new FileDecoder());
        addDecoder(Class.class, new ClassDecoder());

        addDecoder(URL.class, new URLDecoder());

        addDecoder(Date.class, new DateDecoder());

        addDecoder(LongAdder.class, new LongAdderDecoder());
        addDecoder(DoubleAdder.class, new DoubleAdderDecoder());

        addDecoder(AtomicBoolean.class, new AtomicBooleanDecoder());
        addDecoder(AtomicLong.class, new AtomicLongDecoder());
        addDecoder(AtomicInteger.class, new AtomicIntegerDecoder());

        addDecoder(LocalTime.class, new LocalTimeDecoder());
        addDecoder(LocalDateTime.class, new LocalDateTimeDecoder());
        addDecoder(LocalDate.class, new LocalDateDecoder());

        addDecoder(OffsetDateTime.class, new OffsetDateTimeDecoder());
        addDecoder(OffsetTime.class, new OffsetTimeDecoder());

        addDecoder(ZonedDateTime.class, new ZonedDateTimeDecoder());

        addDecoder(BigDecimal.class, new BigDecimalDecoder());
        addDecoder(BigInteger.class, new BigIntegerDecoder());

        addDecoder(URI.class, (c, o) -> URI.create(o.getString()));
        addDecoder(UUID.class, (c, o) -> UUID.fromString(o.getString()));

        addDecoder(java.sql.Date.class, (c, o) -> new java.sql.Date(o.getLong()));
        addDecoder(java.sql.Time.class, (c, o) -> new java.sql.Time(o.getLong()));
        addDecoder(java.sql.Timestamp.class, (c, o) -> new java.sql.Timestamp(o.getLong()));

        addDecoder(String.class, (c, o) -> o.getString());

        addDecoder(Boolean.class, (c, o) -> o.getBoolean(null));
        addDecoder(Boolean.TYPE, (c, o) -> o.getBoolean(false));

        addDecoder(Double.class, (c, o) -> o.getDouble(null));
        addDecoder(Double.TYPE, (c, o) -> o.getDouble(0D));

        addDecoder(Float.class, (c, o) -> o.getFloat(null));
        addDecoder(Float.TYPE, (c, o) -> o.getFloat(0F));

        addDecoder(Long.class, (c, o) -> o.getLong(null));
        addDecoder(Long.TYPE, (c, o) -> o.getLong(0L));

        addDecoder(Integer.class, (c, o) -> o.getInt(null));
        addDecoder(Integer.TYPE, (c, o) -> o.getInt(0));

        addDecoder(Short.class, (c, o) -> o.getShort(null));
        addDecoder(Short.TYPE, (c, o) -> o.getShort((short) 0));

        addDecoder(Byte.class, (c, o) -> o.getByte(null));
        addDecoder(Byte.TYPE, (c, o) -> o.getByte((byte) 0));
    }


    private void loadDefaultEncoders() {
        addEncoder(new _EnumPatternEncoder());
        addEncoder(new _PropertiesPatternEncoder());

        addEncoder(Optional.class, new OptionalEncoder());

        addEncoder(Charset.class, new _CharsetPatternEncoder());
        addEncoder(Date.class, new _DatePatternEncoder());
        addEncoder(Number.class, new _NumberPatternEncoder());
        addEncoder(Calendar.class, new _CalendarPatternEncoder());
        addEncoder(Clob.class, new _ClobPatternEncoder());
        addEncoder(TimeZone.class, new _TimeZonePatternEncoder());
        addEncoder(Currency.class, new _CurrencyPatternEncoder());
        addEncoder(Path.class, new _PathPatternEncoder());
        addEncoder(Duration.class, new DurationEncoder());
        addEncoder(Instant.class, (c, v, t) -> t.setValue(v.toString()));
        addEncoder(Period.class, (c, v, t) -> t.setValue(v.toString()));
        addEncoder(Year.class, (c, v, t) -> t.setValue(v.toString()));
        addEncoder(YearMonth.class, (c, v, t) -> t.setValue(v.toString()));

        addEncoder(KeyValueList.class, new KeyValueListEncoder());
        addEncoder(StackTraceElement.class, new StackTraceElementEncoder());
        addEncoder(InetSocketAddress.class, new InetSocketAddressEncoder());
        addEncoder(SimpleDateFormat.class, new SimpleDateFormatEncoder());

        addEncoder(String.class, new StringEncoder());

        addEncoder(LocalDateTime.class, new LocalDateTimeEncoder());
        addEncoder(LocalDate.class, new LocalDateEncoder());
        addEncoder(LocalTime.class, new LocalTimeEncoder());

        addEncoder(OffsetDateTime.class, new OffsetDateTimeEncoder());
        addEncoder(OffsetTime.class, new OffsetTimeEncoder());

        addEncoder(ZonedDateTime.class, new ZonedDateTimeEncoder());

        addEncoder(Boolean.class, new BooleanEncoder());
        addEncoder(Boolean.TYPE, new BooleanEncoder());

        addEncoder(File.class, (c, v, t) -> t.setValue(v.getPath()));

        addEncoder(LongAdder.class, (c, v, t) -> t.setValue(v.longValue()));
        addEncoder(DoubleAdder.class, (c, v, t) -> t.setValue(v.doubleValue()));
        addEncoder(AtomicBoolean.class, (c, v, t) -> t.setValue(v.get()));

        addEncoder(URL.class, (c, v, t) -> t.setValue(v.toString()));
        addEncoder(URI.class, (c, v, t) -> t.setValue(v.toString()));
        addEncoder(Class.class, (c, v, t) -> t.setValue(v.getName()));

        addEncoder(UUID.class, (ctx, value, target) -> target.setValue(value.toString()));
    }

    private CodecLib loadDefault() {
        loadDefaultCheckers();
        loadDefaultCreators();
        loadDefaultDecoders();
        loadDefaultEncoders();
        return this;
    }
}