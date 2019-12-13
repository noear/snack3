package features;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;
import org.noear.snack.ONode;

import java.util.Date;
import java.util.List;

public class Contains {
    @Test
    public void test0() {

        assert System.identityHashCode(null) == 0;

        assert System.identityHashCode(0) != Integer.hashCode(0);

        assert System.identityHashCode(0l) != Long.hashCode(0);

        assert System.identityHashCode(0) != System.identityHashCode(0l);
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
    public void test12() {
        JSONArray tmp = (JSONArray) JSONArray.parse("[1,2,{c:1,d:2,b:[4]}]");

        JSONObject tmp2 = (JSONObject) JSONObject.parse("{c:1,d:2,b:[4]}");

        assert tmp.contains(tmp2);
    }

    @Test
    public void test21() {

        assert Long.hashCode(2) == new ONode().val(2).hashCode();

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

        assert Long.hashCode(2) == new ONode().val(2).hashCode();

        ONode tmp = ONode.loadStr("[1,2,3,4,5,true, null]");
        assert tmp.ary().contains(2l);

        assert tmp.ary().contains(2);

        assert tmp.ary().contains(true);

        assert tmp.ary().contains(null);

        assert tmp.ary().contains(new ONode().asNull());

        tmp = ONode.loadStr("[1,'2',3,4,5]");
        assert tmp.ary().contains("2");

        long times = System.currentTimeMillis();
        Date time = new Date(times);

        tmp = ONode.loadStr("[1,'2',3,4,new Date(" + times + ")]");
        assert tmp.ary().contains(time);
    }

    @Test
    public void test3() {

        ONode tmp = ONode.loadStr("{a:[1,2,3,4,5],b:2}");
        ONode tmp2 = ONode.loadStr("{a:[1,2,3,4,5],b:2}");

        assert tmp.equals(tmp2);

        ONode tmp3 = ONode.loadStr("[1,2,3,4,5]");
        List<Integer> tmp4 = ONode.loadStr("[1,2,3,4,5]").toObject(List.class);

        List<Integer> tmp41 = ONode.loadStr("[1,2,3,5,4]").toObject(List.class);
        List<Integer> tmp42 = ONode.loadStr("[1,2,3,4]").toObject(List.class);

        assert  tmp.obj().containsKey("a");
        assert  tmp.obj().containsValue(tmp3);
        assert  tmp.obj().containsValue(tmp4);

        assert  tmp.obj().containsValue(tmp41) == false;
        assert  tmp.obj().containsValue(tmp42) == false;

        assert  tmp.obj().containsValue(2);
    }

    @Test
    public void test4() {
        ONode tmp = ONode.loadStr("[1,2,{c:1,d:2,b:[4]}]");

        ONode tmp2 = ONode.loadStr("{c:1,d:2,b:[4]}");

        assert tmp.ary().contains(tmp2);

        assert tmp.ary().indexOf(tmp2) == 2;
    }
}
