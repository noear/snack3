package org.noear.snack.core;

import org.noear.snack.ONode;

/**
 * ONode 编码（用于自定义编码）
 *
 * @author noear
 * @since 3.2
 * */
public interface NodeEncoder<T> {
    void encode(T data, ONode node);
}
