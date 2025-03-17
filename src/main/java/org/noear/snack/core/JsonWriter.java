package org.noear.snack.core;

import org.noear.snack.ONode;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class JsonWriter {
    public static String serialize(ONode node, Options opts) throws IOException {
        StringWriter writer = new StringWriter();
        new JsonWriter(opts, writer).write(node);
        return writer.toString();
    }

    final Options opts;
    final Writer writer;
    int depth = 0;

    public JsonWriter(Options opts, Writer writer) {
        this.opts = opts != null ? opts : Options.def();
        this.writer = writer;
    }

    public void write(ONode node) throws IOException {
        switch (node.getType()) {
            case JsonTypes.TYPE_OBJECT:
                writeObject(node.getObject());
                break;
            case JsonTypes.TYPE_ARRAY:
                writeArray(node.getArray());
                break;
            case JsonTypes.TYPE_STRING:
                writeString(node.getString());
                break;
            case JsonTypes.TYPE_NUMBER:
                if(opts.isFeatureEnabled(Feature.UseBigNumberMode)) {
                    writeString(String.valueOf(node.getValue()));
                }else {
                    writeNumber(node.getNumber());
                }
                break;
            case JsonTypes.TYPE_BOOLEAN:
                writer.write(node.getBoolean() ? "true" : "false");
                break;
            case JsonTypes.TYPE_NULL:
                writer.write("null");
                break;
        }
    }

    private void writeObject(Map<String, ONode> map) throws IOException {
        writer.write('{');
        depth++;
        boolean first = true;
        for (Map.Entry<String, ONode> entry : map.entrySet()) {
            if (opts.isFeatureEnabled(Feature.Output_SkipNullValue) && entry.getValue().isNull()) {
                continue;
            }

            if (!first) {
                writer.write(',');
            }
            writeIndentation();

            String key = opts.isFeatureEnabled(Feature.Output_UseUnderlineStyle) ?
                    toUnderlineName(entry.getKey()) : entry.getKey();
            writeString(key);
            writer.write(':');
            if (opts.isFeatureEnabled(Feature.Output_PrettyFormat)) {
                writer.write(' ');
            }
            write(entry.getValue());
            first = false;
        }
        depth--;
        writeIndentation();
        writer.write('}');
    }

    private void writeArray(List<ONode> list) throws IOException {
        writer.write('[');
        depth++;
        boolean first = true;
        for (ONode item : list) {
            if (!first) {
                writer.write(',');
            }
            writeIndentation();
            write(item);
            first = false;
        }
        depth--;
        writeIndentation();
        writer.write(']');
    }

    private void writeIndentation() throws IOException {
        if (opts.isFeatureEnabled(Feature.Output_PrettyFormat)) {
            writer.write('\n');
            for (int i = 0; i < depth; i++) {
                writer.write(opts.getIndent());
            }
        }
    }

    private void writeNumber(Number num) throws IOException {
        if (opts.isFeatureEnabled(Feature.UseBigNumberMode) && num instanceof Double) {
            writer.write('"' + num.toString() + '"');
        } else {
            writer.write(num.toString());
        }
    }

    private void writeString(String s) throws IOException {
        char quoteChar = opts.isFeatureEnabled(Feature.Output_UseSingleQuotes) ? '\'' : '"';
        writer.write(quoteChar);
        writer.write(escapeString(s, opts));
        writer.write(quoteChar);
    }

    private static String escapeString(String s, Options opts) {
        StringBuilder sb = new StringBuilder(s.length() * 2);
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\'':
                    sb.append("\\'");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < 0x20 || (opts.isFeatureEnabled(Feature.EscapeNonAscii) && c > 0x7F)) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    private String toUnderlineName(String camelName) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camelName.length(); i++) {
            char c = camelName.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    sb.append('_');
                }
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}