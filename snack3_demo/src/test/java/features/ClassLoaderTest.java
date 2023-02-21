package features;

import _model3.Message;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Options;
import org.noear.solon.core.JarClassLoader;

/**
 * @author noear 2023/2/21 created
 */
public class ClassLoaderTest {
    @Test
    public void test() {
        Message data = new Message();

        Options options = Options.serialize();
        options.setClassLoader(data.getClass().getClassLoader());

        String json = ONode.load(data, options).toJson();
        data = ONode.load(json, options).toObject();
    }
}
