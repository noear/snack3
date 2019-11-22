package org.noear.snack.core.utils;

import org.noear.snack.ONode;
import org.noear.snack.core.Constants;
import org.noear.snack.core.Context;
import org.noear.snack.core.Handler;
import org.noear.snack.from.Fromer;
import org.noear.snack.to.Toer;

public final class NodeUtil {
    /**
     * 将 str 转换为 ONode
     */
    public static ONode fromStr(String str, Fromer fromer) {
        return fromStr(Constants.def, str, fromer);
    }

    public static ONode fromStr(Constants cfg, String str, Fromer fromer) {
        return (ONode) do_handler(new Context(cfg, str, true), fromer);
    }

    public static ONode fromObj(Object obj, Fromer fromer) {
        return fromObj(Constants.def, obj, fromer);
    }

    public static ONode fromObj(Constants cfg, Object obj, Fromer fromer) {
        return (ONode) do_handler(new Context(cfg, obj, false), fromer);
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
