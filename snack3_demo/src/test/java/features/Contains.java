package features;

import com.alibaba.fastjson.JSONArray;
import org.junit.Test;
import org.noear.snack.ONode;

import java.util.Date;

public class Contains {
    @Test
    public void test0(){

        assert System.identityHashCode(null) == 0;

        assert System.identityHashCode(0) != Integer.hashCode(0);

        assert System.identityHashCode(0l) != Long.hashCode(0);
    }

    @Test
    public void test1() {
        JSONArray tmp = (JSONArray) JSONArray.parse("[1,2,3,4,5]");
        assert tmp.contains(2);

        tmp = (JSONArray) JSONArray.parse("[1,'2',3,4,5]");
        assert tmp.contains("2");

        long times = System.currentTimeMillis();
        Date time = new Date(times);

        tmp = (JSONArray) JSONArray.parse("[1,'2',3,4,new Date(" + times + ")]");
        assert tmp.contains(time);
    }

    @Test
    public void test21() {

        assert  Long.hashCode(2) == new ONode().val(2).hashCode();

        ONode tmp = ONode.loadStr("[1,2,3,4,5]");
        assert tmp.ary().contains(new ONode().val(2));

        tmp = ONode.loadStr("[1,'2',3,4,5]");
        assert tmp.ary().contains(new ONode().val("2"));

        long times = System.currentTimeMillis();
        Date time = new Date(times);

        tmp = ONode.loadStr("[1,'2',3,4,new Date(" + times + ")]");
        assert tmp.ary().contains(new ONode().val(time));
    }

    @Test
    public void test22() {

        assert  Long.hashCode(2) == new ONode().val(2).hashCode();

        ONode tmp = ONode.loadStr("[1,2,3,4,5]");
        assert tmp.ary().contains(2l);

        tmp = ONode.loadStr("[1,'2',3,4,5]");
        assert tmp.ary().contains("2");

        long times = System.currentTimeMillis();
        Date time = new Date(times);

        tmp = ONode.loadStr("[1,'2',3,4,new Date(" + times + ")]");
        assert tmp.ary().contains(time);
    }
}
