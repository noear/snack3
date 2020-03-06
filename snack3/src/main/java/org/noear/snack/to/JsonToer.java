package org.noear.snack.to;

import org.noear.snack.ONode;
import org.noear.snack.ONodeData;
import org.noear.snack.OValue;
import org.noear.snack.core.Feature;
import org.noear.snack.core.exts.ThData;
import org.noear.snack.core.utils.IOUtil;
import org.noear.snack.core.utils.TypeUtil;
import org.noear.snack.core.Constants;
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
    private static final ThData<StringBuilder> tlBuilder = new ThData(()->new StringBuilder(1024*5));

    @Override
    public void handle(Context ctx) {
        ONode o = (ONode) ctx.source;

        if (null != o) {
            StringBuilder sb =  tlBuilder.get(); // new StringBuilder(1024*5); //
            sb.setLength(0);

            analyse(ctx.config, o, sb);

            ctx.target = sb.toString();
        }
    }


    public void analyse(Constants cfg, ONode o, StringBuilder sb) {
        if (o == null) {
            return;
        }

        switch (o.nodeType()) {
            case Value:
                writeValue(cfg, sb, o.nodeData());
                break;

            case Array:
                writeArray(cfg, sb, o.nodeData());
                break;

            case Object:
                writeObject(cfg, sb, o.nodeData());
                break;

            default:
                sb.append("null");
                break;
        }
    }

    private void writeArray(Constants cfg, StringBuilder sBuf, ONodeData d){
        sBuf.append("[");
        Iterator<ONode> iterator = d.array.iterator();
        while (iterator.hasNext()) {
            ONode sub = iterator.next();
            analyse(cfg, sub, sBuf);
            if (iterator.hasNext()) {
                sBuf.append(",");
            }
        }
        sBuf.append("]");
    }

    private void writeObject(Constants cfg, StringBuilder sBuf, ONodeData d){
        sBuf.append("{");
        Iterator<String> itr = d.object.keySet().iterator();
        while (itr.hasNext()) {
            String k = itr.next();
            writeName(cfg,sBuf,k);
            sBuf.append(":");
            analyse(cfg, d.object.get(k), sBuf);
            if (itr.hasNext()) {
                sBuf.append(",");
            }
        }
        sBuf.append("}");
    }

    private void writeValue(Constants cfg, StringBuilder sBuf, ONodeData d){
        OValue v = d.value;
        switch (v.type()) {
            case Null:
                sBuf.append("null");
                break;

            case String:
                writeValString(cfg, sBuf, v.getRawString(),true);
                break;

            case DateTime:
                writeValDate(cfg,sBuf,v.getRawDate());
                break;

            case Boolean:
                writeValBool(cfg,sBuf,v.getRawBoolean());
                break;

            case Bignumber:
                writeValBignum(cfg,sBuf,v.getRawBignumber());//添加对大数字的处理
                break;

            case Integer:
                sBuf.append(v.getRawInteger());
                break;

            case Decimal:
                sBuf.append(v.getRawDecimal());
                break;

            default:
                sBuf.append(v.getString());
                break;
        }
    }

    private void writeName(Constants cfg, StringBuilder sBuf, String val) {
        if (cfg.hasFeature(Feature.QuoteFieldNames)) {
            if(cfg.hasFeature(Feature.SerializeUseSingleQuotes)){
                sBuf.append("'").append(val).append("'");
            }else {
                sBuf.append("\"").append(val).append("\"");
            }
        } else {
            sBuf.append(val);
        }
    }

    private void writeValDate(Constants cfg, StringBuilder sBuf, Date val){
        if(cfg.hasFeature(Feature.WriteDateUseTicks)){
            sBuf.append(val.getTime());
        }else if(cfg.hasFeature(Feature.WriteDateUseFormat)){
            writeValString(cfg, sBuf, cfg.dateToString(val), false);
        }else{
            sBuf.append("new Date(").append(val.getTime()).append(")");
        }
    }

    private void writeValBool(Constants cfg, StringBuilder sBuf, Boolean val){
        if(cfg.hasFeature(Feature.WriteBoolUse01)){
            sBuf.append(val?1:0);
        }else{
            sBuf.append(val?"true":"false");
        }
    }

    private void writeValBignum(Constants cfg, StringBuilder sBuf, Number val){
        String sVal = val.toString();

        if(val instanceof BigInteger){
            BigInteger v = (BigInteger) val;
            //数字太大时，可用string来表示；
            if(sVal.length()>16 && ( v.compareTo(TypeUtil.INT_LOW)<0 || v.compareTo(TypeUtil.INT_HIGH)>0) && cfg.hasFeature(Feature.BrowserCompatible)) {
                writeValString(cfg, sBuf, sVal, false);
            }else{
                sBuf.append(sVal);
            }
            return;
        }

        if(val instanceof BigDecimal){
            BigDecimal v = (BigDecimal) val;
            //数字太大时，可用string来表示；
            if(sVal.length()>16 && ( v.compareTo(TypeUtil.DEC_LOW)<0 || v.compareTo(TypeUtil.DEC_HIGH)>0) && cfg.hasFeature(Feature.BrowserCompatible)) {
                writeValString(cfg, sBuf, sVal, false);
            }else{
                sBuf.append(sVal);
            }
            return;
        }

        sBuf.append(sVal);
    }

    private void writeValString(Constants cfg, StringBuilder sBuf, String val, boolean isStr) {
        //引号开始
        boolean useSingleQuotes = cfg.hasFeature(Feature.SerializeUseSingleQuotes);
        if(useSingleQuotes){
            sBuf.append("'");
        }else{
            sBuf.append("\"");
        }


        //内容
        if (isStr) {
            boolean isCompatible = cfg.hasFeature(Feature.BrowserCompatible);
            boolean isSecure = cfg.hasFeature(Feature.BrowserSecure);
            for (int i = 0, len = val.length(); i < len; i++) {
                char c = val.charAt(i);
                //特殊字符必须码
                if (c == '\\' || c == '\n' || c == '\r' || c == '\t' || c == '\f' || c == '\b' || (c>='\0' && c<='\7')) {
                    sBuf.append("\\");
                    sBuf.append(IOUtil.CHARS_MARK[(int)c]);
                    continue;
                }

                if(useSingleQuotes){
                    if(c == '\''){
                        sBuf.append("\\");
                        sBuf.append(IOUtil.CHARS_MARK[(int)c]);
                        continue;
                    }
                }else{
                    if(c == '\"'){
                        sBuf.append("\\");
                        sBuf.append(IOUtil.CHARS_MARK[(int)c]);
                        continue;
                    }
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

                if (isCompatible) {
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
        } else {
            //非字符串直接添加
            sBuf.append(val);
        }

        //引号结束
        if(useSingleQuotes){
            sBuf.append("'");
        }else{
            sBuf.append("\"");
        }
    }
}
