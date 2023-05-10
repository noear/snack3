package features;

import features.test5.A;
import org.junit.Test;
import org.noear.snack.ONode;

/**
 * @author noear 2023/5/10 created
 */
public class _test5 {
    @Test
    public void test() {
        String poc = "{\"@type\":\"features.test5.A\"," +
                "\"b\":{\"@type\":\"features.test5.B\",\"bList\":\"str1\"}}";
        System.out.println(poc);
        A o = ONode.deserialize(poc);
        System.out.println(o.getB().getbList().size());
        assert o.getB().getbList().size() == 1;
    }
}
