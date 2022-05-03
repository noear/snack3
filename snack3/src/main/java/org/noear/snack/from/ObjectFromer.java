package org.noear.snack.from;

import org.noear.snack.ONode;
import org.noear.snack.core.Options;
import org.noear.snack.core.*;
import org.noear.snack.core.exts.ClassWrap;
import org.noear.snack.core.exts.FieldWrap;
import org.noear.snack.core.utils.BeanUtil;
import org.noear.snack.core.utils.DateUtil;
import org.noear.snack.core.utils.StringUtil;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.Clob;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * 对象转换器（将 java Object 转为 ONode）
 */
public class ObjectFromer implements Fromer {
    @Override
    public void handle(Context ctx) {
        ctx.target = analyse(ctx.options, ctx.source); //如果是null,会返回 ONode.Null
    }

    private ONode analyse(Options opt, Object source) {

        ONode rst = new ONode(opt);

        if (source == null) {
            return rst;
        }

        Class<?> clz = source.getClass();

        //尝试自定义编码
        for (NodeEncoderEntity encoder : opt.encoders()) {
            if (encoder.isEncodable(clz)) {
                encoder.encode(source, rst);
                return rst;
            }
        }

        if (source instanceof ONode) {
            rst.val(source);
        } else if (source instanceof String) {
            rst.val().setString((String) source);
        } else if (source instanceof Date) {
            rst.val().setDate((Date) source);
        } else if (source instanceof LocalDateTime) {
            Instant instant = ((LocalDateTime) source).atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toInstant();
            rst.val().setDate(new Date((instant.getEpochSecond() * 1000) + (instant.getNano() / 1000_000)));
        } else if (source instanceof LocalDate) {
            Instant instant = ((LocalDate) source).atTime(LocalTime.MIN).atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toInstant();
            rst.val().setDate(new Date(instant.getEpochSecond() * 1000));
        } else if (source instanceof LocalTime) {
            Instant instant = ((LocalTime) source).atDate(LocalDate.of(1970, 1, 1)).atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toInstant();
            rst.val().setDate(new Date(instant.getEpochSecond() * 1000));
        } else if (source instanceof Boolean) {
            rst.val().setBool((boolean) source);
        } else if (source instanceof Number) {
            rst.val().setNumber((Number) source);
        } else if (source instanceof Throwable) { //新补充的类型
            analyseBean(opt, rst, clz, source);
        } else if (source instanceof Properties) {
            analyseProps(opt, rst, clz, source);
        } else if (analyseArray(opt, rst, clz, source)) { //新补充的类型::可适用任何数组

        } else if (clz.isEnum()) { //新补充的类型
            Enum em = (Enum) source;

            if (opt.hasFeature(Feature.EnumUsingName)) {
                rst.val().setString(em.name());
            } else {
                rst.val().setNumber(em.ordinal());
            }
        } else if (source instanceof Map) {
            //为序列化添加特性支持
            if (opt.hasFeature(Feature.WriteClassName)) {
                typeSet(opt, rst, clz);
            }

            rst.asObject();
            Map map = ((Map) source);
            for (Object k : map.keySet()) {
                if (k != null) {
                    rst.setNode(k.toString(), analyse(opt, map.get(k)));
                }
            }
        } else if (source instanceof Iterable) {
            rst.asArray();
            ONode ary = rst;
            //为序列化添加特性支持
            if (opt.hasFeature(Feature.WriteArrayClassName)) {
                rst.add(typeSet(opt, new ONode(opt), clz));
                ary = rst.addNew().asArray();
            }

            for (Object o : ((Iterable) source)) {
                ary.addNode(analyse(opt, o));
            }
        } else if (source instanceof Enumeration) { //新补充的类型
            rst.asArray();
            Enumeration o = (Enumeration) source;
            while (o.hasMoreElements()) {
                rst.addNode(analyse(opt, o.nextElement()));
            }
        } else {
            String clzName = clz.getName();

            if (clzName.endsWith(".Undefined")) {
                rst.val().setNull();
            } else {
                if (analyseOther(opt, rst, clz, source) == false) {
                    if (clzName.startsWith("jdk.") == false) {
                        analyseBean(opt, rst, clz, source);
                    }
                }
            }
        }

        return rst;
    }

    private ONode typeSet(Options cfg, ONode o, Class<?> clz) {
        return o.set(cfg.getTypePropertyName(), clz.getName());
    }


    private boolean analyseArray(Options cfg, ONode rst, Class<?> clz, Object obj) {
        if (obj instanceof Object[]) {
            rst.asArray();
            for (Object o : ((Object[]) obj)) {
                rst.addNode(analyse(cfg, o));
            }
        } else if (obj instanceof byte[]) {
            rst.asArray();
            for (byte o : ((byte[]) obj)) {
                rst.addNode(analyse(cfg, o));
            }
        } else if (obj instanceof short[]) {
            rst.asArray();
            for (short o : ((short[]) obj)) {
                rst.addNode(analyse(cfg, o));
            }
        } else if (obj instanceof int[]) {
            rst.asArray();
            for (int o : ((int[]) obj)) {
                rst.addNode(analyse(cfg, o));
            }
        } else if (obj instanceof long[]) {
            rst.asArray();
            for (long o : ((long[]) obj)) {
                rst.addNode(analyse(cfg, o));
            }
        } else if (obj instanceof float[]) {
            rst.asArray();
            for (float o : ((float[]) obj)) {
                rst.addNode(analyse(cfg, o));
            }
        } else if (obj instanceof double[]) {
            rst.asArray();
            for (double o : ((double[]) obj)) {
                rst.addNode(analyse(cfg, o));
            }
        } else if (obj instanceof boolean[]) {
            rst.asArray();
            for (boolean o : ((boolean[]) obj)) {
                rst.addNode(analyse(cfg, o));
            }
        } else if (obj instanceof char[]) {
            rst.asArray();
            for (char o : ((char[]) obj)) {
                rst.addNode(analyse(cfg, o));
            }
        } else {
            return false;
        }

        return true;
    }

    private boolean analyseProps(Options cfg, ONode rst, Class<?> clz, Object obj) {
        rst.asObject();

        Properties props = (Properties) obj;

        //对key排序，确保数组有序
        Vector<String> keyVector = new Vector<>();
        props.keySet().forEach(k -> {
            if (k instanceof String) {
                keyVector.add((String) k);
            }
        });
        Collections.sort(keyVector);

        for (String key : keyVector) {
            String val = props.getProperty(key);

            String[] kk = key.split("\\.");
            ONode n1 = rst;
            for (int i = 0; i < kk.length; i++) {
                String p1 = kk[i];
                if (p1.endsWith("]")) {
                    int i1 = Integer.parseInt(p1.substring(p1.lastIndexOf('[')+1, p1.length() - 1));
                    p1 = p1.substring(0, p1.lastIndexOf('['));
                    n1 = n1.getOrNew(p1).asArray();

                    if (n1.count() > i1) {
                        n1 = n1.get(i1);
                    } else {
                        n1 = n1.addNew();
                    }
                } else {
                    n1 = n1.getOrNew(p1);
                }
            }

            n1.val(val);
        }

        return true;
    }

    private boolean analyseBean(Options cfg, ONode rst, Class<?> clz, Object obj) {
        rst.asObject();

        //为序列化添加特性支持
        if (cfg.hasFeature(Feature.WriteClassName)) {
            rst.set(cfg.getTypePropertyName(), clz.getName());
        }

        // 遍历每个字段
        Collection<FieldWrap> list = ClassWrap.get(clz).fieldAllWraps();

        for (FieldWrap f : list) {
            if (f.isSerialize() == false) {
                //不做序列化
                continue;
            }

            Object val = f.getValue(obj);

            //如果是null
            if (val == null) {
                //null string 是否以 空字符处理
                if (cfg.hasFeature(Feature.StringFieldInitEmpty) && f.genericType == String.class) {
                    rst.setNode(f.getName(), analyse(cfg, ""));
                    continue;
                }

                //null是否输出
                if (cfg.hasFeature(Feature.SerializeNulls)) {
                    rst.setNode(f.getName(), analyse(cfg, null));
                }
                continue;
            }

            //如果是自引用
            if (val.equals(obj)) {
                continue;
            }

            if (StringUtil.isEmpty(f.getFormat()) == false) {
                if (val instanceof Date) {
                    String val2 = DateUtil.format((Date) val, f.getFormat());
                    rst.set(f.getName(), val2);
                    continue;
                }

                if (val instanceof Number) {
                    NumberFormat format = new DecimalFormat(f.getFormat());
                    String val2 = format.format(val);
                    rst.set(f.getName(), val2);
                    continue;
                }
            }

            rst.setNode(f.getName(), analyse(cfg, val));
        }

        return true;
    }

    private boolean analyseOther(Options cfg, ONode rst, Class<?> clz, Object obj) {
        if (obj instanceof SimpleDateFormat) {
            rst.set(cfg.getTypePropertyName(), clz.getName());
            rst.set("val", ((SimpleDateFormat) obj).toPattern());
        } else if (clz == Class.class) {
            rst.val().setString(clz.getName());
        } else if (obj instanceof InetSocketAddress) {
            InetSocketAddress address = (InetSocketAddress) obj;
            InetAddress inetAddress = address.getAddress();
            rst.set("address", inetAddress.getHostAddress());
            rst.set("port", address.getPort());
        } else if (obj instanceof File) {
            rst.val().setString(((File) obj).getPath());
        } else if (obj instanceof InetAddress) {
            rst.val().setString(((InetAddress) obj).getHostAddress());
        } else if (obj instanceof TimeZone) {
            rst.val().setString(((TimeZone) obj).getID());
        } else if (obj instanceof Currency) {
            rst.val().setString(((Currency) obj).getCurrencyCode());
        } else if (obj instanceof Iterator) {
            rst.asArray();
            ((Iterator) obj).forEachRemaining(v -> {
                rst.add(analyse(cfg, v));
            });
        } else if (obj instanceof Map.Entry) {
            Map.Entry kv = (Map.Entry) obj;
            Object k = kv.getKey();
            Object v = kv.getValue();
            rst.asObject();
            if (k != null) {
                rst.set(k.toString(), analyse(cfg, v));
            }
        } else if (obj instanceof Calendar) {
            rst.val().setDate(((Calendar) obj).getTime());
        } else if (obj instanceof Clob) {
            rst.val().setString(BeanUtil.clobToString((Clob) obj));
        } else if (obj instanceof Appendable) {
            rst.val().setString(obj.toString());
        } else {
            return false;
        }

        return true;
    }
}
