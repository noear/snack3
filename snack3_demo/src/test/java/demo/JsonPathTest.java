package demo;

import org.junit.Test;
import org.noear.snack.ONode;

import java.util.List;


public class JsonPathTest {
    @Test
    public void demo1() {
        //1.加载json
        ONode n = ONode.load("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");

        //2.取一个属性的值
        String msg = n.get("msg").getString();

        //3.取列表里的一项
        int li2  = n.get("data").get("list").get(2).getInt();

        //4.获取一个数组
        //List<Integer> list = n.get("data").get("list").toObject(List.class);
        List<Integer> list = n.select("data.list").toObject(List.class);



        //int mi = n.get("data").get("list").get(0).getInt();
        int mi = n.select("data.list[-1]").getInt();

        List<Integer> list2 = n.select("data.list[2,4]").toObject(List.class);
        List<Integer> list3 = n.select("data.list[2:4]").toObject(List.class);

        ONode ary2_a = n.select("data.ary2[*].b.c");

        ONode ary2_b = n.select("..b");

        ONode ary2_c = n.select("data..b.c");


        assert list.size() == 5;
    }

    @Test
    public void demo2() {
        //1.加载json
        ONode n = ONode.load("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");


        //4.获取一个数组
        //List<Integer> list = n.get("data").get("list").toObject(List.class);
        List<Integer> list = n.select("$.data.list").toObject(List.class);



        //int mi = n.get("data").get("list").get(0).getInt();
        int mi = n.select("$.data.list[-1]").getInt();

        List<Integer> list2 = n.select("$.data.list[2,4]").toObject(List.class);
        List<Integer> list3 = n.select("$.data.list[1:4]").toObject(List.class);
        List<Integer> list4 = n.select("$.data.list[:4]").toObject(List.class);

        ONode ary2_a = n.select("$.data.ary2[*].b.c");

        ONode ary2_b = n.select("$..b");

        ONode ary2_c = n.select("$.data..b.c");


        assert list.size() == 5;
    }

    @Test
    public void demo3() {
        //1.加载json
        ONode n = ONode.load("{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}");


        ONode ary2_a = n.select("$['code','msg']");
        assert ary2_a.count() == 2;



        ONode ary2_b = n.select("$.data[*]");
        assert ary2_b.count() == 2;



        ONode ary2_c = n.select("$['data']['list'][2]");
        assert ary2_c.getInt() == 3;
    }
}
