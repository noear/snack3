//初始化操作
-asObject() -> self:ONode
-asArray() -> self:ONode
-asValue() -> self:ONode
-asNull() -> self:ONode

//检测操作
-isObject() -> bool
-isArray() -> bool
-isNull() -> bool
-isValue() -> bool

//公共
-nodeData() -> ONodeData //内部数据
-nodeType() -> ONodeType //内部类型
-cfg(constants) -> self:ONode //切换配置
-build(n->..) -> self:ONode //用于替代 exp(n->..)
-clear() //争对对象或数缄
-count() //争对对象或数组

//值操作
-val() -> OValue
-val(val:Object) -> self:ONode
-getString()
-getShort()
-getInt()
-getBoolean()
-getLong()
-getDate()
-getFloat()
-getDouble()
-getDouble(scale:int)
-getChar()

//对象操作
-object() -> Map<String,ONode>
-contains(key:String) -> bool
-get(key:String) -> child:ONode
-getNew(key:String) -> child:ONode
-set(key:String,val:Object) -> self:ONode
-setNode(key:String,val:ONode) -> self:ONode
-setAll(obj:ONode) -> self:ONode
-setAll(map:Map<String,T>) ->self:ONode
-setAll(map:Map<String,T>, (n,t)->..) ->self:ONode
-remove(key:String)
-forEach((k,v)->..)

//数组操作
-array() -> List<ONode>
-get(index:int)  -> child:ONode
-addNew() -> child:ONode
-add(obj) -> self:ONode
-addNode(obj:ONode) -> self:ONode
-addAll(ary:ONode)  -> self:ONode
-addAll(ary:Collection<T>) -> self:ONode
-addAll(ary:Collection<T>,(n,t)->..) -> self:ONode
-removeAt(index:int)
-forEach(v->..)

//输出操作
-toString() -> String
-toJson() -> String
-toData() -> Object //可能值，可能是Map，可能是List
-toBean(clz:Class<T>) -> T

//特性操作（不破坏数据的情况上，添加数据；一般用不到）
-attrGet(key:String)
-attrSet(key:String,val:String)
-attrForeach((k,v)->..)

//装载操作
-load(source:Object) -> self:ONode //souce 可以是 String 或 Object
-loadObj(source:Object) -> self:ONode
-loadStr(source:String) -> self:ONode

+from(json:String) -> new:ONode
+fromObj(source:Object) -> new:ONode
+fromObjTry(source:Object) -> new:ONode //不会有异常，且肯定返回一个ONode
+fromStr(source:String) -> new:ONode
+fromStrTry(source:String) -> new:ONode //不会有异常，且肯定返回一个ONode

//序列化操作
+serialize(source:Object) -> String
+serialize(source:Object, constants:Constants) -> String
+deserialize(source:String, clz Class<T>) -> T
+deserialize(source:String, clz Class<T>, constants:constants) -> T
