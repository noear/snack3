package features;

import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author noear 2022/1/8 created
 */
public class StringTest {
    @Test
    public void test1() {
        ONode attr = new ONode();
        attr.set("a", 1);
        attr.set("b", "b");
        attr.set("c", true);
        attr.set("d", new Date());

        ONode oNode = new ONode();

        oNode.set("code", 1);
        oNode.set("name", "world");
        oNode.set("attr", attr.toJson());

        String json = oNode.toJson();
        System.out.println(json);

        String json2 = ONode.loadStr(json).toJson();
        System.out.println(json2);

        assert json.equals(json2);

        System.out.println(ONode.loadStr(json, Feature.StringJsonToNode).toJson());
    }

    @Test
    public void test2() {
        String tmp = ONode.stringify(UUID.randomUUID());
        assert tmp.contains("-");
        System.out.println(tmp);
    }

    @Test
    public void test3() {
        Map<String, Object> map = new HashMap<>();

        map.put("a", 1);
        map.put("b", "2");
        map.put("c", "{d:'3'}");


        String json = ONode.loadObj(map).toJson();
        System.out.println(json);
        assert ONode.loadStr(json).get("c").isValue();

        json = ONode.loadObj(map, Feature.StringJsonToNode).toJson();
        System.out.println(json);
        assert ONode.loadStr(json).get("c").isObject();
    }

    @Test
    public void test4() {
        //测试前台json字符串
        String str = "{\n" +
                "    \"id\": 20,\n" +
                "    \"key\": \"MACHINERY_TYPE_QUIP_TSQZJ_01\",\n" +
                "    \"tenantId\": \"5a920f536428050edddcce5212f36a97\",\n" +
                "    \"moduleLevel\": \"机械管理:电子档案\",\n" +
                "    \"describe\": \"塔吊关联IOT设备类别\",\n" +
                "    \"createTime\": 1659678000000,\n" +
                "    \"modifyTime\": 1659678000000,\n" +
                "    \"value\": \"[{\\\"unitTypeName\\\":\\\"塔机安全监管系统\\\",\\\"iotType\\\":\\\"main\\\"},{\\\"unitTypeName\\\":\\\"吊钩可视化系统\\\",\\\"iotType\\\":\\\"video\\\"}]\",\n" +
                "    \"example\": \"[{\\\"unitTypeName\\\":\\\"塔机安全监管系统\\\",\\\"iotType\\\":\\\"main\\\"},{\\\"unitTypeName\\\":\\\"吊钩可视化系统\\\",\\\"iotType\\\":\\\"video\\\"}]\"\n" +
                "}";
        JSONObject json = (JSONObject) JSONObject.parse(str); //故意转成对象，下面用loadObj
        ONode jsonNode = ONode.loadObj(json, Feature.StringJsonToNode);

        System.out.println(jsonNode.toJson());

        String typeName = jsonNode.select("$.value[?(iotType == 'main')]").get(0).get("unitTypeName").getString();
        System.out.println("------Feature.StringJsonToNode typeName:{}" + typeName);

        String jsonStr = ONode.loadObj(json, Feature.StringJsonToNode).toJson();
        ONode jsonNode2 = ONode.loadStr(jsonStr);
        String typeName2 = jsonNode2.select("$.value[?(iotType == 'main')]").get(0).get("unitTypeName").getString();
        System.out.println("------ typeName:{}" + typeName2);
    }

    @Test
    public void test5() {
        String json = "'1a'";
        System.out.println(ONode.loadStr(json).getString());
        System.out.println(ONode.loadStr(json).toJson());
    }
}
