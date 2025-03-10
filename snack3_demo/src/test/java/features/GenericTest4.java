package features;

import _model6.PageInfo;
import _model6.PageResult;
import _model7.PageInfo2;
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
    public void case1_2() {
        String json ="{code:1,data:{rows:[]}}";

        PageResult<Map<String, Object>> page = ONode.deserialize(json, new PageResult<Map<String, Object>>(){}.getClass());

        assert page != null;
        assert page.getData() != null;
        assert page.getData() instanceof PageInfo;
    }

    @Test
    public void case1_3() {
        String json ="{code:1,data:{rows:['1']}}";

        PageResult<Integer> page = ONode.deserialize(json, new PageResult<Integer>(){}.getClass());

        assert page != null;
        assert page.getData() != null;
        assert page.getData() instanceof PageInfo;
        assert page.getData().getRows().size() == 1;
        assert page.getData().getRows().get(0) instanceof Integer;
    }

    @Test
    public void case2() {
        PageResult2<Map<String, Object>> page = new PageResult2<>();

        String temp = ONode.stringify(page);

        assert temp != null;
    }

    @Test
    public void case2_2() {
        String json ="{code:1,data:{rows:[]}}";

        PageResult2<Map<String, Object>> page = ONode.deserialize(json, new PageResult2<Map<String, Object>>(){}.getClass());

        assert page != null;
        assert page.getData() != null;
        assert page.getData() instanceof PageInfo2;
    }

    @Test
    public void case2_3() {
        String json ="{code:1,data:{rows:['1']}}";

        PageResult2<Integer> page = ONode.deserialize(json, new PageResult2<Integer>(){}.getClass());

        assert page != null;
        assert page.getData() != null;
        assert page.getData() instanceof PageInfo2;
        assert page.getData().getRows().size() == 1;
        assert page.getData().getRows().get(0) instanceof Integer;
    }
}
