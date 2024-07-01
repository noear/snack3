package features;

import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;

/**
 * @author noear 2021/12/7 created
 */
public class FeatureTest {
    @Test
    public void test() {
        ONode oNode = new ONode();


        System.out.println(oNode.options().getFeatures());
        assert oNode.options().hasFeature(Feature.StringNullAsEmpty) == false;
        assert oNode.get("name").getString() == null;

        oNode.options().add(Feature.StringNullAsEmpty);
        System.out.println(oNode.options().getFeatures());
        assert oNode.options().hasFeature(Feature.StringNullAsEmpty);

        assert oNode.get("name").getString() != null;
    }
}
