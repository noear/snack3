package features.reader;

import org.junit.jupiter.api.Test;
import org.noear.snack.core.JsonReader;
import org.noear.snack.ONode;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class JsonReaderMediumTest {

    @Test
    void testParseObjectWithMultipleKeys() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("{\"a\": 1, \"b\": 2, \"c\": 3}"));
        ONode node = parser.read();
        assertEquals(3, node.size());
        assertEquals(1, node.get("a").getInt());
        assertEquals(2, node.get("b").getInt());
        assertEquals(3, node.get("c").getInt());
    }

    @Test
    void testParseArrayWithMixedTypes() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("[1, \"two\", true, null]"));
        ONode node = parser.read();
        assertEquals(4, node.size());
        assertEquals(1, node.get(0).getInt());
        assertEquals("two", node.get(1).getString());
        assertTrue(node.get(2).getBoolean());
        assertNull(node.get(3).getValue());
    }

    @Test
    void testParseDeeplyNestedObject() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("{\"a\": {\"b\": {\"c\": 42}}}"));
        ONode node = parser.read();
        assertEquals(42, node.get("a").get("b").get("c").getInt());
    }

    @Test
    void testParseDeeplyNestedArray() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("[[[1, 2], [3, 4]], [[5, 6], [7, 8]]]"));
        ONode node = parser.read();
        assertEquals(2, node.size());
        assertEquals(1, node.get(0).get(0).get(0).getInt());
        assertEquals(2, node.get(0).get(0).get(1).getInt());
        assertEquals(3, node.get(0).get(1).get(0).getInt());
        assertEquals(4, node.get(0).get(1).get(1).getInt());
        assertEquals(5, node.get(1).get(0).get(0).getInt());
        assertEquals(6, node.get(1).get(0).get(1).getInt());
        assertEquals(7, node.get(1).get(1).get(0).getInt());
        assertEquals(8, node.get(1).get(1).get(1).getInt());
    }

    @Test
    void testParseObjectWithArray() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("{\"a\": [1, 2, 3]}"));
        ONode node = parser.read();
        assertEquals(3, node.get("a").size());
        assertEquals(1, node.get("a").get(0).getInt());
        assertEquals(2, node.get("a").get(1).getInt());
        assertEquals(3, node.get("a").get(2).getInt());
    }

    @Test
    void testParseArrayWithObject() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("[{\"a\": 1}, {\"b\": 2}]"));
        ONode node = parser.read();
        assertEquals(2, node.size());
        assertEquals(1, node.get(0).get("a").getInt());
        assertEquals(2, node.get(1).get("b").getInt());
    }

    @Test
    void testParseComplexObject() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("{\"a\": 1, \"b\": [2, 3], \"c\": {\"d\": 4}}"));
        ONode node = parser.read();
        assertEquals(3, node.size());
        assertEquals(1, node.get("a").getInt());
        assertEquals(2, node.get("b").get(0).getInt());
        assertEquals(3, node.get("b").get(1).getInt());
        assertEquals(4, node.get("c").get("d").getInt());
    }

    @Test
    void testParseComplexArray() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("[1, [2, 3], {\"a\": 4}]"));
        ONode node = parser.read();
        assertEquals(3, node.size());
        assertEquals(1, node.get(0).getInt());
        assertEquals(2, node.get(1).get(0).getInt());
        assertEquals(3, node.get(1).get(1).getInt());
        assertEquals(4, node.get(2).get("a").getInt());
    }

    @Test
    void testParseObjectWithEscapedKeys() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("{\"\\\"key\\\"\": \"value\"}"));
        ONode node = parser.read();
        assertEquals("value", node.get("\"key\"").getString());
    }

    @Test
    void testParseArrayWithEscapedValues() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("[\"\\\"value\\\"\"]"));
        ONode node = parser.read();
        assertEquals("\"value\"", node.get(0).getString());
    }

    @Test
    void testParseObjectWithUnicodeKeys() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("{\"\\u0041\\u0042\\u0043\": 123}"));
        ONode node = parser.read();
        assertEquals(123, node.get("ABC").getInt());
    }

    @Test
    void testParseArrayWithUnicodeValues() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("[\"\\u0041\\u0042\\u0043\"]"));
        ONode node = parser.read();
        assertEquals("ABC", node.get(0).getString());
    }

    @Test
    void testParseObjectWithMixedTypes() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("{\"a\": 1, \"b\": \"two\", \"c\": true}"));
        ONode node = parser.read();
        assertEquals(3, node.size());
        assertEquals(1, node.get("a").getInt());
        assertEquals("two", node.get("b").getString());
        assertTrue(node.get("c").getBoolean());
    }

    @Test
    void testParseArrayWithNestedArrays() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("[[1, 2], [3, 4], [5, 6]]"));
        ONode node = parser.read();
        assertEquals(3, node.size());
        assertEquals(1, node.get(0).get(0).getInt());
        assertEquals(2, node.get(0).get(1).getInt());
        assertEquals(3, node.get(1).get(0).getInt());
        assertEquals(4, node.get(1).get(1).getInt());
        assertEquals(5, node.get(2).get(0).getInt());
        assertEquals(6, node.get(2).get(1).getInt());
    }

    @Test
    void testParseObjectWithNestedObjects() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("{\"a\": {\"b\": 1}, \"c\": {\"d\": 2}}"));
        ONode node = parser.read();
        assertEquals(2, node.size());
        assertEquals(1, node.get("a").get("b").getInt());
        assertEquals(2, node.get("c").get("d").getInt());
    }

    @Test
    void testParseArrayWithNestedObjects() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("[{\"a\": 1}, {\"b\": 2}, {\"c\": 3}]"));
        ONode node = parser.read();
        assertEquals(3, node.size());
        assertEquals(1, node.get(0).get("a").getInt());
        assertEquals(2, node.get(1).get("b").getInt());
        assertEquals(3, node.get(2).get("c").getInt());
    }

    @Test
    void testParseObjectWithNestedArrays() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("{\"a\": [1, 2], \"b\": [3, 4]}"));
        ONode node = parser.read();
        assertEquals(2, node.size());
        assertEquals(1, node.get("a").get(0).getInt());
        assertEquals(2, node.get("a").get(1).getInt());
        assertEquals(3, node.get("b").get(0).getInt());
        assertEquals(4, node.get("b").get(1).getInt());
    }

    @Test
    void testParseArrayWithMixedNestedTypes() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("[1, {\"a\": 2}, [3, 4]]"));
        ONode node = parser.read();
        assertEquals(3, node.size());
        assertEquals(1, node.get(0).getInt());
        assertEquals(2, node.get(1).get("a").getInt());
        assertEquals(3, node.get(2).get(0).getInt());
        assertEquals(4, node.get(2).get(1).getInt());
    }

    @Test
    void testParseObjectWithMixedNestedTypes() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("{\"a\": 1, \"b\": [2, 3], \"c\": {\"d\": 4}}"));
        ONode node = parser.read();
        assertEquals(3, node.size());
        assertEquals(1, node.get("a").getInt());
        assertEquals(2, node.get("b").get(0).getInt());
        assertEquals(3, node.get("b").get(1).getInt());
        assertEquals(4, node.get("c").get("d").getInt());
    }

}