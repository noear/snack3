package benchmark.jsonpath;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.Test;

import java.util.List;

public class SpeedJaywayJsonPathTest2 {
    @Test
    public void test1(){
        String json = "{\"store\":{\"book\":[{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95},{\"category\":\"fiction\",\"author\":\"Evelyn Waugh\",\"title\":\"Sword of Honour\",\"price\":12.99,\"isbn\":\"0-553-21311-3\"}],\"bicycle\":{\"color\":\"red\",\"price\":19.95}}}";

        List<String> t7 = JsonPath.read(json,"$..book[*].author");
        System.out.println(t7);
        assert t7.size()==2;

        JSONArray t8 = JsonPath.read(json,"$..book.author");
        System.out.println(t8);
        assert t8.size() == 0;

        JSONArray t9 = JsonPath.read(json,"$.*.book.author");
        System.out.println(t9);
        assert t9.size() == 0;
    }
}
