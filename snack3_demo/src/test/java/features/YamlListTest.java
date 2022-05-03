package features;

import _models.SwaggerInfo;
import org.junit.Test;
import org.noear.solon.Utils;
import org.noear.solon.core.Props;

/**
 * @author noear 2022/5/3 created
 */
public class YamlListTest {
    @Test
    public void test() {
        Props props = new Props(Utils.loadProperties("app.yml"));
        SwaggerInfo swaggerInfo = props.getBean("swagger", SwaggerInfo.class);

        assert swaggerInfo.getResources() != null;
        assert swaggerInfo.getResources().size() == 2;

        assert "2.0".equals(swaggerInfo.getVersion());
    }
}
