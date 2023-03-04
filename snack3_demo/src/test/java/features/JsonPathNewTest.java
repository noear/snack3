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
        //{"orders":[{"price":500}]}

        assert "{\"orders\":[{\"price\":500}]}".equals(oNode.toJson());


        oNode.selectOrNew("$.orders[10].price").val(600);
        System.out.println(oNode.toJson());
        //{"orders":[{"price":500},null,null,null,null,null,null,null,null,null,{"price":600}]}

        oNode.select("$.orders").forEach(n->n.asObject());
        System.out.println(oNode.toJson());
        //{"orders":[{"price":500},{},{},{},{},{},{},{},{},{},{"price":600}]}
    }
}
