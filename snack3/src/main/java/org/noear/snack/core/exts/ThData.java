package org.noear.snack.core.exts;

/** 线程数据（用于复用） */
public class ThData<T> extends ThreadLocal<T> {
    private Fun0<T> _def;
    public ThData(Fun0<T> def){
        _def = def;
    }

    @Override
    protected T initialValue() {
        return _def.run();
    }
}
