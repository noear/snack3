package org.noear.snack.core.exts;

/**
 * 1参动作接口（用于lambda表达式）
 */
public interface Act1<T> {
    void run(T t);
}
