package org.near.snack3.core.exts;

import org.near.snack3.ONode;
import org.near.snack3.core.Constants;

public class ObjWrap {
    public Constants cfg;
    public Object  obj;
    public ONode rst;
    public Class<?> clz;

    public ObjWrap(Constants _cfg, Object _obj){
        cfg = _cfg;
        obj = _obj;
        clz = obj.getClass();
        rst = new ONode(cfg);
    }
}
