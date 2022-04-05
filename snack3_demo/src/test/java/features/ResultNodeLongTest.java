package features;

import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Options;
import org.noear.solon.core.handle.Result;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2022/3/31 created
 */
public class ResultNodeLongTest {
    @Test
    public void test() {
        ONode oNode = new ONode();
        oNode.set("num", 12L);
        Result result = Result.succeed(oNode);

        Options options = new Options();
        options.addEncoder(Long.class, (v, d) -> d.val().setString(String.valueOf(v)));

        ONode oNode2 = ONode.loadObj(result, options);
        String json = oNode2.toJson();
        System.out.println(json);
        assert json.contains("\"12\"") == false;
    }

    @Test
    public void test2() {
        Map<String,Object> oNode = new HashMap<>();
        oNode.put("num", 12L);
        Result result = Result.succeed(oNode);

        Options options = new Options();
        options.addEncoder(Long.class, (v, d) -> d.val().setString(String.valueOf(v)));

        ONode oNode2 = ONode.loadObj(result, options);
        String json = oNode2.toJson();
        System.out.println(json);
        assert json.contains("\"12\"");
    }
}
