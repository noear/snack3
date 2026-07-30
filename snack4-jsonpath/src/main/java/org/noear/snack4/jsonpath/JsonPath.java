/*
 * Copyright 2005-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.snack4.jsonpath;

import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.jsonpath.segment.DescendantSegment;
import org.noear.snack4.jsonpath.segment.Segment;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * JsonPath
 *
 * @author noear
 * @since 4.0
 */
public class JsonPath {
    private final String expression;
    private final List<Segment> segments;
    private final boolean rooted;

    public JsonPath(String expression, List<Segment> segments) {
        this.expression = expression;
        this.segments = segments;
        this.rooted = expression.charAt(0) == '$';
    }

    public boolean isRooted() {
        return rooted;
    }

    public String getExpression() {
        return expression;
    }

    public List<Segment> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    /**
     * 获取段数量
     */
    public int getSegmentCount() {
        return segments.size();
    }

    /**
     * 截取前 level 段生成子路径
     *
     * @param level 段数（1 表示第一个段，2 表示前两个段，以此类推）
     * @return 由前 level 段构成的新 JsonPath
     */
    public JsonPath subPath(int level) {
        if (level <= 0) {
            throw new IllegalArgumentException("level must be > 0");
        }
        if (level > segments.size()) {
            throw new IllegalArgumentException(
                    "level " + level + " exceeds segment count " + segments.size());
        }

        // 安全检查：DescendantSegment 不能作为最后一段
        if (segments.get(level - 1) instanceof DescendantSegment) {
            throw new IllegalArgumentException(
                    "Cannot subPath at level " + level + ": DescendantSegment ('..') cannot be the last segment");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(rooted ? "$" : "@");

        for (int i = 0; i < level; i++) {
            Segment seg = segments.get(i);
            String text = seg.getOriginalText();

            // 拼接优化：如果前一段是 DescendantSegment("..")，
            // 且当前段的 originalText 以 "." 开头，
            // 则去掉重复的 "."，因为 ".." 已经包含了过渡符
            if (i > 0 && segments.get(i - 1) instanceof DescendantSegment
                    && text != null && text.startsWith(".")) {
                sb.append(text, 1, text.length());
            } else if (text != null) {
                sb.append(text);
            }
        }

        return parse(sb.toString());
    }

    @Override
    public String toString() {
        return "JsonPath{" +
                "path='" + expression + '\'' +
                ", segments=" + segments +
                '}';
    }

    /**
     * 执行
     */
    protected QueryResult evaluate(QueryContextImpl ctx, ONode root) {
        List<ONode> currentNodes = Collections.singletonList(root);

        for (Segment seg : segments) {
            currentNodes = seg.resolve(ctx, currentNodes);
            ctx.tailafter(seg);
        }

        return new QueryResult(ctx, currentNodes);
    }

    public QueryResult select(ONode root) {
        QueryContextImpl ctx = new QueryContextImpl(root, QueryMode.SELECT);

        try {
            return evaluate(ctx, root);
        } catch (Throwable ex) {
            if (ctx.hasFeature(Feature.JsonPath_SuppressExceptions)) {
                return new QueryResult(ctx, null);
            } else {
                throw ex;
            }
        }
    }

    public QueryResult create(ONode root) {
        QueryContextImpl ctx = new QueryContextImpl(root, QueryMode.CREATE);

        try {
            if (expression.contains("..")) {
                throw new JsonPathException("The create mode not support descendant selector");
            }

            return evaluate(ctx, root);
        } catch (Throwable ex) {
            if (ctx.hasFeature(Feature.JsonPath_SuppressExceptions)) {
                return new QueryResult(ctx, null);
            } else {
                throw ex;
            }
        }
    }

    public boolean delete(ONode root) {
        QueryContextImpl ctx = new QueryContextImpl(root, QueryMode.DELETE);

        try {
            boolean deleted = false;
            QueryResult result = evaluate(ctx, root);

            for (ONode n1 : result.getNodeList()) {
                if (n1.source != null) {
                    n1.delete();
                    deleted = true;
                }
            }

            return deleted;
        } catch (Throwable ex) {
            if (ctx.hasFeature(Feature.JsonPath_SuppressExceptions)) {
                //...
                return false;
            } else {
                throw ex;
            }
        }
    }

    /// //////////


    private static Map<String, JsonPath> cached = Collections.synchronizedMap(
            new LinkedHashMap<String, JsonPath>(512, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, JsonPath> eldest) {
                    return size() > 512;
                }
            });

    /**
     * 解析
     */
    public static JsonPath parse(String path) {
        if (!path.startsWith("$") && !path.startsWith("@")) {
            throw new JsonPathException("Path must start with $");
        }

        return cached.computeIfAbsent(path, JsonPathParser::parse);
    }

    /**
     * 根据 jsonpath 查询
     */
    public static ONode select(String json, String path) {
        return select(ONode.ofJson(json), path);
    }

    /**
     * 根据 jsonpath 查询
     */
    public static ONode select(ONode root, String path) {
        return parse(path).select(root).asNode();
    }

    /**
     * 根据 jsonpath 查询
     */
    public static boolean exists(ONode root, String path) {
        return parse(path).select(root).isEmpty() == false;
    }

    /**
     * 根据 jsonpath 生成
     */
    public static ONode create(ONode root, String path) {
        return parse(path).create(root).asNode();
    }

    /**
     * 根据 jsonpath 删除
     */
    public static boolean delete(ONode root, String path) {
        return parse(path).delete(root);
    }
}