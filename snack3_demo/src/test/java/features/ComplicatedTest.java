package features;

import org.junit.Test;
import org.noear.snack.ONode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComplicatedTest {
    @Test
    public void test0(){
        //List<Map<String, List<Map>>>
        Map map1 = new HashMap();
        map1.put("k1","v1");
        map1.put("k2","v2");

        List<Map> list1 = new ArrayList<>();
        list1.add(map1);

        Map<String, List<Map>> map2 = new HashMap<>();

        map2.put("key1", list1);


        List<Map<String, List<Map>>> list2 = new ArrayList<>();

        list2.add(map2);

        //开始序列化
        String json = ONode.serialize(list2);
        System.out.println(json);

        //开始反序列化
        Object obj2 = ONode.deserialize(json);
        String json2 = ONode.serialize(obj2);

        assert obj2 instanceof List;
        assert json.equals(json2);
    }

    @Test
    public void test1(){
        //Map<String,Map>

        Map map1 = new HashMap();
        map1.put("k1","v1");
        map1.put("k2","v2");

        Map<String, Map> map2 = new HashMap<>();

        map2.put("key1", map1);


        //开始序列化
        String json = ONode.serialize(map2);
        System.out.println(json);

        //开始反序列化
        Object obj2 = ONode.deserialize(json);
        String json2 = ONode.serialize(obj2);

        assert obj2 instanceof Map;
        assert json.equals(json2);
    }

    @Test
    public void test2(){
        //Map<String,List>
        List list1 = new ArrayList();
        list1.add("v1");
        list1.add("v2");

        Map<String, List> map2 = new HashMap<>();

        map2.put("key1", list1);


        //开始序列化
        String json = ONode.serialize(map2);
        System.out.println(json);

        //开始反序列化
        Object obj2 = ONode.deserialize(json);
        String json2 = ONode.serialize(obj2);

        assert obj2 instanceof Map;
        assert json.equals(json2);
    }
}
