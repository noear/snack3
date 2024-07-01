package features;

import _models.OrderModel;
import _models.UserModel;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear 2023/3/7 created
 */
public class FormatTest {
    @Test
    public void beautiful1(){
        UserModel user = new UserModel();
        user.id = 1111;
        user.name = "张三";
        user.note = null;

        OrderModel order = new OrderModel();
        order.user = user;
        order.userList = new ArrayList();
        order.userList.add(user);
        order.userList.add(user);
        order.order_id = 2222;
        order.order_num = "ddddd";
        order.orderList = new ArrayList();
        order.orderList.add("111");
        order.orderList.add("222");

        String json =  ONode.load(order, Feature.PrettyFormat).toJson();

        System.out.println(json);

        String json2 = "{\n" +
                "  \"user\": {\n" +
                "    \"id\": 1111,\n" +
                "    \"name\": \"张三\"\n" +
                "  },\n" +
                "  \"userList\": [\n" +
                "    {\n" +
                "      \"id\": 1111,\n" +
                "      \"name\": \"张三\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 1111,\n" +
                "      \"name\": \"张三\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"order_num\": \"ddddd\",\n" +
                "  \"order_id\": 2222,\n" +
                "  \"orderList\": [\n" +
                "    \"111\",\n" +
                "    \"222\"\n" +
                "  ]\n" +
                "}";

        assert json2.equals(json);
    }

    @Test
    public void beautiful2(){
        UserModel user = new UserModel();
        user.id = 1111;
        user.name = "张三";
        user.note = null;

        List list = new ArrayList();
        list.add(user);
        list.add("aaaa");
        list.add(user);

        String json =  ONode.load(list, Feature.PrettyFormat).toJson();

        System.out.println(json);

        String json2 = "[\n" +
                "  {\n" +
                "    \"id\": 1111,\n" +
                "    \"name\": \"张三\"\n" +
                "  },\n" +
                "  \"aaaa\",\n" +
                "  {\n" +
                "    \"id\": 1111,\n" +
                "    \"name\": \"张三\"\n" +
                "  }\n" +
                "]";

        assert json.equals(json2);
    }
}
