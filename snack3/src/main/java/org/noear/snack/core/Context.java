package org.noear.snack.core;

import org.noear.snack.ONode;
import org.noear.snack.exception.SnackException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 处理上下文对象
 * */
public class Context {
    /**
     * 选项
     */
    public final Options options;

    /**
     * 来源
     */
    public Object source;

    /**
     * 目标
     */
    public Object target;
    public Class<?> target_clz;
    public Type target_type;

    /**
     * 用于来源处理的构造
     */
    public Context(Options options, Object from) {
        this.options = options;
        this.source = from;
    }

    /**
     * 用于去处的构造
     */
    public Context(Options options, ONode node, Type type0) {
        this.options = options;
        this.source = node;

        if (type0 == null) {
            return;
        }

        if (type0 instanceof Class) {
            //for class
            //
            Class<?> clz = (Class<?>) type0;

            if (TypeRef.class.isAssignableFrom(clz)) {
                Type superClass = clz.getGenericSuperclass();
                Type type = (((ParameterizedType) superClass).getActualTypeArguments()[0]);

                initType(type);
                return;
            }

            if (clz.getName().indexOf("$") > 0) {
                // 临时类：(new ArrayList<UserModel>(){}).getClass(); (new UserModel(){}).getClass();
                // => features.GenericTest$6;
                // 内部类：Server.One.class
                // => _model3.Server$One
                if (clz.isMemberClass()) {
                    initType(clz, clz);
                } else {
                    initType(clz.getGenericSuperclass());
                }
            } else {
                initType(clz, clz);
            }
        } else {
            //for type
            //
            initType(type0);
        }
    }

    private void initType(Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;

            initType(type, (Class<?>) pType.getRawType());
        } else {
            initType(type, (Class<?>) type);
        }
    }

    private void initType(Type type, Class<?> clz) {
        target_type = type;
        target_clz = clz;

        if (clz != null && clz != Object.class) {
            if (options.getClassLoader() == null) {
                options.setClassLoader(clz.getClassLoader());
            }
        }
    }

    /**
     * 使用代理对当前上下文进行处理
     */
    public Context handle(Handler handler) {
        try {
            handler.handle(this);
            return this;
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new SnackException(e);
        }
    }
}
