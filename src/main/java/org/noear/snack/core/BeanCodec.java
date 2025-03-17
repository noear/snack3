package org.noear.snack.core;

import org.noear.snack.ONode;
import org.noear.snack.core.util.FieldWrapper;
import org.noear.snack.core.util.ReflectionUtil;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class BeanCodec {

    // 序列化：对象转ONode
    public static ONode serialize(Object bean) {
        return serialize(bean, Options.def());
    }

    public static ONode serialize(Object bean, Options opts) {
        if (bean == null) {
            return new ONode(null);
        }
        try {
            return convertBeanToNode(bean, new IdentityHashMap<>(), opts);
        } catch (Throwable e) {
            if (e instanceof StackOverflowError) {
                throw (StackOverflowError) e;
            } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException("Failed to convert bean to ONode", e);
            }
        }
    }

    // 反序列化：ONode转对象
    public static <T> T deserialize(ONode node, Class<T> clazz) {
        return deserialize(node, clazz, Options.def());
    }

    public static <T> T deserialize(ONode node, Class<T> clazz, Options opts) {
        if (node == null || clazz == null) {
            return null;
        }
        try {
            return convertNodeToBean(node, clazz, new IdentityHashMap<>(), opts);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException(e);
        }
    }

    //-- 私有方法 --//

    // 对象转ONode核心逻辑
    private static ONode convertBeanToNode(Object bean, Map<Object, Object> visited, Options opts) throws Exception {
        if (bean == null) {
            return new ONode(null);
        }

        if(bean instanceof ONode){
            return (ONode) bean;
        }

        // 优先使用自定义编解码器
        Codec<Object> codec = (Codec<Object>) opts.getCodecRegistry().get(bean.getClass());
        if (codec != null) {
            ONode node = new ONode();
            codec.encode(node, bean);
            return node;
        }

        // 处理Properties类型
        if (bean instanceof Properties) {
            return convertPropertiesToNode((Properties) bean, visited, opts);
        }

        // 循环引用检测
        if (visited.containsKey(bean)) {
            throw new StackOverflowError("Circular reference detected: " + bean.getClass().getName());
        }
        visited.put(bean, null);

        Class<?> clazz = bean.getClass();

        if (clazz.isArray()) {
            //数组
            return convertArrayToNode(bean, visited, opts);
        } else if (bean instanceof Collection) {
            //集合
            return convertCollectionToNode((Collection<?>) bean, visited, opts);
        } else if (bean instanceof Map) {
            //字典
            return convertMapToNode((Map) bean, visited, opts);
        } else {
            ONode node = new ONode(new LinkedHashMap<>());

            for (FieldWrapper field : ReflectionUtil.getDeclaredFields(clazz)) {
                Object value = field.getField().get(bean);
                ONode fieldNode = convertValueToNode(value, visited, opts);
                node.set(field.getAliasName(), fieldNode);
            }

            return node;
        }
    }

    // 处理Properties类型
    private static ONode convertPropertiesToNode(Properties properties, Map<Object, Object> visited, Options opts) throws Exception {
        ONode rootNode = new ONode(new LinkedHashMap<>());
        for (String key : properties.stringPropertyNames()) {
            String value = properties.getProperty(key);
            setNestedValue(rootNode, key, value);
        }
        return rootNode;
    }

    // 设置嵌套值
    private static void setNestedValue(ONode node, String key, String value) {
        String[] parts = key.split("\\.");
        ONode current = node;
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i == parts.length - 1) {
                current.set(part, new ONode(value));
            } else {
                if (part.endsWith("]")) {
                    // 处理数组
                    String arrayName = part.substring(0, part.indexOf('['));
                    int index = Integer.parseInt(part.substring(part.indexOf('[') + 1, part.indexOf(']')));
                    ONode arrayNode = current.get(arrayName);
                    if (arrayNode == null || arrayNode.isNull()) {
                        arrayNode = new ONode(new ArrayList<>());
                        current.set(arrayName, arrayNode);
                    }
                    while (arrayNode.getArray().size() <= index) {
                        arrayNode.add(new ONode(new LinkedHashMap<>()));
                    }
                    current = arrayNode.get(index);
                } else {
                    ONode nextNode = current.get(part);
                    if (nextNode == null || nextNode.isNull()) {
                        nextNode = new ONode(new LinkedHashMap<>());
                        current.set(part, nextNode);
                    }
                    current = nextNode;
                }
            }
        }
    }

    // 值转ONode处理
    private static ONode convertValueToNode(Object value, Map<Object, Object> visited, Options opts) throws Exception {
        if (value == null) {
            return new ONode(null);
        }

        // 优先使用自定义编解码器
        Codec<Object> codec = (Codec<Object>) opts.getCodecRegistry().get(value.getClass());
        if (codec != null) {
            ONode node = new ONode();
            codec.encode(node, value);
            return node;
        }

        if (value instanceof ONode) {
            return (ONode) value;
        } else if (value instanceof Number) {
            return new ONode((Number) value);
        } else if (value instanceof Boolean) {
            return new ONode((Boolean) value);
        } else if (value instanceof String) {
            return new ONode((String) value);
        } else if (value instanceof Enum) {
            return new ONode(((Enum<?>) value).name());
        } else if (value instanceof Collection) {
            return convertCollectionToNode((Collection<?>) value, visited, opts);
        } else if (value instanceof Map) {
            return convertMapToNode((Map<?, ?>) value, visited, opts);
        } else {
            if (value.getClass().isArray()) {
                return convertArrayToNode(value, visited, opts);
            } else {
                return convertBeanToNode(value, visited, opts);
            }
        }
    }

    // 处理数组类型
    private static ONode convertArrayToNode(Object array, Map<Object, Object> visited, Options opts) throws Exception {
        ONode arrayNode = new ONode(new ArrayList<>());
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            Object element = Array.get(array, i);
            arrayNode.add(convertValueToNode(element, visited, opts));
        }
        return arrayNode;
    }

    // 处理集合类型
    private static ONode convertCollectionToNode(Collection<?> collection, Map<Object, Object> visited, Options opts) throws Exception {
        ONode arrayNode = new ONode(new ArrayList<>());
        for (Object item : collection) {
            arrayNode.add(convertValueToNode(item, visited, opts));
        }
        return arrayNode;
    }

    // 处理Map类型
    private static ONode convertMapToNode(Map<?, ?> map, Map<Object, Object> visited, Options opts) throws Exception {
        ONode objNode = new ONode(new LinkedHashMap<>());
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = entry.getKey().toString();
            ONode valueNode = convertValueToNode(entry.getValue(), visited, opts);
            objNode.set(key, valueNode);
        }
        return objNode;
    }

    //-- 反序列化逻辑 --//

    @SuppressWarnings("unchecked")
    private static <T> T convertNodeToBean(ONode node, Class<T> clazz, Map<Object, Object> visited, Options opts) throws Exception {
        // 优先使用自定义编解码器
        Codec<T> codec = (Codec<T>) opts.getCodecRegistry().get(clazz);
        if (codec != null) {
            return codec.decode(node);
        }

        // 处理Properties类型
        if (clazz == Properties.class) {
            return (T) convertNodeToProperties(node);
        }

        T bean = ReflectionUtil.newInstance(clazz);

        for (FieldWrapper field : ReflectionUtil.getDeclaredFields(clazz)) {
            ONode fieldNode = node.get(field.getAliasName());

            if (fieldNode != null && !fieldNode.isNull()) {
                Object value = convertValue(fieldNode, field.getField().getGenericType(), visited, opts);
                field.getField().set(bean, value);
            } else {
                setPrimitiveDefault(field.getField(), bean);
            }
        }
        return bean;
    }

    // 处理Properties类型
    private static Properties convertNodeToProperties(ONode node) {
        Properties properties = new Properties();
        flattenNodeToProperties(node, properties, "");
        return properties;
    }

    // 将嵌套的ONode扁平化为Properties
    private static void flattenNodeToProperties(ONode node, Properties properties, String prefix) {
        if (node.isObject()) {
            for (Map.Entry<String, ONode> entry : node.getObject().entrySet()) {
                String key = prefix.isEmpty() ? entry.getKey() : prefix + "." + entry.getKey();
                flattenNodeToProperties(entry.getValue(), properties, key);
            }
        } else if (node.isArray()) {
            List<ONode> array = node.getArray();
            for (int i = 0; i < array.size(); i++) {
                String key = prefix + "[" + i + "]";
                flattenNodeToProperties(array.get(i), properties, key);
            }
        } else {
            properties.setProperty(prefix, node.getString());
        }
    }

    // 类型转换核心
    private static Object convertValue(ONode node, Type targetType, Map<Object, Object> visited, Options opts) throws Exception {
        if (node.isNull()) {
            return null;
        }

        // 处理泛型类型
        if (targetType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) targetType;
            Type rawType = pType.getRawType();

            if (List.class.isAssignableFrom((Class<?>) rawType)) {
                return convertToList(node, pType.getActualTypeArguments()[0], visited, opts);
            } else if (Map.class.isAssignableFrom((Class<?>) rawType)) {
                Type[] typeArgs = pType.getActualTypeArguments();
                return convertToMap(node, typeArgs[0], typeArgs[1], visited, opts);
            }
        }

        // 处理基本类型
        Class<?> clazz = (Class<?>) (targetType instanceof Class ? targetType : ((ParameterizedType) targetType).getRawType());

        // 优先使用自定义编解码器
        Codec<?> codec = opts.getCodecRegistry().get(clazz);
        if (codec != null) {
            return codec.decode(node);
        }

        if (clazz == String.class) return node.getString();
        if (clazz == Integer.class || clazz == int.class) return node.getInt();
        if (clazz == Long.class || clazz == long.class) return node.getLong();
        if (clazz == Double.class || clazz == double.class) return node.getDouble();
        if (clazz == Boolean.class || clazz == boolean.class) return node.getBoolean();
        if (clazz.isEnum()) return convertToEnum(node, clazz);
        if (clazz == Object.class) return node.getValue();

        // 处理嵌套对象
        return convertNodeToBean(node, clazz, visited, opts);
    }

    //-- 辅助方法 --//

    // 枚举转换
    private static <E extends Enum<E>> E convertToEnum(ONode node, Class<?> clazz) {
        String value = node.getString();
        try {
            return Enum.valueOf((Class<E>) clazz, value);
        } catch (IllegalArgumentException e) {
            E[] constants = ((Class<E>) clazz).getEnumConstants();
            String valid = Arrays.stream(constants)
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException(
                    "Invalid enum value: '" + value + "'. Valid values: " + valid, e);
        }
    }

    // 处理List泛型
    private static List<?> convertToList(ONode node, Type elementType, Map<Object, Object> visited, Options opts) throws Exception {
        List<Object> list = new ArrayList<>();
        for (ONode itemNode : node.getArray()) {
            list.add(convertValue(itemNode, elementType, visited, opts));
        }
        return list;
    }

    // 处理Map泛型
    private static Map<?, ?> convertToMap(ONode node, Type keyType, Type valueType, Map<Object, Object> visited, Options opts) throws Exception {
        Map<Object, Object> map = new LinkedHashMap<>();

        for (Map.Entry<String, ONode> kv : node.getObject().entrySet()) {
            Object k = convertKey(kv.getKey(), keyType);
            Object v = convertValue(kv.getValue(), valueType, visited, opts);
            map.put(k, v);
        }

        return map;
    }

    // Map键类型转换
    private static Object convertKey(String key, Type keyType) {
        if (keyType == String.class) return key;
        if (keyType == Integer.class || keyType == int.class) return Integer.parseInt(key);
        if (keyType == Long.class || keyType == long.class) return Long.parseLong(key);
        throw new IllegalArgumentException("Unsupported map key type: " + keyType);
    }

    // 基本类型默认值
    private static void setPrimitiveDefault(Field field, Object bean) throws IllegalAccessException {
        Class<?> type = field.getType();
        if (!type.isPrimitive()) return;

        if (type == int.class) field.setInt(bean, 0);
        else if (type == long.class) field.setLong(bean, 0L);
        else if (type == boolean.class) field.setBoolean(bean, false);
        else if (type == double.class) field.setDouble(bean, 0.0);
        else if (type == float.class) field.setFloat(bean, 0.0f);
        else if (type == short.class) field.setShort(bean, (short) 0);
        else if (type == byte.class) field.setByte(bean, (byte) 0);
        else if (type == char.class) field.setChar(bean, '\u0000');
    }
}