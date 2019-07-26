package org.noear.snack.to;

import org.noear.snack.ONode;
import org.noear.snack.ONodeData;
import org.noear.snack.OValue;
import org.noear.snack.OValueType;
import org.noear.snack.core.Constants;
import org.noear.snack.core.Context;
import org.noear.snack.core.exts.EnumWrap;
import org.noear.snack.core.exts.FieldWrap;
import org.noear.snack.core.utils.BeanUtil;
import org.noear.snack.core.utils.StringUtil;
import org.noear.snack.core.utils.TypeUtil;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 对象转换器
 * <p>
 * 将 ONode 转为 Map Object
 */
public class DataToer implements Toer {
    @Override
    public void handle(Context ctx) {
        ctx.object = analyse(ctx.node);
    }

    private Object analyse(ONode o) {
        if (o == null) {
            return null;
        }

        switch (o.nodeType()) {
            case Value:
                OValue d = o.getData().value;
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
