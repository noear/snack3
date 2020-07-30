package features;

import _models.ComplexModel;
import _models.Person;
import _models.Point;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.junit.Test;
import org.noear.snack.ONode;

import java.util.*;

public class _test2 {
    @Test
    public void test(){
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

        Map<String,Object> data = new LinkedHashMap<>();
        data.put("model",model);

        String json = ONode.stringify(data);
        String json2 = JSON.toJSONString(data,
                SerializerFeature.BrowserCompatible,
                SerializerFeature.DisableCircularReferenceDetect);

        call(json,model);
    }

    private void call(String json, ComplexModel<Point> model){
        System.out.println(json);

        ONode data2 = ONode.loadStr(json);
        ComplexModel model2 = data2.get("model").toObject(model.getClass());

        assert model2 != null;
    }

    private void call2(String json, ComplexModel<Point> model){
        System.out.println(json);

        JSONObject data2 = JSON.parseObject(json);
        ComplexModel model2 = data2.getObject("model",model.getClass());

        assert model2 != null;
    }

    @Test
    public void test2(){
         String json = "[{\"code\":0,\"name\":\"缺陷\",\"icon\":\"fa-bug\"},{\"code\":1,\"name\":\"改进\",\"icon\":\"fa-twitter\"},{\"code\":2,\"name\":\"需求\",\"icon\":\"fa-circle-o\"}]";

         Object tmp = ONode.loadStr(json).toData();

         assert tmp instanceof List;
    }
}
