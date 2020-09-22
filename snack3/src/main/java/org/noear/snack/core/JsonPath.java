package org.noear.snack.core;

import org.noear.snack.ONode;
import org.noear.snack.OValue;
import org.noear.snack.OValueType;
import org.noear.snack.core.exts.*;
import org.noear.snack.core.utils.IOUtil;
import org.noear.snack.core.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * json path
 *
 * */
public class JsonPath {
    private static int _cacheSize = 1024;
    private static Map<String,JsonPath> _jpathCache = new HashMap<>(128);

    public static ONode eval(ONode source, String jpath,  boolean useStandard, boolean cacheJpath) {
        tlCache.get().clear();
        return do_get(source, jpath, cacheJpath, useStandard);
    }

    private static ONode do_get(ONode source, String jpath, boolean cacheJpath, boolean useStandard) {
        //解析出指令
        JsonPath jsonPath = null;
        if (cacheJpath) {
            jsonPath = _jpathCache.get(jpath);
            if (jsonPath == null) {
                synchronized (jpath.intern()) {
                    jsonPath = _jpathCache.get(jpath);
                    if (jsonPath == null) {
                        jsonPath = compile(jpath);
                        if(_jpathCache.size() < _cacheSize) {
                            _jpathCache.put(jpath, jsonPath);
                        }
                    }
                }
            }
        } else {
            jsonPath = compile(jpath);
        }


        //执行指令
        return exec(jsonPath, source, useStandard);
    }

    private static final ThData<CharBuffer> tlBuilder = new ThData<>(()->new CharBuffer());
    private static final ThData<TmpCache> tlCache = new ThData<>(()->new TmpCache());

    /**
     * 编译jpath指令
     * */
    private List<Segment> segments;
    private JsonPath(){
        segments  = new ArrayList<>();
    }

    private static JsonPath compile(String jpath) {
        //将..替换为._ 方便解析（用_x替代为深度扫描）
        String jpath2 = jpath.replace("..", "._");
        JsonPath jsonPath = new JsonPath();

        char token = 0;
        char c = 0;
        CharBuffer buffer = tlBuilder.get();
        buffer.setLength(0);
        CharReader reader = new CharReader(jpath2);
        while (true) {
            c = reader.next();

            if (c == IOUtil.EOI) {
                if (buffer.length() > 0) {
                    jsonPath.segments.add(new Segment(buffer.toString()));
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
                            jsonPath.segments.add(new Segment(buffer.toString()));
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
                            jsonPath.segments.add(new Segment(buffer.toString()));
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
                            jsonPath.segments.add(new Segment(buffer.toString()));
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

        return jsonPath;
    }

    /**
     * 执行jpath指令
     * */
    private static ONode exec(JsonPath jsonPath, ONode source, boolean useStandard) {
        ONode tmp = source;
        boolean branch_do = false;
        for (Segment s : jsonPath.segments) {
            if (tmp == null) {//多次转换后，可能为null
                break;
            }

            if (branch_do && (useStandard || s.cmdAry != null)) { //..a[x] 下属进行分支处理 //s.cmdAry != null
                ONode tmp2 = new ONode().asArray();

                Consumer<ONode> act1 = (n1) -> {
                    ONode n2 = s.handler.run(s, source, n1, useStandard);
                    if (n2 != null) {
                        if (s.cmdAry != null) {
                            if (n2.isArray()) {
                                tmp2.addAll(n2.ary());
                            } else {
                                tmp2.addNode(n2);
                            }
                        } else {
                            tmp2.addNode(n2);
                        }
                    }
                };

                tmp.ary().forEach(act1);

                tmp = tmp2;

                if (useStandard == false) {
                    branch_do = false;
                }
            } else {
                tmp = s.handler.run(s, source, tmp, useStandard);
                branch_do = s.cmdHasUnline;
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
    private static void scanByName(String name, ONode source, List<ONode> target) {
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

    private static void scanByAll(String name, ONode source, boolean isRoot, List<ONode> target) {
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
    private static boolean compare(ONode root, ONode parent, ONode leftO, String op, String right, boolean useStandard) {
        if (leftO == null) {
            return false;
        }

        if (leftO.isValue() == false || leftO.val().isNull()) {
            return false;
        }

        OValue left = leftO.val();
        ONode  rightO = null;

        if(right.startsWith("$")) {
            //全局描扫的数据，进行缓存
            rightO = tlCache.get().get(right);
            if (rightO == null) {
                rightO = do_get(root, right, true, useStandard);
                tlCache.get().put(right, rightO);
            }
        }

        if(right.startsWith("@")){
            rightO = do_get(parent,right,true, useStandard);
        }

        if(rightO != null) {
            if (rightO.isValue()) {
                if (rightO.val().type() == OValueType.String) {
                    right = "'" + rightO.getString() + "'";
                } else {
                    right = rightO.getDouble() + "";
                }
            } else {
                right = null;
            }
        }

        switch (op) {
            case "==": {
                if(right == null){
                    return false;
                }

                if (right.startsWith("'")) {
                    return left.getString().equals(right.substring(1, right.length() - 1));
                } else {
                    return left.getDouble() == Double.parseDouble(right);
                }
            }
            case "!=": {
                if(right == null){
                    return false;
                }

                if (right.startsWith("'")) {
                    return left.getString().equals(right.substring(1, right.length() - 1)) == false;
                } else {
                    return left.getDouble() != Double.parseDouble(right);
                }
            }
            case "<": {
                if (right == null) {
                    return false;
                }

                return left.getDouble() < Double.parseDouble(right);
            }
            case "<=": {
                if (right == null) {
                    return false;
                }
                return left.getDouble() <= Double.parseDouble(right);
            }
            case ">": {
                if (right == null) {
                    return false;
                }
                return left.getDouble() > Double.parseDouble(right);
            }
            case ">=": {
                if (right == null) {
                    return false;
                }
                return left.getDouble() >= Double.parseDouble(right);
            }
            case "=~": {
                if(right == null){
                    return false;
                }
                int end = right.lastIndexOf('/');
                String exp = right.substring(1, end);
                return regex(right, exp).matcher(left.getString()).find();
            }
            case "in": {
                if(right == null){
                    Object val = left.getRaw();
                    for(ONode n1 : rightO.ary()){
                        if(n1.val().getRaw().equals(val)){
                            return true;
                        }
                    }
                    return false;
                }else{
                    if (right.indexOf("'") > 0) {
                        return getStringAry(right).contains(left.getString());
                    } else {
                        return getDoubleAry(right).contains(left.getDouble());
                    }
                }

            }
            case "nin": {
                if (right == null) {
                    Object val = left.getRaw();
                    for(ONode n1 : rightO.ary()){
                        if(n1.val().getRaw().equals(val)){
                            return false;
                        }
                    }
                    return true;
                } else {
                    if (right.indexOf("'") > 0) {
                        return getStringAry(right).contains(left.getString()) == false;
                    } else {
                        return getDoubleAry(right).contains(left.getDouble()) == false;
                    }
                }
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

    private static Fun4<ONode,Segment,ONode,ONode,Boolean> handler_$=(s, root, tmp, usd)->{ return tmp;};
    private static Fun4<ONode,Segment,ONode,ONode,Boolean> handler_xx=(s, root, tmp, usd)-> {

        if (s.name.length() > 0) {
            ONode tmp2 = new ONode().asArray();
            if ("*".equals(s.name)) {
                scanByAll(s.name, tmp, true, tmp2.ary());
            } else {
                scanByName(s.name, tmp, tmp2.ary());
            }

            if (tmp2.count() > 0) {
                return tmp2;
            }
        }

        return null;
    };

    private static Fun4<ONode,Segment,ONode,ONode,Boolean> handler_x=(s, root, tmp, usd)->{
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
    private static Fun4<ONode,Segment,ONode,ONode,Boolean> handler_prop=(s, root, tmp, usd)->{
        //.name 指令
        //
        //name
        if (tmp.isObject()) {
            return tmp.getOrNull(s.cmd);
        }

        if (tmp.isArray()) {
            ONode tmp2 =  new ONode(tmp.cfg()).asArray();
            for (ONode n1 : tmp.ary()) {
                if (n1.isObject()) {
                    ONode n2 = n1.nodeData().object.get(s.cmd);
                    if (n2 != null) {
                        tmp2.add(n2);
                    }
                }
            }
            return tmp2;
        }

        return null;
    };

    private static Fun4<ONode,Segment,ONode,ONode,Boolean> handler_fun=(s, root, tmp, usd)->{
        switch (s.cmd) {
            case "size()":{
                return new ONode(tmp.cfg()).val(tmp.count());
            }
            case "length()":{
                if(tmp.isValue()){
                    return new ONode(tmp.cfg()).val(tmp.getString().length());
                }else{
                    return new ONode(tmp.cfg()).val(tmp.count());
                }
            }
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

//                if (tmp.isValue()) {
//                    return tmp;
//                }

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

//                if(tmp.isValue()){
//                    return tmp;
//                }

                return null;
            }

            case "avg()": {
                if (tmp.isArray()) {
                    double sum = 0;
                    int num = 0;
                    for (ONode n1 : tmp.ary()) {
                        if (n1.isValue()) {
                            sum += n1.getDouble();
                            num++;
                        }
                    }

                    if(num>0) {
                        return new ONode(tmp.cfg()).val(sum / num);
                    }
                }

                return null;
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

    private static Fun4<ONode,Segment,ONode,ONode,Boolean> handler_ary_x=(s, root, tmp, usd)->{
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

    private static Fun4<ONode,Segment,ONode,ONode,Boolean> handler_ary_exp=(s, root, tmp, usd)->{
        ONode tmp2 = tmp;
        if (s.op == null) {
            if (tmp.isObject()) {
                if(do_get(tmp,s.left,true, usd).isNull()){
                    return null;
                }
            } else if (tmp.isArray()) {
                tmp2 = new ONode(tmp.cfg()).asArray();
                for (ONode n1 : tmp.ary()) {
                    if(do_get(n1,s.left,true, usd).isNull() == false){
                        tmp2.nodeData().array.add(n1);
                    }
                }
            }
        } else {
            if (tmp.isObject()) {
                if("@".equals(s.left)){
                    return null;
                }

                ONode leftO = do_get(tmp,s.left,true, usd);
                if (compare(root, tmp, leftO, s.op, s.right, usd) == false) {
                    return null;
                }
            } else if (tmp.isArray()) {
                tmp2 = new ONode(tmp.cfg()).asArray();
                if("@".equals(s.left)){
                    for (ONode n1 : tmp.ary()) {
                        if (compare(root, n1, n1, s.op, s.right, usd)) {
                            tmp2.addNode(n1);
                        }
                    }
                }else {
                    for (ONode n1 : tmp.ary()) {
                        ONode leftO = do_get(n1,s.left,true, usd);

                        if (compare(root, n1, leftO, s.op, s.right, usd)) {
                            tmp2.addNode(n1);
                        }
                    }
                }
            } else if(tmp.isValue()){
                if("@".equals(s.left)){
                    if (compare(root, tmp, tmp, s.op, s.right, usd) == false) {
                        return null;
                    }
                }
            }
        }

        return tmp2;
    };

    private static Fun4<ONode,Segment,ONode,ONode,Boolean> handler_ary_ref=(s, root, tmp, usd)-> {
        ONode tmp2 = null;

        if(tmp.isObject()) {
            if (s.cmdAry.startsWith("$")) {
                tmp2 = do_get(root, s.cmdAry, true, usd);
            } else {
                tmp2 = do_get(tmp, s.cmdAry, true, usd);
            }

            if (tmp2.isValue()) {
                tmp2 = tmp.get(tmp2.getString());
            }else{
                tmp2 = null;
            }
        }

        return tmp2;
    };

    private static Fun4<ONode,Segment,ONode,ONode,Boolean> handler_ary_multi=(s, root, tmp, usd)->{
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

            //不知道，该不访加::??
            if(tmp.isArray()) {
                tmp2 = new ONode(tmp.cfg()).asArray();

                for(ONode tmp1 : tmp.ary()){
                    if(tmp1.isObject()){
                        for (String k : s.nameS) {
                            ONode n1 = tmp1.obj().get(k);
                            if (n1 != null) {
                                tmp2.addNode(n1);
                            }
                        }
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

    private static Fun4<ONode,Segment,ONode,ONode,Boolean> handler_ary_range=(s, root, tmp, usd)->{
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

            return new ONode(tmp.cfg()).addAll(tmp.ary().subList(start, end));
        } else {
            return null;
        }
    };

    private static Fun4<ONode,Segment,ONode,ONode,Boolean> handler_ary_prop=(s, root, tmp, usd)-> {
        //如果是value,会返回null
        if (s.cmdHasQuote) {
            if(tmp.isObject()) {
                return tmp.getOrNull(s.name);
            }

            //不知道，该不访加::??
            if(tmp.isArray()) {
                ONode tmp2 = new ONode(tmp.cfg()).asArray();
                for (ONode n1 : tmp.ary()) {
                    if (n1.isObject()) {
                        ONode n2 = n1.nodeData().object.get(s.name);
                        if (n2 != null) {
                            tmp2.add(n2);
                        }
                    }
                }
                return tmp2;
            }

            return null;
        } else {
            if (s.start < 0) {
                return tmp.getOrNull(tmp.count() + s.start);//倒数位
            } else {
                return tmp.getOrNull(s.start);//正数位
            }
        }
    };

    private static class Segment {
        public String cmd;
        public String cmdAry;
        public boolean cmdHasQuote;
        public boolean cmdHasUnline;
        public List<Integer> indexS;
        public List<String> nameS;

        public String name;
        public int start = 0;
        public int end = 0;

        public String left;
        public String op;
        public String right;

        public Fun4<ONode,Segment,ONode,ONode,Boolean> handler;

        public Segment(String test) {
            cmd = test.trim();
            cmdHasQuote = cmd.indexOf("'")>=0;
            cmdHasUnline = cmd.startsWith("_");

            if(cmdHasUnline){
                name = cmd.substring(1);
            }

            if (cmd.endsWith("]")) {
                this.cmdAry = cmd.substring(0, cmd.length() - 1).trim();

                if (cmdAry.startsWith("?")) {
                    String s2 = cmdAry.substring(2, cmdAry.length() - 1);//=>@.a == 1, @.a ==
                    String[] ss2 = s2.split(" ");
                    left = ss2[0];

                    if (ss2.length == 3) {
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
                    } else if (StringUtil.isInteger(cmdAry)) {
                        start = Integer.parseInt(cmdAry);
                    }
                }

            }

            //$指令
            if ("$".equals(this.cmd) || "@".equals(this.cmd)) {
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

                } else if(this.cmdAry.startsWith("$.") || this.cmdAry.startsWith("@.")){
                    //[$.xxx] [@.xxx]
                    handler = handler_ary_ref;
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
