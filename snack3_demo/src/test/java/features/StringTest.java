package features;

import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;

import java.util.Date;

/**
 * @author noear 2022/1/8 created
 */
public class StringTest {
    @Test
    public void test1() {
        ONode attr = new ONode();
        attr.set("a", 1);
        attr.set("b", "b");
        attr.set("c", true);
        attr.set("d", new Date());

        ONode oNode = new ONode();

        oNode.set("code", 1);
        oNode.set("name", "world");
        oNode.set("attr", attr.toJson());

        String json = oNode.toJson();
        System.out.println(json);

        String json2 = ONode.loadStr(json).toJson();
        System.out.println(json2);

        assert json.equals(json2);

        System.out.println(ONode.loadStr(json, Feature.StringJsonToNode).toJson());
    }
}