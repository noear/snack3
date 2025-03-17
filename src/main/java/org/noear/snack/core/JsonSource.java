package org.noear.snack.core;

import org.noear.snack.ONode;

/**
 * @author noear 2025/3/16 created
 */
public class JsonSource {
    public ONode parent;
    public String key;
    public int index;

    public JsonSource(ONode parent, String key, int index) {
        this.parent = parent;
        this.key = key;
        this.index = index;

    }
}
