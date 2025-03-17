package org.noear.snack.query;

import org.noear.snack.ONode;
import org.noear.snack.exception.PathResolutionException;

import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.function.Function;

/**
 * @author noear 2025/3/17 created
 */
public class Functions {
    private static final Map<String, Function<ONode, ONode>> lib = new HashMap<>();

    static {
        register("min", Functions::min);
        register("max", Functions::max);
        register("avg", Functions::avg);
        register("sum", Functions::sum);

        register("size", Functions::size);
        register("first", Functions::first);
        register("last", Functions::last);

        register("length", Functions::length);
        register("upper", Functions::upper);
        register("lower", Functions::lower);
        register("trim", Functions::trim);
    }

    /**
     * 注册
     */
    public static void register(String name, Function<ONode, ONode> func) {
        lib.put(name, func);
    }

    /**
     * 获取
     */
    public static Function<ONode, ONode> get(String funcName) {
        return lib.get(funcName);
    }

    /// /////////////////

    static ONode sum(ONode node) {
        if (node.isArray()) {
            double sum = node.getArray().stream()
                    .filter(ONode::isNumber)
                    .mapToDouble(ONode::getDouble)
                    .sum();
            return new ONode(sum);
        } else {
            throw new PathResolutionException("sum() requires an array");
        }
    }


    static ONode min(ONode node) {
        if (node.isArray()) {
            OptionalDouble min = node.getArray().stream()
                    .filter(ONode::isNumber)
                    .mapToDouble(ONode::getDouble)
                    .min();

            return min.isPresent() ? new ONode(min.getAsDouble()) : new ONode(null);
        } else if (node.isNumber()) {
            return node;
        } else {
            throw new PathResolutionException("min() requires array or number");
        }
    }

    static ONode max(ONode node) {
        if (node.isArray()) {
            OptionalDouble max = node.getArray().stream()
                    .filter(ONode::isNumber)
                    .mapToDouble(ONode::getDouble)
                    .max();

            return max.isPresent() ? new ONode(max.getAsDouble()) : new ONode(null);
        } else if (node.isNumber()) {
            return node;
        } else {
            throw new PathResolutionException("max() requires array or number");
        }
    }

    static ONode avg(ONode node) {
        if (!node.isArray()) {
            throw new PathResolutionException("avg() requires an array");
        }

        DoubleSummaryStatistics stats = node.getArray().stream()
                .filter(ONode::isNumber)
                .mapToDouble(ONode::getDouble)
                .summaryStatistics();

        return stats.getCount() > 0 ?
                new ONode(stats.getAverage()) :
                new ONode(null);
    }

    static ONode first(ONode node) {
        if (node.isArray()) {
            return node.get(0);
        } else {
            throw new PathResolutionException("first() requires array");
        }
    }

    static ONode last(ONode node) {
        if (node.isArray()) {
            return node.get(-1);
        } else {
            throw new PathResolutionException("last() requires array");
        }
    }

    static ONode keys(ONode node) {
        if (node.isObject()) {
            return ONode.loadBean(node.getObject().keySet());
        } else {
            throw new PathResolutionException("keys() requires object");
        }
    }

    static ONode size(ONode node) {
        return new ONode(node.size());
    }

    /* 字符串函数实现 */
    static ONode length(ONode node) {
        if (node.isString()) {
            return new ONode(node.getString().length());
        } else if (node.isArray()) {
            return new ONode(node.size());
        } else if (node.isObject()) {
            return new ONode(node.getObject().size());
        }
        return new ONode(0);
    }


    static ONode upper(ONode node) {
        return node.isString() ?
                new ONode(node.getString().toUpperCase()) :
                node;
    }

    static ONode lower(ONode node) {
        return node.isString() ?
                new ONode(node.getString().toLowerCase()) :
                node;
    }

    static ONode trim(ONode node) {
        return node.isString() ?
                new ONode(node.getString().trim()) :
                node;
    }
}