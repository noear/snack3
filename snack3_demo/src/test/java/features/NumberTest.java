package features;

import _models.NumberModel;
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
    public void test1() {
        String json = "{num:50123.12E25}";
        ONode node = ONode.load(json);

        System.out.println(node.toJson());
        assert 50123.12E25 == node.get("num").getDouble();
    }

    @Test
    public void test2() {
        String json = "{num:5344.34234e3}";
        ONode node = ONode.load(json);

        System.out.println(node.toJson());
        assert 5344.34234e3 == node.get("num").getDouble();
    }

    @Test
    public void test3() {
        String json = "{num:1.0485E+10}";
        ONode node = ONode.load(json);

        System.out.println(node.toJson());
        assert 1.0485E+10 == node.get("num").getDouble();
    }

    @Test
    public void test4() {
        String json = "{num:1.0485E-10}";
        ONode node = ONode.load(json);

        System.out.println(node.toJson());
        assert 1.0485E-10 == node.get("num").getDouble();
    }

    @Test
    public void test5() {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("num", 1.0485E-10);
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

    @Test
    public void test6() {
        NumberModel mod = new NumberModel();

        mod.setNum01(true);
        mod.setNum02(Byte.parseByte("12"));
        mod.setNum11(Short.parseShort("125"));
        mod.setNum12(1);
        mod.setNum13(1L);
        mod.setNum21(new BigInteger("12345678911234567891123456789112345678911244444444444444"));
        mod.setNum22(new BigDecimal("123456789112345678911234567891123456789112.1234567891"));

        String json = ONode.stringify(mod);
        System.out.println(json);

        String json2 = ONode.serialize(mod);
        System.out.println(json2);


        NumberModel obj1 = ONode.deserialize(json, NumberModel.class);
        NumberModel obj2 = ONode.deserialize(json2, NumberModel.class);

        System.out.println(obj1);
        System.out.println(obj2);

        assert obj1.isNum01() == obj2.isNum01();
        assert obj1.getNum02() == obj2.getNum02();
        assert obj1.getNum11() == obj2.getNum11();
        assert obj1.getNum12() == obj2.getNum12();
        assert obj1.getNum13() == obj2.getNum13();
        assert obj1.getNum14() == obj2.getNum14();
        assert obj1.getNum21().compareTo(obj1.getNum21()) == 0;
        assert obj1.getNum22().compareTo(obj1.getNum22()) == 0;
    }
}
