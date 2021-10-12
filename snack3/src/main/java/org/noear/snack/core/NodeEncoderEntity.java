package org.noear.snack.core;

import org.noear.snack.ONode;

/**
 * ONode 编码器实体
 *
 * @author noear
 * @since 3.2
 */
public class NodeEncoderEntity<T> implements NodeEncoder<T> {
    private final Class<T> type;
    private final NodeEncoder<T> encoder;

    public NodeEncoderEntity(Class<T> type, NodeEncoder<T> encoder) {
        this.type = type;
        this.encoder = encoder;
    }

    public boolean isEncodable(Class<?> cls) {
        return type.isAssignableFrom(cls);
    }

    @Override
    public void encode(T source, ONode target) {
        encoder.encode(source, target);
    }
}
