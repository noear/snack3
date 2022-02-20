package features;

import org.junit.Test;
import org.noear.snack.ONode;

import java.util.Properties;

/**
 * @author noear 2022/2/20 created
 */
public class PropertiesTest {
    @Test
    public void test() {
        Properties props = new Properties();
        props.setProperty("title","test");
        props.setProperty("user.id", "1");
        props.setProperty("user.name", "noear");
        props.setProperty("server.urls[0]", "http://x.x.x");
        props.setProperty("server.urls[1]", "http://y.y.y");
        props.setProperty("user.orders[0].items[0].name","手机");

        ONode oNode = ONode.loadObj(props);
        String json = oNode.toJson();

        System.out.println(oNode.toJson());

        Properties props2 = ONode.loadStr(oNode.toJson()).toObject(Properties.class);
        String json2 = ONode.load(props2).toJson();

        System.out.println(json2);

        assert json.length() == json2.length();
    }
}
