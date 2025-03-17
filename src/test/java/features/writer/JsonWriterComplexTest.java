package features.writer;

import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.BeanCodec;
import org.noear.snack.core.Feature;
import org.noear.snack.core.JsonWriter;
import org.noear.snack.core.Options;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonWriterComplexTest {

    @Test
    public void testWriteDeeplyNestedObject() throws IOException {
        ONode node = new ONode();
        ONode current = node;
        for (int i = 0; i < 10; i++) {
            ONode next = new ONode();
            current.set("nested", next);
            current = next;
        }
        current.set("value", "deep");
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"nested\":{\"nested\":{\"nested\":{\"nested\":{\"nested\":{\"nested\":{\"nested\":{\"nested\":{\"nested\":{\"nested\":{\"value\":\"deep\"}}}}}}}}}}}", writer.toString());
    }

    @Test
    public void testWriteObjectWithMixedTypes() throws IOException {
        ONode node = new ONode();
        node.set("string", "value");
        node.set("number", 123);
        node.set("boolean", true);
        node.set("null", null);
        node.set("array", Arrays.asList(1, 2, 3));
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"string\":\"value\",\"number\":123,\"boolean\":true,\"null\":null,\"array\":[1,2,3]}", writer.toString());
    }

    @Test
    public void testWriteObjectWithSpecialKeys() throws IOException {
        ONode node = new ONode();
        node.set("key.with.dots", "value1");
        node.set("key with spaces", "value2");
        node.set("key-with-dashes", "value3");
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"key.with.dots\":\"value1\",\"key with spaces\":\"value2\",\"key-with-dashes\":\"value3\"}", writer.toString());
    }

    @Test
    public void testWriteObjectWithEmptyKeys() throws IOException {
        ONode node = new ONode();
        node.set("", "empty");
        node.set(" ", "space");
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"\":\"empty\",\" \":\"space\"}", writer.toString());
    }

    @Test
    public void testWriteObjectWithUnicodeKeys() throws IOException {
        ONode node = new ONode();
        node.set("こんにちは", "hello");
        node.set("안녕하세요", "hi");
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"こんにちは\":\"hello\",\"안녕하세요\":\"hi\"}", writer.toString());
    }

    @Test
    public void testWriteObjectWithEscapedCharactersInKeys() throws IOException {
        ONode node = new ONode();
        node.set("key\"with\"quotes", "value1");
        node.set("key\\with\\backslashes", "value2");
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"key\\\"with\\\"quotes\":\"value1\",\"key\\\\with\\\\backslashes\":\"value2\"}", writer.toString());
    }

    @Test
    public void testWriteObjectWithNestedArrays() throws IOException {
        ONode node = new ONode();
        node.set("array", Arrays.asList(
                Arrays.asList(1, 2),
                Arrays.asList(3, 4),
                Arrays.asList(5, 6)
        ));
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"array\":[[1,2],[3,4],[5,6]]}", writer.toString());
    }

    @Test
    public void testWriteObjectWithNestedObjectsAndArrays() throws IOException {
        ONode node = new ONode();
        ONode nested = new ONode();
        nested.set("array", Arrays.asList(
                new ONode().set("key1", "value1"),
                new ONode().set("key2", "value2")
        ));
        node.set("nested", nested);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"nested\":{\"array\":[{\"key1\":\"value1\"},{\"key2\":\"value2\"}]}}", writer.toString());
    }

    @Test
    public void testWriteObjectWithComplexNestedStructure() throws IOException {
        ONode node = new ONode();
        ONode nested = new ONode();
        nested.set("array", Arrays.asList(
                new ONode().set("key1", "value1"),
                new ONode().set("key2", "value2")
        ));
        nested.set("object", new ONode().set("nestedKey", "nestedValue"));
        node.set("nested", nested);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"nested\":{\"array\":[{\"key1\":\"value1\"},{\"key2\":\"value2\"}],\"object\":{\"nestedKey\":\"nestedValue\"}}}", writer.toString());
    }

    @Test
    public void testWriteObjectWithLargeNumberOfFields() throws IOException {
        ONode node = new ONode();
        for (int i = 0; i < 100; i++) {
            node.set("key" + i, "value" + i);
        }
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        String expected = "{" +
                "\"key0\":\"value0\"," +
                "\"key1\":\"value1\"," +
                "\"key2\":\"value2\"," +
                "\"key3\":\"value3\"," +
                "\"key4\":\"value4\"," +
                "\"key5\":\"value5\"," +
                "\"key6\":\"value6\"," +
                "\"key7\":\"value7\"," +
                "\"key8\":\"value8\"," +
                "\"key9\":\"value9\"," +
                "\"key10\":\"value10\"," +
                "\"key11\":\"value11\"," +
                "\"key12\":\"value12\"," +
                "\"key13\":\"value13\"," +
                "\"key14\":\"value14\"," +
                "\"key15\":\"value15\"," +
                "\"key16\":\"value16\"," +
                "\"key17\":\"value17\"," +
                "\"key18\":\"value18\"," +
                "\"key19\":\"value19\"," +
                "\"key20\":\"value20\"," +
                "\"key21\":\"value21\"," +
                "\"key22\":\"value22\"," +
                "\"key23\":\"value23\"," +
                "\"key24\":\"value24\"," +
                "\"key25\":\"value25\"," +
                "\"key26\":\"value26\"," +
                "\"key27\":\"value27\"," +
                "\"key28\":\"value28\"," +
                "\"key29\":\"value29\"," +
                "\"key30\":\"value30\"," +
                "\"key31\":\"value31\"," +
                "\"key32\":\"value32\"," +
                "\"key33\":\"value33\"," +
                "\"key34\":\"value34\"," +
                "\"key35\":\"value35\"," +
                "\"key36\":\"value36\"," +
                "\"key37\":\"value37\"," +
                "\"key38\":\"value38\"," +
                "\"key39\":\"value39\"," +
                "\"key40\":\"value40\"," +
                "\"key41\":\"value41\"," +
                "\"key42\":\"value42\"," +
                "\"key43\":\"value43\"," +
                "\"key44\":\"value44\"," +
                "\"key45\":\"value45\"," +
                "\"key46\":\"value46\"," +
                "\"key47\":\"value47\"," +
                "\"key48\":\"value48\"," +
                "\"key49\":\"value49\"," +
                "\"key50\":\"value50\"," +
                "\"key51\":\"value51\"," +
                "\"key52\":\"value52\"," +
                "\"key53\":\"value53\"," +
                "\"key54\":\"value54\"," +
                "\"key55\":\"value55\"," +
                "\"key56\":\"value56\"," +
                "\"key57\":\"value57\"," +
                "\"key58\":\"value58\"," +
                "\"key59\":\"value59\"," +
                "\"key60\":\"value60\"," +
                "\"key61\":\"value61\"," +
                "\"key62\":\"value62\"," +
                "\"key63\":\"value63\"," +
                "\"key64\":\"value64\"," +
                "\"key65\":\"value65\"," +
                "\"key66\":\"value66\"," +
                "\"key67\":\"value67\"," +
                "\"key68\":\"value68\"," +
                "\"key69\":\"value69\"," +
                "\"key70\":\"value70\"," +
                "\"key71\":\"value71\"," +
                "\"key72\":\"value72\"," +
                "\"key73\":\"value73\"," +
                "\"key74\":\"value74\"," +
                "\"key75\":\"value75\"," +
                "\"key76\":\"value76\"," +
                "\"key77\":\"value77\"," +
                "\"key78\":\"value78\"," +
                "\"key79\":\"value79\"," +
                "\"key80\":\"value80\"," +
                "\"key81\":\"value81\"," +
                "\"key82\":\"value82\"," +
                "\"key83\":\"value83\"," +
                "\"key84\":\"value84\"," +
                "\"key85\":\"value85\"," +
                "\"key86\":\"value86\"," +
                "\"key87\":\"value87\"," +
                "\"key88\":\"value88\"," +
                "\"key89\":\"value89\"," +
                "\"key90\":\"value90\"," +
                "\"key91\":\"value91\"," +
                "\"key92\":\"value92\"," +
                "\"key93\":\"value93\"," +
                "\"key94\":\"value94\"," +
                "\"key95\":\"value95\"," +
                "\"key96\":\"value96\"," +
                "\"key97\":\"value97\"," +
                "\"key98\":\"value98\"," +
                "\"key99\":\"value99\"" +
                "}";
        assertEquals(expected, writer.toString());
    }

    @Test
    public void testWriteObjectWithSpecialCharactersInValues() throws IOException {
        ONode node = new ONode();
        node.set("key1", "value with spaces");
        node.set("key2", "value\nwith\nnewlines");
        node.set("key3", "value\twith\ttabs");
        node.set("key4", "value\"with\"quotes");
        node.set("key5", "value\\with\\backslashes");
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"key1\":\"value with spaces\",\"key2\":\"value\\nwith\\nnewlines\",\"key3\":\"value\\twith\\ttabs\",\"key4\":\"value\\\"with\\\"quotes\",\"key5\":\"value\\\\with\\\\backslashes\"}", writer.toString());
    }

    @Test
    public void testWriteObjectWithMixedNumericTypes() throws IOException {
        ONode node = new ONode();
        node.set("int", 123);
        node.set("long", 1234567890123456789L);
        node.set("double", 123.456);
        node.set("float", 123.456f);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"int\":123,\"long\":1234567890123456789,\"double\":123.456,\"float\":123.456}", writer.toString());
    }

    @Test
    public void testWriteObjectWithBooleanValues() throws IOException {
        ONode node = new ONode();
        node.set("true", true);
        node.set("false", false);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"true\":true,\"false\":false}", writer.toString());
    }

    @Test
    public void testWriteObjectWithNullValues() throws IOException {
        ONode node = new ONode();
        node.set("key1", null);
        node.set("key2", null);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"key1\":null,\"key2\":null}", writer.toString());
    }

    @Test
    public void testWriteObjectWithMixedNullAndNonNullValues() throws IOException {
        ONode node = new ONode();
        node.set("key1", "value1");
        node.set("key2", null);
        node.set("key3", "value3");
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"key1\":\"value1\",\"key2\":null,\"key3\":\"value3\"}", writer.toString());
    }

    @Test
    public void testWriteObjectWithArrayOfMixedTypes() throws IOException {
        ONode node = new ONode();
        node.set("array", Arrays.asList(1, "two", true, null, 3.14));
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"array\":[1,\"two\",true,null,3.14]}", writer.toString());
    }

    @Test
    public void testWriteObjectWithNestedNullValues() throws IOException {
        ONode node = new ONode();
        ONode nested = new ONode();
        nested.set("key1", null);
        nested.set("key2", null);
        node.set("nested", nested);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"nested\":{\"key1\":null,\"key2\":null}}", writer.toString());
    }

    @Test
    public void testWriteObjectWithComplexNestedNullValues() throws IOException {
        ONode node = new ONode();
        ONode nested = new ONode();
        nested.set("key1", null);
        ONode nested2 = new ONode();
        nested2.set("key2", null);
        nested.set("nested", nested2);
        node.set("nested", nested);
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"nested\":{\"key1\":null,\"nested\":{\"key2\":null}}}", writer.toString());
    }

    @Test
    public void testWriteObjectWithArrayOfNulls() throws IOException {
        ONode node = new ONode();
        node.set("array", Arrays.asList(null, null, null));
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"array\":[null,null,null]}", writer.toString());
    }

    @Test
    public void testWriteObjectWithArrayOfBooleans() throws IOException {
        ONode node = new ONode();
        node.set("array", BeanCodec.serialize(Arrays.asList(true, false, true)));
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"array\":[true,false,true]}", writer.toString());
    }

    @Test
    public void testWriteObjectWithArrayOfStrings() throws IOException {
        ONode node = new ONode();
        node.set("array", Arrays.asList("one", "two", "three"));
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"array\":[\"one\",\"two\",\"three\"]}", writer.toString());
    }

    @Test
    public void testWriteObjectWithArrayOfNumbers() throws IOException {
        ONode node = new ONode();
        node.set("array", Arrays.asList(1, 2, 3));
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"array\":[1,2,3]}", writer.toString());
    }

    @Test
    public void testWriteObjectWithArrayOfMixedNumbers() throws IOException {
        ONode node = new ONode();
        node.set("array", Arrays.asList(1, 2.5, 3.14f, 4L));
        StringWriter writer = new StringWriter();
        new JsonWriter(Options.def(), writer).write(node);
        assertEquals("{\"array\":[1,2.5,3.14,4]}", writer.toString());
    }
}