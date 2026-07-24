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
package org.noear.snack4.codec.util;

import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.codec.DecodeContext;
import org.noear.snack4.codec.CodecException;
import org.noear.snack4.util.Asserts;

import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 时间解析工具
 * 
 * @author noear 2021/6/13 created
 * @since 4.0
 */
public class DateUtil {
    private static final ZoneId SYSTEM_ZONE = Options.DEF_ZONE;
    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();
    private static final Map<Integer, List<DateTimeFormatter>> LENGTH_BUCKETS = new HashMap<>();
    private static final List<DateTimeFormatter> VARIABLE_FORMATTERS = new ArrayList<>();

    private static final DateTimeFormatter[] COMMON_FORMATTERS = {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME, DateTimeFormatter.ISO_DATE_TIME,
            DateTimeFormatter.ISO_INSTANT, DateTimeFormatter.ISO_ZONED_DATE_TIME,
            DateTimeFormatter.ISO_OFFSET_DATE_TIME, DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ISO_DATE, DateTimeFormatter.ISO_LOCAL_TIME,
            DateTimeFormatter.ISO_TIME, DateTimeFormatter.RFC_1123_DATE_TIME
    };

    private static final String[] PATTERNS = {
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss",

            "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyy-MM-dd HH:mm:ss,SSS",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy.MM.dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.S",
            "yyyy-MM-dd HH:mm",
            "yyyy/MM/dd HH:mm",
            "yyyy.MM.dd HH:mm",
            "yyyy-MM-dd",
            "yyyy/MM/dd",
            "yyyy.MM.dd",

            "yyyy-M-d H:m:s",
            "yyyy/M/d H:m:s",
            "yyyy.M.d H:m:s",
            "yyyy-M-d",
            "yyyy/M/d",
            "yyyy.M.d",

            "yyyyMMddHHmmssSSSZ",
            "yyyyMMddHHmmssSSS",
            "yyyyMMddHHmmss",
            "yyyyMMdd",

            "yyyy-MM-dd'T'HH:mm:ss+HH:mm",

            "HH:mm:ss",
            "HH:mm:ss.SSS",
            "HH:mm:ss.SSSSSS",
            "HH:mm:ssXXX",
            "HH:mm:ss.SSS+HH:mm",
            "HH时mm分ss秒",

            "H:m:s",
            "H:m"
    };

    static {
        for (String pattern : PATTERNS) {
            DateTimeFormatter fmt = getFormatter(pattern);
            if (isVariableLength(pattern)) {
                VARIABLE_FORMATTERS.add(fmt);
            } else {
                int len = pattern.replace("'", "").length();
                LENGTH_BUCKETS.computeIfAbsent(len, k -> new ArrayList<>()).add(fmt);
            }
        }
    }

    private static boolean isVariableLength(String pattern) {
        String clean = pattern.replaceAll("'[^']*'", "");
        return clean.matches(".*\\b[Mdhms]\\b.*") || clean.contains("-M-") ||
                clean.contains("/M/") || clean.contains(".M.") || clean.contains("-d") ||
                clean.contains("/d") || clean.contains(".d");
    }

    public static Date parseTry(String dateStr) {
        try {
            return parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date parse(String dateStr) throws ParseException {
        if (dateStr == null) return null;
        String trimmed = dateStr.trim();
        if (trimmed.isEmpty()) return null;

        int len = trimmed.length();
        Date result;

        if ((result = parseFast(trimmed, len)) != null) return result;
        if ((result = parseSpecial(trimmed, len)) != null) return result;
        if ((result = parseSmart(trimmed)) != null) return result;
        if ((result = parseFormatters(trimmed, len)) != null) return result;
        if (isNumeric(trimmed)) return parseTimestamp(trimmed);

        throw new ParseException("Unsupported date format: " + trimmed, 0);
    }

    private static Date parseFast(String s, int len) {
        try {
            switch (len) {
                case 19:
                    if (s.charAt(4) == '-' && s.charAt(7) == '-') {
                        if (s.charAt(10) == ' ') {
                            return Date.from(LocalDateTime.of(
                                    (s.charAt(0) - '0') * 1000 + (s.charAt(1) - '0') * 100 + (s.charAt(2) - '0') * 10 + (s.charAt(3) - '0'),
                                    (s.charAt(5) - '0') * 10 + (s.charAt(6) - '0'),
                                    (s.charAt(8) - '0') * 10 + (s.charAt(9) - '0'),
                                    (s.charAt(11) - '0') * 10 + (s.charAt(12) - '0'),
                                    (s.charAt(14) - '0') * 10 + (s.charAt(15) - '0'),
                                    (s.charAt(17) - '0') * 10 + (s.charAt(18) - '0')
                            ).atZone(SYSTEM_ZONE).toInstant());
                        } else if (s.charAt(10) == 'T') {
                            return parseWithFormatter(s, getFormatter("yyyy-MM-dd'T'HH:mm:ss"));
                        }
                    }
                    break;
                case 10:
                    if (s.charAt(4) == '-' && s.charAt(7) == '-') {
                        return Date.from(LocalDate.of(
                                (s.charAt(0) - '0') * 1000 + (s.charAt(1) - '0') * 100 + (s.charAt(2) - '0') * 10 + (s.charAt(3) - '0'),
                                (s.charAt(5) - '0') * 10 + (s.charAt(6) - '0'),
                                (s.charAt(8) - '0') * 10 + (s.charAt(9) - '0')
                        ).atStartOfDay(SYSTEM_ZONE).toInstant());
                    }
                    break;
                case 14:
                case 8:
                    if (isNumeric(s)) {
                        return parseWithFormatter(s, getFormatter(len == 14 ? "yyyyMMddHHmmss" : "yyyyMMdd"));
                    }
                    break;
                case 23:
                    if (s.charAt(10) == 'T') {
                        return parseWithFormatter(s, getFormatter("yyyy-MM-dd'T'HH:mm:ss.SSS"));
                    } else if (s.charAt(19) == ',') {
                        return parseWithFormatter(s, getFormatter("yyyy-MM-dd HH:mm:ss,SSS"));
                    } else if (s.charAt(19) == '.') {
                        return parseWithFormatter(s, getFormatter("yyyy-MM-dd HH:mm:ss.SSS"));
                    }
                    break;
                case 29:
                    if (s.charAt(19) == '.') {
                        // yyyy-MM-dd HH:mm:ss.SSSSSSSSS (9位纳秒，截断为毫秒)
                        String truncated = s.substring(0, 23);
                        return parseWithFormatter(truncated, getFormatter("yyyy-MM-dd HH:mm:ss.SSS"));
                    }
                    break;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static Date parseSpecial(String s, int len) throws ParseException{
        // 紧凑带时区: 20231025143045123+0800 (FORMAT_22)
        if (len == 22 && (s.charAt(17) == '+' || s.charAt(17) == '-') && isNumeric(s.substring(0, 17))) {
            try {
                return Date.from(OffsetDateTime.of(
                        parseInt(s, 0, 4), parseInt(s, 4, 6), parseInt(s, 6, 8),
                        parseInt(s, 8, 10), parseInt(s, 10, 12), parseInt(s, 12, 14),
                        parseInt(s, 14, 17) * 1_000_000,
                        ZoneOffset.of(s.substring(17).length() == 5 ? s.substring(17, 20) + ":" + s.substring(20) : s.substring(17))
                ).toInstant());
            } catch (Exception ignored) {
            }
        }

        // 中文时间: 14时30分45秒 (FORMAT_9)
        if (s.indexOf('时') > 0 || s.indexOf('分') > 0) {
            try {
                String[] parts = s.split("[时分秒]");
                if (parts.length >= 3) {
                    LocalTime time = LocalTime.of(parseInt(parts[0]), parseInt(parts[1]), parseInt(parts[2]));
                    return Date.from(LocalDateTime.of(LocalDate.now(), time).atZone(SYSTEM_ZONE).toInstant());
                }
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private static Date parseSmart(String s) throws ParseException {
        // 先尝试规范化小数秒（纳秒/微秒截断为毫秒）
        String normalized = normalizeFractionalSeconds(s);

        if (normalized.indexOf('T') > 0) {
            try {
                // 处理混合格式: 2023-10-25T14:30:45.123+00:00Z
                if (normalized.endsWith("Z") && normalized.contains("+")) {
                    String cleaned = normalized.substring(0, normalized.length() - 1);
                    return Date.from(OffsetDateTime.parse(cleaned).toInstant());
                }
                if (normalized.indexOf('Z') > 0) return Date.from(Instant.parse(normalized));
                if (normalized.indexOf('+') > 0 || normalized.indexOf('-') > 0) return Date.from(OffsetDateTime.parse(normalized).toInstant());
            } catch (Exception ignored) {
            }
        }

        // 时间格式处理
        if (normalized.indexOf(':') > 0) {
            Date result = parseTime(normalized);
            if (result != null) return result;
        }

        // 变长日期处理
        if (normalized.indexOf('-') > 0 || normalized.indexOf('/') > 0 || normalized.indexOf('.') > 0) {
            Date result = parseVariableDate(normalized);
            if (result != null) return result;
        }

        return null;
    }

    /**
     * 将小数秒规范化为毫秒精度（3位），截断多余位数。
     * 例如："2026-03-25 11:00:00.152636324" -> "2026-03-25 11:00:00.152"
     */
    private static String normalizeFractionalSeconds(String s) {
        int dotIdx = s.indexOf('.');
        if (dotIdx < 0) return s;

        // 找到小数部分的结束位置
        int end = dotIdx + 1;
        while (end < s.length() && Character.isDigit(s.charAt(end))) end++;

        int fractionLen = end - dotIdx - 1;
        if (fractionLen <= 3) return s;

        // 截断到3位毫秒精度
        String truncated = s.substring(0, dotIdx + 4) + s.substring(end);
        return truncated;
    }

    private static Date parseTime(String s) throws ParseException {
        try {
            String normalized = s;
            // 小数秒已在 parseSmart 中被规范化为毫秒精度，此处无需再处理

            // 带时区的时间 (FORMAT_18, FORMAT_14_b)
            if (normalized.contains("+") || normalized.contains("-")) {
                try {
                    return Date.from(OffsetDateTime.parse(LocalDate.now() + "T" + normalized).toInstant());
                } catch (Exception e) {
                    // 尝试特定格式 HH:mm:ss.SSS+HH:mm
                    LocalTime time = LocalTime.parse(normalized, getFormatter("HH:mm:ss.SSS+HH:mm"));
                    return Date.from(LocalDateTime.of(LocalDate.now(), time).atZone(SYSTEM_ZONE).toInstant());
                }
            }

            DateTimeFormatter fmt = normalized.contains(".") ?
                    getFormatter("HH:mm:ss.SSS") : getFormatter("HH:mm:ss");
            LocalTime time = LocalTime.parse(normalized, fmt);
            return Date.from(LocalDateTime.of(LocalDate.of(1970, 1, 1), time)
                    .atZone(SYSTEM_ZONE)
                    .toInstant());
        } catch (Exception e) {
            return null;
        }
    }

    private static Date parseVariableDate(String s) {
        try {
            String normalized = s.replace('/', '-').replace('.', '-');
            String[] parts = normalized.split(" ", 2);
            String[] dateParts = parts[0].split("-");

            if (dateParts.length == 3) {
                // 验证日期有效性
                int year = parseInt(dateParts[0]);
                int month = parseInt(dateParts[1]);
                int day = parseInt(dateParts[2]);

                // 基本日期验证 (帮助无效日期测试用例抛出异常)
                if (month < 1 || month > 12 || day < 1 || day > 31) {
                    throw new ParseException("Invalid date", 0);
                }

                StringBuilder std = new StringBuilder()
                        .append(dateParts[0]).append('-')
                        .append(String.format("%02d", month)).append('-')
                        .append(String.format("%02d", day));

                if (parts.length > 1) {
                    String[] timeParts = parts[1].split(":");
                    // 验证时间有效性
                    int hour = parseInt(timeParts[0]);
                    int minute = timeParts.length > 1 ? parseInt(timeParts[1]) : 0;
                    int second = timeParts.length > 2 ? parseInt(timeParts[2]) : 0;

                    if (hour < 0 || hour > 23 || minute < 0 || minute > 59 || second < 0 || second > 59) {
                        throw new ParseException("Invalid time", 0);
                    }

                    std.append(" ").append(String.format("%02d", hour))
                            .append(":").append(String.format("%02d", minute))
                            .append(":").append(String.format("%02d", second));
                }

                return parseFormatters(std.toString(), std.length());
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private static Date parseFormatters(String s, int len) {
        // 1. 尝试标准ISO格式化器
        for (DateTimeFormatter fmt : COMMON_FORMATTERS) {
            Date result = parseWithFormatter(s, fmt);
            if (result != null) return result;
        }

        // 2. 长度分桶匹配
        List<DateTimeFormatter> bucket = LENGTH_BUCKETS.get(len);
        if (bucket != null) {
            for (DateTimeFormatter fmt : bucket) {
                Date result = parseWithFormatter(s, fmt);
                if (result != null) return result;
            }
        }

        // 3. 变长格式
        for (DateTimeFormatter fmt : VARIABLE_FORMATTERS) {
            Date result = parseWithFormatter(s, fmt);
            if (result != null) return result;
        }

        return null;
    }

    private static Date parseWithFormatter(String s, DateTimeFormatter fmt) {
        Instant instant = parseWithFormatter(s, fmt, SYSTEM_ZONE);

        if (instant != null) {
            return Date.from(instant);
        } else {
            return null;
        }
    }

    private static Instant parseWithFormatter(String s, DateTimeFormatter fmt, ZoneId zoneId) {
        try {
            TemporalAccessor accessor = fmt.parse(s);

            if (accessor.isSupported(ChronoField.OFFSET_SECONDS) || accessor.isSupported(ChronoField.INSTANT_SECONDS)) {
                return Instant.from(accessor);
            }

            if (accessor.isSupported(ChronoField.YEAR) && accessor.isSupported(ChronoField.HOUR_OF_DAY)) {
                return LocalDateTime.from(accessor).atZone(zoneId).toInstant();
            }

            if (accessor.isSupported(ChronoField.DAY_OF_MONTH)) {
                return LocalDate.from(accessor).atStartOfDay(zoneId).toInstant();
            }

            if (accessor.isSupported(ChronoField.HOUR_OF_DAY)) {
                return LocalDate.of(1970, 1, 1)
                        .atTime(LocalTime.from(accessor))
                        .atZone(zoneId)
                        .toInstant();
            }

            return Instant.from(accessor);

        } catch (Exception e) {
            return null;
        }
    }

    private static Date parseTimestamp(String s) throws ParseException {
        try {
            long ts = Long.parseLong(s);
            return new Date(s.length() <= 10 ? ts * 1000 : ts);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid timestamp: " + s, 0);
        }
    }

    // ========== 工具方法 ==========
    private static int parseInt(String s) {
        return parseInt(s, 0, s.length());
    }

    private static int parseInt(CharSequence s, int start, int end) {
        int result = 0;
        for (int i = start; i < end; i++) {
            char c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                result = result * 10 + (c - '0');
            } else {
                throw new NumberFormatException("Invalid digit: " + c);
            }
        }
        return result;
    }

    private static boolean isNumeric(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) < '0' || s.charAt(i) > '9') return false;
        }
        return true;
    }

    private static DateTimeFormatter getFormatter(String pattern) {
        return FORMATTER_CACHE.computeIfAbsent(pattern,
                p -> DateTimeFormatter.ofPattern(p).withZone(SYSTEM_ZONE));
    }

    // ========== 格式化方法 ==========
    public static String format(Date date, String pattern) {
        return date == null || pattern == null ? null :
                getFormatter(pattern)
                        .format(date.toInstant());
    }

    public static String format(Date date, String pattern, ZoneId zoneId) {
        return date == null || pattern == null ? null :
                getFormatter(pattern)
                        .withZone(zoneId)
                        .format(date.toInstant());
    }

    public static String format(Date date, String pattern, TimeZone timeZone) {
        return date == null || pattern == null ? null :
                getFormatter(pattern).withZone(timeZone.toZoneId()).format(date.toInstant());
    }

    public static String toISOString(Date date) {
        return date == null ? null :
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                        .withZone(ZoneId.of("UTC")).format(date.toInstant());
    }

    public static String toGmtString(Date date) {
        return date == null ? null :
                DateTimeFormatter.RFC_1123_DATE_TIME
                        .withZone(ZoneId.of("GMT")).format(date.toInstant());
    }

    /// ////////////////////

    public static ZoneId zoneIdOf(DecodeContext ctx) {
        ZoneId zoneId = ctx.getOptions().getZoneId();
        if (ctx.getAttr() != null && ctx.getAttr().getZoneId() != null) {
            zoneId = ctx.getAttr().getZoneId();
        }

        return zoneId;
    }

    public static ZonedDateTime decodeAndZone(DecodeContext ctx, ONode node) {
        ZoneId zoneId = zoneIdOf(ctx);

        return decode(ctx, node).atZone(zoneId);
    }

    /**
     * 解码 LocalTime。默认 ISO 文本直接解析，以保留小数秒精度；
     * 自定义格式和历史数值格式仍复用通用日期解析逻辑。
     */
    public static LocalTime decodeLocalTime(DecodeContext ctx, ONode node) {
        if (isDefaultFormatString(ctx, node)) {
            try {
                return LocalTime.parse(node.getString());
            } catch (Exception ignored) {
            }
        }

        return decodeAndZone(ctx, node).toLocalTime();
    }

    /**
     * 解码 OffsetDateTime。默认 ISO 文本直接解析，以保留原始 offset。
     */
    public static OffsetDateTime decodeOffsetDateTime(DecodeContext ctx, ONode node) {
        if (isDefaultFormatString(ctx, node)) {
            try {
                return OffsetDateTime.parse(node.getString());
            } catch (Exception ignored) {
            }
        }

        return decodeAndZone(ctx, node).toOffsetDateTime();
    }

    /**
     * 解码 OffsetTime。默认 ISO 文本直接解析，以保留原始 offset。
     */
    public static OffsetTime decodeOffsetTime(DecodeContext ctx, ONode node) {
        if (isDefaultFormatString(ctx, node)) {
            try {
                return OffsetTime.parse(node.getString());
            } catch (Exception ignored) {
            }
        }

        return decodeAndZone(ctx, node).toOffsetDateTime().toOffsetTime();
    }

    /**
     * 解码 ZonedDateTime。默认 ISO 文本直接解析，以保留原始 ZoneId。
     */
    public static ZonedDateTime decodeZonedDateTime(DecodeContext ctx, ONode node) {
        if (isDefaultFormatString(ctx, node)) {
            try {
                return ZonedDateTime.parse(node.getString());
            } catch (Exception ignored) {
            }
        }

        return decodeAndZone(ctx, node);
    }

    private static boolean isDefaultFormatString(DecodeContext ctx, ONode node) {
        return node.isString() && (ctx.getAttr() == null || Asserts.isEmpty(ctx.getAttr().getFormat()));
    }

    public static Instant decode(DecodeContext ctx, ONode node) {
        if (node.isDate()) {
            return Instant.ofEpochMilli(node.getDate().getTime());
        } else if (node.isNumber()) {
            return Instant.ofEpochMilli(node.getLong());
        } else if (node.isString()) {
            try {
                if (ctx.getAttr() != null) {
                    if (Asserts.isNotEmpty(ctx.getAttr().getFormat())) {
                        DateTimeFormatter formatter = getFormatter(ctx.getAttr().getFormat());

                        if (formatter != null) {
                            ZoneId zoneId = ctx.getAttr().getZoneId();
                            if (zoneId == null) {
                                zoneId = SYSTEM_ZONE;
                            }

                            Instant instant = parseWithFormatter(node.getString(), formatter, zoneId);
                            return instant;
                        }
                    }
                }

                return DateUtil.parse(node.getString()).toInstant();
            } catch (Exception ex) {
                throw new CodecException("Cannot be converted to " + ctx.getType().getSimpleName() + ": " + node, ex);
            }
        } else {
            throw new CodecException("Cannot be converted to " + ctx.getType().getSimpleName() + ": " + node);
        }
    }
}