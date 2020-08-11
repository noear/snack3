package features;

import _models.UserModel;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.snack.core.Constants;
import org.noear.snack.core.Feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class _test {
    @Test
    public void test0(){
        long times = System.currentTimeMillis();
        String str="{\n" +
                "\t\"size\": 100,\n" +
                "\t\"query\": {\n" +
                "\t\t\"bool\": {\n" +
                "\t\t\t\"must_not\": [{\n" +
                "\t\t\t\t\"terms\": {\n" +
                "\t\t\t\t\t\"post_id\": [439810, 444931, 445966, 440847, 445453, 436746, 152590, 435217, 447006, 447007, 445471, 445978, 438298, 445976, 447014, 445991, 447015, 447012, 435236, 447013, 447010, 445474, 447011, 447008, 445984, 445985, 447009, 444460, 447016, 445993, 447017, 444469, 192056, 444475, 444472, 436792, 447033, 444473, 445511, 445508, 445509, 440910, 439370, 445515, 445000, 445513, 447063, 436821, 437842, 444510, 446559, 445023, 444508, 444509, 435293, 445019, 444507, 439897, 436313, 436324, 435813, 444514, 435810, 445034, 445035, 445559, 446066, 444531, 440435, 437360, 444529, 445564, 447100, 445562, 446598, 446596, 446594, 447106, 436354, 445059, 445056, 447104, 445568, 445057, 446604, 439948, 435853, 439949, 446602, 439946, 445067, 439947, 446600, 445576, 445079, 445076, 445077, 445074, 435859, 435857, 435870, 437406, 444571, 444579, 445091, 437409, 447150, 447151, 435887, 447148, 447149, 447146, 445098, 447147, 447156, 447157, 447154, 447155, 437939, 447152, 447153, 446651, 435896, 438982, 445646, 445132, 435915, 434888, 444118, 445655, 445140, 444117, 445650, 436434, 436435, 444112, 445648, 435920, 444625, 445649, 446161, 437470, 439515, 444120, 444647, 440046, 440044, 437993, 436457, 444660, 447218, 440050, 440048, 445694, 436479, 445693, 444157, 445691, 444152, 444153, 444167, 447236, 437508, 444162, 445696, 444160, 444161, 435969, 444685, 439565, 444170, 444171, 439563, 444168, 444169, 440086, 440084, 440085, 444178, 440083, 444176, 434960, 445201, 444177, 437521, 445213, 447268, 447269, 439086, 446255, 440619, 437032, 446773, 435509, 436531, 437040, 435504, 444735, 437048, 435512, 445252, 444738, 444736, 444737, 435016, 324948, 439636, 445778, 440158, 446812, 446813, 445786, 446810, 445784, 447330, 444258, 447328, 444256, 436064, 445793, 444257, 446318, 436588, 436587, 445303, 436599, 445300, 445301, 436083, 444797, 436601, 436098, 444289, 436623, 444299, 445832, 444297, 436117, 444306, 447391, 444316, 445341, 444317, 436125, 447386, 447387, 445848, 445862, 445861, 445859, 444323, 445871, 444333, 445354, 444343, 444341, 440752, 437168, 445887, 446919, 444366, 444365, 436685, 445399, 435667, 444368, 435664, 445404, 445405, 436186, 445400, 446936, 443864, 445401, 444902, 444903, 436196, 445411, 438243, 445408, 434144, 438254, 437742, 444911, 445942, 438258, 439794, 444912, 435696, 438256, 436222, 445949, 445946, 445947, 436731, 438779, 444408]\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}],\n" +
                "\t\t\t\"must\": [{\n" +
                "\t\t\t\t\"nested\": {\n" +
                "\t\t\t\t\t\"path\": \"tag\",\n" +
                "\t\t\t\t\t\"query\": {\n" +
                "\t\t\t\t\t\t\"terms\": {\n" +
                "\t\t\t\t\t\t\t\"tag.title\": [\"轴线性\", \"屏幕\", \"搭载\", \"像素\", \"摄像头\", \"游戏\", \"一加7T系列\", \"性能\", \"影像技术方面\", \"流体屏\", \"GRF\", \"选手\", \"队员\", \"联盟\", \"英雄\", \"母公司\", \"Griffin\", \"战队\", \"分部\", \"格里芬\", \"区政府\", \"演讲\", \"干部\", \"韶华\", \"比赛\", \"优胜奖\", \"毛丹\", \"讲述\", \"一等奖\", \"版本\", \"业务\", \"数据\", \"架构\", \"扩展性\", \"用户\", \"易用性\", \"一致性\", \"分区\", \"OceanBase\", \"秋促\", \"下架\", \"Steam\", \"清理\", \"小时\", \"封禁\", \"外媒\", \"门槛\", \"战而生\", \"网游\", \"停服\", \"Gearbox\", \"公告\", \"日期\", \"玩家\", \"关闭\", \"专区\", \"库中\", \"游戏库\", \"追踪\", \"节假\", \"清洗\", \"全网\", \"删除库\", \"海量\", \"测试\", \"CPU\", \"平台\", \"搭配独显\", \"功耗\", \"四通道\", \"单线程\", \"基准\", \"唯品会\", \"茅台\", \"公司\", \"商店\", \"合作\", \"快递业务\", \"裁员\", \"蒂芙尼\", \"达成\", \"呆萝卜\", \"视图\", \"增量\", \"Hudi\", \"引擎\", \"摄取\", \"集并\", \"处理\", \"构建\", \"数据集\", \"云存储\"]\n" +
                "\t\t\t\t\t\t}\n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}, {\n" +
                "\t\t\t\t\"range\": {\n" +
                "\t\t\t\t\t\"release_fulltime\": {\n" +
                "\t\t\t\t\t\t\"gte\": 1574785627568 ,\n" +
                "\t\t\t\t\t\t\"gte2\": new Date("+times+") \n" +
                "\t\t\t\t\t}\n" +
                "\t\t\t\t}\n" +
                "\t\t\t}]\n" +
                "\t\t}\n" +
                "\t},\n" +
                "\t\"_source\": [\"post_id\"]\n" +
                "}";

        ONode tmp = ONode.loadStr(str);

        assert tmp.isObject();
    }
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


        ONode o = ONode.load("{code:1,msg:'succeed'}",  Constants.serialize());//当toJson(),会产生@type
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
        ONode tmp = ONode.loadObj("{asdfasdf}");

        System.out.println(tmp.getString());

        assert "{asdfasdf}".equals(tmp.getString());
    }

    @Test
    public void test8() {
        UserModel um = new UserModel();
        um.name = "中文";
        um.id = 1;
        um.note = "你好世界!";

        String json = ONode.serialize(um);
        System.out.println(json);

        String json2 = ONode.loadStr(json).toJson();
        System.out.println(json2);

        UserModel um2 = ONode.deserialize(json, UserModel.class);

        assert  um2.name.equals(um.name);
    }

//    @Test
//    public void test9() {
//        String text = "{a:'\\x'}";
//        ONode tmp = ONode.loadStr(text);
//        assert tmp.count() == 1;
//    }

    @Test
    public void test10(){
        //
        // 单引号输出
        //
        String txt = "{id:1,name:'x'}";
        ONode tmp = ONode.load(txt);

        tmp.cfg().sub(Feature.QuoteFieldNames) //取消字段引号
                 .add(Feature.UseSingleQuotes); //采用单引号

        String txt2 = tmp.toJson();

        assert txt.equals(txt2);
    }

    @Test
    public void test11(){
        ONode tmp = ONode.loadObj("46qh", Constants.serialize().sub(Feature.BrowserCompatible));

        assert "46qh".equals(tmp.val().getString());
    }

    @Test
    public void test12(){
        ONode n = ONode.load("{code:1,msg:'Hello world!',data:[{a:'b',c:'d'}]}");

        System.out.println(n.select("$.data[*].a"));

        System.out.println(n.select("$.data[*]['a']"));
        System.out.println(n.select("$.data[*]['a','c']"));

        System.out.println(n.select("$.data[0]['a']",true));
        System.out.println(n.select("$.data[0]['a','c']",true));
    }

    @Test
    public void test13(){
        String path = "$.definitions.Request«List«BookingNoDTO»»";
        String json = "{\"definitions\":{\"Request«List«BookingNoDTO»»\":{\"type\":\"object\",\"properties\":{\"header\":{\"description\":\"Request header对象\",\"$ref\":\"#/definitions/Request Header\"},\"model\":{\"type\":\"array\",\"description\":\"业务入参对象\",\"items\":{\"$ref\":\"#/definitions/BookingNoDTO\"}}},\"title\":\"Request«List«BookingNoDTO»»\",\"description\":\"Request对象\"}}}";
        ONode root = ONode.load(json);
        ONode sub = root.select(path);
        assert sub.isNull() == false;
    }

    @Test
    public void test14() {
        String json = "{\"k1\":1,\"k2\":\"123\",\"k3\":\"az章\",\"k4\":[1, 2],\"k5\":{\"k51\": \"511\", \"k52\":[{\"k521\":\"e\"},{\"k521\":\"F\"}]}}";

        ONode oNode = ONode.load(json);
        ONode oTmp = oNode.select("$.k1");

        if (oTmp.isValue()) {
            oTmp.val(2);
        } else if (oTmp.isArray()) {
            oTmp.forEach(n -> n.val(2));
        }

        System.err.println(oNode.toJson());
    }

    @Test
    public void test15(){
        String json = "{\"k1\":1,\"k2\":\"123\",\"k3\":\"az章\",\"k4\":[1, 2],\"k5\":{\"k51\": \"511\", \"k52\":[{\"k521\":\"e\"},{\"k521\":\"F\"}]}}";

        ONode oNode = ONode.load(json);

        oNode.select("$.k1").val(2);

        System.err.println(oNode.toJson());
    }

    @Test
    public void test152(){
        String json = "{\"k1\":1,\"k2\":\"123\",\"k3\":\"az章\",\"k4\":[1, 2],\"k5\":{\"k51\": \"511\", \"k52\":[{\"k521\":\"e\"},{\"k521\":\"F\"}]}}";

        ONode oNode = ONode.load(json);

        oNode.select("$.k5.k52[?(@.k521 == 'e')].k521").forEach(n-> n.val("ee"));

        System.err.println(oNode.toJson());
    }

    @Test
    public void test16(){
        String json = "{\"k1\":1,\"k2\":\"123\",\"k3\":\"az章\",\"k4\":[1, 2],\"k5\":{\"k51\": \"511\", \"k52\":[{\"k521\":\"e\"},{\"k521\":\"F\"}]}}";

        ONode oNode = ONode.load(json);
        Map<String, String> map = new HashMap<>();
        map.put("newK1", "中国");
        map.put("newK2", "JSON");
        oNode.select("$.k5.k51").val(map);

        System.err.println(oNode.toJson());
    }
}
