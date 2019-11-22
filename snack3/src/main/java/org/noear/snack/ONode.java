package org.noear.snack;

import org.noear.snack.core.Context;
import org.noear.snack.core.SimpleJsonPath;
import org.noear.snack.core.exts.Act1;
import org.noear.snack.core.exts.Act2;
import org.noear.snack.core.Constants;
import org.noear.snack.core.DEFAULTS;
import org.noear.snack.from.Fromer;
import org.noear.snack.to.Toer;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 节点（One Node）
 *
 * @author noear
 * */
public class ONode {
    //内部配置
    protected Constants _c = Constants.def;
    //内部数据
    protected ONodeData _d = new ONodeData(this);

    /**
     * @return 版本信息
     */
    public static String version() {
        return "3.1.1";
    }

    public ONode() {
    }

    public ONode(Constants cfg) {
        _c = cfg;
    }


    /**
     * simple json path
     * 支持属性和索引
     * 例：.name
     * 例：[1],[1,3,4],[2:10]
     */
    public ONode select(String jpath) {
        String[] ss = jpath.replace("..", ".**.").split("\\.|\\[");

        return SimpleJsonPath.get(ss, 0, this);
    }

    /**
     * 将节点切换为对象
     *
     * @return self:ONode
     */
    public ONode asObject() {
        _d.tryInitObject(_c);
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
        } else {
            _c = cfg.def;
        }
        return this;
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

    private boolean _readonly;

    /**
     * 只读模式
     * get(key) 不会自动产生新节点
     */
    public ONode readonly(boolean readonly) {
        _readonly = readonly;
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
     * 获取对象子节点（不存在，生成新的子节点并返回）
     *
     * @return child:ONode
     */
    public ONode get(String key) {
        _d.tryInitObject(_c);

        ONode tmp = _d.object.get(key);
        if (tmp == null) {
            tmp = new ONode(_c);

            if (_readonly == false) {
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
        _d.tryInitObject(_c);

        return _d.object.get(key);
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

    /**
     * 设置对象的子节点（会自动处理类型）
     *
     * @param val 为常规类型或ONode
     * @return self:ONode
     */
    public ONode set(String key, Object val) {
        _d.tryInitObject(_c);

        if (val instanceof ONode) {
            _d.object.put(key, ((ONode) val).cfg(_c));
        } else {
            _d.object.put(key, new ONode(_c).val(val));
        }

        return this;
    }

    /**
     * 设置对象的子节点，值为ONode类型
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
        _d.tryInitObject(_c);

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
        _d.tryInitObject(_c);

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
        _d.tryInitObject(_c);

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
        _d.tryInitObject(_c);
        _d.object.remove(key);
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
        ONode tmp = getOrNull(index);
        return tmp == null ? new ONode() : tmp;
    }

    /**
     * 获取数组子节点（超界，返回null）
     *
     * @return child:ONode
     */
    public ONode getOrNull(int index) {
        _d.tryInitArray();

        if (index < 0) {
            index = count() + index; //支持倒数取
        }

        if (index >= 0 && _d.array.size() > index) {
            return _d.array.get(index);
        } else {
            return null;
        }
    }

    /**
     * 移除数组的子节点(搞不清楚是自身还是被移除的，所以不返回)
     */
    public void removeAt(int index) {
        _d.tryInitArray();
        _d.array.remove(index);
    }

    /**
     * 生成新的数组子节点
     *
     * @return child:ONode
     */
    public ONode addNew() {
        _d.tryInitArray();
        ONode n = new ONode().cfg(_c);
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
            _d.array.add(new ONode(_c).val(val));
        }

        return this;
    }

    /**
     * 添加数组子节点，值为ONode类型
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
    public void forEach(BiConsumer<String, ONode> consumer) {
        if (isObject()) {
            _d.object.forEach(consumer);
        }
    }

    /**
     * 遍历数组的子节点
     */
    public void forEach(Consumer<ONode> consumer) {
        if (isArray()) {
            _d.array.forEach(consumer);
        }
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
    public void attrSet(String key, String val) {
        _d.attrSet(key, val);
    }

    /**
     * 遍历特性
     */
    public void attrForeach(BiConsumer<String, String> consumer) {
        if (_d.attrs != null) {
            _d.attrs.forEach(consumer);
        }
    }

    ////////////////////
    //
    // 数据转换
    //
    ////////////////////

    /**
     * 将当前ONode 转为 string（由 stringToer 决定）
     */
    @Override
    public String toString() {
        return toObject(null, _c.stringToer);
    }

    /**
     * 将当前ONode 转为 json string
     */
    public String toJson() {
        return toObject(null, DEFAULTS.DEF_JSON_TOER);
    }

    /**
     * 将当前ONode 转为 java object
     * <p>
     * clz = XxxModel.class => XxxModel
     * clz = Object.class   => auto type
     * clz = null           => Map or List or Value
     */
    public <T> T toObject(Class<?> clz) {
        return toObject(clz, _c.objectToer);
    }

    public <T> T toObject(Class<?> clz, Toer toer) {
        return (T) (new Context(_c, this, clz).handle(toer).target);
    }

    /**
     * 将当前ONode 转为 Map or List or val；请改用 toObject(null)
     */
    @Deprecated
    public Object toData() {
        return toObject(null);
    }

    /**
     * 将当前ONode 转为 java Object；请改用 toObject(clz)
     */
    @Deprecated
    public <T> T toBean(Class<T> clz) {
        return toObject(clz);
    }


    /**
     * 填充数据（如有问题会跳过，不会出异常）
     *
     * @param source 可以是 String 或 java object 数据
     * @return self:ONode
     */
    public ONode fill(Object source) {
        val(loadDo(source, source instanceof String, _c, null));
        return this;
    }

    public ONode fill(Object source, Fromer fromer) {
        val(loadDo(source, source instanceof String, _c, fromer));
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
     * @param source 可以是 String 或 been 数据
     * @return new:ONode
     */
    public static ONode load(Object source) {
        return load(source, source instanceof String, Constants.def);
    }

    public static ONode load(Object source , boolean isString) {
        return load(source, isString, Constants.def);
    }

    public static ONode load(Object source, boolean isString, Constants cfg) {
        return loadDo(source, isString, cfg, null);
    }


    private static ONode loadDo(Object source, boolean isString, Constants cfg, Fromer fromer) {
        if (isString) {
            if (fromer == null) {
                fromer = cfg.stringFromer;
            }
        } else {
            if (fromer == null) {
                fromer = cfg.objectFromer;
            }
        }

        return (ONode) new Context(cfg, source, isString).handle(fromer).target;
    }


    /**
     * 字会串化 （由序列化器决定格式）
     *
     * @param source java object
     * @throws Exception
     */
    public static String stringify(Object source) {
        return stringify(source, Constants.def);
    }

    /**
     * 字会串化 （由序列化器决定格式）
     *
     * @param source java object
     * @param cfg    常量配置
     * @throws Exception
     */
    public static String stringify(Object source, Constants cfg) {
        return load(source, false, cfg).toString();
    }

    /**
     * 序列化为 string（由序列化器决定格式）
     *
     * @param source java object
     * @throws Exception
     */
    public static String serialize(Object source) {
        return serialize(source, Constants.serialize);
    }

    /**
     * 序列化为 string（由序列化器决定格式）
     *
     * @param source java object
     * @param cfg    常量配置
     * @throws Exception
     */
    public static String serialize(Object source, Constants cfg) {
        return load(source, false, cfg).toJson();
    }

    /**
     * 反序列化为 bean（由返序列化器决定格式）
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
    public static <T> T deserialize(String source, Class<?> clz) {
        return deserialize(source, clz, Constants.serialize);
    }

    /**
     * 反序列化为 java object（由返序列化器决定格式）
     *
     * @param source string
     * @param cfg    常量配置
     * @throws Exception
     */
    public static <T> T deserialize(String source, Class<?> clz, Constants cfg) {
        return load(source, true, cfg).toObject(clz, cfg.objectToer);
    }
}