package org.noear.snack;

import org.noear.snack.core.Feature;

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

    protected ONode _n;

    public ONodeData(ONode n){
        _n = n;
    }

    public Map<String, ONode> object(){
        tryInitObject();
        return object;
    }

    public List<ONode> array(){
        tryInitArray();
        return array;
    }

    public OValue value(){
        tryInitValue();
        return value;
    }

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
                value = new OValue(_n);
            }
        }
    }

    /** 尝试初始化为 object */
    protected  void tryInitObject() {
        if (nodeType != ONodeType.Object) {
            nodeType = ONodeType.Object;

            if (object == null) {
                if(_n._c.hasFeature(Feature.OrderedField)){
                    object = new ONodeLinkedObject();
                }else {
                    object = new ONodeObject();
                }
            }
        }
    }

    /** 尝试初始化为 array */
    protected  void tryInitArray() {
        if (nodeType != ONodeType.Array) {
            nodeType = ONodeType.Array;

            if (array == null) {
                array = new ONodeArray();
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


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null) {
            return false;
        }

        return this.hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        if(nodeType == ONodeType.Object){
            return object.hashCode();
        }

        if(nodeType == ONodeType.Array){
            return array.hashCode();
        }

        if(nodeType == ONodeType.Value){
            return value.hashCode();
        }

        return 0;
    }

    class ONodeArray extends ArrayList<ONode> {
        @Override
        public int indexOf(Object o) {
            for (int i = 0; i < size(); i++)
                if (get(i).equals(o))
                    return i;

            return -1;
        }
    }

    class ONodeObject extends HashMap<String,ONode> {
        @Override
        public boolean containsValue(Object value) {
            for(ONode n: values()){
                if(n.equals(value)){
                    return true;
                }
            }
            return false;
        }
    }

    class ONodeLinkedObject extends LinkedHashMap<String,ONode> {
        @Override
        public boolean containsValue(Object value) {
            for(ONode n: values()){
                if(n.equals(value)){
                    return true;
                }
            }
            return false;
        }
    }
}
