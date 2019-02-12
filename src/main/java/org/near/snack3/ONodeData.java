package org.near.snack3;

import org.near.snack3.core.Constants;
import org.near.snack3.core.Feature;

import java.util.*;

/**
 * 节点数据
 * */
public class ONodeData {
    /** 节点数据的 value */
    public OValue  value = null;
    /** 节点数据的 object */
    public Map<String, ONode> object = null;
    /** 节点数据的 array */
    public List<ONode> array = null;

    /** 节点类型 */
    public ONodeType nodeType = ONodeType.Null;

    /** 尝试初始化为 null */
    protected void tryInitNull(){
        if(nodeType != ONodeType.Null) {
            nodeType = ONodeType.Null;

            if (object != null) {
                object.clear();
                object = null;
            }

            if (array != null) {
                array.clear();
                array = null;
            }

            value = null;
        }
    }

    /** 尝试初始化为 value */
    protected  void tryInitValue() {
        if (nodeType != ONodeType.Value) {
            nodeType = ONodeType.Value;

            if (value == null) {
                value = new OValue();
            }
        }
    }

    /** 尝试初始化为 object */
    protected  void tryInitObject(Constants cfg) {
        if (nodeType != ONodeType.Object) {
            nodeType = ONodeType.Object;

            if (object == null) {
                if(cfg.hasFeature(Feature.OrderedField)){
                    object = new LinkedHashMap<>();
                }else {
                    object = new HashMap<>();
                }
            }
        }
    }

    /** 尝试初始化为 array */
    protected  void tryInitArray() {
        if (nodeType != ONodeType.Array) {
            nodeType = ONodeType.Array;

            if (array == null) {
                array = new ArrayList<>();
            }
        }
    }

    /** 尝试将 object 换为 array（一般用不到） */
    protected void shiftToArray(){
        tryInitArray();

        if(object!=null) {
            for (ONode n1 : object.values()) {
                array.add(n1);
            }

            object.clear();
            object = null;
        }
    }

    //////////////////////////////

    /** 节点的 特性 */
    public Map<String,String> attrs = null;
    public String attrGet(String key){
        if(attrs != null){
            return attrs.get(key);
        }else{
            return null;
        }
    }
    public void attrSet(String key, String val){
        if(attrs == null){
            attrs = new LinkedHashMap<>();
        }

        attrs.put(key, val);
    }
}
