package features;

import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Feature;

/**
 * @author noear 2022/5/13 created
 */
public class _test4 {
    @Test
    public void testONodeToJson() {
        String jsonSomeFieldValueContainBackslash = "{\"abc\":\"\\abc\"}";
        ONode oNode = ONode.loadStr(jsonSomeFieldValueContainBackslash);
        String toJson = oNode.options(opt -> opt.remove(Feature.TransferCompatible)).toJson();

        System.out.println(jsonSomeFieldValueContainBackslash);
        System.out.println(toJson);

        assert jsonSomeFieldValueContainBackslash.equals(toJson);
    }
}
