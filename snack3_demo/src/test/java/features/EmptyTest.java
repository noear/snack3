package features;

import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;

import java.util.Collections;
import java.util.Map;

/**
 * @author noear 2024/5/22 created
 */
public class EmptyTest {
    @Test
    public void demo() {
        String json = "{data:{a:1,b:2}}";
        EmptyDo emptyDo = ONode.deserialize(json, EmptyDo.class);

        assert emptyDo.data.size() == 2;
    }

    public static class EmptyDo {
        Map<String, Object> data = Collections.emptyMap();
    }
}
