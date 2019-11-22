
## 接口字典
```swift
//初始化操作
-asObject() -> self:ONode  //将节点切换为对象
-asArray()  -> self:ONode  //将节点切换为数组
-asValue()  -> self:ONode  //将节点切换为值
-asNull()   -> self:ONode  //将节点切换为null

//检测操作
-isObject() -> bool  //检查节点是否为对象
-isArray()  -> bool  //检查节点是否为数组
-isValue()  -> bool  //检查节点是否为值
-isNull()   -> bool  //检查节点是否为null

//公共
-nodeData() -> ONodeData //获取节点数据
-nodeType() -> ONodeType //获取节点类型

-cfg(constants:Constants) -> self:ONode   //切换配置

-build(n->..) -> self:ONode     //节点构建表达式

-clear() //清除子节点，对象或数组有效
-count() //子节点数量，对象或数组有效


//值操作
-val() -> OValue                //获取节点值数据结构体（如果不是值类型，会自动转换）
-val(val:Object) -> self:ONode  //设置节点值 //val:为常规类型或ONode
-getString()    //获取值并以string输出 //如果节点为对象或数组，则输出json
-getShort()     //获取值并以short输出...(以下同...)
-getInt()
-getBoolean()
-getLong()
-getDate()
-getFloat()
-getDouble()
-getDouble(scale:int)
-getChar()

//对象操作
-obj() -> Map<String,ONode>             //获取节点对象数据结构体（如果不是对象类型，会自动转换）
-contains(key:String) -> bool           //是否存在对象子节点?
-get(key:String) -> child:ONode         //获取对象子节点（不存在，生成新的子节点并返回）
-getOrNull(key:String) -> child:ONode   //获取对象子节点（不存在，返回null）
-getNew(key:String) -> child:ONode      //生成新的对象子节点，会清除之前的数据
-set(key:String,val:Object) -> self:ONode           //设置对象的子节点（会自动处理类型）//val:为常规类型或ONode
-setNode(key:String,val:ONode) -> self:ONode        //设置对象的子节点，值为ONode类型
-setAll(obj:ONode) -> self:ONode                    //设置对象的子节点，将obj的子节点搬过来
-setAll(map:Map<String,T>) ->self:ONode             //设置对象的子节点，将map的成员搬过来
-setAll(map:Map<String,T>, (n,t)->..) ->self:ONode  //设置对象的子节点，将map的成员搬过来，并交由代理处置
-remove(key:String)     //移除对象的子节点
-forEach((k,v)->..)     //遍历对象的子节点

//数组操作
-ary() -> List<ONode>                   //获取节点数组数据结构体（如果不是数组，会自动转换）
-get(index:int)  -> child:ONode                 //获取数组子节点（超界，返回空节点）
-getOrNull(index:int)  -> child:ONode           //获取数组子节点（超界，返回null）
-addNew() -> child:ONode                        //生成新的数组子节点
-add(val) -> self:ONode                         //添加数组子节点 //val:为常规类型或ONode
-addNode(val:ONode) -> self:ONode               //添加数组子节点，值为ONode类型
-addAll(ary:ONode)  -> self:ONode               //添加数组子节点，将ary的子节点搬过来
-addAll(ary:Collection<T>) -> self:ONode                //添加数组子节点，将ary的成员点搬过来
-addAll(ary:Collection<T>,(n,t)->..) -> self:ONode      //添加数组子节点，将ary的成员点搬过来，并交由代理处置
-removeAt(index:int)    //移除数组的子节点
-forEach(v->..)         //遍历数组的子节点

//输出操作
-toString() -> String           //转为string （如果是对象或数组，则为json）
-toJson() -> String             //转为json string
-toObject(clz:Class<T>) -> T    //转为java object（clz=null：转为Map,List,Value）

//特性操作（不破坏数据的情况上，添加数据；一般用不到）
-attrGet(key:String)                //获取特性
-attrSet(key:String,val:String)     //设置特性
-attrForeach((k,v)->..)             //遍历特性

//填充操作（为当前节点填充数据）
-fill(source:Object)    -> self:ONode       //填充数据（如果异常，会跳过）（souce 可以是 String 或 been）
-fillObj(source:Object) -> self:ONode       //填充been数据，可能会出异常
-fillStr(source:String) -> self:ONode       //填充String数据，可能会出异常

/**
* 以下为静态操作
*/

//加载操作
+load(source:Object)    -> new:ONode    //加载数据（如果异常，会生成空ONode）（souce 可以是 String 或 been）
+loadObj(source:Object) -> new:ONode    //加载bean为ONode，可能会出异常
+loadStr(source:String) -> new:ONode    //加载String为ONode，可能会出异常

//字符串化操作
+stringify(source:Object) -> String                         //字符串化
+stringify(source:Object, constants:Constants) -> String    //字符串化，可定制常量

//序列化操作
+serialize(source:Object) -> String                         //序列化（带@type属性）
+serialize(source:Object, constants:Constants) -> String    //序列化，可定制常量
+deserialize(source:String, clz Class<T>) -> T                          //反序列化
+deserialize(source:String, clz Class<T>, constants:Constants) -> T     //反序列化，可定制常量

```