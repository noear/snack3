package org.noear.snack.to;

import org.noear.snack.ONode;
import org.noear.snack.ONodeData;
import org.noear.snack.OValue;
import org.noear.snack.core.Feature;
import org.noear.snack.core.exts.ThData;
import org.noear.snack.core.utils.DateUtil;
import org.noear.snack.core.utils.IOUtil;
import org.noear.snack.core.utils.TypeUtil;
import org.noear.snack.core.Options;
import org.noear.snack.core.Context;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;

/**
 * Json 序列化
 *
 * 将ONode 转为 json string
 * */
public class JsonToer implements Toer {
    private static final ThData<StringBuilder> tlBuilder = new ThData(() -> new StringBuilder(1024 * 5));

    @Override
    public void handle(Context ctx) {
        ONode o = (ONode) ctx.source;

        if (null != o) {
            StringBuilder sb = null;
            if (ctx.options.hasFeature(Feature.DisThreadLocal)) {
                sb = new StringBuilder(1024 * 5); //5k
            } else {
                sb = tlBuilder.get(); //
                sb.setLength(0);
            }


            analyse(ctx.options, o, sb);

            ctx.target = sb.toString();
        }
    }

    public void analyse(Options opts, ONode o, StringBuilder sb) {
        if (o == null) {
            return;
        }

        switch (o.nodeType()) {
            case Value:
                writeValue(opts, sb, o.nodeData());
                break;

            case Array:
                writeArray(opts, sb, o.nodeData());
                break;

            case Object:
                writeObject(opts, sb, o.nodeData());
                break;

            default:
                sb.append("null");
                break;
        }
    }

    private void writeArray(Options opts, StringBuilder sBuf, ONodeData d) {
        sBuf.append("[");
        Iterator<ONode> iterator = d.array.iterator();
        while (iterator.hasNext()) {
            ONode sub = iterator.next();
            analyse(opts, sub, sBuf);
            if (iterator.hasNext()) {
                sBuf.append(",");
            }
        }
        sBuf.append("]");
    }

    private void writeObject(Options opts, StringBuilder sBuf, ONodeData d) {
        sBuf.append("{");
        Iterator<String> itr = d.object.keySet().iterator();
        while (itr.hasNext()) {
            String k = itr.next();
            writeName(opts, sBuf, k);
            sBuf.append(":");
            analyse(opts, d.object.get(k), sBuf);
            if (itr.hasNext()) {
                sBuf.append(",");
            }
        }
        sBuf.append("}");
    }

    private void writeValue(Options opts, StringBuilder sBuf, ONodeData d) {
        OValue v = d.value;
        switch (v.type()) {
            case Null:
                sBuf.append("null");
                break;

            case String:
                writeValString(opts, sBuf, v.getRawString(), true);
                break;

            case DateTime:
                writeValDate(opts, sBuf, v.getRawDate());
                break;

            case Boolean:
                writeValBool(opts, sBuf, v.getRawBoolean());
                break;

            case Number:
                writeValNumber(opts, sBuf, v.getRawNumber());
                break;

            default:
                sBuf.append(v.getString());
                break;
        }
    }

    private void writeName(Options opts, StringBuilder sBuf, String val) {
        if (opts.hasFeature(Feature.QuoteFieldNames)) {
            if (opts.hasFeature(Feature.UseSingleQuotes)) {
                sBuf.append("'");
                writeString(opts, sBuf, val, '\'');
                sBuf.append("'");
            } else {
                sBuf.append("\"");
                writeString(opts, sBuf, val, '"');
                sBuf.append("\"");
            }
        } else {
            writeString(opts, sBuf, val, '"');
        }
    }

    private void writeValDate(Options opts, StringBuilder sBuf, Date val) {
        if (opts.hasFeature(Feature.WriteDateUseTicks)) {
            sBuf.append(val.getTime());
        } else if (opts.hasFeature(Feature.WriteDateUseFormat)) {
            String valStr = DateUtil.format(val, opts.getDateFormat(), opts.getTimeZone());
            writeValString(opts, sBuf, valStr, false);
        } else {
            sBuf.append("new Date(").append(val.getTime()).append(")");
        }
    }

    private void writeValBool(Options opts, StringBuilder sBuf, Boolean val) {
        if (opts.hasFeature(Feature.WriteBoolUse01)) {
            sBuf.append(val ? 1 : 0);
        } else {
            sBuf.append(val ? "true" : "false");
        }
    }

    private void writeValNumber(Options opts, StringBuilder sBuf, Number val) {

        if (val instanceof BigInteger) {
            BigInteger v = (BigInteger) val;
            String sVal = v.toString();

            if (opts.hasFeature(Feature.WriteNumberUseString)) {
                writeValString(opts, sBuf, sVal, false);
            } else {
                //数字太大时，可用string来表示；
                if (sVal.length() > 16 && (v.compareTo(TypeUtil.INT_LOW) < 0 || v.compareTo(TypeUtil.INT_HIGH) > 0) && opts.hasFeature(Feature.BrowserCompatible)) {
                    writeValString(opts, sBuf, sVal, false);
                } else {
                    sBuf.append(sVal);
                }
            }
            return;
        }

        if (val instanceof BigDecimal) {
            BigDecimal v = (BigDecimal) val;
            String sVal = v.toPlainString();

            if (opts.hasFeature(Feature.WriteNumberUseString)) {
                writeValString(opts, sBuf, sVal, false);
            } else {
                //数字太大时，可用string来表示；
                if (sVal.length() > 16 && (v.compareTo(TypeUtil.DEC_LOW) < 0 || v.compareTo(TypeUtil.DEC_HIGH) > 0) && opts.hasFeature(Feature.BrowserCompatible)) {
                    writeValString(opts, sBuf, sVal, false);
                } else {
                    sBuf.append(sVal);
                }
            }
            return;
        }

        if (opts.hasFeature(Feature.WriteNumberUseString)) {
            writeValString(opts, sBuf, val.toString(), false);
        } else {
            sBuf.append(val.toString());
        }
    }

    /**
     * @param isStr 是否为真实字符串
     */
    private void writeValString(Options opts, StringBuilder sBuf, String val, boolean isStr) {
        //引号开始
        boolean useSingleQuotes = opts.hasFeature(Feature.UseSingleQuotes);
        char quote = (useSingleQuotes ? '\'' : '\"');
        sBuf.append(quote);


        //内容
        if (isStr) {
            writeString(opts,sBuf,val, quote);
        } else {
            //非字符串直接添加
            sBuf.append(val);
        }

        //引号结束
        sBuf.append(quote);
    }

    private void writeString(Options opts, StringBuilder sBuf, String val, char quote) {
        boolean isCompatible = opts.hasFeature(Feature.BrowserCompatible);
        boolean isSecure = opts.hasFeature(Feature.BrowserSecure);
        boolean isTransfer = opts.hasFeature(Feature.TransferCompatible);

        for (int i = 0, len = val.length(); i < len; i++) {
            char c = val.charAt(i);

            //引号转义处理 + 特殊字符必须码 // 去掉 c == '\\' ,不然 "\a" 会变成 "\\a" //移到 isCompatible
            if (c == quote || c == '\n' || c == '\r' || c == '\t' || c == '\f' || c == '\b' || (c >= '\0' && c <= '\7')) {
                sBuf.append("\\");
                sBuf.append(IOUtil.CHARS_MARK[(int) c]);
                continue;
            }

            if (isSecure) {
                if (c == '(' || c == ')' || c == '<' || c == '>') {
                    sBuf.append('\\');
                    sBuf.append('u');
                    sBuf.append(IOUtil.DIGITS[(c >>> 12) & 15]);
                    sBuf.append(IOUtil.DIGITS[(c >>> 8) & 15]);
                    sBuf.append(IOUtil.DIGITS[(c >>> 4) & 15]);
                    sBuf.append(IOUtil.DIGITS[c & 15]);
                    continue;
                }
            }

            if (isTransfer) {
                if (c == '\\') {
                    sBuf.append("\\");
                    sBuf.append(IOUtil.CHARS_MARK[(int) c]);
                    continue;
                }
            }

            if (isCompatible) {
                if (c == '\\') {
                    sBuf.append("\\");
                    sBuf.append(IOUtil.CHARS_MARK[(int) c]);
                    continue;
                }

                //对不可见ASC码，进行编码处理
                if (c < 32) {
                    sBuf.append('\\');
                    sBuf.append('u');
                    sBuf.append('0');
                    sBuf.append('0');
                    sBuf.append(IOUtil.DIGITS[(c >>> 4) & 15]);
                    sBuf.append(IOUtil.DIGITS[c & 15]);
                    continue;
                }
                //对大码，进行编码处理
                if (c >= 127) {
                    sBuf.append('\\');
                    sBuf.append('u');
                    sBuf.append(IOUtil.DIGITS[(c >>> 12) & 15]);
                    sBuf.append(IOUtil.DIGITS[(c >>> 8) & 15]);
                    sBuf.append(IOUtil.DIGITS[(c >>> 4) & 15]);
                    sBuf.append(IOUtil.DIGITS[c & 15]);
                    continue;
                }
            }

            sBuf.append(c);
        }
    }
}
