package features;

import org.junit.Test;
import org.noear.snack.ONode;

import java.util.List;
import java.util.Map;

public class JsonPathTest2 {
    @Test
    public void test1() {
        final String json = "{\"store\":{\"book\":[{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95},{\"category\":\"fiction\",\"author\":\"Evelyn Waugh\",\"title\":\"Sword of Honour\",\"price\":12.99},{\"category\":\"fiction\",\"author\":\"Herman Melville\",\"title\":\"Moby Dick\",\"isbn\":\"0-553-21311-3\",\"price\":8.99},{\"category\":\"fiction\",\"author\":\"J. R. R. Tolkien\",\"title\":\"The Lord of the Rings\",\"isbn\":\"0-395-19395-8\",\"price\":22.99}],\"bicycle\":{\"color\":\"red\",\"price\":19.95}},\"expensive\":10}";

        ONode n = ONode.load(json);

        String t1 = n.select("$.store.book [0].title").getString();
        System.out.println(t1);
        assert "Sayings of the Century".equals(t1);

        String t2 = n.select("$['store']['book'][0]['title']").getString();
        System.out.println(t2);
        assert "Sayings of the Century".equals(t1);

        ONode t3 = n.select("$.store.book[*].author");
        System.out.println(t3);

        ONode t4 = n.select("$..author");
        System.out.println(t4);
        assert t3.toJson().equals(t4.toJson());

        ONode t5 = n.select("$.store.*");
        System.out.println(t5);
        assert t5.count() == 2;

        ONode t6 = n.select("$.store..price");
        System.out.println(t6);
        assert t6.isArray();

        Map<String, Object> t7 = n.select("$..book[2]").toObject(Map.class);
        System.out.println(t7);

        ONode t8 = n.select("$..book[-2]");
        System.out.println(t8);
        assert "Herman Melville".equals(t8.get(0).get("author").getString());

        ONode t9 = n.select("$..book[0,1]");
        System.out.println(t9);
        assert "reference".equals(t9.get(0).get("category").getString());

        ONode ta = n.select("$..book[:2]");
        System.out.println(ta);
        assert ta.count() == 2;

        ONode tb = n.select("$..book[1:2]");
        System.out.println(tb);
        assert tb.count() == 1;

        ONode tc = n.select("$..book[-2:]");
        System.out.println(tc);
        assert tc.count() == 2;

        ONode td = n.select("$..book[2:]");
        System.out.println(td);
        assert tc.count() == 2;

        ONode te = n.select("$..book[?(@.isbn)]");
        System.out.println(te);
        assert tc.count() == 2;

        ONode tf = n.select("$.store.book[?(@.price < 10)]");
        System.out.println(tf);
        assert tc.count() == 2;

        ONode tg = n.select("$..book[?(@.author =~ /.*REES/i)]");
        System.out.println(tg);
        assert tg.count() == 1;

        ONode th = n.select("$..*");
        System.out.println(th);
        assert th.count() == 28;

        ONode ti = n.select("$..book[?(@.price <= $['expensive'])]");
        System.out.println(ti);
        assert ti.count() == 2;

        ONode tj = n.select("$..book[?(@.price <= $.expensive)]");
        System.out.println(tj);
        assert tj.count() == 2;
    }

    @Test
    public void test2() {
        String json = "{\"store\":{\"book\":[{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95},{\"category\":\"fiction\",\"author\":\"Evelyn Waugh\",\"title\":\"Sword of Honour\",\"price\":12.99,\"isbn\":\"0-553-21311-3\"}],\"bicycle\":{\"color\":\"red\",\"price\":19.95}}}";
        ONode n = ONode.load(json);

        String t1 = n.select("$.store.book[0].author").getString();
        System.out.println(t1);
        assert "Nigel Rees".equals(t1);

        List<String> t2 = n.select("$.store.book[*].author").toObject(List.class);
        System.out.println(t2);
        assert t2.size() == 2;

        List<Map> t3 = n.select("$.store.book[?(@.category == 'reference')]").toObject(List.class);
        System.out.println(t3);
        assert t3.size() == 1;

        ONode t4 = n.select("$.store.book[?(@.price > 10)]");
        System.out.println(t4);
        assert t4.isArray() && t4.count() == 1;

        ONode t5 = n.select("$.store.book[?(@.isbn)]");
        System.out.println(t5);
        assert t5.isArray() && t5.count() == 1;

        List<Double> t6 = n.select("$..price").toObject(List.class);
        System.out.println(t6);

        ONode t7 = n.select("$.store.book[*]");
        System.out.println(t7);
        assert t7.isArray() && t7.count() == 2;

        ONode t8 = n.select("$..book[*].author");
        System.out.println(t8);
        assert t8.isArray() && t8.count() == 2;
    }

    @Test
    public void test3() {
        String json = "{\"store\":{\"book\":[{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95},{\"category\":\"fiction\",\"author\":\"Evelyn Waugh\",\"title\":\"Sword of Honour\",\"price\":12.99,\"isbn\":\"0-553-21311-3\"}],\"bicycle\":{\"color\":\"red\",\"price\":19.95}}}";
        ONode n = ONode.load(json);

        List<Map> t3 = n.select("$.store.book[?(@.category == 'reference')]").toObject(List.class);
        System.out.println(t3);
        assert t3.size() == 1;

        ONode t4 = n.select("$.store.book[?(@.price > 10)]");
        System.out.println(t4);
        assert t4.isArray() && t4.count() == 1;

        ONode t5 = n.select("$.store.book[?(@.isbn)]");
        System.out.println(t5);
        assert t5.isArray() && t5.count() == 1;
    }

    @Test
    public void test4() {
        String str = "{\"transaction_id\":\"87cd7d71-c6df-4281-9927-469094395677\",\"data_list\":[{\"_widget_1634621669273\":{\"value\":\"\"},\"_widget_1634621669328\":{\"value\":\"\"},\"_widget_1634621669309\":{\"value\":\"\"},\"_widget_1634621669347\":{\"value\":\"\"},\"_widget_1658975890076\":{\"value\":\"\"},\"_widget_1658971955803\":{\"value\":0},\"_widget_1658971955822\":{\"value\":0},\"_widget_1658971955841\":{\"value\":\"\"},\"_widget_1634621669385\":{\"value\":0},\"_widget_1634622714388\":{\"value\":0},\"_widget_1634621669443\":{\"value\":0},\"_widget_1658971956001\":{\"value\":0},\"_widget_1658971956020\":{\"value\":\"\"},\"_widget_1658971956147\":{\"value\":\"\"},\"_widget_1662702941804\":{\"value\":\"\"},\"_widget_1662702941823\":{\"value\":\"\"},\"_widget_1634625436803\":{\"value\":0},\"_widget_1658971956075\":{\"value\":[]},\"_widget_1634621669481\":{\"value\":\"\"}}]}\n";
        ONode oNode = ONode.loadStr(str);
        ONode select = oNode.select("$.data_list[0]._widget_1634621669273");

        System.out.println(select.toString());

        assert select.isObject();
        assert "".equals(select.get("value").getRawString());
    }

    @Test
    public void test5() {
        String json = "{\"code\":\"0\",\"message\":\"操作成功\",\"data\":{\"records\":[{\"name\":\"测试节点1\"},{\"name\":\"测试节点2\"}]}}";
        ONode oNode = ONode.load(json);

        ONode rst = oNode.select("$.data.records[?(@.name == '测试节点1')]");

        System.out.println(rst.toJson());

        assert rst.isArray();
        assert rst.count() == 1;
    }

//    @Test
//    public void test5() {
//        ONode oNode = new ONode();
//        oNode.select("$.orders[0].price").val(500);
//
//        System.out.println(oNode.toJson());
//    }
}
