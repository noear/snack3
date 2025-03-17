package org.noear.snack.stream;

import org.noear.snack.ONode;

import java.io.IOException;
/**
 * 流式处理回调接口
 */
public interface JsonStreamHandler {
    void startObject() throws IOException;
    void endObject() throws IOException;
    void startArray() throws IOException;
    void endArray() throws IOException;
    void key(String key) throws IOException;
    void value(ONode value) throws IOException;
}