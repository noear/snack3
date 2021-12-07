package features;

import org.junit.Test;
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
        assert oNode.options().hasFeature(Feature.StringNullAsEmpty);


        oNode.options().remove(Feature.StringNullAsEmpty);
        System.out.println(oNode.options().getFeatures());
        assert oNode.options().hasFeature(Feature.StringNullAsEmpty) == false;

        assert oNode.get("name").getString() == null;
    }
}
