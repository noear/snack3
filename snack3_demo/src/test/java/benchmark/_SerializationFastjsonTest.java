package benchmark;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import _models.CModel;
import _models.OrderModel;
import _models.UserGroupModel;
import _models.UserModel;
import org.junit.Test;

import java.util.*;

public class _SerializationFastjsonTest {
    @Test
    public void test1() throws IllegalAccessException {

        UserGroupModel group = new UserGroupModel();
        group.id = 9999;
        group.users = new ArrayList<>();
        group.users2 = new LinkedHashMap<>();
        group.users3 = new TreeSet<>();// 如果只有空实例，反序列化时无法解析json
        group.names = new String[5];
        group.ids = new short[5];
        group.iids = new Integer[5];
        //如果有Queue类型，会出异常

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


        String json = JSON.toJSONString(group,
                SerializerFeature.BrowserCompatible,
                SerializerFeature.WriteClassName,
                SerializerFeature.DisableCircularReferenceDetect);
        System.out.println(json);

        UserGroupModel group2 = JSON.parseObject(json,UserGroupModel.class);

        assert group2.id == 9999;

    }

    @Test
    public void test3() throws Exception {
        Map<String, Object> obj = new LinkedHashMap<String, Object>();

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();//类型，不能被还原
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("a", 1);
        m.put("b", true);
        m.put("c", 1.2);
        m.put("d", new Date());

        list.add(m);

        obj.put("list", list);


        String json = JSON.toJSONString(obj,
                SerializerFeature.BrowserCompatible,
                SerializerFeature.WriteClassName,
                SerializerFeature.DisableCircularReferenceDetect);
        System.out.println(json);

        Map<String, Object> obj2 = JSON.parseObject(json,obj.getClass());

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

        String json = JSON.toJSONString(order,
                SerializerFeature.BrowserCompatible,
                SerializerFeature.WriteClassName,
                SerializerFeature.DisableCircularReferenceDetect);
        System.out.println(json);

        OrderModel order2 = JSON.parseObject(json, OrderModel.class);

        assert 1111 == order2.user.id;
    }

    @Test
    public void test5(){
        CModel obj = new CModel();

        String json = JSON.toJSONString(obj,
                SerializerFeature.BrowserCompatible,
                SerializerFeature.WriteClassName,
                SerializerFeature.DisableCircularReferenceDetect);
        System.out.println(json);

        CModel obj2 = JSON.parseObject(json,CModel.class);

        assert obj2.list == null;
    }

    @Test
    public void test52(){ //无法运行成功；因为不能创建queue；不能创建LinkedHasMap
        CModel obj = new CModel();
        obj.init();

        String json = JSON.toJSONString(obj,
                SerializerFeature.BrowserCompatible,
                SerializerFeature.WriteClassName,
                SerializerFeature.DisableCircularReferenceDetect);
        System.out.println(json);

        CModel obj2 = JSON.parseObject(json,CModel.class);

        assert obj2.list.size()==obj.list.size();
    }

    @Test
    public void test53(){ //无法运行成功；因为不能创建queue；不能创建LinkedHasMap
        CModel obj = new CModel();
        obj.build();

        String json = JSON.toJSONString(obj,
                SerializerFeature.BrowserCompatible,
                SerializerFeature.WriteClassName,
                SerializerFeature.DisableCircularReferenceDetect);
        System.out.println(json);

        CModel obj2 = JSON.parseObject(json,CModel.class);

        assert obj2.list.size()==obj.list.size();
    }
}
