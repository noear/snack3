package demo;

import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;

public class JsonPath2 {
@Test
public void demo1(){
    final String json = "{\"store\":{\"book\":[{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95},{\"category\":\"fiction\",\"author\":\"Evelyn Waugh\",\"title\":\"Sword of Honour\",\"price\":12.99},{\"category\":\"fiction\",\"author\":\"Herman Melville\",\"title\":\"Moby Dick\",\"isbn\":\"0-553-21311-3\",\"price\":8.99},{\"category\":\"fiction\",\"author\":\"J. R. R. Tolkien\",\"title\":\"The Lord of the Rings\",\"isbn\":\"0-395-19395-8\",\"price\":22.99}],\"bicycle\":{\"color\":\"red\",\"price\":19.95}},\"expensive\":10}";

    ONode n = ONode.load(json);

    ONode t1 = n.select("$.store.book [0].title");
    System.out.println("\nt1:" + t1);

    ONode t2 = n.select("$['store']['book'][0]['title']");
    System.out.println("\nt2:" + t2);

    ONode t3 = n.select("$.store.book[*].author");
    System.out.println("\nt3:" + t3);

    ONode t4 = n.select("$..author");
    System.out.println("\nt4:" + t4);

    ONode t5 = n.select("$.store.*");
    System.out.println("\nt5:" + t5);

    ONode t6 = n.select("$.store..price");
    System.out.println("\nt6:" + t6);

    ONode t7 = n.select("$..book[2]");
    System.out.println("\nt7:" + t7);

    ONode t8 =  n.select("$..book[-2]");
    System.out.println("\nt8:" + t8);

    ONode t9 = n.select("$..book[0,1]");
    System.out.println("\nt9:" + t9);

    ONode ta = n.select("$..book[:2]");
    System.out.println("\nta:" + ta);

    ONode tb = n.select("$..book[1:2]");
    System.out.println("\ntb:" + tb);

    ONode tc = n.select("$..book[-2:]");
    System.out.println("\ntc:" + tc);

    ONode td = n.select("$..book[2:]");
    System.out.println("\ntd:" + td);

    ONode te = n.select("$..book[?(@.isbn)]");
    System.out.println("\nte:" + te);

    ONode tf = n.select("$.store.book[?(@.price < 10)]");
    System.out.println("\ntf:" + tf);

    ONode tg = n.select("$..book[?(@.author =~ /.*REES/i)]");
    System.out.println("\ntg:" + tg);

    ONode th = n.select("$..*");
    System.out.println("\nth:" + th);

    ONode ti = n.select("$..book[?(@.price <= $.expensive)]");
    System.out.println("\nti:" + ti);
}
}
