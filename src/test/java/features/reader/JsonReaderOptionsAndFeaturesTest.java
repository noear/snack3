package features.reader;

import org.junit.jupiter.api.Test;
import org.noear.snack.core.Feature;
import org.noear.snack.core.JsonReader;
import org.noear.snack.core.Options;
import org.noear.snack.exception.ParseException;

import java.io.StringReader;
import static org.junit.jupiter.api.Assertions.*;

class JsonReaderOptionsAndFeaturesTest {

    @Test
    void testAllowComments() {
        String json = "{\"key\": \"value\"} // Comment";
        JsonReader reader = new JsonReader(new StringReader(json), Options.builder().enable(Feature.Input_AllowComment).build());
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testAllowComments_2() {
        String json = "// Comment\n{\"key\": \"value\"} ";
        JsonReader reader = new JsonReader(new StringReader(json), Options.builder().enable(Feature.Input_AllowComment).build());
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testAllowComments2() {
        String json = "{\"key\": \"value\"} /* Comment*/";
        JsonReader reader = new JsonReader(new StringReader(json), Options.builder().enable(Feature.Input_AllowComment).build());
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testAllowComments2_2() {
        String json = "/* Comment*/{\"key\": \"value\"}";
        JsonReader reader = new JsonReader(new StringReader(json), Options.builder().enable(Feature.Input_AllowComment).build());
        assertDoesNotThrow(() -> reader.read());
    }


    @Test
    void testAllowSingleQuotes() {
        String json = "{'key': 'value'}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertDoesNotThrow(() -> reader.read());

        //Options.of().enable(Feature.Input_AllowSingleQuotes).build()
    }

    @Test
    void testAllowUnquotedFieldNames() {
        String json = "{key: \"value\"}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertDoesNotThrow(() -> reader.read());

        //Options.of().enable(Feature.Input_AllowUnquotedKeys).build()
    }


    @Test
    void testAllowNumericLeadingZeros() {
        String json = "{\"key\": 00123}";
        JsonReader reader = new JsonReader(new StringReader(json),Options.builder().enable(Feature.Input_AllowZeroLeadingNumbers).build());
        assertDoesNotThrow(() -> reader.read());


    }

//    @Test
//    void testAllowNonNumericNumbers() {
//        String json = "{\"key\": NaN}";
//        JsonReader reader = new JsonReader(new StringReader(json));
//        assertDoesNotThrow(() -> reader.read());
//    }

    @Test
    void testAllowBackslashEscapingAnyCharacter() {
        String json = "{\"key\": \"\\a\"}";
        JsonReader reader = new JsonReader(new StringReader(json), Options.of(Feature.Input_AllowBackslashEscapingAnyCharacter));
        assertDoesNotThrow(() -> reader.read());

        //JsonReader.Options.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER //允许对任何字符进行反斜杠转义
    }


    @Test
    void testAllowEmptyKeys() {
        String json = "{\"\": \"value\"}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertThrows(ParseException.class,() -> reader.read());
    }

    @Test
    void testAllowEmptyKeys2() {
        String json = "{\"\": \"value\"}";
        JsonReader reader = new JsonReader(new StringReader(json), Options.builder().enable(Feature.Input_AllowEmptyKeys).build());
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testParseNullValue() {
        String json = "{\"key\": null}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testParseBooleanValues() {
        String json = "{\"key\": true}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testParseIntegerValues() {
        String json = "{\"key\": 123}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testParseFloatValues() {
        String json = "{\"key\": 123.45}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testParseScientificNotation() {
        String json = "{\"key\": 1.23e10}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testParseStringValues() {
        String json = "{\"key\": \"value\"}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testParseEscapedCharacters() {
        String json = "{\"key\": \"\\\"\\\\\\/\\b\\f\\n\\r\\t\"}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testParseUnicodeCharacters() {
        String json = "{\"key\": \"你好，世界\"}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testParseArrays() {
        String json = "{\"key\": [1, 2, 3]}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testParseNestedArrays() {
        String json = "{\"key\": [[1, 2], [3, 4]]}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testParseObjects() {
        String json = "{\"key\": {\"nestedKey\": \"nestedValue\"}}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testParseNestedObjects() {
        String json = "{\"key\": {\"nestedKey\": {\"deepKey\": \"deepValue\"}}}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testParseMixedStructures() {
        String json = "{\"key\": [1, {\"nestedKey\": \"nestedValue\"}]}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testParseLargeIntegers() {
        String json = "{\"key\": 12345678901234567890}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testParseLargeFloats() {
        String json = "{\"key\": 1.2345678901234567890e100}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testParseLargeJson() {
        StringBuilder json = new StringBuilder("{\"key\": \"");
        for (int i = 0; i < 10000; i++) {
            json.append("a");
        }
        json.append("\"}");
        JsonReader reader = new JsonReader(new StringReader(json.toString()));
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testParseSpecialCharacterKeys() {
        String json = "{\"key!@#\": \"value\"}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testParseWhitespaceKeys() {
        String json = "{\" \": \"value\"}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testParseWhitespaceValues() {
        String json = "{\"key\": \" value \"}";
        JsonReader reader = new JsonReader(new StringReader(json));
        assertDoesNotThrow(() -> reader.read());
    }

    @Test
    void testParseDeepNesting() {
        StringBuilder json = new StringBuilder("{\"key\": ");
        for (int i = 0; i < 100; i++) {
            json.append("{\"nestedKey\": ");
        }
        json.append("\"value\"");
        for (int i = 0; i < 100; i++) {
            json.append("}");
        }
        json.append("}");

        System.out.println(json.toString());
        JsonReader reader = new JsonReader(new StringReader(json.toString()));
        assertDoesNotThrow(() -> reader.read());
    }
}