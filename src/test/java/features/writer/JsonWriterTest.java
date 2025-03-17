package features.writer;

import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.BeanCodec;
import org.noear.snack.core.Feature;
import org.noear.snack.core.JsonWriter;
import org.noear.snack.core.Options;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonWriterTest {

    @Test
    public void testWriteNull() throws IOException {
        ONode node = new ONode(null);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("null", writer.toString());
    }

    @Test
    public void testWriteBooleanTrue() throws IOException {
        ONode node = new ONode(true);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("true", writer.toString());
    }

    @Test
    public void testWriteBooleanFalse() throws IOException {
        ONode node = new ONode(false);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("false", writer.toString());
    }

    @Test
    public void testWriteNumberInteger() throws IOException {
        ONode node = new ONode(123);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("123", writer.toString());
    }

    @Test
    public void testWriteNumberDouble() throws IOException {
        ONode node = new ONode(123.45);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("123.45", writer.toString());
    }

    @Test
    public void testWriteString() throws IOException {
        ONode node = new ONode("hello");
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("\"hello\"", writer.toString());
    }

    @Test
    public void testWriteEmptyArray() throws IOException {
        ONode node = new ONode(new ArrayList<>());
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("[]", writer.toString());
    }

    @Test
    public void testWriteArrayWithNumbers() throws IOException {
        ONode node = new ONode();
        node.add(1).add(2).add(3);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("[1,2,3]", writer.toString());
    }

    @Test
    public void testWriteEmptyObject() throws IOException {
        ONode node = new ONode(new HashMap<>());
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{}", writer.toString());
    }

    @Test
    public void testWriteObjectWithFields() throws IOException {
        ONode node = new ONode();
        node.set("name", new ONode("John"));
        node.set("age", new ONode(30));
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"name\":\"John\",\"age\":30}", writer.toString());
    }

    @Test
    public void testWriteNestedObject() throws IOException {
        ONode node = new ONode();
        ONode address = new ONode();
        address.set("city", new ONode("New York"));
        node.set("address", address);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"address\":{\"city\":\"New York\"}}", writer.toString());
    }

    @Test
    public void testWriteNestedArray() throws IOException {
        ONode node = new ONode();
        ONode array = new ONode();
        array.add(1).add(2).add(3);
        node.set("numbers", array);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"numbers\":[1,2,3]}", writer.toString());
    }

    @Test
    public void testWritePrettyFormat() throws IOException {
        ONode node = new ONode();
        node.set("name", "John");
        node.set("age", 30);
        Options opts = Options.of(Feature.Output_PrettyFormat);
        StringWriter writer = new StringWriter();
        new JsonWriter(opts, writer).write(node);

        String pretty = writer.toString();
        System.out.println(pretty);

        assertEquals("{\n  \"name\": \"John\",\n  \"age\": 30\n}", pretty);
    }

    @Test
    public void testWriteSkipNullValue() throws IOException {
        ONode node = new ONode();
        node.set("name", new ONode("John"));
        node.set("age", new ONode(null));
        Options opts = Options.of(Feature.Output_SkipNullValue);
        StringWriter writer = new StringWriter();
        new JsonWriter(opts, writer).write(node);
        assertEquals("{\"name\":\"John\"}", writer.toString());
    }

    @Test
    public void testWriteUseSingleQuotes() throws IOException {
        ONode node = new ONode("hello");
        Options opts = Options.of(Feature.Output_UseSingleQuotes);
        StringWriter writer = new StringWriter();
        new JsonWriter(opts, writer).write(node);
        assertEquals("'hello'", writer.toString());
    }

    @Test
    public void testWriteUseUnderlineStyle() throws IOException {
        ONode node = new ONode();
        node.set("firstName", new ONode("John"));
        Options opts = Options.of(Feature.Output_UseUnderlineStyle);
        StringWriter writer = new StringWriter();
        new JsonWriter(opts, writer).write(node);
        assertEquals("{\"first_name\":\"John\"}", writer.toString());
    }

    @Test
    public void testWriteEscapeNonAscii() throws IOException {
        ONode node = new ONode("こんにちは");
        Options opts = Options.of(Feature.EscapeNonAscii);
        StringWriter writer = new StringWriter();
        new JsonWriter(opts, writer).write(node);
        assertEquals("\"\\u3053\\u3093\\u306b\\u3061\\u306f\"", writer.toString());
    }

    @Test
    public void testWriteBigNumberMode() throws IOException {
        ONode node = new ONode(1234567890123456789L);
        Options opts = Options.of(Feature.UseBigNumberMode);
        StringWriter writer = new StringWriter();
        new JsonWriter(opts, writer).write(node);
        assertEquals("\"1234567890123456789\"", writer.toString());
    }

    @Test
    public void testWriteComplexObject() throws IOException {
        ONode node = new ONode();
        node.set("name", new ONode("John"));
        ONode address = new ONode();
        address.set("city", new ONode("New York"));
        address.set("zip", new ONode("10001"));
        node.set("address", address);
        ONode phones = new ONode();
        phones.add("123-456-7890").add("987-654-3210");
        node.set("phones", phones);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"name\":\"John\",\"address\":{\"city\":\"New York\",\"zip\":\"10001\"},\"phones\":[\"123-456-7890\",\"987-654-3210\"]}", writer.toString());
    }

    @Test
    public void testWriteArrayOfObjects() throws IOException {
        ONode node = new ONode();
        ONode obj1 = new ONode();
        obj1.set("name", new ONode("John"));
        ONode obj2 = new ONode();
        obj2.set("name", new ONode("Jane"));
        node.add(obj1).add(obj2);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("[{\"name\":\"John\"},{\"name\":\"Jane\"}]", writer.toString());
    }

    @Test
    public void testWriteNestedArrays() throws IOException {
        ONode node = new ONode();
        ONode array1 = new ONode();
        array1.add(1).add(2);
        ONode array2 = new ONode();
        array2.add(3).add(4);
        node.add(array1).add(array2);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("[[1,2],[3,4]]", writer.toString());
    }

    @Test
    public void testWriteMixedTypes() throws IOException {
        ONode node = new ONode();
        node.set("name", "John");
        node.set("age", 30);
        node.set("isStudent", false);
        node.set("grades", BeanCodec.serialize(new int[]{90, 85, 88}));
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"name\":\"John\",\"age\":30,\"isStudent\":false,\"grades\":[90,85,88]}", writer.toString());
    }

    @Test
    public void testWriteEmptyString() throws IOException {
        ONode node = new ONode("");
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("\"\"", writer.toString());
    }

    @Test
    public void testWriteZero() throws IOException {
        ONode node = new ONode(0);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("0", writer.toString());
    }

    @Test
    public void testWriteNegativeNumber() throws IOException {
        ONode node = new ONode(-123);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("-123", writer.toString());
    }

    @Test
    public void testWriteLargeNumber() throws IOException {
        ONode node = new ONode(1234567890123456789L);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("1234567890123456789", writer.toString());
    }

    @Test
    public void testWriteSpecialCharactersInString() throws IOException {
        ONode node = new ONode("Hello, \"World\"!");
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("\"Hello, \\\"World\\\"!\"", writer.toString());
    }

    @Test
    public void testWriteEscapedCharacters() throws IOException {
        ONode node = new ONode("Line1\nLine2\tTab");
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("\"Line1\\nLine2\\tTab\"", writer.toString());
    }

    @Test
    public void testWriteUnicodeCharacters() throws IOException {
        ONode node = new ONode("こんにちは");
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("\"こんにちは\"", writer.toString());
    }

    @Test
    public void testWriteComplexNestedStructure() throws IOException {
        ONode node = new ONode();
        ONode person = new ONode();
        person.set("name", new ONode("John"));
        ONode address = new ONode();
        address.set("city", new ONode("New York"));
        address.set("zip", new ONode("10001"));
        person.set("address", address);
        ONode phones = new ONode();
        phones.add("123-456-7890").add("987-654-3210");
        person.set("phones", phones);
        node.set("person", person);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"person\":{\"name\":\"John\",\"address\":{\"city\":\"New York\",\"zip\":\"10001\"},\"phones\":[\"123-456-7890\",\"987-654-3210\"]}}", writer.toString());
    }
}