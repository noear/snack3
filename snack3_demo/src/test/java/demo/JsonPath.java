package demo;

import org.junit.jupiter.api.Test;
import org.noear.snack.ONode;

import java.util.List;
import java.util.Map;

public class JsonPath {
@Test
public void demo1() {
    String json = "{\"store\":{\"book\":[{\"category\":\"reference\",\"author\":\"Nigel Rees\",\"title\":\"Sayings of the Century\",\"price\":8.95},{\"category\":\"fiction\",\"author\":\"Evelyn Waugh\",\"title\":\"Sword of Honour\",\"price\":12.99,\"isbn\":\"0-553-21311-3\"}],\"bicycle\":{\"color\":\"red\",\"price\":19.95}}}";


    ONode n = ONode.load(json);

    Map map = n.select("$.store.book[0]").toObject(Map.class);

    System.out.println("category: " + map.get("category"));
    System.out.println("author: " + map.get("author"));
    System.out.println("title: " + map.get("title"));
    System.out.println("price: " + map.get("price"));

    System.out.println("========================");

    List<String> list = n.select("$.store.book[*].author").toObject(List.class);
    for (String author : list) {
        System.out.println(author);
    }
}
}
