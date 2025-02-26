package org.noear.snack.core.exts;

import org.noear.snack.core.utils.GenericUtil;

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
    private static Map<TypeDecl, ClassWrap> cached = new ConcurrentHashMap<>();

    /**
     * 根据clz获取一个ClassWrap
     */
    public static ClassWrap get(TypeDecl typeDecl) {
        ClassWrap cw = cached.get(typeDecl);
        if (cw == null) {
            cw = new ClassWrap(typeDecl);
            ClassWrap l = cached.putIfAbsent(typeDecl, cw);
            if (l != null) {
                cw = l;
            }
        }
        return cw;
    }

    //clz //与函数同名，_开头
    private final TypeDecl typeDecl;
    //clz.all_fieldS
    private final Map<String, FieldWrap> _fieldAllWraps;
    private final Map<String, Method> _propertyAll;

    //for record
    private boolean _recordable;
    private Constructor _recordConstructor;
    private Parameter[] _recordParams;

    private boolean _isMemberClass;


    protected ClassWrap(TypeDecl typeDecl) {
        this.typeDecl = typeDecl;

        _recordable = true;

        _isMemberClass = typeDecl.getType().isMemberClass();
        _fieldAllWraps = new LinkedHashMap<>();
        _propertyAll = new LinkedHashMap<>();

        scanAllFields(typeDecl, _fieldAllWraps::containsKey, _fieldAllWraps::put);

        for (Method m : typeDecl.getType().getMethods()) {
            if (m.getName().startsWith("set") && m.getName().length() > 3 && m.getParameterCount() == 1) {
                String name = m.getName().substring(3);
                name = name.substring(0, 1).toLowerCase() + name.substring(1);
                _propertyAll.put(name, m);
            }
        }

        if (_fieldAllWraps.size() == 0) {
            _recordable = false;
        }

        //支持 kotlin data 类型
        Constructor<?>[] constructors = typeDecl.getType().getConstructors();

        if (constructors.length > 0) {
            if (_recordable) {
                //如果合字段只读
                _recordConstructor = constructors[constructors.length - 1];
                _recordParams = _recordConstructor.getParameters();

                if (_recordParams.length == 0) {
                    _recordable = false;
                }
            } else {
                if (constructors.length == 1) {
                    if (constructors[0].getParameterCount() > 0) {
                        //如果合字段只读
                        _recordConstructor = constructors[0];
                        _recordParams = _recordConstructor.getParameters();
                    }
                }
            }
        } else {
            _recordable = false;
        }
    }

    public TypeDecl typeDecl() {
        return typeDecl;
    }


    public Collection<FieldWrap> fieldAllWraps() {
        return _fieldAllWraps.values();
    }

    public FieldWrap getFieldWrap(String fieldName) {
        return _fieldAllWraps.get(fieldName);
    }

    public Method getProperty(String name) {
        return _propertyAll.get(name);
    }


    //for record
    public boolean recordable() {
        return _recordable;
    }

    public Constructor recordConstructor() {
        return _recordConstructor;
    }

    public Parameter[] recordParams() {
        return _recordParams;
    }

    /**
     * 扫描一个类的所有字段
     */
    private void scanAllFields(TypeDecl typeDecl, Predicate<String> checker, BiConsumer<String, FieldWrap> consumer) {
        if (typeDecl == null) {
            return;
        }

        if (typeDecl.getType().isInterface()) {
            return;
        }

        for (Field f : typeDecl.getType().getDeclaredFields()) {
            int mod = f.getModifiers();

            if (!Modifier.isStatic(mod)
                    && !Modifier.isTransient(mod)) {

                if (_isMemberClass && f.getName().equals("this$0")) {
                    continue;
                }

                if (checker.test(f.getName()) == false) {
                    _recordable &= Modifier.isFinal(mod);
                    consumer.accept(f.getName(), new FieldWrap(typeDecl, f, Modifier.isFinal(mod)));
                }
            }
        }

        Class<?> sup = typeDecl.getType().getSuperclass();
        if (sup != null && sup != Object.class) {
            Type supInfo = GenericUtil.reviewType(typeDecl.getType().getGenericSuperclass(), typeDecl.getGenericType());
            scanAllFields(new TypeDecl(sup, supInfo), checker, consumer);
        }
    }
}