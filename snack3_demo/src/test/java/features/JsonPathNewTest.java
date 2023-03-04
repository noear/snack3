package features;

import org.junit.Test;
import org.noear.snack.ONode;

/**
 * @author noear 2023/3/4 created
 */
public class JsonPathNewTest {
    @Test
    public  void test1(){
        ONode oNode = new ONode();
        oNode.selectOrNew("$.orders[0].price").val(500);
        System.out.println(oNode.toJson());

        assert "{\"orders\":[{\"price\":500}]}".equals(oNode.toJson());
    }
}
