package org.near.snack3.core.utils;

import org.near.snack3.ONode;
import org.near.snack3.core.Constants;
import org.near.snack3.core.Context;
import org.near.snack3.from.Fromer;
import org.near.snack3.to.Toer;

public final class NodeUtil {
    /** 将 str 转换为 ONode */
    public static ONode fromStr(Constants cfg, String str) throws Exception{
        Context ctx = new Context(cfg, str).handle(cfg.stringFromer);

        return ctx.node;
    }

    public static ONode fromObj(Constants cfg, Object obj){
        try {
            return new Context(cfg, obj).handle(cfg.objectFromer).node;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
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
