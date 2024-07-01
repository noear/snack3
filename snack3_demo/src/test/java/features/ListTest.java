package features;

import _models.MyList;
import _models.NumberModel;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.TypeRef;

/**
 * @author noear 2023/8/17 created
 */
public class ListTest {
    @Test
    public void test1() {
        String json = "[{}]";

        //自定义 list 接口
        MyList<NumberModel> list = ONode.load(json).toObject(new TypeRef<MyList<NumberModel>>() {
        }.getType());

        assert list != null;
        assert list.size() == 1;
        assert list.get(0) instanceof NumberModel;
    }
}
