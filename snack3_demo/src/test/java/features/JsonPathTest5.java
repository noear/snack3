package features;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;

/**
 * @author noear 2023/11/3 created
 */
public class JsonPathTest5 {
    @Test
    public void test1() {
        String json = "[{\"id\":0,\"treePath\":\"1\",\"a\":[{\"id\":1,\"treePath\":\"123\",\"subItem\":[{\"id\":3,\"treePath\":\"123\"}]}],\"b\":\"a\"},{\"id\":2}]";

        ReadContext context = JsonPath.parse(json);

        JSONArray tmp = context.read("$..*");
        System.out.println(tmp.toJSONString());
        assert tmp.size() == 14;

        ONode tmp2 = ONode.load(json).select("$..*");
        System.out.println(tmp2);
        assert tmp2.isArray();
        assert tmp2.count() == 14;
    }

    @Test
    public void test2() {
        String json = "[{\"id\":0,\"treePath\":\"1\",\"a\":[{\"id\":1,\"treePath\":\"123\",\"subItem\":[{\"id\":3,\"treePath\":\"123\"}]}],\"b\":\"a\"},{\"id\":2}]";

        ReadContext context = JsonPath.parse(json);

        JSONArray tmp = context.read("$..*[?(@.treePath)]");
        System.out.println(tmp);
        assert tmp.size() == 5;

        ONode tmp2 = ONode.load(json).select("$..*[?(@.treePath)]");
        System.out.println(tmp2);
        assert tmp2.isArray();
        assert tmp2.count() == 5;
    }

    @Test
    public void test3() {
        String json = "[{\"id\":0,\"treePath\":\"1\",\"a\":[{\"id\":1,\"treePath\":\"123\",\"subItem\":[{\"id\":3,\"treePath\":\"123\"}]}],\"b\":\"a\"},{\"id\":2}]";

        ReadContext context = JsonPath.parse(json);

        JSONArray tmp = context.read("$..[?(@.treePath)]");
        System.out.println(tmp);
        assert tmp.size() == 3;

        ONode tmp2 = ONode.load(json).select("$..[?(@.treePath)]");
        System.out.println(tmp2);
        assert tmp2.count() == 3;
    }

    @Test
    public void test4() {
        String json = "{\"request1\":{\"result\":[{\"relTickers\":[{\"tickerId\":1},{\"tickerId\":1.1}],\"accountId\":400006},{\"relTickers\":[{\"tickerId\":2},{\"tickerId\":2.2}]},{\"relTickers\":[{\"tickerId\":3}]},{\"relTickers\":[{\"tickerId\":4}]},{\"relTickers\":[{\"tickerId\":5}]},{\"relTickers\":[{\"tickerId\":6}]}]}}\n";

        String jsonpathStr1 = "request1.result[*]";
        String jsonpathStr2 = "request1.result[*].relTickers";
        String jsonpathStr3 = "request1.result[*].relTickers[0]";
        String jsonpathStr4 = "request1.result[*].relTickers[0].tickerId";

        compatible_do("1", json, jsonpathStr1);
        compatible_do("2", json, jsonpathStr2);
        compatible_do("3", json, jsonpathStr3);
        compatible_do("4", json, jsonpathStr4);
    }

    private void compatible_do(String hint, String json, String jsonpathStr) {
        System.out.println("::::" + hint);
        Object tmp = ONode.load(json).select(jsonpathStr);
        System.out.println(tmp);

        Object tmp2 = JsonPath.read(json, jsonpathStr);
        System.out.println(tmp2);

        assert tmp.toString().equals(tmp2.toString());
    }
}
