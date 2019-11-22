package org.noear.snack.core.utils;

import org.noear.snack.ONode;
import org.noear.snack.core.Constants;
import org.noear.snack.core.Context;
import org.noear.snack.core.Handler;
import org.noear.snack.to.Toer;

public final class NodeUtil {
    /**
     * 将 str 转换为 ONode
     */
    public static ONode fromStr(String str)  {
        return fromStr(Constants.def, str);
    }

    public static ONode fromStr(Constants cfg, String str) {
        return (ONode) do_handler(new Context(cfg, str, true), cfg.stringFromer);
        //return new Context(cfg, str, true).handle(cfg.stringFromer).node;
    }

    public static ONode fromObj(Object obj)  {
        return fromObj(Constants.def, obj);
    }

    public static ONode fromObj(Constants cfg, Object obj)  {
        return (ONode) do_handler(new Context(cfg, obj, false), cfg.objectFromer);
        //return new Context(cfg, obj, false).handle(cfg.objectFromer).node;
    }



    /**
     * 将 ONode 转换为 str
     */
    public static String toStr(ONode node, Toer toer) {
        return toStr(Constants.def, node, toer);
    }

    public static String toStr(Constants cfg, ONode node, Toer toer) {
        return (String) do_handler(new Context(cfg, node, null), toer);
    }

    public static Object toObj(ONode node, Class<?> toclz, Toer toer) {
        return toObj(Constants.def, node, toclz, toer);
    }

    public static Object toObj(Constants cfg, ONode node, Class<?> toclz, Toer toer) {
        return do_handler(new Context(cfg, node, toclz), toer);
    }

    private static Object do_handler(Context ctx, Handler handler) {
        return ctx.handle(handler).target;
    }
}
