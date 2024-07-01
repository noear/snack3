package demo;

import _models.NumberModel;
import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;
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
    public void test6() {
        NumberModel mod = new NumberModel();

        mod.setNum01(true);
        mod.setNum02(Byte.parseByte("12"));
        mod.setNum11(Short.parseShort("125"));
        mod.setNum12(1);
        mod.setNum13(1L);
        mod.setNum21(new BigInteger("12345678911234567891123456789112345678911244444444444444"));
        mod.setNum22(new BigDecimal("123456789112345678911234567891123456789112.1234567891"));

        String json = JSON.toJSONString(mod);
        System.out.println(json);


//        NumberModel obj1 = ONode.deserialize(json, NumberModel.class);
//        NumberModel obj2 = ONode.deserialize(json2, NumberModel.class);
//
//        System.out.println(obj1);
//        System.out.println(obj2);
//
//        assert obj1.isNum01() == obj2.isNum01();
//        assert obj1.getNum02() == obj2.getNum02();
//        assert obj1.getNum11() == obj2.getNum11();
//        assert obj1.getNum12() == obj2.getNum12();
//        assert obj1.getNum13() == obj2.getNum13();
//        assert obj1.getNum14() == obj2.getNum14();
//        assert obj1.getNum21().compareTo(obj1.getNum21()) == 0;
//        assert obj1.getNum22().compareTo(obj1.getNum22()) == 0;
    }
}
