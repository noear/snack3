package org.noear.snack.core;

import org.noear.snack.ONode;

import java.lang.reflect.Type;

/**
 * ONode 解码器实体
 *
 * @author noear 2021/10/11 created
 */
public class NodeDecoderEntity<T> implements NodeDecoder<T> {
    private final Class<T> type;
    private final NodeDecoder<T> decoder;

    public NodeDecoderEntity(Class<T> type, NodeDecoder<T> decoder) {
        this.type = type;
        this.decoder = decoder;
    }

    public boolean isDecodable(Class<?> cls) {
        return type.isAssignableFrom(cls);
    }

    @Override
    public T decode(ONode source, Type type) {
        return decoder.decode(source, type);
    }
}
