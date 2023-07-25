package org.noear.snack.to;

import org.noear.snack.*;
import org.noear.snack.core.Context;
import org.noear.snack.core.DEFAULTS;
import org.noear.snack.core.Feature;
import org.noear.snack.core.NodeDecoderEntity;
import org.noear.snack.core.exts.ClassWrap;
import org.noear.snack.core.exts.EnumWrap;
import org.noear.snack.core.exts.FieldWrap;
import org.noear.snack.core.utils.*;
import org.noear.snack.exception.SnackException;

import java.io.File;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

/**
 * 对象转换器
 * <p>
 * 将 ONode 转为 java Object
 */
public class ObjectToer implements Toer {
    @Override
    public void handle(Context ctx) throws Exception {
        ONode o = (ONode) ctx.source;

        if (null != o) {
            ctx.target = analyse(ctx, o, ctx.target, ctx.target_clz, ctx.target_type, null);
        }
    }

    private Object analyse(Context ctx, ONode o, Object rst, Class<?> clz, Type type, Map<String, Type> genericInfo) throws Exception {
        if (o == null) {
            return rst;
        }

        if (clz != null) {
            if (ONode.class.isAssignableFrom(clz)) {
                return o;
            }
        }

        if (o.isNull()) {
            return rst;
        }

        //提前找到@type类型，便于自定义解码器定位
        if (o.isObject() || o.isArray()) {
            clz = getTypeByNode(ctx, o, clz);
        }

        if (clz != null) {
            //自定义解析
            for (NodeDecoderEntity decoder : ctx.options.decoders()) {
                if (decoder.isDecodable(clz)) {
                    return decoder.decode(o, clz);
                }
            }
        }

        //如果是String接收
        if (String.class == clz) {
            return o.getString();
        }


        switch (o.nodeType()) {
            case Value:
                if (clz != null && Collection.class.isAssignableFrom(clz)) {
                    //如果接收对象为集合???尝试做为item
                    if (rst == null) {
                        rst = TypeUtil.createCollection(clz, false);
                    }

                    if (rst != null) {
                        //todo::
                        Type type1 = TypeUtil.getCollectionItemType(type);
                        if (type1 instanceof Class) {
                            Object val1 = analyseVal(ctx, o.nodeData(), (Class<?>) type1);
                            ((Collection) rst).add(val1);
                            return rst;
                        } else {
                            Object val1 = analyseVal(ctx, o.nodeData(), null);
                            ((Collection) rst).add(val1);
                            return rst;
                        }
                    }
                }

                return analyseVal(ctx, o.nodeData(), clz);
            case Object:
                //clz = getTypeByNode(ctx, o, clz);
                o.remove(ctx.options.getTypePropertyName());//尝试移除类型内容

                if (Properties.class.isAssignableFrom(clz)) {
                    return analyseProps(ctx, o, (Properties) rst, clz, type, genericInfo);
                } else if (Map.class.isAssignableFrom(clz)) {
                    return analyseMap(ctx, o, clz, type, genericInfo);
                } else if (StackTraceElement.class.isAssignableFrom(clz)) {
                    String declaringClass = o.get("declaringClass").getString();
                    if (declaringClass == null) {
                        //todo: 兼容fastjson的序列化
                        declaringClass = o.get("className").getString();
                    }

                    return new StackTraceElement(
                            declaringClass,
                            o.get("methodName").getString(),
                            o.get("fileName").getString(),
                            o.get("lineNumber").getInt());
                } else {
                    if (type instanceof ParameterizedType) {
                        genericInfo = GenericUtil.getGenericInfo(type);
                    }

                    return analyseBean(ctx, o, rst, clz, type, genericInfo);
                }
            case Array:
                if (clz.isArray()) {
                    return analyseArray(ctx, o.nodeData(), clz);
                } else {
                    return analyseCollection(ctx, o, rst, clz, type, genericInfo);
                }
            default:
                return rst;
        }
    }

    private boolean is(Class<?> s, Class<?> t) {
        return s.isAssignableFrom(t);
    }

    public Object analyseVal(Context ctx, ONodeData d, Class<?> clz) throws Exception {

        OValue v = d.value;

        if (v.type() == OValueType.Null) {
            return null;
        }

        if (clz == null) {
            return v.getRaw();
        }

        if (is(Byte.class, clz) || clz == Byte.TYPE) {
            return (byte) v.getLong();
        } else if (is(Short.class, clz) || clz == Short.TYPE) {
            return v.getShort();
        } else if (is(Integer.class, clz) || clz == Integer.TYPE) {
            return v.getInt();
        } else if (is(Long.class, clz) || clz == Long.TYPE) {
            return v.getLong();
        } else if (is(LongAdder.class, clz)) {
            LongAdder tmp = new LongAdder();
            tmp.add(v.getLong());
            return tmp;
        } else if (is(Float.class, clz) || clz == Float.TYPE) {
            return v.getFloat();
        } else if (is(Double.class, clz) || clz == Double.TYPE) {
            return v.getDouble();
        } else if (is(DoubleAdder.class, clz)) {
            DoubleAdder tmp = new DoubleAdder();
            tmp.add(v.getDouble());
            return tmp;
        } else if (is(Boolean.class, clz) || clz == Boolean.TYPE) {
            return v.getBoolean();
        } else if (is(Character.class, clz) || clz == Character.TYPE) {
            return v.getChar();
        } else if (is(String.class, clz)) {
            return v.getString();
        } else if (is(java.sql.Timestamp.class, clz)) {
            return new java.sql.Timestamp(v.getLong());
        } else if (is(java.sql.Date.class, clz)) {
            return new java.sql.Date(v.getLong());
        } else if (is(java.sql.Time.class, clz)) {
            return new java.sql.Time(v.getLong());
        } else if (is(Date.class, clz)) {
            return v.getDate();
        } else if (is(LocalDateTime.class, clz)) {
            Date date = v.getDate();
            if (date == null) {
                return null;
            } else {
                return date.toInstant().atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toLocalDateTime();
            }
        } else if (is(LocalDate.class, clz)) {
            Date date = v.getDate();
            if (date == null) {
                return null;
            } else {
                return date.toInstant().atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toLocalDate();
            }
        } else if (is(LocalTime.class, clz)) {
            Date date = v.getDate();
            if (date == null) {
                return null;
            } else {
                return date.toInstant().atZone(DEFAULTS.DEF_TIME_ZONE.toZoneId()).toLocalTime();
            }
        } else if (is(BigDecimal.class, clz)) {
            if (v.type() == OValueType.Number) {
                if (v.getRawNumber() instanceof BigDecimal) {
                    return v.getRawNumber();
                }
            }

            return new BigDecimal(v.getString());
        } else if (is(BigInteger.class, clz)) {
            if (v.type() == OValueType.Number) {
                if (v.getRawNumber() instanceof BigInteger) {
                    return v.getRawNumber();
                }
            }

            return new BigInteger(v.getString());
        } else if (clz.isEnum()) {
            return analyseEnum(ctx, d, clz);
        } else if (is(Class.class, clz)) {
            return ctx.options.loadClass(v.getString());
        } else if (is(File.class, clz)) {
            return new File(v.getString());
        } else if (is(Charset.class, clz)) {
            return Charset.forName(v.getString());
        } else if (is(Object.class, clz)) {
            Object val = v.getRaw();

            if (val instanceof String) {
                //如果是 String，且 isInterface 或 isAbstract
                if (clz.isInterface() || Modifier.isAbstract(clz.getModifiers())) {
                    Class<?> valClz = ctx.options.loadClass((String) val);

                    if (valClz == null) {
                        return null;
                    } else {
                        return BeanUtil.newInstance(valClz);
                    }
                }
            }

            return val;
        } else {
            throw new SnackException("unsupport type " + clz.getName());
        }
    }

    public Object analyseEnum(Context ctx, ONodeData d, Class<?> target) {
        EnumWrap ew = TypeUtil.createEnum(target);
        Enum code = ew.getCode(target.getName() + d.value.getString());
        if (code != null) {
            return code;
        }
        if (d.value.type() == OValueType.String) {
            return ew.get(d.value.getString());
        } else {
            return ew.get(d.value.getInt());
        }
    }

    public Object analyseArray(Context ctx, ONodeData d, Class<?> target) throws Exception {
        int len = d.array.size();

        if (is(byte[].class, target)) {
            byte[] val = new byte[len];
            for (int i = 0; i < len; i++) {
                val[i] = (byte) d.array.get(i).getLong();
            }
            return val;
        } else if (is(short[].class, target)) {
            short[] val = new short[len];
            for (int i = 0; i < len; i++) {
                val[i] = d.array.get(i).getShort();
            }
            return val;
        } else if (is(int[].class, target)) {
            int[] val = new int[len];
            for (int i = 0; i < len; i++) {
                val[i] = d.array.get(i).getInt();
            }
            return val;
        } else if (is(long[].class, target)) {
            long[] val = new long[len];
            for (int i = 0; i < len; i++) {
                val[i] = d.array.get(i).getLong();
            }
            return val;
        } else if (is(float[].class, target)) {
            float[] val = new float[len];
            for (int i = 0; i < len; i++) {
                val[i] = d.array.get(i).getFloat();
            }
            return val;
        } else if (is(double[].class, target)) {
            double[] val = new double[len];
            for (int i = 0; i < len; i++) {
                val[i] = d.array.get(i).getDouble();
            }
            return val;
        } else if (is(boolean[].class, target)) {
            boolean[] val = new boolean[len];
            for (int i = 0; i < len; i++) {
                val[i] = d.array.get(i).getBoolean();
            }
            return val;
        } else if (is(char[].class, target)) {
            char[] val = new char[len];
            for (int i = 0; i < len; i++) {
                val[i] = d.array.get(i).getChar();
            }
            return val;
        } else if (is(String[].class, target)) {
            String[] val = new String[len];
            for (int i = 0; i < len; i++) {
                val[i] = d.array.get(i).getString();
            }
            return val;
        } else if (is(Object[].class, target)) {
            Class<?> c = target.getComponentType();
            Object[] val = (Object[]) Array.newInstance(c, len);
            for (int i = 0; i < len; i++) {
                val[i] = analyse(ctx, d.array.get(i), null, c, c, null);
            }
            return val;
        } else {
            throw new SnackException("unsupport type " + target.getName());
        }
    }


    public Object analyseCollection(Context ctx, ONode o, Object rst, Class<?> clz, Type type, Map<String, Type> genericInfo) throws Exception {
        Collection list = null;
        if (rst instanceof Collection) {
            list = (Collection) rst;
        } else {
            list = TypeUtil.createCollection(clz, false);
        }

        if (list == null) {
            return rst;
        }

        Class<?> itemClz = null;
        Type itemType = null;
        if (ctx.target_type != null) {
            itemType = TypeUtil.getCollectionItemType(type);

            if (itemType instanceof Class) {
                itemClz = (Class<?>) itemType;
            } else if (itemType instanceof ParameterizedType) {
                itemClz = (Class<?>) ((ParameterizedType) itemType).getRawType();
            }
        }

        //解决无法识别的范型
        if (itemType != null && itemType instanceof TypeVariable) {
            itemType = null;
        }

        for (ONode o1 : o.nodeData().array) {
            list.add(analyse(ctx, o1, null, itemClz, itemType, genericInfo));
        }

        return list;
    }

    public Object analyseProps(Context ctx, ONode o, Properties rst, Class<?> clz, Type type, Map<String, Type> genericInfo) throws Exception {
        if (rst == null) {
            rst = new Properties();
        }

        String prefix = "";
        propsLoad0(rst, prefix, o);

        return rst;
    }

    private void propsLoad0(Properties props, String prefix, ONode tmp) {
        if (tmp.isObject()) {
            tmp.forEach((k, v) -> {
                String prefix2 = prefix + "." + k;
                propsLoad0(props, prefix2, v);
            });
            return;
        }

        if (tmp.isArray()) {
            int index = 0;
            for (ONode v : tmp.ary()) {
                String prefix2 = prefix + "[" + index + "]";
                propsLoad0(props, prefix2, v);
                index++;
            }
            return;
        }

        if (tmp.isNull()) {
            propsPut0(props, prefix, "");
        } else {
            propsPut0(props, prefix, tmp.getString());
        }
    }

    private void propsPut0(Properties props, String key, Object val) {
        if (key.startsWith(".")) {
            props.put(key.substring(1), val);
        } else {
            props.put(key, val);
        }
    }


    public Object analyseMap(Context ctx, ONode o, Class<?> clz, Type type, Map<String, Type> genericInfo) throws Exception {
        Map<Object, Object> map = TypeUtil.createMap(clz);

        if (type instanceof ParameterizedType) { //这里还要再研究下
            ParameterizedType ptt = ((ParameterizedType) type);
            Type kType = ptt.getActualTypeArguments()[0];
            Type vType = ptt.getActualTypeArguments()[1];
            Class<?> vClass = null;

            if (kType instanceof ParameterizedType) {
                kType = ((ParameterizedType) kType).getRawType();
            }

            if (vType instanceof Class) {
                vClass = (Class<?>) vType;
            } else if (vType instanceof ParameterizedType) {
                vClass = (Class<?>) ((ParameterizedType) vType).getRawType();
            }

            if (kType == String.class) {
                for (Map.Entry<String, ONode> kv : o.nodeData().object.entrySet()) {
                    map.put(kv.getKey(), analyse(ctx, kv.getValue(), null, vClass, vType, genericInfo));
                }
            } else {
                for (Map.Entry<String, ONode> kv : o.nodeData().object.entrySet()) {
                    map.put(TypeUtil.strTo(kv.getKey(), (Class<?>) kType), analyse(ctx, kv.getValue(), null, vClass, vType, genericInfo));
                }
            }
        } else {
            for (Map.Entry<String, ONode> kv : o.nodeData().object.entrySet()) {
                map.put(kv.getKey(), analyse(ctx, kv.getValue(), null, null, null, genericInfo));
            }
        }

        return map;
    }


    public Object analyseBean(Context ctx, ONode o, Object rst, Class<?> clz, Type type, Map<String, Type> genericInfo) throws Exception {
        if (is(SimpleDateFormat.class, clz)) {
            return new SimpleDateFormat(o.get("val").getString());
        }

        if (is(InetSocketAddress.class, clz)) {
            return new InetSocketAddress(o.get("address").getString(), o.get("port").getInt());
        }

        if (is(Throwable.class, clz)) {
            //todo: 兼容fastjson的异常序列化
            String message = o.get("message").getString();
            if (StringUtil.isEmpty(message) == false) {
                try {
                    Constructor fun = clz.getConstructor(String.class);
                    rst = fun.newInstance(message);
                } catch (Throwable e) {

                }
            }
        }

        ClassWrap clzWrap = ClassWrap.get(clz);

        if (clzWrap.recordable()) {
            //如果所有字段只读,则通过构造函数处理(支持 jdk14+  的 record 类型)
            Parameter[] argsP = clzWrap.recordParams();
            Object[] argsV = new Object[argsP.length];

            for (int j = 0; j < argsP.length; j++) {
                Parameter f = argsP[j];
                String fieldK = f.getName();
                if (o.contains(fieldK)) {
                    Class fieldT = f.getType();
                    Type fieldGt = f.getParameterizedType();

                    Object val = analyseBeanOfValue(fieldK, fieldT, fieldGt, ctx, o, null, genericInfo);
                    argsV[j] = val;
                }
            }

            try {
                rst = clzWrap.recordConstructor().newInstance(argsV);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("The constructor missing parameters: " + clz.getName(), e);
            }
        } else {
            //排除字段
            Set<String> excNames = null;

            if (rst == null) {
                if (clzWrap.recordConstructor() == null) {
                    rst = BeanUtil.newInstance(clz);
                } else {
                    //只有带参数的构造函（像 java record, kotlin data）
                    excNames = new LinkedHashSet<>();
                    Parameter[] argsP = clzWrap.recordParams();
                    Object[] argsV = new Object[argsP.length];

                    for (int j = 0; j < argsP.length; j++) {
                        Parameter f = argsP[j];
                        String fieldK = f.getName();

                        //构造参数有的，进入排除
                        excNames.add(fieldK);

                        if (o.contains(fieldK)) {
                            Class fieldT = f.getType();
                            Type fieldGt = f.getParameterizedType();

                            Object val = analyseBeanOfValue(fieldK, fieldT, fieldGt, ctx, o, null, genericInfo);
                            argsV[j] = val;
                        }
                    }

                    try {
                        rst = clzWrap.recordConstructor().newInstance(argsV);
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("The constructor missing parameters: " + clz.getName(), e);
                    }
                }
            }

            if (rst == null) {
                return null;
            }

            boolean disSetter = !ctx.options.hasFeature(Feature.UseSetter);

            for (FieldWrap f : clzWrap.fieldAllWraps()) {
                if (f.isDeserialize() == false) {
                    continue;
                }
                String fieldK = f.getName();

                if (excNames != null && excNames.contains(fieldK)) {
                    continue;
                }


                if (o.contains(fieldK)) {
                    Class fieldT = f.type;
                    Type fieldGt = f.genericType;

                    if (f.readonly) {
                        analyseBeanOfValue(fieldK, fieldT, fieldGt, ctx, o, f.getValue(rst), genericInfo);
                    } else {


                        Object val = analyseBeanOfValue(fieldK, fieldT, fieldGt, ctx, o, f.getValue(rst), genericInfo);

                        if (val == null) {
                            //null string 是否以 空字符处理
                            if (ctx.options.hasFeature(Feature.StringFieldInitEmpty) && f.type == String.class) {
                                val = "";
                            }
                        }

                        f.setValue(rst, val, disSetter);
                    }
                }
            }
        }

        return rst;
    }


    private Object analyseBeanOfValue(String fieldK, Class fieldT, Type fieldGt, Context ctx, ONode o, Object rst, Map<String, Type> genericInfo) throws Exception {
        if (genericInfo != null) {
            if (fieldGt instanceof TypeVariable) {
                Type tmp = genericInfo.get(fieldGt.getTypeName());
                if (tmp != null) {
                    fieldGt = tmp;
                    if (tmp instanceof Class) {
                        fieldT = (Class) tmp;
                    }

                    //如果是ParameterizedType，下面还会接着处理
                }
            }

            if (fieldGt instanceof ParameterizedType) {
                ParameterizedType fieldGt2 = ((ParameterizedType) fieldGt);
                Type[] actualTypes = fieldGt2.getActualTypeArguments();
                boolean actualTypesChanged = false;

                fieldT = (Class) fieldGt2.getRawType();

                for (int i = 0, len = actualTypes.length; i < len; i++) {
                    Type tmp = actualTypes[i];
                    if (tmp instanceof TypeVariable) {
                        tmp = genericInfo.get(tmp.getTypeName());

                        if (tmp != null) { //有可能不有
                            actualTypes[i] = tmp;
                            actualTypesChanged = true;
                        }
                    }
                }

                if (actualTypesChanged) {
                    fieldGt = new ParameterizedTypeImpl((Class<?>) fieldGt2.getRawType(), actualTypes, fieldGt2.getOwnerType());
                }
            }
        }

        return analyse(ctx, o.get(fieldK), rst, fieldT, fieldGt, genericInfo);
    }


    private Class<?> getTypeByNode(Context ctx, ONode o, Class<?> def) {
        //
        // 下面使用 .ary(), .oby(), .val() 可以减少检查；从而提高性能
        //
        if (ctx.target_type == null) {
            if (o.isObject()) {
                return LinkedHashMap.class;
            }

            if (o.isArray()) {
                return ArrayList.class;
            }
        }

        String typeStr = null;
        if (o.isArray() && o.ary().size() == 2) {
            ONode o1 = o.ary().get(0);
            if (o1.isObject() && o1.obj().size() == 1) { //如果只有一个成员，则可能为list的类型节点
                //
                // 这段，不能与下面的 o.isObject() 复用
                //
                ONode n1 = o1.obj().get(ctx.options.getTypePropertyName());
                if (n1 != null) {
                    typeStr = n1.val().getString();
                }
            }
        }

        if (o.isObject()) {
            ONode n1 = o.obj().get(ctx.options.getTypePropertyName());
            if (n1 != null) {
                typeStr = n1.val().getString();
            }
        }

        if (StringUtil.isEmpty(typeStr) == false) {
            Class<?> clz = ctx.options.loadClass(typeStr);
            if (clz == null) {
                throw new SnackException("unsupport type " + typeStr);
            } else {
                return clz;
            }
        } else {
            if (def == null || def == Object.class) {
                if (o.isObject()) {
                    return LinkedHashMap.class;
                }

                if (o.isArray()) {
                    return ArrayList.class;
                }
            }

            return def;
        }
    }
}
