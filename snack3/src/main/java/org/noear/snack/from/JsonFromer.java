package org.noear.snack.from;

import org.noear.snack.ONode;
import org.noear.snack.OValue;
import org.noear.snack.core.Context;
import org.noear.snack.core.exts.CharBuffer;
import org.noear.snack.core.exts.CharReader;
import org.noear.snack.core.exts.ThData;
import org.noear.snack.core.utils.IOUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * Json 解析器（将 json string 转为 ONode）
 * key：支持双引号、单引号、无引号
 * str：支持双引号、单引号
 */
public class JsonFromer implements Fromer {
    private static final ThData<CharBuffer> tlBuilder = new ThData<>(()->new CharBuffer());

    @Override
    public void handle(Context ctx) throws IOException {
        String text = (String) ctx.source;
        int len = text.length();

        //完整的处理（支持像："xx",'xx',12,true,{...},[],null,undefined 等）
        //
        if (len == 0) {
            ctx.node = new ONode(ctx.config);
        } else {
            char prefix = text.charAt(0);
            char suffix = text.charAt(text.length() - 1);

            if ((prefix == '{' && suffix == '}')
                    || (prefix == '[' && suffix == ']')) {
                //object or array
                //
                CharBuffer sBuf = tlBuilder.get(); // new CharBuffer();//
                sBuf.setLength(0);

                ctx.node = new ONode(ctx.config);
                analyse(new CharReader(text), sBuf, ctx.node);

            } else if (len >= 2 && (
                    (prefix == '"' && suffix == '"') ||
                            (prefix == '\'' && suffix == '\''))) {
                //string
                //
                ctx.node = analyse_val(text.substring(1, len - 1), true, false);
            } else if (prefix != '<' && len < 40) {
                //null,num,bool,other
                //
                ctx.node = analyse_val(text, false, true);
            } else {
                //普通的字符串
                ctx.node = new ONode(ctx.config);
                ctx.node.val().setString(text);
            }
        }
    }

    public void analyse(CharReader sr, CharBuffer sBuf, ONode p) throws IOException {
        String name = null;

        // 读入字符
        while (sr.read()) {
            char c = sr.value();

            // 根据字符
            switch (c) {
                case '"':
                    scanString(sr, sBuf, '"');
                    if (analyse_buf(p, name, sBuf)) {
                        name = null;
                    }
                    break;

                case '\'':
                    scanString(sr, sBuf, '\'');
                    if (analyse_buf(p, name, sBuf)) {
                        name = null;
                    }
                    break;

                case '{':
                    if (p.isObject()) {
                        analyse(sr, sBuf, p.getNew(name).asObject());
                        name = null;
                    } else if (p.isArray()) {
                        analyse(sr, sBuf, p.addNew().asObject());
                    } else {
                        analyse(sr, sBuf, p.asObject());
                    }
                    break;

                case '[':
                    if (p.isObject()) {
                        analyse(sr, sBuf, p.getNew(name).asArray());
                        name = null;
                    } else if (p.isArray()) {
                        analyse(sr, sBuf, p.addNew().asArray());
                    } else {
                        analyse(sr, sBuf, p.asArray());
                    }
                    break;

                case ':':
                    // 新的键名
                    name = sBuf.toString();
                    sBuf.setLength(0);
                    break;

                case ',':
                    if (sBuf.length() > 0) {
                        if (analyse_buf(p, name, sBuf)) {
                            name = null;
                        }
                    }
                    break;

                case '}':
                    if (sBuf.length() > 0) {
                        analyse_buf(p, name, sBuf);//都返回了，不需要name=null了
                    }
                    return;

                case ']':
                    if (sBuf.length() > 0) {
                        analyse_buf(p, name, sBuf);//都返回了，不需要name=null了
                    }
                    return;

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
    }

    private boolean analyse_buf(ONode p, String name, CharBuffer sBuf) {
        if (p.isObject()) {
            if (name != null) {
                p.setNode(name, analyse_val(sBuf));
                sBuf.setLength(0);
                return true;
            }
        } else if (p.isArray()) {
            p.addNode(analyse_val(sBuf));
            sBuf.setLength(0);
        }
        return false;
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

                if ('t' == c || 'r' == c || 'n' == c || 'f' == c || 'b' == c || '"' == c || '\'' == c || '/'==c || (c >= '0' && c <= '7')) {
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
        return analyse_val(sBuf.toString(), sBuf.isString, false);
    }

    private ONode analyse_val(String sval, boolean isString, boolean isNoterr) {
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
                if(isNoterr){
                    oval.setString(sval);
                }else {
                    throw new RuntimeException("Format error!");
                }
            }
        }

        return orst;
    }
}

