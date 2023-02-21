package features;

import _model3.Message;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Options;

/**
 * @author noear 2023/2/21 created
 */
public class ClassLoaderTest {
    @Test
    public void demo() {
        Message data = new Message();

        Options options = Options.serialize();
        //指定类加载器
        options.setClassLoader(data.getClass().getClassLoader());

        //序列化
        String json = ONode.load(data, options).toJson();

        //反序列化
        data = ONode.load(json, options).toObject();
    }
}
