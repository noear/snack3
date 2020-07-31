package benchmark;

import org.junit.Test;
import org.noear.snack.ONode;

import java.util.ArrayList;
import java.util.List;

public class BaseSpeedTest2 {
    @Test
    public void test1(){
        //1.加载json
        ONode n = ONode.load("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");

        ONode tmp = n.select("data.list[1,4]");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            n.select("data.list[1,4]");
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }


    @Test
    public void test2(){
        //1.加载json
        ONode n = ONode.load("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");

        ONode tmp = n.select("data.list[1,4]");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            ONode ary2 = new ONode().asArray();
            ONode tmp2 = n.get("data").get("list");
            ary2.addNode(tmp2.get(1));
            ary2.addNode(tmp2.get(4));
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test3(){
        //1.加载json
        ONode n = ONode.load("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");

        ONode tmp = n.select("data.list[1,4]");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            ONode ary2 = new ONode().asArray();
            ONode tmp2 = n.get("data").get("list");
            ary2.nodeData().array.add(tmp2.get(1));
            ary2.nodeData().array.add(tmp2.get(4));
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }

    @Test
    public void test4(){
        //1.加载json
        ONode n = ONode.load("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");

        ONode tmp = n.select("data.list[1,4]");

        long start = System.currentTimeMillis();
        for(int i=0,len=1000000; i<len; i++) {
            List<ONode> ary2 = new ArrayList<>();
            ONode tmp2 = n.getOrNull("data").getOrNull("list");
            ary2.add(tmp2.get(1));
            ary2.add(tmp2.get(4));
        }

        long times = System.currentTimeMillis() - start;

        System.out.println(times);

        assert times > 0;
    }
}
