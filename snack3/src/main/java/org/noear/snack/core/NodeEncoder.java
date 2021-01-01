package org.noear.snack.core;

import org.noear.snack.ONode;

/**
 * ONode 编码（用于自定义编码）
 *
 * @author noear 2021/1/1 created
 * */
public interface NodeEncoder {
    ONode toNode();
}
