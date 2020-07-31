package benchmark;

import org.junit.Test;

import java.lang.reflect.Array;
import java.util.*;

public class BaseSpeedTest {

    @Test
    public void test01(){
        Map<String, Object> obj = new LinkedHashMap<>();
        obj.put("x", 1);

        long start = System.currentTimeMillis();
        for(int i=0,len=100000 * 20; i<len; i++) {
            if(obj instanceof  Map){

            }
        }
        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test02(){
        Map<String, Object> obj = new LinkedHashMap<>();
        obj.put(String.class.getName(), 1);
        String key =Integer.class.getName();

        long start = System.currentTimeMillis();
        for(int i=0,len=100000; i<len; i++) {
            obj.get(key);
        }
        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test03(){
        Map<Class<?>, Object> obj = new LinkedHashMap<>();
        obj.put(String.class, 1);

        long start = System.currentTimeMillis();
        for(int i=0,len=100000; i<len; i++) {
            obj.get(Integer.class);
        }
        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test11(){
        String[] obj = new String[2];
        obj[0] = "xxx";
        obj[1] = "yyyyyyy";

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            if(obj instanceof Object[]){
                for(Object c : obj){
                    if(c!=null){

                    }
                }
            }
        }
        long times = System.currentTimeMillis() - start;

        System.out.println(times);

    }

    @Test
    public void test12(){
        String[] obj = new String[2];
        obj[0] = "xxx";
        obj[1] = "yyyyyyy";

        Class<?> clz = obj.getClass();

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            if(clz.isArray()){
                int len2 = Array.getLength(obj);
                for(int j=0; j<len2; j++){
                    Object o = Array.get(obj, j);
                    if(o!=null){

                    }
                }
            }
        }
        long times = System.currentTimeMillis() - start;

        System.out.println(times);

    }

    @Test
    public void test13(){
        String[] obj = new String[2];
        obj[0] = "xxx";
        obj[1] = "yyyyyyy";


        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            Class<?> clz = obj.getClass();
            if(clz.isArray()){
                int len2 = Array.getLength(obj);
                for(int j=0; j<len2; j++){
                    Object o = Array.get(obj, j);
                    if(o!=null){

                    }
                }
            }
        }
        long times = System.currentTimeMillis() - start;

        System.out.println(times);

    }
}
