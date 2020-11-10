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
import java.util.stream.Collectors;

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

    @Test
    public void test3(){
        String json = test3_json();

        ONode oNode = ONode.loadStr(json);

        //$.content.amount_detail.amount_units[?(@.name == "C_score")].amount

        ONode tmp = oNode.select("$.content.amount_detail.amount_units[?(@.name == 'C_score')].amount.min()");

        assert tmp.getInt() == 803;


        String x = oNode.select("$.content.amount_detail.amount_units.name").ary().stream().map(n->n.getString()).collect(Collectors.joining(","));

        System.out.println(x);
    }

    @Test
    public void test4(){
        String json = test4_json();

        ONode oNode = ONode.loadStr(json);

        assert oNode != null;
    }

    private String test4_json(){
        return "{\n" +
                "  \"catalogMappings\": [\n" +
                "    { \"index\":0,\n" +
                "      \"left\": {\"name\": \"water2\"},\n" +
                "      \"right\": {\"name\": \"water2\"},\n" +
                "      \"entityMappings\": [\n" +
                "        {\n" +
                "          \"index\":0,\n" +
                "          \"left\": {\"name\": \"test\"},\n" +
                "          \"right\": {\"name\": \"test\"},\n" +
                "          \"filterInsert\": true,\n" +
                "          \"filterUpdate\": true,\n" +
                "          \"filterDelete\": true,\n" +
                "          \"memberMappings\": [\n" +
                "            {\n" +
                "              \"index\":0,\n" +
                "              \"left\": {\"name\": \"n1\", \"type\": \"varchar(40)\" },\n" +
                "              \"right\": {\"name\": \"n1\", \"type\": \"varchar(40)\"}\n" +
                "            },\n" +
                "            {\n" +
                "              \"index\":1,\n" +
                "              \"left\": {\"name\": \"n2\", \"type\": \"varchar(100)\"},\n" +
                "              \"right\": {\"name\": \"n2\", \"type\": \"varchar(100)\"}\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"index\":1,\n" +
                "          \"left\": {\"name\": \"test2\"},\n" +
                "          \"right\": {\"name\": \"test2\"},\n" +
                "          \"filterInsert\": true,\n" +
                "          \"filterUpdate\": true,\n" +
                "          \"filterDelete\": true,\n" +
                "          \"memberMappings\": [\n" +
                "            {\n" +
                "              \"index\":0,\n" +
                "              \"left\": {\"name\": \"t1\", \"type\": \"varchar(40)\" },\n" +
                "              \"right\": {\"name\": \"t1\", \"type\": \"varchar(40)\"}\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

    private String test3_json(){
        return "{\n" +
                "\t\"message\": \"ok\",\n" +
                "\t\"content\": {\n" +
                "\t\t\"result\": 100,\n" +
                "\t\t\"amount\": 3,\n" +
                "\t\t\"rule_detail\": {\n" +
                "\t\t\t\"app_id\": \"\",\n" +
                "\t\t\t\"app_name\": \"\",\n" +
                "\t\t\t\"ext_data\": 0,\n" +
                "\t\t\t\"factor_list\": [],\n" +
                "\t\t\t\"policy_id\": \"5f180d0d28f6eb6cd10df87e\",\n" +
                "\t\t\t\"policy_mode\": 1,\n" +
                "\t\t\t\"policy_name\": \"综合信用评分A\",\n" +
                "\t\t\t\"policy_name_e\": \"XYD_CL_0001\"\n" +
                "\t\t},\n" +
                "\t\t\"credit_id\": \"zxxf_d145105c-2407-436c-b786-a1d3ba0b2c5f\",\n" +
                "\t\t\"amount_detail\": {\n" +
                "\t\t\t\"amount_units\": [\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"amount\": 803,\n" +
                "\t\t\t\t\t\"name\": \"C_score\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"amount\": 6,\n" +
                "\t\t\t\t\t\"name\": \"S_health\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"amount\": 6,\n" +
                "\t\t\t\t\t\"name\": \"C_stability\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"amount\": 7,\n" +
                "\t\t\t\t\t\"name\": \"I_variety\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"amount\": 3,\n" +
                "\t\t\t\t\t\"name\": \"L_index\"\n" +
                "\t\t\t\t},\n" +
                "\t\t\t\t{\n" +
                "\t\t\t\t\t\"amount\": 3,\n" +
                "\t\t\t\t\t\"name\": \"P_index\"\n" +
                "\t\t\t\t}\n" +
                "\t\t\t]\n" +
                "\t\t},\n" +
                "\t\t\"rule_result\": {},\n" +
                "\t\t\"message\": \"accepted\"\n" +
                "\t},\n" +
                "\t\"status\": \"1000\"\n" +
                "}";
    }


}
