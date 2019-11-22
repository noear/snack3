package features;

import _models.UserModel;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Constants;

import java.util.ArrayList;
import java.util.List;

public class _test {
    @Test
    public void test1() {
        String str = "{\"g_udid\":\"1EFB07BFE0D98F8BF9EAF276C92C95FA4BEA3423\",\"g_imid\":\"864499040824376\",\"g_lkey\":\"d359a30a239e9e17daa8f8367ef35422\",\"g_encode\":\"1\",\"g_time\":1572511666,\"g_platform\":\"Android\",\"g_system\":\"8.1.0\",\"g_model\":\"PACM00\",\"g_brand\":\"OPPO\"}";
        ONode n = ONode.load(str);

        n.readonly(true);

        String g_lkey = n.get("g_lkey").getString();
        long g_time = n.get("g_time").getLong();
        int g_encode = n.get("g_encode").getInt();
        String g_platform = n.get("g_platform").getString();
        String g_system = n.get("g_system").getString();
        String g_brand = n.get("g_brand").getString();
        String g_model = n.get("g_model").getString();
        String g_udid = n.get("g_udid").getString();
        String g_imid = n.get("g_imid").getString();
        double g_lng = n.get("g_lng").getDouble();
        double g_lat = n.get("g_lat").getDouble();
        String g_adr = n.get("g_adr").getString();

        String str2 = n.toJson();

        System.out.println(str);
        System.out.println(str2);

        assert str.equals(str2);
    }

    @Test
    public void test2() {
        ONode n = new ONode(); //默认,null string 为 空字符

        assert "".equals(n.getString());
    }

    @Test
    public void test3() {
        ONode n = new ONode(Constants.of()); //空特性，什么都没有

        assert "".equals(n.getString()) == false;
    }

    @Test
    public void test4() {
        List<UserModel> list = new ArrayList<>();
        UserModel u1 = new UserModel();
        u1.id = 1;
        u1.name = "name1";
        list.add(u1);

        UserModel u2 = new UserModel();
        u2.id = 2;
        u2.name = "name2";
        list.add(u2);


        ONode o = ONode.load("{code:1,msg:'succeed'}", true, Constants.serialize);//当toJson(),会产生@type
        o.get("data").get("list").fill(list);

        assert o.select("data.list").count() == 2;

        List<UserModel> list2 = o.select("data.list").toObject(List.class);
        assert list2.size() == 2;

        String message = o.toJson();
        System.out.println(message);

        assert message != null;
    }

    @Test
    public void test5() {
        List<UserModel> list = new ArrayList<>();
        UserModel u1 = new UserModel();
        u1.id = 1;
        u1.name = "name1";
        list.add(u1);

        UserModel u2 = new UserModel();
        u2.id = 2;
        u2.name = "name2";
        list.add(u2);


        ONode o = ONode.load("{code:1,msg:'succeed'}"); //当toJson(),不会产生@type
        o.get("data").get("list").fill(list);

        assert o.select("data.list").count() == 2;


        //普通数据，转为泛型列表
        //
        List<UserModel> list2 = o.select("data.list").toObject((new ArrayList<UserModel>() {
        }).getClass());

        assert list2.size() == list.size();

        String message = o.toJson();
        System.out.println(message);

        assert message != null;
    }

    @Test
    public void test6() {
        ONode tmp = ONode.load("{asdfasdf}");

        System.out.println(tmp.toString());

        assert tmp.isObject();
    }

    @Test
    public void test7() {
        ONode tmp = ONode.load("{asdfasdf}");

        System.out.println(tmp.getString());

        assert "{asdfasdf}".equals(tmp.getString());
    }
}
