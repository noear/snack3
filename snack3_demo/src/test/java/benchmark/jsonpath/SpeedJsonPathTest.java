package benchmark.jsonpath;

import org.junit.Test;
import org.noear.snack.ONode;

public class SpeedJsonPathTest {

    /**
     * 是否使用标准模式解析
     * */
    private boolean useStandard = false;

    @Test
    public void test1(){
        //1000000=>225,225,232
        //
        //1.加载json
        ONode n = ONode.load("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");

        ONode tmp = n.select("$..a", useStandard);
        assert tmp.count()==2;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            n.select("$..a", useStandard);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test2(){
        //1000000=>277,292,275
        //
        //1.加载json
        ONode n = ONode.load("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");

        ONode tmp = n.select("$..*", useStandard);
        assert tmp.count()==16;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            n.select("$..*", useStandard);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test3(){
        //1000000=>133,137,131
        //
        //1.加载json
        ONode n = ONode.load("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");

        ONode tmp = n.select("$.data.list[1,4]", useStandard);
        assert tmp.count() == 2;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            n.select("$.data.list[1,4]", useStandard);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test4(){
        //1000000=>143,145,146
        //
        //1.加载json
        ONode n = ONode.load("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");

        ONode tmp = n.select("$.data.list[1:4]", useStandard);
        assert tmp.count() == 3;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            n.select("$.data.list[1:4]", useStandard);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test41(){
        //1000000=>310,311,314
        //
        //1.加载json
        ONode n = ONode.load("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");

        ONode tmp = n.select("$..ary2[0].a", useStandard);
        assert tmp.count() == 1;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            n.select("$..ary2[0].a)", useStandard);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test42(){
        //1000000=>678,674,667
        //
        //1.加载json
        ONode n = ONode.load("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");

        ONode tmp30 = n.select("$..ary2[0].a", useStandard);
        assert tmp30.count() == 1;

        ONode tmp3 = n.select("$.data.list[?(@ in $..ary2[0].a)]", useStandard);
        assert tmp3.count() == 1;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            n.select("$.data.list[?(@ in $..ary2[0].a)]", useStandard);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test5(){
        //1000000=>84,86,80
        //
        //1.加载json
        ONode n = ONode.load("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");

        ONode tmp = n.select("$.data.ary2[1].b.c", useStandard);
        assert "ddd".equals(tmp.getString());

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            n.select("$.data.ary2[1].b.c", useStandard);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test6(){
        //1000000=>173,152,155
        //
        //1.加载json
        ONode n = ONode.load("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");
        long start = System.currentTimeMillis();

        ONode tmp = n.select("$.data.ary2[*].b.c", useStandard);
        assert tmp.count() == 1;

        for(int i=0,len=1000000; i<len; i++) {
            n.select("$.data.ary2[*].b.c", useStandard);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test7(){
        //1000000=>580,535,532
        //
        //1.加载json
        ONode n = ONode.load("[{b:{c:1}}, {b:{d:1}}, {b:{c:2}}, {b:{c:23}}]");
        long start = System.currentTimeMillis();

        ONode tmp = n.select("$..b[?(@.c == 12)]", useStandard);
        assert tmp.count() == 0;

        for(int i=0,len=1000000; i<len; i++) {
            n.select("$..b[?(@.c == 12)]", useStandard);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test8(){
        //1000000=>279,282,285
        //
        //1.加载json
        ONode n = ONode.load("[{b:{c:1}}, {b:{d:1}}, {b:{c:2}}, {b:{c:23}}]");

        ONode tmp = n.select("$..c", useStandard);
        assert tmp.count() == 3;


        ONode tmp1 = n.select("$..c.min()", useStandard);
        if(useStandard) {
            assert tmp1.count() == 0;
        }else{
            assert tmp1.isValue();
        }

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            n.select("$..c.min()", useStandard);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test9(){
        //1000000=>444,423,429
        //
        //1.加载json
        ONode n = ONode.load("[{c:'aaaa'}, {b:'cccc'}, {c:'cccaa'}]");

        ONode tmp = n.select("$[?(@.c =~ /.*a+/)]", useStandard);
        assert tmp.count() == 2;

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            n.select("$[?(@.c =~ /a+/)]", useStandard);
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }
}
