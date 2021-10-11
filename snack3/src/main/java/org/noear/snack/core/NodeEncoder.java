package org.noear.snack.core;

import org.noear.snack.ONode;

import java.io.IOException;

/**
 * ONode 编码（用于自定义编码）
 *
 * @author noear 2021/1/1 created
 * */
public interface NodeEncoder<T> {
    void encode(T source, ONode target);
}
