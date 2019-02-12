package org.near.snack3.from;


import org.near.snack3.ONode;
import org.near.snack3.core.Constants;
import org.near.snack3.core.Context;
import org.near.snack3.core.Feature;
import org.near.snack3.core.exts.FieldWrap;
import org.near.snack3.core.utils.BeanUtil;

import javax.xml.datatype.XMLGregorianCalendar;
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
public class ObjectFromer3 implements Fromer {
    private static final Map<String,Worker> workerLib = new ConcurrentHashMap<>();
    private static final Worker nullWorker = ((cfg,rst, obj) -> {});
    @Override
    public void handle(Context ctx) {
        if (null != ctx.object) {
            ctx.node = _run(ctx.config, ctx.object);
            ctx.handled = true;
        }
    }

    public ObjectFromer3(){
        _put(ONode.class, ((cfg, rst, obj)->rst.val(obj)));
        _put(String.class, ((cfg, rst, obj) -> rst.val().setString((String) obj)));
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
    private void _put(Class<?> clz, Worker worker){
        String key = clz.getName();

        workerLib.put(key, worker);
    }

    /** 运行类操作者 */
    private ONode _run(Constants cfg, Object obj){
        if(obj == null){
            return ONode.Null;
        }else{
            ONode rst = new ONode(cfg);
            _get(obj.getClass()).run(cfg, rst,obj);
            return rst;
        }
    }

    private void analyse(Class<?> clz)  {

        if(ONode.class.isAssignableFrom(clz)){
            _put(clz, ((cfg, rst, obj)->rst.val(obj)));
        } else if (String.class.isAssignableFrom(clz)) {
            _put(clz, ((cfg, rst, obj) -> rst.val().setString((String) obj)));
        } else if (Date.class.isAssignableFrom(clz)) {
            _put(clz, ((cfg, rst, obj)->rst.val().setDate((Date) obj)));
        } else if (Integer.class.isAssignableFrom(clz) || Integer.TYPE == clz) {
            _put(clz, ((cfg, rst, obj) -> rst.val().setInteger((Integer) obj)));
        } else if (Long.class.isAssignableFrom(clz) || Long.TYPE == clz) {
            _put(clz, ((cfg, rst, obj)->rst.val().setInteger((Long) obj)));
        } else if (Float.class.isAssignableFrom(clz) || Float.TYPE == clz) {
            _put(clz, ((cfg, rst, obj) -> rst.val().setDecimal((Float) obj)));
        } else if (Double.class.isAssignableFrom(clz) || Double.TYPE == clz) {
            _put(clz, (cfg, rst, obj) -> rst.val().setDecimal((Double) obj));
        } else if (Short.class.isAssignableFrom(clz) || Short.TYPE == clz) { //新补充的类型
            _put(clz, (cfg, rst, obj) -> rst.val().setInteger((Short) obj));
        } else if (Character.class.isAssignableFrom(clz) || Character.TYPE == clz) { //新补充的类型
            _put(clz,(cfg, rst, obj) -> rst.val().setInteger((Character) obj));
        } else if (Boolean.class.isAssignableFrom(clz) || Boolean.TYPE == clz) {
            _put(clz, (cfg, rst, obj) -> rst.val().setBool((boolean) obj));
        } else if (Number.class.isAssignableFrom(clz)) {
            _put(clz, (cfg, rst, obj) -> rst.val().setBignumber((Number) obj));
        } else if(Throwable.class.isAssignableFrom(clz)){ //新补充的类型
            analyseBean(clz);
        } else if(clz.isArray()) { //新补充的类型::可适用任何数组
            _put(clz,(cfg, rst, obj) -> {
                rst.asArray();
                int len = Array.getLength(obj);
                for (int i=0; i<len; i++) {
                    Object o = Array.get(obj, i);
                    rst.addNode(_run(cfg, o));
                }
            });
        } else if(clz.isEnum()) { //新补充的类型
            _put(clz, (cfg, rst, obj) -> {
                Enum em = (Enum) obj;

                if (cfg.hasFeature(Feature.EnumUsingName)) {
                    rst.val().setString(em.name());
                } else {
                    rst.val().setInteger(em.ordinal());
                }
            });
        } else if (Map.class.isAssignableFrom(clz)){
            _put(clz, (cfg, rst, obj) -> {
                //为序列化添加特性支持
                if(cfg.hasFeature(Feature.WriteClassName)){
                    rst.set(cfg.type_key, clz.getName());
                }

                rst.asObject();
                Map map = ((Map)obj);
                for(Object k : map.keySet()) {
                    if (k != null) {
                        Object o = map.get(k);
                        rst.setNode(k.toString(),_run(cfg,o));
                    }
                }
            });

        } else if (Iterable.class.isAssignableFrom(clz)) {
            _put(clz,(cfg, rst, obj) -> {
                rst.asArray();
                for (Object o : ((Iterable) obj)) {
                    rst.addNode(_run(cfg, o));
                }
            });

        } else if(Enumeration.class.isAssignableFrom(clz)){ //新补充的类型
            _put(clz,(cfg, rst, obj) -> {
                rst.asArray();
                Enumeration o = (Enumeration)obj;
                while (o.hasMoreElements()){
                    rst.addNode(_run(cfg, o.nextElement()));
                }
            });
        } else {
            if(analyseOther(clz)==false){
                String clzName = clz.getName();

                if(clzName.startsWith("jdk.") == false && clzName.startsWith("java.") == false){
                    analyseBean(clz);
                }
            }
        }
    }

    private boolean analyseBean(Class<?> clz){
        //为序列化添加特性支持
        _put(clz, (cfg, rst, obj) -> {
            rst.asObject();

            if (cfg.hasFeature(Feature.WriteClassName)) {
                rst.set(cfg.type_key, clz.getName());
            }

            for (FieldWrap f : BeanUtil.getAllFields(clz)) {
                Object o = f.get(obj);
                if(o!=null) {
                    rst.set(f.name(), _run(cfg, o));
                }
            }
        });


        return true;
    }

    private boolean analyseOther(Class<?> clz){
        if(SimpleDateFormat.class.isAssignableFrom(clz)) {
            _put(clz,(cfg, rst, obj) -> {
                rst.set(cfg.type_key,clz.getName());
                rst.set("val", ((SimpleDateFormat) obj).toPattern());
            });

        } else if(clz == Class.class){
            _put(clz,(cfg, rst, obj) -> rst.val().setString(clz.getName()));
        } else if(InetSocketAddress.class.isAssignableFrom(clz)){
            _put(clz,(cfg, rst, obj) -> {
                InetSocketAddress address = (InetSocketAddress) obj;
                InetAddress inetAddress = address.getAddress();

                rst.set("address", inetAddress.getHostAddress());
                rst.set("port", address.getPort());
            });
        } else if(File.class.isAssignableFrom(clz)){
            _put(clz,(cfg, rst, obj) -> rst.val().setString(((File)obj).getPath()));
        } else if(InetAddress.class.isAssignableFrom(clz)){
            _put(clz,(cfg, rst, obj) -> rst.val().setString(((InetAddress) obj).getHostAddress()));
        } else if(TimeZone.class.isAssignableFrom(clz)){
            _put(clz,(cfg, rst, obj) -> rst.val().setString(((TimeZone)obj).getID()));
        } else if(Currency.class.isAssignableFrom(clz)){
            _put(clz,(cfg, rst, obj) -> rst.val().setString(((Currency)obj).getCurrencyCode()));
        } else if(Iterator.class.isAssignableFrom(clz)){
            _put(clz,(cfg, rst, obj) -> {
                rst.asArray();
                ((Iterator)obj).forEachRemaining(v->{
                    rst.addNode(_run(cfg,v));
                });
            });
        } else if(Map.Entry.class.isAssignableFrom(clz)){
            _put(clz,(cfg, rst, obj) -> {
                Map.Entry kv = (Map.Entry) obj;
                Object k = kv.getKey();
                Object v = kv.getValue();
                rst.asObject();
                if(k!=null){
                    rst.setNode(k.toString(), _run(cfg,v));
                }
            });
        } else if(Calendar.class.isAssignableFrom(clz)){
            _put(clz,(cfg, rst, obj) -> rst.val().setDate(((Calendar)obj).getTime()));
        } else if(XMLGregorianCalendar.class.isAssignableFrom(clz)){
            _put(clz,(cfg, rst, obj) -> rst.val().setDate(((XMLGregorianCalendar)obj).toGregorianCalendar().getTime()));
        } else if(Clob.class.isAssignableFrom(clz)){
            _put(clz,(cfg, rst, obj) -> rst.val().setString(BeanUtil.clobToString((Clob)obj)));
        } else if(Appendable.class.isAssignableFrom(clz)){
            _put(clz,(cfg, rst, obj) -> {
                rst.val().setString(obj.toString());
            });
        } else{
            return false;
        }

        return true;
    }

    public interface Worker{
        void run(Constants cfg, ONode rst, Object obj);
    }
}
