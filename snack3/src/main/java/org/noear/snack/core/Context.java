package org.noear.snack.core;

import org.noear.snack.ONode;

/**
 * 处理上下文对象
 * */
public class Context {
    /**
     * 前量配置
     */
    public final Constants config;

    public Object source;

    public ONode node;

    public Object target;
    public Class<?> target_type;

    /**
     * 用于来源处理的构造
     * */
    public Context(Constants config, boolean fromIsStr,  Object from) {
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
     * */
    public Context(Constants config, ONode node, Class<?> target_type) {
        this.config = config;
        this.node = node;
        this.target_type = target_type;
    }

    /**
     * 使用代理对当前上下文进行处理
     */
    public Context handle(Handler handler) throws Exception{
        handler.handle(this);
        return this;
    }
}
