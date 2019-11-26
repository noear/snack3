package features;

import org.junit.Test;
import org.noear.snack.ONode;

public class JsonPathTest2 {
    final static String json = "{\"store\":{\"book\":[{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95},{\"category\":\"fiction\",\"author\":\"Evelyn Waugh\",\"title\":\"Sword of Honour\",\"price\":12.99},{\"category\":\"fiction\",\"author\":\"Herman Melville\",\"title\":\"Moby Dick\",\"isbn\":\"0-553-21311-3\",\"price\":8.99},{\"category\":\"fiction\",\"author\":\"J. R. R. Tolkien\",\"title\":\"The Lord of the Rings\",\"isbn\":\"0-395-19395-8\",\"price\":22.99}],\"bicycle\":{\"color\":\"red\",\"price\":19.95}},\"expensive\":10}";
    @Test
    public void test1(){
        ONode n = ONode.load(json);

        String t1 = n.select("$.store.book [0].title").getString();
        System.out.println(t1);

        String t2 = n.select("$['store']['book'][0]['title']").getString();
        System.out.println(t2);

        ONode t3 = n.select("$.store.book[*].author");
        System.out.println(t3);

        ONode t4 = n.select("$..author");
        System.out.println(t4);

        ONode t5 = n.select("$.store.*");
        System.out.println(t5);

        ONode t6 = n.select("$.store..price");
        System.out.println(t6);

        ONode t7 = n.select("$..book[2]");
        System.out.println(t7);

        ONode t8 =  n.select("$..book[-2]");
        System.out.println(t8);

        ONode t9 = n.select("$..book[0,1]");
        System.out.println(t9);

        ONode ta = n.select("$..book[:2]");
        System.out.println(ta);

        ONode tb = n.select("$..book[1:2]");
        System.out.println(tb);

        ONode tc = n.select("$..book[-2:]");
        System.out.println(tc);

        ONode td = n.select("$..book[2:]");
        System.out.println(td);

        ONode te = n.select("$..book[?(@.isbn)]");
        System.out.println(te);

        ONode tf = n.select("$.store.book[?(@.price < 10)]");
        System.out.println(tf);

        ONode tg = n.select("$..book[?(@.author =~ /.*REES/i)]");
        System.out.println(tg);

        ONode th = n.select("$..*");
        System.out.println(th);

    }
}
