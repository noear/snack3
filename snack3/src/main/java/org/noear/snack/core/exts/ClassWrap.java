package org.noear.snack.core.exts;

import org.noear.snack.ONode;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * @author noear 2021/1/1 created
 */
public class ClassWrap {
    private static Map<Class<?>, ClassWrap> cached = new ConcurrentHashMap<>();

    /**
     * 根据clz获取一个ClassWrap
     */
    public static ClassWrap get(Class<?> clz) {
        ClassWrap cw = cached.get(clz);
        if (cw == null) {
            cw = new ClassWrap(clz);
            ClassWrap l = cached.putIfAbsent(clz, cw);
            if (l != null) {
                cw = l;
            }
        }
        return cw;
    }

    //clz //与函数同名，_开头
    private final Class<?> _clz;
    //clz.all_fieldS
    private final Collection<FieldWrap> _fieldAllWraps;

    //for record
    private boolean _recordable;
    private Constructor _recordConstructor;
    private Parameter[] _recordParams;

    private boolean _isMemberClass;


    protected ClassWrap(Class<?> clz) {
        _clz = clz;
        _recordable = true;

        _isMemberClass = clz.isMemberClass();

        Map<String, FieldWrap> map = new LinkedHashMap<>();
        scanAllFields(clz, map::containsKey, map::put);

        _fieldAllWraps = map.values();

        if (_fieldAllWraps.size() == 0) {
            _recordable = false;
        }

        //支持 kotlin data 类型
        Constructor<?>[] constructors = clz.getConstructors();

        if (constructors.length == 1) {
            if (_recordable) {
                //如果合字段只读
                _recordConstructor = constructors[0];
                _recordParams = _recordConstructor.getParameters();
            }else{
                if (constructors[0].getParameterCount() > 0) {
                    //如果合字段只读
                    _recordConstructor = constructors[0];
                    _recordParams = _recordConstructor.getParameters();
                }
            }
        }
    }

    public Class<?> clz() {
        return _clz;
    }


    public Collection<FieldWrap> fieldAllWraps() {
        return _fieldAllWraps;
    }


    //for record
    public boolean recordable() {
        return _recordable;
    }

    public Constructor recordConstructor(){
        return _recordConstructor;
    }

    public Parameter[] recordParams(){
        return _recordParams;
    }

    /**
     * 扫描一个类的所有字段
     */
    private void scanAllFields(Class<?> clz, Predicate<String> checker, BiConsumer<String, FieldWrap> consumer) {
        if (clz == null) {
            return;
        }

        for (Field f : clz.getDeclaredFields()) {
            int mod = f.getModifiers();

            if (!Modifier.isStatic(mod)
                    && !Modifier.isTransient(mod)) {

                if(_isMemberClass && f.getName().equals("this$0")){
                    continue;
                }

                if (checker.test(f.getName()) == false) {
                    _recordable &= Modifier.isFinal(mod);
                    consumer.accept(f.getName(), new FieldWrap(clz, f, Modifier.isFinal(mod)));
                }
            }
        }

        Class<?> sup = clz.getSuperclass();
        if (sup != Object.class) {
            scanAllFields(sup, checker, consumer);
        }
    }
}
