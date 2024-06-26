package org.noear.snack.core.utils;

public class StringUtil {
    /**
     * 检查字符串是否为null or 空
     */
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /**
     * 是否为数字
     */
    public static boolean isInteger(String str) {
        return isNumberDo(str, false);
    }

    public static boolean isNumber(String str) {
        return isNumberDo(str, true);
    }

    /**
     * 是否为数值（可以是整数 或 小数 或 负数）
     */
    private static boolean isNumberDo(String str, boolean incDot) {
        if (str != null && str.length() != 0) {
            int l = str.length();

            int start = str.charAt(0) != '-' && str.charAt(0) != '+' ? 0 : 1;
            boolean hasDot = false;

            for (int i = start; i < l; ++i) {
                int ch = str.charAt(i);

                if (incDot) {
                    if (ch == 46) {
                        if (hasDot) {
                            return false;
                        } else {
                            hasDot = true;
                            continue;
                        }
                    }
                }

                if (!Character.isDigit(ch)) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
