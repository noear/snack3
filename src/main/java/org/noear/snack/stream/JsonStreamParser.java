package org.noear.snack.stream;

import org.noear.snack.ONode;
import org.noear.snack.core.JsonReader;

import java.io.IOException;
import java.util.Map;

/**
 * 流式JSON解析器
 */
public class JsonStreamParser {
    public interface Handler {
        void startObject() throws IOException;
        void endObject() throws IOException;
        void startArray() throws IOException;
        void endArray() throws IOException;
        void key(String key) throws IOException;
        void value(ONode value) throws IOException;
    }

    private final JsonReader parser;

    public JsonStreamParser(JsonReader parser) {
        this.parser = parser;
    }

    public void parse(Handler handler) throws IOException {
        ONode root = parser.read();
        traverse(root, handler);
    }

    private void traverse(ONode node, Handler handler) throws IOException {
        if (node.isObject()) {
            handler.startObject();
            for (Map.Entry<String, ONode> entry : node.getObject().entrySet()) {
                handler.key(entry.getKey());
                traverse(entry.getValue(), handler);
            }
            handler.endObject();
        } else if (node.isArray()) {
            handler.startArray();
            for (ONode item : node.getArray()) {
                traverse(item, handler);
            }
            handler.endArray();
        } else {
            handler.value(node);
        }
    }
}