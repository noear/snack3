package benchmark.jsonpath;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import org.junit.jupiter.api.Test;

import java.util.List;


public class SpeedFastjsonJsonPathTest {


    @Test
    public void test1(){
        //1000000=>529,546,539;;;353,370,397
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        JSONObject obj = JSON.parseObject(text);

        Object tmp = JSONPath.eval(obj,"$..a");
        System.out.println(tmp);
        assert tmp instanceof List;
        assert ((List)tmp).size() ==2;

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
        //不支持（结果不对）
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        JSONObject obj = JSON.parseObject(text);

        Object tmp = JSONPath.eval(obj,"$..*");
        System.out.println(tmp);
        assert tmp instanceof List;
        assert ((List)tmp).size() ==4;

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
        //1000000=>85,90,86;;;104,108,113
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        JSONObject obj = JSON.parseObject(text);

        Object tmp = JSONPath.eval(obj,"$.data.list[1,4]");
        System.out.println(tmp);
        assert tmp instanceof List;
        assert ((List)tmp).size() ==2;

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
        //1000000=>105,109,114;;;（不兼容）
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        JSONObject obj = JSON.parseObject(text);

        Object tmp = JSONPath.eval(obj,"$.data.list[1:4]");
        System.out.println(tmp);
        assert tmp instanceof List;
        assert ((List)tmp).size() ==3;

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
        //1000000=>48,56,48
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
        //1000000=>325,321,319;;;230,219,250
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
        //1000000=>60,58,58
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
        //不支持*;;;;331,340,315
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        JSONObject obj = JSON.parseObject(text);

        Object tmp = JSONPath.eval(obj,"$.data.ary2[*].b.c");
        System.out.println(tmp);
        assert tmp instanceof List;
        assert ((List)tmp).size() == 1;

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
        //不支持（不兼容）
        //
        //1.加载json
        String text = ("[{b:{c:1}}, {b:{d:1}}, {b:{c:2}}, {b:{c:23}}]");
        JSONArray obj = JSONArray.parseArray(text);

        Object tmp = JSONPath.eval(obj,"$..b[?(@.c == 12)]");//不支持
        System.out.println(tmp);
        assert tmp instanceof List;
        assert ((List)tmp).size() == 1;

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
        //不支持;;;389,385,404
        //
        //1.加载json
        String text = ("[{b:{c:1}}, {b:{d:1}}, {b:{c:2}}, {b:{c:23}}]");
        JSONArray obj = JSONArray.parseArray(text);

        Object tmp = JSONPath.eval(obj,"$..c.min()");
        System.out.println(tmp);
        assert tmp instanceof Number;

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
        //不支持（不兼容）
        //
        //1.加载json
        String text = ("[{c:'aaaa'}, {b:'cccc'}, {c:'cccaa'}]");
        JSONArray obj = JSONArray.parseArray(text);

        Object tmp = JSONPath.eval(obj,"$[?(@.c =~ /a+/)]");//不支持
        System.out.println(tmp);
        assert tmp instanceof List;
        assert ((List)tmp).size() == 2;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JSONPath.eval(obj,"$[?(@.c =~ /a+/)]");//不支持
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }
}
