package org.noear.snack.core;

import org.noear.snack.ONode;
import org.noear.snack.OValue;
import org.noear.snack.core.exts.CharBuffer;
import org.noear.snack.core.exts.CharReader;
import org.noear.snack.core.exts.Fun3;
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
    private static Map<String,List<Segment>> _map = new HashMap<>(1024);
    public static ONode get(ONode source, String jpath, boolean cacheJpath) {
        //解析出指令
        List<Segment> cmds = null;
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
        return exec(cmds,  source);
    }

    private static final ThData<CharBuffer> tlBuilder = new ThData<>(()->new CharBuffer());
    /**
     * 编译jpath指令
     * */
    private static List<Segment> compile(String jpath) {
        //将..替换为._ 方便解析（用_x替代为深度扫描）
        String jpath2 = jpath.replace("..", "._");
        List<Segment> cmds = new ArrayList<>();

        char token = 0;
        char c = 0;
        CharBuffer buffer = tlBuilder.get();
        buffer.setLength(0);
        CharReader reader = new CharReader(jpath2);
        while (true) {
            c = reader.next();

            if (c == IOUtil.EOI) {
                if (buffer.length() > 0) {
                    cmds.add(new Segment(buffer.toString()));
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
                            cmds.add(new Segment(buffer.toString()));
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
                            cmds.add(new Segment(buffer.toString()));
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
                            cmds.add(new Segment(buffer.toString()));
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

        return cmds;
    }

    /**
     * 执行jpath指令
     * */
    private static ONode exec(List<Segment> cmds, ONode source) {
        ONode tmp = source;
        for (Segment s : cmds) {
            if (tmp == null) {//多次转换后，可能为null
                break;
            }

            tmp = s.handler.run(s, source, tmp);
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
    private static void scanByName(String name, ONode source, ONode target) {
        if (source.isObject()) {
            for (Map.Entry<String, ONode> kv : source.obj().entrySet()) {
                if (name.equals(kv.getKey())) {
                    target.add(kv.getValue());
                }

                scanByName(name, kv.getValue(), target);
            }
            return;
        }

        if (source.isArray()) {
            for (ONode n1 : source.ary()) {
                scanByName(name, n1,  target);
            }
            return;
        }
    }

    private static void scanByAll(String name, ONode source, boolean isRoot, ONode target) {
        if (isRoot == false) {
            target.add(source);
        }

        if (source.isObject()) {
            for (Map.Entry<String, ONode> kv : source.obj().entrySet()) {
                scanByAll(name, kv.getValue(), false, target);
            }
            return;
        }

        if (source.isArray()) {
            for (ONode n1 : source.ary()) {
                scanByAll(name, n1, false, target);
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

    public static Fun3<ONode,Segment,ONode,ONode> handler_$=(s,root, tmp)->{ return tmp;};
    public static Fun3<ONode,Segment,ONode,ONode> handler_xx=(s,root, tmp)-> {
        String p = s.cmd.substring(1);
        if (p.length() > 0) {
            ONode tmp2 = new ONode(tmp.cfg()).asArray();
            if ("*".equals(p)) {
                scanByAll(p, tmp, true, tmp2);
            } else {
                scanByName(p, tmp, tmp2);
            }

            return tmp2;
        } else {
            return null;
        }
    };

    public static Fun3<ONode,Segment,ONode,ONode> handler_x=(s,root, tmp)->{
        ONode tmp2 = null;

        if (tmp.count() > 0) {
            tmp2 = new ONode(tmp.cfg()).asArray();//有节点时，才初始化

            if (tmp.isObject()) {
                tmp2.addAll(tmp.obj().values());
            } else {
                tmp2.addAll(tmp.ary());
            }
        }

        return tmp2;
    };
    public static Fun3<ONode,Segment,ONode,ONode> handler_prop=(s,root,tmp)->{
        //.name 指令
        //
        //name
        if (tmp.isObject()) {
            return tmp.getOrNull(s.cmd);
        }

        if (tmp.isArray()) {
            ONode tmp2 = null;
            for (ONode n1 : tmp.ary()) {
                if (n1.isObject()) {
                    ONode n2 = n1.nodeData().object.get(s.cmd);
                    if (n2 != null) {
                        if(tmp2 == null){ //有节点时，才初始化
                            tmp2 =new ONode(tmp.cfg()).asArray();
                        }

                        tmp2.add(n2);
                    }
                }
            }
            return tmp2;
        }

        return null;
    };

    public static Fun3<ONode,Segment,ONode,ONode> handler_fun=(s,root,tmp)->{
        switch (s.cmd) {
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
                    int num = 0;
                    for(ONode n1 : tmp.ary()){
                        sum+=n1.getDouble();
                        num++;
                    }
                    return new ONode(tmp.cfg()).val(sum/num);
                }else {
                    return null;
                }
            }

            case "sum()":{
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
    };

    public static Fun3<ONode,Segment,ONode,ONode> handler_ary_x=(s,root, tmp)->{
        ONode tmp2 = null;
        if (tmp.isArray()) {
            tmp2 = tmp;
        }

        if (tmp.isObject()) {
            tmp2 = new ONode(tmp.cfg()).asArray();
            tmp2.addAll(tmp.obj().values());
        }

        return tmp2;
    };

    public static Fun3<ONode,Segment,ONode,ONode> handler_ary_exp=(s,root, tmp)->{
        ONode tmp2 = tmp;
        if (s.op == null) {
            if (tmp.isObject()) {
                if (tmp.contains(s.left) == false) {
                    tmp2 = null;
                }
            } else if (tmp.isArray()) {
                tmp2 = new ONode(tmp.cfg()).asArray();
                for (ONode n1 : tmp.ary()) {
                    if (n1.contains(s.left)) {
                        tmp2.nodeData().array.add(n1);
                    }
                }
            }
        } else{
            if (tmp.isObject()) {
                if (compare(tmp, tmp.getOrNull(s.left), s.op, s.right) == false) {
                    tmp2 = null;
                }
            } else if (tmp.isArray()) {
                 tmp2 = new ONode(tmp.cfg()).asArray();
                for (ONode n1 : tmp.ary()) {
                    if (compare(n1, n1.getOrNull(s.left), s.op, s.right)) {
                        tmp2.addNode(n1);
                    }
                }
            }
        }

        return tmp2;
    };

    public static Fun3<ONode,Segment,ONode,ONode> handler_ary_multi=(s,root, tmp)->{
        ONode tmp2 = null;

        if(s.cmdAry.indexOf("'")>=0){
            if(tmp.isObject()){
                for (String k : s.nameS) {
                    ONode n1 = tmp.obj().get(k);
                    if (n1 != null) {
                        if(tmp2 == null){
                            tmp2 = new ONode(tmp.cfg()).asArray();
                        }

                        tmp2.addNode(n1);
                    }
                }
            }
        }else{
            if(tmp.isArray()) {
                List<ONode> list2 = tmp.nodeData().array;
                int len2 = list2.size();

                for (int idx : s.indexS) {
                    if (idx >= 0 && idx < len2) {
                        if(tmp2 == null){
                            tmp2 = new ONode(tmp.cfg()).asArray();
                        }

                        tmp2.addNode(list2.get(idx));
                    }
                }
            }
        }

        return tmp2;
    };

    public static Fun3<ONode,Segment,ONode,ONode> handler_ary_range=(s,root, tmp)->{
        if (tmp.isArray()) {
            int count = tmp.count();
            int start = s.start;
            int end = s.end;

            if (start < 0) {//如果是倒数？
                start = count + start;
            }

            if (end == 0) {
                end = count;
            }

            if (end < 0) { //如果是倒数？
                end = count + end;
            }

            if (start < 0) {
                start = 0;
            }
            if (end > count) {
                end = count;
            }

            ONode tmp2 = new ONode(tmp.cfg()).addAll(tmp.ary().subList(start, end));

            return tmp2;
        } else {
            return null;
        }
    };

    public static Fun3<ONode,Segment,ONode,ONode> handler_ary_prop=(s,root, tmp)->{
        ONode tmp2 = null;
        if (s.cmd.startsWith("'")) {
            tmp2 = tmp.getOrNull(s.name);
        } else {
            int idx = s.start;
            if (idx < 0) {
                tmp2 = tmp.getOrNull(tmp.count() + idx);//倒数位
            } else {
                tmp2 = tmp.getOrNull(idx);//正数位
            }
        }

        return tmp2;
    };

    public static class Segment {
        public String cmd;
        public String cmdAry;
        public List<Integer> indexS;
        public List<String> nameS;

        public String name;
        public int start = 0;
        public int end = 0;

        public String left;
        public String op;
        public String right;

        public Fun3<ONode,Segment,ONode,ONode> handler;

        public Segment(String cmd) {
            this.cmd = cmd;
            if (cmd.endsWith("]")) {
                this.cmdAry = cmd.substring(0, cmd.length() - 1).trim();

                if (cmdAry.startsWith("?")) {
                    String s2 = cmdAry.substring(4, cmdAry.length() - 1);//=>@.a == 1, @.a == @.b
                    String[] ss2 = s2.split(" ");
                    left = ss2[0];
                    if (ss2.length == 1) {

                    } else if (ss2.length == 3) {
                        op = ss2[1];
                        right = ss2[2];
                    }
                } else if (cmdAry.indexOf(":") >= 0) {
                    String[] iAry = cmdAry.split(":", -1);
                    start = 0;
                    if (iAry[0].length() > 0) {
                        start = Integer.parseInt(iAry[0]);
                    }
                    end = 0;
                    if (iAry[1].length() > 0) {
                        end = Integer.parseInt(iAry[1]);
                    }
                } else if (cmdAry.indexOf(",") > 0) {
                    if (cmdAry.indexOf("'") >= 0) {
                        nameS = new ArrayList<>();
                        String[] iAry = cmdAry.split(",");
                        for (String i1 : iAry) {
                            i1 = i1.trim();
                            nameS.add(i1.substring(1, i1.length() - 1));
                        }

                    } else {
                        indexS = new ArrayList<>();
                        String[] iAry = cmdAry.split(",");
                        for (String i1 : iAry) {
                            i1 = i1.trim();
                            indexS.add(Integer.parseInt(i1));
                        }
                    }
                } else {
                    if (cmdAry.indexOf("'") >= 0) {
                        name = cmdAry.substring(1, cmdAry.length() - 1);
                    } else if (cmdAry.equals("*") == false) {
                        start = Integer.parseInt(cmdAry);
                    }
                }

            }

            //$指令
            if ("$".equals(this.cmd)) {
                handler = handler_$;
                return;
            }

            //_指令（即..指令）
            if (this.cmd.startsWith("_")) {
                handler = handler_xx;
                return;
            }

            //*指令
            if ("*".equals(this.cmd)) {
                handler = handler_x;
                return;
            }

            //[]指令
            if (this.cmd.endsWith("]")) {
                if ("*".equals(this.cmdAry)) {
                    //[*]指令
                    //
                    handler = handler_ary_x;
                    return;
                } else if (this.cmd.startsWith("?")) {
                    //[?()]指令
                    //
                    handler = handler_ary_exp;
                } else if (this.cmdAry.indexOf(",") > 0) {
                    //[,]指令
                    //
                    //[1,4,6] //['p1','p2']
                    handler = handler_ary_multi;

                } else if (this.cmdAry.indexOf(":") >= 0) {
                    //[:]指令
                    //
                    //[2:4]
                    handler = handler_ary_range;

                } else {
                    //[x]指令
                    //
                    //[2] [-2] ['p1']
                    //
                    handler = handler_ary_prop;
                }
            }
            else if(this.cmd.endsWith(")")) {
                //.fun()指令
                handler = handler_fun;
            } else {
                //.name 指令
                //
                //name
                handler = handler_prop;
            }
        }

        public int length() {
            return cmd.length();
        }

        @Override
        public String toString() {
            return this.cmd;
        }
    }
}