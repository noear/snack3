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
package org.noear.snack4.jsonpath.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Js 正则工具
 *
 * @author noear 2025/10/9 created
 * @since 4.0
 */
public class RegexUtil {
    /**
     * 解析 js 正则
     */
    private static Map<String, Pattern> patternCached = Collections.synchronizedMap(
            new LinkedHashMap<String, Pattern>(512, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<String, Pattern> eldest) {
                    return size() > 512;
                }
            });

    public static Pattern parse(String jsRegex) {
        return patternCached.computeIfAbsent(jsRegex, k -> parseDo(k));
    }

    private static Pattern parseDo(String jsRegex) {
        // 1. 检查输入是否以 / 开头和结尾
        if (!jsRegex.startsWith("/") || !jsRegex.contains("/")) {
            return Pattern.compile(jsRegex);

            //throw new IllegalArgumentException("Invalid JavaScript regex format: " + jsRegex);
        }

        // 2. 分离正则主体和修饰符
        int lastSlashIndex = jsRegex.lastIndexOf('/');
        String regexBody = jsRegex.substring(1, lastSlashIndex);
        String flags = jsRegex.substring(lastSlashIndex + 1);

        // 3. 转换修饰符为 Java 的 Pattern 标志
        int javaFlags = 0;
        for (char flag : flags.toCharArray()) {
            switch (flag) {
                case 'i':
                    javaFlags |= Pattern.CASE_INSENSITIVE;
                    break;
                case 'm':
                    javaFlags |= Pattern.MULTILINE;
                    break;
                case 's':
                    javaFlags |= Pattern.DOTALL;
                    break;
                // 忽略 g（全局匹配），Java 通过 Matcher 循环实现
                case 'g':
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported flag: " + flag);
            }
        }

        // 4. 创建 Pattern
        return Pattern.compile(regexBody, javaFlags);
    }
}