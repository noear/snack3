package features.reader;

import org.junit.jupiter.api.Test;
import org.noear.snack.core.JsonReader;
import org.noear.snack.ONode;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class JsonReaderSimpleTest {

    @Test
    void testParseEmptyObject() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("{}"));
        ONode node = parser.read();
        assertTrue(node.isObject());
        assertEquals(0, node.size());
    }

    @Test
    void testParseEmptyArray() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("[]"));
        ONode node = parser.read();
        assertTrue(node.isArray());
        assertEquals(0, node.size());
    }

    @Test
    void testParseString() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("\"Hello, World!\""));
        ONode node = parser.read();
        assertEquals("Hello, World!", node.getString());
    }

    @Test
    void testParseInteger() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("42"));
        ONode node = parser.read();
        assertEquals(42, node.getInt());
    }

    @Test
    void testParseDouble() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("3.14"));
        ONode node = parser.read();
        assertEquals(3.14, node.getDouble());
    }

    @Test
    void testParseTrue() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("true"));
        ONode node = parser.read();
        assertTrue(node.getBoolean());
    }

    @Test
    void testParseFalse() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("false"));
        ONode node = parser.read();
        assertFalse(node.getBoolean());
    }

    @Test
    void testParseNull() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("null"));
        ONode node = parser.read();
        assertNull(node.getValue());
    }

    @Test
    void testParseSimpleObject() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("{\"key\": \"value\"}"));
        ONode node = parser.read();
        assertEquals("value", node.get("key").getString());
    }

    @Test
    void testParseSimpleArray() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("[1, 2, 3]"));
        ONode node = parser.read();
        assertEquals(3, node.size());
        assertEquals(1, node.get(0).getInt());
        assertEquals(2, node.get(1).getInt());
        assertEquals(3, node.get(2).getInt());
    }

    @Test
    void testParseNegativeNumber() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("-42"));
        ONode node = parser.read();
        assertEquals(-42, node.getInt());
    }

    @Test
    void testParseScientificNotation() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("1.23e+5"));
        ONode node = parser.read();
        assertEquals(123000.0, node.getDouble());
    }

    @Test
    void testParseUnicodeString() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("\"\\u0041\\u0042\\u0043\""));
        ONode node = parser.read();
        assertEquals("ABC", node.getString());
    }

    @Test
    void testParseEscapedCharacters() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("\"\\\"\\\\\\/\\b\\f\\n\\r\\t\""));
        ONode node = parser.read();
        assertEquals("\"\\/\b\f\n\r\t", node.getString());
    }

    @Test
    void testParseMixedArray() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("[1, \"two\", true]"));
        ONode node = parser.read();
        assertEquals(3, node.size());
        assertEquals(1, node.get(0).getInt());
        assertEquals("two", node.get(1).getString());
        assertTrue(node.get(2).getBoolean());
    }

    @Test
    void testParseNestedObject() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("{\"outer\": {\"inner\": 42}}"));
        ONode node = parser.read();
        assertEquals(42, node.get("outer").get("inner").getInt());
    }

    @Test
    void testParseNestedArray() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("[[1, 2], [3, 4]]"));
        ONode node = parser.read();
        assertEquals(2, node.size());
        assertEquals(1, node.get(0).get(0).getInt());
        assertEquals(2, node.get(0).get(1).getInt());
        assertEquals(3, node.get(1).get(0).getInt());
        assertEquals(4, node.get(1).get(1).getInt());
    }

    @Test
    void testParseLargeInteger() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("1234567890123456789"));
        ONode node = parser.read();
        assertEquals(1234567890123456789L, node.getLong());
    }

    @Test
    void testParseLargeDouble() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("1.2345678901234567e+308"));
        ONode node = parser.read();
        assertEquals(1.2345678901234567e+308, node.getDouble());
    }

    @Test
    void testParseEmptyString() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("\"\""));
        ONode node = parser.read();
        assertEquals("", node.getString());
    }

    @Test
    void testParseWhitespace() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("  {  \"key\"  :  \"value\"  }  "));
        ONode node = parser.read();
        assertEquals("value", node.get("key").getString());
    }

    @Test
    void testParseMultipleWhitespace() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("  \n  \t  {  \r  \"key\"  :  \"value\"  }  "));
        ONode node = parser.read();
        assertEquals("value", node.get("key").getString());
    }

    @Test
    void testParseSingleQuoteString() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("\"It's a test\""));
        ONode node = parser.read();
        assertEquals("It's a test", node.getString());
    }

    @Test
    void testParseSpecialCharacters() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("\"!@#$%^&*()_+-={}[]|:;'<>,.?/\""));
        ONode node = parser.read();
        assertEquals("!@#$%^&*()_+-={}[]|:;'<>,.?/", node.getString());
    }

    @Test
    void testParseZero() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("0"));
        ONode node = parser.read();
        assertEquals(0, node.getInt());
    }

    @Test
    void testParseNegativeZero() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("-0"));
        ONode node = parser.read();
        assertEquals(0, node.getInt());
    }

    @Test
    void testParseLargeExponent() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("1e+308"));
        ONode node = parser.read();
        assertEquals(1e+308, node.getDouble());
    }

    @Test
    void testParseSmallExponent() throws Exception {
        JsonReader parser = new JsonReader(new StringReader("1e-308"));
        ONode node = parser.read();
        assertEquals(1e-308, node.getDouble());
    }
}