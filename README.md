[![Maven Central](https://img.shields.io/maven-central/v/org.noear/snack3.svg)](https://mvnrepository.com/search?q=snack3)


` QQ交流群：22200020 `

# Snack3 for java
一个微型JSON + Jsonpath框架

基于jdk8，70kb。有序列化反序列化、解析和转换、支持 Json path 查询。

```xml
<dependency>
  <groupId>org.noear</groupId>
  <artifactId>snack3</artifactId>
  <version>3.1.13</version>
</dependency>
```

Snack3 借鉴了 `Javascript` 所有变量由 `var` 申明，及 `Xml dom` 一切都是 `Node` 的设计。其下一切数据都以`ONode`表示，`ONode`也即 `One node` 之意，代表任何类型，也可以转换为任何类型。
* 强调文档树的操控和构建能力
* 做为中间媒体，方便不同格式互转
* 高性能`Json path`查询（兼容性和性能很赞）
* 支持`序列化、反序列化`

## 随便放几个示例

```java
//demo0::字符串化
String json = ONode.stringify(user); 

//demo1::序列化
// -- 输出带@type
String json = ONode.serialize(user); 

//demo2::反序列化
// -- json 有已带@type
UserModel user = ONode.deserialize(json); 
// -- json 可以不带@type (clz 申明了)
UserModel user = ONode.deserialize(json, UserModel.class); 
// -- json 可以不带@type，泛型方式输出（类型是已知的）
List<UserModel> list = ONode.deserialize(json, (new ArrayList<UserModel>(){}).getClass()); 

//demo3::转为ONode
ONode o = ONode.loadStr(json); //将json String 转为 ONode
ONode o = ONode.loadObj(user); //将java Object 转为 ONode

//demo3.1::转为ONode，取子节点进行序列化
ONode o = ONode.loadStr(json);
UserModel user = o.get("user").toObject(UserModel.class);


//demo4:构建json数据(极光推送的rest api调用)
public static void push(Collection<String> alias_ary, String text)  {
    ONode data = new ONode().build((d)->{
        d.getOrNew("platform").val("all");

        d.getOrNew("audience").getOrNew("alias").addAll(alias_ary);

        d.getOrNew("options")
                .set("apns_production",false);

        d.getOrNew("notification").build(n->{
            n.getOrNew("ios")
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


//demo6::json path //不确定返回数量的，者会返回array类型
//找到所有的187开头的手机号，改为186，最后输出修改后的json
o.select("$..mobile[?(@ =~ /^187/)]").forEach(n->n.val("186")).toJson();
//找到data.list[1]下的的mobile字段，并转为long
o.select("$.data.list[1].mobile").getLong();

//查找所有手机号，并转为List<String> 
List<String> list = o.select("$..mobile").toObject(List.class);
//查询data.list下的所有mobile，并转为List<String>
List<String> list = o.select("$.data.list[*].mobile").toObject(List.class);
//找到187手机号的用户，并输出List<UserModel>
List<UserModel> list = o.select("$.data.list[?(@.mobile =~ /^187/)]")
                        .toObjectList(UserModel.class);
//或
List<UserModel> list = o.select("$.data.list[?(@.mobile =~ /^187/)]")
                        .toObjectList(UserModel.class);


//demo7:遍历
//如果是个Object
o.forEach((k,v)->{
  //...
});
//如果是个Array
o.forEach((v)->{
  //...
});

//demo8:互转
String xml = "<xml>....</xml>";
XmlFromer xmlFromer = new XmlFromer();
YmalToer  ymalToer  = new YmalToer();

//加载xml，输出ymal
String ymal = ONode.load(xml,Constants.def(),xmlFromer).to(ymalToer);

//加载xml，去掉手机号，再转为java object
ONode tmp =ONode.load(xml,Constants.def(),xmlFromer);
tmp.select("$..[?(@.mobile)]").forEach(n->n.remove("mobile"));
XxxModel m =tmp.toObject(XxxModel.class);
```

## 关于序列化的特点
#### 对象（可以带type）
```json
{"a":1,"b":"2"}
//或
{"@type":"...","a":1,"b":"2"}
```
#### 数组（可以带type）
```json
[1,2,3]
//或
[{"@type":"..."},[1,2,3]]
```

## 关于Json path的支持
* 字符串使用单引号，例：\['name']
* 过滤操作用空隔号隔开，例：\[?(@.type == 1)]

| 支持操作 |	说明 |
| --- | --- |
| `$`	| 表示根元素 |
| `@`	| 当前节点（做为过滤表达式的谓词使用） |
| `*`	| 通用配配符，可以表示一个名字或数字。 |
| `..`	| 深层扫描。 可以理解为递归搜索。 |
| `.<name>`	| 表示一个子节点 |
| `['<name>' (, '<name>')]` | 表示一个或多个子节点 |
| `[<number> (, <number>)]`	| 表示一个或多个数组下标（负号为倒数） |
| `[start:end]`	| 数组片段，区间为\[start,end),不包含end（负号为倒数） |
| `[?(<expression>)]`	| 过滤表达式。 表达式结果必须是一个布尔值。 |

| 支持过滤操作符(操作符两边要加空隔) |	说明 |
| --- | --- |
| `==`	| left等于right（注意1不等于'1'） |
| `!=`	| 不等于 |
| `<`	| 小于 |
| `<=`	| 小于等于 |
| `>`	| 大于 |
| `>=`	| 大于等于 |
| `=~`	| 匹配正则表达式[?(@.name =~ /foo.*?/i)] |
| `in`	| 左边存在于右边 [?(@.size in ['S', 'M'])] |
| `nin`	| 左边不存在于右边 |

| 支持尾部函数 |	说明 |
| --- | --- |
| `min()`	| 计算数字数组的最小值 |
| `max()`	| 计算数字数组的最大值 |
| `avg()`	| 计算数字数组的平均值 |
| `sum()`	| 计算数字数组的汇总值（新加的） |

例：`n.select("$.store.book[0].title")` 或 `n.select("$['store']['book'][0]['title']")`

例：`n.select("$..book.price.min()") //找到最低的价格` 

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
-toObjectList(clz:Class<T>) -> List<T>   //转为java list

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
