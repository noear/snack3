package org.noear.snack.core;

import java.util.*;

/**
 * 名字值集合
 *
 * @author noear
 * @since 2.0
 */
public class NameValues {
    private final List<Map.Entry<String, String>> items = new ArrayList<>();

    public int size() {
        return items.size();
    }

    public void sort() {
        items.sort(Map.Entry.comparingByKey());
    }

    public void add(String name, String value) {
        items.add(new AbstractMap.SimpleEntry<>(name, value));
    }

    public Map.Entry<String, String> get(int index) {
        return items.get(index);
    }

    public List<Map.Entry<String, String>> getItems() {
        return items;
    }
}
