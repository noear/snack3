package demo;

import org.junit.Test;
import org.noear.snack.core.Constants;
import org.noear.snack.core.Context;
import org.noear.snack.core.Feature;
import org.noear.snack.from.JsonFromer;
import org.noear.snack.to.JsonToer;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * 2019.01.28
 *
 * @author cjl
 */
public class JsonTest {

    /** 测试非对象，非数组数据 */
    @Test
    public void test11() throws IOException{
        Context c = new Context(Constants.def, "\"xxx\"");
        new JsonFromer().handle(c);
        assert "xxx".equals(c.node.getString());

        c =new Context(Constants.def, "'xxx'");
        new JsonFromer().handle(c);
        assert "xxx".equals(c.node.getString());

        c =new Context(Constants.def, "true");
        new JsonFromer().handle(c);
        assert c.node.getBoolean();

        c =new Context(Constants.def, "false");
        new JsonFromer().handle(c);
        assert c.node.getBoolean()==false;

        c =new Context(Constants.def, "123");
        new JsonFromer().handle(c);
        assert 123 == c.node.getInt();

        c =new Context(Constants.def, "null");
        new JsonFromer().handle(c);
        assert c.node.isNull();

        c =new Context(Constants.def, "NaN");
        new JsonFromer().handle(c);
        assert c.node.isNull();

        c =new Context(Constants.def, "undefined");
        new JsonFromer().handle(c);
        assert c.node.isNull();

    }

    @Test
    public void test21() throws IOException {
        Context c = new Context(Constants.def, "{'a':'b','c':{'d':'e'},'f':{'g':\"h\"},'i':[{'j':'k','l':'m'},'n']}");

        new JsonFromer().handle(c);

        assert "m".equals(c.node.get("i").get(0).get("l").getString());
        assert "n".equals(c.node.get("i").get(1).getString());

        new JsonToer().handle(c);

        assert "{\"a\":\"b\",\"c\":{\"d\":\"e\"},\"f\":{\"g\":\"h\"},\"i\":[{\"j\":\"k\",\"l\":\"m\"},\"n\"]}".equals(c.text);
    }

    @Test
    public void test22() throws IOException {
        Context c = new Context(Constants.def, "{a:\"b\"}");

        new JsonFromer().handle(c);

        assert "b".equals(c.node.get("a").getString());

        new JsonToer().handle(c);

        assert "{\"a\":\"b\"}".equals(c.text);
    }

    @Test
    public void test23() throws IOException {
        Context c = new Context(Constants.def, "{a:{b:{c:{d:{e:'f'}}}}}");

        new JsonFromer().handle(c);

        assert "f".equals(c.node.get("a").get("b").get("c").get("d").get("e").getString());

        new JsonToer().handle(c);

        assert "{\"a\":{\"b\":{\"c\":{\"d\":{\"e\":\"f\"}}}}}".equals(c.text);
    }

    @Test
    public void test24() throws IOException {
        String json = "[[[],[]],[[]],[],[{},{},null]]";

        Context c = new Context(Constants.def, json);

        new JsonFromer().handle(c);

        new JsonToer().handle(c);

        assert json.equals(c.text);
    }

    @Test
    public void test25() throws IOException {
        Context c = new Context(Constants.def, "[{a:'b'},{c:'d'},[{e:'f'}]]");

        new JsonFromer().handle(c);

        assert "f".equals(c.node.get(2).get(0).get("e").getString());

        new JsonToer().handle(c);

        assert "[{\"a\":\"b\"},{\"c\":\"d\"},[{\"e\":\"f\"}]]".equals(c.text);
    }

    @Test
    public void test26() throws IOException {
        Context c = new Context(Constants.def, "[123,123.45,'123.45','2019-01-02 03:04:05',true,false]");

        new JsonFromer().handle(c);

        assert 123 == c.node.get(0).getInt();
        assert 123.45 == c.node.get(1).getDouble();
        assert "123.45".equals(c.node.get(2).getString());
        assert "2019-01-02 03:04:05".equals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.node.get(3).getDate()));
        assert c.node.get(4).getBoolean();
        assert !c.node.get(5).getBoolean();

        new JsonToer().handle(c);

        assert "[123,123.45,\"123.45\",\"2019-01-02 03:04:05\",true,false]".equals(c.text);
    }

    /** 测试：换行符之类的 转码 */
    @Test
    public void test27() throws IOException {

        Context c = new Context(Constants.def, "{\"a\":\"\\t\"}");

        new JsonFromer().handle(c);

        assert "\t".equals(c.node.get("a").getString());

        new JsonToer().handle(c);

        assert "{\"a\":\"\\t\"}".equals(c.text);

    }

    /** 测试：unicode 转码 */
    @Test
    public void test28() throws IOException {

        Context c = new Context(Constants.def, "{\"a\":\"'\\u7684\\t\\n\"}");

        new JsonFromer().handle(c);

        assert "'的\t\n".equals(c.node.get("a").getString());

        new JsonToer().handle(c);

        assert "{\"a\":\"'的\\t\\n\"}".equals(c.text);

    }

    /** 测试：emoji unicode 转码 */
    @Test
    public void test29() throws IOException {

        Context c = new Context(Constants.of(Feature.BrowserCompatible), "{\"a\":\"'\\ud83d\\udc4c\\t\\n\"}");

        new JsonFromer().handle(c);

        assert "'👌\t\n".equals(c.node.get("a").getString());

        new JsonToer().handle(c);

        assert "{\"a\":\"'\\ud83d\\udc4c\\t\\n\"}".equalsIgnoreCase(c.text);

    }

    @Test
    public void test30() throws IOException {

        Context c = new Context(Constants.def, "{\"a\":\" \\0\\1\\2\\3\\4\\5\\6\\7\"}");

        new JsonFromer().handle(c);

        assert " \0\1\2\3\4\5\6\7".equals(c.node.get("a").getString());

        new JsonToer().handle(c);

        assert "{\"a\":\" \\0\\1\\2\\3\\4\\5\\6\\7\"}".equalsIgnoreCase(c.text);

    }

    @Test
    public void test31() throws IOException {

        Context c = new Context(Constants.of(Feature.BrowserCompatible), "{\"a\":\" \\u000f\\u0012\"}");

        new JsonFromer().handle(c);

        assert " \u000f\u0012".equals(c.node.get("a").getString());

        new JsonToer().handle(c);

        assert "{\"a\":\" \\u000f\\u0012\"}".equalsIgnoreCase(c.text);

    }

}