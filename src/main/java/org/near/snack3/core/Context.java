package org.near.snack3.core;

import org.near.snack3.ONode;

/**
 * 处理上下文对象
 * */
public class Context {
    /**
     * 已处理状态
     */
    public boolean handled = false;

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
    public Class<?> type;

    /**
     * 用于来源处理的构造
     * */
    public Context(Constants config, Object from) {
        this.config = config;

        if (from == null) {
            return;
        }

        if (from instanceof String) {
            this.text = ((String) from);//.trim();
        } else {
            this.object = from;
        }
    }

    /**
     * 用于去处的构造
     * */
    public Context(Constants config, ONode node, Class<?> to) {
        this.config = config;

        if (to == null) {
            return;
        }

        this.node = node;
        this.type = to;
    }

    /**
     * 使用代理对当前上下文进行处理
     */
    public Context handle(Handler handler) throws Exception{
        handler.handle(this);
        return this;
    }
}
