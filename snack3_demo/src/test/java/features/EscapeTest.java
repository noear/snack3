package features;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Context;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.snack.from.JsonFromer;
import org.noear.snack.to.JsonToer;

import java.io.IOException;

/**
 * @author noear 2025/1/21 created
 */
public class EscapeTest {
    @Test
    public void case1() {
        String json = "{\"a\":\"\1\"}";

        ONode node = ONode.load(json);
        String json2 = node.toJson();
        String json2Val = node.get("a").getString();
        String json2Val2 = node.get("a").toJson();

        System.out.println(node);
        System.out.println(json2);
        System.out.println(json2Val);
        System.out.println(json2Val2);

        JSONObject tmp = JSON.parseObject(json);
        String tmpJson = JSON.toJSONString(tmp);
        String tmpJsonVal = tmp.getString("a");

        System.out.println(tmp);
        System.out.println(tmpJson);
        System.out.println(tmpJsonVal);

        assert json2.equals(tmpJson);
        assert json2Val.equals(tmpJsonVal);
    }

    @Test
    public void case2() throws IOException {

        Context c = new Context(Options.def(), "{\"a\":\" \\0\\1\\2\\3\\4\\5\\6\\7\"}");

        new JsonFromer().handle(c);

        assert " \0\1\2\3\4\5\6\7".equals(((ONode) c.target).get("a").getString());


        c.source = c.target;
        new JsonToer().handle(c);

        assert "{\"a\":\" \\u0000\\u0001\\u0002\\u0003\\u0004\\u0005\\u0006\\u0007\"}".equals(c.target);

    }

    @Test
    public void case2_2() throws IOException {

        Context c = new Context(Options.def(), "{\"a\":\" \\u0000\\u0001\\u0002\\u0003\\u0004\\u0005\\u0006\\u0007\"}");

        new JsonFromer().handle(c);

        assert " \0\1\2\3\4\5\6\7".equals(((ONode) c.target).get("a").getString());


        c.source = c.target;
        new JsonToer().handle(c);

        assert "{\"a\":\" \\u0000\\u0001\\u0002\\u0003\\u0004\\u0005\\u0006\\u0007\"}".equals(c.target);

    }

    @Test
    public void case3() throws IOException {

        Context c = new Context(Options.of(Feature.BrowserCompatible), "{\"a\":\" \\u000f\\u0012\"}");

        new JsonFromer().handle(c);

        assert " \u000f\u0012".equals(((ONode) c.target).get("a").getString());


        c.source = c.target;
        new JsonToer().handle(c);

        assert "{\"a\":\" \\u000f\\u0012\"}".equalsIgnoreCase((String) c.target);

    }



    /**
     * 测试：unicode 转码
     */
    @Test
    public void case4() throws IOException {

        Context c = new Context(Options.def(), "{\"a\":\"'\\u7684\\t\\n\"}");

        new JsonFromer().handle(c);

        assert "'的\t\n".equals(((ONode) c.target).get("a").getString());


        c.source = c.target;
        new JsonToer().handle(c);

        assert "{\"a\":\"'的\\t\\n\"}".equals(c.target);

    }

    /**
     * 测试：emoji unicode 转码
     */
    @Test
    public void case5() throws IOException {

        Context c = new Context(Options.of(Feature.BrowserCompatible), "{\"a\":\"'\\ud83d\\udc4c\\t\\n\"}");

        new JsonFromer().handle(c);

        assert "'👌\t\n".equals(((ONode) c.target).get("a").getString());


        c.source = c.target;
        new JsonToer().handle(c);

        assert "{\"a\":\"'\\ud83d\\udc4c\\t\\n\"}".equalsIgnoreCase((String) c.target);

    }
}
