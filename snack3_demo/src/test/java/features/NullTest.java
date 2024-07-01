package features;

import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;

/**
 * @author noear 2022/12/15 created
 */
public class NullTest {
    @Test
    public void test1() {
        String json = "{num:null}";
        ONode node = ONode.load(json);

        System.out.println(node.toJson());
        assert node.get("num").isNull();

        Object tmp = node.options(opt->opt.add(Feature.SerializeNulls)).toObject();
        System.out.println(tmp);
    }
}
