package demo;

import demo.models.CModel;
import demo.models.OrderModel;
import demo.models.UserGroupModel;
import demo.models.UserModel;
import org.junit.Test;
import org.noear.snack.ONode;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class SerializationTest {

    @Test
    public void test0() throws Exception {
        String temp = ONode.serialize("aaa");
        assert "\"aaa\"".equals(temp);

        temp = ONode.serialize(12);
        assert "12".equals(temp);

        temp = ONode.serialize(true);
        assert "true".equals(temp);

        temp = ONode.serialize(null);
        assert "null".equals(temp);

        temp = ONode.serialize(new Date());
        assert "null".equals(temp)==false;


        String tm2 = "{a:'http:\\/\\/raas.dev.zmapi.cn'}";

        ONode tm3 = ONode.load(tm2);

        tm3.toJson().equals("{\"a\":\"http://raas.dev.zmapi.cn\"}");
    }

    @Test
    public void test1()throws Exception
    {
        try{
            String val = null;
            val.equals("");
        }catch (Exception ex){
            ex.printStackTrace();

            String json = ONode.serialize(ex);

            System.out.println(json);

            NullPointerException ex2 = ONode.deserialize(json,NullPointerException.class);

            ex2.printStackTrace();

            assert json != null;
        }
    }

    @Test
    public void test2() throws Exception {

        UserGroupModel group = new UserGroupModel();
        group.id = 9999;
        group.users = new ArrayList<>();
        group.users2 = new LinkedHashMap<>();
        group.users3 = new TreeSet<>();
        group.names = new String[5];
        group.ids = new short[5];
        group.iids = new Integer[5];
        group.dd = new BigDecimal(12);
        group.tt1 = new Timestamp(new Date().getTime());
        group.tt2 = new Date();

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

        Map<String, Object> objx = new HashMap<>();
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
        Map<String, Object> obj2 = ONode.deserialize(json, String.class);

        assert obj2.size() == 1;
    }


    @Test
    public void test4() throws Exception{
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
    public void test5() throws Exception{
        CModel obj = new CModel();

        String json = ONode.serialize(obj);
        System.out.println(json);

        CModel obj2 = ONode.deserialize(json,CModel.class);

        assert obj2.list == null;
    }

    @Test
    public void test52() throws Exception{
        CModel obj = new CModel();
        obj.init();

        String json = ONode.serialize(obj);
        System.out.println(json);

        CModel obj2 = ONode.deserialize(json,CModel.class);

        assert obj2.list.size()==obj.list.size();
    }
    @Test
    public void test53() throws Exception{
        CModel obj = new CModel();
        obj.build();

        String json = ONode.serialize(obj);
        System.out.println(json);

        CModel obj2 = ONode.deserialize(json,CModel.class);

        assert obj2.list.size()==obj.list.size();
    }

    @Test
    public void test6() throws Exception{
        String tmp = "{code:1,msg:'Hello world',data:{list:[1,2,3,4,5], ary2:[{a:2},{a:3,b:{c:'ddd'}}]}}";
        //1.加载json
        Object n = ONode.deserialize(tmp);

        assert n instanceof Map;
        assert ((Map)n).size() == 3;
    }
}
