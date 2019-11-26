package speed;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
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
        //不支持
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        JSONObject obj = JSON.parseObject(text);

        Object tmp  =JSONPath.eval(obj,"$..*");;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JSONPath.eval(obj,"$..*");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test3(){
        //1000000=>105,109,102
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
    public void test4(){
        //1000000=>105,109,114
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
    public void test41(){
        //1000000=>143,145,146
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        JSONObject obj = JSON.parseObject(text);

        Object tmp0 = JSONPath.eval(obj,"$..ary2.a");
        Object tmp1 = JSONPath.eval(obj,"$..ary2[0].a");
        Object tmp2 = JSONPath.eval(obj,"$.data.list[?(@ == $..ary2[0].a)]");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JSONPath.eval(obj,"$.data.list[?(@ == $..ary2[0].a)]");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test5(){
        //1000000=>34,34,34
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        JSONObject obj = JSON.parseObject(text);

        Object tmp =JSONPath.eval(obj,"ata.ary2[1].b.c");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JSONPath.eval(obj,"ata.ary2[1].b.c");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test6(){
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
    public void test7(){
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
    public void test8(){
        //不支持
        //
        //1.加载json
        String text = ("[{b:{c:1}}, {b:{d:1}}, {b:{c:2}}, {b:{c:23}}]");
        JSONArray obj = JSONArray.parseArray(text);


        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JSONPath.eval(obj,"$..c.min()");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test9(){
        //1.加载json
        String text = ("[{c:'aaaa'}, {b:'cccc'}, {c:'cccaa'}]");
        JSONArray obj = JSONArray.parseArray(text);

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JSONPath.eval(obj,"$[?(@.c =~ /a+/)]");//不支持
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }
}
