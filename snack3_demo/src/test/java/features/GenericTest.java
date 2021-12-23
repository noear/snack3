package features;

import _model2.House;
import _model2.Result;
import _models.ComplexModel;
import _models.Person;
import _models.Point;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.TypeReference;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.TypeRef;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

        //Result<House> result = JSON.parseObject(json, new Result<House>().getClass());
        //Result<House> result = JSONUtil.parseObj

        Result<House> result2 = ONode.loadStr(json)
                .options(opt -> opt.addDecoder(List.class, (o, t) -> o.toObjectList(House.class)))
                .toObject(new Result<House>().getClass());

        assert result2.getData().getContent().size() == 1;
        assert result2.getData().getContent().get(0).getClass() == House.class;
    }
}
