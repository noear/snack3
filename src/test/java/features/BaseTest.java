package features;

import org.junit.jupiter.api.Test;
import org.noear.snack.*;
import org.noear.snack.core.BeanCodec;
import org.noear.snack.core.JsonReader;
import org.noear.snack.core.Options;
import org.noear.snack.exception.SchemaException;
import org.noear.snack.query.JsonPath;
import org.noear.snack.schema.SchemaValidator;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author noear 2025/3/15 created
 */
public class BaseTest {
    @Test
    public void case1() throws Exception {
        String json = "{"
                + "\"name\": \"Alice\","
                + "\"age\": 28,"
                + "\"scores\": [95.5, 89.0, 92.3],"
                + "\"metadata\": {"
                + "  \"uid\": \"\\u0041\\u0042\\u0043\","
                + "  \"active\": true"
                + "}"
                + "}";

        ONode node = JsonReader.read(json);

        // 验证解析结果
        System.out.println(node.get("name").getString()); // Alice
        System.out.println(node.get("age").getInt()); // 28
        System.out.println(node.get("scores").get(0).getDouble()); // 95.5
        System.out.println(node.get("metadata").get("uid").getString()); // ABC
        System.out.println(node.get("metadata").get("active").getBoolean()); // true
    }

    @Test
    public void case2() throws IOException {
        // Schema定义示例
        String schemaJson = "{"
                + "\"type\": \"object\","
                + "\"required\": [\"name\", \"age\"],"
                + "\"properties\": {"
                + "  \"name\": {\"type\": \"string\"},"
                + "  \"age\": {\"type\": \"integer\", \"minimum\": 0}"
                + "}"
                + "}";


        System.out.println(schemaJson);

        // 数据校验
        JsonReader parser = new JsonReader(new StringReader(schemaJson));
        ONode schemaNode = parser.read();
        SchemaValidator validator = new SchemaValidator(schemaNode);

        ONode data = new JsonReader(new StringReader("{\"name\":\"Alice\",\"age\":-5}")).read();
        try {
            validator.validate(data);
        } catch (SchemaException e) {
            System.out.println(e.getMessage());
            // 输出: Value -5.0 < minimum(0.0) at $.age
        }
    }

    @Test
    public void case3() throws Exception {
        Properties properties = new Properties();
        properties.setProperty("order.item[0].user", "1");

        ONode node = BeanCodec.serialize(properties);
        System.out.println(node.toJson()); // 输出: {"order":{"item":[{"user":"1"}]}}
        assert "{\"order\":{\"item\":[{\"user\":\"1\"}]}}".equals(node.toJson());

        Properties deserializedProperties = BeanCodec.deserialize(node, Properties.class);
        System.out.println(deserializedProperties.getProperty("order.item[0].user")); // 输出: 1
        assert "1".equals(deserializedProperties.getProperty("order.item[0].user"));
    }

    @Test
    public void case4() {
        ONode root = ONode.loadJson("{}");
        JsonPath.delete(root, "$.store.book[0]");
        ONode result = JsonPath.select(root, "$.store.book[0]");
        assertNull(result.isNull());
    }

    @Test
    public void case5() {
        ONode root = ONode.loadJson("{}");
        JsonPath.create(root, "$.store.newNode");
        ONode result = JsonPath.select(root, "$.store.newNode");
        assertNotNull(result);
        assertTrue(result.isNull());
    }

    @Test
    public void case6() {
        Options options = Options.builder().schema(ONode.loadJson("{user:{name:''}}")).build();
        ONode.loadJson("{}", options);
    }
}
