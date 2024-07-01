package features;

import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.snack.ONodeType;

/**
 * @author noear 2023/1/19 created
 */
public class DomTest {
    @Test
    public void ary(){
        ONode oNode = new ONode();
        oNode.getOrNew(5, ONodeType.Object);

        String json = oNode.toJson();

        System.out.println(json);

        assert ONode.load(json).get(1).isObject();
    }

    @Test
    public void obj(){
        ONode oNode = new ONode();
        oNode.getOrNew("n1", ONodeType.Object);

        String json = oNode.toJson();

        System.out.println(json);

        assert ONode.load(json).get("n1").isObject();
    }
}
