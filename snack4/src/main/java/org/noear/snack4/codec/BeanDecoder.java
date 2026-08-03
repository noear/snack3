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

import org.noear.eggg.*;
import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.annotation.ONodeAttrHolder;
import org.noear.snack4.codec.util.*;
import org.noear.snack4.util.Asserts;

import java.lang.reflect.*;
import java.util.*;

/**
 * 对象解码器
 *
 * @author noear 2025/3/16 created
 * @since 4.0
 */
public class BeanDecoder {
    /**
     * ONode 解码为 Java Object
     *
     * @param type 类型
     *
     */
    public static <T> T decode(ONode node, Type type) {
        return decode(node, type, null, null);
    }

    /**
     * ONode 解码为 Java Object
     *
     * @param type 类型
     * @[param opts 选项
     *
     */
    public static <T> T decode(ONode node, Type type, Object target, Options opts) {
        if (node == null || type == null) {
            return null;
        }

        return new BeanDecoder(node, type, target, opts).decode();
    }

    public static <T> T decode(ONode node, Parameter parameter) throws Throwable {
        if (node == null || parameter == null) {
            return null;
        }

        Executable executable = parameter.getDeclaringExecutable();
        ClassEggg classEggg = EgggUtil.getClassEggg(executable.getDeclaringClass());

        ParamEggg paramEggg;
        if (executable instanceof Method) {
            paramEggg = classEggg.findMethodEgggOrNew((Method) executable).getParamEgggBy(parameter);
        } else {
            paramEggg = classEggg.findConstrEggg(executable.getParameterTypes()).getParamEgggBy(parameter);
        }

        BeanDecoder decoder = new BeanDecoder(node, paramEggg.getGenericType(), null, node.options());
        return (T) decoder.getParam(paramEggg, node);
    }

    private final ONode source0;
    private final Type targetType0;
    private final Object target0;

    private final Options opts0;

    private final boolean Decode_OnlyUseSetter;
    private final boolean Decode_AllowUseSetter;
    private final boolean Decode_IgnoreError;

    private BeanDecoder(ONode source, Type type, Object target, Options opts) {
        this.source0 = source;
        this.targetType0 = type;
        this.target0 = target;
        this.opts0 = opts == null ? Options.DEF_OPTIONS : opts;

        this.Decode_OnlyUseSetter = opts0.hasFeature(Feature.Decode_OnlyUseSetter) || opts0.hasFeature(Feature.Write_OnlyUseSetter);
        this.Decode_AllowUseSetter = Decode_OnlyUseSetter || opts0.hasFeature(Feature.Decode_AllowUseSetter) || opts0.hasFeature(Feature.Write_AllowUseSetter);
        this.Decode_IgnoreError = opts0.hasFeature(Feature.Decode_IgnoreError);
    }

    public <T> T decode() {
        TypeEggg typeEggg = EgggUtil.getTypeEggg(targetType0);

        try {
            return (T) decodeValueFromNode(source0, typeEggg, target0, null);
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new CodecException("Failed to decode bean from ONode", e);
        }
    }

    // 类型转换核心
    private Object decodeValueFromNode(ONode node, TypeEggg typeEggg, Object target, ONodeAttrHolder attr) throws Throwable {
        if (node.isNull()) {
            return null;
        }

        //如果是ONode 类型，则直接返回
        if (typeEggg.getType().equals(ONode.class)) {
            return node;
        }

        // 优先使用自定义编解码器
        //提前找到@type类型，便于自定义解码器定位
        typeEggg = confirmNodeType(node, typeEggg);

        // 优先使用自定义编解码器
        ObjectDecoder decoder = opts0.getDecoder(typeEggg.getType());
        if (decoder != null) {
            if (Decode_IgnoreError) {
                try {
                    return decoder.decode(new DecodeContext(opts0, attr, target, typeEggg), node);
                } catch (Throwable e) {
                    return null;
                }
            } else {
                return decoder.decode(new DecodeContext(opts0, attr, target, typeEggg), node);
            }
        }

        if (node.isValue()) {
            if (node.isString()) {
                String str = node.getValueAs();
                boolean isColl = typeEggg.isCollection() || typeEggg.isArray();

                if (typeEggg.getType().isInterface() || Modifier.isAbstract(typeEggg.getType().getModifiers())) {
                    if (str.length() > 1) {
                        //可能是 json array
                        if (isColl && Asserts.isArrayJsonString(str)) {
                            return ONode.ofJson(str, opts0).toBean(typeEggg.getOriginType());
                        }

                        //可能是 class
                        if (Asserts.isClassName(str)) {
                            if (opts0.isTypeBlocked(str)) {
                                if (Decode_IgnoreError) {
                                    return null;
                                } else {
                                    throw new CodecException("Blocked type, class: " + str);
                                }
                            }

                            Class<?> clz = opts0.loadClass(node.getString(), true);

                            if (clz != null && typeEggg.getType().isAssignableFrom(clz)) {
                                return ClassUtil.newInstance(clz);
                            } else {
                                if (Decode_IgnoreError) {
                                    return null;
                                } else {
                                    String clzName = (clz == null ? str : clz.getName());
                                    throw new CodecException("Incompatible type: " + typeEggg.getType().getName() + " <- " + clzName);
                                }
                            }
                        }
                    }
                }

                if (isColl == false) {
                    //如果不是集合（直接返回值）
                    if (typeEggg.isString() == false &&
                            typeEggg.isObject() == false &&
                            Asserts.isObjectJsonString(str)) {
                        //如果不是字符串，不是对象，且是 json string；则自动解码（否则类型对不上）
                        return ONode.ofJson(str, opts0).toBean(typeEggg.getOriginType());
                    } else {
                        return str;
                    }
                }
            } else {
                return node.getValue();
            }
        }

        if (target == null) {
            // 如果没有传入 target，则执行原有的创建新对象的逻辑
            ObjectCreator creator = null;

            // 优先使用属性级别的 creator（来自 @ONodeAttr(creator = ...)）
            if (attr != null) {
                creator = attr.getCreator();
            }

            // 其次使用 Options 级别的 creator
            if (creator == null) {
                creator = opts0.getCreator(typeEggg.getType());
            }

            if (creator != null) {
                target = creator.create(opts0, node, typeEggg.getType());
            }

            if (target == null) {
                if (typeEggg.getType().isInterface()) {
                    if (node.isEmpty()) {
                        return null;
                    }

                    throw new CodecException("can not convert bean to type: " + typeEggg.getType());
                }

                ConstrEggg constrEggg = typeEggg.getClassEggg().getCreator();
                if (constrEggg == null) {
                    throw new CodecException("Create instance failed: " + typeEggg.getType().getName());
                }

                constrEggg = matchBestConstructor(typeEggg.getClassEggg(), constrEggg, node);

                if (constrEggg.getParamCount() == 0) {
                    target = constrEggg.newInstance();
                } else {
                    if (constrEggg.isSecurity() == false //有参数
                            && opts0.hasFeature(Feature.Write_AllowParameterizedConstructor) == false //不支持参数
                            && typeEggg.getClassEggg().isLikeRecordClass() == false)  //不像记录类
                    {
                        throw new CodecException("Parameterized constructor are not allowed: " + typeEggg.getType());
                    }

                    Object[] args = getConstrArgs(constrEggg, node);
                    target = constrEggg.newInstance(args);
                }
            }
        }

        if (target instanceof Map) {
            target = decodeMapFromNode(node, typeEggg, target);
        } else if (target instanceof Collection) {
            target = decodeCollectionFromNode(node, typeEggg, target);
        } else {
            return decodeBeanFromNode(node, typeEggg, target);
        }

        return target;
    }

    private Object decodeBeanFromNode(ONode node, TypeEggg typeEggg, Object target) throws Throwable {
        boolean failOnUnknownProperties = opts0.hasFeature(Feature.Write_FailOnUnknownProperties);

        ClassEggg classEggg = typeEggg.getClassEggg();

        if (failOnUnknownProperties) {
            //以数据为主，才能支持 Read_FailOnUnknownProperties
            for (Map.Entry<String, ONode> kv : node.getObject().entrySet()) {
                if (kv.getKey().equals(opts0.getTypePropertyName())) {
                    continue;
                }

                if (classEggg.getCreator() != null) {
                    if (classEggg.getCreator().hasParamEgggByAlias(kv.getKey())) {
                        continue;
                    }
                }

                PropertyEggg pe = classEggg.getPropertyEgggByAlias(kv.getKey());

                if (pe != null) {
                    decodeBeanPropertyFromNode(node, pe, target);
                } else {
                    throw new CodecException("Unknown property : " + kv.getKey());
                }
            }
        } else {
            //以类为主，才能支持 flat
            for (PropertyEggg pe : classEggg.getPropertyEgggs()) {
                if (classEggg.getCreator() != null && classEggg.getCreator().getParamCount() > 0) {
                    if (classEggg.getCreator().hasParamEgggByAlias(pe.getAlias())) {
                        continue;
                    }
                }

                decodeBeanPropertyFromNode(node, pe, target);
            }
        }

        return target;
    }

    private void decodeBeanPropertyFromNode(ONode node, PropertyEggg pe, Object target) throws Throwable {
        final Property property;
        if (Decode_OnlyUseSetter) {
            property = pe.getSetterEggg();
        } else if (Decode_AllowUseSetter && pe.getSetterEggg() != null) {
            property = pe.getSetterEggg();
        } else {
            property = pe.getFieldEggg();
        }

        decodeBeanPropertyFromNode0(node, property, target);
    }

    private void decodeBeanPropertyFromNode0(ONode node, Property property, Object target) throws Throwable {
        if (property == null || property.isTransient() || property.<ONodeAttrHolder>getDigest().isDecode() == false) {
            return;
        }


        ONode oNode = (property.<ONodeAttrHolder>getDigest().isFlat() ? node : node.get(property.getAlias()));

        if (oNode != null && !oNode.isNull()) {
            //深度填充：获取字段当前的值，作为递归调用的 target
            Object exisValue = property.getValue(target);
            Object propValue = null;

            ONodeAttrHolder attr = property.<ONodeAttrHolder>getDigest();
            ObjectCreator creator = attr.getCreator();

            if (exisValue == null) {
                // 尝试从字段类型的类级别 @ONodeAttr 注解获取 creator（作为 fallback）
                if (creator == null && property.getTypeEggg().isPrimitive() == false) {
                    ONodeAttrHolder clzAttr = property.getTypeEggg().getClassEggg().getDigest();
                    if (clzAttr != null && clzAttr.getCreator() != null) {
                        creator = clzAttr.getCreator();
                    }
                }

                if (creator != null) {
                    exisValue = creator.create(opts0, oNode, property.getTypeEggg().getType());
                }
            }

            if (attr.getDecoder() != null) {
                propValue = property.<ONodeAttrHolder>getDigest()
                        .getDecoder()
                        .decode(new DecodeContext(opts0, property.getDigest(), exisValue, property.getTypeEggg()), oNode);
            } else {
                propValue = decodeValueFromNode(oNode, property.getTypeEggg(), exisValue, property.getDigest());
            }

            property.setValue(target, propValue);
        }
    }


    //-- 辅助方法 --//
    // 处理List泛型
    private Collection decodeCollectionFromNode(ONode node, TypeEggg typeEggg, Object target) throws Throwable {
        Type elementType = Object.class;
        if (typeEggg.isParameterizedType()) {
            elementType = typeEggg.getActualTypeArguments()[0];
        }

        if (elementType instanceof WildcardType) {
            WildcardType tmp = (WildcardType) elementType;

            if (Asserts.isEmpty(tmp.getLowerBounds())) {
                elementType = tmp.getUpperBounds()[0];
            } else {
                elementType = tmp.getLowerBounds()[0];
            }
        }

        Collection coll = (Collection) target;

        if (node.isArray()) {
            if (coll == Collections.EMPTY_LIST) {
                coll = new ArrayList();
            } else if (coll == Collections.EMPTY_SET) {
                coll = new HashSet();
            }
            TypeEggg elementTypeEggg = EgggUtil.getTypeEggg(elementType);

            for (ONode n1 : node.getArray()) {
                //填充集合时，元素为新创建的，所以 target 传 null
                Object item = decodeValueFromNode(n1, elementTypeEggg, null, null);
                coll.add(item);
            }
        } else if (node.isString()) {
            if (coll == Collections.EMPTY_LIST) {
                coll = new ArrayList();
            } else if (coll == Collections.EMPTY_SET) {
                coll = new HashSet();
            }

            String strValue = node.getValueAs();

            if (Asserts.isArrayJsonString(strValue)) {
                // string 支持嵌套 json 自动加载
                ONode aryNode = ONode.ofJson(strValue, opts0);
                TypeEggg elementTypeEggg = EgggUtil.getTypeEggg(elementType);

                for (ONode o1 : aryNode.getArrayUnsafe()) {
                    Object item = decodeValueFromNode(o1, elementTypeEggg, null, null);
                    coll.add(item);
                }
            } else {
                // string 支持自动转数组
                String[] strArray = strValue.split(",");
                TypeEggg elementTypeEggg = EgggUtil.getTypeEggg(elementType);

                for (String str : strArray) {
                    Object item = decodeValueFromNode(new ONode(opts0, str.trim()), elementTypeEggg, null, null);
                    coll.add(item);
                }
            }
        } else {
            throw new CodecException("The type of node " + node.type() + " cannot be converted to collection.");
        }

        return coll;
    }

    // 处理Map泛型
    private Map decodeMapFromNode(ONode node, TypeEggg targetTypeEggg, Object target) throws Throwable {
        if (node.isObject()) {
            Type keyType = Object.class;
            Type valueType = Object.class;
            if (targetTypeEggg.isParameterizedType()) {
                keyType = targetTypeEggg.getActualTypeArguments()[0];
                valueType = targetTypeEggg.getActualTypeArguments()[1];
            }

            if (valueType instanceof WildcardType) {
                WildcardType tmp = (WildcardType) valueType;

                if (Asserts.isEmpty(tmp.getLowerBounds())) {
                    valueType = tmp.getUpperBounds()[0];
                } else {
                    valueType = tmp.getLowerBounds()[0];
                }
            }

            TypeEggg keyTypeEggg = EgggUtil.getTypeEggg(keyType);
            TypeEggg valueTypeEggg = EgggUtil.getTypeEggg(valueType);

            Map map = null;
            if (target != Collections.EMPTY_MAP) {
                map = (Map) target;
            } else {
                map = new LinkedHashMap<>();
            }

            for (Map.Entry<String, ONode> kv : node.getObject().entrySet()) {
                if (kv.getKey().equals(opts0.getTypePropertyName())) {
                    continue;
                }

                //Map 的值是新对象，递归调用时 target 传 null
                Object k = decodeKey(kv.getKey(), keyTypeEggg);
                Object v = decodeValueFromNode(kv.getValue(), valueTypeEggg, null, null);
                map.put(k, v);
            }

            return map;
        } else {
            throw new CodecException("The type of node " + node.type() + " cannot be converted to map.");
        }
    }

    // Map键类型转换
    private Object decodeKey(String key, TypeEggg keyType) {
        if (keyType.getType() == String.class || keyType.getType() == Object.class) return key;
        if (keyType.getType() == Integer.class || keyType.getType() == int.class) return Integer.parseInt(key);
        if (keyType.getType() == Long.class || keyType.getType() == long.class) return Long.parseLong(key);
        if (keyType.getType().isEnum()) {
            ObjectDecoder decoder = opts0.getDecoder(keyType.getType());
            if (decoder == null) {
                return Enum.valueOf((Class<Enum>) keyType.getType(), key);
            } else {
                return decoder.decode(new DecodeContext(opts0, null, null, keyType), new ONode(opts0, key));
            }
        }

        throw new CodecException("Unsupported map key type: " + keyType.getType());
    }

    /**
     * 根据数据匹配度选择最适合的构造方法
     * 委托给 ClassEggg.matchBestCreator，利用预计算的参数别名快照进行高效匹配
     *
     * @param classEggg  类包装器
     * @param defCreator 默认创造器
     */
    private ConstrEggg matchBestConstructor(ClassEggg classEggg, ConstrEggg defCreator, ONode oNode) {
        if (defCreator == null || defCreator.isSecurity() || oNode.isObject() == false) {
            return defCreator;
        }

        if (oNode.getObjectUnsafe().size() == 0) {
            return defCreator;
        }

        return classEggg.matchConstrEggg(oNode.getObject().keySet(), defCreator);
    }

    public Object[] getConstrArgs(ConstrEggg constrEggg, ONode node) throws Throwable {
        //只有带参数的构造函（像 java record, kotlin data）
        Object[] argsV = new Object[constrEggg.getParamCount()];

        for (int j = 0; j < argsV.length; j++) {
            ParamEggg p = constrEggg.getParamEgggAt(j);
            argsV[j] = getParam(p, node);
        }

        return argsV;
    }

    private Object getParam(ParamEggg p, ONode node) throws Throwable {
        if (node.hasKey(p.getAlias())) {
            ONodeAttrHolder attr = p.getDigest();
            Object val = decodeValueFromNode(node.get(p.getAlias()), p.getTypeEggg(), null, attr);
            return val;
        } else {
            return null;
        }
    }

    /**
     * 确认节点类型
     */
    private TypeEggg confirmNodeType(ONode oRef, TypeEggg def) {
        TypeEggg type0 = resolveNodeType(oRef, def);

        if (type0 == null) {
            return def;
        }

        if (Throwable.class.isAssignableFrom(type0.getType())) {
            //如果有异常，则异常优先
            return type0;
        }

        if (def.getType() != Object.class
                && def.isInterface() == false
                && def.isAbstract() == false) {
            // 如果自定义了类型，则自定义的类型优先
            return def;
        }

        return type0;
    }

    /**
     * 分析节点类型
     */
    private TypeEggg resolveNodeType(ONode oRef, TypeEggg def) {
        if (oRef.isObject()) {
            String typeStr = null;
            if (isReadClassName(oRef)) {
                ONode n1 = oRef.getObject().get(opts0.getTypePropertyName());
                if (n1 != null) {
                    typeStr = n1.getString();
                }

                if (Asserts.isNotEmpty(typeStr)) {
                    if (opts0.isTypeBlocked(typeStr)) {
                        if (Decode_IgnoreError) {
                            return null;
                        } else {
                            throw new CodecException("Blocked type, class: " + typeStr);
                        }
                    }

                    Class<?> clz = opts0.loadClass(typeStr, true);
                    if (clz == null) {
                        if (Decode_IgnoreError) {
                            return null;
                        } else {
                            throw new CodecException("Unsupported type, class: " + typeStr);
                        }
                    } else {
                        return EgggUtil.getTypeEggg(clz);
                    }
                }
            }
        }

        if (def.getType() == Object.class) {
            if (oRef.isObject()) {
                return EgggUtil.getTypeEggg(LinkedHashMap.class);
            }

            if (oRef.isArray()) {
                return EgggUtil.getTypeEggg(ArrayList.class);
            }
        }

        return def;
    }

    /**
     * 是否读取类名字
     */
    private boolean isReadClassName(ONode node) {
        if (opts0.hasFeature(Feature.Read_AutoType) == false) {
            return false;
        }

        if (node.isObject() == false) {
            return false;
        }

        return true;
    }
}