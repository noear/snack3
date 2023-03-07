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

            ctx.beautiful = ctx.options.hasFeature(Feature.Beautiful);
            analyse(ctx, o, sb);

            ctx.target = sb.toString();
        }
    }

    public void analyse(Context ctx, ONode o, StringBuilder sb) {
        if (o == null) {
            return;
        }

        switch (o.nodeType()) {
            case Value:
                writeValue(ctx, sb, o.nodeData());
                break;

            case Array:
                writeArray(ctx, sb, o.nodeData());
                break;

            case Object:
                writeObject(ctx, sb, o.nodeData());
                break;

            default:
                sb.append("null");
                break;
        }
    }

    private void writeArray(Context ctx, StringBuilder sBuf, ONodeData d) {
        sBuf.append("[");
        if (ctx.beautiful) {
            ctx.depth++;
        }

        Iterator<ONode> iterator = d.array.iterator();
        while (iterator.hasNext()) {
            if (ctx.beautiful) {
                sBuf.append("\n");
                writeDepth(ctx, sBuf);
            }

            ONode sub = iterator.next();
            analyse(ctx, sub, sBuf);
            if (iterator.hasNext()) {
                sBuf.append(",");
            }
        }

        if (ctx.beautiful) {
            sBuf.append("\n");
            ctx.depth--;
            writeDepth(ctx, sBuf);
        }
        sBuf.append("]");
    }

    private void writeObject(Context ctx, StringBuilder sBuf, ONodeData d) {
        sBuf.append("{");
        if (ctx.beautiful) {
            ctx.depth++;
        }


        Iterator<String> itr = d.object.keySet().iterator();
        while (itr.hasNext()) {
            String k = itr.next();

            if(ctx.beautiful) {
                sBuf.append("\n");
                writeDepth(ctx, sBuf);
            }

            writeName(ctx, sBuf, k);
            sBuf.append(":");

            if(ctx.beautiful){
                sBuf.append(" ");
            }

            analyse(ctx, d.object.get(k), sBuf);
            if (itr.hasNext()) {
                sBuf.append(",");
            }
        }

        if (ctx.beautiful) {
            sBuf.append("\n");
            ctx.depth--;
            writeDepth(ctx, sBuf);
        }

        sBuf.append("}");
    }

    private void writeDepth(Context ctx, StringBuilder sBuf) {
        if (ctx.depth > 0 && ctx.beautiful) {
            for (int i = 0; i < ctx.depth; i++) {
                sBuf.append("  ");
            }
        }
    }

    private void writeValue(Context ctx, StringBuilder sBuf, ONodeData d) {
        OValue v = d.value;
        switch (v.type()) {
            case Null:
                sBuf.append("null");
                break;

            case String:
                writeValString(ctx, sBuf, v.getRawString(), true);
                break;

            case DateTime:
                writeValDate(ctx, sBuf, v.getRawDate());
                break;

            case Boolean:
                writeValBool(ctx, sBuf, v.getRawBoolean());
                break;

            case Number:
                writeValNumber(ctx, sBuf, v.getRawNumber());
                break;

            default:
                sBuf.append(v.getString());
                break;
        }
    }

    private void writeName(Context ctx, StringBuilder sBuf, String val) {
        if (ctx.options.hasFeature(Feature.QuoteFieldNames)) {
            if (ctx.options.hasFeature(Feature.UseSingleQuotes)) {
                sBuf.append("'");
                writeString(ctx, sBuf, val, '\'');
                sBuf.append("'");
            } else {
                sBuf.append("\"");
                writeString(ctx, sBuf, val, '"');
                sBuf.append("\"");
            }
        } else {
            writeString(ctx, sBuf, val, '"');
        }
    }

    private void writeValDate(Context ctx, StringBuilder sBuf, Date val) {
        if (ctx.options.hasFeature(Feature.WriteDateUseTicks)) {
            sBuf.append(val.getTime());
        } else if (ctx.options.hasFeature(Feature.WriteDateUseFormat)) {
            String valStr = DateUtil.format(val, ctx.options.getDateFormat(), ctx.options.getTimeZone());
            writeValString(ctx, sBuf, valStr, false);
        } else {
            sBuf.append("new Date(").append(val.getTime()).append(")");
        }
    }

    private void writeValBool(Context ctx, StringBuilder sBuf, Boolean val) {
        if (ctx.options.hasFeature(Feature.WriteBoolUse01)) {
            sBuf.append(val ? 1 : 0);
        } else {
            sBuf.append(val ? "true" : "false");
        }
    }

    private void writeValNumber(Context ctx, StringBuilder sBuf, Number val) {

        if (val instanceof BigInteger) {
            BigInteger v = (BigInteger) val;
            String sVal = v.toString();

            if (ctx.options.hasFeature(Feature.WriteNumberUseString)) {
                writeValString(ctx, sBuf, sVal, false);
            } else {
                //数字太大时，可用string来表示；
                if (sVal.length() > 16 && (v.compareTo(TypeUtil.INT_LOW) < 0 || v.compareTo(TypeUtil.INT_HIGH) > 0) && ctx.options.hasFeature(Feature.BrowserCompatible)) {
                    writeValString(ctx, sBuf, sVal, false);
                } else {
                    sBuf.append(sVal);
                }
            }
            return;
        }

        if (val instanceof BigDecimal) {
            BigDecimal v = (BigDecimal) val;
            String sVal = v.toPlainString();

            if (ctx.options.hasFeature(Feature.WriteNumberUseString)) {
                writeValString(ctx, sBuf, sVal, false);
            } else {
                //数字太大时，可用string来表示；
                if (sVal.length() > 16 && (v.compareTo(TypeUtil.DEC_LOW) < 0 || v.compareTo(TypeUtil.DEC_HIGH) > 0) && ctx.options.hasFeature(Feature.BrowserCompatible)) {
                    writeValString(ctx, sBuf, sVal, false);
                } else {
                    sBuf.append(sVal);
                }
            }
            return;
        }

        if (ctx.options.hasFeature(Feature.WriteNumberUseString)) {
            writeValString(ctx, sBuf, val.toString(), false);
        } else {
            sBuf.append(val.toString());
        }
    }

    /**
     * @param isStr 是否为真实字符串
     */
    private void writeValString(Context ctx, StringBuilder sBuf, String val, boolean isStr) {
        //引号开始
        boolean useSingleQuotes = ctx.options.hasFeature(Feature.UseSingleQuotes);
        char quote = (useSingleQuotes ? '\'' : '\"');
        sBuf.append(quote);


        //内容
        if (isStr) {
            writeString(ctx,sBuf,val, quote);
        } else {
            //非字符串直接添加
            sBuf.append(val);
        }

        //引号结束
        sBuf.append(quote);
    }

    private void writeString(Context ctx, StringBuilder sBuf, String val, char quote) {
        boolean isCompatible = ctx.options.hasFeature(Feature.BrowserCompatible);
        boolean isSecure = ctx.options.hasFeature(Feature.BrowserSecure);
        boolean isTransfer = ctx.options.hasFeature(Feature.TransferCompatible);

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
