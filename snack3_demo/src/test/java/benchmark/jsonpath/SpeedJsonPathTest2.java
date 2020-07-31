package benchmark.jsonpath;

import org.junit.Test;
import org.noear.snack.ONode;

public class SpeedJsonPathTest2 {
    @Test
    public void test1(){
        String json = "{\"store\":{\"book\":[{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95},{\"category\":\"fiction\",\"author\":\"Evelyn Waugh\",\"title\":\"Sword of Honour\",\"price\":12.99,\"isbn\":\"0-553-21311-3\"}],\"bicycle\":{\"color\":\"red\",\"price\":19.95}}}";
        ONode n = ONode.load(json);

        ONode t7 = n.select("$..book[*].author");
        System.out.println(t7);
        assert t7.isArray() && t7.count()==2;

        ONode t8 = n.select("$..book.author");
        System.out.println(t8);
        assert t8.isArray() && t8.count()==0;
    }
}
