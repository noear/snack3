package org.noear.snack.core.exts;

/**
 * 2参动作接口（用于lambda表达式）
 */
public interface Act2<T1,T2> {
    void run(T1 t1, T2 t2);
}
