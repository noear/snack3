package org.noear.snack.core;

import java.util.List;
import java.util.Map;

/**
 * @author noear 2025/3/16 created
 */
public interface JsonTypes {
    int TYPE_NULL = 0;
    int TYPE_BOOLEAN = 1;
    int TYPE_NUMBER = 2;
    int TYPE_STRING = 3;
    int TYPE_ARRAY = 4;
    int TYPE_OBJECT = 5;

    static String getTypeName(int type) {
        switch (type) {
            case JsonTypes.TYPE_NULL:
                return "null";
            case JsonTypes.TYPE_BOOLEAN:
                return "boolean";
            case JsonTypes.TYPE_NUMBER:
                return "number";
            case JsonTypes.TYPE_STRING:
                return "string";
            case JsonTypes.TYPE_ARRAY:
                return "array";
            case JsonTypes.TYPE_OBJECT:
                return "object";
            default:
                return "unknown";
        }
    }

    static boolean isValue(int type) {
        return type > TYPE_NULL && type < TYPE_ARRAY;
    }

    static int resolveType(Object value) {
        if (value == null) return JsonTypes.TYPE_NULL;
        if (value instanceof Boolean) return JsonTypes.TYPE_BOOLEAN;
        if (value instanceof Number) return JsonTypes.TYPE_NUMBER;
        if (value instanceof String) return JsonTypes.TYPE_STRING;
        if (value instanceof List) return JsonTypes.TYPE_ARRAY;
        if (value instanceof Map) return JsonTypes.TYPE_OBJECT;

        throw new IllegalArgumentException("Unsupported type");
    }
}