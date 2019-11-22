package org.noear.snack.core;

import org.noear.snack.ONode;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 处理上下文对象
 * */
public class Context {
    /** 常量配置 */
    public final Constants config;

    /** 来源 */
    public Object source;

    public ONode node;

    /** 目标 */
    public Object target;
    public Class<?> target_clz;
    public Type     target_type;

    /**
     * 用于来源处理的构造
     */
    public Context(Constants config, Object from) {
        this(config, from, from instanceof String);
    }

    public Context(Constants config, Object from, boolean fromIsStr) {
        this.config = config;

        if (from == null) {
            return;
        }

        if (fromIsStr) {
            this.source = ((String) from).trim(); //不能去掉 .trim()
        } else {
            this.source = from;
        }
    }

    /**
     * 用于去处的构造
     */
    public Context(Constants config, ONode node, Class<?> target_type) {
        this.config = config;
        this.node = node;

        if(target_type == null){
            return;
        }

        Type type = target_type.getGenericSuperclass();
        if(type instanceof ParameterizedType){
            this.target_type = type;
            this.target_clz = (Class<?>) ((ParameterizedType) type).getRawType();
        }else{
            this.target_type = target_type;
            this.target_clz = target_type;
        }
    }

    /**
     * 使用代理对当前上下文进行处理
     */
    public Context handle(Handler handler) throws Exception {
        handler.handle(this);
        return this;
    }
}
