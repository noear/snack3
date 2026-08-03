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
 * 类型前缀黑名单（{@link TypeChecker} 的默认实现）
 *
 * <p>采用前缀匹配：注册 "org.apache.commons." 后，该包下所有类均被拦截。
 * 支持通过 {@link org.noear.snack4.Options} 在全局（静态）或实例两级配置。</p>
 *
 * <p>线程安全：内部使用 {@link CopyOnWriteArrayList}，读多写少场景下无锁。</p>
 *
 * @author noear 2026/8/3 created
 * @since 4.0
 */
public class TypeBlacklist implements TypeChecker {
    public static final TypeBlacklist GLOBAL = new TypeBlacklist().then(bl -> {
        bl.add("sun.")
                .add("com.sun.")
                .add("javax.")
                .add("jdk.")
                .add("java.lang.Runtime")
                .add("java.lang.ProcessBuilder")
                .add("java.lang.ClassLoader")
                .add("java.lang.reflect.")
                .add("java.io.FileOutputStream")
                .add("java.io.ObjectInputStream")
                .add("java.io.ObjectOutputStream")
                .add("java.net.URL")
                .add("java.net.URLClassLoader")
                .add("java.rmi.");
    });

    /**
     * 拒绝名单
     */
    private final List<String> denyPrefixes = new CopyOnWriteArrayList<>();

    public TypeBlacklist() {
    }

    /**
     * 添加被拦截的类名前缀（如 "sun."、"org.apache.commons."）
     */
    public TypeBlacklist add(String prefix) {
        if (prefix != null && !prefix.isEmpty()) {
            denyPrefixes.add(prefix);
        }
        return this;
    }

    public TypeBlacklist addAll(String... prefixes) {
        if (prefixes != null) {
            for (String p : prefixes) {
                add(p);
            }
        }
        return this;
    }

    public TypeBlacklist addAll(Collection<String> prefixes) {
        if (prefixes != null) {
            for (String p : prefixes) {
                add(p);
            }
        }
        return this;
    }

    @Override
    public Result check(String className) {
        if (className == null) {
            return Result.DENY; //拒绝
        }

        for (String prefix : denyPrefixes) {
            if (className.startsWith(prefix)) {
                return Result.DENY; //拒绝
            }
        }
        return Result.SKIP;
    }

    public TypeBlacklist then(Consumer<TypeBlacklist> consumer) {
        consumer.accept(this);
        return this;
    }
}