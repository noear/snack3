package features.query;

import org.noear.snack.ONode;
import org.junit.jupiter.api.Test;
import org.noear.snack.query.JsonPath;

import static org.junit.jupiter.api.Assertions.*;

public class JsonPathCreateTest {

    @Test
    public void testCreateSimplePath() {
        ONode root = new ONode();
        JsonPath.create(root, "$.name");
        assertTrue(root.hasKey("name"));
    }

    @Test
    public void testCreateNestedPath() {
        ONode root = new ONode();
        JsonPath.create(root, "$.user.name");
        assertTrue(root.get("user").hasKey("name"));
    }

    @Test
    public void testCreateArrayPath() {
        ONode root = new ONode();
        JsonPath.create(root, "$.users[0]");
        assertTrue(root.get("users").isArray());
        assertEquals(1, root.get("users").size());
    }

    @Test
    public void testCreateNestedArrayPath() {
        ONode root = new ONode();
        JsonPath.create(root, "$.users[0].name");
        assertTrue(root.get("users").get(0).hasKey("name"));
    }

    @Test
    public void testCreateMultipleNestedPaths() {
        ONode root = new ONode();
        JsonPath.create(root, "$.user.name");
        JsonPath.create(root, "$.user.age");
        assertTrue(root.get("user").hasKey("name"));
        assertTrue(root.get("user").hasKey("age"));
    }

    @Test
    public void testCreatePathWithExistingNode() {
        ONode root = new ONode();
        root.set("user", new ONode().set("name", "John"));
        JsonPath.create(root, "$.user.age");
        assertTrue(root.get("user").hasKey("age"));
    }

    @Test
    public void testCreatePathWithExistingArray() {
        ONode root = new ONode();
        root.set("users", new ONode().add(new ONode()));
        JsonPath.create(root, "$.users[0].name");
        assertTrue(root.get("users").get(0).hasKey("name"));
    }

    @Test
    public void testCreatePathWithNegativeIndex() {
        ONode root = new ONode();
        root.set("users", new ONode().add(new ONode()));
        JsonPath.create(root, "$.users[-1].name");
        assertTrue(root.get("users").get(0).hasKey("name"));
    }

    @Test
    public void testCreatePathWithWildcard() {
        ONode root = new ONode();
        root.set("users", new ONode().add(new ONode()));
        JsonPath.create(root, "$.users[*].name");
        assertTrue(root.get("users").get(0).hasKey("name"));
    }

    @Test
    public void testCreatePathWithRecursiveSearch() {
        ONode root = new ONode();
        root.set("user", new ONode().set("name", "John"));
        JsonPath.create(root, "$..name");
        assertTrue(root.get("user").hasKey("name"));
    }
}