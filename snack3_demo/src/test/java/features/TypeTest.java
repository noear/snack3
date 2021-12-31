package features;

import _model3.Server;
import org.junit.Test;
import org.noear.snack.ONode;

/**
 * @author noear 2021/12/31 created
 */
public class TypeTest {
    @Test
    public void test1(){
        String json = "{\"id\":1, \"name\":\"n\"}";
        Server.One one = ONode.deserialize(json, Server.One.class);

        assert one != null;
    }
}
