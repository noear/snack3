package features;

import org.junit.Test;
import org.noear.snack.ONode;

import java.util.*;

public class _test_base {
    @Test
    public void test1() {
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", 12);
        map.put("nickname", "谢谢");

        ONode o = new ONode();

        o.set("user", map);

        String tmp = o.toJson();

        System.out.println(tmp);

        assert tmp.indexOf("谢谢") > 0;
    }

    @Test
    public void test2() {
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", 12);
        map.put("nickname", "谢谢");

        ONode o = new ONode();

        o.add(map);

        String tmp = o.toJson();

        System.out.println(tmp);

        assert tmp.indexOf("谢谢") > 0;
    }

    @Test
    public void test3() {
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", 12);
        map.put("nickname", "谢谢");

        List<Map<String, Object>> list = new ArrayList<>();

        list.add(map);

        ONode o = new ONode();

        o.add(list);

        String tmp = o.toJson();

        System.out.println(tmp);

        assert tmp.indexOf("谢谢") > 0;
    }

    @Test
    public void test4() {
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", 12);
        map.put("nickname", "谢谢");

        List<Map<String, Object>> list = new ArrayList<>();

        list.add(map);

        Map<String, Object> map2 = new HashMap<>();

        map2.put("users", list);

        ONode o = new ONode();

        o.add(map2);

        String tmp = o.toJson();

        System.out.println(tmp);

        assert tmp.indexOf("谢谢") > 0;
    }

    @Test
    public void test5() {
        Integer[] a = {1, 2, 3, 4, 5};
        String json = "{\"list\":[],\"name\":\"nn\"}";
        ONode node = ONode.load(json);
        node.set("value", a);

        System.out.println(node.toJson());
    }
}
