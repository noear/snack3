package features;

import org.junit.Test;
import org.noear.snack.ONode;

/**
 * @author noear 2022/5/13 created
 */
public class _test4 {
    @Test
    public void testONodeToJson() {
        String jsonSomeFieldValueContainBackslash = "{\"abc\":\"\\abc\"}";
        ONode oNode = ONode.loadStr(jsonSomeFieldValueContainBackslash);
        String toJson = oNode.toJson();

        System.out.println(jsonSomeFieldValueContainBackslash);
        System.out.println(toJson);

        assert jsonSomeFieldValueContainBackslash.equals(toJson);
    }
}
