package org.noear.snack.core;


import org.noear.snack.ONode;

/**
 * ONode 翻译工厂
 *
 * @author noear
 * @since 3.2
 */
public interface NodeTranslateFactory {
    /**
     * 翻译
     */
    void translate(String translator, String fieldKey, Object fieldVal, ONode node);
}
