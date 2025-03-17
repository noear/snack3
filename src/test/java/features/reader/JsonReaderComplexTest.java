package features.reader;

import org.junit.jupiter.api.Test;
import org.noear.snack.core.Feature;
import org.noear.snack.core.JsonReader;
import org.noear.snack.core.Options;
import org.noear.snack.exception.ParseException;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class JsonReaderComplexTest {

    // ========================= 复杂测试用例（30 个） =========================

    @Test
    void testParseInvalidJsonMissingClosingBrace() {
        String json = "{\"name\": \"Alice\"";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonMissingClosingBracket() {
        String json = "[1, 2, 3";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonExtraComma() {
        String json = "{\"name\": \"Alice\",}";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonExtraCommaInArray() {
        String json = "[1, 2, 3,]";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonMissingKey() {
        String json = "{: \"Alice\"}";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonMissingValue() {
        String json = "{\"name\":}";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonInvalidNumber() {
        String json = "123abc";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonInvalidBoolean() {
        String json = "tru";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonInvalidNull() {
        String json = "nul";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonInvalidString() {
        String json = "\"Hello";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonInvalidUnicode() {
        String json = "\"\\u123\"";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonInvalidEscape() {
        String json = "\"\\x\"";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonInvalidObject() {
        String json = "{name: \"Alice\"}";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json), Options.builder().disable(Feature.Input_AllowUnquotedKeys).build()).read());
    }

    @Test
    void testParseInvalidJsonInvalidArray() {
        String json = "[1, 2, 3,}";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonEmptyKey() {
        String json = "{\"\": \"Alice\"}";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonInvalidNestedObject() {
        String json = "{\"person\": {\"name\": \"Alice\", \"age\":}}";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonInvalidNestedArray() {
        String json = "{\"scores\": [1, 2,]}";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonInvalidScientificNotation() {
        String json = "1.23e";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonInvalidNegativeScientificNotation() {
        String json = "-1.23e-";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonInvalidFloat() {
        String json = "3.14.15";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonInvalidNegativeFloat() {
        String json = "-3.14.15";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonInvalidBooleanInObject() {
        String json = "{\"active\": tru}";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonInvalidNullInObject() {
        String json = "{\"value\": nul}";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonInvalidMixedTypesInObject() {
        String json = "{\"name\": \"Alice\", \"age\": twenty-eight}";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonInvalidBooleanInArray() {
        String json = "[true, fals, true]";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonInvalidNullInArray() {
        String json = "[null, nul, null]";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonInvalidMixedTypesInArray() {
        String json = "[1, \"two\", three]";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonInvalidDeeplyNestedObject() {
        String json = "{\"a\": {\"b\": {\"c\": {\"d\": {\"e\": {\"f\": {\"g\": 42}}}}}}";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }

    @Test
    void testParseInvalidJsonInvalidDeeplyNestedArray() {
        String json = "[[[[[[[42]]]]]]";
        assertThrows(ParseException.class, () -> new JsonReader(new StringReader(json)).read());
    }
}
