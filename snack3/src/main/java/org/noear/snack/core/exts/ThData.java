package org.noear.snack.core.exts;

import org.noear.snack.core.JsonPath;
import org.noear.snack.from.JsonFromer;
import org.noear.snack.to.JsonToer;

import java.util.function.Supplier;

/** 线程数据（用于复用） */
public class ThData<T> extends ThreadLocal<T> {
    private Supplier<T> _def;
    public ThData(Supplier<T> def){
        _def = def;
    }

    @Override
    protected T initialValue() {
        return _def.get();
    }

    /**
     * 清空线程缓存
     * */
    public static void clear(){
        JsonPath.clear();
        JsonFromer.clear();
        JsonToer.clear();
    }
}
