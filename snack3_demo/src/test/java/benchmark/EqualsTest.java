package benchmark;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author noear 2021/5/17 created
 */
public class EqualsTest {
    @Test
    public void test1() {
        Number val1 = 1;
        Number val2 = 1;
        boolean rst = false;

        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            if (val1 instanceof BigInteger) {

            } else if (val1 instanceof BigDecimal) {

            } else if (val1 instanceof Integer) {
                rst = val1.longValue() == val2.longValue();
            }
        }

        System.out.println((System.currentTimeMillis() - start) + "ms");
    }

    @Test
    public void test2() {
        Integer val1 = 1;
        Integer val2 = 1;
        boolean rst = false;

        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            rst = val1.toString().equals(val1.toString());
        }

        System.out.println((System.currentTimeMillis() - start) + "ms");
    }
}
