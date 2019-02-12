package org.near.snack3.core.utils;

/**
 * IO工具类
 * */
public final class IOUtil {
    public final static char EOI = 0; // 0x1A;
    public final static char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    public final static int[] DIGITS_MARK = new int[(int)'f'+1];
    public final static char[] CHARS_MARK = new char[93];
    public final static char[] CHARS_MARK_REV = new char[120];
    static {
        for (int i = '0'; i <= '9'; ++i) {
            DIGITS_MARK[i] = i - '0';
        }

        for (int i = 'a'; i <= 'f'; ++i) {
            DIGITS_MARK[i] = (i - 'a') + 10;
        }
        for (int i = 'A'; i <= 'F'; ++i) {
            DIGITS_MARK[i] = (i - 'A') + 10;
        }

        CHARS_MARK['\0'] = '0';
        CHARS_MARK['\1'] = '1';
        CHARS_MARK['\2'] = '2';
        CHARS_MARK['\3'] = '3';
        CHARS_MARK['\4'] = '4';
        CHARS_MARK['\5'] = '5';
        CHARS_MARK['\6'] = '6';
        CHARS_MARK['\7'] = '7';
        CHARS_MARK['\b'] = 'b'; // 8
        CHARS_MARK['\t'] = 't'; // 9
        CHARS_MARK['\n'] = 'n'; // 10
        CHARS_MARK['\u000B'] = 'v'; // 11
        CHARS_MARK['\f'] = 'f'; // 12
        CHARS_MARK['\r'] = 'r'; // 13
        CHARS_MARK['\"'] = '"'; // 34
        CHARS_MARK['\''] = '\''; // 39
        CHARS_MARK['/'] = '/'; // 47
        CHARS_MARK['\\'] = '\\'; // 92

        CHARS_MARK_REV['0'] = '\0'; //48
        CHARS_MARK_REV['1'] = '\1';
        CHARS_MARK_REV['2'] = '\2';
        CHARS_MARK_REV['3'] = '\3';
        CHARS_MARK_REV['4'] = '\4';
        CHARS_MARK_REV['5'] = '\5';
        CHARS_MARK_REV['6'] = '\6';
        CHARS_MARK_REV['7'] = '\7'; //55
        CHARS_MARK_REV['b'] = '\b'; // 98
        CHARS_MARK_REV['t'] = '\t'; // 116
        CHARS_MARK_REV['n'] = '\n'; // 110
        CHARS_MARK_REV['v'] = '\u000B'; // 76
        CHARS_MARK_REV['f'] = '\f'; // 102
        CHARS_MARK_REV['r'] = '\r'; // 114
        CHARS_MARK_REV['"'] = '\"'; // 34
        CHARS_MARK_REV['\''] = '\''; // 39
        CHARS_MARK_REV['/'] = '/'; // 47
        CHARS_MARK_REV['\\'] = '\\'; // 92
    }
}
