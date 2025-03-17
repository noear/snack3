package features.query;

import org.noear.snack.ONode;
import org.junit.jupiter.api.Test;
import org.noear.snack.query.JsonPath;

import static org.junit.jupiter.api.Assertions.*;

public class JsonPathDeleteTest {

    @Test
    public void testDeleteSimplePath() {
        ONode root = new ONode().set("name", "John");
        JsonPath.delete(root, "$.name");
        assertFalse(root.hasKey("name"));
    }

    @Test
    public void testDeleteNestedPath() {
        ONode root = new ONode().set("user", new ONode().set("name", "John"));
        JsonPath.delete(root, "$.user.name");
        assertFalse(root.get("user").hasKey("name"));
    }

    @Test
    public void testDeleteArrayElement() {
        ONode root = new ONode().set("users", new ONode().add(new ONode().set("name", "John")));
        JsonPath.delete(root, "$.users[0]");
        assertEquals(0, root.get("users").size());
    }

    @Test
    public void testDeleteNestedArrayElement() {
        ONode root = new ONode().set("users", new ONode().add(new ONode().set("name", "John")));
        JsonPath.delete(root, "$.users[0].name");
        assertFalse(root.get("users").get(0).hasKey("name"));
    }

    @Test
    public void testDeleteMultipleNestedPaths() {
        ONode root = new ONode().set("user", new ONode().set("name", "John").set("age", 30));
        JsonPath.delete(root, "$.user.name");
        JsonPath.delete(root, "$.user.age");
        assertFalse(root.get("user").hasKey("name"));
        assertFalse(root.get("user").hasKey("age"));
    }

    @Test
    public void testDeletePathWithNegativeIndex() {
        ONode root = new ONode().set("users", new ONode().add(new ONode().set("name", "John")));
        JsonPath.delete(root, "$.users[-1]");
        assertEquals(0, root.get("users").size());
    }

    @Test
    public void testDeletePathWithWildcard() {
        ONode root = new ONode().set("users", new ONode().add(new ONode().set("name", "John")));
        JsonPath.delete(root, "$.users[*]");
        assertEquals(0, root.get("users").size());
    }

    @Test
    public void testDeletePathWithRecursiveSearch() {
        ONode root = new ONode().set("user", new ONode().set("name", "John"));
        JsonPath.delete(root, "$..name");
        assertFalse(root.get("user").hasKey("name"));
    }

    @Test
    public void testDeleteNonExistentPath() {
        ONode root = new ONode().set("name", "John");
        JsonPath.delete(root, "$.age");
        assertTrue(root.hasKey("name"));
    }
}