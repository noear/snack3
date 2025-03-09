package features;

import _model6.PageResult;
import _model7.PageResult2;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;

import java.util.Map;

/**
 * @author noear 2025/3/9 created
 */
public class GenericTest4 {
    @Test
    public void case1() {
        PageResult<Map<String, Object>> page = new PageResult<>();

        System.out.println(page.getClass());

        String temp = ONode.stringify(page);

        assert temp != null;
    }

    @Test
    public void case2() {
        PageResult2<Map<String, Object>> page = new PageResult2<>();

        String temp = ONode.stringify(page);

        assert temp != null;
    }
}
