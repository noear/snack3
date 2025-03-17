package org.noear.snack.core;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * JSON路径追踪器，用于记录当前JSON节点的访问路径
 */
public class PathTracker {
    private final Deque<String> stack = new ArrayDeque<>();

    /** 初始化根路径为$ */
    public PathTracker() {
        stack.push("$");
    }

    /** 进入对象属性 */
    public void enterProperty(String property) {
        String current = stack.peek();
        stack.push(current + "." + property);
    }

    /** 进入数组索引 */
    public void enterIndex(int arrayIndex) {
        String current = stack.peek();
        stack.push(current + "[" + arrayIndex + "]");
    }

    /** 获取当前路径 */
    public String currentPath() {
        return stack.peek();
    }

    /** 退出当前层级 */
    public void exit() {
        if (stack.size() > 1) {
            stack.pop();
        }
    }

    /** 创建新实例 */
    public static PathTracker begin() {
        return new PathTracker();
    }
}