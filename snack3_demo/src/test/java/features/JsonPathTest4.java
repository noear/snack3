package features;

import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.noear.snack.ONode;

/**
 * @author noear 2023/5/13 created
 */
public class JsonPathTest4 {
    @Test
    public void test1(){
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
    public void test2(){
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
    public void test2_2(){
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
    public void test2_2_2(){
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
    public void test3(){
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
    public void test3_2(){
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
    public void test4(){
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
    public void test4_2(){
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
    public void test5(){
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
    public void test5_2(){
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
    public void test6(){
        String test = "[{a:1},{a:2},{a:3}]";
        String jsonPath = "$.*.[*]";
        String json1 = ONode.load(test).select(jsonPath).toJson();
        System.out.println("org.noear.snack: " + json1);

        Object documentContext = JsonPath.read(test, jsonPath);
        String json2 = ONode.stringify(documentContext);
        System.out.println("com.jayway.jsonpath: " + json2);

        assert json1.length() == json2.length();
    }
}
