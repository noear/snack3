package org.noear.snack.core;

import org.noear.snack.ONode;

import java.lang.reflect.Type;

/**
 * ONode 解码（用于控制自定义解码）
 *
 * @author noear
 * @since 3.2
 */
public interface NodeDecoder<T> {
    T decode(ONode node, Type type);
}
