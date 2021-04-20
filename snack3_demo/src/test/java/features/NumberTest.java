package features;

import org.junit.Test;
import org.noear.snack.ONode;

/**
 * @author noear 2021/4/21 created
 */
public class NumberTest {
    @Test
    public void test1(){
        String json = "{num:50123.12E25}";
        ONode node = ONode.load(json);

        System.out.println(node.toJson());
        assert 50123.12E25 == node.get("num").getDouble();
    }

    public void test2(){

    }

    public void test3(){

    }
}
