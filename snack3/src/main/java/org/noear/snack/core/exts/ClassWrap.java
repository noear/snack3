package org.noear.snack.core.exts;

import org.noear.snack.annotation.NodeName;
import org.noear.snack.core.utils.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
    private String _name;


    protected ClassWrap(Class<?> clz) {
        _clz = clz;


        Map<String, FieldWrap> map = new LinkedHashMap<>();
        scanAllFields(clz, map::containsKey, map::put);

        _fieldAllWraps = map.values();

        NodeName anno = clz.getAnnotation(NodeName.class);
        if (anno != null) {
            _name = anno.value();
        }

        if (StringUtil.isEmpty(_name)) {
            _name = clz.getName();
        }
    }

    public Class<?> clz() {
        return _clz;
    }

    public String name(){
        return _name;
    }

    public Collection<FieldWrap> fieldAllWraps(){
        return _fieldAllWraps;
    }



    /**
     * 扫描一个类的所有字段
     */
    private static void scanAllFields(Class<?> clz, Predicate<String> checker, BiConsumer<String, FieldWrap> consumer) {
        if (clz == null) {
            return;
        }

        for (Field f : clz.getDeclaredFields()) {
            int mod = f.getModifiers();

            if (!Modifier.isTransient(mod) && !Modifier.isStatic(mod)) {
                f.setAccessible(true);

                if (checker.test(f.getName()) == false) {
                    consumer.accept(f.getName(), new FieldWrap(clz, f));
                }
            }
        }

        Class<?> sup = clz.getSuperclass();
        if (sup != Object.class) {
            scanAllFields(sup, checker, consumer);
        }
    }
}
