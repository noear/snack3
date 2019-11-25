package org.noear.snack.core;

import org.noear.snack.ONode;
import org.noear.snack.OValue;
import org.noear.snack.core.exts.CharBuffer;
import org.noear.snack.core.exts.CharReader;
import org.noear.snack.core.exts.ThData;
import org.noear.snack.core.utils.IOUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * json path
 *
 * */
public class JsonPath {
    private static Map<String,String[]> _map = new HashMap<>(1024);
    public static ONode get(ONode source, String jpath, boolean cacheJpath) {
        //解析出指令
        String[] cmds = null;
        if (cacheJpath) {
            cmds = _map.get(jpath);
            if (cmds == null) {
                synchronized (jpath.intern()) {
                    cmds = _map.get(jpath);
                    if (cmds == null) {
                        cmds = compile(jpath);
                        _map.put(jpath, cmds);
                    }
                }
            }
        } else {
            cmds = compile(jpath);
        }


        //执行指令
        return exec(cmds, 0, source);
    }

    private static final ThData<CharBuffer> tlBuilder = new ThData<>(()->new CharBuffer());
    /**
     * 编译jpath指令
     * */
    private static String[] compile(String jpath) {
        //将..替换为.**. 方便解析（用**替代为深度扫描）
        String jpath2 = jpath.replace("..", ".**.");
        List<String> cmds = new ArrayList<>();

        char token = 0;
        char c = 0;
        CharBuffer buffer = tlBuilder.get();
        buffer.setLength(0);
        CharReader reader = new CharReader(jpath2);
        while (true) {
            c = reader.next();

            if (c == IOUtil.EOI) {
                if (buffer.length() > 0) {
                    cmds.add(buffer.toString());
                    buffer.clear();
                }
                break;
            }

            switch (c) {
                case '.':
                    if (token > 0) {
                        buffer.append(c);
                    } else {
                        if (buffer.length() > 0) {
                            cmds.add(buffer.toString());
                            buffer.clear();
                        }
                    }
                    break;
                case '(':
                    if (token == '[') {
                        token = c;
                    }
                    buffer.append(c);
                    break;
                case ')':
                    if (token == '(') {
                        token = c;
                    }
                    buffer.append(c);
                    break;
                case '[':
                    if (token == 0) {
                        token = c;
                        if (buffer.length() > 0) {
                            cmds.add(buffer.toString());
                            buffer.clear();
                        }
                    } else {
                        buffer.append(c);
                    }
                    break;
                case ']':
                    if (token == '[' || token == ')') {
                        token = 0;
                        buffer.append(c);
                        if (buffer.length() > 0) {
                            cmds.add(buffer.toString());
                            buffer.clear();
                        }
                    } else {
                        buffer.append(c);
                    }
                    break;
                default:
                    buffer.append(c);
                    break;
            }
        }

        return cmds.toArray(new String[cmds.size()]);
    }

    /**
     * 执行jpath指令
     * */
    private static ONode exec(String[] cmds, int index, ONode source) {
        ONode tmp = source;
        for (int i = index; i < cmds.length; i++) {

            if (tmp == null || tmp.isNull()) {//多次转换后，可能为null
                break;
            }

            String s = cmds[i];

            //$指令
            if (s.length() == 0 || "$".equals(s)) {
                continue; //当前节点
            }

            //**指令（即..指令）
            if ("**".equals(s)) {

                if (i + 1 < cmds.length) {
                    String c1 = cmds[i + 1];

                    ONode tmp2 = new ONode(tmp.cfg()).asArray();

                    scan(c1, tmp,true, tmp2);

                    i = i + 1;
                    tmp = tmp2;
                    continue;
                }else{
                    tmp = null;
                    break;
                }
            }

            //*指令
            if ("*".equals(s)) {
                ONode tmp2 = null;

                if (tmp.count() > 0) {
                    tmp2 = new ONode(tmp.cfg()).asArray();//有节点时，才初始化

                    if (tmp.isObject()) {
                        tmp2.addAll(tmp.obj().values());
                    } else {
                        tmp2.addAll(tmp.ary());
                    }
                }

                tmp = tmp2;
                continue;
            }

            //[]指令
            if (s.endsWith("]")) {
                String idx_s = s.substring(0, s.length() - 1);

                if ("*".equals(idx_s)) {
                    //[*]指令
                    //
                    ONode tmp2 = null;
                    if (tmp.isArray()) {
                        tmp2 = tmp;
                    }

                    if (tmp.isObject()) {
                        tmp2 = new ONode(tmp.cfg()).asArray();
                        tmp2.addAll(tmp.obj().values());
                    }

                    tmp = tmp2;
                    continue;
                } else if (idx_s.startsWith("?")) {
                    //[?()]指令
                    //
                    String s2 = idx_s.substring(4, idx_s.length() - 1);//=>@.a == 1, @.a == @.b
                    String[] ss2 = s2.split(" ");
                    String left = ss2[0];
                    if (ss2.length == 1) {
                        if (tmp.isObject()) {
                            if (tmp.contains(left) == false) {
                                tmp = null;
                            }
                        } else if (tmp.isArray()) {
                            ONode tmp2 = new ONode(tmp.cfg()).asArray();
                            for (ONode n1 : tmp.ary()) {
                                if (n1.contains(left)) {
                                    tmp2.addNode(n1);
                                }
                            }
                            tmp = tmp2;
                        }
                    } else if (ss2.length == 3) {
                        if (tmp.isObject()) {
                            if (compare(tmp, tmp.getOrNull(left), ss2[1], ss2[2]) == false) {
                                tmp = null;
                            }
                        } else if (tmp.isArray()) {
                            ONode tmp2 = new ONode(tmp.cfg()).asArray();
                            for (ONode n1 : tmp.ary()) {
                                if (compare(n1, n1.getOrNull(left), ss2[1], ss2[2])) {
                                    tmp2.addNode(n1);
                                }
                            }
                            tmp = tmp2;
                        }
                    }
                } else if (idx_s.indexOf(",") > 0) {
                    //[,]指令
                    //
                    //[1,4,6] //['p1','p2']
                    ONode tmp2 = new ONode(tmp.cfg()).asArray();
                    String[] iAry = idx_s.split(",");

                    if(idx_s.indexOf("'")>=0){
                        if(tmp.isObject()){
                            for (String i1 : iAry) {
                                ONode n1 = tmp.obj().get(i1.substring(1, i1.length() - 1));
                                if (n1 != null) {
                                    tmp2.addNode(n1);
                                }
                            }
                        }
                    }else {
                        if (tmp.isArray()) {
                            List<ONode> list2 = tmp.nodeData().array;
                            int len2 = list2.size();

                            for (String i1 : iAry) {
                                int i2 = Integer.parseInt(i1);
                                if (i2 >= 0 && i2 < len2) {
                                    tmp2.addNode(list2.get(i2));
                                }
                            }
                        }
                    }

                    tmp=tmp2;
                    continue;

                } else if (idx_s.indexOf(":") >= 0) {
                    //[:]指令
                    //
                    //[2:4]
                    if(tmp.isArray()) {
                        String[] iAry = idx_s.split(":", -1);
                        int count = tmp.count();
                        int start = 0;
                        if (iAry[0].length() > 0) {
                            start = Integer.parseInt(iAry[0]);
                            if (start < 0) {//如果是倒数？
                                start = count + start;
                            }
                        }
                        int end = count;
                        if (iAry[1].length() > 0) {
                            end = Integer.parseInt(iAry[1]);
                            if (end < 0) { //如果是倒数？
                                end = count + end;
                            }
                        }

                        if (start < 0) {
                            start = 0;
                        }
                        if (end > count) {
                            end = count;
                        }

                        ONode tmp2 = new ONode(tmp.cfg()).addAll(tmp.ary().subList(start, end));

                        tmp = tmp2;
                        continue;
                    }else{
                        return null;
                    }

                } else {
                    //[x]指令
                    //
                    //[2] [-2] ['p1']
                    //
                    if (idx_s.startsWith("'")) {
                        if (idx_s.endsWith("'")) {
                            String k2 = idx_s.substring(1, idx_s.length() - 1);
                            tmp = tmp.getOrNull(k2);
                        } else {
                            tmp = null;
                        }
                    } else {
                        int idx = Integer.parseInt(idx_s);
                        if (idx < 0) {
                            tmp = tmp.getOrNull(tmp.count() + idx);//倒数位
                        } else {
                            tmp = tmp.getOrNull(idx);//正数位
                        }
                    }
                }
            }
            else if(s.endsWith(")")) {
                //.fun()指令
                switch (s) {
                    case "size()":
                        return new ONode(tmp.cfg()).val(tmp.count());
                    case "min()": {
                        if (tmp.isArray()) {
                            ONode min_n = null;
                            for (ONode n1 : tmp.ary()) {
                                if(n1.isValue()) {
                                    if (min_n == null) {
                                        min_n = n1;
                                    } else if (n1.getDouble() < min_n.getDouble()) {
                                        min_n = n1;
                                    }
                                }
                            }
                            return min_n;
                        }

                        if (tmp.isValue()) {
                            return tmp;
                        }

                        return null;
                    }

                    case "max()":{
                        if(tmp.isArray()){
                            ONode max_n = null;
                            for(ONode n1 : tmp.ary()){
                                if(n1.isValue()) {
                                    if(max_n == null){
                                        max_n = n1;
                                    }else if (n1.getDouble() > max_n.getDouble()) {
                                        max_n = n1;
                                    }
                                }
                            }
                            return max_n;
                        }

                        if(tmp.isValue()){
                            return tmp;
                        }

                        return null;
                    }

                    case "avg()":{
                        if(tmp.isArray()){
                            double sum = 0;
                            for(ONode n1 : tmp.ary()){
                                sum+=n1.getDouble();
                            }
                            return new ONode(tmp.cfg()).val(sum);
                        }else {
                            return null;
                        }
                    }

                    default:
                        return null;
                }
            } else {
                //.name 指令
                //
                //name
                if (tmp.isArray()) {
                    ONode tmp2 = null;
                    for (ONode n1 : tmp.ary()) {
                        if (n1.isObject()) {
                            ONode n2 = n1.nodeData().object.get(s);
                            if (n2 != null) {
                                if(tmp2 == null){ //有节点时，才初始化
                                    tmp2 =new ONode(tmp.cfg()).asArray();
                                }

                                tmp2.add(n2);
                            }
                        }
                    }
                    tmp =tmp2;
                    continue;
                } else {
                    if (tmp.isObject()) {
                        tmp = tmp.nodeData().object.get(s);
                    } else {
                        tmp = null;
                    }
                }
            }
        }

        if (tmp == null) {
            return new ONode();
        } else {
            return tmp;
        }
    }

    /**
     * 深度扫描
     * */
    private static void scan(String name, ONode source, boolean isRoot, ONode target) {
        if (!isRoot && "*".equals(name)) {
            target.add(source);
        }

        if (source.isObject()) {
            for (Map.Entry<String, ONode> kv : source.obj().entrySet()) {
                if (name.equals(kv.getKey())) {
                    target.add(kv.getValue());
                }

                scan(name, kv.getValue(), false, target);
            }
            return;
        }

        if (source.isArray()) {
            for (ONode n1 : source.ary()) {
                scan(name, n1, false, target);
            }
            return;
        }


    }

    /*
    * ?(left op right)
    * */
    private static boolean compare(ONode parent, ONode leftO, String op, String right) {
        if (leftO == null) {
            return false;
        }

        if (leftO.isValue() == false || leftO.val().isNull()) {
            return false;
        }

        OValue left = leftO.val();

        switch (op) {
            case "==": {
                if (right.startsWith("'")) {
                    return left.getString().equals(right.substring(1, right.length() - 1));
                } else {
                    return left.getDouble() == Double.parseDouble(right);
                }
            }
            case "!=": {
                if (right.startsWith("'")) {
                    return left.getString().equals(right.substring(1, right.length() - 1)) == false;
                } else {
                    return left.getDouble() != Double.parseDouble(right);
                }
            }
            case "<":
                return left.getDouble() < Double.parseDouble(right);
            case "<=":
                return left.getDouble() <= Double.parseDouble(right);
            case ">":
                return left.getDouble() > Double.parseDouble(right);
            case ">=":
                return left.getDouble() >= Double.parseDouble(right);
            case "=~": {
                int end = right.lastIndexOf('/');
                String exp = right.substring(1, end);
                return regex(right, exp).matcher(left.getString()).find();
            }
            case "in": {
                if (right.indexOf("'") > 0) {
                    return getStringAry(right).contains(left.getString());
                } else {
                    return getDoubleAry(right).contains(left.getDouble());
                }
            }
            case "nin":
                if (right.indexOf("'") > 0) {
                    return getStringAry(right).contains(left.getString()) == false;
                } else {
                    return getDoubleAry(right).contains(left.getDouble()) == false;
                }
        }

        return false;
    }

    /**
     * 将 ['a','1'] 转为 List<String>
     * */
    private static List<String> getStringAry(String text) {
        List<String> ary = new ArrayList<>();
        String test2 = text.substring(1, text.length() - 1);
        String[] ss = test2.split(",");
        for (String s : ss) {
            ary.add(s.substring(1, s.length() - 1));
        }

        return ary;
    }

    /**
     * 将 [1,2,3,1.1] 转为 List<Double>
     * */
    private static List<Double> getDoubleAry(String text) {
        List<Double> ary = new ArrayList<>();
        String test2 = text.substring(1, text.length() - 1);
        String[] ss = test2.split(",");
        for (String s : ss) {
            ary.add(Double.parseDouble(s));
        }

        return ary;
    }

    private static Map<String,Pattern> _regexLib = new HashMap<>();
    private static Pattern regex(String exprFull, String expr) {
        Pattern p = _regexLib.get(exprFull);
        if (p == null) {
            synchronized (exprFull.intern()) {
                if (p == null) {
                    if (exprFull.endsWith("i")) {
                        p = Pattern.compile(expr, Pattern.CASE_INSENSITIVE);
                    } else {
                        p = Pattern.compile(expr);
                    }
                    _regexLib.put(exprFull, p);
                }
            }
        }

        return p;
    }
}
