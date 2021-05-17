package features;

import org.junit.Test;
import org.noear.snack.ONode;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.Map;

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

    @Test
    public void test5(){
        Map<String,Object> map = new LinkedHashMap<>();

        map.put("num",1.0485E-10);
        map.put("num11", new BigDecimal("12.1234567891").toPlainString());
        map.put("num12", new BigDecimal("123456789112345678911234567891123456789112.1234567891"));
        map.put("num21", new BigInteger("12"));
        map.put("num22", new BigInteger("123456789112345678911234567891123456789112"));

        ONode node = ONode.load(map);
        String json = node.toJson();

        System.out.println(json);
        assert 1.0485E-10 == node.get("num").getDouble();
        assert new BigInteger("123456789112345678911234567891123456789112").compareTo((BigInteger) node.get("num22").val().getRaw()) == 0;

        ONode node2 = ONode.loadStr(json);
        assert json.equals(node2.toJson());
    }
}
