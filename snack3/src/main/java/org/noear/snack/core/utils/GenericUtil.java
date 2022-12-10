package org.noear.snack.core.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2022/12/10 created
 */
public class GenericUtil {

    private static final Map<Type, Map<TypeVariable, Type>> genericInfoCached = new HashMap<>();

    /**
     * 获取泛型变量和泛型实际类型的对应关系Map
     *
     * @param type 被解析的包含泛型参数的类
     * @return 泛型对应关系Map
     */
    public static Map<TypeVariable, Type> getGenericInfo(Type type) {
        Map<TypeVariable, Type> tmp = genericInfoCached.get(type);
        if (tmp == null) {
            synchronized (type) {
                tmp = genericInfoCached.get(type);

                if (tmp == null) {
                    tmp = createTypeGenericMap(type);
                    genericInfoCached.put(type, tmp);
                }
            }
        }

        return tmp;
    }


    /**
     * 创建类中所有的泛型变量和泛型实际类型的对应关系Map
     *
     * @param type 被解析的包含泛型参数的类
     * @return 泛型对应关系Map
     */
    private static Map<TypeVariable, Type> createTypeGenericMap(Type type) {
        final Map<TypeVariable, Type> typeMap = new HashMap<>();

        // 按继承层级寻找泛型变量和实际类型的对应关系
        // 在类中，对应关系分为两类：
        // 1. 父类定义变量，子类标注实际类型
        // 2. 父类定义变量，子类继承这个变量，让子类的子类去标注，以此类推
        // 此方法中我们将每一层级的对应关系全部加入到Map中，查找实际类型的时候，根据传入的泛型变量，
        // 找到对应关系，如果对应的是继承的泛型变量，则递归继续找，直到找到实际或返回null为止。
        // 如果传入的非Class，例如TypeReference，获取到泛型参数中实际的泛型对象类，继续按照类处理
        while (null != type) {
            final ParameterizedType parameterizedType = toParameterizedType(type);
            if(null == parameterizedType){
                break;
            }
            final Type[] typeArguments = parameterizedType.getActualTypeArguments();
            final Class<?> rawType = (Class<?>) parameterizedType.getRawType();
            final TypeVariable[] typeParameters = rawType.getTypeParameters();

            Type value;
            for (int i = 0; i < typeParameters.length; i++) {
                value = typeArguments[i];
                // 跳过泛型变量对应泛型变量的情况
                if(false == value instanceof TypeVariable){
                    typeMap.put(typeParameters[i], value);
                }
            }

            type = rawType;
        }

        return typeMap;
    }


    /**
     * 将{@link Type} 转换为{@link ParameterizedType}<br>
     * {@link ParameterizedType}用于获取当前类或父类中泛型参数化后的类型<br>
     * 一般用于获取泛型参数具体的参数类型，例如：
     *
     * <pre>
     * class A&lt;T&gt;
     * class B extends A&lt;String&gt;
     * </pre>
     * <p>
     * 通过此方法，传入B.class即可得到B{@link ParameterizedType}，从而获取到String
     *
     * @param type {@link Type}
     * @return {@link ParameterizedType}
     * @since 4.5.2
     */
    public static ParameterizedType toParameterizedType(Type type) {
        ParameterizedType result = null;
        if (type instanceof ParameterizedType) {
            result = (ParameterizedType) type;
        } else if (type instanceof Class) {
            final Class<?> clazz = (Class<?>) type;
            Type genericSuper = clazz.getGenericSuperclass();
            if (null == genericSuper || Object.class.equals(genericSuper)) {
                // 如果类没有父类，而是实现一些定义好的泛型接口，则取接口的Type
                final Type[] genericInterfaces = clazz.getGenericInterfaces();
                if (genericInterfaces != null && genericInterfaces.length > 0) {
                    // 默认取第一个实现接口的泛型Type
                    genericSuper = genericInterfaces[0];
                }
            }
            result = toParameterizedType(genericSuper);
        }
        return result;
    }
}
