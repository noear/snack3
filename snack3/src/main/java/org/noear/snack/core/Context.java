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

    /**
     * 文本
     */
    public String text;
    /**
     * ONode
     */
    public ONode node;
    /**
     * 对象
     */
    public Object object;
    /**
     * 目标类型
     */
    public Class<?> target_type;

    public Context(Constants config, Object from){
        this.config = config;

        if (from == null) {
            return;
        }

        if (from instanceof String) {
            this.text = ((String) from).trim(); //不能去掉 .trim()
        } else {
            this.object = from;
        }
    }
    /**
     * 用于来源处理的构造
     * */
    public Context(Constants config, Object from , boolean fromIsStr) {
        this.config = config;

        if (from == null) {
            return;
        }

        if (fromIsStr) {
            this.text = ((String) from).trim(); //不能去掉 .trim()
        } else {
            this.object = from;
        }
    }

    /**
     * 用于去处的构造
     * */
    public Context(Constants config, ONode node, Class<?> to) {
        this.config = config;
        this.node = node;
        this.target_type = to;
    }

    /**
     * 使用代理对当前上下文进行处理
     */
    public Context handle(Handler handler) throws Exception{
        handler.handle(this);
        return this;
    }
}
