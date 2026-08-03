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
package org.noear.snack4.codec;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * 类型前缀安全名单（{@link TypeChecker} 的默认实现）
 *
 * <p>采用前缀匹配：注册 "org.apache.commons." 后，该包下所有类均被拦截。
 * 支持通过 {@link org.noear.snack4.Options} 在全局（静态）或实例两级配置。</p>
 *
 * <p>线程安全：内部使用 {@link CopyOnWriteArrayList}，读多写少场景下无锁。</p>
 *
 * @author noear 2026/8/3 created
 * @since 4.0
 */
public class TypeSafelist implements TypeChecker {
    public static final TypeSafelist GLOBAL = new TypeSafelist().then(bl -> {
        bl.denyAdd("sun.")
                .denyAdd("com.sun.")
                .denyAdd("javax.")
                .denyAdd("jdk.")
                .denyAdd("java.lang.Runtime")
                .denyAdd("java.lang.ProcessBuilder")
                .denyAdd("java.lang.ClassLoader")
                .denyAdd("java.lang.reflect.")
                .denyAdd("java.io.FileOutputStream")
                .denyAdd("java.io.ObjectInputStream")
                .denyAdd("java.io.ObjectOutputStream")
                .denyAdd("java.net.URL")
                .denyAdd("java.net.URLClassLoader")
                .denyAdd("java.rmi.")
                // 第三方反序列化 gadget 常用包（与 fastjson/jackson 默认黑名单对齐）
                .denyAdd("org.apache.commons.collections.")
                .denyAdd("org.apache.commons.collections4.")
                .denyAdd("org.apache.commons.beanutils.")
                .denyAdd("org.springframework.")
                .denyAdd("com.mchange.")
                .denyAdd("org.codehaus.groovy.")
                .denyAdd("org.apache.xbean.")
                .denyAdd("com.alibaba.fastjson.")
                // JDK 侧常用 gadget 链起点 / 桥梁类
                .denyAdd("java.security.SignedObject")
                .denyAdd("java.beans.EventHandler")
                .denyAdd("java.util.PriorityQueue")
                .denyAdd("javax.management.BadAttributeValueExpException");
    });

    /**
     * 拒绝名单
     */
    private final List<String> denyPrefixes = new CopyOnWriteArrayList<>();

    public TypeSafelist() {
    }


    public TypeSafelist denyRemove(String prefix) {
        if (prefix != null) {
            denyPrefixes.remove(prefix);
        }
        return this;
    }

    /**
     * 添加被拦截的类名规则：
     * 包前缀（如 "sun."、"org.apache.commons."）或以 "." 结尾的按包拦截；
     * 精确类名（如 "java.lang.Runtime"）仅拦截该类（含内部类）
     */
    public TypeSafelist denyAdd(String prefix) {
        if (prefix != null && !prefix.isEmpty()) {
            denyPrefixes.add(prefix);
        }
        return this;
    }

    public TypeSafelist denyAddAll(String... prefixes) {
        if (prefixes != null) {
            for (String p : prefixes) {
                denyAdd(p);
            }
        }
        return this;
    }

    public TypeSafelist denyAddAll(Collection<String> prefixes) {
        if (prefixes != null) {
            for (String p : prefixes) {
                denyAdd(p);
            }
        }
        return this;
    }

    @Override
    public Result check(String className) {
        if (className == null) {
            return Result.DENY; //拒绝
        }

        for (String rule : denyPrefixes) {
            if (isMatch(className, rule)) {
                return Result.DENY; //拒绝
            }
        }
        return Result.SKIP;
    }

    /**
     * 规则匹配：
     * 以 "." 结尾的为包前缀（天然包边界）；否则为精确类名
     */
    private static boolean isMatch(String className, String rule) {
        if (rule.endsWith(".")) {
            // 包前缀：startsWith 即按包边界匹配（如 "org.foo." 不会命中 "org.foobar.X"）
            return className.startsWith(rule);
        } else {
            // 精确类名：仅命中自身及其内部类，避免 "java.lang.RuntimeFoo" 类误伤
            return className.equals(rule);
        }
    }

    public TypeSafelist then(Consumer<TypeSafelist> consumer) {
        consumer.accept(this);
        return this;
    }
}