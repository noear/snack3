package speed;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JsonProvider;
import org.junit.Test;


public class SpeedJaywayJsonPathTest {


    @Test
    public void test1(){
        //1000000=>2658,2633,2590
        //
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
        //1000000=>3227,3220,3156
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
        //1000000=>782,798,776
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
        //1000000=>941,899,947
        //
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
        //1000000=>929,826,837
        //
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
        //1000000=>1105,1025,1050
        //
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
        //1000000=>5628,5739,5636
        //
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
        //运行会出错
        //1.加载json
        String text =("[{b:{c:1}}, {b:{d:1}}, {b:{c:2}}, {b:{c:23}}]");
        Object obj = Configuration.defaultConfiguration().jsonProvider().parse(text);

        Object tmp = JsonPath.read(obj,"$..c");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            JsonPath.read(obj,"$..c.min()");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test6(){
        //1000000=>3575,3591,3813
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
