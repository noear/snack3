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
package org.noear.snack4.json;

import org.noear.snack4.Feature;
import org.noear.snack4.ONode;
import org.noear.snack4.Options;
import org.noear.snack4.json.util.IoUtil;
import org.noear.snack4.json.util.NameUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Json 读取器
 *
 * @author noear noear 2025/3/16 created
 * @since 4.0
 */
public class JsonReader {
    private static final Logger LOG = LoggerFactory.getLogger(JsonReader.class);

    public static ONode read(String json) throws IOException {
        return read(json, null);
    }

    public static ONode read(String json, Options opts) throws IOException {
        return new JsonReader(new StringReader(json), opts).read();
    }

    public static ONode read(Reader reader) throws IOException {
        return new JsonReader(reader, null).read();
    }

    public static ONode read(Reader reader, Options opts) throws IOException {
        return new JsonReader(reader, opts).read();
    }

    /// ///////////////

    private final Options opts;
    private final ParserState state;

    private final StringBuilder stringBuilder;

    private final boolean Read_AllowComment;
    private final boolean Read_DisableUnquotedKeys;
    private final boolean Read_DisableSingleQuotes;
    private final boolean Read_UnwrapJsonString;
    private final boolean Read_TrimString;
    private final boolean Read_ConvertSnakeToCamel;
    private final boolean Read_ConvertCamelToSnake;
    private final boolean Read_AutoRepair;

    private StringBuilder getStringBuilder() {
        stringBuilder.setLength(0);
        return stringBuilder;
    }

    public JsonReader(String json) {
        this(json, null);
    }

    public JsonReader(String json, Options opts) {
        this(new StringReader(json), opts);
    }

    public JsonReader(Reader reader) {
        this(reader, null);
    }

    public JsonReader(Reader reader, Options opts) {
        Objects.requireNonNull(reader, "reader");

        this.opts = opts == null ? Options.DEF_OPTIONS : opts;
        this.stringBuilder = new StringBuilder(32);

        this.Read_AllowComment = this.opts.hasFeature(Feature.Read_AllowComment);
        this.Read_DisableUnquotedKeys = this.opts.hasFeature(Feature.Read_DisableUnquotedKeys);
        this.Read_DisableSingleQuotes = this.opts.hasFeature(Feature.Read_DisableSingleQuotes);
        this.Read_UnwrapJsonString = this.opts.hasFeature(Feature.Read_UnwrapJsonString);
        this.Read_TrimString = this.opts.hasFeature(Feature.Read_TrimString);
        this.Read_ConvertSnakeToCamel = this.opts.hasFeature(Feature.Read_ConvertSnakeToSmlCamel);
        this.Read_ConvertCamelToSnake = this.opts.hasFeature(Feature.Read_ConvertCamelToSmlSnake);
        this.Read_AutoRepair = this.opts.hasFeature(Feature.Read_AutoRepair);


        this.state = new ParserState(reader, Read_AutoRepair);
    }

    public ONode read() throws IOException {
        try {
            state.fillBuffer();
            ONode node = parseValue();
            state.skipWhitespace();

            if (Read_AllowComment) {
                state.skipComments();
            }

            if (!Read_AutoRepair && state.bufferPosition < state.bufferLimit) {
                throw state.error("Unexpected data after json root");
            }
            return node;
        } finally {
            state.reader.close();
        }
    }

    public ONode readTry() throws IOException {
        try {
            state.fillBuffer();
            ONode node = parseValue();
            state.skipWhitespace();

            if (Read_AllowComment) {
                state.skipComments();
            }

            if (!Read_AutoRepair && state.bufferPosition < state.bufferLimit) {
                throw state.error("Unexpected data after json root");
            }
            return node;
        } catch (Throwable e) {
            LOG.debug("Read failure: {}", e.getMessage());
            return new ONode(opts);
        } finally {
            state.reader.close();
        }
    }

    /**
     * 流式读取：不断读出一段完整的 json 并返回 ONode
     *
     * @return 如果没有更多数据，则返回 null
     * @since 4.0.32
     */
    public ONode readNext() {
        try {
            state.skipWhitespace();

            if (Read_AllowComment) {
                state.skipComments();
                state.skipWhitespace();
            }

            // 检查是否已到达流末尾
            if (state.peekChar() == 0) {
                return null;
            }

            // 解析当前位置的一个完整值（Object, Array, String...）
            return parseValue();
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("ReadNext finished with partial content: {}", e.getMessage());
            }

            return null;
        }
    }

    /**
     * 流式读取：跳过中间节点，直接读出当前流中最后一个完整的 json 节点
     *
     * @return 如果没有有效数据，则返回 null
     * @since 4.0.32
     */
    public ONode readLast() {
        ONode last = null, next;
        while ((next = readNext()) != null) {
            last = next;
        }
        return last;
    }

    /**
     * 获取迭代器，以便使用 for-each 遍历 JSON 流
     *
     * @since 4.0.32
     */
    public Iterable<ONode> iterableNext() {
        return () -> new Iterator<ONode>() {
            private ONode nextNode;
            private boolean searched = false;

            @Override
            public boolean hasNext() {
                if (!searched) {
                    nextNode = readNext();
                    searched = true;
                }
                return nextNode != null;
            }

            @Override
            public ONode next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                searched = false;
                return nextNode;
            }
        };
    }

    /**
     * 流式读取：不断读出一段完整的 json 并返回 ONode
     *
     * @return 如果没有更多数据，则返回 null
     * @deprecated 4.0.32
     */
    @Deprecated
    public ONode streamRead() throws IOException {
        return readNext();
    }

    private ONode parseValue() throws IOException {
        state.skipWhitespace();

        if (Read_AllowComment) {
            state.skipComments();
        }

        char c = state.peekChar();

        if (c == '{') return parseObject();
        if (c == '[') return parseArray();
        if (c == '"' || (Read_DisableSingleQuotes == false && c == '\'')) {
            String str = parseString();

            if (Read_UnwrapJsonString) {
                if (str.length() > 1) {
                    char c1 = str.charAt(0);
                    char c2 = str.charAt(str.length() - 1);
                    if ((c1 == '{' && c2 == '}') || (c1 == '[' && c2 == ']')) {
                        return ONode.ofJson(str, opts);
                    }
                }
            }

            if (Read_TrimString) {
                if (str.length() > 1) {
                    str = str.trim();
                }
            }

            return new ONode(opts, str);
        }
        // 新增的 JavaScript Date 对象支持
        if (c == 'n' && state.peekChar(1) == 'e' && state.peekChar(2) == 'w') {
            return parseDate();
        }

        if (c == '-' || c == '+' || (c >= '0' && c <= '9')) return new ONode(opts, parseNumber());
        if (c == 't') return parseKeyword("true", true);
        if (c == 'f') return parseKeyword("false", false);
        if (c == 'n') return parseKeyword("null", null);
        if (c == 'N') return parseKeyword("NaN", null);
        if (c == 'u') return parseKeyword("undefined", null);

        if (Read_AutoRepair) {
            state.nextChar(); // 消费掉无法识别的字符，防止 readLast() 死循环
            return new ONode(opts);
        } else {
            throw state.error("Unexpected character: " + c);
        }
    }

    /**
     * 解析 JavaScript Date 对象: new Date(long)
     *
     * @return ONode (Date)
     * @throws IOException
     */
    private ONode parseDate() throws IOException {
        // 期望 "new Date("
        state.expect('n');
        state.expect('e');
        state.expect('w');
        state.skipWhitespace(); // 允许 'new' 后有空格
        state.expect('D');
        state.expect('a');
        state.expect('t');
        state.expect('e');
        state.expect('(');
        state.skipWhitespace();

        // 🌟 优化点：使用 parseNumber() 解析时间戳
        Number number = parseNumber();

        if (number == null) {
            return new ONode(opts);
        }

        // 确保数字是 Long 类型或可以安全转换为 Long
        long timestamp;
        if (number instanceof Long) {
            timestamp = (Long) number;
        } else {
            // 如果是 Double/BigDecimal，可能丢失精度，但此处需做出决定
            timestamp = number.longValue();
        }

        state.skipWhitespace();
        state.expect(')'); // 期望 ')'

        return new ONode(opts, new Date(timestamp));
    }

    private ONode parseObject() throws IOException {
        Map<String, ONode> map = opts.createMap();
        state.expect('{');
        while (true) {
            state.skipWhitespace();
            char c = state.peekChar();
            if (c == 0) {
                // 增加对 EOF 的支持
                break;
            }
            if (c == '}') {
                state.bufferPosition++;
                break;
            }

            String key = parseKey();

            if (key.isEmpty() && opts.hasFeature(Feature.Read_AllowEmptyKeys) == false) {
                throw new JsonParseException("Empty key is not allowed");
            }

            state.skipWhitespace();

            if (!state.expect(':')) {
                break;
            }

            ONode value = parseValue();
            map.put(key, value);

            state.skipWhitespace();
            if (state.peekChar() == ',') {
                state.bufferPosition++;
                state.skipWhitespace();
                if (state.peekChar() == '}') {
                    if (Read_AutoRepair) {
                        break;
                    } else {
                        throw state.error("Trailing comma in object");
                    }
                }
            } else if (state.peekChar() == '}') {
                // Continue to closing
            } else {
                if (Read_AutoRepair) {
                    break;
                } else {
                    throw state.error("Expected ',' or '}'");
                }
            }
        }

        return new ONode(opts, map);
    }

    private String parseKey() throws IOException {
        String key;
        if (Read_DisableUnquotedKeys == false) {
            char c = state.peekChar();
            if (c != '"' && c != '\'') {
                key = parseUnquotedString();
            } else {
                key = parseString();
            }
        } else {
            key = parseString();
        }

        // 如果启用了蛇形转驼峰特性，则进行转换
        if (Read_ConvertSnakeToCamel) {
            key = NameUtil.toSmlCamelStyle(getStringBuilder(), key);
        } else if (Read_ConvertCamelToSnake) {
            key = NameUtil.toSmlSnakeStyle(getStringBuilder(), key);
        }

        return key;
    }

    private String parseUnquotedString() throws IOException {
        StringBuilder sb = getStringBuilder();
        while (true) {
            char c = state.peekChar();
            if (c == ':' || c == ',' || c == '}' || c == ']' || Character.isWhitespace(c)) {
                break;
            }
            sb.append(state.nextChar());
        }
        return sb.toString();
    }

    private ONode parseArray() throws IOException {
        List<ONode> list = opts.createList();
        state.expect('[');
        while (true) {
            state.skipWhitespace();
            if (state.peekChar() == ']') {
                state.bufferPosition++;
                break;
            }

            ONode tmp = parseValue();

            if (tmp.isUndefined() == false) {
                list.add(tmp);
            }

            state.skipWhitespace();
            if (state.peekChar() == ',') {
                state.bufferPosition++;
                state.skipWhitespace();
                if (state.peekChar() == ']') {
                    if (Read_AutoRepair) {
                        break;
                    } else {
                        throw state.error("Trailing comma in array");
                    }
                }
            } else if (state.peekChar() == ']') {
                // Continue to closing
            } else {
                if (Read_AutoRepair) {
                    break;
                } else {
                    throw state.error("Expected ',' or ']'");
                }
            }
        }
        return new ONode(opts, list);
    }

    private String parseString() throws IOException {
        char quoteChar = state.nextChar();
        if (quoteChar != '"' && !(Read_DisableSingleQuotes == false && quoteChar == '\'')) {
            throw state.error("Expected string to start with a quote");
        }

        StringBuilder sb = getStringBuilder();

        // 性能优化：在缓冲区内进行批量复制，减少 nextChar() 和单字符 append() 的调用
        while (true) {
            // 确保缓冲区有内容
            if (state.bufferPosition >= state.bufferLimit) {
                if (!state.fillBuffer()) {
                    if (Read_AutoRepair) {
                        break;
                    } else {
                        throw state.error("Unclosed string");
                    }
                }
            }

            int start = state.bufferPosition;
            int end = start;

            // 在当前缓冲区内快速扫描，直到遇到：
            // 1. 结束引号 (quoteChar)
            // 2. 转义字符 (\)
            // 3. 控制字符 (c < 0x20)
            while (end < state.bufferLimit) {
                char c = state.buffer[end];
                if (c == quoteChar || c == '\\' || c < 32) { //0x20
                    break;
                }
                end++;
            }

            // 1. 批量添加字符到 StringBuilder
            if (end > start) {
                // 使用 StringBuilder 的批量追加方法，性能更高
                sb.append(state.buffer, start, end - start);
            }

            // 2. 更新位置
            state.bufferPosition = end;

            // 3. 处理终止条件
            if (state.bufferPosition == state.bufferLimit) {
                // 如果是因为 buffer 耗尽而退出，继续下一轮 fillBuffer()
                continue;
            }

            // 4. 处理特殊字符 (必须使用 nextChar() 来正确消耗和更新位置)
            char c = state.nextChar();

            if (c == quoteChar) {
                break; // 遇到结束引号，解析完成
            }

            if (c == '\\') {
                // 处理转义字符
                c = state.nextChar();
                switch (c) {
                    case '"':
                    case '\'':
                    case '\\':
                        sb.append(c);
                        break;
                    case '/':
                        sb.append('/');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'u': {
                        int val = 0;
                        for (int i = 0; i < 4; i++) {
                            char hex_char = state.nextChar();
                            val <<= 4; // val = val * 16
                            if (hex_char >= '0' && hex_char <= '9') {
                                val += hex_char - '0';
                            } else if (hex_char >= 'a' && hex_char <= 'f') {
                                val += hex_char - 'a' + 10;
                            } else if (hex_char >= 'A' && hex_char <= 'F') {
                                val += hex_char - 'A' + 10;
                            } else {
                                if (Read_AutoRepair) {
                                    break;
                                } else {
                                    throw state.error("Invalid Unicode escape");
                                }
                            }
                        }
                        sb.append((char) val);
                        break;
                    }
                    default: {
                        // 兼容旧的八进制转义（虽然 JSON 不支持）
                        if (c >= '0' && c <= '7') {
                            // 假设 IoUtil.CHARS_MARK_REV 存在并能正确映射
                            sb.append(IoUtil.CHARS_MARK_REV[(int) c]);
                        } else if (opts.hasFeature(Feature.Read_AllowInvalidEscapeCharacter)) {
                            sb.append(c); // 忽略转义，直接追加字符
                        } else if (opts.hasFeature(Feature.Read_AllowBackslashEscapingAnyCharacter)) {
                            // 允许 \X 形式的任意转义，追加 \ 和 X
                            sb.append('\\').append(c);
                        } else {
                            if (Read_AutoRepair) {
                                break;
                            } else {
                                // 严格模式 (RFC 8259)
                                throw state.error("Invalid escape character: \\" + c);
                            }
                        }
                    }
                }
            } else if (c < 32) { //0x20
                // 处理未转义的控制字符
                if (opts.hasFeature(Feature.Read_AllowUnescapedControlCharacters)) {
                    // 允许追加
                    sb.append(c);
                } else {
                    if (Read_AutoRepair) {
                        //自动修正
                        char mapping = IoUtil.CHARS_MARK[c];
                        if (mapping > 0) {
                            sb.append(IoUtil.CHARS_MARK_REV[mapping]);
                        } else {
                            sb.append(c);
                        }
                    } else {
                        // 严格模式
                        throw state.error("Unescaped control character: 0x" + Integer.toHexString(c));
                    }
                }
            } else {
                // 理论上，在块复制逻辑中，普通字符已经被处理了，
                // 除非 nextChar() 意外读取了一个普通字符，但为了安全保留。
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private Number parseNumber() throws IOException {
        StringBuilder sb = getStringBuilder();
        char c = state.peekChar();

        // 处理正号
        if (c == '+') {
            //跳过
            state.nextChar();
            c = state.peekChar();
        }

        // 处理负数
        if (c == '-') {
            sb.append(state.nextChar());
        }

        // 解析整数部分
        if (opts.hasFeature(Feature.Read_AllowZeroLeadingNumbers) == false) {
            if (state.peekChar() == '0') {
                sb.append(state.nextChar());
                if (isDigit(state.peekChar())) {
                    throw state.error("Leading zeros not allowed");
                }
            }
        }

        if (isDigit(state.peekChar())) {
            while (isDigit(state.peekChar())) {
                sb.append(state.nextChar());
            }
        } else if (sb.length() == 0) {
            if (Read_AutoRepair) {
                return null;
            } else {
                throw state.error("Invalid number format");
            }
        }

        // 解析小数部分
        if (state.peekChar() == '.') {
            sb.append(state.nextChar());
            if (!isDigit(state.peekChar())) {
                if (Read_AutoRepair == false) {
                    throw state.error("Invalid decimal format");
                }
            } else {
                while (isDigit(state.peekChar())) {
                    sb.append(state.nextChar());
                }
            }
        }

        // 解析指数部分
        if (state.peekChar() == 'e' || state.peekChar() == 'E') {
            sb.append(state.nextChar());

            if (state.peekChar() == '+' || state.peekChar() == '-') {
                sb.append(state.nextChar());
            }

            if (!isDigit(state.peekChar())) {
                if (Read_AutoRepair) {
                    char c2 = sb.charAt(sb.length() - 1);

                    if (c2 == '+' || c2 == '-') {
                        sb.setLength(sb.length() - 2);
                    } else {
                        sb.setLength(sb.length() - 1);
                    }
                } else {
                    throw state.error("Invalid exponent format");
                }
            } else {
                while (isDigit(state.peekChar())) {
                    sb.append(state.nextChar());
                }
            }
        }

        // 检查类型后缀 (L, F, D, M) - 但不添加到字符串中
        char postfix = 0;
        if (state.peekChar() == 'L' || state.peekChar() == 'F' || state.peekChar() == 'D' || state.peekChar() == 'M') {
            postfix = state.nextChar(); // 读取后缀但不添加到sb中
        }

        String numStr = sb.toString();
        if (Read_AutoRepair) {
            // 如果以小数点或正负号结尾，则清理掉非法末尾
            if (numStr.endsWith(".") || numStr.endsWith("-") || numStr.endsWith("+")) {
                numStr = numStr.substring(0, numStr.length() - 1);
            }
        }
        if (numStr.isEmpty()) {
            return 0;
        }

        try {
            // 根据后缀类型解析数字
            if (postfix == 'D') {
                return Double.parseDouble(numStr);
            } else if (postfix == 'F') {
                return Float.parseFloat(numStr);
            } else if (postfix == 'L') {
                return Long.parseLong(numStr);
            } else {
                if (numStr.indexOf('.') >= 0 || numStr.indexOf('e') >= 0 || numStr.indexOf('E') >= 0) {
                    if (numStr.length() > 19 || opts.hasFeature(Feature.Read_UseBigDecimalMode)) {
                        return new BigDecimal(numStr);
                    } else {
                        return Double.parseDouble(numStr);
                    }
                } else {
                    if (numStr.length() > 19 || opts.hasFeature(Feature.Read_UseBigIntegerMode)) {
                        return new BigInteger(numStr);
                    } else {
                        long longVal = Long.parseLong(numStr);
                        if (longVal <= Integer.MAX_VALUE && longVal >= Integer.MIN_VALUE) {
                            return (int) longVal;
                        }
                        return longVal;
                    }
                }
            }
        } catch (NumberFormatException e) {
            throw state.error("Invalid number: " + numStr);
        }
    }

    private ONode parseKeyword(String expect, Object value) throws IOException {
        // 逐个字符验证，避免数组越界
        for (int i = 0; i < expect.length(); i++) {
            char expectedChar = expect.charAt(i);
            char actualChar = state.nextChar(); // 使用 nextChar 确保自动处理缓冲区填充

            if (actualChar != expectedChar) {
                if (Read_AutoRepair) {
                    break;
                } else {
                    throw state.error("Unexpected keyword: expected '" + expect + "'");
                }
            }
        }

        return new ONode(opts, value);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    static class ParserState {
        private static final int BUFFER_SIZE = 8192;
        private final Reader reader;
        private final boolean autoRepair;

        private long line = 1;
        private long column = 0;

        private final char[] buffer = new char[BUFFER_SIZE];
        private int bufferPosition;
        private int bufferLimit;

        public ParserState(Reader reader, boolean autoRepair) {
            this.reader = reader;
            this.autoRepair = autoRepair;
        }

        private char nextChar() throws IOException {
            if (bufferPosition >= bufferLimit && !fillBuffer()) {
                if (autoRepair) {
                    return 0;
                } else {
                    throw error("Unexpected end of input");
                }
            }

            char c = buffer[bufferPosition++];

            // 集中处理行/列计数
            if (c == '\n') {
                line++;
                column = 0;
            } else if (c == '\r') {
                // 检查 Windows 换行符 \r\n
                if (peekChar() == '\n') {
                    bufferPosition++; // 跳过下一个 \n
                }
                line++;
                column = 0;
            } else {
                column++;
            }

            return c;
        }

        private char peekChar() throws IOException {
            return peekChar(0);
        }

        private char peekChar(int offset) throws IOException {
            if (bufferPosition + offset >= bufferLimit && !fillBuffer()) {
                return 0;
            }
            return (bufferPosition + offset < bufferLimit) ? buffer[bufferPosition + offset] : 0;
        }

        private boolean fillBuffer() throws IOException {
            if (bufferPosition < bufferLimit) return true;
            bufferLimit = reader.read(buffer);
            bufferPosition = 0;
            return bufferLimit > 0;
        }

        private boolean expect(char expected) throws IOException {
            char c = nextChar();
            if (c != expected) {
                if (autoRepair) {
                    return false;
                } else {
                    throw error("Expected '" + expected + "' but found '" + c + "'");
                }
            } else {
                return true;
            }
        }

        private JsonParseException error(String message) {
            return new JsonParseException(message + " at line " + line + " column " + column);
        }

        private void skipWhitespace() throws IOException {
            while (true) {
                if (bufferPosition >= bufferLimit && !fillBuffer()) {
                    return; // 文件结束
                }

                char c = buffer[bufferPosition];
                if ((c < 32 || c == ' ' || c == '\t' || c == '\n' || c == '\r')) {
                    nextChar(); // 使用 nextChar() 确保行/列计数正确
                } else {
                    break;
                }
            }
        }

        private void skipComments() throws IOException {
            char c = peekChar();
            if (c == '/') {
                bufferPosition++;
                char next = peekChar();
                if (next == '/') {
                    skipLineComment();
                } else if (next == '*') {
                    skipBlockComment();
                }
            }
        }

        private void skipLineComment() throws IOException {
            while (true) {
                if (bufferPosition >= bufferLimit && !fillBuffer()) break;
                char c = buffer[bufferPosition];
                if (c == '\n') {
                    line++;
                    column = 0;
                    bufferPosition++;
                    break;
                }
                bufferPosition++;
                column++;
            }
        }

        private void skipBlockComment() throws IOException {
            bufferPosition++; // 跳过起始的 '/'
            boolean closed = false;
            while (true) {
                if (bufferPosition >= bufferLimit && !fillBuffer()) {
                    break;
                }
                char c = buffer[bufferPosition++];
                if (c == '\n') {
                    line++;
                    column = 0;
                } else if (c == '\r') {
                    if (peekChar() == '\n') bufferPosition++;
                    line++;
                    column = 0;
                } else {
                    column++;
                }

                if (c == '*' && peekChar() == '/') {
                    bufferPosition++;
                    closed = true;
                    break;
                }
            }
            if (!closed) {
                throw error("Unclosed block comment");
            }
        }
    }
}