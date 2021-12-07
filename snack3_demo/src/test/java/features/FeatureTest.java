package features;

import org.junit.Test;
import org.noear.snack.core.Options;

/**
 * @author noear 2021/12/7 created
 */
public class FeatureTest {
    @Test
    public void test(){
        System.out.println(Options.def().getFeatures());
    }
}
