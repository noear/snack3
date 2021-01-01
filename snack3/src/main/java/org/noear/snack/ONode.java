package org.noear.snack;

import org.noear.snack.core.*;
import org.noear.snack.core.exts.Act1;
import org.noear.snack.core.exts.Act2;
import org.noear.snack.core.utils.BeanUtil;
import org.noear.snack.from.Fromer;
import org.noear.snack.to.Toer;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 节点（One Node）
 *
 * @author noear
 * */
public class ONode {
    //内部配置
    protected Constants _c;
    //内部数据
    protected ONodeData _d;

    /**
     * @return 版本信息
     */
    public static String version() {
        return "3.1.12";
    }

    public ONode() {
        _c = Constants.def();
        _d = new ONodeData(this);
    }

    public ONode(Constants cfg) {
        _d = new ONodeData(this);

        if(cfg ==null){
            _c = Constants.def();
        }else {
            _c = cfg;
        }
    }

    public static ONode newValue(){
        return new ONode().asValue();
    }

    public static ONode newObject(){
        return new ONode().asObject();
    }

    public static ONode newArray(){
        return new ONode().asArray();
    }


    /**
     * Json path select
     *
     * @param jpath json path express
     * @param useStandard use standard mode(default: false)
     * @param cacheJpath cache json path parsing results
     */
    public ONode select(String jpath,  boolean useStandard, boolean cacheJpath) {
        return JsonPath.eval(this, jpath, useStandard, cacheJpath);
    }

    public ONode select(String jpath,  boolean useStandard) {
        return select(jpath, useStandard, true);
    }

    public ONode select(String jpath) {
        return select(jpath, false);
    }

    /**
     * 将节点切换为对象
     *
     * @return self:ONode
     */
    public ONode asObject() {
        _d.tryInitObject();
        return this;
    }

    /**
     * 将节点切换为数组
     *
     * @return self:ONode
     */
    public ONode asArray() {
        _d.tryInitArray();
        return this;
    }

    /**
     * 将节点切换为值
     *
     * @return self:ONode
     */
    public ONode asValue() {
        _d.tryInitValue();
        return this;
    }

    /**
     * 将节点切换为null
     *
     * @return self:ONode
     */
    public ONode asNull() {
        _d.tryInitNull();
        return this;
    }

    /**
     * 节点数据
     *
     * @return ONodeData
     * @see ONodeData
     */
    public ONodeData nodeData() {
        return _d;
    }

    /**
     * 节点类型
     *
     * @return ONodeType
     * @see ONodeType
     */
    public ONodeType nodeType() {
        return _d.nodeType;
    }

    /**
     * 切换配置
     *
     * @param cfg 常量配置
     * @return self:ONode
     */
    public ONode cfg(Constants cfg) {
        if (cfg != null) {
            _c = cfg;
        }
        return this;
    }

    public Constants cfg(){
        return _c;
    }


    /**
     * 构建表达式
     *
     * @param fun lambda表达式
     * @return self:ONode
     */
    public ONode build(Act1<ONode> fun) {
        fun.run(this);
        return this;
    }

    ////////////////////
    //
    // 值处理
    //
    ////////////////////

    /**
     * 获取节点值数据结构体（如果不是值类型，会自动转换）
     *
     * @return OValue
     * @see OValue
     */
    public OValue val() {
        return asValue()._d.value;
    }

    /**
     * 设置节点值
     *
     * @param val 为常规类型或ONode
     * @return self:ONode
     */
    public ONode val(Object val) {
        if (val == null) {
            _d.tryInitNull();
        } else if (val instanceof ONode) { //支持数据直接copy
            _d.tryInitNull();
            _d = ((ONode) val)._d;
        } else if (val instanceof Map || val instanceof Collection) {
            _d.tryInitNull();
            _d = buildVal(val)._d;
        } else {
            _d.tryInitValue();
            _d.value.set(val);
        }

        return this;
    }


    /**
     * 获取节点值并以 String 输出
     * 如果节点为对象或数组类型，则输出json
     */
    public String getString() {
        if (isValue()) {
            return _d.value.getString();
        } else {
            if (isArray()) {
                return toJson();
            }

            if (isObject()) {
                return toJson();
            }

            return _c.null_string();
        }
    }

    /**
     * 获取节点值并以 short 输出
     */
    public short getShort() {
        if (isValue())
            return _d.value.getShort();
        else
            return 0;
    }

    /**
     * 获取节点值并以 int 输出
     */
    public int getInt() {
        if (isValue())
            return _d.value.getInt();
        else
            return 0;
    }

    /**
     * 获取节点值并以 boolean 输出
     */
    public boolean getBoolean() {
        if (isValue())
            return _d.value.getBoolean();
        else
            return false;
    }

    /**
     * 获取节点值并以 long 输出
     */
    public long getLong() {
        if (isValue())
            return _d.value.getLong();
        else
            return 0;
    }

    /**
     * 获取节点值并以 Date 输出
     */
    public Date getDate() {
        if (isValue())
            return _d.value.getDate();
        else
            return null;
    }

    /**
     * 获取节点值并以 float 输出
     */
    public float getFloat() {
        if (isValue())
            return _d.value.getFloat();
        else
            return 0;
    }

    /**
     * 获取节点值并以 double 输出
     */
    public double getDouble() {
        if (isValue())
            return _d.value.getDouble();
        else
            return 0;
    }

    /**
     * 获取节点值并以 double 输出
     *
     * @param scale 精度，即小数点长度
     */
    public double getDouble(int scale) {
        double temp = getDouble();

        if (temp == 0)
            return 0;
        else
            return new BigDecimal(temp)
                    .setScale(scale, BigDecimal.ROUND_HALF_UP)
                    .doubleValue();
    }

    /**
     * 获取节点值并以 char 输出
     */
    public char getChar() {
        if (isValue())
            return _d.value.getChar();
        else
            return 0;
    }

    ////////////////////
    //
    // 对象与数组公共处理
    //
    ////////////////////

    /**
     * 清空子节点（对象或数组有效）
     */
    public void clear() {
        if (isObject()) {
            _d.object.clear();
        } else if (isArray()) {
            _d.array.clear();
        }
    }

    /**
     * 子节点数量（对象或数组有效）
     */
    public int count() {
        if (isObject()) {
            return _d.object.size();
        }

        if (isArray()) {
            return _d.array.size();
        }

        return 0;
    }

    ////////////////////
    //
    // 对象处理
    //
    ////////////////////

    /**
     * 获取节点对象数据结构体（如果不是对象类型，会自动转换）
     *
     * @return Map<String, ONode>
     */
    public Map<String, ONode> obj() {
        return asObject()._d.object;
    }

    /**
     * 只读模式
     * get(key) 不会自动产生新节点
     */
    public ONode readonly(boolean readonly) {
        _c.get_readonly = readonly;
        return this;
    }

    public ONode readonly() {
        return readonly(true);
    }

    /**
     * 是否存在对象子节点
     */
    public boolean contains(String key) {
        if (isObject()) {
            return _d.object.containsKey(key);
        } else {
            return false;
        }
    }

    /**
     * 重命名一个子节点（如果不存在则跳过）
     * */
    public ONode rename(String key, String newKey) {
        if (isObject()) {
            rename_do(this, key, newKey);
        } else if (isArray()) {
            for (ONode n : _d.array) {
                rename_do(n, key, newKey);
            }
        }

        return this;
    }

    private static void rename_do(ONode n,String key, String newKey) {
        if (n.isObject()) {
            ONode tmp = n._d.object.get(key);
            if (tmp != null) {
                n._d.object.put(newKey, tmp);
                n._d.object.remove(key);
            }
        }
    }

    /**
     * 获取对象子节点（不存在，生成新的子节点并返回）
     *
     * @return child:ONode
     */
    public ONode get(String key) {
        return getOrNew(key);
    }

    public ONode getOrNew(String key) {
        _d.tryInitObject();

        ONode tmp = _d.object.get(key);
        if (tmp == null) {
            tmp = new ONode(_c);

            if (_c.get_readonly == false) {
                _d.object.put(key, tmp);
            }
        }

        return tmp;
    }

    /**
     * 获取对象子节点（不存在，返回null）
     *
     * @return child:ONode
     */
    public ONode getOrNull(String key) {
        if(isObject()) {
            return _d.object.get(key);
        }else{
            return null;
        }
    }

    /**
     * 生成新的对象子节点，会清除之前的数据
     *
     * @return child:ONode
     */
    public ONode getNew(String key) {
        ONode tmp = new ONode(_c);
        _d.object.put(key, tmp);

        return tmp;
    }

    private ONode buildVal(Object val) {
        if (val instanceof Map) {
            return new ONode(_c).setAll((Map<String, ?>) val);
        } else if (val instanceof Collection) {
            return new ONode(_c).addAll((Collection<?>) val);
        } else {
            //可能会影响性能...
            //
            if (val != null && val.getClass().isArray()) {
                return new ONode(_c).addAll(Arrays.asList((Object[]) val));
            }
            return new ONode(_c).val(val);
        }
    }

    /**
     * 设置对象的子节点（会自动处理类型）
     *
     * @param val 为常规类型或ONode
     * @return self:ONode
     */
    public ONode set(String key, Object val) {
        _d.tryInitObject();

        if (val instanceof ONode) {
            _d.object.put(key, ((ONode) val));
        } else {
            _d.object.put(key, buildVal(val));
        }

        return this;
    }

    /**
     * 设置对象的子节点，值为ONode类型 (需要在外部初始化类型)
     *
     * @return self:ONode
     */
    public ONode setNode(String key, ONode val) {
        _d.object.put(key, val);
        return this;
    }

    /**
     * 设置对象的子节点，将obj的子节点搬过来
     *
     * @param obj 对象类型的节点
     * @return self:ONode
     */
    public ONode setAll(ONode obj) {
        _d.tryInitObject();

        if (obj != null && obj.isObject()) {
            _d.object.putAll(obj._d.object);
        }

        return this;
    }

    /**
     * 设置对象的子节点，将map的成员搬过来
     *
     * @return self:ONode
     */
    public <T> ONode setAll(Map<String, T> map) {
        _d.tryInitObject();

        if (map != null) {
            map.forEach((k, v) -> {
                set(k, v);
            });
        }
        return this;
    }

    /**
     * 设置对象的子节点，将map的成员搬过来，并交由代理处置
     *
     * @return self:ONode
     */
    public <T> ONode setAll(Map<String, T> map, Act2<ONode, T> handler) {
        _d.tryInitObject();

        if (map != null) {
            map.forEach((k, v) -> {
                handler.run(this.get(k), v);
            });
        }
        return this;
    }

    /**
     * 移除对象的子节点 (搞不清楚是自身还是被移除的，所以不返回)
     */
    public void remove(String key) {
        if(isObject()) {
            _d.object.remove(key);
        }
    }

    ////////////////////
    //
    // 数组处理
    //
    ////////////////////

    /**
     * 获取节点数组数据结构体（如果不是数组，会自动转换）
     *
     * @return List<ONode>
     */
    public List<ONode> ary() {
        return asArray()._d.array;
    }

    /**
     * 获取数组子节点（超界，返回空节点） //支持倒数取
     *
     * @return child:ONode
     */
    public ONode get(int index) {
        _d.tryInitArray();

        if (index >= 0 && _d.array.size() > index) {
            return _d.array.get(index);
        }

        return new ONode();
    }

    public ONode getOrNew(int index) {
        _d.tryInitArray();

        if (_d.array.size() > index) {
            return _d.array.get(index);
        } else {
            ONode n = null;
            for (int i = _d.array.size(); i <= index; i++) {
                n = new ONode(_c);
                _d.array.add(n);
            }
            return n;
        }
    }

    /**
     * 获取数组子节点（超界，返回null）
     *
     * @return child:ONode
     */
    public ONode getOrNull(int index) {
        if(isArray()) {
            if (index >= 0 && _d.array.size() > index) {
                return _d.array.get(index);
            }
        }

        return null;
    }

    /**
     * 移除数组的子节点(搞不清楚是自身还是被移除的，所以不返回)
     */
    public void removeAt(int index) {
        if (isArray()) {
            _d.array.remove(index);
        }
    }

    /**
     * 生成新的数组子节点
     *
     * @return child:ONode
     */
    public ONode addNew() {
        _d.tryInitArray();
        ONode n = new ONode(_c);
        _d.array.add(n);
        return n;
    }

    /**
     * 添加数组子节点
     *
     * @param val 为常规类型或ONode
     * @return self:ONode
     */
    public ONode add(Object val) {
        _d.tryInitArray();

        if (val instanceof ONode) {
            _d.array.add((ONode) val);
        } else {
            _d.array.add(buildVal(val));
        }

        return this;
    }

    /**
     * 添加数组子节点，值为ONode类型 (需要在外部初始化类型)
     *
     * @return self:ONode
     */
    public ONode addNode(ONode val) {
        _d.array.add(val);
        return this;
    }

    /**
     * 添加数组子节点，将ary的子节点搬过来
     *
     * @param ary 数组类型的节点
     * @return self:ONode
     */
    public ONode addAll(ONode ary) {
        _d.tryInitArray();

        if (ary != null && ary.isArray()) {
            _d.array.addAll(ary._d.array);
        }

        return this;
    }

    /**
     * 添加数组子节点，将ary的成员点搬过来
     *
     * @return self:ONode
     */
    public <T> ONode addAll(Collection<T> ary) {
        _d.tryInitArray();

        if (ary != null) {
            ary.forEach(m -> add(m));
        }
        return this;
    }

    /**
     * 添加数组子节点，将ary的成员点搬过来，并交由代理处置
     *
     * @return self:ONode
     */
    public <T> ONode addAll(Collection<T> ary, Act2<ONode, T> handler) {
        _d.tryInitArray();

        if (ary != null) {
            ary.forEach(m -> handler.run(addNew(), m));
        }
        return this;
    }

    //////////////////////

    /**
     * 检查节点是否为null
     */
    public boolean isNull() {
        return (_d.nodeType == ONodeType.Null) || (isValue() && _d.value.isNull());
    }

    /**
     * 检查节点是否为值
     */
    public boolean isValue() {
        return _d.nodeType == ONodeType.Value;
    }

    /**
     * 检查节点是否为对象
     */
    public boolean isObject() {
        return _d.nodeType == ONodeType.Object;
    }

    /**
     * 检查节点是否为数组
     */
    public boolean isArray() {
        return _d.nodeType == ONodeType.Array;
    }

    //////////////////////


    /**
     * 遍历对象的子节点
     */
    public ONode forEach(BiConsumer<String, ONode> consumer) {
        if (isObject()) {
            _d.object.forEach(consumer);
        }

        return this;
    }

    /**
     * 遍历数组的子节点
     */
    public ONode forEach(Consumer<ONode> consumer) {
        if (isArray()) {
            _d.array.forEach(consumer);
        }

        return this;
    }

    ////////////////////
    //
    // 特性处理
    //
    ////////////////////

    /**
     * 获取特性
     */
    public String attrGet(String key) {
        return _d.attrGet(key);
    }

    /**
     * 设置特性
     */
    public ONode attrSet(String key, String val) {
        _d.attrSet(key, val);
        return this;
    }

    /**
     * 遍历特性
     */
    public ONode attrForeach(BiConsumer<String, String> consumer) {
        if (_d.attrs != null) {
            _d.attrs.forEach(consumer);
        }
        return this;
    }

    ////////////////////
    //
    // 转换操作
    //
    ////////////////////

    /**
     * 将当前ONode 转为 string（由 stringToer 决定）
     */
    @Override
    public String toString() {
        return to(DEFAULTS.DEF_STRING_TOER);
    }

    /**
     * 将当前ONode 转为 json string
     */
    public String toJson() {
        return to(DEFAULTS.DEF_JSON_TOER);
    }

    /**
     * 将当前ONode 转为 数据结构体（Map or List or val）
     */
    public Object toData() {
        return to(DEFAULTS.DEF_OBJECT_TOER);
    }

    /**
     * 将当前ONode 转为 java object
     *
     * clz = XxxModel.class => XxxModel
     * clz = Object.class   => auto type
     * clz = null           => Map or List or Value
     */
    public <T> T toObject(Type clz) {
        return to(DEFAULTS.DEF_OBJECT_TOER, clz);
    }


    /**
     * 将当前ONode 转为 java list
     *
     * clz = XxxModel.class => XxxModel
     * clz = Object.class   => auto type
     * clz = null           => Map or List or Value
     */
    public<T> List<T> toObjectList(Class<T> clz){
        List<T> list = new ArrayList<>();

        for(ONode n: ary()){
            list.add(n.toObject(clz));
        }

        return list;
    }

    @Deprecated
    public<T> List<T> toArray(Class<T> clz){
        return toObjectList(clz);
    }

    /**
     * 将当前ONode 通过 toer 进行转换
     */
    public <T> T to(Toer toer, Type clz) {
        return (T) (new Context(_c, this, clz).handle(toer).target);
    }
    public <T> T to(Toer toer) {
        return to(toer, null);
    }


    /**
     * 填充数据（如有问题会跳过，不会出异常）
     *
     * @param source 可以是 String 或 java object 数据
     * @return self:ONode
     */
    public ONode fill(Object source) {
        val(doLoad(source, source instanceof String, _c, null));
        return this;
    }

    public ONode fill(Object source,  Feature... features) {
        val(doLoad(source, source instanceof String, Constants.def().add(features), null));
        return this;
    }

    public ONode fillObj(Object source, Feature... features) {
        val(doLoad(source, false, Constants.def().add(features), null));
        return this;
    }

    public ONode fillStr(String source, Feature... features) {
        val(doLoad(source, true, Constants.def().add(features), null));
        return this;
    }

    ////////////////////
    //
    // 来源加载
    //
    ////////////////////

    /**
     * 加载数据并生成新节点（如果异常，会生成空ONode）
     *
     * @param source 可以是 String 或 java object 数据
     * @return new:ONode
     */
    public static ONode load(Object source) {
        return load(source, null, null);
    }

    /**
     * @param features 特性
     * */
    public static ONode load(Object source, Feature... features) {
        return load(source, Constants.def().add(features), null);
    }

    /**
     * @param cfg 常数配置
     * */
    public static ONode load(Object source, Constants cfg) {
        return load(source, cfg, null);
    }



    /**
     * @param fromer 来源处理器
     * */
    public static ONode load(Object source, Constants cfg, Fromer fromer) {
        return doLoad(source, source instanceof String, cfg, fromer);
    }

    /**
     * 加载string并生成新节点
     * */
    public static ONode loadStr(String source) {
        return doLoad(source, true, null, null);
    }

    public static ONode loadStr(String source, Constants cfg) {
        return doLoad(source, true, cfg, null);
    }

    public static ONode loadStr(String source, Feature... features) {
        return doLoad(source, true, Constants.def().add(features), null);
    }

    /**
     * 加载java object并生成新节点
     * */
    public static ONode loadObj(Object source) {
        return doLoad(source, false, null, null);
    }

    //loadStr 不需要 cfg
    public static ONode loadObj(Object source, Constants cfg) {
        return doLoad(source, false, cfg, null);
    }

    public static ONode loadObj(Object source, Feature... features) {
        return doLoad(source, false, Constants.def().add(features), null);
    }



    private static ONode doLoad(Object source, boolean isString, Constants cfg, Fromer fromer) {
        if (fromer == null) {
            if (isString) {
                fromer = DEFAULTS.DEF_STRING_FROMER;
            } else {
                fromer = DEFAULTS.DEF_OBJECT_FROMER;
            }
        }

        if (cfg == null) {
            cfg = Constants.def();
        }

        return (ONode) new Context(cfg, source).handle(fromer).target;
    }

    ////////////////////
    //
    // 字符串化
    //
    ////////////////////

    /**
     * 字会串化 （由序列化器决定格式）
     *
     * @param source java object
     * @throws Exception
     */
    public static String stringify(Object source) {
        return stringify(source, Constants.def());
    }

    /**
     * 字会串化 （由序列化器决定格式）
     *
     * @param source java object
     * @param cfg    常量配置
     * @throws Exception
     */
    public static String stringify(Object source, Constants cfg) {
        //加载java object，须指定Fromer
        return load(source, cfg, DEFAULTS.DEF_OBJECT_FROMER).toString();
    }

    ////////////////////
    //
    // 序列化
    //
    ////////////////////

    /**
     * 序列化为 string（由序列化器决定格式）
     *
     * @param source java object
     * @throws Exception
     */
    public static String serialize(Object source) {
        //加载java object，须指定Fromer
        return load(source, Constants.serialize(), DEFAULTS.DEF_OBJECT_FROMER).toJson();
    }

    /**
     * 反序列化为 java object（由返序列化器决定格式）
     *
     * @param source string
     * @throws Exception
     */
    public static <T> T deserialize(String source) {
        return deserialize(source, Object.class);
    }

    /**
     * 反序列化为 java object（由返序列化器决定格式）
     *
     * @param source string
     * @throws Exception
     */
    public static <T> T deserialize(String source, Type clz) {
        //加载String，不需指定Fromer
        return load(source,  Constants.serialize(), null).toObject(clz);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null) {
            return isNull();
        }

        if(isArray()) {
            if (o instanceof ONode) {
                return Objects.equals(ary(), ((ONode) o).ary());
            } else {
                return Objects.equals(ary(), o);
            }
        }

        if(isObject()) {
            if (o instanceof ONode) {
                return Objects.equals(obj(), ((ONode) o).obj());
            } else {
                return Objects.equals(obj(), o);
            }
        }

        if(isValue()){
            if (o instanceof ONode) {
                return Objects.equals(val(), ((ONode) o).val());
            } else {
                return Objects.equals(val(), o);
            }
        }

        //最后是null type
        if(o instanceof ONode){
            return ((ONode) o).isNull(); //都是 null
        }else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return _d.hashCode();
    }
}