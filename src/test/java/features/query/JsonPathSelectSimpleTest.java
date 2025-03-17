package features.query;

import org.noear.snack.ONode;
import org.noear.snack.exception.PathResolutionException;
import org.noear.snack.query.JsonPath;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonPathSelectSimpleTest {

    private static final String JSON = "{\n" +
            "  \"store\": {\n" +
            "    \"book\": [\n" +
            "      {\n" +
            "        \"category\": \"reference\",\n" +
            "        \"author\": \"Nigel Rees\",\n" +
            "        \"title\": \"Sayings of the Century\",\n" +
            "        \"price\": 8.95\n" +
            "      },\n" +
            "      {\n" +
            "        \"category\": \"fiction\",\n" +
            "        \"author\": \"Evelyn Waugh\",\n" +
            "        \"title\": \"Sword of Honour\",\n" +
            "        \"price\": 12.99\n" +
            "      },\n" +
            "      {\n" +
            "        \"category\": \"fiction\",\n" +
            "        \"author\": \"Herman Melville\",\n" +
            "        \"title\": \"Moby Dick\",\n" +
            "        \"price\": 8.99\n" +
            "      },\n" +
            "      {\n" +
            "        \"category\": \"fiction\",\n" +
            "        \"author\": \"J. R. R. Tolkien\",\n" +
            "        \"title\": \"The Lord of the Rings\",\n" +
            "        \"price\": 22.99\n" +
            "      }\n" +
            "    ],\n" +
            "    \"bicycle\": {\n" +
            "      \"color\": \"red\",\n" +
            "      \"price\": 19.95\n" +
            "    }\n" +
            "  },\n" +
            "  \"expensive\": 10\n" +
            "}";

    @Test
    public void testRootNode() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$");
        assertNotNull(result);
        assertTrue(result.isObject());
    }

    @Test
    public void testStoreNode() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store");
        assertNotNull(result);
        assertTrue(result.isObject());
    }

    @Test
    public void testBooksNode() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store.book");
        assertNotNull(result);
        assertTrue(result.isArray());
    }

    @Test
    public void testFirstBook() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store.book[0]");
        assertNotNull(result);
        assertEquals("Nigel Rees", result.get("author").getString());
    }

    @Test
    public void testSecondBook() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store.book[1]");
        assertNotNull(result);
        assertEquals("Evelyn Waugh", result.get("author").getString());
    }

    @Test
    public void testThirdBook() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store.book[2]");
        assertNotNull(result);
        assertEquals("Herman Melville", result.get("author").getString());
    }

    @Test
    public void testFourthBook() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store.book[3]");
        assertNotNull(result);
        assertEquals("J. R. R. Tolkien", result.get("author").getString());
    }

    @Test
    public void testBicycleNode() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store.bicycle");
        assertNotNull(result);
        assertTrue(result.isObject());
    }

    @Test
    public void testBicycleColor() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store.bicycle.color");
        assertNotNull(result);
        assertEquals("red", result.getString());
    }

    @Test
    public void testBicyclePrice() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store.bicycle.price");
        assertNotNull(result);
        assertEquals(19.95, result.getDouble());
    }

    @Test
    public void testExpensiveNode() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.expensive");
        assertNotNull(result);
        assertEquals(10, result.getInt());
    }

    @Test
    public void testFirstBookCategory() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store.book[0].category");
        assertNotNull(result);
        assertEquals("reference", result.getString());
    }

    @Test
    public void testFirstBookTitle() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store.book[0].title");
        assertNotNull(result);
        assertEquals("Sayings of the Century", result.getString());
    }

    @Test
    public void testFirstBookPrice() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store.book[0].price");
        assertNotNull(result);
        assertEquals(8.95, result.getDouble());
    }

    @Test
    public void testSecondBookCategory() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store.book[1].category");
        assertNotNull(result);
        assertEquals("fiction", result.getString());
    }

    @Test
    public void testSecondBookTitle() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store.book[1].title");
        assertNotNull(result);
        assertEquals("Sword of Honour", result.getString());
    }

    @Test
    public void testSecondBookPrice() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store.book[1].price");
        assertNotNull(result);
        assertEquals(12.99, result.getDouble());
    }

    @Test
    public void testThirdBookCategory() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store.book[2].category");
        assertNotNull(result);
        assertEquals("fiction", result.getString());
    }

    @Test
    public void testThirdBookTitle() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store.book[2].title");
        assertNotNull(result);
        assertEquals("Moby Dick", result.getString());
    }

    @Test
    public void testThirdBookPrice() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store.book[2].price");
        assertNotNull(result);
        assertEquals(8.99, result.getDouble());
    }

    @Test
    public void testFourthBookCategory() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store.book[3].category");
        assertNotNull(result);
        assertEquals("fiction", result.getString());
    }

    @Test
    public void testFourthBookTitle() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store.book[3].title");
        assertNotNull(result);
        assertEquals("The Lord of the Rings", result.getString());
    }

    @Test
    public void testFourthBookPrice() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store.book[3].price");
        assertNotNull(result);
        assertEquals(22.99, result.getDouble());
    }

    @Test
    public void testNegativeIndex() {
        ONode root = ONode.loadJson(JSON);
        ONode result = JsonPath.select(root, "$.store.book[-1]");
        assertNotNull(result);
        assertEquals("J. R. R. Tolkien", result.get("author").getString());
    }

    @Test
    public void testNegativeIndexOutOfBounds() {
        ONode root = ONode.loadJson(JSON);
        assertThrows(PathResolutionException.class, () -> {
            JsonPath.select(root, "$.store.book[-10]");
        });
    }

    @Test
    public void testPositiveIndexOutOfBounds() {
        ONode root = ONode.loadJson(JSON);
        assertThrows(PathResolutionException.class, () -> {
            JsonPath.select(root, "$.store.book[10]");
        });
    }

    @Test
    public void testNonExistentKey() {
        ONode root = ONode.loadJson(JSON);
        assertTrue(JsonPath.select(root, "$.store.nonExistentKey").isNullOrEmpty());
    }
}