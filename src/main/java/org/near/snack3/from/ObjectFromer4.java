package org.near.snack3.from;


import org.near.snack3.ONode;
import org.near.snack3.core.Constants;
import org.near.snack3.core.Context;
import org.near.snack3.core.Feature;
import org.near.snack3.core.exts.FieldWrap;
import org.near.snack3.core.exts.ObjWrap;
import org.near.snack3.core.utils.BeanUtil;

import java.io.File;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对象转换器（将 java Object 转为 ONode）
 * */
public class ObjectFromer4 implements Fromer {
    private static final Map<String,Worker> workerLib = new ConcurrentHashMap<>();
    private static final Worker nullWorker = ((ObjWrap ow) -> {});
    @Override
    public void handle(Context ctx) {
        if (null != ctx.object) {
            ctx.node = _run(ctx.config, ctx.object);
            ctx.handled = true;
        }
    }

    //::工作库的操控
    /** 获取类操作者 */
    private Worker _get(Class<?> clz){
        String key = clz.getName();

        Worker worker = workerLib.get(key);
        if(worker == null){
            analyse(clz);
            worker = workerLib.get(key);
            if(worker == null){
                worker = nullWorker;
                _put(clz,worker);
            }
        }

        return worker;
    }
    /** 添加类操作者 */
    private Worker _put(Class<?> clz, Worker worker){
        String key = clz.getName();
        workerLib.put(key, worker);
        return worker;
    }

    /** 运行类操作者 */
    private ONode _run(Constants cfg, Object obj){
        if(obj == null){
            return ONode.Null;
        }else{
            ObjWrap ow = new ObjWrap(cfg,obj);
            _get(ow.clz).run(ow);
            return ow.rst;
        }
    }

    public ObjectFromer4(){
        _put(ONode.class, ((ObjWrap ow)-> ow.rst.val(ow.obj)));
        _put(String.class, ((ObjWrap ow) -> ow.rst.val().setString((String) ow.obj)));
        _put(Date.class, ((ObjWrap ow)-> ow.rst.val().setDate((Date) ow.obj)));

        _put(Character.class,(ObjWrap ow) -> ow.rst.val().setInteger((Character) ow.obj));
        _put(Character.TYPE,(ObjWrap ow) -> ow.rst.val().setInteger((Character) ow.obj));

        _put(Short.class, (ObjWrap ow) -> ow.rst.val().setInteger((Short) ow.obj));
        _put(Short.TYPE, (ObjWrap ow) -> ow.rst.val().setInteger((Short) ow.obj));
        _put(Integer.class, ((ObjWrap ow) -> ow.rst.val().setInteger((Integer) ow.obj)));
        _put(Integer.TYPE, ((ObjWrap ow) -> ow.rst.val().setInteger((Integer) ow.obj)));
        _put(Long.class, ((ObjWrap ow)-> ow.rst.val().setInteger((Long) ow.obj)));
        _put(Long.TYPE, ((ObjWrap ow)-> ow.rst.val().setInteger((Long) ow.obj)));

        _put(Float.class, ((ObjWrap ow) -> ow.rst.val().setDecimal((Float) ow.obj)));
        _put(Float.TYPE, ((ObjWrap ow) -> ow.rst.val().setDecimal((Float) ow.obj)));
        _put(Double.class, (ObjWrap ow) -> ow.rst.val().setDecimal((Double) ow.obj));
        _put(Double.TYPE, (ObjWrap ow) -> ow.rst.val().setDecimal((Double) ow.obj));

        _put(Boolean.class, (ObjWrap ow) -> ow.rst.val().setBool((boolean) ow.obj));
        _put(Boolean.TYPE, (ObjWrap ow) -> ow.rst.val().setBool((boolean) ow.obj));

        _put(Number.class, (ObjWrap ow) -> ow.rst.val().setBignumber((Number) ow.obj));

        _put(Class.class,(ObjWrap ow) -> ow.rst.val().setString(((Class) ow.obj).getName()));
    }

    private Worker analyse(Class<?> clz) {
        Worker worker = null;
        if (Throwable.class.isAssignableFrom(clz)) { //新补充的类型
            worker = _put(clz, beanWorker);
        } else if (clz.isArray()) { //新补充的类型::可适用任何数组
            worker = _put(clz, (ObjWrap ow) -> {
                ow.rst.asArray();
                int len = Array.getLength(ow.obj);
                for (int i = 0; i < len; i++) {
                    Object o = Array.get(ow.obj, i);
                    ow.rst.addNode(_run(ow.cfg, o));
                }
            });
        } else if (clz.isEnum()) { //新补充的类型
            worker = _put(clz, (ObjWrap ow) -> {
                Enum em = (Enum) ow.obj;

                if (ow.cfg.hasFeature(Feature.EnumUsingName)) {
                    ow.rst.val().setString(em.name());
                } else {
                    ow.rst.val().setInteger(em.ordinal());
                }
            });
        } else if (Map.class.isAssignableFrom(clz)) {
            worker = _put(clz, (ObjWrap ow) -> {
                //为序列化添加特性支持
                //为序列化添加特性支持
                if (ow.cfg.hasFeature(Feature.WriteClassName)) {
                    typeSet(ow.cfg, ow.rst, clz);
                }

                ow.rst.asObject();
                Map map = ((Map) ow.obj);
                for (Object k : map.keySet()) {
                    if (k != null) {
                        Object o = map.get(k);
                        ow.rst.setNode(k.toString(), _run(ow.cfg, o));
                    }
                }
            });

        } else if (Iterable.class.isAssignableFrom(clz)) {
            worker = _put(clz, (ObjWrap ow) -> {
                ow.rst.asArray();
                ONode ary =ow.rst;
                //为序列化添加特性支持
                if (ow.cfg.hasFeature(Feature.WriteClassName)) {
                    ow.rst.add(typeSet(ow.cfg,new ONode(), clz));
                    ary = ow.rst.addNew().asArray();
                }

                for (Object o : ((Iterable) ow.obj)) {
                    ary.addNode(_run(ow.cfg, o));
                }
            });

        } else if (Enumeration.class.isAssignableFrom(clz)) { //新补充的类型
            worker = _put(clz, (ObjWrap ow) -> {
                ow.rst.asArray();
                Enumeration o = (Enumeration) ow.obj;
                while (o.hasMoreElements()) {
                    ow.rst.addNode(_run(ow.cfg, o.nextElement()));
                }
            });
        } else {
            worker = analyseOther(clz);

            if (worker == null) {
                String clzName = clz.getName();

                if (clzName.startsWith("jdk.") == false && clzName.startsWith("java.") == false) {
                    worker = _put(clz, beanWorker);
                }
            }
        }

        if (worker == null) {
            return nullWorker;
        }
        else {
            return worker;
        }
    }

    private ONode typeSet(Constants cfg, ONode o, Class<?> clz) {
        return o.set(cfg.type_key, clz.getName());
    }

    private Worker analyseOther(Class<?> clz){
        if(SimpleDateFormat.class.isAssignableFrom(clz)) {
            return _put(clz,(ObjWrap ow) -> {
                ow.rst.set(ow.cfg.type_key,clz.getName());
                ow.rst.set("val", ((SimpleDateFormat) ow.obj).toPattern());
            });

        } else if(InetSocketAddress.class.isAssignableFrom(clz)){
            return _put(clz,(ObjWrap ow) -> {
                InetSocketAddress address = (InetSocketAddress) ow.obj;
                InetAddress inetAddress = address.getAddress();

                ow.rst.set("address", inetAddress.getHostAddress());
                ow.rst.set("port", address.getPort());
            });
        } else if(File.class.isAssignableFrom(clz)){
            return _put(clz,(ObjWrap ow) -> ow.rst.val().setString(((File)ow.obj).getPath()));
        } else if(InetAddress.class.isAssignableFrom(clz)){
            return _put(clz,(ObjWrap ow) -> ow.rst.val().setString(((InetAddress) ow.obj).getHostAddress()));
        } else if(TimeZone.class.isAssignableFrom(clz)){
            return _put(clz,(ObjWrap ow) -> ow.rst.val().setString(((TimeZone)ow.obj).getID()));
        } else if(Currency.class.isAssignableFrom(clz)){
            return _put(clz,(ObjWrap ow) -> ow.rst.val().setString(((Currency)ow.obj).getCurrencyCode()));
        } else if(Iterator.class.isAssignableFrom(clz)){
            return _put(clz,(ObjWrap ow) -> {
                ow.rst.asArray();
                ((Iterator)ow.obj).forEachRemaining(v->{
                    ow.rst.addNode(_run(ow.cfg,v));
                });
            });
        } else if(Map.Entry.class.isAssignableFrom(clz)){
            return _put(clz,(ObjWrap ow) -> {
                Map.Entry kv = (Map.Entry) ow.obj;
                Object k = kv.getKey();
                Object v = kv.getValue();
                ow.rst.asObject();
                if(k!=null){
                    ow.rst.setNode(k.toString(), _run(ow.cfg,v));
                }
            });
        } else if(Calendar.class.isAssignableFrom(clz)){
            return _put(clz,(ObjWrap ow) -> ow.rst.val().setDate(((Calendar)ow.obj).getTime()));
        } else if(Clob.class.isAssignableFrom(clz)){
            return _put(clz,(ObjWrap ow) -> ow.rst.val().setString(BeanUtil.clobToString((Clob)ow.obj)));
        } else if(Appendable.class.isAssignableFrom(clz)){
            return _put(clz,(ObjWrap ow) -> {
                ow.rst.val().setString(ow.obj.toString());
            });
        }

        return null;
    }

    private Worker beanWorker = (ObjWrap ow) -> {
        ow.rst.asObject();

        if (ow.cfg.hasFeature(Feature.WriteClassName)) {
            ow.rst.set(ow.cfg.type_key, ow.clz.getName());
        }

        for (FieldWrap f : BeanUtil.getAllFields(ow.clz)) {
            Object o = f.get(ow.obj);
            if(o!=null) {
                ow.rst.set(f.name(), _run(ow.cfg, o));
            }
        }
    };

    public interface Worker{
        void run(ObjWrap ow);
    }
}
