# Snack3 接口字典
```swift
//快捷构建
//
+newValue()  -> new:ONode 创建值类型节点
+newObject() -> new:ONode 创建对象类型节点
+newArray()  -> new:ONode 创建数组类型节点

//初始化操作
//
-asObject() -> self:ONode  //将当前节点切换为对象
-asArray()  -> self:ONode  //将当前节点切换为数组
-asValue()  -> self:ONode  //将当前节点切换为值
-asNull()   -> self:ONode  //将当前节点切换为null

//检测操作
//
-isObject() -> bool  //检查当前节点是否为对象
-isArray()  -> bool  //检查当前节点是否为数组
-isValue()  -> bool  //检查当前节点是否为值
-isNull()   -> bool  //检查当前节点是否为null

//公共
//
-nodeData() -> ONodeData //获取当前节点数据
-nodeType() -> ONodeType //获取当前节点类型

-cfg(cfg:Constants) -> self:ONode   //切换配置
-cfg() -> Constants 				//获取配置

-build(n->..) -> self:ONode     	//节点构建表达式
-select(jpath:String) -> new:ONode 	                    //使用JsonPath表达式选择节点（默认缓存路径编译）
-select(jpath:String, useStandard:boolean)-> new:ONode  //useStandard:使用标准模式,默认非标准
-select(jpath:String, useStandard:boolean, cacheJpath:boolean)-> new:ONode   //cacheJpath:是否缓存javaPath编译成果，默认缓存

-clear()                    //清除子节点，对象或数组有效
-count() -> int             //子节点数量，对象或数组有效
-readonly() -> self:ONode   //只读形态（get时，不添加子节点）

//值操作
//
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
//
-obj() -> Map<String,ONode>                     //获取节点对象数据结构体（如果不是对象类型，会自动转换）
-contains(key:String) -> bool                   //是否存在对象子节点?
-rename(key:String,newKey:String) -> self:ONode //重命名子节点并返回自己
-get(key:String) -> child:ONode                 //获取对象子节点（不存在，生成新的子节点并返回）
-getOrNew(key:String) -> child:ONode            //获取对象子节点（不存在，生成新的子节点并返回）
-getOrNull(key:String) -> child:ONode           //获取对象子节点（不存在，返回null）
-getNew(key:String) -> child:ONode              //生成新的对象子节点，会清除之前的数据
-set(key:String,val:Object) -> self:ONode           //设置对象的子节点（会自动处理类型）
-setNode(key:String,val:ONode) -> self:ONode        //设置对象的子节点，值为ONode类型（需要在外部初始化类型，建议用set(k,v)）
-setAll(obj:ONode) -> self:ONode                    //设置对象的子节点，将obj的子节点搬过来
-setAll(map:Map<String,T>) ->self:ONode             //设置对象的子节点，将map的成员搬过来
-setAll(map:Map<String,T>, (n,t)->..) ->self:ONode  //设置对象的子节点，将map的成员搬过来，并交由代理处置
-remove(key:String)                   //移除对象的子节点
-forEach((k,v)->..) -> self:ONode     //遍历对象的子节点

//数组操作
//
-ary() -> List<ONode>                   //获取节点数组数据结构体（如果不是数组，会自动转换）
-get(index:int)  -> child:ONode                 //获取数组子节点（超界，返回空节点）
-getOrNew(index:int)  -> child:ONode            //获取数组子节点（不存在，生成新的子节点并返回）
-getOrNull(index:int)  -> child:ONode           //获取数组子节点（超界，返回null）
-addNew() -> child:ONode                        //生成新的数组子节点
-add(val) -> self:ONode                         //添加数组子节点 //val:为常规类型或ONode
-addNode(val:ONode) -> self:ONode               //添加数组子节点，值为ONode类型（需要在外部初始化类型，建议用add(v)）
-addAll(ary:ONode)  -> self:ONode               //添加数组子节点，将ary的子节点搬过来
-addAll(ary:Collection<T>) -> self:ONode                //添加数组子节点，将ary的成员点搬过来
-addAll(ary:Collection<T>,(n,t)->..) -> self:ONode      //添加数组子节点，将ary的成员点搬过来，并交由代理处置
-removeAt(index:int)                 //移除数组的子节点
-forEach(v->..) -> self:ONode        //遍历数组的子节点

//特性操作（不破坏数据的情况上，添加数据；或用于构建xml dom）
//
-attrGet(key:String) -> String                  //获取特性
-attrSet(key:String,val:String) -> self:ONode   //设置特性
-attrForeach((k,v)->..) -> self:ONode           //遍历特性

//转换操作
//
-toString() -> String               //转为string （由字符串转换器决定，默认为json）
-toJson() -> String                 //转为json string
-toData() -> Object 			    //转为数据结构体（Map,List,Value）
-toObject(clz:Class<T>) -> T        //转为java object（clz=Object.class：自动输出类型）
-toObjectList(clz:Class<T>) -> List<T>   //转为java list，用于简化：toObject((new ArrayList<User>()).getClass()) 这种写法

-to(toer:Toer, clz:Class<T>) -> T   //将当前节点通过toer进行转换
-to(toer:Toer) -> T                 //将当前节点通过toer进行转换

//填充操作（为当前节点填充数据；source 为 String 或 java object）
-fill(source:Object)    -> self:ONode  //填充数据
-fill(source:Object, Feature... features)    -> self:ONode //填充数据
-fillObj(source:Object, Feature... features)    -> self:ONode //填充数据
-fillStr(source:String, Feature... features)    -> self:ONode //填充数据

/**
 * 以下为静态操作
**/

//加载操作（source 为 String 或 java object）
//
+load(source:Object) -> new:ONode    //加载数据
+load(source:Object, Feature... features) -> new:ONode
+load(source:Object, cfg:Constants) -> new:ONode
+load(source:Object, cfg:Constants, fromer:Fromer) -> new:ONode

//加载 string
+loadStr(source:String) -> new:ONode	//仅String
+loadStr(source:String, Feature... features) -> new:ONode	//仅String
//加载 java object
+loadObj(source:Object) -> new:ONode	//仅java object
+loadObj(source:Object, Feature... features) -> new:ONode	//仅java object

//字符串化操作
//
+stringify(source:Object) -> String                   //字符串化；

//序列化操作
//
+serialize(source:Object) -> String                   //序列化（带@type属性）
+deserialize(source:String) -> T                      //反序列化
+deserialize(source:String, clz:Class<?>) -> T        //反序列化

```
