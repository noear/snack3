package org.noear.snack.core.utils;

import org.noear.snack.ONode;
import org.noear.snack.core.Constants;
import org.noear.snack.core.Context;
import org.noear.snack.to.Toer;

public final class NodeUtil {
    /** 将 str 转换为 ONode */
    public static ONode fromStr(Constants cfg, String str) throws Exception{
        return new Context(cfg, str, true).handle(cfg.stringFromer).node;
    }

    public static ONode fromObj(Constants cfg, Object obj) throws Exception {
        return new Context(cfg, obj, false).handle(cfg.objectFromer).node;
    }

    /** 将 ONode 转换为 str */
    public static String toStr(Constants cfg, ONode node, Toer toer) {
        try {
            return new Context(cfg, node, String.class).handle(toer).text;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public static Object toObj(Constants cfg, ONode node, Class<?> toclz, Toer toer) {
        try {
            return new Context(cfg, node, toclz).handle(toer).object;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

}
