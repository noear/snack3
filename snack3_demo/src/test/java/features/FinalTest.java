package features;

import _model3.BSProps;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;

/**
 * @author noear 2022/5/22 created
 */
public class FinalTest {
    @Test
    public void test() throws Exception{
//        String json = "{'bean-searcher':{'sql':{'dialect':'Oracle'}}}";
        String json = "{'sql':{'dialect':'Oracle'}}";
        BSProps bsProps = ONode.loadStr(json, Feature.UseSetter).toObject(BSProps.class);

        assert "Oracle".equals(bsProps.getSql().getDialect());
    }
}
