package speed;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import org.junit.Test;
import org.noear.snack.ONode;


public class SpeedFastjsonJsonPathTest {
    @Test
    public void test1(){
        //1000000=>529,546,539
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        JSONObject obj = JSON.parseObject(text);

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JSONPath.eval(obj,"$..a");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test2(){
        //1000000=>105,109
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        JSONObject obj = JSON.parseObject(text);

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JSONPath.eval(obj,"data.list[1,4]");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test22(){
        //1000000=>105,109
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        JSONObject obj = JSON.parseObject(text);

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JSONPath.eval(obj,"data.list[1:4]");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test3(){
        //不支持*
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        JSONObject obj = JSON.parseObject(text);

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JSONPath.eval(obj,"data.ary2[*].b.c"); //不支持*
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test4(){
        //不支持
        //
        //1.加载json
        String text = ("[{b:{c:1}}, {b:{d:1}}, {b:{c:2}}, {b:{c:23}}]");
        JSONArray obj = JSONArray.parseArray(text);

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JSONPath.eval(obj,"$..b[?(@.c == 12)]");//不支持
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test5(){
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5],b:2,ary2:[{a:2,b:8},{a:3,d:{c:'ddd',b:23}}]}}");
        JSONObject obj = JSON.parseObject(text);

        Object tmp = JSONPath.eval(obj,"$..b");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JSONPath.eval(obj,"$..b.min()");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test6(){
        //1.加载json
        String text = ("[{c:'aaaa'}, {b:'cccc'}, {c:'cccaa'}]");
        JSONObject obj = JSON.parseObject(text);

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JSONPath.eval(obj,"$[?(@.c =~ /a+/)]");//
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }
}
