package features;

import _model2.Data;
import _model2.House;
import _model2.Result;
import _model3.MessageListItem;
import _models.ComplexModel;
import _models.Person;
import _models.Point;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.TypeRef;

import java.util.*;

/**
 * @author noear 2021/1/2 created
 */
public class GenericTest {
    @Test
    public void test() {
        ComplexModel<Point> model = new ComplexModel<Point>();
        model.setId(1);
        Person person = new Person();
        person.setName("Tom");
        person.setAge(86);
        person.setBirthDay(new Date());
        person.setSensitiveInformation("This should be private over the wire");
        model.setPerson(person);

        List<Point> points = new ArrayList<Point>();
        Point point = new Point();
        point.setX(3);
        point.setY(4);
        points.add(point);

        point = new Point();
        point.setX(100);
        point.setY(100);
        points.add(point);

        //远程方法调用
        model.setPoints(points);

        String json = ONode.serialize(model);

        System.out.println(json);

        ComplexModel<Point> model1 = ONode.deserialize(json, new TypeRef<ComplexModel<Point>>() {
        }.getType());

        List<Point> points1 = model1.getPoints();
        for (Point p1 : points1) {
            System.out.println(p1.getX());
        }
    }

    @Test
    public void test2() {
        String json = "{\n" +
                "\t\"code\": \"2000\",\n" +
                "\t\"data\": {\n" +
                "\t\t\"content\": [{\n" +
                "\t\t\t\"sn\": \"P009-F07-H002-B001-R0001\",\n" +
                "\t\t\t\"dver_type\": \"1\",\n" +
                "\t\t\t\"data_status\": \"0\",\n" +
                "\t\t\t\"created_by\": \"lvm\",\n" +
                "\t\t\t\"created_date\": \"2021-12-21 14:44:36\",\n" +
                "\t\t\t\"updated_by\": \"lvm\",\n" +
                "\t\t\t\"updated_date\": \"2021-12-21 14:44:36\"\n" +
                "\t\t}],\n" +
                "\t\t\"pageNum\": 1,\n" +
                "\t\t\"pageSize\": 20,\n" +
                "\t\t\"totalElements\": 2,\n" +
                "\t\t\"pages\": 1\n" +
                "\t}\n" +
                "}";

        String json2 = ONode.loadStr(json).toJson();
        System.out.println(json2);

        Result<House> result = JSON.parseObject(json, new TypeReference<Result<House>>() {
        });
        //Result<House> result = JSONUtil.parseObj


        Result<House> result1 = ONode.deserialize(json, new Result<House>() {
        }.getClass());
        assert result1.getData().getContent().size() == 1;
        assert result1.getData().getContent().get(0).getClass() == House.class;


        Result<House> result2 = ONode.deserialize(json, new TypeRef<Result<House>>() {
        }.getType());
        assert result2.getData().getContent().size() == 1;
        assert result2.getData().getContent().get(0).getClass() == House.class;
    }

    @Test
    public void test3() {
        String json = "{\n" +
                "\t\"data\": {\n" +
                "\t\t\"content\": [{\n" +
                "\t\t\t\"sn\": \"P009-F07-H002-B001-R0001\",\n" +
                "\t\t\t\"dver_type\": \"1\",\n" +
                "\t\t\t\"data_status\": \"0\",\n" +
                "\t\t\t\"created_by\": \"lvm\",\n" +
                "\t\t\t\"created_date\": \"2021-12-21 14:44:36\",\n" +
                "\t\t\t\"updated_by\": \"lvm\",\n" +
                "\t\t\t\"updated_date\": \"2021-12-21 14:44:36\"\n" +
                "\t\t}],\n" +
                "\t\t\"pageNum\": 1,\n" +
                "\t\t\"pageSize\": 20,\n" +
                "\t\t\"totalElements\": 2,\n" +
                "\t\t\"pages\": 1\n" +
                "\t}\n" +
                "}";


        Map<String, Data> map = ONode.deserialize(json, new HashMap<String, Data>() {
        }.getClass());

        assert map.get("data").getClass() == Data.class;
    }

    @Test
    public void test4() {
        String json = "[{\n" +
                "\t\t\t\"sn\": \"P009-F07-H002-B001-R0001\",\n" +
                "\t\t\t\"dver_type\": \"1\",\n" +
                "\t\t\t\"data_status\": \"0\",\n" +
                "\t\t\t\"created_by\": \"lvm\",\n" +
                "\t\t\t\"created_date\": \"2021-12-21 14:44:36\",\n" +
                "\t\t\t\"updated_by\": \"lvm\",\n" +
                "\t\t\t\"updated_date\": \"2021-12-21 14:44:36\"\n" +
                "\t\t}]";


        List<House> ary = ONode.deserialize(json, new ArrayList<House>() {
        }.getClass());

        assert ary.size() > 0;
        assert ary.get(0).getClass() == House.class;
    }

    @Test
    public void test5() {
        String json = "{\"code\":\"2000\",\"data\":{\"content\":[{\"sn\":\"P0008\",\"dver_type\":\"1\",\"data_status\":\"0\",\"created_by\":\"xieb\",\"created_date\":\"2021-12-16 13:25:16\",\"updated_by\":\"xieb\",\"updated_date\":\"2021-12-16 13:25:16\"},{\"sn\":\"P0009\",\"dver_type\":\"1\",\"data_status\":\"0\",\"created_by\":\"xieb\",\"created_date\":\"2021-12-16 13:41:36\",\"updated_by\":\"xieb\",\"updated_date\":\"2021-12-16 17:09:02\"}],\"obj\":{\"sn\":\"P0008\",\"dver_type\":\"1\",\"data_status\":\"0\",\"created_by\":\"xieb\",\"created_date\":\"2021-12-16 13:25:16\",\"updated_by\":\"xieb\",\"updated_date\":\"2021-12-16 13:25:16\"},\"pageNum\":1,\"pageSize\":20,\"totalElements\":4,\"pages\":1}}";

        Result<House> result1 = ONode.deserialize(json, new Result<House>() {
        }.getClass());
        assert result1.getData().getContent().size() > 0;
        assert result1.getData().getContent().get(0).getClass() == House.class;

        assert result1.getData().getObj() != null;
        assert result1.getData().getObj().getClass() == House.class;
    }

    @Test
    public void test6() {
//        String s = "[{\"type\":\"receive\",\"message\":{\"msg_id\":30603422,\"id\":1639933436,\"sender\":\"8a6c90e660ed11ecbbec52540025c377\",\"r_status\":2,\"type\":2,\"content\":{\"out_trade_no\":\"202112200103454952555517322\",\"show_amount\":\"5.00\",\"total_amount\":\"5.00\",\"per_month\":\"5.00\",\"month\":1,\"discount\":\"0.00\",\"is_upgrade\":0,\"remark\":\"\",\"ext\":{\"address\":{\"name\":\"\",\"phone\":\"\",\"address\":\"\"}},\"pay_type\":2,\"product_type\":0,\"sku_detail\":[],\"sku_count\":0,\"plan\":{\"plan_id\":\"a4fa470c988411e9a72252540025c377\",\"rank\":0,\"user_id\":\"0e4112d2988411e9adc252540025c377\",\"status\":1,\"name\":\"请吃5包辣条\",\"pic\":\"\",\"desc\":\"\",\"price\":\"5.00\",\"update_time\":1561603416,\"pay_month\":1,\"show_price\":\"5.00\",\"independent\":0,\"permanent\":0,\"can_buy_hide\":1,\"need_address\":0,\"product_type\":0,\"sale_limit_count\":-1,\"need_invite_code\":false},\"time_range\":{\"begin_time\":1639929600,\"end_time\":1642608000}},\"send_time\":1639933436}},{\"type\":\"receive\",\"message\":{\"msg_id\":30603692,\"id\":1639933644,\"sender\":\"8a6c90e660ed11ecbbec52540025c377\",\"r_status\":2,\"type\":1,\"content\":\"landmark的不录了嘛\",\"send_time\":1639933644}},{\"type\":\"send\",\"message\":{\"msg_id\":30644266,\"id\":1640009303,\"sender\":\"0e4112d2988411e9adc252540025c377\",\"r_status\":2,\"type\":1,\"content\":\"直播有广告，过不了审\",\"send_time\":1640009303}},{\"type\":\"receive\",\"message\":{\"msg_id\":30689842,\"id\":1640085379,\"sender\":\"8a6c90e660ed11ecbbec52540025c377\",\"r_status\":2,\"type\":1,\"content\":\"啊好想看\",\"send_time\":1640085379}}]";
        String s = "[{\"type\":\"receive\",\"message\":{\"msg_id\":30603422,\"id\":1639933436,\"sender\":\"8a6c90e660ed11ecbbec52540025c377\",\"r_status\":2,\"type\":2,\"content\":{\"out_trade_no\":\"202112200103454952555517322\",\"show_amount\":\"5.00\",\"total_amount\":\"5.00\",\"per_month\":\"5.00\",\"month\":1,\"discount\":\"0.00\",\"is_upgrade\":0,\"remark\":\"\",\"ext\":{\"address\":{\"name\":\"\",\"phone\":\"\",\"address\":\"\"}},\"pay_type\":2,\"product_type\":0,\"sku_detail\":[],\"sku_count\":0,\"plan\":{\"plan_id\":\"a4fa470c988411e9a72252540025c377\",\"rank\":0,\"user_id\":\"0e4112d2988411e9adc252540025c377\",\"status\":1,\"name\":\"请吃5包辣条\",\"pic\":\"\",\"desc\":\"\",\"price\":\"5.00\",\"update_time\":1561603416,\"pay_month\":1,\"show_price\":\"5.00\",\"independent\":0,\"permanent\":0,\"can_buy_hide\":1,\"need_address\":0,\"product_type\":0,\"sale_limit_count\":-1,\"need_invite_code\":false},\"time_range\":{\"begin_time\":1639929600,\"end_time\":1642608000}},\"send_time\":1639933436}}]";
        System.out.println(s);
        List<MessageListItem> messageListItems = ONode.loadStr(s).toObjectList(MessageListItem.class);
        for (MessageListItem messageListItem : messageListItems) {
            System.out.println(messageListItem);
            assert messageListItem.getMessage().getContent() instanceof String;
        }
    }

    @Test
    public void test7() {
        String s = "[[1,2,4],[6,9,1001]]";

        List<List<Integer>> list = ONode.deserialize(s, new TypeRef<List<List<Integer>>>() {
        }.getType());

        assert list != null;
        assert list.size() == 2;
        assert list.get(0).size() == 3;
    }
}
