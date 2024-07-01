package features;

import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonPathTest3 {
    public static class Entity {
        public int id;
        public String name;
        public Object value;

        public Entity() {
        }

        public Entity(int id, Object value) {
            this.id = id;
            this.value = value;
        }

        public Entity(String name) {
            this.name = name;
        }
    }


    @Test
    public void test1() {
        Entity entity = new Entity(123, new Object());
        ONode n = ONode.load(entity);

        assert n.select("$.id").getInt() == 123;
        assert n.select("$.*").count() == 2;//因为 StringNullAsEmpty，使 name 变成了 ""
    }

    @Test
    public void test2() {
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

        List<Entity> result = n.select("$[1,2]").toObject((new ArrayList<Entity>() {
        }).getClass());
        assert result.size() == 2;
    }

    @Test
    public void test4() {
        List<Entity> entities = new ArrayList<Entity>();
        entities.add(new Entity("wenshao"));
        entities.add(new Entity("ljw2083"));
        entities.add(new Entity("Yako"));
        ONode n = ONode.load(entities);

        List<Entity> result = n.select("$[0:2]").toObject((new ArrayList<Entity>() {
        }).getClass());
        assert result.size() == 2;
    }

    @Test
    public void test5() {
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
    public void test6() {
        Entity entity = new Entity(1001, "ljw2083");
        ONode n = ONode.load(entity);

        assert n.select("$[?(id == 1001)]").isObject();
        assert n.select("$[?(id == 1002)]").isNull();

        n.select("$").set("id", 123456);
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
        assertEquals(3, ids.size());
        assertEquals(1001, ids.get(0));
        assertEquals(1002, ids.get(1));
        assertEquals(1003, ids.get(2));
    }

    @Test
    public void testx() {

        String jsonStr = "{\n" +
                "    \"store\": {\n" +
                "        \"bicycle\": {\n" +
                "            \"color\": \"red\",\n" +
                "            \"price\": 19.95\n" +
                "        },\n" +
                "        \"book\": [\n" +
                "            {\n" +
                "                \"author\": \"刘慈欣\",\n" +
                "                \"price\": 8.95,\n" +
                "                \"category\": \"科幻\",\n" +
                "                \"title\": \"三体\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"author\": \"itguang\",\n" +
                "                \"price\": 12.99,\n" +
                "                \"category\": \"编程语言\",\n" +
                "                \"title\": \"go语言实战\"\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}";

        ONode o = ONode.load(jsonStr);

        //得到所有的书
        ONode books = o.select("$.store.book");
        System.out.println("books=::" + books);

        //得到所有的书名
        ONode titles = o.select("$.store.book.title");
        System.out.println("titles=::" + titles);

        //第一本书title
        ONode title = o.select("$.store.book[0].title");
        System.out.println("title=::" + title);

        //倒数第一本书title
        ONode title2 = o.select("$.store.book[-1].title");
        System.out.println("title=::" + title2);
        //assert "go语言实战".equals(title2);

        //price大于10元的book
        ONode list = o.select("$.store.book[?(price > 10)]");
        System.out.println("price大于10元的book=::" + list);

        //price大于10元的title
        ONode list2 = o.select("$.store.book[?(price > 10)].title");
        System.out.println("price大于10元的title=::" + list2);

        //category(类别)为科幻的book
        ONode list3 = o.select("$.store.book[?(category == '科幻')]");
        System.out.println("category(类别)为科幻的book=::" + list3);


        //bicycle的所有属性值
        ONode values = o.select("$.store.bicycle.*");
        System.out.println("bicycle的所有属性值=::" + values);


        //bicycle的color和price属性值
        ONode read = o.select("$.store.bicycle['color','price']");
        System.out.println("bicycle的color和price属性值=::" + read);

    }

    @Test
    public void testx2() {
        String json = "{\"school\":[{\"name\":\"清华\",\"grade\":[{\"class\":\"二\",\"manSum\":12},{\"class\":\"一班\",\"manSum\":12}]},{\"name\":\"北大\",\"grade\":[{\"class\":\"二\",\"manSum\":12},{\"class\":\"一班\",\"manSum\":12}]}]}";

        ONode oNode = ONode.loadStr(json);

        ONode oNode1 = null;

        oNode1 = oNode.select("$.school[?(@.name == '清华')]");
        System.out.println(oNode1.toJson());

        oNode1 = oNode.select("$.school[?(@.name == '清华')].grade[0]");
        System.out.println(oNode1.toJson());

        oNode1 = oNode.select("$.school[?(@.name == '清华')].grade[0][?(@.class == '一班')]");
        System.out.println(oNode1.toJson());

        oNode1 = oNode.select("$.school[?(@.name == '清华')].grade[0][?(@.class == '一班')].manSum");
        System.out.println(oNode1.toJson());

        oNode1 = oNode.select("$.school[?(@.name == '清华')].grade[0][?(@.class == '一班')].manSum.sum()");
        System.out.println(oNode1.toJson());

        oNode1 = oNode.select("$..manSum.sum()");
        System.out.println(oNode1.toJson());
    }
}
