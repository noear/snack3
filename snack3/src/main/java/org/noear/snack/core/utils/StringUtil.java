package org.noear.snack.core.utils;

public class StringUtil {
    /**
     * 检查字符串是否为null or 空
     */
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /**
     * 是否为整型
     */
    public static boolean isInteger(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        int start = 0;
        if (str.charAt(0) == '-' || str.charAt(0) == '+') {
            if (str.length() == 1) {
                return false;
            }
            start = 1;
        }

        for (int i = start; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否为数字
     * */
    public static boolean isNumber(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        str = str.trim();
        int length = str.length();
        boolean hasDigit = false;
        boolean hasDot = false;
        boolean hasExp = false;
        boolean hasSign = false;

        for (int i = 0; i < length; i++) {
            char c = str.charAt(i);

            if (c >= '0' && c <= '9') {
                hasDigit = true;
            } else if (c == '.') {
                // 小数点不能出现在指数部分或多次出现
                if (hasDot || hasExp) {
                    return false;
                }
                hasDot = true;
            } else if (c == 'e' || c == 'E') {
                // 指数符号前必须有数字且不能重复
                if (!hasDigit || hasExp) {
                    return false;
                }
                hasExp = true;
                hasDigit = false; // 重置，要求指数部分必须有数字
            } else if (c == '+' || c == '-') {
                // 符号只能出现在开头或指数符号后
                if (i != 0 && str.charAt(i - 1) != 'e' && str.charAt(i - 1) != 'E') {
                    return false;
                }
                hasSign = true;
            } else {
                return false; // 非法字符
            }
        }

        // 必须有数字且最后一个字符不能是e/E或+/-
        return hasDigit &&
                !(str.charAt(length - 1) == 'e' ||
                        str.charAt(length - 1) == 'E' ||
                        str.charAt(length - 1) == '+' ||
                        str.charAt(length - 1) == '-');
    }
}
