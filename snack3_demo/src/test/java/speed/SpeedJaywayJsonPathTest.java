package speed;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JsonProvider;
import org.junit.Test;


public class SpeedJaywayJsonPathTest {
    @Test
    public void test0(){
        //100000=>162,153,157
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");

        JsonProvider jsonProvider = Configuration.defaultConfiguration().jsonProvider();

        long start = System.currentTimeMillis();
        for(int i=0,len=100000; i<len; i++) {
            jsonProvider.parse(text);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test00(){
        //100000=>1095,1227,,1151
        //
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");

        long start = System.currentTimeMillis();
        for(int i=0,len=100000; i<len; i++) {
            JsonPath.read(text,"$..a");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test1(){
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        Object obj = Configuration.defaultConfiguration().jsonProvider().parse(text);

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JsonPath.read(obj,"$..a");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test12(){
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        Object obj = Configuration.defaultConfiguration().jsonProvider().parse(text);

        Object tmp = JsonPath.read(obj,"$..*");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JsonPath.read(obj,"$..*");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test2(){
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        Object obj = Configuration.defaultConfiguration().jsonProvider().parse(text);

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JsonPath.read(obj,"data.list[1,4]");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test22(){
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        Object obj = Configuration.defaultConfiguration().jsonProvider().parse(text);

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JsonPath.read(obj,"data.list[1:4]");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test23(){
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        Object obj = Configuration.defaultConfiguration().jsonProvider().parse(text);

        Object tmp = JsonPath.read(obj,"data.ary2[1].b.c");;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JsonPath.read(obj,"data.ary2[1].b.c");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test3(){
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        Object obj = Configuration.defaultConfiguration().jsonProvider().parse(text);

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JsonPath.read(obj,"data.ary2[*].b.c"); //不支持*
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test4(){
        //1.加载json
        String text = ("[{b:{c:1}}, {b:{d:1}}, {b:{c:2}}, {b:{c:23}}]");
        Object obj = Configuration.defaultConfiguration().jsonProvider().parse(text);

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JsonPath.read(obj,"$..b[?(@.c == 12)]");//不支持
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test5(){
        //1.加载json
        String text = ("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5],b:2,ary2:[{a:2,b:8},{a:3,d:{c:'ddd',b:23}}]}}");
        Object obj = Configuration.defaultConfiguration().jsonProvider().parse(text);

        Object tmp = JsonPath.read(obj,"$..b");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JsonPath.read(obj,"$..b.min()");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test6(){
        //1000000=>3575,3591
        //
        //1.加载json
        String text = ("[{c:'aaaa'}, {b:'cccc'}, {c:'cccaa'}]");
        Object obj = Configuration.defaultConfiguration().jsonProvider().parse(text);

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JsonPath.read(obj,"$[?(@.c =~ /a+/)]");//
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }
}
