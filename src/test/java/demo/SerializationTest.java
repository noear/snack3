package demo;

import demo.models.CModel;
import demo.models.OrderModel;
import demo.models.UserGroupModel;
import demo.models.UserModel;
import org.junit.Test;
import org.near.snack3.ONode;

import java.util.*;

public class SerializationTest {
    @Test
    public void test1() throws IllegalAccessException {

        UserGroupModel group = new UserGroupModel();
        group.id = 9999;
        group.users = new ArrayList<>();
        group.users2 = new LinkedHashMap<>();
        group.users3 = new TreeSet<>();
        group.names = new String[5];
        group.ids = new short[5];
        group.iids = new Integer[5];

        for (short i = 0; i < 5 ; i++) {
            UserModel user = new UserModel();
            user.id = i;
            user.name = "张三" + i;
            user.note = null;
            group.users.add(user);
            group.users2.put(Integer.valueOf(i),user);
            group.names[i] = "李四" + i;
            group.ids[i] = i;
        }

        String json = ONode.serialize(group);
        System.out.println(json);
        UserGroupModel group2 = ONode.deserialize(json, UserGroupModel.class);

        assert group2.id == 9999;

    }

    @Test
    public void test3() throws Exception {
        Map<String, Object> obj = new LinkedHashMap<String, Object>();

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> m = new LinkedHashMap<String, Object>();
        m.put("a", 1);
        m.put("b", true);
        m.put("c", 1.2);
        m.put("d", new Date());

        list.add(m);

        obj.put("list", list);


        String json = ONode.serialize(obj);
        System.out.println(json);
        Map<String, Object> obj2 = ONode.deserialize(json, obj.getClass());

        assert obj2.size() == 1;
    }


    @Test
    public void test4(){
        UserModel user = new UserModel();
        user.id = 1111;
        user.name = "张三";
        user.note = null;

        OrderModel order = new OrderModel();
        order.user = user;
        order.order_id = 2222;
        order.order_num = "ddddd";


        String json = ONode.serialize(order);
        System.out.println(json);
        OrderModel order2 = ONode.deserialize(json,OrderModel.class);



        assert 1111 == order2.user.id;
    }

    @Test
    public void test5(){
        CModel obj = new CModel();

        String json = ONode.serialize(obj);
        System.out.println(json);

        CModel obj2 = ONode.deserialize(json,CModel.class);

        assert obj2.list == null;
    }

    @Test
    public void test52(){
        CModel obj = new CModel();
        obj.init();

        String json = ONode.serialize(obj);
        System.out.println(json);

        CModel obj2 = ONode.deserialize(json,CModel.class);

        assert obj2.list.size()==obj.list.size();
    }
    @Test
    public void test53(){
        CModel obj = new CModel();
        obj.build();

        String json = ONode.serialize(obj);
        System.out.println(json);

        CModel obj2 = ONode.deserialize(json,CModel.class);

        assert obj2.list.size()==obj.list.size();
    }
}
