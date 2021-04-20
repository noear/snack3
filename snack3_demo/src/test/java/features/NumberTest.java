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

    @Test
    public void test2(){
        String json = "{num:5344.34234e3}";
        ONode node = ONode.load(json);

        System.out.println(node.toJson());
        assert 5344.34234e3 == node.get("num").getDouble();
    }

    @Test
    public void test3(){
        String json = "{num:1.0485E+10}";
        ONode node = ONode.load(json);

        System.out.println(node.toJson());
        assert 1.0485E+10 == node.get("num").getDouble();
    }

    @Test
    public void test4(){
        String json = "{num:1.0485E-10}";
        ONode node = ONode.load(json);

        System.out.println(node.toJson());
        assert 1.0485E-10 == node.get("num").getDouble();
    }
}
