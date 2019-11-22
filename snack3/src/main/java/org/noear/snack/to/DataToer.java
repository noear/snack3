package org.noear.snack.to;

import org.noear.snack.ONode;
import org.noear.snack.OValue;
import org.noear.snack.core.Context;

import java.util.*;

/**
 * 对象转换器
 * <p>
 * 将 ONode 转为 Map Object
 */
public class DataToer implements Toer {
    @Override
    public void handle(Context ctx) {
        ctx.target = analyse(ctx.node);
    }

    private Object analyse(ONode o) {
        if (o == null) {
            return null;
        }

        switch (o.nodeType()) {
            case Value:
                OValue d = o.nodeData().value;
                switch (d.type()) {
                    case Integer:
                        return d.getRawInteger();
                    case Decimal:
                        return d.getRawDecimal();
                    case Boolean:
                        return d.getRawBoolean();
                    case DateTime:
                        return d.getRawDate();
                    case Bignumber:
                        return d.getRawBignumber();
                    case Null:
                        return null;
                    default:
                        return d.getString();
                }
            case Object:
                Map<String,Object> map = new HashMap<>();
                o.forEach((k,v)->{
                    map.put(k, analyse(v));
                });
                return map;
            case Array:
                List<Object> ary = new ArrayList<>();
                o.forEach((v)->{
                    ary.add(analyse(v));
                });
                return ary;
            default:
                return null;
        }
    }
}
