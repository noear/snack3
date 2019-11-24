[![Maven Central](https://img.shields.io/maven-central/v/org.noear/snack3.svg)](https://search.maven.org/search?q=snack3)


` QQ交流群：22200020 `

# Snack3 for java
一个微型JSON框架

基于jdk8，60kb。有序列化反序列化、解析和转换、支持 Json path 查询。

```xml
<dependency>
  <groupId>org.noear</groupId>
  <artifactId>snack3</artifactId>
  <version>3.1.3</version>
</dependency>
```

ONode 即 `One node` 之意；借签了 `Javascript` 所有变量由 `var` 申明，及 `Xml dom` 一切都是 `Node` 的设计。

## 随便放几个示例

```java
//demo0::字符串化
String json = ONode.stringify(user); 

//demo1::序列化
String json = ONode.serialize(user); //带@type

//demo2::反序列化
UserModel user = ONode.deserialize(json); //json已带@type
UserModel user = ONode.deserialize(json, UserModel.class); //json可以不带@type
List<UserModel> list = ONode.deserialize(json, (new ArrayList<UserModel>(){}).class); //json可以不带@type，泛型方式

//demo3::转为ONode
ONode o = ONode.load(json); //将json String 转为 ONode
ONode o = ONode.load(user); //将java Object 转为 ONode

//demo4:构建json数据(极光推送的rest api调用)
public static void push(Collection<String> alias_ary, String text)  {
    ONode data = new ONode().build((d)->{
        d.get("platform").val("all");

        d.get("audience").get("alias").addAll(alias_ary);

        d.get("options")
                .set("apns_production",false);

        d.get("notification").build(n->{
            n.get("ios")
                    .set("alert",text)
                    .set("badge",0)
                    .set("sound","happy");
        });
    });

    String message = data.toJson();
    String author = Base64Util.encode(appKey+":"+masterSecret);

    Map<String,String> headers = new HashMap<>();
    headers.put("Content-Type","application/json");
    headers.put("Authorization","Basic "+author);

    HttpUtil.postString(apiUrl, message, headers);
}

//demo5:取值
o.get("name").getString();
o.get("num").getInt();
o.get("list").get(0).get("lev").getInt();

//demo5.1::取值并转换
UserModel user = o.get("user").toObject(UserModel.class); //取user节点，并转为UserModel

//demo5.2::取值并填充
o.get("list2").fill("[1,2,3,4,5,5,6]");


//demo6:Simple json path（只支持选择，不支持过滤）//不确定返回数量的，会返回array类型
//修改所有的手机号为186
o.select("$..mobile").forEach(n->n.val("186"));
//修改data.list[1]下的的mobile字段
o.select("$.data.list[1].mobile").val("186");

//查找所有手机号，并转为List<String> //$可写，也可不写
List<String> list = o.select("..mobile").toObject(List.class);//性能差点
//查询data.list下的所有mobile，并转为List<String>
List<String> list = o.select("data.list[*].mobile").toObject(List.class);//性能好点


//demo7:遍历
//如果是个Object
o.forEach((k,v)->{
  //...
});
//如果是个Array
o.forEach((v)->{
  //...
});
```

## 关于序列化的特点
#### 对象（与fastJson一致）
```json
{"@type":"...","a":1,"b":"2"}
```
#### 数组（与fastJson不同；可以精准反序列化类型）
```json
[{"@type":"..."},[1,2,3]]
```

## 关于Json path的支持
* 支持`Json path`的选择操作，但不支持过滤操作。可称它为：`Simple json path`

| 支持操作 |	说明 |
| --- | --- |
| $	| 查询根元素（或当前元素）。这将启动所有路径表达式（可以不输）。 |
| *	| 通配符，必要时可用任何地方的名称或数字。 |
| ..	| 深层扫描。 必要时在任何地方可以使用名称。 |
| .\<name>	| 点，表示子节点 |
| ['\<name>' (, '\<name>')] | 括号表示子项 |
| [\<number> (, \<number>)]	| 数组索引或索引 |
| [start:end]	| 数组切片操作 |

例：`n.select("$.store.book[0].title")` 或 `n.select("$['store']['book'][0]['title']")`

## Snack3 接口字典
```swift
//初始化操作
//
-asObject() -> self:ONode  //将节点切换为对象
-asArray()  -> self:ONode  //将节点切换为数组
-asValue()  -> self:ONode  //将节点切换为值
-asNull()   -> self:ONode  //将节点切换为null

//检测操作
//
-isObject() -> bool  //检查节点是否为对象
-isArray()  -> bool  //检查节点是否为数组
-isValue()  -> bool  //检查节点是否为值
-isNull()   -> bool  //检查节点是否为null

//公共
//
-nodeData() -> ONodeData //获取节点数据
-nodeType() -> ONodeType //获取节点类型

-cfg(cfg:Constants) -> self:ONode   //切换配置
-cfg() -> Constants 				//获取配置

-build(n->..) -> self:ONode     	//节点构建表达式
-select(jpath:String) -> new:ONode 	//使用JsonPath表达式选择节点（只支持选择，不支持过滤）

-clear() //清除子节点，对象或数组有效
-count() //子节点数量，对象或数组有效


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
-obj() -> Map<String,ONode>             //获取节点对象数据结构体（如果不是对象类型，会自动转换）
-readonly() -> self:ONode               //只读形态（get时，不添加子节点）
-contains(key:String) -> bool           //是否存在对象子节点?
-get(key:String) -> child:ONode         //获取对象子节点（不存在，生成新的子节点并返回）
-getOrNull(key:String) -> child:ONode   //获取对象子节点（不存在，返回null）
-getNew(key:String) -> child:ONode      //生成新的对象子节点，会清除之前的数据
-set(key:String,val:Object) -> self:ONode           //设置对象的子节点（会自动处理类型）
-setNode(key:String,val:ONode) -> self:ONode        //设置对象的子节点，值为ONode类型
-setAll(obj:ONode) -> self:ONode                    //设置对象的子节点，将obj的子节点搬过来
-setAll(map:Map<String,T>) ->self:ONode             //设置对象的子节点，将map的成员搬过来
-setAll(map:Map<String,T>, (n,t)->..) ->self:ONode  //设置对象的子节点，将map的成员搬过来，并交由代理处置
-remove(key:String)     //移除对象的子节点
-forEach((k,v)->..)     //遍历对象的子节点

//数组操作
//
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

//特性操作（不破坏数据的情况上，添加数据；或用于构建xml dom）
//
-attrGet(key:String)                //获取特性
-attrSet(key:String,val:String)     //设置特性
-attrForeach((k,v)->..)             //遍历特性

//转换操作
//
-toString() -> String           //转为string （由字符串转换器决定，默认为json）
-toJson() -> String             //转为json string
-toData() -> Object 			//转为数据结构体（Map,List,Value）
-toObject(clz:Class<T>) -> T    //转为java object（clz=Object.class：自动输出类型）
-toObject(clz:Class<T>, toer:Toer) -> T   //转为java object，由toer决定处理

//填充操作（为当前节点填充数据；source 为 String 或 java object）
-fill(source:Object)    -> self:ONode               //填充数据
-fill(source:Object, fromer:Fromer) -> self:ONode   //填充数据，由fromer决定处理

/**
 * 以下为静态操作
**/

//加载操作（source 为 String 或 java object）
//
+load(source:Object) -> new:ONode    //加载数据
+load(source:Object, cfg:Constants) -> new:ONode
+load(source:Object, cfg:Constants, fromer:Fromer) -> new:ONode

//加载 string
+loadStr(source:String) -> new:ONode	//仅String
//加载 java object
+loadObj(source:Object) -> new:ONode	//仅java object

//字符串化操作
//
+stringify(source:Object) -> String                   //字符串化；

//序列化操作
//
+serialize(source:Object) -> String                   //序列化（带@type属性）
+deserialize(source:String) -> T                      //反序列化
+deserialize(source:String, clz:Class<?>) -> T        //反序列化

```
