package benchmark.jsonpath;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONPath;
import org.junit.jupiter.api.Test;


public class SpeedFastjson2JsonPathTest {


    @Test
    public void test1(){
        //1000000=>872,764,715
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        JSONObject obj = JSON.parseObject(text);

        Object tmp = JSONPath.eval(obj,"$..a");
        System.out.println(tmp);
        assert tmp instanceof JSONArray;
        assert ((JSONArray)tmp).size() ==2;

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

        Object tmp = JSONPath.eval(obj,"$..*");
        System.out.println(tmp);
        assert tmp instanceof JSONArray;
        assert ((JSONArray)tmp).size() ==16;

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
        //1000000=>577,524,419
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        JSONObject obj = JSON.parseObject(text);

        Object tmp = JSONPath.eval(obj,"$.data.list[1,4]");
        System.out.println(tmp);
        assert tmp instanceof JSONArray;
        assert ((JSONArray)tmp).size() ==2;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JSONPath.eval(obj,"$.data.list[1,4]");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test4(){
        //1000000=>332,367,391
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        JSONObject obj = JSON.parseObject(text);

        Object tmp = JSONPath.eval(obj,"$.data.list[1:4]");
        System.out.println(tmp);
        assert tmp instanceof JSONArray;
        assert ((JSONArray)tmp).size() ==3;


        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JSONPath.eval(obj,"$.data.list[1:4]");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test40(){
        //1000000=>315,339,329
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        JSONObject obj = JSON.parseObject(text);

        Object tmp = JSONPath.eval(obj,"$.data.ary2[0].a");
        System.out.println(tmp);
        assert tmp instanceof Integer;


        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JSONPath.eval(obj,"$.data.ary2[0].a");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test41(){
        //1000000=>735,728,736
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        JSONObject obj = JSON.parseObject(text);

        Object tmp = JSONPath.eval(obj,"$..ary2[0].a");
        System.out.println(tmp);
        assert tmp instanceof Integer;


        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JSONPath.eval(obj,"$..ary2[0].a");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test42(){
        //出错
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        JSONObject obj = JSON.parseObject(text);

        JSONPath.eval(obj,"$.data.list[?(@ in $..ary2[0].a)]");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JSONPath.eval(obj,"$.data.list[?(@ in $..ary2[0].a)]");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test5(){
        //1000000=>422,424,415
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        JSONObject obj = JSON.parseObject(text);

        Object tmp = JSONPath.eval(obj,"$.data.ary2[1].b.c");
        System.out.println(tmp);
        assert tmp instanceof String;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JSONPath.eval(obj,"$.data.ary2[1].b.c");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test6(){
        //1000000=>642,645,660
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        JSONObject obj = JSON.parseObject(text);

        Object tmp = JSONPath.eval(obj,"$.data.ary2[*].b.c");
        System.out.println(tmp);
        assert tmp instanceof JSONArray;
        assert ((JSONArray)tmp).size() ==1;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JSONPath.eval(obj,"$.data.ary2[*].b.c"); //不支持*
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
        String text = ("[{b:{c:1}}, {b:{d:1}}, {b:{c:2}}, {b:{c:23}}]"); //解析会出错
        JSONArray obj = JSON.parseArray(text);

        JSONPath.eval(obj,"$..b[?(@.c == 12)]");//不支持

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
        String text = ("[{b:{c:1}}, {b:{d:1}}, {b:{c:2}}, {b:{c:23}}]");//解析会出错
        JSONArray obj = JSON.parseArray(text);

        JSONPath.eval(obj,"$..c.min()");

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
        JSONArray obj = JSON.parseArray(text);

        JSONPath.eval(obj,"$[?(@.c =~ /a+/)]");//不支持

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JSONPath.eval(obj,"$[?(@.c =~ /a+/)]");//不支持
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }
}
