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
import java.util.regex.Pattern;

/**
 * json path
 *
 * */
public class JsonPath {
    private static int _cacheSize = 1024;
    private static Map<String,JsonPath> _jpathCache = new HashMap<>(128);

    public static ONode eval(ONode source, String jpath,  boolean useStandard, boolean cacheJpath) {
        return eval(source, jpath, useStandard, cacheJpath, CRUD.GET);
    }

    public static ONode eval(ONode source, String jpath,  boolean useStandard, boolean cacheJpath, CRUD crud) {
        tlCache.get().clear();
        return evalDo(source, jpath, cacheJpath, useStandard, crud);
    }

    private static ONode evalDo(ONode source, String jpath, boolean cacheJpath, boolean useStandard, CRUD crud) {
        //解析出指令
        JsonPath jsonPath = null;
        if (cacheJpath) {
            jsonPath = _jpathCache.get(jpath);
            if (jsonPath == null) {
                synchronized (jpath.intern()) {
                    jsonPath = _jpathCache.get(jpath);
                    if (jsonPath == null) {
                        jsonPath = compile(jpath);
                        if (_jpathCache.size() < _cacheSize) {
                            _jpathCache.put(jpath, jsonPath);
                        }
                    }
                }
            }
        } else {
            jsonPath = compile(jpath);
        }


        //执行指令
        return exec(jsonPath, source, useStandard, crud);
    }

    /**
     * 分析路径
     *
     * @param parentPath 父路径
     * @param parentNode 父节点
     * */
    public static void resolvePath(String parentPath, ONode parentNode) {
        parentNode.attrSet("$PATH", parentPath);

        if (parentNode.isArray()) {
            for (int i = 0; i < parentNode.count(); i++) {
                resolvePath(parentPath + "[" + i + "]", parentNode.get(i));
            }
        } else if (parentNode.isObject()) {
            for (Map.Entry<String, ONode> kv : parentNode.obj().entrySet()) {
                //用[]可支持有空隔的 key 或 特殊符号的 key
                resolvePath(parentPath + "['" + kv.getKey() + "']", kv.getValue());
            }
        }
    }

    /**
     * 提取路径
     * */
    public static void extractPath(List<String> paths, ONode oNode) {
        String path = oNode.attrGet("$PATH");

        if (StringUtil.isEmpty(path)) {
            if (oNode.isArray()) {
                for (int i = 0; i < oNode.count(); i++) {
                    extractPath(paths, oNode.get(i));
                }
            } else if (oNode.isObject()) {
                for (Map.Entry<String, ONode> kv : oNode.obj().entrySet()) {
                    extractPath(paths, kv.getValue());
                }
            }
        } else {
            paths.add(path);
        }
    }

    private static final ThData<CharBuffer> tlBuilder = new ThData<>(()->new CharBuffer());
    private static final ThData<TmpCache> tlCache = new ThData<>(()->new TmpCache());

    /**
     * 清空线程缓存
     * */
    public static void clear(){
        tlBuilder.remove();
        tlCache.remove();
    }

    /**
     * 编译jpath指令
     * */
    private List<Segment> segments;
    private JsonPath(){
        segments  = new ArrayList<>();
    }

    private static JsonPath compile(String jpath) {
        //将..替换为.^ 方便解析（用_x替代为深度扫描）
        String jpath2 = jpath.replace("..", ".^");
        JsonPath jsonPath = new JsonPath();

        char token = 0;
        char c = 0;
        char c_last = 0;
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
                        if (buffer.length() > 0 && c_last != '^') {
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

            c_last = c;
        }

        return jsonPath;
    }

    /**
     * 执行jpath指令
     * */
    private static ONode exec(JsonPath jsonPath, ONode source, boolean useStandard, CRUD crud) {
        ONode tmp = source;
        Segment last = null; //最后执行的片段（做为前片段参数）
        boolean branch_do = false; //分支标志
        boolean regroup = false; //重组标志
        for (Segment s : jsonPath.segments) {
            if (tmp == null) {//多次转换后，可能为null
                break;
            }

            if (branch_do && (useStandard || s.cmdAry != null)) { //..a[x] 下属进行分支处理 //s.cmdAry != null
                ONode tmp2 = new ONode(null, source.options()).asArray();

                for (ONode n1 : tmp.ary()) {
                    ONode n2 = s.handler.run(last, s, regroup, source, n1, useStandard, crud);
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
                }

                tmp = tmp2;

                if (useStandard == false) {
                    branch_do = false;
                }
            } else {
                tmp = s.handler.run(last, s, regroup, source, tmp, useStandard, crud);
                branch_do = s.cmdHasUnline;
            }

            if (s.regroup) {
                regroup = true;
            } else if (s.ranged) {
                regroup = false;//如果有匹间选择，则重组解除
            }

            last = s;
        }

        if (tmp == null) {
            return new ONode(null, source.options());
        } else {
            return tmp;
        }
    }

    /**
     * 深度扫描
     * */
    private static void scanByExpr(ONode source, List<ONode> target,Segment bef, Segment s, Boolean regroup, ONode root, ONode tmp, Boolean usd, CRUD crud) {
        if (source.isObject()) {
            ONode tmp2 = handler_ary_exp.run(bef, s, regroup, root, source, usd, crud);
            if (tmp2 != null) {
                target.add(tmp2);
            }

            for (Map.Entry<String, ONode> kv : source.obj().entrySet()) {
                scanByExpr(kv.getValue(), target, bef, s, regroup, root, tmp, usd, crud);
            }
            return;
        }

        if (source.isArray()) {
            for (ONode n1 : source.ary()) {
                scanByExpr(n1, target, bef, s, regroup, root, tmp, usd, crud);
            }
            return;
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
    private static boolean compare(ONode root, ONode parent, ONode leftO, String op, String right, boolean useStandard, CRUD crud) {
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
                rightO = evalDo(root, right, true, useStandard, crud);
                tlCache.get().put(right, rightO);
            }
        }

        if(right.startsWith("@")){
            rightO = evalDo(parent,right,true, useStandard, crud);
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

    /**
     * "$"
     * */
    private static Resolver handler_$=(bef, s, regroup, root, tmp, usd, orNew)->{ return tmp;};

    /**
     * ".."
     * */
    private static Resolver handler_xx=(bef, s, regroup, root, tmp, usd, orNew)-> {
        if (s.name.length() > 0) {
            ONode tmp2 = new ONode(null, root.options()).asArray();
            if ("*".equals(s.name)) {
                scanByAll(s.name, tmp, true, tmp2.ary());
            } else if (s.name.startsWith("?")) {
                scanByExpr(tmp, tmp2.ary(), bef, s, regroup, root, tmp, usd, orNew);
            } else {
                scanByName(s.name, tmp, tmp2.ary());
            }

            if (tmp2.count() > 0) {
                return tmp2;
            }
        }

        return null;
    };

    /**
     * ".*"
     * */
    private static Resolver handler_x=(bef, s, regroup, root, tmp, usd, orNew)->{
        ONode tmp2 = null;

        if (tmp.count() > 0) {
            tmp2 = new ONode(null, tmp.options()).asArray();//有节点时，才初始化

            if (tmp.isObject()) {
                tmp2.addAll(tmp.obj().values());
            } else if(tmp.isArray()){
                if(regroup){
                    for(ONode n1 : tmp.ary()){
                        if(n1.isObject()){
                            tmp2.addAll(n1.obj().values());
                        }else{
                            tmp2.addAll(n1.ary());
                        }
                    }
                }else {
                    tmp2.addAll(tmp.ary());
                }
            }
        }

        return tmp2;
    };

    /**
     * ".name"指令
     * */
    private static Resolver handler_prop=(bef, s, regroup, root, tmp, usd, crud)->{

        if (tmp.isObject()) {
            if(crud == CRUD.GET_OR_NEW){
                return tmp.getOrNew(s.cmd);
            }else {
                return tmp.getOrNull(s.cmd);
            }
        }

        if (tmp.isArray()) {
            ONode tmp2 = new ONode(null, tmp.options()).asArray();

            for (ONode n1 : tmp.ary()) {
                if (n1.isObject()) {
                    if (crud == CRUD.GET_OR_NEW) {
                        tmp2.add(n1.getOrNew(s.cmd));
                    } else {
                        ONode n2 = n1.nodeData().object.get(s.cmd);
                        if (n2 != null) {
                            tmp2.add(n2);
                        }
                    }
                }

                if (regroup && n1.isArray()) {
                    for (ONode n2 : n1.ary()) {
                        if (n2.isObject()) {
                            if (crud == CRUD.GET_OR_NEW) {
                                tmp2.add(n2.getOrNew(s.cmd));
                            } else {
                                ONode n3 = n2.nodeData().object.get(s.cmd);
                                if (n3 != null) {
                                    tmp2.add(n3);
                                }
                            }
                        }
                    }
                }
            }

            return tmp2;
        }

        if(crud == CRUD.GET_OR_NEW && tmp.isNull()){
            return tmp.getOrNew(s.cmd);
        }

        return null;
    };

    /**
     * ".fun()" 指令
     * */
    private static Resolver handler_fun=(bef, s, regroup, root, tmp, usd, crud)->{
        switch (s.cmd) {
            case "size()": {
                return new ONode(null,tmp.options()).val(tmp.count());
            }
            case "length()": {
                if (tmp.isValue()) {
                    return new ONode(null,tmp.options()).val(tmp.getString().length());
                } else {
                    return new ONode(null,tmp.options()).val(tmp.count());
                }
            }
            case "keys()": {
                if (tmp.isObject()) {
                    return new ONode(null,tmp.options()).addAll(tmp.obj().keySet());
                } else {
                    return null;
                }
            }
            case "min()": {
                if (tmp.isArray()) {
                    if(tmp.count() == 0){
                        return null;
                    }

                    ONode min_n = null;
                    for (ONode n1 : tmp.ary()) {
                        if (n1.isValue()) {
                            if (min_n == null) {
                                min_n = n1;
                            } else if (n1.getDouble() < min_n.getDouble()) {
                                min_n = n1;
                            }
                        }

                        if (regroup && n1.isArray()) {
                            for (ONode n2 : n1.ary()) {
                                if (n2.isValue()) {
                                    if (min_n == null) {
                                        min_n = n2;
                                    } else if (n2.getDouble() < min_n.getDouble()) {
                                        min_n = n2;
                                    }
                                }
                            }
                        }
                    }
                    return min_n;
                }

                return null;
            }

            case "max()": {
                if (tmp.isArray()) {
                    if(tmp.count() == 0){
                        return null;
                    }

                    ONode max_n = null;
                    for (ONode n1 : tmp.ary()) {
                        if (n1.isValue()) {
                            if (max_n == null) {
                                max_n = n1;
                            } else if (n1.getDouble() > max_n.getDouble()) {
                                max_n = n1;
                            }
                        }

                        if (regroup && n1.isArray()) {
                            for (ONode n2 : n1.ary()) {
                                if (n2.isValue()) {
                                    if (max_n == null) {
                                        max_n = n2;
                                    } else if (n2.getDouble() > max_n.getDouble()) {
                                        max_n = n2;
                                    }
                                }
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
                    if(tmp.count() == 0){
                        return null;
                    }

                    double sum = 0;
                    int num = 0;
                    for (ONode n1 : tmp.ary()) {
                        if (n1.isValue()) {
                            sum += n1.getDouble();
                            num++;
                        }

                        if (regroup && n1.isArray()) {
                            for (ONode n2 : n1.ary()) {
                                if (n2.isValue()) {
                                    sum += n2.getDouble();
                                    num++;
                                }
                            }
                        }
                    }

                    if (num > 0) {
                        return new ONode(null,tmp.options()).val(sum / num);
                    }
                }

                return null;
            }

            case "sum()": {
                if (tmp.isArray()) {
                    if(tmp.count() == 0){
                        return null;
                    }

                    double sum = 0;
                    for (ONode n1 : tmp.ary()) {
                        if (n1.isValue()) {
                            sum += n1.getDouble();
                        }

                        if (regroup && n1.isArray()) {
                            for (ONode n2 : n1.ary()) {
                                if (n2.isValue()) {
                                    sum += n2.getDouble();
                                }
                            }
                        }
                    }
                    return new ONode(null,tmp.options()).val(sum);
                } else {
                    return null;
                }
            }

            case "first()": {
                if (tmp.isArray()) {
                    if(tmp.count() == 0){
                        return null;
                    }

                    ONode n1 = tmp.get(0);

                    if (regroup && n1.isArray()) {
                        return n1.get(0);
                    } else {
                        return n1;
                    }
                } else {
                    return null;
                }
            }

            case "last()": {
                if (tmp.isArray()) {
                    if(tmp.count() == 0){
                        return null;
                    }

                    ONode n1 = tmp.get(tmp.count() - 1);

                    if (regroup && n1.isArray()) {
                        return n1.get(n1.count() - 1);
                    } else {
                        return n1;
                    }
                } else {
                    return null;
                }
            }

            default:
                return null;
        }
    };

    /**
     * ".*]" 指令
     * */
    private static Resolver handler_ary_x=(bef, s, regroup, root, tmp, usd, crud)-> {
        ONode tmp2 = null;

        if (tmp.isArray()) {
            if(regroup){
                tmp2 = new ONode(null,tmp.options()).asArray();
                for (ONode n1 : tmp.ary()) {
                    if (n1.isObject()) {
                        tmp2.addAll(n1.obj().values());
                    }else{
                        tmp2.addAll(n1.ary());
                    }
                }
            } else {
                tmp2 = tmp;
            }
        }

        if (tmp.isObject()) {
            tmp2 = new ONode(null,tmp.options()).asArray();
            tmp2.addAll(tmp.obj().values());
        }

        return tmp2;
    };

    /**
     * ".[?()]" 指令
     * */
    private static Resolver handler_ary_exp=(bef, s, regroup, root, tmp, usd, crud)->{
        ONode tmp2 = tmp;
        if (s.op == null) {
            if (tmp.isObject()) {
                if(evalDo(tmp,s.left,true, usd, crud).isNull()){
                    return null;
                }
            } else if (tmp.isArray()) {
                tmp2 = new ONode(null,tmp.options()).asArray();

                for (ONode n1 : tmp.ary()) {
                    if(n1.isObject() && evalDo(n1,s.left,true, usd, crud).isNull() == false){
                        tmp2.nodeData().array.add(n1);
                    }

                    if(regroup && n1.isArray()){
                        for(ONode n2 : n1.ary()){
                            if(n2.isObject() && evalDo(n2,s.left,true, usd, crud).isNull() == false){
                                tmp2.nodeData().array.add(n2);
                            }
                        }
                    }
                }
            } else{
                return null;
            }
        } else {
            if (tmp.isObject()) {
                if ("@".equals(s.left)) {
                    return null;
                }

                ONode leftO = evalDo(tmp, s.left, true, usd, crud);
                if (compare(root, tmp, leftO, s.op, s.right, usd, crud) == false) {
                    return null;
                }
            } else if (tmp.isArray()) {
                tmp2 = new ONode(null,tmp.options()).asArray();
                if ("@".equals(s.left)) {
                    for (ONode n1 : tmp.ary()) {
                        if (n1.isArray()) {
                            for (ONode n2 : n1.ary()) {
                                if (compare(root, n2, n2, s.op, s.right, usd, crud)) {
                                    tmp2.addNode(n2);
                                }
                            }
                        } else {
                            if (compare(root, n1, n1, s.op, s.right, usd, crud)) {
                                tmp2.addNode(n1);
                            }
                        }
                    }
                } else {
                    for (ONode n1 : tmp.ary()) {
                        if (n1.isArray()) {
                            for (ONode n2 : n1.ary()) {
                                ONode leftO = evalDo(n2, s.left, true, usd, crud);

                                if (compare(root, n2, leftO, s.op, s.right, usd, crud)) {
                                    tmp2.addNode(n2);
                                }
                            }
                        } else {
                            ONode leftO = evalDo(n1, s.left, true, usd, crud);

                            if (compare(root, n1, leftO, s.op, s.right, usd, crud)) {
                                tmp2.addNode(n1);
                            }
                        }
                    }
                }
            } else if (tmp.isValue()) {
                if ("@".equals(s.left)) {
                    if (compare(root, tmp, tmp, s.op, s.right, usd, crud) == false) {
                        return null;
                    }
                }
            }
        }

        return tmp2;
    };

    /**
     * "[$.xxx] [@.xxx]" 指令
     * */
    private static Resolver handler_ary_ref=(bef, s, regroup, root, tmp, usd, crud)-> {
        ONode tmp2 = null;

        if(tmp.isObject()) {
            if (s.cmdAry.startsWith("$")) {
                tmp2 = evalDo(root, s.cmdAry, true, usd, crud);
            } else {
                tmp2 = evalDo(tmp, s.cmdAry, true, usd, crud);
            }

            if (tmp2.isValue()) {
                tmp2 = tmp.get(tmp2.getString());
            }else{
                tmp2 = null;
            }
        }

        return tmp2;
    };

    /**
     * "[1,4,6] //['p1','p2']" 指令
     * */
    private static Resolver handler_ary_multi=(bef, s, regroup, root, tmp, usd, crud)->{
        ONode tmp2 = null;

        if(s.cmdAry.indexOf("'")>=0){
            if(tmp.isObject()){
                for (String k : s.nameS) {
                    ONode n1 = tmp.obj().get(k);
                    if (n1 != null) {
                        if(tmp2 == null){
                            tmp2 = new ONode(null,tmp.options()).asArray();
                        }

                        tmp2.addNode(n1);
                    }
                }
            }

            //不知道，该不访加::??
            if(tmp.isArray()) {
                tmp2 = new ONode(null,tmp.options()).asArray();

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
                            tmp2 = new ONode(null,tmp.options()).asArray();
                        }

                        tmp2.addNode(list2.get(idx));
                    }
                }
            }
        }

        return tmp2;
    };

    /**
     * "[2:4]" 指令
     * */
    private static Resolver handler_ary_range=(bef, s, regroup, root, tmp, usd, crud)->{
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

            return new ONode(null,tmp.options()).addAll(tmp.ary().subList(start, end));
        } else {
            return null;
        }
    };

    /**
     * "[2] [-2] ['p1']" 索引指令
     * */
    private static Resolver handler_ary_prop=(bef, s, regroup, root, tmp, usd, crud)-> {
        //如果是value,会返回null
        if (s.cmdHasQuote) {
            //['p1']
            if(tmp.isObject()) {
                return tmp.getOrNull(s.name);
            }

            //不知道，该不访加::??
            if(tmp.isArray()) {
                ONode tmp2 = new ONode(null,tmp.options()).asArray();
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
            //[1]
            if (regroup) {
                ONode tmp2 = new ONode(null,tmp.options()).asArray();
                for (ONode n1 : tmp.ary()) {
                    ONode n2 = null;
                    if (s.start < 0) {
                        if (crud == CRUD.GET_OR_NEW) {
                            n2 = n1.getOrNew(n1.count() + s.start);//倒数位
                        } else {
                            n2 = n1.getOrNull(n1.count() + s.start);//倒数位
                        }
                    } else {
                        if (crud == CRUD.GET_OR_NEW) {
                            n2 = n1.getOrNew(s.start);
                        } else {
                            n2 = n1.getOrNull(s.start);//正数位
                        }
                    }

                    if (n2 != null) {
                        tmp2.add(n2);
                    }
                }

                return tmp2;
            } else {
                if (s.start < 0) {
                    if (crud == CRUD.GET_OR_NEW) {
                        return tmp.getOrNew(tmp.count() + s.start);//倒数位
                    } else {
                        return tmp.getOrNull(tmp.count() + s.start);//倒数位
                    }
                } else {
                    if (crud == CRUD.GET_OR_NEW) {
                        return tmp.getOrNew(s.start);
                    } else {
                        return tmp.getOrNull(s.start);//正数位
                    }
                }
            }
        }
    };

    @FunctionalInterface
    private interface Resolver {
        /**
         * @param bef     之前指令片断
         * @param s       当前指令片断
         * @param regroup 组重的
         * @param root    根节点
         * @param tmp     上一次处理结果
         * @param usd     是否用标准处理
         * @param crud    命令（crud）
         */
        ONode run(Segment bef, Segment s, Boolean regroup, ONode root, ONode tmp, Boolean usd, CRUD crud);
    }


    private static class Segment {
        public final String cmd;
        public String cmdAry;
        public final boolean cmdHasQuote; //是否有引号
        public final boolean cmdHasUnline; //是否为**（指令里用_表达）
        public final boolean regroup;//是否有重组（外层会再套个数组）
        public List<Integer> indexS;
        public List<String> nameS;

        public String name;
        public int start = 0;
        public int end = 0;
        public boolean ranged = false;

        public String left;
        public String op;
        public String right;

        public Resolver handler;

        public Segment(String test) {
            cmd = test.trim();
            cmdHasQuote = cmd.indexOf("'")>=0;
            cmdHasUnline = cmd.startsWith("^");

            regroup = cmd.contains("?") || cmd.startsWith("*");

            if(cmdHasUnline){
                name = cmd.substring(1);
            }

            if (cmd.endsWith("]")) {
                if(cmdHasUnline){
                    //如要前面是..
                    this.cmdAry = cmd.substring(1, cmd.length() - 1).trim();
                }else{
                    this.cmdAry = cmd.substring(0, cmd.length() - 1).trim();
                }

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
                    ranged = true;
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
                        ranged = true;
                    }
                }
            }

            //$指令
            if ("$".equals(this.cmd) || "@".equals(this.cmd)) {
                handler = handler_$;
                return;
            }

            //_指令（即..指令）
            if (this.cmd.startsWith("^")) {
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

    ////////////////

    public static enum CRUD {
        GET, //获取
        GET_OR_NEW, //获取或新构
        REMOVE, // 移除
    }
}