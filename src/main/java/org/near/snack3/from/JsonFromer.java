package org.near.snack3.from;

import org.near.snack3.ONode;
import org.near.snack3.OValue;
import org.near.snack3.core.*;
import org.near.snack3.core.exts.CharBuffer;
import org.near.snack3.core.exts.CharReader;
import org.near.snack3.core.exts.ThData;
import org.near.snack3.core.utils.IOUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * Json 解析器（将 json string 转为 ONode）
 * key：支持双引号、单引号、无引号
 * str：支持双引号、单引号
 */
public class JsonFromer implements Fromer {
    private static final ThData<Queue<ONode>> tlBases = new ThData<>(Collections.asLifoQueue(new ArrayDeque<>()));
    private static final ThData<Queue<String>> tlKeys = new ThData<>(Collections.asLifoQueue(new ArrayDeque<>()));
    private static final ThData<CharBuffer> tlBuilder = new ThData<>(new CharBuffer());

    @Override
    public void handle(Context ctx) throws IOException {
        int len = ctx.text.length();

        //完整的处理（支持像："xx",'xx',12,true,{...},[],null,undefined 等）
        //
        if (len == 0) {
            ctx.node = new ONode();
        } else {
            char prefix = ctx.text.charAt(0);

            if (prefix == '{' || prefix == '[') { //object or array
                ctx.node = analyse(new CharReader(ctx.text));
            } else if (len >= 2 && (prefix == '"' || prefix == '\'')) {//string
                ctx.node = analyse_val(ctx.text.substring(1, len - 1), true);
            } else if (prefix != '<' && len < 40) {//null,num,bool,other
                ctx.node = analyse_val(ctx.text, false);
            }
        }

        if (null != ctx.node) {
            ctx.handled = true;
        }
    }

    public ONode analyse(CharReader sr) throws IOException {
        // 用来存放上级对象/数组的栈
        Queue<ONode> bases = tlBases.get();
        bases.clear();

        // 用来存放键名的栈
        Queue<String> keys = tlKeys.get();
        keys.clear();

        // 当前读到的元素("k":"v" 或者 "v")
        CharBuffer sBuf = tlBuilder.get();
        sBuf.setLength(0);

        // 读入字符
        while (sr.read()) {
            char c = sr.value();

            // 根据字符
            switch (c) {
                case '"':
                case '\'':
                    scanString(sr, sBuf, c);
                    break;

                case '{':
                    bases.offer(new ONode().asObject());
                    break;

                case '[':
                    bases.offer(new ONode().asArray());
                    break;

                case ':':
                    // 新的键名
                    keys.offer(sBuf.toString());
                    sBuf.setLength(0);
                    break;

                case ',':
                    // 值
                    if (sBuf.length() > 0) {
                        ONode base = bases.peek();
                        if (base.isArray()) {
                            base.add(analyse_val(sBuf));
                        } else if (base.isObject()) {
                            base.set(keys.poll(), analyse_val(sBuf));
                        }
                        sBuf.setLength(0);
                    }
                    break;

                case '}':
                    // 这是它自己
                    ONode self_object = bases.poll();
                    // 把最后一个元素加进去
                    if (sBuf.length() > 0) {
                        self_object.set(keys.poll(), analyse_val(sBuf));
                        sBuf.setLength(0);
                    }
                    // 如果有父级
                    if (!bases.isEmpty()) {
                        ONode base = bases.peek();
                        if (base.isArray()) {
                            base.add(self_object);
                        } else if (base.isObject()) {
                            base.set(keys.poll(), self_object);
                        }
                    } else {
                        return self_object;
                    }
                    break;

                case ']':
                    // 这是它自己
                    ONode self_array = bases.poll();
                    // 把最后一个元素加进去
                    if (sBuf.length() > 0) {
                        self_array.add(analyse_val(sBuf));
                        sBuf.setLength(0);
                    }
                    // 如果有父级
                    if (!bases.isEmpty()) {
                        ONode base = bases.peek();
                        if (base.isArray()) {
                            base.add(self_array);
                        } else if (base.isObject()) {
                            base.set(keys.poll(), self_array);
                        }
                    } else {
                        return self_array;
                    }
                    break;

                default:
                    if (sBuf.length() == 0) { //支持：new Date(xxx) //当中有空隔
                        if (c > 32) {//无引号的，只添加可见字符(key,no string val)
                            sBuf.append(c);
                        }
                    } else {
                        sBuf.append(c);
                    }
                    break;

            }

        }

        return null;

    }

    private void scanString(CharReader sr, CharBuffer sBuf, char quote) throws IOException {
        //没有包括引号，不需要删除动作
        sBuf.isString = true;

        while (sr.read()) {
            char c = sr.value();

            if (quote == c) {
                return;
            }

            if ('\\' == c) {
                c = sr.next();

                if ('t' == c || 'r' == c || 'n' == c || 'f' == c || 'b' == c || '"' == c || '\'' == c || (c >= '0' && c <= '7')) {
                    sBuf.append(IOUtil.CHARS_MARK_REV[(int) c]);
                    continue;
                }

                if ('x' == c) {
                    //16进制的处理。
                    char x1 = sr.next();
                    char x2 = sr.next();

                    int val = IOUtil.DIGITS_MARK[x1] * 16 + IOUtil.DIGITS_MARK[x2];
                    sBuf.append((char) val);
                    continue;
                }

                if ('u' == c) {
                    int val = 0;
                    //unicode的处理。对于码值大于0xFFFF的字符，仅支持\ud83d\udc4c这样4字节表示方法，不支持\u1f44c这样的两个半字节表示法
                    c = sr.next();
                    val = ((val << 4) + IOUtil.DIGITS_MARK[c]); //Character.digit(c, 16)
                    c = sr.next();
                    val = ((val << 4) + IOUtil.DIGITS_MARK[c]);
                    c = sr.next();
                    val = ((val << 4) + IOUtil.DIGITS_MARK[c]);
                    c = sr.next();
                    val = ((val << 4) + IOUtil.DIGITS_MARK[c]);
                    sBuf.append((char) val);
                    continue;
                }

                sBuf.append('\\');
                sBuf.append(c);
            } else {
                sBuf.append(c);
            }
        }
    }

    private ONode analyse_val(CharBuffer sBuf) {
        if (sBuf.isString == false) {
            sBuf.trimLast();//去掉尾部的空格
        }
        return analyse_val(sBuf.toString(), sBuf.isString);
    }

    private ONode analyse_val(String sval, boolean isString) {
        ONode orst = new ONode();
        OValue oval = orst.val();

        if (isString) {
            oval.setString(sval);
        } else {
            char c = sval.charAt(0);
            int len = sval.length();

            if (c == 't' && len == 4) { //true
                oval.setBool(true);
            } else if (c == 'f' && len == 5) { //false
                oval.setBool(false);
            } else if (c == 'n') { // null or new (new not sup)
                if (len == 4) {
                    oval.setNull();
                } else if (sval.indexOf('D') == 4) { //new Date(xxx)
                    long ticks = Long.parseLong(sval.substring(9, sval.length() - 1));
                    oval.setDate(new Date(ticks));
                }
            } else if (c == 'N' && len == 3) { // NaN
                oval.setNull();
            } else if (c == 'u' && len == 9) { // undefined
                oval.setNull();
            } else if ((c >= '0' && c <= '9') || (c == '-')) { //number
                if (sval.length() > 16) { //超过16位长度；采用大数字处理
                    if (sval.indexOf('.') > 0) {
                        oval.setBignumber(new BigDecimal(sval));
                    } else {
                        oval.setBignumber(new BigInteger(sval));
                    }
                } else { //小于16位长度；采用常规数字处理
                    if (sval.indexOf('.') > 0) {
                        oval.setDecimal(Double.parseDouble(sval));
                    } else {
                        oval.setInteger(Long.parseLong(sval));
                    }
                }
            } else { //other
                throw new RuntimeException("Format error!");
            }
        }

        return orst;
    }
}

