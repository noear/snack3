package features.snack4.json.reader;

import org.junit.jupiter.api.Test;
import org.noear.snack4.ONode;
import org.noear.snack4.Feature;
import org.noear.snack4.json.JsonReader;
import org.noear.snack4.Options;
import org.noear.snack4.json.JsonParseException;

import static org.junit.jupiter.api.Assertions.*;

class JsonReaderComplexTest {

    // ========================= 复杂测试用例（30 个） =========================

    @Test
    void testParseInvalidJsonMissingClosingBrace() {
        String json = "{\"name\": \"Alice\"";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonMissingClosingBracket() {
        String json = "[1, 2, 3";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonExtraComma() {
        String json = "{\"name\": \"Alice\",}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonExtraCommaInArray() {
        String json = "[1, 2, 3,]";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonMissingKey() {
        String json = "{: \"Alice\"}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonMissingValue() {
        String json = "{\"name\":}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidNumber() {
        String json = "123abc";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidBoolean() {
        String json = "tru";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidNull() {
        String json = "nul";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidString() {
        String json = "\"Hello";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidUnicode() {
        String json = "\"\\u123\"";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidEscape() {
        String json = "\"\\x\"";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidObject() {
        String json = "{name: \"Alice\"}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json, Options.of(Feature.Read_DisableUnquotedKeys)));
    }

    @Test
    void testParseInvalidJsonInvalidArray() {
        String json = "[1, 2, 3,}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonEmptyKey() {
        String json = "{\"\": \"Alice\"}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidNestedObject() {
        String json = "{\"person\": {\"name\": \"Alice\", \"age\":}}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidNestedArray() {
        String json = "{\"scores\": [1, 2,]}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidScientificNotation() {
        String json = "1.23e";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidNegativeScientificNotation() {
        String json = "-1.23e-";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidFloat() {
        String json = "3.14.15";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidNegativeFloat() {
        String json = "-3.14.15";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidBooleanInObject() {
        String json = "{\"active\": tru}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidNullInObject() {
        String json = "{\"value\": nul}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidMixedTypesInObject() {
        String json = "{\"name\": \"Alice\", \"age\": twenty-eight}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidBooleanInArray() {
        String json = "[true, fals, true]";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidNullInArray() {
        String json = "[null, nul, null]";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidMixedTypesInArray() {
        String json = "[1, \"two\", three]";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidDeeplyNestedObject() {
        String json = "{\"a\": {\"b\": {\"c\": {\"d\": {\"e\": {\"f\": {\"g\": 42}}}}}}";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseInvalidJsonInvalidDeeplyNestedArray() {
        String json = "[[[[[[[42]]]]]]";
        assertThrows(JsonParseException.class, () -> JsonReader.read(json));
    }

    @Test
    void testParseColonNesting() {
        String json = "{\"SocketChannel\":\"SOCKET::test1=12\",\"HttpChannel\":\"POST::test1=12\"}";
        assertDoesNotThrow(() -> {
           ONode node =  JsonReader.read(json);
           System.out.println(node.toJson());
        });
    }

    // ========================= 深度限制安全测试 =========================

    @Test
    void testParseExcessivelyDeeplyNestedObjectThrows() {
        // 超过 1000 层 object 嵌套，应抛出 JsonParseException 而非 StackOverflowError
        StringBuilder sb = new StringBuilder();
        int depth = 1001;
        for (int i = 0; i < depth; i++) sb.append("{\"a\":");
        sb.append("1");
        for (int i = 0; i < depth; i++) sb.append("}");
        assertThrows(JsonParseException.class, () -> JsonReader.read(sb.toString()));
    }

    @Test
    void testParseExcessivelyDeeplyNestedArrayThrows() {
        // 超过 1000 层 array 嵌套，应抛出 JsonParseException 而非 StackOverflowError
        StringBuilder sb = new StringBuilder();
        int depth = 1001;
        for (int i = 0; i < depth; i++) sb.append("[");
        sb.append("1");
        for (int i = 0; i < depth; i++) sb.append("]");
        assertThrows(JsonParseException.class, () -> JsonReader.read(sb.toString()));
    }

    @Test
    void testParseDeepNestingWithinLimitSucceeds() {
        // 999 层嵌套在限制内，应正常解析
        StringBuilder sb = new StringBuilder();
        int depth = 999;
        for (int i = 0; i < depth; i++) sb.append("[");
        sb.append("1");
        for (int i = 0; i < depth; i++) sb.append("]");
        assertDoesNotThrow(() -> JsonReader.read(sb.toString()));
    }
}