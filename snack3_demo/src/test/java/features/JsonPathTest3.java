package features;

import org.junit.Test;
import org.noear.snack.ONode;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class JsonPathTest3 {
    public static class Entity {
        public int id;
        public String name;
        public Object value;

        public Entity(){}
        public Entity(int id, Object value) { this.id = id; this.value = value; }
        public Entity(String name) { this.name = name; }
    }


    @Test
    public void test1(){
        Entity entity = new Entity(123, new Object());
        ONode n = ONode.load(entity);

        assert n.select("$.id").getInt() == 123;
        assert n.select("$.*").count() == 2;
    }

    @Test
    public void test2(){
        List<Entity> entities = new ArrayList<Entity>();
        entities.add(new Entity("wenshao"));
        entities.add(new Entity("ljw2083"));
        ONode n = ONode.load(entities);

        List<String> names = n.select("$.name").toObject(List.class);
        assert names.size() == 2;
    }

    @Test
    public void test3() {
        List<Entity> entities = new ArrayList<Entity>();
        entities.add(new Entity("wenshao"));
        entities.add(new Entity("ljw2083"));
        entities.add(new Entity("Yako"));
        ONode n = ONode.load(entities);

        List<Entity> result = n.select("$[1,2]").toObject((new ArrayList<Entity>() {}).getClass());
        assert result.size() == 2;
    }

    @Test
    public void test4(){
        List<Entity> entities = new ArrayList<Entity>();
        entities.add(new Entity("wenshao"));
        entities.add(new Entity("ljw2083"));
        entities.add(new Entity("Yako"));
        ONode n = ONode.load(entities);

        List<Entity> result = n.select("$[0:2]").toObject((new ArrayList<Entity>(){}).getClass());
        assert result.size() == 2;
    }

    @Test
    public void test5(){
        List<Entity> entities = new ArrayList<Entity>();
        entities.add(new Entity(1001, "ljw2083"));
        entities.add(new Entity(1002, "wenshao"));
        entities.add(new Entity(1003, "yakolee"));
        entities.add(new Entity(1004, null));
        ONode n = ONode.load(entities);

        ONode rst = n.select("$[?($.id in [1001,1002])]");
        assert rst.count() == 2;
    }

    @Test
    public void test6(){
        Entity entity = new Entity(1001, "ljw2083");
        ONode n = ONode.load(entity);

        assert n.select("$[?($.id == 1001)]").isObject();
        assert n.select("$[?($.id == 1002)]").isNull();

        n.select("$").set("id",123456);
        assert n.get("id").getInt() == 123456;

        n.get("value").add(1).add(2).add(3);
        assert n.get("value").count() == 3;
    }

    @Test
    public void test7() {
        Map root = Collections.singletonMap("company",
                Collections.singletonMap("departs",
                        Arrays.asList(
                                Collections.singletonMap("id",
                                        1001),
                                Collections.singletonMap("id",
                                        1002),
                                Collections.singletonMap("id", 1003)
                        )
                ));

        ONode n = ONode.load(root);

        List<Object> ids = n.select("$..id").toObject(List.class);
        assertEquals(3l, ids.size());
        assertEquals(1001l, ids.get(0));
        assertEquals(1002l, ids.get(1));
        assertEquals(1003l, ids.get(2));
    }
}
