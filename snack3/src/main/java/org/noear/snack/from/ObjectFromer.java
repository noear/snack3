package org.noear.snack.from;

import org.noear.snack.ONode;
import org.noear.snack.core.Constants;
import org.noear.snack.core.Context;
import org.noear.snack.core.Feature;
import org.noear.snack.core.NodeEncoder;
import org.noear.snack.core.exts.ClassWrap;
import org.noear.snack.core.exts.FieldWrap;
import org.noear.snack.core.utils.BeanUtil;

import java.io.File;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 对象转换器（将 java Object 转为 ONode）
 */
public class ObjectFromer implements Fromer {
    @Override
    public void handle(Context ctx) {
        ctx.target = analyse(ctx.config, ctx.source); //如果是null,会返回 ONode.Null
    }

    private ONode analyse(Constants cfg, Object source) {

        ONode rst = new ONode(cfg);

        if (source == null) {
            return rst;
        }

        if(source instanceof NodeEncoder){
            return ((NodeEncoder)source).toNode();
        }

        Class<?> clz = source.getClass();

        if (source instanceof ONode) {
            rst.val(source);
        } else if (source instanceof String) {
            rst.val().setString((String) source);
        } else if (source instanceof Date) {
            rst.val().setDate((Date) source);
        } else if (source instanceof Integer) {
            rst.val().setInteger((Integer) source);
        } else if (source instanceof Long) {
            rst.val().setInteger((Long) source);
        } else if (source instanceof Float) {
            rst.val().setDecimal((Float) source);
        } else if (source instanceof Double) {
            rst.val().setDecimal((Double) source);
        } else if (source instanceof Short) { //新补充的类型
            rst.val().setInteger((Short) source);
        } else if (source instanceof Character) { //新补充的类型
            rst.val().setInteger((Character) source);
        } else if (source instanceof Byte) { //新补充的类型
            rst.val().setInteger((Byte) source);
        } else if (source instanceof Boolean) {
            rst.val().setBool((boolean) source);
        } else if (source instanceof Number) {
            rst.val().setBignumber((Number) source);
        } else if (source instanceof Throwable) { //新补充的类型
            analyseBean(cfg, rst, clz, source);
        } else if (analyseArray(cfg, rst, clz, source)) { //新补充的类型::可适用任何数组

        } else if (clz.isEnum()) { //新补充的类型
            Enum em = (Enum) source;

            if (cfg.hasFeature(Feature.EnumUsingName)) {
                rst.val().setString(em.name());
            } else {
                rst.val().setInteger(em.ordinal());
            }
        } else if (source instanceof Map) {
            //为序列化添加特性支持
            if (cfg.hasFeature(Feature.WriteClassName)) {
                typeSet(cfg, rst, clz);
            }

            rst.asObject();
            Map map = ((Map) source);
            for (Object k : map.keySet()) {
                if (k != null) {
                    rst.setNode(k.toString(), analyse(cfg, map.get(k)));
                }
            }
        } else if (source instanceof Iterable) {
            rst.asArray();
            ONode ary =rst;
            //为序列化添加特性支持
            if (cfg.hasFeature(Feature.WriteArrayClassName)) {
                rst.add(typeSet(cfg,new ONode(cfg), clz));
                ary = rst.addNew().asArray();
            }

            for (Object o : ((Iterable) source)) {
                ary.addNode(analyse(cfg, o));
            }
        } else if (source instanceof Enumeration) { //新补充的类型
            rst.asArray();
            Enumeration o = (Enumeration) source;
            while (o.hasMoreElements()) {
                rst.addNode(analyse(cfg, o.nextElement()));
            }
        } else {
            String clzName = clz.getName();

            if(clzName.endsWith(".Undefined")){
                rst.val().setNull();
            }else {
                if (analyseOther(cfg, rst, clz, source) == false) {
                    if (clzName.startsWith("jdk.") == false) {
                        analyseBean(cfg, rst, clz, source);
                    }
                }
            }
        }

        return rst;
    }

    private ONode typeSet(Constants cfg, ONode o, Class<?> clz) {
        return o.set(cfg.type_key, clz.getName());
    }


    private boolean analyseArray(Constants cfg, ONode rst, Class<?> clz, Object obj) {
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

    private boolean analyseBean(Constants cfg, ONode rst, Class<?> clz, Object obj) {
        if (obj instanceof NodeEncoder) {
            rst.val(((NodeEncoder) obj).toNode());
        } else {

            rst.asObject();

            //为序列化添加特性支持
            if (cfg.hasFeature(Feature.WriteClassName)) {
                rst.set(cfg.type_key, clz.getName());
            }

            Collection<FieldWrap> list = ClassWrap.get(clz).fieldAllWraps();

            for (FieldWrap f : list) {
                Object val = f.setValue(obj);

                if (val == null) {
                    //null string 是否以 空字符处理
                    if (cfg.hasFeature(Feature.StringFieldInitEmpty) && f.genericType == String.class) {
                        rst.setNode(f.name(), analyse(cfg, ""));
                        continue;
                    }

                    //null是否输出
                    if (cfg.hasFeature(Feature.SerializeNulls)) {
                        rst.setNode(f.name(), analyse(cfg, null));
                    }
                    continue;
                }

                if (val.equals(obj) == false) { //null 和 自引用 不需要处理
                    rst.setNode(f.name(), analyse(cfg, val));
                }
            }
        }

        return true;
    }

    private boolean analyseOther(Constants cfg, ONode rst, Class<?> clz, Object obj) {
        if (obj instanceof SimpleDateFormat) {
            rst.set(cfg.type_key, clz.getName());
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
