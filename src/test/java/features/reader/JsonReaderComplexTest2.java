package features.reader;

import org.junit.jupiter.api.Test;
import org.noear.snack.core.JsonReader;
import org.noear.snack.ONode;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class JsonReaderComplexTest2 {

    // ========================= 复杂测试用例（30 个） =========================

    @Test
    void testParseDeeplyNestedObject() throws Exception {
        String json = "{\"a\": {\"b\": {\"c\": {\"d\": {\"e\": {\"f\": {\"g\": 42}}}}}}}";
        ONode node = new JsonReader(new StringReader(json)).read();
        assertEquals(42, node.get("a").get("b").get("c").get("d").get("e").get("f").get("g").getInt());
    }

    @Test
    void testParseDeeplyNestedArray() throws Exception {
        String json = "[[[[[[[42]]]]]]]";
        ONode node = new JsonReader(new StringReader(json)).read();
        assertEquals(42, node.get(0).get(0).get(0).get(0).get(0).get(0).get(0).getInt());
    }

    @Test
    void testParseMixedNestedTypes() throws Exception {
        String json = "{\"a\": [{\"b\": 1}, {\"c\": [2, 3]}]}";
        ONode node = new JsonReader(new StringReader(json)).read();
        assertEquals(1, node.get("a").get(0).get("b").getInt());
        assertTrue(node.get("a").get(1).get("c").isArray());
        assertEquals(2, node.get("a").get(1).get("c").get(0).getInt());
        assertEquals(3, node.get("a").get(1).get("c").get(1).getInt());
    }

    @Test
    void testParseLargeNestedObject() throws Exception {
        StringBuilder json = new StringBuilder("{\"a\": {\"b\": {");
        for (int i = 0; i < 1000; i++) {
            json.append("\"key").append(i).append("\": ").append(i);
            if (i < 999) json.append(", ");
        }
        json.append("}}}");
        ONode node = new JsonReader(new StringReader(json.toString())).read();
        assertEquals(999, node.get("a").get("b").get("key999").getInt());
    }

    @Test
    void testParseLargeNestedArray() throws Exception {
        StringBuilder json = new StringBuilder("[[");
        for (int i = 0; i < 1000; i++) {
            json.append(i);
            if (i < 999) json.append(", ");
        }
        json.append("]]");
        System.out.println(json.toString());
        ONode node = new JsonReader(new StringReader(json.toString())).read();

        // 验证外层数组
        assertTrue(node.isArray());
        assertEquals(1, node.size());

        // 验证内层数组
        ONode innerArray = node.get(0);
        assertTrue(innerArray.isArray());
        assertEquals(1000, innerArray.size());

        // 验证内层数组的元素
        for (int i = 0; i < 1000; i++) {
            assertEquals(i, innerArray.get(i).getInt());
        }
    }

    @Test
    void testParseObjectWithMixedTypes() throws Exception {
        String json = "{\"string\": \"Hello\", \"number\": 42, \"float\": 3.14, \"boolean\": true, \"null\": null, \"array\": [1, 2, 3], \"object\": {\"key\": \"value\"}}";
        ONode node = new JsonReader(new StringReader(json)).read();
        assertEquals("Hello", node.get("string").getString());
        assertEquals(42, node.get("number").getInt());
        assertEquals(3.14, node.get("float").getDouble());
        assertTrue(node.get("boolean").getBoolean());
        assertNull(node.get("null").getValue());
        assertTrue(node.get("array").isArray());
        assertEquals(3, node.get("array").size());
        assertTrue(node.get("object").isObject());
        assertEquals("value", node.get("object").get("key").getString());
    }

    @Test
    void testParseArrayWithMixedTypes() throws Exception {
        String json = "[1, \"two\", true, null, {\"key\": \"value\"}]";
        ONode node = new JsonReader(new StringReader(json)).read();
        assertTrue(node.isArray());
        assertEquals(5, node.size());
        assertEquals(1, node.get(0).getInt());
        assertEquals("two", node.get(1).getString());
        assertTrue(node.get(2).getBoolean());
        assertNull(node.get(3).getValue());
        assertTrue(node.get(4).isObject());
        assertEquals("value", node.get(4).get("key").getString());
    }

    @Test
    void testParseObjectWithEmptyArray() throws Exception {
        String json = "{\"array\": []}";
        ONode node = new JsonReader(new StringReader(json)).read();
        assertTrue(node.get("array").isArray());
        assertEquals(0, node.get("array").size());
    }

    @Test
    void testParseObjectWithEmptyObject() throws Exception {
        String json = "{\"object\": {}}";
        ONode node = new JsonReader(new StringReader(json)).read();
        assertTrue(node.get("object").isObject());
        assertEquals(0, node.get("object").size());
    }

    @Test
    void testParseArrayWithEmptyObject() throws Exception {
        String json = "[{}]";
        ONode node = new JsonReader(new StringReader(json)).read();
        assertTrue(node.isArray());
        assertEquals(1, node.size());
        assertTrue(node.get(0).isObject());
        assertEquals(0, node.get(0).size());
    }

    @Test
    void testParseArrayWithEmptyArray() throws Exception {
        String json = "[[]]";
        ONode node = new JsonReader(new StringReader(json)).read();
        assertTrue(node.isArray());
        assertEquals(1, node.size());
        assertTrue(node.get(0).isArray());
        assertEquals(0, node.get(0).size());
    }

    @Test
    void testParseObjectWithNestedEmptyArrays() throws Exception {
        String json = "{\"a\": [], \"b\": [[]], \"c\": [[[]]]}";
        ONode node = new JsonReader(new StringReader(json)).read();
        assertTrue(node.get("a").isArray());
        assertEquals(0, node.get("a").size());
        assertTrue(node.get("b").isArray());
        assertEquals(1, node.get("b").size());
        assertTrue(node.get("b").get(0).isArray());
        assertEquals(0, node.get("b").get(0).size());
        assertTrue(node.get("c").isArray());
        assertEquals(1, node.get("c").size());
        assertTrue(node.get("c").get(0).isArray());
        assertEquals(1, node.get("c").get(0).size());
        assertTrue(node.get("c").get(0).get(0).isArray());
        assertEquals(0, node.get("c").get(0).get(0).size());
    }

    @Test
    void testParseObjectWithNestedEmptyObjects() throws Exception {
        String json = "{\"a\": {}, \"b\": {\"c\": {}}, \"d\": {\"e\": {\"f\": {}}}}";
        ONode node = new JsonReader(new StringReader(json)).read();
        assertTrue(node.get("a").isObject());
        assertEquals(0, node.get("a").size());
        assertTrue(node.get("b").isObject());
        assertEquals(1, node.get("b").size());
        assertTrue(node.get("b").get("c").isObject());
        assertEquals(0, node.get("b").get("c").size());
        assertTrue(node.get("d").isObject());
        assertEquals(1, node.get("d").size());
        assertTrue(node.get("d").get("e").isObject());
        assertEquals(1, node.get("d").get("e").size());
        assertTrue(node.get("d").get("e").get("f").isObject());
        assertEquals(0, node.get("d").get("e").get("f").size());
    }

    @Test
    void testParseArrayWithNestedMixedTypes() throws Exception {
        String json = "[1, {\"a\": [2, {\"b\": 3}]}, [4, {\"c\": 5}]]";
        ONode node = new JsonReader(new StringReader(json)).read();
        assertTrue(node.isArray());
        assertEquals(3, node.size());
        assertEquals(1, node.get(0).getInt());
        assertTrue(node.get(1).isObject());
        assertEquals(2, node.get(1).get("a").get(0).getInt());
        assertEquals(3, node.get(1).get("a").get(1).get("b").getInt());
        assertTrue(node.get(2).isArray());
        assertEquals(4, node.get(2).get(0).getInt());
        assertEquals(5, node.get(2).get(1).get("c").getInt());
    }

    @Test
    void testParseObjectWithNestedMixedTypes() throws Exception {
        String json = "{\"a\": [1, {\"b\": 2}], \"c\": {\"d\": [3, {\"e\": 4}]}}";
        ONode node = new JsonReader(new StringReader(json)).read();
        assertTrue(node.isObject());
        assertEquals(2, node.size());
        assertTrue(node.get("a").isArray());
        assertEquals(1, node.get("a").get(0).getInt());
        assertEquals(2, node.get("a").get(1).get("b").getInt());
        assertTrue(node.get("c").isObject());
        assertEquals(3, node.get("c").get("d").get(0).getInt());
        assertEquals(4, node.get("c").get("d").get(1).get("e").getInt());
    }

    @Test
    void testParseArrayWithNestedArrays() throws Exception {
        String json = "[[1, 2], [3, 4], [5, 6]]";
        ONode node = new JsonReader(new StringReader(json)).read();
        assertTrue(node.isArray());
        assertEquals(3, node.size());
        assertEquals(1, node.get(0).get(0).getInt());
        assertEquals(2, node.get(0).get(1).getInt());
        assertEquals(3, node.get(1).get(0).getInt());
        assertEquals(4, node.get(1).get(1).getInt());
        assertEquals(5, node.get(2).get(0).getInt());
        assertEquals(6, node.get(2).get(1).getInt());
    }

    @Test
    void testParseObjectWithNestedArrays() throws Exception {
        String json = "{\"a\": [1, 2], \"b\": [3, 4], \"c\": [5, 6]}";
        ONode node = new JsonReader(new StringReader(json)).read();
        assertTrue(node.isObject());
        assertEquals(3, node.size());
        assertEquals(1, node.get("a").get(0).getInt());
        assertEquals(2, node.get("a").get(1).getInt());
        assertEquals(3, node.get("b").get(0).getInt());
        assertEquals(4, node.get("b").get(1).getInt());
        assertEquals(5, node.get("c").get(0).getInt());
        assertEquals(6, node.get("c").get(1).getInt());
    }

    @Test
    void testParseArrayWithNestedObjects() throws Exception {
        String json = "[{\"a\": 1}, {\"b\": 2}, {\"c\": 3}]";
        ONode node = new JsonReader(new StringReader(json)).read();
        assertTrue(node.isArray());
        assertEquals(3, node.size());
        assertEquals(1, node.get(0).get("a").getInt());
        assertEquals(2, node.get(1).get("b").getInt());
        assertEquals(3, node.get(2).get("c").getInt());
    }

    @Test
    void testParseObjectWithNestedObjects() throws Exception {
        String json = "{\"a\": {\"b\": 1}, \"c\": {\"d\": 2}, \"e\": {\"f\": 3}}";
        ONode node = new JsonReader(new StringReader(json)).read();
        assertTrue(node.isObject());
        assertEquals(3, node.size());
        assertEquals(1, node.get("a").get("b").getInt());
        assertEquals(2, node.get("c").get("d").getInt());
        assertEquals(3, node.get("e").get("f").getInt());
    }

    @Test
    void testParseArrayWithMixedNestedTypes() throws Exception {
        String json = "[1, {\"a\": 2}, [3, {\"b\": 4}]]";
        ONode node = new JsonReader(new StringReader(json)).read();
        assertTrue(node.isArray());
        assertEquals(3, node.size());
        assertEquals(1, node.get(0).getInt());
        assertEquals(2, node.get(1).get("a").getInt());
        assertEquals(3, node.get(2).get(0).getInt());
        assertEquals(4, node.get(2).get(1).get("b").getInt());
    }

    @Test
    void testParseObjectWithMixedNestedTypes() throws Exception {
        String json = "{\"a\": 1, \"b\": {\"c\": 2}, \"d\": [3, {\"e\": 4}]}";
        ONode node = new JsonReader(new StringReader(json)).read();
        assertTrue(node.isObject());
        assertEquals(3, node.size());
        assertEquals(1, node.get("a").getInt());
        assertEquals(2, node.get("b").get("c").getInt());
        assertEquals(3, node.get("d").get(0).getInt());
        assertEquals(4, node.get("d").get(1).get("e").getInt());
    }

    @Test
    void testParseArrayWithDeeplyNestedMixedTypes() throws Exception {
        String json = "[1, {\"a\": [2, {\"b\": [3, {\"c\": 4}]}]}]";
        ONode node = new JsonReader(new StringReader(json)).read();
        assertTrue(node.isArray());
        assertEquals(2, node.size());
        assertEquals(1, node.get(0).getInt());
        assertEquals(2, node.get(1).get("a").get(0).getInt());
        assertEquals(3, node.get(1).get("a").get(1).get("b").get(0).getInt());
        assertEquals(4, node.get(1).get("a").get(1).get("b").get(1).get("c").getInt());
    }

    @Test
    void testParseObjectWithDeeplyNestedMixedTypes() throws Exception {
        String json = "{\"a\": 1, \"b\": {\"c\": [2, {\"d\": [3, {\"e\": 4}]}]}}";
        ONode node = new JsonReader(new StringReader(json)).read();
        assertTrue(node.isObject());
        assertEquals(2, node.size());
        assertEquals(1, node.get("a").getInt());
        assertEquals(2, node.get("b").get("c").get(0).getInt());
        assertEquals(3, node.get("b").get("c").get(1).get("d").get(0).getInt());
        assertEquals(4, node.get("b").get("c").get(1).get("d").get(1).get("e").getInt());
    }
}