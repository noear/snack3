package demo;

import org.junit.Test;
import org.noear.snack.ONode;

import java.util.List;
import java.util.Map;

public class JsonPath {
@Test
public void demo1() {
    String json = "{ \"store\": {\n" +
            "    \"book\": [ \n" +
            "      { \"category\": \"reference\",\n" +
            "        \"author\": \"Nigel Rees\",\n" +
            "        \"title\": \"Sayings of the Century\",\n" +
            "        \"price\": 8.95\n" +
            "      },\n" +
            "      { \"category\": \"fiction\",\n" +
            "        \"author\": \"Evelyn Waugh\",\n" +
            "        \"title\": \"Sword of Honour\",\n" +
            "        \"price\": 12.99,\n" +
            "        \"isbn\": \"0-553-21311-3\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"bicycle\": {\n" +
            "      \"color\": \"red\",\n" +
            "      \"price\": 19.95\n" +
            "    }\n" +
            "  }\n" +
            "}\n";


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
