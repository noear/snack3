package org.noear.snack.core;

import org.noear.snack.ONode;

import java.util.Map;

/**
 * Simple json path
 *
 * 支持：
 * .name
 * [index] //负数表示倒取
 * [index,index] //负数表示倒取
 * [start:end] //负数表示倒取
 * */
public class JsonPath {
    public static ONode get(String[] ss, int index, ONode source) {
        ONode tmp = source;
        for (int i = index; i < ss.length; i++) {

            if(tmp == null){
                break;
            }

            String s = ss[i];

            if (s.length() == 0 || "$".equals(s)) {
                continue; //当前节点
            }

            if ("**".equals(s)) {
                ONode tmp2 = new ONode().asArray();
                if (i + 1 < ss.length) {
                    scan(ss[i + 1], tmp, tmp2);
                }

                return get(ss, i + 2, tmp2);
            }

            if("*".equals(s)){
                ONode tmp2 = new ONode().asArray();
                if(tmp.isArray()){
                    tmp2.addAll(tmp.ary());
                }else if(tmp.isObject()){
                    tmp2.addAll(tmp.obj().values());
                }

                return get(ss, i + 1, tmp2);
            }

            if (s.endsWith("]")) {
                String idx_s = s.substring(0, s.length() - 1);

                if ("*".equals(idx_s)) {
                    //[*]
                    ONode tmp2 = new ONode().asArray();
                    if(tmp.isArray()) {
                        for (ONode n1 : tmp.ary()) {
                            ONode n2 = get(ss, i + 1, n1);
                            if (n2.isNull() == false) {
                                tmp2.add(n2);
                            }
                        }
                    }

                    if(tmp.isObject()){
                        for (ONode n1 : tmp.obj().values()) {
                            ONode n2 = get(ss, i + 1, n1);
                            if (n2.isNull() == false) {
                                tmp2.add(n2);
                            }
                        }
                    }

                    return tmp2;
                } else if (idx_s.indexOf(",") > 0) {
                    //[1,4,6] //['p1','p2']
                    ONode tmp2 = new ONode().asArray();
                    String[] iAry = idx_s.split(",");

                    for (String i1 : iAry) {
                        ONode n1 = null;
                        if(i1.startsWith("'")){
                            if(i1.endsWith("'")){
                                n1 = tmp.getOrNull(i1.substring(1,i1.length()-1));
                            }
                        }else {
                            n1 = tmp.getOrNull(Integer.parseInt(i1));
                        }

                        if (n1 != null) {
                            ONode n2 = get(ss, i + 1, n1);
                            if (n2.isNull() == false) {
                                tmp2.add(n2);
                            }
                        }
                    }
                    return tmp2;

                } else if (idx_s.indexOf(":") >= 0) {
                    //[2:4]
                    ONode tmp2 = new ONode().asArray();
                    String[] iAry = idx_s.split(":");
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

                    for (int i1 = start; i1 < end; i1++) {
                        ONode n1 = tmp.getOrNull(i1);

                        if (n1 != null) {
                            ONode n2 = get(ss, i + 1, n1);
                            if (n2.isNull() == false) {
                                tmp2.add(n2);
                            }
                        }
                    }
                    return tmp2;

                } else {
                    // [2] [-2] ['p1']
                    //
                    if(idx_s.startsWith("'")){
                        if (idx_s.endsWith("'")){
                            String k2 = idx_s.substring(1,idx_s.length()-1);
                            tmp = tmp.getOrNull(k2);
                        }else{
                            tmp = null;
                        }
                    }else {
                        int idx = Integer.parseInt(idx_s);
                        if (idx < 0) {
                            tmp = tmp.getOrNull(tmp.count() + idx);//倒数位
                        } else {
                            tmp = tmp.getOrNull(idx);//正数位
                        }
                    }
                }
            } else {
                //name
                if(tmp.isArray()){
                    ONode tmp2  =new ONode().asArray();
                    for(ONode n1: tmp.ary()){
                        if(n1.isObject()) {
                            ONode n2 = n1.getOrNull(s);
                            if (n2 != null) {
                                tmp2.add(n2);
                            }
                        }
                    }
                    return get(ss,i+1,tmp2);
                }else {
                    if(tmp.isObject()) {
                        tmp = tmp.getOrNull(s);
                    }else{
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

    private static void scan(String name, ONode source, ONode target) {
        if (source.isObject()) {
            for (Map.Entry<String, ONode> kv : source.obj().entrySet()) {
                if (name.equals(kv.getKey())) {
                    target.add(kv.getValue());
                }else{
                    scan(name, kv.getValue(), target);
                }
            }
            return;
        }

        if (source.isArray()) {
            for (ONode n1 : source.ary()) {
                scan(name, n1, target);
            }
            return;
        }

        if("*".equals(name)){
            target.add(source);
        }
    }
}
