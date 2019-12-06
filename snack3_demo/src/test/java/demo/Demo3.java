package demo;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Constants;
import org.noear.snack.core.Feature;
import org.noear.snack.core.TypeRef;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Demo3 {
    @Test
    public void demo1() {

        int i = ONode.load("100").getInt(); //100
        double d = ONode.load("\"99.99\"").getDouble();  //99.99
        boolean b = ONode.load("true").getBoolean();     // true
        String str = ONode.load("String").getString();   // String

        assert i == 100;
        assert d == 99.99;
        assert b == true;
        assert str == "String";
    }

    @Test
    public void demo2() {
        String jsonNumber = ONode.load(100).toJson();       // 100
        String jsonBoolean = ONode.load(false).toJson();    // false
        String jsonString = ONode.load("String").toString(); //"String"

        Assert.assertEquals(jsonNumber, "100");
        Assert.assertEquals(jsonBoolean, "false");
        Assert.assertEquals(jsonString, "\"String\"");
    }

    @Test
    public void demo3() {
        User user = new User("张三", 24);
        String json = ONode.stringify(user); // {"name":"张三","age":24}
    }

    @Test
    public void demo4() {
        String json = "{name:'张三',age:24}";
        User user = ONode.deserialize(json, User.class);

        assert user.age == 24;
    }

    @Test
    public void demo5() {
        String jsonArray = "[\"Android\",\"Java\",\"PHP\"]";
        String[] strings = ONode.load(jsonArray).toObject(String[].class);

        assert strings.length == 3;
    }

    @Test
    public void demo6() {
        String jsonArray = "[\"Android\",\"Java\",\"PHP\"]";
        List<String> ary1 = ONode.load(jsonArray).toObject((new ArrayList<String>() {
        }).getClass());
        List<String> ary2 = ONode.load(jsonArray).toObject((new TypeRef<List<String>>() {
        }).getClass());

        assert ary1.size() == ary2.size();
    }

    @Test
    public void demo7() {
        String json = "{\"name\":\"张三\",\"age\":\"24\"}";

        //反序列化
        User user = ONode.load(json).toObject(User.class);

        //序列化
        ONode.load(user).toJson();
    }

    @Test
    public void demo8() {
        User user = new User("张三", 24);
        System.out.println(ONode.stringify(user)); //{"name":"张三","age":24}

        Constants cfg = Constants.of(Feature.SerializeNulls,Feature.OrderedField);

        System.out.println(ONode.load(user, cfg).toJson()); //{"name":"张三","age":24,"emailAddress":null}
    }

    @Test
    public void demo9() {
        Date date = new Date();

        Constants cfg = Constants.of(Feature.WriteDateUseFormat)
                .build(c-> c.date_format = new SimpleDateFormat("yyyy-MM-dd",c.locale));

        System.out.println(ONode.load(date, cfg).toJson());
    }

}
