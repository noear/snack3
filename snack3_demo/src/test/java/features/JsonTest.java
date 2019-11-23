package features;

import org.junit.Test;
import org.noear.snack.ONode;
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
        Context c = new Context(Constants.def(), "\"xxx\"");
        new JsonFromer().handle(c);
        assert "xxx".equals(((ONode)c.target).getString());

        c =new Context(Constants.def(), "'xxx'");
        new JsonFromer().handle(c);
        assert "xxx".equals(((ONode)c.target).getString());

        c =new Context(Constants.def(), "true");
        new JsonFromer().handle(c);
        assert ((ONode)c.target).getBoolean();

        c =new Context(Constants.def(), "false");
        new JsonFromer().handle(c);
        assert ((ONode)c.target).getBoolean()==false;

        c =new Context(Constants.def(), "123");
        new JsonFromer().handle(c);
        assert 123 == ((ONode)c.target).getInt();

        c =new Context(Constants.def(), "null");
        new JsonFromer().handle(c);
        assert ((ONode)c.target).isNull();

        c =new Context(Constants.def(), "NaN");
        new JsonFromer().handle(c);
        assert ((ONode)c.target).isNull();

        c =new Context(Constants.def(), "undefined");
        new JsonFromer().handle(c);
        assert ((ONode)c.target).isNull();

    }

    @Test
    public void test21() throws IOException {
        Context c = new Context(Constants.def(), "{'a':'b','c':{'d':'e'},'f':{'g':\"h\"},'i':[{'j':'k','l':'m'},'n']}");

        new JsonFromer().handle(c);

        assert "m".equals(((ONode)c.target).get("i").get(0).get("l").getString());
        assert "n".equals(((ONode)c.target).get("i").get(1).getString());

        c.source = c.target;
        new JsonToer().handle(c);

        assert "{\"a\":\"b\",\"c\":{\"d\":\"e\"},\"f\":{\"g\":\"h\"},\"i\":[{\"j\":\"k\",\"l\":\"m\"},\"n\"]}".equals(c.target);
    }

    @Test
    public void test22() throws IOException {
        Context c = new Context(Constants.def(), "{a:\"b\"}");

        new JsonFromer().handle(c);

        assert "b".equals(((ONode)c.target).get("a").getString());

        c.source = c.target;
        new JsonToer().handle(c);

        assert "{\"a\":\"b\"}".equals(c.target);
    }

    @Test
    public void test23() throws IOException {
        Context c = new Context(Constants.def(), "{a:{b:{c:{d:{e:'f'}}}}}");

        new JsonFromer().handle(c);

        assert "f".equals(((ONode)c.target).get("a").get("b").get("c").get("d").get("e").getString());

        c.source = c.target;
        new JsonToer().handle(c);

        assert "{\"a\":{\"b\":{\"c\":{\"d\":{\"e\":\"f\"}}}}}".equals(c.target);
    }

    @Test
    public void test24() throws IOException {
        String json = "[[[],[]],[[]],[],[{},{},null]]";

        Context c = new Context(Constants.def(), json);

        new JsonFromer().handle(c);

        c.source = c.target;
        new JsonToer().handle(c);

        assert json.equals(c.target);
    }

    @Test
    public void test25() throws IOException {
        Context c = new Context(Constants.def(), "[{a:'b'},{c:'d'},[{e:'f'}]]");

        new JsonFromer().handle(c);

        assert "f".equals(((ONode)c.target).get(2).get(0).get("e").getString());

        c.source = c.target;
        new JsonToer().handle(c);

        assert "[{\"a\":\"b\"},{\"c\":\"d\"},[{\"e\":\"f\"}]]".equals(c.target);
    }

    @Test
    public void test26() throws IOException {
        Context c = new Context(Constants.def(), "[123,123.45,'123.45','2019-01-02 03:04:05',true,false]");

        new JsonFromer().handle(c);

        assert 123 == ((ONode)c.target).get(0).getInt();
        assert 123.45 == ((ONode)c.target).get(1).getDouble();
        assert "123.45".equals(((ONode)c.target).get(2).getString());
        assert "2019-01-02 03:04:05".equals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(((ONode)c.target).get(3).getDate()));
        assert ((ONode)c.target).get(4).getBoolean();
        assert !((ONode)c.target).get(5).getBoolean();

        c.source = c.target;
        new JsonToer().handle(c);

        assert "[123,123.45,\"123.45\",\"2019-01-02 03:04:05\",true,false]".equals(c.target);
    }

    /** 测试：换行符之类的 转码 */
    @Test
    public void test27() throws IOException {

        Context c = new Context(Constants.def(), "{\"a\":\"\\t\"}");

        new JsonFromer().handle(c);

        assert "\t".equals(((ONode)c.target).get("a").getString());


        c.source = c.target;
        new JsonToer().handle(c);

        assert "{\"a\":\"\\t\"}".equals(c.target);

    }

    /** 测试：unicode 转码 */
    @Test
    public void test28() throws IOException {

        Context c = new Context(Constants.def(), "{\"a\":\"'\\u7684\\t\\n\"}");

        new JsonFromer().handle(c);

        assert "'的\t\n".equals(((ONode)c.target).get("a").getString());


        c.source = c.target;
        new JsonToer().handle(c);

        assert "{\"a\":\"'的\\t\\n\"}".equals(c.target);

    }

    /** 测试：emoji unicode 转码 */
    @Test
    public void test29() throws IOException {

        Context c = new Context(Constants.of(Feature.BrowserCompatible), "{\"a\":\"'\\ud83d\\udc4c\\t\\n\"}");

        new JsonFromer().handle(c);

        assert "'👌\t\n".equals(((ONode)c.target).get("a").getString());


        c.source = c.target;
        new JsonToer().handle(c);

        assert "{\"a\":\"'\\ud83d\\udc4c\\t\\n\"}".equalsIgnoreCase((String) c.target);

    }

    @Test
    public void test30() throws IOException {

        Context c = new Context(Constants.def(), "{\"a\":\" \\0\\1\\2\\3\\4\\5\\6\\7\"}");

        new JsonFromer().handle(c);

        assert " \0\1\2\3\4\5\6\7".equals(((ONode)c.target).get("a").getString());


        c.source = c.target;
        new JsonToer().handle(c);

        assert "{\"a\":\" \\0\\1\\2\\3\\4\\5\\6\\7\"}".equals(c.target);

    }

    @Test
    public void test31() throws IOException {

        Context c = new Context(Constants.of(Feature.BrowserCompatible), "{\"a\":\" \\u000f\\u0012\"}");

        new JsonFromer().handle(c);

        assert " \u000f\u0012".equals(((ONode)c.target).get("a").getString());


        c.source = c.target;
        new JsonToer().handle(c);

        assert "{\"a\":\" \\u000f\\u0012\"}".equalsIgnoreCase((String) c.target);

    }

}