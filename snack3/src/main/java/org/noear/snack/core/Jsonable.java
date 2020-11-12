package org.noear.snack.core;

import org.noear.snack.ONode;

/**
 * Jsonable 接口
 * */
public interface Jsonable {
    ONode toJsonNode();
    default String toJson(){
        return toJsonNode().toJson();
    }

    void fromJsonNode(ONode node);
    default void fromJson(String json){
        fromJsonNode(ONode.loadStr(json));
    }
}
