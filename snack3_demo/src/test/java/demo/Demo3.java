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

        String json2 = ONode.serialize(user); // {"@type":"demo.User","name":"\u5F20\u4E09","age":24}

        System.out.println(json);
        System.out.println(json2);
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
        String[] strings = ONode.deserialize(jsonArray,String[].class);

        assert strings.length == 3;
    }

    @Test
    public void demo6() {
        String jsonArray = "[\"Android\",\"Java\",\"PHP\"]";

        ONode ary0 		  = ONode.load(jsonArray);
        List<String> ary1 = ONode.deserialize(jsonArray,(new ArrayList<String>(){}).getClass());
        List<String> ary2 = ONode.deserialize(jsonArray,(new TypeRef<List<String>>(){}).getClass());

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

        Constants cfg = Constants.of(Feature.SerializeNulls);
        System.out.println(ONode.load(user, cfg).toJson()); //{"name":"张三","age":24,"emailAddress":null}
    }

    @Test
    public void demo9() {
        Date date = new Date();

        Constants cfg = Constants.of(Feature.WriteDateUseFormat)
                .build(c -> c.date_format = new SimpleDateFormat("yyyy-MM-dd", c.locale));

        System.out.println(ONode.load(date, cfg).toJson()); //2019-12-06
    }
    @Test
    public void demo10() {
        User user = new User("name", 12, "xxx@mail.cn");
        String json = ONode.load(user).rename("emailAddress", "email").toJson(); // {"name":"name","age":12,"email":"xxx@mail.cn"}

        System.out.println(json);
    }

}
