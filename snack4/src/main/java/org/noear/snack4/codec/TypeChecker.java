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

/**
 * 类型安全检测器
 *
 * <p>用于定制反序列化过程中的类型拦截策略（黑名单 / 白名单 / 审计等）</p>
 *
 * @author noear 2026/8/3 created
 * @since 4.0
 */
@FunctionalInterface
public interface TypeChecker {

    /**
     * 检测给定的类名
     *
     * @param className 待检测的完整类名（如 "org.apache.commons.collections.Transformer"）
     * @return 检测结果：{@link Result#ALLOW} 放行、{@link Result#DENY} 拒绝、{@link Result#SKIP} 跳过（交由后续检测器决定）
     */
    Result check(String className);

    enum Result {
        /**
         * 放行
         */
        ALLOW,
        /**
         * 拒绝
         */
        DENY,
        /**
         * 跳过
         */
        SKIP
    }
}