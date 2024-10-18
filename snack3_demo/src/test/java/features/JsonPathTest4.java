package features;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;

/**
 * @author noear 2023/5/13 created
 */
public class JsonPathTest4 {
    @Test
    public void test1() {
        String test = "{\"1\":{\"a1\":[{\"id\":\"a1\"},{\"id\":\"a2\"}],\"b1\":[{\"id\":\"b1\"},{\"id\":\"b2\"}]},\"2\":{\"a2\":[{\"id\":\"a1\",\"id1\":\"a11\",\"userId\":\"a12\"},{\"id\":\"a2\"}],\"b2\":[{\"id\":\"b1\"},{\"id\":\"b2\"}]}}";
        String jsonPath = "$..*[?(@.id)]";
        String json1 = ONode.load(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.stringify(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test2() {
        String test = "{\"1\":{\"a1\":[{\"id\":\"a1\"},{\"id\":\"a2\"}],\"b1\":[{\"id\":\"b1\"},{\"id\":\"b2\"}]},\"2\":{\"a2\":[{\"id\":\"a1\",\"id1\":\"a11\",\"userId\":\"a12\"},{\"id\":\"a2\"}],\"b2\":[{\"id\":\"b1\"},{\"id\":\"b2\"}]}}";
        String jsonPath = "$.*.*";
        String json1 = ONode.load(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.stringify(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test2_2() {
        String test = "{\"1\":{\"a1\":[{\"id\":\"a1\"},{\"id\":\"a2\"}],\"b1\":[{\"id\":\"b1\"},{\"id\":\"b2\"}]},\"2\":{\"a2\":[{\"id\":\"a1\",\"id1\":\"a11\",\"userId\":\"a12\"},{\"id\":\"a2\"}],\"b2\":[{\"id\":\"b1\"},{\"id\":\"b2\"}]}}";
        String jsonPath = "$.*.*.*";
        String json1 = ONode.load(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.stringify(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test2_2_2() {
        String test = "{\"1\":{\"a1\":[{\"id\":\"a1\"},{\"id\":\"a2\"}],\"b1\":[{\"id\":\"b1\"},{\"id\":\"b2\"}]},\"2\":{\"a2\":[{\"id\":\"a1\",\"id1\":\"a11\",\"userId\":\"a12\"},{\"id\":\"a2\"}],\"b2\":[{\"id\":\"b1\"},{\"id\":\"b2\"}]}}";
        String jsonPath = "$.[*].[*].[*]";
        String json1 = ONode.load(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.stringify(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test3() {
        String test = "[1,2,3]";
        String jsonPath = "$.*";
        String json1 = ONode.load(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.stringify(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test3_2() {
        String test = "[1,2,3]";
        String jsonPath = "$.[*]";
        String json1 = ONode.load(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.stringify(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test4() {
        String test = "[{a:1},{a:2},{a:3}]";
        String jsonPath = "$.*";
        String json1 = ONode.load(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.stringify(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test4_2() {
        String test = "[{a:1},{a:2},{a:3}]";
        String jsonPath = "$.[*]";
        String json1 = ONode.load(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.stringify(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test5() {
        String test = "[{a:1},{a:2},{a:3}]";
        String jsonPath = "$.*.*";
        String json1 = ONode.load(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.stringify(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test5_2() {
        String test = "[{a:1},{a:2},{a:3}]";
        String jsonPath = "$.[*].[*]";
        String json1 = ONode.load(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.stringify(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test6() {
        String test = "[{a:1},{a:2},{a:3}]";
        String jsonPath = "$.*.[*]";
        String json1 = ONode.load(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.stringify(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }

    @Test
    public void test7() {
        String test = "[{\"field\":\"l1-field-1\",\"children\":[{\"field\":\"l2-field-1\",\"fields\":[{\"field\":\"l3-field-1\"},{\"field\":\"l3-field-2\"}]},{\"field\":\"l2-field-2\",\"fields\":[{\"field\":\"l3-field-1\"},{\"field\":\"l3-field-2\"}]}]},{\"field\":\"l1-field-2\",\"children\":[{\"field\":\"l2-field-1\",\"fields\":[{\"field\":\"l3-field-1\"},{\"field\":\"l3-field-2\"}]},{\"field\":\"l2-field-2\",\"fields\":[{\"field\":\"l3-field-1\"},{\"field\":\"l3-field-2\"}]}]}]";

        String jsonPath = "$.[?(@.field == 'l1-field-1')].children[?(@.field == 'l2-field-1')]";
        String json1 = ONode.load(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.stringify(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();

    }

    @Test
    public void test8() {
        String test = "[{\"field\":\"l1-field-1\",\"children\":[{\"field\":\"l2-field-1\",\"fields\":[{\"field\":\"l3-field-1\"},{\"field\":\"l3-field-2\"}]},{\"field\":\"l2-field-2\",\"fields\":[{\"field\":\"l3-field-1\"},{\"field\":\"l3-field-2\"}]}]},{\"field\":\"l1-field-2\",\"children\":[{\"field\":\"l2-field-1\",\"fields\":[{\"field\":\"l3-field-1\"},{\"field\":\"l3-field-2\"}]},{\"field\":\"l2-field-2\",\"fields\":[{\"field\":\"l3-field-1\"},{\"field\":\"l3-field-2\"}]}]}]";

        String jsonPath = "$[?(@.field == 'l1-field-1')].children[?(@.field == 'l2-field-1')].fields[*]";
        String json1 = ONode.load(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.stringify(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();

    }

    @Test
    public void test9() {
        String json = "{\"request1\":{\"result\":[{\"relTickers\":[{\"tickerId\":1},{\"tickerId\":1.1}],\"accountId\":400006},{\"relTickers\":[{\"tickerId\":2},{\"tickerId\":2.2}]},{\"relTickers\":[{\"tickerId\":3}]},{\"relTickers\":[{\"tickerId\":4}]},{\"relTickers\":[{\"tickerId\":5}]},{\"relTickers\":[{\"tickerId\":6}]}]}}\n";

        String jsonpathStr1 = "request1..tickerId.first()";
        String jsonpathStr1_b = "request1..tickerId.last()";

        String jsonpathStr2 = "request1.result[*].relTickers.tickerId.first()";
        String jsonpathStr2_b = "request1.result[*].relTickers.tickerId.last()";

        String jsonpathStr3 = "request1.result[*].relTickers.first().tickerId";
        String jsonpathStr3_b = "request1.result[*].relTickers.last().tickerId";


        assert_do("1", json, jsonpathStr1);
        assert_do("6", json, jsonpathStr1_b);

        assert_do("1", json, jsonpathStr2);
        assert_do("6", json, jsonpathStr2_b);

        System.out.println(ONode.load(json).select("request1.result[*].relTickers.first()"));

        assert_do("1", json, jsonpathStr3);
        assert_do("6", json, jsonpathStr3_b);
    }

    private void assert_do(String hint, String json, String jsonpathStr) {
        System.out.println("::::" + hint);
        Object tmp = ONode.load(json).select(jsonpathStr);
        System.out.println(tmp);

        assert tmp.toString().equals(hint.toString());
    }

    @Test
    public void test10_1() {
        String json = "{\"result\":[]}";

        ONode oNode = ONode.load(json).select("$.result[*].amount.sum()");
        System.out.println(oNode.toString());

        assert oNode.getLong() == 0L;
    }

    @Test
    public void test10_2() {
        String json = "{\"result\":[]}";

        ONode oNode = ONode.load(json).select("$.result[*].amount.min()");
        System.out.println(oNode.toString());

        assert oNode.getLong() == 0L;
    }

    @Test
    public void test10_3() {
        String json = "{\"result\":[]}";

        ONode oNode = ONode.load(json).select("$.result[*].amount.max()");
        System.out.println(oNode.toString());

        assert oNode.getLong() == 0L;


        System.out.println(ONode.load(json).pathList("$.result[*].amount.max()"));
        assert ONode.load(json).pathList("$.result[*].amount.max()").count() == 0;
    }
}
